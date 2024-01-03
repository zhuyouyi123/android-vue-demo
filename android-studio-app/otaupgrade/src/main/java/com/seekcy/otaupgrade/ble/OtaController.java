/********************************************************************************************************
 * @file OtaController.java
 *
 * @brief for TLSR chips
 *
 * @author telink
 * @date Sep. 30, 2019
 *
 * @par Copyright (c) 2019, Telink Semiconductor (Shanghai) Co., Ltd. ("TELINK")
 *
 *          Licensed under the Apache License, Version 2.0 (the "License");
 *          you may not use this file except in compliance with the License.
 *          You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *          Unless required by applicable law or agreed to in writing, software
 *          distributed under the License is distributed on an "AS IS" BASIS,
 *          WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *          See the License for the specific language governing permissions and
 *          limitations under the License.
 *******************************************************************************************************/
package com.seekcy.otaupgrade.ble;

import android.bluetooth.BluetoothGattService;
import android.os.Handler;

import com.seekcy.otaupgrade.foundation.Opcode;
import com.seekcy.otaupgrade.foundation.OtaProtocol;
import com.seekcy.otaupgrade.foundation.OtaSetting;
import com.seekcy.otaupgrade.foundation.OtaStatusCode;
import com.seekcy.otaupgrade.foundation.ResultCode;
import com.seekcy.otaupgrade.util.Arrays;
import com.seekcy.otaupgrade.util.Crc;
import com.seekcy.otaupgrade.util.OtaLogger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Update direct connected device for update,
 * based on gatt connection {@link GattConnection}
 */
public class OtaController {

    /**
     * command tags
     */
    private static final int TAG_OTA_WRITE = 1;

    private static final int TAG_OTA_READ = 2;

    private static final int TAG_OTA_LAST = 3;

    private static final int TAG_OTA_REQUEST_MTU = 4;

    private static final int TAG_OTA_VERSION = 5;

    private static final int TAG_OTA_START = 6;

    private static final int TAG_OTA_END = 7;

    private static final int TAG_OTA_START_EXT = 8;

    private static final int TAG_OTA_FW_VERSION_REQ = 9;

    private static final int TAG_OTA_SET_FW_INDEX = 10;
    /**
     * timer for version request
     */
    private static final long TIMEOUT_VERSION_RSP = 3 * 1000;

    private final OtaPacketParser mOtaParser = new OtaPacketParser();


    private OtaSetting otaSetting;

    private OtaProtocol otaProtocol;

    private boolean otaRunning = false;
    private final String LOG_TAG = "GATT-OTA";

    public static final int OTA_STATE_SUCCESS = 1;
    public static final int OTA_STATE_FAILURE = 0;
    public static final int OTA_STATE_PROGRESS = 2;

    protected Handler mTimeoutHandler;

    private GattConnection mConnection;

    private GattOtaCallback mCallback;

    private static final int DEFAULT_READ_INTERVAL = 8;


    public OtaController(GattConnection gattConnection) {
        mTimeoutHandler = new Handler();
        mConnection = gattConnection;
    }

    public void setOtaCallback(GattOtaCallback callback) {
        this.mCallback = callback;
    }

    private void clear() {
        this.mOtaParser.clear();
    }

    public void startOta(OtaSetting otaSetting) {
        OtaLogger.d("start ota - 0: " + otaSetting.toString());
        if (otaRunning) {
            onOtaFailure(OtaStatusCode.BUSY, "busy");
            return;
        }

        if (mConnection == null || !mConnection.isConnected()) {
            onOtaFailure(OtaStatusCode.FAIL_UNCONNECTED, "OTA fail: device not connected");
            return;
        }

        this.otaSetting = otaSetting;
        resetOta();

        if (!validateOtaSettings()) {
            return;
        }
        OtaLogger.d("start ota: " + otaSetting.toString());
        otaRunning = true;
        mTimeoutHandler.postDelayed(OTA_TIMEOUT_TASK, otaSetting.timeout);
        mConnection.enableNotification(getOtaService(), getOtaCharacteristic());
        onOtaStart();

        if (otaSetting.sendFwIndex) {
            sendSetFwIndexCmd();
        }

        if (otaSetting.sendOTAVersion) {
            sendOTAVersionCmd();
        }
        if (isLegacyProtocol()) {
            sendOtaStartCmd();
        } else {
            sendOtaFwVersionReqCommand();
        }
    }

    public void pushNotification(byte[] notificationData) {
        if (notificationData.length < 2) return;

        int opcode = (notificationData[0] & 0xFF) | ((notificationData[1] & 0xFF) << 8);
        OtaLogger.d(String.format("ota notify: %04X", opcode));
        if (opcode == Opcode.CMD_OTA_FW_VERSION_RSP.value) {
            // cancel rsp timer
            if (notificationData.length < 5) {
                onOtaFailure(OtaStatusCode.FAIL_VERSION_RSP_ERROR, "version response command format error");
                return;
            }
            int index = 2;
            byte[] deviceVersion = new byte[2];
            System.arraycopy(notificationData, 2, deviceVersion, 0, 2);
            index += 2;
            boolean accept = notificationData[index] == 1;
            OtaLogger.d(String.format("version response: version-%s accept-%b",
                    Arrays.bytesToHexString(deviceVersion, ":"),
                    accept));
            mTimeoutHandler.removeCallbacks(OTA_FW_VERSION_RSP_TASK);
            if (accept) {
                sendOtaStartExtCmd();
            } else {
                onOtaFailure(OtaStatusCode.FAIL_VERSION_COMPARE_ERR, "device version compare fail");
            }

            // check is notification data format err
        } else if (opcode == Opcode.CMD_OTA_RESULT.value) {
            if (!otaRunning) return;
            if (notificationData.length < 3) return;
            byte result = notificationData[2];
            if (result == ResultCode.OTA_SUCCESS.value) {
                if (!isLegacyProtocol()) {
                    resetOta();
                    onOtaSuccess();
                }
            } else {
                ResultCode resultCode = ResultCode.getResultCode(result);
                onOtaFailure(OtaStatusCode.FAIL_OTA_RESULT_NOTIFICATION, resultCode == null ? "unknown result code" : resultCode.toString());
            }
        }
    }

    private BluetoothGattService getService(UUID serviceUUID) {
        if (mConnection == null) return null;
        if (mConnection.getServices() != null) {
            for (BluetoothGattService service : mConnection.getServices()
            ) {
                if (service.getUuid().equals(serviceUUID)) {
                    return service;
                }
            }
        }
        return null;
    }

    public void stopOta(boolean disconnect) {
        resetOta();
        if (disconnect) {
            mConnection.disconnect();
        }
    }

    public boolean validateOtaSettings() {
        if (otaSetting == null) {
            onOtaFailure(OtaStatusCode.FAIL_PARAMS_ERR, "OTA fail: params error");
            return false;
        }

        UUID serviceUUID = getOtaService();
        BluetoothGattService service = getService(serviceUUID);
        if (service == null) {
            onOtaFailure(OtaStatusCode.FAIL_SERVICE_NOT_FOUND, "OTA fail: service not found");
            return false;
        }
        if (service.getCharacteristic(getOtaCharacteristic()) == null) {
            onOtaFailure(OtaStatusCode.FAIL_CHARACTERISTIC_NOT_FOUND, "OTA fail: characteristic not found");
            return false;
        }

        byte[] firmwareData = parseFirmware(otaSetting.firmwarePath, otaSetting.checkFirmwareCrc);
        if (firmwareData == null) {
            onOtaFailure(OtaStatusCode.FAIL_FIRMWARE_CHECK_ERR, "OTA fail: check selected bin error");
            return false;
        }

        this.otaProtocol = otaSetting.protocol;
        int maxPduLen = mConnection.getMtu() - 7; // 7 : 1 byte opcode, 2 byte handle, 2 byte pdu index, 2 byte crc
        int checkedLen = Math.min(otaSetting.pduLength, maxPduLen);
        OtaLogger.d("used pdu len: " + checkedLen);
        this.mOtaParser.set(firmwareData, checkedLen);
        return true;
    }


    private byte[] parseFirmware(String fileName, boolean checkFirmwareCrc) {
        if (fileName == null) return null;
        try {
            InputStream stream = new FileInputStream(fileName);
            int length = stream.available();
            byte[] firmware = new byte[length];
            stream.read(firmware);
            stream.close();

            // check firmware crc
            if (checkFirmwareCrc && !checkCRC(firmware)) {
                OtaLogger.d("check firmware fail");
                return null;
            }
//            TelinkLog.w("firmware data: " + Arrays.bytesToHexString(firmwareData, ""));
            return firmware;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * calculate firmware crc
     *
     * @param firmware selected bin
     * @return check result
     */
    private boolean checkCRC(byte[] firmware) {
        if (firmware == null || firmware.length < 4) {
            return false;
        }

        int len = firmware.length;
        byte[] buffer = new byte[len - 4];
        System.arraycopy(firmware, 0, buffer, 0, buffer.length);
        // calc value
        int crcValue = Crc.calCrc32(buffer);

        // read from firmware
        long crcInFirmware = ((firmware[len - 1] & 0xFF) << 24) +
                ((firmware[len - 2] & 0xFF) << 16) +
                ((firmware[len - 3] & 0xFF) << 8) +
                (firmware[len - 4] & 0xFF);
        OtaLogger.d("crc check compare: crc : " + crcValue + " local : " + crcInFirmware);
        if (crcValue != crcInFirmware) {
            OtaLogger.d("crc check err");
            return false;
        }
        return true;
    }


    private void updateOtaState(int code, String extra) {
        if (mCallback != null) {
            mCallback.onOtaStatusChanged(code, extra, this.mConnection, this);
        }
    }


    protected void onOtaStart() {
        updateOtaState(OtaStatusCode.STARTED, "OTA started");
    }

    protected void onOtaSuccess() {
        otaRunning = false;
        updateOtaState(OtaStatusCode.SUCCESS, "OTA success");
    }

    protected void onOtaFailure(int statusCode, String extra) {
        otaRunning = false;
        updateOtaState(statusCode, extra);
    }

    protected void onOtaProgress() {
        if (mCallback != null) {
            mCallback.onOtaProgressUpdate(getOtaProgress(), this.mConnection, this);
        }
    }


    public int getOtaProgress() {
        return this.mOtaParser.getProgress();
    }

    private void resetOta() {
        otaRunning = false;
        this.mTimeoutHandler.removeCallbacksAndMessages(null);
        this.mOtaParser.clear();
    }

    private void setOtaProgressChanged() {
        if (this.mOtaParser.invalidateProgress()) {
            onOtaProgress();
        }
    }

    private boolean isLegacyProtocol() {
        return this.otaProtocol == OtaProtocol.Legacy;
    }

    private void sendSetFwIndexCmd() {
        sendOtaCmd(Opcode.CMD_OTA_SET_FW_INDEX.value, TAG_OTA_SET_FW_INDEX, new byte[]{otaSetting.fwIndex});
    }

    private void sendOTAVersionCmd() {
        sendOtaCmd(Opcode.CMD_OTA_VERSION.value, TAG_OTA_VERSION, null);
    }

    // OTA 开始时发送的命令
    private void sendOtaStartCmd() {
        sendOtaCmd(Opcode.CMD_OTA_START.value, TAG_OTA_START, null);
    }

    private void sendOtaStartExtCmd() {
        byte[] extData = new byte[18];
        extData[0] = (byte) otaSetting.pduLength;
        extData[1] = (byte) (otaSetting.versionCompare ? 1 : 0);
        // extData[2~18] : Reserved
        sendOtaCmd(Opcode.CMD_OTA_START_EXT.value, TAG_OTA_START_EXT, extData);
    }

    // OTA 开始时发送的命令
    private void sendOtaFwVersionReqCommand() {
        byte[] reqData = new byte[3];
        System.arraycopy(otaSetting.firmwareVersion, 0, reqData, 0, 2);
        reqData[2] = (byte) (otaSetting.versionCompare ? 0x01 : 0x00);
        sendOtaCmd(Opcode.CMD_OTA_FW_VERSION_REQ.value, TAG_OTA_FW_VERSION_REQ, reqData);
        mTimeoutHandler.postDelayed(OTA_FW_VERSION_RSP_TASK, TIMEOUT_VERSION_RSP);
    }

    private void sendOtaEndCommand() {
        int index = mOtaParser.getIndex();
        byte[] data = new byte[18];
        data[0] = (byte) (index & 0xFF);
        data[1] = (byte) (index >> 8 & 0xFF);
        data[2] = (byte) (~index & 0xFF);
        data[3] = (byte) (~index >> 8 & 0xFF);
//        int crc = mOtaParser.crc16(data); // include opcode
//        mOtaParser.fillCrc(data, crc);

        sendOtaCmd(Opcode.CMD_OTA_END.value, TAG_OTA_END, data);
    }

    private void sendOtaCmd(int opcode, int tag, byte[] data) {
        Command cmd = Command.newInstance();
        cmd.serviceUUID = getOtaService();
        cmd.characteristicUUID = getOtaCharacteristic();
        cmd.type = Command.CommandType.WRITE_NO_RESPONSE;
        cmd.tag = tag;

        byte[] cmdData;
        if (data == null) {
            cmdData = new byte[]{(byte) (opcode & 0xFF), (byte) (opcode >> 8 & 0xFF)};
        } else {
            cmdData = new byte[2 + data.length];
            cmdData[0] = (byte) (opcode & 0xFF);
            cmdData[1] = (byte) ((opcode >> 8) & 0xFF);
            System.arraycopy(data, 0, cmdData, 2, data.length);
        }
        cmd.data = cmdData;
        this.sendGattCmd(cmd, OTA_CMD_CB);
    }


    private void sendNextOtaPacketCommand() {

        if (this.mOtaParser.hasNextPacket()) {
            Command cmd = Command.newInstance();
            cmd.serviceUUID = getOtaService();
            cmd.characteristicUUID = getOtaCharacteristic();
            cmd.type = Command.CommandType.WRITE_NO_RESPONSE;
            cmd.data = this.mOtaParser.getNextPacket();
            if (this.mOtaParser.isLast()) {
                cmd.tag = TAG_OTA_LAST;
            } else {
                cmd.tag = TAG_OTA_WRITE;
            }
            this.sendGattCmd(cmd, OTA_CMD_CB);
            setOtaProgressChanged();
        } else {
            OtaLogger.d("no other packet");
        }
    }

    private boolean validateOta() {
        /**
         * 发送read指令
         */
        int readInterval = otaSetting.readInterval;
        if (readInterval <= 0) {
            return false;
        }
        int sectionSize = 16 * readInterval;
        int sendTotal = this.mOtaParser.getNextPacketIndex() * otaSetting.pduLength;
        OtaLogger.i("ota onCommandSampled byte length : " + sendTotal);
        if (sendTotal > 0 && sendTotal % sectionSize == 0) {
            OtaLogger.i("onCommandSampled ota read packet " + mOtaParser.getNextPacketIndex());
            Command cmd = Command.newInstance();
            cmd.serviceUUID = getOtaService();
            cmd.characteristicUUID = getOtaCharacteristic();
            cmd.type = Command.CommandType.READ;
            cmd.tag = TAG_OTA_READ;

            this.sendGattCmd(cmd, OTA_CMD_CB);
            return true;
        }
        return false;
    }

    private void sendGattCmd(Command command, Command.Callback callback) {
        if (mConnection != null) {
            mConnection.sendCommand(callback, command);
        }
    }

    private UUID getOtaService() {
        if (this.otaSetting != null && this.otaSetting.serviceUUID != null) {
            return this.otaSetting.serviceUUID;
        }
        return UuidInfo.OTA_SERVICE_UUID;
    }

    private UUID getOtaCharacteristic() {
        if (this.otaSetting != null && this.otaSetting.characteristicUUID != null) {
            return this.otaSetting.characteristicUUID;
        }
        return UuidInfo.OTA_CHARACTERISTIC_UUID;
    }

    /**
     * OTA End command send complete
     *
     * @param success is cmd send success
     */
    private void onEndCmdComplete(boolean success) {
        // if (isLegacyProtocol() || success) {
        if (isLegacyProtocol()) {
            resetOta();
            setOtaProgressChanged();
            onOtaSuccess();
        } else if (!success) {
            onOtaFailure(OtaStatusCode.FAIL_PACKET_SENT_ERR, "OTA fail: end packet sent err");
        }
    }

    private final Command.Callback OTA_CMD_CB = new Command.Callback() {

        @Override
        public void success(Peripheral peripheral, Command command, Object obj) {
            if (!otaRunning) return;
            if (command.tag.equals(TAG_OTA_VERSION)) {
                /*if (otaProtocol == OtaProtocol.Legacy) {
                    sendOtaStartCmd();
                } else {
                    // for extend protocol
                    sendOtaFwVersionReqCommand();
                }*/
            } else if (command.tag.equals(TAG_OTA_START)) {
                // Ota
                OtaLogger.d("start success: ");
                sendNextOtaPacketCommand();
            } else if (command.tag.equals(TAG_OTA_START_EXT)) {
                sendNextOtaPacketCommand();
            } else if (command.tag.equals(TAG_OTA_END)) {
                // ota success
                onEndCmdComplete(true);
            } else if (command.tag.equals(TAG_OTA_LAST)) {
                sendOtaEndCommand();
            } else if (command.tag.equals(TAG_OTA_WRITE)) {
                if (!validateOta()) {
                    sendNextOtaPacketCommand();
                }
            } else if (command.tag.equals(TAG_OTA_READ)) {
                sendNextOtaPacketCommand();
            }
        }

        @Override
        public void error(Peripheral peripheral, Command command, String errorMsg) {
            if (!otaRunning) return;
            OtaLogger.d("error packet : " + command.tag + " errorMsg : " + errorMsg);
            if (command.tag.equals(TAG_OTA_END)) {
                onEndCmdComplete(false);
            } else {
                resetOta();
                onOtaFailure(OtaStatusCode.FAIL_PACKET_SENT_ERR, "OTA fail: packet sent err");
            }
        }

        @Override
        public boolean timeout(Peripheral peripheral, Command command) {
            if (!otaRunning) return false;
            OtaLogger.d("timeout : " + Arrays.bytesToHexString(command.data, ":"));
            if (command.tag.equals(TAG_OTA_END)) {
                onEndCmdComplete(false);
            } else {
                resetOta();
                onOtaFailure(OtaStatusCode.FAIL_PACKET_SENT_TIMEOUT, "OTA fail: packet sent timeout");
            }
            return false;
        }
    };

    private final Runnable OTA_FW_VERSION_RSP_TASK = new Runnable() {
        @Override
        public void run() {
            resetOta();
            onOtaFailure(OtaStatusCode.FAIL_FW_VERSION_REQ_TIMEOUT, "OTA fail: firmware version request timeout");
        }
    };


    private final Runnable OTA_TIMEOUT_TASK = new Runnable() {
        @Override
        public void run() {
            resetOta();
            onOtaFailure(OtaStatusCode.FAIL_FLOW_TIMEOUT, "OTA fail: flow timeout");
        }
    };

    public interface GattOtaCallback {
        /**
         * @param statusCode {@link OtaStatusCode}
         */
        void onOtaStatusChanged(int statusCode, String info, GattConnection connection, OtaController controller);

        void onOtaProgressUpdate(int progress, GattConnection connection, OtaController controller);
    }

}

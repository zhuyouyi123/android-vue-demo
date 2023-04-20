package com.ble.blescansdk.ble.analysis;

import android.bluetooth.BluetoothDevice;

import com.ble.blescansdk.ble.entity.seek.DecryptProcessUtil;
import com.ble.blescansdk.ble.entity.seek.SeekBleDevice;
import com.ble.blescansdk.ble.entity.bo.SeekDeviceAnalysisBO;
import com.ble.blescansdk.ble.enums.SKYBeaconPowerEnum;
import com.ble.blescansdk.ble.enums.SKYDeviceTypeEnum;
import com.ble.blescansdk.ble.utils.ProtocolUtil;
import com.ble.blescansdk.ble.utils.SKYBeaconEncryptUtil;
import com.ble.blescansdk.ble.utils.SeekVersionAdaptionUtil;

public class AnalysisSeekBeaconHandle extends AbstractDeviceAnalysis<SeekBleDevice> {

    private static AnalysisSeekBeaconHandle instance = null;

    public static AnalysisSeekBeaconHandle getInstance() {
        if (instance == null) {
            return instance = new AnalysisSeekBeaconHandle();
        }
        return instance;
    }

    @Override
    public SeekBleDevice analysis(byte[] scanBytes, BluetoothDevice device, int rssi) {
        SeekBleDevice seekBleDevice = preHandle(new SeekBleDevice(), device, rssi);
        return handle(scanBytes, seekBleDevice);
    }

    @Override
    protected SeekBleDevice handle(byte[] scanBytes, SeekBleDevice seekBleDevice) {
        return fromScanDataToSKYBeacon(scanBytes, seekBleDevice);
    }


    public SeekBleDevice fromScanDataToSKYBeacon(byte[] scanData, SeekBleDevice seekBleDevice) {
        if (scanData == null) {
            return null;
        }
        SeekDeviceAnalysisBO seekDeviceAnalysisBO = checkResponseData(scanData, seekBleDevice);
        if (null == seekDeviceAnalysisBO) {
            return seekBleDevice;
        }
        // 设置设备类型
        seekBleDevice.setDeviceType(seekDeviceAnalysisBO.getDeviceType().value());

        switch (seekDeviceAnalysisBO.getDeviceType()) {
            case DEVICE_TYPE_IBEACON:
                return analyzeIBeacon(seekDeviceAnalysisBO, scanData, seekBleDevice);
            case DEVICE_TYPE_FOOT_RING:
                break;
            case DEVICE_TYPE_NULL:
                break;
            case BLE_DEVICE_TYPE_SPEED_BEACON:
                break;
            default:
                return seekBleDevice;
        }

        return seekBleDevice;
    }

    /**
     * 获取开始 校验状态
     *
     * @param scanData      扫描数据
     * @param seekBleDevice 寻息设备
     * @return {@link SeekDeviceAnalysisBO}
     */
    private SeekDeviceAnalysisBO checkResponseData(byte[] scanData, SeekBleDevice seekBleDevice) {
        SKYDeviceTypeEnum deviceType = SKYDeviceTypeEnum.DEVICE_TYPE_NULL;

        int startByte = 2;
        int responseStartByte = 34;
        boolean responseFind = false;
        while (startByte <= 5) {
            if (((int) scanData[startByte + 30] & 0xff) == 0x86 && ((int) scanData[startByte + 31] & 0xff) == 0x43) {
                responseStartByte = startByte + 32;
                responseFind = true;
            }
            if (((int) scanData[startByte + 31] & 0xff) == 0x86 && ((int) scanData[startByte + 32] & 0xff) == 0x43) {
                responseStartByte = startByte + 33;
                responseFind = true;
            }
            if (((int) scanData[startByte + 30] & 0xff) == 0x86 && ((int) scanData[startByte + 31] & 0xff) == 0x44) {
                responseStartByte = 34;
                responseFind = true;
            }
            if (((int) scanData[startByte + 2] & 0xff) == 0x02 && ((int) scanData[startByte + 3] & 0xff) == 0x15) {
                // yes! This is an iBeacon
                deviceType = SKYDeviceTypeEnum.DEVICE_TYPE_IBEACON;
                break;
            } else if (((int) scanData[startByte] & 0xff) == 0x2d && ((int) scanData[startByte + 1] & 0xff) == 0x24 && ((int) scanData[startByte + 2] & 0xff) == 0xbf
                    && ((int) scanData[startByte + 3] & 0xff) == 0x16) {
                return null;
            } else if (((int) scanData[startByte] & 0xff) == 0xad && ((int) scanData[startByte + 1] & 0xff) == 0x77 && ((int) scanData[startByte + 2] & 0xff) == 0x00
                    && ((int) scanData[startByte + 3] & 0xff) == 0xc6) {
                return null;
            } else if (((int) scanData[startByte + 2] & 0xff) == 0x86 && ((int) scanData[startByte + 3] & 0xff) == 0x44) {
                deviceType = SKYDeviceTypeEnum.DEVICE_TYPE_FOOT_RING;
                break;
            } else if (((int) scanData[startByte] & 0xff) == 0x86 && ((int) scanData[startByte + 1] & 0xff) == 0x43 && ((int) scanData[startByte + 2] & 0xff) == 0x03
                    && ((int) scanData[startByte + 3] & 0xff) == 0x03) {
                deviceType = SKYDeviceTypeEnum.BLE_DEVICE_TYPE_SPEED_BEACON;
                break;
            }
            startByte++;
        }
        return new SeekDeviceAnalysisBO(deviceType, responseFind, responseStartByte, startByte);
    }


    private SeekBleDevice analyzeIBeacon(SeekDeviceAnalysisBO seekDeviceAnalysisBO, byte[] scanData, SeekBleDevice beacon) {
        boolean isOad = false;
        int startByte = seekDeviceAnalysisBO.getStartByte();
        if (seekDeviceAnalysisBO.getResponseFind()) {
            byte[] decryptScanData;
            decryptScanData = SKYBeaconEncryptUtil.decryptResponseDatas(scanData, seekDeviceAnalysisBO.getResponseStartByte());
            beacon.setSeekBeacon(1);
            int hardwareVersion = decryptScanData[0] & 0x00FF;
            int firmwareVersionMajor = decryptScanData[1] & 0x00FF;
            int firmwareVersionMinor = decryptScanData[2] & 0x00FF;
            beacon.setHardwareVersion(hardwareVersion);
            beacon.setFirmwareVersionMajor(firmwareVersionMajor);
            beacon.setFirmwareVersionMinor(firmwareVersionMinor);
            beacon.setDeviceModel(SeekVersionAdaptionUtil.getProductName(hardwareVersion, firmwareVersionMajor, firmwareVersionMinor));
            // 不在oad模式下
            isOad = checkIsOad(hardwareVersion, firmwareVersionMajor, firmwareVersionMinor, decryptScanData);
            if (!isOad) {
                // 设置光感值
                if (SeekVersionAdaptionUtil.isLightSensorContain(beacon.getHardwareVersion(), beacon.getFirmwareVersionMajor(), beacon.getFirmwareVersionMinor())) {
                    beacon.setLightValue((decryptScanData[24] & 0x00FF) * 4);
                }
                beacon.setTxPower(SKYBeaconPowerEnum.getPower((int) decryptScanData[21]));
                beacon.setSendInterval(setIntervalMillisecond(hardwareVersion, firmwareVersionMajor, decryptScanData));
            }
            beacon.setIsEncrypted((decryptScanData[5] & 0x02) > 0 ? 1 : 0);
            beacon.setIsLocked((decryptScanData[5] & 0x01) > 0x00 ? 1 : 0);
        } else {
            // 没有response
            byte measuredPowerByte = scanData[startByte + 24];
            byte[] bytes = ProtocolUtil.byteToBit(measuredPowerByte);
            // 解析电量 否则不解析
            if (bytes[0] == 0) {
                beacon.setBattery(calcBatteryByMeasurePower(measuredPowerByte));
            }
        }
        if (beacon.getIsEncrypted() != 1 || isOad) {
            beacon.setMajor(((scanData[startByte + 20] & 0x00ff) << 8) + (scanData[startByte + 21] & 0x00ff));
            beacon.setMinor(((scanData[startByte + 22] & 0x00ff) << 8) + (scanData[startByte + 23] & 0x00ff));
        } else {
            setMajorAndMinor(scanData, startByte, beacon);
        }
        beacon.setMeasuredPower(scanData[startByte + 24]);
        beacon.setUuid(getUuid(scanData, startByte));
        beacon.setDistance(solveDistance(beacon.getMeasuredPower(), beacon.getRssi()));
        return beacon;
    }


    /**
     * 校验是否在oad模式下
     *
     * @param hardwareVersion      硬件版本号
     * @param firmwareVersionMajor 主软件版本号
     * @param firmwareVersionMinor 次软件版本号
     * @param decryptScanData      解析数据
     * @return 是否为oad
     */
    private boolean checkIsOad(int hardwareVersion, int firmwareVersionMajor, int firmwareVersionMinor, byte[] decryptScanData) {
        // 判断response协议（是否为OAD Mode下）
        if (SeekVersionAdaptionUtil.isLA(hardwareVersion, firmwareVersionMajor, firmwareVersionMinor) && ((decryptScanData[5] & 0x04) > 0x00)) {
            return (decryptScanData[5] & 0x08) > 0x00;
        } else {
            return false;
        }
    }

    /**
     * 设置发送间隔
     *
     * @param hardwareVersion      硬件版本号
     * @param firmwareVersionMajor 主软件版本号
     * @param decryptScanData      解析数据
     * @return 发送间隔
     */
    private int setIntervalMillisecond(int hardwareVersion, int firmwareVersionMajor, byte[] decryptScanData) {
        if (SeekVersionAdaptionUtil.isIntervalFiveSecondProtocol(hardwareVersion, firmwareVersionMajor)) {
            int tmpInterval = (decryptScanData[22] & 0x00FF);
            if (tmpInterval > 100) {
                return (tmpInterval - 100) * 50 + 1000;
            } else {
                return tmpInterval * 10;
            }
        } else {
            return (decryptScanData[22] & 0x00FF) * 10;
        }
    }

    /**
     * 根据measuredPower计算电量
     *
     * @param measuredPowerByte byte
     * @return 电量
     */
    private static int calcBatteryByMeasurePower(byte measuredPowerByte) {
        int battery;
        switch (measuredPowerByte) {
            case 10:
                battery = 100;
                break;
            case 1:
                battery = 10;
                break;
            case 0:
                battery = 1;
                break;
            default:
                battery = -1;
                break;
        }
        return battery;
    }

    private String getUuid(byte[] scanData, int startByte) {
        byte[] proximityUuidBytes = new byte[16];
        System.arraycopy(scanData, startByte + 4, proximityUuidBytes, 0, 16);
        String hexString = ProtocolUtil.byteArrToHexStr(proximityUuidBytes);
        return hexString.substring(0, 8) +
                "-" +
                hexString.substring(8, 12) +
                "-" +
                hexString.substring(12, 16) +
                "-" +
                hexString.substring(16, 20) +
                "-" +
                hexString.substring(20, 32);
    }

    /**
     * 根据MeasuredPower和rssi解算距离
     *
     * @param measuredPower 场强
     * @param rssi          信号值
     * @return 距离
     */
    public static float solveDistance(int measuredPower, int rssi) {
        float d = 0;

        if (rssi > (measuredPower + 20)) {
            d = (float) 0.1;
        } else if ((rssi > measuredPower) && (rssi <= (measuredPower + 20))) {
            d = (float) (0.002 * (rssi - measuredPower) * (rssi - measuredPower) - 0.0857 * (rssi - measuredPower) + 1); // 0.1至1米
        } else if ((rssi > (measuredPower - 15)) && (rssi <= measuredPower)) {
            d = (float) (0.0133 * (rssi - measuredPower) * (rssi - measuredPower) - 0.0667 * (rssi - measuredPower) + 1); // 1至5米
            // 15分贝4米
        } else if ((rssi > (measuredPower - 25)) && (rssi <= (measuredPower - 15))) {
            d = (float) (0.033 * (rssi - measuredPower) * (rssi - measuredPower) + 0.8187 * (rssi - measuredPower) + 9.8626); // 5至10米
            // 10分贝5米
        } else if ((rssi > (measuredPower - 33)) && (rssi <= (measuredPower - 25))) {
            d = (float) (0.05 * (rssi - measuredPower) * (rssi - measuredPower) + 2.15 * (rssi - measuredPower) + 32.5); // 10至16米
            // 8分贝6米
        } else if ((rssi > (measuredPower - 39)) && (rssi <= (measuredPower - 33))) {
            d = (float) (0.0571 * (rssi - measuredPower) * (rssi - measuredPower) + 3.1143 * (rssi - measuredPower) + 56.5429); // 16至22米
            // 6分贝6米
        } else if (rssi <= (measuredPower - 39)) {
            d = (float) (-1.3 * (rssi - measuredPower + 39) + 22); // 22米以上
            // 1分贝1.3米
        }

        return d;
    }

    private void setMajorAndMinor(byte[] scanData, int startByte, SeekBleDevice beacon) {
        byte[] majorTmp = new byte[]{scanData[startByte + 20], scanData[startByte + 21]};
        byte[] tmp1 = new byte[16];
        byte[] tmp2 = new byte[2];
        if (SeekVersionAdaptionUtil.isEncryptedVersion2(beacon.getHardwareVersion(), beacon.getFirmwareVersionMajor(), beacon.getFirmwareVersionMinor())) {
            byte[] minorTmp = new byte[]{scanData[startByte + 22], scanData[startByte + 23]};
            DecryptProcessUtil.getUuidMajorMinorV2(tmp1, majorTmp, minorTmp);
            beacon.setMajor(((majorTmp[0] & 0x00ff) << 8) + (int) (majorTmp[1] & 0x00ff));
            beacon.setMinor(((minorTmp[0] & 0x00ff) << 8) + (int) (minorTmp[1] & 0x00ff));
        } else if (SeekVersionAdaptionUtil.isEncryptedVersion3(beacon.getHardwareVersion(), beacon.getFirmwareVersionMajor(), beacon.getFirmwareVersionMinor())) {
            beacon.setMajor(((scanData[startByte + 20] & 0x00ff) << 8) + (int) (scanData[startByte + 21] & 0x00ff));
            beacon.setMinor(((scanData[startByte + 22] & 0x00ff) << 8) + (scanData[startByte + 23] & 0x00ff));
        } else {
            DecryptProcessUtil.getUuidMajorMinor(tmp1, majorTmp, tmp2);
            beacon.setMajor((majorTmp[0] & 0xff) * 0x100 + (majorTmp[1] & 0xff));
            beacon.setMinor((scanData[startByte + 22] & 0xff) * 0x100 + (scanData[startByte + 23] & 0xff));
        }
    }

    private void handleMultiIdsBeacon(SeekBleDevice device) {

    }
}

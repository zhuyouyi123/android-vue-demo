/********************************************************************************************************
 * @file OtaPacketParser.java
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

import com.seekcy.otaupgrade.util.Arrays;
import com.seekcy.otaupgrade.util.OtaLogger;

import java.util.Locale;

public class OtaPacketParser {

    private int total;
    private int index = -1;
    private byte[] data;
    private int progress;
    private int pduLength = 16;

    public void set(byte[] data, int pduLen) {
        this.clear();
        this.data = data;
        this.pduLength = pduLen;
        int length = this.data.length;
        if (length % pduLen == 0) {
            total = length / pduLen;
        } else {
            total = (int) Math.floor(((float) length) / pduLen + 1);
        }
    }

    public void clear() {
        this.progress = 0;
        this.total = 0;
        this.index = -1;
        this.data = null;
    }

    public byte[] getFirmwareVersion() {
        if (data.length < 6) return null;
        byte[] version = new byte[4];
        System.arraycopy(data, 2, version, 0, 4);
        return version;
//        return new String(version);
    }

    public boolean hasNextPacket() {
        return this.total > 0 && (this.index + 1) < this.total;
    }

    public boolean isLast() {
        return (this.index + 1) == this.total;
    }

    public int getNextPacketIndex() {
        return this.index + 1;
    }

    public byte[] getNextPacket() {

        int index = this.getNextPacketIndex();
        byte[] packet = this.getPacket(index);
        this.index = index;

        return packet;
    }

    public byte[] getPacket(int index) {

        int length = this.data.length;
        int packetSize;

        if (length > pduLength) {
            if ((index + 1) == this.total) {
                packetSize = length - index * pduLength; // 剩余 data
            } else {
                packetSize = pduLength;
            }
        } else {
            packetSize = length;
        }


        int totalSize;
        if (packetSize == pduLength) {
            totalSize = pduLength + 4; //
        } else {
            int dataSize = (packetSize % 16 == 0) ? packetSize : ((packetSize / 16 + 1) * 16);
            totalSize = dataSize + 4;
            OtaLogger.d("last: " + totalSize);
            //  (int) Math.ceil(((double) packetSize) / 16) * 16;
        }

        byte[] packet = new byte[totalSize];
        for (int i = 0; i < totalSize; i++) {
            packet[i] = (byte) 0xFF;
        }
        System.arraycopy(this.data, index * pduLength, packet, 2, packetSize);

        this.fillIndex(packet, index);
        int crc = this.crc16(packet);
        this.fillCrc(packet, crc);
        OtaLogger.d(String.format(Locale.getDefault(), "ota packet ---> index : %d  total : %d crc : %04X content : %s", index, this.total, crc, Arrays.bytesToHexString(packet, "")));
        return packet;
    }

    public byte[] getCheckPacket() {
        byte[] packet = new byte[16];
        for (int i = 0; i < 16; i++) {
            packet[i] = (byte) 0xFF;
        }

        int index = this.getNextPacketIndex();
        this.fillIndex(packet, index);
        int crc = this.crc16(packet);
        this.fillCrc(packet, crc);
        OtaLogger.d("ota check packet ---> index : " + index + " crc : " + crc + " content : " + Arrays.bytesToHexString(packet, ""));
        return packet;
    }

    public void fillIndex(byte[] packet, int index) {
        int offset = 0;
        packet[offset++] = (byte) (index & 0xFF);
        packet[offset] = (byte) (index >> 8 & 0xFF);
    }

    public void fillCrc(byte[] packet, int crc) {
        int offset = packet.length - 2;
        packet[offset++] = (byte) (crc & 0xFF);
        packet[offset] = (byte) (crc >> 8 & 0xFF);
    }

    public int crc16(byte[] packet) {

        int length = packet.length - 2;
        short[] poly = new short[]{0, (short) 0xA001};
        int crc = 0xFFFF;
        int ds;

        for (int j = 0; j < length; j++) {

            ds = packet[j];

            for (int i = 0; i < 8; i++) {
                crc = (crc >> 1) ^ poly[(crc ^ ds) & 1] & 0xFFFF;
                ds = ds >> 1;
            }
        }

        return crc;
    }

    public boolean invalidateProgress() {

        float a = this.getNextPacketIndex();
        float b = this.total;
        OtaLogger.d("invalidate progress: " + a + " -- " + b);
        int progress = (int) Math.floor((a / b * 100));

        if (progress == this.progress)
            return false;

        this.progress = progress;

        return true;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getIndex() {
        return this.index;
    }
}

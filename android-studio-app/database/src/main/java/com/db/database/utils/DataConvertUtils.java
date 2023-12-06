package com.db.database.utils;

public class DataConvertUtils {

    public static char[] hexChars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String byteToHexStr(byte b) {
        int a = (255 & b) / 16;
        int c = (255 & b) % 16;
        return hexChars[a] + "" + hexChars[c];
    }

    /**
     * 根据长度和开始byte解析
     *
     * @param scanData  数据
     * @param startByte 开始位
     * @param length    长度
     * @return 解析结果
     */
    public static String analysisByStartByte(byte[] scanData, int startByte, int length) {

        return byteArrToHexStr(copyBytes(scanData, startByte, length));
    }

    public static String byteArrToHexStr(byte[] arr) {
        if (arr == null) {
            return null;
        } else {
            StringBuilder builder = new StringBuilder();

            for (byte b : arr) {
                builder.append(byteToHexStr(b));
            }

            return builder.toString();
        }
    }

    /**
     * 根据长度和开始byte复制bytes
     *
     * @param scanData  数据
     * @param startByte 开始位
     * @param length    长度
     * @return bytes
     */
    public static byte[] copyBytes(byte[] scanData, int startByte, int length) {
        byte[] proximityUuidBytes = new byte[length];
        System.arraycopy(scanData, startByte, proximityUuidBytes, 0, length);
        return proximityUuidBytes;
    }

    // 截取byte数组
    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }
}

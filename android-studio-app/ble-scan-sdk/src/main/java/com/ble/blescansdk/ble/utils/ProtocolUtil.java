//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ble.blescansdk.ble.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

/**
 * @author zhuyouyi
 */
public class ProtocolUtil {
    public static char[] hexChars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static byte charToByte(char i) {
        return (byte) "0123456789ABCDEF".indexOf(i);
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

    public static byte[] hexStrToBytes(String hex) {
        if (StringUtils.isNotBlank(hex)) {
            hex = hex.toUpperCase();
            int left = hex.length() % 2;
            int size = hex.length() / 2;
            byte[] bs = new byte[size + left];
            for (int i = 0; i < size; i++) {
                int a = charToByte(hex.charAt(2 * i));
                int b = charToByte(hex.charAt(2 * i + 1));
                bs[i] = (byte) (a << 4 | b);
            }
            if (left == 1) {
                bs[size] = charToByte(hex.charAt(hex.length() - 1));
            }
            return bs;
        }
        return null;
    }

    public static String byteToHexStr(byte b) {
        int a = (255 & b) / 16;
        int c = (255 & b) % 16;
        return hexChars[a] + "" + hexChars[c];
    }

    // byte转换成Int
    public static Integer byteToInt(byte b) {
        int a = (255 & b) / 16;
        int c = (255 & b) % 16;
        return Integer.parseInt(hexChars[a] + "" + hexChars[c], 16);
    }


    public static String getMacHex(long mac) {
        String re = "";

        for (int i = 0; i < 5; ++i) {
            re = re + byteToHexStr((byte) ((int) (mac >> 8 * (5 - i))));
        }

        re = re + byteToHexStr((byte) ((int) mac));
        return re;
    }

    public static long getMacLong(String mac) {
        return Long.parseLong(mac, 16);
    }

    public static void putByteToBuffer(byte[] bs, byte b, int index) {
        bs[index] = b;
    }

    public static void putShortToBuffer(byte[] bs, short s, int index) {
        putByteArrToBuffer(bs, shortToByteArr(s), index);
    }

    public static void putIntToBuffer(byte[] bs, int i, int index) {
        putByteArrToBuffer(bs, intToByteArr(i), index);
    }

    public static void put8LongToBuffer(byte[] bs, long l, int index) {
        putByteArrToBuffer(bs, longTo8LenByteArr(l), index);
    }

    public static void put6LongToBuffer(byte[] bs, long l, int index) {
        putByteArrToBuffer(bs, longTo6LenByteArr(l), index);
    }

    public static void put4LongToBuffer(byte[] bs, long l, int index) {
        putByteArrToBuffer(bs, longTo4LenByteArr(l), index);
    }

    public static void putByteArrToBuffer(byte[] bs, byte[] toAddArr, int index) {
        int i = 0;

        for (int l = toAddArr.length; i < l; ++i) {
            bs[index + i] = toAddArr[i];
        }

    }

    public static byte[] shortToByteArr(short s) {
        byte[] bs = new byte[]{(byte) (s >> 8), (byte) s};
        return bs;
    }

    public static byte[] intToByteArr(int i) {
        byte[] bs = new byte[]{(byte) (i >> 24), (byte) (i >> 16), (byte) (i >> 8), (byte) i};
        return bs;
    }

    public static byte[] intToByteArrayTwo(int in) {
        return new byte[]{(byte) (in >> 8 & 255), (byte) (in & 255)};
    }

    public static long bytes6LenToLong(byte[] bs) {
        return ((long) bs[0] & 255L) << 40 | ((long) bs[1] & 255L) << 32 | ((long) bs[2] & 255L) << 24 | ((long) bs[3] & 255L) << 16 | ((long) bs[4] & 255L) << 8 | (long) bs[5] & 255L;
    }

    public static long bytes8LenToLong(byte[] bs) {
        return ((long) bs[0] & 255L) << 56 | ((long) bs[1] & 255L) << 48 | ((long) bs[2] & 255L) << 40 | ((long) bs[3] & 255L) << 32 | ((long) bs[4] & 255L) << 24 | ((long) bs[5] & 255L) << 16 | ((long) bs[6] & 255L) << 8 | (long) bs[7] & 255L;
    }

    public static byte[] longTo6LenByteArr(long l) {
        byte[] bs = new byte[]{(byte) ((int) (l >> 40)), (byte) ((int) (l >> 32)), (byte) ((int) (l >> 24)), (byte) ((int) (l >> 16)), (byte) ((int) (l >> 8)), (byte) ((int) l)};
        return bs;
    }

    public static byte[] longTo8LenByteArr(long l) {
        byte[] bs = new byte[]{(byte) ((int) (l >> 56)), (byte) ((int) (l >> 48)), (byte) ((int) (l >> 40)), (byte) ((int) (l >> 32)), (byte) ((int) (l >> 24)), (byte) ((int) (l >> 16)), (byte) ((int) (l >> 8)), (byte) ((int) l)};
        return bs;
    }

    private ProtocolUtil() {
    }

    public static long bytes4LenToLong(byte[] bs) {
        return ((long) bs[0] & 255L) << 24 | ((long) bs[1] & 255L) << 16 | ((long) bs[2] & 255L) << 8 | (long) bs[3] & 255L;
    }

    public static short bytes2LenToShort(byte[] bs) {
        return (short) (((short) bs[0] & 255) << 8 | (short) bs[1] & 255);
    }

    public static byte[] longTo4LenByteArr(long l) {
        byte[] bs = new byte[]{(byte) ((int) (l >> 24)), (byte) ((int) (l >> 16)), (byte) ((int) (l >> 8)), (byte) ((int) l)};
        return bs;
    }

    public static byte[] longTo3LenByteArr(long l) {
        byte[] bs = new byte[]{(byte) ((int) (l >> 16)), (byte) ((int) (l >> 8)), (byte) ((int) l)};
        return bs;
    }

    public static byte[] double2Bytes(double d) {
        long value = Double.doubleToRawLongBits(d);
        byte[] byteRet = new byte[8];

        for (int i = 0; i < 8; ++i) {
            byteRet[i] = (byte) ((int) (value >> 8 * i & 255L));
        }

        return byteRet;
    }

    public static double bytes2Double(byte[] arr) {
        long value = 0L;

        for (int i = 0; i < 8; ++i) {
            value |= (long) (arr[i] & 255) << 8 * i;
        }

        return Double.longBitsToDouble(value);
    }

    public static int byteArrayToInt(byte[] array, boolean bigEndian) {
        checkByteArray(array, 4);
        return (int) byteArrayToLong(array, bigEndian);
    }

    public static long byteArrayToLong(byte[] array, boolean bigEndian) {
        checkByteArray(array, 8);
        long value = 0L;
        int len = array.length;
        int i;
        if (bigEndian) {
            for (i = 0; i < len; ++i) {
                value |= (long) (array[i] & 255) << (len - 1 - i) * 8;
            }
        } else {
            for (i = 0; i < len; ++i) {
                value |= (long) (array[i] & 255) << i * 8;
            }
        }

        return value;
    }

    public static byte[] intToByteArray(int value, boolean bigEndian) {
        return longToByteArray((long) value, 4, bigEndian);
    }

    public static byte[] intToByteArray(int value, int len, boolean bigEndian) {
        checkNumberBytes(4, len);
        return longToByteArray((long) value, len, bigEndian);
    }

    public static byte[] longToByteArray(long value, boolean bigEndian) {
        return longToByteArray(value, 8, bigEndian);
    }

    public static byte[] longToByteArray(long value, int len, boolean bigEndian) {
        checkNumberBytes(8, len);
        byte[] array = new byte[len];
        int i;
        if (bigEndian) {
            for (i = 0; i < len; ++i) {
                array[i] = (byte) ((int) (value >> (len - i - 1) * 8));
            }
        } else {
            for (i = 0; i < len; ++i) {
                array[i] = (byte) ((int) (value >> i * 8));
            }
        }

        return array;
    }

    private static void checkByteArray(byte[] array, int bytes) {
        if (array != null && array.length != 0) {
            if (array.length > bytes) {
                throw new IllegalArgumentException("expected: array.length <= " + bytes + ", but was: " + array.length);
            }
        } else {
            throw new IllegalArgumentException("array must be not empty.");
        }
    }

    private static void checkNumberBytes(int expectedBytes, int actualBytes) {
        if (actualBytes < 1 || actualBytes > expectedBytes) {
            throw new IllegalArgumentException("expected: 1 <= len <= " + expectedBytes + ", but was: " + actualBytes);
        }
    }


    public static byte[] byteToBit(byte b) {
        return new byte[]{(byte) (b >> 7 & 1), (byte) (b >> 6 & 1), (byte) (b >> 5 & 1), (byte) (b >> 4 & 1), (byte) (b >> 3 & 1), (byte) (b >> 2 & 1), (byte) (b >> 1 & 1), (byte) (b >> 0 & 1)};
    }

    public static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        int length = bytes.length;
        for (int i = 0; i < length; i++) {
            value |= (int) (bytes[i] & 255) << (length - 1 - i) * 8;
        }
        return value;
    }

    public static int byteArrayToInt(byte[] b, int offset) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }

    public static int getBitOffsetToIntByByte(byte b, int offset) {
        return byteArrayToInt(byteToBit(b), offset);
    }

    // 截取byte数组
    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

    // 计算几个bit）（byte的值）
    public static Integer getLongValue(byte[] bitArr, int start, int length) {
        byte[] result = new byte[length];
        System.arraycopy(bitArr, start, result, 0, length);
        String binaryStr = getBinaryStr(result);
        return Integer.parseInt(binaryStr, 2);
    }

    public static Integer getIntByByte(byte b, int start, int length) {
        return getLongValue(byteToBit(b), start, length);
    }


    private static String getBinaryStr(byte[] bitArray) {
        StringBuilder sb = new StringBuilder();
        if (bitArray.length < 1) {
            return null;
        } else {
            for (int i = 0; i < bitArray.length; ++i) {
                sb.append(bitArray[i]);
            }

            return sb.toString();
        }
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

    public static int hexadecimal16Conversion(String hexadecimalStr) {
        //转化得到的目标数据
        int getDataDecimal = 0;
        //16进制代表数据 4位数字
        if (hexadecimalStr.length() == 4) {
            // 获取第一位。判断是正数还是负数
            int bit1Num = Integer.parseInt(hexadecimalStr.substring(0, 1), 16);
            //小于8是正数
            if (bit1Num < 8) {
                getDataDecimal = Integer.parseInt(hexadecimalStr, 16);
            } else {
                //负数
                //先不全八位
                hexadecimalStr = "FFFF" + hexadecimalStr;
                getDataDecimal = new BigInteger(hexadecimalStr, 16).intValue();
            }
            return getDataDecimal;
        }
        return 0;
    }

    public static double convertCharToShort(char a) {
        int b = a;
        int i = (b & 0x80);
        if (0 != i) {
            b = (b & 0x7f);
            b = ~b;
            b = (b + 1);
            b = (b & 0x7f);
            b = -b;
        }
        return b / 128.0 * 2.0;
    }

    /**
     * byte数组转proximityUuid String
     *
     * @param uuid bytes
     * @return 结果
     */
    public static String byte16ToProximityUuidStr(byte[] uuid) {
        if (null == uuid) {
            return "";
        }
        String hexString = byteArrToHexStr(uuid);
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
     * mac String转byte数组
     *
     * @param macString
     * @return 结果
     */
    public static byte[] macStringToBytes(String macString) {
        byte[] result = new byte[6];

        byte[] tmp0 = new byte[12];
        byte[] tmp1 = null;
        try {
            tmp1 = macString.getBytes("ISO-8859-1");
            int i = 0, j = 0;
            for (; i < tmp1.length; i++) {
                if (i != 2 && i != 5 && i != 8 && i != 11 && i != 14) {
                    if (j < 12) {
                        tmp0[j] = tmp1[i];
                        j++;
                    }
                }
            }
            String tmp2 = new String(tmp0, "ISO-8859-1");
            result = hexStrToBytes(tmp2);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }


    /**
     * byte数组转MacString，格式：00:00:00:00:00:00
     *
     * @param src
     * @return 结果
     */
    public static String bytesToMacString(byte[] src) {
        String hexString = byteArrToHexStr(src);
        return hexString.substring(0, 2) +
                ":" +
                hexString.substring(2, 4) +
                ":" +
                hexString.substring(4, 6) +
                ":" +
                hexString.substring(6, 8) +
                ":" +
                hexString.substring(8, 10) +
                ":" +
                hexString.substring(10, 12);
    }

}

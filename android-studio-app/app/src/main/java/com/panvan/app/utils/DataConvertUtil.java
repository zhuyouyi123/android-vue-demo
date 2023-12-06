package com.panvan.app.utils;

import com.ble.blescansdk.ble.utils.ProtocolUtil;

import java.util.Arrays;

public class DataConvertUtil {

    public static byte[] mergeBytes(byte[] array1, byte[] array2) {
        byte[] mergedArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, mergedArray, 0, array1.length);
        System.arraycopy(array2, 0, mergedArray, array1.length, array2.length);
        return mergedArray;
    }

    public static byte[] getSubArray(byte[] array, int startIndex, int length) {
        if (startIndex < 0 || startIndex >= array.length || length <= 0) {
            throw new IllegalArgumentException("Invalid start index or length.");
        }

        int endIndex = startIndex + length;
        if (endIndex > array.length) {
            throw new IllegalArgumentException("Invalid start index or length.");
        }

        return Arrays.copyOfRange(array, startIndex, endIndex);
    }

    public static boolean checkSum(byte[] bytes) {
        byte[] analysisByStartBytes = ProtocolUtil.copyBytes(bytes, 0, bytes.length - 2);
        byte calcAddSum = ProtocolUtil.calcAddSum(analysisByStartBytes);

        return calcAddSum == bytes[bytes.length - 2];
    }
}

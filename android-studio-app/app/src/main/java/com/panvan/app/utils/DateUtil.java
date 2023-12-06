package com.panvan.app.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;

public class DateUtil {

    public static String getCurrentDateHex(int daysToSubtract) {
        Calendar calendar = Calendar.getInstance();
        // 减去指定的天数
        calendar.add(Calendar.DAY_OF_YEAR, -daysToSubtract);
        int year = calendar.get(Calendar.YEAR) - 2000;
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String hexYear = Integer.toHexString(year).toUpperCase();
        String hexMonth = String.format("%02X", month);
        String hexDay = String.format("%02X", day);
        return hexDay + hexMonth + hexYear;
    }

    public static String getCurrentDateHex() {
        return getCurrentDateHex(0);
    }

    public static long getTodayStartTime() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        return today.getTimeInMillis();
    }

    public static long getTimestamp(int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static boolean isBeforeNow(long dateTime) {
        return dateTime < System.currentTimeMillis();
    }

    // 将long类型转换为4个字节的byte数组
    public static byte[] longToBytes(long value) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt((int) value);
        return buffer.array();
    }

    public static byte[] longToBytes(long value, ByteOrder byteOrder) {
        ByteBuffer buffer = ByteBuffer.allocate(4).order(byteOrder);
        buffer.putInt((int) value);
        return buffer.array();
    }

    public static String getCurrentDateTime() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1; // 注意，月份从 0 开始
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);

        return year + "-" +
                month + "-" +
                day + " " +
                hour + ":" +
                minute + ":" +
                second+": ";
    }

}

package com.db.database.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static boolean isSameDay(String date) {
        return getPreviousDate().equals(date);
    }

    public static boolean isSameDay(Integer date) {
        return getPreviousDate().equals(String.valueOf(date));
    }

    public static String getPreviousDate() {
        return getPreviousDate(0);
    }

    public static Integer getPreviousIntDate() {
        return Integer.parseInt(getPreviousDate(0));
    }

    public static Integer getPreviousIntDate(int daysToSubtract) {
        return Integer.parseInt(getPreviousDate(daysToSubtract));
    }

    public static String getPreviousDate(int daysToSubtract) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -daysToSubtract); // 减去指定的天数

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        Date previousDate = calendar.getTime();

        return sdf.format(previousDate);
    }

    public static String formatDate(byte dayByte, byte monthByte, byte yearByte) {

        // 将十六进制的年月日转换为对应的十进制数值
        int year = (yearByte & 0xff) + 2000;
        int month = monthByte & 0xff;
        int day = dayByte & 0xff;

        // 格式化年月日为字符串
        return String.format(Locale.ENGLISH, "%04d%02d%02d", year, month, day);
    }

    public static Integer formatDateToInt(byte dayByte, byte monthByte, byte yearByte) {
        return Integer.parseInt(formatDate(dayByte, monthByte, yearByte));
    }

    public static String formatDate(byte[] bytes) {
        // 将十六进制的年月日转换为对应的十进制数值
        int year = (bytes[2] & 0xff) + 2000;
        int month = bytes[1] & 0xff;
        int day = bytes[0] & 0xff;

        // 格式化年月日为字符串
        return String.format(Locale.ENGLISH, "%04d%02d%02d", year, month, day);
    }

}

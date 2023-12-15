package com.panvan.app.utils;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                second + ": ";
    }

    private static Calendar parseCalendar(String inputDate) throws ParseException {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = sdf.parse(inputDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static int calculateWeeksToCurrentWeek(String date) {
        Calendar localCalendar;
        int weeks = 0;
        try {
            localCalendar = parseCalendar(date);
            Calendar currentWeekStart = Calendar.getInstance();
            currentWeekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

            while (localCalendar.before(currentWeekStart)) {
                localCalendar.add(Calendar.DATE, 7);
                weeks++;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return weeks + 1;
    }

    public static int getDaysBetweenTodayAndFirstUseTime(int firstUseTime) {
        Calendar today = Calendar.getInstance();
        Calendar firstUseDate = Calendar.getInstance();
        int year = firstUseTime / 10000;
        int month = (firstUseTime % 10000) / 100 - 1; // Calendar中月份是从0开始的
        int day = firstUseTime % 100;
        firstUseDate.set(year, month, day);

        // 将时间设置为当天的第0小时0分0秒
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        firstUseDate.set(Calendar.HOUR_OF_DAY, 0);
        firstUseDate.set(Calendar.MINUTE, 0);
        firstUseDate.set(Calendar.SECOND, 0);
        firstUseDate.set(Calendar.MILLISECOND, 0);

        long millisecondsBetween = today.getTimeInMillis() - firstUseDate.getTimeInMillis();
        int daysBetween = (int) (millisecondsBetween / (1000 * 60 * 60 * 24));
        return daysBetween + 1;
    }

    public static int calculateMonthsToCurrentDate(String date) {
        Calendar localCalendar;
        int months = 0;
        try {
            localCalendar = parseCalendar(date);
            Calendar currentDate = Calendar.getInstance();

            while (localCalendar.before(currentDate)) {
                localCalendar.add(Calendar.MONTH, 1);
                months++;
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return months;
    }

    public static Map<Integer, List<Integer>> getWeeksDates(int weeks) {
        Map<Integer, List<Integer>> weekData = new HashMap<>(weeks);
        Calendar current = Calendar.getInstance();
        for (int i = 0; i < weeks; i++) {
            List<Integer> weekDates = new ArrayList<>();
            Calendar startOfWeek = (Calendar) current.clone();
            startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            for (int j = 0; j < 7; j++) {
                int intDate = startOfWeek.get(Calendar.YEAR) * 10000 + (startOfWeek.get(Calendar.MONTH) + 1) * 100 + startOfWeek.get(Calendar.DAY_OF_MONTH);
                weekDates.add(intDate);
                startOfWeek.add(Calendar.DATE, 1);
            }
            weekData.put(i + 1, weekDates);
            current.add(Calendar.DATE, -7);
        }
        return weekData;
    }

    public static Map<Integer, List<Integer>> getDatesForMonth(int months) {
        Map<Integer, List<Integer>> monthDateMap = new HashMap<>(months);

        for (int i = 1; i <= months; i++) {
            List<Integer> dates = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -months + i);

            calendar.set(Calendar.DAY_OF_MONTH, 1);
            int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int day = 1; day <= maxDay; day++) {
                calendar.set(Calendar.DAY_OF_MONTH, day);
                int intDate = calendar.get(Calendar.YEAR) * 10000 + (calendar.get(Calendar.MONTH) + 1) * 100 + calendar.get(Calendar.DAY_OF_MONTH);
                dates.add(intDate);
            }
            monthDateMap.put(i + 1, dates);
        }

        return monthDateMap;
    }

    public static String convertDate(String inputDate) {
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd");
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "日期格式转换错误";
        }
    }

}

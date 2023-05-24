package com.ble.blescansdk.ble.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class TimeUtil {


    @SuppressLint("DefaultLocale")
    public static String convertTimeToDay(long milliseconds) {
        milliseconds = milliseconds * 100;
        final long day = TimeUnit.MILLISECONDS.toDays(milliseconds);
        final long hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(milliseconds));
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds));
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds));
        final long ms = TimeUnit.MILLISECONDS.toMillis(milliseconds)
                - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(milliseconds));
        return String.format("%d days %d:%d:%d.%d", day, hours, minutes, seconds, ms);
    }


    /**
     * 获取系统时间
     *
     * @return 结果
     */
    public static long getCurrentTimestampMillisecond() {
        return System.currentTimeMillis();
    }

    /**
     * 获取系统时间，精确到天
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getCurrentTimestampDay() {
        String result = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        result = formatter.format(new java.util.Date());
        return result;
    }

    /**
     * 获取系统时间，精确到秒
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getCurrentTimestampSecond() {
        String result = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        result = formatter.format(new java.util.Date());
        return result;
    }

    /**
     * 7个字节分别为：0:秒 1:分 2:时 3:星期 4:日 5:月 6:年
     *
     * @return 结果
     */
    public static byte[] getCurrentTimeBytes() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR) - 2000;
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int week = c.get(Calendar.DAY_OF_WEEK);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        byte[] re = new byte[7];
        re[6] = digitTo2SegmentHex((byte) (year));
        re[5] = digitTo2SegmentHex((byte) month);
        re[4] = digitTo2SegmentHex((byte) day);
        re[3] = digitTo2SegmentHex((byte) week);
        re[2] = digitTo2SegmentHex((byte) hour);
        re[1] = digitTo2SegmentHex((byte) minute);
        re[0] = digitTo2SegmentHex((byte) second);

        return re;
    }

    private static byte digitTo2SegmentHex(byte b) {
        byte r = (byte) ((b / 10) << 4);
        r |= (byte) ((0xff & b) % 10);
        return r;
    }

}

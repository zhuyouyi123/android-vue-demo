package com.seek.config.utils;

import android.annotation.SuppressLint;

import com.ble.blescansdk.ble.utils.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtil {

    @SuppressLint("SimpleDateFormat")
    public static final DateFormat[] DF = new SimpleDateFormat[]{
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZZZ"),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
            new SimpleDateFormat("yyyy-MM-dd HH:mm"),
            new SimpleDateFormat("yyyy-MM-dd"),
    };

    /**
     * 日期转换
     *
     * @param val      时间
     * @param tryCount tryCount
     * @return 格式化时间
     * @throws Exception 异常
     */
    public static Date parseDate(String val, int tryCount) throws Exception {
        if (tryCount >= DF.length) {
            return null;
        }
        try {
            return DF[tryCount].parse(val);
        } catch (ParseException e) {
            tryCount++;
            return parseDate(val, tryCount);
        }
    }

    public static long stringDateFormatLong(String dateTime) {
        if (StringUtils.isNotBlank(dateTime) && dateTime.contains("T")) {
            dateTime=  dateTime.replaceAll("T"," ");
        }
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date = (Date) sdf.parse(dateTime);
            if (null == date) {
                return 0;
            }
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}

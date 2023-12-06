package com.panvan.app.data.holder.chart;

import com.panvan.app.data.constants.ChartConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChartInfo<T> {

    private final List<String> xAxis = ChartConstants.HOURS_OF_DAY;

    private List<T> hourlyData;

    private Integer date;

    public ChartInfo(Integer date,List<T> hourlyData) {
        this.date = date;
        this.hourlyData = hourlyData;
    }

    public List<String> getxAxis() {
        return xAxis;
    }

    public List<T> getHourlyData() {
        return hourlyData;
    }

    public void setHourlyData(List<T> hourlyData) {
        this.hourlyData = hourlyData;
    }


    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }
}

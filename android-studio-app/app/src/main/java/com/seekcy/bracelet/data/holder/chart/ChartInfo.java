package com.seekcy.bracelet.data.holder.chart;

import java.util.List;

public class ChartInfo<T> {

    private List<T> hourlyData;

    private Integer date;

    public ChartInfo(Integer date, List<T> hourlyData) {
        this.date = date;
        this.hourlyData = hourlyData;
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

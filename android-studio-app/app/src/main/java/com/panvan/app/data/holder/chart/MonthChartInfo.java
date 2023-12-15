package com.panvan.app.data.holder.chart;

import java.util.List;

public class MonthChartInfo<T> {
    private List<String> xAxis;
    // 每周
    private List<T> dayData;

    public MonthChartInfo(List<String> xAxis, List<T> dayData) {
        this.xAxis = xAxis;
        this.dayData = dayData;
    }

    public List<String> getxAxis() {
        return xAxis;
    }

    public void setxAxis(List<String> xAxis) {
        this.xAxis = xAxis;
    }

    public List<T> getDayData() {
        return dayData;
    }

    public void setDayData(List<T> dayData) {
        this.dayData = dayData;
    }
}

package com.panvan.app.data.holder.chart;

import java.util.List;

public class WeekChartInfo<T> {
    // 每天数据
    private List<T> dayData;

    public WeekChartInfo( List<T> dayData) {
        this.dayData = dayData;
    }

    public List<T> getDayData() {
        return dayData;
    }

    public void setDayData(List<T> dayData) {
        this.dayData = dayData;
    }

}

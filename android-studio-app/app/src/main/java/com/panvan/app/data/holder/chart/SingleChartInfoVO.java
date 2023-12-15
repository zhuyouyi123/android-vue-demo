package com.panvan.app.data.holder.chart;

import java.util.List;

public class SingleChartInfoVO<T> {

    private Integer chartSize;

    private List<T> list;

    private Integer dataIndex;

    private String average;

    private String max;
    private String min;


    public Integer getChartSize() {
        return chartSize;
    }

    public void setChartSize(Integer chartSize) {
        this.chartSize = chartSize;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Integer getDataIndex() {
        return dataIndex;
    }

    public void setDataIndex(Integer dataIndex) {
        this.dataIndex = dataIndex;
    }

    public String getAverage() {
        return average;
    }

    public void setAverage(String average) {
        this.average = average;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }
}

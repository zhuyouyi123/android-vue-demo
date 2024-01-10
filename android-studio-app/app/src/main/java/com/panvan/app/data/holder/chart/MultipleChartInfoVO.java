package com.panvan.app.data.holder.chart;

public class MultipleChartInfoVO<T> {

    private Integer chartSize;

    private T data;

    private Integer dataIndex;

    private String average = "0-0";

    private String max = "0-0";
    private String min = "0-0";

    private final Boolean isMultiple = true;

    private Boolean needSoar = false;

    public Integer getChartSize() {
        return chartSize;
    }

    public void setChartSize(Integer chartSize) {
        this.chartSize = chartSize;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
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

    public Boolean getNeedSoar() {
        return needSoar;
    }

    public void setNeedSoar(Boolean needSoar) {
        this.needSoar = needSoar;
    }
}

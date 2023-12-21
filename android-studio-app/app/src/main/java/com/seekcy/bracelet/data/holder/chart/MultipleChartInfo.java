package com.seekcy.bracelet.data.holder.chart;

import java.util.List;

public class MultipleChartInfo<T> {

    private List<T> dataList;

    private Integer date;

    private List<String> xAxis;

    private List<String> originalData;

    public MultipleChartInfo(Integer date,List<T> dataList) {
        this.dataList = dataList;
        this.date = date;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public List<String> getxAxis() {
        return xAxis;
    }

    public void setxAxis(List<String> xAxis) {
        this.xAxis = xAxis;
    }

    public List<String> getOriginalData() {
        return originalData;
    }

    public void setOriginalData(List<String> originalData) {
        this.originalData = originalData;
    }
}

package com.seekcy.bracelet.data.holder.statistics;

import com.seekcy.bracelet.data.holder.chart.ChartInfo;

import java.util.List;
import java.util.Objects;

public class StepStatisticsInfo<T> extends ChartInfo<T> {


    private ExtendedInfo extendedInfo;

    public StepStatisticsInfo(Integer date, List<T> hourlyData) {
        super(date, hourlyData);
    }

    private static class ExtendedInfo {
        /**
         * 总里程
         */
        private String totalMileage;

        /**
         * 总用时
         */
        private String totalTime;
        /**
         * 总消耗
         */
        private String totalConsumption;
    }

    public ExtendedInfo getExtendedInfo() {
        if (Objects.isNull(extendedInfo)) {
            extendedInfo = new ExtendedInfo();
        }
        return extendedInfo;
    }

    public void setExtendedInfo(ExtendedInfo extendedInfo) {
        this.extendedInfo = extendedInfo;
    }
}

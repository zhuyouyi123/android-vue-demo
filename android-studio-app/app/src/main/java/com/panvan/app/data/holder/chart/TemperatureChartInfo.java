package com.panvan.app.data.holder.chart;

import java.util.List;

public class TemperatureChartInfo<T> extends ChartInfo<T> {
    public TemperatureChartInfo(Integer date, List<T> hourlyData) {
        super(date, hourlyData);
        this.extendedInfo = new ExtendedInfo(0.0, 0.0, 0.0);
    }

    private ExtendedInfo extendedInfo;

    public ExtendedInfo getExtendedInfo() {
        return extendedInfo;
    }

    public void setExtendedInfo(ExtendedInfo extendedInfo) {
        this.extendedInfo = extendedInfo;
    }

    public static class ExtendedInfo {
        private Double ave;
        private Double max;
        private Double min;

        public ExtendedInfo(Double ave, Double max, Double min) {
            this.ave = ave;
            this.max = max;
            this.min = min;
        }

        public Double getAve() {
            return ave;
        }

        public void setAve(Double ave) {
            this.ave = ave;
        }

        public Double getMax() {
            return max;
        }

        public void setMax(Double max) {
            this.max = max;
        }

        public Double getMin() {
            return min;
        }

        public void setMin(Double min) {
            this.min = min;
        }
    }


}

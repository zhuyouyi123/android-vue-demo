<template>
  <div
    ref="chart"
    :style="{
      width: `${width}px`,
      height: `${height}px`,
    }"
  ></div>
</template>

<script>
import * as echarts from "echarts";

export default {
  name: "LineChart",
  props: {
    width: {
      type: Number,
      default: window.innerWidth - 28,
    },
    height: {
      type: Number,
      default: window.innerWidth / 1.8 - 11,
    },
    data: {
      type: Array,
      required: true,
    },
    lineColor: {
      type: String,
      default: "#000",
    },
    type: {
      type: String,
      default: "line",
    },

    // 是否平滑
    smooth: {
      type: Boolean,
      default: false,
      required: false,
    },
  },
  mounted() {
    this.renderChart();
  },
  methods: {
    renderChart() {
      const chart = echarts.init(this.$refs.chart);

      const option = {
        xAxis: {
          type: "category",
          data: this.data.map((item) => item.name),
        },
        yAxis: {
          type: "value",
        },
        series: [
          {
            type: "line",
            data: this.data.map((item) => item.value),
            lineStyle: {
              color: this.lineColor,
            },
          },
        ],
      };

      chart.setOption(option);
    },
  },
};
</script>

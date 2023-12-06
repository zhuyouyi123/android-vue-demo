<template>
  <div ref="speedometer" style="width: 180px; height: 180px"></div>
</template>

<script>
import * as echarts from "echarts";

export default {
  props: {
    data: {
      type: Number,
      default: 70,
    },
  },

  mounted() {
    this.renderChart();
  },
  methods: {
    renderChart() {
      const chartDom = this.$refs.speedometer;
      const myChart = echarts.init(chartDom);
      const option = {
        series: [
          {
            type: "gauge",
            progress: {
              show: true,
              width: 10,
              itemStyle: {
                color: "#F09A37", // 设置进度条的颜色为黄色
              },
            },
            axisLine: {
              lineStyle: {
                width: 10,
              },
            },
            axisTick: {
              show: false,
            },
            splitLine: {
              show: false,
              lineStyle: {
                width: 2,
                color: "#999",
              },
            },
            axisLabel: {
              show: false,
              distance: 25,
              color: "#999",
              fontSize: 10,
            },
            anchor: {
              itemStyle: {
                borderWidth: 10,
              },
            },
            pointer: {
              itemStyle: {
                color: "grey", // 设置指针颜色为灰色
              },
            },
            title: {
              show: false,
            },
            detail: {
              show: true,
              valueAnimation: false,
              fontSize: 14,
              offsetCenter: [0, "70%"],
              formatter: function (value) {
                if (value > 50) {
                  return "非常疲惫";
                } else {
                  return "舒适";
                }
              },
            },
            data: [
              {
                value: this.data,
              },
            ],
          },
        ],
      };
      option && myChart.setOption(option);
    },
  },
};
</script>

// echartsComponent.js
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
  name: "EChartsComponent",
  props: {
    options: {
      type: Object,
      required: true,
    },
    width: {
      type: Number,
      default: window.innerWidth - 20,
    },
    height: {
      type: Number,
      default: window.innerWidth / 1.6,
    },
    type: {
      type: String,
      default: "line",
    },

    // 时间间隔
    timeInterval: {
      type: Boolean,
      default: false,
    },

    // 隐藏X
    xAxisHide: {
      type: Boolean,
    },
    // 隐藏Y
    yAxisHide: {
      type: Boolean,
      default: false,
    },

    axisLabelInterval: {
      type: String,
    },

    // 柱形图间隔
    barGapHide: {
      type: Boolean,
      default: false,
      required: false,
    },
    // 是否平滑
    smooth: {
      type: Boolean,
      default: false,
      required: false,
    },
    tooltip: {
      type: Boolean,
      default: false,
    },
    tooltipTitle: {
      type: Array,
      default: function () {
        return [];
      },
    },
    tooltipUnit: {
      type: String,
      default: "",
    },

    areaStyleGradientBcg: {
      type: Boolean,
      default: false,
    },
    lineStyleColor: {
      type: Array,
      default: () => {
        return ["#1DA772"];
      },
    },
  },
  data() {
    return {
      tooltipFontSize: "32px", // 设置 tooltip 字体大小
      myChart: null,
    };
  },
  mounted() {
    this.renderChart();
  },

  watch: {
    options: {
      handler() {
        this.renderChart();
      },
      deep: true,
    },
  },

  methods: {
    renderChart() {
      const chartDom = this.$el;

      if (
        this.myChart != null &&
        this.myChart != "" &&
        this.myChart != undefined
      ) {
        this.myChart.dispose();
      }

      this.myChart = echarts.init(chartDom);

      this.handleOptions();

      this.myChart.setOption(this.options, true);
    },

    handleOptions() {
      if (!this.options.xAxis.type) {
        this.options.xAxis.type = "category";
      }

      for (let i = 0; i < this.options.series.length; i++) {
        const e = this.options.series[i];
        // 设置为true以绘制平滑的曲线
        e.smooth = this.smooth;
        e.symbol = "none";

        if (this.type == "line") {
          // 设置折线颜色
          e.lineStyle = {
            color:
              this.lineStyleColor.length == 1
                ? this.lineStyleColor[0]
                : this.lineStyleColor[i],
          };

          if (this.areaStyleGradientBcg) {
            e.areaStyle = {
              color: {
                type: "linear",
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  {
                    offset: 0,
                    color: e.lineStyle.color, // 渐变起始色
                  },
                  {
                    offset: 1,
                    color: "#FFFFFF", // 渐变结束色
                  },
                ],
              },
            };
          }
        } else {
          // 如果是睡眠数据
          if (this.options.sleepData && this.options.sleepData.length > 0) {
            for (let j = 0; j < e.data.length; j++) {
              let sdv = e.data[j];
              const d = this.options.sleepData[j];
              if (d == 1) {
                this.options.series[i].data[j] = !sdv ? 2000 : 5001;
              } else if (d == 2) {
                this.options.series[i].data[j] = !sdv ? 2000 : 5002;
              } else if (d == 3) {
                this.options.series[i].data[j] = !sdv ? 2000 : 5003;
              } else {
                this.options.series[i].data[j] = 2000;
              }
            }

            e.itemStyle = {
              normal: {
                color: function (params) {
                  // 根据数值设置颜色
                  if (params.value === 5001) {
                    return "#6C7AFF";
                  } else if (params.value === 5002) {
                    return "#AEB4FF";
                  } else if (params.value === 5003) {
                    return "#FED567";
                  } else {
                    return "#3641a1";
                  }
                },
              },
            };
          } else {
            if (!e.itemStyle || !e.itemStyle.color) {
              e.itemStyle = {
                color: "#1DA772",
              };
            }
          }

          if (this.barGapHide) {
            e.barGap = 0;
            e.barCategoryGap = -0.2;
          }
        }

        if (!e.type) {
          e.type = this.type;
        }
      }

      // 设置y显示还是隐藏
      this.options.xAxis.show = !this.xAxisHide ? true : false;
      // 强制显示所有标签
      this.options.xAxis.axisLabel = {
        interval:
          this.axisLabelInterval && this.axisLabelInterval > 0
            ? parseInt(this.axisLabelInterval)
            : "auto",
        // 隐藏刻度标签
        show: true,
      };

      this.options.xAxis.axisTick = {
        // 隐藏刻度线
        show: false,
      };

      if (!this.options.yAxis) {
        this.options.yAxis = {
          type: "value",
        };
      }

      // 设置y轴的分割段数为5
      this.options.yAxis.splitNumber = 4;

      // 设置y显示还是隐藏
      this.options.yAxis.show = !this.yAxisHide ? true : false;

      this.options.yAxis.axisLabel = {
        formatter: function (value) {
          // 使用 toFixed 方法去掉小数点并格式化成不带逗号的形式
          return value.toFixed(0);
        },
        fontSize: 10,
      };

      if (!this.options.grid) {
        this.options.grid = {
          // // 设置上边距为 10%
          top: "13%",
          // // 设置下边距为 10%
          bottom: "13%",
          left: "13%",
        };
      }

      let tooltipUnit = this.tooltipUnit;
      let lineStyleColor = this.lineStyleColor;
      let xAxisData = this.options.xAxis.data;
      let that = this;
      this.options.tooltip = {
        borderWidth: 0, // 设置边框宽度为0
        show: this.tooltip,
        padding: [0, 0], // 设置内边距
        trigger: "axis", // 设置触发类型为坐标轴触发
        formatter: function (params) {
          let result = "";
          let time = params[0].axisValueLabel;
          if (that.timeInterval) {
            for (let i = 0; i < xAxisData.length; i++) {
              const item = xAxisData[i];
              if (item == time) {
                if (i == 0) {
                  return;
                } else {
                  time = xAxisData[i - 1] + "-" + item;
                  break;
                }
              }
            }
          }

          result +=
            '<div style="background-color: black; padding: 5px; border-radius: 5px; color: white; font-size: 14px;">';
          result +=
            '<div style="text-align: center; font-weight: bold">' +
            time +
            "</div>";

          for (let i = 0; i < params.length; i++) {
            let seriesName = params[i].seriesName;
            let value = params[i].value;

            result +=
              '<div style="display: flex; align-items: center; margin-top: 5px;">';
            result +=
              '<div style="background-color: ' +
              lineStyleColor[i] +
              '; width: 10px; height: 10px; margin-right: 5px; border: none;"></div>';
            result +=
              '<div style="flex: 1;">' +
              seriesName +
              ': </div><div style="font-weight: bold;">' +
              value +
              tooltipUnit +
              "</div>";
            result += "</div>";
          }

          result += "</div>";
          return result;
        },
      };
    },
  },
};
</script>

// chartOptions: { // xAxis: { // type: 'category', // data: ['9', '10', '11',
'12', '13', '14', '今'] // }, // yAxis: { // type: 'value' // }, // series: [{
// data: [1000, 2000, 1500, 3000, 2500, 1800, 2200], // type: 'line' //
也可以是其他类型，比如 'bar' // }] // },

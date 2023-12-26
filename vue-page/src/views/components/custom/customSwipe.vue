<!-- 自定义swipe 轮播 -->
<template>
  <div>
    <div class="tabs-box date-stitching l-m-t">
      <van-tabs
        v-model="timeActive"
        @click="onClickTab"
        type="card"
        color="#1DA772"
      >
        <van-tab title="日"></van-tab>
        <van-tab title="周"></van-tab>
        <van-tab title="月"></van-tab>
      </van-tabs>
    </div>

    <div class="date-heart-num base-space-between-box">
      <div class="date">{{ dateText }}</div>
      <div class="heart-num">
        <span>{{ realData }}</span
        >{{ unit }}
      </div>
    </div>
    <van-swipe
      class="my-swipe"
      :initial-swipe="chartOptionsArray.length - 1"
      :loop="false"
      :show-indicators="false"
      @change="vanSwipeChange"
    >
      <van-swipe-item
        v-for="(item, index) in chartOptionsArray"
        :key="item.date"
      >
        <echarts-component
          v-if="chartOptionsArray[index].show"
          :options="chartOptionsArray[index]"
          :height="pageWidth - 190"
          :axisLabelInterval="timeActive == 0 ? axisLabelInterval : null"
          :timeInterval="timeActive == 0"
          :tooltip="tooltip"
          areaStyleGradientBcg
          :yAxisMin="yAxisMin"
          :type="chartType"
          :lineStyleColor="lineStyleColor"
          :tooltipTitle="tooltipTitle"
          @toolTipValueResponse="toolTipValueResponse"
        />

        <div
          v-if="!chartOptionsArray[index].show"
          class="no-data b-d-c"
          :style="{
            height: `${pageWidth - 190}px`,
          }"
        >
          暂无数据
        </div>

        <div class="mark-box" v-show="type == '0E'">
          <div class="low-box">
            <div class="round-box round-box-low"></div>
            <div class="name">低压</div>
          </div>
          <div class="high-box">
            <div class="round-box round-box-high"></div>
            <div class="name">高压</div>
          </div>
        </div>
      </van-swipe-item>
    </van-swipe>
  </div>
</template>

<script>
import echartsComponent from "@/components/Charts/echartsComponent.vue";
import chartUtil from "@/utils/chartUtil";
export default {
  data() {
    return {
      pageWidth: window.innerWidth,
      chartOptionsArray: [],
      chartSize: 1,
      // 数据时间类型 1 天 2 周 3 月
      dataDateType: 1,
      // 日期显示
      dateText: "",
      timeActive: 0,
      currIndex: -1,
      extendedInfo: {
        average: 0,
      },
      yAxisMin: 0,
      realData: 0,
      unit: "",
    };
  },

  props: {
    type: {
      type: String,
      required: true,
    },
    chartType: {
      type: String,
      default: "bar",
    },
    // x间隔
    axisLabelInterval: {
      type: Number,
      default: 5,
    },

    timeInterval: {
      type: Boolean,
      default: false,
    },
    tooltip: {
      type: Boolean,
      default: true,
    },

    tooltipName: {
      type: String,
    },

    xAxisData: {
      type: Array,
    },

    lineStyleColor: {
      type: Array,
    },

    tooltipTitle: {
      type: Array,
    },
  },

  components: { echartsComponent },

  mounted() {
    // 设置显示日期
    this.setDateText();
    this.calcChartData();
    this.setUnit();
  },

  methods: {
    onClickTab(e) {
      this.chartOptionsArray = [];
      this.currIndex = -1;
      // 设置显示日期
      this.setDateText();
      this.changeDateType(e);
      this.$emit("tabsChange", e);
    },

    changeDateType(type) {
      type = type + 1;
      this.dataDateType = type;
      this.calcChartData();
      this.setUnit();
    },

    calcChartData() {
      chartUtil
        .calcChartData(
          this.type,
          this.xAxisData,
          this.timeActive == 0
            ? this.tooltipName
              ? this.tooltipName
              : this.tooltipTitle
            : this.tooltipTitle && this.tooltipTitle.length > 0
            ? this.tooltipTitle
            : this.tooltipName,
          this.dataDateType,
          this.currIndex
        )
        .then((res) => {
          this.chartSize = res.chartSize;
          this.chartOptionsArray = [...res.chartOptionsArray];
          this.setExtendedInfo(res.extendedInfo);

          this.$emit("extendedInfoResponse", res.extendedInfo);
        });
    },

    vanSwipeChange(e) {
      this.currIndex = e;
      this.setDateText(this.chartSize - (e + 1));
      setTimeout(() => {
        this.calcChartData();
      }, 300);
    },

    /**
     * 设置日期文本
     */
    setDateText(e) {
      this.dateText = this.$dateUtil.getDateTextByActive(this.timeActive, e);
    },

    setExtendedInfo(extendedInfo) {
      this.extendedInfo.average = extendedInfo.average;
      this.realData = extendedInfo.average;
    },

    toolTipValueResponse(e) {
      this.$emit("toolTipValueResponse", e);
      this.setRealData(e);
    },

    setUnit() {
      this.yAxisMin = 0;
      switch (this.type) {
        case "04":
          if (this.timeActive == 0) {
            this.unit = "步/小时";
          } else {
            this.unit = "步/天";
          }
          break;
        case "07":
          this.unit = "次/分钟";
          break;
        case "09":
          this.unit = "%";
          break;
        case "0B":
          this.unit = "℃";
          this.yAxisMin = 10;
          break;
        case "0E":
          this.unit = "mmHg";
          break;
        default:
          break;
      }
    },

    setRealData(value) {
      switch (this.type) {
        case "04":
          this.realData = value[0];
          break;
        case "07":
          if (this.timeActive != 0) {
            this.realData =
              value[0] + "-" + (parseInt(value[0]) + parseInt(value[1]));
          } else {
            this.realData = value[0];
          }
          break;
        case "09":
          this.realData = value[0];
          break;
        case "0B":
          if (this.timeActive != 0) {
            this.realData =
              value[0] + "-" + (parseFloat(value[0]) + parseFloat(value[1]));
          } else {
            this.realData = value[0];
          }
          break;
        case "0E":
          this.realData = value[0] + "-" + value[1];
          break;
        default:
          break;
      }
    },
  },
};
</script>
<style lang="scss" scoped>
.date-heart-num {
  margin-top: 0.22rem;
  margin-bottom: 0.1rem;
}

.mark-box {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-right: 2.16rem;
  margin-left: 2.12rem;
  margin-bottom: 0.2rem;
  margin-top: 0.1rem;
  font-size: 0.3rem;
  .round-box {
    width: 0.27rem;
    height: 0.03rem;
    margin: 0.1rem;
  }
  .round-box-low {
    background-color: #1da772;
  }
  .round-box-high {
    background-color: #fc5a58;
  }
  .low-box {
    display: flex;
    align-items: center;
    color: #1da772;
  }
  .high-box {
    display: flex;
    align-items: center;
    color: #fc5a58;
  }
}
</style>

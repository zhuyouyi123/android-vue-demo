<!-- 心率 -->
<template>
  <div class="heart-rate-page">
    <custom-nav-bar title="心率" left-arrow> </custom-nav-bar>

    <div class="page-content">
      <div class="tabs-box date-stitching">
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
        <div class="heart-num"><span>95</span>次/分</div>
      </div>

      <!-- 折线图 -->
      <custom-swipe
        type="07"
        tooltipName="心率"
        :xAxisData="times"
        chartType="line"
        axisLabelInterval="-1"
      >
      </custom-swipe>

      <!-- 统计 -->
      <div class="statistics round l-m-t">
        <div class="base-title">日统计</div>

        <div class="base-space-between-box">
          <div class="unit">平均心率</div>
          <div><span>76</span>次/分</div>
        </div>
        <div class="base-space-between-box">
          <div class="unit">最高心率</div>
          <div><span>120</span>次/分</div>
        </div>
        <div class="base-space-between-box">
          <div class="unit">最低心率</div>
          <div><span>55</span>次/分</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import customNavBar from "../custom/customNavBar.vue";
import echartsComponent from "@/components/Charts/echartsComponent.vue";
import customSwipe from "../custom/customSwipe.vue";
export default {
  name: "heartRate",
  data() {
    return {
      timeActive: 0,
      // 日期显示
      dateText: "",
      pageWidth: window.innerWidth,
      times: [],
    };
  },

  components: { customNavBar, echartsComponent, customSwipe },

  created() {
    this.generateTimeData();
  },

  mounted() {
    // 设置显示日期
    this.setDateText();
  },

  methods: {
    onClickTab() {
      // 设置显示日期
      this.setDateText();
    },

    generateTimeData() {
      for (let hour = 0; hour < 24; hour++) {
        for (let minute = 0; minute < 60; minute += 5) {
          const formattedHour = hour.toString().padStart(2, "0");
          const formattedMinute = minute.toString().padStart(2, "0");
          this.times.push(`${formattedHour}:${formattedMinute}`);
        }
      }
    },

    /**
     * 设置日期文本
     */
    setDateText() {
      this.dateText = this.$dateUtil.getDateTextByActive(this.timeActive);
    },

    currentActiveText() {
      if (this.timeActive == 0) {
        return "日";
      } else if (this.timeActive == 1) {
      }
    },
  },
};
</script>
<style lang="scss" scoped>
.page-content {
  .tabs-box {
    margin-top: 0.2rem;
  }

  .date-heart-num {
    margin-top: 0.22rem;
    margin-bottom: 0.1rem;
  }

  .heart-chart {
    height: 3.96rem;
    background: #ffffff;
    margin-top: 0.1rem;
  }

  //   统计、
  .statistics {
    height: 2.76rem;
    background: #ffffff;
    .base-title {
      margin-bottom: 0.1rem;
    }
    .base-space-between-box {
      margin: 0 0.27rem 0.05rem 0.27rem;
    }
  }
}
</style>

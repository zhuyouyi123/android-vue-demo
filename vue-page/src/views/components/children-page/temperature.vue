<!-- 心率 -->
<template>
  <div class="temperature-rate-page">
    <custom-nav-bar title="体温" left-arrow> </custom-nav-bar>

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

      <div class="date-temperature-num base-space-between-box">
        <div class="date">{{ dateText }}</div>
        <div class="temperature-num"><span>36.5</span>℃</div>
      </div>

      <!-- 折线图 -->
      <div class="temperature-chart round">
        <custom-swipe
          type="0B"
          tooltipName="体温"
          @chartResponse="chartResponse"
        >
        </custom-swipe>
      </div>

      <!-- 统计 -->
      <div class="statistics round l-m-t">
        <div class="base-title">日统计</div>

        <div class="base-space-between-box">
          <div class="unit">平均体温</div>
          <div>
            <span>{{ info.ave }}</span
            >℃
          </div>
        </div>
        <div class="base-space-between-box">
          <div class="unit">最高体温</div>
          <div>
            <span>{{ info.max }}</span
            >℃
          </div>
        </div>
        <div class="base-space-between-box">
          <div class="unit">最低体温</div>
          <div>
            <span>{{ info.min }}</span
            >℃
          </div>
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
  name: "temperature",
  data() {
    return {
      timeActive: 0,
      // 日期显示
      dateText: "",
      pageWidth: window.innerWidth,
      chartSize: 0,
      chartOptionsArray: [{}],
      info: {
        max: "--",
        min: "--",
        ave: "--",
      },
    };
  },

  components: { customNavBar, echartsComponent, customSwipe },

  mounted() {
    // 设置显示日期
    this.setDateText();
  },

  methods: {
    onClickTab() {
      // 设置显示日期
      this.setDateText();
    },

    /**
     * 设置日期文本
     */
    setDateText() {
      this.dateText = this.$dateUtil.getDateTextByActive(this.timeActive);
    },

    chartResponse(e) {
      if (e) {
        this.info = {
          max: e.max,
          min: e.min,
          ave: e.ave,
        };
      } else {
        this.info = {
          max: "--",
          min: "--",
          ave: "--",
        };
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

  .date-temperature-num {
    margin-top: 0.22rem;
    margin-bottom: 0.1rem;
  }

  .temperature-chart {
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

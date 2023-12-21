<!-- 心率 -->
<template>
  <div class="heart-rate-page">
    <custom-nav-bar
      :returnRouter="$deviceHolder.homeReturnRouter"
      title="心率"
      left-arrow
    >
    </custom-nav-bar>

    <div class="page-content">
      <!-- 折线图 -->
      <custom-swipe
        ref="customSwipe"
        type="07"
        tooltipName="心率"
        :tooltipTitle="['最低', '最高']"
        :lineStyleColor="['#1DA772', '#EC526A']"
        :xAxisData="times"
        :chartType="timeActive == 0 ? 'line' : 'bar'"
        :axisLabelInterval="null"
        @extendedInfoResponse="extendedInfoResponse"
        @tabsChange="tabsChange"
      >
      </custom-swipe>

      <!-- 统计 -->
      <div class="statistics round l-m-t">
        <div class="base-title">{{ timeUnit }}统计</div>

        <div class="base-space-between-box">
          <div class="unit">平均心率</div>
          <div>
            <span>{{ extendedInfo.average }}</span
            >次/分
          </div>
        </div>
        <div class="base-space-between-box">
          <div class="unit">最高心率</div>
          <div>
            <span>{{ extendedInfo.max }}</span
            >次/分
          </div>
        </div>
        <div class="base-space-between-box">
          <div class="unit">最低心率</div>
          <div>
            <span>{{ extendedInfo.min }}</span
            >次/分
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
  name: "heartRate",
  data() {
    return {
      timeActive: 0,
      // 日期显示
      dateText: "",
      pageWidth: window.innerWidth,
      times: [],
      extendedInfo: {
        average: "",
        max: "",
        min: "",
      },
      timeUnit: "日",
    };
  },

  components: { customNavBar, echartsComponent, customSwipe },

  created() {
    this.generateTimeData();
  },

  mounted() {this.$deviceHolder.routerPath = "home";},

  methods: {
    generateTimeData() {
      for (let hour = 0; hour < 24; hour++) {
        for (let minute = 0; minute < 60; minute += 5) {
          const formattedHour = hour.toString().padStart(2, "0");
          const formattedMinute = minute.toString().padStart(2, "0");
          this.times.push(`${formattedHour}:${formattedMinute}`);
        }
      }
    },
    extendedInfoResponse(extendedInfo) {
      this.extendedInfo = { ...extendedInfo };
    },

    tabsChange(e) {
      if (e == 0) {
        this.timeUnit = "日";
      } else if (e == 1) {
        this.timeUnit = "周";
      } else {
        this.timeUnit = "月";
      }
      this.timeActive = e;
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

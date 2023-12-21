<!-- 心率 -->
<template>
  <div class="temperature-rate-page">
    <custom-nav-bar
      :returnRouter="$deviceHolder.homeReturnRouter"
      title="体温"
      left-arrow
    >
    </custom-nav-bar>

    <div class="page-content">
      <!-- 折线图 -->
      <custom-swipe
        type="0B"
        tooltipName="体温"
        :tooltipTitle="['最低', '最高']"
        :lineStyleColor="['#1DA772', '#EC526A']"
        :chartType="timeActive == 0 ? 'line' : 'bar'"
        @extendedInfoResponse="extendedInfoResponse"
        @tabsChange="tabsChange"
      >
      </custom-swipe>

      <!-- 统计 -->
      <div class="statistics round l-m-t">
        <div class="base-title">{{ timeUnit }}统计</div>

        <div class="base-space-between-box">
          <div class="unit">平均体温</div>
          <div>
            <span>{{ extendedInfo.average }}</span
            >℃
          </div>
        </div>
        <div class="base-space-between-box">
          <div class="unit">最高体温</div>
          <div>
            <span>{{ extendedInfo.max }}</span
            >℃
          </div>
        </div>
        <div class="base-space-between-box">
          <div class="unit">最低体温</div>
          <div>
            <span>{{ extendedInfo.min }}</span
            >℃
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import customNavBar from "../custom/customNavBar.vue";
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
      timeUnit: "日",
      extendedInfo: {
        average: "--",
        max: "--",
        min: "--",
      },
    };
  },

  components: { customNavBar, customSwipe },

  mounted() {this.$deviceHolder.routerPath = "home";},

  methods: {
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

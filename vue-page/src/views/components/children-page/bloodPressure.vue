<!-- 心率 -->
<template>
  <div class="blood-pressure-rate-page">
    <custom-nav-bar
      :returnRouter="$deviceHolder.homeReturnRouter"
      title="血压"
      left-arrow
    >
    </custom-nav-bar>

    <div class="page-content">
      <!-- 图表 -->
      <custom-swipe
        ref="customSwipe"
        type="0E"
        chartType="line"
        :lineStyleColor="['#1DA772', '#EC526A']"
        :tooltipTitle="['低压', '高压']"
        @extendedInfoResponse="extendedInfoResponse"
        @tabsChange="tabsChange"
      >
      </custom-swipe>

      <!-- 统计 -->
      <div class="statistics round l-m-t">
        <div class="base-title">{{ timeUnit }}统计</div>

        <div class="base-space-between-box">
          <div class="unit">{{ timeUnit }}平均值</div>
          <div>
            <span>{{ extendedInfo.average }}</span
            >mmHg
          </div>
        </div>
        <div class="base-space-between-box">
          <div class="unit">{{ timeUnit }}最高值</div>
          <div>
            <span>{{ extendedInfo.max }}</span
            >mmHg
          </div>
        </div>
        <div class="base-space-between-box">
          <div class="unit">{{ timeUnit }}最低值</div>
          <div>
            <span>{{ extendedInfo.min }}</span
            >mmHg
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
  name: "bloodPressure",
  data() {
    return {
      timeUnit: "日",
      // 日期显示
      dateText: "",
      pageWidth: window.innerWidth,
      extendedInfo: {
        average: "",
        max: "",
        min: "",
      },
    };
  },

  components: { customNavBar, echartsComponent, customSwipe },

  mounted() {
    this.$deviceHolder.routerPath = "home";
  },

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
    },
  },
};
</script>
<style lang="scss" scoped>
.page-content {
  .tabs-box {
    margin-top: 0.2rem;
  }

  .date-blood-pressure-num {
    margin-top: 0.22rem;
    margin-bottom: 0.1rem;
  }

  .blood-pressure-chart {
    height: 4.1rem;
    background: #ffffff;
    .mark-box {
      display: flex;
      align-items: flex-start;
      justify-content: space-between;
      margin: 0 2.12rem;
      font-size: 0.3rem;
      .round-box {
        width: 0.27rem;
        height: 0.27rem;
        border-radius: 50%;
        margin: 0.1rem;
      }
      .low-box {
        display: flex;
        align-items: center;
        color: #1da772;
        .round-box {
          border: 1px solid #1da772;
        }
      }
      .high-box {
        display: flex;
        align-items: center;
        color: #fc5a58;
        .round-box {
          border: 1px solid #fc5a58;
        }
      }
    }
  }

  //   统计、
  .statistics {
    height: 2.76rem;
    background: #ffffff;
    .base-title {
      margin-bottom: 0.1rem;
    }
    .base-space-between-box {
      margin: 0 0.27rem 0.07rem 0.27rem;
    }
  }
}
</style>

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
          <van-tab title="年"></van-tab>
        </van-tabs>
      </div>

      <div class="date-blood-pressure-num base-space-between-box">
        <div class="date">{{ dateText }}</div>
        <div class="heart-num"><span>88-120</span>次/分</div>
      </div>

      <!-- 折线图 -->
      <div class="blood-pressure-chart round">
        <echarts-component
          :options="$testCharts.bloodPressureData"
          :height="pageWidth - 208"
          tooltip
          :tooltipTitle="['高压', '低压']"
          tooltipUnit="次/分"
          areaStyleGradientBcg
          :lineStyleColor="['#EC526A', '#1DA772']"
        >
        </echarts-component>

        <div class="mark-box">
          <div class="low-box">
            <div class="round-box"></div>
            <div class="name">低压</div>
          </div>
          <div class="high-box">
            <div class="round-box"></div>
            <div class="name">高压</div>
          </div>
        </div>
      </div>

      <!-- 统计 -->
      <div class="statistics round l-m-t">
        <div class="base-title">日统计</div>

        <div class="base-space-between-box">
          <div class="unit">日平均值</div>
          <div><span>76-116</span>mmHg</div>
        </div>
        <div class="base-space-between-box">
          <div class="unit">日最高值</div>
          <div><span>80-120</span>mmHg</div>
        </div>
        <div class="base-space-between-box">
          <div class="unit">日最低值</div>
          <div><span>60-100</span>mmHg</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import customNavBar from "../custom/customNavBar.vue";
import echartsComponent from "@/components/Charts/echartsComponent.vue";
export default {
  name: "bloodPressure",
  data() {
    return {
      timeActive: 0,
      // 日期显示
      dateText: "",
      pageWidth: window.innerWidth,
    };
  },

  components: { customNavBar, echartsComponent },

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
      margin: 0 0.27rem 0.05rem 0.27rem;
    }
  }
}
</style>

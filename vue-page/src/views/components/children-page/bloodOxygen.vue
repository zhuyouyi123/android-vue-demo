<!-- 血氧饱和度 -->
<template>
  <div class="blood-xygen">
    <custom-nav-bar
      :returnRouter="$deviceHolder.homeReturnRouter"
      title="血氧饱和度"
      left-arrow
    >
    </custom-nav-bar>
    <div class="page-content">
      <!-- 折线图 -->
      <custom-swipe
        type="09"
        tooltipName="血氧"
        chartType="line"
        @tabsChange="tabsChange"
        @extendedInfoResponse="extendedInfoResponse"
      >
        <!-- @extendedInfoResponse="extendedInfoResponse"
        @tabsChange="tabsChange" -->
      </custom-swipe>

      <!-- 统计 -->
      <div class="statistics round l-m-t">
        <div class="base-title">{{ timeUnit }}统计</div>

        <div class="base-space-between-box">
          <div class="unit">平均血氧</div>
          <div>
            <span>{{ extendedInfo.average }}</span
            >%
          </div>
        </div>
        <div class="base-space-between-box">
          <div class="unit">最高血氧</div>
          <div>
            <span>{{ extendedInfo.max }}</span
            >%
          </div>
        </div>
        <div class="base-space-between-box">
          <div class="unit">最低血氧</div>
          <div>
            <span>{{ extendedInfo.min }}</span
            >%
          </div>
        </div>
      </div>

      <!-- <van-steps active="-1" direction="vertical" inactive-color="#1da772">
        <van-step>
          8月14日 23:55
          <van-cell-group>
            <van-cell class="only-cell" title="90%" value="12:00" />
          </van-cell-group>
        </van-step>

        <van-step>
          8月15日 23:55
          <van-cell-group>
            <van-cell title="90%" value="13:15" />
            <van-cell title="95%" value="13:30" />
            <van-cell title="92%" value="13:55" />
          </van-cell-group>
        </van-step>
        <van-step>
          8月16日 10:55
          <van-cell-group>
            <van-cell title="90%" value="13:55" />
          </van-cell-group>
        </van-step>
      </van-steps> -->
    </div>
  </div>
</template>

<script>
import customNavBar from "../custom/customNavBar.vue";
import customSwipe from "../custom/customSwipe.vue";
export default {
  name: "bloodOxygen",
  data() {
    return {
      active: 0,
      extendedInfo: {
        average: "-",
        max: "-",
        min: "-",
      },
      timeActive: 0,
      timeUnit: "日",
    };
  },

  components: { customNavBar, customSwipe },

  mounted() {
    // this.$androidApi.getHistoryData("09").then((data) => {
    //   console.log(JSON.stringify(data));
    // });
  },

  methods: {
    extendedInfoResponse(extendedInfo) {
      console.log("extendedInfo", JSON.stringify(extendedInfo));
      this.extendedInfo = { ...extendedInfo };
    },
    toolTipValueResponse(e) {
      this.realData = e[0];
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
</style>

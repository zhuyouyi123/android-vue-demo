<!--卡路里 -->
<template>
  <div class="calorie-page">
    <custom-nav-bar
    :returnRouter="$deviceHolder.homeReturnRouter"
      title="卡路里"
      left-arrow
      @rightIconClick="setTarget"
      :right-icon="require('../../../assets/image/navbar/target-icon.svg')"
    />

    <div class="page-content">
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

      <div class="today-consume r-card l-m-t b-d-c vertical">
        <div class="title">今日消耗</div>
        <div class="num">
          <span>{{ todayCalorie }}</span
          >千卡
        </div>
      </div>

      <!-- 图表 -->
      <custom-swipe time-interval type="05" tooltipName="卡路里">
      </custom-swipe>

      <div
        class="total-box r-card l-m-t base-space-between-box"
        v-show="timeActive == 0"
      >
        <div class="total-consum vertical">
          <div class="title">总消耗</div>
          <div class="consum-calorie">
            <span>{{ todayCalorie }}</span
            >千卡
          </div>
        </div>

        <div class="target-plan vertical">
          <div class="title">目标完成</div>
          <div class="consum-calorie">
            <span>{{ todayCalorie }}</span
            >%
          </div>
        </div>
      </div>

      <div class="total-box r-card l-m-t" v-show="timeActive > 0">
        <div class="total-child-box base-space-between-box">
          <div class="total-consum vertical">
            <div class="title">日均消耗</div>
            <div class="consum-calorie"><span>222</span>千卡</div>
          </div>

          <div class="target-plan vertical">
            <div class="title">总消耗</div>
            <div class="consum-calorie"><span>7250</span>千卡</div>
          </div>
        </div>

        <div class="total-child-box base-space-between-box">
          <div class="total-consum vertical">
            <div class="title">单日最高</div>
            <div class="consum-calorie"><span>426</span>千卡</div>
          </div>

          <div class="target-plan vertical">
            <div class="title">达标天数</div>
            <div class="consum-calorie"><span>7</span>天</div>
          </div>
        </div>
      </div>
    </div>
    <!-- 弹窗 -->
    <custom-picker
      ref="customPicker"
      type="CALORIE"
      title="卡路里目标"
    ></custom-picker>
  </div>
</template>

<script>
import customNavBar from "@/views/components/custom/customNavBar";
import echartsComponent from "@/components/Charts/echartsComponent.vue";
import customSwipe from "../custom/customSwipe.vue";
import customPicker from "@/views/components/custom/customPicker";
export default {
  name: "calorie",
  data() {
    return {
      timeActive: 0,
      todayCalorie: this.$deviceHolder.deviceInfo.stepInfo.calories,
      // 目标卡路里
      targetCalorie: 100,
      pageWidth: window.innerWidth,
    };
  },

  components: {
    customNavBar,
    echartsComponent,
    customSwipe,
    customPicker,
  },
  mounted() {this.$deviceHolder.routerPath = "home";},

  methods: {
    onClickTab() {},
    setTarget() {
      this.$refs.customPicker.open();
    },
  },
};
</script>
<style lang="scss" scoped>
.page-content {
  .today-consume {
    .title {
      font-size: 0.3rem;
    }

    .num {
      margin-top: 0.2rem;
      font-size: 0.26rem;
      line-height: 1;

      span {
        font-size: 0.46rem;
        line-height: 1;
      }
    }
  }

  .total-box {
    padding: 0 0.3rem;

    .total-child-box {
      padding: 0.3rem 0;
    }

    .vertical {
      display: flex;
      justify-content: flex-start;
    }
    .title {
      font-size: 0.26rem;
      font-weight: 400;
      color: #6b7b75;
      margin-left: 0.04rem;
    }
  }
}
</style>

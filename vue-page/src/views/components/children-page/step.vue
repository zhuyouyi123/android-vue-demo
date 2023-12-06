<!-- 步数页面 -->
<template>
  <div class="step step-page">
    <custom-nav-bar
      title="步数"
      left-arrow
      @rightIconClick="setTarget"
      :right-icon="require('../../../assets/image/navbar/target-icon.svg')"
    />

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

      <div class="date-step-num">
        <div class="date">{{ dateText }}</div>
        <div class="step-num">
          <span> {{ stepInfo.stepNumber }} </span>步
        </div>
      </div>
      <!-- 图表 -->
      <custom-swipe type="04" time-interval @swipeResponse="vanSwipeChange">
      </custom-swipe>

      <!-- 步数消耗统计 -->
      <div class="statistics round">
        <div class="box round">
          <div class="name">总里程</div>
          <div class="num">
            <span>
              {{ stepInfo.mileage / 1000 }}
            </span>
            公里
          </div>
        </div>
        <div class="box round">
          <div class="name">总用时</div>
          <div class="num">
            <span>{{ "01:15:00" }}</span>
          </div>
        </div>
        <div class="box round">
          <div class="name">总消耗</div>
          <div class="num">
            <span>{{ stepInfo.calories }}</span>
          </div>
        </div>
      </div>

      <!-- 步数达标 -->
      <div class="compliance round">
        <div class="base-title">步数达标</div>
        <div class="compliance-children-box">
          <div class="compliance-lable">总计达标天数</div>
          <div class="compliance-num"><span>8</span>天</div>
        </div>
        <div class="compliance-children-box">
          <div class="compliance-lable">连续达标天数</div>
          <div class="compliance-num"><span>10</span>天</div>
        </div>
      </div>

      <!-- 周步数 -->
      <div class="weekly-steps steps-statistics round">
        <div class="base-title">周步数</div>
        <div class="base-space-between-box">
          <div>本周</div>
          <div>
            <span>5555</span>
            <span class="unit">步/天</span>
          </div>
        </div>

        <!-- 进度条 -->
        <van-progress
          :percentage="50"
          :color="'#1DA772'"
          :track-color="'#d1ede5'"
          :show-pivot="false"
          stroke-width=".2rem"
        />

        <div class="base-space-between-box">
          <div>上周</div>
          <div>
            <span>5122</span>
            <span class="unit">步/天</span>
          </div>
        </div>

        <!-- 进度条 -->
        <van-progress
          :percentage="30"
          :color="'#1DA772'"
          :track-color="'#d1ede5'"
          :show-pivot="false"
          stroke-width=".2rem"
        />
      </div>
      <!-- 月步数 -->
      <div class="monthly-steps steps-statistics round">
        <div class="base-title">月步数</div>

        <div class="base-space-between-box">
          <div>本月</div>
          <div>
            <span>5555</span>
            <span class="unit">步/天</span>
          </div>
        </div>

        <!-- 进度条 -->
        <van-progress
          :percentage="50"
          :color="'#1DA772'"
          :track-color="'#d1ede5'"
          :show-pivot="false"
          stroke-width=".2rem"
        />

        <div class="base-space-between-box">
          <div>上月</div>
          <div>
            <span>5122</span>
            <span class="unit">步/天</span>
          </div>
        </div>

        <!-- 进度条 -->
        <van-progress
          :percentage="30"
          :color="'#1DA772'"
          :track-color="'#d1ede5'"
          :show-pivot="false"
          stroke-width=".2rem"
        />
      </div>
    </div>

    <!-- 弹窗 -->
    <custom-picker
      ref="customPicker"
      type="STEP"
      title="步数目标"
    ></custom-picker>
  </div>
</template>

<script>
import step from "@/views/components/children-page/step";
export default step;
</script>
<style lang="scss" scoped>
.step {
  .tabs-box {
    margin-top: 0.2rem;
    width: 100%;
    display: flex;
    justify-content: center;
  }

  .date-step-num {
    font-size: 0.3rem;
    font-weight: 400;
    color: #000000;
    margin-top: 0.22rem;

    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    .step-num {
      span {
        font-size: 0.46rem;
        padding: 0 0.1rem;
      }
    }
  }

  // 图表📈
  .step-charts {
    width: 7.02rem;
    height: 3.96rem;
    background: #ffffff;
    margin-top: 0.2rem;
  }

  // 统计
  .statistics {
    width: 7.02rem;
    height: 2.56rem;
    background: #ffffff;
    margin-top: 0.2rem;
    display: flex;
    align-items: center;
    justify-content: center;

    .box {
      width: 2.04rem;
      height: 2rem;
      background: #f0f1f68d;
      margin: 0 0.1rem;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      .name {
        font-size: 0.3rem;
        font-weight: 400;
        color: #6b7b75;
      }
      .num {
        margin-top: 0.1rem;
        font-weight: 400;
        color: #000000;
        font-size: 0.26rem;
        span {
          font-size: 0.4rem;
        }
      }
    }
  }

  // 达标统计
  .compliance {
    width: 7.02rem;
    height: 2.2rem;
    background: #ffffff;
    margin-top: 0.2rem;
    .compliance-children-box {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0.13rem 0.27rem 0rem 0.27rem;
      .compliance-lable {
        font-size: 0.36rem;
        font-weight: 400;
        color: #6b7b75;
      }
      .compliance-num {
        font-size: 0.26rem;
        span {
          font-size: 0.32rem;
          padding: 0 0.02rem;
        }
      }
    }
  }

  // 步数统计
  .steps-statistics {
    width: 7.02rem;
    height: 3.12rem;
    background: #ffffff;
    margin-top: 0.2rem;
    &:last-child {
      margin-bottom: 0.2rem;
    }

    .base-space-between-box {
      padding: 0.1rem 0.27rem;
    }
  }
}
</style>

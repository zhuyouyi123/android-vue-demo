<!-- 睡眠 -->
<template>
  <div class="sleep-page">
    <custom-nav-bar
      :returnRouter="$deviceHolder.homeReturnRouter"
      title="睡眠"
      left-arrow
    >
    </custom-nav-bar>
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
          <van-tab title="年"></van-tab>
        </van-tabs>
      </div>

      <!--时间统计  -->
      <div class="statistics round l-m-t">
        <!-- 时间切换 -->
        <div class="time-switching b-d-c">
          <van-icon name="arrow-left" size="23" />
          <div class="time-box">2023/11/14</div>
          <van-icon name="arrow" size="23" />
        </div>

        <div class="time-duration b-d-c num-increase-content">
          <span>8</span>小时<span>20</span>分钟
        </div>

        <div class="desc b-d-c">全天睡眠时长</div>
      </div>

      <!-- 图表 -->
      <div class="chart-box round l-m-t">
        <echarts-component
          :options="
            timeActive == 0
              ? $testCharts.sleepChartOptionsV2
              : $testCharts.sleepChartOptionsWeek
          "
          :type="timeActive == 0 ? 'line' : 'bar'"
          :height="190"
        >
        </echarts-component>
      </div>

      <div class="sleep-time-statistics round l-m-t">
        <div class="title">睡眠统计</div>

        <div class="day" v-if="timeActive == 0">
          <custom-progress :options="sleepTimeStatistics" />

          <div class="time-desc-box">
            <div class="time-child-box" v-for="item in sleepTimeStatistics">
              <div class="name-box">
                <div
                  class="point"
                  :style="{
                    background: item.color,
                  }"
                ></div>
                <div class="name">
                  {{ item.name }}
                </div>
              </div>

              <div class="time">1小时25分钟</div>
            </div>
          </div>
        </div>

        <div class="week" v-else>
          <div class="sleep-statistics-box round">
            <div class="child-title-box">日均睡眠</div>

            <div class="child-value-box num-increase-content purple">
              <span>1</span>小时 <span>25</span> 分钟
            </div>
          </div>

          <div class="sleep-statistics-box round">
            <div class="child-title-box">日均浅睡</div>

            <div class="child-value-box num-increase-content light-purple">
              <span>5</span>小时 <span>55</span> 分钟
            </div>
          </div>

          <div class="sleep-statistics-box round">
            <div class="child-title-box">日均快速眼动睡眠</div>

            <div class="child-value-box num-increase-content green">
              <span>1</span>小时 <span>25</span> 分钟
            </div>
          </div>

          <div class="sleep-statistics-box round">
            <div class="child-title-box">日均入睡</div>

            <div class="child-value-box num-increase-content light-green">
              <span>5</span>小时 <span>55</span> 分钟
            </div>
          </div>

          <div class="sleep-statistics-box round">
            <div class="child-title-box">日均醒来</div>

            <div class="child-value-box num-increase-content khaki">
              <span>1</span>小时 <span>25</span> 分钟
            </div>
          </div>

          <div class="sleep-statistics-box round">
            <div class="child-title-box">日均清醒</div>

            <div class="child-value-box num-increase-content yellow">
              <span>5</span>小时 <span>55</span> 分钟
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import sleep from "./sleep";
export default sleep;
</script>
<style lang="scss" scoped>
.page-content {
  .statistics {
    width: 7.02rem;
    height: 2.32rem;
    background: #ffffff;

    .time-switching {
      padding-top: 0.3rem;

      .time-box {
        min-width: 2.4rem;
        text-align: center;
      }
    }
    // 时长
    .time-duration {
      margin-top: 0.22rem;
    }
    .desc {
      font-size: 0.34rem;
      font-weight: 400;
      color: #000000;
      margin-top: 0.21rem;
    }
  }

  .chart-box {
    width: 7.02rem;
    height: 4rem;
    background: #ffffff;
  }

  .sleep-time-statistics {
    min-height: 3.8rem;
    background: #ffffff;
    padding: 0 0.27rem;

    .title {
      padding-top: 0.18rem;
    }

    .time-desc-box {
      margin-top: 0.34rem;
      display: flex;
      flex-wrap: wrap;

      .time-child-box {
        width: 50%; /* 每行显示两个元素 */
        box-sizing: border-box; /* 考虑边框和内边距的影响 */
        &:nth-child(n + 3) {
          margin-top: 0.35rem;
        }
        .name-box {
          display: flex;
          align-items: center;
          .point {
            width: 0.14rem;
            height: 0.14rem;
            border-radius: 50%;
          }

          .name {
            font-size: 0.26rem;
            font-weight: 400;
            color: #6b7b75;
            margin-left: 0.08rem;
          }
        }
      }
    }

    .week {
      display: flex;
      flex-wrap: wrap;
      justify-content: space-between;
      padding-bottom: 0.28rem;
      margin-top: 0.1rem;

      .sleep-statistics-box {
        width: 3.13rem;
        height: 1.8rem;
        background: #f0f1f6;
        margin: 0.1rem 0;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: column;
        .child-title-box {
          font-size: 0.3rem;
          height: 0.6rem;
        }
      }
    }
  }
}
</style>

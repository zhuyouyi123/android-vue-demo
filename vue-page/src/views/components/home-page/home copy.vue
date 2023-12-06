<!-- 首页页面 -->
<template>
  <div class="home">
    <div class="connect-info">
      <div class="info-icon">
        <van-loading color="#fff" size="15" />
      </div>
      <div class="info">正在连接中...</div>
    </div>

    <div class="page-content">
      <div class="step-number info-box">
        <div class="title">步数</div>

        <div class="step-info">
          <div class="step-container">
            <van-circle
              v-model="currentStep"
              :rate="50"
              :speed="100"
              :stroke-width="60"
              layer-color="#ebedf0"
              color="#f56a0f"
            >
              <div class="circle-container">
                <van-image
                  :src="require('../../../assets/image/home/footstep-icon.svg')"
                >
                </van-image>
                <div class="step-text">
                  {{ currentStepText }}
                </div>
              </div>
            </van-circle>

            <div class="desc">
              <p>总里程<b> 0.71 </b>公里</p>
              <p>总消耗<b> 23 </b>卡路里</p>
            </div>
          </div>

          <!-- 折现图 -->
          <div class="line-chart-container" v-if="stepInfoChartShow">
            <p>近七天平均步数 3958</p>
            <echarts-component :options="chartOptions" />
          </div>
        </div>
      </div>

      <div class="step-number info-box">
        <div class="title">步数</div>

        <div class="step-info">
          <div class="step-container">
            <van-circle
              v-model="currentStep"
              :rate="50"
              :speed="100"
              :stroke-width="60"
              layer-color="#ebedf0"
              color="#f56a0f"
            >
              <div class="circle-container">
                <van-image
                  :src="require('../../../assets/image/home/footstep-icon.svg')"
                >
                </van-image>
                <div class="step-text">
                  {{ currentStepText }}
                </div>
              </div>
            </van-circle>

            <div class="desc">
              <p>总里程<b> 0.71 </b>公里</p>
              <p>总消耗<b> 23 </b>卡路里</p>
            </div>
          </div>

          <!-- 折现图 -->
          <div class="line-chart-container">
            <p>近七天平均步数 3958</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// import echarts from '../../../components/Charts/index.vue'
import echartsComponent from "../../../components/Charts/echartsComponent.vue";
export default {
  components: {
    echartsComponent,
  },
  data() {
    return {
      currentStep: 50,
      currentStepText: "99999",
      gradientColor: "#f56a0f",

      stepInfoChartShow: true,
      chartOptions: {
        xAxis: {
          data: [],
        },
        series: [
          {
            data: [1, 10, 10000, 100, 1000, 100000, 2200],
          },
        ],
      },
    };
  },

  computed: {},

  mounted() {
    this.getChartOptions();
  },

  methods: {
    getChartOptions() {
      const xAxisData = Array.from({ length: 7 }, (_, i) => {
        const date = new Date();
        date.setDate(date.getDate() - i);
        return date.getDate().toString();
      });

      xAxisData.push("今");
      this.chartOptions.xAxis.data = xAxisData;
    },
  },
};
</script>
<style lang="scss" scoped>
.home {
  padding-top: 1.5rem;

  .connect-info {
    height: 1.5rem;
    color: #fff;
    font-size: 12px;
    display: flex;
    align-items: center;

    .info {
      padding-left: 0.2rem;
    }
  }

  .page-content {
    height: 86.9vh;
    overflow: auto;
    .info-box {
      z-index: 2;
      position: relative;
      border-radius: 0.4rem;
      margin-bottom: 0.5rem;
    }

    .step-number {
      background: #fff;
      // width: 100%;
      padding: 0 0.7rem;

      .title {
        height: 1rem;
        padding-top: 0.7rem;
        font-size: 14px;
      }

      .step-info {
        padding-bottom: 1rem;
        .step-container {
          display: flex;
          align-items: center;
          margin-top: 1rem;
          .van-circle {
            display: flex;
            align-items: center;
            justify-content: center;
            .circle-container {
              flex-direction: column;
            }

            .van-image {
              width: 2rem;
            }
          }
          .desc {
            margin-left: 2rem;
          }
        }

        .line-chart-container {
          font-size: 13px;
        }
      }
    }
  }
}
</style>

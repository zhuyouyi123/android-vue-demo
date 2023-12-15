<!-- 首页页面 -->
<template>
  <div class="home">
    <custom-nav-bar title="首页"> </custom-nav-bar>
    <div class="page-content">
      <van-pull-refresh v-model="loading" @refresh="onRefresh">
        <!-- 头部 步数信息 -->
        <div
          v-if="getCardShow('STEP')"
          class="step-container round l-m-t"
          @click="$router.push('/step')"
        >
          <!-- title 刷新时间 -->
          <div class="step-title-refresh-time">
            <div class="title">步数</div>
            <div class="refresh-time">
              <van-loading v-if="refreshTimeLoading" size="17" />
              <div>{{ refreshTime }}</div>
            </div>
          </div>

          <div class="step-num-box">
            <van-circle
              v-model="currentStepRate"
              :rate="(deviceInfo.stepInfo.stepNumber / stepTarget) * 100"
              :speed="100"
              color="#1DA772"
              :stroke-width="90"
              layer-color="#ebedf0"
              size="3rem"
            >
              <van-image
                :src="require('../../../assets/image/home/footstep-icon.png')"
              />

              <div class="num">
                {{ deviceInfo.stepInfo.stepNumber }}
              </div>
            </van-circle>
          </div>

          <div class="mileage-calorie">
            <div class="mileage-calorie-box">
              <div class="top-box">
                <van-image
                  :src="require('../../../assets/image/home/total-mileage.svg')"
                ></van-image>
                总里程
              </div>
              <div class="bottom-box">
                <div class="num">{{ deviceInfo.stepInfo.mileage / 1000 }}</div>
                公里
              </div>
            </div>
            <div class="mileage-calorie-box">
              <div class="top-box">
                <van-image
                  :src="require('../../../assets/image/home/running-icon.svg')"
                ></van-image>
                总消耗
              </div>
              <div class="bottom-box">
                <div class="num">{{ deviceInfo.stepInfo.calories }}</div>
                千卡
              </div>
            </div>
          </div>
        </div>

        <div class="module-container">
          <!-- 睡眠 -->
          <div
            v-show="getCardShow('SLEEP')"
            class="sleep-container child-box round"
            @click="$router.push('/sleep')"
          >
            <div class="home-title">睡眠</div>
            <echarts-component
              :options="$testCharts.sleepChartOptions"
              type="bar"
              :width="chartsWidth"
              :height="chartsHeight"
              yAxisHide
              xAxisHide
              barGapHide
            >
            </echarts-component>
            <div class="time-unit"><span>8</span> 小时</div>
          </div>

          <!-- 血氧 -->
          <div
            v-show="getCardShow('BLOOD_OXYGEN_SATURATION')"
            class="blood-oxygen-saturation-container child-box round"
            @click="$router.push('/bloodOxygen')"
          >
            <div class="home-title">血氧饱和度</div>
            <div class="blood-oxygen-chart">
              <div
                class="arrow"
                :style="{
                  'padding-left': `${(bloodOxygenSaturation / 100) * 2.24}rem`,
                }"
              >
                <van-icon color="#909998" name="location" />
              </div>
              <div class="chart">
                <div class="unhealthy"></div>
                <div class="healthy"></div>
              </div>
            </div>
            <div class="time-unit">
              <span>{{ bloodOxygenSaturation }}%</span>
            </div>
          </div>

          <!-- 心率 -->
          <div
            v-show="getCardShow('HEART_RATE')"
            class="heart-rate-container child-box round"
            @click="$router.push('/heartRate')"
          >
            <div class="home-title">心率</div>
            <echarts-component
              :options="$testCharts.heartRateData"
              :width="chartsWidth"
              :height="chartsHeight"
              yAxisHide
              xAxisHide
              barGapHide
              areaStyleGradientBcg
              :lineStyleColor="['#EC526A']"
            />
            <div class="time-unit"><span>64</span> 次/分钟</div>
          </div>

          <!-- 压力 -->
          <div
            v-show="getCardShow('PRESSURE')"
            class="pressure-container child-box round"
            @click="$router.push('/pressure')"
          >
            <div class="home-title">压力</div>

            <div class="circle-box display-center">
              <van-circle
                v-model="pressureValue"
                :rate="100"
                size="2rem"
                :stroke-width="80"
                :color="{
                  '0%': '#3C82E6',
                  '16%': '#46C5D5',
                  '40%': '#4ADB77',
                  '59%': '#B7D346',
                  '76%': '#DDBD58',
                  '100%': '#E27D51',
                }"
              >
                <van-image
                  :src="require('../../../assets/image/home/pressure-icon.png')"
                ></van-image>
                <div class="pressure-text">47</div>
              </van-circle>
            </div>

            <div class="medium-font-size">正常</div>
          </div>

          <!-- 卡路里 -->
          <div
            v-show="getCardShow('CALORIE')"
            class="calorie-container child-box round"
            @click="$router.push('/calorie')"
          >
            <div class="home-title">卡路里</div>
            <div class="circle-box display-center">
              <van-circle
                v-model="calorieValue"
                :rate="calcCalorieRate(deviceInfo.stepInfo.calories)"
                size="2rem"
                :stroke-width="80"
                layer-color="#FAECEB"
                color="#E9625A"
              >
                <van-image
                  :src="require('../../../assets/image/home/calorie-icon.png')"
                ></van-image>
                <div class="pressure-text">
                  {{ deviceInfo.stepInfo.calories }}
                </div>
              </van-circle>
            </div>
          </div>

          <!-- 体温 -->
          <div
            v-show="getCardShow('TEMPERATURE')"
            class="temperature-container child-box round"
            @click="$router.push('/temperature')"
          >
            <div class="home-title">体温</div>
            <div class="temperature-chart"></div>
          </div>

          <!-- 血压 -->
          <div
            v-show="getCardShow('BLOOD_PRESSURE')"
            class="blood-pressure-container child-box round"
            @click="$router.push('/bloodPressure')"
          >
            <div class="home-title">血压</div>

            <div class="blood-pressure-chart">
              <div class="blood-pressure-box">
                高压
                <div class="blood-pressure-num">
                  {{ deviceInfo.bloodPressureInfo.systolicPressure }}
                </div>
                mmHg
              </div>
              <custom-blood-pressure :level="110" :blood-pressure="140">
              </custom-blood-pressure>
              <div class="blood-pressure-box">
                低压
                <div class="blood-pressure-num">
                  {{ deviceInfo.bloodPressureInfo.diastolicPressure }}
                </div>
                mmHg
              </div>
              <custom-blood-pressure :level="70" :blood-pressure="90">
              </custom-blood-pressure>
            </div>
          </div>

          <!-- 疲劳度 -->
          <div
            class="fatigue-container child-box round"
            v-show="getCardShow('FATIGUE')"
          >
            <div class="home-title">疲劳度</div>
            <dashboard></dashboard>
          </div>

          <!-- 运动记录 -->
          <div
            class="sport-record-container round"
            v-show="getCardShow('SPORT_RECORDS')"
          >
            <div class="home-title">运动记录</div>
            <div class="sport-record-box">
              <div class="record-child-box round">
                <div class="record-title">本周运动次数</div>
                <div class="record-num"><span>12</span>次</div>
              </div>
              <div class="record-child-box round">
                <div class="record-title">本周运动时长</div>
                <div class="record-num"><span>256</span>分钟</div>
              </div>
            </div>
          </div>

          <!-- 编辑数据 -->
          <van-button class="edit-button" to="/homeConfig">编辑数据</van-button>
        </div>
      </van-pull-refresh>
    </div>
  </div>
</template>

<script>
import echartsComponent from "@/components/Charts/echartsComponent.vue";
import customBloodPressure from "../custom/customBloodPressure.vue";
import dashboard from "@/components/Charts/dashboard.vue";
import customNavBar from "../custom/customNavBar.vue";
import deviceData from "@/store/deviceData";
export default {
  components: {
    echartsComponent,
    customBloodPressure,
    dashboard,
    customNavBar,
  },

  data() {
    return {
      loading: false,
      // 首页卡片显示
      homeCard: [
        { enable: true, type: "STEP" },
        { enable: true, type: "SLEEP" },
        { enable: true, type: "BLOOD_OXYGEN_SATURATION" },
        { enable: true, type: "HEART_RATE" },
        { enable: true, type: "PRESSURE" },
        { enable: true, type: "CALORIE" },
        { enable: true, type: "TEMPERATURE" },
        { enable: true, type: "BLOOD_PRESSURE" },
        { enable: true, type: "SPORT_RECORDS" },
        { enable: true, type: "FATIGUE" },
      ],
      homeCardMap: new Map(),
      // 步数进度
      currentStepRate: 0,

      chartsWidth: window.innerWidth / 2.1,
      chartsHeight: window.innerWidth / 3.5,

      refreshTimeLoading: false,
      // 刷新时间
      refreshTime: "15分钟前",

      deviceInfo: this.$deviceHolder.deviceInfo,

      // 目标值
      stepTarget: 8000,
      calorieTarget: 300,

      // // 血液信息
      // bloodPressureInfo: this.$deviceHolder.bloodPressureInfo,
      // 血氧饱和度
      bloodOxygenSaturation: 60,
      // 压力
      pressureValue: 100,
      // 卡路里
      calorieValue: 2,
      calorieInfo: {
        rate: 0,
      },

      refreshInterval: null,
    };
  },

  computed: {},

  mounted() {
    window.addEventListener("commonAndroidEvent", this.callJs);
    this.init();
    this.updatePageInfo();
  },

  destroyed() {
    window.removeEventListener("commonAndroidEvent", this.callJs);
    if (!this.refreshInterval) {
      clearInterval(this.refreshInterval);
    }
  },

  methods: {
    init() {
      // 查询首页卡片显示
      this.queryMenuCard();
      // 查询目标值
      deviceData.querySportTarget().then((res) => {
        this.stepTarget = res.stepTarget;
        this.calorieTarget = res.calorieTarget;
      });
      deviceData.queryRealInfo().then((res) => {
        this.deviceInfo = res;
        this.calcUpdateTimeDiff();
      });

      this.monitorUpdateTime();
      // 设备连接初始化
      this.$androidApi.init();
    },

    onRefresh() {
      this.$androidApi.init();
      setTimeout(() => {
        this.loading = false;
      }, 3000);
    },

    callJs(e) {
      console.log("接收到数据返回：" + JSON.stringify(e));
      let eventName = e.data.eventName;
      let data = e.data.data;
      switch (eventName) {
        case "DEVICE_BINDING_STATUS":
          switch (data) {
            case 3001:
              this.refreshTimeLoading = true;
              this.refreshTime = "正在连接中...";
              break;
            case 3002:
              this.refreshTime = "设备连接成功";
              break;
            case 3003:
              this.refreshTimeLoading = false;
              this.refreshTime = "设备连接失败";
              break;
            case 3004:
              this.refreshTime = "数据同步中...";
              break;
            case 3005:
              this.refreshTime = "同步成功";
              this.$deviceHolder.bindingInfo.status = true;
              this.$deviceHolder.bindingInfo.time = new Date().getTime();
              setTimeout(() => {
                this.refreshTime = "刚刚";
                this.refreshTimeLoading = false;
              }, 500);
              break;
            case 3006:
              this.refreshTime = "连接错误";
              this.refreshTimeLoading = false;
              break;
            default:
              break;
          }
          break;
        case "DEVICE_REAL_TIME":
          this.deviceInfo = data;
          this.$emit("updateDeviceInfo");
          break;
        default:
          break;
      }
    },

    /**
     * 查询首页卡片
     */
    queryMenuCard() {
      this.$androidApi.queryConfigurationByGroup("HOME_CARD").then((data) => {
        this.homeCard = data;
        this.homeCardMap = new Map();
        data.forEach((e) => this.homeCardMap.set(e.type, e.enable));
      });
    },

    /**
     *计算卡路里所占百分比
     * @param {当前卡路里} calories
     */
    calcCalorieRate(calories) {
      if (calories >= this.calorieTarget) {
        return 100;
      } else {
        return parseInt((calories / this.calorieTarget) * 100);
      }
    },

    updatePageInfo() {},

    monitorUpdateTime() {
      if (!this.refreshInterval) {
        this.refreshInterval = setInterval(() => {
          // 计算多少分钟之前
          this.calcUpdateTimeDiff();
        }, 60000);
      }
    },

    calcUpdateTimeDiff() {
      // 计算多少分钟之前
      this.refreshTime = this.$dateUtil.getTimeDiffInMinutes(
        this.$deviceHolder.bindingInfo.time
      );
    },

    /**
     * 是否显示
     * @param {类型} type
     */
    getCardShow(type) {
      if (this.homeCardMap.has(type)) {
        return this.homeCardMap.get(type);
      }
      return false;
    },
  },
};
</script>
<style lang="scss" scoped>
.home {
  .step-container {
    width: 7.02rem;
    height: 3.96rem;
    background: #ffffff;
    position: relative;

    .step-num-box {
      width: 3rem;
      height: 3rem;
      position: absolute;
      left: 28%;
      top: 16%;

      .van-circle {
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: column;

        .van-image {
          width: 0.62rem;
          height: 0.62rem;
        }

        .num {
          font-size: 0.4rem;
          font-weight: bold;
          color: #000000;
          line-height: 0.65rem;
        }
      }
    }

    .step-title-refresh-time {
      display: flex;
      align-items: center;
      justify-content: space-between;

      padding: 0.2rem 0.27rem 0rem 0.27rem;

      .refresh-time {
        font-size: 0.26rem;
        font-weight: 400;
        color: #6b7b75;
        line-height: 0.29rem;
        display: flex;
        align-items: center;
        .van-loading {
          margin: 0 0.05rem;
          margin-bottom: 0.01rem;
        }
      }
    }

    // 里程 卡路里
    .mileage-calorie {
      display: flex;
      align-items: center;
      justify-content: space-between;

      margin-top: 1.88rem;
      padding: 0.27rem;

      font-size: 0.26rem;

      .mileage-calorie-box {
        .top-box {
          display: flex;
          align-items: center;
          justify-content: flex-end;
          font-weight: 400;
          color: #6b7b75;
          line-height: 0.29rem;

          .van-image {
            width: 0.27rem;
            height: 0.27rem;
            border: 0.01rem solid rgba(0, 0, 0, 0);
          }
        }

        .bottom-box {
          display: flex;
          align-items: flex-end;
          .num {
            font-size: 0.36rem;
            margin-top: 0.1rem;
            margin-bottom: 0.01rem;
            margin-right: 0.04rem;
            line-height: 0.38rem;
          }
        }
      }
    }
  }

  .module-container {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    margin-top: 0.2rem;

    .child-box {
      width: 3.4rem;
      height: 3.72rem;
      background: #ffffff;
      margin-bottom: 0.2rem;
      /* 设置下方间距 */
    }

    // 血氧饱和
    .blood-oxygen-saturation-container {
      .blood-oxygen-chart {
        height: 2.1rem;
        display: flex;
        flex-direction: column;
        margin-left: 0.27rem;
        .arrow {
          margin-top: 0.51rem;
        }
        .chart {
          width: 2.48rem;
          height: 0.13rem;
          display: flex;

          .unhealthy {
            width: 1.5rem;
            height: 0.13rem;
            background: #f09a37;
            border-radius: 0.07rem 0rem 0rem 0.07rem;
          }

          .healthy {
            width: 0.94rem;
            height: 0.13rem;
            background: #1da772;
            border-radius: 0rem 0.07rem 0.07rem 0rem;
            margin-left: 0.04rem;
          }
        }
      }
    }

    // 心率
    .heart-rate {
    }

    .pressure-container {
      .circle-box {
        margin-top: 0.1rem;
        height: 2.2rem;
      }
      .medium-font-size {
        margin-top: 0.15rem;
        margin-left: 0.27rem;
      }
    }

    // 体温
    .temperature-container {
      .temperature-chart {
        width: 2.55rem;
        height: 2.55rem;
        background: #c3d1f77a;
        border-radius: 50%;
        margin-top: 0.28rem;
        margin-left: 0.43rem;
      }
    }

    // 血压
    .blood-pressure-container {
      .blood-pressure-chart {
        .blood-pressure-box {
          height: 0.65rem;
          display: flex;
          justify-content: center;
          align-items: flex-end;
          font-size: 0.22rem;
          margin-bottom: 0.1rem;
          .blood-pressure-num {
            font-size: 0.44rem;
            line-height: 0.44rem;
            padding: 0 0.05rem;
          }
        }
      }
    }

    // 运动记录
    .sport-record-container {
      width: 7.02rem;
      height: 3.32rem;
      background: #ffffff;
      margin-bottom: 0.2rem;
      .sport-record-box {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 0 0.27rem;
        .record-child-box {
          width: 3.13rem;
          height: 2.27rem;
          background: #f0f1f6;
          margin-top: 0.1rem;
          display: flex;
          flex-direction: column;
          justify-content: center;
          align-items: center;

          font-weight: 400;
          color: #000000;
          font-size: 0.3rem;
          span {
            font-size: 0.4rem;
            padding: 0 0.02rem;
          }
        }
      }
    }

    .edit-button {
      width: 7.02rem;
      height: 0.88rem;
      background: #ffffff;
      border-radius: 0.44rem 0.44rem 0.44rem 0.44rem;
      font-size: 0.32rem;
      font-weight: 400;
      color: #1da772;
    }

    .time-unit {
      margin-top: 0.2rem;
      margin-left: 0.27rem;
      height: 0.44rem;
      font-size: 0.3rem;

      span {
        font-weight: 400;
        font-size: 0.44rem;
        padding: 0 0.01rem;
      }
    }
  }
}
</style>

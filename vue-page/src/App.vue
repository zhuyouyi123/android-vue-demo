<template>
  <div id="app" class="w h over-h box-b">
    <!-- <resize autoHeight width="375"> -->
    <router-view :nowTime="time" />
    <!-- </resize> -->
    <van-popup
      v-model="popupInfo.show"
      position="bottom"
      :style="{ height: `${popupInfo.height}%` }"
      round
    >
      <div class="content-box">
        <div class="content">
          {{ popupInfo.content }}
        </div>
        <div class="button-group">
          <!-- <div class="cancel button">取消</div> -->
          <div class="confirm button" @click="popupConfirm">知道了</div>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script>
import resize from "@/components/Resize/Resize.vue";
import axios from "axios";
import { getNowDateTime } from "@/utils/index.js";
import deviceHolder from "./store/deviceHolder";
import rem from "../static/rem";
import { Toast } from "vant";
export default {
  name: "App",
  components: { resize, rem },
  data() {
    return {
      intervalId: null,
      time: getNowDateTime(0),
      commonAndroidEvent: null,
      popupInfo: {
        show: false,
        content:
          "为了扫描设备二维码，APP需要使用摄像头，需要获取拍摄照片、录制视频的权限。",
        height: 20,
        key: "",
      },
      currTime: 0,
    };
  },

  mounted() {
    this.getFirstUseTime();

    this.initConfig();

    //通用安卓推送回调
    window.commonAndroidEvent = this.commonAndroidCallBack;
    window._androidGoBackBySystemButton = this.androidGoBackBySystemButton;
    this.commonAndroidEvent = new Event("commonAndroidEvent");
  },

  methods: {
    initConfig() {
      this.$androidApi.initConfig();
    },
    /**
     * 检查网络请求
     */
    checkNetWork() {
      axios
        .get(window.location.href, { timeout: 3000 })
        .then((res) => {
          this.msg = "";
        })
        .catch((error) => {
          try {
            if (
              error.message.indexOf("Failed") >= 0 ||
              error.message.indexOf("timeout") >= 0
            ) {
              this.msg = "服务器断开连接！";
              // alert('服务器断开连接！')
              // this.$notify.error({ title: '错误', message: '服务器断开连接！', duration: 3500, });
              return;
            }
          } catch (error) {}
          this.msg = "当前处于离线状态，请检查并连接！";
          // alert('当前处于离线状态，请检查并连接！')
          // this.$notify.error({ title: '错误', message: '当前处于离线状态，请检查并连接！', duration: 3500, });
        });
    },
    /**
     * 在进行和android基座调用时的拦截器
     */
    init() {
      //拦截器
      this.$ano.requestSetting.interceptor = {
        /**
         * 请求之前调用的方法
         * @param {object} 请求之前的所构造参数
         * @return Boolean  true,false 表示是否继续执行
         */
        before: (event) => {
          return true;
        },
        /**
         * 请求之后调用的方法,对返回数据进行加工操作
         * @param {object} 请求之前的所构造参数
         * @return Object event 表示加工后返回的数据
         */
        after: (event) => {
          return event;
        },
        /**
         * 异常处理程序
         */
        error: (error, option) => {
          return error;
        },
      };
    },

    commonAndroidCallBack(returnJson) {
      try {
        this.commonAndroidEvent.data = JSON.parse(returnJson);

        this.responseDataHandle(this.commonAndroidEvent.data);

        window.dispatchEvent(this.commonAndroidEvent);
      } catch (error) {
        console.error("commonAndroidCallBack error");
      }
    },

    androidGoBackBySystemButton() {
      if (
        !this.$deviceHolder.routerPath ||
        this.$deviceHolder.routerPath == "/"
      ) {
        let time = new Date().getTime();

        if (time - this.currTime > 3000) {
          Toast({ message: "再次返回后退出", position: "top" });
          this.currTime = time;
          return;
        } else if (time - this.currTime <= 1000) {
          return false;
        }
      } else if (this.$deviceHolder.routerPath == "home") {
        this.$router.replace({
          path: "/layout/index",
          query: { active: 0 },
        });
      } else if (this.$deviceHolder.routerPath == "mine") {
        this.$router.replace({
          path: "/layout/index",
          query: { active: 1 },
        });
      }
      this.$deviceHolder.routerPath = "";
    },

    goBack() {
      return new Promise((resolve, reject) => {
        this.$dialog
          .alert({
            title: "提示",
            message: "是否退出",
            showCancelButton: true,
          })
          .then(() => {
            resolve(false);
          })
          .catch(() => {
            reject();
          });
      });
    },

    responseDataHandle(data) {
      switch (data.eventName) {
        case "DEVICE_BATTERY":
          if (data.data && data.data > 0) {
            deviceHolder.deviceInfo.battery = data.data;
          }
          break;
        case "DEVICE_REAL_TIME":
          let battery = 0;
          if (deviceHolder.deviceInfo.battery > 0) {
            battery = deviceHolder.deviceInfo.battery;
          }
          deviceHolder.deviceInfo = JSON.parse(JSON.stringify(data.data));
          deviceHolder.deviceInfo.battery = battery;
          break;
        case "STEP_HISTORY_DATA":
          deviceHolder.stepStatisticsInfo = JSON.parse(
            JSON.stringify(data.data)
          );
          break;
        case "CALORIE_HISTORY_DATA":
          deviceHolder.calorieStatisticsInfo = JSON.parse(
            JSON.stringify(data.data)
          );
          break;

        // 弹窗显示
        case "POPUP_SHOW":
          this.popupInfo.key = "CAMERA_POPUP_SHOW";
          this.popupInfo.show = true;
          this.popupInfo.content =
            "为了扫描设备二维码，APP需要使用摄像头，需要获取拍摄照片、录制视频的权限。";
          break;
        default:
          break;
      }
    },

    getFirstUseTime() {
      this.$androidApi.shareGet("FIRST_USE_TIME").then((data) => {
        deviceHolder.firstUseTime = data;
      });
    },

    popupConfirm() {
      if (this.popupInfo.key == "CAMERA_POPUP_SHOW") {
        this.$androidApi.requestCameraPermission();
      }

      this.popupInfo.show = false;
    },
  },
  created() {},
  destroyed() {},
};
</script>

<style lang="scss">
// @import url(./assets/font.css);

html {
  font-size: 15px; //可以使用 1/10 rem= 10 px
}

html,
body,
#app {
  background: #f0f1f6;
  font-family: Source Han Sans CN-Regular, Source Han Sans CN;
  font-size: 0.36rem;
  overflow: auto;

  .component {
    padding: 0 0.25rem;
  }

  .content-box {
    height: 100%;
    width: 100%;

    text-align: center;
    display: flex;
    justify-content: center;
    font-size: 0.28rem;
    .content {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 75%;
      height: 70%;
      line-height: 0.6rem;
    }
    .button-group {
      width: 100%;
      height: 30%;
      position: absolute;

      bottom: 0;
      background: #e9e2e271;

      display: flex;
      align-items: center;
      justify-content: center;
      .button {
        // width: 50%;
        font-size: 0.3rem;
        font-weight: 400;
      }

      .confirm {
        color: rgba(28, 62, 231, 0.787);
      }
    }
  }
}
</style>

<!-- 我的页面 -->
<template>
  <div class="mine">
    <custom-nav-bar title="我的设备"> </custom-nav-bar>
    <div class="page-content l-m-t">
      <div class="user-info-box">
        <div class="head-icon">
          <van-image
            :src="require('../../../assets/image/mine/head-icon.svg')"
          ></van-image>
        </div>
        <div class="user-info" @click="$router.push('/user/info')">
          <div class="username">{{ username }}</div>
          <div class="compliance-day">
            <van-image
              :src="require('../../../assets/image/mine/star-icon.svg')"
            ></van-image>

            <div class="day-num" v-if="complianceDays > 0">
              已达标{{ complianceDays }}天
            </div>
            <div class="day-num" v-else>暂无达标天数</div>
          </div>
        </div>
      </div>

      <div class="my-devices-box">
        <div class="null-box"></div>
        <van-cell
          v-show="device"
          :title="`${name}手环`"
          :value="`剩余电量${battery}%`"
          size="large"
          center
        />
        <div class="add-device" @click="addDevice" v-show="!device">
          <van-image
            :src="require('../../../assets/image/mine/add-device-icon.svg')"
          ></van-image>
          <span>绑定设备</span>
        </div>
      </div>

      <!-- 配置 -->
      <van-cell-group inset>
        <van-cell title="来电提醒" size="large" to="/mine/incall" is-link>
          <van-image
            class="l-p-l"
            slot="icon"
            :src="
              require('../../../assets/image/braceletconfig/incoming-call-icon.svg')
            "
          ></van-image>
        </van-cell>
        <!-- App通知 -->
        <van-cell
          title="APP通知提醒"
          size="large"
          to="/mine/appnotification"
          is-link
        >
          <van-image
            class="l-p-l"
            slot="icon"
            :src="
              require('../../../assets/image/braceletconfig/app-notice-icon.svg')
            "
          ></van-image>
        </van-cell>
        <!-- 短信提醒 -->
        <van-cell title="短信提醒" size="large" to="/mine/sms" is-link>
          <van-image
            class="l-p-l"
            slot="icon"
            :src="
              require('../../../assets/image/braceletconfig/text-message.icon.svg')
            "
          ></van-image>
        </van-cell>
      </van-cell-group>

      <van-cell-group inset class="l-m-t">
        <van-cell
          title="固件更新"
          size="large"
          is-link
          @click="checkNeedUpdate('DFU_FIRMWARE', false)"
          :value="
            updateState.DFU_FIRMWARE ? '有新版本可用' : updateState.dfuDurrVer
          "
        >
          <van-image
            class="l-p-l"
            slot="icon"
            :src="
              require('../../../assets/image/braceletconfig/firmware-icon.svg')
            "
          ></van-image>
        </van-cell>
        <!-- App通知 -->
        <van-cell title="恢复出厂设置" size="large" is-link @click="reset">
          <van-image
            class="l-p-l"
            slot="icon"
            :src="
              require('../../../assets/image/braceletconfig/reset-icon.svg')
            "
          ></van-image>
        </van-cell>
        <!-- 开关配置 -->
        <van-cell
          title="功能开关配置"
          size="large"
          is-link
          @click="$router.replace('/function/switch')"
        >
          <van-image
            class="l-p-l"
            slot="icon"
            :src="
              require('../../../assets/image/braceletconfig/function-switch-icon.svg')
            "
          ></van-image>
        </van-cell>
      </van-cell-group>

      <van-cell-group inset class="l-m-t">
        <van-cell
          title="检查更新"
          size="large"
          is-link
          @click="checkNeedUpdate('ANDROID_APP', false)"
          :value="
            updateState.ANDROID_APP ? '有新版本可用' : updateState.appCurrVer
          "
        >
          <van-image
            class="l-p-l"
            slot="icon"
            :src="
              require('../../../assets/image/braceletconfig/firmware-icon.svg')
            "
          ></van-image>
        </van-cell>
      </van-cell-group>

      <van-cell-group inset class="l-m-t" v-show="device">
        <van-cell title="解除绑定" size="large" is-link @click="unbind">
          <van-image
            width=".85rem"
            height=".85rem"
            slot="icon"
            :src="require('../../../assets/image/mine/unbind-icon.svg')"
          ></van-image>
        </van-cell>
      </van-cell-group>
    </div>

    <!--  加载弹窗 -->
    <van-overlay z-index="11" :show="overlayShow">
      <div class="wrapper b-d-c" @click.stop>
        <div class="block b-d-c">
          <van-loading size="24px" vertical> {{ loadingText }}</van-loading>
        </div>
      </div>
    </van-overlay>
  </div>
</template>

<script>
import { Toast, Popup, Notify, Dialog } from "vant";
import customNavBar from "../custom/customNavBar.vue";

export default {
  components: {
    customNavBar,
    Popup,
    [Dialog.Component.name]: Dialog.Component,
  },
  data() {
    return {
      username: "未设置",
      battery: 0,
      device: null,
      // 遮罩
      overlayShow: false,
      loadingText: "加载中...",
      // 达标天数
      complianceDays: 0,
      name: "",
      canShow: false,

      updateState: {
        ANDROID_APP: false,
        appCurrVer: "",
        DFU_FIRMWARE: false,
        dfuDurrVer: "",
        OTA_FIRMWARE: false,
      },
      appVersion: "",
    };
  },

  mounted() {
    window.addEventListener("commonAndroidEvent", this.callJs);
    setTimeout(() => {
      this.readBattery();
      this.getUserInfo();
      this.queryDevice();
      this.queryComplianceDays();
    }, 20);

    setTimeout(() => {
      this.checkAllUpdate();
    }, 200);
  },

  destroyed() {
    window.removeEventListener("commonAndroidEvent", this.callJs);
  },

  methods: {
    checkAllUpdate() {
      this.checkNeedUpdate("ANDROID_APP", true).then((res) => {
        this.updateState.ANDROID_APP = res.result;
      });

      this.checkNeedUpdate("DFU_FIRMWARE", true).then((res) => {
        this.updateState.DFU_FIRMWARE = res.result;
      });
    },

    callJs(e) {
      let eventName = e.data.eventName;
      let data = e.data.data;
      switch (eventName) {
        case "DEVICE_BINDING_STATUS":
          switch (data) {
            case 3001:
              if (this.device) {
                return;
              }
              if (!this.canShow) {
                return;
              }
              this.overlayShow = true;
              this.loadingText = "正在连接中...";
              break;
            case 3002:
              this.loadingText = "设备连接成功";
              break;
            case 3003:
              this.loadingText = "设备连接失败";
              setTimeout(() => {
                this.overlayShow = false;
              }, 1000);
              break;
            case 3004:
              this.loadingText = "正在准备初始化...";
              break;
            case 3005:
              this.loadingText = "初始化中，请稍等...";
              setTimeout(() => {
                this.queryDevice();
              }, 2000);

              setTimeout(() => {
                if (!this.device) {
                  this.queryDevice();
                }
                this.overlayShow = false;
              }, 8000);
              break;
          }

          break;
        case "DEVICE_REAL_TIME":
          this.readBattery();
          this.setName();
          break;
        case "FIRMWARE_UPGRADE_KEY":
          this.handleFirmwareUpgrade(data);
          break;
      }
    },
    addDevice() {
      if (this.device) {
        Toast({ message: "请先解绑当前设备!", position: "top" });
        return;
      }

      this.canShow = true;
      this.$androidApi.startScanQr();
    },

    reset() {
      if (!this.device) {
        Toast.fail({ message: "请先绑定设备", position: "top" });
        return;
      }
      Dialog.alert({
        title: "提示",
        message: "确认将设备恢复到出厂设置吗？未同步数据可能会丢失。",
        showCancelButton: true,
        messageAlign: "left",
      })
        .then(() => {
          Dialog.alert({
            title: "提示",
            message: "点击确定，设备将恢复出厂设置",
            showCancelButton: true,
            messageAlign: "left",
          })
            .then(() => {
              this.$androidApi.reset();
            })
            .catch(() => {});
        })
        .catch(() => {});
    },

    readBattery() {
      this.battery = this.$deviceHolder.deviceInfo.battery;
    },

    getUserInfo() {
      this.$androidApi.queryUserInfo().then((data) => {
        if (data) {
          this.username = data.username;
          this.$deviceHolder.userInfo = data;
        }
      });
    },

    queryDevice() {
      this.$androidApi.queryDevice().then((data) => {
        if (!data) {
          return;
        }
        this.setName();
        this.device = data;
      });
    },

    setName() {
      if (
        this.$deviceHolder.deviceInfo.model &&
        0 != this.$deviceHolder.deviceInfo.model
      ) {
        this.name = this.$deviceHolder.deviceInfo.model;
      }
    },

    unbind() {
      Dialog.alert({
        title: "提示",
        confirmButtonText: "知道了",
        showCancelButton: true,
        confirmButtonText: "解除绑定",
        message: "是否解除绑定，如设备未连接，未同步数据可能会丢失",
      })
        .then((_) => {
          this.$androidApi.unbindDevice().then((res) => {
            if (res) {
              Toast({ message: "解除绑定成功", position: "top" });
              this.device = null;
              this.updateState.dfuDurrVer = "";
              this.$deviceHolder.deviceInfo = {};
              this.$deviceHolder.deviceInfo = this.$deviceHolder.initDeviceInfo;
              this.queryDevice();
            }
          });
        })
        .catch(() => {});
    },

    /**
     * 查询达标天数
     */
    queryComplianceDays() {
      this.$androidApi.queryComplianceDays().then((res) => {
        this.complianceDays = res.complianceDays;
      });
    },

    lastVersionTips(noTips) {
      if (!noTips) {
        Toast({ message: "当前是最新版", position: "top" });
      }
      return {
        result: false,
      };
    },

    getDfuUpdate(noTips) {
      let deviceInfo = this.$deviceHolder.deviceInfo;
      if (!deviceInfo || !deviceInfo.firmwareVersion) {
        return this.lastVersionTips(noTips);
      }

      let split = String(deviceInfo.firmwareVersion).split("_");
      if (split.length != 2 || !split[1]) {
        return this.lastVersionTips(noTips);
      }

      return {
        result: true,
        version: split[1],
      };
    },

    /**
     *
     * @param {只需要结果，需要更新也不去 只返回结果 不弹窗} onlyResult
     */
    async checkNeedUpdate(type, onlyResult) {
      if (!navigator.onLine) {
        if (!onlyResult) {
          Toast.fail("网络错误");
        }
        return { result: false };
      }
      // ANDROID_APP   DFU_FIRMWARE
      let urlPath =
        "http://172.16.55.55:40001/api-node/app/file-download/BCG_WRISTBAND/";
      let url;
      let currentVersion;
      if (type == "DFU_FIRMWARE") {
        let deviceInfoData = await this.$androidApi.getDeviceInfo();

        if (!deviceInfoData) {
          if (!onlyResult) {
            Toast.fail({ message: "设备未连接", position: "top" });
          }
          return {
            result: false,
          };
        }
        if (this.battery <= 20) {
          if (!onlyResult) {
            Toast.fail({
              message: "当电量低于20%，无法进行升级",
              position: "top",
            });
          }
          return { result: false };
        }

        if (!deviceInfoData.connectStatus) {
          if (!onlyResult) {
            Toast.fail({ message: "设备未连接", position: "top" });
          }
          return {
            result: false,
          };
        }
        let data = this.getDfuUpdate(onlyResult);
        if (!data.result) {
          return this.lastVersionTips(onlyResult);
        }
        currentVersion = data.version;
        this.updateState.dfuDurrVer = "Version " + data.version;
        url = `${urlPath}${type}/${data.version}`;
      } else if (type == "ANDROID_APP") {
        let version = await this.checkAppUpdate();
        this.updateState.appCurrVer = "Version " + version;
        currentVersion = version;
        url = `${urlPath}${type}/${version}`;
      }
      console.log(url);

      let res = await this.fetchData(url);
      if (!res || !res.data) {
        if (!onlyResult) {
          Toast.fail({ message: "更新失败", position: "top" });
        }
        return { result: false };
      }
      let resData = res.data;
      if (!resData.needUpdate) {
        return this.lastVersionTips(onlyResult);
      }

      if (onlyResult) {
        return {
          result: true,
        };
      }

      let fileSize = (resData.fileSize / (1024 * 1024)).toFixed(2) + "MB";

      Dialog.confirm({
        title: "有新版本可用",
        message: `当前版本：${currentVersion}\n新版本：${resData.fileName}\n文件大小：${fileSize}\n`,
        confirmButtonText: "立即更新",
        showCancelButton: true,
        cancelButtonText: "稍后再说",
        messageAlign: "left",
      })
        .then(() => {
          this.overlayShow = true;
          this.loadingText = "文件下载中，请稍等...";
          if (type == "DFU_FIRMWARE" || type == "ANDROID_APP") {
            setTimeout(() => {
              this.fileUpdateHandle(type, resData.fileName);
            }, 500);
          }
        })
        .catch(() => {});
    },

    getAppUpdate() {},
    /**
     * 查询App版本
     */
    async checkAppUpdate() {
      let res = await this.$androidApi.queryAppVersion();
      return res;
    },

    /**
     * 文件更新处理
     */

    fileUpdateHandle(type, fileName) {
      let params = {
        fileType: type,
        fileName: fileName,
      };

      this.$androidApi
        .downloadFile(params)
        .then(() => {
          this.loadingText = "文件下载成功...";
          if (type == "DFU_FIRMWARE") {
            this.enterFirmwareUpgrade();
          } else if (type == "ANDROID_APP") {
            this.installApp();
          }
        })
        .catch(() => {
          this.loadingText = "升级失败";
          setTimeout(() => {
            this.overlayShow = false;
          }, 1000);
        });
    },

    /**
     * 进入固件升级
     */
    enterFirmwareUpgrade() {
      this.$androidApi
        .writeCommand("B1")
        .then(() => {
          setTimeout(() => {
            this.$androidApi.startDfuUpgrade();
            this.loadingText = "准备连接...";
          }, 1000);
        })
        .catch(() => {
          this.loadingText = "升级失败";
          setTimeout(() => {
            this.overlayShow = false;
          }, 1000);
        });
      setTimeout(() => {
        this.loadingText = "升级准备中...";
      }, 500);
    },

    installApp() {
      setTimeout(() => {
        this.overlayShow = false;
        this.$androidApi.installApp();
      }, 1000);
    },

    handleFirmwareUpgrade(data) {
      let needClose = false;
      if (!this.overlayShow) {
        this.overlayShow = true;
      }
      switch (data.key) {
        case "UPGRADE_FAILED":
          this.loadingText = "升级失败";
          needClose = true;
          break;
        case "START_UPGRADE":
          this.loadingText = "开始升级...";
          break;
        case "END_UPGRADE":
          this.loadingText = "升级结束...";
          break;
        case "UPGRADE_SUCCESS":
          this.loadingText = "升级成功...";
          this.checkAllUpdate();
          needClose = true;
          break;
        case "UPGRADING":
          this.loadingText = `升级中 ${data.value}%...`;
          break;
        case "CONNECTING":
          this.loadingText = "连接中...";
          break;
        case "CONNECT_SUCCESS":
          this.loadingText = "连接成功...";
          break;
        default:
          break;
      }

      console.log("handleFirmwareUpgrade", needClose);

      if (needClose) {
        setTimeout(() => {
          this.overlayShow = false;
        }, 2000);
      }
    },

    // 调用接口
    async fetchData(url) {
      try {
        const response = await fetch(url, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
        });
        const data = await response.json();
        // 在这里处理返回的数据
        return data;
      } catch (error) {
        // 在这里处理错误
        console.error("Fetch error:", error);
        return null;
      }
    },
  },
};
</script>
<style lang="scss" scoped>
.mine {
  .user-info-box {
    width: 7.02rem;
    height: 1.6rem;
    background: #ffffff;
    border-radius: 0.1rem 0.1rem 0.1rem 0.1rem;

    display: flex;
    align-items: center;

    .head-icon {
      .van-image {
        width: 0.96rem;
        height: 0.96rem;
        margin-left: 0.28rem;
      }
    }

    .user-info {
      margin-left: 0.2rem;
      display: flex;
      flex-direction: column;
      align-items: flex-start;

      .username {
        font-size: 0.36rem;
        font-weight: bold;
        color: #000000;
      }

      .compliance-day {
        display: flex;
        align-items: center;
        height: 0.5rem;

        .van-image {
          width: 0.26rem;
          height: 0.26rem;
          padding-right: 0.08rem;
        }

        .day-num {
          font-size: 0.3rem;
          font-weight: 400;
          color: #909998;
        }
      }
    }
  }

  .van-cell__value {
    font-size: 0.26rem;
  }

  // 我的设备
  .my-devices-box {
    width: 7.02rem;

    border-radius: 0.1rem;
    margin-top: 0.2rem;

    .null-box {
      border-top-left-radius: 0.1rem;
      border-top-right-radius: 0.1rem;
      background: #ffffff;
      height: 0.1rem;
    }
    .add-device {
      height: 1.03rem;
      background: #ffffff;
      border-bottom-left-radius: 0.1rem;
      border-bottom-right-radius: 0.1rem;
      display: flex;
      align-items: center;
      justify-content: center;

      .van-image {
        width: 0.4rem;
        height: 0.4rem;
      }

      span {
        width: 1.5rem;
        text-align: center;
        height: 0.3rem;
        font-size: 0.3rem;
        font-weight: 400;
        color: #1da772;
        line-height: 0.29rem;
      }
    }
  }

  // 配置
  .van-cell-group {
    .van-cell {
      display: flex;
      align-items: center;
      .van-image {
        width: 0.63rem;
        height: 0.63rem;
        padding-right: 0.2rem;
      }
      .l-p-l {
        padding-right: 0.3rem;
        padding-left: 0.1rem;
      }
    }
  }
  .wrapper {
    height: 100%;
  }

  .block {
    width: 3rem;
    height: 120px;
    background-color: #fff;
    border-radius: 0.1rem;
  }
}
</style>

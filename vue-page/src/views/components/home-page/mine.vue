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
            <div class="day-num">已达标202天</div>
          </div>
        </div>
      </div>

      <div class="my-devices-box">
        <div class="null-box"></div>
        <van-cell
          v-show="device"
          title="X15X手环"
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
            slot="icon"
            :src="
              require('../../../assets/image/braceletconfig/app-notice-icon.svg')
            "
          ></van-image>
        </van-cell>
        <!-- 短信提醒 -->
        <van-cell title="短信提醒" size="large" to="/mine/sms" is-link>
          <van-image
            slot="icon"
            :src="
              require('../../../assets/image/braceletconfig/text-message.icon.svg')
            "
          ></van-image>
        </van-cell>
      </van-cell-group>

      <van-cell-group inset class="l-m-t">
        <van-cell title="固件更新" size="large" is-link>
          <van-image
            slot="icon"
            :src="
              require('../../../assets/image/braceletconfig/firmware-icon.svg')
            "
          ></van-image>
        </van-cell>
        <!-- App通知 -->
        <van-cell title="恢复出厂设置" size="large" is-link>
          <van-image
            slot="icon"
            :src="
              require('../../../assets/image/braceletconfig/reset-icon.svg')
            "
          ></van-image>
        </van-cell>
      </van-cell-group>

      <van-cell-group inset class="l-m-t">
        <van-cell title="检查更新" size="large" is-link>
          <van-image
            slot="icon"
            :src="
              require('../../../assets/image/braceletconfig/firmware-icon.svg')
            "
          ></van-image>
        </van-cell>
      </van-cell-group>
    </div>
  </div>
</template>

<script>
import { Toast, Popup } from "vant";
import code from "../../../store/code";
import customNavBar from "../custom/customNavBar.vue";
export default {
  components: { customNavBar, Popup },
  data() {
    return {
      username: "未设置",
      battery: 0,
      device: null,
    };
  },

  mounted() {
    window.addEventListener("commonAndroidEvent", this.callJs);

    this.readBattery();
    this.getUserInfo();
    this.queryDevice();
  },

  destroyed() {
    window.removeEventListener("commonAndroidEvent", this.callJs);
  },

  methods: {
    callJs(e) {
      let eventName = e.data.eventName;
      let data = e.data.data;
      switch (eventName) {
        case "DEVICE_BINDING_STATUS":
          setTimeout(() => {
            this.queryDevice();
          }, 100);
          break;
        case "DEVICE_REAL_TIME":
          this.readBattery();
          break;
      }
    },
    addDevice() {
      if (this.device) {
        Toast({ message: "请先解绑当前设备!", position: "top" });
        return;
      }

      this.$androidApi.startScanQr();
      // this.$androidApi.connectScanCode();
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
        this.device = data;
      });
    },

    test() {
      this.$androidApi.test({
        data: code.BATTERY_CODE,
        isByte: true,
      });
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
    }
  }
}
</style>

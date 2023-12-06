<!-- 手环配置页面 -->
<template>
  <div class="bracelet-config bracelet-config-page">
    <custom-nav-bar title="手环名称" left-arrow />
    <div class="page-content">
      <div class="battery-show round">
        <div class="battery">
          <span>{{ battery }}</span
          >%
        </div>
        <div class="last-charging-time">距离上次充电已8天</div>
      </div>

      <van-cell-group inset>
        <van-cell title="来电提醒" size="large" is-link>
          <van-image
            slot="icon"
            :src="
              require('../../../assets/image/braceletconfig/incoming-call-icon.svg')
            "
          ></van-image>
        </van-cell>
        <!-- App通知 -->
        <van-cell title="APP通知提醒" size="large" is-link>
          <van-image
            slot="icon"
            :src="
              require('../../../assets/image/braceletconfig/app-notice-icon.svg')
            "
          ></van-image>
        </van-cell>
        <!-- 短信提醒 -->
        <van-cell title="短信提醒" size="large" is-link>
          <van-image
            slot="icon"
            :src="
              require('../../../assets/image/braceletconfig/text-message.icon.svg')
            "
          ></van-image>
        </van-cell>
      </van-cell-group>
    </div>
  </div>
</template>

<script>
import customNavBar from "@/views/components/custom/customNavBar";
export default {
  name: "braceletConfig",
  data() {
    return {
      battery: -1,
    };
  },

  components: { customNavBar },

  mounted() {
    this.battery = this.$deviceHolder.deviceInfo.battery;
  },

  methods: {},
};
</script>
<style lang="scss" scoped>
.page-content {
  .battery-show {
    width: 7.02rem;
    height: 1.92rem;
    // background: #1da772;
    background-color: green;
    background-image: url("../../../assets/image/braceletconfig/battery-bcg.svg");
    background-size: 93%; /* 调整背景图片大小以填充整个.box */
    background-repeat: no-repeat;
    background-position: center center; /* 将背景图片在上下左右都居中显示 */
    margin-top: 0.2rem;
    margin-bottom: 0.19rem;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    color: #ffffff;
    opacity: 0.7;

    .battery {
      span {
        font-size: 0.72rem;
      }
    }
  }

  .van-cell-group {
    .van-cell {
      display: flex;
      align-items: center;
      .van-image {
        width: 0.63rem;
        height: 0.63rem;
        padding-right: 0.1rem;
      }
    }
  }
}
</style>

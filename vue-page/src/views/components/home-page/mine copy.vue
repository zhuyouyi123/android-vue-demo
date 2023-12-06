<!-- 我的页面 -->
<template>
  <div class="mine">
    <!-- 头信息 -->
    <div class="head-info">
      <!-- 头像 -->
      <div class="head-sculpture">
        <van-image
          :src="require('../../../assets/image/mine/head.png')"
        ></van-image>
      </div>

      <div class="username">
        {{ username }}
      </div>
    </div>

    <div class="my-device">
      <div class="lable">我的设备</div>

      <div class="device-list">
        <div class="add-device" @click="addDevice">+ 添加设备</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      username: "真趣手环App",
    };
  },

  mounted() {},

  methods: {
    addDevice() {
      this.$androidApi.connectScanCode();
    },
  },
};
</script>
<style lang="scss" scoped>
.mine {
  .head-info {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding-top: 3rem;

    .head-sculpture {
      .van-image {
        width: 4rem;
      }
    }

    .username {
      margin-top: 1rem;
      font-weight: bold;
    }
  }

  // 我的设备
  .my-device {
    margin-top: 1rem;

    .lable {
      margin-left: 0.5rem;
    }

    .device-list {
      height: 4rem;
      background: #fff;
      margin-top: 0.8rem;
      border-radius: 0.5rem;

      .add-device {
        height: 2rem;
        display: flex;
        align-items: center;
        justify-content: center;
        color: orangered;
        padding-top: 1rem;
      }
    }
  }
}
</style>

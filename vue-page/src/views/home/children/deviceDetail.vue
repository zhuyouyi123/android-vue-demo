<!-- 设备详情 -->
<template>
  <div>
    <nav-bar>
      <van-icon name="arrow-left" slot="left" class="navIcon" @click="goBack" />
      <div slot="title">{{ $t("pageTitle.deviceDetail") }}</div>
    </nav-bar>
    <div class="content">
      <div class="base-info-box">
        <div class="box-title">
          {{ $t("device.detail.title.basicInformation") }}
        </div>
        <van-cell-group inset>
          <van-field
            input-align="right"
            label-width="4rem"
            v-model="value"
            label="Mac"
            placeholder="Mac"
          />

          <van-field
            input-align="right"
            label-width="4rem"
            v-model="value"
            :label="$t('device.detail.lable.model')"
            :placeholder="$t('device.detail.lable.model')"
          />
          <van-field
            input-align="right"
            label-width="4rem"
            v-model="value"
            :label="$t('device.detail.lable.softwareVersion')"
            :placeholder="$t('device.detail.lable.softwareVersion')"
          />
          <van-field
            input-align="right"
            label-width="4rem"
            v-model="value"
            :label="$t('device.detail.lable.hardwareVersion')"
            :placeholder="$t('device.detail.lable.hardwareVersion')"
          />
          <van-field
            input-align="right"
            label-width="4rem"
            v-model="value"
            :label="$t('device.detail.lable.firmwareVersion')"
            :placeholder="$t('device.detail.lable.firmwareVersion')"
          />
        </van-cell-group>
      </div>

      <div class="special-info-box">
        <div class="box-title">
          {{ $t("device.detail.title.specialInformation") }}
        </div>
        <van-cell-group inset>
          <van-field
            input-align="right"
            label-width="4rem"
            v-model="value"
            :label="$t('device.detail.lable.numberOfChannels')"
            :placeholder="$t('device.detail.lable.numberOfChannels')"
          />
          <van-field
            input-align="right"
            label-width="4rem"
            v-model="value"
            :label="$t('device.detail.lable.supportPower')"
            :placeholder="$t('device.detail.lable.supportPower')"
          />
          <van-field
            input-align="right"
            label-width="4rem"
            v-model="value"
            :label="$t('device.detail.lable.supportData')"
            :placeholder="$t('device.detail.lable.supportData')"
          />
        </van-cell-group>
      </div>

      <div class="function-button">
        <div class="function">
          <div class="logo">
            <van-image
              :src="require('../../../assets/device/detail/can-connect.svg')"
              alt=""
            />
          </div>
          <div class="name">{{ $t("device.detail.button.canConncet") }}</div>
        </div>
        <div class="function">
          <div class="logo">
            <van-image
              :src="require('../../../assets/device/detail/reset.svg')"
              alt=""
            />
          </div>
          <div class="name">{{ $t("device.detail.button.reset") }}</div>
        </div>
        <div class="function">
          <div class="logo">
            <van-image
              :src="require('../../../assets/device/detail/shutdown.svg')"
              alt=""
            />
          </div>
          <div class="name">{{ $t("device.detail.button.shutdown") }}</div>
        </div>
        <div class="function" @click="jumpToPage('configureChannel')">
          <div class="logo">
            <van-image
              :src="
                require('../../../assets/device/detail/configure-channel.svg')
              "
              alt=""
            />
          </div>
          <div class="name">
            {{ $t("device.detail.button.configureChannel") }}
          </div>
        </div>
        <div class="function">
          <div class="logo">
            <van-image
              :src="require('../../../assets/device/detail/can-connect.svg')"
              alt=""
            />
          </div>
          <div class="name">
            {{ $t("device.detail.button.removeSecretKey") }}
          </div>
        </div>
        <div class="function">
          <div class="logo">
            <van-image
              :src="require('../../../assets/device/detail/can-connect.svg')"
              alt=""
            />
          </div>
          <div class="name">
            {{ $t("device.detail.button.updateSecretKey") }}
          </div>
        </div>
        <div class="function">
          <div class="logo">
            <van-image
              :src="require('../../../assets/device/detail/can-connect.svg')"
              alt=""
            />
          </div>
          <div class="name">
            {{ $t("device.detail.button.modifyDeviceName") }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "deviceDetail",
  components: {
    navBar: () => import("@/components/navigation/navBar.vue"),
  },
  data() {
    return {
      value: 1,
    };
  },

  mounted() {
    if (window.history && window.history.pushState) {
      history.pushState(null, null, document.URL);
      window.addEventListener("popstate", this.goBack, false); //false阻止默认事件
    }
  },

  destroyed() {
    window.removeEventListener("popstate", this.goBack, false); //false阻止默认事件
  },

  methods: {
    goBack() {
      this.$router.replace("/home");
    },
    jumpToPage(page) {
      this.$router.push("/home/deviceDetail/" + page);
    },
  },
};
</script>
<style lang='scss' scoped>
.content {
  // 基础信息
  .base-info-box {
    height: 4.65rem;
    background: #ffffff;
    box-shadow: 0rem 0.04rem 0.06rem 0.01rem rgba(0, 0, 0, 0.1);
    border-radius: 0.1rem 0.1rem 0.1rem 0.1rem;
  }

  .special-info-box {
    height: 3.05rem;
    background: #ffffff;
    box-shadow: 0rem 0.04rem 0.06rem 0.01rem rgba(0, 0, 0, 0.1);
    border-radius: 0.1rem 0.1rem 0.1rem 0.1rem;
    margin-top: 0.16rem;
  }

  .function-button {
    margin-top: 0.29rem;
    display: flex;
    flex-wrap: wrap;
    .function {
      height: 1.6rem;
      width: 33.33%;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-direction: column;
      &:nth-child(n + 4) {
        margin-top: 0.36rem;
      }
      .logo {
        width: 1rem;
        height: 1rem;
        background: #ffffff;
        box-shadow: 0rem 0.04rem 0.06rem 0.01rem rgba(0, 0, 0, 0.1);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        .van-image {
          width: 0.5rem;
          height: 0.5rem;
        }
      }
      .name {
        margin-top: 0.1rem;
        height: 0.42rem;
        font-size: 0.28rem;
        font-family: PingFang SC-Regular, PingFang SC;
        font-weight: 400;
        line-height: 0.39rem;
        display: flex;
        // justify-content: center;
        align-items: center;
        text-align: center;
      }
    }
  }
}
</style>
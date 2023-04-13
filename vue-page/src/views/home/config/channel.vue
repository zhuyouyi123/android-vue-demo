<!-- 配置通道 -->
<template>
  <div>
    <nav-bar>
      <van-icon name="arrow-left" slot="left" class="navIcon" @click="goBack" />
      <div slot="title">{{ $t("pageTitle.configureChannel") }}</div>
    </nav-bar>

    <div class="content">
      <div class="configured-channel">
        {{ i18nInfo.configChannel.channelConfigured }}
        <div class="num">2/6</div>
      </div>
      <!-- 通道列表 -->
      <div class="channel-list">
        <div
          class="channel"
          :style="{ height: item.sh ? '4.68rem' : '1.26rem' }"
          v-for="(item, index) in channelInfo"
        >
          <div class="top">
            <div class="name">
              {{ i18nInfo.channel + " " + (index + 1) }}
            </div>

            <div class="sh">
              <van-button size="10" @click="showDetail(item)">
                {{ item.sh ? "收起" : "展开" }}</van-button
              >
            </div>
          </div>
          <div class="bottom">
            <div class="frame-type">
              {{ i18nInfo.frameType + "：" + item.frameType }}
            </div>
            <div class="broadcast-type">
              {{ i18nInfo.broadcastType + "：" + item.broadcastType }}
            </div>
          </div>

          <div class="division-line" v-show="item.sh"></div>
          <div class="channel-detail" v-show="item.sh">
            <div class="content-title">
              {{ i18nInfo.configChannel.broadcastContent }}
            </div>

            <div class="info">
              <div>UUID:1918FC80B1113441A9ACB1001C2FE510</div>
              <div class="basic-info">
                <div>Major:20011</div>
                <div>Minor:888</div>
              </div>
            </div>

            <div class="division-line"></div>

            <div class="content-title">
              {{ i18nInfo.configChannel.basicParams }}
            </div>

            <div class="info">
              <div class="basic-info">
                <div>{{ i18nInfo.configChannel.broadcastInterval }}:500ms</div>
                <div>
                  {{ i18nInfo.configChannel.calibrationDistance }}:-79dBm
                </div>
              </div>
              <div>{{ i18nInfo.configChannel.broadcastPower }}:0dBm</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 按钮 -->
      <van-button class="add">{{ $t("baseButton.add") }}</van-button>
    </div>
  </div>
</template>

<script>
import { Collapse, CollapseItem } from "vant";
export default {
  name: "configureChannel",
  components: {
    [Collapse.name]: Collapse,
    [CollapseItem.name]: CollapseItem,
    navBar: () => import("@/components/navigation/navBar.vue"),
  },

  data() {
    return {
      // 国际化信息
      i18nInfo: this.$i18n.t("device.channel"),
      activeNames: [],
      // 通道信息
      channelInfo: [
        {
          index: 1,
          frameType: "iBeacon",
          broadcastType: "始终广播",
          sh: true,
        },
        {
          index: 2,
          frameType: "ACC",
          broadcastType: "触发广播",
          sh: false,
        },
        {
          index: 3,
          frameType: "ACC",
          broadcastType: "触发广播",
          sh: false,
        },
        {
          index: 4,
          frameType: "ACC",
          broadcastType: "触发广播",
          sh: false,
        },
      ],
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
      console.log(1111);
      this.$router.replace("/home/deviceDetail");
    },

    showDetail(item) {
      item.sh = !item.sh;
    },
  },
};
</script>
<style lang='scss' scoped>
.configured-channel {
  display: flex;
  font-size: 0.32rem;
  font-family: Source Han Sans CN-Regular, Source Han Sans CN;
  font-weight: 400;
  .num {
    color: #007fff;
  }
}
.channel-list {
  height: 86%;
  overflow: auto;
  margin-top: 0.2rem;
  font-family: Source Han Sans CN-Regular, Source Han Sans CN;
  .channel {
    height: 4.68rem;
    background: #ffffff;
    box-shadow: 0rem 0.04rem 0.06rem 0.01rem rgba(0, 0, 0, 0.1);
    border-radius: 0.1rem 0.1rem 0.1rem 0.1rem;
    &:nth-child(n + 2) {
      margin-top: 0.16rem;
    }
    .top {
      height: 0.32rem;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 0.27rem;
      padding-top: 0.23rem;
      .name {
        font-size: 0.32rem;
        color: #000000;
      }
      .sh .van-button {
        width: 0.76rem;
        height: 0.4rem;
        font-size: 0.26rem;
        color: #007fff;
        border-radius: 0.05rem 0.05rem 0.05rem 0.05rem;
        border: 0.01rem solid #007fff;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }
    .bottom {
      height: 0.71rem;
      padding: 0 0.27rem;
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-size: 0.28rem;
      font-weight: 400;
      color: #000000;
    }
  }

  .channel-detail {
    .content-title {
      padding: 0 0.27rem;
      padding-top: 0.2rem;
      font-size: 0.32rem;
      font-weight: 400;
      color: #000000;
    }
    .info {
      padding: 0 0.27rem;
      margin-top: 0.05rem;
      font-size: 0.28rem;
      font-weight: 400;
      color: #000000;
      .basic-info {
        height: 0.51rem;
        display: flex;
        justify-content: space-between;
      }
    }
  }

  >>> .van-cell__title {
    font-size: 0.32rem;
    font-weight: 400;
    color: #000000;
  }
}
.add {
  width: 7.02rem;
  height: 0.88rem;
  background: #007fff;
  box-shadow: 0rem 0.04rem 0.06rem 0.01rem rgba(0, 0, 0, 0.1);
  border-radius: 0.44rem 0.44rem 0.44rem 0.44rem;

  font-size: 0.36rem;
  font-family: PingFang SC-Regular, PingFang SC;
  font-weight: 400;
  color: #ffffff;
  line-height: 0.29rem;

  position: absolute;
  bottom: 0.36rem;
}
</style>
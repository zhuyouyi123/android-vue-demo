<!-- 批量配置首页 -->
<template>
  <div class="content">
    <nav-bar>
      <van-icon name="arrow-left" slot="left" class="navIcon" @click="goBack" />
      <div slot="title">{{ $t("pageTitle.batchConfigureChannel") }}</div>
    </nav-bar>

    <div class="to-be-configured">
      {{ batchI18nInfo.lable.channelToBeConfigured }}
      ：
      <span> {{ toBeConfiguredChannelList.length }}/6 </span>
    </div>

    <!-- 通道列表 -->
    <div class="channel-list">
      <div class="channel" v-for="(item, index) in toBeConfiguredChannelList">
        <van-swipe-cell>
          <div class="top">
            <div class="name">
              {{ i18nInfo.channel + " " + index }}
            </div>

            <div class="sh">
              <van-button size="10" @click="showDetail(index)">
                {{
                  item.sh
                    ? batchI18nInfo.button.retract
                    : batchI18nInfo.button.expand
                }}
              </van-button>
            </div>
          </div>
          <div class="bottom">
            <div class="frame-type">
              {{ i18nInfo.frameType + "：" + item.frameType }}
            </div>
            <div class="broadcast-type">
              {{
                i18nInfo.broadcastType +
                "：" +
                (item.triggerSwitch && item.alwaysBroadcast
                  ? batchI18nInfo.lable.triggerBroadcast
                  : batchI18nInfo.lable.alwaysBroadcast)
              }}
            </div>
          </div>
          <div class="channel-detail" v-show="item.sh">
            <div class="division-line"></div>
            <div class="content-title">
              {{ i18nInfo.common.basicParams }}
            </div>

            <div class="info">
              <div class="basic-info">
                <div>
                  {{ i18nInfo.common.broadcastInterval }}:
                  {{ item.broadcastInterval }}ms
                </div>
                <div>
                  Rssi @ 1m:
                  {{ item.calibrationDistance }}dBm
                </div>
              </div>
              <div>
                {{ i18nInfo.common.broadcastPower }}:
                {{ item.broadcastPower }}dBm
              </div>
            </div>

            <!-- 触发器 -->
            <div class="division-line" v-show="item.triggerSwitch"></div>

            <div class="content-title" v-show="item.triggerSwitch">
              {{ i18nInfo.common.triggerParams }}
            </div>

            <div class="info" v-show="item.triggerSwitch">
              <div class="basic-info">
                <div>
                  {{ i18nInfo.common.triggerCondition }}:
                  {{ item.triggerCondition }}
                </div>
                <div>
                  {{ i18nInfo.common.broadcastTime }}:
                  {{ item.triggerBroadcastTime }}s
                </div>
              </div>
              <div class="basic-info">
                <div>
                  {{ i18nInfo.common.broadcastPower }}:
                  {{ item.broadcastPower }}dBm
                </div>
                <div>
                  {{ i18nInfo.common.broadcastInterval }}:
                  {{ item.broadcastInterval }}ms
                </div>
              </div>
            </div>

            <div style="height: 0.2rem"></div>
          </div>

          <template #right>
            <van-button
              @click="deleteChannel(index)"
              square
              type="danger"
              :text="$t('baseButton.remove')"
            />
          </template>
        </van-swipe-cell>
      </div>
    </div>

    <!-- 功能组 -->
    <div class="button-group">
      <div
        class="function"
        v-for="(item, index) in functions"
        @click="buttonClick(index)"
      >
        <div class="round">
          <van-image :src="item.imgPath"></van-image>
        </div>
        <div class="lable">{{ item.lable }}</div>
      </div>
    </div>

    <!-- tool -->

    <div>
      <van-overlay :show="configOverlay">
        <div class="wrapper" @click.stop>
          <div class="block">
            <van-loading vertical type="spinner" color="#ffffff">
              {{ $t("device.batchConfigChannel.message.configuration") }}
              {{ alreadConfigNum }}
              /
              {{ allConfigNum }}
              ...
            </van-loading>
          </div>
        </div>
      </van-overlay>

      <!-- 弹窗 -->
      <van-dialog
        v-model="secretKeyDialog"
        :title="batchI18nInfo.message.secretKeySetting"
        show-cancel-button
        :cancel-button-text="$t('baseButton.cancel')"
        :confirm-button-text="$t('baseButton.sure')"
        :before-close="secretKeyDialogBeforeClose"
        theme="round-button"
      >
        <van-field
          maxlength="6"
          v-model.trim="secretKey"
          :placeholder="batchI18nInfo.message.secretDialogPlaceholder"
        />
      </van-dialog>
    </div>
  </div>
</template>

<script>
import {
  Notify,
  Collapse,
  CollapseItem,
  SwipeCell,
  Overlay,
  Loading,
  Dialog,
} from "vant";
export default {
  data() {
    return {
      i18nInfo: this.$i18n.t("device.channel"),
      batchI18nInfo: this.$i18n.t("device.batchConfigChannel"),
      // 功能列表
      functions: [
        {
          lable: this.$i18n.t("device.batchConfigChannel").lable.addChannel,
          imgPath: require("@/assets/image/batch/add-channel.png"),
        },
        {
          lable: this.$i18n.t("device.batchConfigChannel").lable.cancelConfig,
          imgPath: require("@/assets/image/batch/cancel-config.png"),
        },
        {
          lable: this.$i18n.t("device.batchConfigChannel").lable.configCahnnel,
          imgPath: require("@/assets/image/batch/config-channel.png"),
        },
      ],

      toBeConfiguredChannelList: [],

      // 配置通道遮罩
      configOverlay: false,
      // 已配置数量
      alreadConfigNum: 0,
      allConfigNum: 0,

      executable: true,

      secretKeyDialog: false,
      secretKey: "",
    };
  },

  components: {
    [Collapse.name]: Collapse,
    [CollapseItem.name]: CollapseItem,
    [Overlay.name]: Overlay,
    [Loading.name]: Loading,
    [SwipeCell.name]: SwipeCell,
    navBar: () => import("@/components/navigation/navBar.vue"),
  },
  mounted() {
    if (window.history && window.history.pushState) {
      history.pushState(null, null, document.URL);
      //false阻止默认事件
      window.addEventListener("popstate", this.goBack, false);
    }
    this.loadingToBeConfiguredChannel();
  },

  destroyed() {
    //false阻止默认事件
    window.removeEventListener("popstate", this.goBack, false);
  },

  methods: {
    goBack() {
      this.$router.replace("/home");
    },

    /**
     * 加载待配置通道
     */
    loadingToBeConfiguredChannel() {
      this.toBeConfiguredChannelList = this.$storage.toBeConfiguredChannelList;

      console.log(this.toBeConfiguredChannelList);
      this.toBeConfiguredChannelList.forEach((e) => (e.sh = false));
      this.allConfigNum = this.$storage.toBeConfiguredList.length;
    },

    /**
     * 显示详情
     * @param {序号} index
     */
    showDetail(index) {
      let item = this.toBeConfiguredChannelList[index];
      item.sh = !item.sh;
      this.$set(this.toBeConfiguredChannelList, index, item);
    },

    /**
     * 点击按钮
     */
    buttonClick(e) {
      switch (e) {
        case 0:
          if (this.toBeConfiguredChannelList.length == 6) {
            return;
          }
          this.$router.replace("channel-home/add");
          break;
        case 1:
          this.$router.replace("/home");
          break;
        case 2:
          // 配置通道
          // 校验
          if (!this.executable) {
            Notify({ type: "warning", message: "请退出当前页面，重新编辑" });
            return;
          }
          this.secretKeyDialog = true;

          if (
            !this.toBeConfiguredChannelList ||
            this.toBeConfiguredChannelList.length == 0
          ) {
            Notify({
              type: "warning",
              message: this.batchI18nInfo.message.addChannelFirst,
            });
            return;
          }

          let data = this.$storage.toBeConfiguredChannelList;
          // 只有一条 不能是Coreaiot
          if (data) {
            if (
              data.length == 1 &&
              data[0].frameType == this.$storage.frameTypes[7].name
            ) {
              Notify({
                type: "warning",
                message: this.batchI18nInfo.message.notOnlyExistCoreaiot,
              });
              return;
            }
            let alwaysBroadcast = false;
            data.forEach((e) => {
              if (e.alwaysBroadcast) {
                alwaysBroadcast = true;
              }
            });
            if (!alwaysBroadcast) {
              Notify({
                type: "warning",
                message: this.batchI18nInfo.message.atLeastOneAlwaysBroadcast,
              });
              return;
            }
          }
          break;
        default:
          break;
      }
    },

    deleteChannel(e) {
      this.$storage.toBeConfiguredChannelList.splice(e, 1);
    },

    secretKeyDialogBeforeClose(action, done) {
      if (action == "confirm") {
        if (!this.secretKey || this.secretKey.length != 6) {
          Notify({
            type: "warning",
            message: this.$i18n.t("notifyMessage.base.paramsError"),
          });
          done(false);
        }

        this.$storage.batchConfigChannelInfo.batchConfigChannelFlag = true;
        this.$storage.batchConfigChannelInfo.secretKey = this.secretKey;

        this.$router.replace("/home");
        // return;

        // let params = {
        //   secretKey: this.secretKey,
        //   addressJson: JSON.stringify(this.$storage.toBeConfiguredList),
        //   beaconListJson: JSON.stringify(
        //     this.$storage.toBeConfiguredChannelList
        //   ),
        // };
        // this.$androidApi
        //   .batchConfigChannel(params)
        //   .then(() => {
        //     this.configOverlay = true;
        //     this.alreadConfigNum = 0;
        //     this.queryConfigResultList();
        //   })
        //   .catch((errorMsg) => {
        //     Notify({ type: "warning", message: errorMsg });
        //   });
      }
      done();
    },

    queryConfigResultList() {
      if (this.interval) {
        clearInterval(this.interval);
      }
      this.interval = setInterval(() => {
        this.$androidApi.batchConfigChannelList().then((res) => {
          console.log(res);
          let num = 0;
          let successNum = 0;
          let failNum = 0;
          if (res && res.length > 0) {
            res.forEach((e) => {
              if (e.state != 1) {
                num++;
                if (e.state == 0) {
                  successNum++;
                } else {
                  failNum++;
                }
              }
            });
            this.alreadConfigNum = num;
            if (num == this.allConfigNum) {
              this.executable = false;
              clearInterval(this.interval);
              this.configOverlay = false;
              Dialog.confirm({
                title: this.batchI18nInfo.lable.configResult,
                message:
                  "本次配置共" +
                  this.allConfigNum +
                  "个设备：<br>" +
                  "成功：" +
                  successNum +
                  this.batchI18nInfo.message.individua +
                  "，失败：" +
                  failNum +
                  this.batchI18nInfo.message.individua,
                className: "warnDialogClass",
                cancelButtonText: this.batchI18nInfo.button.errorList,
                confirmButtonText: this.$i18n.t("baseButton.sure"),
                theme: "round-button",
                messageAlign: "left",
              })
                .then(() => {})
                .catch(() => {});
            }
          }
        });
      }, 1500);
    },
  },
};
</script>
<style lang='scss' scoped>
.to-be-configured {
  height: 0.7rem;
  font-size: 0.32rem;
  font-family: Source Han Sans CN-Regular, Source Han Sans CN;
  font-weight: 400;
  span {
    color: #007fff;
  }
}

.channel-list {
  .channel {
    background: #ffffff;
    box-shadow: 0rem 0.04rem 0.06rem 0.01rem rgba(0, 0, 0, 0.1);
    border-radius: 0.1rem 0.1rem 0.1rem 0.1rem;
    &:nth-child(n + 2) {
      margin-top: 0.16rem;
    }
    .top {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 0.27rem;
      padding-top: 0.13rem;
      .name {
        font-size: 0.32rem;
        color: #000000;
      }
      .sh .van-button {
        width: 1.1rem;
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
      font-size: 0.23rem;
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
      margin-top: 0.08rem;
      font-size: 0.28rem;
      font-weight: 400;
      color: #000000;
      line-height: 0.43rem;

      .basic-info {
        height: 0.53rem;
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

.button-group {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 0.4rem;
  .function {
    .round {
      display: flex;
      justify-content: center;
      align-items: center;
      width: 1rem;
      height: 1rem;
      background: #ffffff;
      box-shadow: 0rem 0.04rem 0.06rem 0.01rem rgba(0, 0, 0, 0.1);
      border-radius: 50%;
      margin: 0 0.51rem;
      .van-image {
        width: 0.46rem;
        height: 0.46rem;
      }
    }
    .lable {
      text-align: center;
      line-height: 0.7rem;
    }
  }
}

.van-overlay {
  background: #00000025;
}
.wrapper {
  background: rgb(163, 153, 153);
  height: 2rem;
  width: 3rem;
  .block {
    display: flex;
    align-items: center;
    justify-content: center;
  }
}
</style>
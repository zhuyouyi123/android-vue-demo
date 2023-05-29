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
        <div class="num">{{ configuredNumber }}/6</div>
      </div>
      <!-- 通道列表 -->
      <div class="channel-list">
        <div class="channel" v-for="(item, index) in channelInfo">
          <van-swipe-cell>
            <div class="top">
              <div class="name">
                {{ i18nInfo.channel + " " + item.index }}
              </div>

              <div class="sh">
                <van-button
                  size="10"
                  @click="showDetail(item)"
                  :disabled="!item.used"
                >
                  {{
                    item.sh ? i18nInfo.button.retract : i18nInfo.button.expand
                  }}</van-button
                >
              </div>
            </div>
            <div class="bottom">
              <div class="frame-type">
                {{
                  i18nInfo.frameType +
                  "：" +
                  (item.used ? getFrameType(item.frameType) : "Empty")
                }}
              </div>
              <div class="broadcast-type">
                {{
                  i18nInfo.broadcastType +
                  "：" +
                  (item.used ? getBroadcastType(item.broadcastType) : "Empty")
                }}
              </div>
            </div>
            <div
              class="division-line"
              v-show="item.sh && item.broadcastContentShow"
            ></div>
            <div class="channel-detail" v-show="item.sh">
              <div class="content-title" v-show="item.broadcastContentShow">
                {{ i18nInfo.common.broadcastContent }}
              </div>

              <div class="info">
                <div v-if="item.agreementData.data1">
                  {{ item.agreementData.data1 }}
                </div>
                <div class="basic-info" v-show="item.broadcastContentShow">
                  <div v-if="item.agreementData.data2">
                    {{ item.agreementData.data2 }}
                  </div>
                  <div v-if="item.agreementData.data3">
                    {{ item.agreementData.data3 }}
                  </div>
                </div>
              </div>

              <div class="division-line"></div>

              <div class="content-title">
                {{ i18nInfo.common.basicParams }}
              </div>

              <div class="info">
                <div class="basic-info">
                  <div>
                    {{ i18nInfo.common.broadcastInterval }}:
                    {{ item.basicParams.broadcastInterval }}ms
                  </div>
                  <div>
                    Rssi @ 1m:
                    {{ item.basicParams.calibrationDistance }}dBm
                  </div>
                </div>
                <div>
                  {{ i18nInfo.common.broadcastPower }}:
                  {{ item.basicParams.broadcastPower }}dBm
                </div>
              </div>

              <!-- 触发器 -->
              <div
                class="division-line"
                v-show="item.triggerData.triggerSwitch"
              ></div>

              <div
                class="content-title"
                v-show="item.triggerData.triggerSwitch"
              >
                {{ i18nInfo.common.triggerParams }}
              </div>

              <div class="info" v-show="item.triggerData.triggerSwitch">
                <div class="basic-info">
                  <div>
                    {{ i18nInfo.common.triggerCondition }}:
                    {{ item.triggerData.triggerCondition }}
                  </div>
                  <div>
                    {{ i18nInfo.common.broadcastTime }}:
                    {{ item.triggerData.triggerTime }}s
                  </div>
                </div>
                <div class="basic-info">
                  <div>
                    {{ i18nInfo.common.broadcastPower }}:
                    {{ item.triggerData.broadcastPower }}dBm
                  </div>
                  <div>
                    {{ i18nInfo.common.broadcastInterval }}:
                    {{ item.triggerData.broadcastInterval }}ms
                  </div>
                </div>
                <div></div>
              </div>

              <div style="height: 0.2rem"></div>
            </div>

            <template #right v-if="!item.used">
              <van-button
                square
                type="primary"
                @click="updateChannel(item)"
                :text="$t('baseButton.add')"
              />
            </template>
            <template #right v-else>
              <van-button
                square
                type="primary"
                @click="updateChannel(item)"
                :text="$t('baseButton.update')"
              />
              <van-button
                square
                type="danger"
                @click="deleteChannel(item)"
                :text="$t('baseButton.remove')"
              />
            </template>
          </van-swipe-cell>
        </div>
      </div>
    </div>

    <!-- 遮罩层 -->
    <van-overlay :show="overlayShow">
      <van-loading type="spinner" color="#1989fa" />
    </van-overlay>
  </div>
</template>

<script>
import { Collapse, CollapseItem, Dialog, SwipeCell, Notify } from "vant";
import deviceDetailHelper from "../hepler/deviceDetailHelper";
import instruct from "@/fetch/instruct";
export default {
  name: "configureChannel",
  components: {
    [Collapse.name]: Collapse,
    [CollapseItem.name]: CollapseItem,
    [SwipeCell.name]: SwipeCell,
    navBar: () => import("@/components/navigation/navBar.vue"),
  },

  data() {
    return {
      // 国际化信息
      i18nInfo: this.$i18n.t("device.channel"),
      address: this.$route.query.address,
      // 已配置通道数
      configuredNumber: 0,
      // 触发器
      triggerActions: this.$storage.triggerActions,
      // 广播类型
      broadcastTypes: [],

      frameTypes: this.$storage.frameTypes,

      // 通道信息
      channelInfo: [],
      // 遮罩层
      overlayShow: false,
      // 操作通道
      operationChannelNumber: "",
    };
  },

  mounted() {
    if (window.history && window.history.pushState) {
      history.pushState(null, null, document.URL);
      //false阻止默认事件
      window.addEventListener("popstate", this.goBack, false);
    }

    window.addEventListener("commonAndroidEvent", this.callJs);

    this.broadcastTypes = [
      {
        code: 0,
        name: this.i18nInfo.common.triggerBroadcast,
      },
      {
        code: 1,
        name: this.i18nInfo.common.alwaysBroadcast,
      },
    ];

    this.overlayShow = true;
    // 需要重新加载通道信息
    if (
      this.$route.query.reloadChannel &&
      this.$route.query.reloadChannel == "RELOAD"
    ) {
      this.readConfigAgreementInfo(this.$route.query.channelNumber).then(() => {
        this.loadingChannelInfo();
      });
    } else {
      this.loadingChannelInfo();
    }
  },

  destroyed() {
    //false阻止默认事件
    window.removeEventListener("popstate", this.goBack, false);
    window.removeEventListener("commonAndroidEvent", this.callJs);
  },

  methods: {
    goBack() {
      this.$router.replace({
        path: "/home/deviceDetail",
        query: {
          address: this.address,
        },
      });
    },

    /**
     * 加载通道信息
     */
    loadingChannelInfo() {
      this.$storage.agreementInfoList = null;
      deviceDetailHelper
        .getConnectDetail(this.address)
        .then((res) => {
          console.log(res);
          this.channelInfo = [];
          let agreementInfoList = res.agreementInfoList;
          // 存储赋值storage
          this.$storage.agreementInfoList = agreementInfoList;
          this.configuredNumber = 0;
          for (let i = 0; i < agreementInfoList.length; i++) {
            let agreementInfo = agreementInfoList[i];
            let channel = {
              sh: false,
              frameType: agreementInfo.agreementType,
              index: agreementInfo.channelNumber,
              used: false,
              broadcastType: 0,
              basicParams: {},
              agreementData: [],
              triggerData: {},
              broadcastContentShow: false,
            };
            // 协议类型
            if (agreementInfo.agreementType != 0) {
              this.configuredNumber++;
              channel.used = true;
              channel.broadcastType =
                agreementInfo.alwaysBroadcast == true ? 1 : 0;

              channel.basicParams = {
                // 广播间隔
                broadcastInterval: agreementInfo.broadcastInterval,
                // 校准距离
                calibrationDistance:
                  agreementInfo.calibrationDistance == 0
                    ? 0
                    : agreementInfo.calibrationDistance - 0xff - 1,
                // 广播功率
                broadcastPower: agreementInfo.broadcastPowerValue,
              };

              // 触发器开关打开
              if (agreementInfo.triggerSwitch) {
                channel.triggerData.triggerSwitch = agreementInfo.triggerSwitch;
                channel.triggerData.broadcastInterval =
                  agreementInfo.txInterval;
                channel.triggerData.triggerTime = agreementInfo.triggerTime;
                channel.triggerData.broadcastPower =
                  agreementInfo.triggerBroadcastPowerValue;
                channel.triggerData.triggerCondition =
                  this.$storage.triggerActions[
                    agreementInfo.triggerCondition - 1
                  ].name;
              } else {
                channel.triggerData.triggerSwitch = false;
              }

              let dataLength = agreementInfo.agreementData.length;
              switch (agreementInfo.agreementType) {
                case "1":
                  // 设备显示广播内容
                  channel.broadcastContentShow = true;
                  channel.agreementData.data1 =
                    dataLength >= 0
                      ? "UUID：" + agreementInfo.agreementData[0]
                      : "";
                  channel.agreementData.data2 =
                    dataLength >= 1
                      ? "Major：" + agreementInfo.agreementData[1]
                      : "";
                  channel.agreementData.data3 =
                    dataLength >= 2
                      ? "Minor：" + agreementInfo.agreementData[2]
                      : "";
                  break;
                case "2":
                  // 设备显示广播内容
                  channel.broadcastContentShow = true;
                  channel.agreementData.data1 =
                    dataLength >= 0
                      ? "NameSpaceId：" + agreementInfo.agreementData[0]
                      : "";
                  channel.agreementData.data2 =
                    dataLength >= 1
                      ? "InstanceId：" + agreementInfo.agreementData[1]
                      : "";
                  break;
                case "3":
                  // 设备显示广播内容
                  channel.broadcastContentShow = true;
                  channel.agreementData.data1 =
                    dataLength >= 3
                      ? "URL：" +
                        agreementInfo.agreementData[0] +
                        agreementInfo.agreementData[1] +
                        agreementInfo.agreementData[2]
                      : "";
                  break;
                default:
                  break;
              }
            }
            this.channelInfo.push(channel);
          }
          this.overlayShow = false;
        })
        .catch(() => {
          this.overlayShow = false;
        });
    },

    /**
     * 读取通道信息
     */
    readConfigAgreementInfo(channelNumber) {
      return new Promise((resolve, reject) => {
        if (channelNumber) {
          deviceDetailHelper.writeData(
            this.address,
            instruct.QUERY_CONFIG_AGREEMENT_REQUEST,
            channelNumber.toString()
          );
          setTimeout(() => {
            resolve();
          }, 100);
        } else {
          let num = 0;
          let interval = setInterval(() => {
            if (num < 6) {
              deviceDetailHelper.writeData(
                this.address,
                instruct.QUERY_CONFIG_AGREEMENT_REQUEST,
                num.toString()
              );
              num++;
            } else {
              clearInterval(interval);
              interval = null;
              setTimeout(() => {
                resolve();
              }, 100);
            }
          }, 100);
        }
      });
    },

    /**
     * 获取通道类型
     * @param {通道编号} frameType
     */
    getFrameType(frameType) {
      if (frameType && frameType != 0) {
        let type = this.frameTypes[frameType - 1];
        return type.name;
      }
      return "Empty";
    },
    /**
     * 获取通道类型
     * @param {通道编号} frameType
     */
    getBroadcastType(broadcastType) {
      if (broadcastType == 1) {
        return this.i18nInfo.common.alwaysBroadcast;
      } else if (broadcastType == 0) {
        return this.i18nInfo.common.triggerBroadcast;
      } else {
        return "Empty";
      }
    },

    showDetail(item) {
      item.sh = !item.sh;
    },

    deleteChannel(item) {
      if (item.used) {
        Dialog.confirm({
          title: this.i18nInfo.title.deleteChannel,
          message: this.i18nInfo.tips.deleteChannelTips,
          className: "warnDialogClass",
          cancelButtonText: this.$i18n.t("baseButton.cancel"),
          confirmButtonText: this.$i18n.t("baseButton.sure"),
          theme: "round-button",
          messageAlign: "left",
        })
          .then(() => {
            this.overlayShow = true;
            this.operationChannelNumber = item.index;
            // 删除通道
            let saveForm = {
              frameType: "EMPTY",
              channelNumber: item.index,
            };
            this.$androidApi
              .saveChannelConfig(saveForm)
              .then(() => {
                let e = this.channelInfo[item.index];
                e.sh = false;
                e.used = false;

                let bakArray = [];
                bakArray = [...this.channelInfo];
                this.channelInfo = [];
                setTimeout(() => {
                  this.channelInfo = bakArray;
                  this.overlayShow = false;
                }, 100);
              })
              .catch((errorMsg) => {
                Notify({ type: "warning", message: errorMsg });
                this.overlayShow = false;
              });
          })
          .catch((errorMsg) => {
            Notify({ type: "warning", message: errorMsg });
            this.overlayShow = false;
          });
      }
    },

    /**
     * 编辑或者添加通道
     * @param {通道信息} item
     */
    updateChannel(item) {
      console.log(item);
      // 编辑
      this.$router.replace({
        path: "/home/deviceDetail/configureChannel/add",
        query: {
          address: this.address,
          channel: item.index,
        },
      });
    },

    /**
     * 添加通道
     */
    add(item) {
      // 编辑
      this.$router.replace({
        path: "/home/deviceDetail/configureChannel/add",
        query: {
          address: this.address,
          channel: item.channelNumber,
        },
      });
    },

    callJs(e) {
      let result = e.data;
      let content = result.data;
      if (result.eventName == instruct.NOTIFY_STATUS_CHANGE) {
        if (content.type) {
          let notifyData = content.data;
          console.log(JSON.stringify(notifyData));
          switch (content.type) {
            case instruct.CHANNEL_CONFIG_BEACON_RESPONSE:
              if (notifyData.code == 0) {
                Notify({
                  type: "warning",
                  message: this.$i18n.t("notifyMessage.base.operationError"),
                });
              }
              break;
            case instruct.CHANNEL_CONFIG_BEACON_RESULT:
              if (notifyData.code == 1) {
                Notify({
                  type: "warning",
                  message: this.$i18n.t("notifyMessage.base.operationError"),
                });
              } else {
                Notify({
                  type: "success",
                  message: this.$i18n.t("notifyMessage.base.operationSuccess"),
                });
                this.readConfigAgreementInfo(this.operationChannelNumber).then(
                  () => {
                    this.loadingChannelInfo();
                  }
                );
              }
              break;
          }
        }
      }
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
      .redName {
        color: rgb(242, 145, 26);
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
      font-size: 0.25rem;
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
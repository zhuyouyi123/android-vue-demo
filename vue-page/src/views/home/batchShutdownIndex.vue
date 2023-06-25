<!-- 批量关机 -->
<template>
  <div class="content" :class="{ 'mini-content': isChildren }">
    <nav-bar v-if="!isChildren"> </nav-bar>

    <div>
      <van-cell-group inset>
        <van-cell title="总设备数量（个）" :value="list.length"></van-cell>
        <van-cell title="待关机数量（个）" :value="standbyNumber"></van-cell>
      </van-cell-group>

      <div class="function-box">
        <div class="function">
          <van-icon name="scan" size="26" color="#007FFF" @click="scanQrCode" />
        </div>

        <div class="function">
          <van-image
            :src="require('../../assets/image/device/detail/shutdown.png')"
            @click="shutdown"
          >
          </van-image>
        </div>
        <div class="function">
          <van-image
            :src="require('../../assets/image/batch/failed-list.svg')"
            @click="queryFialedList"
          >
          </van-image>
        </div>
      </div>

      <van-cell-group
        inset
        class="list-box"
        :class="{ 'mini-list-box': isChildren }"
      >
        <van-cell v-if="list.length == 0" title="请先扫码录入设备"></van-cell>

        <van-cell
          class="device-item"
          v-show="list.length > 0"
          v-for="(item, index) in list"
          clickable
          :key="item.address"
          :title="item.address"
          :value="item.state"
          @click="shutdownOne(index)"
          :class="{
            'already-shutdown': item.state == '已关机',
          }"
        >
        </van-cell>
      </van-cell-group>

      <van-overlay :show="overlayShow">
        <div class="wrapper" @click.stop>
          <div class="block">
            <van-loading vertical type="spinner" color="#ffffff">
              配置中
              {{ alreadConfigNum }}
              /
              {{
                shutdownMode && shutdownMode.list && shutdownMode.list.length
              }}
              ...
            </van-loading>
          </div>
        </div>
      </van-overlay>

      <!-- 弹窗 -->
      <van-dialog
        v-model="secretKeyDialog"
        title="填写秘钥"
        show-cancel-button
        cancel-button-text="取消"
        confirm-button-text="确定"
        :before-close="secretKeyDialogBeforeClose"
        theme="round-button"
      >
        <van-field
          maxlength="6"
          v-model.trim="secretKey"
          :placeholder="i18nInfo.tips.secretDialogPlaceholder"
        />
      </van-dialog>

      <!-- 记录 -->
      <!-- 圆角弹窗（底部） -->
      <van-popup
        v-model="batchConfigRecordPopupShow"
        round
        position="bottom"
        :style="{ height: '90%' }"
        closeable
      >
        <div class="title">失败记录</div>
        <div class="list">
          <div class="item" v-for="(item, index) in failedList">
            <div class="item-box">
              <div class="index">{{ index + 1 }}</div>

              <div class="address">
                <div>Mac</div>
                <div>{{ item.address }}</div>
              </div>
              <div class="division-line2"></div>

              <div class="error-reason">
                <div>失败原因</div>
                <div class="failure-reason">{{ item.failReason }}</div>
              </div>
            </div>
          </div>
        </div>

        <div class="retry-button">
          <van-button round @click="retryShutdown(true)">重试</van-button>
        </div>
      </van-popup>
    </div>
  </div>
</template>

<script>
import navBar from "@/components/navigation/navBar.vue";

import {
  Search,
  DropdownMenu,
  DropdownItem,
  Notify,
  Checkbox,
  CheckboxGroup,
  Loading,
  Overlay,
  Dialog,
  Popup,
  Tab,
  Tabs,
} from "vant";
import app from "vue";
app.use(Search);
app.use(DropdownMenu);
app.use(DropdownItem);
app.use(Checkbox);
app.use(CheckboxGroup);
app.use(Loading);
app.use(Overlay);
app.use(Dialog);
app.use(Search);
app.use(Popup);
app.use(Tab);
app.use(Tabs);
app.use(navBar);
export default {
  components: {
    [DropdownMenu.name]: DropdownMenu,
    [DropdownItem.name]: DropdownItem,
    [Search.name]: Search,
    [Checkbox.name]: Checkbox,
    [CheckboxGroup.name]: CheckboxGroup,
    [Loading.name]: Loading,
    [Overlay.name]: Overlay,
    [Popup.name]: Popup,
    [Tab.name]: Tab,
    [Tabs.name]: Tabs,
    navBar: navBar,
  },
  props: ["isChildren"],
  data() {
    return {
      i18nInfo: this.$i18n.t("home"),
      list: [],
      historyRecordMap: null,
      // 错误列表
      failedList: [],
      overlayShow: false,
      batchConfigInterval: null,
      alreadShutdownNum: 0,
      alreadConfigNum: 0,
      // 待关机数量
      standbyNumber: 0,
      // 待关机列表
      standbyList: [],
      // 秘钥弹窗
      secretKeyDialog: false,

      shutdownMode: {
        // multiple single retry
        type: "multiple",
        list: [],
      },
      secretKey: "",
      // 记录弹窗
      batchConfigRecordPopupShow: false,

      // 扫码录入结果
      scanResult: "",

      // 清除历史数据
      clearHistory: true,
      // 重试倒计时
      retryCountdown: 5,
      // 重试定时器
      retryInterval: null,
    };
  },
  mounted() {
    window.addEventListener("commonAndroidEvent", this.callJs);
    if (window.history && window.history.pushState) {
      history.pushState(null, null, document.URL);
      //false阻止默认事件
      window.addEventListener("popstate", this.goBack, false);
      this.$androidApi.clearCanBackHistory();
    }
    this.loadingLastTimeRecords();
  },

  destroyed() {
    //false阻止默认事件
    window.removeEventListener("popstate", this.goBack, false);
    window.removeEventListener("commonAndroidEvent", this.callJs);
  },
  methods: {
    // 退出APP
    goBack() {},
    callJs(e) {
      switch (e.data.eventName) {
        case "SCAN_RESULT":
          // 扫码
          this.scanResult = e.data.data;
          this.handleBatchScanResult(e.data.data);
          break;
        default:
          break;
      }
    },

    /**
     * 加载上一次升级记录
     */
    loadingLastTimeRecords() {
      return new Promise((resolve, reject) => {
        let params = {
          type: 2,
        };
        this.$androidApi.queryBatchConfigRecord(params).then((data) => {
          this.historyRecordMap = new Map();
          this.failedList = [];
          if (data && data.length > 0) {
            data.forEach((e) => {
              if (e.result != 0) {
                this.failedList.push(e);
              }
              this.historyRecordMap.set(e.address, e);
            });
            resolve();
            return;
          }
          resolve();
        });
      });
    },

    scanQrCode() {
      setTimeout(() => {
        this.$androidApi.scanQrCode();
      }, 50);
      this.standbyNumber = 0;
      this.standbyList = [];
      this.alreadShutdownNum = 0;
      // this.handleBatchScanResult("19:18:FC:25:0C:01-19:18:FC:25:0C:02");
    },

    handleBatchScanResult(data) {
      if (!data) {
        Notify({ type: "danger", message: "二维码内容错误" });
        return;
      }

      data = data.trim().replace(/:/g, "");
      let map = new Map();
      if (this.macReg(data)) {
        map.set(data.toUpperCase(), data);
      } else {
        let scanDataSplit = data.split("-");
        let startMac, endMac;
        if (scanDataSplit.length == 2) {
          startMac = parseInt(scanDataSplit[0], 16);
          endMac = parseInt(scanDataSplit[1], 16);
          for (let i = 0; i <= endMac - startMac; i++) {
            map.set((startMac + i).toString(16).toUpperCase(), startMac + i);
          }
        } else {
          scanDataSplit = data.split("_");
          // 19:18:FC:09:DA:EC_19:18:FC:09:DA:ED
          if (scanDataSplit.length == 2) {
            startMac = parseInt(scanDataSplit[0], 16);
            endMac = parseInt(scanDataSplit[1], 16);
          }
          // 19:18:FC:09:DA:EC_1918FC80-B111-3441-A9AC-B1001C2FE510_20001_18851
          else if (scanDataSplit.length == 4) {
            startMac = parseInt(scanDataSplit[0], 16);
            endMac = startMac;
          }
          // 19:18:FC:09:DA:EC_1918FC80-B111-3441-A9AC-B1001C2FE510_20001_18851_16
          else if (scanDataSplit.length == 5) {
            startMac = parseInt(scanDataSplit[0], 16);
            endMac = startMac + parseInt(scanDataSplit[4]) - 1;
          } else {
            Notify({ type: "danger", message: "二维码内容错误" });
            return;
          }
          for (let i = 0; i <= endMac - startMac; i++) {
            map.set((startMac + i).toString(16).toUpperCase(), startMac + i);
          }
        }
      }

      this.list = [];
      this.clearHistory = true;
      if (map.size > 0) {
        map.forEach((v, k) => {
          let address = k.replace(/(.{2})(?!$)/g, "$1:");
          let state = "等待关机";
          if (this.historyRecordMap && this.historyRecordMap.has(address)) {
            // 上一次数据包含此次的设备 不清楚上一次的记录
            this.clearHistory = false;
            let data = this.historyRecordMap.get(address);
            if (data && data.result == 0) {
              state = "已关机";
              this.alreadShutdownNum++;
            } else {
              this.standbyNumber++;
              this.standbyList.push(address);
            }
          } else {
            this.standbyNumber++;
            this.standbyList.push(address);
          }

          let data = {
            address: address,
            state: state,
          };
          this.list.push(data);
        });
      }

      if (this.clearHistory) {
        console.log("需要清除历史数据");
      } else {
        console.log("不清除历史数据");
      }
    },

    retryShutdown(showDialog) {
      if (this.failedList.length == 0) {
        Notify({ type: "danger", message: "当前不存在失败设备" });
        return;
      }
      let retryList = [];
      this.failedList.forEach((e) => retryList.push(e.address));
      this.shutdownMode = {
        type: "retry",
        list: retryList,
      };
      this.secretKeyDialog = showDialog ? showDialog : false;
      this.batchConfigRecordPopupShow = false;
    },

    shutdown() {
      if (this.standbyNumber == 0) {
        Notify({ type: "danger", message: "请先录入需要关机的设备" });
        return;
      }

      Dialog.confirm({
        title: "批量关机提示",
        message: "确认开始进行批量关机吗？",
        className: "warnDialogClass",
        cancelButtonText: "取消",
        confirmButtonText: "确认",
        theme: "round-button",
        messageAlign: "left",
      })
        .then(() => {
          if (this.standbyNumber == 0) {
            Notify({ type: "danger", message: "当前没有设备需要升级" });
            return;
          }
          this.shutdownMode = {
            type: "multiple",
            list: this.standbyList,
          };
          this.secretKeyDialog = true;
        })
        .catch(() => {});
    },

    shutdownOne(e) {
      let item = this.list[e];

      let message =
        item.state == "已关机"
          ? `设备${item.address}可能已经是关机状态了，是否继续进行关机？`
          : `是否将设备${item.address}进行关机？`;
      Dialog.confirm({
        title: "关机提示",
        message: message,
        className: "warnDialogClass",
        cancelButtonText: "取消",
        confirmButtonText: "确认",
        theme: "round-button",
        messageAlign: "left",
      })
        .then(() => {
          this.shutdownMode = {
            type: "single",
            list: [item.address],
          };
          this.secretKeyDialog = true;
        })
        .catch(() => {});
    },

    queryFialedList() {
      this.batchConfigRecordPopupShow = true;
    },

    queryConfigResultList() {
      if (null != this.batchConfigInterval) {
        clearInterval(this.batchConfigInterval);
      }

      this.batchConfigInterval = setInterval(() => {
        let num = 0;
        let successNum = 0;
        let failNum = 0;
        // 查询批量配置记录
        this.$androidApi.batchConfigChannelList().then((res) => {
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

            if (num == this.shutdownMode.list.length) {
              clearInterval(this.batchConfigInterval);
              this.overlayShow = false;

              this.alreadConfigNum = 0;
              this.alreadShutdownNum = 0;
              this.standbyNumber = 0;
              this.standbyList = [];
              this.list = [];

              this.loadingLastTimeRecords().then(() => {
                if (this.scanResult) {
                  this.handleBatchScanResult(this.scanResult);
                }
                if (failNum <= 0) {
                  this.showResultDialog(num, successNum, failNum);
                } else {
                  this.showRetryDialog(num, successNum, failNum);
                }
              });
            }
          }
        });
      }, 1500);
    },

    showResultDialog(num, successNum, failNum) {
      Dialog.confirm({
        title: "批量关机结果",
        message: `批量关机结束，共${num}个设备\r\n成功${successNum}个，失败${failNum}个`,
        className: "warnDialogClass",
        cancelButtonText: "失败列表",
        confirmButtonText: "确认",
        theme: "round-button",
        messageAlign: "left",
        showCancelButton: failNum > 0,
      })
        .then(() => {})
        .catch(() => {
          this.batchConfigRecordPopupShow = true;
        });
    },

    /**
     * 显示重试弹窗
     * @param {成功数量} successNum
     * @param {失败数量} failNum
     */
    showRetryDialog(num, successNum, failNum) {
      this.retryCountdown = 5;
      Dialog.confirm({
        title: this.i18nInfo.title.updateResult,
        message:
          "配置数量：" +
          num +
          "<br>" +
          "成功数量：" +
          successNum +
          "、 " +
          "失败数量：" +
          failNum +
          "<br>" +
          "将在5秒后进行重试，点击查看结果后不会进行重试",
        className: "warnDialogClass",
        cancelButtonText: "查看结果",
        confirmButtonText: "立即重试",
        theme: "round-button",
        messageAlign: "left",
      })
        .then(() => {
          this.retryShutdown(false);
          this.startConfigBatchShutdown();
          clearInterval(this.retryInterval);
          this.retryInterval = null;
        })
        .catch(() => {
          clearInterval(this.retryInterval);
          this.retryInterval = null;
          this.showResultDialog(num, successNum, failNum);
        });
      this.retryInterval = setInterval(() => {
        this.retryCountdown--;
        if (this.retryCountdown == 0) {
          Dialog.close();
          // 开始重试
          this.retryShutdown(false);
          this.startConfigBatchShutdown();
          clearInterval(this.retryInterval);
          this.retryInterval = null;
        }
      }, 1000);
    },

    secretKeyDialogBeforeClose(action, done) {
      if (action == "confirm") {
        if (!this.secretKey || this.secretKey.length != 6) {
          Notify({ type: "danger", message: "秘钥格式错误" });
          done(false);
          return;
        }
        this.startConfigBatchShutdown();
      }
      done();
    },
    startConfigBatchShutdown() {
      this.overlayShow = true;

      this.$androidApi.batchShutdown({
        addressJson: JSON.stringify(this.shutdownMode.list),
        secretKey: this.secretKey,
        clearHistory:
          this.shutdownMode.type == "retry" ? false : this.clearHistory,
      });
      // 设置已配置设备数量0
      this.alreadConfigNum = 0;
      // 查询批量配置结果列表
      this.queryConfigResultList();
    },

    // 验证是否是mac
    macReg(value) {
      let reg = /^[0-9a-fA-F]{12}$/;
      return reg.test(value);
    },
  },
};
</script>
<style lang='scss' scoped>
.mini-content {
  height: 83vh !important;
}
.function-box {
  height: 1.4rem;
  display: flex;
  align-items: center;

  .function {
    width: 1rem;
    height: 1rem;
    background: #ffffff;
    box-shadow: 0rem 0.04rem 0.06rem 0.01rem rgba(0, 0, 0, 0.1);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    &:nth-child(n + 2) {
      margin-left: 0.4rem;
    }
    .van-image {
      width: 0.5rem;
      height: 0.5rem;
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

.list-box {
  height: 70vh;
  overflow: auto;
  .van-cell__value {
    color: rgb(214, 132, 33);
  }
  .already-shutdown {
    .van-cell__value {
      color: rgb(127, 194, 26);
    }
  }
}
.mini-list-box {
  height: 62vh;
}

.van-popup {
  .title {
    height: 0.8rem;
    font-weight: bold;
    font-size: 0.32rem;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .list {
    height: 83%;
    background: #eef3fa;
    overflow: auto;
    color: #000000;
    font-family: Source Han Sans CN-Regular, Source Han Sans CN;
    .item {
      width: 7rem;
      background: #ffffff;
      box-shadow: 0rem 0.04rem 0.06rem 0.01rem rgba(0, 0, 0, 0.1);
      border-radius: 0.1rem 0.1rem 0.1rem 0.1rem;
      margin: auto;
      margin-top: 0.16rem;

      .item-box {
        padding: 0.2rem 0.2rem;
      }
      .index {
        height: 0.32rem;
        font-size: 0.32rem;
        font-weight: 400;
      }
      .address,
      .error-reason {
        min-height: 0.8rem;
        display: flex;
        justify-content: space-between;
        align-items: center;
        .failure-reason {
          width: 4.5rem;
          margin-top: 0.15rem;
          text-align: right;
          font-size: 0.27rem;
        }
      }
    }
  }

  .retry-button {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 1.4rem;

    .van-button {
      height: 0.88rem;
      width: 6rem;
      background: #007fff;
      box-shadow: 0rem 0.04rem 0.06rem 0.01rem rgba(0, 0, 0, 0.1);
      border-radius: 0.44rem 0.44rem 0.44rem 0.44rem;
      font-size: 0.36rem;
      font-family: PingFang SC-Regular, PingFang SC;
      font-weight: 400;
      color: #ffffff;
      line-height: 0.29rem;
    }
  }
}
</style>
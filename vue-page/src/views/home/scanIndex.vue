<!--  -->
<template>
  <div class="content">
    <nav-bar> </nav-bar>
    <van-tabs v-model="active">
      <van-tab title="扫描测试工具">
        <div class="head">
          <van-button round @click="scanQrCode">扫码录入设备</van-button>
          <span>设备数量：{{ list.length }}</span>
        </div>

        <div class="device-list-box">
          <van-cell-group inset>
            <van-cell
              v-for="(item, index) in list"
              :key="item.address"
              :title="`序号：${index + 1}`"
              :value="item.address"
            ></van-cell>
          </van-cell-group>
        </div>

        <div class="button-group-box">
          <van-grid clickable>
            <van-grid-item text="开始" icon="play-circle-o" @click="start" />
            <van-grid-item text="暂停" icon="pause-circle-o" @click="pause" />
            <van-grid-item text="重置计时" icon="replay" @click="reset" />
          </van-grid>
          <van-count-down
            ref="countDown"
            millisecond
            :time="20000"
            :auto-start="false"
            format="ss:SSS"
            @finish="onFinish"
          />
        </div>

        <div class="scan-result-box">
          <van-collapse v-model="activeNames" accordion>
            <van-collapse-item
              title="测试成功设备"
              name="1"
              :value="`数量：${statistics.success.count}`"
            >
              <div class="collapse-content">
                <div
                  v-for="item in statistics.success.data"
                  :key="item.address"
                >
                  <div class="item success">
                    <div>{{ item.address }}</div>
                    <div>{{ item.rssi }}</div>
                  </div>
                </div>
              </div>
            </van-collapse-item>
            <van-collapse-item
              title="信号强度不足"
              name="2"
              :value="`数量：${statistics.insufficientStrength.count}`"
            >
              <div class="collapse-content">
                <div
                  v-for="item in statistics.insufficientStrength.data"
                  :key="item.address"
                >
                  <div class="item">
                    <div>{{ item.address }}</div>
                    <div class="warning">{{ item.rssi }}</div>
                  </div>
                </div>
              </div>
            </van-collapse-item>
            <van-collapse-item
              title="未扫描到设备"
              name="3"
              :value="`数量：${statistics.notScanned.count}`"
            >
              <div class="collapse-content">
                <div
                  v-for="item in statistics.notScanned.data"
                  :key="item.address"
                >
                  <div class="item error">
                    <div>{{ item.address }}</div>
                    <div>{{ item.rssi }}</div>
                  </div>
                </div>
              </div>
            </van-collapse-item>
          </van-collapse>
        </div></van-tab
      >
      <van-tab title="批量关机工具">
        <batch-shutdown-index :isChildren="true"></batch-shutdown-index>
      </van-tab>
    </van-tabs>

    <!-- <div class="scan-explain"></div> -->
  </div>
</template>

<script>
import navBar from "@/components/navigation/navBar.vue";
import batchShutdownIndex from "./batchShutdownIndex.vue";
import { CountDown, Notify, Collapse, CollapseItem, Tab, Tabs } from "vant";
import BatchShutdownIndex from "./batchShutdownIndex.vue";
export default {
  components: {
    [CountDown.name]: CountDown,
    [Collapse.name]: Collapse,
    [CollapseItem.name]: CollapseItem,
    [Tab.name]: Tab,
    [Tabs.name]: Tabs,
    navBar: navBar,
    BatchShutdownIndex,
  },
  data() {
    return {
      active: 0,
      list: [],
      // 扫描二维码的map
      scanQrDeviceMap: null,
      activeNames: "",
      scanList: [],
      scanResultMap: null,
      statistics: {
        success: {
          count: 0,
          data: [],
        },
        notScanned: {
          count: 0,
          data: [],
        },
        insufficientStrength: {
          count: 0,
          data: [],
        },
      },
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
          this.handleBatchScanResult(e.data.data);
          break;
        case "SCAN_DEVICE_LIST_RESULT":
          console.log(JSON.stringify(e.data.data));
          this.handleScanBleDevice(e.data.data);
          break;
        default:
          break;
      }
    },

    scanQrCode() {
      this.list = [];
      setTimeout(() => {
        this.$androidApi.scanQrCode();
      }, 50);
    },

    handleBatchScanResult(data) {
      if (!data) {
        Notify({ type: "danger", message: "二维码内容错误" });
        return;
      }
      data = data.trim().replace(/:/g, "");
      this.scanQrDeviceMap = new Map();
      if (this.macReg(data)) {
        this.scanQrDeviceMap.set(data.toUpperCase(), data);
      } else {
        let scanDataSplit = data.split("-");
        let startMac, endMac;
        if (scanDataSplit.length == 2) {
          startMac = parseInt(scanDataSplit[0], 16);
          endMac = parseInt(scanDataSplit[1], 16);
          for (let i = 0; i <= endMac - startMac; i++) {
            this.scanQrDeviceMap.set(
              (startMac + i).toString(16).toUpperCase(),
              startMac + i
            );
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
            this.scanQrDeviceMap.set(
              (startMac + i).toString(16).toUpperCase(),
              startMac + i
            );
          }
        }
      }

      this.list = [];

      if (this.scanQrDeviceMap.size > 0) {
        this.scanQrDeviceMap.forEach((v, k) => {
          let address = k.replace(/(.{2})(?!$)/g, "$1:");
          let data = {
            address: address,
          };
          this.list.push(data);
        });

        this.scanQrDeviceMap = new Map();
        this.list.forEach((e) =>
          this.scanQrDeviceMap.set(e.address, e.address)
        );
      }
    },

    handleScanBleDevice(data) {
      if (!data || data.length == 0) {
        return;
      }

      for (let i = 0; i < data.length; i++) {
        const device = data[i];
        let address = device.address;
        if (!this.scanQrDeviceMap.has(address)) {
          continue;
        }
        if (this.scanResultMap.has(address)) {
          let rssi = this.scanResultMap.get(address);
          if (!rssi || device.rssi > rssi) {
            console.log("扫描到信号更强的设备了：address" + address);
            console.log("信号值变化：" + rssi + "=>" + device.rssi);
            this.scanResultMap.set(address, device.rssi);
          }
        } else {
          console.log("扫描到了设备：address" + address);
          this.scanResultMap.set(address, device.rssi);
        }
      }
    },

    start() {
      if (!this.list || this.list.length == 0) {
        Notify({ type: "danger", message: "请先扫码录入设备" });
        return;
      }

      this.scanResultMap = new Map();

      this.$androidApi
        .startBleRanging()
        .then(() => {
          this.initStatistics();
          this.$refs.countDown.start();
          this.scanResultMap = new Map();
        })
        .catch((errorMsg) => {
          Notify({ type: "danger", message: errorMsg });
        });
    },
    pause() {
      this.$androidApi.stopBleRanging();
      this.$refs.countDown.pause();
    },
    reset() {
      this.$androidApi.stopBleRanging();
      this.$refs.countDown.reset();
      this.statisticsResult();
      this.scanResultMap = new Map();
    },
    /**
     * 倒计时结束
     */
    onFinish() {
      Notify({ type: "success", message: "扫描结束" });
      this.reset();
    },

    /**
     * 统计扫描结果
     */
    statisticsResult() {
      if (this.scanQrDeviceMap) {
        this.scanQrDeviceMap.forEach((v, k) => {
          // 扫描到了
          if (this.scanResultMap.has(k)) {
            let rssi = this.scanResultMap.get(k);
            if (rssi && rssi >= -52) {
              this.statistics.success.count++;
              this.statistics.success.data.push({ address: k, rssi: rssi });
            } else {
              this.statistics.insufficientStrength.count++;
              this.statistics.insufficientStrength.data.push({
                address: k,
                rssi: rssi,
              });
            }
          } else {
            this.statistics.notScanned.count++;
            this.statistics.notScanned.data.push({ address: k, rssi: 0 });
          }
        });
      }
    },

    initStatistics() {
      this.statistics = {
        success: {
          count: 0,
          data: [],
        },
        notScanned: {
          count: 0,
          data: [],
        },
        insufficientStrength: {
          count: 0,
          data: [],
        },
      };
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
.content {
  overflow: auto;
  padding: 0.15rem 0.15rem !important;
}
.head {
  margin:0.16rem  0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  .van-button {
    height: 0.68rem;
    width: 2.5rem;
    background: #007fff;
    font-size: 0.25rem;
    font-family: PingFang SC-Regular, PingFang SC;
    font-weight: 400;
    color: #ffffff;
    line-height: 0.29rem;
  }
}

.device-list-box {
  max-height: 23vh;
  .van-cell {
    height: 0.7rem;
    display: flex;
    align-items: center;
  }

  .van-cell__value {
    color: #323233;
  }
  overflow: auto;
}

.button-group-box {
  .van-grid-item {
    margin-top: 0.16rem;
    flex-basis: 33.3% !important;
    height: 1.1rem;
  }
  .van-count-down {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 0.6rem;
    font-size: 0.3rem;
    font-weight: bold;
  }
}
.scan-result-box {
  max-height: 44vh;
  background: #ffffff;
  border-radius: 0.1rem;
  .collapse-content {
    max-height: 3.5rem;
    overflow: auto;
    .item {
      height: 0.6rem;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 0.2rem;
      color: #323233;
    }
    .success {
      color: green;
    }
    .warning {
      color: orange;
    }

    .error {
      color: red;
    }
  }
}

</style>
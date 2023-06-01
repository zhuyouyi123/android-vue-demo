<!-- 批量关机 -->
<template>
  <div class="content">
    <nav-bar > </nav-bar>

    <div>
      <van-cell-group inset>
        <van-cell title="总设备数（个）" :value="list.length"></van-cell>
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
            @click="shutdown"
          >
          </van-image>
        </div>
      </div>

      <van-cell-group inset class="list-box">
        <van-cell v-if="list.length == 0" title="请先扫码录入设备"></van-cell>

        <van-cell
          v-show="list.length > 0"
          v-for="(item, index) in list"
          clickable
          :key="item.address"
          :title="item.address"
          :value="item.state"
        >
        </van-cell>
      </van-cell-group>

      <van-overlay :show="overlayShow" />
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
  data() {
    return {
      // 搜索条件
      searchValue: "",
      // 获取到焦点时候的值
      searchFocusValue: "",
      list: [],
      initParams: {
        sortType: "",
        address: "",
        rssiValue: 58,
        deviceType: "此品牌设备",
      },
      checked: [],
      checkboxRefs: [],
      scanState: true,

      interval: null,

      overlayShow: false,
    };
  },
  mounted() {
    window.addEventListener("commonAndroidEvent", this.callJs);
  },

  destroyed() {
    window.removeEventListener("commonAndroidEvent", this.callJs);
  },
  methods: {
    callJs(e) {
      switch (e.data.eventName) {
        case "SCAN_RESULT":
          // 扫码
          this.handleBatchScanResult(e.data.data);
          break;
        default:
          break;
      }
    },

    queryFilterInfo() {
      return new Promise((resolve, reject) => {
        this.$androidApi.queryFilterInfo().then((data) => {
          console.log(JSON.stringify(data));
          resolve(data);
        });
      });
    },

    init(data) {
      if (data) {
        this.searchValue = data.filterAddress;
        this.initParams.address = data.filterAddress;
        this.initParams.rssiValue = -data.rssi;
      }

      this.$androidApi
        .init({
          sortType: "rssi_fall",
          address: this.initParams.address,
          rssiValue: -this.initParams.rssiValue,
          allDevice: false,
        })
        .then(() => {
          this.startScan();
        });
    },

    scanQrCode() {
      this.$androidApi.scanQrCode();
      // this.handleBatchScanResult("19:18:FC:09:DA:EC_19:18:FC:09:DA:FD");
    },

    handleBatchScanResult(data) {
      if (!data) {
        return;
      }
      data = data.replace(/:/g, "");
      let scanDataSplit = data.split("_");
      let map = new Map();
      // 19:18:FC:09:DA:EC_19:18:FC:09:DA:ED
      let startMac, endMac;
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
      }

      for (let i = 0; i <= endMac - startMac; i++) {
        map.set((startMac + i).toString(16).toUpperCase(), startMac + i);
      }

      this.list = [];

      if (map.size > 0) {
        map.forEach((k, v) => {
          let data = {
            address: k.toString(16).toUpperCase(),
            state: "等待关机",
          };
          this.list.push(data);
        });
      }
    },

    shutdown() {
      if (this.list.length == 0) {
        Notify({ type: "danger", message: "请先扫码录入设备" });
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
          this.overlayShow = true;
        })
        .catch(() => {});
    },

    toggle(index) {
      console.log(this.checked);
      console.log(this.checked.length);
    },

    startScan() {
      this.$androidApi.startScan();
      this.scanState = true;
      setTimeout(() => {
        this.getList();
      }, 200);
    },

    getList() {
      if (this.interval) {
        clearInterval(this.interval);
        this.interval = null;
      }

      this.interval = setInterval(() => {
        this.$androidApi.deviceList().then((data) => {
          this.list = data.list;
          this.count = this.list.length;
          if (data.scanning != this.scanState) {
            this.scanState = data.scanning;
          }
          if (!this.scanState || false == this.scanState) {
            clearInterval(this.interval);
            return;
          }
        });
      }, 500);
    },

    stopScan() {
      this.$androidApi.stopScan();
      this.scanState = false;
    },

    submit() {
      console.log(this.checked);
      console.log(this.checked.length);
    },
    // 搜索框获取到焦点
    searchFocus() {
      if (this.searchValue) {
        this.searchFocusValue = this.searchValue;
      }
    },
    // 搜索框丢失焦点
    searchBlur() {
      if (this.searchFocusValue != this.searchValue) {
        this.initParams.address = this.searchValue;
        this.init();
      }
      this.searchFocusValue = "";
    },
  },
};
</script>
<style lang='scss' scoped>
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

.list-box {
  height: 76vh;
  overflow: auto;
}
</style>
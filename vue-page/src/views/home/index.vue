<!--  -->
<template>
  <div>
    <nav-bar>
      <van-icon name="arrow-left" slot="left" class="navIcon" />
    </nav-bar>

    <div class="content">
      <div class="condition-box">
        <div class="condition">
          <div>
            <el-dropdown trigger="click" @command="handleSort">
              <span class="el-dropdown-link">
                <van-icon name="descending" size="23" />
                {{ i18nInfo.button.sorted }}
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="rssi_rise">
                  {{ i18nInfo.rssiRise }}
                </el-dropdown-item>
                <el-dropdown-item command="rssi_fall">
                  {{ i18nInfo.rssiFall }}
                </el-dropdown-item>
                <el-dropdown-item command="mac_rise">
                  {{ i18nInfo.macRise }}</el-dropdown-item
                >
                <el-dropdown-item command="mac_fall">{{
                  i18nInfo.macFall
                }}</el-dropdown-item>
                <el-dropdown-item command="battery_rise">
                  {{ i18nInfo.batteryRise }}
                </el-dropdown-item>
                <el-dropdown-item command="battery_fall" class="unborder">
                  {{ i18nInfo.batteryFall }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>

          <div>
            <van-icon name="filter-o" size="19" /> {{ i18nInfo.button.filter }}
          </div>
          <div>
            <van-icon name="todo-list-o" size="20" />
            {{ i18nInfo.button.language }}
          </div>
          <div>
            <van-icon name="bars" size="20" /> {{ i18nInfo.button.batch }}
          </div>
        </div>
        <div class="division-line"></div>
        <div class="count">
          {{ i18nInfo.scannedCount }}{{ count }}{{ i18nInfo.individua }}
        </div>
      </div>

      <van-search
        v-model="searchValue"
        :placeholder="$t('notifyMessage.searchPlaceholder')"
      >
        <template #right-icon>
          <van-icon name="scan" size="23" color="black" @click="scanQrCode" />
        </template>
      </van-search>

      <div class="list-box">
        <div class="item" v-for="item in list" :key="item.address">
          <div class="device-info" @click="detail(item)">
            <div class="name">
              <b>{{ item.name ? item.name : "Unnamed" }}</b>
              <div class="info">
                <div class="info-box">
                  <div>Mac: {{ item.address }}</div>
                  <!-- <div>电量: 100%</div> -->
                  <div>电量: {{ item.battery }}%</div>
                </div>
                <div class="info-box">
                  <div>Rssi: {{ item.rssi }}</div>
                  <!-- <div>广播间隔:330ms</div> -->
                  <div>广播间隔: {{ item.broadcastInterval }}ms</div>
                </div>
              </div>
            </div>
          </div>
          <div class="division-line"></div>
          <div class="other-info">
            <div class="thoroughfares-null" v-show="!item.thoroughfares">
              {{ $t("notifyMessage.base.dataEmpty") }}
            </div>

            <div class="thoroughfares-info">
              <div v-for="thoroughfare in item.thoroughfares">
                <div v-show="thoroughfare.type == 'I_BEACON'">
                  <div class="name">iBeacon</div>
                  <div class="info-box">
                    <div class="uuid">
                      UUID:1918FC80B1113441A9ACB1001C2FE510
                    </div>
                    <div class="beacon-info">
                      <div class="major">Major:{{ thoroughfare.major }}</div>
                      <div class="minor">Minor:{{ thoroughfare.minor }}</div>
                      <div class="distance">
                        校准距离:{{ thoroughfare.calibrationDistance }}dBm
                      </div>
                    </div>
                  </div>
                </div>

                <div v-show="thoroughfare.type == 'CORE_IOT_AOA'"></div>

                <div v-show="thoroughfare.type == 'QUUPPA_AOA'"></div>
                <div v-show="thoroughfare.type == 'EDDYSTONE_UID'"></div>
                <div v-show="thoroughfare.type == 'EDDYSTONE_URL'"></div>
                <div v-show="thoroughfare.type == 'EDDYSTONE_TLM'"></div>
                <div v-show="thoroughfare.type == 'LINE'"></div>
                <div v-show="thoroughfare.type == 'ACC'"></div>
                <div v-show="thoroughfare.type == 'INFO'"></div>
              </div>
            </div>

            <!-- 
            <div class="name">ACC</div>
            <div class="info-box">
              <div class="beacon-info">
                <div class="major">X-Axis:{{ item.acc.xAxis }}gee</div>
                <div class="minor">Y-Axis:{{ item.acc.yAxis }}gee</div>
                <div class="distance">Z-Axis:{{ item.acc.yAxis }}gee</div>
              </div>
            </div> -->
          </div>
        </div>
      </div>

      <!-- 加载 -->
      <div class="scanning" v-if="!scanState" @click="startScan">
        <div class="scan">刷新</div>
      </div>
      <div class="scanning scanning-animation" v-else>
        <div class="loading">
          <div class="dot dot-1"></div>
          <div class="dot dot-2"></div>
          <div class="dot dot-3"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import navBar from "@/components/navigation/navBar.vue";

import { Search, DropdownMenu, DropdownItem, Notify } from "vant";
export default {
  data() {
    return {
      // 搜索条件
      searchValue: "",
      // 扫描数量
      count: 0,

      list: [],
      interval: null,
      // 扫描状态
      scanState: true,
      // 国际化参数
      i18nInfo: this.$i18n.t("home"),
    };
  },

  components: {
    [DropdownMenu.name]: DropdownMenu,
    [DropdownItem.name]: DropdownItem,
    [Search.name]: Search,
    navBar: navBar,
  },

  mounted() {
    this.init();
  },

  destroyed() {
    this.stopScan();
    if (this.interval) {
      clearInterval(this.interval);
    }
  },

  methods: {
    scanQrCode() {
      this.$androidApi.scanQrCode();
    },

    init() {
      this.$androidApi.init().then(() => {
        this.startScan();
      });
    },

    /**
     * 处理排序
     */
    handleSort(command) {
      let params = {
        sortType: command,
      };
      this.$androidApi.init(params).then(() => {
        this.startScan();
      });
    },

    startScan() {
      this.$androidApi.startScan();
      this.scanState = true;
      setTimeout(() => {
        this.getList();
      }, 200);
    },

    /**
     * 停止扫描
     */
    stopScan() {
      this.$androidApi.stopScan();
    },

    getList() {
      if (this.interval) {
        clearInterval(this.interval);
        this.interval = null;
      }
      // 开启扫描

      this.interval = setInterval(() => {
        this.$androidApi.deviceList().then((data) => {
          if (this.$config.developmentMode) {
            this.list = data;
            this.count = this.list.length;
          } else if (data) {
            if (data.list) {
              this.list = data.list;
              this.count = this.list.length;
            }
            this.updateScanState(data.scanning);
            if (!this.scanState || false == this.scanState) {
              clearInterval(this.interval);
              return;
            }
          }
        });
      }, 2000);
    },

    /**
     * 更新扫描状态
     * @param {扫描状态} state
     */
    updateScanState(state) {
      if (state != this.scanState) {
        this.scanState = state;
      }
    },

    detail(item) {
      this.$router.push({
        path: "/home/deviceDetail",
        query: {
          address: item.address,
        },
      });
    },
  },
};
</script>
<style lang='scss' scoped>
.content {
  .condition-box {
    width: 7.02rem;
    height: 1.8rem;
    background: #ffffff;
    box-shadow: 0rem 0.04rem 0.06rem 0.01rem rgba(0, 0, 0, 0.1);
    border-radius: 0.1rem 0.1rem 0.1rem 0.1rem;
    .condition {
      font-size: 0.3rem;
      font-family: Source Han Sans CN-Regular, Source Han Sans CN;
      font-weight: 400;
      color: #333333;
      height: 0.9rem;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding-left: 0.27rem;
      padding-right: 0.27rem;
      div {
        display: flex;
        align-items: center;
        height: 0.3rem;
      }
      .el-dropdown {
        display: flex;
        align-items: center;
      }
      .el-dropdown-link {
        font-size: 0.3rem;
        font-family: Source Han Sans CN-Regular, Source Han Sans CN;
        color: #333333;
        display: flex;
        align-items: center;
      }
    }
    .count {
      width: 3.81rem;
      height: 0.8rem;
      font-size: 0.32rem;
      font-family: Source Han Sans CN-Regular, Source Han Sans CN;
      font-weight: 400;
      color: #000000;
      display: flex;
      align-items: center;
      padding-left: 0.26rem;
    }
  }

  // 搜索

  .van-search {
    width: 7.02rem;
    height: 0.72rem;
    box-shadow: 0rem 0.04rem 0.06rem 0.01rem rgba(0, 0, 0, 0.1);
    border-radius: 0.1rem 0.1rem 0.1rem 0.1rem;
    margin-top: 0.2rem;
    .van-search__content {
      padding-left: 0;
    }
  }

  .van-search {
    .van-field {
      background: #ffffff;
    }
  }

  // 列表
  .list-box {
    height: 74vh;
    width: 7rem;
    overflow: auto;
    margin-top: 0.16rem;
    .item {
      &:nth-child(n + 2) {
        margin-top: 0.16rem;
      }
      width: 6.98rem;
      // height: 4.36rem;
      background: #ffffff;
      box-shadow: 0rem 0.04rem 0.06rem 0.01rem rgba(0, 0, 0, 0.1);
      border-radius: 0.1rem 0.1rem 0.1rem 0.1rem;
      .device-info {
        padding: 0.2rem 0.27rem;
        height: 1.32rem;
        .name {
          font-size: 0.32rem;
          font-family: Source Han Sans CN-Bold, Source Han Sans CN;
          font-weight: bold;
          color: #000000;
          .info {
            font-size: 0.28rem;
            font-family: Source Han Sans CN-Regular, Source Han Sans CN;
            font-weight: 400;
            color: #000000;
            line-height: 0.29rem;
            .info-box {
              display: flex;
              align-items: center;
              justify-content: space-between;
              margin-top: 0.16rem;
            }
          }
        }
      }
      // 其他信息
      .other-info {
        padding: 0.2rem 0.27rem;
        font-weight: 400;
        color: #000000;
        line-height: 0.45rem;
        font-family: Source Han Sans CN-Regular, Source Han Sans CN;
        .thoroughfares-null {
          height: 1rem;
          color: #666666;
          display: flex;
          align-items: center;
          justify-content: center;
        }
        .name {
          font-size: 0.32rem;
          font-family: Source Han Sans CN-Regular, Source Han Sans CN;
          font-weight: 400;
          color: #000000;
          &:nth-child(n + 2) {
            margin-top: 0.08rem;
          }
        }
        .info-box {
          font-size: 0.28rem;
          .beacon-info {
            display: flex;
            justify-content: space-between;
          }
        }
      }
    }
  }

  .scanning-animation {
    animation: rotate-move 2s ease-in-out infinite;
    .loading {
      position: absolute;
      bottom: 0.58rem;
      right: 0.65rem;
    }

    .dot {
      width: 0.2rem;
      height: 0.2rem;
      border-radius: 50%;
      position: absolute;
      top: 0;
      bottom: 0;
      left: 0;
      right: 0;
      margin: auto;
    }
    .dot-3 {
      background-color: #f74d75;
      animation: dot-3-move 2s ease infinite;
    }

    .dot-2 {
      background-color: #10beae;
      animation: dot-2-move 2s ease infinite;
    }

    .dot-1 {
      background-color: blue;
      animation: dot-1-move 2s ease infinite;
    }

    @keyframes dot-3-move {
      20% {
        transform: scale(1);
      }

      25% {
        filter: blur(2px);
      }

      45% {
        filter: none;
        transform: translateY(-0.1rem) scale(0.65);
      }

      60% {
        transform: translateY(-0.25rem) scale(0.65);
      }

      80% {
        transform: translateY(-0.25rem) scale(0.65);
      }

      100% {
        transform: translateY(0px) scale(1);
      }
    }

    @keyframes dot-2-move {
      20% {
        transform: scale(1);
      }

      25% {
        filter: blur(2px);
      }
      45% {
        filter: none;
        transform: translate(-0.1rem, 0.1rem) scale(0.65);
      }

      60% {
        transform: translate(-0.25rem, 0.25rem) scale(0.65);
      }

      80% {
        transform: translate(-0.25rem, 0.25rem) scale(0.65);
      }

      100% {
        transform: translateY(0px) scale(1);
      }
    }

    @keyframes dot-1-move {
      20% {
        transform: scale(1);
      }
      25% {
        filter: blur(2px);
      }

      45% {
        filter: none;
        transform: translate(0.1rem, 0.1rem) scale(0.65);
      }

      60% {
        transform: translate(0.25rem, 0.25rem) scale(0.65);
      }

      80% {
        transform: translate(0.25rem, 0.25rem) scale(0.65);
      }

      100% {
        transform: translateY(0px) scale(1);
      }
    }
    @keyframes rotate-move {
      55% {
        transform: rotate(0deg);
      }

      80% {
        transform: rotate(360deg);
      }

      100% {
        transform: rotate(360deg);
      }
    }
  }
  .scanning {
    position: fixed;
    border-radius: 50%;
    background: var(--theme-color);

    opacity: 0.9;
    width: 1.1rem;
    height: 1.1rem;
    bottom: 0.8rem;
    right: 0.5rem;
    display: flex;
    align-items: center;
    justify-content: center;

    .scan {
      color: #ffffff;
    }
  }
}
</style>
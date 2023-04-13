<!--  -->
<template>
  <div>
    <nav-bar>
      <van-icon name="arrow-left" slot="left" class="navIcon" />
    </nav-bar>

    <div class="content">
      <div class="condition-box">
        <div class="condition">
          <!-- <div><van-icon name="descending" /> 排序</div> -->
          <div>
            <el-dropdown trigger="click" @command="handleSort">
              <span class="el-dropdown-link">
                <van-icon name="descending" size="23" />
                排序
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="rssi_rise"
                  >Rssi升序</el-dropdown-item
                >
                <el-dropdown-item command="rssi_fall"
                  >Rssi降序</el-dropdown-item
                >
                <el-dropdown-item command="mac_rise">Mac降序</el-dropdown-item>
                <el-dropdown-item command="mac_fall">Mac升序</el-dropdown-item>
                <el-dropdown-item command="battery_rise"
                  >电量降序</el-dropdown-item
                >
                <el-dropdown-item command="battery_fall" class="unborder"
                  >电量升序</el-dropdown-item
                >
              </el-dropdown-menu>
            </el-dropdown>
          </div>

          <!-- <div>
            <van-icon name="descending" />
            <van-dropdown-menu active-color="#007FFF" :overlay="false">
              <van-dropdown-item v-model="sortCode" :options="sortOptions" />
            </van-dropdown-menu>
          </div> -->
          <div><van-icon name="filter-o" size="19" />过滤</div>
          <div><van-icon name="todo-list-o" size="20" />语言</div>
          <div><van-icon name="bars" size="20" />批量</div>
        </div>
        <div class="division-line"></div>
        <div class="count">扫描到设备数量：{{ count }}个</div>
      </div>

      <van-search v-model="searchValue" placeholder="请输入搜索关键词">
        <template #right-icon>
          <van-icon name="scan" size="23" color="black" />
        </template>
      </van-search>

      <div class="list-box">
        <div class="item" v-for="item in list" :key="item.address">
          <div class="device-info" @click="detail(item)">
            <div class="name">
              <b>{{ item.name ? item.name : "Unnamed" }}</b>
            </div>
            <div class="info">
              <div class="info-box">
                <div>Mac: {{ item.address }}</div>
                <div>电量: 100%</div>
                <!-- <div>电量: {{ item.battery }}%</div> -->
              </div>
              <div class="info-box">
                <div>Rssi: {{ item.rssi }}</div>
                <div>广播间隔:330ms</div>
                <!-- <div>广播间隔: {{ item.broadcastInterval }}ms</div> -->
              </div>
            </div>
          </div>
          <div class="division-line"></div>
          <div class="other-info">
            <div class="name">iBeacon</div>
            <div class="info-box">
              <div class="uuid">UUID:1918FC80B1113441A9ACB1001C2FE510</div>
              <div class="beacon-info">
                <div class="major">Major:111</div>
                <div class="minor">Minor:222</div>
                <div class="distance">校准距离:22dBm</div>
                <!-- <div class="major">Major:{{ item.beacon.major }}</div>
                <div class="minor">Minor:{{ item.beacon.minor }}</div>
                <div class="distance">
                  校准距离:{{ item.beacon.calibrationDistance }}dBm
                </div> -->
              </div>
            </div>
            <div class="name">ACC</div>
            <div class="info-box">
              <div class="beacon-info">
                <div class="major">X-Axis:1.1gee</div>
                <div class="minor">Y-Axis:1.2gee</div>
                <div class="distance">Z-Axis:1.3gee</div>
                <!-- <div class="major">X-Axis:{{ item.acc.xAxis }}gee</div>
                <div class="minor">Y-Axis:{{ item.acc.yAxis }}gee</div>
                <div class="distance">Z-Axis:{{ item.acc.yAxis }}gee</div> -->
              </div>
            </div>

            <div class="name">ACC</div>
            <div class="info-box">
              <div class="beacon-info">
                <div class="major">X-Axis:1.1gee</div>
                <div class="minor">Y-Axis:1.2gee</div>
                <div class="distance">Z-Axis:1.3gee</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 加载 -->
      <div class="scanning">
        <div class="scan" v-if="!scanState" @click="startScan">刷新</div>
        <div v-else>
          <div class="point-item"></div>
          <div class="point-item"></div>
          <div class="point-item"></div>
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
      scanState: false,
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

  methods: {
    init() {
      this.$androidApi.init().then(() => {
        this.startScan();
      });
    },

    /**
     * 处理排序
     */
    handleSort(command) {
      this.$message("click on item " + command);
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
          if (data) {
            this.updateScanState(data.scanning);
            if (!data.scanning || false == data.scanning) {
              clearInterval(this.interval);
              return;
            }

            if (data.list) {
              this.list = data.list;
              this.count = this.list.length;
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
      this.$router.push("/home/deviceDetail");
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
    .van-cell {
      background: #ffffff;
    }
    .van-search__content {
      padding-left: 0;
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
        }
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
      // 其他信息
      .other-info {
        padding: 0.2rem 0.27rem;
        font-weight: 400;
        color: #000000;
        line-height: 0.45rem;
        font-family: Source Han Sans CN-Regular, Source Han Sans CN;
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
  .scanning {
    position: fixed;
    bottom: 0.8rem;
    right: 0.5rem;
    width: 1.1rem;
    height: 1.1rem;
    border-radius: 50%;
    display: flex;
    justify-content: space-evenly;
    align-items: center;
    background: #2748aa;
    .scan {
      color: #ffffff;
    }
    .point-item {
      width: 0.26rem;
      height: 0.26rem;
      border-radius: 0.1rem;
      &:nth-child(1) {
        background: #e8f07b;
        animation: twinkle 2s -1s linear infinite;
      }
      &:nth-child(2) {
        background: #26c126;
        animation: twinkle 2s -0.5s linear infinite;
      }
      &:nth-child(3) {
        background: #4ee1e1;
        animation: twinkle 2s linear infinite;
      }
    }
  }
}

@keyframes twinkle {
  0%,
  70%,
  100% {
    transform: scale(0);
    opacity: 0.2;
  }

  40% {
    transform: scale(1);
    opacity: 1;
  }
}
</style>
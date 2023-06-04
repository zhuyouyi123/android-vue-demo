<!--  -->
<template>
  <div>
    <nav-bar>
      <van-icon
        name="setting-o"
        slot="left"
        class="navIcon"
        @click="sdkConfig"
      />

      <van-icon
        :name="require('../../assets/image/batch/batch-record.svg')"
        slot="right"
        @click="openBatchConfigRecordPopup"
        v-show="batchModel"
      />
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
                <el-dropdown-item
                  :class="{
                    'filtering-selected': initParams.sortType == 'rssi_rise',
                  }"
                  command="rssi_rise"
                >
                  {{ i18nInfo.sort.rssiRise }}
                </el-dropdown-item>
                <el-dropdown-item
                  command="rssi_fall"
                  :class="{
                    'filtering-selected': initParams.sortType == 'rssi_fall',
                  }"
                >
                  {{ i18nInfo.sort.rssiFall }}
                </el-dropdown-item>
                <el-dropdown-item
                  command="mac_rise"
                  :class="{
                    'filtering-selected': initParams.sortType == 'mac_rise',
                  }"
                >
                  {{ i18nInfo.sort.macRise }}</el-dropdown-item
                >
                <el-dropdown-item
                  command="mac_fall"
                  :class="{
                    'filtering-selected': initParams.sortType == 'mac_fall',
                  }"
                  >{{ i18nInfo.sort.macFall }}</el-dropdown-item
                >
                <el-dropdown-item
                  command="battery_rise"
                  :class="{
                    'filtering-selected': initParams.sortType == 'battery_rise',
                  }"
                >
                  {{ i18nInfo.sort.batteryRise }}
                </el-dropdown-item>
                <el-dropdown-item
                  command="battery_fall"
                  class="unborder"
                  :class="{
                    'filtering-selected': initParams.sortType == 'battery_fall',
                  }"
                >
                  {{ i18nInfo.sort.batteryFall }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>

          <div @click="openDeviceFilterDialog">
            <van-icon name="filter-o" size="19" />
            {{ i18nInfo.button.filter }}
          </div>
          <div @click="openLanguageSettingDialog">
            <van-icon name="todo-list-o" size="20" />
            {{ i18nInfo.button.language }}
          </div>
          <div :class="{ 'blue-icon': batchModel }" @click.stop="setBatchModel">
            <!-- 批量模式 -->
            <van-icon name="bars" size="20" />
            {{ i18nInfo.button.batch }}
          </div>
        </div>
        <div class="division-line"></div>
        <div class="count">
          <div class="scan-info">
            {{ i18nInfo.scannedCount }}{{ count }}{{ i18nInfo.individua }}
          </div>
          <div
            class="select-all-box"
            v-show="batchModel"
            @click.stop="batchSelectAllBeacon()"
          >
            <div class="lable">{{ i18nInfo.button.selectAll }}</div>
            <van-image
              v-if="batchSelectAllCheck"
              :src="require('@/assets/image/home/multiple-choice-selected.svg')"
            />

            <van-image
              v-else-if="!batchSelectAllCheck"
              :src="require('@/assets/image/home/multiple-choice-unselect.svg')"
            />
          </div>
        </div>
      </div>

      <!-- 搜索框 -->
      <van-search
        v-model.trim="searchValue"
        :disabled="batchModel"
        @focus="searchFocus"
        @blur="searchBlur"
        :placeholder="$t('notifyMessage.searchPlaceholder')"
      >
        <template #right-icon>
          <van-icon name="scan" size="23" color="black" @click="scanQrCode" />
        </template>
      </van-search>

      <div class="list-box">
        <div class="item" v-for="(item, index) in list" :key="item.address">
          <div class="device-info" @click="detail(item)">
            <div class="base-info-box">
              <div class="name">
                <div>
                  <b>
                    {{
                      item.standardThoroughfareInfo &&
                      item.standardThoroughfareInfo.deviceName
                        ? item.standardThoroughfareInfo.deviceName
                        : "Unnamed"
                    }}
                  </b>
                </div>
                <!-- <el-checkbox :key="item.selected" /> -->
                <van-image
                  v-if="item.selected && batchModel"
                  @click.stop="batchSelectBeacon(index)"
                  :src="
                    require('@/assets/image/home/multiple-choice-selected.svg')
                  "
                />

                <van-image
                  v-else-if="!item.selected && batchModel"
                  @click.stop="batchSelectBeacon(index)"
                  :src="
                    require('@/assets/image/home/multiple-choice-unselect.svg')
                  "
                />
              </div>

              <div class="info">
                <div class="info-box">
                  <div>Mac: {{ item.address }}</div>
                  <!-- <div>电量: 100%</div> -->
                  <div>
                    Battery:
                    {{
                      item.standardThoroughfareInfo &&
                      item.standardThoroughfareInfo.battery > 0
                        ? item.standardThoroughfareInfo.battery + "%"
                        : "-"
                    }}
                  </div>
                </div>
                <div class="info-box">
                  <div>Rssi: {{ item.rssi }}</div>
                  <div v-show="item.broadcastInterval > 0">
                    {{ i18nInfo.broadcastInterval }}:
                    {{ item.broadcastInterval }}ms
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="division-line"></div>
          <div class="other-info">
            <div
              class="thoroughfares-null"
              v-show="!item.standardThoroughfareInfo"
            >
              {{ $t("notifyMessage.base.dataEmpty") }}
            </div>

            <div
              class="thoroughfares-info"
              v-show="item.standardThoroughfareInfo"
            >
              <div class="thoroughfare">
                <div v-for="beacon in item.standardThoroughfareInfo.beacons">
                  <div class="beacon" v-show="beacon">
                    <div class="name">
                      {{ beacon.type }}
                    </div>
                    <div class="info-box">
                      <div class="uuid">UUID：{{ beacon.uuid }}</div>
                      <div class="detail-info">
                        <div class="major">Major：{{ beacon.major }}</div>
                        <div class="minor">Minor：{{ beacon.minor }}</div>
                        <div class="distance">
                          Rssi @ 1m：
                          {{ beacon.measurePower }}dBm
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- UID -->
                <div v-for="uid in item.standardThoroughfareInfo.uids">
                  <div class="name">
                    {{ uid.type }}
                  </div>
                  <div class="info-box">
                    <div class="uuid">NameSpace ID：{{ uid.namespaceId }}</div>
                    <div class="detail-info">
                      <div>Instance ID：{{ uid.instanceId }}</div>
                      <div class="distance">
                        Rssi @ 1m：
                        {{ uid.measurePower }}dBm
                      </div>
                    </div>
                  </div>
                </div>
                <!-- URL -->
                <div v-for="url in item.standardThoroughfareInfo.urls">
                  <div class="name">{{ url.type }}</div>
                  <div class="info-box">
                    <div>
                      link：
                      <span style="color: blue" @click="toLink(url.link)">{{
                        url.link
                      }}</span>
                    </div>
                    <div class="detail-info">
                      Rssi @ 1m：
                      {{ url.measurePower }}dBm
                    </div>
                  </div>
                </div>
                <!-- TLM -->
                <div v-if="item.standardThoroughfareInfo.tlm">
                  <div class="name">
                    {{ item.standardThoroughfareInfo.tlm.type }}
                  </div>
                  <div class="info-box">
                    <div>
                      time working：{{
                        item.standardThoroughfareInfo.tlm.workTime
                      }}
                    </div>

                    <div class="detail-info">
                      <div>
                        PDU count：
                        {{ item.standardThoroughfareInfo.tlm.pduCount }}
                      </div>
                      <div>
                        voltage：{{ item.standardThoroughfareInfo.tlm.voltage }}
                      </div>
                    </div>
                  </div>
                </div>
                <!-- LINE -->
                <div v-if="item.standardThoroughfareInfo.line">
                  <div class="name">
                    {{ item.standardThoroughfareInfo.line.type }}
                  </div>
                  <div class="info-box">
                    <div class="detail-info">
                      <div>
                        hwid：{{ item.standardThoroughfareInfo.line.hwid }}
                      </div>
                      <div>
                        TimeStamp：{{
                          item.standardThoroughfareInfo.line.timesTamp
                        }}
                      </div>
                    </div>
                    <div class="detail-info">
                      <div>
                        SecureMessage：{{
                          item.standardThoroughfareInfo.line.secureMessage
                        }}
                      </div>
                      <div>
                        Rssi @ 1m ：{{
                          item.standardThoroughfareInfo.line.measurePower
                        }}dBm
                      </div>
                    </div>
                  </div>
                </div>

                <!--  -->
                <div v-if="item.standardThoroughfareInfo.acc">
                  <div class="name">
                    {{ item.standardThoroughfareInfo.acc.type }}
                  </div>
                  <div class="info-box">
                    <div class="detail-info">
                      <div>
                        xAxis：{{ item.standardThoroughfareInfo.acc.xAxis }}
                      </div>
                      <div>
                        yAxis：{{ item.standardThoroughfareInfo.acc.yAxis }}
                      </div>
                      <div>
                        zAxis：{{ item.standardThoroughfareInfo.acc.zAxis }}
                      </div>
                    </div>
                  </div>
                </div>
                <!-- DeviceInfo -->
                <div v-if="item.standardThoroughfareInfo.info">
                  <div class="name">
                    {{ item.standardThoroughfareInfo.info.type }}
                  </div>
                  <div class="info-box">
                    <div class="detail-info">
                      <div>
                        {{ i18nInfo.triggerConditions }}

                        <span
                          v-if="
                            !item.standardThoroughfareInfo.info
                              .triggerCondition ||
                            item.standardThoroughfareInfo.info.triggerCondition
                              .length == 0
                          "
                          >Empty</span
                        >
                        <span v-else>
                          <span
                            v-for="e in item.standardThoroughfareInfo.info
                              .triggerCondition"
                          >
                            {{ $storage.triggerActions[e - 1].name + " " }}
                          </span>
                        </span>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- <div v-show="thoroughfare.type == 'Coreaiot'"></div>-->
                <div v-if="item.standardThoroughfareInfo.quuppa">
                  <div class="name">
                    {{ item.standardThoroughfareInfo.quuppa.type }}
                  </div>
                  <div class="info-box">
                    <div class="detail-info">
                      <div>
                        Quuppa Tag ID：
                        {{ item.standardThoroughfareInfo.quuppa.tagId }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 批量配置 -->
      <div class="batch-config" v-show="batchModel">
        <van-button
          class="channel"
          @click="enterBatchConfigPage('channel-home')"
          :disabled="batchCount == 0"
        >
          {{ i18nInfo.button.batchConfigChannel }}
        </van-button>
        <van-button
          class="secret-key"
          @click="enterBatchConfigPage('secret-key')"
          :disabled="batchCount == 0"
        >
          {{ i18nInfo.button.batchConfigSecretKey }}
        </van-button>
      </div>

      <!-- 加载 -->
      <div
        class="scanning"
        v-show="!scanState && !batchModel"
        @click="startScan"
      >
        <div class="scan">{{ i18nInfo.button.refresh }}</div>
      </div>

      <div
        class="scanning scanning-animation"
        v-show="scanState && !batchModel"
      >
        <div class="loading">
          <div class="dot dot-1"></div>
          <div class="dot dot-2"></div>
          <div class="dot dot-3"></div>
        </div>
      </div>

      <!-- 设备过滤弹窗  -->
      <van-dialog
        v-model="deviceFilterDialog"
        :title="i18nInfo.lable.deviceFilter"
        show-cancel-button
        :close-on-click-overlay="false"
        @confirm="deviceFilterConfirm"
        @cancel="deviceFilterCancel"
        :cancel-button-text="$t('baseButton.cancel')"
        :confirm-button-text="$t('baseButton.sure')"
      >
        <div class="dialog-box">
          <el-radio-group v-model="initParams.deviceType">
            <el-radio-button
              :label="i18nInfo.button.allDevice"
            ></el-radio-button>
            <el-radio-button
              :label="i18nInfo.button.thisBrandDevice"
            ></el-radio-button>
          </el-radio-group>

          <div class="slider">
            <div class="title">RSSI</div>
            <van-slider
              step="1"
              max="110"
              min="40"
              v-model="initParams.rssiValue"
            />
            <div class="value">
              {{ -initParams.rssiValue }}
            </div>
          </div>
        </div>
      </van-dialog>

      <!-- 语言选择弹窗  -->
      <van-dialog
        v-model="languageSettingDialog"
        :title="i18nInfo.lable.selectLanguage"
        show-cancel-button
        :close-on-click-overlay="false"
        @confirm="languageConfirm"
        @cancel="languageCancel"
        :cancel-button-text="$t('baseButton.cancel')"
        :confirm-button-text="$t('baseButton.sure')"
      >
        <div class="dialog-box">
          <el-radio-group v-model="language">
            <el-radio-button label="简体中文"></el-radio-button>
            <el-radio-button label="English"></el-radio-button>
          </el-radio-group>
        </div>
      </van-dialog>

      <!-- 批量配置秘钥弹窗 -->
      <van-dialog
        v-model="batchConfigSecretKeyDialog"
        :title="i18nInfo.title.batchConfigSecretKey"
        show-cancel-button
        :close-on-click-overlay="false"
        :before-close="batchSecretKeyDialogBeforeClose"
        :cancel-button-text="$t('baseButton.cancel')"
        :confirm-button-text="$t('baseButton.sure')"
      >
        <div class="dialog-box">
          <van-field
            maxlength="6"
            clearable
            v-model.trim="secretKey.oldSecretKey"
            @input="secretKeyInput(secretKey.oldSecretKey, 'old')"
            :placeholder="i18nInfo.tips.oldSecretDialogPlaceholder"
          />
          <van-field
            maxlength="6"
            clearable
            v-model.trim="secretKey.newSecretKey"
            @input="secretKeyInput(secretKey.newSecretKey, 'new')"
            :placeholder="i18nInfo.tips.newSecretDialogPlaceholder"
          />
        </div>
      </van-dialog>

      <!-- 弹窗 -->
      <van-dialog
        v-model="secretKeyDialog"
        :title="i18nInfo.title.secretKeySetting"
        show-cancel-button
        :cancel-button-text="$t('baseButton.cancel')"
        :confirm-button-text="$t('baseButton.sure')"
        :before-close="secretKeyDialogBeforeClose"
        theme="round-button"
      >
        <van-field
          maxlength="6"
          v-model.trim="channelSecretKey"
          :placeholder="i18nInfo.tips.secretDialogPlaceholder"
        />
      </van-dialog>

      <!-- 批量配置遮罩 -->

      <van-overlay :show="configOverlay">
        <div class="wrapper" @click.stop>
          <div class="block">
            <van-loading vertical type="spinner" color="#ffffff">
              {{ i18nInfo.tips.configuration }}
              {{ alreadConfigNum }}
              /
              {{ batchCount }}
              ...
            </van-loading>
          </div>
        </div>
      </van-overlay>

      <!-- 批量记录弹出层 -->
      <!-- 圆角弹窗（底部） -->
      <van-popup
        v-model="batchConfigRecordPopupShow"
        round
        position="bottom"
        :style="{ height: '90%' }"
        closeable
      >
        <div class="title">{{ i18nInfo.title.batchRecord }}</div>
        <van-tabs v-model:active="batchConfigPopupActive">
          <van-tab title="Channel">
            <div class="list">
              <div class="item" v-for="(item, index) in channelRecordList">
                <div class="item-box">
                  <div class="index">{{ index + 1 }}</div>

                  <div class="address">
                    <div>Mac</div>
                    <div>{{ item.address }}</div>
                  </div>
                  <div class="division-line2"></div>

                  <div class="error-reason">
                    <div>{{ i18nInfo.lable.errorReason }}</div>

                    <div class="failure-reason">{{ item.failReason }}</div>
                  </div>
                </div>
              </div>
            </div>
          </van-tab>
          <van-tab title="SecretKey">
            <div class="list">
              <div class="item" v-for="(item, index) in secretKeyRecordList">
                <div class="item-box">
                  <div class="index">{{ index + 1 }}</div>

                  <div class="address">
                    <div>Mac</div>
                    <div>{{ item.address }}</div>
                  </div>
                  <div class="division-line2"></div>

                  <div class="error-reason">
                    <div>{{ i18nInfo.lable.errorReason }}</div>

                    <div class="failure-reason">{{ item.failReason }}</div>
                  </div>
                </div>
              </div>
            </div></van-tab
          >
        </van-tabs>
        <div class="retry-button">
          <van-button
            v-if="batchConfigPopupActive == 0"
            :disabled="!channelRecordList.length"
            @click="batchConfigRetry('channel')"
          >
            {{ $t("notifyMessage.base.retry") }}
          </van-button>

          <van-button
            v-else
            :disabled="!secretKeyRecordList.length"
            @click="batchConfigRetry('secret')"
          >
            {{ $t("notifyMessage.base.retry") }}
          </van-button>
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

export default {
  data() {
    return {
      contentReload: true,
      i18nInfo: this.$i18n.t("home"),
      // 搜索条件
      searchValue: "",
      // 获取到焦点时候的值
      searchFocusValue: "",
      // 扫描数量
      count: 0,
      // 批量模式
      batchModel: false,
      // 批量全选复选框
      batchSelectAllCheck: false,
      list: [],
      interval: null,
      // 扫描状态
      scanState: true,
      // 国际化参数
      initParams: {
        sortType: "rssi_fall",
        address: "",
        rssiValue: 58,
        deviceType: "全部设备",
      },

      filterInfoParamsCache: {},

      deviceFilterDialog: false,
      languageSettingDialog: false,
      // 批量配置秘钥弹窗
      batchConfigSecretKeyDialog: false,
      secretKeyDialog: false,

      secretKey: {
        newSecretKey: "",
        oldSecretKey: "",
      },

      channelSecretKey: "",

      // 配置秘钥遮罩
      configOverlay: false,
      // 已配置数量
      alreadConfigNum: 0,
      language: "简体中文",

      // 批量模式列表
      batchList: [],
      // 批量模式数量
      batchCount: 0,
      batchConfigInterval: null,
      // 批量配置弹窗层显示
      batchConfigRecordPopupShow: false,
      // tab active
      batchConfigPopupActive: 0,
      channelRecordList: [],
      secretKeyRecordList: [],

      // 批量重试
      isBatchConfigRetry: false,
      // 批量配置通道
      batchConfigChannelFlag: false,
    };
  },

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

  mounted() {
    this.queryFilterInfo().then((res) => {
      this.init(res);
    });
    if (window.history && window.history.pushState) {
      history.pushState(null, null, document.URL);
      //false阻止默认事件
      window.addEventListener("popstate", this.goBack, false);
      this.$androidApi.clearCanBackHistory();
    }
    window.addEventListener("commonAndroidEvent", this.callJs);

    this.loadingConfigurationInfo();
  },

  destroyed() {
    //false阻止默认事件
    window.removeEventListener("popstate", this.goBack, false);
    window.removeEventListener("commonAndroidEvent", this.callJs);
    this.stopScan();
    if (this.interval) {
      clearInterval(this.interval);
    }
  },

  methods: {
    // 退出APP
    goBack() {},
    openBatchConfigRecordPopup() {
      this.batchConfigRecordPopupShow = true;

      this.channelRecordList = [];
      this.secretKeyRecordList = [];
      this.$androidApi.queryBatchConfigFailureRecord().then((data) => {
        if (data && data.length > 0) {
          console.log(JSON.stringify(data));
          data.forEach((e) => {
            if (e.type == 0) {
              this.channelRecordList.push(e);
            } else if (e.type == 1) {
              this.secretKeyRecordList.push(e);
            }
          });
        }
      });
    },

    sdkConfig() {
      this.$router.push("/config");
    },

    callJs(e) {
      console.log(JSON.stringify(e));
      switch (e.data.eventName) {
        case "SCAN_RESULT":
          // 批量模式扫码
          if (this.batchModel) {
            this.handleBatchScanResult(e.data.data);
          } else {
            this.searchValue = e.data.data;
            // 重新调用开始扫描
            this.initParams.address = e.data.data;
            this.init();
          }
          break;
        default:
          break;
      }
    },

    handleBatchScanResult(data) {
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
          }

          for (let i = 0; i <= endMac - startMac; i++) {
            map.set((startMac + i).toString(16).toUpperCase(), startMac + i);
          }
        }
      }
      if (map.size > 0) {
        for (let i = 0; i < this.list.length; i++) {
          const device = this.list[i];
          let mac = device.address.replace(/:/g, "");
          if (map.has(mac)) {
            device.selected = true;
            this.$set(this.list, i, device);
          }
        }
        let unselectList = this.list.filter((e) => !e.selected);
        if (unselectList.length == 0) {
          this.batchSelectAllCheck = true;
        }
      }
    },

    loadingConfigurationInfo() {
      return new Promise((resolve, reject) => {
        this.$androidApi.queryconfigurationInfo().then((res) => {
          this.$storage.language = res.language;
          this.language = res.language;
          if (this.language == "简体中文") {
            this.$i18n.locale = "zh";
          } else {
            this.$i18n.locale = "en";
          }
          localStorage.setItem("lang", this.$i18n.locale);
          resolve();
        });
      });
    },

    /**
     * 设置批量模式
     */
    setBatchModel() {
      this.batchModel = !this.batchModel;
      this.batchSelectAllCheck = false;
      if (this.batchModel) {
        // 停止扫描
        this.stopScan();
        if (this.interval) {
          clearInterval(this.interval);
          this.interval = null;
          this.scanState = false;
        }
        // 并且设置列表选中状态
        if (this.list && this.list.length > 0) {
          // 设置列表选中状态为false
          this.list.forEach((e) => (e.selected = false));
        }
        // 重置批量数据
        this.batchList = [];
        this.$storage.toBeConfiguredList = [];
        this.batchCount = 0;
      }
    },

    batchSelectAllBeacon() {
      this.batchCount = 0;
      this.batchSelectAllCheck = !this.batchSelectAllCheck;
      if (this.batchSelectAllCheck) {
        this.list.forEach((e) => {
          e.selected = true;
          this.batchCount++;
        });
      } else {
        this.list.forEach((e) => (e.selected = false));
      }
    },

    /**
     * 选中设备
     */

    batchSelectBeacon(index) {
      if (!this.list || this.list.length < index) {
        return;
      }
      let item = this.list[index];
      item.selected = !item.selected;
      this.$set(this.list, index, item);
      if (item.selected) {
        this.batchCount++;
        let unselectList = this.list.filter((e) => !e.selected);
        if (unselectList.length == 0) {
          this.batchSelectAllCheck = true;
        }
      } else {
        this.batchCount--;
        if (this.batchSelectAllCheck) {
          this.batchSelectAllCheck = false;
        }
      }
    },

    /**
     * 校验是否批量模式
     */
    checkBatchModel(notNeedTips) {
      if (this.batchModel) {
        if (!notNeedTips) {
          Notify({
            type: "warning",
            message: this.i18nInfo.batchModelNotAllowedClick,
          });
        }
        return false;
      }
      return true;
    },

    /**
     * 进入批量配置页面
     * @param {页面} e
     */
    enterBatchConfigPage(e) {
      this.$storage.toBeConfiguredChannelList = [];
      let list = [];
      this.list.forEach((e) => {
        if (e.selected == true) {
          list.push(e.address);
        }
      });
      this.$storage.toBeConfiguredList = [...list];
      if (e == "channel-home") {
        this.$storage.batchConfigChannelInfo = {
          batchConfigChannelFlag: false,
          secretKey: "",
        };
        this.$router.push("/home/batch-config/" + e);
      } else if (e == "secret-key") {
        this.batchConfigSecretKeyDialog = true;
        this.secretKey = {
          oldSecretKey: "",
          newSecretKey: "",
        };
      }
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
        // 重新调用开始扫描
        this.initParams.address = this.searchValue;
        this.init();
      }
      this.searchFocusValue = "";
    },

    scanQrCode() {
      // if (!this.checkBatchModel()) {
      //   return;
      // }
      this.$androidApi.scanQrCode();
    },

    queryFilterInfo() {
      return new Promise((resolve, reject) => {
        this.$androidApi.queryFilterInfo().then((data) => {
          resolve(data);
        });
      });
    },

    init(data) {
      if (data) {
        this.searchValue = data.filterAddress;
        this.initParams.address = data.filterAddress;
        this.initParams.rssiValue = -data.rssi;
        if (data.normDevice) {
          this.initParams.deviceType = this.i18nInfo.button.thisBrandDevice;
        } else {
          this.initParams.deviceType = this.i18nInfo.button.allDevice;
        }
      }

      let params = {
        sortType: this.initParams.sortType,
        address: this.initParams.address,
        rssiValue: -this.initParams.rssiValue,
        allDevice: this.initParams.deviceType == this.i18nInfo.button.allDevice,
      };

      this.$androidApi.init(params).then(() => {
        if (this.$storage.batchConfigChannelInfo.batchConfigChannelFlag) {
          this.$storage.batchConfigChannelInfo.batchConfigChannelFlag = false;
          this.startBatchConfigChannel();
          this.scanState = false;
          return;
        }
        this.startScan();
      });
    },

    openDeviceFilterDialog() {
      if (!this.checkBatchModel()) {
        return;
      }
      this.deviceFilterDialog = true;
      setTimeout(() => {
        this.filterInfoParamsCache = JSON.parse(
          JSON.stringify(this.initParams)
        );
      }, 50);
    },

    openLanguageSettingDialog() {
      this.languageSettingDialog = true;
      this.language = this.$storage.language;
    },

    batchConfigRetry(type) {
      this.isBatchConfigRetry = true;
      this.alreadConfigNum = 0;
      this.batchCount = 0;
      this.$storage.toBeConfiguredList = [];

      if (type == "channel") {
        this.channelRecordList.forEach((e) =>
          this.$storage.toBeConfiguredList.push(e.address)
        );
        this.batchCount = this.channelRecordList.length;
        this.$storage.toBeConfiguredChannelList = [];
        this.secretKeyDialog = true;
        this.channelSecretKey = "";
      } else if (type == "secret") {
        this.secretKeyRecordList.forEach((e) =>
          this.$storage.toBeConfiguredList.push(e.address)
        );
        this.batchCount = this.secretKeyRecordList.length;
        this.batchConfigRecordPopupShow = false;
        this.batchConfigSecretKeyDialog = true;
        this.secretKey = {
          oldSecretKey: "",
          newSecretKey: "",
        };
      }
    },

    startBatchConfigChannel() {
      this.isBatchConfigRetry = false;

      let params = {
        secretKey: this.$storage.batchConfigChannelInfo.secretKey,
        addressJson: JSON.stringify(this.$storage.toBeConfiguredList),
        beaconListJson: JSON.stringify(this.$storage.toBeConfiguredChannelList),
        retry: false,
      };
      this.alreadConfigNum = 0;
      this.batchCount = this.$storage.toBeConfiguredList.length;
      this.$androidApi
        .batchConfigChannel(params)
        .then(() => {
          this.configOverlay = true;
          this.queryConfigResultList();
        })
        .catch((errorMsg) => {
          Notify({ type: "warning", message: errorMsg });
        });

      // 关闭错误列表弹窗
      this.batchConfigChannelFlag = false;
      this.$storage.toBeConfiguredChannelList = [];
      this.$storage.toBeConfiguredList = [];
    },

    /**
     * 秘钥批量配置弹窗
     * @param {欣慰} action
     * @param {done} done
     */
    batchSecretKeyDialogBeforeClose(action, done) {
      if (action == "confirm") {
        if (
          !this.secretKey.oldSecretKey ||
          !this.secretKey.newSecretKey ||
          this.secretKey.oldSecretKey.length != 6 ||
          this.secretKey.newSecretKey.length != 6
        ) {
          Notify({
            type: "warning",
            message: this.$i18n.t("notifyMessage.base.paramsError"),
          });
          done(false);
          return;
        }

        if (this.secretKey.oldSecretKey == this.secretKey.newSecretKey) {
          Notify({
            type: "warning",
            message: this.i18nInfo.tips.secretKeySameTips,
          });
          done(false);
          return;
        }

        // 调用Android 开始获取结果
        this.$androidApi
          .batchConfigSecretKey({
            addressJson: JSON.stringify(this.$storage.toBeConfiguredList),
            oldSecretKey: this.secretKey.oldSecretKey,
            secretKey: this.secretKey.newSecretKey,
          })
          .then(() => {
            this.configOverlay = true;
            this.batchModel = false;
            this.alreadConfigNum = 0;
            this.queryConfigResultList();
          })
          .catch((errorMsg) => {
            Notify({ type: "warning", message: errorMsg });
          });
      }
      // 关闭错误列表弹窗
      this.batchConfigRecordPopupShow = false;
      done();
    },

    secretKeyDialogBeforeClose(action, done) {
      if (action == "confirm") {
        if (!this.channelSecretKey || this.channelSecretKey.length != 6) {
          Notify({
            type: "warning",
            message: this.$i18n.t("notifyMessage.base.paramsError"),
          });
          done(false);
        }

        let params = {
          secretKey: this.channelSecretKey,
          addressJson: JSON.stringify(this.$storage.toBeConfiguredList),
          retry: true,
        };
        this.$androidApi
          .batchConfigChannel(params)
          .then(() => {
            this.configOverlay = true;
            this.alreadConfigNum = 0;
            this.queryConfigResultList();
          })
          .catch((errorMsg) => {
            Notify({ type: "warning", message: errorMsg });
          });
      }
      // 关闭错误列表弹窗
      this.batchConfigRecordPopupShow = false;
      done();
    },

    secretKeyInput(value, type) {
      let codeReg = new RegExp("[A-Za-z0-9]+"); //正则 英文+数字；
      let len = value.length;
      let str = "";
      for (var i = 0; i < len; i++) {
        if (codeReg.test(value[i])) {
          str += value[i];
        }
      }
      if (type == "old") {
        this.secretKey.oldSecretKey = str;
      } else {
        this.secretKey.newSecretKey = str;
      }
    },

    /**
     * 查询批量配置结果
     */
    queryConfigResultList() {
      if (null != this.batchConfigInterval) {
        clearInterval(this.batchConfigInterval);
      }

      this.batchConfigInterval = setInterval(() => {
        this.$androidApi.batchConfigChannelList().then((res) => {
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
            if (num == this.batchCount) {
              clearInterval(this.batchConfigInterval);
              this.configOverlay = false;
              Dialog.confirm({
                title: this.i18nInfo.title.updateResult,
                message:
                  this.i18nInfo.title.configurationQuantity +
                  this.batchCount +
                  "<br>" +
                  this.i18nInfo.tips.success +
                  successNum +
                  "、 " +
                  this.i18nInfo.tips.failed +
                  failNum,
                className: "warnDialogClass",
                cancelButtonText: this.i18nInfo.button.errorList,
                confirmButtonText: this.$i18n.t("baseButton.sure"),
                theme: "round-button",
                messageAlign: "left",
                showCancelButton: failNum > 0,
              })
                .then(() => {})
                .catch(() => {
                  this.openBatchConfigRecordPopup();
                });
            }
          }
        });
      }, 1500);
    },

    /**
     * 处理排序
     */
    handleSort(command) {
      if (!this.checkBatchModel()) {
        return;
      }
      this.initParams.sortType = command;
      this.init();
    },

    startScan() {
      if (!this.checkBatchModel()) {
        return;
      }
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
      this.scanState = false;
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
      }, 1000);
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
      if (!this.checkBatchModel()) {
        return;
      }
      if (!item.connectable) {
        Notify({
          type: "danger",
          message: this.i18nInfo.tips.deviceCanNotConnect,
        });
        return;
      }
      this.$router.push({
        path: "/home/deviceDetail",
        query: {
          address: item.address,
          connectable: item.connectable,
        },
      });
    },

    toLink(url) {
      this.$androidApi.openLink({
        link: url,
      });
    },

    // 设备筛选弹框 确认
    deviceFilterConfirm() {
      this.init();
    },

    deviceFilterCancel() {
      this.initParams = JSON.parse(JSON.stringify(this.filterInfoParamsCache));
    },

    languageConfirm() {
      if (this.language == this.$storage.language) {
        return;
      }
      let params = {
        language: this.language,
      };
      this.$androidApi
        .updateconfigurationInfo(params)
        .then(() => {
          this.$storage.language = this.language;
          if (this.language == "简体中文") {
            this.$i18n.locale = "zh";
          } else {
            this.$i18n.locale = "en";
          }
          localStorage.setItem("lang", this.$i18n.locale);
          this.$storage.triggerActions = [
            { name: this.$i18n.t("baseButton.doubleClick"), code: 1 },
            { name: this.$i18n.t("baseButton.tripleClick"), code: 2 },
            { name: this.$i18n.t("baseButton.acceleration"), code: 3 },
          ];
          Notify({ type: "success", message: "Success" });
        })
        .catch((errorMsg) => {
          Notify({ type: "warning", message: errorMsg });
        });
    },

    languageCancel(e) {
      console.log(e);
    },

    macReg(value) {
      let reg = /^[0-9a-fA-F]{12}$/;
      return reg.test(value);
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
      // width: 3.81rem;
      height: 0.82rem;
      font-size: 0.32rem;
      font-family: Source Han Sans CN-Regular, Source Han Sans CN;
      font-weight: 400;
      color: #000000;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 0.26rem;
      .scan-info {
        font-size: 0.26rem;
      }
      .select-all-box {
        display: flex;
        align-items: center;
        .lable {
          margin-right: 0.1rem;
        }
        .van-image {
          width: 0.42rem;
          height: 0.42rem;
        }
      }
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

  // 批量
  .blue-icon {
    color: #007fff;
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
        height: 1.48rem;
        .base-info-box {
          font-size: 0.32rem;
          font-family: Source Han Sans CN-Bold, Source Han Sans CN;
          font-weight: bold;
          color: #000000;
          .name {
            display: flex;
            align-items: center;
            justify-content: space-between;
            .van-image {
              width: 0.4rem;
              height: 0.4rem;
            }
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
        .thoroughfares-info {
          .thoroughfare {
            &:nth-child(n + 2) {
              margin-top: 0.1rem;
            }
          }
          .name {
            font-size: 0.32rem;
            font-family: Source Han Sans CN-Regular, Source Han Sans CN;
            font-weight: 500;
            color: #0b1a64;
            &:nth-child(n + 2) {
              margin-top: 0.08rem;
            }
          }
          .info-box {
            font-size: 0.25rem;
            .uuid {
              font-size: 0.26rem;
            }
            .detail-info {
              display: flex;
              justify-content: space-between;
            }
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
      font-size: 0.26rem;
    }
  }
  .batch-config {
    position: fixed;
    bottom: 0.32rem;
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 7rem;

    .van-button {
      width: 3.4rem;
      height: 0.88rem;
      border-radius: 0.44rem 0.44rem 0.44rem 0.44rem;
      font-size: 0.26rem;
      font-family: PingFang SC-Regular, PingFang SC;
      font-weight: 400;
      line-height: 0.29rem;
    }
    .channel {
      background: #ffffff;
      border: 0.01rem solid #107fff;
      color: #007fff;
    }
    .secret-key {
      background: #007fff;
      color: #ffffff;
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

  .van-popup {
    .title {
      width: 1.44rem;
      height: 0.86rem;
      font-size: 0.36rem;
      font-family: Source Han Sans CN-Regular, Source Han Sans CN;
      font-weight: 400;
      color: #000000;
      width: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .list {
      height: 68vh;
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
}
</style>
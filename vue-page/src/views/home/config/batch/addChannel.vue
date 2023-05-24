<!-- 批量配置添加通道 -->
<template>
  <div>
    <nav-bar>
      <van-icon name="arrow-left" slot="left" class="navIcon" @click="goBack" />
      <div slot="title">{{ $t("pageTitle.addChannel") }}</div>
    </nav-bar>

    <div class="content">
      <van-cell
        :title="i18nInfo.frameType"
        :value="frameType"
        @click="frameTypeShow = !frameTypeShow"
        is-link
        arrow-direction="down"
      />

      <van-cell
        class="little-margin-top"
        center
        :title="i18nInfo.addChannel.alwaysBroadcast"
      >
        <template #right-icon>
          <van-switch
            :disabled="!triggerSwitch"
            v-model="alwaysBroadcastValue"
            size="17"
          />
        </template>
      </van-cell>

      <!-- 广播内容 -->
      <van-cell-group
        class="little-margin-top"
        v-show="
          frameType == 'UID' ||
          frameType == 'URL' ||
          frameType == 'DeviceInfo' ||
          frameType == 'LINE' ||
          frameType == 'Quuppa'
        "
      >
        <van-cell
          class="little-margin-top"
          :title="i18nInfo.common.broadcastContent"
        />

        <div v-if="frameType == 'UID'">
          <van-field
            required
            label="NamespaceID"
            label-width="2.1rem"
            input-align="right"
            maxlength="20"
            :placeholder="i18nInfo.addChannel.namespaceContent"
            v-model.trim="iBeaconBroadcastData[0]"
            @input="inputFormat(0, true)"
          />
          <van-field
            required
            label="InstanceID"
            label-width="2.1rem"
            input-align="right"
            maxlength="12"
            :placeholder="i18nInfo.addChannel.instanceContent"
            v-model.trim="iBeaconBroadcastData[1]"
            @input="inputFormat(1, true)"
          />
        </div>

        <div v-else-if="frameType == 'URL'" class="url-box">
          <div class="lable">URL</div>
          <el-dropdown trigger="click" @command="selectPrefix">
            <span class="el-dropdown-link">
              {{ iBeaconBroadcastData[0] }}
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="http://www.">
                http://www.
              </el-dropdown-item>
              <el-dropdown-item command="https://www.">
                https://www.
              </el-dropdown-item>
              <el-dropdown-item command="http://"> http:// </el-dropdown-item>
              <el-dropdown-item command="https://"> https:// </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <van-field
            required
            input-align="center"
            maxlength="16"
            v-model.trim="iBeaconBroadcastData[1]"
            :placeholder="i18nInfo.addChannel.urlContent"
          />
          <el-dropdown trigger="click" @command="selectSuffix">
            <span class="el-dropdown-link"> {{ iBeaconBroadcastData[2] }}</span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command=".com">.com</el-dropdown-item>
              <el-dropdown-item command=".org">.org</el-dropdown-item>
              <el-dropdown-item command=".edu">.edu</el-dropdown-item>
              <el-dropdown-item command=".net">.net</el-dropdown-item>
              <el-dropdown-item command=".info">.info</el-dropdown-item>
              <el-dropdown-item command=".biz">.biz</el-dropdown-item>
              <el-dropdown-item command=".gov">.gov</el-dropdown-item>
              <el-dropdown-item command=".com/">.com/</el-dropdown-item>
              <el-dropdown-item command=".org/">.org/</el-dropdown-item>
              <el-dropdown-item command=".edu/">.edu/</el-dropdown-item>
              <el-dropdown-item command=".net/">.net/</el-dropdown-item>
              <el-dropdown-item command=".info/">.info/</el-dropdown-item>
              <el-dropdown-item command=".biz/">.biz/</el-dropdown-item>
              <el-dropdown-item command=".gov/">.gov/</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>

        <div v-else-if="frameType == 'DeviceInfo'">
          <!-- 设备名称 -->
          <van-field
            required
            label="DeviceName"
            label-width="2rem"
            input-align="right"
            maxlength="6"
            placeholder="DeviceName"
            v-model.trim="deviceName"
          />
        </div>

        <div v-else-if="frameType == 'LINE'">
          <!-- Hwid -->
          <van-field
            required
            label="Hwid"
            label-width="2rem"
            input-align="right"
            maxlength="10"
            @input="inputFormat(2)"
            :placeholder="i18nInfo.addChannel.hwidContent"
            v-model.trim="iBeaconBroadcastData[2]"
          />
          <!-- Vendor_Key -->
          <van-field
            required
            label="Vendor_Key"
            label-width="2rem"
            input-align="right"
            maxlength="8"
            @input="inputFormat(0)"
            :placeholder="i18nInfo.addChannel.vendorKeyContent"
            v-model.trim="iBeaconBroadcastData[0]"
          />
          <!-- Lot_Key -->
          <van-field
            label="Lot_Key"
            required
            label-width="2rem"
            input-align="right"
            maxlength="16"
            @input="inputFormat(1)"
            :placeholder="i18nInfo.addChannel.lotKeyContent"
            v-model.trim="iBeaconBroadcastData[1]"
          />
        </div>
        <div v-else-if="frameType == 'Quuppa'">
          <van-field
            label="Quuppa Tag"
            label-width="2rem"
            input-align="right"
            maxlength="12"
            :placeholder="i18nInfo.addChannel.instanceContent"
            @input="inputFormat(0)"
            v-model.trim="iBeaconBroadcastData[0]"
          />
        </div>
      </van-cell-group>

      <!-- 基础参数 -->
      <van-cell-group class="little-margin-top">
        <van-cell
          class="little-margin-top"
          :title="i18nInfo.common.basicParams"
        />
        <!-- 广播间隔 -->
        <div class="slider-box">
          <div class="lable">
            {{ i18nInfo.common.broadcastInterval }}
          </div>

          <div class="slider">
            <van-slider
              step="100"
              max="3000"
              min="100"
              v-model="broadcastSliderValue"
            />
          </div>

          <div class="unit">{{ broadcastSliderValue }}ms</div>
        </div>
        <!-- 校准距离 -->
        <div class="slider-box">
          <div class="lable">
            {{ i18nInfo.common.calibrationDistance }}
          </div>

          <div class="slider">
            <van-slider
              step="1"
              max="0"
              min="-100"
              v-model="calibrationDistanceValue"
            />
          </div>

          <div class="unit">{{ calibrationDistanceValue }}dBm</div>
        </div>
        <!-- 广播功率 -->
        <div class="slider-box">
          <div class="lable">
            {{ i18nInfo.common.broadcastPower }}
          </div>

          <div class="slider">
            <van-slider
              step="1"
              max="120"
              min="0"
              v-model="broadcastPowerPageValue"
            />
          </div>

          <div class="unit">{{ broadcastPowerValue / 10 }}dBm</div>
        </div>
      </van-cell-group>

      <!-- 触发器 -->
      <van-cell-group class="little-margin-top">
        <van-cell center :title="i18nInfo.addChannel.trigger">
          <template #right-icon>
            <van-switch
              v-model="triggerSwitch"
              @change="triggerSwitchChange"
              size="17"
            />
          </template>
        </van-cell>

        <div :class="{ 'not-click': !triggerSwitch }">
          <!-- 触发条件 -->
          <van-cell
            :title="i18nInfo.addChannel.triggerConditions"
            :value="triggerCondition ? triggerCondition : '请选择'"
            @click="triggerShow = !triggerShow"
            is-link
            arrow-direction="down"
          />
          <!-- 广播时间 -->
          <div class="slider-box">
            <div class="lable">
              {{ i18nInfo.addChannel.broadcastTime }}
            </div>

            <div class="slider">
              <van-slider
                step="1"
                max="60"
                min="1"
                v-model="triggerBroadcastTimeValue"
              />
            </div>

            <div class="unit">{{ triggerBroadcastTimeValue }}s</div>
          </div>
          <!-- 广播间隔 -->
          <div class="slider-box">
            <div class="lable">
              {{ i18nInfo.common.broadcastInterval }}
            </div>

            <div class="slider">
              <van-slider
                step="100"
                max="3000"
                min="100"
                v-model="triggerBroadcastSliderValue"
              />
            </div>

            <div class="unit">{{ triggerBroadcastSliderValue }}ms</div>
          </div>

          <!-- 广播功率 -->
          <div class="slider-box">
            <div class="lable">
              {{ i18nInfo.common.broadcastPower }}
            </div>

            <div class="slider">
              <van-slider
                step="1"
                max="120"
                min="0"
                v-model="triggerBroadcastPowerPageValue"
              />
            </div>

            <div class="unit">{{ triggerBroadcastPowerValue / 10 }}dBm</div>
          </div>
        </div>
      </van-cell-group>

      <div class="button-box-max">
        <van-button class="canecl-max">{{
          $t("baseButton.cancel")
        }}</van-button>
        <van-button @click="saveChannel" class="sure-max">
          {{ $t("baseButton.save") }}</van-button
        >
      </div>
    </div>

    <div class="components">
      <!-- 帧类型选择器 -->
      <van-action-sheet
        v-model="frameTypeShow"
        :actions="frameTypeActions"
        :cancel-text="$t('baseButton.cancel')"
        close-on-click-action
        @select="frameTypeOnSelect"
      />

      <!-- 触发条件选择 -->
      <van-action-sheet
        v-model="triggerShow"
        :actions="triggerActions"
        :cancel-text="$t('baseButton.cancel')"
        close-on-click-action
        @select="triggerConditionOnSelect"
      />
    </div>
  </div>
</template>

<script>
import { ActionSheet, Notify, Slider } from "vant";
export default {
  components: {
    [ActionSheet.name]: ActionSheet,
    [Slider.name]: Slider,
    navBar: () => import("@/components/navigation/navBar.vue"),
  },

  data() {
    return {
      // 国际化信息
      i18nInfo: this.$i18n.t("device.channel"),
      address: this.$route.query.address,
      // 帧类型
      frameType: "iBeacon",
      // 帧类型ActionSheet
      frameTypeShow: false,
      // 帧类型选项列表
      frameTypeActions: this.$storage.frameTypes,
      // 设备名称
      deviceName: "",
      // response开关
      responseSwitch: false,
      // 始终广播
      alwaysBroadcastValue: true,
      // 滑块
      // 广播间隔
      broadcastSliderValue: 1000,
      // 校准距离
      calibrationDistanceValue: -51,
      // 广播功率
      broadcastPowerValue: -100,
      broadcastPowerPageValue: 25,
      // 触发器 默认关
      triggerSwitch: false,

      triggerActions: this.$storage.triggerActions,

      // 触发条件
      triggerCondition: "加速度",
      // 触发条件 ActionSheet
      triggerShow: false,
      // 滑块
      // 广播间隔
      triggerBroadcastSliderValue: 100,
      // 广播时间
      triggerBroadcastTimeValue: 1,
      // 广播功率
      triggerBroadcastPowerValue: -100,
      triggerBroadcastPowerPageValue: 25,

      // 广播内容
      iBeaconBroadcastData: ["", "", ""],

      saveForm: {
        frameType: "iBeacon",
        alwaysBroadcast: true,
        calibrationDistance: -50,
        broadcastPower: -100,
        broadcastInterval: 1000,
        triggerSwitch: false,
        triggerBroadcastTime: 0,
        triggerBroadcastInterval: 0,
        triggerBroadcastPower: 0,
        triggerCondition: 0,
        broadcastDataJson: "",
      },
    };
  },

  watch: {
    broadcastPowerPageValue(o, n) {
      if (n == null) {
        return;
      }
      if (n <= 10) {
        this.broadcastPowerValue = -195;
      } else if (n > 10 && n <= 20) {
        this.broadcastPowerValue = -135;
      } else if (n > 20 && n <= 30) {
        this.broadcastPowerValue = -100;
      } else if (n > 30 && n <= 40) {
        this.broadcastPowerValue = -70;
      } else if (n > 40 && n <= 50) {
        this.broadcastPowerValue = -50;
      } else if (n > 50 && n <= 60) {
        this.broadcastPowerValue = 60;
      } else if (n > 60 && n <= 70) {
        this.broadcastPowerValue = -20;
      } else if (n > 70 && n <= 80) {
        this.broadcastPowerValue = -10;
      } else if (n > 80 && n <= 90) {
        this.broadcastPowerValue = 0;
      } else if (n > 90 && n <= 100) {
        this.broadcastPowerValue = 10;
      } else if (n > 100 && n <= 110) {
        this.broadcastPowerValue = 15;
      } else if (n > 110) {
        this.broadcastPowerValue = 25;
      }
    },
    triggerBroadcastPowerPageValue(o, n) {
      if (n == null) {
        return;
      }
      if (n <= 10) {
        this.triggerBroadcastPowerValue = -195;
      } else if (n > 10 && n <= 20) {
        this.triggerBroadcastPowerValue = -135;
      } else if (n > 20 && n <= 30) {
        this.triggerBroadcastPowerValue = -100;
      } else if (n > 30 && n <= 40) {
        this.triggerBroadcastPowerValue = -70;
      } else if (n > 40 && n <= 50) {
        this.triggerBroadcastPowerValue = -50;
      } else if (n > 50 && n <= 60) {
        this.triggerBroadcastPowerValue = 60;
      } else if (n > 60 && n <= 70) {
        this.triggerBroadcastPowerValue = -20;
      } else if (n > 70 && n <= 80) {
        this.triggerBroadcastPowerValue = -10;
      } else if (n > 80 && n <= 90) {
        this.triggerBroadcastPowerValue = 0;
      } else if (n > 90 && n <= 100) {
        this.triggerBroadcastPowerValue = 10;
      } else if (n > 100 && n <= 110) {
        this.triggerBroadcastPowerValue = 15;
      } else if (n > 110) {
        this.triggerBroadcastPowerValue = 25;
      }
    },
  },

  mounted() {
    if (window.history && window.history.pushState) {
      history.pushState(null, null, document.URL);
      //false阻止默认事件
      window.addEventListener("popstate", this.fun, false);
    }
  },

  destroyed() {
    //false阻止默认事件
    window.removeEventListener("popstate", this.fun, false);
  },

  methods: {
    fun() {
      this.goBack();
    },
    goBack() {
      this.$router.replace("/home/batch-config/channel-home");
    },

    // 帧类型选择器
    frameTypeOnSelect(e) {
      this.frameType = e.name;
      if (this.frameType == "URL") {
        // url需要赋值前后缀
        this.iBeaconBroadcastData[0] = "http://www.";
        this.iBeaconBroadcastData[1] = "";
        this.iBeaconBroadcastData[2] = ".com";
      } else {
        this.iBeaconBroadcastData = ["", "", "", ""];
      }
    },

    // 触发类型选择
    triggerConditionOnSelect(e) {
      this.triggerCondition = e.name;
    },

    /**
     * 始终广播开关改变
     * 需要把始终广播打开
     */
    triggerSwitchChange(value) {
      if (!value) {
        this.alwaysBroadcastValue = true;
      }
    },

    saveChannel() {
      if (!this.paramsCheck()) {
        return;
      }
      this.saveForm.frameType = this.frameType;

      this.saveForm.alwaysBroadcast = this.alwaysBroadcastValue;
      this.saveForm.calibrationDistance = this.calibrationDistanceValue;
      this.saveForm.broadcastPower = this.broadcastPowerValue / 10;
      this.saveForm.broadcastInterval = this.broadcastSliderValue;
      this.saveForm.triggerSwitch = this.triggerSwitch;

      if (this.triggerSwitch) {
        this.saveForm.triggerBroadcastTime = this.triggerBroadcastTimeValue;
        this.saveForm.triggerBroadcastInterval =
          this.triggerBroadcastSliderValue;
        this.saveForm.triggerBroadcastPower =
          this.triggerBroadcastPowerValue / 10;
        for (let i = 0; i < this.triggerActions.length; i++) {
          const triggerAction = this.triggerActions[i];
          if (triggerAction.name == this.triggerCondition) {
            this.saveForm.triggerCondition = triggerAction.code;
            break;
          }
        }
      } else {
        this.saveForm.triggerBroadcastTime = 0;
        this.saveForm.triggerBroadcastInterval = 0;
        this.saveForm.triggerBroadcastPower = 0;
        this.saveForm.triggerCondition = 0;
      }

      this.saveForm.broadcastDataJson = JSON.stringify(
        this.iBeaconBroadcastData
      );

      this.$storage.addConfigurableChannel(this.saveForm);
      this.$router.replace("/home/batch-config/channel-home");
    },
    // 选择网址前缀
    selectPrefix(e) {
      this.iBeaconBroadcastData[0] = e;
      this.$forceUpdate();
      console.log(this.iBeaconBroadcastData[0]);
    },

    /**
     * 选择网址后缀
     * @param {*} e
     */
    selectSuffix(e) {
      this.iBeaconBroadcastData[2] = e;
      this.$forceUpdate();
      console.log(this.iBeaconBroadcastData[2]);
    },

    inputFormat(index, converUpper) {
      let value = this.iBeaconBroadcastData[index];
      let codeReg = new RegExp("[A-Fa-f0-9]+"); //正则 英文+数字；
      let len = value.length;
      let str = "";
      for (var i = 0; i < len; i++) {
        if (codeReg.test(value[i])) {
          str += value[i];
        }
      }
      if (converUpper) {
        str = str.toUpperCase();
      }
      this.iBeaconBroadcastData[index] = str;
    },

    /**
     * 参数校验
     */
    paramsCheck() {
      if (this.frameType == "UID") {
        if (
          this.iBeaconBroadcastData.length > 0 &&
          this.iBeaconBroadcastData[0].length != 20
        ) {
          Notify({
            type: "warning",
            message: this.i18nInfo.tips.namespaceFormatError,
          });
          return false;
        }
        if (
          this.iBeaconBroadcastData.length > 1 &&
          this.iBeaconBroadcastData[1].length != 12
        ) {
          Notify({
            type: "warning",
            message: this.i18nInfo.tips.instanceFormatError,
          });
          return false;
        }
      } else if (this.frameType == "URL") {
        if (
          this.iBeaconBroadcastData.length > 0 &&
          this.iBeaconBroadcastData[1].length == 0
        ) {
          Notify({
            type: "warning",
            message: this.i18nInfo.tips.urlFormatError,
          });
          return false;
        }
      } else if (this.frameType == "DeviceInfo") {
        if (this.deviceName.length != 6) {
          Notify({
            type: "warning",
            message: this.i18nInfo.tips.deviceNameFormatError,
          });
          return false;
        }
      } else if (this.frameType == "LINE") {
        if (
          this.iBeaconBroadcastData.length > 0 &&
          this.iBeaconBroadcastData[2].length != 10
        ) {
          Notify({
            type: "warning",
            message: this.i18nInfo.tips.hwidFormatError,
          });
          return false;
        }
        if (
          this.iBeaconBroadcastData.length > 1 &&
          this.iBeaconBroadcastData[0].length != 8
        ) {
          Notify({
            type: "warning",
            message: this.i18nInfo.tips.vendorKeyFormatError,
          });
          return false;
        }
        if (
          this.iBeaconBroadcastData.length > 2 &&
          this.iBeaconBroadcastData[1].length != 16
        ) {
          Notify({
            type: "warning",
            message: this.i18nInfo.tips.lotKeyFormatError,
          });
          return false;
        }
      } else if (this.frameType == "Quuppa") {
        console.log(this.iBeaconBroadcastData.length);
        if (
          this.iBeaconBroadcastData[0] &&
          this.iBeaconBroadcastData[0].length != 12 &&
          this.iBeaconBroadcastData[0].length != 0
        ) {
          Notify({
            type: "warning",
            message: this.i18nInfo.tips.quuppaTagFormatError,
          });
          return false;
        }
      }
      return true;
    },
  },
};
</script>
<style lang='scss' scoped>
.content {
  overflow: auto;
  .van-cell__value {
    font-size: 0.3rem;
    font-family: PingFang SC-Regular, PingFang SC;
    font-weight: 400;
    color: #000000;
  }

  .little-margin-top {
    margin-top: var(--little-margin-top);
  }

  .slider-box {
    height: 0.8rem;
    margin-left: 0.28rem;
    display: flex;
    align-items: center;
    border-bottom: 1px solid #d8dceb47;
    font-size: 0.28rem;
    .lable {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 1.7rem;
    }
    .slider {
      margin: 0 0.32rem;
    }
    .unit {
      width: 1rem;
      text-align: center;
      float: right;
    }
  }

  .url-box {
    display: flex;
    align-items: center;
    .lable {
      width: 1.8rem;
      margin-left: 0.29rem;
    }
    .el-dropdown {
      font-size: 0.27rem;
      width: 2rem;
      &:last-child {
        width: 1.1rem;
      }
    }
    .van-field {
      width: 3.5rem;
      padding: 10px 2px;
      font-size: 0.25rem;
    }

    .el-dropdown-link {
      cursor: pointer;
      color: #409eff;
    }
    .el-icon-arrow-down {
      font-size: 14px;
    }
  }

  // 大的取消返回按钮
  .button-box-max {
    position: fixed;
    bottom: 0.32rem;
    height: 0.88rem;
    width: 7rem;
    display: flex;
    justify-content: space-between;
  }
}
.el-dropdown-menu {
  max-height: 6rem;
  overflow: auto;
}
</style>
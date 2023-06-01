<!-- 添加通道 -->
<template>
  <div>
    <nav-bar>
      <van-icon name="arrow-left" slot="left" class="navIcon" @click="goBack" />
      <div slot="title">{{ $t("pageTitle.configureChannel") }}</div>
    </nav-bar>

    <div class="content">
      <div class="content-box">
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
            frameType == 'iBeacon' ||
            frameType == 'UID' ||
            frameType == 'URL' ||
            frameType == 'DeviceInfo' ||
            frameType == 'LINE' ||
            frameType == 'Coreaiot' ||
            frameType == 'Quuppa'
          "
        >
          <van-cell
            class="little-margin-top"
            :title="i18nInfo.common.broadcastContent"
          />
          <div v-if="frameType == 'iBeacon'" class="beacon-info-box">
            <van-field
              clearable
              required
              class="uuid-box"
              label="UUID"
              label-width="1.5rem"
              :placeholder="i18nInfo.addChannel.uuidContent"
              input-align="right"
              maxlength="32"
              v-model.trim="iBeaconBroadcastData[0]"
              @input="uuidInput"
            />
            <van-field
              clearable
              required
              label="Major"
              type="number"
              label-width="2rem"
              input-align="right"
              maxlength="5"
              placeholder="0-65535"
              v-model.trim="iBeaconBroadcastData[1]"
              @input="majorInput"
            />
            <van-field
              clearable
              required
              label="Minor"
              type="number"
              label-width="2rem"
              input-align="right"
              maxlength="5"
              placeholder="0-65535"
              v-model.trim="iBeaconBroadcastData[2]"
              @input="minorInput"
            />

            <van-cell center title="Response">
              <template #right-icon>
                <van-switch v-model="responseSwitch" size="17" />
              </template>
            </van-cell>
            <!-- 设备名称 -->
            <van-field
              clearable
              required
              v-if="responseSwitch"
              label="DeviceName"
              label-width="2rem"
              input-align="right"
              maxlength="6"
              :placeholder="i18nInfo.addChannel.deviceNameContent"
              v-model.trim="deviceName"
              @input="deviceNameInput"
            />
          </div>

          <div v-else-if="frameType == 'UID'">
            <van-field
              clearable
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
              clearable
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
                <el-dropdown-item command="https://">
                  https://
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
            <van-field
              clearable
              required
              input-align="center"
              maxlength="16"
              v-model.trim="iBeaconBroadcastData[1]"
              :placeholder="i18nInfo.addChannel.urlContent"
            />
            <el-dropdown trigger="click" @command="selectSuffix">
              <span class="el-dropdown-link">
                {{ iBeaconBroadcastData[2] }}</span
              >
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
              clearable
              required
              label="DeviceName"
              label-width="2rem"
              input-align="right"
              maxlength="6"
              :placeholder="i18nInfo.addChannel.deviceNameContent"
              v-model.trim="deviceName"
            />
          </div>

          <div v-else-if="frameType == 'LINE'">
            <!-- Hwid -->
            <van-field
              clearable
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
              clearable
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
              clearable
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
          <div v-else-if="frameType == 'Coreaiot'">
            <van-cell :title="i18nInfo.addChannel.broadcastChannel">
              <el-select
                v-model="iBeaconBroadcastData[0]"
                placeholder="channel"
                size="mini"
              >
                <el-option
                  v-for="item in broadcastChannelOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </van-cell>
          </div>
          <div v-else-if="frameType == 'Quuppa'">
            <van-field
              clearable
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
              :value="
                triggerCondition ? triggerCondition : i18nInfo.tips.pleaseSelect
              "
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
          <van-button class="canecl-max" @click="cancelConfig">{{
            $t("baseButton.cancel")
          }}</van-button>
          <van-button @click="saveChannel" class="sure-max">
            {{ $t("baseButton.save") }}</van-button
          >
        </div>
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
import { ActionSheet, Notify, Slider, Stepper } from "vant";
import deviceDetailHelper from "../../hepler/deviceDetailHelper";
import instruct from "@/fetch/instruct";
export default {
  components: {
    [ActionSheet.name]: ActionSheet,
    [Slider.name]: Slider,
    [Stepper.name]: Stepper,
    navBar: () => import("@/components/navigation/navBar.vue"),
  },

  data() {
    return {
      // 国际化信息
      i18nInfo: this.$i18n.t("device.channel"),
      address: this.$route.query.address,
      channelNumber: this.$route.query.channel,
      updateOperation: this.$route.query.updateOperation,
      // 帧类型
      frameType: "iBeacon",
      // 帧类型ActionSheet
      frameTypeShow: false,
      // 帧类型选项列表
      frameTypeActions: [],
      // 通道选择列表
      broadcastChannelOptions: [
        {
          label: "37",
          value: 0,
        },
        {
          label: "38",
          value: 1,
        },
        {
          label: "39",
          value: 2,
        },
        {
          label: "37、38、39",
          value: 3,
        },
      ],
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
      broadcastPowerValue: 0,
      broadcastPowerPageValue: null,
      // 广播内容
      iBeaconBroadcastData: ["", "", ""],
      // 广播内容
      broadcastDataBak: {
        frameType: "iBeacon",
        iBeaconBroadcastData: ["", "", ""],
        responseSwitch: false,
        deviceName: "",
      },
      // 触发器 默认关
      triggerSwitch: false,

      triggerActions: this.$storage.triggerActions,

      // 触发条件
      triggerCondition: this.$i18n.t("baseButton.acceleration"),
      // 触发条件 ActionSheet
      triggerShow: false,

      // 滑块
      // 广播间隔
      triggerBroadcastSliderValue: 100,
      // 广播时间
      triggerBroadcastTimeValue: 1,
      // 广播功率
      triggerBroadcastPowerValue: 0,
      triggerBroadcastPowerPageValue: null,

      saveForm: {
        frameType: "iBeacon",
        channelNumber: 0,
        alwaysBroadcast: true,
        calibrationDistance: -50,
        broadcastPower: -100,
        broadcastInterval: 1000,
        triggerSwitch: false,
        triggerBroadcastTime: 0,
        triggerBroadcastInterval: 0,
        triggerBroadcastPower: 0,
        triggerCondition: 1,
      },
    };
  },

  watch: {
    broadcastPowerPageValue(o, n) {
      if (n == null) {
        return;
      }
      console.log(n);
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
        this.broadcastPowerValue = -35;
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
        this.triggerBroadcastPowerValue = -35;
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
    window.addEventListener("commonAndroidEvent", this.callJs);
    this.init();
  },

  destroyed() {
    //false阻止默认事件
    window.removeEventListener("popstate", this.fun, false);
    window.removeEventListener("commonAndroidEvent", this.callJs);
  },

  methods: {
    fun() {
      this.goBack();
    },
    goBack(data) {
      let reload = "NULL";
      if (data && data == "SUCCESS") {
        reload = "RELOAD";
      }
      this.$router.replace({
        path: "/home/deviceDetail/configureChannel",
        query: {
          address: this.address,
          reloadChannel: reload,
          channelNumber: this.channelNumber,
        },
      });
    },

    init() {
      let channelInfo = this.$storage.getAgreementInfoByNumber(
        this.channelNumber
      );

      // triggerActions
      let supportACC = this.$storage.supportACC;
      if (!supportACC) {
        this.triggerActions[2].disabled = true;
      }

      let params = {
        currentFrameType: "",
      };
      if (channelInfo && channelInfo.agreementType != 0) {
        // 帧类型
        this.frameType = channelInfo.agreementName;
        params.currentFrameType = channelInfo.agreementName;
        // 备份帧类型
        this.broadcastDataBak.frameType = this.frameType;
        if (this.frameType == "Coreaiot") {
          this.iBeaconBroadcastData[0] = parseInt(channelInfo.agreementData[0]);
        } else if (
          this.frameType == "UID" ||
          this.frameType == "URL" ||
          this.frameType == "LINE" ||
          this.frameType == "Quuppa"
        ) {
          this.iBeaconBroadcastData = channelInfo.agreementData;
        } else if (this.frameType == "iBeacon") {
          this.iBeaconBroadcastData = channelInfo.agreementData;
          if (this.iBeaconBroadcastData.length > 3) {
            this.responseSwitch = this.iBeaconBroadcastData[3] == 1;
            // 备份deviceName
            this.broadcastDataBak.responseSwitch = this.responseSwitch;
          }
          if (this.iBeaconBroadcastData.length > 4) {
            // 备份deviceName
            this.broadcastDataBak.deviceName = this.deviceName;
            this.deviceName = this.iBeaconBroadcastData[4];
          }
        } else if (this.frameType == "DeviceInfo") {
          this.iBeaconBroadcastData = channelInfo.agreementData;
          this.deviceName = this.iBeaconBroadcastData[0];
          // 备份deviceName
          this.broadcastDataBak.deviceName = this.deviceName;
        }

        // 备份广播数据
        this.broadcastDataBak.iBeaconBroadcastData = this.iBeaconBroadcastData;

        // 始终广播
        this.alwaysBroadcastValue = channelInfo.alwaysBroadcast;
        // 广播间隔
        this.broadcastSliderValue = channelInfo.broadcastInterval;
        // 校准距离
        this.calibrationDistanceValue =
          channelInfo.calibrationDistance == 0
            ? 0
            : channelInfo.calibrationDistance - 0xff - 1;
        // 广播功率
        this.broadcastPowerValue = channelInfo.broadcastPowerValue * 10;
        this.broadcastPowerPageValue = this.$storage.getBroadcastValue(
          channelInfo.broadcastPowerValue
        );
        // 触发器开关
        this.triggerSwitch = channelInfo.triggerSwitch;
        if (this.triggerSwitch) {
          this.triggerCondition =
            this.triggerActions[
              channelInfo.triggerCondition <= 0
                ? 1
                : channelInfo.triggerCondition - 1
            ].name;
        } else {
          this.triggerCondition = this.triggerActions[0].name;
        }

        // 触发时间
        this.triggerBroadcastTimeValue =
          channelInfo.triggerTime == 0 ? 1 : channelInfo.triggerTime;
        // 广播功率
        this.triggerBroadcastPowerValue =
          channelInfo.triggerBroadcastPowerValue * 10;
        this.triggerBroadcastPowerPageValue = this.$storage.getBroadcastValue(
          channelInfo.triggerBroadcastPowerValue
        );
        // 广播间隔
        this.triggerBroadcastSliderValue = channelInfo.txInterval
          ? channelInfo.txInterval
          : 100;
      } else {
        this.broadcastPowerPageValue = 85;
        this.triggerBroadcastPowerPageValue = 85;
        this.triggerCondition = this.$i18n.t("baseButton.doubleClick");
      }

      // 通知下拉框
      deviceDetailHelper.getFrameTypeDropDown(params).then((data) => {
        this.frameTypeActions = data;
      });
    },

    callJs(e) {
      let result = e.data;
      let content = result.data;
      if (result.eventName == "CONNECT_STATUS_CHANGE") {
        Notify({
          type: "danger",
          message: this.$i18n.t("notifyMessage.base.bluetoothDisconnected"),
        });
      } else if (result.eventName == instruct.NOTIFY_STATUS_CHANGE) {
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
                this.goBack("SUCCESS");
              }
              break;
          }
        }
      }
    },

    // 帧类型选择
    frameTypeOnSelect(e) {
      this.frameType = e.name;
      let type = this.broadcastDataBak.frameType;
      if (type && e.name == type) {
        if (type == "iBeacon") {
          this.responseSwitch = this.broadcastDataBak.responseSwitch;
          this.deviceName = this.broadcastDataBak.deviceName;
        } else if (type == "DeviceInfo") {
          this.deviceName = this.broadcastDataBak.deviceName;
        }
        this.iBeaconBroadcastData = this.broadcastDataBak.iBeaconBroadcastData;
      } else if (this.frameType == "URL") {
        // url需要赋值前后缀
        this.iBeaconBroadcastData[0] = "http://www.";
        this.iBeaconBroadcastData[1] = "";
        this.iBeaconBroadcastData[2] = ".com";
      } else if (this.frameType == "Coreaiot") {
        this.iBeaconBroadcastData[0] = 0;
      } else {
        this.iBeaconBroadcastData = ["", "", "", ""];
      }
    },

    // 帧类型选择
    triggerConditionOnSelect(e) {
      this.triggerCondition = e.name;
    },

    // 广播间隔滑块
    broadcastSliderChange() {},

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
      // 如果帧类型是Beacon 校验一下response开关 设置设备名称
      if (this.frameType == "iBeacon") {
        this.saveForm.responseSwitch = this.responseSwitch;
        if (this.responseSwitch) {
          this.saveForm.deviceName = this.deviceName;
        }
      } else if (this.frameType == "DeviceInfo") {
        this.saveForm.deviceName = this.deviceName;
      }

      if (this.iBeaconBroadcastData && this.iBeaconBroadcastData.length > 0) {
        this.saveForm.broadcastData = JSON.stringify(this.iBeaconBroadcastData);
      } else {
        this.saveForm.broadcastData = null;
      }

      this.saveForm.channelNumber = this.channelNumber;
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
        this.saveForm.triggerCondition = 1;
      }

      this.$androidApi.saveChannelConfig(this.saveForm).catch((errorMsg) => {
        Notify({
          type: "warning",
          message: errorMsg,
        });
      });
    },

    /**
     * 取消配置
     */
    cancelConfig() {
      this.goBack();
    },

    /**
     * 参数校验
     */
    paramsCheck() {
      if (this.frameType == "iBeacon") {
        if (
          (this.iBeaconBroadcastData.length > 0 &&
            !this.iBeaconBroadcastData[0]) ||
          this.iBeaconBroadcastData[0].length != 32
        ) {
          Notify({
            type: "warning",
            message: this.i18nInfo.tips.uuidFormatError,
          });
          return false;
        }

        if (
          this.iBeaconBroadcastData.length > 1 &&
          this.iBeaconBroadcastData[1].length == 0
        ) {
          Notify({
            type: "warning",
            message: this.i18nInfo.tips.majorFormatError,
          });
          return false;
        }

        if (
          this.iBeaconBroadcastData.length > 2 &&
          this.iBeaconBroadcastData[2].length == 0
        ) {
          Notify({
            type: "warning",
            message: this.i18nInfo.tips.minorFormatError,
          });
          return false;
        }

        if (
          this.responseSwitch &&
          (!this.deviceName || this.deviceName.length != 6)
        ) {
          Notify({
            type: "warning",
            message: this.i18nInfo.tips.deviceNameFormatError,
          });
          return false;
        }
      } else if (this.frameType == "UID") {
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

    uuidInput(value) {
      let codeReg = new RegExp("[A-Fa-f0-9]+"); //正则 英文+数字；
      let len = value.length;
      let str = "";
      for (var i = 0; i < len; i++) {
        if (codeReg.test(value[i])) {
          str += value[i];
        }
      }
      if (str) {
        str = str.toUpperCase();
      }
      this.iBeaconBroadcastData[0] = str;
    },

    majorInput(value) {
      if (!value) {
        this.iBeaconBroadcastData[1] = "";
      } else if (value < 0) {
        this.iBeaconBroadcastData[1] = 0;
      } else if (value > 65535) {
        this.iBeaconBroadcastData[1] = 65535;
      } else {
        this.iBeaconBroadcastData[1] = parseInt(value);
      }
    },

    minorInput(value) {
      if (!value) {
        this.iBeaconBroadcastData[2] = "";
      } else if (value < 0) {
        this.iBeaconBroadcastData[2] = 0;
      } else if (value > 65535) {
        this.iBeaconBroadcastData[2] = 65535;
      } else {
        this.iBeaconBroadcastData[2] = parseInt(value);
      }
    },

    deviceNameInput(value) {
      let codeReg = new RegExp("[A-Za-z0-9]+"); //正则 英文+数字；
      let len = value.length;
      let str = "";
      for (var i = 0; i < len; i++) {
        if (codeReg.test(value[i])) {
          str += value[i];
        }
      }

      this.deviceName = str;
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
  },
};
</script>
<style lang='scss' scoped>
.content {
  .content-box {
    height: 84vh;
    overflow: auto;
    border-radius: 0.1rem 0.1rem 0.1rem 0.1rem;
  }
  .van-cell__value {
    font-size: 0.3rem;
    font-family: PingFang SC-Regular, PingFang SC;
    font-weight: 400;
    color: #000000;
  }

  .little-margin-top {
    margin-top: var(--little-margin-top);
  }

  .beacon-info-box {
    .uuid-box {
      font-size: 0.23rem;
    }
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
      width: 1.5rem;
    }
    .slider {
      margin: 0 0.32rem;
    }
    .unit {
      width: 1.4rem;
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
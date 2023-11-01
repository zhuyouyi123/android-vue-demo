<!-- 设备详情 -->
<template>
  <div>
    <nav-bar>
      <van-icon name="arrow-left" slot="left" class="navIcon" @click="goBack" />
      <div slot="title">{{ $t("pageTitle.deviceDetail") }}</div>
      <van-icon
        :name="
          bluetoothConnectStatus == 1
            ? require('@/assets/image/ble/connecting.svg')
            : bluetoothConnectStatus == 2
            ? require('@/assets/image/ble/connected.svg')
            : require('@/assets/image/ble/disconnected.svg')
        "
        slot="right"
        class="navIcon"
      />
    </nav-bar>
    <div class="content">
      <div class="info-box">
        <div class="base-info-box">
          <div class="box-title">
            {{ $t("device.detail.title.basicInformation") }}
          </div>
          <van-cell-group inset>
            <van-field
              input-align="right"
              label-width="3.5rem"
              v-model="address"
              disabled
              label="Mac"
              placeholder="Mac"
            />

            <van-field
              input-align="right"
              label-width="3.5rem"
              v-model="deviceInfo.factoryVersionInfo.model"
              disabled
              :label="$t('device.detail.lable.model')"
              :placeholder="$t('device.detail.lable.model')"
            />
            <van-field
              input-align="right"
              label-width="3.8rem"
              disabled
              v-model="deviceInfo.factoryVersionInfo.softwareVersion"
              :label="$t('device.detail.lable.softwareVersion')"
              :placeholder="$t('device.detail.lable.softwareVersion')"
            />
            <van-field
              input-align="right"
              label-width="3.6rem"
              disabled
              v-model="deviceInfo.factoryVersionInfo.hardwareVersion"
              :label="$t('device.detail.lable.hardwareVersion')"
              :placeholder="$t('device.detail.lable.hardwareVersion')"
            />
            <van-field
              input-align="right"
              label-width="3.8rem"
              disabled
              v-model="deviceInfo.factoryVersionInfo.firmwareVersion"
              :label="$t('device.detail.lable.firmwareVersion')"
              :placeholder="$t('device.detail.lable.firmwareVersion')"
            />
          </van-cell-group>
        </div>

        <div class="special-info-box">
          <div class="box-title">
            {{ $t("device.detail.title.specialInformation") }}
          </div>
          <van-cell-group inset>
            <van-field
              input-align="right"
              label-width="3.8rem"
              v-model="deviceInfo.featureInfo.channelNum"
              disabled
              :label="$t('device.detail.lable.numberOfChannels')"
              :placeholder="$t('device.detail.lable.numberOfChannels')"
            />
            <van-field
              class="field-textarea"
              label-width="2.5rem"
              v-model="deviceInfo.featureInfo.supportPower"
              type="textarea"
              rows="4"
              disabled
              :label="$t('device.detail.lable.supportPower')"
              :placeholder="$t('device.detail.lable.supportPower')"
            />
            <van-field
              class="field-textarea"
              label-width="2.5rem"
              v-model="deviceInfo.featureInfo.supportAgreement"
              type="textarea"
              rows="3"
              disabled
              :label="$t('device.detail.lable.supportData')"
              :placeholder="$t('device.detail.lable.supportData')"
            />
          </van-cell-group>
        </div>
      </div>

      <!-- 按钮组 -->
      <div class="function-button">
        <div class="function">
          <div class="logo">
            <van-image
              :src="require('@/assets/image/device/detail/can-connect.png')"
              @click="canConncetConfig"
              alt=""
            />
          </div>
          <div class="name">
            {{ $t("device.detail.button.canConncet") }}
            <div class="cann-connect-open" v-show="canConncetSwitch">
              {{ $t("device.detail.button.open") }}
            </div>
            <div class="cann-connect-close" v-show="!canConncetSwitch">
              {{ $t("device.detail.button.close") }}
            </div>
          </div>
        </div>
        <div class="function">
          <div class="logo">
            <van-image
              :src="
                require('@/assets/image/device/detail/restore-factory-settings.png')
              "
              @click="restoreFactorySettings"
              alt=""
            />
          </div>
          <div class="name">
            {{ $t("device.detail.button.restoreFactorySettings") }}
          </div>
        </div>
        <div class="function">
          <div class="logo">
            <van-image
              @click="shoudown"
              :src="require('@/assets/image/device/detail/shutdown.png')"
              alt=""
            />
          </div>
          <div class="name">{{ $t("device.detail.button.shutdown") }}</div>
        </div>
        <div class="function" @click="jumpToPage('configureChannel')">
          <div class="logo">
            <van-image
              :src="
                require('@/assets/image/device/detail/configure-channel.png')
              "
              alt=""
            />
          </div>
          <div class="name">
            {{ $t("device.detail.button.configureChannel") }}
          </div>
        </div>
        <div class="function" v-show="needSecretKey">
          <div class="logo">
            <van-image
              :src="
                require('@/assets/image/device/detail/remove-secret-key.png')
              "
              @click="removeSecretKey"
              alt=""
            />
          </div>
          <div class="name">
            {{ $t("device.detail.button.removeSecretKey") }}
          </div>
        </div>
        <div class="function" v-show="needSecretKey">
          <div class="logo">
            <van-image
              :src="
                require('@/assets/image/device/detail/update-secret-key.png')
              "
              @click="openSecretKeyDialog"
              alt=""
            />
          </div>
          <div class="name">
            {{ $t("device.detail.button.updateSecretKey") }}
          </div>
        </div>
        <!-- 添加秘钥 -->
        <div class="function" v-if="!needSecretKey">
          <div class="logo">
            <van-image
              :src="require('@/assets/image/device/detail/add-secret-key.png')"
              @click="openSecretKeyDialog"
              alt=""
            />
          </div>
          <div class="name">
            {{ $t("device.detail.button.addSecretKey") }}
          </div>
        </div>
        <!-- 触发响应时间 -->
        <div class="function">
          <div class="logo">
            <van-image
              :src="
                require('@/assets/image/device/detail/trigger-response-time.png')
              "
              @click="keyTiggeredResponseDialog = true"
              alt=""
            />
          </div>
          <div class="name">
            {{ $t("device.detail.button.triggerResponseTime") }}
          </div>
        </div>
      </div>

      <!-- 遮罩层 -->
      <van-overlay :show="loading">
        <div class="wrapper" @click.stop>
          <van-circle
            v-model="currentRate"
            :speed="30"
            size="2.7rem"
            :rate="rate"
            :text="text"
            layer-color="#ebedf0"
          />
          <div class="rate">
            {{ rate + "%" }}
          </div>
        </div>
      </van-overlay>

      <van-overlay :show="functionLoading">
        <van-loading color="#0094ff" size="24px">loading...</van-loading>
      </van-overlay>

      <!-- 弹窗 -->
      <van-dialog
        v-model="secretKeyDialog"
        :title="i18nInfo.function.inputSecretKey"
        show-cancel-button
        :cancel-button-text="$t('baseButton.cancel')"
        :confirm-button-text="$t('baseButton.sure')"
        :before-close="secretKeyDialogBeforeClose"
        theme="round-button"
      >
        <van-field
          maxlength="6"
          v-model.trim="secretKey"
          :placeholder="i18nInfo.function.secretDialogPlaceholder"
        />
      </van-dialog>

      <van-dialog
        v-model="keyTiggeredResponseDialog"
        :title="i18nInfo.function.keyTiggeredResponseTime"
        show-cancel-button
        :cancel-button-text="$t('baseButton.cancel')"
        :confirm-button-text="$t('baseButton.sure')"
        :before-close="tiggeredResponseDialogBeforeClose"
        theme="round-button"
      >
        <van-field
          :label="i18nInfo.lable.responseTime + '(S)'"
          type="digit"
          maxlength="2"
          @input="keyTiggeredResponseTimeInput"
          v-model="keyTiggeredResponseTime"
          :placeholder="i18nInfo.function.keyTiggeredResponseTime"
          clearable
        />
        <div class="tips-explain">
          {{ i18nInfo.tips.responseTimeTips }}
        </div>
      </van-dialog>

      <!-- 校验秘钥 -->
      <van-dialog
        v-model="secretKeyDialog"
        :title="i18nInfo.function.inputSecretKey"
        show-cancel-button
        :cancel-button-text="$t('baseButton.cancel')"
        :confirm-button-text="$t('baseButton.sure')"
        :before-close="secretKeyDialogBeforeClose"
        theme="round-button"
      >
        <van-field
          maxlength="6"
          v-model.trim="secretKey"
          :placeholder="i18nInfo.function.secretDialogPlaceholder"
        />
      </van-dialog>
    </div>
  </div>
</template>

<script>
import deviceDetail from "@/views/home/children/deviceDetail";
export default deviceDetail;
</script>
<style lang='scss' scoped>
.content {
  overflow: auto;

  .info-box {
    height: 8.5rem;
    overflow: auto;
  }

  .rate {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: 0.1rem;
    font-size: 0.4rem;
  }
  // 基础信息
  .base-info-box {
    background: #ffffff;
    box-shadow: 0rem 0.04rem 0.06rem 0.01rem rgba(0, 0, 0, 0.1);
    border-radius: 0.1rem 0.1rem 0.1rem 0.1rem;
  }

  .special-info-box {
    background: #ffffff;
    box-shadow: 0rem 0.04rem 0.06rem 0.01rem rgba(0, 0, 0, 0.1);
    border-radius: 0.1rem;
    margin-top: 0.16rem;
  }

  .function-button {
    margin-top: 0.29rem;
    margin-bottom: 0.29rem;
    display: flex;
    flex-wrap: wrap;
    .function {
      width: 33.33%;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-direction: column;
      &:nth-child(n + 4) {
        margin-top: 0.36rem;
      }
      .logo {
        width: 1rem;
        height: 1rem;
        background: #ffffff;
        box-shadow: 0rem 0.04rem 0.06rem 0.01rem rgba(0, 0, 0, 0.1);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        .van-image {
          width: 0.5rem;
        }
      }
      .name {
        margin-top: 0.1rem;
        height: 0.45rem;
        font-size: 0.28rem;
        font-family: PingFang SC-Regular, PingFang SC;
        font-weight: 400;
        line-height: 0.39rem;
        display: flex;
        align-items: center;
        text-align: center;
        .cann-connect-open {
          color: #007fff;
        }
        .cann-connect-close {
          color: #cc0000;
        }
      }
    }
  }

  .tips-explain {
    font-size: 0.25rem;
    margin: auto;
    width: 80%;
  }
}
</style>
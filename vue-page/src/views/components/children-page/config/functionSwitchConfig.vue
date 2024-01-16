<!--
* @Component: 
* @Maintainer: 
* @Description: 功能开关配置
-->
<template>
  <div>
    <custom-nav-bar
      ref="customNavBar"
      title="功能开关配置"
      :returnRouter="$deviceHolder.mineReturnRouter"
      left-arrow
    >
    </custom-nav-bar>

    <div class="page-content config-page">
      <van-cell-group inset v-if="!editStatus">
        <van-cell
          title="手环未连接或者数据获取失败"
          label="开关获取存在延迟，请稍等或重新连接手环"
        >
          <van-image
            slot="icon"
            :src="require('../../../../assets/image/mine/config/tips-icon.svg')"
          ></van-image>
        </van-cell>
      </van-cell-group>

      <van-cell-group inset>
        <van-cell
          center
          title="全天血氧监控"
          label="全天监控会增加手表耗电，减少使用时间"
        >
          <template #right-icon>
            <van-switch
              :disabled="!editStatus"
              v-model="form.bloodOxygen"
              @change="stateChange('bloodOxygen')"
              size="24"
            />
          </template>
        </van-cell>

        <van-cell title="监控间隔（分钟）" value="">
          <template #right-icon>
            <van-stepper
              :disabled="!editStatus"
              v-model="form.bloodOxygenInterval"
              :min="5"
              :max="30"
              :step="5"
            />
          </template>
        </van-cell>
        <van-cell
          center
          title="全天血压监控"
          label="全天监控会增加手表耗电，减少使用时间"
        >
          <template #right-icon>
            <van-switch
              :disabled="!editStatus"
              v-model="form.bloodPressure"
              size="24"
            />
          </template>
        </van-cell>
        <van-cell title="监控间隔（分钟）" value="">
          <template #right-icon>
            <van-stepper
              :disabled="!editStatus"
              v-model="form.bloodPressureInterval"
              :min="5"
              :max="30"
              :step="5"
            />
          </template>
        </van-cell>
      </van-cell-group>

      <van-button class="save" type="primary" round @click="save"
        >保存</van-button
      >
    </div>
  </div>
</template>

<script>
import customNavBar from "@/views/components/custom/customNavBar";
import { Toast } from "vant";
export default {
  name: "functionSwitchConfig",
  data() {
    return {
      editStatus: false,
      form: {
        bloodPressureInterval: 5,
        bloodPressure: false,
        bloodOxygenInterval: 5,
        bloodOxygen: 5,
      },
    };
  },
  mounted() {
    window.addEventListener("commonAndroidEvent", this.callJs);
    this.$deviceHolder.routerPath = "mine";
    this.queryFunctionSwitch();
  },
  destroyed() {
    window.removeEventListener("commonAndroidEvent", this.callJs);
  },
  methods: {
    callJs(e) {
      console.log("12123242323");
      console.log("callJs", JSON.stringify(e));
      let eventName = e.data.eventName;
      let data = e.data.data;
      if (eventName == "FUNCTION_SWITCH_KEY") {
        this.editStatus = true;
        this.form = { ...data };
      }
    },
    async queryFunctionSwitch() {
      let deviceInfoData = await this.$androidApi.getDeviceInfo();
      if (!deviceInfoData.connectStatus) {
        return;
      }
      this.$androidApi.queryFunctionSwitch();
    },

    save() {
      if (!this.editStatus) {
        return;
      }
      this.$androidApi.saveFunctionSwitch(this.form).then(() => {
        // this.$refs.customNavBar.clickLeft();
        setTimeout(() => {
          this.queryFunctionSwitch();
        }, 500);
      });
    },
  },
  components: { customNavBar },
};
</script>

<style scoped lang="scss">
.save {
  width: 100%;
  margin-top: 0.5rem;
}

// 配置
.van-cell-group {
  .van-cell {
    display: flex;
    align-items: center;
    .van-image {
      width: 0.63rem;
      height: 0.63rem;
      padding-right: 0.2rem;
    }
  }
}
</style>

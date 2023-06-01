<!-- sdk配置 -->
<template>
  <div>
    <nav-bar>
      <van-icon
        name="arrow-left"
        slot="left"
        class="navIcon"
        @click="clickLeft"
      />

      <!-- <van-icon
        name="completed"
        slot="right"
        class="navIcon"
        @click="onSubmit"
      /> -->
    </nav-bar>
    <van-form @submit="onSubmit">
      <van-field name="stepper" label="扫描等级" input-align="right">
        <template #input>
          <van-stepper
            input-width="1.2rem"
            min="0"
            max="2"
            v-model="filterInfo.scanLevel"
          />
        </template>
      </van-field>

      <van-field name="stepper" label="设备存活时间" input-align="right">
        <template #input>
          <van-stepper
            input-width="1.2rem"
            min="1000"
            max="500000"
            step="1000"
            v-model="filterInfo.deviceSurviveTime"
          />
        </template>
      </van-field>

      <van-field name="stepper" label="持续扫描" input-align="right">
        <template #input>
          <van-stepper
            input-width="1.2rem"
            min="0"
            max="1"
            step="1"
            v-model="filterInfo.continuousScanning"
          />
        </template>
      </van-field>

      <van-field name="stepper" label="间歇扫描间隔" input-align="right">
        <template #input>
          <van-stepper
            input-width="1.2rem"
            min="500"
            max="5000"
            step="100"
            v-model="filterInfo.intermittentTime"
          />
        </template>
      </van-field>

      <van-field name="stepper" label="通信加密开关" input-align="right">
        <template #input>
          <van-stepper
            input-width="1.2rem"
            min="0"
            max="1"
            step="1"
            v-model="filterInfo.communicationEncryption"
          />
        </template>
      </van-field>

      <div style="margin: 16px">
        <van-button round block type="primary" native-type="submit">
          保存
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script>
import navBar from "@/components/navigation/navBar.vue";

import {
  DropdownMenu,
  DropdownItem,
  Notify,
  Form,
  CellGroup,
  Cell,
} from "vant";
export default {
  data() {
    return {
      filterInfo: {
        continuousScanning: false,
        deviceSurviveTime: 10000,
        intermittentScanning: true,
        intermittentTime: 500,
        normDevice: false,
        rssi: -100,
        scanLevel: 2,
        scanPeriod: 10000,
        supportConnectable: false,
        communicationEncryption: 0,
      },
    };
  },

  components: {
    [Form.name]: Form,
    [CellGroup.name]: CellGroup,
    [Cell.name]: Cell,
    [DropdownMenu.name]: DropdownMenu,
    [DropdownItem.name]: DropdownItem,
    navBar: navBar,
  },

  mounted() {
    this.queryFilterInfo();
  },

  methods: {
    clickLeft() {
      this.$router.replace("/home");
    },

    queryFilterInfo() {
      this.$androidApi.queryFilterInfo().then((data) => {
        this.filterInfo = data;
        if (data.continuousScanning) {
          this.filterInfo.continuousScanning = 1;
        } else {
          this.filterInfo.continuousScanning = 0;
        }
      });
    },

    onSubmit() {
      this.$androidApi.saveFilterInfo(this.filterInfo).then(() => {
        Notify({ type: "success", message: "配置修改成功" });
        setTimeout(() => {
          this.clickLeft();
        }, 10);
      });
    },
  },
};
</script>
<style lang='scss' scoped>
</style>
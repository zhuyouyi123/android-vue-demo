<!--
短信配置
-->
<template>
  <div>
    <custom-nav-bar
      :returnRouter="$deviceHolder.mineReturnRouter"
      left-arrow
      title="来电提醒"
    />
    <div class="page-content">
      <van-cell-group inset>
        <van-cell title="手机需要一直连接手环" label="请保持蓝牙开启">
          <van-image
            slot="icon"
            :src="require('../../../../assets/image/mine/config/tips-icon.svg')"
          ></van-image>
        </van-cell>
      </van-cell-group>
      <van-cell-group inset v-if="!notificationPermissions">
        <van-cell
          title="请开启通知权限"
          size="large"
          @click="requestContactsPermission"
          is-link
        >
          <van-image
            slot="icon"
            :src="
              require('../../../../assets/image/mine/config/warning-icon.svg')
            "
          ></van-image>
        </van-cell>
      </van-cell-group>

      <van-cell-group inset class="l-m-t">
        <van-cell
          center
          title="开启手机短信提醒"
          label="手机收到短信时，手环会进行提醒"
        >
          <template #right-icon>
            <van-switch @change="stateChange()" v-model="smsEnable" size="24" />
          </template>
        </van-cell>
      </van-cell-group>
    </div>
  </div>
</template>

<script>
import customNavBar from "@/views/components/custom/customNavBar";
import debounce from "@/utils/debounce";
import { Dialog } from "vant";
export default {
  name: "sms",
  components: { customNavBar, [Dialog.Component.name]: Dialog.Component },
  data() {
    return {
      smsEnable: false,
      notificationPermissions: false,
      configKey: "SMS",
      pageGroup: "NOTIFICATION",
      permissionInterval: null,
      returnRouter: {
        path: "/layout/index",
        query: { active: 2 },
      },
    };
  },
  mounted() {
    this.queryPermissionExist();
    this.queryNotificationConfig();
    this.permissionCheck();
  },
  methods: {
    permissionCheck() {
      if (!this.notificationPermissions) {
        this.permissionInterval = setInterval(() => {
          this.queryPermissionExist();
        }, 3000);
      }
    },
    // 查询
    queryPermissionExist() {
      this.$androidApi.queryPermissionExist(this.pageGroup).then((data) => {
        this.notificationPermissions = data;
        if (this.notificationPermissions && this.permissionInterval) {
          clearInterval(this.permissionInterval);
        }
      });
    },

    // 查询配置
    queryNotificationConfig() {
      this.$androidApi
        .queryConfigurationByGroup(this.pageGroup)
        .then((data) => {
          data.forEach((e) => {
            if (e.type == this.configKey) {
              this.smsEnable = e.value == 1;
            }
          });
        });
    },

    stateChange() {
      let params = {
        type: this.configKey,
        value: this.smsEnable ? 1 : 0,
        group: this.pageGroup,
      };
      this.$androidApi.updateConfigStatus(params).then(() => {
        this.queryNotificationConfig();
      });
    },
    /**
     * 申请权限
     */
    requestContactsPermission() {
      Dialog.alert({
        title: "提示",
        confirmButtonText: "知道了",
        showCancelButton: true,
        message: "请开启通知读取权限",
      }).then(() => {
        // on close
        this.$androidApi.requestPermission(this.pageGroup);
      });
    },
  },
};
</script>

<style scoped lang="scss">
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

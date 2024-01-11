<!-- 提醒配置 -->
<template>
  <div>
    <custom-nav-bar
      left-arrow
      title="APP通知提醒"
      :returnRouter="$deviceHolder.mineReturnRouter"
    ></custom-nav-bar>
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
          title="开启APP通知提醒"
          label="当手机通知栏收到APP推送消息时，手环提醒"
        >
          <template #right-icon>
            <van-switch
              @change="stateChange()"
              v-model="notifyEnable"
              size="24"
            />
          </template>
        </van-cell>
      </van-cell-group>

      <van-cell-group inset class="l-m-t">
        <van-cell center is-link title="管理App" @click="toManagerAppPage">
        </van-cell>
      </van-cell-group>
    </div>
  </div>
</template>

<script>
import customNavBar from "@/views/components/custom/customNavBar";
import { Dialog, Toast } from "vant";
export default {
  components: { customNavBar, [Dialog.Component.name]: Dialog.Component },
  data() {
    return {
      // 开启通知
      notifyEnable: false,
      configKey: "NOTIFY",
      pageGroup: "NOTIFICATION",
      notificationPermissions: false,
      permissionInterval: null,
      returnRouter: {
        path: "/layout/index",
        query: { active: 2 },
      },
      firstTips: true,
    };
  },

  mounted() {
    this.$deviceHolder.routerPath = "mine";
    this.queryPermissionExist();
    this.queryNotificationConfig();
    this.start();
  },
  destroyed() {
    if (this.permissionInterval) {
      clearInterval(this.permissionInterval);
    }
  },

  methods: {
    start() {
      if (!this.notificationPermissions) {
        this.permissionInterval = setInterval(() => {
          this.queryPermissionExist();
        }, 3000);
      }
    },
    stateChange() {
      if (!this.notificationPermissions) {
        this.notifyEnable = !this.notifyEnable;
        this.requestContactsPermission();
        return;
      }
      let params = {
        type: this.configKey,
        value: this.notifyEnable ? 1 : 0,
        group: this.pageGroup,
      };
      this.$androidApi.updateConfigStatus(params).then(() => {
        this.queryNotificationConfig();
      });
    },

    // 查询
    queryPermissionExist() {
      this.$androidApi.queryPermissionExist(this.pageGroup).then((data) => {
        this.notificationPermissions = data;
        if (this.notificationPermissions) {
          if (this.permissionInterval) {
            clearInterval(this.permissionInterval);
          }
        } else {
          if (this.firstTips) {
            this.firstTips = false;
            this.requestContactsPermission();
          }
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
              this.notifyEnable = e.value == 1;
              if (this.notifyEnable && this.notificationPermissions) {
                Toast({ message: "可通过管理App添加应用", position: "bottom" });
              }
            }
          });
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
        this.$androidApi.requestPermission(this.pageGroup);
      });
    },
    toManagerAppPage() {
      Toast.loading({
        message: "加载中...",
        forbidClick: true,
        loadingType: "spinner",
      });
      setTimeout(() => {
        this.$androidApi.openManagerAppPage();
        Toast.clear();
      }, 200);
    },
  },
};
</script>
<style lang="scss" scoped>
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

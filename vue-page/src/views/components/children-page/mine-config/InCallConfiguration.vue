<!-- 提醒配置 -->
<template>
  <div>
    <custom-nav-bar
    :returnRouter="$deviceHolder.mineReturnRouter"
      left-arrow
      title="来电提醒"
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
          title="开启电话和读取通话记录权限"
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

      <van-cell-group inset>
        <van-cell
          center
          title="开启来电提醒"
          label="手机来电时，手表会进行提醒"
        >
          <template #right-icon>
            <van-switch
              v-model="inCallChecked"
              @change="stateChange(inCallCheckedKey)"
              size="24"
            />
          </template>
        </van-cell>

        <van-cell
          center
          title="显示联系人信息"
          label="手机有来电，手环会显示姓名或号码"
        >
          <template #right-icon>
            <van-switch
              v-model="showCallInfo"
              @change="stateChange(showCallInfoKey)"
              size="24"
            />
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
  data() {
    return {
      pageGroup: "NOTIFICATION",
      // 开启来电显示
      inCallChecked: false,
      inCallCheckedKey: "IN_CALL",
      // 显示联系人信息
      showCallInfo: false,
      showCallInfoKey: "IN_CALL_CONTACTS",
      debouncedConfig: debounce((item) => this.updateState(item), 200),
      // tongzhi 权限
      notificationPermissions: false,
      permissionInterval: null,
      returnRouter: {
        path: "/layout/index",
        query: { active: 2 },
      },
    };
  },

  components: { customNavBar, [Dialog.Component.name]: Dialog.Component },

  mounted() {
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
    // 查询
    queryPermissionExist() {
      this.$androidApi.queryPermissionExist("READ_PHONE_STATE").then((data) => {
        this.notificationPermissions = data;
        if (this.notificationPermissions && this.permissionInterval) {
          clearInterval(this.permissionInterval);
        }
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
        message: "请开启电话和读取通话记录权限",
      }).then(() => {
        // on close
        this.$androidApi.requestPermission("READ_PHONE_STATE");
      });
    },

    // 查询配置
    queryNotificationConfig() {
      this.$androidApi
        .queryConfigurationByGroup(this.pageGroup)
        .then((data) => {
          console.log(JSON.stringify(data));
          data.forEach((e) => {
            if (e.type == "IN_CALL") {
              this.inCallChecked = e.value == 1;
            } else if (e.type == "IN_CALL_CONTACTS") {
              this.showCallInfo = e.value == 1;
            }
          });
        });
    },

    stateChange(type) {
      this.debouncedConfig(type);
    },

    updateState(type) {
      let enable = 0;
      if (type == this.inCallCheckedKey) {
        enable = this.inCallChecked ? 1 : 0;
      } else if (type == this.showCallInfoKey) {
        enable = this.showCallInfo ? 1 : 0;
      }

      let params = {
        type: type,
        value: enable,
        group: this.pageGroup,
      };
      this.$androidApi.updateConfigStatus(params).then(() => {
        this.queryNotificationConfig();
      });
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

<!-- 导航栏 -->
<template>
  <div>
    <van-nav-bar fixed z-index="5">
      <slot slot="left" name="left"></slot>
      <slot slot="title" name="title">
        <van-image
          @click="clg"
          :src="require('@/assets/system/logo.png')"
          alt=""
        />
      </slot>
      <slot slot="right" name="right"></slot>
    </van-nav-bar>
    <div>
      <van-action-sheet
        title="APP类型"
        v-model="appTypeSelectShow"
        :actions="[
          { name: '信标配置工具' },
          { name: '批量关机工具' },
          { name: '扫描测试工具' },
        ]"
        @select="onSelect"
        :close-on-click-overlay="false"
      />

      <van-dialog
        v-model="passwordDialogShow"
        title="请输入密码"
        show-cancel-button
        cancel-button-text="取消"
        confirm-button-text="确定"
        :before-close="passwordDialogBeforeClose"
        theme="round-button"
      >
        <van-field
          maxlength="6"
          v-model.trim="password"
          placeholder="请输入密码"
          clearable
        />
      </van-dialog>
    </div>
  </div>
</template>

<script >
import { NavBar, Toast, Icon, Dialog, Notify, ActionSheet } from "vant";
export default {
  data() {
    return {
      num: 0,
      password: null,
      appType: "CONFIG",
      // APP类型选择弹窗
      appTypeSelectShow: false,
      // 密码输入弹窗
      passwordDialogShow: false,
      changeParams: {
        type: "",
        path: "/home",
      },
    };
  },

  components: {
    [NavBar.name]: NavBar,
    [Icon.name]: Icon,
    [ActionSheet.name]: ActionSheet,
    [Toast.name]: Toast,
  },

  mounted() {},

  methods: {
    clg() {
      this.num++;
      console.log(this.num);
      setTimeout(() => {
        if (this.num > 0) {
          this.num = 0;
        }
      }, 5000);
      if (this.num >= 8) {
        let params = {
          key: "APP_TYPE",
        };

        this.$androidApi.shareGet(params).then((data) => {
          this.appType = this.$config.appType;
          if (data) {
            this.appType = data;
          }
          this.appTypeSelectShow = true;
        });
      }
    },

    onSelect(e) {
      this.changeParams.type = "CONFIG";
      this.changeParams.path = "/home";
      if (e.name == "信标配置工具") {
        this.changeParams.type = "CONFIG";
        this.changeParams.path = "/home";
      } else if (e.name == "批量关机工具") {
        this.changeParams.type = "SHUTDOWN";
        this.changeParams.path = "/home1";
      } else if (e.name == "扫描测试工具") {
        this.changeParams.type = "SCAN";
        this.changeParams.path = "/home2";
      }

      if (this.changeParams.type != this.appType) {
        Dialog.confirm({
          title: "更换工具类型",
          message: `确认将APP模式切换成${e.name}吗？`,
          messageAlign: "left",
          showCancelButton: "true",
        })
          .then(() => {
            this.passwordDialogShow = true;
            this.num = 0;
          })
          .catch(() => {
            this.num = 0;
          });
      }
      this.appTypeSelectShow = false;
    },

    passwordDialogBeforeClose(action, done) {
      if (action == "confirm") {
        if (this.password != 666666) {
          done(false);
          Notify({ type: "danger", message: "密码错误，请重新输入" });
          return;
        }
        this.$androidApi.shareSet({
          key: "APP_TYPE",
          value: this.changeParams.type,
        });
        this.$router.replace(this.changeParams.path);
      }
      this.password = null;
      done();
    },
  },
};
</script>
<style lang='scss' scoped>
.van-nav-bar {
  overflow: hidden;

  .van-nav-bar__title {
    .van-image {
      margin-top: 0.06rem;
      width: 2.18rem;
    }
  }
}
</style>
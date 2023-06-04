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
  </div>
</template>

<script >
import { NavBar, Toast, Icon, Dialog, Notify } from "vant";
export default {
  data() {
    return {
      num: 0,
    };
  },

  components: {
    [NavBar.name]: NavBar,
    [Icon.name]: Icon,
    [Toast.name]: Toast,
  },

  mounted() {},

  methods: {
    clg() {
      this.num++;
      if (this.num == 10) {
        let params = {
          key: "APP_TYPE",
        };
        this.$androidApi.shareGet(params).then((data) => {
          let type = this.$config.appType;
          let message = "";

          let isConfig = true;
          if (!data) {
            if (type == "CONFIG") {
              message = "确认将APP模式从配置模式切换成批量关机模式吗？";
            } else {
              isConfig = false;
              message = "确认将APP模式从批量关机模式切换成配置模式吗？";
            }
          } else {
            if (data == "CONFIG") {
              message = "确认将APP模式从配置模式切换成批量关机模式吗？";
            } else {
              isConfig = false;
              message = "确认将APP模式从批量关机模式切换成配置模式吗？";
            }
          }
          Dialog.confirm({
            title: "更换工具类型",
            message: message,
            messageAlign: "left",
            showCancelButton: "true",
          })
            .then(() => {
              this.num = 0;
              if (isConfig) {
                this.$androidApi.shareSet({
                  key: "APP_TYPE",
                  value: "SHUTDOWN",
                });
                this.$router.replace("/home1");
              } else {
                this.$androidApi.shareSet({
                  key: "APP_TYPE",
                  value: "CONFIG",
                });
                this.$router.replace("/home");
              }
            })
            .catch(() => {
              this.num = 0;
            });
        });
      }
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
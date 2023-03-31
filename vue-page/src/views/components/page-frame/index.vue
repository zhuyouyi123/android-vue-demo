<template>
  <div class="w h box-b over-h page-frame">
    <div class="page-header">
      <slot name="header">
        <van-nav-bar v-if="showNavBar" class="nav-bar">
          <template #left>
            <slot name="header-left">
              <span class="header-left">
                <i class="el-icon-arrow-left"></i>
                <span class=" " @click="goBack(isBackedReload)">返回</span>
              </span>
            </slot>
          </template>
          <template #title>
            <slot name="header-title">
              <span></span>
            </slot>
          </template>
          <template #right>
            <slot name="header-right">
              <span></span>
            </slot>
            <van-icon
              class="m-l2 m-r2"
              name="bullhorn-o"
              v-if="isDevelopment"
              @click="testPushMessage"
            />
            <van-icon class="m-l2" name="replay" @click="reloadPage" />
            <div v-if="isDevelopment" class="w-20 h-10"></div>
          </template>
        </van-nav-bar>
      </slot>
    </div>
    <div
      class="page-content"
      :style="{ height: `calc(100% - ${showNavBar ? '46px' : '0px'})` }"
    >
      <slot name="content"> </slot>
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: "pageFrame",
  components: {
  },
  computed: {
    isDevelopment() {
      return process.env.NODE_ENV !== "production";
    },
  },
  data() {
    return {};
  },
  methods: {
    /**
     * 测试发送消息
     */
    testPushMessage() {
      this.$ano.requestSync("system/pushMessage", "post", {
        title: "16:57:04测试内推",
        text: "测试消息，成功发送搞一条信息",
      });
    },
    reloadPage() {
      window.location.reload();
      // let href = window.location.href;
      // window.location.href = "";
      // this.$nextTick(() => {
      //   window.location.href = href;
      // });
    },
  },
};
</script>

<style lang="scss" scoped>
.page-frame {
  height: 100%;
  width: 100%;
  .nav-bar {
    background: var(--default-bar-color);
    color: var(--default-font-color);

    .header-left {
      color: var(--default-bar-left-color);
    }
  }

  .page-header {
    // margin-top:20px;
    height: 46px;
    width: 100%;
    box-sizing: border-box;
    color: var(--default-font-color);
  }

  .page-content {
    width: 100%;
    box-sizing: border-box;
    color: var(--default-font-color);
    background: var(--body-background-color);
    overflow: hidden;
  }
}
</style>

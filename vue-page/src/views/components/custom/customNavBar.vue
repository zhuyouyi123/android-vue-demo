<!-- 自定义导航栏 -->
<template>
  <div>
    <van-nav-bar
      :title="title"
      :left-text="leftText"
      :left-arrow="leftArrow"
      safe-area-inset-top
      @click-left="clickLeft"
    >
      <template #right>
        <!-- <van-icon name="search" size="18" /> -->
        <van-image
          v-show="rightIcon"
          :src="rightIcon"
          @click="rightIconClick"
        />
      </template>
    </van-nav-bar>
  </div>
</template>

<script>
export default {
  props: {
    // 标题属性
    title: String,
    // 图标属性
    leftArrow: Boolean,
    // 图标属性
    rightIcon: String,
    leftText: String,
    returnRouter: {
      type: Object,
      default: () => {
        return {
          path: "",
          query: {},
        };
      },
    },
  },
  data() {
    return {
      // returnRouter: {
      //   path: "",
      //   query: {},
      // },
    };
  },

  mounted() {},

  methods: {
    clickLeft() {
      if (!this.returnRouter.path) {
        this.$router.go(-1);
      } else {
        this.$router.replace(this.returnRouter);
        this.returnRouter = {};
      }
    },

    setReturnRouter(params) {
      this.returnRouter = params;
    },

    rightIconClick() {
      this.$emit("rightIconClick");
    },
  },
};
</script>
<style lang="scss" scoped>
.van-image {
  width: 0.45rem;
  height: 0.45rem;
}
</style>

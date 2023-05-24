<!-- 导航栏 -->
<template>
  <div>
    <van-nav-bar fixed z-index="5">
      <slot slot="left" name="left"></slot>
      <slot slot="title" name="title">
        <van-image :src="require('@/assets/system/logo.png')" alt="" />
      </slot>
      <slot slot="right" name="right"></slot>
    </van-nav-bar>
  </div>
</template>

<script >
import { NavBar, Toast, Icon } from "vant";
export default {
  data() {
    return {};
  },

  components: {
    [NavBar.name]: NavBar,
    [Icon.name]: Icon,
    [Toast.name]: Toast,
  },

  mounted() {},

  methods: {},
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
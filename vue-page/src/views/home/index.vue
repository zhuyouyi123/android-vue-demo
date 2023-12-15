<!--  -->
<template>
  <div class="content">
    <div class="index-home">
      <home @updateDeviceInfo="queryRealInfo" v-if="active == 0" />
      <sport v-else-if="active == 2" />
      <mine v-else-if="active == 1" />
    </div>

    <van-tabbar v-model="active" @change="activeChange" active-color="#1DA772">
      <van-tabbar-item icon="home-o">首页</van-tabbar-item>
      <!-- <van-tabbar-item
        :icon="
          require(active == 1
            ? '../../assets/image/index/home-sport-icon-b.svg'
            : '../../assets/image/index/home-user-icon-b.svg')
        "
        >运动</van-tabbar-item
      > -->
      <van-tabbar-item
        :icon="
          require(active == 1
            ? '../../assets/image/index/home-user-icon-b.svg'
            : '../../assets/image/index/home-user-icon-a.svg')
        "
      >
        我的
      </van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script>
import { Toast } from "vant";
import home from "@/views/components/home-page/home";
import sport from "../components/home-page/sport.vue";
import mine from "@/views/components/home-page/mine";
export default {
  data() {
    return { active: 0, realTimeInterval: null };
  },

  components: {
    home,
    mine,
    sport,
  },

  mounted() {
    // 如果是别的页面跳转过来
    if (this.$route.query && this.$route.query.active) {
      this.active = this.$route.query.active;
    }
  },

  destroyed() {
    this.clearIntervals();
  },

  methods: {
    activeChange(e) {},

    /**
     * 初始化定时任务
     */
    initInterval() {},

    /**
     * 清理定时任务
     */
    clearIntervals() {
      if (this.realTimeInterval) {
        clearInterval(this.realTimeInterval);
      }
    },

    queryRealInfo(){
      
    },
  },
};
</script>
<style lang="scss" scoped>
.index-home {
  height: 93vh;
  overflow: auto;
}

.van-tabbar {
  z-index: 10;
}
</style>

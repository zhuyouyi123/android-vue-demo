<!--  -->
<template>
  <div class="content">
    <div class="index-home">
      <home v-if="active == 0" />
      <sport v-if="active == 1" />
      <mine v-else-if="active == 2" />
    </div>

    <van-tabbar v-model="active" @change="activeChange" active-color="#1DA772">
      <van-tabbar-item icon="home-o">首页</van-tabbar-item>
      <van-tabbar-item
        :icon="
          require(active == 1
            ? '../../assets/image/index/home-sport-icon-b.svg'
            : '../../assets/image/index/home-user-icon-b.svg')
        "
        >运动</van-tabbar-item
      >
      <van-tabbar-item
        :icon="
          require(active == 2
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
    return { active: 0 };
  },

  components: {
    home,
    mine,
    sport,
  },

  mounted() {
    this.init();
  },

  methods: {
    activeChange(e) {},
    init() {
      let params = {
        title: "1111",
        text: "你好啊",
      };
      this.$androidApi.init(params);
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

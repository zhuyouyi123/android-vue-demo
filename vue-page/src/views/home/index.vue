<!--  -->
<template>
  <div>
    <van-nav-bar title="标题" />
  </div>
</template>

<script>
import { NavBar } from "vant";
export default {
  data() {
    return {};
  },

  components: {
    [NavBar.name]: NavBar,
  },

  mounted() {
    this.init();
  },

  methods: {
    init() {
      let params={
        title:"1111",
        text:"你好啊"
      }
      this.$androidApi.init(params).then(()=>{
        console.log(1111111);
      });
    },
  },
};
</script>
<style lang='scss' scoped>
</style>
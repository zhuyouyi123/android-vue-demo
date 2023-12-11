<!-- 血氧饱和度 -->
<template>
  <div class="blood-xygen">
    <custom-nav-bar
      :returnRouter="$deviceHolder.homeReturnRouter"
      title="血氧饱和度"
      left-arrow
    >
    </custom-nav-bar>
    <div class="page-content">
      <van-steps active="-1" direction="vertical" inactive-color="#1da772">
        <van-step>
          8月14日 23:55
          <van-cell-group>
            <van-cell class="only-cell" title="90%" value="12:00" />
          </van-cell-group>
        </van-step>

        <van-step>
          8月15日 23:55
          <van-cell-group>
            <van-cell title="90%" value="13:15" />
            <van-cell title="95%" value="13:30" />
            <van-cell title="92%" value="13:55" />
          </van-cell-group>
        </van-step>
        <van-step>
          8月16日 10:55
          <van-cell-group>
            <van-cell title="90%" value="13:55" />
          </van-cell-group>
        </van-step>
      </van-steps>
    </div>
  </div>
</template>

<script>
import customNavBar from "../custom/customNavBar.vue";
export default {
  name: "bloodOxygen",
  data() {
    return {
      active: 0,
    };
  },

  components: { customNavBar },

  mounted() {
    this.$androidApi.getHistoryData("09").then((data) => {
      console.log(JSON.stringify(data));
    });
  },

  methods: {},
};
</script>
<style lang="scss" scoped></style>

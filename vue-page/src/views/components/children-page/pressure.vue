<!-- 压力 -->
<template>
  <div class="pressure-page">
    <custom-nav-bar title="压力" left-arrow> </custom-nav-bar>
    <div class="page-content">
      <!-- 图表 -->
      <div class="pressure-chart b-d-c l-m-t round">
        <div class="chart b-d-c">
          <div class="center-box b-d-c">77</div>
        </div>
        <div class="result">
          上次检测结果
          <div class="status">正常</div>
        </div>
      </div>

      <!--压力区间-->
      <div class="pressure-range l-m-t round">
        <div class="base-title">压力区间</div>

        <custom-progress
          :value="79"
          scale
          markShow
          statusShow
          :options="[
            { name: '放松', value: 39, color: '#498BF0' },
            { name: '正常', value: 20, color: '#5EBF79' },
            { name: '中等', value: 20, color: '#F3C342' },
            { name: '偏高', value: 21, color: '#EA6C2C' },
          ]"
        >
        </custom-progress>
      </div>
    </div>
  </div>
</template>

<script>
import customNavBar from "../custom/customNavBar.vue";
import customProgress from "@/views/components/custom/customProgress";
export default {
  name: "pressure",
  data() {
    return {};
  },

  components: { customNavBar, customProgress },

  mounted() {},

  methods: {},
};
</script>
<style lang="scss" scoped>
.page-content {
  .pressure-chart {
    width: 7.02rem;
    height: 3.96rem;
    background: #ffffff;

    flex-direction: column;
    .chart {
      width: 2.62rem;
      height: 2.62rem;
      border-radius: 50%;
      background: linear-gradient(
        89deg,
        #3c82e6 0%,
        #46c5d5 16%,
        #4adb77 40%,
        #b7d346 59%,
        #ddbd58 76%,
        #e27d51 100%
      );

      .center-box {
        width: 2.3rem;
        height: 2.3rem;
        border-radius: 50%;

        font-weight: bold;
        color: #000000;
        font-size: 0.72rem;

        background: #ffffff;
      }
    }

    .result {
      display: flex;
      margin-top: 0.28rem;
      font-size: 0.36rem;
      .status {
        margin-left: 0.2rem;
        font-weight: 400;
        color: #1da772;
      }
    }
  }

  // 压力区间
  .pressure-range {
    width: 7.02rem;
    height: 2.72rem;
    background: #ffffff;

    .custom-progress {
      margin: 0 0.25rem;
      margin-top: 0.18rem;
    }
  }
}
</style>

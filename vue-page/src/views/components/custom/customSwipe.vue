<!-- 自定义swipe 轮播 -->
<template>
  <div>
    <van-swipe
      class="my-swipe"
      :initial-swipe="chartOptionsArray.length - 1"
      :loop="false"
      :show-indicators="false"
      @change="vanSwipeChange"
    >
      <van-swipe-item
        v-for="(item, index) in chartOptionsArray"
        :key="item.date"
      >
        <echarts-component
          v-if="chartOptionsArray[index].show"
          :options="chartOptionsArray[index]"
          :height="pageWidth - 190"
          :axisLabelInterval="axisLabelInterval"
          :timeInterval="timeInterval"
          tooltip
          areaStyleGradientBcg
          :type="chartType"
        />

        <div
          v-else
          class="no-data b-d-c"
          :style="{
            height: `${pageWidth - 190}px`,
          }"
        >
          暂无数据
        </div>
      </van-swipe-item>
    </van-swipe>
  </div>
</template>

<script>
import echartsComponent from "@/components/Charts/echartsComponent.vue";
import chartUtil from "@/utils/chartUtil";
export default {
  data() {
    return {
      pageWidth: window.innerWidth,
      chartOptionsArray: [{}],
      chartSize: 1,
    };
  },

  props: {
    type: {
      type: String,
      required: true,
    },
    chartType: {
      type: String,
      default: "bar",
    },
    // x间隔
    axisLabelInterval: {
      type: Number,
      default: 5,
    },

    timeInterval: {
      type: Boolean,
      default: false,
    },

    tooltipName: {
      type: String,
    },

    xAxisData: {
      type: Object,
    },
  },

  components: { echartsComponent },

  mounted() {
    chartUtil
      .calcChartData(this.type, this.xAxisData, this.tooltipName)
      .then((res) => {
        this.chartSize = res.chartSize;
        this.chartOptionsArray = res.chartOptionsArray;
        console.log(JSON.stringify(this.chartOptionsArray));

        this.$emit("swipeResponse", this.chartSize - 1, this.chartSize);
        this.$emit(
          "chartResponse",
          this.chartOptionsArray[this.chartOptionsArray.length - 1].chartData
        );
      });
  },

  methods: {
    vanSwipeChange(e) {
      this.$emit("swipeResponse", e, this.chartSize);
      this.$emit("chartResponse", this.chartOptionsArray[e].chartData);
    },
  },
};
</script>
<style lang="scss" scoped></style>

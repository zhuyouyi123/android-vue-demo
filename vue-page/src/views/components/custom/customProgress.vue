<!--自定义进度条 -->
<template>
  <div class="custom-progress">
    <div
      class="mark"
      v-if="markShow"
      :style="{
        'margin-left': `${value - 1}%`,
      }"
    >
      <van-icon name="diamond" color="#909998" size="10" />
    </div>

    <div class="progress">
      <div
        class="status"
        v-for="item in statistics"
        v-if="item.proportion"
        :style="{ width: `${item.proportion}%`, background: `${item.color}` }"
      ></div>
    </div>

    <div class="scale" v-if="scale">
      <div
        class="scale-item"
        v-for="item in statistics"
        v-if="item.proportion"
        :style="{ width: `${item.proportion}%` }"
      >
        {{ item.sum }}
      </div>

      <div class="scale-item">100</div>
    </div>

    <div class="status-box" v-if="statusShow">
      <div class="status" v-for="item in options">
        <div class="color" :style="{ background: `${item.color}` }"></div>
        <div class="name">
          {{ item.name }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "customProgress",
  props: {
    options: {
      type: Array,
      default: () => ({
        data: [
          { name: "深睡", value: 5, color: "#3366CC" },
          { name: "浅睡", value: 5, color: "#66CC99" },
        ],
      }),
    },
    value: {
      type: Number,
      default: 0,
    },

    markShow: {
      type: Boolean,
      default: false,
    },

    scale: {
      type: Boolean,
      default: false,
    },
    statusShow: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      statistics: [],
    };
  },

  components: {},

  mounted() {
    this.calculatePercentage();
  },

  methods: {
    calculatePercentage() {
      // 计算数组元素的总和
      var total = this.options.reduce((a, b) => a + b.value, 0);

      this.statistics = [];

      this.options.forEach((item) => {
        this.statistics.push({
          color: item.color,
          proportion: (item.value / total) * 100,
          sum: this.statistics.reduce((a, b) => a + b.proportion, 0),
        });
      });
    },
  },
};
</script>
<style lang="scss" scoped>
.custom-progress {
  margin-top: 0.36rem;
  .mark {
    height: 0.2rem;
    display: flex;
    align-items: center;
    margin-bottom: 0.1rem;
  }
  .progress {
    display: flex;
    align-items: center;
    .status {
      height: 0.14rem;
      &:first-child {
        border-top-left-radius: 0.07rem;
        border-bottom-left-radius: 0.07rem;
      }

      &:last-child {
        border-top-right-radius: 0.07rem;
        border-bottom-right-radius: 0.07rem;
      }
    }
  }

  .scale {
    display: flex;
    margin-top: 0.1rem;
    .scale-item {
      font-size: 0.26rem;
      color: #000000;
      height: 0.2rem;
      &:nth-child(n + 1) {
        padding-right: 20px;
      }
      &:last-child {
        width: 0;
      }
    }
  }

  .status-box {
    display: flex;
    font-weight: 400;
    color: #000000;
    font-size: 0.3rem;
    justify-content: space-between;
    margin-top: 0.4rem;

    .status {
      display: flex;
      align-items: center;
      .color {
        width: 0.24rem;
        height: 0.24rem;
        border-radius: 50%;
        margin-right: 0.1rem;
      }
    }
  }
}
</style>

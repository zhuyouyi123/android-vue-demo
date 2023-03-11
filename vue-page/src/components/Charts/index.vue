<template>
  <div class="w h chart-content" style="position:relative" v-bind="$attrs">
    <slot v-if="loadingVNode||!hiddenAnimation" name="loadingForm">
      <div class="w h chart-box" style="position:absolute;top:0px;left:0px;">
        <section>
          <span></span>
          <span></span>
          <span></span>
        </section>
      </div>
    </slot>
    <div v-show="!loadingVNode" class="w h">
      <div :id="id" class="w h chart-div"></div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts';
export default {
  name: 'chartComp',
  props: {
    option: {
      type: Object,
      default: () => {
        return {}
      }
    },
    isSlotRelative: {
      type: Boolean,
      default: true,
    },
    isDark: Boolean,
    loading: Boolean,
  },
  computed: {
    loadingVNode() {
      if (!this.loading) {

        clearTimeout(this.timeId);
        this.timeId = setTimeout(() => {
          this.randomChar();
        }, 1500);
      }
      return !this.loading;
    }
  },
  watch: {
    option: {
      deep: true,
      handler(news) {
        this.options = news;
        //数据变化 重新渲染
        this.renderChart();
      }
    },
  },
  data() {
    return {
      // loadingVNode: this.loading,
      options: this.option,
      id: `chart-${this.randomChar(15)}`,
      timeId: null,
      hiddenAnimation: false,
    }
  },
  mounted() {
    clearTimeout(this.timeId);
    this.timeId = setTimeout(() => {
      this.renderChart();
    }, 1500)

  },
  methods: {
    /**
     * 渲染图表
     */
    renderChart() {
      this.id = `chart-${this.randomChar(5)}`;

      clearTimeout(this.timeId);
      this.timeId = setTimeout(() => {
        let em = document.getElementById(this.id);
        if (!em) {
          return;
        }
        let myChart = !this.isDark ? echarts.init(em) : echarts.init(em, 'dark');
        myChart.setOption(this.options);

        setTimeout(() => {
          this.hiddenAnimation = true;
        }, 100)
      }, 500)
    },
    /**
     * 产生随机数
     * @param {Object} len 随机数长度
     */
    randomChar(len) {
      len = len || 5;
      var x = "0123456789qwertyuioplkjhgfdsazxcvbnm";
      var tmp = "";
      for (var i = 0; i < len; i++) {
        tmp += x.charAt(Math.ceil(Math.random() * 100000000) % x.length);
      }
      return tmp;
    }
  },
  destroyed() {
    clearTimeout(this.timeId);
  }

}
</script>

<style>
.w {
  width: 100%;
}

.h {
  height: 100%;
}

.chart-div {
  transform-origin: center;
}
</style>
<style scoped>
.chart-box {
  --box-size: 150px;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

section {
  width: var(--box-size);
  height: var(--box-size);
  position: relative;
  zoom: 0.3;
}

section span {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
}

section span:nth-child(1) {
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  z-index: 2;
  border-top: 1px solid rgba(255, 255, 255, 0.5);
  border-left: 1px solid rgba(255, 255, 255, 0.5);
}

section span:nth-child(2) {
  background: #35dd4f;
  transform: translate(calc(0 - var(--box-size)), 50px) scale(0.4)
    rotate(360deg);
  animation: animate 4s ease-in-out infinite;
}

section span:nth-child(3) {
  background: #ffffff;
  transform: translate(calc(0 - var(--box-size)), 50px) scale(0.4)
    rotate(360deg);
  animation: animate 4s ease-in-out infinite;
  animation-delay: -2s;
  border-radius: 0px;
}
@keyframes animate {
  0% {
    transform: translate(-150px, 50px) scale(0.4) rotate(0deg);
    z-index: 1;
  }

  50% {
    transform: translate(150px, -50px) scale(0.4) rotate(180deg);
    z-index: 1;
  }

  50.0001% {
    transform: translate(150px, -50px) scale(0.4) rotate(180deg);
    z-index: 3;
  }

  100% {
    transform: translate(-150px, 50px) scale(0.4) rotate(360deg);
    z-index: 3;
  }
}
</style>


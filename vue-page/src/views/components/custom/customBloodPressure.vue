<!-- 血压自定义图表 -->
<template>
  <div class="box">
    <div class="num">
      <div class="scaleplate" v-for="i in 5">
        {{ level + (i - 1) * 10 }}
      </div>
    </div>
    <div class="punctuation-box">
      <div
        class="punctuation"
        v-for="i in 41"
        :style="getPunctuationStyle(i)"
      ></div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    bloodPressure: {
      type: Number,
      require: true,
    },

    level: {
      type: Number,
      require: true,
    },
  },
  data() {
    return {};
  },

  components: {},

  mounted() {},

  methods: {
    getPunctuationStyle(i) {
      let setBcg = false;
      let height = 0.2;

      //   let diff = i + this.level - Math.round(this.bloodPressure);
      let diff = this.bloodPressure - this.level;
      console.log(diff);

      if (diff <= 3) {
        if (i <= 3) {
          height = 0.18 - (4 - i) / 100;
          setBcg = true;
        } else if (i == 4) {
          height = 0.2;
          setBcg = true;
        } else if (i > 4 && i <= 7) {
          height = 0.18 - (i - 4) / 100;
          setBcg = true;
        }
      } else if (diff >= 38) {
        if (i >= 38) {
          setBcg = true;
          height = 0.18 - (i - 39) / 100;
        } else if (i == 37) {
          height = 0.2;
          setBcg = true;
        } else if (i >= 34 && i < 37) {
          height = 0.18 - (36 - i) / 100;
          setBcg = true;
        }
      } else {
        let realDiff = i - (this.bloodPressure - this.level);
        if (realDiff == 0) {
          setBcg = true;
          height = 0.2;
        } else if (realDiff < 0 && realDiff >= -3) {
          setBcg = true;
          height = 0.18 - Math.abs(realDiff) / 100;
        } else if (realDiff > 0 && realDiff <= 3) {
          setBcg = true;
          height = 0.18 - Math.abs(realDiff) / 100;
        }
      }

      return {
        background: `${setBcg ? "#E9625A" : "#7070704d"}`,
        height: setBcg ? `${height}rem` : `.15rem`,
      };
    },
  },
};
</script>
<style lang="scss" scoped>
.box {
  height: 0.6rem;
  margin: 0 0.12rem;
  border: 0.015rem solid #707070a5;
  border-radius: 0.44rem;

  .num {
    height: 0.24rem;
    width: 2.2rem;
    display: flex;
    align-items: center;
    font-size: 0.16rem;
    margin-left: 0.2rem;
    margin-top: 0.08rem;
    color: #707070dc;

    .scaleplate {
      min-width: .25rem;
      &:nth-child(2) {
        margin-left: 0.32rem;
      }
      &:nth-child(3) {
        margin-left: 0.37rem;
      }
      &:nth-child(4) {
        margin-left: 0.39rem;
      }

      &:nth-child(5) {
        margin-left: 0.39rem;
      }
    }
  }

  .punctuation-box {
    display: flex;
    align-items: flex-end;
    .punctuation {
      background: #7070704d;
      width: 0.03rem;
      height: 0.15rem;
      &:nth-child(n + 1) {
        margin-left: 0.037rem;
      }
    }
    &:nth-child(n) {
      margin-left: 0.2rem;
    }
  }
  .highlight {
    background-color: #ff0000; // 修改高亮颜色为红色，你可以根据需要修改这里的颜色值
  }
}
</style>

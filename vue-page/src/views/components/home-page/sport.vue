<!-- 运动页面 -->
<template>
  <div class="sport">
    <custom-nav-bar title="户外运动"> </custom-nav-bar>

    <div class="page-content">
      <div class="tabs-box date-stitching l-m-t">
        <van-tabs
          v-model="timeActive"
          @click="onClickTab"
          type="card"
          color="#1DA772"
        >
          <van-tab :title="item" v-for="item in sports" :key="item" />
        </van-tabs>
      </div>

      <!-- 累计信息 -->
      <div class="step-sum-box r-card l-m-t">
        <div class="left-box">
          <div class="left-top-box">累计徒步距离</div>
          <div class="left-bottom-box"><span>0.17</span>公里</div>
        </div>

        <div class="right-box">徒步总距离 ></div>
      </div>

      <!--开始模块  -->
      <div class="start-box r-card l-m-t">
        <div class="go-box b-d-c" @click="go">
          {{ goText }}
        </div>
      </div>
    </div>

    <div class="sport">
      <van-overlay :show="overlayShow"> {{ countdown }} </van-overlay>
    </div>
  </div>
</template>

<script>
import customNavBar from "../custom/customNavBar.vue";
export default {
  data() {
    return {
      timeActive: 0,
      goText: "Go",
      overlayShow: false,
      countdown: 3,
      interval: null,
      sports: ["徒步", "跑步", "骑行"],
    };
  },

  components: { customNavBar },

  mounted() {},

  methods: {
    onClickTab() {},
    go() {
      this.$androidApi.startSport();
      this.startCountdown();
    },

    startCountdown() {
      this.overlayShow = true;
      this.countdown = 3;
      this.interval = setInterval(() => {
        if (this.countdown > 1) {
          this.countdown--;
        } else if (this.countdown == 1) {
          this.countdown = "Start!";
          clearInterval(this.interval);
          setTimeout(() => {
            this.$router.push({
              path: "/sport/movement",
              query: { type: this.sports[this.timeActive] },
            });
          }, 500);
        }
      }, 1000);
    },
  },
};
</script>
<style lang="scss" scoped>
.step-sum-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 0.31rem;
  font-weight: 400;
  color: #6b7b75;
  .left-box {
    .left-bottom-box {
      color: #000000;
      span {
        font-size: 0.68rem;
        font-weight: bold;
      }
    }
  }
}

.start-box {
  height: 65.5vh;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  align-items: center;
  .go-box {
    background: #fc6030;
    width: 1.76rem;
    height: 1.76rem;
    margin-bottom: 0.56rem;
    border-radius: 50%;

    font-size: 0.72rem;
    font-weight: 800;
    color: #ffffff;
  }
}
</style>

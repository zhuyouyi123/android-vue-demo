<!-- 运动 -->
<template>
  <div class="movement">
    <custom-nav-bar :title="type"> </custom-nav-bar>

    <div class="status-info-box">
      <div class="mileage-box">
        <span class="super-f-s b">0.17</span>
        公里
      </div>
      <div class="time-box r-l-t-b-box">
        <div class="left-box">
          <div class="top-box">00:00:15</div>
          <div class="bottom-box">时长</div>
        </div>
        <div class="right-box">
          <div class="top-box">
            <span>0.11</span><span class="unit">千米/分</span>
          </div>
          <div class="bottom-box">配速</div>
        </div>
      </div>

      <div class="time-box r-l-t-b-box">
        <div class="left-box">
          <div class="top-box">100<span class="unit">次/分</span></div>
          <div class="bottom-box">心率</div>
        </div>
      </div>

      <div class="button-group-box" v-if="status == 0">
        <van-image
          class="shifting"
          width="1.76rem"
          height="1.76rem"
          :src="require('../../../../assets/image/sport/stop-button.svg')"
          @click="status = 1"
        />
        <van-image
          class="shifting"
          width="1.2rem"
          height="1.2rem"
          :src="require('../../../../assets/image/sport/lock.svg')"
          @click="status = 2"
        />
      </div>

      <div class="button-group-box both-ends" v-else-if="status == 1">
        <van-image
          width="1.76rem"
          height="1.76rem"
          :src="require('../../../../assets/image/sport/start.svg')"
          @click="status = 0"
        />
        <van-image
          width="1.76rem"
          height="1.76rem"
          :src="require('../../../../assets/image/sport/end.svg')"
        />
      </div>

      <div class="button-group-box button-center" v-if="status == 2">
        <div
          @touchstart="lockTouchstart()"
          @touchmove="lockTtouchmove()"
          @touchend="lockTouchend()"
        >
          <van-circle
            v-model="currentRate"
            :layer-color="showPress ? '#ebedf0' : '#ffffff'"
            :color="showPress ? '#cccccc' : '#ffffff'"
            :rate="currentRate"
            :speed="100"
            size="1.9rem"
          >
            <van-image
              width="1.76rem"
              height="1.76rem"
              :src="require('../../../../assets/image/sport/unlocked.svg')"
            />
          </van-circle>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import customNavBar from "@/views/components/custom/customNavBar";

export default {
  data() {
    return {
      type: this.$route.query.type,
      suspend: true,
      // 0:正常 1：暂停 2：加锁
      status: 2,
      currentRate: 0,
      pressTimer: null,

      showPress: false,
    };
  },

  components: { customNavBar },

  mounted() {},

  methods: {
    lockTouchstart() {
      this.pressTimer = setInterval(() => {
        this.currentRate += 3;

        if (!this.showPress) {
          this.showPress = true;
        }

        if (this.currentRate >= 100) {
          setTimeout(() => {
            this.status = 0;
          }, 200);
          this.lockTouchend();
        }
      }, 20);
    },
    lockTtouchmove() {
      this.lockTouchend();
    },
    lockTouchend() {
      if (this.pressTimer) {
        clearInterval(this.pressTimer);
      }

      if (this.showPress) {
        this.showPress = false;
      }

      this.currentRate = 0;
    },
  },
};
</script>
<style lang="scss" scoped>
.status-info-box {
  height: 9.92rem;
  width: 100%;
  border-radius: 0.5rem 0.5rem 0rem 0rem;
  position: fixed;
  bottom: 0;
  background: #fff;
  .mileage-box {
    padding-top: 0.3rem;
    .super-f-s {
      margin-left: 0.3rem;
    }
  }

  .button-group-box {
    margin-top: 0.8rem;
    height: 2rem;
    display: flex;
    align-items: center;

    .shifting {
      &:first-child {
        margin-left: 39vw;
      }
      &:last-child {
        margin-left: 1.12rem;
      }
    }
  }

  .both-ends {
    justify-content: space-between;
    margin: 0.8rem 0.89rem 0 0.89rem;
  }

  .button-center {
    justify-content: center;
  }

  .van-circle {
    display: flex;
    align-items: center;
    justify-content: center;
  }
}
</style>

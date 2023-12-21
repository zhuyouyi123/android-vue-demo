<!-- 首页配置 -->
<template>
  <div class="home-config-page">
    <custom-nav-bar title="编辑卡片" left-arrow> </custom-nav-bar>

    <div class="page-content">
      <div class="desc">显示在首页</div>

      <div class="fun-box-row round" v-for="(item, index) in funList">
        <div class="left-box">
          <van-image :src="item.imgSrc"></van-image>
          <div class="lable">{{ item.name }}</div>
        </div>

        <div class="left-box">
          <van-image
            @click="switchState(item)"
            :src="
              item.enable
                ? require('../../../assets/image/homeconfig/switch-v2.svg')
                : require('../../../assets/image/homeconfig/switch-close-v2.svg')
            "
          ></van-image>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import customNavBar from "../custom/customNavBar.vue";
import debounce from "@/utils/debounce";
import { Notify } from "vant";

export default {
  name: "homeConfig",
  components: {
    customNavBar,
  },

  data() {
    return {
      homeCardMap: new Map(),
      funList: [
        {
          type: "STEP",
          name: "步数",
          imgSrc: require("../../../assets/image/homeconfig/step.svg"),
          enable: true,
        },
        {
          type: "SLEEP",
          name: "睡眠",
          imgSrc: require("../../../assets/image/homeconfig/sleep.svg"),
          enable: true,
        },
        {
          type: "BLOOD_OXYGEN_SATURATION",
          name: "血氧饱和度",
          imgSrc: require("../../../assets/image/homeconfig/blood-oxygen.svg"),
          enable: true,
        },
        {
          type: "HEART_RATE",
          name: "心率",
          imgSrc: require("../../../assets/image/homeconfig/heart-rate.svg"),
          enable: true,
        },
        {
          type: "PRESSURE",
          name: "压力",
          imgSrc: require("../../../assets/image/homeconfig/pressure.svg"),
          enable: true,
        },
        {
          type: "CALORIE",
          name: "卡路里",
          imgSrc: require("../../../assets/image/homeconfig/calorie.svg"),
          enable: true,
        },
        {
          type: "TEMPERATURE",
          name: "体温",
          imgSrc: require("../../../assets/image/homeconfig/temperature.svg"),
          enable: true,
        },
        {
          type: "BLOOD_PRESSURE",
          name: "血压",
          imgSrc: require("../../../assets/image/homeconfig/blood-pressure.svg"),
          enable: true,
        },
        {
          type: "SPORT_RECORDS",
          name: "运动记录",
          imgSrc: require("../../../assets/image/homeconfig/sport.svg"),
          enable: true,
        },
        {
          type: "FATIGUE",
          name: "疲劳度",
          imgSrc: require("../../../assets/image/homeconfig/fatigue.svg"),
          enable: true,
        },
      ],
      debouncedLog: debounce((item) => this.updateState(item), 200),
      pageGroup: "HOME_CARD",
    };
  },

  mounted() {
    this.$deviceHolder.routerPath = "home";
    this.setSwitchState();
  },

  methods: {
    setSwitchState() {
      this.queryMenuCard().then(() => {
        this.funList.forEach((e) => (e.enable = this.homeCardMap.get(e.type)));
      });
    },

    queryMenuCard() {
      return new Promise((resolve, reject) => {
        this.$androidApi
          .queryConfigurationByGroup(this.pageGroup)
          .then((data) => {
            this.homeCardMap = new Map();
            data.forEach((e) => this.homeCardMap.set(e.type, e.enable));
            resolve();
          });
      });
    },

    switchState(item) {
      this.debouncedLog(item);
    },

    updateState(item) {
      let params = {
        type: item.type,
        value: !item.enable ? 1 : 0,
        group: this.pageGroup,
      };
      this.$androidApi.updateConfigStatus(params).then(() => {
        this.setSwitchState();
      });
    },
  },
};
</script>
<style lang="scss" scoped>
.home-config-page {
  .desc {
    font-size: 0.36rem;
    font-weight: 400;
    color: #000000;
    margin-top: 0.2rem;
  }

  .fun-box-row {
    height: 1.44rem;
    background: #ffffff;
    margin-top: 0.24rem;
    margin-bottom: 0.2rem;
    padding: 0 0.25rem 0 0.36rem;

    .left-box {
      display: flex;
      align-items: center;
      //   margin-left: 0.36rem;
      .van-image {
        width: 0.63rem;
        height: 0.63rem;
      }
      .lable {
        margin-left: 0.16rem;
      }
    }

    .right-box {
      //   margin-right: 0.25rem;

      .van-image {
        width: 0.56rem;
        height: 0.56rem;
      }
    }

    display: flex;
    align-items: center;
    justify-content: space-between;
  }
}
</style>

<template>
  <div id="app" class="w h over-h box-b">
    <div style="height: 0.9rem"></div>
    <router-view :nowTime="time" :key="key" />
  </div>
</template>

<script>
import axios from "axios";
import { Toast, Notify, Icon, Field, Cell, CellGroup } from "vant";
import { getNowDateTime } from "@/utils/index.js";
export default {
  name: "App",
  components: {
    [Icon.name]: Icon,
    [Field.name]: Field,
    [Cell.name]: Cell,
    [CellGroup.name]: CellGroup,
  },
  data() {
    return {
      intervalId: null,
      time: getNowDateTime(0),
    };
  },
  computed: {
    key() {
      return this.$route.path + Math.random();
    },
  },
  methods: {
    /**
     * 检查网络请求
     */
    checkNetWork() {
      axios
        .get(window.location.href, { timeout: 3000 })
        .then((res) => {
          this.msg = "";
        })
        .catch((error) => {
          try {
            if (
              error.message.indexOf("Failed") >= 0 ||
              error.message.indexOf("timeout") >= 0
            ) {
              this.msg = "服务器断开连接！";
              // alert('服务器断开连接！')
              // this.$notify.error({ title: '错误', message: '服务器断开连接！', duration: 3500, });
              return;
            }
          } catch (error) {}
          this.msg = "当前处于离线状态，请检查并连接！";
          // alert('当前处于离线状态，请检查并连接！')
          // this.$notify.error({ title: '错误', message: '当前处于离线状态，请检查并连接！', duration: 3500, });
        });
    },
    /**
     * 在进行和android基座调用时的拦截器
     */
    init() {
      //拦截器
      this.$ano.requestSetting.interceptor = {
        /**
         * 请求之前调用的方法
         * @param {object} 请求之前的所构造参数
         * @return Boolean  true,false 表示是否继续执行
         */
        before: (event) => {
          return true;
        },
        /**
         * 请求之后调用的方法,对返回数据进行加工操作
         * @param {object} 请求之前的所构造参数
         * @return Object event 表示加工后返回的数据
         */
        after: (event) => {
          return event;
        },
        /**
         * 异常处理程序
         */
        error: (error, option) => {
          return error;
        },
      };
    },
  },
  mounted() {},
  created() {},
  destroyed() {},
};
</script>

<style lang="scss">
html {
  font-size: 10px; //可以使用 1/10 rem= 10 px
}
html,
body,
#app {
  height: 100%;
  width: 100%;
  min-height: 100vh;
  overflow: hidden;
  box-sizing: border-box;
  margin: 0px;
  font-size: 0.3rem;
  font-family: PingFang SC-Regular, PingFang SC;
  font-weight: 400;
  color: #333333;
  .box-title {
    font-size: 0.32rem;
    font-family: Source Han Sans CN-Regular, Source Han Sans CN;
    color: #000000;
    padding-top: 0.33rem;
    padding-left: 0.23rem;
  }

  background-color: #eff0f4;
  --body-background-color: #f7f8fa;
  // --default-bar-color: #fff;
  // --default-font-color: #000;
  // --default-bar-left-color: rgb(9, 134, 130);

  // --van-white: #fff;
  // --van-blue: var(--body-background-color);
  // --van-button-primary-color: var(--van-white);
  // --van-button-primary-background-color: var(--van-primary-color);

  .page-white {
    background: var(--body-background-color);

    .van-button--primary {
      background: #032d58;
    }

    .van-primary-button {
      background: #032d58;
      color: #fff;
    }
    .van-primary-title {
      color: #032d58;
    }
  }
  .van-nav-bar .van-icon,
  .van-nav-bar__text {
    color: var(--default-font-color);
  }

  .van-nav-bar__title {
    color: var(--default-font-color);
  }
  .van-icon {
    font-size: 18px;
  }

  .division-line {
    border: 0.01rem solid #d8dceb;
    width: 95%;
    margin-left: 2.5%;
  }

  .van-cell-group {
    .van-field {
      height: 0.75rem;
      &:last-child {
        border-bottom-left-radius: 0.1rem;
        border-bottom-right-radius: 0.1rem;
      }
    }
    .van-cell {
      &:last-child {
        border-bottom-left-radius: 0.1rem;
        border-bottom-right-radius: 0.1rem;
      }
    }
  }
  .van-hairline--top-bottom {
    position: unset;
  }
}

/* 设置滚动条的样式 */
::-webkit-scrollbar {
  width: 1px;
  /*高宽分别对应横竖滚动条的尺寸*/
  height: 1px;
}

/* 滚动槽 */
::-webkit-scrollbar-track {
  -webkit-box-shadow: inset 2px rgba(0, 0, 0, 0.3);
  border-radius: 2px;
}

/* 滚动条滑块 */
::-webkit-scrollbar-thumb {
  border-radius: 2px;
  background: rgba(0, 0, 0, 0.1);
  -webkit-box-shadow: inset 2px rgba(0, 0, 0, 0.5);
}

::-webkit-scrollbar-thumb:window-inactive {
  background: transparent;
}
.nav-bar {
  position: sticky;
  top: 0;
  z-index: 5;
}

.el-message {
  top: 30px !important;
  width: 100% !important;
  z-index: 10002 !important;
}
.el-message-box {
  top: 35% !important;
  max-width: 100% !important;
  z-index: 10002 !important;
  width: 85% !important;
}

// .van-field--error .van-field__control,
// .van-field--error .van-field__control::placeholder {
//   color: #dcdcdc !important;
//   -webkit-text-fill-color: currentColor;
// }
</style>

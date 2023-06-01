<template>
  <div id="app" class="w h over-h box-b" ref="app">
    <div style="height: 0.9rem"></div>

    <router-view :nowTime="time" v-if="isReload" :key="key" />
  </div>
</template>

<script>
import axios from "axios";
import {
  Toast,
  Notify,
  Icon,
  Field,
  Cell,
  CellGroup,
  Button,
  Dialog,
} from "vant";

import { getNowDateTime } from "@/utils/index.js";
export default {
  name: "App",
  components: {
    [Icon.name]: Icon,
    [Field.name]: Field,
    [Cell.name]: Cell,
    [CellGroup.name]: CellGroup,
    [Button.name]: Button,
    [Dialog.name]: Dialog,
    [Dialog.Component.name]: Dialog.Component,
  },
  provide() {
    return {
      reload: this.reload,
    };
  },
  data() {
    return {
      intervalId: null,
      time: getNowDateTime(0),
      commonAndroidEvent: null,
      isReload: true,
    };
  },
  created() {},

  computed: {
    key() {
      return this.$route.path + Math.random();
    },
  },

  watch: {
    "$i18n.locale"(newValue, oldValue) {
      if (newValue != oldValue) {
        this.reload();
      }
    },
  },

  mounted() {
    //通用安卓推送回调
    window.commonAndroidEvent = this.commonAndroidCallBack;
    this.commonAndroidEvent = new Event("commonAndroidEvent");
  },

  methods: {
    reload() {
      this.isReload = false;
      this.$nextTick(() => {
        this.isReload = true;
      });
    },
    commonAndroidCallBack(returnJson) {
      try {
        this.commonAndroidEvent.data = JSON.parse(returnJson);
        window.dispatchEvent(this.commonAndroidEvent);
      } catch (error) {
        console.log(returnJson);
        console.error("commonAndroidCallBack error");
      }
    },
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
  created() {},
  destroyed() {},
};
</script>

<style lang="scss" >
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
  // 距离上方一点点
  --little-margin-top: 0.16rem;
  .box-title {
    font-size: 0.32rem;
    font-family: Source Han Sans CN-Regular, Source Han Sans CN;
    color: #000000;
    padding-top: 0.33rem;
    padding-left: 0.23rem;
  }

  .content {
    height: 93vh;
    padding: 0.24rem 0.24rem;
  }

  background-color: #eff0f4;

  .division-line {
    border: 1px solid #d8dceb;
    width: 95%;
    margin-left: 2.5%;
  }

  .division-line2 {
    border: 1px solid #d8dceb;
  }

  .filtering-selected {
    color: #007fff;
  }

  .not-click {
    pointer-events: none;
    background: rgba(99, 98, 98, 0.213);
    opacity: 0.1;
    z-index: 100;
  }
}
</style>

<!-- 设备详情 -->
<template>
  <div>
    <nav-bar>
      <van-icon name="arrow-left" slot="left" class="navIcon" />
    </nav-bar>
    <div class="content">
      <div class="base-info-box">
        <div class="box-title">基础信息</div>
        <van-cell-group inset>
          <van-field
            label-width="4rem"
            v-model="value"
            label="文本"
            placeholder="Mac"
          />
          <van-field
            label-width="4rem"
            v-model="value"
            label="文本"
            placeholder="model"
          />
          <van-field
            label-width="4rem"
            v-model="value"
            label="文本"
            placeholder="version"
          />
          <van-field
            label-width="4rem"
            v-model="value"
            label="文本"
            placeholder="version"
          />
          <van-field
            label-width="4rem"
            v-model="value"
            label="文本"
            placeholder="version"
          />
        </van-cell-group>
      </div>

      <div class="special-info-box">
        <div class="box-title">特件信息</div>
        <van-cell-group inset>
          <van-field
            label-width="4rem"
            v-model="value"
            label="文本"
            placeholder="请输入用户名"
          />
          <van-field
            label-width="4rem"
            v-model="value"
            label="文本"
            placeholder="请输入用户名"
          />
          <van-field
            label-width="4rem"
            v-model="value"
            label="文本"
            placeholder="请输入用户名"
          />
        </van-cell-group>
      </div>

      <div class="function-button">
        <div class="function">
          <div class="logo"></div>
          <div class="name">可连接</div>
        </div>
        <div class="function">
          <div class="logo"></div>
          <div class="name">恢复出厂设置</div>
        </div>
        <div class="function">
          <div class="logo"></div>
          <div class="name">关机</div>
        </div>
        <div class="function">
          <div class="logo"></div>
          <div class="name">配置通道</div>
        </div>
        <div class="function">
          <div class="logo"></div>
          <div class="name">移除秘钥</div>
        </div>
        <div class="function">
          <div class="logo"></div>
          <div class="name">修改秘钥</div>
        </div>
        <div class="function">
          <div class="logo"></div>
          <div class="name">修改设备名称</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import navBar from "@/components/navigation/navBar.vue";
import {} from "vant";
export default {
  name: "deviceDetail",
  components: {
    navBar: navBar,
  },
  data() {
    return {};
  },

  mounted() {},

  methods: {},
};
</script>
<style lang='scss' scoped>
.content {
  margin-left: 0.24rem;
  width: 7.02rem;

  // 基础信息
  .base-info-box {
    height: 4.65rem;
    background: #ffffff;
    box-shadow: 0rem 0.04rem 0.06rem 0.01rem rgba(0, 0, 0, 0.1);
    border-radius: 0.1rem 0.1rem 0.1rem 0.1rem;
    margin-top: 0.24rem;
  }

  .special-info-box {
    height: 3.05rem;
    background: #ffffff;
    box-shadow: 0rem 0.04rem 0.06rem 0.01rem rgba(0, 0, 0, 0.1);
    border-radius: 0.1rem 0.1rem 0.1rem 0.1rem;
    margin-top: 0.16rem;
  }

  .function-button {
    margin-top: 0.29rem;
    display: flex;
    flex-wrap: wrap;
    .function {
      height: 1.57rem;
      width: 33.33%;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-direction: column;
      &:nth-child(n + 4) {
        margin-top: 0.36rem;
      }
      .logo {
        width: 1rem;
        height: 1rem;
        background: #ffffff;
        box-shadow: 0rem 0.04rem 0.06rem 0.01rem rgba(0, 0, 0, 0.1);
        border-radius: 50%;
      }
      .name {
        margin-top: 0.08rem;
        height: 0.42rem;
        font-size: 0.3rem;
        font-family: PingFang SC-Regular, PingFang SC;
        font-weight: 400;
        line-height: 0.39rem;
        display: flex;
        justify-content: center;
        align-items: center;
      }
    }
  }
}
</style>
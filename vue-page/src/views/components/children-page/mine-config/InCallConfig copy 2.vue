<!-- 提醒配置 -->
<template>
  <div>
    <custom-nav-bar left-arrow title="来电提醒"></custom-nav-bar>
    <div class="page-content">
  
      <van-cell-group inset class="l-m-t">
        <van-cell
          center
          title="开启来电提醒"
          label="手机来电时，手表会进行提醒"
        >
          <template #right-icon>
            <van-switch v-model="inCallChecked" size="24" />
          </template>
        </van-cell>

        <van-cell
          center
          title="显示联系人信息"
          label="手机有来电，手环会显示姓名或号码"
        >
          <template #right-icon>
            <van-switch v-model="showCallInfo" size="24" />
          </template>
        </van-cell>
      </van-cell-group>
    </div>
  </div>
</template>

<script>
import customNavBar from "@/views/components/custom/customNavBar";

export default {
  data() {
    return {
      // 开启来电显示
      inCallChecked: false,
      // 显示联系人信息
      showCallInfo: false,
    };
  },

  components: { customNavBar },

  mounted() {},

  methods: {},
};
</script>
<style lang="scss" scoped></style>

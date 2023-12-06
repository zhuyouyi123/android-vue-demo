<!-- 用户信息 -->
<template>
  <div class="user-info">
    <custom-nav-bar title="用户信息" left-arrow> </custom-nav-bar>
    <div class="page-content config-page">
      <van-form @submit="onSubmit" class="l-m-t">
        <van-cell-group inset>
          <van-field
            v-model="userInfo.username"
            name="姓名"
            label="姓名"
            placeholder="请输入姓名"
            maxlength="10"
            input-align="right"
            clearable
          />
        </van-cell-group>
        <!-- 性别 -->
        <van-field name="radio" label="性别" input-align="right">
          <template #input>
            <van-radio-group v-model="userInfo.gender" direction="horizontal">
              <van-radio name="1">男</van-radio>
              <van-radio name="2">女</van-radio>
            </van-radio-group>
          </template>
        </van-field>

        <!-- 出生年月 -->
        <!-- <van-field
          v-model="userInfo.birthDate"
          is-link
          readonly
          name="datePicker"
          label="时间选择"
          placeholder="点击选择时间"
          @click="showPicker = true"
        /> -->

        <div style="margin: 16px">
          <van-button round block type="primary" native-type="submit">
            提交
          </van-button>
        </div>
      </van-form>
    </div>
  </div>
</template>

<script>
import customNavBar from "@/views/components/custom/customNavBar";
import CustomNavBar from "../../custom/customNavBar.vue";
import { Toast } from "vant";
export default {
  data() {
    return {
      userInfo: {
        id: "",
        username: "未设置",
        gender: "1",
      },
      showPicker: false,
    };
  },

  components: { customNavBar },

  mounted() {
    this.getUserInfo();
  },

  methods: {
    onSubmit() {
      let params = {
        id: this.userInfo.id,
        username: this.userInfo.username,
        gender: this.userInfo.gender,
      };
      this.$androidApi.saveUserInfo(params);
      Toast.success("保存成功");
    },
    onSelectTimeConfirm(e) {
      console.log(e);
    },
    getUserInfo() {
      this.$androidApi.queryUserInfo().then((data) => {
        if (data) {
          this.userInfo = {
            id: data.id,
            username: data.username,
            gender: data.gender + "",
          };
        }
      });
    },

    onGenderSelect(e) {
      this.genderShow = false;
    },
  },

  CustomNavBar,
};
</script>
<style lang="scss" scoped>
.page-content {
  .van-field__label {
    font-size: 0.26rem;
  }
  .van-cell__title {
    font-size: 0.26rem;
  }
}
</style>

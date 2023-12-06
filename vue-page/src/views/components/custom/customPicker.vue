<!-- 自定义 选择器 -->
<template>
  <div>
    <van-popup v-model="showPicker" round position="bottom">
      <van-picker
        :title="title"
        show-toolbar
        :columns="columns"
        @confirm="onConfirm"
        @cancel="onCancel"
        @change="onChange"
        :default-index="defaultIndex"
      />
    </van-popup>
  </div>
</template>

<script>
import { Toast } from "vant";
export default {
  props: {
    title: {
      type: String,
      default: "请选择",
    },
    type: {
      type: String,
    },
  },
  data() {
    return {
      showPicker: false,
      columns: [],
      group: "TARGET",
      defaultIndex: 0,
    };
  },

  components: {},

  mounted() {
    this.$androidApi.queryConfigurationByGroup(this.group).then((data) => {
      this.columns = [];
      let tar = data.find((e) => e.type == this.type);
      switch (this.type) {
        case "STEP":
          for (let i = 2000; i <= 30000; i += 1000) {
            this.columns.push(i);
            if (i == tar.value) {
              this.defaultIndex = (tar.value - 2000) / 1000;
            }
          }
          break;
        case "CALORIE":
          for (let i = 100; i <= 2000; i += 100) {
            this.columns.push(i);
            if (i == tar.value) {
              this.defaultIndex = (tar.value - 100) / 100;
            }
          }
          break;

        default:
          break;
      }
    });
  },

  methods: {
    onConfirm(e) {
      let params = {
        type: this.type,
        group: this.group,
        value: e,
      };
      this.$androidApi.updateConfigStatus(params).then(() => {
        Toast.success({ message: "保存成功", position: "top" });
        this.onCancel();
      });
    },
    onCancel() {
      this.showPicker = false;
    },
    onChange() {},

    open() {
      this.showPicker = true;
    },

    close() {
      this.showPicker = false;
    },
  },
};
</script>
<style lang="scss" scoped></style>

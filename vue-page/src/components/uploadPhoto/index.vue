<template>
  <el-button v-bind="$attrs" style="padding:0px;">
    <div class="w h f-c a-i-c" style="position:relative;">
      <input :id="id" @change="getImageFile" type="file" capture="camera" class="w h-400"
        accept="image/*" style="opacity: 0; position: absolute; top: 0; left: 0" />
      <slot>
        <span class="" style="padding:9px 15px">拍照
        </span>
      </slot>
    </div>
  </el-button>
</template>

<script>
import axios from 'axios';
import { randomChar } from '@/utils/index.js';
export default {
  props: {
    uploadPath: String,
    isSuccess: Boolean
  },
  data() {
    return {
      success: this.isSuccess,
      reloadFileInput: true,

      imageInfo: {},
      image: '',

      id: `takePhotoImageFile_${randomChar(10)}`
    }
  },
  watch: {
    isSuccess(news) {
      this.success = news;
    },
    imageInfo: {
      deep: true,
      handler(news) {
        this.$emit('uploadSuccess', news);
      }
    }
  },
  methods: {
    getImageFile(e) {
      /**
       * 为了防止再次选择相同文件，不触发当前函数
       **/
      this.$nextTick(() => {
        this.reloadFileInput = true;
        this.$nextTick(() => {
          this.reloadFileInput = false;
        });
      });
      this.imageInfo = {};

      const el = document.querySelector("#" + this.id);
      const _file = el?.files?.[0];
      if (!_file) {
        this.$message({
          showClose: true,
          message: "没有选择图片或取消了上传"
        });
        return;
      }

      let reader = new FileReader();
      reader.readAsDataURL(_file);
      reader.onloadend = () => {
        this.image = reader.result;
      };

      const data = new FormData();
      data.append("file", _file);
      //上传图片
      axios.post(this.uploadPath, data, {
        headers: {
          "Content-Type": "multipart/form-data"
        }
      }).then(res => {
        let _data = res.data;
        _data = typeof _data === "string" ? JSON.parse(_data) : _data;
        if (_data.code) {
          this.imageInfo = _data.data;
          this.success = true;
          this.$message.success('上传成功!');
        }
      }).catch(err => {
        this.$dialog.alert({
          title: '错误',
          message: "上传失败，出现错误"
        });
      });
    },
  }
}
</script>

<style lang="scss" scoped>
</style>

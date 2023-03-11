<template>
  <div class="w h">
    <!-- <div class="resize_box" :style="style"> -->
    <slot></slot>
  </div>
</template>

<script>
export default {
  name: '',
  //参数注入
  props: {
    width: {
      type: String,
      default: '1920',
    },
    height: {
      type: String,
      default: '1080',
    },
    autoHeight: Boolean
  },
  data() {
    return {
      style: {
        width: this.width + 'px',
        height: this.boxHeight() + 'px',
        transform: 'scale(1) translate(-50%, -50%)',
      },

    }
  },
  mounted() {
    this.setScale()
    window.onresize = this.Debounce(this.setScale, 1000, false)
  },
  methods: {
    boxHeight() {
      if (this.autoHeight) {
        const scale = window.innerWidth / this.width;
        console.log(window.innerHeight / scale);
        return window.innerHeight / scale;
      }
      return this.height;
    },
    Debounce: (fn, t) => {
      const delay = t || 500
      let timer
      return function () {
        const args = arguments
        if (timer) {
          clearTimeout(timer)
        }
        const context = this
        timer = setTimeout(() => {
          timer = null
          fn.apply(context, args)
        }, delay)
      }
    },
    // 获取放大缩小比例
    getScale() {
      const w = window.innerWidth / this.width
      const h = window.innerHeight / this.boxHeight()
      return w < h ? w : h
    },
    // 设置比例
    setScale() {
      // const el = document.getElementById("app");
      const el = document.querySelector("body");
      el.style.width = this.width + 'px';
      el.style.background = "#ddd";
      el.style.height = this.boxHeight() + 'px';
      // el.style.transform = 'scale(' + this.getScale() + ') translate(-50%, -50%)';
      el.style.position = "absolute";
      el.style.transform = 'scale(' + this.getScale() + ') translate(-50%, -50%)';
      el.setAttribute('zoom',this.getScale());
      // this.style = {
      //   width: this.width + 'px',
      //   height: this.boxHeight() + 'px',
      //   transform: 'scale(' + this.getScale() + ') translate(-50%, -50%)',
      // }
    },
  },
}
</script>

<style scoped>

body {
  background-repeat: no-repeat;
  background-size: 100%;
  transform-origin: 0 0;
  position: absolute;
  left: 50%;
  top: 50%;
  transition: 0.3s;
  width: 100%;
  margin: 0px;
}
</style>

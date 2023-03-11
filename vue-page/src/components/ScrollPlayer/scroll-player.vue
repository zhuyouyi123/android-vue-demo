<template>
  <div :id="id" class="scroll-wrap" v-bind="$attrs" >
    <div class="scroll-item">
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      id: `scroll_${new Date().getTime()}_${parseInt(Math.random() * 100000)}`,
      timeoutId: null,
      isStop: false,
    }
  },
  methods: {
    /**
     * 计算位置
     */
    getBoxAnimation() {
      let el = document.querySelector(`#${this.id}`);
      let contentEl = document.querySelector(`#${this.id} .scroll-item`);
      //获取高度 
      const boxHeight = el.offsetHeight || el.clientHeight || 200;
      let contentHeight = contentEl.offsetHeight || contentEl.clientHeight || 200;
      if (boxHeight >= contentHeight) {
        return;
      }
      contentEl.innerHTML += contentEl.innerHTML + contentEl.innerHTML + contentEl.innerHTML;
      contentHeight = contentEl.offsetHeight || contentEl.clientHeight || 200;
      //计算动画速度
      const duration = Math.round(contentHeight / boxHeight) * 2;
      contentEl.setAttribute('style', `--scroll-duration:${duration}s;--scroll-height:${boxHeight}px`)
      contentEl.className += ' scroll-item-animation';
    },
    setAnimation() {
      let el = document.querySelector(`#${this.id}`);
      if (!el) {
        clearTimeout(this.timeoutId);
        this.timeoutId = setTimeout(() => {
          this.setAnimation();
        }, 1000);
      } else {
        this.getBoxAnimation();
      }
      ;
    },

  },
  mounted() {
    clearTimeout(this.timeoutId);
    this.timeoutId = setTimeout(() => {
      this.setAnimation();
    }, 2600);
  },
  destroyed() {
    clearTimeout(this.timeoutId);
  }
}
</script>

<style lang="scss" scoped>
.scroll-wrap {
  overflow: hidden;
  --scroll-default-content-display: none;
  .scroll-item-animation {
    --scroll-height: 200px;
    --scroll-duration: 4s;
    animation: scroll-player linear 10s infinite;
    animation-duration: calc(var(--scroll-duration));
  }
  .scroll-item-animation:hover{
    animation-play-state: paused;
  }
}
@keyframes scroll-player {
  0% {
    transform: translate3d(0, -25%, 0);
  }

  100% {
    transform: translate3d(0, -75%, 0);
  }
}
</style>

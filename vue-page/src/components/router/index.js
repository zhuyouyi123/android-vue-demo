import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)


export const constantRoutes = [{
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'home',
    component: () => import('@/views/home/index.vue'),
  },
  // 设备详情
  {
    path: '/home/deviceDetail',
    name: 'deviceDetail',
    component: () => import('@/views/home/children/deviceDetail.vue'),
  },
  // 设备详情
  {
    path: '/home/deviceDetail/configureChannel',
    name: 'configureChannel',
    component: () => import('@/views/home/config/channel.vue'),
  },
]





const createRouter = () => new Router({
  scrollBehavior: () => ({
    y: 0
  }),
  routes: constantRoutes
})

const router = createRouter()

router.beforeEach((to, form, next) => {
  next();
})

export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router

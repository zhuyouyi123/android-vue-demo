import Vue from 'vue'
import Router from 'vue-router'
import Index from "../views/home/index.vue"

Vue.use(Router)


export const constantRoutes = [{
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'home',
    component: Index,
  },
  // 设备详情
  {
    path: '/home/deviceDetail',
    name: 'deviceDetail',
    component: () => import('@/views/home/children/deviceDetail.vue'),
  }
  // {
  //   path: '/home/deviceDetail',
  //   name: 'deviceDetail',
  //   component: () => import('@/views/home/children/deviceDetail.vue'),
  // },
  // {
  //   path: '/thoroughfare/deviceDetail',
  //   name: 'deviceDetail',
  //   component: () => import('@/views/home/children/deviceDetail.vue'),
  // },

  // {
  //   path: '/deviceDetail',
  //   name: 'deviceDetail',
  //   component: () => import('@/views/home/children/deviceDetail.vue'),
  // }
]





const createRouter = () => new Router({

  // mode: 'history', // require service support
  scrollBehavior: () => ({
    y: 0
  }),
  routes: constantRoutes
})

const router = createRouter()

router.beforeEach((to, form, next) => {
  next();
})
// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router

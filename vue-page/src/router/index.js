import Vue from 'vue'
import Router from 'vue-router'
import { excludesRouterPath, tokenName } from '@/server/base.js'

Vue.use(Router)


export const constantRoutes = [
  {
    path: '/',
    redirect: '/layout/index'
  },
  {
    path: '/layout',
    name: 'layout',
    component: () => import('@/views/home/index.vue'),
    children: [
      {
        path: 'index',
        name: 'index',
        component: () => import('@/views/home/index.vue'),
        // children: [{
        //   path: 'main',
        //   name: 'main',
        //   component: () => import('@/views/home/main/index.vue'),
        // }]
      },
      

      // {
      //   path: 'login',
      //   name: 'login',
      //   component: () => import('@/views/login/index.vue')
      // }
    ]
  },
]





const createRouter = () => new Router({

  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

router.beforeEach((to, form, next) => {
  // debugger
  if (!excludesRouterPath.includes(to.path)) {
    try {
      if (!localStorage.getItem(tokenName)) {
        next(`/layout/index`);
        return false;
      }

    } catch (e) {
      //ignore
    }
  } //判断跳转的是否是登录地址
  if (excludesRouterPath.includes(to.path) && localStorage.getItem(tokenName)) {
    next('/')
    return;
  }
  next();
})
// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router

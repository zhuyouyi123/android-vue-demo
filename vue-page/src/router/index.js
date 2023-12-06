import Vue from "vue";
import Router from "vue-router";
import { excludesRouterPath, tokenName } from "@/server/base.js";

Vue.use(Router);

export const constantRoutes = [
  {
    path: "/",
    redirect: "/layout/index",
  },
  {
    path: "/layout",
    name: "layout",
    component: () => import("@/views/home/index.vue"),
    children: [
      {
        path: "index",
        name: "index",
        component: () => import("@/views/home/index.vue"),
      },
    ],
  },
  {
    path: "/step",
    name: "step",
    component: () => import("@/views/components/children-page/step.vue"),
  },
  {
    path: "/sleep",
    name: "sleep",
    component: () => import("@/views/components/children-page/sleep.vue"),
  },
  // 血氧
  {
    path: "/bloodOxygen",
    name: "bloodOxygen",
    component: () => import("@/views/components/children-page/bloodOxygen.vue"),
  },
  // 心率
  {
    path: "/heartRate",
    name: "heartRate",
    component: () => import("@/views/components/children-page/heartRate.vue"),
  },
  // 压力
  {
    path: "/pressure",
    name: "pressure",
    component: () => import("@/views/components/children-page/pressure.vue"),
  },
  // 卡路里
  {
    path: "/calorie",
    name: "calorie",
    component: () => import("@/views/components/children-page/calorie.vue"),
  },
  // 体温
  {
    path: "/temperature",
    name: "temperature",
    component: () => import("@/views/components/children-page/temperature.vue"),
  },
  // 血压🩸
  {
    path: "/bloodPressure",
    name: "bloodPressure",
    component: () => import("@/views/components/children-page/bloodPressure.vue"),
  },
  // 运动页面
  {
    path: "/sport/movement",
    name: "movement",
    component: () => import("@/views/components/children-page/sport/movement.vue"),
  },
  // 手环配置
  {
    path: "/braceletConfig",
    name: "braceletConfig",
    component: () => import("@/views/components/children-page/braceletConfig.vue"),
  },
  // 首页卡片配置
  {
    path: "/homeConfig",
    name: "homeConfig",
    component: () => import("@/views/components/children-page/homeConfig.vue"),
  },
  // 用户配置
  {
    path: "/user/info",
    name: "userInfo",
    component: () => import("@/views/components/children-page/config/userInfo.vue"),
  }
];


const createRouter = () =>
  new Router({
    // mode: 'history', // require service support
    scrollBehavior: () => ({ y: 0 }),
    routes: constantRoutes,
  });

const router = createRouter();

router.beforeEach((to, form, next) => {
  // debugger
  // if (!excludesRouterPath.includes(to.path)) {
  //   try {
  //     if (!localStorage.getItem(tokenName)) {
  //       next(`/layout/index`);
  //       return false;
  //     }
  //   } catch (e) {
  //     //ignore
  //   }
  // }
  //判断跳转的是否是登录地址
  // if (excludesRouterPath.includes(to.path) && localStorage.getItem(tokenName)) {
  //   next("/");
  //   return;
  // }
  next();
});
// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter();
  router.matcher = newRouter.matcher; // reset router
}

export default router;

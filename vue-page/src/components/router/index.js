import Vue from "vue";
import Router from "vue-router";

import index from "../../views/home/index.vue";
import shutdownIndex from "../../views/home/batchShutdownIndex.vue";

import androidApi from "@/api/android-api";

Vue.use(Router);

var APP_TYPE = "";

export const constantRoutes = [
  {
    path: "/",
    redirect: "/home",
  },
  {
    path: "/home",
    name: "home",
    component: index,
  },
  {
    path: "/home1",
    name: "home1",
    component: shutdownIndex,
  },
  {
    path: "/config",
    name: "config",
    component: () => import("@/views/config/sdkConfig.vue"),
  },

  // 设备详情
  {
    path: "/home/deviceDetail",
    name: "deviceDetail",
    component: () => import("@/views/home/children/deviceDetail.vue"),
  },
  // 设备详情
  {
    path: "/home/deviceDetail/configureChannel",
    name: "configureChannel",
    component: () => import("@/views/home/config/channel.vue"),
  },
  {
    path: "/home/batch-config/channel-home",
    name: "batchConfigHome",
    component: () => import("@/views/home/config/batch/configChannelHome.vue"),
  },
  {
    path: "/home/batch-config/channel-home/add",
    name: "batchConfigChannel",
    component: () => import("@/views/home/config/batch/addChannel.vue"),
  },
  // 设备详情
  {
    path: "/home/deviceDetail/configureChannel/add",
    name: "add",
    component: () => import("@/views/home/config/components/add.vue"),
  },
];

const createRouter = () =>
  new Router({
    scrollBehavior: () => ({
      y: 0,
    }),
    routes: constantRoutes,
  });

const router = createRouter();

router.beforeEach((to, form, next) => {
  if (!APP_TYPE) {
    androidApi.shareGet({ key: "APP_TYPE" }).then((data) => {
      if (data) {
        APP_TYPE = data;
        let path = data == "SHUTDOWN" ? "/home1" : "/home";
        next({ path: path });
      } else {
        APP_TYPE = "CONFIG";
        next({ path: "/home" });
      }
    });
  } else {
    next();
  }
});

export function resetRouter() {
  const newRouter = createRouter();
  router.matcher = newRouter.matcher; // reset router
}

export default router;

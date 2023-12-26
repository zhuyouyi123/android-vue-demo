
import androidApi from "@/api/android-api"
import deviceHolder from "./deviceHolder"
var isFirst = true;

export default {

    /**
     * 查询实时信息
     * @returns 实时数据
     */
    queryRealInfo() {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                androidApi.getDeviceInfo().then((data) => {
                    if (data && data.deviceInfo) {
                        deviceHolder.deviceInfo = data.deviceInfo;
                        deviceHolder.connectStatus = data.connectStatus
                        deviceHolder.bindingInfo.time = new Date().getTime();
                        resolve(data.deviceInfo);
                        return
                    }
                    resolve();
                    return
                });
            }, 80);
        })
    },

    /**
     * 查询运动目标
     */
    querySportTarget() {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                androidApi.queryConfigurationByGroup("TARGET").then((data) => {
                    let params = {
                        stepTarget: 8000,
                        calorieTarget: 300,
                    }
                    data.forEach((e) => {
                        switch (e.type) {
                            case "STEP":
                                params.stepTarget = e.value;
                                break;
                            case "CALORIE":
                                params.calorieTarget = e.value;
                                break;
                        }
                    });

                    resolve(params)
                });
            }, 20);
        })
    },

    queryWeekAndMonthStepData() {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                androidApi.queryWeekAndMonthStepData().then((res) => {
                    resolve(res)
                })
            }, 50);
        })
    },

    /**
     * 查询当天最新信息
     * 最新血氧
     * 心率图标
     * 体温
     * 血压
     */
    queryCurrDayLastInfo() {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                androidApi.queryCurrDayLastInfo().then(res => {
                    resolve(res);
                });
            }, 80);
        })
    },

}
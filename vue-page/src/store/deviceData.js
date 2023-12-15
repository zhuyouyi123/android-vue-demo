
import androidApi from "@/api/android-api"
import deviceHolder from "./deviceHolder"

export default {

    /**
     * 查询实时信息
     * @returns 实时数据
     */
    queryRealInfo() {
        return new Promise((resolve, reject) => {
            androidApi.getDeviceInfo().then((data) => {
                if (data && data.deviceInfo) {
                    deviceHolder.deviceInfo = data.deviceInfo;
                    deviceHolder.bindingInfo.time = new Date().getTime();
                    resolve(data.deviceInfo);
                    return
                }
                reject();
            });

        })
    },

    /**
     * 查询运动目标
     */
    querySportTarget() {
        return new Promise((resolve, reject) => {
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
        })
    },

}
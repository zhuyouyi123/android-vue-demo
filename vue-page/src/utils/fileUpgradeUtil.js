import { Toast } from "vant";
import androidApi from "@/api/android-api";
import deviceHolder from "@/store/deviceHolder";
var result = {
    data: {
        appUpdateInfo: {
            errorCode: 0,
            errorMsg: "",
            needUpdate: false,
            updateData: {},
            version: ""
        },
        dfuUpdateInfo: {
            errorCode: 0,
            errorMsg: "",
            needUpdate: false,
            updateData: {},
            version: ""
        },
        otaUpdateInfo: {
            errorCode: 0,
            errorMsg: "",
            needUpdate: false,
            updateData: {},
            version: ""
        },
    },
    tips: "",
    errorCode: 0,
    errorMsg: ""
}

var url = "https://pre.joysuch.com/api-node/app/file-download/BCG_WRISTBAND/";

export default {

    init() {
        result = {
            data: {
                appUpdateInfo: {
                    errorCode: 0,
                    errorMsg: "",
                    needUpdate: false,
                    version: "",
                    updateData: {},
                    url: ""
                },
                dfuUpdateInfo: {
                    errorCode: 0,
                    errorMsg: "",
                    needUpdate: false,
                    version: "",
                    updateData: {},
                    url: ""
                },
                otaUpdateInfo: {
                    errorCode: 0,
                    errorMsg: "",
                    needUpdate: false,
                    version: "",
                    updateData: {},
                    url: ""
                },
            },
            tips: "",
            errorCode: 0,
            errorMsg: ""
        }
        return result;
    },

    /**
     * 校验升级结果
     */
    async checkUpgradeResult(battery) {
        // 初始化结果
        result = this.init();
        // 校验网络
        if (!navigator.onLine) {
            return this.setErrorMsg("网络错误");
        }

        let deviceInfoData = await androidApi.getDeviceInfo();
        if (!deviceInfoData | !deviceInfoData.connectStatus) {
            result = this.unConnectError("设备未连接")
        } else {
            if (battery <= 20) {
                result = this.lowBatteryError("电量低于20%，无法升级");
            } else {
                await this.getOtaVersion();
                await this.getDfuVersion();
            }
        }

        await this.getAppVersion();

        return result;
    },

    /**
     * 
     * @returns 获取dfu固件版本
     */
    async getDfuVersion() {
        let deviceInfo = deviceHolder.deviceInfo;
        if (!deviceInfo || !deviceInfo.firmwareVersion) {
            result.data.dfuUpdateInfo.needUpdate = false
            return;
        }
        let split = String(deviceInfo.firmwareVersion).split("_");

        if (split.length != 2 || !split[1]) {
            result.data.dfuUpdateInfo.needUpdate = false
            return;
        }
        result.data.dfuUpdateInfo.version = split[1]
        result.data.dfuUpdateInfo.url = `${url}DFU_FIRMWARE/${split[1]}`;
        let res = await this.fetchData(result.data.dfuUpdateInfo.url);
        if (!result.data.otaUpdateInfo.needUpdate && (!res || !res.data)) {
            result.data.dfuUpdateInfo.needUpdate = false
            result.data.dfuUpdateInfo.tips = `Version ${split[1]}`;
            result.tips = `已是最新版本`;
            return
        }
        result.data.dfuUpdateInfo.needUpdate = res.data.needUpdate;
        if (result.data.dfuUpdateInfo.needUpdate || result.data.otaUpdateInfo.needUpdate) {
            result.tips = "有新版本可用"
        } else {
            result.tips = `已是最新版本`;
        }
        result.data.dfuUpdateInfo.updateData = { ...res.data };

    },

    async getOtaVersion() {
        let deviceInfo = deviceHolder.deviceInfo;
        if (!deviceInfo || !deviceInfo.otaFirmwareVersion || !deviceInfo.otaAddress) {
            result.data.otaUpdateInfo.needUpdate = false
            return;
        }
        let split = String(deviceInfo.otaFirmwareVersion).split("-");

        if (split.length != 2 || !split[1]) {
            result.data.otaUpdateInfo.needUpdate = false
            return;
        }
        let version = this.convertVersion(split[1]);
        result.data.otaUpdateInfo.version = version
        result.data.otaUpdateInfo.url = `${url}OTA_FIRMWARE/${version}`;
        let res = await this.fetchData(result.data.otaUpdateInfo.url);
        if (!res || !res.data) {
            result.data.otaUpdateInfo.needUpdate = false
            result.data.otaUpdateInfo.tips = `Version ${version}`;
            result.tips = `已是最新版本`;
            return
        }
        result.data.otaUpdateInfo.needUpdate = res.data.needUpdate;
        if (result.data.otaUpdateInfo.needUpdate) {
            result.tips = "有新版本可用"
        } else {
            result.tips = `已是最新版本`;
        }
        result.data.otaUpdateInfo.updateData = { ...res.data };
        console.log(version);
        console.log("getOtaVersion", JSON.stringify(deviceInfo));
    },

    async getAppVersion() {
        let version = await androidApi.queryAppVersion();
        if (!version) {
            result.data.appUpdateInfo.needUpdate = false;
            return
        }
        result.data.appUpdateInfo.version = version
        result.data.appUpdateInfo.url = `${url}ANDROID_APP/${version}`;
        let res = await this.fetchData(result.data.appUpdateInfo.url);

        if (!res || !res.data) {
            result.data.appUpdateInfo.needUpdate = false
            result.data.appUpdateInfo.tips = `Version ${version}`;
            return
        }
        result.data.appUpdateInfo.needUpdate = res.data.needUpdate;
        if (result.data.appUpdateInfo.needUpdate) {
            result.data.appUpdateInfo.tips = "有新版本可用"
        } else {
            result.data.appUpdateInfo.tips = `Version ${version}`;
        }
        result.data.appUpdateInfo.updateData = { ...res.data };
    },

    // 调用接口
    async fetchData(url) {
        try {
            const response = await fetch(url, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
            });
            const data = await response.json();
            // 在这里处理返回的数据
            return data;
        } catch (error) {
            // 在这里处理错误
            console.error("Fetch error:", error);
            return null;
        }
    },

    setErrorMsg(msg) {
        result.errorCode = -1
        result.errorMsg = msg;
        Toast.fail({ message: msg, position: "top" })
        return result;
    },

    lowBatteryError(msg) {
        result.tips = "设备电量低"
        // Toast.fail({ message: msg, position: "top" })
        result.data.dfuUpdateInfo.errorCode = -1
        result.data.otaUpdateInfo.errorCode = -1

        result.data.dfuUpdateInfo.errorMsg = msg
        result.data.otaUpdateInfo.errorMsg = msg
        return result;
    },

    unConnectError(msg) {
        result.tips = "设备未连接"
        result.data.dfuUpdateInfo.errorCode = -1
        result.data.otaUpdateInfo.errorCode = -1

        result.data.dfuUpdateInfo.errorMsg = msg
        result.data.otaUpdateInfo.errorMsg = msg
        return result;
    },

    convertVersion(version) {
        // 使用正则表达式匹配版本号中的数字和字母部分
        const regex = /(\d+|[A-Za-z]+)/g;
        const matches = version.match(regex);

        if (!matches) {
            // 版本号不符合预期格式
            return version;
        }

        // 转换每个部分的值
        const convertedParts = matches.map((part, index) => {
            // 如果是数字部分，则移除前导零
            if (/^\d+$/.test(part)) {
                return parseInt(part, 10).toString();
            }
            // 如果是字母部分，则将大写字母转换为小写
            return part.toLowerCase();
        });

        // 连接转换后的部分并返回结果
        return convertedParts.join('.');
    }


}
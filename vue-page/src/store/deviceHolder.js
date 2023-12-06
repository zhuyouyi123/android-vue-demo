export default {

    userInfo:{
        
    },
    needRefresh: true,
    firstUseTime: "",
    // 设备绑定信息
    bindingInfo: {
        status: false,
        time: 0,
    },
    // 设备信息
    deviceInfo: {
        battery: -1,
        // 心率
        heartRate: 0,
        // 步数信息
        stepInfo: {
            stepNumber: 0,
            mileage: 0,
            calories: 0
        },
        // 佩戴状态
        wearingStatus: false,
        // 血液信息
        bloodPressureInfo: {
            // 血氧
            bloodOxygen: 0,
            // 收缩压
            systolicPressure: '未知',
            // 舒张压
            diastolicPressure: '未知',
            // 血液粘稠度
            bloodViscosity: 0
        },
    },

    // 步数信息
    stepStatisticsInfo: {
        xAxis: [],
        hourlyData: [],
    },
    calorieStatisticsInfo: {
        xAxis: [],
        hourlyData: [],
    },
}
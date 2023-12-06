import deviceHolder from '@/store/deviceHolder'
import dateUtil from './dateUtil';
import androidApi from '@/api/android-api';
export default {

    calcChartData(type, xAxisData, name = '步数') {
        return new Promise((resolve, reject) => {
            let chartMap = new Map();
            androidApi.getHistoryData(type).then((data) => {
                console.log(JSON.stringify(data));
                data.forEach((item) => chartMap.set(item.date + "", item));

                // 获取第一次使用时间
                let firstUseTime = deviceHolder.firstUseTime;
                // 计算chart size
                let chartSize = dateUtil.getDaysDiff(null, firstUseTime);

                let chartOptionsArray = [];

                for (let i = 0; i < chartSize; i++) {
                    let dateKey = dateUtil.getDateByOffset(i, true);
                    console.log("dateKey", dateKey);
                    let chartData = chartMap.get(dateKey);
                    console.log("chartData", JSON.stringify(chartData));
                    // 是否显示
                    let show = (chartData && chartData.hourlyData) ?
                        !this.allZeros(chartData.hourlyData) : false;
                    chartOptionsArray.push({
                        xAxis: {
                            data: show ? xAxisData ? xAxisData : dateUtil.default24Date : [],
                        },
                        series: [
                            {
                                data: (chartData && chartData.hourlyData) ? this.handleHourlyData(chartData.hourlyData) : [],
                                name: name,
                            },
                        ],
                        show: show,
                        chartData: (chartData && chartData.extendedInfo) ? chartData.extendedInfo : '',
                    });
                }

                chartOptionsArray = chartOptionsArray.reverse();
                resolve({
                    chartSize: chartSize,
                    chartOptionsArray: chartOptionsArray
                });
            }).catch(error => {
                resolve({ "chartSize": 5, "chartOptionsArray": [{ "xAxis": { "data": [] }, "series": [{ "data": [], "name": "体温" }], "show": false, "chartData": "" }, { "xAxis": { "data": [] }, "series": [{ "data": [], "name": "体温" }], "show": false, "chartData": "" }, { "xAxis": { "data": [] }, "series": [{ "data": [], "name": "体温" }], "show": false, "chartData": "" }, { "xAxis": { "data": ["00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", "23:59"] }, "series": [{ "data": [0, 0, 0, 0, 0, 0, 0, 0, 0, 41.02, 39.84, 40.13, 39.98, 39.53, 38.99, 39.41, 39.24, 39.13, 39.76, 38.49, 0, 0, 0, 0], "name": "体温" }], "show": true, "chartData": { "ave": 39.59, "max": 41.02, "min": 38.49 } }, { "xAxis": { "data": ["00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", "23:59"] }, "series": [{ "data": [0, 0, 0, 0, 0, 0, 0, 0, 0, 42.12, 40.83, 40.65, 40.07, 40.33, 40.3, 0, 0, 0, 0, 0, 0, 0, 0, 0], "name": "体温" }], "show": true, "chartData": { "ave": 40.72, "max": 42.12, "min": 40.07 } }] })
            });

        })

    },

    handleHourlyData: (arr) => arr.map(e => {
        if (e == 0) {
            return ''
        }
        return e
    }),


    allZeros: (arr) => arr.every((element) => element === 0),

}
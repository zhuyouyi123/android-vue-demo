import deviceHolder from '@/store/deviceHolder'
import dateUtil from './dateUtil';
import androidApi from '@/api/android-api';
var day_7 = ["周一", "周二", "周三", "周四", "周五", "周六", "周日"];
export default {
    calcChartData(type, xAxisData, name = "", dateType = 1, currIndex) {
        return new Promise((resolve, reject) => {
            switch (dateType) {
                case 1:
                    this.handleDay(type, xAxisData, name, dateType, currIndex).then(res => resolve(res)).catch((error) => (error))
                    break
                case 2:
                    this.handleWeek(type, name, dateType, currIndex).then(res => resolve(res)).catch((error) => (error))
                    break
                case 3:
                    this.handleMonth(type, name, dateType, currIndex).then(res => resolve(res)).catch((error) => (error))
                    break
                default:
                    break;
            }

        })
    },


    /**
     * 
     * @param {类型} type 
     * @param {xAxis} xAxisData 
     * @param {名称} name 
     * @param {日期类型} dateType 
     * @returns 数据
     */
    handleDay(type, xAxisData, name, dateType, currIndex) {
        return new Promise((resolve, reject) => {
            androidApi.getHistoryData(type, dateType, currIndex).then((data) => {
                let chartSize = data.chartSize;
                let chartOptionsArray = [];

                for (let i = 0; i < chartSize; i++) {
                    if (data.dataIndex == i) {
                        if (data.isMultiple) {
                            chartOptionsArray.push(this.getMultipleOptions(xAxisData, data.data.dataList, name, !this.allMultipleZeros(data.data.dataList)));
                        } else {
                            chartOptionsArray.push(this.getSingleOptions(xAxisData, data.list[0].hourlyData, name, !this.allZeros(data.list[0].hourlyData)));
                        }
                    } else {
                        chartOptionsArray.push({
                            xAxis: { data: xAxisData ? xAxisData : dateUtil.default24Date },
                            series: [
                                {
                                    data: [],
                                    name: name,
                                },
                            ],
                            show: false,
                        })
                    }
                }

                chartOptionsArray = chartOptionsArray.reverse();
                resolve({
                    chartSize: chartSize,
                    chartOptionsArray: chartOptionsArray,
                    extendedInfo: {
                        average: data.average,
                        max: data.max,
                        min: data.min
                    }
                });
            }).catch(error => {
                resolve({ "chartSize": 5, "chartOptionsArray": [{ "xAxis": { "data": [] }, "series": [{ "data": [], "name": "体温" }], "show": false, "chartData": "" }, { "xAxis": { "data": [] }, "series": [{ "data": [], "name": "体温" }], "show": false, "chartData": "" }, { "xAxis": { "data": [] }, "series": [{ "data": [], "name": "体温" }], "show": false, "chartData": "" }, { "xAxis": { "data": ["00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", "23:59"] }, "series": [{ "data": [0, 0, 0, 0, 0, 0, 0, 0, 0, 41.02, 39.84, 40.13, 39.98, 39.53, 38.99, 39.41, 39.24, 39.13, 39.76, 38.49, 0, 0, 0, 0], "name": "体温" }], "show": true, "chartData": { "ave": 39.59, "max": 41.02, "min": 38.49 } }, { "xAxis": { "data": ["00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", "23:59"] }, "series": [{ "data": [0, 0, 0, 0, 0, 0, 0, 0, 0, 42.12, 40.83, 40.65, 40.07, 40.33, 40.3, 0, 0, 0, 0, 0, 0, 0, 0, 0], "name": "体温" }], "show": true, "chartData": { "ave": 40.72, "max": 42.12, "min": 40.07 } }] })
            });
        })
    },

    handleMonth(type, name, dateType, currIndex) {
        return new Promise((resolve, reject) => {
            androidApi.getHistoryData(type, dateType, currIndex).then((data) => {
                let chartSize = data.chartSize;
                let chartOptionsArray = [];

                for (let i = 0; i < chartSize; i++) {
                    if (data.dataIndex == i) {
                        if (data.isMultiple) {
                            chartOptionsArray.push(this.getMultipleOptions(data.data.xAxis, data.data.dataList, name, !this.allZeros(data.data.dataList[0]), data.needSoar));
                        } else {
                            chartOptionsArray.push({
                                xAxis: { data: data.list[0].xAxis },
                                series: [
                                    {
                                        data: data.list[0].dayData,
                                        name: name,
                                    },
                                ],
                                show: true,
                            })
                        }

                    } else {
                        chartOptionsArray.push({
                            xAxis: { data: [] },
                            series: [
                                {
                                    data: [],
                                    name: name,
                                },
                            ],
                            show: false,
                        })
                    }
                }

                chartOptionsArray = chartOptionsArray.reverse();

                resolve({
                    chartSize: chartSize,
                    chartOptionsArray: chartOptionsArray,
                    extendedInfo: {
                        average: data.average,
                        max: data.max,
                        min: data.min
                    }
                });
            })
                .catch(() => {

                })
        })
    },

    handleWeek(type, name, dateType, currIndex) {
        return new Promise((resolve, reject) => {
            androidApi.getHistoryData(type, dateType, currIndex).then((data) => {
                let chartSize = data.chartSize;
                let chartOptionsArray = [];
                for (let i = 0; i < chartSize; i++) {
                    if (data.dataIndex == i) {
                        if (data.isMultiple) {
                            chartOptionsArray.push(this.getMultipleOptions(day_7, data.data.dataList, name, !this.allZeros(data.data.dataList[0]), data.needSoar));
                        } else {
                            chartOptionsArray.push(this.getSingleOptions(day_7, data.list[0].dayData, name, !this.allZeros(data.list[0].dayData)));
                        }
                    } else {
                        chartOptionsArray.push({
                            xAxis: { data: ["周一", "周二", "周三", "周四", "周五", "周六", "周日"] },
                            series: [
                                {
                                    data: [],
                                    name: name,
                                },
                            ],
                            show: false,
                        })
                    }
                }

                chartOptionsArray = chartOptionsArray.reverse();

                resolve({
                    chartSize: chartSize,
                    chartOptionsArray: chartOptionsArray,
                    extendedInfo: {
                        average: data.average,
                        max: data.max,
                        min: data.min
                    }
                });
            })
                .catch(() => {

                })
        })
    },


    handleHourlyData: (arr) => arr.map(e => {
        if (e == 0) {
            return ''
        }
        return e
    }),

    allMultipleZeros: (arr) => {
        return arr[0].length == 0 || arr[0].every((element) => element === 0 || element == '0.0' || element === '0' || !element)
    },

    allZeros: (arr) => {
        return arr.length == 0 || arr.every((element) => element === 0 || element == '0.0' || element === '0' || !element)
    },

    getOptions: (xAxis, data, name, show) => {
        return {
            xAxis: { data: xAxis ? xAxis : dateUtil.default24Date },
            series: [
                {
                    data: data,
                    name: name,
                },
            ],
            show: show,
        }
    },

    getMultipleOptions: (xAxis, data, name, show, needSoar = false) => {
        let dataArr = [];
        if (needSoar) {
            let dataA = data[0];
            let dataB = data[1];
            for (let i = 0; i < dataB.length; i++) {
                const item = dataB[i];
                const res = item - dataA[i];
                if (item == 0) {
                    dataArr.push(0);
                } else {
                    dataArr.push(res <= 0 ? 1 : res)
                }
            }
        } else {
            dataArr = data[1];
        }
        return {
            xAxis: { data: xAxis ? xAxis : dateUtil.default24Date },
            series: [
                {
                    data: show ? data[0] : [],
                    name: show ? name[0] : [],
                    soar: needSoar
                },
                {
                    data: show ? dataArr : [],
                    name: show ? name[1] : [],
                },
            ],
            show: show,
            needSoar: needSoar
        }
    },

    getSingleOptions: (xAxis, data, name, show) => {
        return {
            xAxis: { data: xAxis ? xAxis : dateUtil.default24Date },
            series: [
                {
                    data: show ? data : [],
                    name: show ? name : [],
                }
            ],
            show: show,
        }
    },

    getAverage: (data) => {
        if (data.isMultiple) {
            return !data.average || data.average.length == 0 ? '-' : data.average[0] + "-" + data.average[1]
        } else {
            return type == '0B' ? data.average : parseInt(data.average);
        }
    }
}
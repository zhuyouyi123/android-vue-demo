var date_week = [
    "周一",
    "周二",
    "周三",
    "周四",
    "周五",
    "周六",
    "周日",
]


var date_12 = [
    "00:00",
    "01:00",
    "02:00",
    "03:00",
    "04:00",
    "05:00",
    "06:00",
    "07:00",
    "08:00",
    "09:00",
    "10:00",
    "11:00",
]

var date_24 = () => {
    let arr = [];
    for (let i = 1; i <= 24; i++) {
        if (i == 1) {
            arr.push('今日0时')
        } else {
            arr.push(i)
        }

    }
    return arr;
};

var data_7 = () => {

};

var data_24 = () => {
    let arr = [];
    for (let i = 1; i <= 24; i++) {
        arr.push(Math.floor(Math.random() * 20))
    }
    return arr;
};

var data_12 = [60, 72, 88, 65, 76, 64, 75, 85, 46, 63, 74, 63]
var data_100 = [60, 60, 20, 20, 76, 64, 75, 85, 46, 63, 60, 60, 20,]

export default {
    default24Data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
    default24Date: ["00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", "23:59"],
    customizedFirst24Date: ["今日0时", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", "23:59"],
    calorieData() {
        return {
            xAxis: {
                data: [
                    "23:33",
                    "23:33",
                    "23:33",
                    "23:33",
                    "23:33",
                    "23:33",
                    "23:33",
                    "23:33",
                    "23:33",
                    "23:33",
                    "23:33",
                    "23:33",
                ],
            },
            series: [
                {
                    data: [
                        2000, 2000, 2000, 2000, 2000, 2000, 2000, 2000, 2000, 2000, 2000,
                        2000,
                    ],
                },
            ],

            grid: {
                top: "1%",
                left: "8%",
                bottom: "0",
            },
        }
    },

    sleepChartOptions: {
        sleepData: [0, 1, 2, 1, 3, 0, 0, 0, 2, 1, 3, 0],
        xAxis: {
            data: JSON.parse(JSON.stringify(date_12)),
        },
        series: [
            {
                data: JSON.parse(JSON.stringify(data_12)),
            },
        ],

        grid: {
            top: "1%",
            left: "6%",
            bottom: "0",
        },
    },

    sleepChartOptionsV2: {
        sleepData: [0, 1, 2, 1, 3, 0, 0, 0, 2, 1, 3, 0],
        xAxis: {
            data: JSON.parse(JSON.stringify(data_100)),
        },
        series: [
            {
                data: JSON.parse(JSON.stringify(data_100)),
            },
        ],

        grid: {
            top: "15%",
            left: "10%",
            bottom: "15%",
        },
    },

    sleepChartOptionsWeek: {
        xAxis: {
            type: 'category',
            data: ['9', '10', '11', '12', '13', '14', '今']
        },
        yAxis: {
            type: 'value'
        },
        series: [{
            data: [1000, 2000, 1500, 3000, 2500, 1800, 2200],
            type: 'bar',
            itemStyle: {
                color: '#1d36a7'
            },
            stack: 'stack'
        }, {
            data: [800, 1700, 1300, 2400, 2100, 1600, 2000],
            type: 'bar',
            itemStyle: {
                color: 'red'
            },
            stack: 'stack'
        },
        {
            data: [800, 1700, 1300, 2400, 2100, 1600, 2000],
            type: 'bar',
            stack: 'stack'
        }],
        grid: {
            top: "15%",
            left: "12%",
            bottom: "10%",
        },
    },

    heartRateData: {
        xAxis: {
            data: JSON.parse(JSON.stringify(date_12)),
        },
        series: [
            {
                data: JSON.parse(JSON.stringify(data_12)),
            },
        ],

        grid: {
            top: "1%",
            left: "5%",
            bottom: "0",
        },
    },

    heartRateDataV2: {
        xAxis: {
            data: JSON.parse(JSON.stringify(date_12)),
        },
        series: [
            {
                data: JSON.parse(JSON.stringify(data_12)),
            },

        ],
        grid: {
            top: "15%",
            left: "13%",
            bottom: "10%",
        },
    },

    calorieData: {
        xAxis: {
            data: JSON.parse(JSON.stringify(date_24())),
        },
        series: [
            {
                data: JSON.parse(JSON.stringify(data_24())),
            },
        ],
    },

    temperatureData: {
        xAxis: {
            data: JSON.parse(JSON.stringify(date_week))
        },
        series: [
            {
                type: 'bar',
                stack: 'Total',
                itemStyle: {
                    borderColor: 'transparent',
                    color: 'transparent'
                },
                data: [0, 1700, 1400, 1200, 300, 0]
            },
            {
                name: 'Life Cost',
                type: 'bar',
                stack: 'Total',
                data: [2900, 1200, 300, 200, 900, 300]
            }
        ]
    },

    bloodPressureData: {
        xAxis: {
            data: JSON.parse(JSON.stringify(date_24())),
        },
        series: [
            {
                name: '高压',
                data: JSON.parse(JSON.stringify(data_24())),
            },
            {
                name: '低压',
                data: JSON.parse(JSON.stringify(data_24())),
            },
        ],
        grid: {
            // // 设置上边距为 10%
            top: "13%",
            // // 设置下边距为 10%
            bottom: "20%",
            left: "15%",
        }
    },
}
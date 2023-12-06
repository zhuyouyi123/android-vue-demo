export default {

    default24Data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
    default24Date: ["00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", "23:59"],
    customizedFirst24Date: ["今日0时", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", "23:59"],
    /**
     * 根据类型获取日期文本
     * @param {时间类型} timeActive 
     */
    getDateTextByActive2(timeActive) {
        const currentDate = new Date();

        switch (timeActive) {
            case 0:
                return `${currentDate.getMonth() + 1}月${currentDate.getDate()}日`;
            case 1:
                const startOfWeek = new Date(currentDate);
                startOfWeek.setDate(currentDate.getDate() - currentDate.getDay() + 1);
                const endOfWeek = new Date(currentDate);
                endOfWeek.setDate(currentDate.getDate() - currentDate.getDay() + 7);
                return `${startOfWeek.getMonth() + 1}月${startOfWeek.getDate()}日 -
                ${endOfWeek.getMonth() + 1}月${endOfWeek.getDate()}日`;
            case 2:
                return `${currentDate.getFullYear()}年${currentDate.getMonth() + 1}月`;
            case 3:
                return `${currentDate.getFullYear()}年`;
            default:
                return "";
        }
    },

    getDateTextByActive(timeActive, offset = 0) {
        const currentDate = new Date();
        const previousDay = new Date(currentDate);
        const startOfWeek = new Date(currentDate);
        const endOfWeek = new Date(currentDate);
        const previousMonth = new Date(currentDate);
        const previousYear = new Date(currentDate);

        switch (timeActive) {
            case 0:
                previousDay.setDate(currentDate.getDate() - offset);
                return `${previousDay.getMonth() + 1}月${previousDay.getDate()}日`;
            case 1:
                startOfWeek.setDate(currentDate.getDate() - currentDate.getDay() + 1 - (7 * offset));
                endOfWeek.setDate(currentDate.getDate() - currentDate.getDay() + 7 - (7 * offset));
                return `${startOfWeek.getMonth() + 1}月${startOfWeek.getDate()}日 - ${endOfWeek.getMonth() + 1}月${endOfWeek.getDate()}日`;
            case 2:
                previousMonth.setMonth(currentDate.getMonth() - offset);
                return `${previousMonth.getFullYear()}年${previousMonth.getMonth() + 1}月`;
            case 3:
                previousYear.setFullYear(currentDate.getFullYear() - offset);
                return `${previousYear.getFullYear()}年`;
            default:
                return "";
        }
    },

    /**
     * 这个函数接受一个整数类型的参数offset，表示要获取的日期相对于当前日期的偏移量。
     * @param {偏移} offset 
     * @returns 
     */
    getDateByOffset(offset, isKey = false) {
        var today = new Date();
        today.setDate(today.getDate() - offset);
        if (isKey) {
            var year = today.getFullYear();
            var month = String(today.getMonth() + 1).padStart(2, '0');
            var day = String(today.getDate()).padStart(2, '0');
            return year + month + day;
        }
        return today.toISOString().split('T')[0];
    },


    /**
     * 该函数接收一个时间戳作为参数，并返回当前时间与给定时间戳之间的分钟差。
     * @param {时间戳} timestamp 
     * @returns 
     */
    getTimeDiffInMinutes(timestamp) {
        if (timestamp < 10000) {
            return "无效时间";
        }
        const currentTime = new Date();
        const targetTime = new Date(timestamp);

        // 计算时间差（单位：毫秒）
        const timeDiff = currentTime - targetTime;

        // 转换为分钟并取整
        const minutesDiff = Math.floor(timeDiff / 60000);

        if (minutesDiff < 1) {
            return "刚刚";
        }

        return minutesDiff + "分钟";
    },

    getDaysDiff(date1, date2) {
        // 定义日期格式的正则表达式
        const pattern = /^(\d{4})(\d{2})(\d{2})$/;
        // 使用正则表达式提取年、月、日等信息
        const d1 = new Date();

        if (date1) {
            const [, year1, month1, day1] = pattern.exec(date1);
            d1 = new Date(year1, month1 - 1, day1);
        }

        const [, year2, month2, day2] = pattern.exec(date2);
        const d2 = new Date(year2, month2 - 1, day2);

        // 计算相差的天数
        const diffTime = Math.abs(d1 - d2);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        return diffDays;
    }
}
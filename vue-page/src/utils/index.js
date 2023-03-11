/**
 * 左补全
 * @param {Object} num
 * @param {Object} length
 */
const padLeft = function(num, length) {
    return (num + '').padStart(length, "0") || ("0000000000000000" + num).substr(-length);
}



//获取两个日期之间的时间差
export function getDayDiff(endDate, startDate) {
    startDate = startDate || _getNowDate();
    startDate = typeof startDate == "string" ? new Date(startDate) : startDate;
    endDate = typeof endDate == "string" ? new Date(endDate) : endDate;
    var s1 = startDate.getTime(),
        s2 = endDate.getTime();
    var total = (s2 - s1) / (1000 * 24 * 60 * 60);
    var day = parseInt(total); //计算整数天数
    return day;
}

//获取两个日期之间的年差
export function getYearDiff(endDate, startDate) {
    startDate = startDate || _getNowDate();
    startDate = typeof startDate == "string" ? new Date(startDate) : startDate;
    endDate = typeof endDate == "string" ? new Date(endDate) : endDate;
    var s1 = startDate.getFullYear(),
        s2 = endDate.getFullYear();
    var year = s2 - s1
    return year;
}

//获取两个日期之间的时间差
export function getMonthDiff(endDate, startDate) {
    startDate = startDate || _getNowDate();
    startDate = typeof startDate == "string" ? new Date(startDate) : startDate;
    endDate = typeof endDate == "string" ? new Date(endDate) : endDate;
    var s1 = startDate.getMonth(),
        s2 = endDate.getMonth();
    var month = s2 - s1 + (getYearDiff(endDate, startDate) * 12)
    return month;
}

const _getNowDate = function(addDay = 0, date = new Date()) {
        date = date || new Date();
        date = date instanceof Date ? date : new Date(date);
        addDay = addDay || 0;
        date.setDate(date.getDate() + addDay);
        return date.getFullYear() + "-" + padLeft((date.getMonth() + 1), 2) + "-" + padLeft(date.getDate(), 2);
    }
    /**
     * 获取当前日期时间
     * @param {Object} addDay
     * @param {Object} date
     */
export function getNowDateTime(addDay = 0, date = new Date()) {
    date = date || new Date();
    date = date instanceof Date ? date : new Date(date);
    addDay = addDay || 0;
    date.setDate(date.getDate() + addDay);
    return _getNowDate(addDay, date) +
        " " + padLeft(date.getHours(), 2) + ":" + padLeft(date.getMinutes(), 2) + ":" + padLeft(date
            .getSeconds(),
            2);
}

/**
 * 获取当前日期
 * @param {Object} addDay
 * @param {Object} date
 */
export function getNowDate(addDay = 0, date = new Date()) {
    return _getNowDate(addDay, date);
}

/**
 * 获取日期对因的周几
 * @param {Object} dateTime 指定日期
 * @param {Object} lang :cn / en /en_simple 显示的格式 中文/英文/英文简写
 */
export function getWeek(dateTime, lang = "cn") {
    if (dateTime && ['cn', 'en', 'en_simple'].includes(dateTime.toLocaleLowerCase())) {
        lang = dateTime;
        dateTime = null;
    }
    lang = lang.toLocaleLowerCase() || 'cn';
    var now = null;
    try {
        now = !dateTime ? new Date() : new Date(dateTime);
    } catch (e) {
        throw `输入参数dateTime=${dateTime} 错误`;
    }
    var day = now.getDay();
    var weeks = lang === 'cn' ? ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"] : ["Sunday",
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    ];
    var week = weeks[day];
    return lang == 'en_simple' ? week.substring(0, 3) : week;
}


// /**
//  * 获取本地图片，防止图片在打包编译后路劲出现问题
//  * @param {Object} name 图片名 xxx.png
//  */
// export function getLocalImagePath(name) {
// 	return new URL(`../assets/imgs/${name}`,
// 		import.meta.url).href;
// }
/**
 * 产生随机数字
 * @param {number} len 
 * @returns 
 */
export function randomChar(len) {
    len = len || 5;
    var x = "0123456789qwertyuioplkjhgfdsazxcvbnm";
    var tmp = "";
    var timestamp = new Date().getTime();
    for (var i = 0; i < len; i++) {
        tmp += x.charAt(Math.ceil(Math.random() * 100000000) % x.length);
    }
    return tmp;
}

/**
 * 评分获取星数
 * @param {Object} rate
 */
export function getRate(rate) {
    return "★★★★★☆☆☆☆☆".slice(5 - rate, 10 - rate)
}

/**
 * 深度复制
 * obj{any}要复制的对象
 * hashWeakMap {WeakMap} 软引用解决循环引用问题
 */
const deepClone = function(obj, hashWeakMap = new WeakMap()) {
    if (obj === null) {
        return null;
    }
    if (!['[object Object]', '[object Array]'].includes(Object.prototype.toString.call(obj))) {
        return obj;
    }

    if (hashWeakMap.has(obj)) {
        return hashWeakMap.get(obj);
    }

    const resObj = Array.isArray(obj) ? [] : {};

    hashWeakMap.set(obj, resObj);

    Reflect.ownKeys(obj).forEach(key => {
        resObj[key] = deepClone(obj[key], hashWeakMap);
    });

    return resObj;
}

/**
 * JS 复制函数
 * @param {Object} val
 */
export function copy(val) {
    return deepClone(val);
}
const _base = '/apis';
export const baseApp='/app';
export const baseApi = _base;

/**
 * 平台、设备和操作系统 ，返回true或false,true表示是移动端，false表示不是移动端
 */
function _isMobile() {
    var mobileArray = ["iPhone", "iPad", "Android", "Windows Phone", "BB10; Touch", "BB10; Touch", "PlayBook", "Nokia"];
    var ua = navigator.userAgent;
    console.log(ua);
    var res = mobileArray.filter(key => {
        return ua.indexOf(key) > 0;
    });
    return res.length > 0;
}
export const isMobile = _isMobile();
/**
 * 根据不同的平台设置不同的token
 */
export const tokenName = _isMobile() ? '_AppCustomerToken' : '_PcCustomerToken';


/**
 * 放行请求路径
 */
export const excludesRequestPath = [
    `${_base}/api/machine/checks/login`
];

/**
 * 放行Router路径
 */
export const excludesRouterPath = [
    `/home`,
];

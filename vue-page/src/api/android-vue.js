import apiRequest from '@/api/server/android-proxy.js'; //使用请求调用csharp方法获取数据

const install = (Vue, opts = {}) => {

    Vue.prototype.$requestInit = function(axios) {
        return apiRequest.axiosInit(axios);
    };
    Vue.prototype.$ano = {
        //请求
        requestSetting: {
            //配置
            config: {

            },
            //拦截器
            interceptor: apiRequest.interceptor,
        },
        //请求方法
        requestSync: apiRequest.requestSync,
        request: apiRequest.request,
        postRequest: apiRequest.postRequest,
        getRequest: apiRequest.getRequest,
    }
}

/* istanbul ignore if */
if (typeof window !== 'undefined' && window.Vue) {
    install(window.Vue);
}

export default {
    request: apiRequest.request,
    postRequest: apiRequest.postRequest,
    getRequest: apiRequest.getRequest,
    install
}
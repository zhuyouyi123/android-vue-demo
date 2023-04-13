/**axios封装
 * 请求拦截、相应拦截、错误统一处理
 */
import axios from 'axios'
import {
  tokenName,
  excludesRequestPath
} from './base.js';
import {
  Message
} from 'element-ui';


const getToken = function () {
  const tokenStr = localStorage.getItem(tokenName);
  let token = "";
  try {
    const userInfo = JSON.parse(tokenStr);
    token = userInfo.token || userInfo.username;
  } catch (e) {

  }
  return token;
}
// 环境跟目录
const baseURL = '';

const _config = {
  baseURL: baseURL, // url = base url + request url
  withCredentials: true, // send cookies when cross-domain requests
  timeout: 30000, // request timeout
  headers: {
    "Content-Type": "application/json;charset=UTF-8"
  }
}

const service = axios.create(_config)
const successCode = 200;


// 请求拦截器
service.interceptors.request.use(
  config => {

    if (!excludesRequestPath.includes(config.url)) {
      //添加token
      const token = getToken();
      if (!token) {
        /**
         * 通过vue-router 进行前置导航守卫进行token验证和登录，因此这里只需要刷新页面，其他的交给导航首位处理
         */
        location.reload();

        return;
      }
      //添加header
      token && (config.headers.Authorization = token, config.headers['X-Token'] = token);

    }
    return config;
  },
  error => {
    return Promise.reject(error)
  })

// 响应拦截器

service.interceptors.response.use(
  response => {
    if (response.status === 200) {
      // 抛出异常
      if (response.data.code != successCode) {
        return Promise.reject(errorCode(response.data.code, response));
      }
      return Promise.resolve(response);
    } else {
      Message({
        message: response.message || response.data.message || response.errMsg || response.data.errMsg || '网络请求失败，未知错误！',
        type: 'error'
      });
      return Promise.reject(response);
    }
  },
  // 服务器状态码不是200的情况
  error => {
    if (error.response.status) {
      errorCode(error.response.status, error.response);
      return Promise.reject(error.response);
    }
  }
);
/**
 * 状态码
 * @param {number,string}} code 
 * @param {*} response 
 * @returns 
 */
const errorCode = function (code, response) {
  Message.closeAll();
  switch (code) {
    /**
     * 401:  未登录
     * 未登录则跳转登录页面，并携带当前页面的路径
     * 在登录成功后返回当前页面，这一步需要在登录页操作。
     */
    case 401:
      Message({
        message: response.data.msg || response.message || '网络请求失败，未知错误！',
        type: 'error'
      });
      localStorage.setItem(tokenName, '');
      //通过vue-router 进行前置导航守卫进行token验证和登录，因此这里只需要刷新页面，其他的交给导航首位处理
      window.location.reload();

      break;
      /**
       *  403 token过期
       *  清除本地token和清空vuex中token对象
       *  跳转登录页面
       */
    case 403:
      Message({
        message: "登录过期，请重新登录",
        type: 'error'
      });
      // 清除token
      localStorage.setItem(tokenName, '');
      //通过vue-router 进行前置导航守卫进行token验证和登录，因此这里只需要刷新页面，其他的交给导航首位处理
      window.location.reload();

      break;
      /**
       * 其他错误
       */
    default:
      Message({
        message: error.response.data.message || error.response.message || '网络请求失败，未知错误！',
        type: 'error'
      });
  }
  return response;
}

/**
 * 统一接管不同请求返回的结果处理
 */
/**
 * 统一接管不同请求返回的结果处理
 */
const promiseMethods = function (method, resolve, reject) {
  method.then(res => {
    if (res.data.code != successCode) {
      reject(res.data)
      return;
    }
    resolve(res.data.data);
  }).catch(err => {
    reject(err.data)
  })
}



/**
 * get方法，对应get请求
 * @param {String} url [请求的url地址]
 * @param {Object} params [请求时携带的参数]
 */
export function get(url, params) {
  return new Promise((resolve, reject) => {
    promiseMethods(service.get(url, {
      params: params
    }), resolve, reject);
  });
}

/**
 * post方法，对应post请求
 * @param {String} url [请求的url地址]
 * @param {Object} params [请求时携带的参数]
 */
export function post(url, params) {
  return new Promise((resolve, reject) => {
    promiseMethods(service.post(url, params), resolve, reject);
  });
}
/**
 * 删除
 * @param {Object} url
 * @param {Object} params
 */
export function deletes(url, params) {
  return new Promise((resolve, reject) => {
    promiseMethods(service.delete(url, {
      params,
      data: params
    }), resolve, reject);
  });
}

/**
 * put
 * @param {Object} url
 * @param {Object} params
 */
export function put(url, params) {
  return new Promise((resolve, reject) => {
    promiseMethods(service.put(url, params), resolve, reject);
  });
}





export {
  service as request
}

try {
    window.androidJS = typeof androidJS == 'undefined' ? {
      executeMethod: function (router, method, params) {
        console.log(`router:${router}  \r\t method:${method} \r\t params:${params}\r\n `);
      }
    } : androidJS;
  } catch (e) {
    //TODO handle the exception
  }
  
  const methodType = [
    "GET",
    "HEAD",
    "POST",
    "PUT",
    "PATCH",
    "DELETE",
    "OPTIONS",
    "TRACE",
  ]
  const _request = {
    //
    config: {
      timeout: 1000 * 60,
      //是否使用mockjs作为测试数据
      isMock: false,
    },
  
    //拦截器
    interceptor: {
      /**
       * 请求之前调用的方法
       * @param {object} 请求之前的所构造参数
       * @return {Boolean|Promise} ,
       *        Boolean true,继续执行，false:终止执行
       *        Promise 需要使用 Promise.resolve(Boolean:true|false) true,false 表示是否继续执行
       */
      before: (event) => {
        return true;
      },
      /**
       * 请求之后调用的方法,对返回数据进行加工操作
       * @param {object} 请求之前的所构造参数
       * @return {Promise,Object}  ,
       *        Object 被加工后的数据
       *        Promise 需要使用 Promise.resolve(event) ;event 表示加工后返回的数据
       */
      after: (event) => {
        return event;
      },
      // /**
      //  * 请求超时处理
      //  */
      // timeout: (result) => {
      //   return result;
      // },
      /**
       * 异常处理程序
       */
      error: (error, option) => {
        return error;
      }
    },
  }
  
  const requestSync = function (router, method = "GET", params = {}) {
    if (typeof method !== 'string' || (typeof method === 'string' && !methodType.includes(method.toUpperCase()))) {
      params = method;
      method = "GET";
    }

    const getinterceptor = () => {
      try {
        return (this.$ano && this.$ano.requestSetting ? this.$ano.requestSetting : this.requestSetting ? this.requestSetting : _request)
          .interceptor;
      } catch (e) {
        return {
          before: (event) => true,
          after: (event) => event,
        };
      }
    }
  
    let interceptor = getinterceptor();
    let option = {
      method: method,
      url: router,
      data: params || ''
    };
    let event = {
      options: option,
      data: null,
      interceptor: 'before',
    };
  
    try {
      if (window.androidJS && window.androidJS.executeMethod && typeof window.androidJS.executeMethod ==
        'function') {
        let data = typeof params == 'object' ? JSON.stringify(params) : (params || '');
  
        //请求前拦截
  
        let flg = interceptor.before(event);
        if (!flg) {
          return {
            code: '-1',
            msg: '请求在进过拦截器验证时，验证失败！'
          };
        }
  
        let result = window.androidJS.executeMethod(router, method || 'GET', data);
        result = eval(`(${result})`);
        try {
          result['data'] = JSON.parse(result['data']);
        } catch (e) {
          //TODO handle the exception
        }
        return interceptor.after(result);
      }
      return null;
    } catch (err) {
      if (typeof interceptor.error == 'function') {
        interceptor.error(err, option);
      }
    }
  };
  
  export default {
  
    requestSync: requestSync,
    getRequest: function (router, params = {}) {
      return requestSync(router, "GET", params);
    },
    postRequest: function (router, params = {}) {
      return requestSync(router, "POST", params);
    },
    headRequest: function (router, params = {}) {
      return requestSync(router, "HEAD", params);
    },
    request: (router, method = "GET", params = {}) => {
  
      try {
        return new Promise((s, v) => {
          let st = setInterval(() => {
            if (!window.androidJS) {
              return;
            }
            let result = requestSync(router, method, params);
            clearInterval(st);
            s(result);
            return;
          }, 100);
        })
      } catch (e) {
        //TODO handle the exception
      }
      return null;
    }
  };
  
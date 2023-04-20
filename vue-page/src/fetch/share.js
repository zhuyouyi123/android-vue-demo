import androidVue from "@/api/android-vue";

export default {
  get(key) {
    return new Promise((resolve, reject) => {
      let params = {
        key: key,
      }
      let res = androidVue.getRequest("share/get", params);

      if (!res) {
        resolve();
        return;
      }

      if (res.errorCode == 0) {
        if (res.data !== 'undefined') {
          return resolve(res.data);
        } else {
          return resolve();
        }
      } else {
        reject(res.errorMsg[0])
      }
    })

  },

  set(key, value) {
    let params = {
      key: key,
      value: value,
    }
    androidVue.postRequest("share/set", params);
  },

  remove(key) {
    let params = {
      key: key,
    }
    androidVue.postRequest("share/remove", params);
  },
}

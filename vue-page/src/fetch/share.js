

export default {
  get(key) {
    let value = window.android.callAndroidShare("get", key, null);
    if (value !== 'undefined') {
      return value;
    } else {
      return null;
    }
  },

  set(key, value) {
    if (value != 'undefined') {
      return window.android.callAndroidShare("set", key, value);
    } else {
      return null;
    }
  },

  remove(key) {
    return window.android.callAndroidShare("remove", key, null);
  },
}

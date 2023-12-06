package com.panvan.app.utils;

import android.app.ProgressDialog;

import com.panvan.app.Config;

public class MaskUtil {

    private static final ProgressDialog mProgressDialog;

    static {
        mProgressDialog = new ProgressDialog(Config.mainContext);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
    }

    /**
     * 遮罩层
     *
     * @param message 遮罩层的文字显示
     */
    public static void showProgressDialog(String message) {
        mProgressDialog.setMessage(message);
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public static void hide() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


}

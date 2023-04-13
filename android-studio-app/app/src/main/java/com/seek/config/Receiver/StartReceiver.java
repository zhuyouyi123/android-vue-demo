package com.seek.config.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.seek.config.AppActivity;

public class StartReceiver extends BroadcastReceiver {
    public StartReceiver(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //此处及是重启的之后，打开我们app的方法
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            intent= new Intent(context, AppActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 非常重要，如果缺少的话，程序将在启动时报错
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //自启动APP（Activity）
            context.startActivity(intent);
            //自启动服务（Service）
            //context.startService(intent);
        }
    }

}

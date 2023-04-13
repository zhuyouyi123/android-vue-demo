package com.seek.config;

import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class WelComeActivity extends AppCompatActivity {
    public static WelComeActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_wel_come);
        //利用timer让此界面延迟3秒后跳转，timer有一个线程，该线程不断执行task
        Timer timer = new Timer();
        //TimerTask实现runnable接口，TimerTask类表示在一个指定时间内执行的task
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //发送intent实现页面跳转，第一个参数为当前页面的context，第二个参数为要跳转的主页
                WelComeActivity.this.finish();//跳转后关闭当前欢迎页面
            }
        };
        timer.schedule(timerTask,2000);
    }

}

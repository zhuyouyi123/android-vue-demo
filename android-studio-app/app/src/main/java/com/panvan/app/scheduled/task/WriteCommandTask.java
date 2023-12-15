package com.panvan.app.scheduled.task;

import android.os.AsyncTask;

import com.panvan.app.utils.SdkUtil;

import java.util.List;

public class WriteCommandTask extends AsyncTask<Void, Void, Void> {

    private final List<String> instructions;

    public WriteCommandTask(List<String> instructions) {
        this.instructions = instructions;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        for (String instruction : instructions) {
            // 检查任务是否被取消
            if (isCancelled()) {
                break;
            }
            SdkUtil.writeCommand(instruction);
            try {
                Thread.sleep(30);  // 添加适当的延迟
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        cancel(true);
    }
}

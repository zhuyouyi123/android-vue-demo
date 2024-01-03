package com.panvan.app.scheduled.task;

import android.os.AsyncTask;

import com.panvan.app.utils.SdkUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WriteCommandTask extends AsyncTask<Void, Void, Void> {

    private final ConcurrentMap<String, String> instructionsMap = new ConcurrentHashMap<>();

    public WriteCommandTask(List<String> instructions) {
        for (String instruction : instructions) {
            instructionsMap.put(instruction, instruction);
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        for (Map.Entry<String, String> entry : instructionsMap.entrySet()) {
            String key = entry.getKey();
            // 检查任务是否被取消
            if (isCancelled()) {
                break;
            }
            SdkUtil.writeCommand(key);
            try {
                Thread.sleep(200);  // 添加适当的延迟
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

    public void addList(List<String> needList) {
        for (String instruction : needList) {
            instructionsMap.put(instruction, instruction);
        }
    }

    public void clear() {
        instructionsMap.clear();
    }
}

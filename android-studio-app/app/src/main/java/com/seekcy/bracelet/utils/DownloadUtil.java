package com.seekcy.bracelet.utils;

import com.seekcy.bracelet.data.entity.vo.response.RespVO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * @author zhuyouyi
 */
public class DownloadUtil {
    public static RespVO<Void> downloadFile(String requestUrl, String filePath, String fileName) {
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            // 设置读取时间 60秒超时
            connection.setReadTimeout(60_000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/octet-stream");
            connection.setRequestProperty("accept-language", "zh-CN,zh;q=0.9");
            connection.setRequestProperty("accept", "application/json, text/plain, */*");
            connection.connect();
            File picFile = new File(filePath);
            if (!picFile.exists()) {
                //有s的是创建全路径的
                boolean mkdirs = picFile.mkdirs();
            } else {
                if (null != picFile.listFiles()) {
                    for (File file : picFile.listFiles()) {
                        if (!file.isHidden()) {
                            boolean delete = picFile.delete();
                        }
                    }
                }

            }
            File file = new File(filePath + File.separator + fileName);
            if (file.exists()) {
                file.createNewFile();
            }
            //对存储目录、存储文件进行判空后就可以拿到文件流了,利用fileinputstream写入本地
            try (FileOutputStream fileOutputStream = new FileOutputStream(file);
                 InputStream inputStream = connection.getInputStream()) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                    fileOutputStream.write(buffer, 0, len);
                }
                fileOutputStream.flush();
            } catch (Exception e) {
                if (e instanceof SocketTimeoutException) {
                    return RespVO.failure("请求超时,请检查网络状态");
                }
                e.printStackTrace();
                return RespVO.failure("请求出错,请稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return RespVO.failure("请求出错,请稍后重试");
        }
        return RespVO.success();
    }


}

package com.siyanhui.mojif.bqss_demo.api;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 可以被线程池执行的网络任务，访问自己存储的URL，并从服务器读取到字符串
 * Created by lixiao on 16-9-13.
 */
public abstract class BQSSBaseRequest extends BQSSBaseNetworkTask implements Runnable {
    public BQSSBaseRequest(BQSSBaseNetworkTask task) {
        super(task);
    }

    public BQSSBaseRequest(String url, Map<String, String> params, byte[] data, BQSSNetworkManager.ResultCallback callback) {
        super(url, params, data, callback);
    }

    @Override
    public void run() {
        try {
            if (mParams != null) {
                List<StringBuilder> queryBuilders = new ArrayList<>();
                for (String key : mParams.keySet()) {
                    queryBuilders.add(new StringBuilder().append(key).append("=").append(URLEncoder.encode(mParams.get(key), "UTF-8").replace("+", "%20")));
                }
                mUrl = mUrl + "?" + TextUtils.join("&", queryBuilders);
            }
            HttpURLConnection connection = (HttpURLConnection) new URL(mUrl).openConnection();
            initConnection(connection);
            if (mData != null) {
                connection.setRequestProperty("Content-Length", String.valueOf(mData.length));
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(mData);
                outputStream.close();
            }
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder resultBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    resultBuilder.append(line);
                }
                reader.close();
                if (mCallback != null) {
                    mCallback.onSuccess(resultBuilder.toString());
                }
            } else {
                if (mCallback != null) {
                    mCallback.onFailure("Post failed with error code " + responseCode);
                }
            }
        } catch (IOException e) {
            if (mCallback != null) {
                mCallback.onFailure(e.toString());
            }
        }
    }

    protected abstract void initConnection(HttpURLConnection connection) throws IOException;
}

package com.siyanhui.mojif.bqss_demo.api;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by fantasy on 16/12/29.
 */

public class BQSSNetworkManager {
    private static ExecutorService requestExecutor = Executors.newCachedThreadPool();
    public static void get(BQSSBaseNetworkTask task) {
        requestExecutor.execute(new GetRequestBQSS(task));
    }

    public static void get(String url, Map<String, String> params, ResultCallback callback) {
        requestExecutor.execute(new GetRequestBQSS(url, params, callback));
    }

    public static void post(BQSSBaseNetworkTask task) {
        requestExecutor.execute(new PostRequestBQSS(task));
    }

    public static void post(String url, Map<String, String> params, byte[] data, ResultCallback callback) {
        requestExecutor.execute(new PostRequestBQSS(url, params, data, callback));
    }

    public interface ResultCallback {
        void onSuccess(String result);

        void onFailure(String errorInfo);
    }

    private static class PostRequestBQSS extends BQSSBaseRequest {
        public PostRequestBQSS(BQSSBaseNetworkTask task) {
            super(task);
        }

        public PostRequestBQSS(String url, Map<String, String> params, byte[] data, ResultCallback callback) {
            super(url, params, data, callback);
        }

        @Override
        protected void initConnection(HttpURLConnection connection) throws IOException {
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        }
    }

    private static class GetRequestBQSS extends BQSSBaseRequest {

        public GetRequestBQSS(BQSSBaseNetworkTask task) {
            super(task);
        }

        public GetRequestBQSS(String url, Map<String, String> params, ResultCallback callback) {
            super(url, params, null, callback);
        }

        @Override
        protected void initConnection(HttpURLConnection connection) throws IOException {
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        }
    }
}

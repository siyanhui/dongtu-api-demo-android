package com.siyanhui.mojif.bqss_demo.api;

import android.text.TextUtils;

import com.siyanhui.mojif.bqss_demo.BQSSApplication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fantasy on 16/12/29.
 */

public class BQSSBaseApi {
    public static final String API_BASE = "http://open-api.dongtu.com:8081/open-api/";

    public static void accessGetApi(String apiName, Map<String, String> params, BQSSNetworkManager.ResultCallback callback) {
        try {
            completeParams(API_BASE + apiName, params);
            BQSSNetworkManager.get(new BQSSBaseNetworkTask(API_BASE + apiName,params,null,callback));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static void accessPostApi(String apiName, Map<String, String> params, byte[] data, BQSSNetworkManager.ResultCallback callback) {
        try {
            completeParams(API_BASE + apiName, params);
            BQSSNetworkManager.get(new BQSSBaseNetworkTask(API_BASE + apiName,params,data,callback));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static void completeParams(String url, Map<String, String> params) throws NoSuchAlgorithmException {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        params.put("app_id", BQSSApplication.getAppId());
        params.remove("signature");
        List<String> sortedKeyList = new ArrayList<>();
        sortedKeyList.addAll(params.keySet());
        Collections.sort(sortedKeyList);
        List<StringBuilder> queryBuilders = new ArrayList<>();
        for (String key : sortedKeyList) {
            queryBuilders.add(new StringBuilder().append(key).append("=").append(params.get(key)));
        }
        params.put("signature", md5(url + TextUtils.join("&", queryBuilders)).toUpperCase());
    }

    private static String md5(String input) throws NoSuchAlgorithmException {
        MessageDigest digest;
        digest = MessageDigest.getInstance("MD5");
        digest.reset();
        digest.update(input.getBytes());
        byte[] a = digest.digest();
        int len = a.length;
        StringBuilder sb = new StringBuilder(len << 1);
        for (byte anA : a) {
            sb.append(Character.forDigit((anA & 0xf0) >> 4, 16));
            sb.append(Character.forDigit(anA & 0x0f, 16));
        }
        return sb.toString();
    }
}

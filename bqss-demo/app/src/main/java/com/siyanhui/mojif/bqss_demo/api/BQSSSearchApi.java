package com.siyanhui.mojif.bqss_demo.api;

import com.siyanhui.mojif.bqss_demo.model.BQSSHotTag;
import com.siyanhui.mojif.bqss_demo.model.BQSSWebSticker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fantasy on 16/12/29.
 */

public class BQSSSearchApi extends BQSSBaseApi {
    /**
     * 获取某个关键词对应的网络表情
     *
     * @param q        关键词
     * @param p        页码
     * @param size     数量
     * @param callback API请求回调
     */
    public static void getSearchStickers(String q, int p, int size, final BQSSApiCallback<BQSSWebSticker> callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("q", q);
        params.put("p", String.valueOf(p));
        params.put("size", String.valueOf(size));
        accessGetApi("/emojis/net/search/", params, new BQSSNetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String t) {
                BQSSApiResponseObject responseObject = new BQSSApiResponseObject();
                try {
                    JSONObject jsonObject = new JSONObject(t);
                    responseObject.setDatas(getStickerListFromData(jsonObject.optJSONArray("emojis")));
                    int count = jsonObject.getInt("count");
                    responseObject.setCount(count);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.onSuccess(responseObject);
            }

            @Override
            public void onFailure(String errorInfo) {
                callback.onError(errorInfo);
            }
        });
    }

    /**
     * 获取热门网络表情
     *
     * @param p        页码
     * @param size     数量
     * @param callback API请求回调
     */
    public static void getTrendingStickers(int p, int size, final BQSSApiCallback<BQSSWebSticker> callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("p", String.valueOf(p));
        params.put("size", String.valueOf(size));
        accessGetApi("/trending/", params, new BQSSNetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String t) {
                BQSSApiResponseObject responseObject = new BQSSApiResponseObject();
                try {
                    JSONObject jsonObject = new JSONObject(t);
                    responseObject.setDatas(getStickerListFromData(jsonObject.optJSONArray("emojis")));
                    int count = jsonObject.getInt("count");
                    responseObject.setCount(count);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.onSuccess(responseObject);
            }

            @Override
            public void onFailure(String errorInfo) {
                callback.onError(errorInfo);
            }
        });
    }

    /**
     * 获取热门标签
     *
     * @param callback API请求回调
     */
    public static void getHotTagStickers(final BQSSApiCallback<BQSSHotTag> callback) {
        HashMap<String, String> params = new HashMap<>();
        accessGetApi("/netword/hot/", params, new BQSSNetworkManager.ResultCallback() {
            @Override
            public void onSuccess(String t) {
                BQSSApiResponseObject responseObject = new BQSSApiResponseObject();
                try {
                    JSONObject jsonObject = new JSONObject(t);
                    responseObject.setDatas(getHotTagsFromData(jsonObject.optJSONArray("data_list")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.onSuccess(responseObject);
            }

            @Override
            public void onFailure(String errorInfo) {
                callback.onError(errorInfo);
            }
        });
    }

    private static List<BQSSHotTag> getHotTagsFromData(JSONArray jsonArray) throws JSONException {
        if (jsonArray == null) {
            return null;
        }
        List<BQSSHotTag> BQSSHotTagList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            BQSSHotTag BQSSHotTag = new BQSSHotTag(jsonArray.getJSONObject(i));
            BQSSHotTagList.add(BQSSHotTag);
        }
        return BQSSHotTagList;
    }

    private static List<BQSSWebSticker> getStickerListFromData(JSONArray jsonArray) throws JSONException {
        if (jsonArray == null) {
            return null;
        }
        List<BQSSWebSticker> BQSSWebStickerList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            BQSSWebSticker BQSSWebSticker = new BQSSWebSticker(jsonArray.getJSONObject(i));
            BQSSWebStickerList.add(BQSSWebSticker);
        }
        return BQSSWebStickerList;
    }
}

package com.siyanhui.mojif.bqss_demo.api;

import java.util.Map;

/**
 * Created by fantasy on 16/12/29.
 */

public class BQSSBaseNetworkTask {
    protected String mUrl;
    protected Map<String, String> mParams;
    protected byte[] mData;
    protected BQSSNetworkManager.ResultCallback mCallback;

    public BQSSBaseNetworkTask(BQSSBaseNetworkTask another) {
        mUrl = another.mUrl;
        mParams = another.mParams;
        mData = another.mData;
        mCallback = another.mCallback;
    }

    public BQSSBaseNetworkTask(String url, Map<String, String> params, byte[] data, BQSSNetworkManager.ResultCallback callback) {
        mUrl = url;
        mParams = params;
        mCallback = callback;
        mData = data;
    }

}

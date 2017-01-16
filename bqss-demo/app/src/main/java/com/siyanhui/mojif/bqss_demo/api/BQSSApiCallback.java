package com.siyanhui.mojif.bqss_demo.api;


/**
 * Created by fantasy on 16/10/19.
 */
public interface BQSSApiCallback<T> {
    void onSuccess(BQSSApiResponseObject<T> result);

    void onError(String errorInfo);
}

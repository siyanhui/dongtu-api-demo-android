package com.siyanhui.mojif.bqss_demo;

import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by fantasy on 16/12/30.
 */

public class BQSSApplication extends Application {
    private static String appId;
    private static BQSSApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        try {
            Bundle bundle = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData;
            appId = bundle.getString("bqmm_app_id");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Fresco.initialize(this);
    }

    public static String getAppId() {
        return appId;
    }

    public static BQSSApplication getApplication() {
        return mInstance;
    }
}

package com.siyanhui.mojif.bqss_demo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import com.siyanhui.mojif.bqss_demo.BQSSApplication;
import com.siyanhui.mojif.bqss_demo.BQSSConstants;

import java.util.Arrays;
import java.util.List;

/**
 * Created by fantasy on 17/1/6.
 * 存储全屏搜素关键词历史记录的工具
 */

public class BQSSPreferenceHelper {


    public static void clearSearchHistory() {
        SharedPreferences preference = BQSSApplication.getApplication().getSharedPreferences(BQSSConstants.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        preference.edit().clear().commit();
    }

    public static void addSearchKeyword(String keyword) {
        writeKeyword(keyword);
    }

    public static List<String> getSearchHistory() {
        if (getStringArray() == null)
            return null;
        String[] strings = getStringArray();
        return Arrays.asList(strings);
    }

    private static String[] getStringArray() {
        String regularEx = "/";
        String[] strs;
        String[] resultArray = new String[10];
        SharedPreferences sp = BQSSApplication.getApplication().getSharedPreferences(BQSSConstants.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        String values;
        int k = 0;
        values = sp.getString(BQSSConstants.KEY, "");
        if (!values.equals("")) {
            strs = values.split(regularEx);
            for (int i = 0; i < 10 && i < strs.length; i++) {
                byte[] bytes = Base64.decode(strs[i], Base64.URL_SAFE);
                resultArray[k++] = new String(bytes);
            }
        } else {
            return null;
        }
        return resultArray;
    }

    private static void writeKeyword(String value) {
        String valueSafe = Base64.encodeToString(value.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
        String regularEx = "/";
        SharedPreferences sp = BQSSApplication.getApplication().getSharedPreferences(BQSSConstants.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        String str = sp.getString(BQSSConstants.KEY, "");
        boolean needAdd;
        int k = 0;
        StringBuilder stringBuilder = new StringBuilder();
        String[] strs = str.split(regularEx);
        if (strs != null) {
            for (int i = 0; i < 10 && i < strs.length; i++) {
                stringBuilder.append(strs[i] + regularEx);
                if (strs[i].equals(valueSafe)) {
                    k++;
                }
            }
        }
        if (k > 0) {
            needAdd = false;
        } else {
            needAdd = true;
        }
        if (!TextUtils.isEmpty(valueSafe) && needAdd) {
            stringBuilder.insert(0, valueSafe + regularEx);
            SharedPreferences.Editor et = sp.edit();
            et.putString(BQSSConstants.KEY, stringBuilder.toString());
            et.commit();
        }
    }
}

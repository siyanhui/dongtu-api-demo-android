package com.siyanhui.mojif.bqss_demo.model;

import org.json.JSONObject;

/**
 * Created by fantasy on 17/1/5.
 * 全屏搜索首页热门标签
 */

public class BQSSHotTag {
    public BQSSHotTag(JSONObject jsonObject) {
        this.setText(jsonObject.optString("text"));
        this.setCover(jsonObject.optString("cover"));

    }

    private String text;
    private String cover;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}

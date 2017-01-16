package com.siyanhui.mojif.bqss_demo.model;

import org.json.JSONObject;

/**
 * Created by fantasy on 16/12/29.
 * 网络表情
 */
public class BQSSWebSticker {
    public BQSSWebSticker() {

    }

    public BQSSWebSticker(JSONObject jsonObject) {
        this.setText(jsonObject.optString("text"));
        this.setThumb(jsonObject.optString("thumb"));
        this.setMain(jsonObject.optString("main"));
        this.setWidth(jsonObject.optInt("width"));
        this.setHeight(jsonObject.optInt("height"));
        this.setIs_animated(jsonObject.optInt("is_animated"));
    }

    private String text; //表情含义词
    private String thumb; //缩略图，默认为主图尺寸的80%
    private String main; //表情主图，可能是gif, png或jpg格式
    private int width; //返回图片宽度，单位px
    private int height; //返回图片高度，单位px
    private int is_animated; //是否动态（gif）表情，1为动态表情，0为静态表情

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getIs_animated() {
        return is_animated;
    }

    public void setIs_animated(int is_animated) {
        this.is_animated = is_animated;
    }
}

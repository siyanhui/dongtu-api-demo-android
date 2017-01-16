package com.siyanhui.mojif.bqss_demo.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.siyanhui.mojif.bqss_demo.R;

/**
 * Created by fantasy on 16/12/29.
 * 导航页面
 */

public class BQSSMainActivity extends Activity implements View.OnClickListener {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        RelativeLayout mQuaterBtn = (RelativeLayout) findViewById(R.id.quater_search_btn);
        RelativeLayout mHalfBtn = (RelativeLayout) findViewById(R.id.half_search_btn);
        RelativeLayout mFullBtn = (RelativeLayout) findViewById(R.id.full_search_btn);
        mQuaterBtn.setOnClickListener(this);
        mHalfBtn.setOnClickListener(this);
        mFullBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.quater_search_btn://进入联想搜索页面
                Intent goQuaterIntent = new Intent(mContext, BQSSQuaterSearchActivity.class);
                startActivity(goQuaterIntent);
                break;
            case R.id.half_search_btn://进入键盘搜索页面
                Intent goHalfIntent = new Intent(mContext, BQSSHalfSearchActivity.class);
                startActivity(goHalfIntent);
                break;
            case R.id.full_search_btn://进入全屏搜索页面
                Intent goFullIntent = new Intent(mContext, BQSSFullSearchActivity.class);
                startActivity(goFullIntent);
                break;
        }
    }
}

package com.siyanhui.mojif.bqss_demo.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.siyanhui.mojif.bqss_demo.R;

/**
 * Created by fantasy on 17/1/9.
 * 自定义上拉加载更多
 */

public class BQSSFooterView extends LinearLayout {
    private Context mContext;

    public static final int HIDE = 0;
    public static final int MORE = 1;
    public static final int LOADING = 2;
    public static final int BADNETWORK = 3;
    public static final int LOADALL = 4;

    private ProgressBar progressBar;
    private TextView textView;
    private Button btn;

    private int curStatus;

    private OnClickListener ml;

    public BQSSFooterView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public BQSSFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.view_footer_item,
                this, true);
        progressBar = (ProgressBar) findViewById(R.id.footer_loading);
        textView = (TextView) findViewById(R.id.footview_text);
        btn = (Button) findViewById(R.id.footview_button);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ml != null) {
                    ml.onClick(v);
                }

            }
        });

        setStatus(MORE);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        ml = l;
        super.setOnClickListener(ml);
    }

    public void setStatus(int status) {
        curStatus = status;
        switch (status) {
            case HIDE:
                setVisibility(View.GONE);
                break;
            case MORE:
                progressBar.setVisibility(View.GONE);
                btn.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                textView.setText("上拉加载更多");
                this.setVisibility(View.VISIBLE);
                break;
            case LOADING:
                progressBar.setVisibility(View.VISIBLE);
                btn.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                textView.setText("正在加载更多");
                this.setVisibility(View.VISIBLE);
                break;
            case BADNETWORK:
                progressBar.setVisibility(View.GONE);
                btn.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                textView.setText("网络连接有问题");
                this.setVisibility(View.VISIBLE);
                break;
            case LOADALL:
                progressBar.setVisibility(View.GONE);
                btn.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                textView.setText("------ 已经到底了 -------");
                this.setVisibility(View.GONE);
                break;

        }
    }

    public int getStatus() {
        return curStatus;
    }

}

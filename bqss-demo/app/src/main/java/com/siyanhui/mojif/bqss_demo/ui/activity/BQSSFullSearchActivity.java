package com.siyanhui.mojif.bqss_demo.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.siyanhui.mojif.bqss_demo.BQSSBase64Img;
import com.siyanhui.mojif.bqss_demo.BQSSConstants;
import com.siyanhui.mojif.bqss_demo.R;
import com.siyanhui.mojif.bqss_demo.ui.widget.BQSSMessageAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fantasy on 16/12/30.
 * 全屏搜索页面
 */

public class BQSSFullSearchActivity extends Activity {
    public static final int REQUESTCODE = 1000;
    private static final int REFRESH = 3;
    private TextView mTitleText;
    private LinearLayout mBackBtn;
    private ImageView mGoFullSearchBtn;
    private BQSSMessageAdapter mBQSSMessageAdapter;
    private ListView mListView;
    private List<String> mBQSSMessages;
    private MyHandler mHandler;

    private EditText mEditText;
    private Button mTextSendBtn;
    private LinearLayout mOtherBtnsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_search);
        bindView();
        mTitleText.setText("全屏搜索");
        mHandler = new MyHandler(this);
        mBQSSMessages = new ArrayList<>();
        mBQSSMessageAdapter = new BQSSMessageAdapter(mBQSSMessages);
        mListView.setAdapter(mBQSSMessageAdapter);
        setClickListeners();
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftInput(mListView);
                return false;
            }
        });
    }

    private void bindView() {
        mTitleText = (TextView) findViewById(R.id.title_text);
        mBackBtn = (LinearLayout) findViewById(R.id.back_btn);
        mGoFullSearchBtn = (ImageView) findViewById(R.id.go_bqss_btn);
        mListView = (ListView) findViewById(R.id.listview);
        mEditText = (EditText) findViewById(R.id.edit_text);
        mTextSendBtn = (Button) findViewById(R.id.text_send_btn);
        mOtherBtnsLayout = (LinearLayout) findViewById(R.id.other_btns_layout);
    }

    private void setClickListeners() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mGoFullSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BQSSFullSearchActivity.this, BQSSFullSearchHomePage.class);
                startActivityForResult(intent, REQUESTCODE);
            }
        });
        mTextSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = BQSSConstants.BQSS_TEXT_TAG + mEditText.getText().toString();
                if (TextUtils.isEmpty(mEditText.getText().toString().trim())) {
                    Toast.makeText(BQSSFullSearchActivity.this, "不能发送空消息", Toast.LENGTH_SHORT).show();
                } else {
                    mBQSSMessages.add(text);
                    mBQSSMessageAdapter.refresh(mBQSSMessages);
                    mEditText.setText("");
                }
            }
        });

        mOtherBtnsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BQSSFullSearchActivity.this, "示例按钮：无功能", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE && resultCode == BQSSFullSearchHomePage.RESULTCODE) {
            final String imgUrl = data.getStringExtra("stickerData");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String str = BQSSBase64Img.getImageStrFromUrl(imgUrl);
                    Message message = Message.obtain();
                    message.obj = str;
                    message.arg1 = REFRESH;
                    mHandler.sendMessage(message);
                }
            }).start();
        }
    }

    private static class MyHandler extends Handler {
        WeakReference<BQSSFullSearchActivity> weakReference;

        MyHandler(BQSSFullSearchActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BQSSFullSearchActivity activity = weakReference.get();
            if (activity != null) {
                switch (msg.arg1) {
                    case REFRESH:
                        activity.mBQSSMessages.add((String) msg.obj);
                        activity.mBQSSMessageAdapter.refresh(activity.mBQSSMessages);
                        break;
                }
            }
        }
    }

    private void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

package com.siyanhui.mojif.bqss_demo.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.siyanhui.mojif.bqss_demo.BQSSBase64Img;
import com.siyanhui.mojif.bqss_demo.BQSSConstants;
import com.siyanhui.mojif.bqss_demo.R;
import com.siyanhui.mojif.bqss_demo.api.BQSSApiCallback;
import com.siyanhui.mojif.bqss_demo.api.BQSSApiResponseObject;
import com.siyanhui.mojif.bqss_demo.api.BQSSSearchApi;
import com.siyanhui.mojif.bqss_demo.model.BQSSWebSticker;
import com.siyanhui.mojif.bqss_demo.ui.widget.BQSSMessageAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fantasy on 16/12/29.
 * 联想搜索页面
 */

public class BQSSQuaterSearchActivity extends Activity {
    private static final int SUCCESS = 1;
    private static final int ERROR = 2;
    private static final int REFRESH = 3;
    private static final int SEARCH = 4;

    private MyHandler mHandler;

    private LinearLayout mStickersContainer;
    private EditText mBqssEditView;
    private BqssHorizontalScrollView mBqssHScrollview;
    private View mTopLine;
    private ImageView mBqssBtn;
    private EditText mEditText;
    private LinearLayout mETLayout;
    private Button mTextSendBtn;
    private LinearLayout mBqssSearchLayout;
    private TextView mCancelBtn;
    private LinearLayout mBackBtn;
    private TextView mTitleText;
    private LinearLayout mOtherBtnsLayout;

    private boolean isHiddenBqss = false;

    private BQSSMessageAdapter mBQSSMessageAdapter;
    private ListView mListView;
    private List<String> mBQSSMessages;

    private int mSessionId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quater_search);
        bindView();
        setClickListeners();
        mTitleText.setText("联想搜索");
        mETLayout.setVisibility(View.GONE);
        setEditTextLeftDrawable();
        mBqssBtn.setImageResource(R.mipmap.icon);

        mHandler = new MyHandler(this);
        mBqssHScrollview.weakReference = new WeakReference<>(this);
        mBqssHScrollview.keyword = BQSSConstants.TRENDING_STICKER_TAG;
        mBqssHScrollview.currentPage = 1;
        mBqssHScrollview.getTrendingStickers();
        mBQSSMessages = new ArrayList<>();
        mBQSSMessageAdapter = new BQSSMessageAdapter(mBQSSMessages);
        mListView.setAdapter(mBQSSMessageAdapter);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftInput(mListView);
                mBqssHScrollview.setVisibility(View.GONE);
                mTopLine.setVisibility(View.GONE);
                return false;
            }
        });
        mBqssEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString().trim();
                Message message = Message.obtain();
                message.arg1 = SEARCH;
                message.arg2 = ++mSessionId;
                message.obj = content;
                mHandler.sendMessageDelayed(message, 1000);
            }
        });
    }

    private void setEditTextLeftDrawable() {
        float density = getResources().getDisplayMetrics().density;
        int defSize = (int) (18 * density + 0.5f);
        Drawable drawable = getResources().getDrawable(R.mipmap.search_icon);
        drawable.setBounds(0, 0, defSize, defSize);
        mBqssEditView.setCompoundDrawables(drawable, null, null, null);
    }

    private void bindView() {
        mTitleText = (TextView) findViewById(R.id.title_text);
        mBqssEditView = (EditText) findViewById(R.id.bqss_editview);
        mCancelBtn = (TextView) findViewById(R.id.cancel_btn);
        mBqssHScrollview = (BqssHorizontalScrollView) findViewById(R.id.bqss_hscrollview);
        mETLayout = (LinearLayout) findViewById(R.id.edittext_layout);
        mEditText = (EditText) findViewById(R.id.edit_text);
        mTextSendBtn = (Button) findViewById(R.id.text_send_btn);
        mBqssSearchLayout = (LinearLayout) findViewById(R.id.bqss_search_layout);
        mTopLine = findViewById(R.id.top_line);
        mListView = (ListView) findViewById(R.id.listview);
        mBackBtn = (LinearLayout) findViewById(R.id.back_btn);
        mStickersContainer = (LinearLayout) findViewById(R.id.stickers_container);
        mBqssBtn = (ImageView) findViewById(R.id.go_bqss_btn);
        mOtherBtnsLayout = (LinearLayout) findViewById(R.id.other_btns_layout);
    }

    private void setClickListeners() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHiddenBqss = true;
                mBqssSearchLayout.setVisibility(View.GONE);
                mBqssBtn.setImageResource(R.mipmap.icon_gray);
                showEdittext();
            }
        });
        mBqssBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHiddenBqss) {//打开表情搜搜
                    mETLayout.setVisibility(View.GONE);
                    mBqssSearchLayout.setVisibility(View.VISIBLE);
                    isHiddenBqss = false;
                    mBqssEditView.setFocusable(true);
                    mBqssEditView.setFocusableInTouchMode(true);
                    mBqssEditView.requestFocus();
                    mBqssBtn.setImageResource(R.mipmap.icon);
                    showSoftInput(mBqssEditView);
                } else {//关闭表情搜搜
                    isHiddenBqss = true;
                    mBqssSearchLayout.setVisibility(View.GONE);
                    mBqssBtn.setImageResource(R.mipmap.icon_gray);
                    showEdittext();
                }
            }
        });
        mTextSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = BQSSConstants.BQSS_TEXT_TAG + mEditText.getText().toString();
                if (TextUtils.isEmpty(mEditText.getText().toString().trim())) {
                    Toast.makeText(BQSSQuaterSearchActivity.this, "不能发送空消息", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(BQSSQuaterSearchActivity.this, "示例按钮：无功能", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEdittext() {
        mETLayout.setVisibility(View.VISIBLE);
        mEditText.setFocusable(true);
        mEditText.setFocusableInTouchMode(true);
        mEditText.requestFocus();
        showSoftInput(mEditText);
    }

    private static class BqssHorizontalScrollView extends HorizontalScrollView {
        private WeakReference<BQSSQuaterSearchActivity> weakReference;
        private String keyword = "";
        private int currentPage = 1;
        private boolean needLoadMore = false;

        public BqssHorizontalScrollView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public BqssHorizontalScrollView(Context context) {
            super(context);
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {
            super.onScrollChanged(l, t, oldl, oldt);
            int maxX = getChildAt(0).getMeasuredWidth() - getMeasuredWidth();
            if (maxX == getScrollX()) {
                loadMore();
            }
        }

        public void loadMore() {
            if (needLoadMore) {
                needLoadMore = false;
                if (keyword.equals(BQSSConstants.TRENDING_STICKER_TAG)) {
                    getTrendingStickers();
                } else {
                    getStickers();
                }
            }
        }

        private void getStickers() {
            BQSSSearchApi.getSearchStickers(keyword, currentPage, BQSSConstants.LOADSIZE, new BQSSApiCallback<BQSSWebSticker>() {
                @Override
                public void onSuccess(BQSSApiResponseObject<BQSSWebSticker> result) {
                    Message message = Message.obtain();
                    message.arg1 = SUCCESS;
                    message.arg2 = currentPage;
                    message.obj = result.getDatas();
                    if (weakReference.get() != null)
                        weakReference.get().mHandler.sendMessage(message);
                    if (result.getDatas().size() == BQSSConstants.LOADSIZE && currentPage < 5) {
                        needLoadMore = true;
                        currentPage++;
                    } else {
                        needLoadMore = false;
                    }
                }

                @Override
                public void onError(String errorInfo) {
                    Message message = Message.obtain();
                    message.arg1 = ERROR;
                    message.obj = errorInfo;
                    if (weakReference.get() != null)
                        weakReference.get().mHandler.sendMessage(message);
                }
            });

        }

        private void getTrendingStickers() {
            BQSSSearchApi.getTrendingStickers(currentPage, BQSSConstants.LOADSIZE, new BQSSApiCallback<BQSSWebSticker>() {
                @Override
                public void onSuccess(BQSSApiResponseObject<BQSSWebSticker> result) {
                    Message message = Message.obtain();
                    message.arg1 = SUCCESS;
                    message.arg2 = currentPage;
                    message.obj = result.getDatas();
                    if (weakReference.get() != null)
                        weakReference.get().mHandler.sendMessage(message);
                    if (result.getDatas().size() == BQSSConstants.LOADSIZE) {
                        needLoadMore = true;
                        currentPage++;
                    } else {
                        needLoadMore = false;
                    }
                }

                @Override
                public void onError(String errorInfo) {
                    Message message = Message.obtain();
                    message.arg1 = ERROR;
                    message.obj = errorInfo;
                    if (weakReference.get() != null)
                        weakReference.get().mHandler.sendMessage(message);
                }
            });
        }
    }

    private static class MyHandler extends Handler {
        WeakReference<BQSSQuaterSearchActivity> weakReference;

        MyHandler(BQSSQuaterSearchActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final BQSSQuaterSearchActivity activity = weakReference.get();
            if (activity != null) {
                switch (msg.arg1) {
                    case SUCCESS:
                        List<BQSSWebSticker> BQSSWebStickerList = (List<BQSSWebSticker>) msg.obj;
                        if (BQSSWebStickerList == null || BQSSWebStickerList.size() == 0) {
                            if (msg.arg2 == 1)
                                Toast.makeText(activity, "无搜索结果", Toast.LENGTH_SHORT).show();
                        } else {
                            if (msg.arg2 == 1) {
                                activity.mStickersContainer.removeAllViews();
                                activity.mBqssHScrollview.scrollTo(0, 0);
                            }
                            for (int i = 0; i < BQSSWebStickerList.size(); i++) {
                                BQSSWebSticker bqssWebSticker = BQSSWebStickerList.get(i);
                                final String mainImg = bqssWebSticker.getMain();
                                int imgWidth = bqssWebSticker.getWidth();
                                int imgHeight = bqssWebSticker.getHeight();
                                int outHeight = activity.mStickersContainer.getHeight();
                                int needWidth = imgWidth * outHeight / imgHeight;
                                Uri uri = Uri.parse(mainImg);
                                SimpleDraweeView simpleDraweeView = new SimpleDraweeView(activity);
                                DraweeController draweeController = Fresco.newDraweeControllerBuilder().setUri(uri).setAutoPlayAnimations(true).build();
                                GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(activity.getResources());
                                GenericDraweeHierarchy hierarchy = builder.setPlaceholderImage(R.raw.bqss_sticker_loading).setFailureImage(R.mipmap.icon_loading_failed).setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER).build();
                                simpleDraweeView.setHierarchy(hierarchy);
                                simpleDraweeView.setController(draweeController);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(needWidth, ViewGroup.LayoutParams.MATCH_PARENT);
                                layoutParams.setMargins(2, 2, 2, 2);
                                activity.mBqssHScrollview.setVisibility(View.VISIBLE);
                                activity.mTopLine.setVisibility(View.VISIBLE);
                                activity.mStickersContainer.addView(simpleDraweeView, layoutParams);
                                simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                String str = BQSSBase64Img.getImageStrFromUrl(mainImg);
                                                Message message = Message.obtain();
                                                message.obj = str;
                                                message.arg1 = REFRESH;
                                                activity.mHandler.sendMessage(message);
                                            }
                                        }).start();
                                        activity.mBqssHScrollview.setVisibility(View.GONE);
                                        activity.mTopLine.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }
                        break;
                    case ERROR:
                        Toast.makeText(activity, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    case REFRESH:
                        activity.mBQSSMessages.add((String) msg.obj);
                        activity.mBQSSMessageAdapter.refresh(activity.mBQSSMessages);
                        break;
                    case SEARCH:
                        activity.mBqssHScrollview.keyword = (String) msg.obj;
                        activity.mBqssHScrollview.currentPage = 1;
                        if (activity.mSessionId == msg.arg2 && !TextUtils.isEmpty(activity.mBqssHScrollview.keyword)) {
                            activity.mBqssHScrollview.getStickers();
                        }
                        break;
                }
            }
        }

    }

    private void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showSoftInput(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

}

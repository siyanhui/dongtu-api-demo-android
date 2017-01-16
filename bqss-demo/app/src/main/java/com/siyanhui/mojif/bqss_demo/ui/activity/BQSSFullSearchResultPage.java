package com.siyanhui.mojif.bqss_demo.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.siyanhui.mojif.bqss_demo.BQSSConstants;
import com.siyanhui.mojif.bqss_demo.R;
import com.siyanhui.mojif.bqss_demo.api.BQSSApiCallback;
import com.siyanhui.mojif.bqss_demo.api.BQSSApiResponseObject;
import com.siyanhui.mojif.bqss_demo.api.BQSSSearchApi;
import com.siyanhui.mojif.bqss_demo.model.BQSSWebSticker;
import com.siyanhui.mojif.bqss_demo.ui.widget.BQSSEditText;
import com.siyanhui.mojif.bqss_demo.ui.widget.BQSSFooterView;
import com.siyanhui.mojif.bqss_demo.ui.widget.BQSSWordWrapView;
import com.siyanhui.mojif.bqss_demo.utils.BQSSPreferenceHelper;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

/**
 * Created by fantasy on 17/1/5.
 * 全屏搜索结果页
 */

public class BQSSFullSearchResultPage extends Activity {
    public static int RESULTCODE = 1003;
    private static final int SUCCESS = 1;
    private static final int ERROR = 2;
    private static final int PREVIEWIMG = 3;

    private GridAdapter mAdapter;
    private BQSSEditText mBqssEditView;
    private GridView mGridView;
    private RelativeLayout mPreviewImageLayout;
    private SimpleDraweeView mSimpleDraweeView;
    private RelativeLayout mSendBtn;
    private RelativeLayout mCancelBtn;
    private BQSSWordWrapView mBQSSWordWrapView;
    private LinearLayout mSearchHistoryView;
    private TextView mClearSearchHistoryBtn;
    private TextView mTitleText;
    private TextView mBackText;
    private LinearLayout mBackBtn;

    private String mSendImageUrl;
    private String mKeyword;
    private int mCurrentPage = 1;
    private boolean mNeedLoadMore;
    private MyHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fs_result_page);
        bindView();
        mTitleText.setText("全屏搜索");
        mBackText.setText("返回");
        mHandler = new MyHandler(this);
        Intent intent = getIntent();
        mKeyword = intent.getStringExtra("keyword");
        getStickers(mCurrentPage);
        mBqssEditView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showSearchHistory();
                mSearchHistoryView.setVisibility(View.VISIBLE);
                return false;
            }
        });

        setOnClickListeners();
        setEditTextLeftDrawable();
        setGridViewConfigure();
    }

    private void bindView() {
        mTitleText = (TextView) findViewById(R.id.title_text);
        mBackText = (TextView) findViewById(R.id.back_text);
        mBackBtn = (LinearLayout) findViewById(R.id.back_btn);
        mPreviewImageLayout = (RelativeLayout) findViewById(R.id.preview_bg);
        mSimpleDraweeView = (SimpleDraweeView) findViewById(R.id.preview_img);
        mBqssEditView = (BQSSEditText) findViewById(R.id.bqss_editview);
        mSendBtn = (RelativeLayout) findViewById(R.id.send_btn);
        mCancelBtn = (RelativeLayout) findViewById(R.id.cancel_btn);
        mSearchHistoryView = (LinearLayout) findViewById(R.id.search_history_view);
        mBQSSWordWrapView = (BQSSWordWrapView) findViewById(R.id.word_wrap_view);
        mClearSearchHistoryBtn = (TextView) findViewById(R.id.clear_history_btn);
    }

    private void setOnClickListeners() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.putExtra("stickerData", mSendImageUrl);
                setResult(RESULTCODE, sendIntent);
                finish();
            }
        });
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPreviewImageLayout.setVisibility(View.GONE);
            }
        });
        mClearSearchHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BQSSPreferenceHelper.clearSearchHistory();
                mBQSSWordWrapView.removeAllViews();
            }
        });
    }

    private void setGridViewConfigure() {
        mGridView = (GridView) findViewById(R.id.result_gridview);
        mGridView.setNumColumns(3);
        mGridView.setPadding(10, 10, 10, 10);
        mGridView.setHorizontalSpacing(10);
        mGridView.setVerticalSpacing(10);
        mAdapter = new GridAdapter();
        mGridView.setAdapter(mAdapter);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() >= view.getCount() - 1) {
                        if (mNeedLoadMore) {
                            mNeedLoadMore = false;
                            if (mAdapter != null) {
                                mAdapter.setFooterViewStatus(BQSSFooterView.LOADING);
                            }
                            getStickers(mCurrentPage);
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

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

    private void showSearchHistory() {
        mBQSSWordWrapView.removeAllViews();
        if (BQSSPreferenceHelper.getSearchHistory() != null) {
            List<String> keywords = BQSSPreferenceHelper.getSearchHistory();
            Iterator<String> iterator = keywords.iterator();
            while (iterator.hasNext()) {
                final String keyword = iterator.next();
                if (!TextUtils.isEmpty(keyword)) {
                    TextView textview = new TextView(this);
                    textview.setText(keyword);
                    textview.setTextColor(getResources().getColor(R.color.gray_97));
                    textview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCurrentPage = 1;
                            mKeyword = keyword;
                            mSearchHistoryView.setVisibility(View.GONE);
                            getStickers(mCurrentPage);
                        }
                    });
                    mBQSSWordWrapView.addView(textview);
                }
            }
        }
    }

    private void getStickers(int page) {
        BQSSSearchApi.getSearchStickers(mKeyword, page, BQSSConstants.FULLSEARCH_LOADSIZE, new BQSSApiCallback<BQSSWebSticker>() {
            @Override
            public void onSuccess(BQSSApiResponseObject<BQSSWebSticker> result) {
                Message message = Message.obtain();
                message.arg1 = SUCCESS;
                message.arg2 = mCurrentPage;
                message.obj = result.getDatas();
                if (result.getDatas().size() == BQSSConstants.FULLSEARCH_LOADSIZE && mCurrentPage < 5) {
                    ++mCurrentPage;
                    mNeedLoadMore = true;
                } else {
                    mNeedLoadMore = false;
                }
                mHandler.sendMessage(message);
            }

            @Override
            public void onError(String errorInfo) {
                Message message = Message.obtain();
                message.arg1 = ERROR;
                message.obj = errorInfo;
                mHandler.sendMessage(message);
            }
        });
    }

    private class GridAdapter extends BaseAdapter {
        List<BQSSWebSticker> content;
        private BQSSFooterView footerView;
        private boolean footerViewEnable = false;

        @Override
        public int getCount() {
            if (content != null)
                return content.size();
            return 0;
        }

        public boolean isFooterViewEnable() {
            return footerViewEnable;
        }


        public void setFootreViewEnable(boolean enable) {
            footerViewEnable = enable;
        }

        private int getDisplayWidth(Activity activity) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            int width = display.getWidth();
            return width;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (content == null) return null;
            if (footerViewEnable && position == content.size() - 1 && mNeedLoadMore) {
                if (footerView == null) {
                    footerView = new BQSSFooterView(parent.getContext());

                    GridView.LayoutParams pl = new GridView.LayoutParams(
                            getDisplayWidth((Activity) parent.getContext()),
                            GridView.LayoutParams.WRAP_CONTENT);
                    footerView.setLayoutParams(pl);
                }
                setFooterViewStatus(BQSSFooterView.MORE);
                return footerView;
            }
            SimpleDraweeView simpleDraweeView;
            if (convertView == null || (convertView == footerView)) {
                simpleDraweeView = new SimpleDraweeView(parent.getContext());
                int width = parent.getWidth() / 3;
                simpleDraweeView.setLayoutParams(new GridView.LayoutParams(width - 5, width - 5));
            } else {
                simpleDraweeView = (SimpleDraweeView) convertView;
            }

            if (content.get(position) != null) {
                final String mainImg = content.get(position).getMain();
                Uri uri = Uri.parse(mainImg);

                DraweeController draweeController = Fresco.newDraweeControllerBuilder().setUri(uri).setAutoPlayAnimations(true).build();
                GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(parent.getResources());
                GenericDraweeHierarchy hierarchy = builder.setPlaceholderImage(R.raw.bqss_sticker_loading).setFailureImage(R.mipmap.icon_loading_failed).setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER).build();
                simpleDraweeView.setHierarchy(hierarchy);
                simpleDraweeView.setController(draweeController);
                simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Message message = Message.obtain();
                        message.obj = mainImg;
                        message.arg1 = PREVIEWIMG;
                        mHandler.sendMessage(message);
                    }
                });
            }
            return simpleDraweeView;
        }

        public void setContent(List<BQSSWebSticker> content) {
            this.content = content;
            notifyDataSetChanged();
        }


        public void setFooterViewStatus(int status) {
            if (footerView != null) {
                footerView.setStatus(status);
            }
        }
    }

    private static class MyHandler extends Handler {
        WeakReference<BQSSFullSearchResultPage> weakReference;

        MyHandler(BQSSFullSearchResultPage activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BQSSFullSearchResultPage activity = weakReference.get();
            if (activity != null) {
                switch (msg.arg1) {
                    case SUCCESS:
                        List<BQSSWebSticker> BQSSWebStickerList = (List<BQSSWebSticker>) msg.obj;
                        if (BQSSWebStickerList == null || BQSSWebStickerList.size() == 0) {
                            Toast.makeText(activity, "无搜索结果", Toast.LENGTH_SHORT).show();
                        } else {
                            activity.mGridView.setVisibility(View.VISIBLE);

                            if (msg.arg2 == 1) {
                                activity.mAdapter.content = BQSSWebStickerList;
                            } else {
                                if (activity.mAdapter.content == null)
                                    activity.mAdapter.content = BQSSWebStickerList;
                                else
                                    // 在添加数据之前删除最后的伪造item
                                    if (activity.mAdapter.isFooterViewEnable()) {
                                        activity.mAdapter.content.remove(activity.mAdapter.content.get(activity.mAdapter.content.size() - 1));
                                    }
                                activity.mAdapter.content.addAll(BQSSWebStickerList);
                            }
                            if (activity.mNeedLoadMore) {
                                activity.mAdapter.content.add(null);
                                activity.mAdapter.setFootreViewEnable(true);
                            }
                            activity.mAdapter.notifyDataSetChanged();
                            if (msg.arg2 == 1) {
                                activity.mGridView.smoothScrollToPosition(0);
                            }
                        }
                        break;
                    case ERROR:
                        Toast.makeText(activity, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    case PREVIEWIMG:
                        activity.mPreviewImageLayout.setVisibility(View.VISIBLE);
                        activity.mSendImageUrl = (String) msg.obj;
                        Uri uri = Uri.parse(activity.mSendImageUrl);
                        DraweeController draweeController = Fresco.newDraweeControllerBuilder().setUri(uri).setAutoPlayAnimations(true).build();
                        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(activity.getResources());
                        GenericDraweeHierarchy hierarchy = builder.setPlaceholderImage(R.raw.bqss_sticker_loading).setFailureImage(R.mipmap.icon_loading_failed).setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER).build();
                        activity.mSimpleDraweeView.setHierarchy(hierarchy);
                        activity.mSimpleDraweeView.setController(draweeController);
                        break;
                }
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            mSearchHistoryView.setVisibility(View.GONE);
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(BQSSFullSearchResultPage.this.getCurrentFocus().getWindowToken(), 0);
            }
            mKeyword = mBqssEditView.getText().toString();
            if (!TextUtils.isEmpty(mKeyword)) {
                BQSSPreferenceHelper.addSearchKeyword(mKeyword);
                mCurrentPage = 1;
                getStickers(mCurrentPage);
            } else {
                Toast.makeText(BQSSFullSearchResultPage.this, "请输入关键词", Toast.LENGTH_SHORT).show();
            }

            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}

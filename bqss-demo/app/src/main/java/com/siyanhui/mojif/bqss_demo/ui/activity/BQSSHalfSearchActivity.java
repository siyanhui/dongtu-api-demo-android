package com.siyanhui.mojif.bqss_demo.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
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

import static com.siyanhui.mojif.bqss_demo.BQSSConstants.TRENDING_STICKER_TAG;

/**
 * Created by fantasy on 16/12/30.
 * 键盘搜索页面
 */

public class BQSSHalfSearchActivity extends Activity {
    private static final int SUCCESS = 1;
    private static final int ERROR = 2;
    private static final int REFRESH = 3;

    private GridView mGridView;
    private EditText mBqssEditView;
    private ListView mListView;
    private View mTopLine;
    private ImageView mBqssBtn;
    private EditText mEditText;
    private LinearLayout mETLayout;
    private Button mTextSendBtn;
    private LinearLayout mBqssSearchLayout;
    private TextView mTitleText;
    private LinearLayout mBackBtn;
    private TextView mCancelBtn;
    private LinearLayout mOtherBtnsLayout;

    private GridAdapter mAdapter;
    private MyHandler mHandler;
    private Context mContext;
    private String mKeyword;
    private int mCurrentPage = 1;
    private boolean mNeedLoadMore;
    private boolean mIsHiddenBqss = false;
    private List<String> mBQSSMessages;
    private BQSSMessageAdapter mBQSSMessageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_half_search);
        mContext = this;
        bindView();
        setClickListeners();
        setEditTextLeftDrawable();
        mTitleText.setText("键盘搜索");
        mKeyword = TRENDING_STICKER_TAG;
        mHandler = new MyHandler(this);
        getTrendingStickers(1);
        setGridViewConfigure();

        mBqssEditView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGridView.setVisibility(View.GONE);
                showSoftInput(mBqssEditView);
                return false;
            }
        });

        mBQSSMessages = new ArrayList<>();
        mBQSSMessageAdapter = new BQSSMessageAdapter(mBQSSMessages);
        mListView.setAdapter(mBQSSMessageAdapter);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGridView.setVisibility(View.GONE);
                hideSoftInput(mListView);
                return false;
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

    private void setGridViewConfigure() {
        mGridView.setNumColumns(4);
        mGridView.setPadding(10, 10, 10, 10);
        mGridView.setHorizontalSpacing(10);
        mGridView.setVerticalSpacing(10);
        mAdapter = new GridAdapter();
        mGridView.setAdapter(mAdapter);
        mGridView.setVisibility(View.GONE);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() >= view.getCount() - 1) {
                        if (mNeedLoadMore) {
                            mNeedLoadMore = false;
                            if (mKeyword.equals(BQSSConstants.TRENDING_STICKER_TAG)) {
                                getTrendingStickers(mCurrentPage);
                            } else {
                                getStickers(mCurrentPage);
                            }
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void bindView() {
        mBackBtn = (LinearLayout) findViewById(R.id.back_btn);
        mTitleText = (TextView) findViewById(R.id.title_text);
        mListView = (ListView) findViewById(R.id.listview);
        mBqssEditView = (EditText) findViewById(R.id.bqss_editview);
        mCancelBtn = (TextView) findViewById(R.id.cancel_btn);
        mBqssBtn = (ImageView) findViewById(R.id.go_bqss_btn);
        mTopLine = findViewById(R.id.top_line);
        mBqssBtn.setImageResource(R.mipmap.icon);
        mETLayout = (LinearLayout) findViewById(R.id.edittext_layout);
        mETLayout.setVisibility(View.GONE);
        mEditText = (EditText) findViewById(R.id.edit_text);
        mTextSendBtn = (Button) findViewById(R.id.text_send_btn);
        mBqssSearchLayout = (LinearLayout) findViewById(R.id.bqss_search_layout);
        mGridView = (GridView) findViewById(R.id.gridview);
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
                mIsHiddenBqss = true;
                mBqssSearchLayout.setVisibility(View.GONE);
                mGridView.setVisibility(View.GONE);
                mBqssBtn.setImageResource(R.mipmap.icon_gray);
                showEdittext();
            }
        });
        mBqssBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsHiddenBqss) {//打开表情搜搜
                    mIsHiddenBqss = false;
                    hideSoftInput(mListView);
                    mETLayout.setVisibility(View.GONE);
                    mBqssBtn.setImageResource(R.mipmap.icon);
                    mBqssSearchLayout.setVisibility(View.VISIBLE);
                    mGridView.setVisibility(View.VISIBLE);
                } else {//关闭表情搜搜
                    mIsHiddenBqss = true;
                    mBqssSearchLayout.setVisibility(View.GONE);
                    mGridView.setVisibility(View.GONE);
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
                    Toast.makeText(BQSSHalfSearchActivity.this, "不能发送空消息", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(BQSSHalfSearchActivity.this, "示例按钮：无功能", Toast.LENGTH_SHORT).show();
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

    private void getTrendingStickers(int page) {
        BQSSSearchApi.getTrendingStickers(page, BQSSConstants.LOADSIZE, new BQSSApiCallback<BQSSWebSticker>() {
            @Override
            public void onSuccess(BQSSApiResponseObject<BQSSWebSticker> result) {
                Message message = Message.obtain();
                message.arg1 = SUCCESS;
                message.arg2 = mCurrentPage;
                message.obj = result.getDatas();
                mHandler.sendMessage(message);
                if (result.getDatas().size() == BQSSConstants.LOADSIZE) {
                    ++mCurrentPage;
                    mNeedLoadMore = true;
                } else {
                    mNeedLoadMore = false;
                }
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

    private void getStickers(int page) {
        BQSSSearchApi.getSearchStickers(mKeyword, page, BQSSConstants.LOADSIZE, new BQSSApiCallback<BQSSWebSticker>() {
            @Override
            public void onSuccess(BQSSApiResponseObject<BQSSWebSticker> result) {
                Message message = Message.obtain();
                message.arg1 = SUCCESS;
                message.arg2 = mCurrentPage;
                message.obj = result.getDatas();
                mHandler.sendMessage(message);
                if (result.getDatas().size() == BQSSConstants.LOADSIZE && mCurrentPage < 5) {
                    ++mCurrentPage;
                    mNeedLoadMore = true;
                } else {
                    mNeedLoadMore = false;
                }
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

        @Override
        public int getCount() {
            if (content != null)
                return content.size();
            return 0;
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
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(parent.getContext(), R.layout.sticker_item, null);
                viewHolder.simpleDraweeView = (SimpleDraweeView) convertView.findViewById(R.id.sticker_img);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final String mainImg = content.get(position).getMain();
            Uri uri = Uri.parse(mainImg);

            DraweeController draweeController = Fresco.newDraweeControllerBuilder().setUri(uri).setAutoPlayAnimations(true).build();
            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(parent.getResources());
            GenericDraweeHierarchy hierarchy = builder.setPlaceholderImage(R.raw.bqss_sticker_loading).setFailureImage(R.mipmap.icon_loading_failed).setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER).build();
            viewHolder.simpleDraweeView.setHierarchy(hierarchy);
            viewHolder.simpleDraweeView.setController(draweeController);
            viewHolder.simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String str = BQSSBase64Img.getImageStrFromUrl(mainImg);
                            Message message = Message.obtain();
                            message.obj = str;
                            message.arg1 = REFRESH;
                            mHandler.sendMessage(message);
                        }
                    }).start();
                }
            });
            return convertView;
        }

        public void setContent(List<BQSSWebSticker> content) {
            this.content = content;
            notifyDataSetChanged();
        }
    }

    private static class MyHandler extends Handler {
        WeakReference<BQSSHalfSearchActivity> weakReference;

        MyHandler(BQSSHalfSearchActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BQSSHalfSearchActivity activity = weakReference.get();
            if (activity != null) {
                switch (msg.arg1) {
                    case SUCCESS:
                        List<BQSSWebSticker> BQSSWebStickerList = (List<BQSSWebSticker>) msg.obj;
                        if (BQSSWebStickerList == null || BQSSWebStickerList.size() == 0) {
                            Toast.makeText(activity, "无搜索结果", Toast.LENGTH_SHORT).show();
                        } else {
                            activity.mGridView.setVisibility(View.VISIBLE);
                            activity.mTopLine.setVisibility(View.VISIBLE);
                            if (msg.arg2 == 1) {
                                activity.mAdapter.content = BQSSWebStickerList;
                            } else {
                                if (activity.mAdapter.content == null)
                                    activity.mAdapter.content = BQSSWebStickerList;
                                else
                                    activity.mAdapter.content.addAll(BQSSWebStickerList);
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

    private void showSoftInput(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN && mBqssEditView.hasFocus()) {
            mKeyword = mBqssEditView.getText().toString().trim();
            if (!TextUtils.isEmpty(mKeyword)) {
                hideSoftInput(mGridView);
                mCurrentPage = 1;
                getStickers(mCurrentPage);
            } else {
                Toast.makeText(mContext, "请输入关键词", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    static class ViewHolder {
        private SimpleDraweeView simpleDraweeView;
    }
}

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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.siyanhui.mojif.bqss_demo.R;
import com.siyanhui.mojif.bqss_demo.api.BQSSApiCallback;
import com.siyanhui.mojif.bqss_demo.api.BQSSApiResponseObject;
import com.siyanhui.mojif.bqss_demo.api.BQSSSearchApi;
import com.siyanhui.mojif.bqss_demo.model.BQSSHotTag;
import com.siyanhui.mojif.bqss_demo.ui.widget.BQSSEditText;
import com.siyanhui.mojif.bqss_demo.ui.widget.BQSSWordWrapView;
import com.siyanhui.mojif.bqss_demo.utils.BQSSPreferenceHelper;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

/**
 * Created by fantasy on 17/1/4.
 * 全屏搜索主页面
 */

public class BQSSFullSearchHomePage extends Activity {
    public static int REQUESTCODE = 1002;
    public static int RESULTCODE = 1001;
    private static final int SUCCESS = 1;
    private static final int ERROR = 2;
    private GridView mGridView;
    private MyHandler myHandler;
    private BQSSEditText mEditText;
    private BQSSWordWrapView mBQSSWordWrapView;
    private LinearLayout mSearchHistoryView;
    private TextView mClearSearchHistoryBtn;
    private TextView mTitleText;
    private TextView mBackText;
    private LinearLayout mBackBtn;
    private GridAdapter mGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fs_home_page);
        bindView();
        mTitleText.setText("全屏搜索");
        mBackText.setText("返回");

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mGridView = (GridView) findViewById(R.id.home_gridview);
        mGridView.setNumColumns(3);
        myHandler = new MyHandler(this);
        mGridAdapter = new GridAdapter(this);
        mGridView.setAdapter(mGridAdapter);
        BQSSSearchApi.getHotTagStickers(new BQSSApiCallback<BQSSHotTag>() {
            @Override
            public void onSuccess(BQSSApiResponseObject<BQSSHotTag> result) {
                List<BQSSHotTag> BQSSHotTagList = result.getDatas();
                for (int i = 0; i < BQSSHotTagList.size(); i++) {
                    Message message = Message.obtain();
                    message.arg1 = SUCCESS;
                    message.obj = BQSSHotTagList;
                    myHandler.sendMessage(message);
                }

            }

            @Override
            public void onError(String errorInfo) {

            }
        });
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showSearchHistory();
                mSearchHistoryView.setVisibility(View.VISIBLE);
                return false;
            }
        });

        mClearSearchHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BQSSPreferenceHelper.clearSearchHistory();
                mBQSSWordWrapView.removeAllViews();
            }
        });
        setEditTextLeftDrawable();
    }

    private void bindView() {
        mTitleText = (TextView) findViewById(R.id.title_text);
        mBackText = (TextView) findViewById(R.id.back_text);
        mSearchHistoryView = (LinearLayout) findViewById(R.id.search_history_view);
        mBQSSWordWrapView = (BQSSWordWrapView) findViewById(R.id.word_wrap_view);
        mEditText = (BQSSEditText) findViewById(R.id.bqss_editview);
        mBackBtn = (LinearLayout) findViewById(R.id.back_btn);
        mClearSearchHistoryBtn = (TextView) findViewById(R.id.clear_history_btn);
    }

    private void setEditTextLeftDrawable() {
        float density = getResources().getDisplayMetrics().density;
        int defSize = (int) (18 * density + 0.5f);
        Drawable drawable = getResources().getDrawable(R.mipmap.search_icon);
        drawable.setBounds(0, 0, defSize, defSize);
        mEditText.setCompoundDrawables(drawable, null, null, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSearchHistoryView.setVisibility(View.GONE);
        showSearchHistory();
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
                            Intent intent = new Intent(BQSSFullSearchHomePage.this, BQSSFullSearchResultPage.class);
                            intent.putExtra("keyword", keyword);
                            startActivityForResult(intent, REQUESTCODE);
                        }
                    });
                    mBQSSWordWrapView.addView(textview);
                }
            }
        }
    }

    private static class MyHandler extends Handler {
        private WeakReference<BQSSFullSearchHomePage> weakReference;

        public MyHandler(BQSSFullSearchHomePage activity) {
            this.weakReference = new WeakReference<>(activity);
        }


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BQSSFullSearchHomePage activity = weakReference.get();
            if (activity != null) {
                switch (msg.arg1) {
                    case SUCCESS:
                        List<BQSSHotTag> hotTagList = (List<BQSSHotTag>) msg.obj;
                        activity.mGridAdapter.setContent(hotTagList);
                        break;
                    case ERROR:
                        Toast.makeText(activity, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }

            }

        }
    }

    private class GridAdapter extends BaseAdapter {
        private List<BQSSHotTag> bqssHotTags;
        private Activity activity;

        public GridAdapter(Activity activity) {
            this.activity = activity;
        }

        public void setContent(List<BQSSHotTag> content) {
            this.bqssHotTags = content;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (bqssHotTags != null)
                return bqssHotTags.size();
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
        public View getView(int position, View convertView, final ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.hot_tag_item, null);
                viewHolder = new ViewHolder();
                viewHolder.simpleDraweeView = (SimpleDraweeView) convertView.findViewById(R.id.hot_tag_img);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.hot_tag_text);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final BQSSHotTag BQSSHotTag = bqssHotTags.get(position);
            viewHolder.textView.setText(BQSSHotTag.getText());
            Uri uri = Uri.parse(BQSSHotTag.getCover());
            DraweeController draweeController = Fresco.newDraweeControllerBuilder().setUri(uri).setAutoPlayAnimations(true).build();
            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(parent.getResources());
            GenericDraweeHierarchy hierarchy = builder.setPlaceholderImage(R.raw.bqss_sticker_loading).setFailureImage(R.mipmap.icon_loading_failed).setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER).build();
            viewHolder.simpleDraweeView.setHierarchy(hierarchy);
            viewHolder.simpleDraweeView.setController(draweeController);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BQSSPreferenceHelper.addSearchKeyword(BQSSHotTag.getText());
                    Intent intent = new Intent(parent.getContext(), BQSSFullSearchResultPage.class);
                    intent.putExtra("keyword", BQSSHotTag.getText());
                    activity.startActivityForResult(intent, REQUESTCODE);
                }
            });
            return convertView;
        }
    }

    static class ViewHolder {
        private SimpleDraweeView simpleDraweeView;
        private TextView textView;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(BQSSFullSearchHomePage.this.getCurrentFocus().getWindowToken(), 0);
            }
            String keyword = mEditText.getText().toString();
            if (!TextUtils.isEmpty(keyword)) {
                BQSSPreferenceHelper.addSearchKeyword(keyword);
                mEditText.setText("");
                Intent intent = new Intent(BQSSFullSearchHomePage.this, BQSSFullSearchResultPage.class);
                intent.putExtra("keyword", keyword);
                startActivityForResult(intent, REQUESTCODE);
            } else {
                Toast.makeText(BQSSFullSearchHomePage.this, "请输入关键词", Toast.LENGTH_SHORT).show();
            }

            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE && resultCode == BQSSFullSearchResultPage.RESULTCODE) {
            setResult(RESULTCODE, data);
            finish();
        }
    }
}

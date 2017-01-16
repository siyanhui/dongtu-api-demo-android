package com.siyanhui.mojif.bqss_demo.ui.widget;

import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.siyanhui.mojif.bqss_demo.BQSSConstants;
import com.siyanhui.mojif.bqss_demo.R;

import java.util.List;

/**
 * Created by fantasy on 17/1/4.
 * 模拟聊天消息适配器
 */

public class BQSSMessageAdapter extends BaseAdapter {
    private List<String> mBQSSMessages;

    public BQSSMessageAdapter(List<String> datas) {
        this.mBQSSMessages = datas;
    }

    public void refresh(List<String> datas) {
        this.mBQSSMessages = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mBQSSMessages != null)
            return mBQSSMessages.size();
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
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(parent.getContext(), R.layout.bqss_chat_item_list_right, null);
            viewHolder.imageView = (SimpleDraweeView) convertView.findViewById(R.id.chat_item_img_content);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.chat_item_text_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String data = mBQSSMessages.get(position);
        if (data.startsWith(BQSSConstants.BQSS_TEXT_TAG)) {
            viewHolder.imageView.setVisibility(View.INVISIBLE);
            viewHolder.textView.setVisibility(View.VISIBLE);
            viewHolder.textView.setText(data.replace(BQSSConstants.BQSS_TEXT_TAG, ""));
        } else {
            viewHolder.imageView.setVisibility(View.VISIBLE);
            viewHolder.textView.setVisibility(View.INVISIBLE);
            Uri uri = Uri.parse("data:*;base64," + data);
            DraweeController draweeController = Fresco.newDraweeControllerBuilder().setUri(uri).setAutoPlayAnimations(true).build();
            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(parent.getResources());
            GenericDraweeHierarchy hierarchy = builder.setPlaceholderImage(R.raw.bqss_sticker_loading).setFailureImage(R.mipmap.icon_loading_failed).setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER).build();
            viewHolder.imageView.setController(draweeController);
            viewHolder.imageView.setHierarchy(hierarchy);
        }

        return convertView;
    }

    class ViewHolder {
        SimpleDraweeView imageView;
        TextView textView;
    }
}

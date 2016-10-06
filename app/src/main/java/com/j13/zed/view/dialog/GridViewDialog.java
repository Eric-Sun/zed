package com.j13.zed.view.dialog;

/**
 * Created by aaronliu on 15-12-21.
 */

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.j13.zed.R;


public class GridViewDialog extends AlertDialog implements AdapterView.OnItemClickListener {

    private Context mContext;
    private String mTitle;
    private final int[] mTitles;
    private final int[] mIcons;
    private int mSize;
    private int mNumColumns = 0;
    private OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int titleId);
    }

    public GridViewDialog(Context context, int titleId, int[] titles, int[] icons, OnItemClickListener listener) {
        super(context);
        mContext = context;
        mTitles = titles;
        mIcons = icons;
        if (titleId > 0) {
            mTitle = mContext.getString(titleId);
        }

        if (mTitles.length == mIcons.length) {
            mSize = mTitles.length;
        }

        mItemClickListener = listener;
    }

    public void setNumColumns(int columns) {
        mNumColumns = columns;
    }

    protected void onCreate(Bundle savedInstanceState) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.grid_dialog, null);

        if (!TextUtils.isEmpty(mTitle)) {
            setTitle(mTitle);
        }

        GridView gridView = (GridView) view.findViewById(R.id.grid_view);
        if (mNumColumns > 0) {
            gridView.setNumColumns(mNumColumns);
        }
        gridView.setAdapter(new GridDialogAdapter());
        gridView.setOnItemClickListener(this);

        setView(view);

        super.onCreate(savedInstanceState);

        FrameLayout custom = (FrameLayout) findViewById(R.id.custom);
        custom.setPadding(0, 0, 0, 0);

        setWindowMatchBottom();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dismiss();
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick(view, mTitles[position]);
        }
    }

    class GridDialogAdapter extends BaseAdapter {

        TextView title;
        ImageView icon;

        @Override
        public int getCount() {
            return mSize;
        }

        @Override
        public Integer getItem(int position) {
            return mTitles[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_grid_dialog, null);
            }

            int titleId = mTitles[position];
            int iconId = mIcons[position];

            title = (TextView) convertView.findViewById(R.id.title);
            icon = (ImageView) convertView.findViewById(R.id.icon);

            title.setText(titleId);
            icon.setImageResource(iconId);

            return convertView;
        }
    }
}

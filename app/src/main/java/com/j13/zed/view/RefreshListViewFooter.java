package com.j13.zed.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.j13.zed.R;


public class RefreshListViewFooter extends LinearLayout {
    public final static int STATE_NORMAL = 0;
    public final static int STATE_LOADING = 1;

    private View mContentView;
    private View mProgressBar;
    private TextView mHintView;

    public RefreshListViewFooter(Context context) {
        super(context);
        initView(context);
    }

    public RefreshListViewFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }


    public void setState(int state) {
        if (state == STATE_LOADING) {
            mHintView.setVisibility(View.VISIBLE);
            mHintView.setText(R.string.rlv_loading_more);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mHintView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    public void setBottomMargin(int height) {
        if (height < 0) return;
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        lp.bottomMargin = height;
        mContentView.setLayoutParams(lp);
    }

    public int getBottomMargin() {
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        return lp.bottomMargin;
    }


    /**
     * normal status
     */
    public void normal() {
        mHintView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }


    /**
     * loading status
     */
    public void loading() {
        mHintView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * hide footer when disable pull load more
     */
    public void hide() {
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        lp.height = 0;
        mContentView.setLayoutParams(lp);
    }

    /**
     * show footer
     */
    public void show() {
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        lp.height = LayoutParams.WRAP_CONTENT;
        mContentView.setLayoutParams(lp);
    }

    private void initView(Context context) {
        LinearLayout moreView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.rlv_footer, null);
        addView(moreView);
        moreView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        mContentView = moreView.findViewById(R.id.rlv_footer_content);
        mProgressBar = moreView.findViewById(R.id.rlv_footer_progressbar);
        mHintView = (TextView) moreView.findViewById(R.id.rlv_footer_hint_textview);
    }


}

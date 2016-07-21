package com.j13.zed.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.j13.zed.R;


public class RefreshListViewHeader extends LinearLayout {
    private View mContainer;
    private FrameLayout mProgress;
    private ImageView mArrowImageView;
    private SectorBar mPullProgress;
    private ProgressBar mProgressBar;
    private TextView mHintTextView;
    private TextView mTimeTextView;
    private int mState = STATE_NORMAL;

    private int mRefreshingText = R.string.rlv_refreshing;

    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;

    private static final int ROTATE_ANIM_DURATION = 180;

    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_REFRESHING = 2;
    public final static int STATE_PRIVATE = 3;

    public RefreshListViewHeader(Context context) {
        super(context);
        initView(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public RefreshListViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        // 初始情况，设置下拉刷新view高度为0
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        mContainer = LayoutInflater.from(context).inflate(
                R.layout.rlv_header, null);
        addView(mContainer, lp);
//        setGravity(Gravity.BOTTOM);

        mArrowImageView = (ImageView) findViewById(R.id.rlv_header_arrow);
        mHintTextView = (TextView) findViewById(R.id.rlv_header_hint_textview);
        mProgressBar = (ProgressBar) findViewById(R.id.rlv_header_progressbar);
        mTimeTextView = (TextView) findViewById(R.id.rlv_header_time);
        mPullProgress = (SectorBar) findViewById(R.id.pull_progress);
        mProgress = (FrameLayout) findViewById(R.id.fl_progress);

        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);
    }

    public void setState(int state) {
        if (state == mState) return;

        if (state == STATE_REFRESHING) {    // 显示进度
            mArrowImageView.clearAnimation();
            mArrowImageView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mTimeTextView.setVisibility(View.GONE);
        } else {    // 显示箭头图片
            mArrowImageView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mTimeTextView.setVisibility(View.VISIBLE);
        }

        switch (state) {
            case STATE_NORMAL:
                if (mState == STATE_READY) {
                    mArrowImageView.startAnimation(mRotateDownAnim);
                }
                if (mState == STATE_REFRESHING) {
                    mArrowImageView.clearAnimation();
                }
                mProgress.setVisibility(GONE);
                setPullProgress(0);
                mHintTextView.setText(R.string.rlv_pull_to_refresh);
                break;
            case STATE_READY:
                if (mState != STATE_READY) {
                    mArrowImageView.clearAnimation();
                    mArrowImageView.startAnimation(mRotateUpAnim);
                    mHintTextView.setText(R.string.rlv_release_to_refresh);
                    mProgress.setVisibility(GONE);
                    setPullProgress(0);
                }
                break;
            case STATE_REFRESHING:
                mHintTextView.setText(mRefreshingText);
                setPullProgress(0);
                mProgress.setVisibility(GONE);
                break;
            case STATE_PRIVATE:
                mHintTextView.setText(getContext().getString(R.string.open_private_folder));
                mArrowImageView.clearAnimation();
                mArrowImageView.setVisibility(View.GONE);
                mProgress.setVisibility(VISIBLE);
                break;
            default:
                mProgress.setVisibility(GONE);
                break;

        }

        mState = state;
    }

    public void setPullProgress(int progress){
        float p = (float)progress / 100;
        mPullProgress.setPercent(p);
        mPullProgress.invalidate();
    }

    public float getProgress(){
        return mPullProgress.getPercent();
    }
    public void setLastRefreshTime(String time) {
        mTimeTextView.setText(time);
    }

    public void setVisibleHeight(int height) {
        if (height < 0)
            height = 0;
        LayoutParams lp = (LayoutParams) mContainer
                .getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    public int getVisibleHeight() {
        return mContainer.getLayoutParams().height;
    }

    public void setRefreshingText(int mRefreshingText) {
        this.mRefreshingText = mRefreshingText;
    }
    
}

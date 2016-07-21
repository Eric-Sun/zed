package com.j13.zed.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Scroller;

import com.j13.zed.R;


public class RefreshListView extends ListView implements OnScrollListener {

    //    private final int PRIVATE_PADDING = 200;
//    private final int MESSAGE_PADDING = 200;

    private final int PULL_SCALE = 7;
    private float mLastY = -1; // save event y
    private Scroller mScroller; // used for scroll back
    private OnScrollListener mScrollListener; // user's scroll listener

    // the interface to trigger refresh and load more.
    private OnRefreshListener mListViewListener;

    // -- header view
    private RefreshListViewHeader mHeaderView;
    // header view content, use it to calculate the Header's height. And hide it
    // when disable pull refresh.
    private View mHeaderViewContent;
    private int mHeaderViewHeight; // header view's height
    private boolean mEnablePullRefresh = true;
    private boolean mPullRefreshing = false; // is refreshing.

    // -- footer view
    private RefreshListViewFooter mFooterView;
    private boolean mEnablePullLoad = false;
    private boolean mPullLoading;
    private boolean mIsFooterReady = false;

    private boolean mEnablePullPrivate = false;

    // total list items, used to detect is at the bottom of listview.
    private int mTotalItemCount;

    // for mScroller, scroll back from header or footer.
    private int mScrollBack;
    private final static int SCROLLBACK_HEADER = 0;
    private final static int SCROLLBACK_FOOTER = 1;

    private final static int SCROLL_DURATION = 400; // scroll back duration
    private final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px
    // at bottom, trigger
    // load more.
    private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
    private int mScreenHeight;
    // feature.

    private Handler mHandler = new Handler();
    private static final long SMOOTH_DELAY = 15;

    /**
     * @param context
     */
    public RefreshListView(Context context) {
        super(context);
        initWithContext(context);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWithContext(context);
    }

    private void initWithContext(Context context) {
        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        mScreenHeight = displaymetrics.heightPixels;

        mScroller = new Scroller(context, new DecelerateInterpolator());
        // XListView need the scroll event, and it will dispatch the event to
        // user's listener (as a proxy).
        super.setOnScrollListener(this);

        // init header view
        mHeaderView = new RefreshListViewHeader(context);
        mHeaderViewContent = mHeaderView.findViewById(R.id.rlv_header_content);
        addHeaderView(mHeaderView);

        // init footer view
        mFooterView = new RefreshListViewFooter(context);

        // init header height
        mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mHeaderViewHeight = mHeaderViewContent.getHeight();
                        getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        // make sure XListViewFooter is the last footer view, and only add once.
        if (!mIsFooterReady) {
            mIsFooterReady = true;
            addFooterView(mFooterView);
        }
        super.setAdapter(adapter);
    }

    /**
     * enable or disable pull down refresh feature.
     *
     * @param enable
     */
    public void setPullRefreshEnable(boolean enable) {
        mEnablePullRefresh = enable;
        if (!mEnablePullRefresh) { // disable, hide the content
            mHeaderViewContent.setVisibility(View.INVISIBLE);
        } else {
            mHeaderViewContent.setVisibility(View.VISIBLE);
        }
    }

    public void setPullPrivateEnable(boolean enable) {
        mEnablePullPrivate = enable;
    }

    /**
     * enable or disable pull up load more feature.
     *
     * @param enable
     */
    public void setPullLoadEnable(boolean enable) {
        mEnablePullLoad = enable;
        if (!mEnablePullLoad) {
            mFooterView.hide();
            mFooterView.setOnClickListener(null);
            //make sure "pull up" don't show a line in bottom when listview with one page
            setFooterDividersEnabled(false);
        } else {
            mPullLoading = false;
            mFooterView.show();
            mFooterView.setState(RefreshListViewFooter.STATE_NORMAL);
            //make sure "pull up" don't show a line in bottom when listview with one page
            setFooterDividersEnabled(true);
            // both "pull up" and "click" will invoke load more.
            mFooterView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLoadMore();
                }
            });
        }
    }

    public boolean isRefreshing() {
        return mPullRefreshing;
    }

    /**
     * stop refresh, reset header view.
     */
    public void onRefreshComplete() {
        if (mPullRefreshing) {
            mPullRefreshing = false;
            resetHeaderHeight();

            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mHeaderView != null && !mPullRefreshing) {
                        mHeaderView.setState(RefreshListViewHeader.STATE_NORMAL);
                    }
                }
            }, 500);
        }
    }

    public boolean isLoadingMore() {
        return mPullLoading;
    }

    /**
     * stop load more, reset footer view.
     */
    public void onLoadMoreComplete() {
        if (mPullLoading) {
            mPullLoading = false;
            mFooterView.setState(RefreshListViewFooter.STATE_NORMAL);
        }
    }

    /**
     * set last refresh time
     *
     * @param time
     */
    public void setRefreshTime(String time) {
        mHeaderView.setLastRefreshTime(time);
    }

    /**
     * set loading text
     *
     * @param resourceId
     */
    public void setRefreshingText(int resourceId) {
        mHeaderView.setRefreshingText(resourceId);
    }

    private void updateHeaderHeight(float delta) {
        int height = (int) delta + mHeaderView.getVisibleHeight();
        mHeaderView.setVisibleHeight(height);
        if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
            if (height > mHeaderViewHeight) {
                if (height > mHeaderViewHeight + mScreenHeight / PULL_SCALE && mEnablePullPrivate) {
                    mHeaderView.setState(RefreshListViewHeader.STATE_PRIVATE);
                    mHeaderView.setPullProgress(height - mHeaderViewHeight - mScreenHeight / PULL_SCALE);
                } else {
                    mHeaderView.setState(RefreshListViewHeader.STATE_READY);
                }
            } else {
                mHeaderView.setState(RefreshListViewHeader.STATE_NORMAL);
            }
        }
        setSelection(0); // scroll to top each time
    }

    /**
     * reset header view's height.
     */
    private void resetHeaderHeight() {
        int height = mHeaderView.getVisibleHeight();
        if (height == 0) // not visible.
            return;
        // refreshing and header isn't shown fully. do nothing.
        if (mPullRefreshing && height <= mHeaderViewHeight) {
            return;
        }
        int finalHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mPullRefreshing && height > mHeaderViewHeight) {
            finalHeight = mHeaderViewHeight;
        }

        mScrollBack = SCROLLBACK_HEADER;
        mScroller.forceFinished(true);
        mScroller.startScroll(0, height, 0, finalHeight - height,
                SCROLL_DURATION);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.post(mScrollRunnable);
    }

    private void startLoadMore() {
        mPullLoading = true;
        mFooterView.setState(RefreshListViewFooter.STATE_LOADING);
        if (mListViewListener != null) {
            mListViewListener.onLoadMore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (mEnablePullRefresh && getFirstVisiblePosition() == 0
                        && (mHeaderView.getVisibleHeight() > 0 || deltaY > 0)) {
                    // the first item is showing, header has shown or pull down.
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                }
                break;
            default:
                mLastY = -1; // reset
                if (getFirstVisiblePosition() == 0) {
                    int height = mHeaderView.getVisibleHeight();
                    if (mEnablePullPrivate && mHeaderView.getProgress() == 1) {
                        mHeaderView.setState(RefreshListViewHeader.STATE_NORMAL);
                        mHeaderView.setVisibleHeight(0);
                        if (mListViewListener != null) {
                            mListViewListener.onEnterPrivate();
                        }
                    }
                    // invoke refresh
                    else if (mEnablePullRefresh
                            && height > mHeaderViewHeight
                            && (!mEnablePullPrivate || height < mHeaderViewHeight + mScreenHeight / PULL_SCALE)) {
                        mPullRefreshing = true;
                        mHeaderView.setState(RefreshListViewHeader.STATE_REFRESHING);
                        if (mListViewListener != null) {
                            mListViewListener.onRefresh();
                        }
                    } else {
                        if (mEnablePullRefresh && mPullRefreshing) {
                            mHeaderView.setState(RefreshListViewHeader.STATE_REFRESHING);
                        } else {
                            mHeaderView.setState(RefreshListViewHeader.STATE_NORMAL);
                        }
                    }
                    resetHeaderHeight();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private Runnable mScrollRunnable = new Runnable() {
        @Override
        public void run() {
            final Scroller scroller = mScroller;
            if (scroller.computeScrollOffset()) {
                if (mScrollBack == SCROLLBACK_HEADER) {
                    mHeaderView.setVisibleHeight(mScroller.getCurrY());
                } else {
                    mFooterView.setBottomMargin(mScroller.getCurrY());
                }

                mHandler.removeCallbacksAndMessages(null);
                mHandler.postDelayed(this, SMOOTH_DELAY);
            }
        }
    };

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }

        if (scrollState == SCROLL_STATE_IDLE
                && getLastVisiblePosition() >= mTotalItemCount - 1
                && mEnablePullLoad && !mPullRefreshing) {
            startLoadMore();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // send to user's listener
        mTotalItemCount = totalItemCount;
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
                    totalItemCount);
        }
    }

    public void setOnRefreshListener(OnRefreshListener l) {
        mListViewListener = l;
    }

    /**
     * implements this interface to get refresh/load more event.
     */
    public interface OnRefreshListener {
        public void onRefresh();

        public void onLoadMore();

        public void onEnterPrivate();
    }
}

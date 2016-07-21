package com.j13.zed.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.j13.zed.R;
import com.j13.zed.adapter.DZInfoAdapter;
import com.j13.zed.dz.DZInfoLoadEvent;
import com.j13.zed.dz.DZManager;
import com.j13.zed.helper.FileIconHelper;
import com.j13.zed.view.RefreshListView;
import com.j13.zed.view.ToastTextView;

import de.greenrobot.event.EventBus;

/**
 * Created by sunbo on 16/5/28.
 */
public class HotDZFragment extends LazyFragment {

    private Activity activity;
    private Context context;


    private DZManager dzManager;
    private DZInfoAdapter adapter = null;
    private FrameLayout mRootView = null;

    private RefreshListView listView = null;

    private ToastTextView toastTextView = null;
    private FileIconHelper fileIconHelper = null;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        context = getContext();
        fileIconHelper = FileIconHelper.getInstance(context);

    }


    @Override
    public void onUserVisible(boolean first) {
        super.onUserVisible(first);
        initData();
    }

    private void initData() {
        dzManager.load();
    }


    public void onEventMainThread(DZInfoLoadEvent event) {

        adapter.setData(event.getDzInfoList());
        adapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = (FrameLayout) inflater.inflate(R.layout.layout_video_list, container, false);
        dzManager = DZManager.getInstance(context);


        toastTextView = (ToastTextView) mRootView.findViewById(R.id.toast);
        toastTextView.setAnimation(R.anim.pull_down, R.anim.push_up);
        listView = (RefreshListView) mRootView.findViewById(R.id.video_list);
        listView.setRefreshingText(R.string.file_loading);
        listView.setChoiceMode(RefreshListView.CHOICE_MODE_SINGLE);

        listView.setPullRefreshEnable(false);
        listView.setPullLoadEnable(false);
        adapter = new DZInfoAdapter(context);
        listView.setAdapter(adapter);

        listView.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDZ(false);
            }

            @Override
            public void onLoadMore() {
//                if (!mIsLoading) {
//                    loadMoreVideoList(mLastKey);
//                }
            }

            @Override
            public void onEnterPrivate() {

            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i == SCROLL_STATE_FLING) {
                    fileIconHelper.pause();
                } else {
                    fileIconHelper.resume();
                }
                if (adapter != null) {
//                    adapter.setScrollState(i);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
//                mVideoFrameLayout.onListScroll();
            }
        });

        EventBus.getDefault().register(this);

        return mRootView;
    }

    private void refreshDZ(boolean isFirst) {
        dzManager.load();
    }

}

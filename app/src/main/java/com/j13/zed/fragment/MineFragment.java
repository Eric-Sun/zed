package com.j13.zed.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.j13.zed.R;
import com.j13.zed.activity.LoginActivity;
import com.j13.zed.activity.UserInfoSettingActivity;
import com.j13.zed.helper.FileIconHelper;
import com.j13.zed.helper.ProfileHeader;
import com.j13.zed.user.User;
import com.j13.zed.user.UserContext;
import com.j13.zed.user.event.LoginSuccessEvent;
import com.j13.zed.util.Constants;
import com.j13.zed.util.DebugLog;
import com.j13.zed.util.StringUtils;
import com.j13.zed.view.RefreshListView;

import de.greenrobot.event.EventBus;

public class MineFragment extends BaseMainFragment implements  View.OnClickListener,Constants {

    private static final String TAG = "MineFragmentssss";

    private Activity mActivity;
    private FileIconHelper mFileIconHelper;

    private View mContent;
    private ViewGroup mHeaderContainer;
    private ProfileHeader mHeader;
    private RefreshListView mListView;
    private boolean isNeedLoad = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();

        mFileIconHelper = FileIconHelper.getInstance(mActivity);

//        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }

    @Override
    protected int geContentViewId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected boolean isDisplayWithToolbar() {
        return false;
    }

    @Override
    protected void initView(LayoutInflater inflater, View rootView, Bundle savedInstanceState) {

        mListView = (RefreshListView) mRootView.findViewById(R.id.list_view);
        mListView.setRefreshingText(R.string.file_loading);

        mListView.setPullRefreshEnable(false);
        mListView.setPullLoadEnable(false);

        mContent = inflater.inflate(R.layout.layout_mine_user_center, null);
        mListView.addHeaderView(mContent);

        mHeader = new ProfileHeader(false);
        mHeader.onCreate(mActivity);
        mHeaderContainer = (ViewGroup) setupViewClick(mRootView, R.id.mine_header);
        mHeaderContainer.removeAllViewsInLayout();
        mHeaderContainer.addView(mHeader.getView());

        mListView.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }

            @Override
            public void onLoadMore() {

            }

            @Override
            public void onEnterPrivate() {

            }
        });

        mListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
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
                return null;
            }
        });

    }

    private View setupViewClick(View rootView, int id) {
        View view = rootView == null ? null : rootView.findViewById(id);
        if (view != null) {
            view.setOnClickListener(this);
        }
        return view;
    }

    private boolean isUserLogin() {
        boolean isLogin = UserContext.getInstance(getActivity()).isLogin();
        if (!isLogin) {
            Intent it = new Intent(getActivity(), LoginActivity.class);
//            it.putExtra(EXTRA_TOPIC, MINE_TAG);
            getActivity().startActivity(it);
        }
        return isLogin;
    }

    private long getUserId() {
        UserContext user = UserContext.getInstance(mActivity);
        return user == null ? 0 : user.getLoginUid();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_header:
                if (isUserLogin()) {
                    Intent to = new Intent(mActivity, UserInfoSettingActivity.class);
                    to.putExtra(EXTRA_USER_ID, getUserId());
                    startActivity(to);
                }
                break;

            default:
                break;
        }

    }

    @Override
    public void onUserVisible(boolean first) {
        super.onUserVisible(first);
        if (UserContext.getInstance(getActivity()).isLogin()) {
            if (first || isNeedLoad) {
                load();
            } else {
                User user = UserContext.getInstance(getActivity()).getLoginUser();
                loadUserInfo(user);
            }
        } else {
//            loadUserInfo(0, getString(R.string.click_to_login), null, null, getString(R.string.click_to_login_msg), 0, 0, 0, 0, 0, 0);
        }
    }

    @Override
    public void onUserInvisible(boolean first) {
        super.onUserInvisible(first);
    }

    private void load() {
        isNeedLoad = false;
        mListView.setPullRefreshEnable(true);
//        UserInfoManager.getInstance(mActivity).getUserInfo(UserContext.getInstance(mActivity).getLoginUid(), UserInfoEvent.FROM_MINE_USER_CENTER);
    }

    public void onEventMainThread(LoginSuccessEvent event) {
        DebugLog.d(TAG, "onEventMainThread  LoginSuccessEvent=" + event);

        User user = UserContext.getInstance(mActivity).getCurrentUser();
        if (user != null) {
            loadUserInfo(user);
        }
        //load();
    }


    private void loadUserInfo(User user) {
        if (mHeader != null) {
            mHeader.setUserName(user.getUserName());
            mHeader.setHeaderIcon(mFileIconHelper, user.getHeadIconUrl());
            mHeader.setUserInfo(user.getDesc());
            mHeader.setUserSex(user.getSex());
        }
    }

    private String getCountString(long count, boolean empty) {
        String result = empty ? "" : "0";
        if (count > 0) {
            result = StringUtils.formatNumForCN(count);
        }
        return result;
    }

}

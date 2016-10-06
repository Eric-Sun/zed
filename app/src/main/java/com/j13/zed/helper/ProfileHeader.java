package com.j13.zed.helper;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.j13.zed.R;
import com.j13.zed.user.UserContext;

/**
 * Created by lz on 16/4/14.
 */

public class ProfileHeader implements IHeader {

    private Activity mActivity;
    private View mHeaderView;
    private ImageView mHeaderIcon;
    private TextView mUserName;
    private TextView mUserInfo;
    private View mFollowLayout;
    private TextView mFollowBtn;
    private TextView mFollowedBtn;

    private boolean mIsOther = false;

    public interface FollowListener {
        void follow(boolean isFollow);
    }

    private FollowListener mFollowListener;

    public ProfileHeader(boolean other) {
        mIsOther = other;
    }

    @Override
    public void onCreate(Activity activity) {
        mActivity = activity;
        createView();
    }

    private void createView() {

        int rootId = mIsOther ? R.layout.header_other_profile : R.layout.header_own_profile;
        mHeaderView = LayoutInflater.from(mActivity).inflate(rootId, null);

        mHeaderIcon = (ImageView) mHeaderView.findViewById(R.id.header_icon_img);
        mUserName = (TextView) mHeaderView.findViewById(R.id.user_name_tv);
        mUserInfo = (TextView) mHeaderView.findViewById(R.id.user_info);

        if (mIsOther) {
            mFollowLayout = mHeaderView.findViewById(R.id.fl_follow);

            mFollowBtn = (TextView) mHeaderView.findViewById(R.id.btn_follow);
            mFollowedBtn = (TextView) mHeaderView.findViewById(R.id.btn_followed);

            mFollowLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isFollow = mFollowLayout.isSelected();
//                    if (mFollowLayout.isSelected()) {
//                        mFollowBtn.setVisibility(View.VISIBLE);
//                        mFollowedBtn.setVisibility(View.GONE);
//                        mFollowLayout.setSelected(false);
//                    } else {
//                        mFollowBtn.setVisibility(View.GONE);
//                        mFollowedBtn.setVisibility(View.VISIBLE);
//                        mFollowLayout.setSelected(true);
//                    }

                    if (mFollowListener != null) {
                        mFollowListener.follow(!isFollow);
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onLoadData() {

    }

    @Override
    public View getView() {
        return mHeaderView;
    }

    public void setUserInfo(String info) {
        if (TextUtils.isEmpty(info)) {
            mUserInfo.setVisibility(View.GONE);
            mUserInfo.setText(null);
        } else {
            mUserInfo.setVisibility(View.VISIBLE);
            mUserInfo.setText(info);
        }
    }

    public void setHeaderIcon(FileIconHelper fileIconHelper, String url) {
        int iconId = mIsOther ? R.drawable.default_head_icon : R.drawable.my_head_icon;
        if (TextUtils.isEmpty(url)) {
            mHeaderIcon.setImageResource(iconId);
            return;
        }
        fileIconHelper.loadInto(url, 0, 0, iconId, mHeaderIcon, true);
    }

    public void setUserName(String name) {
        if (!TextUtils.isEmpty(name) && UserContext.getInstance(mActivity).getCurrentUser() != null) {
            if (name.equals(UserContext.getInstance(mActivity).getCurrentUser().getPhoneNum()) && name.length() == 11) {
                String temp = name.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                mUserName.setText(temp);
                return;
            }
        }
        mUserName.setText(name);
    }

    public void setUserSex(String sex) {
        if ("male".equals(sex)) {
            mUserName.setCompoundDrawablesWithIntrinsicBounds(null, null, mActivity.getResources().getDrawable(R.drawable.man_icon), null);
        } else if ("female".equals(sex)) {
            mUserName.setCompoundDrawablesWithIntrinsicBounds(null, null, mActivity.getResources().getDrawable(R.drawable.women_icon), null);
        } else {
            mUserName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    public TextView getUserInfo() {
        return mUserInfo;
    }

    public void showFollowView(boolean isShow, boolean isFollowed) {
        if (!mIsOther) {
            return;
        }
        if (isShow) {
            if (isFollowed) {
                mFollowLayout.setSelected(true);
                mFollowedBtn.setVisibility(View.VISIBLE);
                mFollowBtn.setVisibility(View.GONE);
            } else {
                mFollowLayout.setSelected(false);
                mFollowedBtn.setVisibility(View.GONE);
                mFollowBtn.setVisibility(View.VISIBLE);
            }
            mFollowLayout.setVisibility(View.VISIBLE);
        } else {
            mFollowLayout.setVisibility(View.GONE);
        }
    }

    public void setFollowListener(FollowListener listener) {
        if (!mIsOther) {
            return;
        }
        mFollowListener = listener;
    }

}

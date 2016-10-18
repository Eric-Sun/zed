package com.j13.zed.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.j13.zed.Catagory;
import com.j13.zed.Constants;
import com.j13.zed.fragment.HotDZFragment;
import com.j13.zed.fragment.MineFragment;

public class ScrollFragmentAdapter extends BaseFragmentAdapter {

    private Context mContext;

    public ScrollFragmentAdapter(Context context, FragmentManager manager) {
        super(manager);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        if (position == 1) {
            return new HotDZFragment();

        } else {
            return new MineFragment();
        }

    }

    @Override
    public String makeFragmentName(int index) {
        if (index == 1) {
            return "最热";

        } else {
            return "我的";
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 1) {
            return "最热";

        } else {
            return "我的";
        }
    }
}

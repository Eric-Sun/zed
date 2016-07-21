package com.j13.zed.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.j13.zed.fragment.HotDZFragment;

public class ScrollFragmentAdapter extends BaseFragmentAdapter {

    private Context mContext;

    public ScrollFragmentAdapter(Context context, FragmentManager manager) {
        super(manager);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        return new HotDZFragment();
    }

    @Override
    public String makeFragmentName(int index) {
        return "最热";
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "最热";
    }
}

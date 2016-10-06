package com.j13.zed.fragment;

public interface ILazyFragment {

    /**
     * fragment可见
     */
    void onUserVisible(boolean first);

    /**
     * fragment不可见
     */
    void onUserInvisible(boolean first);
}

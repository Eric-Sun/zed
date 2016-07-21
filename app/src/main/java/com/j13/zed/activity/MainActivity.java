/**
 *
 */
package com.j13.zed.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.widget.AbsListView;
import android.widget.SimpleAdapter;

import com.j13.zed.R;
import com.j13.zed.adapter.DZInfoAdapter;
import com.j13.zed.adapter.ScrollFragmentAdapter;
import com.j13.zed.dz.DZInfoLoadEvent;
import com.j13.zed.dz.DZManager;
import com.j13.zed.fragment.HotDZFragment;
import com.j13.zed.view.ScrollControlViewPager;

import de.greenrobot.event.EventBus;

/**
 * @author allin
 */
public class MainActivity extends BaseActivity{


    private ActionBar actionBar = null;
    final List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    final Context ctxt = this;


    private TabLayout mTabBar;
    private AppBarLayout mAppBar;
    private ScrollControlViewPager mViewPager;
    private ScrollFragmentAdapter mPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.dz_activity);
        super.onCreate(savedInstanceState);

        actionBar = getActionBar();
        mViewPager = (ScrollControlViewPager) findViewById(R.id.vp_short_video_container);
        mPagerAdapter = new ScrollFragmentAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        //设置Tab
        mAppBar = (AppBarLayout) findViewById(R.id.appbar_short_video);
        mTabBar = (TabLayout) findViewById(R.id.tab_short_video_indicator);
        mTabBar.setupWithViewPager(mViewPager);
        mTabBar.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
//                Fragment fragment = getCurrentFragment();
//                if (fragment != null && fragment instanceof HotDZFragment) {
//                    ((HotDZFragment) fragment).goTop();
//                }
            }
        });
        mViewPager.setCurrentItem(1);

    }

    private Fragment getCurrentFragment() {
        int position = mViewPager.getCurrentItem();
        String tag= mPagerAdapter.makeFragmentName(position);
        return getSupportFragmentManager().findFragmentByTag(tag);
    }


}

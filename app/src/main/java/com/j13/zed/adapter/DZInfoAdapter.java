package com.j13.zed.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.j13.zed.dz.DZInfo;
import com.j13.zed.view.DZInfoView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by sunbo on 16/5/29.
 */
public class DZInfoAdapter extends BaseAdapter {

    private Context context = null;

    private List<DZInfo> dzInfoList = new LinkedList<DZInfo>();

    public DZInfoAdapter(Context context) {
        this.context = context;
    }



    public void setData(List<DZInfo> dzInfoList) {
        this.dzInfoList = dzInfoList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dzInfoList != null ? dzInfoList.size() : 0;
    }

    @Override
    public DZInfo getItem(int position) {
        return dzInfoList != null ? dzInfoList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DZInfo dzInfo = getItem(position);

        DZInfoView view = new DZInfoView(context, dzInfo);
        view.getContent().setText(dzInfo.getContent().replaceAll("<br />","\n"));

        return view;
    }

}

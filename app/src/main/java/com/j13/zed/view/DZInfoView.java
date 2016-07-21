package com.j13.zed.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.j13.zed.R;
import com.j13.zed.dz.DZInfo;

/**
 * Created by sunbo on 16/5/29.
 */
public class DZInfoView extends FrameLayout {
    private Context context;
    private TextView content;
    private DZInfo dzInfo;

    public TextView getContent() {
        return content;
    }

    public void setContent(TextView content) {
        this.content = content;
    }

    public DZInfo getDzInfo() {
        return dzInfo;
    }

    public void setDzInfo(DZInfo dzInfo) {
        this.dzInfo = dzInfo;
    }

    public DZInfoView(Context context, DZInfo dzInfo) {
        super(context);
        this.context = context;
        this.dzInfo = dzInfo;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dz_info_view, this, true);
        content = (TextView) view.findViewById(R.id.content);
    }

}

package com.j13.zed.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.j13.zed.R;

/**
 * Created by lz on 16/4/20.
 */
public class TextImgCenterBtn extends FrameLayout{

    private ImageView imageView;
    private TextView textView;
    private View mContainer;

    public TextImgCenterBtn(Context context) {
        super(context);
        initView();
    }

    public TextImgCenterBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TextImgCenterBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        mContainer = layoutInflater.inflate(R.layout.text_img_center_layout, this);

        imageView = (ImageView) findViewById(R.id.img_iv);
        textView = (TextView) findViewById(R.id.text_tv);
    }

    public void setBtnContent(int imgId, int textId) {
        if (imageView != null) {
            imageView.setImageResource(imgId);
        }
        if (textView != null) {
            textView.setText(textId);
        }
    }

    public void setBackground(int resId) {
        if (mContainer != null) {
            mContainer.setBackgroundResource(resId);
        }
    }

    public void setTextColor(int color) {
        if (textView != null) {
            textView.setTextColor(color);
        }
    }
}

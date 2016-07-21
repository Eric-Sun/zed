package com.j13.zed.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.j13.zed.R;


public class SectorBar extends View {

    private int mBackgroundColor;
    private int mPercentColor;
    private float mPercent;
    private Paint mPaint;
    private int mStrokeWidth;
    private int mDrawStyle;

    public SectorBar(Context context) {
        this(context, null);
    }

    public SectorBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SectorBar);
        mBackgroundColor = a.getColor(R.styleable.SectorBar_backgroundColor, Color.BLACK);
        mPercentColor = a.getColor(R.styleable.SectorBar_percentColor, Color.WHITE);
        mPercent = a.getFloat(R.styleable.SectorBar_percent, 0);
        mStrokeWidth = a.getInt(R.styleable.SectorBar_strokeWidth, 0);
        mDrawStyle = a.getInt(R.styleable.SectorBar_drawStyle, 1);

        a.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        if (mDrawStyle == 1) {
            mPaint.setStyle(Paint.Style.FILL);
        } else {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(mStrokeWidth);
        }
    }

    public void setPercent(float percent) {
        if (percent < 0) {
            mPercent = 0;
        } else if (percent > 1) {
            mPercent = 1;
        } else {
            mPercent = percent;
        }
    }

    public float getPercent() {
        return mPercent;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // draw background
        mPaint.setColor(mBackgroundColor);
        int radius = Math.min(width, height) >> 1;
        canvas.drawCircle(width >> 1, height >> 1, radius - mStrokeWidth, mPaint);

        // draw percent
        mPaint.setColor(mPercentColor);
        RectF oval = new RectF(mStrokeWidth, mStrokeWidth, width - mStrokeWidth, height - mStrokeWidth);
        canvas.drawArc(oval, 270, mPercent * 360, mDrawStyle == 1, mPaint);
    }
}

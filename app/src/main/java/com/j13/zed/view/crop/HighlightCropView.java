
package com.j13.zed.view.crop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

public class HighlightCropView extends View {
    public HighlightCropView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public HighlightCropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private final Paint mFocusPaint = new Paint();
    private final Paint mNoFocusPaint = new Paint();
    private final Paint mOutlinePaint = new Paint();
    public RectF mCropRect;
    private RectF mRect;
    private int mPaddingLeft = 10;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mPaddingLeft = (int) (getWidth() / 20);
        mCropRect = new RectF(mPaddingLeft,
                (getHeight() - getWidth() + 2 * mPaddingLeft) / 2, getWidth()
                        - mPaddingLeft,
                (getHeight() - getWidth() + 2 * mPaddingLeft) / 2 + getWidth()
                        - 2 * mPaddingLeft);
        mRect = new RectF(0, 0, getWidth(), getHeight());
    }

    public HighlightCropView(Context context) {
        super(context);
        init();
    }

    void init() {
        mFocusPaint.setARGB(125, 50, 50, 50);
        mNoFocusPaint.setARGB(125, 50, 50, 50);

        mOutlinePaint.setStrokeWidth(3F);
        mOutlinePaint.setStyle(Paint.Style.STROKE);
        mOutlinePaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.save();
        Path path = new Path();
        path.addRect(new RectF(mCropRect), Path.Direction.CW);
        mOutlinePaint.setColor(Color.parseColor("#f1be23"));
        canvas.clipPath(path, Region.Op.DIFFERENCE);
        canvas.drawRect(mRect, mNoFocusPaint);

        canvas.restore();
        canvas.drawPath(path, mOutlinePaint);

    }
}

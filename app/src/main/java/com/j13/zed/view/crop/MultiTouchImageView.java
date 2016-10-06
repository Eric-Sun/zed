
package com.j13.zed.view.crop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class MultiTouchImageView extends ImageView {
    public boolean mCrop;
    private int mMode = MODE_NONE;
    private static final int MODE_NONE = 0;
    private static final int MODE_DRAG = 1;
    private static final int MODE_ZOOM = 2;
    private static final float MAX_SCALE = 30f;
    private static final float MIN_SCALE = 0.1f;
    private static final int SCALE_ANIM = 0;
    private static final int INIT_SCALE = 1;

    private PointF mStartPoint = new PointF();
    private Matrix mMatrix = new Matrix();
    private Matrix mCurrentMatrix = new Matrix();
    private float mStartDis;
    private PointF mMidPoint;
    private Bitmap mBitmap;
    public RectF mDrawRect;
    public float mScale = 1;
    private HighlightCropView mHighlightView;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCALE_ANIM:
                    checkScale();
                    break;

                case INIT_SCALE:
                    initPostion();
                    break;
            }

        }

        ;
    };

    public HighlightCropView getHighlightView() {
        return mHighlightView;
    }

    public void setHighlightView(HighlightCropView highlightView) {
        this.mHighlightView = highlightView;
    }

    public MultiTouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiTouchImageView(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    }

    private void initPostion() {
        if (mHighlightView.mCropRect == null) {
            mHandler.sendEmptyMessageDelayed(INIT_SCALE, 50);
            return;
        }
        float viewWidth = getWidth();
        float viewHeight = getHeight();

        float w = mBitmap.getWidth();
        float h = mBitmap.getHeight();
        mDrawRect = new RectF(0, 0, w, h);
        moveBy((viewWidth - w) / 2, (viewHeight - h) / 2);

    }

    public void moveBy(float dx, float dy) {
        mCurrentMatrix.set(getImageMatrix());
        mMatrix.set(mCurrentMatrix);
        mMatrix.postTranslate(dx, dy);
        setImageMatrix(mMatrix);
        translatePostion(dx, dy);
        initScale();
    }

    private void translatePostion(float dx, float dy) {
        mDrawRect.left += dx;
        mDrawRect.top += dy;
        mDrawRect.right += dx;
        mDrawRect.bottom += dy;
    }

    private void scalePostion(float scale, float midx, float midy) {
        mDrawRect.left = midx - (midx - mDrawRect.left) * scale;
        mDrawRect.right = midx + (mDrawRect.right - midx) * scale;
        mDrawRect.top = midy - (midy - mDrawRect.top) * scale;
        mDrawRect.bottom = midy + (mDrawRect.bottom - midy) * scale;
    }

    public void initScale() {
        RectF cropDrawRect = mHighlightView.mCropRect;
        float midx = mDrawRect.right - mDrawRect.width() / 2;
        float midy = mDrawRect.bottom - mDrawRect.height() / 2;
        float scale = 1;

        if (mDrawRect.width() > mDrawRect.height()) {
            scale = cropDrawRect.height() / mDrawRect.height();

        } else {
            scale = cropDrawRect.width() / mDrawRect.width();
        }
        mMatrix.postScale(scale, scale, midx, midy);
        setScale(scale);
        scalePostion(scale, midx, midy);
        setImageMatrix(mMatrix);
        resetPostion();
    }

    private void checkScale() {
        RectF cropDrawRect = mHighlightView.mCropRect;
        if (cropDrawRect.width() > mDrawRect.width()
                || cropDrawRect.height() > mDrawRect.height()) {
            float midx = mDrawRect.right - mDrawRect.width() / 2;
            float midy = mDrawRect.bottom - mDrawRect.height() / 2;
            float scale = 1.1f;
            if (mDrawRect.width() > mDrawRect.height()) {
                float scaleTemp = cropDrawRect.height() / mDrawRect.height();
                if (scaleTemp < scale) {
                    scale = scaleTemp;
                }

            } else {

                float scaleTemp = cropDrawRect.width() / mDrawRect.width();
                if (scaleTemp < scale) {
                    scale = scaleTemp;
                }
            }
            mMatrix.postScale(scale, scale, midx, midy);
            setScale(scale);
            scalePostion(scale, midx, midy);
            setImageMatrix(mMatrix);
            center();
            mHandler.sendEmptyMessageDelayed(0, 10);
        }
    }

    public void center() {
        if (mHighlightView != null) {
            RectF cropRect = mHighlightView.mCropRect;
            float dx = cropRect.right - cropRect.width() / 2 - mDrawRect.right
                    + mDrawRect.width() / 2;
            float dy = cropRect.bottom - cropRect.height() / 2
                    - mDrawRect.bottom + mDrawRect.height() / 2;
            if (dx != 0 && dy != 0) {
                mMatrix.postTranslate(dx, dy);
                translatePostion(dx, dy);
                setImageMatrix(mMatrix);
            }
        }
    }

    public void resetPostion() {
        if (mHighlightView != null
                && !mHighlightView.mCropRect.contains(mDrawRect)) {
            RectF cropRect = mHighlightView.mCropRect;
            float dx = 0;
            float dy = 0;
            if (mDrawRect.right < cropRect.right) {
                dx = cropRect.right - mDrawRect.right;

            }
            if (mDrawRect.bottom < cropRect.bottom) {
                dy = cropRect.bottom - mDrawRect.bottom;

            }
            if (mDrawRect.left > cropRect.left) {
                dx = cropRect.left - mDrawRect.left;

            }
            if (mDrawRect.top > cropRect.top) {
                dy = cropRect.top - mDrawRect.top;

            }
            mMatrix.set(getImageMatrix());
            mMatrix.postTranslate(dx, dy);
            translatePostion(dx, dy);
            if (mDrawRect.width() < cropRect.width()) {
                dx = cropRect.right / 2 - mDrawRect.right / 2;
                if (dx == 0) {
                    dx = cropRect.right - cropRect.width() / 2
                            - (mDrawRect.right - mDrawRect.width() / 2);
                }
                mMatrix.postTranslate(dx, 0);
                translatePostion(dx, 0);
            }
            if (mDrawRect.height() < cropRect.height()) {
                dy = cropRect.bottom / 2 - mDrawRect.bottom / 2;
                if (dy == 0) {
                    dy = cropRect.bottom - cropRect.height() / 2
                            - (mDrawRect.bottom - mDrawRect.height() / 2);
                }
                mMatrix.postTranslate(0, dy);
                translatePostion(0, dy);
            }

        }
        setImageMatrix(mMatrix);

    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        super.setImageBitmap(bitmap);
        initPostion();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mBitmap != null) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mMode = MODE_DRAG;
                    mCurrentMatrix.set(getImageMatrix());
                    mStartPoint.set(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mMode == MODE_DRAG) {
                        float dx = event.getX() - mStartPoint.x;
                        float dy = event.getY() - mStartPoint.y;
                        mStartPoint.set(event.getX(), event.getY());
                        mCurrentMatrix.set(getImageMatrix());
                        mMatrix.set(mCurrentMatrix);
                        mMatrix.postTranslate(dx, dy);
                        translatePostion(dx, dy);
                    } else if (mMode == MODE_ZOOM) {
                        float endDis = distance(event);
                        float scale = endDis / mStartDis;
                        if (!(mScale > MAX_SCALE && scale > 1)
                                && !(mScale < MIN_SCALE && scale < 1)) {
                            mStartPoint.set(event.getX(), event.getY());
                            mCurrentMatrix.set(getImageMatrix());
                            mMatrix.set(mCurrentMatrix);
                            mMatrix.postScale(scale, scale, mMidPoint.x,
                                    mMidPoint.y);
                            setScale(scale);
                            scalePostion(scale, mMidPoint.x, mMidPoint.y);
                            mStartDis = distance(event);
                        }
                        PointF midPoint = mid(event);
                        float dx = midPoint.x - mMidPoint.x;
                        float dy = midPoint.y - mMidPoint.y;
                        mMatrix.postTranslate(dx, dy);
                        translatePostion(dx, dy);

                        mMidPoint = mid(event);
                        mCurrentMatrix.set(getImageMatrix());
                    }

                    break;

                case MotionEvent.ACTION_UP:
                    mMode = MODE_NONE;
                    resetPostion();
                    checkScale();
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    mMode = MODE_NONE;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    mMode = MODE_ZOOM;
                    mStartDis = distance(event);
                    mMidPoint = mid(event);
                    mCurrentMatrix.set(getImageMatrix());

                    break;
            }
            setImageMatrix(mMatrix);
        }
        return true;
    }

    private float distance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private PointF mid(MotionEvent event) {
        float midX = (event.getX(1) + event.getX(0)) / 2;
        float midY = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midX, midY);
    }

    private void setScale(float scale) {
        mScale *= scale;
    }

    public void release() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}

package com.j13.zed.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.j13.zed.R;

/**
 * 支持动画显示、自动消失
 */
public class ToastTextView extends LinearLayout {

    private int mInAnim = R.anim.pull_up;
    private int mOutAnim = R.anim.push_down;

    public static final int TEXT_GRAVITY_LEFT = 1;
    public static final int TEXT_GRAVITY_CENTER = 2;
    public static final int TEXT_GRAVITY_RIGHT = 3;

    public static final int ICON_TYPE_NONE = 1;
    public static final int ICON_TYPE_CLOSE = 2;
    public static final int ICON_TYPE_ARROW = 3;

    private TextView mTextView;
    private ImageView mIcon;

    public ToastTextView(Context context) {
        this(context, null);
    }

    public ToastTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToastTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater.from(context).inflate(R.layout.layout_toast_text, this);
        setGravity(Gravity.CENTER_VERTICAL);
        setClickable(true);
        mTextView = (TextView) findViewById(R.id.text);
        mIcon = (ImageView) findViewById(R.id.icon);

        int textGravity = TEXT_GRAVITY_LEFT;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ToastTextView);
        for (int i = 0; i < a.getIndexCount(); i++) {
            switch (a.getIndex(i)) {
                case R.styleable.ToastTextView_textColor:
                    mTextView.setTextColor(a.getColor(R.styleable.ToastTextView_textColor, 0));
                    break;
                case R.styleable.ToastTextView_textSize:
                    mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            a.getDimension(R.styleable.ToastTextView_textSize, 0));
                    break;
                case R.styleable.ToastTextView_textGravity:
                    textGravity = a.getInt(R.styleable.ToastTextView_textGravity, TEXT_GRAVITY_LEFT);
                    break;
                case R.styleable.ToastTextView_backGround:
                    setBackgroundDrawable(a.getDrawable(R.styleable.ToastTextView_backGround));
                    break;
                case R.styleable.ToastTextView_iconType:
                    int iconType = a.getInt(R.styleable.ToastTextView_iconType, ICON_TYPE_NONE);
                    setIconType(iconType);
                    break;
            }
        }
        a.recycle();

        switch (textGravity) {
            case TEXT_GRAVITY_CENTER:
                mTextView.setGravity(Gravity.CENTER);
                break;
            case TEXT_GRAVITY_RIGHT:
                mTextView.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
                break;
            case TEXT_GRAVITY_LEFT:
            default:
                mTextView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                break;
        }

        setClickable(true);
    }

    public void setAnimation(int inAnim, int outAnim) {
        mInAnim = inAnim;
        mOutAnim = outAnim;
    }

    public void setIconType(int iconType) {
        switch (iconType) {
            case ICON_TYPE_NONE:
                mIcon.setVisibility(View.GONE);
                mIcon.setOnClickListener(null);
                break;
            case ICON_TYPE_CLOSE:
                mIcon.setVisibility(View.VISIBLE);
                mIcon.setImageResource(R.drawable.ic_toast_close);
                mIcon.setDuplicateParentStateEnabled(false);
                mIcon.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                break;
            case ICON_TYPE_ARROW:
                mIcon.setVisibility(View.VISIBLE);
                mIcon.setImageResource(R.drawable.ic_toast_arrow);
                mIcon.setDuplicateParentStateEnabled(true);
                mIcon.setOnClickListener(null);
                break;
        }
    }

    public void show(String text) {
        show(text, false, 0);
    }

    public void show(final String text, final boolean autoDismiss, final long dismissDelay) {
        cancelAnimation();
        mTextView.setText(text);
        setVisibility(View.VISIBLE);

        if (mInAnim > 0) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), mInAnim);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (autoDismiss) {
                        postDismiss(dismissDelay);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            startAnimation(animation);
        } else {
            if (autoDismiss) {
                postDismiss(dismissDelay);
            }
        }
    }

    public void dismiss() {
        cancelAnimation();
        if (getVisibility() == View.GONE) {
            return;
        }

        if (mOutAnim > 0) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), mOutAnim);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            startAnimation(animation);
        } else {
            setVisibility(View.GONE);
        }
    }

    private void cancelAnimation() {
        Animation animation = getAnimation();
        if (animation != null) {
            animation.setAnimationListener(null);
            animation.cancel();
        }
        clearAnimation();
    }

    private void postDismiss(long delayMillis) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, delayMillis);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAnimation();
    }

}

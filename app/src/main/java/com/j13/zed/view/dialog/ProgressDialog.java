package com.j13.zed.view.dialog;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.j13.zed.R;

import java.text.NumberFormat;

/**
 * <p>A dialog showing a progress indicator and an optional text message or view.
 * Only a text message or a view can be used at the same time.</p>
 * <p>The dialog can be made cancelable on back key press.</p>
 * <p>The progress range is 0..10000.</p>
 */
public class ProgressDialog extends AlertDialog {

    /**
     * Creates a ProgressDialog with a circular, spinning progress
     * bar. This is the default.
     */
    public static final int STYLE_SPINNER = 0;

    /**
     * Creates a ProgressDialog with a horizontal progress bar.
     */
    public static final int STYLE_HORIZONTAL = 1;

    private ProgressBar mProgress;
    private TextView mMessageView;

    private int mProgressStyle = STYLE_SPINNER;
    private String mProgressNumberFormat;
    private NumberFormat mProgressPercentFormat;

    private int mMax;
    private int mProgressVal;
    private int mSecondaryProgressVal;
    private int mIncrementBy;
    private int mIncrementSecondaryBy;
    private Drawable mProgressDrawable;
    private Drawable mIndeterminateDrawable;
    private CharSequence mMessage;
    private boolean mIndeterminate;

    private boolean mHasStarted;
    private Handler mViewUpdateHandler;

    /**
     * Create a progress dialog that uses the default dialog frame style.
     *
     * @param context The Context the Dialog is to run it.  In particular, it
     *                uses the window manager and theme in this context to
     *                present its UI.
     */
    public ProgressDialog(Context context) {
        super(context);
        initFormats();
    }

    /**
     * Create a progress dialog that uses a custom dialog style.
     *
     * @param context The Context in which the progress dialog should run. In particular, it
     *                uses the window manager and theme from this context to
     *                present its UI.
     * @param theme   A style resource describing the theme to use for the
     *                window. See <a href="{@docRoot}guide/topics/resources/available-resources.html#stylesandthemes">Style
     *                and Theme Resources</a> for more information about defining and using
     *                styles.  This theme is applied on top of the current theme in
     *                <var>context</var>.  If 0, the default dialog theme will be used.
     */
    public ProgressDialog(Context context, int theme) {
        super(context, theme);
        initFormats();
    }

    private void initFormats() {
        mProgressNumberFormat = "%1d/%2d";
        mProgressPercentFormat = NumberFormat.getPercentInstance();
        mProgressPercentFormat.setMaximumFractionDigits(0);
    }

    /**
     * Show a progress dialog
     *
     * @param context The Context in which the progress dialog should run
     * @param title   Title show in that dialog
     * @param message Message show in that dialog
     * @return ProgressDialog instance
     */
    public static ProgressDialog show(Context context, CharSequence title,
                                      CharSequence message) {
        return show(context, title, message, false);
    }

    /**
     * Show a progress dialog
     *
     * @param context       The Context in which the progress dialog should run
     * @param title         Title to show in that dialog
     * @param message       Message to show in that dialog
     * @param indeterminate true if the progress is indeterminate
     * @return ProgressDialog instance
     */
    public static ProgressDialog show(Context context, CharSequence title,
                                      CharSequence message, boolean indeterminate) {
        return show(context, title, message, indeterminate, false, null);
    }

    /**
     * Show a progress dialog
     *
     * @param context       The Context in which the progress dialog should run
     * @param title         Title to show in that dialog
     * @param message       Message to show int that dialog
     * @param indeterminate True if the progress is indeterminate
     * @param cancelable    Whether the dialog is cancelable with {@link android.view.KeyEvent}
     * @return
     */
    public static ProgressDialog show(Context context, CharSequence title,
                                      CharSequence message, boolean indeterminate, boolean cancelable) {
        return show(context, title, message, indeterminate, cancelable, null);
    }

    /**
     * Show a progress dialog
     *
     * @param context        The Context in which the progress dialog should run
     * @param title          Title to show in that dialog
     * @param message        Message to show int that dialog
     * @param indeterminate  True if the progress is indeterminate
     * @param cancelable     Whether the dialog is cancelable with {@link android.view.KeyEvent}
     * @param cancelListener Listener to invoked when dialog is canceled
     * @return ProgressDialog instance
     */
    public static ProgressDialog show(Context context, CharSequence title,
                                      CharSequence message, boolean indeterminate,
                                      boolean cancelable, OnCancelListener cancelListener) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIndeterminate(indeterminate);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        dialog.show();
        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        TypedArray a = getContext().obtainStyledAttributes(null,
                R.styleable.AlertDialog,
                android.R.attr.alertDialogStyle, 0);
        if (mProgressStyle == STYLE_HORIZONTAL) {

            /* Use a separate handler to update the text views as they
             * must be updated on the same thread that created them.
             */
            mViewUpdateHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    /* Update the number and percent */
                    int progress = mProgress.getProgress();
                    int max = mProgress.getMax();

                    if (mProgressPercentFormat != null) {
                        double percent = (double) progress / (double) max;
                        int endIndex = 0;
                        SpannableStringBuilder sb = new SpannableStringBuilder();
                        if (!TextUtils.isEmpty(mMessage)) {
                            endIndex = mMessage.length();
                            sb.append(mMessage);
                        }
                        final String progressPercent = mProgressPercentFormat.format(percent);
                        sb.append(progressPercent);
                        sb.setSpan(
                                new ForegroundColorSpan(getContext().getResources().getColor(
                                        R.color.colorAccent)),
                                endIndex, endIndex
                                        + progressPercent.length(),
                                SpannableStringBuilder.SPAN_EXCLUSIVE_INCLUSIVE);
                        ProgressDialog.super.setMessage(sb);
                    }
                }
            };
            View view = inflater.inflate(a.getResourceId(
                    R.styleable.AlertDialog_horizontalProgressLayout,
                    R.layout.alert_dialog_progress), null);
            mProgress = (ProgressBar) view.findViewById(android.R.id.progress);
            setView(view);
        } else {
            View view = inflater.inflate(a.getResourceId(
                    R.styleable.AlertDialog_progressLayout,
                    R.layout.progress_dialog), null);
            mProgress = (ProgressBar) view.findViewById(android.R.id.progress);
            mMessageView = (TextView) view.findViewById(R.id.message);
            setView(view);
        }
        a.recycle();
        if (mMax > 0) {
            setMax(mMax);
        }
        if (mProgressVal > 0) {
            setProgress(mProgressVal);
        }
        if (mSecondaryProgressVal > 0) {
            setSecondaryProgress(mSecondaryProgressVal);
        }
        if (mIncrementBy > 0) {
            incrementProgressBy(mIncrementBy);
        }
        if (mIncrementSecondaryBy > 0) {
            incrementSecondaryProgressBy(mIncrementSecondaryBy);
        }
        if (mProgressDrawable != null) {
            setProgressDrawable(mProgressDrawable);
        }
        if (mIndeterminateDrawable != null) {
            setIndeterminateDrawable(mIndeterminateDrawable);
        }
        if (mMessage != null) {
            setMessage(mMessage);
        }
        setIndeterminate(mIndeterminate);

        onProgressChanged();
        super.onCreate(savedInstanceState);

        setWindowMatchBottom();
    }

    @Override
    public void onStart() {
        super.onStart();
        mHasStarted = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHasStarted = false;
    }

    /**
     * Set progress value
     *
     * @param value progress value
     */
    public void setProgress(int value) {
        if (mHasStarted) {
            mProgress.setProgress(value);
            onProgressChanged();
        } else {
            mProgressVal = value;
        }
    }

    /**
     * Set secondary progress value
     *
     * @param secondaryProgress
     */
    public void setSecondaryProgress(int secondaryProgress) {
        if (mProgress != null) {
            mProgress.setSecondaryProgress(secondaryProgress);
            onProgressChanged();
        } else {
            mSecondaryProgressVal = secondaryProgress;
        }
    }

    /**
     * Get progress value
     *
     * @return progress value
     */
    public int getProgress() {
        if (mProgress != null) {
            return mProgress.getProgress();
        }
        return mProgressVal;
    }

    /**
     * Get secondary progress value
     *
     * @return secondary progress value
     */
    public int getSecondaryProgress() {
        if (mProgress != null) {
            return mProgress.getSecondaryProgress();
        }
        return mSecondaryProgressVal;
    }

    /**
     * Get the upper limit of the progress bar's range
     *
     * @return the upper limit of the progress bar's range
     */
    public int getMax() {
        if (mProgress != null) {
            return mProgress.getMax();
        }
        return mMax;
    }

    /**
     * Set the upper limit of the progress bar's range
     *
     * @param max the upper limit of the progress bar's range
     */
    public void setMax(int max) {
        if (mProgress != null) {
            mProgress.setMax(max);
            onProgressChanged();
        } else {
            mMax = max;
        }
    }

    /**
     * Increase the progress value by {@code diff}
     *
     * @param diff progress value to increase
     */
    public void incrementProgressBy(int diff) {
        if (mProgress != null) {
            mProgress.incrementProgressBy(diff);
            onProgressChanged();
        } else {
            mIncrementBy += diff;
        }
    }

    /**
     * Increase the secondary progress bar value by {@code diff}
     *
     * @param diff progress value to increase
     */
    public void incrementSecondaryProgressBy(int diff) {
        if (mProgress != null) {
            mProgress.incrementSecondaryProgressBy(diff);
            onProgressChanged();
        } else {
            mIncrementSecondaryBy += diff;
        }
    }

    /**
     * Set progress drawable
     *
     * @param d progress drawable
     * @see ProgressBar#setProgressDrawable(Drawable)
     */
    public void setProgressDrawable(Drawable d) {
        if (mProgress != null) {
            mProgress.setProgressDrawable(d);
        } else {
            mProgressDrawable = d;
        }
    }

    /**
     * Set indeterminate drawable
     *
     * @param d indeterminate drawable
     * @see ProgressBar#setIndeterminateDrawable(Drawable)
     */
    public void setIndeterminateDrawable(Drawable d) {
        if (mProgress != null) {
            mProgress.setIndeterminateDrawable(d);
        } else {
            mIndeterminateDrawable = d;
        }
    }

    /**
     * Set whether the progress bar is indeterminate
     *
     * @param indeterminate True if the progress is indeterminate
     * @see ProgressBar#setIndeterminate(boolean)
     */
    public void setIndeterminate(boolean indeterminate) {
        if (mProgress != null) {
            mProgress.setIndeterminate(indeterminate);
        } else {
            mIndeterminate = indeterminate;
        }
    }

    /**
     * Indicate whether this progress bar is in indeterminate mode.
     *
     * @return true if the progress bar is in indeterminate mode
     */
    public boolean isIndeterminate() {
        if (mProgress != null) {
            return mProgress.isIndeterminate();
        }
        return mIndeterminate;
    }

    @Override
    public void setMessage(CharSequence message) {
        if (mProgress != null) {
            if (mProgressStyle == STYLE_HORIZONTAL) {
                super.setMessage(message);
                mMessage = message;
            } else {
                mMessageView.setText(message);
            }
        } else {
            mMessage = message;
        }
    }

    /**
     * Set progress bar style
     *
     * @param style progress bar style
     * @see ProgressDialog#STYLE_SPINNER
     * @see ProgressDialog#STYLE_HORIZONTAL
     */
    public void setProgressStyle(int style) {
        mProgressStyle = style;
    }

    /**
     * Change the format of the small text showing current and maximum units
     * of progress.  The default is "%1d/%2d".
     * Should not be called during the number is progressing.
     *
     * @param format A string passed to {@link String#format String.format()};
     *               use "%1d" for the current number and "%2d" for the maximum.  If null,
     *               nothing will be shown.
     */
    public void setProgressNumberFormat(String format) {
        mProgressNumberFormat = format;
        onProgressChanged();
    }

    /**
     * Change the format of the small text showing the percentage of progress.
     * The default is
     * {@link NumberFormat#getPercentInstance() NumberFormat.getPercentageInstnace().}
     * Should not be called during the number is progressing.
     *
     * @param format An instance of a {@link NumberFormat} to generate the
     *               percentage text.  If null, nothing will be shown.
     */
    public void setProgressPercentFormat(NumberFormat format) {
        mProgressPercentFormat = format;
        onProgressChanged();
    }

    private void onProgressChanged() {
        if (mProgressStyle == STYLE_HORIZONTAL) {
            if (mViewUpdateHandler != null && !mViewUpdateHandler.hasMessages(0)) {
                mViewUpdateHandler.sendEmptyMessage(0);
            }
        }
    }
}

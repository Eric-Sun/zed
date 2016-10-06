package com.j13.zed.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by aaronliu on 15-7-2.
 */
public class ToastManager {

    private static Toast sToast;

    public static void show(Context context, int resId) {
        show(context, context.getString(resId));
    }

    public static void show(final Context context, final String text) {
        final Context appContext = context.getApplicationContext();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (sToast == null) {
                    sToast = Toast.makeText(appContext, text, Toast.LENGTH_SHORT);
                } else {
                    sToast.setText(text);
                    sToast.setDuration(Toast.LENGTH_SHORT);
                }

                sToast.show();
            }
        });
    }

}

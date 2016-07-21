
package com.j13.zed.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.j13.zed.R;

import java.io.File;
import java.util.List;


public class IntentBuilder {

    public static void startViewIntent(Context context, String filePath, String ext) {
        if (TextUtils.isEmpty(ext)) {
            ext = MimeUtils.getFileExt(filePath);
        }
        String type = MimeUtils.guessMimeTypeFromExtension(ext);
        startViewIntent(context, filePath, type, null);
    }

    private static void startViewIntent(Context context, String filePath, String type, Bundle extras) {
        Intent intent = buildViewIntent(filePath, type, extras);

        if (isIntentResolvable(context, intent)) {
            try {
                context.startActivity(intent);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(context,
                R.string.error_open_file_failed_no_app_found,
                Toast.LENGTH_SHORT).show();
    }

    private static Intent buildViewIntent(String filePath, String type, Bundle extras) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(filePath)), type);
        if (extras != null) {
            intent.putExtras(extras);
        }
        return intent;
    }

    /**
     * Query if have activities that can be performed for the given intent
     */
    public static boolean isIntentResolvable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfo =
                packageManager.queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }

    /**
     * Query if have default activity that can be performed for the given intent
     */
    public static boolean hasDefaultResolvable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        ResolveInfo resolveInfo =
                packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo != null && resolveInfo.activityInfo != null
                && !"android".equals(resolveInfo.activityInfo.packageName);
    }

    public static void viewUrl(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if (isIntentResolvable(context, intent)) {
            context.startActivity(intent);
        }
    }
}

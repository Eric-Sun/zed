package com.j13.zed.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtils {

    public static Bitmap roundCornerBitmap(Bitmap bitmap, float roundPx) {
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight()));
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            final Rect src = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            canvas.drawBitmap(bitmap, src, rect, paint);
            return output;
        } catch (Exception e) {
            return bitmap;
        }
    }

    /**
     * support gif jpeg png
     *
     * @param filePath
     * @return
     */
    public static String getImageSuffix(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }

        InputStream is = null;
        try {
            is = new FileInputStream(new File(filePath));
            byte[] buffer = new byte[8];
            int result = is.read(buffer);
            if (result != -1) {
                if (buffer[0] == (byte) 0x47 && buffer[1] == (byte) 0x49 && buffer[2] == (byte) 0x46
                        && (buffer[4] == (byte) 0x37 || buffer[4] == (byte) 0x39)) {
                    return "gif";
                } else if (buffer[0] == (byte) 0xff && buffer[1] == (byte) 0xd8) {
                    return "jpeg";
                } else if (buffer[0] == (byte) 0x89 && buffer[1] == (byte) 0x50) {
                    return "png";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "";
    }

    public static String getImageSuffix(byte[] bytes) {
        if (bytes == null) {
            return "";
        }

        if (bytes[0] == (byte) 0x47 && bytes[1] == (byte) 0x49 && bytes[2] == (byte) 0x46
                && (bytes[4] == (byte) 0x37 || bytes[4] == (byte) 0x39)) {
            return "gif";
        } else if (bytes[0] == (byte) 0xff && bytes[1] == (byte) 0xd8) {
            return "jpeg";
        } else if (bytes[0] == (byte) 0x89 && bytes[1] == (byte) 0x50) {
            return "png";
        }

        return "";
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        byte[] result = os.toByteArray();
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}

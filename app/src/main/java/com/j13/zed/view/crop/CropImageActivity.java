package com.j13.zed.view.crop;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.j13.zed.R;
import com.j13.zed.activity.BaseActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Pattern;


/**
 * The activity can crop specific region of interest from an image.
 */
public class CropImageActivity extends BaseActivity {

    public static final String EXTRA_IMAGE_URI = "image_uri";
    public static final String EXTRA_CROP_IMAGE_URI = "crop_image_uri";

    private static final String LOG_TAG = "CropImageActivity";
    private static final String CROP_PREFIX = "crop_image";
    private static final String CROP_SUFFIX = ".jpg";

    private static final int DEFAULT_WIDTH = 480;
    private static final int DEFAULT_HEIGHT = 480;

    private Bitmap mBitmap;

    private Uri mTargetUri;
    private ContentResolver mContentResolver;

    private int mWidth;

    private int mHeight;

    private int mSampleSize = 1;

    boolean mWaitingToPick;

    boolean mSaving;

    private MultiTouchImageView mImageView;

    private HighlightCropView mHighlightView;

    protected HighlightView mCrop;

    private boolean mIsBitmapRotate = false;
    private boolean mLoadBitmap = true;

    @Override
    protected int geContentViewId() {
        return R.layout.activity_crop_image;
    }

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        mImageView = (MultiTouchImageView) this.findViewById(R.id.image_view);
        mHighlightView = (HighlightCropView) this.findViewById(R.id.crop_view);
        mImageView.setHighlightView(mHighlightView);

        Intent intent = getIntent();
        if (savedState != null) {
            mTargetUri = Uri.parse(savedState.getString(EXTRA_CROP_IMAGE_URI));
        } else {
            mTargetUri = intent.getParcelableExtra(EXTRA_IMAGE_URI);
        }

        mContentResolver = getContentResolver();

        if (mBitmap == null) {

            String path = ImagePathUtil.getPath(this, mTargetUri);

            if (!TextUtils.isEmpty(path)) {
                mIsBitmapRotate = isRotateImage(path);
                new BackgroundTask().execute(this);
            } else {
                finish();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mTargetUri != null) {
            outState.putString(EXTRA_CROP_IMAGE_URI, mTargetUri.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_crop_image, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true;
        switch (item.getItemId()) {
            case R.id.action_crop_image:
                onCropPhoto();
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }
        return result;
    }

    private void onCropPhoto() {
        String imgPath = ImagePathUtil.getPath(this, mTargetUri);
        if (TextUtils.isEmpty(imgPath)) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        } else {
            mLoadBitmap = false;
            new BackgroundTask().execute(this);
        }
    }

    private void getBitmapSize() {

        InputStream is = null;

        try {

            is = getInputStream(mTargetUri);

            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(is, null, options);

            mWidth = options.outWidth;

            mHeight = options.outHeight;

        } catch (IOException e) {

            Log.e(LOG_TAG, "CropImageActivity getBitmapSize() : ", e);

        } finally {

            if (is != null) {

                try {

                    is.close();

                } catch (IOException ignored) {

                }

            }

        }

    }

    private void getBitmap() {

        InputStream is = null;

        try {

            try {

                is = getInputStream(mTargetUri);

            } catch (IOException e) {
                Log.e(LOG_TAG, "CropImageActivity getBitmap() : ", e);
            }

            while ((mWidth / mSampleSize > DEFAULT_WIDTH * 2)

                    || (mHeight / mSampleSize > DEFAULT_HEIGHT * 2)) {

                mSampleSize *= 2;

            }

            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inSampleSize = mSampleSize;

            mBitmap = BitmapFactory.decodeStream(is, null, options);

        } finally {

            if (is != null) {

                try {

                    is.close();

                } catch (IOException ignored) {

                }

            }

        }

    }

    private boolean isRotateImage(String path) {
        try {

            ExifInterface exifInterface = new ExifInterface(path);

            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                return true;
            }

        } catch (IOException e) {

            Log.e(LOG_TAG, "CropImageActivity isRotateImage() : ", e);

        }

        return false;

    }

    private InputStream getInputStream(Uri uri) throws IOException {

        try {

            if (uri.getScheme().equals("file")) {

                return new java.io.FileInputStream(uri.getPath());

            } else {

                return mContentResolver.openInputStream(uri);

            }

        } catch (FileNotFoundException ex) {

            return null;

        }

    }

    private void startFaceDetection(final boolean isRotate) {
        if (!isFinishing() && isRotate) {
            initBitmap();
        }
    }

    private void initBitmap() {

        Matrix m = new Matrix();

        m.setRotate(90);

        int width = mBitmap.getWidth();

        int height = mBitmap.getHeight();

        try {
            mBitmap = Bitmap
                    .createBitmap(mBitmap, 0, 0, width, height, m, true);
        } catch (OutOfMemoryError ooe) {

            m.postScale((float) 1 / mSampleSize, (float) 1 / mSampleSize);

            mBitmap = Bitmap
                    .createBitmap(mBitmap, 0, 0, width, height, m, true);
        }

    }

    private void saveDrawableToCache(Bitmap bitmap, String filePath) {
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            file.createNewFile();

            OutputStream outStream = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

            outStream.flush();

            outStream.close();

        } catch (IOException e) {
            Log.e(LOG_TAG, "save file error", e);
        }

    }

    private Rect getCropRect() {
        RectF imageDrawRect = mImageView.mDrawRect;
        RectF cropDrawRect = mHighlightView.mCropRect;
        float scale = mImageView.mScale;
        int left = 0, right = 0, top = 0, bottom = 0;
        if (imageDrawRect.contains(cropDrawRect)) {
            left = (int) ((cropDrawRect.left - imageDrawRect.left) / scale);
            right = (int) ((cropDrawRect.right - imageDrawRect.left) / scale);
            top = (int) ((cropDrawRect.top - imageDrawRect.top) / scale);
            bottom = (int) ((cropDrawRect.top - imageDrawRect.top + cropDrawRect
                    .height()) / scale);
        } else if (cropDrawRect.contains(imageDrawRect)) {
            right = mBitmap.getWidth();
            bottom = mBitmap.getHeight();

        } else {
            if (cropDrawRect.width() > imageDrawRect.width()) {
                left = 0;
                right = mBitmap.getWidth();
            } else {
                left = (int) ((cropDrawRect.left - imageDrawRect.left) / scale);
                right = (int) ((cropDrawRect.right - imageDrawRect.left) / scale);
            }

            if (cropDrawRect.height() > imageDrawRect.height()) {
                top = 0;
                bottom = mBitmap.getHeight();
            } else {
                top = (int) ((cropDrawRect.top - imageDrawRect.top) / scale);
                bottom = (int) ((cropDrawRect.bottom - imageDrawRect.top) / scale);
            }
        }
        return new Rect(left, top, right, bottom);
    }

    class BackgroundTask extends AsyncTask<Context, Void, Void> {
        private String mCropPath;

        @Override
        protected Void doInBackground(Context... params) {
            if (mLoadBitmap) {
                getBitmapSize();
                getBitmap();
                startFaceDetection(mIsBitmapRotate);
            } else {
                final Bitmap croppedImage;

                Rect r = getCropRect();

                int width = r.width();

                int height = r.height();

                croppedImage = Bitmap

                        .createBitmap(width, height, Bitmap.Config.RGB_565);

                Canvas canvas = new Canvas(croppedImage);

                Rect dstRect = new Rect(0, 0, width, height);
                canvas.drawBitmap(mBitmap, r, dstRect, null);

                Context context = params[0];
                File cacheDir = context.getExternalCacheDir();
                deleteCropCache(cacheDir);

                String name = CROP_PREFIX + System.currentTimeMillis() + CROP_SUFFIX;
                mCropPath = new File(cacheDir, name).getPath();

                saveDrawableToCache(croppedImage, mCropPath);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (mLoadBitmap && mBitmap != null) {
                mImageView.setImageBitmap(mBitmap);

            } else {
                Uri cropUri = Uri.fromFile(new File(mCropPath));

                Intent intent = new Intent("inline-data");

                intent.putExtra(EXTRA_CROP_IMAGE_URI, cropUri);

                setResult(RESULT_OK, intent);

                finish();
            }
        }

        private void deleteCropCache(File cacheDir) {
            if (cacheDir == null || !cacheDir.exists() || !cacheDir.isDirectory()) {
                return;
            }

            File[] cache = cacheDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    String rex = "^" + CROP_PREFIX + "\\.*\\" + CROP_SUFFIX + "$";
                    return Pattern.matches(rex, filename);
                }
            });

            for (File file : cache) {
                file.delete();
            }
        }

    }

}

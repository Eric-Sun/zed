package com.j13.zed.helper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ImageView;

import com.j13.zed.R;
import com.j13.zed.util.CircleTransformation;
import com.j13.zed.util.FileIconUtils;
import com.j13.zed.util.FileUtils;
import com.j13.zed.util.MimeUtils;
import com.j13.zed.util.RoundedTransformation;
import com.j13.zed.util.Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.RequestHandler;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.IOException;

public class FileIconHelper {

    private volatile static FileIconHelper fileIconHelperInstance;
    private Context mContext;
    private static final String VOLUME_NAME = "external";
    public static final String PICASSO_TAG = "PICASSO";

    private Picasso mPicassoInstance;

    private FileIconHelper(Context context) {
        mContext = context.getApplicationContext();
        mPicassoInstance = new Picasso.Builder(context.getApplicationContext())
                .addRequestHandler(new IconRequestHandler())
                .build();
    }

    public static FileIconHelper getInstance(Context context) {
        if (fileIconHelperInstance == null) {
            synchronized (FileIconHelper.class) {
                if (fileIconHelperInstance == null)
                    fileIconHelperInstance = new FileIconHelper(context);
            }
        }
        return fileIconHelperInstance;
    }

    public Picasso getPicasso() {
        return mPicassoInstance;
    }

    public void setIcon(String filePath, ImageView imageView, String ext, int defaultIcon) {
        // 支持没有后缀名的文件
        String extension = !TextUtils.isEmpty(ext) ? ext : FileUtils.getFileExt(filePath);
        int id = defaultIcon == 0 ? FileIconUtils.getFileIconId(extension) : defaultIcon;

        String mimeType = MimeUtils.guessMimeTypeFromExtension(extension);
        if (mimeType.startsWith(MimeUtils.MIME_TYPE_IMAGE)) {
            int target = mContext.getResources().getDimensionPixelSize(R.dimen.file_icon_width);
            loadInto(new File(filePath), target, target, id, imageView);
        } else {
            Uri uri = Uri.fromParts(mimeType, filePath, extension);
            mPicassoInstance.load(uri).noFade().tag(PICASSO_TAG).placeholder(id).into(imageView);
        }
    }

    public void setIcon(String filePath, ImageView imageView, int defaultIcon) {
        setIcon(filePath, imageView, null, defaultIcon);
    }

    public void loadInto(File file, int targetWidth, int targetHeight, int defaultIcon, ImageView picIv) {
        RequestCreator creator = getRequestCreator(Uri.fromFile(file), targetWidth, targetHeight, defaultIcon);
        if (targetWidth > 0 && targetHeight > 0) {
            creator.onlyScaleDown();
        }
        creator.centerCrop();
        creator.into(picIv);
    }

    public void loadInto(String url, int targetWidth, int targetHeight, int defaultIcon, ImageView picIv) {
        loadInto(url, targetWidth, targetHeight, defaultIcon, picIv, false);
    }

    public void loadInto(String url, int targetWidth, int targetHeight, int defaultIcon, ImageView picIv, boolean isUseCircle) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        RequestCreator creator = getRequestCreator(Uri.parse(url), targetWidth, targetHeight, defaultIcon);
        if (isUseCircle) {
            creator.transform(new CircleTransformation());
        }
        if (targetWidth > 0 && targetHeight > 0) {
            creator.onlyScaleDown();
            creator.centerCrop();
        }
        creator.into(picIv);
    }

    public void loadInto(File file, int defaultIcon, Target target) {
        RequestCreator creator = getRequestCreator(Uri.fromFile(file), 0, 0, defaultIcon);
        creator.into(target);
    }

    public void loadInto(String url, int defaultIcon, boolean isUseCircle, Target target) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        RequestCreator creator = getRequestCreator(Uri.parse(url), 0, 0, defaultIcon);
        if (isUseCircle) {
            creator.transform(new RoundedTransformation(20, 0));
        }
        creator.into(target);
    }

    public RequestCreator getRequestCreator(Uri uri, int targetWidth, int targetHeight, int defaultIcon) {
        RequestCreator creator = mPicassoInstance
                .load(uri)
                .tag(PICASSO_TAG);
        if (targetWidth > 0 && targetHeight > 0) {
            creator.resize(targetWidth, targetHeight);
        }
        if (defaultIcon > 0) {
            creator.placeholder(defaultIcon);
        }
        creator.config(Bitmap.Config.RGB_565);
        return creator;
    }

    public void pause() {
        mPicassoInstance.pauseTag(PICASSO_TAG);
    }

    public void resume() {
        mPicassoInstance.resumeTag(PICASSO_TAG);
    }

    public class IconRequestHandler extends RequestHandler {

        @Override
        public boolean canHandleRequest(Request data) {
            if (data != null && data.uri != null) {
                String scheme = data.uri.getScheme();
                String extFromFilename = data.uri.getFragment();
                return MimeUtils.guessFileTypeFromMimeType(extFromFilename, scheme) != MimeUtils.MEDIA_TYPE_UNKNOWN;
            } else {
                return false;
            }
        }

        @Override
        public Result load(Request data, int arg1) throws IOException {
            Bitmap bitmap = null;
            try {
                String scheme = data.uri.getScheme();
                String path = data.uri.getSchemeSpecificPart();
                String extra = data.uri.getFragment();

                int fileType = MimeUtils.guessFileTypeFromMimeType(extra, scheme);
                switch (fileType) {
                    case MimeUtils.MEDIA_TYPE_IMAGE:
                        bitmap = getImageThumb(mContext, path);
                        break;
                    case MimeUtils.MEDIA_TYPE_AUDIO:
                        bitmap = getAudioThumb(mContext, path);
                        break;
                    case MimeUtils.MEDIA_TYPE_VIDEO:
                        bitmap = getVideoThumb(path);
                        break;
                    case MimeUtils.MEDIA_TYPE_APK:
                        bitmap = getApkThumb(mContext, path);
                        break;
                    default:
                        break;
                }
            } catch (Exception ignored) {
                //Exception ignored
            }
            return new Result(bitmap, Picasso.LoadedFrom.DISK);
        }

    }


    public static Bitmap getApkThumb(Context context, String path) {
        Bitmap bitmap = null;
        Drawable drawable = Util.getApkIcon(context, path);
        if (drawable != null && drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        }
        return bitmap;
    }

    public static Bitmap getImageThumb(Context context, String path) {
        Bitmap bitmap = null;
        long picId = getDbId(context, path,
                MediaStore.Images.Media.getContentUri(VOLUME_NAME));
        if (picId > 0) {
            bitmap = MediaStore.Images.Thumbnails
                    .getThumbnail(context.getContentResolver(), picId, MediaStore.Images.Thumbnails.MICRO_KIND, null);
        }
        return bitmap;
    }

    private static Bitmap getAnotherMusicThumbnail(String mFilePath) {
//        Bitmap bitmap = null;
//        try {
//            AudioFile f = AudioFileIO.read(new File(mFilePath));
//            if (f == null) {
//                return null;
//            }
//            Tag tag = f.getTag();
//            if (tag == null) {
//                return null;
//            }
//            Artwork artwork = tag.getFirstArtwork();
//            if (artwork != null) {
//                byte[] pic = artwork.getBinaryData();
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inJustDecodeBounds = true;
//
//                BitmapFactory.decodeByteArray(pic, 0, pic.length, options);
//
//                if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {
//                    return null;
//                }
//                options.inSampleSize = computeSampleSize(options, 50, (int) (0.5 * 1024 * 1024));
//                options.inJustDecodeBounds = false;
//                options.inPurgeable = true;     //recyclable
//                options.inDither = false;       //抖动
//                options.inPreferredConfig = Bitmap.Config.RGB_565;
//                bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length, options);
//            }
//        } catch (Exception ignored) {
//            //Exception ignored
//        }
//        return bitmap;
        return null;
    }

    public static Bitmap getImageThumbnail(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {
            return null;
        }

        options.inSampleSize = computeSampleSize(options, 50, (int) (0.5 * 1024 * 1024));
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;     //recyclable
        options.inDither = false;       //抖动
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap getVideoThumb(String path) {
        return ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
    }

    public static Bitmap getAudioThumb(Context context, String path) {
        Bitmap bitmap = getMusicThumbnail(context, path);
        if (bitmap == null) {
            bitmap = getAnotherMusicThumbnail(path);
        }
        return bitmap;
    }

    public static long getDbId(Context context, String path, Uri uri) {
        String selection = MediaStore.Files.FileColumns.DATA + "=?";
        String[] selectionArgs = new String[]{
                path
        };

        String[] columns = new String[]{
                MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA
        };

        Cursor c = context.getContentResolver()
                .query(uri, columns, selection, selectionArgs, null);
        if (c == null) {
            return 0;
        }
        long id = 0;
        try {
            if (c.moveToFirst()) {
                id = c.getLong(0);
            }
        } finally {
            c.close();
        }
        return id;
    }

    private static Bitmap getMusicThumbnail(Context context, String path) {
//        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        String selection = MediaStore.Files.FileColumns.DATA + "=?";
//        String[] selectionArgs = new String[]{
//                path
//        };
//
//        String[] columns = new String[]{
//                MediaStore.Audio.Media._ID,
//                MediaStore.Audio.Media.ALBUM_ID
//        };
//        Cursor c = context.getContentResolver()
//                .query(uri, columns, selection, selectionArgs, null);
//        if (c == null) {
//            return null;
//        }
//
//        Bitmap thumb = null;
//        if (c.moveToFirst()) {
//            thumb = MusicUtils.getArtwork(context, c.getLong(0), c.getLong(1));
//        }
//        c.close();
//
//        return thumb;
        return null;
    }


    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    public static int computeInitialSampleSize(BitmapFactory.Options options,
                                               int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) &&
                (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

}

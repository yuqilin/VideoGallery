package com.github.yuqilin.videogallery;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by yuqilin on 2018/3/12.
 */

public class MediaUtil {

    private static final String TAG = "MediaUtil";

    public static final String ACTION_SCAN_START = StringUtil.buildPkgString("gui.ScanStart");
    public static final String ACTION_SCAN_STOP = StringUtil.buildPkgString("gui.ScanStop");

    public static final String ACTION_DELETE_FILE = StringUtil.buildPkgString("video.delete");
    public static final String EXTRA_DELETE_FILE = "delete_file";

//    public static void actionScanStart() {
//        Intent intent = new Intent();
//        intent.setAction(ACTION_SCAN_START);
//        LocalBroadcastManager.getInstance(MyApplication.getAppContext()).sendBroadcast(intent);
//    }
//
//    public static void actionScanStop() {
//        Intent intent = new Intent();
//        intent.setAction(ACTION_SCAN_STOP);
//        LocalBroadcastManager.getInstance(MyApplication.getAppContext()).sendBroadcast(intent);
//    }
//
//    public static void actionDeleteFile(MediaWrapper media) {
//        Intent intent = new Intent();
//        intent.setAction(ACTION_DELETE_FILE);
//        intent.putExtra(EXTRA_DELETE_FILE, media);
//        LocalBroadcastManager.getInstance(MyApplication.getAppContext()).sendBroadcast(intent);
//    }

    public static String getResolution(MediaWrapper media) {
        if (media.getWidth() > 0 && media.getHeight() > 0)
            return String.format(Locale.US, "%dx%d", media.getWidth(), media.getHeight());
        return "";
    }

//    public static String mediasToJson(List<MediaWrapper> medias) {
//        Gson gson = new Gson();
//        return gson.toJson(medias);
//    }
//
//    public static List<MediaWrapper> mediasFromJson(String json) {
//        List<MediaWrapper> medias = new ArrayList<>();
//        Gson gson = new Gson();
//        try {
//            MediaWrapper[] mediasArray = gson.fromJson(json, MediaWrapper[].class);
//            if (mediasArray != null && mediasArray.length > 0) {
//                medias = new ArrayList<>(Arrays.asList(mediasArray));
//            }
//        } catch (JsonSyntaxException e) {
//            LogUtil.e(TAG, "mediasFromJson json : " + json + ", e : " + e);
//        }
//        return medias;
//    }
//
//    public static String mediaIdsToJson(List<Long> ids) {
//        Gson gson = new Gson();
//        return gson.toJson(ids);
//    }
//
//    public static List<Long> mediaIdsFromJson(String json) {
//        List<Long> ids = new ArrayList<>();
//        Gson gson = new Gson();
//        try {
//            Long[] idsArray = gson.fromJson(json, Long[].class);
//            if (idsArray != null && idsArray.length > 0) {
//                ids = new ArrayList<>(Arrays.asList(idsArray));
//            }
//        } catch (JsonSyntaxException e) {
//            LogUtil.e(TAG, "mediaIdsFromJson json : " + json + ", e : " + e);
//        }
//        return ids;
//    }
//
//    public static boolean deleteMediaFile(MediaWrapper media) {
//        boolean deleted = false;
//        //Delete from Android Medialib, for consistency with device MTP storing and other apps listing content:// media
//        if (AndroidUtil.isHoneycombOrLater){
//            ContentResolver cr = PlayerApplication.getAppContext().getContentResolver();
//            try {
//                deleted = cr.delete(MediaStore.Files.getContentUri("external"),
//                        MediaStore.Files.FileColumns.DATA + "=?", new String[]{media.getFilePath()}) > 0;
//            } catch (IllegalArgumentException ignored) {} // Can happen on some devices...
//        }
//
//        deleted |= FileUtil.deleteFile(media.getFilePath());
//        return deleted;
//    }
//
//    public static void preloadVideoThumbnail(Context context, MediaWrapper media) {
////        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_thumbnail_default);
//        Glide.with(context)
//                .load(media.getThumbnailPath())
////                .apply(requestOptions)
//                .preload();
//    }

//    public static void loadVideoThumbnail(Context context, MediaWrapper media, ImageView targetView) {
//        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_thumbnail_default).dontAnimate();
//        Glide.with(context)
//                .load(media.getThumbnailPath())
//                .apply(requestOptions)
//                .placeholder(R.drawable.ic_thumbnail_default)
//                .dontAnimate()
//                .dontTransform()
//                .into(targetView);
//    }

    public static boolean saveBitmapToFile(Bitmap bitmap, String filePath) {
        if (bitmap == null)
            return false;
        File imageFile = new File(filePath);
        FileOutputStream outputStream = null;
        boolean saved = true;
        try {
            outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        } catch (IOException e) {
            LogUtil.e(TAG, "Failed to create outputStream");
            saved = false;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return saved;
    }

    public static String queryMediaThumbnail(long id) {
        String[] thumbnailColumns = new String[] {
                MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID
        };

        String selection = MediaStore.Video.Thumbnails.VIDEO_ID + "=?";
        String selectionArgs[] = new String[] { String.valueOf(id) };
        Cursor thumbCursor = MyApplication.getAppContext().getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                thumbnailColumns, selection, selectionArgs, null);
        String thumbPath = "";
        if (thumbCursor != null && thumbCursor.moveToFirst()) {
            thumbPath = thumbCursor.getString(thumbCursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
            thumbCursor.close();
        }
        return thumbPath;
    }

    public static Bitmap getMediaThumbnail(long id) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        Bitmap thumbBitmap = MediaStore.Video.Thumbnails.getThumbnail(MyApplication.getAppContext().getContentResolver(),
                id, MediaStore.Video.Thumbnails.MINI_KIND, options);
        return thumbBitmap;
    }

//    public static Bitmap readCoverBitmap(String path, int width) {
//        if (TextUtils.isEmpty(path))
//            return null;
//        if (path.startsWith("file"))
//            path = path.substring(7);
//        Bitmap cover = null;
//        BitmapFactory.Options options = new BitmapFactory.Options();
//
//        /* Get the resolution of the bitmap without allocating the memory */
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(path, options);
//
//        if (options.outWidth > 0 && options.outHeight > 0) {
//            options.inJustDecodeBounds = false;
//            options.inSampleSize = 1;
//
//            // Find the best decoding scale for the bitmap
//            if (width > 0) {
//                while (options.outWidth / options.inSampleSize > width)
//                    options.inSampleSize = options.inSampleSize * 2;
//            }
//
//            // Decode the file (with memory allocation this time)
//            cover = BitmapFactory.decodeFile(path, options);
//            BitmapCache.getInstance().addBitmapToMemCache(path, cover);
//        }
//        return cover;
//    }


    private static final String THUMBS_FOLDER_NAME = "/thumbs";
    private static String thumbFolderPath;

    static {
        File extFilesDir = MyApplication.getAppContext().getExternalFilesDir(null);
        thumbFolderPath = extFilesDir + THUMBS_FOLDER_NAME;
        File folder = new File(thumbFolderPath);
        if (!folder.exists())
            folder.mkdirs();
    }

    private static String buildThumbnailPath(MediaWrapper media) {
        return thumbFolderPath + "/" + media.getId() + ".jpg";
    }

    public static Bitmap retrieveMediaThumbnail(MediaWrapper media) {
        String thumbnailPath = buildThumbnailPath(media);

        // Query Thumbnails table
//        if (TextUtils.isEmpty(media.getThumbnailPath())) {
//            long startTime = System.currentTimeMillis();
//            String thumbnail = MediaUtil.queryMediaThumbnail(media.getId());
//            if (FileUtil.copyFile(thumbnail, thumbnailPath) && FileUtil.invalidFile(thumbnailPath)) {
//                media.setThumbnailPath(thumbnailPath);
//            } else {
//                LogUtil.e(TAG, "copyFile failed: src : " + thumbnail + ", dst : " + thumbnailPath);
//            }
//            LogUtil.d(TAG, " query thumb costtime : " + (System.currentTimeMillis() - startTime) + ", mediaId : " + media.getId() + ", thumbPath : " + media.getThumbnailPath());
//        }

        Bitmap thumbBitmap = null;
        // MediaStore getThumbnail
        if (TextUtils.isEmpty(media.getThumbnailPath())) {
            long startTime = System.currentTimeMillis();
            thumbBitmap = MediaUtil.getMediaThumbnail(media.getId());
            if (MediaUtil.saveBitmapToFile(thumbBitmap, thumbnailPath) && FileUtil.invalidFile(thumbnailPath)) {
                media.setThumbnailPath(thumbnailPath);
            }
            LogUtil.d(TAG, " getThumbnail costtime : " + (System.currentTimeMillis() - startTime) + ", mediaId : " + media.getId() + ", thumbPath : " + media.getThumbnailPath());
        }
        return thumbBitmap;

        // MediaMetadataRetriever getFrameAtTime
//        if (TextUtils.isEmpty(media.getThumbnailPath())) {
//            long startTime = System.currentTimeMillis();
//            Bitmap frameBitmap = retriever.getFrameAtTime(0);
//            if (MediaUtil.saveBitmapToFile(frameBitmap, thumbnailPath) && FileUtil.invalidFile(thumbnailPath)) {
//                media.setThumbnailPath(thumbnailPath);
//            }
//            LogUtil.d(TAG, " retrieve thumb costtime : " + (System.currentTimeMillis() - startTime) + ", mediaId : " + media.getId() + ", thumbPath : " + media.getThumbnailPath());
//        }
    }

    public static Bitmap readThumbnailBitmap(String path, int width) {
        if (path == null)
            return null;
        if (path.startsWith("file"))
            path = path.substring(7);
        Bitmap cover = null;
        BitmapFactory.Options options = new BitmapFactory.Options();

        /* Get the resolution of the bitmap without allocating the memory */
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        if (options.outWidth > 0 && options.outHeight > 0) {
            options.inJustDecodeBounds = false;
            options.inSampleSize = 1;

            // Find the best decoding scale for the bitmap
            if (width > 0) {
                while (options.outWidth / options.inSampleSize > width)
                    options.inSampleSize = options.inSampleSize * 2;
            }

            // Decode the file (with memory allocation this time)
            cover = BitmapFactory.decodeFile(path, options);
            BitmapCache.getInstance().addBitmapToMemCache(path, cover);
        }
        return cover;
    }
}

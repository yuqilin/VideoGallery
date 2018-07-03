package com.github.yuqilin.videogallery;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MediaLibrary {
    private static final String TAG = "MediaLibrary";

    private static final String MEDIASTORE_VIDEO_VIDEOCOLUMNS_WIDTH = "width";
    private static final String MEDIASTORE_VIDEO_VIDEOCOLUMNS_HEIGHT = "height";

    private boolean isStopping = false;
    private boolean isCompleted = false;

    private List<MediaWrapper> videos;

    private Thread scanningThread;
    private List<ScanListener> scanListeners = new ArrayList<>();

    private static MediaLibrary instance;

    private final ExecutorService thumbnailExecutor = Executors.newSingleThreadExecutor();

    private static final String THUMBS_FOLDER_NAME = "/thumbs";
    private String thumbFolderPath;

    public synchronized static MediaLibrary getInstance() {
        if (instance == null)
            instance = new MediaLibrary();
        return instance;
    }

    private MediaLibrary() {
        File extFilesDir = MyApplication.getAppContext().getExternalFilesDir(null);
        thumbFolderPath = extFilesDir + THUMBS_FOLDER_NAME;
        File folder = new File(thumbFolderPath);
        folder.mkdirs();
    }

    public List<MediaWrapper> loadVideosFromMediaStore() {
        final long costTime = System.currentTimeMillis();

        String[] mediaColumns = new String[] {
                MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DISPLAY_NAME,
                MediaStore.Video.VideoColumns.TITLE,
                MediaStore.Video.VideoColumns.MIME_TYPE,
                MediaStore.Video.VideoColumns.DURATION,
                MediaStore.Video.VideoColumns.SIZE,
                MediaStore.Video.VideoColumns.DATE_TAKEN,
                MediaStore.Video.VideoColumns.DATE_MODIFIED,
                MediaStore.Video.VideoColumns.RESOLUTION,
                MEDIASTORE_VIDEO_VIDEOCOLUMNS_WIDTH,
                MEDIASTORE_VIDEO_VIDEOCOLUMNS_HEIGHT
        };

        ArrayList<MediaWrapper> newVideos = new ArrayList<>();

        ContentResolver contentResolver = MyApplication.getAppContext().getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null, null, null);
        if (cursor == null) {
            LogUtil.e(TAG, "ContentResolver query got null cursor!");
            return newVideos;
        }
        LogUtil.d(TAG, "cursor.getCount() = " + cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                final MediaWrapper media = new MediaWrapper();

                try {
                    parseVideo(cursor, media);
                } catch (Exception e) {
                    LogUtil.e(TAG, "loadVideosFromMediaStore parseVideo got null, e:" + e);
                }
//                checkMediaInfo(media);

                retrieveMediaThumbnail(media);

                if (BuildConfig.DEBUG) {
                    MediaWrapper.dumpMediaWrapper(media);
                }

//                if (videos.contains(media)) {
//                    LogUtil.d(TAG, "yscan contains media id : " + media.getId());
//                    continue;
//                }
//                synchronized (pauseLock) {
//                    while (paused) {
//                        try {
//                            LogUtil.d(TAG, "yscan pauseLock wait");
//                            pauseLock.wait();
//                            LogUtil.d(TAG, "yscan pauseLock waked up");
//                        } catch (InterruptedException e) {
//                            LogUtil.e(TAG, "InterruptedException e:" + e);
//                        }
//                    }
//                }                LogUtil.d(TAG, "ylist scan add media id : " + media.getId());

                if (isStopping)
                    break;


                newVideos.add(media);

//                if (!videos.contains(media)) {
//                    videos.add(media);
//                    notifyMediaAdded(media);
//                    thumbnailExecutor.execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            retrieveMediaThumbnail(media);
//                            notifyThumbnailUpdate(media);
//                        }
//                    });
//                }

            } while(cursor.moveToNext());
        }

        cursor.close();

        if (!isStopping) {
            isCompleted = true;

            if (BuildConfig.DEBUG) {
                MyApplication.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MyApplication.getAppContext(), "Scanning cost " + (System.currentTimeMillis() - costTime) + " ms", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

        LogUtil.d(TAG, "ymedia loadvideos costtime " + (System.currentTimeMillis() - costTime));

        return newVideos;
    }

    public List<MediaWrapper> getVideos() {
        return videos;
    }

    public interface ScanListener {
        void onScanStart();
        void onMediaAdded(MediaWrapper video);
        void onMediaUpdate(MediaWrapper media);
        void onThumbnailUpdate(MediaWrapper media);
        void onScanStop(boolean completed);
    }

    public void addScanListener(ScanListener listener) {
        if (!scanListeners.contains(listener)) {
            scanListeners.add(listener);
        }
    }

    public void removeScanListener(ScanListener listener) {
        if (scanListeners.contains(listener)) {
            scanListeners.remove(listener);
        }
    }

    private void notifyScanStart() {
        for (ScanListener listener : scanListeners) {
            listener.onScanStart();
        }
    }

    private void notifyScanStop(boolean completed) {
        for (ScanListener listener : scanListeners) {
            listener.onScanStop(completed);
        }
    }

    private void notifyMediaAdded(MediaWrapper videos) {
        for (ScanListener listener : scanListeners) {
            listener.onMediaAdded(videos);
        }
    }

    private void notifyThumbnailUpdate(MediaWrapper media) {
        for (ScanListener listener : scanListeners) {
            listener.onThumbnailUpdate(media);
        }
    }


    public void startScan() {
        if (scanningThread == null || scanningThread.getState() == Thread.State.TERMINATED) {
            LogUtil.d(TAG, "yscan scanMediaItems ------------ ");
            isStopping = false;
            scanningThread = new Thread(new ScanMediaItemsRunnable());
//            MediaUtil.actionScanStart();
//            scanningThread.setPriority(Process.THREAD_PRIORITY_DEFAULT + Process.THREAD_PRIORITY_LESS_FAVORABLE);
            scanningThread.start();
        }
    }

    public void stopScan() {
        isStopping = true;
    }

    private class ScanMediaItemsRunnable implements Runnable {

        @Override
        public void run() {

            // isCompleted
            // loadVideos
            isStopping = false;
            isCompleted = false;

            notifyScanStart();

            // scan
            List<MediaWrapper> scannedVideos = loadVideosFromMediaStore();

            if (!isStopping) {
                videos = scannedVideos;
            }

            notifyScanStop(isCompleted);

        }
    }

    private void parseVideo(Cursor cursor, MediaWrapper media) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns._ID));
        media.setId(id);
        media.setFilePath(cursorGetString(cursor, MediaStore.Video.VideoColumns.DATA));
        media.setFileSize(StringUtil.parseLong(cursorGetString(cursor, MediaStore.Video.VideoColumns.SIZE)));
        media.setTitle(cursorGetString(cursor, MediaStore.Video.VideoColumns.DISPLAY_NAME));
        media.setMimeType(cursorGetString(cursor, MediaStore.Video.VideoColumns.MIME_TYPE));
        media.setDateTaken(StringUtil.parseLong(cursorGetString(cursor, MediaStore.Video.VideoColumns.DATE_TAKEN)));
        media.setLastModified(StringUtil.parseLong(cursorGetString(cursor, MediaStore.Video.VideoColumns.DATE_MODIFIED)));
        media.setDuration(StringUtil.parseLong(cursorGetString(cursor, MediaStore.Video.VideoColumns.DURATION)));
        String resolution = cursorGetString(cursor, MediaStore.Video.VideoColumns.RESOLUTION);
        int width = (int)StringUtil.parseLong(cursorGetString(cursor, MEDIASTORE_VIDEO_VIDEOCOLUMNS_WIDTH));
        int height = (int)StringUtil.parseLong(cursorGetString(cursor, MEDIASTORE_VIDEO_VIDEOCOLUMNS_HEIGHT));
        LogUtil.d(TAG, "ymedia parseVideo id : " + id
                + ", displayName : " + media.getTitle()
                + ", duration : " + media.getDuration()
                + ", resolution : " + resolution
                + ", width : " + width
                + ", height : " + height);
        if (width > 0 && height > 0) {
            media.setWidth(width);
            media.setHeight(height);
        } else if (!TextUtils.isEmpty(resolution) && resolution.contains("x")) {
            int w = (int)StringUtil.parseLong(resolution.split("x")[0]);;
            int h = (int)StringUtil.parseLong(resolution.split("x")[1]);
            media.setWidth(w);
            media.setHeight(h);
        }
    }

    private String cursorGetString(Cursor cursor, String columnName) {
        String result = null;
        try {
            result = cursor.getString(cursor.getColumnIndexOrThrow(columnName));
        } catch (Exception e) {
            LogUtil.d(TAG, "cursorGetString columnName:" + columnName + ", e:" + e);
        }
        return result;
    }

    private void retrieveMediaThumbnail(MediaWrapper media) {
        String thumbnailPath = buildThumbnailPath(media);
        // Query Thumbnails table
        if (TextUtils.isEmpty(media.getThumbnailPath())) {
            long startTime = System.currentTimeMillis();
            String thumbnail = MediaUtil.queryMediaThumbnail(media.getId());
            if (FileUtil.copyFile(thumbnail, thumbnailPath) && FileUtil.invalidFile(thumbnailPath)) {
                media.setThumbnailPath(thumbnailPath);
            } else {
                LogUtil.e(TAG, "copyFile failed: src : " + thumbnail + ", dst : " + thumbnailPath);
            }
            LogUtil.d(TAG, " query thumb costtime : " + (System.currentTimeMillis() - startTime) + ", mediaId : " + media.getId() + ", thumbPath : " + media.getThumbnailPath());
        }

        // MediaStore getThumbnail
        if (TextUtils.isEmpty(media.getThumbnailPath())) {
            long startTime = System.currentTimeMillis();
            Bitmap thumbBitmap = MediaUtil.getMediaThumbnail(media.getId());
            if (MediaUtil.saveBitmapToFile(thumbBitmap, thumbnailPath) && FileUtil.invalidFile(thumbnailPath)) {
                media.setThumbnailPath(thumbnailPath);
            }
            LogUtil.d(TAG, " getThumbnail costtime : " + (System.currentTimeMillis() - startTime) + ", mediaId : " + media.getId() + ", thumbPath : " + media.getThumbnailPath());
        }

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

    private String buildThumbnailPath(MediaWrapper media) {
        return thumbFolderPath + "/" + media.getId() + ".jpg";
    }

}

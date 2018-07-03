package com.github.yuqilin.videogallery;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yuqilin on 2018/3/12.
 */

public class MediaWrapper implements Parcelable {
    private static final String TAG = "MediaWrapper";

    public static final int ITEM_TYPE_MEDIA = 1;
    public static final int ITEM_TYPE_AD = 2;

    private long id;
    private String filePath;
    private String title;
    private String mimeType;
    private String thumbnailPath;
    private long dateTaken;
    private long lastModified;
    private long fileSize;
    private int width;
    private int height;
    private long duration;
    private long lastPlayTime;
    private int rotation = -1;
//    private boolean favorite;

    public MediaWrapper() {}

    protected MediaWrapper(Parcel in) {
        id = in.readLong();
        filePath = in.readString();
        title = in.readString();
        mimeType = in.readString();
        thumbnailPath = in.readString();
        dateTaken = in.readLong();
        lastModified = in.readLong();
        fileSize = in.readLong();
        width = in.readInt();
        height = in.readInt();
        duration = in.readLong();
        lastPlayTime = in.readLong();
        rotation = in.readInt();
    }

    public static final Creator<MediaWrapper> CREATOR = new Creator<MediaWrapper>() {
        @Override
        public MediaWrapper createFromParcel(Parcel in) {
            return new MediaWrapper(in);
        }

        @Override
        public MediaWrapper[] newArray(int size) {
            return new MediaWrapper[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof MediaWrapper) || ((MediaWrapper) obj).getItemType() != ITEM_TYPE_MEDIA)
            return false;
        return id == ((MediaWrapper) obj).id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public long getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(long dateTaken) {
        this.dateTaken = dateTaken;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getLastPlayTime() {
        return lastPlayTime;
    }

    public void setLastPlayTime(long lastPlayTime) {
        this.lastPlayTime = lastPlayTime;
    }

    public Uri getContentUri() {
        Uri contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        return ContentUris.withAppendedId(contentUri, id);
    }

    public static void dumpMediaWrapper(MediaWrapper media) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        Log.i(TAG, ">>>>>>>>> MediaWrapper : [ "
                + "id : " + media.getId() + ","
                + "filePath : " + media.getFilePath() + ","
                + "title : " + media.getTitle() + ","
                + "mimeType : " + media.getMimeType() + ","
                + "thumbnailPath : " + media.getThumbnailPath() + ","
                + "dateTaken : " + simpleDateFormat.format(new Date(media.getDateTaken())) + ","
                + "lastModified : " + simpleDateFormat.format(new Date(media.getLastModified())) + ","
                + "width : " + media.getWidth() + ","
                + "height : " + media.getHeight() + ","
                + "fileSize : " + media.getFileSize() + ","
                + "duration : " + media.getDuration() + ","
                + "lastPlayTime : " + media.getLastPlayTime() +" "
                + "rotation : " + media.getRotation() + " "
                + "] <<<<<<<<<");
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(filePath);
        dest.writeString(title);
        dest.writeString(mimeType);
        dest.writeString(thumbnailPath);
        dest.writeLong(dateTaken);
        dest.writeLong(lastModified);
        dest.writeLong(fileSize);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeLong(duration);
        dest.writeLong(lastPlayTime);
        dest.writeInt(rotation);
    }

    public int getItemType() {
        return ITEM_TYPE_MEDIA;
    }

//    public boolean isFavorite() {
//        return favorite;
//    }
//
//    public void setFavorite(boolean favorite) {
//        this.favorite = favorite;
//    }
}

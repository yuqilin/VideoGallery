/*
 * *************************************************************************
 *  AsyncImageLoader.java
 * **************************************************************************
 *  Copyright © 2015 VLC authors and VideoLAN
 *  Author: Geoffrey Métais
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *  ***************************************************************************
 */

package com.github.yuqilin.videogallery;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

public class AsyncImageLoader {

    public interface Callbacks {
        Bitmap getImage();
        void updateImage(Bitmap bitmap, View target);
    }

    public final static String TAG = "VLC/AsyncImageLoader";
    private static final Handler sHandler = new Handler(Looper.getMainLooper());

//    public static final Bitmap DEFAULT_COVER_VIDEO = BitmapCache.getFromResource(MyApplication.getAppResources(), R.drawable.ic_no_thumbnail_1610);
//    public static final BitmapDrawable DEFAULT_COVER_VIDEO_DRAWABLE = new BitmapDrawable(VLCApplication.getAppResources(), DEFAULT_COVER_VIDEO);
//    public static final Bitmap DEFAULT_COVER_AUDIO = BitmapCache.getFromResource(VLCApplication.getAppResources(), R.drawable.ic_no_song);
//    public static final BitmapDrawable DEFAULT_COVER_AUDIO_DRAWABLE = new BitmapDrawable(VLCApplication.getAppResources(), DEFAULT_COVER_AUDIO);
//    public static final BitmapDrawable DEFAULT_COVER_ARTIST_DRAWABLE = new BitmapDrawable(VLCApplication.getAppResources(), BitmapCache.getFromResource(VLCApplication.getAppResources(), R.drawable.ic_no_artist));
//    public static final BitmapDrawable DEFAULT_COVER_ALBUM_DRAWABLE = new BitmapDrawable(VLCApplication.getAppResources(), BitmapCache.getFromResource(VLCApplication.getAppResources(), R.drawable.ic_no_album));
//    public static final BitmapDrawable DEFAULT_COVER_AUDIO_PLAYLIST_DRAWABLE = new BitmapDrawable(VLCApplication.getAppResources(), BitmapCache.getFromResource(VLCApplication.getAppResources(), R.drawable.ic_audio_playlist));
//    public static final BitmapDrawable DEFAULT_COVER_AUDIO_FOLDER_DRAWABLE = new BitmapDrawable(VLCApplication.getAppResources(), BitmapCache.getFromResource(VLCApplication.getAppResources(), R.drawable.ic_music_folder));

    public static void loadThumbnail(View v, MediaWrapper item) {
        final Bitmap bitmap = BitmapCache.getInstance().getBitmapFromMemCache(item.getThumbnailPath());
        if (bitmap != null) {
            ImageView iv = (ImageView) v;
            iv.setVisibility(View.VISIBLE);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setImageBitmap(bitmap);
        }
    }

//    public static void loadPicture(View v, MediaWrapper item) {
//        if (v == null || item == null || TextUtils.isEmpty(item.getThumbnailPath()))
//            return;
//        final Bitmap bitmap = BitmapCache.getInstance().getBitmapFromMemCache(item.getThumbnailPath());
//        if (bitmap != null) {
//            updateTargetImage(bitmap, v, DataBindingUtil.findBinding(v));
//            return;
//        }
//        if (item.getItemType() == MediaLibraryItem.TYPE_MEDIA && ((MediaWrapper)item).getType() != MediaWrapper.TYPE_GROUP) {
//            MediaWrapper mw = (MediaWrapper) item;
//            int type = mw.getType();
//            boolean isMedia = type == MediaWrapper.TYPE_AUDIO || type == MediaWrapper.TYPE_VIDEO;
//            Uri uri = mw.getUri();
//            if (!isMedia && !(type == MediaWrapper.TYPE_DIR && "upnp".equals(uri.getScheme())))
//                return;
//            item = mw;
//            if (item.getId() == 0L && (isMedia) && "file".equals(uri.getScheme())) {
//                mw = MyApplication.getMLInstance().getMedia(uri);
//                if (mw != null)
//                    item = mw;
//            }
//        }
//        AsyncImageLoader.LoadImage(new MLItemCoverFetcher(v, item), v);
//    }
//
//    public static void LoadImage(final Callbacks cbs, final View target){
//        MyApplication.runBackground(new Runnable() {
//            @Override
//            public void run() {
//                final Bitmap bitmap = cbs.getImage();
//                cbs.updateImage(bitmap, target);
//            }
//        });
//        new AsyncTask<>()
//    }
//
//    private static class MLItemCoverFetcher extends AsyncImageLoader.CoverFetcher {
//        MediaLibraryItem item;
//        int width;
//
//        MLItemCoverFetcher(View v, MediaLibraryItem item) {
//            super(DataBindingUtil.findBinding(v));
//            this.item = item;
//            width = v.getWidth();
//        }
//
//        @Override
//        public Bitmap getImage() {
//            if (bindChanged)
//                return null;
//            String artworkUrl = item.getArtworkMrl();
//            if (item instanceof MediaGroup)
//                return ImageComposer.composeImage((MediaGroup) item);
//            if (!TextUtils.isEmpty(artworkUrl) && artworkUrl.startsWith("http"))
//                return HttpImageLoader.downloadBitmap(artworkUrl);
//            return AudioUtil.readCoverBitmap(Uri.decode(item.getArtworkMrl()), width);
//        }
//
//        @Override
//        public void updateImage(Bitmap bitmap, View target) {
//            if (!bindChanged)
//                updateTargetImage(bitmap, target, binding);
//        }
//    }
//
//    private static void updateTargetImage(final Bitmap bitmap, final View target, final ViewDataBinding vdb) {
//        if (bitmap == null || bitmap.getWidth() <= 1 || bitmap.getHeight() <= 1)
//            return;
//        if (vdb != null) {
//            vdb.setVariable(BR.cover, new BitmapDrawable(MyApplication.getAppResources(), bitmap));
//            vdb.setVariable(BR.protocol, null);
//        } else {
//            sHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (target instanceof ImageView) {
//                        ImageView iv = (ImageView) target;
//                        iv.setVisibility(View.VISIBLE);
//                        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                        iv.setImageBitmap(bitmap);
//                    } else if (target instanceof TextView) {
//                        ViewCompat.setBackground(target, new BitmapDrawable(VLCApplication.getAppResources(), bitmap));
//                        ((TextView) target).setText(null);
//                    }
//                }
//            });
//        }
//    }
//
//    abstract static class CoverFetcher implements AsyncImageLoader.Callbacks {
//        protected ViewDataBinding binding = null;
//        boolean bindChanged = false;
//        final OnRebindCallback<ViewDataBinding> rebindCallbacks = new OnRebindCallback<ViewDataBinding>() {
//            @Override
//            public boolean onPreBind(ViewDataBinding binding) {
//                bindChanged = true;
//                return super.onPreBind(binding);
//            }
//
//            @Override
//            public void onCanceled(ViewDataBinding binding) {
//                super.onCanceled(binding);
//            }
//
//            @Override
//            public void onBound(ViewDataBinding binding) {
//                super.onBound(binding);
//            }
//        };
//
//        CoverFetcher(ViewDataBinding binding){
//            if (binding != null) {
//                this.binding = binding;
//                this.binding.executePendingBindings();
//                this.binding.addOnRebindCallback(rebindCallbacks);
//            }
//        }
//    }


}

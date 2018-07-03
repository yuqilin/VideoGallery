package com.github.yuqilin.videogallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "VideoListAdapter";

    private static final int UPDATE_THUMB = 1;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private List<MediaWrapper> videos = new ArrayList<>();

    public void updateVideos(final List<MediaWrapper> videos) {
        this.videos = videos;
        handler.post(new Runnable() {
            @Override
            public void run() {
                notifyItemRangeChanged(0, videos.size());
            }
        });
    }

    public void updateThumbnail(MediaWrapper video) {
        final int index = videos.indexOf(video);
        if (index != -1) {
            videos.set(index, video);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notifyItemRangeChanged(index, 1, UPDATE_THUMB);
                }
            });
        } else {
            LogUtil.d(TAG, "updateThumbnail index = " + index);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int layout = R.layout.video_item_grid;

        View itemView = inflater.inflate(layout, parent, false);

        return new VideoItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MediaWrapper media = videos.get(position);
        VideoItemViewHolder viewHolder = (VideoItemViewHolder) holder;


        if (viewHolder.duration != null) {
            viewHolder.duration.setText(StringUtil.millisToString(media.getDuration()));
        }

        if (viewHolder.thumbnail != null) {
            viewHolder.thumbnail.setTag(media);
            new ThumbnailAsyncTask(viewHolder.thumbnail, media).execute();
        }
//        AsyncImageLoader.loadThumbnail(viewHolder.thumbnail, media);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        MediaWrapper media = videos.get(position);
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            for (Object data : payloads) {
                switch ((int) data) {
                    case UPDATE_THUMB:
                        AsyncImageLoader.loadThumbnail(((VideoItemViewHolder)holder).thumbnail, media);
                        break;
                }
            }
        }
    }



        @Override
    public int getItemCount() {
        return videos.size();
    }

    public class VideoItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView thumbnail;
        private TextView title;
        private TextView duration;
        private TextView filesize;

        public VideoItemViewHolder(View itemView) {
            super(itemView);

            thumbnail = (ImageView) itemView.findViewById(R.id.item_thumbnail);
//            title = (TextView) itemView.findViewById(R.id.item_title);
            duration = (TextView) itemView.findViewById(R.id.item_duration);
//            filesize = (TextView) itemView.findViewById(R.id.item_filesize);
        }
    }

    private static class ThumbnailAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private final WeakReference<ImageView> imageView;
        MediaWrapper media;

        public ThumbnailAsyncTask(ImageView imageView, MediaWrapper media) {
            this.imageView = new WeakReference<>(imageView);
            this.media = media;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {

            Bitmap thumbnail = MediaUtil.readThumbnailBitmap(media.getThumbnailPath(), media.getWidth());

            return thumbnail;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView v = imageView.get();
            if (v != null && v.getTag().equals(media)) {
                v.setVisibility(View.VISIBLE);
                v.setScaleType(ImageView.ScaleType.CENTER_CROP);
                v.setImageBitmap(bitmap);
            }
        }
    }
}

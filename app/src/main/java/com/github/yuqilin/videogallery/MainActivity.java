package com.github.yuqilin.videogallery;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public class MainActivity extends Activity implements MediaLibrary.ScanListener {

    private MediaLibrary mediaLibrary;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView videoList;
    private VideoListAdapter videoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoList = (RecyclerView) findViewById(R.id.video_list);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        videoListAdapter = new VideoListAdapter();
        videoList.setLayoutManager(layoutManager);
        videoList.setAdapter(videoListAdapter);

        mediaLibrary = MediaLibrary.getInstance();
        mediaLibrary.addScanListener(this);
        mediaLibrary.startScan();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onScanStart() {

    }

    @Override
    public void onMediaAdded(MediaWrapper media) {

    }

    @Override
    public void onMediaUpdate(MediaWrapper media) {

    }

    @Override
    public void onThumbnailUpdate(MediaWrapper media) {
        videoListAdapter.updateThumbnail(media);
    }

    @Override
    public void onScanStop(boolean completed) {
        if (completed) {
            videoListAdapter.updateVideos(mediaLibrary.getVideos());
        }
    }
}

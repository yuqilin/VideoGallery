package com.github.yuqilin.videogallery;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;

public class MyApplication extends Application {

    private static MyApplication instance;

    private Handler mHandler = new Handler(Looper.getMainLooper());


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Context getAppContext() {
        return instance;
    }

    public static Resources getAppResources() {
        return instance.getResources();
    }

    public static void runOnMainThread(Runnable runnable) {
        instance.mHandler.post(runnable);
    }

}

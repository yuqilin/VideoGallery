package com.github.yuqilin.videogallery;

import android.util.Log;

/**
 * Created by yuqilin on 2018/3/12.
 */

public class LogUtil {
    public static boolean mEnabled = BuildConfig.DEBUG;

    public static void v(String tag, String msg) {
        if (mEnabled) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (mEnabled) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (mEnabled) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (mEnabled) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable e) {
        if (mEnabled) {
            Log.w(tag, msg, e);
        }
    }

    public static void e(String tag, String msg) {
        if (mEnabled) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable e) {
        if (mEnabled) {
            Log.e(tag, msg, e);
        }
    }
}

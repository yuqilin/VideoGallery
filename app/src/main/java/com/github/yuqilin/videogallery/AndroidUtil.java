package com.github.yuqilin.videogallery;

import android.os.Build;
import android.os.Environment;

/**
 * Created by yuqilin on 2018/3/13.
 */

public class AndroidUtil {

    public final static String EXTERNAL_PUBLIC_DIRECTORY = Environment.getExternalStorageDirectory().getPath();


    public static final boolean isNougatOrLater = android.os.Build.VERSION.SDK_INT >= 24;//Build.VERSION_CODES.N;
    public static final boolean isMarshMallowOrLater = isNougatOrLater || android.os.Build.VERSION.SDK_INT >= 23;//Build.VERSION_CODES.M;
    public static final boolean isLolliPopOrLater = isMarshMallowOrLater || android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    public static final boolean isKitKatOrLater = isLolliPopOrLater || android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    public static final boolean isJellyBeanMR2OrLater = isKitKatOrLater || android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    public static final boolean isJellyBeanMR1OrLater = isJellyBeanMR2OrLater || android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    public static final boolean isJellyBeanOrLater = isJellyBeanMR1OrLater || android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    public static final boolean isICSOrLater = isJellyBeanOrLater || android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    public static final boolean isHoneycombMr2OrLater = isICSOrLater || android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2;
    public static final boolean isHoneycombMr1OrLater = isHoneycombMr2OrLater || android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    public static final boolean isHoneycombOrLater = isHoneycombMr1OrLater || android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB;

}

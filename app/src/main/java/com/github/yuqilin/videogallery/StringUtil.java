package com.github.yuqilin.videogallery;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by yuqilin on 2018/3/12.
 */

public class StringUtil {
    private static final String TAG = "StringUtil";

    private static StringBuilder sb = new StringBuilder();
    private static DecimalFormat format = (DecimalFormat) NumberFormat.getInstance(Locale.US);
    static {
        format.applyPattern("00");
    }

    public static String buildPkgString(String string) {
        return BuildConfig.APPLICATION_ID + "." + string;
    }

    public static long parseLong(String value) {
        long result = 0;
        try {
            result = Long.parseLong(value);
        } catch (NumberFormatException e) {
            LogUtil.e(TAG, "parseLong NumberFormatException : " + value);
        }
        return result;
    }

    /**
     * Convert time to a string
     * @param millis e.g.time/length from file
     * @return formated string (hh:)mm:ss
     */
    public static String millisToString(long millis) {
        return millisToString(millis, false);
    }

    /**
     * Convert time to a string
     * @param millis e.g.time/length from file
     * @return formated string "[hh]h[mm]min" / "[mm]min[s]s"
     */
    public static String millisToText(long millis) {
        return millisToString(millis, true);
    }

    public static String millisToString(long millis, boolean text) {
        sb.setLength(0);
        if (millis < 0) {
            millis = -millis;
            sb.append("-");
        }

        if (millis == 0) {
            return text ? "0s" : "0:00";
        } else if (millis < 1000) {
            return text ? "1s" : "0:01";
        }

        millis /= 1000;
        int sec = (int) (millis % 60);
        millis /= 60;
        int min = (int) (millis % 60);
        millis /= 60;
        int hours = (int) millis;

        if (text) {
            if (millis > 0)
                sb.append(hours).append('h').append(format.format(min)).append("min");
            else if (min > 0)
                sb.append(min).append("min");
            else
                sb.append(sec).append("s");
        } else {
            if (millis > 0)
                sb.append(hours).append(':').append(format.format(min)).append(':').append(format.format(sec));
            else
                sb.append(min).append(':').append(format.format(sec));
        }
        return sb.toString();
    }

    public static String readableSize(long size) {
        if(size <= 0) return "0B";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/ Math.log10(1000));
        return new DecimalFormat("#,##0.#").format(size/ Math.pow(1000, digitGroups)) + " " + units[digitGroups];
    }
}

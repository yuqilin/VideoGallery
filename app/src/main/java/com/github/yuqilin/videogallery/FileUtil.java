package com.github.yuqilin.videogallery;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yuqilin on 2018/3/21.
 */

public class FileUtil {
    private static final String TAG = "FileUtil";

    public static String getParent(String path){
        if (path == null || TextUtils.equals("/", path))
            return path;
        String parentPath = path;
        if (parentPath.endsWith("/"))
            parentPath = parentPath.substring(0, parentPath.length()-1);
        int index = parentPath.lastIndexOf('/');
        if (index > 0){
            parentPath = parentPath.substring(0, index);
        } else if (index == 0)
            parentPath = "/";
        return parentPath;
    }

    public static String getFileNameFromPath(String path){
        if (path == null)
            return "";
        int index = path.lastIndexOf('/');
        if (index == path.length()-1) {
            path = path.substring(0, index);
            index = path.lastIndexOf('/');
        }
        if (index > -1)
            return path.substring(index+1);
        else
            return path;
    }

    public static String getFolderName(String folderPath) {
        String folderName = "";
        if (folderPath.endsWith("/"))
            folderPath = folderPath.substring(0, folderPath.length()-1);
        int index = folderPath.lastIndexOf('/');
        if (index > 0) {
            folderName = folderPath.substring(index + 1);
        }
        return folderName;
    }

    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.delete();
    }

    public static boolean createFolderPath(String folderPath) {
        File folder = new File(folderPath);
        return folder.mkdirs();
    }

    public static boolean exists(String filePath) {
        return new File(filePath).exists();
    }

    public static boolean copyFile(String src, String dst) {
        if (TextUtils.isEmpty(src) || TextUtils.isEmpty(dst)) {
            return false;
        }
        File srcFile = new File(src);
        File dstFile = new File(dst);
        if (!srcFile.exists())
            return false;
        return copyFile(srcFile, dstFile);
    }

    public static boolean copyFile(File src, File dst) {
        boolean ret = true;
        if (src.isDirectory()) {
            File[] filesList = src.listFiles();
            dst.mkdirs();
            for (File file : filesList)
                ret &= copyFile(file, new File(dst, file.getName()));
        } else if (src.isFile()) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = new BufferedInputStream(new FileInputStream(src));
                out = new BufferedOutputStream(new FileOutputStream(dst));

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                return true;
            } catch (FileNotFoundException e) {
                LogUtil.e(TAG, "copyFile: FileNotFoundException : " + e);
            } catch (IOException e) {
                LogUtil.e(TAG, "copyFile: IOException : " + e);
            } finally {
                FileUtil.close(in);
                FileUtil.close(out);
            }
            return false;
        } else {
            return false;
        }
        return ret;
    }

    public static boolean close(Closeable closeable) {
        if (closeable != null)
            try {
                closeable.close();
                return true;
            } catch (IOException e) {}
        return false;
    }

    public static boolean invalidFile(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.length() > 0) {
            return true;
        }
        return false;
    }
}

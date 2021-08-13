package com.rongcloud.st.beauty.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sensetime on 16-11-16.
 */

public class STFileUtils {
    public static List<String> copyFilterModelFiles(Context context, String index) {
        String files[] = null;
        ArrayList<String> modelFiles = new ArrayList<String>();

        try {
            files = context.getAssets().list(index);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + index;

            File folder = new File(folderpath);

            if (!folder.exists()) {
                folder.mkdir();
            }
        }
        for (int i = 0; i < files.length; i++) {
            String str = files[i];
            if (str.indexOf(".model") != -1) {
                copyFileIfNeed(context, str, index);
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        if (subFile == null || subFile.length == 0) {
            return modelFiles;
        }

        for (int i = 0; i < subFile.length; i++) {
            // 判断是否为文件夹
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                // 判断是否为model结尾
                if (filename.trim().toLowerCase().endsWith(".model") && filename.indexOf("filter") != -1) {
                    modelFiles.add(filename);
                }
            }
        }

        return modelFiles;
    }

    public static String getFilePath(Context context, String fileName) {
        String path = null;
        File dataDir = context.getApplicationContext().getExternalFilesDir(null);
        if (dataDir != null) {
            path = dataDir.getAbsolutePath() + File.separator + fileName;
        }

        return path;
    }

    public static boolean copyFileIfNeed(Context context, String fileName, String className) {
        String path = getFilePath(context, className + File.separator + fileName);
        if (path != null) {
            File file = new File(path);
            if (!file.exists()) {
                //如果模型文件不存在
                try {
                    if (file.exists()) {
                        file.delete();
                    }

                    file.createNewFile();
                    InputStream in = context.getAssets().open(className + File.separator + fileName);
                    if (in == null) {
                        Log.e("copyMode", "the src is not existed");
                        return false;
                    }

                    OutputStream out = new FileOutputStream(file);
                    byte[] buffer = new byte[4096];
                    int n;
                    while ((n = in.read(buffer)) > 0) {
                        out.write(buffer, 0, n);
                    }
                    in.close();
                    out.close();
                } catch (IOException e) {
                    file.delete();
                    return false;
                }
            }
        }

        return true;
    }
}


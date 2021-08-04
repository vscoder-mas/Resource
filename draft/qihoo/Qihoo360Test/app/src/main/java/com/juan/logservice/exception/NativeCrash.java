package com.juan.logservice.exception;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.qihoo360.test.BuildConfig;

import java.io.File;

public class NativeCrash {
    static {
        try {
            System.loadLibrary("breakpad");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * @param path dump文件的存储文件目录
     * @param cmd  native崩溃运行的指令
     */
    public static native void initNativeCrash(String path, String cmd);

    public static void initNativeCrashHandler(Context c) {
        try {
            String path = Environment.getExternalStorageDirectory() + "/hellobreakpad";
            String cmd = "am startservice -n " + c.getPackageName() + "/com.juan.logservice.exception.CrashUploadService";
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            initNativeCrash(path, cmd);
            if (BuildConfig.DEBUG) {
                Log.d("CrashUploadService", "initNativeCrashHandler path is:" + path);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
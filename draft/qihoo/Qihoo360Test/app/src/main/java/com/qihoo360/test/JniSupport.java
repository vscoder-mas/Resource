package com.qihoo360.test;

import android.content.Context;

/**
 * Created by mashuai-s on 2017/6/2.
 */

public class JniSupport {
    static {
        System.loadLibrary("crash-jni");
    }

    public static native int getCrashId();

    public static native String getBase64();

    public static native String encrypt(Context context, String origin);

    public static native String decrypt(Context context, String cipher);
}
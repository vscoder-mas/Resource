package com.qihoo360.test.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by mashuai-s on 2017/6/2.
 */

public class CrashUploadService extends IntentService {
    private String TAG = CrashUploadService.class.getSimpleName();

    public CrashUploadService() {
        super("CrashUploadService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w(TAG, "-> onCreate().");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.w(TAG, "-> onHandleIntent().");

    }
}
package com.qihoo360.test;

import android.app.Application;
import android.util.Log;


/**
 * Created by mashuai-s on 2017/5/17.
 */

public class TestApplication extends Application {
    private String TAG = TestApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w(TAG, "-> onCreate().");
    }
}
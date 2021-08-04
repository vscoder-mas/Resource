package com.tinysoft.rongcloud.test;

import android.app.Application;

import io.rong.imlib.RongIMClient;

public class MainApplication extends Application {
    private static MainApplication INSTANCE;

    public static MainApplication getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        RongIMClient.init(this, "x18ywvqfx4hec", false);
    }
}

package com.tinysoft.rongrtc.v5;

import android.app.Application;

import cn.rongcloud.rtc.api.RCRTCConfig;
import cn.rongcloud.rtc.api.RCRTCEngine;
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
        // 初始化 SDK，在整个应用程序全局只需要调用一次, 建议在 Application 继承类中调用。
        RongIMClient.init(this, "x18ywvqfx4hec", false);
        // 初始化 RTC SDK，可根据需求更改相应配置
        RCRTCConfig.Builder configBuilder = RCRTCConfig.Builder.create();
        RCRTCEngine.getInstance().init(this, configBuilder.build());
    }
}

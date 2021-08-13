package com.rongcloud.st.beauty;

public abstract class RCRTCBeautyEngine {
    public RCRTCBeautyEngine() {
    }

    public static RCRTCBeautyEngine getInstance() {
        return RCRTCBeautyEngineImpl.InstanceHolder.engine;
    }

    public abstract boolean setBeautyEnable(boolean enable);

    public abstract boolean setBeautyOption(boolean var1, RCRTCBeautyOption var2);

    public abstract boolean setBeautyFilter(RCRTCBeautyFilter var1);

    public abstract String getVersion();

    public abstract RCRTCBeautyOption getCurrentBeautyOption();

    public abstract RCRTCBeautyFilter getCurrentFilter();

    public abstract void reset();
}

package com.rongcloud.st.beauty;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class RCRTCBeautyEngine {
    protected float mCurrentFilterStrength = -1f;
    protected float mFilterStrength = 0.80f;

    public RCRTCBeautyEngine() {
    }

    public static RCRTCBeautyEngine getInstance() {
        return RCRTCBeautyEngineImpl.InstanceHolder.engine;
    }

    public abstract boolean setBeautyEnable(boolean enable);

    public abstract AtomicBoolean getEnableBeauty();

    public abstract boolean setBeautyOption(RCRTCBeautyOption option);

    public abstract boolean setBeautyFilter(RCRTCBeautyFilter filter);

    public abstract String getVersion();

    public abstract RCRTCBeautyOption getCurrentBeautyOption();

    public abstract RCRTCBeautyFilter getCurrentFilter();

    public abstract void reset();
}

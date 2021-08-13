package com.rongcloud.st.beauty;

import android.content.Context;

import java.util.concurrent.atomic.AtomicBoolean;

public class RCRTCBeautyEngineImpl extends RCRTCBeautyEngine implements BeautyPlugin {
    private static final String TAG = RCRTCBeautyEngineImpl.class.getSimpleName();
    private Context context;
    private volatile boolean initFlag = false;
    private RCRTCBeautyOption currBeautyOption = new RCRTCBeautyOption(0, 0, 0);
    private RCRTCBeautyFilter currBeautyFilter;
    private AtomicBoolean enableBeauty;

    public RCRTCBeautyEngineImpl() {
        currBeautyFilter = RCRTCBeautyFilter.NONE;
        enableBeauty = new AtomicBoolean(false);
    }

    @Override
    public void init(Context context) {
        currBeautyOption = new RCRTCBeautyOption(0, 0, 0);
        currBeautyFilter = RCRTCBeautyFilter.NONE;
        this.context = context;
        initFlag = true;
    }

    @Override
    public void start() {

    }

    @Override
    public int processFrame(int textureId) {
        return 0;
    }

    @Override
    public void stop() {

    }

    @Override
    public void unInit() {
        //todo://reset 商汤engine
        reset();
        initFlag = false;
    }

    @Override
    public boolean setBeautyEnable(boolean enable) {
        enableBeauty.set(enable);
        return enableBeauty.get();
    }

    @Override
    public boolean setBeautyOption(boolean var1, RCRTCBeautyOption var2) {
        return false;
    }

    @Override
    public boolean setBeautyFilter(RCRTCBeautyFilter var1) {
        return false;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public RCRTCBeautyOption getCurrentBeautyOption() {
        return null;
    }

    @Override
    public RCRTCBeautyFilter getCurrentFilter() {
        return null;
    }

    @Override
    public void reset() {
        if (this.initFlag) {
            currBeautyOption.setRuddyLevel(0);
            currBeautyOption.setSmoothLevel(0);
            currBeautyOption.setWhitenessLevel(0);
            currBeautyFilter = RCRTCBeautyFilter.NONE;
            enableBeauty.set(false);
        }
    }

    static class InstanceHolder {
        static RCRTCBeautyEngine engine = new RCRTCBeautyEngineImpl();

        InstanceHolder() {
        }
    }
}

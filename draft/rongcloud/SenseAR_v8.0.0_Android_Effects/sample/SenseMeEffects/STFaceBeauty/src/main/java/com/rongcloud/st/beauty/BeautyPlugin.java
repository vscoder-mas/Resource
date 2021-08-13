package com.rongcloud.st.beauty;

import android.content.Context;

public interface BeautyPlugin {
    void init(Context context);

    void start();

    //    void processFrame(RCRTCVideoFrame var1, BeautyPlugin.FrameType var2);
    int processFrame(int textureId);

    void stop();

    void unInit();

    public static enum FrameType {
        RGB,
        OES,
        TEXTURE_2D,
        NV21,
        I420;

        private FrameType() {
        }
    }
}

package com.rongcloud.st.beauty;

import android.content.Context;

public interface BeautyPlugin {
    void init(Context context, int cameraId, int orientation, int width, int height);

    void start();

    //    void processFrame(RCRTCVideoFrame var1, BeautyPlugin.FrameType var2);
    int processFrame(byte[] ori);

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

package com.rongcloud.st.beauty;

import android.content.Context;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.util.Log;

import com.rongcloud.st.beauty.utils.STAccelerometer;
import com.rongcloud.st.beauty.utils.STFileUtils;
import com.rongcloud.st.beauty.utils.STGlUtil;
import com.sensetime.stmobile.STCommon;
import com.sensetime.stmobile.STEffectBeautyType;
import com.sensetime.stmobile.STHumanActionParamsType;
import com.sensetime.stmobile.STMobileColorConvertNative;
import com.sensetime.stmobile.STMobileEffectNative;
import com.sensetime.stmobile.STMobileHumanActionNative;
import com.sensetime.stmobile.STRotateType;
import com.sensetime.stmobile.model.STEffectRenderInParam;
import com.sensetime.stmobile.model.STEffectRenderOutParam;
import com.sensetime.stmobile.model.STEffectTexture;
import com.sensetime.stmobile.model.STHumanAction;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class RCRTCBeautyEngineImpl extends RCRTCBeautyEngine implements BeautyPlugin {
    private static final String TAG = RCRTCBeautyEngineImpl.class.getSimpleName();
    private Context context;
    private int mCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private int mWidth = 0;
    private int mHeight = 0;
    private int mOrientation = 0;
    private volatile boolean initFlag = false;
    private final Object mHumanActionHandleLock = new Object();
    private final Object mHumanActionLock = new Object();
    private RCRTCBeautyOption currBeautyOption = new RCRTCBeautyOption(0, 0, 0);
    private RCRTCBeautyFilter currBeautyFilter;
    private AtomicBoolean enableBeauty;

    private CountDownLatch initCDLatch;
    private CountDownLatch processCDLatch;
    private boolean mIsCreateHumanActionHandleSucceeded = false;
    private ExecutorService mDetectThreadPool = Executors.newFixedThreadPool(1);
    private int mHumanActionCreateConfig = STMobileHumanActionNative.ST_MOBILE_HUMAN_ACTION_DEFAULT_CONFIG_VIDEO;
    private STMobileHumanActionNative mSTHumanActionNative;
    private STMobileEffectNative mSTMobileEffectNative;
    private STMobileColorConvertNative mSTMobileColorConvertNative;
    private long mDetectConfig = 0;
    private STAccelerometer accelerometer;

    private int[] mBeautifyTextureId;
    private int[] mCameraInputTexture;
    private int mCameraInputTextureIndex = 0;

    public RCRTCBeautyEngineImpl() {
        currBeautyFilter = RCRTCBeautyFilter.NONE;
        enableBeauty = new AtomicBoolean(false);
        mSTHumanActionNative = new STMobileHumanActionNative();
        mSTMobileEffectNative = new STMobileEffectNative();
        mSTMobileColorConvertNative = new STMobileColorConvertNative();
        accelerometer = new STAccelerometer(context);
    }

    @Override
    public void init(Context context, int cameraId, int orientation, int width, int height) {
        mCameraID = cameraId;
        mOrientation = orientation;
        mWidth = width;
        mHeight = height;
        currBeautyOption = new RCRTCBeautyOption(0, 0, 0);
        currBeautyFilter = RCRTCBeautyFilter.NONE;
        this.context = context;
        initColorConvert();
        initCDLatch = new CountDownLatch(1);
        accelerometer.start();

        try {
            initHumanAction();
            initCDLatch.await();
            setDefaultParams();
            initFlag = true;
            Log.d(TAG, "- init: finished !");
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, "- init: failed! ex:" + ex);
            initFlag = false;
        }
    }

    protected void initHumanAction() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (mHumanActionHandleLock) {
                        long startLoadHumanActionModel = System.currentTimeMillis();
                        //从asset资源文件夹读取model到内存，再使用底层st_mobile_human_action_create_from_buffer接口创建handle
                        int result = mSTHumanActionNative.createInstanceFromAssetFile(STFileUtils.getActionModelName(), mHumanActionCreateConfig, context.getAssets());
                        Log.i(TAG, String.format("the result for createInstance for human_action is %d", result));
                        Log.d(TAG, "load model name: " + STFileUtils.getActionModelName() + " cost time: " +
                                (System.currentTimeMillis() - startLoadHumanActionModel));

                        if (result == 0) {
                            result = mSTHumanActionNative.addSubModelFromAssetFile(STFileUtils.MODEL_NAME_HAND, context.getAssets());
                            Log.i(TAG, String.format("add hand model result: %d", result));
                            result = mSTHumanActionNative.addSubModelFromAssetFile(STFileUtils.MODEL_NAME_SEGMENT, context.getAssets());
                            Log.i(TAG, String.format("add figure segment model result: %d", result));

                            mIsCreateHumanActionHandleSucceeded = true;
                            mSTHumanActionNative.setParam(STHumanActionParamsType.ST_HUMAN_ACTION_PARAM_BACKGROUND_BLUR_STRENGTH, 0.35f);
                            //mSTHumanActionNative.setParam(STHumanActionParamsType.ST_HUMAN_ACTION_PARAM_HEAD_SEGMENT_RESULT_ROTATE, 1.0f);

                            //240
                            result = mSTHumanActionNative.addSubModelFromAssetFile(STFileUtils.MODEL_NAME_FACE_EXTRA, context.getAssets());
                            Log.i(TAG, String.format("add face extra model result: %d", result));

                            //eye
                            result = mSTHumanActionNative.addSubModelFromAssetFile(STFileUtils.MODEL_NAME_EYEBALL_CONTOUR, context.getAssets());
                            Log.i(TAG, String.format("add eyeball contour model result: %d", result));

                            result = mSTHumanActionNative.addSubModelFromAssetFile(STFileUtils.MODEL_NAME_HAIR, context.getAssets());
                            Log.i(TAG, String.format("add hair model result: %d", result));

                            result = mSTHumanActionNative.addSubModelFromAssetFile(STFileUtils.MODEL_NAME_LIPS_PARSING, context.getAssets());
                            Log.i(TAG, String.format("add lips parsing model result: %d", result));

                            result = mSTHumanActionNative.addSubModelFromAssetFile(STFileUtils.HEAD_SEGMENT_MODEL_NAME, context.getAssets());
                            Log.i(TAG, String.format("add head segment model result: %d", result));
                        }

                        Log.d(TAG, "- initHumanAction run: finished !");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.e(TAG, "- initHumanAction run: ex:" + ex);
                } finally {
                    initCDLatch.countDown();
                }
            }
        }).start();
    }

    private void setDefaultParams() {
        int result = mSTMobileEffectNative.createInstance(context, STMobileEffectNative.EFFECT_CONFIG_NONE);
        Log.i(TAG, String.format("- setDefaultParams mSTMobileEffectNative.createInstance result:" + result));
        updateHumanActionDetectConfig();
    }

    private void updateHumanActionDetectConfig() {
        mDetectConfig = mSTMobileEffectNative.getHumanActionDetectConfig();
    }

    @Override
    public void start() {

    }

    @Override
    public int processFrame(byte[] ori) {
        int textureId = -1;
        long mStartRenderTime = System.currentTimeMillis();
        Log.d(TAG, "- processFrame:begin !");
        if (mBeautifyTextureId == null) {
            mBeautifyTextureId = new int[1];
            STGlUtil.initEffectTexture(mWidth, mHeight, mBeautifyTextureId, GLES20.GL_TEXTURE_2D);
        }

        if (mCameraInputTexture == null) {
            mCameraInputTexture = new int[2];
            STGlUtil.initEffectTexture(mWidth, mHeight, mCameraInputTexture, GLES20.GL_TEXTURE_2D);
        }

        Log.d(TAG, "- processFrame: enableBeauty:" + enableBeauty);
        if (enableBeauty != null && enableBeauty.get()) {
            processCDLatch = new CountDownLatch(1);
        } else {
            processCDLatch = new CountDownLatch(2);
        }

        //避免计数器wait block
        boolean isCreateHumanActionHandleSucceeded = mIsCreateHumanActionHandleSucceeded;
        if (enableBeauty != null && enableBeauty.get() && isCreateHumanActionHandleSucceeded) {
            synchronized (mHumanActionLock) {
                mSTHumanActionNative.nativeHumanActionPtrCopy();
            }

            //检测线程
            mDetectThreadPool.submit(new Runnable() {
                @Override
                public void run() {
                    int orientation = getHumanActionOrientation();
                    synchronized (mHumanActionLock) {
                        long startHumanAction = System.currentTimeMillis();
                        int ret = mSTHumanActionNative.nativeHumanActionDetectPtr(ori, STCommon.ST_PIX_FMT_NV21,
                                mDetectConfig, orientation, mHeight, mWidth);
                        //nv21数据为横向，相对于预览方向需要旋转处理，前置摄像头还需要镜像
                        STHumanAction.nativeHumanActionRotateAndMirror(mSTHumanActionNative,
                                mSTHumanActionNative.getNativeHumanActionResultPtr(), mWidth, mHeight,
                                mCameraID, orientation, STAccelerometer.getDirection());
                    }

                    processCDLatch.countDown();
                }
            });
        }

        int cameraOrientation;
        boolean cameraNeedMirror;
        if (mCameraID == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            cameraOrientation = STRotateType.ST_CLOCKWISE_ROTATE_90;
            cameraNeedMirror = true;
        } else {
            cameraOrientation = STRotateType.ST_CLOCKWISE_ROTATE_270;
            cameraNeedMirror = false;
        }

        //上传nv21 buffer到纹理
        int ret = mSTMobileColorConvertNative.nv21BufferToRgbaTexture(mHeight, mWidth,
                cameraOrientation, cameraNeedMirror, ori, mCameraInputTexture[mCameraInputTextureIndex]);
        //双缓冲策略，提升帧率
        mCameraInputTextureIndex = 1 - mCameraInputTextureIndex;
        textureId = mCameraInputTexture[mCameraInputTextureIndex];
        if (!GLES20.glIsTexture(textureId)) {
            Log.e(TAG, "- processFrame: !GLES20.glIsTexture(textureId) failed !");
            return -1;
        }

        if (enableBeauty != null && enableBeauty.get()) {
            //核心渲染接口
            if (mSTMobileEffectNative != null) {
                if (mCurrentFilterStrength != mFilterStrength) {
                    mCurrentFilterStrength = mFilterStrength;
                    mSTMobileEffectNative.setBeautyStrength(STEffectBeautyType.EFFECT_BEAUTY_FILTER, mCurrentFilterStrength);
                }

                //输入纹理
                STEffectTexture stEffectTexture = new STEffectTexture(textureId, mWidth, mHeight, 0);
                //输出纹理，需要在上层初始化
                STEffectTexture stEffectTextureOut = new STEffectTexture(mBeautifyTextureId[0], mWidth, mHeight, 0);
                //输入纹理的人脸方向
                int renderOrientation = getCurrentOrientation();

                //渲染接口输入参数
                STEffectRenderInParam sTEffectRenderInParam = new STEffectRenderInParam(mSTHumanActionNative.getNativeHumanActionPtrCopy(),
                        null, renderOrientation, renderOrientation, false,
                        null, stEffectTexture, null);

                //渲染接口输出参数
                STEffectRenderOutParam stEffectRenderOutParam = new STEffectRenderOutParam(stEffectTextureOut, null, null);
                mSTMobileEffectNative.render(sTEffectRenderInParam, stEffectRenderOutParam, false);

                if (stEffectRenderOutParam != null && stEffectRenderOutParam.getTexture() != null) {
                    textureId = stEffectRenderOutParam.getTexture().getId();
                }
                Log.i(TAG, String.format("- processFrame() render cost time total:%f",
                        System.currentTimeMillis() - mStartRenderTime));

                try {
                    processCDLatch.countDown();
                    if (enableBeauty != null && enableBeauty.get() && isCreateHumanActionHandleSucceeded) {
                        processCDLatch.await();
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "- processFrame failed ! ex:" + ex);
                }
            }
        }

        return textureId;
    }

    @Override
    public void stop() {

    }

    @Override
    public void unInit() {
        //todo://reset 商汤engine
        reset();
        initFlag = false;
        if (accelerometer != null) {
            accelerometer.stop();
        }
    }

    @Override
    public boolean setBeautyEnable(boolean enable) {
        enableBeauty.set(enable);
        return enableBeauty.get();
    }

    @Override
    public AtomicBoolean getEnableBeauty() {
        return enableBeauty;
    }

    @Override
    public boolean setBeautyOption(RCRTCBeautyOption option) {
        return false;
    }

    @Override
    public boolean setBeautyFilter(RCRTCBeautyFilter filter) {
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

    private void initColorConvert() {
        int ret = mSTMobileColorConvertNative.createInstance();
        if (ret == 0) {
            mSTMobileColorConvertNative.setTextureSize(mWidth, mHeight);
        }
    }

    /**
     * 用于humanActionDetect接口。根据传感器方向计算出在不同设备朝向时，人脸在buffer中的朝向
     *
     * @return 人脸在buffer中的朝向
     */
    protected int getHumanActionOrientation() {
        boolean frontCamera = (mCameraID == Camera.CameraInfo.CAMERA_FACING_FRONT);
        //获取重力传感器返回的方向
        int orientation = STAccelerometer.getDirection();
        //在使用后置摄像头，且传感器方向为0或2时，后置摄像头与前置orentation相反
        if (!frontCamera && orientation == STRotateType.ST_CLOCKWISE_ROTATE_0) {
            orientation = STRotateType.ST_CLOCKWISE_ROTATE_180;
        } else if (!frontCamera && orientation == STRotateType.ST_CLOCKWISE_ROTATE_180) {
            orientation = STRotateType.ST_CLOCKWISE_ROTATE_0;
        }

        // 请注意前置摄像头与后置摄像头旋转定义不同 && 不同手机摄像头旋转定义不同
        if (((mOrientation == 270 && (orientation & STRotateType.ST_CLOCKWISE_ROTATE_90) == STRotateType.ST_CLOCKWISE_ROTATE_90) ||
                (mOrientation == 90 && (orientation & STRotateType.ST_CLOCKWISE_ROTATE_90) == STRotateType.ST_CLOCKWISE_ROTATE_0)))
            orientation = (orientation ^ STRotateType.ST_CLOCKWISE_ROTATE_180);
        return orientation;
    }

    protected int getCurrentOrientation() {
        int dir = STAccelerometer.getDirection();
        int orientation = dir - 1;
        if (orientation < 0) {
            orientation = dir ^ 3;
        }

        return orientation;
    }

}

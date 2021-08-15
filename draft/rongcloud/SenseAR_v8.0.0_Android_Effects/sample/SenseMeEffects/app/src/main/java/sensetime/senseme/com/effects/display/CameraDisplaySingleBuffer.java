package sensetime.senseme.com.effects.display;

import android.content.Context;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.rongcloud.st.beauty.RCRTCBeautyEngineImpl;
import com.sensetime.stmobile.STCommon;
import com.sensetime.stmobile.STEffectBeautyType;
import com.sensetime.stmobile.STMobileColorConvertNative;
import com.sensetime.stmobile.STMobileEffectNative;
import com.sensetime.stmobile.STMobileHumanActionNative;
import com.sensetime.stmobile.STRotateType;
import com.sensetime.stmobile.model.STEffectCustomParam;
import com.sensetime.stmobile.model.STEffectRenderInParam;
import com.sensetime.stmobile.model.STEffectRenderOutParam;
import com.sensetime.stmobile.model.STEffectTexture;
import com.sensetime.stmobile.model.STHumanAction;
import com.sensetime.stmobile.model.STQuaternion;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import sensetime.senseme.com.effects.display.glutils.GlUtil;
import sensetime.senseme.com.effects.utils.Accelerometer;
import sensetime.senseme.com.effects.utils.LogUtils;

/**
 * 代替之前的CameraDisplayDoubleInputMultiThread模式
 */
public class CameraDisplaySingleBuffer extends BaseCameraDisplay {

    private String TAG = "CameraDisplaySingleBuffer";
    private Object mHumanActionLock = new Object();

    private ExecutorService mDetectThreadPool = Executors.newFixedThreadPool(1);
    private CountDownLatch mCountDownLatch = new CountDownLatch(2);

    private int[] mCameraInputTexture;
    private int mCameraInputTextureIndex = 0;
    private int mCameraOrientation = STRotateType.ST_CLOCKWISE_ROTATE_90;
    private boolean mCameraNeedMirror = true;

    public CameraDisplaySingleBuffer(Context context, ChangePreviewSizeListener listener, GLSurfaceView glSurfaceView) {
        super(context, listener, glSurfaceView);
        //todo:mascode
        mSTMobileColorConvertNative = beautyEngine.getSTMobileColorConvertNative();
    }

//    private STMobileColorConvertNative mSTMobileColorConvertNative = new STMobileColorConvertNative();
    private STMobileColorConvertNative mSTMobileColorConvertNative;

    private void initColorConvert(){
        int ret = mSTMobileColorConvertNative.createInstance();
        if(ret == 0){
            mSTMobileColorConvertNative.setTextureSize(mImageWidth, mImageHeight);
        }
    }

    /**
     * 工作在opengl线程, 具体渲染的工作函数
     *
     * @param gl
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        // during switch camera
        if (mCameraChanging || mIsChangingPreviewSize || mIsPaused || mSTMobileColorConvertNative == null) {
            return;
        }

        if (mCameraProxy.getCamera() == null) {
            return;
        }

        LogUtils.i(TAG, "- onDrawFrame begin");
        if (mImageData == null) {
            return;
        }

        if(mNv21ImageData == null || mNv21ImageData.length != mImageHeight * mImageWidth *3 /2){
            mNv21ImageData = new byte[mImageHeight * mImageWidth * 3/2];
        }

        synchronized (mImageDataLock) {
            try {
                System.arraycopy(mImageData, 0, mNv21ImageData, 0, mImageData.length);
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                return;
            }
        }

//        if (mBeautifyTextureId == null) {
//            mBeautifyTextureId = new int[1];
//            GlUtil.initEffectTexture(mImageWidth, mImageHeight, mBeautifyTextureId, GLES20.GL_TEXTURE_2D);
//        }
//
////        if (mMakeupTextureId == null) {
////            mMakeupTextureId = new int[1];
////            GlUtil.initEffectTexture(mImageWidth, mImageHeight, mMakeupTextureId, GLES20.GL_TEXTURE_2D);
////        }
////
////        if (mTextureOutId == null) {
////            mTextureOutId = new int[1];
////            GlUtil.initEffectTexture(mImageWidth, mImageHeight, mTextureOutId, GLES20.GL_TEXTURE_2D);
////        }
//
//        if (mCameraInputTexture == null) {
//            mCameraInputTexture = new int[2];
//            GlUtil.initEffectTexture(mImageWidth, mImageHeight, mCameraInputTexture, GLES20.GL_TEXTURE_2D);
//        }
//
////        if (mVideoEncoderTexture == null) {
////            mVideoEncoderTexture = new int[1];
////        }
//
//        Log.d(TAG, "- onDrawFrame: mShowOriginal:" + mShowOriginal);
//        if (mShowOriginal) {
//            mCountDownLatch = new CountDownLatch(1);
//        } else {
//            mCountDownLatch = new CountDownLatch(2);
//        }
//
//        mStartTime = System.currentTimeMillis();
//        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//
//        //避免计数器wait block
//        boolean isCreateHumanActionHandleSucceeded = mIsCreateHumanActionHandleSucceeded;
//        if (!mShowOriginal && isCreateHumanActionHandleSucceeded) {
//            synchronized (mHumanActionLock) {
//                mSTHumanActionNative.nativeHumanActionPtrCopy();
//            }
//
//            //检测线程
//            mDetectThreadPool.submit(new Runnable() {
//                @Override
//                public void run() {
//                    int orientation = getHumanActionOrientation();
//                    synchronized (mHumanActionLock) {
//                        long startHumanAction = System.currentTimeMillis();
//                        int ret = mSTHumanActionNative.nativeHumanActionDetectPtr(mNv21ImageData, STCommon.ST_PIX_FMT_NV21, mDetectConfig, orientation, mImageHeight, mImageWidth);
//                        LogUtils.i(TAG, "human action cost time: %d", System.currentTimeMillis() - startHumanAction);
//
//                        //nv21数据为横向，相对于预览方向需要旋转处理，前置摄像头还需要镜像
////                        mSTHumanAction[1 - mCameraInputTextureIndex] = STHumanAction.humanActionRotateAndMirror(humanAction, mImageWidth, mImageHeight, mCameraID, mCameraProxy.getOrientation(), Accelerometer.getDirection());
//                        STHumanAction.nativeHumanActionRotateAndMirror(mSTHumanActionNative,
//                                mSTHumanActionNative.getNativeHumanActionResultPtr(), mImageWidth, mImageHeight,
//                                mCameraID, mCameraProxy.getOrientation(), Accelerometer.getDirection());
//
////                        if(mNeedAnimalDetect){
////                            animalDetect(mNv21ImageData, STCommon.ST_PIX_FMT_NV21, getHumanActionOrientation(), mImageHeight, mImageWidth, 1 - mCameraInputTextureIndex);
////                        }
//                    }
//
//                    mCountDownLatch.countDown(); // 计数减1
//                }
//            });
//        }
//
//        if (mCameraID == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//            mCameraOrientation = STRotateType.ST_CLOCKWISE_ROTATE_90;
//            mCameraNeedMirror = true;
//        } else {
//            mCameraOrientation = STRotateType.ST_CLOCKWISE_ROTATE_270;
//            mCameraNeedMirror = false;
//        }
//
//        long preProcessCostTime = System.currentTimeMillis();
//        //上传nv21 buffer到纹理
//        int ret = mSTMobileColorConvertNative.nv21BufferToRgbaTexture(mImageHeight, mImageWidth, mCameraOrientation, mCameraNeedMirror, mNv21ImageData, mCameraInputTexture[mCameraInputTextureIndex]);
//        LogUtils.i(TAG, "preprocess cost time: %d", System.currentTimeMillis() - preProcessCostTime);
//
//        //双缓冲策略，提升帧率
//        mCameraInputTextureIndex = 1 - mCameraInputTextureIndex;
//        int textureId = mCameraInputTexture[mCameraInputTextureIndex];
//        if (!GLES20.glIsTexture(textureId)) {
//            Log.e(TAG, "- onDrawFrame: !GLES20.glIsTexture(textureId)");
//            return;
//        }
//
//        if (!mShowOriginal) {
//            //核心渲染接口
//            if (mSTMobileEffectNative != null) {
//                if (mCurrentFilterStrength != mFilterStrength) {
//                    mCurrentFilterStrength = mFilterStrength;
//                    mSTMobileEffectNative.setBeautyStrength(STEffectBeautyType.EFFECT_BEAUTY_FILTER, mCurrentFilterStrength);
//                }
//
//                //输入纹理
//                STEffectTexture stEffectTexture = new STEffectTexture(textureId, mImageWidth, mImageHeight, 0);
//                //输出纹理，需要在上层初始化
//                STEffectTexture stEffectTextureOut = new STEffectTexture(mBeautifyTextureId[0], mImageWidth, mImageHeight, 0);
//                //输入纹理的人脸方向
//                int renderOrientation = getCurrentOrientation();
//
//                //用户自定义参数设置
////                int event = mCustomEvent;
////                STEffectCustomParam customParam;
////                if (mSensorEvent != null && mSensorEvent.values != null && mSensorEvent.values.length > 0) {
////                    customParam = new STEffectCustomParam(new STQuaternion(mSensorEvent.values), mCameraID == Camera.CameraInfo.CAMERA_FACING_FRONT, event);
////                } else {
////                    customParam = new STEffectCustomParam(new STQuaternion(0f, 0f, 0f, 1f), mCameraID == Camera.CameraInfo.CAMERA_FACING_FRONT, event);
////                }
//
//                //渲染接口输入参数
////                STEffectRenderInParam sTEffectRenderInParam = new STEffectRenderInParam(mSTHumanActionNative.getNativeHumanActionPtrCopy(),
////                        mAnimalFaceInfo[mCameraInputTextureIndex], renderOrientation, renderOrientation, false,
////                        customParam, stEffectTexture, null);
//
//                STEffectRenderInParam sTEffectRenderInParam = new STEffectRenderInParam(mSTHumanActionNative.getNativeHumanActionPtrCopy(),
//                        null, renderOrientation, renderOrientation, false,
//                        null, stEffectTexture, null);
//                //渲染接口输出参数
////                STEffectRenderOutParam stEffectRenderOutParam = new STEffectRenderOutParam(stEffectTextureOut, null, mSTHumanAction[mCameraInputTextureIndex]);
//                STEffectRenderOutParam  stEffectRenderOutParam = new STEffectRenderOutParam(stEffectTextureOut, null, null);
//                long mStartRenderTime = System.currentTimeMillis();
//                mSTMobileEffectNative.render(sTEffectRenderInParam, stEffectRenderOutParam, false);
//                LogUtils.i(TAG, "render cost time total: %d", System.currentTimeMillis() - mStartRenderTime);
//
//                if (stEffectRenderOutParam != null && stEffectRenderOutParam.getTexture() != null) {
//                    textureId = stEffectRenderOutParam.getTexture().getId();
//                }
//
////                if (event == mCustomEvent) {
////                    mCustomEvent = 0;
////                }
//            }
//        }
//
////        if (mNeedSave) {
////            savePicture(textureId);
////            mNeedSave = false;
////        }
//
//        mCountDownLatch.countDown(); // 计数减1
//        try {
//            if (!mShowOriginal && !mIsPaused && !mCameraChanging && isCreateHumanActionHandleSucceeded) {
//                mCountDownLatch.await();
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "onDrawFrame: " + e.getMessage());
//        }

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        int textureId = beautyEngine.processFrame(mNv21ImageData);
        mFrameCost = (int) (System.currentTimeMillis() - mStartTime);
        long timer = System.currentTimeMillis();
        mCount++;

        if (mIsFirstCount) {
            mCurrentTime = timer;
            mIsFirstCount = false;
        } else {
            int cost = (int) (timer - mCurrentTime);
            if (cost >= 1000) {
                mCurrentTime = timer;
                mFps = (((float) mCount * 1000) / cost);
                mCount = 0;
            }
        }

        LogUtils.i(TAG, "frame cost time total: %d", System.currentTimeMillis() - mStartTime);
        LogUtils.i(TAG, "render fps: %f", mFps);

        GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight);
        mGLRender.onDrawFrame(textureId);

//        //video capturing
//        if(mVideoEncoder != null){
//            GLES20.glFinish();
//            mVideoEncoderTexture[0] = textureId;
//            synchronized (this) {
//                if (mVideoEncoder != null) {
//                    if(mNeedResetEglContext){
//                        mVideoEncoder.setEglContext(EGL14.eglGetCurrentContext(), mVideoEncoderTexture[0]);
//                        mNeedResetEglContext = false;
//                    }
//                    mVideoEncoder.frameAvailableSoon(mTextureEncodeMatrix);
//                }
//            }
//        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        //非常重要!!! 屏幕会黑
        initColorConvert();
    }

    @Override
    public void onPause() {
        LogUtils.i(TAG, "onPause");
        mSetPreViewSizeSucceed = false;

        mIsPaused = true;
        mImageData = null;
        mCameraProxy.releaseCamera();
        LogUtils.d(TAG, "Release camera");

        mGlSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                //mSTHumanActionNative.reset();
                //mSTMobileEffectNative.destroyInstance();
                //mSTMobileColorConvertNative.destroyInstance();
                //mNv21ImageData = null;
                //deleteTextures();
                if(mSurfaceTexture != null){
                //    mSurfaceTexture.release();
                }
                //mGLRender.destroyFrameBuffers();
            }
        });

        mGlSurfaceView.onPause();
    }

    @Override
    protected void deleteInternalTextures() {
        super.deleteInternalTextures();
        if (mCameraInputTexture != null) {
            GLES20.glDeleteTextures(2, mCameraInputTexture, 0);
            mCameraInputTexture = null;
        }
    }
}

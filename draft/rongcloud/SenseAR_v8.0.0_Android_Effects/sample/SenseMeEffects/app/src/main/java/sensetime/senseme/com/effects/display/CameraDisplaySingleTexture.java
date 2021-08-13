//package sensetime.senseme.com.effects.display;
//
//import android.content.Context;
//import android.graphics.Rect;
//import android.graphics.SurfaceTexture;
//import android.hardware.Camera;
//import android.opengl.EGL14;
//import android.opengl.GLES20;
//import android.opengl.GLSurfaceView;
//import android.os.Message;
//import android.util.Log;
//
//import com.sensetime.stmobile.STCommon;
//import com.sensetime.stmobile.STEffectBeautyType;
//import com.sensetime.stmobile.STMobileAnimalNative;
//import com.sensetime.stmobile.STMobileColorConvertNative;
//import com.sensetime.stmobile.STMobileHumanActionNative;
//import com.sensetime.stmobile.STRotateType;
//import com.sensetime.stmobile.model.STAnimalFace;
//import com.sensetime.stmobile.model.STAnimalFaceInfo;
//import com.sensetime.stmobile.model.STEffectCustomParam;
//import com.sensetime.stmobile.model.STEffectRenderInParam;
//import com.sensetime.stmobile.model.STEffectRenderOutParam;
//import com.sensetime.stmobile.model.STEffectTexture;
//import com.sensetime.stmobile.model.STHumanAction;
//import com.sensetime.stmobile.model.STQuaternion;
//import com.sensetime.stmobile.model.STRect;
//
//import java.nio.ByteBuffer;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import javax.microedition.khronos.egl.EGLConfig;
//import javax.microedition.khronos.opengles.GL10;
//
//import sensetime.senseme.com.effects.activity.CameraActivity;
//import sensetime.senseme.com.effects.display.glutils.GlUtil;
//import sensetime.senseme.com.effects.display.glutils.OpenGLUtils;
//import sensetime.senseme.com.effects.utils.LogUtils;
//import sensetime.senseme.com.effects.utils.STUtils;
//
///**
// * 代替之前的CameraDisplaySingleInputMultiThread模式
// */
//public class CameraDisplaySingleTexture extends BaseCameraDisplay {
//
//    private String TAG = "CameraDisplaySingleTexture";
//    private Object mHumanActionLock = new Object();
//
//    private ExecutorService mDetectThreadPool = Executors.newFixedThreadPool(1);
//    private CountDownLatch mCountDownLatch = new CountDownLatch(2);
//
//    private int[] mCameraInputTexture;
//    private int mCameraInputTextureIndex = 0;
//    private int mCameraOrientation = STRotateType.ST_CLOCKWISE_ROTATE_90;
//    private boolean mCameraNeedMirror = true;
//
//    public CameraDisplaySingleTexture(Context context, ChangePreviewSizeListener listener, GLSurfaceView glSurfaceView) {
//        super(context, listener, glSurfaceView);
//    }
//
////    private ByteBuffer[] mInputRGBABuffer;
//    private static final float GPU_RESIZE_RATIO = 2.0f;
//
//    private int mDetectWidth, mDetectHeight;
//    private ByteBuffer[] mDetectRGBABuffers;
//    private byte[][] mDetectGray8Buffers;
//
//
//    private STMobileColorConvertNative mSTMobileColorConvertNative = new STMobileColorConvertNative();
//    private void initColorConvert(){
//        if(mSTMobileColorConvertNative != null){
//            int ret = mSTMobileColorConvertNative.createInstance();
//            Log.e(TAG, "initColorConvert ret: "+ ret );
//            if(ret == 0){
//                mSTMobileColorConvertNative.setTextureSize(mDetectWidth, mDetectHeight);
//            }
//        }
//    }
//
//    @Override
//    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        super.onSurfaceCreated(gl, config);
//        initColorConvert();
//    }
//
//    @Override
//    public void onSurfaceChanged(GL10 gl, int width, int height) {
//        LogUtils.i(TAG, "onSurfaceChanged");
//        if (mIsPaused == true) {
//            return ;
//        }
//        adjustViewPort(width, height);
//
//        mGLRender.init(mImageWidth, mImageHeight, mDetectWidth, mDetectHeight);
//        mStartTime = System.currentTimeMillis();
//    }
//
//    /**
//     * camera设备startPreview
//     */
//    @Override
//    protected void setUpCamera() {
//        // 初始化Camera设备预览需要的显示区域(mSurfaceTexture)
//        if (mTextureId == OpenGLUtils.NO_TEXTURE) {
//            mTextureId = OpenGLUtils.getExternalOESTextureID();
//            mSurfaceTexture = new SurfaceTexture(mTextureId);
//            mSurfaceTexture.setOnFrameAvailableListener(mOnFrameAvailableListener);
//        }
//
//        String size = mSupportedPreviewSizes.get(mCurrentPreview);
//        int index = size.indexOf('x');
//        mImageHeight = Integer.parseInt(size.substring(0, index));
//        mImageWidth = Integer.parseInt(size.substring(index + 1));
//
//        mDetectWidth = (int)(mImageWidth/GPU_RESIZE_RATIO);
//        mDetectHeight = (int)(mImageHeight/GPU_RESIZE_RATIO);
//
//        if(mIsPaused)
//            return;
//
//        while(!mSetPreViewSizeSucceed){
//            try{
//                mCameraProxy.setPreviewSize(mImageHeight, mImageWidth);
//                mSetPreViewSizeSucceed = true;
//            }catch (Exception e){
//                mSetPreViewSizeSucceed = false;
//            }
//
//            try{
//                Thread.sleep(10);
//            }catch (Exception e){
//
//            }
//        }
//
//        boolean flipHorizontal = mCameraProxy.isFlipHorizontal();
//        boolean flipVertical = mCameraProxy.isFlipVertical();
//        mGLRender.adjustTextureBuffer(mCameraProxy.getOrientation(), flipVertical, flipHorizontal);
//
//        if(mIsPaused)
//            return;
//        mCameraProxy.startPreview(mSurfaceTexture, null);
//    }
//
//    /**
//     * 工作在opengl线程, 具体渲染的工作函数
//     *
//     * @param gl
//     */
//    @Override
//    public void onDrawFrame(GL10 gl) {
//        // during switch camera
//        if (mCameraChanging || mIsChangingPreviewSize || mIsPaused || mCameraProxy.getCamera() == null) {
//            return;
//        }
//        LogUtils.i(TAG, "onDrawFrame");
//
//        if (mDetectRGBABuffers == null) {
//            mDetectRGBABuffers = new ByteBuffer[2];
//            mDetectRGBABuffers[0] = ByteBuffer.allocate(mDetectWidth * mDetectHeight * 4);
//            mDetectRGBABuffers[1] = ByteBuffer.allocate(mDetectWidth * mDetectHeight * 4);
//        }
//
//        long start = System.currentTimeMillis();
//
//        if(mDetectGray8Buffers == null || mDetectGray8Buffers.length < 2){
//            mDetectGray8Buffers = new byte[2][mDetectWidth * mDetectHeight];
//        }
//
//        if (mBeautifyTextureId == null) {
//            mBeautifyTextureId = new int[1];
//            GlUtil.initEffectTexture(mImageWidth, mImageHeight, mBeautifyTextureId, GLES20.GL_TEXTURE_2D);
//        }
//
//        if (mMakeupTextureId == null) {
//            mMakeupTextureId = new int[1];
//            GlUtil.initEffectTexture(mImageWidth, mImageHeight, mMakeupTextureId, GLES20.GL_TEXTURE_2D);
//        }
//
//        if (mTextureOutId == null) {
//            mTextureOutId = new int[1];
//            GlUtil.initEffectTexture(mImageWidth, mImageHeight, mTextureOutId, GLES20.GL_TEXTURE_2D);
//        }
//
//        if (mCameraInputTexture == null) {
//            mCameraInputTexture = new int[2];
//            GlUtil.initEffectTexture(mImageWidth, mImageHeight, mCameraInputTexture, GLES20.GL_TEXTURE_2D);
//        }
//
//        if (mVideoEncoderTexture == null) {
//            mVideoEncoderTexture = new int[1];
//        }
//
//        if(mSurfaceTexture != null && !mIsPaused){
//            mSurfaceTexture.updateTexImage();
//        }else{
//            return;
//        }
//
//        mCountDownLatch = new CountDownLatch(2);
//        final boolean needRgba = true;//ifNeedRgba(mDetectConfig) || mNeedObject;
//
//        mStartTime = System.currentTimeMillis();
//        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//        mDetectRGBABuffers[mCameraInputTextureIndex].rewind();
//
//        long preProcessCostTime = System.currentTimeMillis();
//        if(!mShowOriginal){
//            //从纹理读取buffer用于检测，若只需要检测人脸信息，可使用灰度图（灰度图拷贝数据量少，速度快）
//            if(!needRgba){
//                int[] resizedTexture = new int[1];
//                //resize纹理，读取数据量小，速度快
//                mCameraInputTexture[mCameraInputTextureIndex] = mGLRender.preProcessAndResizeTexture(mTextureId,  true, resizedTexture,null, mCameraInputTextureIndex);
//
//                //读取会读图数据
//                if(mSTMobileColorConvertNative != null){
//                    mSTMobileColorConvertNative.rgbaTextureToGray8Buffer(resizedTexture[0], mDetectWidth, mDetectHeight, mDetectGray8Buffers[mCameraInputTextureIndex]);
//                }
//            }else {
//                //readpixel读取纹理数据到buffer
//                mCameraInputTexture[mCameraInputTextureIndex] = mGLRender.preProcess(mTextureId, mDetectRGBABuffers[mCameraInputTextureIndex], mCameraInputTextureIndex);
//            }
//        }else {
//            mCameraInputTexture[mCameraInputTextureIndex] = mGLRender.preProcess(mTextureId, null);
//        }
//        LogUtils.i(TAG, "preprocess cost time: %d", System.currentTimeMillis() - preProcessCostTime);
//
//        //双缓冲策略，提升帧率
//        mCameraInputTextureIndex = 1 - mCameraInputTextureIndex;
//        int textureId = mCameraInputTexture[mCameraInputTextureIndex];
//        if(!GLES20.glIsTexture(textureId))return;
//
//        //检测线程
//        boolean isCreateHumanActionHandleSucceeded = mIsCreateHumanActionHandleSucceeded;
//        if(!mShowOriginal && isCreateHumanActionHandleSucceeded){
//            mDetectThreadPool.submit(new Runnable() {
//                @Override
//                public void run() {
//                    int orientation = getCurrentOrientation();
//                    synchronized (mHumanActionLock) {
//                        long startHumanAction = System.currentTimeMillis();
//
//                        STHumanAction humanAction;
//                        if(needRgba){
//                            humanAction = mSTHumanActionNative.humanActionDetect(mDetectRGBABuffers[1 - mCameraInputTextureIndex].array(), STCommon.ST_PIX_FMT_RGBA8888, mDetectConfig, orientation, mDetectWidth, mDetectHeight);
//                            if(mNeedAnimalDetect){
//                                animalDetect(mDetectRGBABuffers[1 - mCameraInputTextureIndex].array(), STCommon.ST_PIX_FMT_RGBA8888, orientation, mDetectWidth, mDetectHeight, 1 - mCameraInputTextureIndex);
//                            }
//                        }else {
//                            humanAction = mSTHumanActionNative.humanActionDetect(mDetectGray8Buffers[1 - mCameraInputTextureIndex], STCommon.ST_PIX_FMT_GRAY8,
//                                    mDetectConfig, orientation, mDetectWidth, mDetectHeight);
//
//                            if(mNeedAnimalDetect){
//                                animalDetect(mDetectGray8Buffers[1 - mCameraInputTextureIndex], STCommon.ST_PIX_FMT_GRAY8, orientation, mDetectWidth, mDetectHeight, 1 - mCameraInputTextureIndex);
//                            }
//                        }
//                        LogUtils.i(TAG, "human action cost time: %d", System.currentTimeMillis() - startHumanAction);
//
//                        //若使用了gpu resize，需要将检测结果放大回去
//                        if(GPU_RESIZE_RATIO != 1.0f) {
//                            mSTHumanAction[1 - mCameraInputTextureIndex] = STHumanAction.humanActionResize(GPU_RESIZE_RATIO, humanAction);
//                        }else {
//                            mSTHumanAction[1 - mCameraInputTextureIndex] = humanAction;
//                        }
//                    }
//
//                    mCountDownLatch.countDown(); // 计数减1
//                }
//            });
//        }
//
//        int result = -1;
//        if(!mShowOriginal){
////          //通用物体追踪
//            objectTrack();
//
//            //核心渲染部分
//            if(mSTMobileEffectNative != null){
//                if(mCurrentFilterStyle != mFilterStyle){
//                    mCurrentFilterStyle = mFilterStyle;
//                    mSTMobileEffectNative.setBeauty(STEffectBeautyType.EFFECT_BEAUTY_FILTER, mCurrentFilterStyle);
//                }
//                if(mCurrentFilterStrength != mFilterStrength){
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
//                int event = mCustomEvent;
//                STEffectCustomParam customParam;
//                if(mSensorEvent != null && mSensorEvent.values != null && mSensorEvent.values.length > 0){
//                    customParam = new STEffectCustomParam(new STQuaternion(mSensorEvent.values), mCameraID == Camera.CameraInfo.CAMERA_FACING_FRONT, event);
//                } else {
//                    customParam = new STEffectCustomParam(new STQuaternion(0f,0f,0f,1f), mCameraID == Camera.CameraInfo.CAMERA_FACING_FRONT, event);
//                }
//
//                //渲染接口输入参数
//                STEffectRenderInParam sTEffectRenderInParam = new STEffectRenderInParam(mSTHumanAction[mCameraInputTextureIndex], mAnimalFaceInfo[mCameraInputTextureIndex], renderOrientation, renderOrientation, false, customParam, stEffectTexture, null);
//                //渲染接口输出参数
//                STEffectRenderOutParam stEffectRenderOutParam = new STEffectRenderOutParam(stEffectTextureOut, null, mSTHumanAction[mCameraInputTextureIndex]);
//                result = mSTMobileEffectNative.render(sTEffectRenderInParam, stEffectRenderOutParam, false);
//
//                if(stEffectRenderOutParam != null && stEffectRenderOutParam.getTexture() != null){
//                    textureId = stEffectRenderOutParam.getTexture().getId();
//                }
//
//                if(event == mCustomEvent){
//                    mCustomEvent = 0;
//                }
//            }
//
//            LogUtils.i(TAG, "render cost time total: %d", System.currentTimeMillis() - mStartTime);
//        }
//
//        if(mNeedSave) {
//            savePicture(textureId);
//            mNeedSave = false;
//        }
//
//        mCountDownLatch.countDown(); // 计数减1
//        try{
//            if(!mShowOriginal && !mIsPaused && !mCameraChanging && isCreateHumanActionHandleSucceeded){
//                mCountDownLatch.await();
//            }
//        }catch (Exception e){
//            Log.e(TAG, "onDrawFrame: "+ e.getMessage());
//        }
//
//        mFrameCost = (int)(System.currentTimeMillis() - mStartTime + mRotateCost + mObjectCost + mFaceAttributeCost/20);
//
//        long timer  = System.currentTimeMillis();
//        mCount++;
//        if(mIsFirstCount){
//            mCurrentTime = timer;
//            mIsFirstCount = false;
//        }else{
//            int cost = (int)(timer - mCurrentTime);
//            if(cost >= 1000){
//                mCurrentTime = timer;
//                mFps = (((float)mCount *1000)/cost);
//                mCount = 0;
//            }
//        }
//
//        LogUtils.i(TAG, "frame cost time total: %d", System.currentTimeMillis() - mStartTime);
//        LogUtils.i(TAG, "render fps: %f", mFps);
//
//        GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight);
//        mGLRender.onDrawFrame(textureId);
//
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
//    }
//
//    @Override
//    protected void animalDetect(byte[] imageData, int format, int orientation, int width ,int height, int index){
//        long catDetectStartTime = System.currentTimeMillis();
//        STAnimalFace[] animalFaces = mStAnimalNative.animalDetect(imageData, format, orientation, width, height);
//        LogUtils.i(TAG, "cat face detect cost time: %d", System.currentTimeMillis() - catDetectStartTime);
//
//        if(animalFaces != null && animalFaces.length > 0 && GPU_RESIZE_RATIO != 1.0f){
//            animalFaces = STMobileAnimalNative.animalResize(GPU_RESIZE_RATIO, animalFaces, animalFaces.length);
//        }
//        mAnimalFaceInfo[index] = new STAnimalFaceInfo(animalFaces, animalFaces == null ? 0 : animalFaces.length);
//    }
//
//    @Override
//    public void onPause() {
//        LogUtils.i(TAG, "onPause");
//        mSetPreViewSizeSucceed = false;
//
//        mIsPaused = true;
//        mImageData = null;
//        mCameraProxy.releaseCamera();
//        LogUtils.d(TAG, "Release camera");
//
//        mGlSurfaceView.queueEvent(new Runnable() {
//            @Override
//            public void run() {
//                mSTHumanActionNative.reset();
//                mSTMobileColorConvertNative.destroyInstance();
//
//                if (mDetectRGBABuffers != null) {
//                    mDetectRGBABuffers[0].clear();
//                    mDetectRGBABuffers[1].clear();
//                    mDetectRGBABuffers = null;
//                }
//
//                if(mDetectGray8Buffers != null){
//                    mDetectGray8Buffers[0] = null;
//                    mDetectGray8Buffers[1] = null;
//                    mDetectGray8Buffers = null;
//                }
//
//                deleteTextures();
//                if(mSurfaceTexture != null){
//                    mSurfaceTexture.release();
//                }
//                mGLRender.destroyFrameBuffers();
//            }
//        });
//
//        mGlSurfaceView.onPause();
//    }
//
//    @Override
//    protected void deleteInternalTextures() {
//        super.deleteInternalTextures();
//        if (mCameraInputTexture != null) {
//            GLES20.glDeleteTextures(2, mCameraInputTexture, 0);
//            mCameraInputTexture = null;
//        }
//    }
//
//    @Override
//    public void changePreviewSize(int currentPreview) {
//        if (mCameraProxy.getCamera() == null || mCameraChanging
//                || mIsPaused) {
//            return;
//        }
//
//        mCurrentPreview = currentPreview;
//        mSetPreViewSizeSucceed = false;
//        mIsChangingPreviewSize = true;
//
//        mCameraChanging = true;
//        mCameraProxy.stopPreview();
//        mGlSurfaceView.queueEvent(new Runnable() {
//            @Override
//            public void run() {
//                if (mDetectRGBABuffers != null) {
//                    mDetectRGBABuffers[0].clear();
//                    mDetectRGBABuffers[1].clear();
//                    mDetectRGBABuffers = null;
//                }
//
//                if(mDetectGray8Buffers != null){
//                    mDetectGray8Buffers[0] = null;
//                    mDetectGray8Buffers[1] = null;
//                    mDetectGray8Buffers = null;
//                }
//
//                deleteTextures();
//                if (mCameraProxy.getCamera() != null) {
//                    setUpCamera();
//                }
//
//                mGLRender.init(mImageWidth, mImageHeight, mDetectWidth, mDetectHeight);
//                if(DEBUG){
//                    mGLRender.initDrawPoints();
//                }
//
//                if(mNeedObject){
//                    resetIndexRect();
//                }
//
//                mGLRender.calculateVertexBuffer(mSurfaceWidth, mSurfaceHeight, mImageWidth, mImageHeight);
//                if (mListener != null) {
//                    mListener.onChangePreviewSize(mImageHeight, mImageWidth);
//                }
//
//                mCameraChanging = false;
//                mIsChangingPreviewSize = false;
//                mGlSurfaceView.requestRender();
//                LogUtils.d(TAG, "exit  change Preview size queue event");
//            }
//        });
//    }
//
//    @Override
//    public void switchCamera() {
//        if (Camera.getNumberOfCameras() == 1
//                || mCameraChanging) {
//            return;
//        }
//
//
//        final int cameraID = 1 - mCameraID;
//        mCameraChanging = true;
//        mCameraProxy.openCamera(cameraID);
//
//        if(mCameraProxy.cameraOpenFailed()){
//            return;
//        }
//
//        mSetPreViewSizeSucceed = false;
//
//        if(mNeedObject){
//            resetIndexRect();
//        }else{
//            Message msg = mHandler.obtainMessage(CameraActivity.MSG_CLEAR_OBJECT);
//            mHandler.sendMessage(msg);
//        }
//
//        mGlSurfaceView.queueEvent(new Runnable() {
//            @Override
//            public void run() {
////                deleteTextures();
//                deleteCameraPreviewTexture();
//                if (mCameraProxy.getCamera() != null) {
//                    setUpCamera();
//                }
//                mCameraChanging = false;
//                mCameraID = cameraID;
//            }
//        });
//        //fix 双输入camera changing时，贴纸和画点mirrow显示
//        mGlSurfaceView.requestRender();
//    }
//
//    private static final long M_CONFIG = 0xFFFFFFFF ^ (STMobileHumanActionNative.ST_MOBILE_FACE_DETECT_FULL |STMobileHumanActionNative.ST_MOBILE_DETECT_EXTRA_FACE_POINTS
//            |STMobileHumanActionNative.ST_MOBILE_DETECT_EYEBALL_CENTER |STMobileHumanActionNative.ST_MOBILE_DETECT_EYEBALL_CONTOUR
//            |STMobileHumanActionNative.ST_MOBILE_DETECT_TONGUE | STMobileHumanActionNative.ST_MOBILE_DETECT_GAZE | STMobileHumanActionNative.ST_MOBILE_DETECT_MOUTH_PARSE);
//    private boolean ifNeedRgba(long detectConfig){
//        boolean need = false;
//        if((detectConfig & M_CONFIG) > 0){
//            need = true;
//        }
//
//        return need;
//    }
//
//    @Override
//    protected void objectTrack(){
//        if(mNeedObject) {
//            if (mNeedSetObjectTarget) {
//                long startTimeSetTarget = System.currentTimeMillis();
//
//                STRect inputRect = new STRect((int)(mTargetRect.left/GPU_RESIZE_RATIO), (int)(mTargetRect.top/GPU_RESIZE_RATIO), (int)(mTargetRect.right/GPU_RESIZE_RATIO), (int)(mTargetRect.bottom/GPU_RESIZE_RATIO));
//
//                mSTMobileObjectTrackNative.setTarget(mDetectRGBABuffers[1 - mCameraInputTextureIndex].array(), STCommon.ST_PIX_FMT_RGBA8888, mDetectWidth, mDetectHeight, inputRect);
//                LogUtils.i(TAG, "setTarget cost time: %d", System.currentTimeMillis() - startTimeSetTarget);
//                mNeedSetObjectTarget = false;
//                mIsObjectTracking = true;
//            }
//
//            Rect rect = new Rect(0, 0, 0, 0);
//
//            if (mIsObjectTracking) {
//                long startTimeObjectTrack = System.currentTimeMillis();
//                float[] score = new float[1];
//                STRect outputRect = mSTMobileObjectTrackNative.objectTrack(mDetectRGBABuffers[1 - mCameraInputTextureIndex].array(), STCommon.ST_PIX_FMT_RGBA8888, mImageWidth, mImageHeight, score);
//                LogUtils.i(TAG, "objectTrack cost time: %d", System.currentTimeMillis() - startTimeObjectTrack);
//                mObjectCost = System.currentTimeMillis() - startTimeObjectTrack;
//
//                if(outputRect != null && score != null && score.length >0){
//                    rect = STUtils.adjustToScreenRectMin(outputRect.getRect(), mSurfaceWidth, mSurfaceHeight, mDetectWidth, mDetectHeight);
//                }
//
//                Message msg = mHandler.obtainMessage(CameraActivity.MSG_DRAW_OBJECT_IMAGE);
//                msg.obj = rect;
//                mHandler.sendMessage(msg);
//                mIndexRect = rect;
//            }else{
//                if (mNeedShowRect) {
//                    Message msg = mHandler.obtainMessage(CameraActivity.MSG_DRAW_OBJECT_IMAGE_AND_RECT);
//                    msg.obj = mIndexRect;
//                    mHandler.sendMessage(msg);
//                } else {
//                    Message msg = mHandler.obtainMessage(CameraActivity.MSG_DRAW_OBJECT_IMAGE);
//                    msg.obj = rect;
//                    mHandler.sendMessage(msg);
//                    mIndexRect = rect;
//                }
//            }
//        }else{
//            mObjectCost = 0;
//
//            if(!mNeedObject || !(mNeedBeautify || mNeedSticker)){
//                Message msg = mHandler.obtainMessage(CameraActivity.MSG_CLEAR_OBJECT);
//                mHandler.sendMessage(msg);
//            }
//        }
//    }
//}

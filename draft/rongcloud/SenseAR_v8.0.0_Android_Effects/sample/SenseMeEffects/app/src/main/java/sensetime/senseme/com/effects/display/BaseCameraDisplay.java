package sensetime.senseme.com.effects.display;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.SensorEvent;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.rongcloud.st.beauty.RCRTCBeautyEngineImpl;
import com.sensetime.stmobile.STCommon;
import com.sensetime.stmobile.STEffectBeautyType;
import com.sensetime.stmobile.STHumanActionParamsType;
import com.sensetime.stmobile.STMobileAnimalNative;
import com.sensetime.stmobile.STMobileEffectNative;
import com.sensetime.stmobile.STMobileHumanActionNative;
import com.sensetime.stmobile.STRotateType;
import com.sensetime.stmobile.model.STAnimalFace;
import com.sensetime.stmobile.model.STAnimalFaceInfo;
import com.sensetime.stmobile.model.STEffectBeautyInfo;
import com.sensetime.stmobile.model.STEffectCustomParam;
import com.sensetime.stmobile.model.STEffectRenderInParam;
import com.sensetime.stmobile.model.STEffectRenderOutParam;
import com.sensetime.stmobile.model.STEffectTexture;
import com.sensetime.stmobile.model.STFaceAttribute;
import com.sensetime.stmobile.model.STHumanAction;
import com.sensetime.stmobile.model.STMobile106;
import com.sensetime.stmobile.model.STQuaternion;
import com.sensetime.stmobile.model.STRect;
import com.sensetime.stmobile.sticker_module_types.STCustomEvent;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import sensetime.senseme.com.effects.activity.CameraActivity;
import sensetime.senseme.com.effects.SenseMeApplication;
import sensetime.senseme.com.effects.camera.CameraProxy;
import sensetime.senseme.com.effects.display.glutils.STGLRender;
import sensetime.senseme.com.effects.encoder.MediaVideoEncoder;
import sensetime.senseme.com.effects.encoder.mediacodec.utils.CollectionUtils;
import sensetime.senseme.com.effects.utils.Accelerometer;
import sensetime.senseme.com.effects.utils.Constants;
import sensetime.senseme.com.effects.utils.EffectInfoDataHelper;
import sensetime.senseme.com.effects.utils.FileUtils;
import sensetime.senseme.com.effects.display.glutils.OpenGLUtils;
import sensetime.senseme.com.effects.display.glutils.TextureRotationUtil;
import sensetime.senseme.com.effects.display.glutils.GlUtil;
import sensetime.senseme.com.effects.utils.STUtils;

import static sensetime.senseme.com.effects.utils.STUtils.convertMakeupTypeToNewType;

/**
 * 代替之前的CameraDisplayDoubleInput模式
 */
public class BaseCameraDisplay extends BaseDisplay implements Renderer {
    private static final String TAG = "BaseCameraDisplay";
    protected boolean DEBUG;

    protected boolean mNeedAvatar = true;
    protected int mLastBeautyOverlapCount = -1;

    protected STHumanAction[] mSTHumanAction = new STHumanAction[2];

    /**
     * SurfaceTexure texture id
     */
    protected int mTextureId = OpenGLUtils.NO_TEXTURE;

    protected int mImageWidth;
    protected int mImageHeight;
    protected ChangePreviewSizeListener mListener;
    protected int mSurfaceWidth;
    protected int mSurfaceHeight;

    protected Context mContext;

    public CameraProxy mCameraProxy;
    protected SurfaceTexture mSurfaceTexture;
    protected String mCurrentSticker;
    protected String mCurrentFilterStyle = "";
    protected float mCurrentFilterStrength = -1f;
    protected String mFilterStyle;

    protected int mCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    protected STGLRender mGLRender;

    protected int[] mBeautifyTextureId, mMakeupTextureId;
    protected int[] mTextureOutId;
    protected int[] mFilterTextureOutId;
    protected boolean mCameraChanging = false;
    protected int mCurrentPreview = 0;
    protected ArrayList<String> mSupportedPreviewSizes;
    protected boolean mSetPreViewSizeSucceed = false;
    protected boolean mIsChangingPreviewSize = false;

    protected long mStartTime;
    protected boolean mShowOriginal = false;
    protected boolean mNeedBeautify = false;
    protected boolean mNeedFaceAttribute = false;
    protected boolean mNeedSticker = false;
    protected boolean mNeedFilter = false;
    protected boolean mNeedSave = false;
    protected boolean mNeedObject = false;
    protected boolean mNeedMakeup = false;

    protected FloatBuffer mTextureBuffer;
    protected Handler mHandler;
    protected String mFaceAttribute;
    protected boolean mIsPaused = false;
    protected boolean mIsCreateHumanActionHandleSucceeded = false;
    protected Object mHumanActionHandleLock = new Object();
    protected Object mImageDataLock = new Object();

    protected boolean mNeedShowRect = true;
    protected int mScreenIndexRectWidth = 0;

    protected Rect mTargetRect = new Rect();
    protected Rect mIndexRect = new Rect();
    protected boolean mNeedSetObjectTarget = false;
    protected boolean mIsObjectTracking = false;

    protected int mHumanActionCreateConfig = STMobileHumanActionNative.ST_MOBILE_HUMAN_ACTION_DEFAULT_CONFIG_VIDEO;
    protected byte[] mNv21ImageData;

    protected HandlerThread mProcessImageThread;
    protected Handler mProcessImageHandler;
    protected static final int MESSAGE_PROCESS_IMAGE = 100;
    protected byte[] mImageData;
    protected long mRotateCost = 0;
    protected long mObjectCost = 0;
    protected long mFaceAttributeCost = 0;

    //for test fps
    protected float mFps;
    protected int mCount = 0;
    protected long mCurrentTime = 0;
    protected boolean mIsFirstCount = true;
    protected int mFrameCost = 0;

    protected MediaVideoEncoder mVideoEncoder;
    protected final float[] mTextureEncodeMatrix = {1f, 0f, 0f, 0f,
            0f, -1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 1f, 0f, 1f};

    protected int[] mVideoEncoderTexture;
    protected boolean mNeedResetEglContext = false;

    protected static final int MESSAGE_ADD_SUB_MODEL = 1001;
    protected static final int MESSAGE_REMOVE_SUB_MODEL = 1002;
    protected static final int MESSAGE_NEED_CHANGE_STICKER = 1003;
    protected static final int MESSAGE_NEED_REMOVE_STICKER = 1004;
    protected static final int MESSAGE_NEED_REMOVEALL_STICKERS = 1005;
    protected static final int MESSAGE_NEED_ADD_STICKER = 1006;

    protected HandlerThread mSubModelsManagerThread;
    protected Handler mSubModelsManagerHandler;

    protected HandlerThread mChangeStickerManagerThread;
    protected Handler mChangeStickerManagerHandler;

    protected long mHandAction = 0;
    protected long mBodyAction = 0;
    protected boolean[] mFaceExpressionResult;

    protected int mCustomEvent = 0;
    protected SensorEvent mSensorEvent;

    public TreeMap<Integer, String> mCurrentStickerMaps = new TreeMap<>();
    protected int mParamType = 0;

    protected int[] mMakeupPackageId = new int[Constants.MAKEUP_TYPE_COUNT];
    protected String[] mCurrentMakeup = new String[Constants.MAKEUP_TYPE_COUNT];
    protected float[] mMakeupStrength = new float[Constants.MAKEUP_TYPE_COUNT];

    //todo:mascode
    protected RCRTCBeautyEngineImpl beautyEngine;

    public BaseCameraDisplay(Context context, ChangePreviewSizeListener listener, GLSurfaceView glSurfaceView) {
        super(glSurfaceView);
        mCameraProxy = new CameraProxy(context);
        mListener = listener;
        mContext = context;
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setEGLContextFactory(this);
        glSurfaceView.setRenderer(this);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        mTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION).position(0);
        mGLRender = new STGLRender();

        //是否为debug模式
        DEBUG = CameraActivity.DEBUG;

        //初始化非OpengGL相关的句柄，包括人脸检测及人脸属性
        initHumanAction();
//        initCatFace();
//        initObjectTrack();

        //因为人脸模型加载较慢，建议异步调用
        if (DEBUG) {
//            initFaceAttribute();
        }

//        initHandlerManager();
        setDefaultParams();
//        setDefaultMakeup();
//        setDefaultFilter();
        updateHumanActionDetectConfig();

//        //todo:mascode
//        beautyEngine = new RCRTCBeautyEngineImpl();
//        beautyEngine.init(mContext, Camera.CameraInfo.CAMERA_FACING_FRONT, 270, 720, 1280);
//        beautyEngine.setBeautyEnable(true);
    }

    protected void initHandlerManager() {
        mProcessImageThread = new HandlerThread("ProcessImageThread");
        mProcessImageThread.start();
        mProcessImageHandler = new Handler(mProcessImageThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_PROCESS_IMAGE && !mIsPaused && !mCameraChanging) {
                    objectTrack();
                }
            }
        };

        mSubModelsManagerThread = new HandlerThread("SubModelManagerThread");
        mSubModelsManagerThread.start();
        mSubModelsManagerHandler = new Handler(mSubModelsManagerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (!mIsPaused && !mCameraChanging && mIsCreateHumanActionHandleSucceeded) {
                    switch (msg.what) {
                        case MESSAGE_ADD_SUB_MODEL:
                            String modelName = (String) msg.obj;
                            if (modelName != null) {
                                addSubModel(modelName);
                            }
                            break;

                        case MESSAGE_REMOVE_SUB_MODEL:
                            int config = (int) msg.obj;
                            if (config != 0) {
                                removeSubModel(config);
                            }
                            break;

                        default:
                            break;
                    }
                }
            }
        };

        mChangeStickerManagerThread = new HandlerThread("ChangeStickerManagerThread");
        mChangeStickerManagerThread.start();
        mChangeStickerManagerHandler = new Handler(mChangeStickerManagerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (!mIsPaused && !mCameraChanging) {
                    switch (msg.what) {
                        case MESSAGE_NEED_CHANGE_STICKER:
                            String sticker = (String) msg.obj;
                            mCurrentSticker = sticker;
//                            int packageId1 = mSTMobileEffectNative.changePackage(mCurrentSticker);
//                            if (packageId1 > 0) {
//                                mLastBeautyOverlapCount = -1;
//                                CollectionUtils.removeByValue(mCurrentStickerMaps, sticker);
//                                mCurrentStickerMaps.put(packageId1, sticker);
//                            }
//                            updateHumanActionDetectConfig();
//                            updateAnimalDetectConfig();
//                            Message message = mHandler.obtainMessage(CameraActivity.MSG_NEED_UPDATE_STICKER_TIPS);
//                            mHandler.sendMessage(message);
                            break;
                        case MESSAGE_NEED_ADD_STICKER:
//                            String addSticker = (String) msg.obj;
//                            mCurrentSticker = addSticker;
//                            int stickerId = mSTMobileEffectNative.addPackage(mCurrentSticker);
//                            if (stickerId > 0) {
//                                if (mCurrentStickerMaps != null) {
//                                    CollectionUtils.removeByValue(mCurrentStickerMaps, addSticker);
//                                    mCurrentStickerMaps.put(stickerId, mCurrentSticker);
//                                }
//                            } else {
//                                Toast.makeText(SenseMeApplication.getContext(), "添加太多贴纸了", Toast.LENGTH_SHORT).show();
//                            }
//                            updateHumanActionDetectConfig();
//                            updateAnimalDetectConfig();
//                            Message messageAdd = mHandler.obtainMessage(CameraActivity.MSG_NEED_UPDATE_STICKER_TIPS);
//                            mHandler.sendMessage(messageAdd);
                            break;
                        case MESSAGE_NEED_REMOVE_STICKER:
//                            int packageId = (int) msg.obj;
//                            int result = mSTMobileEffectNative.removeEffect(packageId);
//
//                            if (mCurrentStickerMaps != null && result == 0) {
//                                mCurrentStickerMaps.remove(packageId);
//                            }
//                            updateHumanActionDetectConfig();
                            break;
                        case MESSAGE_NEED_REMOVEALL_STICKERS:
////                            mStStickerNative.removeAllStickers();
//                            if (mCurrentStickerMaps != null) {
//                                mCurrentStickerMaps.clear();
//                            }
////                            setHumanActionDetectConfig();
//
//                            mSTMobileEffectNative.clear();
//                            updateHumanActionDetectConfig();
                            break;
                        default:
                            break;
                    }
                }
            }
        };
    }

    public void updateBeautyParamsUI() {
        int beautyOverlapCount = mSTMobileEffectNative.getOverlappedBeautyCount();
        mLastBeautyOverlapCount = beautyOverlapCount;
        Log.d(TAG, "updateBeautyParamsUI: beautyOverlapCount:" + beautyOverlapCount);
        if (beautyOverlapCount > 0) {
            STEffectBeautyInfo[] beautyInfos = mSTMobileEffectNative.getOverlappedBeauty(beautyOverlapCount);
            Log.d(TAG, "run: " + Arrays.toString(beautyInfos));
            setBeautyParamsFromPackage(beautyInfos);

            Message message1 = mHandler.obtainMessage(CameraActivity.MSG_NEED_UPDATE_BEAUTY_PARAMS);
            mHandler.sendMessage(message1);
        } else {
            mHandler.sendEmptyMessage(CameraActivity.MSG_NEED_RECOVERUI);
        }
    }

    protected void addSubModel(final String modelName) {
//        synchronized (mHumanActionHandleLock) {
//            int result = mSTHumanActionNative.addSubModelFromAssetFile(modelName, mContext.getAssets());
//            Log.i(TAG, String.format("add sub model result: %d", result));
//
//            if (result == 0) {
//                if (modelName.equals(FileUtils.MODEL_NAME_BODY_FOURTEEN)) {
//                    mDetectConfig |= STMobileHumanActionNative.ST_MOBILE_BODY_KEYPOINTS;
//                    mSTHumanActionNative.setParam(STHumanActionParamsType.ST_HUMAN_ACTION_PARAM_BODY_LIMIT, 3.0f);
//                } else if (modelName.equals(FileUtils.MODEL_NAME_FACE_EXTRA)) {
//                    mDetectConfig |= STMobileHumanActionNative.ST_MOBILE_DETECT_EXTRA_FACE_POINTS;
//                } else if (modelName.equals(FileUtils.MODEL_NAME_EYEBALL_CONTOUR)) {
//                    mDetectConfig |= STMobileHumanActionNative.ST_MOBILE_DETECT_EYEBALL_CONTOUR |
//                            STMobileHumanActionNative.ST_MOBILE_DETECT_EYEBALL_CENTER;
//                } else if (modelName.equals(FileUtils.MODEL_NAME_HAND)) {
//                    mDetectConfig |= STMobileHumanActionNative.ST_MOBILE_HAND_DETECT_FULL;
//                } else if (modelName.equals(FileUtils.MODEL_NAME_AVATAR_HELP)) {
//                    mDetectConfig |= STMobileHumanActionNative.ST_MOBILE_DETECT_AVATAR_HELPINFO;
//                }
//            }
//        }
    }

    protected void removeSubModel(final int config) {
//        synchronized (mHumanActionHandleLock) {
//            int result = mSTHumanActionNative.removeSubModelByConfig(config);
//            Log.i(TAG, String.format("remove sub model result: %d", result));
//
//            if (config == STMobileHumanActionNative.ST_MOBILE_ENABLE_BODY_KEYPOINTS) {
//                mDetectConfig &= ~STMobileHumanActionNative.ST_MOBILE_BODY_KEYPOINTS;
//            } else if (config == STMobileHumanActionNative.ST_MOBILE_ENABLE_FACE_EXTRA_DETECT) {
//                mDetectConfig &= ~STMobileHumanActionNative.ST_MOBILE_DETECT_EXTRA_FACE_POINTS;
//            } else if (config == STMobileHumanActionNative.ST_MOBILE_ENABLE_EYEBALL_CONTOUR_DETECT) {
//                mDetectConfig &= ~(STMobileHumanActionNative.ST_MOBILE_DETECT_EYEBALL_CONTOUR |
//                        STMobileHumanActionNative.ST_MOBILE_DETECT_EYEBALL_CENTER);
//            } else if (config == STMobileHumanActionNative.ST_MOBILE_ENABLE_HAND_DETECT) {
//                mDetectConfig &= ~STMobileHumanActionNative.ST_MOBILE_HAND_DETECT_FULL;
//            } else if (config == STMobileHumanActionNative.ST_MOBILE_DETECT_AVATAR_HELPINFO) {
//                mDetectConfig &= ~STMobileHumanActionNative.ST_MOBILE_DETECT_AVATAR_HELPINFO;
//            }
//        }
    }

    public void enableBeautify(boolean needBeautify) {
        mNeedBeautify = needBeautify;
        updateHumanActionDetectConfig();
        mNeedResetEglContext = true;
    }

    public void enableSticker(boolean needSticker) {
        mNeedSticker = needSticker;
        //reset humanAction config
        if (!needSticker) {
            updateHumanActionDetectConfig();
        }

        mNeedResetEglContext = true;
    }

    public void enableMakeUp(boolean needMakeup) {
        mNeedMakeup = needMakeup;
        mNeedResetEglContext = true;

        updateHumanActionDetectConfig();
    }

    public void enableFilter(boolean needFilter) {
        mNeedFilter = needFilter;
        mNeedResetEglContext = true;
    }

    public boolean getFaceAttribute() {
        return mNeedFaceAttribute;
    }

    public String getFaceAttributeString() {
        return mFaceAttribute;
    }

    public boolean getSupportPreviewsize(int size) {
        if (size == 0 && mSupportedPreviewSizes.contains("640x480")) {
            return true;
        } else if (size == 1 && mSupportedPreviewSizes.contains("1280x720")) {
            return true;
        } else {
            return false;
        }
    }

    public void setSaveImage() {
        mNeedSave = true;
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    /**
     * 工作在opengl线程, 当前Renderer关联的view创建的时候调用
     *
     * @param gl
     * @param config
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated");
        //recoverParams();
        if (mIsPaused == true) {
            return;
        }
        GLES20.glEnable(GL10.GL_DITHER);
        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glEnable(GL10.GL_DEPTH_TEST);

        while (!mCameraProxy.isCameraOpen()) {
            if (mCameraProxy.cameraOpenFailed()) {
                return;
            }
            try {
                Thread.sleep(10, 0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (mCameraProxy.getCamera() != null) {
            setUpCamera();
        }
    }

    protected void setDefaultMakeup() {
        if (EffectInfoDataHelper.getInstance().getFullBeautyName().equals("")) {
            mCurrentMakeup = EffectInfoDataHelper.getInstance().getCurrentMakeUp();
            mMakeupStrength = EffectInfoDataHelper.getInstance().getCurrentMakeUpStrength(mMakeupStrength);
            mCurrentFilterStyle = EffectInfoDataHelper.getInstance().getFilterStyle();
            try {
                for (int i = 0; i < Constants.MAKEUP_TYPE_COUNT; i++) {
                    if (mCurrentMakeup[i] != null && !mCurrentMakeup[i].equals("null")) {
                        setMakeupForType(i, mCurrentMakeup[i]);
                        setStrengthForType(i, mMakeupStrength[i]);
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    protected void setDefaultFilter() {
//        if (EffectInfoDataHelper.getInstance().getFullBeautyName().equals("")) {
//            mSTMobileEffectNative.setBeauty(STEffectBeautyType.EFFECT_BEAUTY_FILTER, EffectInfoDataHelper.getInstance().getFilterStyle());
//            setFilterStrength(EffectInfoDataHelper.getInstance().getFilterStrength());
//        }
    }

    protected void initFaceAttribute() {
        int result = mSTFaceAttributeNative.createInstanceFromAssetFile(FileUtils.MODEL_NAME_FACE_ATTRIBUTE, mContext.getAssets());
        Log.i(TAG, String.format("the result for createInstance for faceAttribute is %d", result));
    }

    protected void initHumanAction() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (mHumanActionHandleLock) {
                    long startLoadHumanActionModel = System.currentTimeMillis();

                    //从sd读取model路径，创建handle
                    //int result = mSTHumanActionNative.createInstance(FileUtils.getTrackModelPath(mContext), mHumanActionCreateConfig);

                    //从asset资源文件夹读取model到内存，再使用底层st_mobile_human_action_create_from_buffer接口创建handle
                    int result = mSTHumanActionNative.createInstanceFromAssetFile(FileUtils.getActionModelName(), mHumanActionCreateConfig, mContext.getAssets());
                    Log.i(TAG, String.format("the result for createInstance for human_action is %d", result));
                    Log.e(TAG, "load model name: " + FileUtils.getActionModelName() + " cost time: " +
                            (System.currentTimeMillis() - startLoadHumanActionModel));

                    if (result == 0) {
                        result = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_HAND, mContext.getAssets());
                        Log.i(TAG, String.format("add hand model result: %d", result));
                        result = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_SEGMENT, mContext.getAssets());
                        Log.i(TAG, String.format("add figure segment model result: %d", result));

                        mIsCreateHumanActionHandleSucceeded = true;
                        mSTHumanActionNative.setParam(STHumanActionParamsType.ST_HUMAN_ACTION_PARAM_BACKGROUND_BLUR_STRENGTH, 0.35f);
                        //mSTHumanActionNative.setParam(STHumanActionParamsType.ST_HUMAN_ACTION_PARAM_HEAD_SEGMENT_RESULT_ROTATE, 1.0f);

                        //240
                        result = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_FACE_EXTRA, mContext.getAssets());
                        Log.i(TAG, String.format("add face extra model result: %d", result));

                        //eye
                        result = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_EYEBALL_CONTOUR, mContext.getAssets());
                        Log.i(TAG, String.format("add eyeball contour model result: %d", result));

                        result = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_HAIR, mContext.getAssets());
                        Log.i(TAG, String.format("add hair model result: %d", result));

                        result = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_LIPS_PARSING, mContext.getAssets());
                        Log.i(TAG, String.format("add lips parsing model result: %d", result));

                        result = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.HEAD_SEGMENT_MODEL_NAME, mContext.getAssets());
                        Log.i(TAG, String.format("add head segment model result: %d", result));

                        //for test avatar
//                        if (mNeedAvatar) {
//                            int ret = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_AVATAR_HELP, mContext.getAssets());
//                            Log.i(TAG, String.format("add avatar help model result: %d", ret));
//
////                            ret = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_TONGUE, mContext.getAssets());
////                            Log.i(TAG,"add tongue model result: %d", ret );
//                        }
                    }
                }
            }
        }).start();
    }

    protected void initCatFace() {
        int result = mStAnimalNative.createInstanceFromAssetFile(FileUtils.MODEL_NAME_CATFACE_CORE, STCommon.ST_MOBILE_TRACKING_MULTI_THREAD, mContext.getAssets());
        Log.i(TAG, String.format("create animal handle result: %d", result));
    }

    protected void setDefaultParams() {
        int result = mSTMobileEffectNative.createInstance(mContext, STMobileEffectNative.EFFECT_CONFIG_NONE);
        Log.i(TAG, String.format("the result is for initEffect:" + result));

//        mBeautifyParamsTypeBase = EffectInfoDataHelper.getInstance().getBaseParams();
//        mBeautifyParamsTypeMicro = EffectInfoDataHelper.getInstance().getMicroParams();
//        mBeautifyParamsTypeAdjust = EffectInfoDataHelper.getInstance().getAdjustParams();
//        mBeautifyParamsTypeProfessional = EffectInfoDataHelper.getInstance().getProfessionalParams();
//
//        mSTMobileEffectNative.setBeautyMode(103, 2);
//
//        setBeautifyBaseParam(mBeautifyParamsTypeBase);
//
//        for (int i = 0; i < mBeautifyParamsTypeProfessional.length; i++) {
//            setBeautyParam(STEffectBeautyType.EFFECT_BEAUTY_RESHAPE_SHRINK_FACE + i, (mBeautifyParamsTypeProfessional[i]));
//        }
//
//        for (int i = 0; i < mBeautifyParamsTypeMicro.length; i++) {
//            setBeautyParam(STEffectBeautyType.EFFECT_BEAUTY_PLASTIC_THINNER_HEAD + i, (mBeautifyParamsTypeMicro[i]));
//        }
//
//        for (int i = 0; i < mBeautifyParamsTypeAdjust.length; i++) {
//            setBeautyParam(STEffectBeautyType.EFFECT_BEAUTY_TONE_CONTRAST + i, (mBeautifyParamsTypeAdjust[i]));
//        }
    }

    protected void initObjectTrack() {
        int result = mSTMobileObjectTrackNative.createInstance();
    }

    protected void objectTrack() {
        if (mImageData == null || mImageData.length == 0) {
            return;
        }

        //mNeedObject=false
        if (mNeedObject) {
            if (mNeedSetObjectTarget) {
                long startTimeSetTarget = System.currentTimeMillis();

                mTargetRect = STUtils.getObjectTrackInputRect(mTargetRect, mImageWidth, mImageHeight, mCameraID, mCameraProxy.getOrientation());
                STRect inputRect = new STRect(mTargetRect.left, mTargetRect.top, mTargetRect.right, mTargetRect.bottom);

                mSTMobileObjectTrackNative.setTarget(mImageData, STCommon.ST_PIX_FMT_NV21, mImageHeight, mImageWidth, inputRect);
                Log.i(TAG, String.format("setTarget cost time: %d", System.currentTimeMillis() - startTimeSetTarget));
                mNeedSetObjectTarget = false;
                mIsObjectTracking = true;
            }

            Rect rect = new Rect(0, 0, 0, 0);

            if (mIsObjectTracking) {
                long startTimeObjectTrack = System.currentTimeMillis();
                float[] score = new float[1];
                STRect outputRect = mSTMobileObjectTrackNative.objectTrack(mImageData, STCommon.ST_PIX_FMT_NV21, mImageHeight, mImageWidth, score);
                Log.i(TAG, String.format("objectTrack cost time: %d", System.currentTimeMillis() - startTimeObjectTrack));
                mObjectCost = System.currentTimeMillis() - startTimeObjectTrack;

                if (outputRect != null && score != null && score.length > 0) {
                    Rect outputTargetRect = new Rect(outputRect.getRect().left, outputRect.getRect().top, outputRect.getRect().right, outputRect.getRect().bottom);
                    outputTargetRect = STUtils.getObjectTrackOutputRect(outputTargetRect, mImageWidth, mImageHeight, mCameraID, mCameraProxy.getOrientation());

                    rect = STUtils.adjustToScreenRectMin(outputTargetRect, mSurfaceWidth, mSurfaceHeight, mImageWidth, mImageHeight);

                    if (!mNeedShowRect) {
                        Message msg = mHandler.obtainMessage(CameraActivity.MSG_DRAW_OBJECT_IMAGE);
                        msg.obj = rect;
                        mHandler.sendMessage(msg);
                        mIndexRect = rect;
                    }
                }

            } else {
                if (mNeedShowRect) {
                    Message msg = mHandler.obtainMessage(CameraActivity.MSG_DRAW_OBJECT_IMAGE_AND_RECT);
                    msg.obj = mIndexRect;
                    mHandler.sendMessage(msg);
                } else {
                    Message msg = mHandler.obtainMessage(CameraActivity.MSG_DRAW_OBJECT_IMAGE);
                    msg.obj = rect;
                    mHandler.sendMessage(msg);
                    mIndexRect = rect;
                }
            }
        } else {
            mObjectCost = 0;

            if (!mNeedObject || !(mNeedBeautify || mNeedSticker || mNeedFaceAttribute)) {
                Message msg = mHandler.obtainMessage(CameraActivity.MSG_CLEAR_OBJECT);
                mHandler.sendMessage(msg);
            }
        }
    }

    protected void faceAttributeDetect(byte[] data, STHumanAction humanAction) {
        if (humanAction != null && data != null && data.length == mImageWidth * mImageHeight * 3 / 2) {
            STMobile106[] arrayFaces = null;
            arrayFaces = humanAction.getMobileFaces();

            if (arrayFaces != null && arrayFaces.length != 0) { // face attribute
                STFaceAttribute[] arrayFaceAttribute = new STFaceAttribute[arrayFaces.length];
                long attributeCostTime = System.currentTimeMillis();
                int result = mSTFaceAttributeNative.detect(data, STCommon.ST_PIX_FMT_NV21, mImageHeight, mImageWidth, arrayFaces, arrayFaceAttribute);
                Log.i(TAG, String.format("attribute cost time: %d", System.currentTimeMillis() - attributeCostTime));
                mFaceAttributeCost = System.currentTimeMillis() - attributeCostTime;
                if (result == 0) {
                    if (arrayFaceAttribute[0].getAttributeCount() > 0) {
                        mFaceAttribute = STFaceAttribute.getFaceAttributeString(arrayFaceAttribute[0]);
                    } else {
                        mFaceAttribute = "null";
                    }
                }
            } else {
                mFaceAttribute = null;
                mFaceAttributeCost = 0;
            }
        }
    }

    protected STAnimalFaceInfo[] mAnimalFaceInfo = new STAnimalFaceInfo[2];

    protected void animalDetect(byte[] imageData, int format, int orientation, int width, int height, int index) {
        if (mNeedAnimalDetect) {
            long catDetectStartTime = System.currentTimeMillis();
            STAnimalFace[] animalFaces = mStAnimalNative.animalDetect(imageData, format, orientation, width, height);
            Log.i(TAG, String.format("cat face detect cost time: %d", System.currentTimeMillis() - catDetectStartTime));

            if (animalFaces != null && animalFaces.length > 0) {
                animalFaces = processAnimalFaceResult(animalFaces, mCameraID == Camera.CameraInfo.CAMERA_FACING_FRONT, mCameraProxy.getOrientation());
            }

            mAnimalFaceInfo[index] = new STAnimalFaceInfo(animalFaces, animalFaces == null ? 0 : animalFaces.length);
        }
    }

    protected STAnimalFace[] processAnimalFaceResult(STAnimalFace[] animalFaces, boolean isFrontCamera, int cameraOrientation) {
        if (animalFaces == null) {
            return null;
        }
        if (isFrontCamera && cameraOrientation == 90) {
            animalFaces = STMobileAnimalNative.animalRotate(mImageHeight, mImageWidth, STRotateType.ST_CLOCKWISE_ROTATE_90, animalFaces, animalFaces.length);
            animalFaces = STMobileAnimalNative.animalMirror(mImageWidth, animalFaces, animalFaces.length);
        } else if (isFrontCamera && cameraOrientation == 270) {
            animalFaces = STMobileAnimalNative.animalRotate(mImageHeight, mImageWidth, STRotateType.ST_CLOCKWISE_ROTATE_270, animalFaces, animalFaces.length);
            animalFaces = STMobileAnimalNative.animalMirror(mImageWidth, animalFaces, animalFaces.length);
        } else if (!isFrontCamera && cameraOrientation == 270) {
            animalFaces = STMobileAnimalNative.animalRotate(mImageHeight, mImageWidth, STRotateType.ST_CLOCKWISE_ROTATE_270, animalFaces, animalFaces.length);
        } else if (!isFrontCamera && cameraOrientation == 90) {
            animalFaces = STMobileAnimalNative.animalRotate(mImageHeight, mImageWidth, STRotateType.ST_CLOCKWISE_ROTATE_90, animalFaces, animalFaces.length);
        }
        return animalFaces;
    }

    public void setShowOriginal(boolean isShow) {
        mShowOriginal = isShow;
    }

    /**
     * 工作在opengl线程, 当前Renderer关联的view尺寸改变的时候调用
     *
     * @param gl
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged");
        if (mIsPaused == true) {
            return;
        }
        adjustViewPort(width, height);

        mGLRender.init(mImageWidth, mImageHeight);
        mStartTime = System.currentTimeMillis();
    }

    /**
     * 根据显示区域大小调整一些参数信息
     *
     * @param width
     * @param height
     */
    protected void adjustViewPort(int width, int height) {
        mSurfaceHeight = height;
        mSurfaceWidth = width;
        GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight);
        mGLRender.calculateVertexBuffer(mSurfaceWidth, mSurfaceHeight, mImageWidth, mImageHeight);
    }

    protected Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(final byte[] data, Camera camera) {

            if (mCameraChanging || mCameraProxy.getCamera() == null || data == null ||
                    mIsChangingPreviewSize || data.length != mImageWidth * mImageHeight * 3 / 2) {
                return;
            }

            if (mImageData == null || mImageData.length != mImageHeight * mImageWidth * 3 / 2) {
                mImageData = new byte[mImageWidth * mImageHeight * 3 / 2];
            }
            synchronized (mImageDataLock) {
                System.arraycopy(data, 0, mImageData, 0, data.length);
            }

//            mProcessImageHandler.removeMessages(MESSAGE_PROCESS_IMAGE);
//            mProcessImageHandler.sendEmptyMessage(MESSAGE_PROCESS_IMAGE);

            mGlSurfaceView.requestRender();
            camera.addCallbackBuffer(data);
        }
    };

    /**
     * 工作在opengl线程, 具体渲染的工作函数
     *
     * @param gl
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        // during switch camera
//        if (mCameraChanging) {
//            return;
//        }
//
//        if (mCameraProxy.getCamera() == null) {
//            return;
//        }
//
//        Log.i(TAG, "onDrawFrame");
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
//        if (mVideoEncoderTexture == null) {
//            mVideoEncoderTexture = new int[1];
//        }
//
//        if (mSurfaceTexture != null && !mIsPaused) {
//            mSurfaceTexture.updateTexImage();
//        } else {
//            return;
//        }
//
//        mStartTime = System.currentTimeMillis();
//        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//
//        long preProcessCostTime = System.currentTimeMillis();
//        int textureId = mGLRender.preProcess(mTextureId, null);
//        Log.i(TAG, String.format("preprocess cost time: %d", System.currentTimeMillis() - preProcessCostTime));
//
//        int result = -1;
//        if (!mShowOriginal) {
//            if (mIsCreateHumanActionHandleSucceeded) {
//                if (mCameraChanging || mImageData == null || mImageData.length != mImageHeight * mImageWidth * 3 / 2) {
//                    return;
//                }
//                synchronized (mImageDataLock) {
//                    if (mNv21ImageData == null || mNv21ImageData.length != mImageHeight * mImageWidth * 3 / 2) {
//                        mNv21ImageData = new byte[mImageWidth * mImageHeight * 3 / 2];
//                    }
//
//                    if (mImageData != null && mNv21ImageData.length >= mImageData.length) {
//                        System.arraycopy(mImageData, 0, mNv21ImageData, 0, mImageData.length);
//                    }
//                }
//
//                if (mImageHeight * mImageWidth * 3 / 2 > mNv21ImageData.length) {
//                    return;
//                }
//
//                long startHumanAction = System.currentTimeMillis();
//                STHumanAction humanAction = mSTHumanActionNative.humanActionDetect(mNv21ImageData, STCommon.ST_PIX_FMT_NV21,
//                        mDetectConfig, getHumanActionOrientation(), mImageHeight, mImageWidth);
//                Log.i(TAG, String.format("human action cost time: %d", System.currentTimeMillis() - startHumanAction));
//
//                if (mNeedAnimalDetect) {
//                    animalDetect(mNv21ImageData, STCommon.ST_PIX_FMT_NV21, getHumanActionOrientation(), mImageHeight, mImageWidth, 0);
//                }
//
//                /**
//                 * HumanAction rotate && mirror:双输入场景中，buffer为相机原始数据，而texture已根据预览旋转和镜像处理，所以buffer和texture方向不一致，
//                 * 根据buffer计算出的HumanAction不能直接使用，需要根据摄像头ID和摄像头方向处理后使用
//                 */
//                mSTHumanAction[0] = STHumanAction.humanActionRotateAndMirror(humanAction, mImageWidth, mImageHeight, mCameraID, mCameraProxy.getOrientation(), Accelerometer.getDirection());
//            }
//
//            if (mSTMobileEffectNative != null) {
//                if (mCurrentFilterStyle != mFilterStyle) {
//                    mCurrentFilterStyle = mFilterStyle;
//                    mSTMobileEffectNative.setBeauty(STEffectBeautyType.EFFECT_BEAUTY_FILTER, mCurrentFilterStyle);
//                }
//                if (mCurrentFilterStrength != mFilterStrength) {
//                    mCurrentFilterStrength = mFilterStrength;
//                    mSTMobileEffectNative.setBeautyStrength(STEffectBeautyType.EFFECT_BEAUTY_FILTER, mCurrentFilterStrength);
//                }
//
//                STEffectTexture stEffectTexture = new STEffectTexture(textureId, mImageWidth, mImageHeight, 0);
//                STEffectTexture stEffectTextureOut = new STEffectTexture(mBeautifyTextureId[0], mImageWidth, mImageHeight, 0);
//
//                int renderOrientation = getCurrentOrientation();
//
//                int event = mCustomEvent;
//                STEffectCustomParam customParam;
////                if (mSensorEvent != null && mSensorEvent.values != null && mSensorEvent.values.length > 0) {
////                    customParam = new STEffectCustomParam(new STQuaternion(mSensorEvent.values), mCameraID == Camera.CameraInfo.CAMERA_FACING_FRONT, event);
////                } else {
////                    customParam = new STEffectCustomParam(new STQuaternion(0f, 0f, 0f, 1f), mCameraID == Camera.CameraInfo.CAMERA_FACING_FRONT, event);
////                }
//                customParam = new STEffectCustomParam(new STQuaternion(0f, 0f, 0f, 1f), mCameraID == Camera.CameraInfo.CAMERA_FACING_FRONT, event);
//
//                STEffectRenderInParam sTEffectRenderInParam = new STEffectRenderInParam(mSTHumanAction[0], mAnimalFaceInfo[0], renderOrientation, STRotateType.ST_CLOCKWISE_ROTATE_0, false, customParam, stEffectTexture, null);
//                STEffectRenderOutParam stEffectRenderOutParam = new STEffectRenderOutParam(stEffectTextureOut, null, mSTHumanAction[0]);
//                result = mSTMobileEffectNative.render(sTEffectRenderInParam, stEffectRenderOutParam, false);
//
//                if (stEffectRenderOutParam != null && stEffectRenderOutParam.getTexture() != null) {
//                    textureId = stEffectRenderOutParam.getTexture().getId();
//                }
//
//                if (event == mCustomEvent) {
//                    mCustomEvent = 0;
//                }
//            }
//
//
//            Log.i(TAG, String.format("render cost time total: %d",
//                    System.currentTimeMillis() - mStartTime + mRotateCost + mObjectCost + mFaceAttributeCost / 20));
//        }
//
//
//        if (mNeedSave) {
//            savePicture(textureId);
//            mNeedSave = false;
//        }
//
//        mFrameCost = (int) (System.currentTimeMillis() - mStartTime + mRotateCost + mObjectCost + mFaceAttributeCost / 20);
//
//        long timer = System.currentTimeMillis();
//        mCount++;
//        if (mIsFirstCount) {
//            mCurrentTime = timer;
//            mIsFirstCount = false;
//        } else {
//            int cost = (int) (timer - mCurrentTime);
//            if (cost >= 1000) {
//                mCurrentTime = timer;
//                mFps = (((float) mCount * 1000) / cost);
//                mCount = 0;
//            }
//        }
//
//        Log.i(TAG, String.format("frame cost time total: %d", System.currentTimeMillis() - mStartTime));
//        Log.i(TAG, String.format("render fps: %f", mFps));
//
//        GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight);
//        mGLRender.onDrawFrame(textureId);
//
//        //video capturing
//        if (mVideoEncoder != null) {
//            GLES20.glFinish();
//            mVideoEncoderTexture[0] = textureId;
//            synchronized (this) {
//                if (mVideoEncoder != null) {
//                    if (mNeedResetEglContext) {
//                        mVideoEncoder.setEglContext(EGL14.eglGetCurrentContext(), mVideoEncoderTexture[0]);
//                        mNeedResetEglContext = false;
//                    }
//                    mVideoEncoder.frameAvailableSoon(mTextureEncodeMatrix);
//                }
//            }
//        }
    }

    protected void savePicture(int textureId) {
        ByteBuffer mTmpBuffer = ByteBuffer.allocate(mImageHeight * mImageWidth * 4);
        mGLRender.saveTextureToFrameBuffer(textureId, mTmpBuffer);

        mTmpBuffer.position(0);
        Message msg = Message.obtain(mHandler);
        msg.what = CameraActivity.MSG_SAVING_IMG;
        msg.obj = mTmpBuffer;
        Bundle bundle = new Bundle();
        bundle.putInt("imageWidth", mImageWidth);
        bundle.putInt("imageHeight", mImageHeight);
        msg.setData(bundle);
        msg.sendToTarget();
    }

    protected void saveImageBuffer2Picture(byte[] imageBuffer) {
        ByteBuffer mTmpBuffer = ByteBuffer.allocate(mImageHeight * mImageWidth * 4);
        mTmpBuffer.put(imageBuffer);

        Message msg = Message.obtain(mHandler);
        msg.what = CameraActivity.MSG_SAVING_IMG;
        msg.obj = mTmpBuffer;
        Bundle bundle = new Bundle();
        bundle.putInt("imageWidth", mImageWidth);
        bundle.putInt("imageHeight", mImageHeight);
        msg.setData(bundle);
        msg.sendToTarget();
    }

    protected int getCurrentOrientation() {
        int dir = Accelerometer.getDirection();
        int orientation = dir - 1;
        if (orientation < 0) {
            orientation = dir ^ 3;
        }

        return orientation;
    }

    protected SurfaceTexture.OnFrameAvailableListener mOnFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() {

        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            if (!mCameraChanging) {
                mGlSurfaceView.requestRender();
            }
        }
    };

    /**
     * camera设备startPreview
     */
    protected void setUpCamera() {
        // 初始化Camera设备预览需要的显示区域(mSurfaceTexture)
        if (mTextureId == OpenGLUtils.NO_TEXTURE) {
            mTextureId = OpenGLUtils.getExternalOESTextureID();
            mSurfaceTexture = new SurfaceTexture(mTextureId);
//            mSurfaceTexture.setOnFrameAvailableListener(mOnFrameAvailableListener);
        }

        String size = mSupportedPreviewSizes.get(mCurrentPreview);
        int index = size.indexOf('x');
        mImageHeight = Integer.parseInt(size.substring(0, index));
        mImageWidth = Integer.parseInt(size.substring(index + 1));

        if (mIsPaused)
            return;

        while (!mSetPreViewSizeSucceed) {
            try {
                mCameraProxy.setPreviewSize(mImageHeight, mImageWidth);
                mSetPreViewSizeSucceed = true;
            } catch (Exception e) {
                mSetPreViewSizeSucceed = false;
            }

            try {
                Thread.sleep(10);
            } catch (Exception e) {

            }
        }

        boolean flipHorizontal = mCameraProxy.isFlipHorizontal();
        boolean flipVertical = mCameraProxy.isFlipVertical();
        mGLRender.adjustTextureBuffer(mCameraProxy.getOrientation(), flipVertical, flipHorizontal);

        if (mIsPaused)
            return;
        mCameraProxy.startPreview(mSurfaceTexture, mPreviewCallback);
//        mCameraProxy.startFaceDetection();
    }

    public void changeSticker(String sticker) {
        mChangeStickerManagerHandler.removeMessages(MESSAGE_NEED_CHANGE_STICKER);
        Message msg = mChangeStickerManagerHandler.obtainMessage(MESSAGE_NEED_CHANGE_STICKER);
        msg.obj = sticker;

        mChangeStickerManagerHandler.sendMessage(msg);
    }

    public void addSticker(String addSticker) {
        if (mCurrentStickerMaps.containsValue(addSticker)) {
            return;
        }
        mCurrentSticker = addSticker;
        Message msg = mChangeStickerManagerHandler.obtainMessage(MESSAGE_NEED_ADD_STICKER);
        msg.obj = addSticker;
        mChangeStickerManagerHandler.sendMessage(msg);
    }

    public void removeSticker(String path) {
        removeSticker(CollectionUtils.getKey(mCurrentStickerMaps, path));
    }

    public void removeSticker(int packageId) {
        mChangeStickerManagerHandler.removeMessages(MESSAGE_NEED_REMOVE_STICKER);
        Message msg = mChangeStickerManagerHandler.obtainMessage(MESSAGE_NEED_REMOVE_STICKER);
        msg.obj = packageId;

        mChangeStickerManagerHandler.sendMessage(msg);
    }

    public void removeAllStickers() {
        mChangeStickerManagerHandler.removeMessages(MESSAGE_NEED_REMOVEALL_STICKERS);
        Message msg = mChangeStickerManagerHandler.obtainMessage(MESSAGE_NEED_REMOVEALL_STICKERS);

        mChangeStickerManagerHandler.sendMessage(msg);
    }

    /**
     * 非常重要!!! 设置滤镜函数
     * @param filterType
     * @param filterName
     * @param modelPath
     */
    public void setFilterStyle(String filterType, String filterName, String modelPath) {
        //mFilterStyle = modelPath;
        EffectInfoDataHelper.getInstance().filterType = filterType;
        EffectInfoDataHelper.getInstance().setFilterName(filterName);
        mSTMobileEffectNative.setBeauty(STEffectBeautyType.EFFECT_BEAUTY_FILTER, modelPath);
        mCurrentFilterStyle = modelPath;
        EffectInfoDataHelper.getInstance().filterStyle = modelPath;
    }

    public void setFilterStrength(float strength) {
        Log.d(TAG, "setFilterStrength() called with: strength = [" + strength + "]");
        mFilterStrength = strength;
        EffectInfoDataHelper.getInstance().filterStrength = strength;
    }

    public long getStickerTriggerAction() {
        return mSTMobileEffectNative.getHumanActionDetectConfig();
    }

    public void onResume() {
        Log.i(TAG, "onResume");

        if (mCameraProxy.getCamera() == null) {
            if (mCameraProxy.getNumberOfCameras() == 1) {
                mCameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
            }
            mCameraProxy.openCamera(mCameraID);
            mSupportedPreviewSizes = mCameraProxy.getSupportedPreviewSize(new String[]{"1280x720", "640x480", "1920x1080"});
        }
        mIsPaused = false;
        mSetPreViewSizeSucceed = false;
        mNeedResetEglContext = true;

        if (mGLRender != null)
            mGLRender = new STGLRender();

        mGlSurfaceView.onResume();
        mGlSurfaceView.forceLayout();
        //mGlSurfaceView.requestRender();
    }

    public void onPause() {
        Log.i(TAG, "onPause");
        mSetPreViewSizeSucceed = false;
        mIsPaused = true;
        mImageData = null;
        mCameraProxy.releaseCamera();
        Log.d(TAG, "Release camera");

        mGlSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mNv21ImageData = null;
                deleteTextures();
                if (mSurfaceTexture != null) {
                    mSurfaceTexture.release();
                }
                mGLRender.destroyFrameBuffers();
            }
        });

        mGlSurfaceView.onPause();
    }

    public void onDestroy() {
        //必须释放非opengGL句柄资源,负责内存泄漏
        synchronized (mHumanActionHandleLock) {
            mSTHumanActionNative.destroyInstance();
        }
        mStAnimalNative.destroyInstance();
        mSTFaceAttributeNative.destroyInstance();
        mSTMobileObjectTrackNative.destroyInstance();

        if (mEGLContextHelper != null) {
            mEGLContextHelper.eglMakeCurrent();
            mSTMobileEffectNative.destroyInstance();
            mEGLContextHelper.eglMakeNoCurrent();

            mEGLContextHelper.release();
            mEGLContextHelper = null;
        }

        if (mCurrentStickerMaps != null) {
            mCurrentStickerMaps.clear();
            mCurrentStickerMaps = null;
        }
    }

    /**
     * 释放纹理资源
     */
    protected void deleteTextures() {
        Log.i(TAG, "delete textures");
        deleteCameraPreviewTexture();
        deleteInternalTextures();
    }

    // must in opengl thread
    protected void deleteCameraPreviewTexture() {
        if (mTextureId != OpenGLUtils.NO_TEXTURE) {
            GLES20.glDeleteTextures(1, new int[]{
                    mTextureId
            }, 0);
        }
        mTextureId = OpenGLUtils.NO_TEXTURE;
    }

    protected void deleteInternalTextures() {
        if (mBeautifyTextureId != null) {
            GLES20.glDeleteTextures(1, mBeautifyTextureId, 0);
            mBeautifyTextureId = null;
        }

        if (mMakeupTextureId != null) {
            GLES20.glDeleteTextures(1, mMakeupTextureId, 0);
            mMakeupTextureId = null;
        }

        if (mTextureOutId != null) {
            GLES20.glDeleteTextures(1, mTextureOutId, 0);
            mTextureOutId = null;
        }

        if (mFilterTextureOutId != null) {
            GLES20.glDeleteTextures(1, mFilterTextureOutId, 0);
            mFilterTextureOutId = null;
        }

        if (mVideoEncoderTexture != null) {
            GLES20.glDeleteTextures(1, mVideoEncoderTexture, 0);
            mVideoEncoderTexture = null;
        }
    }

    public void switchCamera() {
        if (Camera.getNumberOfCameras() == 1
                || mCameraChanging) {
            return;
        }


        final int cameraID = 1 - mCameraID;
        mCameraChanging = true;
        mCameraProxy.openCamera(cameraID);

        if (mCameraProxy.cameraOpenFailed()) {
            return;
        }

        mSetPreViewSizeSucceed = false;

        if (mNeedObject) {
            resetIndexRect();
        } else {
            Message msg = mHandler.obtainMessage(CameraActivity.MSG_CLEAR_OBJECT);
            mHandler.sendMessage(msg);
        }

        mGlSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                deleteTextures();
                if (mCameraProxy.getCamera() != null) {
                    setUpCamera();
                }
                mCameraChanging = false;
                mCameraID = cameraID;
            }
        });
        //fix 双输入camera changing时，贴纸和画点mirrow显示
        //mGlSurfaceView.requestRender();
    }

    public int getCameraID() {
        return mCameraID;
    }

    public void changePreviewSize(int currentPreview) {
        if (mCameraProxy.getCamera() == null || mCameraChanging
                || mIsPaused) {
            return;
        }

        mCurrentPreview = currentPreview;
        mSetPreViewSizeSucceed = false;
        mIsChangingPreviewSize = true;

        mCameraChanging = true;
        mCameraProxy.stopPreview();
        mGlSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                deleteTextures();
                if (mCameraProxy.getCamera() != null) {
                    setUpCamera();
                }

                mGLRender.init(mImageWidth, mImageHeight);
                if (DEBUG) {
                    mGLRender.initDrawPoints();
                }

                if (mNeedObject) {
                    resetIndexRect();
                }

                mGLRender.calculateVertexBuffer(mSurfaceWidth, mSurfaceHeight, mImageWidth, mImageHeight);
                if (mListener != null) {
                    mListener.onChangePreviewSize(mImageHeight, mImageWidth);
                }

                mCameraChanging = false;
                mIsChangingPreviewSize = false;
                //mGlSurfaceView.requestRender();
                Log.d(TAG, "exit  change Preview size queue event");
            }
        });
    }

    public void enableObject(boolean enabled) {
        mNeedObject = enabled;

        if (mNeedObject) {
            resetIndexRect();
        }
    }

    public void setIndexRect(int x, int y, boolean needRect) {
        mIndexRect = new Rect(x, y, x + mScreenIndexRectWidth, y + mScreenIndexRectWidth);
        mNeedShowRect = needRect;
    }

    public Rect getIndexRect() {
        return mIndexRect;
    }

    public void setObjectTrackRect() {
        mNeedSetObjectTarget = true;
        mIsObjectTracking = false;
        mTargetRect = STUtils.adjustToImageRectMin(getIndexRect(), mSurfaceWidth, mSurfaceHeight, mImageWidth, mImageHeight);
    }

    public void disableObjectTracking() {
        mIsObjectTracking = false;
    }

    public void resetObjectTrack() {
        mSTMobileObjectTrackNative.reset();
    }

    public void resetIndexRect() {
        if (mImageWidth == 0) {
            return;
        }

        mScreenIndexRectWidth = mSurfaceWidth / 4;

        mIndexRect.left = (mSurfaceWidth - mScreenIndexRectWidth) / 2;
        mIndexRect.top = (mSurfaceHeight - mScreenIndexRectWidth) / 2;
        mIndexRect.right = mIndexRect.left + mScreenIndexRectWidth;
        mIndexRect.bottom = mIndexRect.top + mScreenIndexRectWidth;

        mNeedShowRect = true;
        mNeedSetObjectTarget = false;
        mIsObjectTracking = false;
    }

    /**
     * 用于humanActionDetect接口。根据传感器方向计算出在不同设备朝向时，人脸在buffer中的朝向
     *
     * @return 人脸在buffer中的朝向
     */
    protected int getHumanActionOrientation() {
        boolean frontCamera = (mCameraID == Camera.CameraInfo.CAMERA_FACING_FRONT);

        //获取重力传感器返回的方向
        int orientation = Accelerometer.getDirection();

        //在使用后置摄像头，且传感器方向为0或2时，后置摄像头与前置orentation相反
        if (!frontCamera && orientation == STRotateType.ST_CLOCKWISE_ROTATE_0) {
            orientation = STRotateType.ST_CLOCKWISE_ROTATE_180;
        } else if (!frontCamera && orientation == STRotateType.ST_CLOCKWISE_ROTATE_180) {
            orientation = STRotateType.ST_CLOCKWISE_ROTATE_0;
        }

        // 请注意前置摄像头与后置摄像头旋转定义不同 && 不同手机摄像头旋转定义不同
        if (((mCameraProxy.getOrientation() == 270 && (orientation & STRotateType.ST_CLOCKWISE_ROTATE_90) == STRotateType.ST_CLOCKWISE_ROTATE_90) ||
                (mCameraProxy.getOrientation() == 90 && (orientation & STRotateType.ST_CLOCKWISE_ROTATE_90) == STRotateType.ST_CLOCKWISE_ROTATE_0)))
            orientation = (orientation ^ STRotateType.ST_CLOCKWISE_ROTATE_180);
        return orientation;
    }

    public int getPreviewWidth() {
        return mImageWidth;
    }

    public int getPreviewHeight() {
        return mImageHeight;
    }

    public void setVideoEncoder(final MediaVideoEncoder encoder) {

        mGlSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    if (encoder != null && mVideoEncoderTexture != null) {
                        encoder.setEglContext(EGL14.eglGetCurrentContext(), mVideoEncoderTexture[0]);
                    }
                    mVideoEncoder = encoder;
                }
            }
        });
    }

    public int getFrameCost() {
        return mFrameCost;
    }

    public float getFpsInfo() {
        return (float) (Math.round(mFps * 10)) / 10;
    }

    public boolean isChangingPreviewSize() {
        return mIsChangingPreviewSize;
    }

    public void addSubModelByName(String modelName) {
        Message msg = mSubModelsManagerHandler.obtainMessage(MESSAGE_ADD_SUB_MODEL);
        msg.obj = modelName;

        mSubModelsManagerHandler.sendMessage(msg);
    }

    public void removeSubModelByConfig(int Config) {
        Message msg = mSubModelsManagerHandler.obtainMessage(MESSAGE_REMOVE_SUB_MODEL);
        msg.obj = Config;
        mSubModelsManagerHandler.sendMessage(msg);
    }

    public long getHandActionInfo() {
        return mHandAction;
    }

    public long getBodyActionInfo() {
        return mBodyAction;
    }

    public boolean[] getFaceExpressionInfo() {
        return mFaceExpressionResult;
    }


    public void changeCustomEvent() {
        mCustomEvent = STCustomEvent.ST_CUSTOM_EVENT_1 | STCustomEvent.ST_CUSTOM_EVENT_2;
    }

    public void setSensorEvent(SensorEvent event) {
        mSensorEvent = event;
    }

    public void setMeteringArea(float touchX, float touchY) {
        float[] touchPosition = new float[2];
        STUtils.calculateRotatetouchPoint(touchX, touchY, mSurfaceWidth, mSurfaceHeight, mCameraID, mCameraProxy.getOrientation(), touchPosition);
        Rect rect = STUtils.calculateArea(touchPosition, mSurfaceWidth, mSurfaceHeight, 100);
        mCameraProxy.setMeteringArea(rect);
    }

    public void handleZoom(boolean isZoom) {
        mCameraProxy.handleZoom(isZoom);
    }

    public void setExposureCompensation(int progress) {
        mCameraProxy.setExposureCompensation(progress);
    }

    public void setStrengthForType(int type, float strength) {
//        mMakeupStrength[type] = strength;
//        EffectInfoDataHelper.getInstance().currentMakeupStrength = mMakeupStrength;
//        if (convertMakeupTypeToNewType(type) == STEffectBeautyType.EFFECT_BEAUTY_HAIR_DYE) {
//            strength = strength * MAKEUP_HAIRDYE_STRENGTH_RATIO;
//        }
//        int ret = mSTMobileEffectNative.setBeautyStrength(convertMakeupTypeToNewType(type), strength);
    }

    public String mMakeupPath;

    public void setMakeupForTypeFromAssets(int type, String typePath) {
        mMakeupPath = typePath;
//        int ret = mSTMobileEffectNative.setBeautyFromAssetsFile(402, typePath, mContext.getAssets());
        updateHumanActionDetectConfig();
    }

    public void removeMakeupByType(int type) {
//        int ret = mSTMobileEffectNative.setBeauty(convertMakeupTypeToNewType(type), null);
//        EffectInfoDataHelper.getInstance().setEmptyMakeup(type);
//        try {
//            if (ret == 0) {
//                mCurrentMakeup[type] = null;
//            }
//        } catch (ArrayIndexOutOfBoundsException e) {
//            e.printStackTrace();
//        }
//
//        updateHumanActionDetectConfig();
    }

    public void setMakeupForType(int type, String typePath) {
//        try {
//            mCurrentMakeup[type] = typePath;
//            EffectInfoDataHelper.getInstance().setCurrentMakeup(mCurrentMakeup);
//            mSTMobileEffectNative.setBeauty(convertMakeupTypeToNewType(type), typePath);
//            updateHumanActionDetectConfig();
//        } catch (ArrayIndexOutOfBoundsException e) {
//            e.printStackTrace();
//        }
    }

    private void setBeautyParamsFromPackage(STEffectBeautyInfo[] beautyInfos) {
        if (null == beautyInfos) return;
        boolean needResetBeautyParamsBase = false;
        boolean needResetBeautyParamsProfessional = false;
        boolean needResetBeautyParamsMicro = false;
        boolean needResetBeautyParamsAdjust = false;

        boolean needResetMakeup = false;
        boolean needResetFilter = false;

        for (int i = 0; i < beautyInfos.length; i++) {
            if (beautyInfos[i] == null) continue;
            if ((beautyInfos[i].getType() / 100) == 1) {
                needResetBeautyParamsBase = false;
            } else if ((beautyInfos[i].getType() / 100) == 2) {
                needResetBeautyParamsProfessional = false;
            } else if ((beautyInfos[i].getType() / 100) == 3) {
                needResetBeautyParamsMicro = false;
            } else if ((beautyInfos[i].getType() / 100) == 6) {
                needResetBeautyParamsAdjust = false;
            } else if ((beautyInfos[i].getType()/* / 100*/) == 410) {
                needResetMakeup = true;
                mHandler.sendEmptyMessage(CameraActivity.MSG_STICKER_HAS_MAKEUP);
            } else if((beautyInfos[i].getType() / 100) == 4){
                int type = STUtils.convertMakeupTypeToNewType2(beautyInfos[i].getType());
                Message message = Message.obtain();
                message.what = CameraActivity.MSG_STICKER_HAS_MAKEUP;
                Bundle bundle = new Bundle();
                bundle.putInt("makeupType", type);
                message.setData(bundle);
                mHandler.sendMessage(message);
                //mHandler.sendEmptyMessage(CameraActivity.MSG_STICKER_HAS_MAKEUP);
            } else if ((int) (beautyInfos[i].getType() / 100) == 5) {
                needResetFilter = true;
            }
        }

        mNeedClearMakeupView = needResetMakeup;
        mNeedClearFilterView = needResetFilter;

        if (needResetBeautyParamsBase) {
            mBeautifyParamsTypeBase = new float[mBeautifyParamsTypeBase.length];
        }
        if (needResetBeautyParamsProfessional) {
            mBeautifyParamsTypeProfessional = new float[mBeautifyParamsTypeProfessional.length];
        }
        if (needResetBeautyParamsMicro) {
            mBeautifyParamsTypeMicro = new float[mBeautifyParamsTypeMicro.length];
        }
        if (needResetBeautyParamsAdjust) {
            mBeautifyParamsTypeAdjust = new float[mBeautifyParamsTypeAdjust.length];
        }

        for (STEffectBeautyInfo beautyInfo : beautyInfos) {
            if ((beautyInfo.getType() / 100) == 1) {
                //mBeautifyParamsTypeBase[beautyInfo.getType() % 100 - 1] = beautyInfo.getStrength();
            } else if ((beautyInfo.getType() / 100) == 2) {
                mBeautifyParamsTypeProfessional[beautyInfo.getType() % 100 - 1] = beautyInfo.getStrength();
            } else if ((beautyInfo.getType() / 100) == 3) {
                mBeautifyParamsTypeMicro[beautyInfo.getType() % 100 - 1] = beautyInfo.getStrength();
            } else if ((beautyInfo.getType() / 100) == 6) {
                mBeautifyParamsTypeAdjust[beautyInfo.getType() % 100 - 1] = beautyInfo.getStrength();
            }
        }

        if (STUtils.containsBaseParams(beautyInfos)) {
            mBeautifyParamsTypeBase = STUtils.getBeautifyParamsTypeBase(mBeautifyParamsTypeBase, beautyInfos);
        }
    }

    public boolean overlappedBeautyCountChanged() {
        int overlappedBeautyCount = mSTMobileEffectNative.getOverlappedBeautyCount();
        return mLastBeautyOverlapCount != overlappedBeautyCount;
    }

    public boolean stickerMapIsEmpty() {
        boolean b = mCurrentStickerMaps == null || mCurrentStickerMaps.size() == 0;
        if (b) mLastBeautyOverlapCount = -1;
        return b;
    }
}

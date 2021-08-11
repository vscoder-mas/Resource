package sensetime.senseme.com.effects.encoder.mediacodec;

import android.content.Context;
import android.opengl.GLES20;

import com.sensetime.stmobile.STCommon;
import com.sensetime.stmobile.STEffectBeautyType;
import com.sensetime.stmobile.STHumanActionParamsType;
import com.sensetime.stmobile.STMobileAnimalNative;
import com.sensetime.stmobile.STMobileEffectNative;
import com.sensetime.stmobile.STMobileHumanActionNative;
import com.sensetime.stmobile.STRotateType;
import com.sensetime.stmobile.model.STAnimalFace;
import com.sensetime.stmobile.model.STAnimalFaceInfo;
import com.sensetime.stmobile.model.STEffectCustomParam;
import com.sensetime.stmobile.model.STEffectRenderInParam;
import com.sensetime.stmobile.model.STEffectRenderOutParam;
import com.sensetime.stmobile.model.STEffectTexture;
import com.sensetime.stmobile.model.STHumanAction;
import com.sensetime.stmobile.model.STQuaternion;

import sensetime.senseme.com.effects.display.glutils.STGLRender;
import sensetime.senseme.com.effects.utils.Constants;
import sensetime.senseme.com.effects.utils.FileUtils;
import sensetime.senseme.com.effects.utils.LogUtils;
import sensetime.senseme.com.effects.display.glutils.GlUtil;

public class DetectAndRender {

    private String TAG = "DetectAndRender";

    private STMobileHumanActionNative mSTMobileHumanActionNative;
    private int mHumanActionConfigCreate = STMobileHumanActionNative.ST_MOBILE_HUMAN_ACTION_DEFAULT_CONFIG_VIDEO_SINGLE_THREAD;
    private long mHumanActionConfigDetect = STMobileHumanActionNative.ST_MOBILE_FACE_DETECT;
    private STMobileAnimalNative mSTMobileAnimalNative = new STMobileAnimalNative();

    private STGLRender mSTGLRender = new STGLRender();

    private Context mContext;
    protected boolean mNeedAvatar = true;

    float[] normals;

    public DetectAndRender(Context context){
        mContext = context;
    }
    public void initHumanAction(){
        if(mSTMobileHumanActionNative == null){
            mSTMobileHumanActionNative = new STMobileHumanActionNative();
        }
        int result = mSTMobileHumanActionNative.createInstanceFromAssetFile(FileUtils.getActionModelName(), mHumanActionConfigCreate, mContext.getAssets());
        LogUtils.i(TAG, "the result for createInstance for human_action is %d", result);

        if (result == 0) {
            result = mSTMobileHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_HAND, mContext.getAssets());
            LogUtils.i(TAG, "add hand model result: %d", result);
            result = mSTMobileHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_SEGMENT, mContext.getAssets());
            LogUtils.i(TAG, "add figure segment model result: %d", result);

            mSTMobileHumanActionNative.setParam(STHumanActionParamsType.ST_HUMAN_ACTION_PARAM_BACKGROUND_BLUR_STRENGTH, 0.35f);

            //240
            result = mSTMobileHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_FACE_EXTRA, mContext.getAssets());
            LogUtils.i(TAG, "add face extra model result: %d", result);
            //eye
            result = mSTMobileHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_EYEBALL_CONTOUR, mContext.getAssets());
            LogUtils.i(TAG, "add eyeball contour model result: %d", result);
            result = mSTMobileHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_HAIR, mContext.getAssets());
            LogUtils.i(TAG,"add hair model result: %d", result );
            result = mSTMobileHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_LIPS_PARSING, mContext.getAssets());
            LogUtils.i(TAG,"add lips parsing model result: %d", result );

            result = mSTMobileHumanActionNative.addSubModelFromAssetFile(FileUtils.HEAD_SEGMENT_MODEL_NAME, mContext.getAssets());
            LogUtils.i(TAG,"add head segment model result: %d", result );

            result = mSTMobileHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_AVATAR_HELP, mContext.getAssets());
            LogUtils.i(TAG, "add avatar help model result: %d", result);

            if (mNeedAvatar) {
                int ret = mSTMobileHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_AVATAR_HELP, mContext.getAssets());
                LogUtils.i(TAG, "add avatar help model result: %d", ret);

//                            ret = mSTHumanActionNative.addSubModelFromAssetFile(FileUtils.MODEL_NAME_TONGUE, mContext.getAssets());
//                            LogUtils.i(TAG,"add tongue model result: %d", ret );
            }
        }

        initAnimalDetect();
    }

    public void initAnimalDetect(){
        int result = mSTMobileAnimalNative.createInstance(FileUtils.getFilePath(mContext, FileUtils.MODEL_NAME_CATFACE_CORE), STCommon.ST_MOBILE_TRACKING_MULTI_THREAD);
        LogUtils.i(TAG, "create animal handle result: %d", result);
    }

    private boolean mNeedBeautify, mNeedFilter, mNeedSticker, mNeedMakeup;

    private STMobileEffectNative mSTMobileEffectNative = new STMobileEffectNative();
    private int mCustomEvent = 0;
    private int[] mBeautifyTextureId, mMakeupTextureId, mFilterTextureOutId, mTextureStickerId;
    protected STAnimalFace[] mAnimalFace;
    protected int animalFaceLlength = 0;
    protected boolean needAnimalDetect;
    protected STHumanAction mHumanActionBeautyOutput = new STHumanAction();
    public int humanActionDetectAndRender(byte[] imageBuffer, int imageFormat, int orientation, int width, int height, int textureId){
        STHumanAction humanAction = mSTMobileHumanActionNative.humanActionDetect(imageBuffer, imageFormat,
                mHumanActionConfigDetect, orientation, width, height);

        long catDetectStartTime = System.currentTimeMillis();
        mAnimalFace = mSTMobileAnimalNative.animalDetect(imageBuffer, imageFormat, orientation, width, height);
        LogUtils.i(TAG, "cat face detect cost time: %d", System.currentTimeMillis() - catDetectStartTime);
        animalFaceLlength = mAnimalFace == null ? 0 : mAnimalFace.length;


        if (mBeautifyTextureId == null) {
            mBeautifyTextureId = new int[1];
            GlUtil.initEffectTexture(width, height, mBeautifyTextureId, GLES20.GL_TEXTURE_2D);
        }

        if (mMakeupTextureId == null) {
            mMakeupTextureId = new int[1];
            GlUtil.initEffectTexture(width, height, mMakeupTextureId, GLES20.GL_TEXTURE_2D);
        }

        if (mTextureStickerId == null) {
            mTextureStickerId = new int[1];
            GlUtil.initEffectTexture(width, height, mTextureStickerId, GLES20.GL_TEXTURE_2D);
        }

        if(mFilterTextureOutId == null){
            mFilterTextureOutId = new int[1];
            GlUtil.initEffectTexture(width, height, mFilterTextureOutId, GLES20.GL_TEXTURE_2D);
        }

        if (mNeedSticker) {
            if (mCurrentStickerPath != mStickerPath) {
                mCurrentStickerPath = mStickerPath;
                mSTMobileEffectNative.changePackage(mStickerPath);
            }
        }

        STEffectTexture stEffectTexture = new STEffectTexture(textureId, width, height, 0);
        STEffectTexture stEffectTextureOut = new STEffectTexture(mBeautifyTextureId[0], width, height, 0);
        int event = mCustomEvent;
        STEffectCustomParam customParam = new STEffectCustomParam(new STQuaternion(0f,0f,0f,1f), false, event);
        STAnimalFaceInfo animalFaceInfo = null;
        if(mAnimalFace != null && mAnimalFace.length >0){
            animalFaceInfo = new STAnimalFaceInfo(mAnimalFace, mAnimalFace.length);
        }
        STEffectRenderInParam sTEffectRenderInParam = new STEffectRenderInParam(humanAction, animalFaceInfo, orientation, STRotateType.ST_CLOCKWISE_ROTATE_0, false, customParam, stEffectTexture, null);
        STEffectRenderOutParam stEffectRenderOutParam = new STEffectRenderOutParam(stEffectTextureOut, null, humanAction);
        int result = mSTMobileEffectNative.render(sTEffectRenderInParam, stEffectRenderOutParam, false);

        if(stEffectRenderOutParam != null && stEffectRenderOutParam.getTexture() != null){
            textureId = stEffectRenderOutParam.getTexture().getId();
        }

        return textureId;
    }

    public void initRender(int width, int height){
        if(mSTGLRender == null){
            mSTGLRender = new STGLRender();
        }

        mSTGLRender.init(width, height);
        initEffect();
    }

    private void initEffect() {
        int ret = mSTMobileEffectNative.createInstance(mContext, 1);
    }

    public void releaseRender() {
        if (mSTGLRender != null) {
            mSTGLRender.destroy();
            mSTGLRender.destroyFrameBuffers();
            mSTGLRender = null;
        }

        if(mSTMobileEffectNative != null){
            mSTMobileEffectNative.destroyInstance();
            mSTMobileEffectNative = null;
        }

        if (mFilterTextureOutId != null) {
            GLES20.glDeleteTextures(1, mFilterTextureOutId, 0);
        }
        mFilterTextureOutId = null;

        if (mBeautifyTextureId != null) {
            GLES20.glDeleteTextures(1, mBeautifyTextureId, 0);
        }
        mBeautifyTextureId = null;

        if (mMakeupTextureId != null) {
            GLES20.glDeleteTextures(1, mMakeupTextureId, 0);
        }
        mMakeupTextureId = null;

        if (mTextureStickerId != null) {
            GLES20.glDeleteTextures(1, mTextureStickerId, 0);
        }
        mTextureStickerId = null;
    }

    public void releaseHumanAction(){
        if(mSTMobileHumanActionNative != null){
            mSTMobileHumanActionNative.destroyInstance();
            mSTMobileHumanActionNative = null;
        }

        if(mSTMobileAnimalNative != null){
            mSTMobileAnimalNative.destroyInstance();
            mSTMobileAnimalNative = null;
        }
    }

    public STGLRender getSTGLRender(){
        return mSTGLRender;
    }

    public void setDetectConfig(Long config){
        mHumanActionConfigDetect = config;
    }

    public void setRenderOptions(boolean needBeauty, boolean needMakeup, boolean needSticker, boolean needFilter){
        mNeedBeautify = needBeauty;
        mNeedMakeup = needMakeup;
        mNeedSticker = needSticker;
        mNeedFilter = needFilter;
    }

    private String mStickerPath;
    private String mCurrentStickerPath;
    public void setCurrentStickerPath(String path){
        mStickerPath = path;
    }

    private float[] mBaseBeautyParams = new float[Constants.mNewBeautifyParamsTypeBase.length];
    private float[] mProfessionalBeautyParams = new float[Constants.mNewBeautifyParamsTypeProfessional.length];
    private float[] mMicroBeautyParams = new float[Constants.mNewBeautifyParamsTypeMicro.length];
    private float[] mAdjustBeautyParams = new float[Constants.mNewBeautifyParamsTypeAdjust.length];
    public void setCurrentBeautyParams(float[] paramsBase, float[] paramsProfessional, float[] paramsMicro, float[] paramsAdjust){
        mBaseBeautyParams = paramsBase;
        mProfessionalBeautyParams = paramsProfessional;
        mMicroBeautyParams = paramsMicro;
        mAdjustBeautyParams = paramsAdjust;

        setBeautyParamsForHandle();
    }

    private String[] mMakeupPaths = new String[Constants.MAKEUP_TYPE_COUNT];
    private float[] mMakeupStrengths = new float[Constants.MAKEUP_TYPE_COUNT];
    public void setCurrentMakeups(String[] paths, float[] strengths){
        for (int i = 0; i < paths.length; i++){
            mMakeupPaths[i] = paths[i];
            mMakeupStrengths[i] = strengths[i];

            if(mMakeupPaths[i] != null){
                int ret = mSTMobileEffectNative.setBeauty(convertMakeupTypeToNewType(i), mMakeupPaths[i]);
            }

            mSTMobileEffectNative.setBeautyStrength(convertMakeupTypeToNewType(i), mMakeupStrengths[i]);
        }
    }

    public int convertMakeupTypeToNewType(int type){
        int newType = 0;

        if(type == 1){
            newType = 406;
        } else if(type == 2){
            newType = 403;
        }else if(type == 3){
            newType = 402;
        }else if(type == 4){
            newType = 404;
        }else if(type == 5){
            newType = 405;
        }else if(type == 6){
            newType = 407;
        }else if(type == 7){
            newType = 408;
        }else if(type == 8){
            newType = 409;
        }else if(type == 9){
            newType = 401;
        }

        return newType;
    }

    private String mFilterPath;
    private float mFilterStrength;
    public void setCurrentFilter(String path, float strength){
        mFilterPath = path;
        mFilterStrength = strength;

        mSTMobileEffectNative.setBeauty(STEffectBeautyType.EFFECT_BEAUTY_FILTER, mFilterPath);
        mSTMobileEffectNative.setBeautyStrength(STEffectBeautyType.EFFECT_BEAUTY_FILTER, mFilterStrength);
    }

    private boolean mNeedResetBeauty = false;
    private void setBeautyParamsForHandle(){
        for(int i = 0; i < mBaseBeautyParams.length; i++){
            mSTMobileEffectNative.setBeautyStrength(STEffectBeautyType.EFFECT_BEAUTY_BASE_WHITTEN + i, mBaseBeautyParams[i]);
        }
        for(int i = 0; i < mProfessionalBeautyParams.length; i++){
            mSTMobileEffectNative.setBeautyStrength(STEffectBeautyType.EFFECT_BEAUTY_RESHAPE_SHRINK_FACE + i, mProfessionalBeautyParams[i]);
        }
        for(int i = 0; i < mMicroBeautyParams.length; i++){
            mSTMobileEffectNative.setBeautyStrength(STEffectBeautyType.EFFECT_BEAUTY_PLASTIC_THINNER_HEAD + i, mMicroBeautyParams[i]);
        }
        for(int i = 0; i < mAdjustBeautyParams.length; i++){
            mSTMobileEffectNative.setBeautyStrength(STEffectBeautyType.EFFECT_BEAUTY_TONE_CONTRAST + i, mAdjustBeautyParams[i]);
        }
    }
}

package sensetime.senseme.com.effects.display;

import android.opengl.GLSurfaceView;
import android.util.Log;
import android.util.SparseArray;

import com.sensetime.stmobile.STEffectBeautyType;
import com.sensetime.stmobile.STMobileAnimalNative;
import com.sensetime.stmobile.STMobileEffectNative;
import com.sensetime.stmobile.STMobileFaceAttributeNative;
import com.sensetime.stmobile.STMobileHumanActionNative;
import com.sensetime.stmobile.STMobileObjectTrackNative;
import com.sensetime.stmobile.model.STHumanAction;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

import sensetime.senseme.com.effects.SenseMeApplication;
import sensetime.senseme.com.effects.display.glutils.EGLContextHelper;
import sensetime.senseme.com.effects.utils.Constants;
import sensetime.senseme.com.effects.utils.EffectInfoDataHelper;
import sensetime.senseme.com.effects.utils.STUtils;

public class BaseDisplay implements GLSurfaceView.EGLContextFactory{

    private String TAG = BaseDisplay.class.getSimpleName();
    protected GLSurfaceView mGlSurfaceView;
    protected EGLContext mEglContext;
    protected EGLContextHelper mEGLContextHelper = new EGLContextHelper();
//    protected STMobileEffectNative mSTMobileEffectNative = new STMobileEffectNative();
//    protected STMobileHumanActionNative mSTHumanActionNative = new STMobileHumanActionNative();
    protected STMobileEffectNative mSTMobileEffectNative;
    protected STMobileHumanActionNative mSTHumanActionNative;
    protected long mDetectConfig = 0;
    protected boolean mNeedAnimalDetect = false;

    protected STHumanAction mHumanActionBeautyOutput = new STHumanAction();
    protected STMobileAnimalNative mStAnimalNative = new STMobileAnimalNative();
    protected STMobileFaceAttributeNative mSTFaceAttributeNative = new STMobileFaceAttributeNative();
    protected STMobileObjectTrackNative mSTMobileObjectTrackNative = new STMobileObjectTrackNative();

    public float[] mBeautifyParamsTypeBase = Constants.mNewBeautifyParamsTypeBase.clone();
    public float[] mBeautifyParamsTypeProfessional = Constants.mNewBeautifyParamsTypeProfessional.clone();
    public float[] mBeautifyParamsTypeMicro = Constants.mNewBeautifyParamsTypeMicro.clone();
    public float[] mBeautifyParamsTypeAdjust = Constants.mNewBeautifyParamsTypeAdjust.clone();
    protected float mFilterStrength = 0.80f;

    protected boolean mNeedClearMakeupView = false;
    protected boolean mNeedClearFilterView = false;

    public static final float MAKEUP_HAIRDYE_STRENGTH_RATIO = 0.22f;

    private static final SparseArray<Integer> makeUpAliasMap = new SparseArray<Integer>() {
        {
            put(101, STEffectBeautyType.EFFECT_BEAUTY_BASE_WHITTEN);
            put(102, STEffectBeautyType.EFFECT_BEAUTY_BASE_WHITTEN);
            put(103, STEffectBeautyType.EFFECT_BEAUTY_BASE_WHITTEN);
            put(104, STEffectBeautyType.EFFECT_BEAUTY_BASE_REDDEN);
            put(105, STEffectBeautyType.EFFECT_BEAUTY_BASE_FACE_SMOOTH);
            put(106, STEffectBeautyType.EFFECT_BEAUTY_BASE_FACE_SMOOTH);
        }
    };

    private static final SparseArray<Integer> makeUpParamAliasMap = new SparseArray<Integer>() {
        {
            put(101, 0);
            put(102, 0);
            put(103, 2);
            put(105, 1);
            put(106, 2);
        }
    };

    public BaseDisplay(GLSurfaceView glSurfaceView) {
        initEglContext();
        if(mEGLContextHelper != null){
            mEGLContextHelper.eglMakeCurrent();
            mEglContext = mEGLContextHelper.getEGLContext();
            mEGLContextHelper.eglMakeNoCurrent();
        }
        this.mGlSurfaceView = glSurfaceView;
        glSurfaceView.setEGLContextFactory(this);
    }

    public void setPerformanceHint(final int hint) {
        mGlSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
//                mStStickerNative.setPerformanceHint(hint);
            }
        });
    }

    public void setBeautifyBaseParam(float[] beautifyParamsBase) {
//        for (int i = 0; i < beautifyParamsBase.length; i++) {
//            if (STUtils.isValidBeautifyParamsBase(beautifyParamsBase, i)) {
//                int type = STEffectBeautyType.EFFECT_BEAUTY_BASE_WHITTEN + i;
//                if (makeUpParamAliasMap.indexOfKey(type) >= 0) {
//                    mSTMobileEffectNative.setParam(makeUpAliasMap.get(type, 0), makeUpParamAliasMap.get(type));
//                }
//                mSTMobileEffectNative.setBeautyStrength(makeUpAliasMap.get(type, STEffectBeautyType.EFFECT_BEAUTY_BASE_WHITTEN + i), beautifyParamsBase[i]);
//            }
//        }
//        mBeautifyParamsTypeBase = beautifyParamsBase;
//        EffectInfoDataHelper.getInstance().baseParams = beautifyParamsBase;
    }

    /**
     * 非常重要!!! 基础美颜调用函数
     * @param index
     * @param value
     */
    public void setBeautyParam(int index, float value) {
        if ((int) (index / 100) == 1) {
            mBeautifyParamsTypeBase[index % 100 - 1] = value;
            EffectInfoDataHelper.getInstance().baseParams[index % 100 - 1] = value;
        } else if ((int) (index / 100) == 2) {
            mBeautifyParamsTypeProfessional[index % 100 - 1] = value;
            EffectInfoDataHelper.getInstance().professionalParams[index % 100 - 1] = value;
        } else if ((int) (index / 100) == 3) {
            mBeautifyParamsTypeMicro[index % 100 - 1] = value;
            EffectInfoDataHelper.getInstance().microParams[index % 100 - 1] = value;
        } else if ((int) (index / 100) == 6) {
            mBeautifyParamsTypeAdjust[index % 100 - 1] = value;
            EffectInfoDataHelper.getInstance().adjustParams[index % 100 - 1] = value;
        }
        if (makeUpParamAliasMap.indexOfKey(index) > 0) {
            mSTMobileEffectNative.setBeautyMode(makeUpAliasMap.get(index, index), makeUpParamAliasMap.get(index));
        }

        Log.d(TAG, String.format("- setBeautyParam: index:%d, value:%f", index, value));
        mSTMobileEffectNative.setBeautyStrength(makeUpAliasMap.get(index, index), value);
        updateHumanActionDetectConfig();
    }

    public void setMutex(int subItemSelectedIndex) {
        mBeautifyParamsTypeBase[subItemSelectedIndex] = 0f;
        EffectInfoDataHelper.getInstance().baseParams[subItemSelectedIndex] = 0f;
    }

    public void setWhitenFromAssetsFile(final String path) {
//        mGlSurfaceView.queueEvent(new Runnable() {
//            @Override
//            public void run() {
//                mSTMobileEffectNative.setBeautyFromAssetsFile(STEffectBeautyType.EFFECT_BEAUTY_BASE_WHITTEN,
//                        path, SenseMeApplication.getContext().getAssets());
//            }
//        });
    }

    public float[] getBeautifyParamsTypeBase() {
        return mBeautifyParamsTypeBase;
    }

    public float[] getBeautifyParamsTypeProfessional() {
        return mBeautifyParamsTypeProfessional;
    }

    public float[] getBeautifyParamsTypeMicro() {
        return mBeautifyParamsTypeMicro;
    }

    public float[] getBeautifyParamsTypeAdjust() {
        return mBeautifyParamsTypeAdjust;
    }

    public boolean getNeedResetMakeupView() {
        return mNeedClearMakeupView;
    }

    public boolean getNeedResetFilterView() {
        return mNeedClearFilterView;
    }


    /**
     * human action detect的配置选项,根据渲染接口需要配置
     */
    public void updateHumanActionDetectConfig() {
        mDetectConfig = mSTMobileEffectNative.getHumanActionDetectConfig();
    }

    public void updateAnimalDetectConfig() {
//        mNeedAnimalDetect = mSTMobileEffectNative.getAnimalDetectConfig() > 0;
    }

    @Override
    public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
        EGLContext shareContext = mEglContext;
        return shareContext;
    }

    @Override
    public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
    }

    private void initEglContext(){
        try {
            mEGLContextHelper.initEGL();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mEGLContextHelper.eglMakeCurrent();
        mEglContext = mEGLContextHelper.getEGLContext();
        mEGLContextHelper.eglMakeNoCurrent();
    }
}

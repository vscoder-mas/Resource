package sensetime.senseme.com.effects.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;

import static com.sensetime.stmobile.model.STMobileMakeupType.ST_MAKEUP_TYPE_ALL;

/**
 * @Description
 * @Author Lu Guoqiang
 * @Time 4/25/21 5:32 PM
 */
public class EffectInfoDataHelper implements IEffectInfoDataHelper {
    private static final String TAG = "EffectInfoDataHelper";

    public enum Type {
        IMG,
        CAMERA
    }

    private EffectInfoDataHelper() {
    }

    private static Type mType = Type.CAMERA;

    public static void setType(Type type) {
        mType = type;
    }

    public static EffectInfoDataHelper getInstance() {
        if (mType == Type.IMG) {
            return EffectInfoDataHolder.instanceImg;
        }
        return EffectInfoDataHolder.instanceCamera;
    }

    private static class EffectInfoDataHolder {
        @SuppressLint("StaticFieldLeak")
        private static final EffectInfoDataHelper instanceImg = new EffectInfoDataHelper();
        @SuppressLint("StaticFieldLeak")
        private static final EffectInfoDataHelper instanceCamera = new EffectInfoDataHelper();
    }

    // 整体效果选中索引记录
    public String fullBeautyName = "";
    public int fullBeautyMakeupProgress = 85;
    public int fullBeautyFilterProgress = 85;

    // 美妆
    private String[] currentMakeup = new String[]{"", "", "","", "","", "", "","", "",""};
    public float[] currentMakeupStrength;

    // 滤镜
    public String filterStyle;
    public String filterType;
    private String filterName;
    public float filterStrength;

    // 基础美颜
    public float[] baseParams = Constants.mNewBeautifyParamsTypeBase.clone();

    // 美形
    public float[] professionalParams = Constants.mNewBeautifyParamsTypeProfessional.clone();

    // 微整形
    public float[] microParams = Constants.mNewBeautifyParamsTypeMicro.clone();

    // 调整
    public float[] adjustParams = Constants.mNewBeautifyParamsTypeAdjust.clone();

    // get 基础美颜参数
    public float[] getBaseParams() {
        float[] array = SpUtils.getFloatArray(SP_BASE_PARAMS + mType.name(), Constants.mNewBeautifyParamsTypeBase.clone());
        this.baseParams = array;
        return array;
    }

    // get 美形参数
    public float[] getProfessionalParams() {
        float[] array = SpUtils.getFloatArray(SP_PROFESSIONAL_PARAMS + mType.name(), Constants.mNewBeautifyParamsTypeProfessional.clone());
        Log.d(TAG, "getProfessionalParams() called：" + Arrays.toString(array));
        this.professionalParams = array;
        return array;
    }

    public void setFilterName(String filterName) {
        this.fullBeautyName = "";
        this.filterName = filterName;
    }

    // get 微整形参数
    public float[] getMicroParams() {
        float[] array = SpUtils.getFloatArray(SP_MICRO_PARAMS + mType.name(), Constants.mNewBeautifyParamsTypeMicro.clone());
        Log.d(TAG, "getMicroParams() called" + Arrays.toString(array));
        this.microParams = array;
        return array;
    }

    // get 调整参数
    public float[] getAdjustParams() {
        float[] array = SpUtils.getFloatArray(SP_ADJUST_PARAMS + mType.name(), Constants.mNewBeautifyParamsTypeAdjust.clone());
        Log.d(TAG, "getAdjustParams() called" + Arrays.toString(array));
        return array;
    }

    public String[] getCurrentMakeUp() {
        String[] array = SpUtils.getStringArray(SP_MAKEUP + mType.name(),
                new String[] {"", "", "", "", "", "", "", "", "", "", ""}
        );
        Log.d(TAG, mType.name() + " getCurrentMakeUp() called with: def = " + Arrays.toString(array));
        this.currentMakeup = array;
        return array;
    }

    public float[] getCurrentMakeUpStrength(float[] def) {
        float[] array = SpUtils.getFloatArray(SP_MAKEUP_STRENGTH + mType.name(), def);
        Log.d(TAG, "getCurrentMakeUp() called with: def = [" + Arrays.toString(array) + "]");
        this.currentMakeupStrength = array;
        return array;
    }

    public String getFilterStyle() {
        String filterStyle = (String) SpUtils.getParam(SP_FILTER + mType.name(), "");
        Log.d(TAG, "getFilterStyle() called" + SpUtils.getParam(SP_FILTER + mType.name(), ""));
        this.filterStyle = filterStyle;
        return filterStyle;
    }

    public String getFilterType() {
        String filterType = (String) SpUtils.getParam(SP_FILTER_TYPE + mType.name(), "");
        Log.d(TAG, "getFilterType() called" + filterType);
        this.filterType = filterType;
        return filterType;
    }

    public String getFilterName() {
        String filterName = (String) SpUtils.getParam(SP_FILTER_NAME + mType.name(), "");
        Log.d(TAG, "getFilterName() called" + filterName);
        this.filterName = filterName;
        return filterName;
    }

    public float getFilterStrength() {
        float filterStrength = 0f;
        Object obj = SpUtils.getParam(SP_FILTER_STRENGTH + mType.name(), 0f);
        if (obj != null) {
            filterStrength = (float) obj;
        }
        this.filterStrength = filterStrength;
        return filterStrength;
    }

    public int getFullBeautyMakeupProgress() {
        Object obj = SpUtils.getParam(SP_FULL_BEAUTIFY_MAKEUP_PROGRESS + mType.name(), 85);
        if (obj != null) {
            return (int) obj;
        }
        return 0;
    }

    public String getFullBeautyName() {
        String result = "";
        Object obj = SpUtils.getParam(SP_FULL_BEAUTIFY_NAME + mType.name(), "Default");
        if (obj != null) {
            result = (String) obj;
        }
        this.fullBeautyName = result;
        Log.d(TAG, "getFullBeautyName() called:" + result);
        return result;
    }

    public int getFullBeautyFilterProgress() {
        Object obj = SpUtils.getParam(SP_FULL_BEAUTIFY_FILTER_PROGRESS + mType.name(), 85);
        if (obj != null) {
            return (int) obj;
        }
        return 0;
    }

    public void save() {
        Log.d(TAG, "save() called," + mType.name() + "，fullBeautyName：" + fullBeautyName);
        if (!fullBeautyName.equals("")) {
            // 整妆
            saveFullBeauty();
        } else {
            // 美妆
            saveMakeup();
            // 滤镜
            saveFilter();
        }

        // 基础美颜
        SpUtils.saveFloatArray(SP_BASE_PARAMS + mType.name(), baseParams);

        // 美形
        SpUtils.saveFloatArray(SP_PROFESSIONAL_PARAMS + mType.name(), professionalParams);
        Log.d(TAG, mType.name() + " save() called：professionalParams=" + Arrays.toString(professionalParams) +
                " ,base_params=" + Arrays.toString(baseParams));

        // 微整形
        SpUtils.saveFloatArray(SP_MICRO_PARAMS + mType.name(), microParams);

        // 调整
        SpUtils.saveFloatArray(SP_ADJUST_PARAMS + mType.name(), adjustParams);
    }

    private void saveFullBeauty() {
        SpUtils.setParam(SP_FULL_BEAUTIFY_NAME + mType.name(), fullBeautyName);
        Log.d(TAG, "save() called fullBeautyName:" + fullBeautyName);
        SpUtils.setParam(SP_FULL_BEAUTIFY_MAKEUP_PROGRESS + mType.name(), fullBeautyMakeupProgress);
        SpUtils.setParam(SP_FULL_BEAUTIFY_FILTER_PROGRESS + mType.name(), fullBeautyFilterProgress);
        Log.d(TAG, "save() called fullBeautyMakeupProgress:" + fullBeautyMakeupProgress +
                ", fullBeautyFilterProgress:" + fullBeautyFilterProgress);
    }

    private void saveFilter() {
        resetFullBeauty();//美妆和整体效果互斥
        if (null == filterStyle) {
            SpUtils.removeData(SP_FILTER + mType.name());
        } else {
            SpUtils.setParam(SP_FILTER + mType.name(), filterStyle);
            SpUtils.setParam(SP_FILTER_STRENGTH + mType.name(), filterStrength);
        }
        SpUtils.setParam(SP_FILTER_NAME + mType.name(), filterName);
        Log.d(TAG, "save filterType: " + filterType);
        Log.d(TAG, "save filterStyle: " + filterStyle);
        Log.d(TAG, "save filterName: " + filterName);
        Log.d(TAG, "save filterStrength: " + filterStrength);
//        SpUtils.setParam(SP_FILTER_GROUP_INDEX, filterGroupIndex);
        if (null == filterType) {
            SpUtils.removeData(SP_FILTER_TYPE + mType.name());
        } else {
            SpUtils.setParam(SP_FILTER_TYPE + mType.name(), filterType);
        }
    }

    private void resetFullBeauty() {
        fullBeautyName = "";//美妆和整体效果互斥
        SpUtils.setParam(SP_FULL_BEAUTIFY_NAME + mType.name(), fullBeautyName);
    }

    private void saveMakeup() {
        resetFullBeauty();
        SpUtils.saveStringArray(SP_MAKEUP + mType.name(), currentMakeup);
        SpUtils.saveFloatArray(SP_MAKEUP_STRENGTH + mType.name(), currentMakeupStrength);
        Log.d(TAG, mType.name() + " saveEffectInfo() called currentMakeup:" + Arrays.toString(currentMakeup) +
                " ,currentMakeupStrength:" + Arrays.toString(currentMakeupStrength));
    }

    @Override
    public void clear() {
        // 整妆
        SpUtils.removeData(SP_MAKEUP + mType.name());
        SpUtils.removeData(SP_MAKEUP_STRENGTH + mType.name());

        // filter
        SpUtils.removeData(SP_FILTER + mType.name());
        SpUtils.removeData(SP_FILTER_TYPE + mType.name());

        // 整体效果
        SpUtils.removeData(SP_FULL_BEAUTIFY_NAME + mType.name());

        SpUtils.removeData(SP_BASE_PARAMS + mType.name());
        SpUtils.removeData(SP_ADJUST_PARAMS + mType.name());
        SpUtils.removeData(SP_MICRO_PARAMS + mType.name());
        SpUtils.removeData(SP_PROFESSIONAL_PARAMS + mType.name());
    }

    public void setCurrentMakeup(String[] array) {
        if (!TextUtils.isEmpty(fullBeautyName)) {
            this.fullBeautyName = "";
            this.currentMakeup[ST_MAKEUP_TYPE_ALL] = null;
            array[ST_MAKEUP_TYPE_ALL] = "";
        }
        this.currentMakeup = array.clone();
    }

    public void setZeroBaseParams() {
        if (baseParams == null) return;
        Arrays.fill(baseParams, 0f);
    }

    public void setZeroProfessionalParams() {
        if (professionalParams == null) return;
        Arrays.fill(professionalParams, 0f);
    }

    public void setZeroMicroParams() {
        if (microParams == null) return;
        Arrays.fill(microParams, 0f);
    }

    public void setZeroMakeup() {
        if (currentMakeup == null) return;
        Arrays.fill(currentMakeup, "");
    }

    public void setEmptyMakeup(int position) {
        try {
            if(null == currentMakeup) return;
            currentMakeup[position] = "";
        }catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public void setZeroAdjustParams() {
        if (adjustParams == null) return;
        Arrays.fill(adjustParams, 0f);
    }

    // 基础美颜
    public static String SP_BASE_PARAMS = "sp_base_params";

    // 美形
    public static String SP_PROFESSIONAL_PARAMS = "sp_professional_params";

    // 微整形
    public static String SP_MICRO_PARAMS = "sp_micro_params";

    // 调整
    public static String SP_ADJUST_PARAMS = "sp_adjust_params";

    // 整体效果
    private static final String SP_FULL_BEAUTIFY_NAME = "sp_full_beautify_name";
    private static final String SP_FULL_BEAUTIFY_FILTER_PROGRESS = "sp_full_beautify_filter_progress";
    private static final String SP_FULL_BEAUTIFY_MAKEUP_PROGRESS = "sp_full_beautify_makeup_progress";

    // 美妆
    private static final String SP_MAKEUP = "sp_makeup" + mType.name();
    private static final String SP_MAKEUP_STRENGTH = "sp_makeup_strength";

    // 滤镜
    private static final String SP_FILTER = "sp_filter";
    private static final String SP_FILTER_TYPE = "sp_filter_type";
    private static final String SP_FILTER_NAME = "sp_filter_name";
    private static final String SP_FILTER_STRENGTH = "sp_filter_strength";

}

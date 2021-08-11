package sensetime.senseme.com.effects.utils;

import android.content.Context;
import android.util.DisplayMetrics;

import com.sensetime.stmobile.model.STMobileMakeupType;

public class Constants {

    // 基础美颜
    public static final float[] mNewBeautifyParamsTypeBase = {
            0.00f,  // 1.美白1
            0.00f,  // 2.美白2
            0.00f,  // 3.美白3
            0.00f,  // 4.红润
            0.00f,  // 5.磨皮1
            0.50f   // 6.磨皮2
    };
    // 美形
    public static final float[] mNewBeautifyParamsTypeProfessional = {
            0.34f,  // 1.瘦脸
            0.29f,  // 2.大眼
            0.10f,  // 3.小脸
            0.25f,  // 4.窄脸
            0.07f   // 5.圆眼
    };
    // 微整形
    public static final float[] mNewBeautifyParamsTypeMicro = {
            0.00f,  // 1.小头
            0.45f,  // 2.瘦脸型
            0.20f,  // 3.下巴
            0.00f,  // 4.额头
            0.30f,  // 5.苹果肌
            0.21f,   // 6.瘦鼻翼
            0.00f,  // 7.长鼻
            0.10f,  // 8.侧脸隆鼻
            0.51f,  // 9.嘴型
            0.00f,  // 10.缩人中
            -0.23f, // 11.眼距
            0.00f,  // 12.眼睛角度
            0.00f,  // 13.开眼角
            0.25f,  // 14.亮眼
            0.69f,  // 15.祛黑眼圈
            0.60f,  // 16.祛法令纹
            0.20f,  // 17.白牙
            0.36f,  // 18.瘦颧骨
            0.00f   // 19.开外眼角
    };
    // 调整
    public static final float[] mNewBeautifyParamsTypeAdjust = {
            0.00f,  // 1.对比度
            0.00f,  // 2.饱和度
            0.50f,  // 3.清晰度
            0.20f   // 4.锐化
    };

    public static boolean ACTIVITY_MODE_LANDSCAPE = false;
    public static boolean ACTIVITY_MODE_FOR_TV = false;

    //正式服appid appkey
    public final static String APPID = "6dc0af51b69247d0af4b0a676e11b5ee";//正式服
    public final static String APPKEY = "e4156e4d61b040d2bcbf896c798d06e3";//正式服

    //正式服group id
    public final static String GROUP_NEW = "ff81fc70f6c111e899f602f2be7c2171";
    public final static String GROUP_2D = "3cd2dae0f6c211e8877702f2beb67403";
    public final static String GROUP_3D = "4e869010f6c211e888ea020d88863a42";
    public final static String GROUP_HAND = "5aea6840f6c211e899f602f2be7c2171";
    public final static String GROUP_BG = "65365cf0f6c211e8877702f2beb67403";
    public final static String GROUP_FACE = "6d036ef0f6c211e899f602f2be7c2171";
    public final static String GROUP_AVATAR = "46028a20f6c211e888ea020d88863a42";
    public final static String GROUP_BEAUTY = "73bffb50f6c211e899f602f2be7c2171";
    public final static String GROUP_PARTICLE = "7c6089f0f6c211e8877702f2beb67403";

    public final static String GROUP_MAKEUP_ALL = "4db72d684a08473d8018837b16ffa9cc";
    public final static String GROUP_MAKEUP_LIP = "4a1cb40c732146ecbf7857c3052809e6";
    public final static String GROUP_MAKEUP_HAIRDYE = "a539b106d7e14038887fece6a601d9ec";
    public final static String GROUP_MAKEUP_BLUSH = "8563a7afe8234db683752e40efe460bd";
    public final static String GROUP_MAKEUP_HIGH_LIGHT = "ee3e45997b584b2b8f3ad976f500c62c";
    public final static String GROUP_MAKEUP_BROW = "913a02bde7834109934030231c7517a7";
    public final static String GROUP_MAKEUP_EYE = "855afaa09ced4560bc029ec09eeef950";
    public final static String GROUP_MAKEUP_EYELINER = "231a9fc91e0c4218977f2e4002a5bc84";
    public final static String GROUP_MAKEUP_EYELASH = "ff92608b29ef4644bcf02d2160eeb948";
    public final static String GROUP_MAKEUP_EYEBALL = "89c4e32f0ece4c9f9eb3219ff2dd1923";

    public static final int ST_MAKEUP_LIP = STMobileMakeupType.ST_MAKEUP_TYPE_LIP;//口红
    public static final int ST_MAKEUP_HIGHLIGHT = STMobileMakeupType.ST_MAKEUP_TYPE_HIGHLIGHT;//修容
    public static final int ST_MAKEUP_BLUSH = STMobileMakeupType.ST_MAKEUP_TYPE_BLUSH;//腮红
    public static final int ST_MAKEUP_BROW = STMobileMakeupType.ST_MAKEUP_TYPE_BROW;//眉毛
    public static final int ST_MAKEUP_EYE = STMobileMakeupType.ST_MAKEUP_TYPE_EYE;//眼影
    public static final int ST_MAKEUP_EYELINER = STMobileMakeupType.ST_MAKEUP_TYPE_EYELINER;//眼线
    public static final int ST_MAKEUP_EYELASH = STMobileMakeupType.ST_MAKEUP_TYPE_EYELASH;//睫毛
    public static final int ST_MAKEUP_EYEBALL = STMobileMakeupType.ST_MAKEUP_TYPE_EYEBALL;//美瞳
    public static final int ST_MAKEUP_HAIR_DYE = STMobileMakeupType.ST_MAKEUP_TYPE_HAIR_DYE;//染发
    public static final int ST_MAKEUP_ALL = STMobileMakeupType.ST_MAKEUP_TYPE_ALL;// 整妆

    public static final int MAKEUP_TYPE_COUNT = 11;

    /**
     * 屏幕宽高
     */
    public static int screenWidth;
    public static int screenHeight;

    /**
     * 三种画幅的具体显示尺寸
     */
    public static int mode_por_width_9_16;
    public static int mode_por_height_9_16;
    public static int mode_por_width_1_1;
    public static int mode_por_height_1_1;
    public static int mode_por_width_16_9;
    public static int mode_por_height_16_9;

    public static void init(Context context) {
        DisplayMetrics mDisplayMetrics = context.getResources()
                .getDisplayMetrics();
        screenWidth = mDisplayMetrics.widthPixels;
        screenHeight = mDisplayMetrics.heightPixels;
        mode_por_width_9_16 = screenWidth;
        mode_por_height_9_16 = screenHeight;
        mode_por_width_1_1 = screenWidth;
        mode_por_height_1_1 = screenWidth;
        mode_por_width_16_9 = screenWidth;
        mode_por_height_16_9 = screenWidth / 16 * 9;
    }

}

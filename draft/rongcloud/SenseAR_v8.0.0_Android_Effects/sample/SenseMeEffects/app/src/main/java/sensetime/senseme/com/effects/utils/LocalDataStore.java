package sensetime.senseme.com.effects.utils;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.HashMap;

import sensetime.senseme.com.effects.R;
import sensetime.senseme.com.effects.SenseMeApplication;
import sensetime.senseme.com.effects.adapter.FilterInfo;
import sensetime.senseme.com.effects.adapter.MakeupInfo;
import sensetime.senseme.com.effects.view.BeautyItem;
import sensetime.senseme.com.effects.view.BeautyOptionsItem;
import sensetime.senseme.com.effects.view.FullBeautyItem;
import sensetime.senseme.com.effects.view.MakeupItem;
import sensetime.senseme.com.effects.view.StickerOptionsItem;

public class LocalDataStore implements LocalDataStoreI {

    private LocalDataStore() {
    }

    public static LocalDataStore getInstance() {
        return LocalDataManagerHolder.instance;
    }

    private static class LocalDataManagerHolder {
        @SuppressLint("StaticFieldLeak")
        private static final LocalDataStore instance = new LocalDataStore();
    }

    @Override
    public HashMap<String, ArrayList<MakeupItem>> getMakeupLists() {
        HashMap<String, ArrayList<MakeupItem>> mMakeupLists = new HashMap<>();
        mMakeupLists.put("makeup_lip", FileUtils.getMakeupFiles(SenseMeApplication.getContext(), "makeup_lip"));
        mMakeupLists.put("makeup_highlight", FileUtils.getMakeupFiles(SenseMeApplication.getContext(), "makeup_highlight"));
        mMakeupLists.put("makeup_blush", FileUtils.getMakeupFiles(SenseMeApplication.getContext(), "makeup_blush"));
        mMakeupLists.put("makeup_brow", FileUtils.getMakeupFiles(SenseMeApplication.getContext(), "makeup_brow"));
        mMakeupLists.put("makeup_eye", FileUtils.getMakeupFiles(SenseMeApplication.getContext(), "makeup_eye"));
        mMakeupLists.put("makeup_eyeliner", FileUtils.getMakeupFiles(SenseMeApplication.getContext(), "makeup_eyeliner"));
        mMakeupLists.put("makeup_eyelash", FileUtils.getMakeupFiles(SenseMeApplication.getContext(), "makeup_eyelash"));
        mMakeupLists.put("makeup_eyeball", FileUtils.getMakeupFiles(SenseMeApplication.getContext(), "makeup_eyeball"));
        mMakeupLists.put("makeup_hairdye", FileUtils.getMakeupFiles(SenseMeApplication.getContext(), "makeup_hairdye"));
        mMakeupLists.put("makeup_all", FileUtils.getMakeupFiles(SenseMeApplication.getContext(), "makeup_all"));
        return mMakeupLists;
    }

    @Override
    public ArrayList<BeautyOptionsItem> getBeautyOptionsList() {
        ArrayList<BeautyOptionsItem> mBeautyOptionsList = new ArrayList<>();
        mBeautyOptionsList.add(0, new BeautyOptionsItem(MultiLanguageUtils.getStr(R.string.menu_title_full_beauty)));
        mBeautyOptionsList.add(1, new BeautyOptionsItem(MultiLanguageUtils.getStr(R.string.menu_title_base)));
        mBeautyOptionsList.add(2, new BeautyOptionsItem(MultiLanguageUtils.getStr(R.string.menu_title_reshape)));
        mBeautyOptionsList.add(3, new BeautyOptionsItem(MultiLanguageUtils.getStr(R.string.menu_title_plastic)));
        mBeautyOptionsList.add(4, new BeautyOptionsItem(MultiLanguageUtils.getStr(R.string.menu_title_makeup)));
        mBeautyOptionsList.add(5, new BeautyOptionsItem(MultiLanguageUtils.getStr(R.string.menu_title_filter)));
        mBeautyOptionsList.add(6, new BeautyOptionsItem(MultiLanguageUtils.getStr(R.string.menu_title_tone)));
        return mBeautyOptionsList;
    }

    @Override
    public ArrayList<StickerOptionsItem> getStickerOptionsList() {
        ArrayList<StickerOptionsItem> mStickerOptionsList = new ArrayList<>();
        //new
        mStickerOptionsList.add(0, new StickerOptionsItem(Constants.GROUP_NEW, BitmapFactory.decodeResource(getResources(), R.drawable.sticker_new_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.sticker_new_selected)));
        //2d
        mStickerOptionsList.add(1, new StickerOptionsItem(Constants.GROUP_2D, BitmapFactory.decodeResource(getResources(), R.drawable.sticker_2d_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.sticker_2d_selected)));
        //3d
        mStickerOptionsList.add(2, new StickerOptionsItem(Constants.GROUP_3D, BitmapFactory.decodeResource(getResources(), R.drawable.sticker_3d_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.sticker_3d_selected)));
        //手势贴纸
        mStickerOptionsList.add(3, new StickerOptionsItem(Constants.GROUP_HAND, BitmapFactory.decodeResource(getResources(), R.drawable.sticker_hand_action_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.sticker_hand_action_selected)));
        //背景贴纸
        mStickerOptionsList.add(4, new StickerOptionsItem(Constants.GROUP_BG, BitmapFactory.decodeResource(getResources(), R.drawable.sticker_bg_segment_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.sticker_bg_segment_selected)));
        //脸部变形贴纸
        mStickerOptionsList.add(5, new StickerOptionsItem(Constants.GROUP_FACE, BitmapFactory.decodeResource(getResources(), R.drawable.sticker_dedormation_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.sticker_dedormation_selected)));
        //avatar
        mStickerOptionsList.add(6, new StickerOptionsItem(Constants.GROUP_AVATAR, BitmapFactory.decodeResource(getResources(), R.drawable.sticker_avatar_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.sticker_avatar_selected)));
        //美妆贴纸
        mStickerOptionsList.add(7, new StickerOptionsItem(Constants.GROUP_BEAUTY, BitmapFactory.decodeResource(getResources(), R.drawable.sticker_face_morph_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.sticker_face_morph_selected)));
        //粒子贴纸
        mStickerOptionsList.add(8, new StickerOptionsItem(Constants.GROUP_PARTICLE, BitmapFactory.decodeResource(getResources(), R.drawable.particles_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.particles_selected)));
        //通用物体跟踪
        mStickerOptionsList.add(9, new StickerOptionsItem("object_track", BitmapFactory.decodeResource(getResources(), R.drawable.object_track_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.object_track_selected)));
        //new
        //使用本地模型加载
        mStickerOptionsList.add(10, new StickerOptionsItem("sticker_new_engine", BitmapFactory.decodeResource(getResources(), R.drawable.sticker_local_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.sticker_local_selected)));
        // 测试add_sticker
        //mStickerOptionsList.add(11, new StickerOptionsItem("sticker_add_package", BitmapFactory.decodeResource(getResources(), R.drawable.sticker_add_selected), BitmapFactory.decodeResource(getResources(), R.drawable.sticker_add_unselected)));
        return mStickerOptionsList;
    }

    @Override
    public ArrayList<BeautyItem> getMicroBeautyList() {
        ArrayList<BeautyItem> mMicroBeautyItem = new ArrayList<>();
        mMicroBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.plastic_thinner_head), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_small_head_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_small_head_selected)));
        mMicroBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.plastic_thin_face), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_thin_face_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_thin_face_selected)));
        mMicroBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.plastic_chin_length), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_chin_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_chin_selected)));
        mMicroBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.plastic_hairline_height), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_forehead_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_forehead_selected)));
        mMicroBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.plastic_apple_musle), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_apple_musle_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_apple_musle_selected)));
        mMicroBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.plastic_narrow_nose), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_thin_nose_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_thin_nose_selected)));
        mMicroBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.plastic_nose_length), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_long_nose_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_long_nose_selected)));
        mMicroBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.plastic_profile_rhinoplasty), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_profile_rhinoplasty_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_profile_rhinoplasty_selected)));
        mMicroBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.plastic_mouth_size), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_mouth_type_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_mouth_type_selected)));
        mMicroBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.plastic_philtrum_length), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_philtrum_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_philtrum_selected)));
        mMicroBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.plastic_eye_distance), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_eye_distance_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_eye_distance_selected)));
        mMicroBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.plastic_eye_angle), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_eye_angle_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_eye_angle_selected)));
        mMicroBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.plastic_open_canthus), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_open_canthus_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_open_canthus_selected)));
        mMicroBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.plastic_bright_eye), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_bright_eye_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_bright_eye_selected)));
        mMicroBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.plastic_black_eye), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_remove_dark_circles_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_remove_dark_circles_selected)));
        mMicroBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.plastic_wrinkle), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_remove_nasolabial_folds_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_remove_nasolabial_folds_selected)));
        mMicroBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.plastic_white_teeth), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_white_teeth_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_white_teeth_selected)));
        mMicroBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.plastic_cheekbone), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_thin_cheekbone_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_thin_cheekbone_selected)));
        mMicroBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.plastic_open_external_canthus), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_open_external_canthus_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_open_external_canthus_selected)));
        return mMicroBeautyItem;
    }

    @Override
    public ArrayList<FullBeautyItem> getFullBeautyList() {
        ArrayList<FullBeautyItem> mBeautyFullItem = new ArrayList<>();
        mBeautyFullItem.add(LocalDataStore.getInstance().fullBeautyItemDefault());
        mBeautyFullItem.add(LocalDataStore.getInstance().fullBeautyItemTianRan());
        mBeautyFullItem.add(LocalDataStore.getInstance().fullBeautyItemSweetGirl());
        mBeautyFullItem.add(LocalDataStore.getInstance().fullBeautyItemOxyGen());
        mBeautyFullItem.add(LocalDataStore.getInstance().fullBeautyItemRedWine());

        mBeautyFullItem.add(LocalDataStore.getInstance().fullBeautyItemWhiteTea());
        mBeautyFullItem.add(LocalDataStore.getInstance().fullBeautyItemZhigan());
        mBeautyFullItem.add(LocalDataStore.getInstance().fullBeautyItemSweet());
        mBeautyFullItem.add(LocalDataStore.getInstance().fullBeautyItemWestern());
        mBeautyFullItem.add(LocalDataStore.getInstance().fullBeautyItemDeep());

        mBeautyFullItem.add(LocalDataStore.getInstance().fullBeautyItemZiPaiZiRan());
        mBeautyFullItem.add(LocalDataStore.getInstance().fullBeautyItemZiPaiYuanQi());
        mBeautyFullItem.add(LocalDataStore.getInstance().fullBeautyItemZhiBoZiRan());
        mBeautyFullItem.add(LocalDataStore.getInstance().fullBeautyItemZhiBoYuanQi());
        mBeautyFullItem.add(LocalDataStore.getInstance().fullBeautyItemZhiBoYuanHua());
        return mBeautyFullItem;
    }

    @Override
    public ArrayList<BeautyItem> getBeautyBaseList() {
        ArrayList<BeautyItem> mBeautyBaseItem = new ArrayList<>();
        mBeautyBaseItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.basic_whiten1), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_whiten_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_whiten_selected)));
        mBeautyBaseItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.basic_whiten2), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_whiten_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_whiten_selected)));
        mBeautyBaseItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.basic_whiten3), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_whiten_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_whiten_selected)));
        mBeautyBaseItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.basic_redden), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_redden_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_redden_selected)));
        mBeautyBaseItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.basic_smooth1), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_smooth_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_smooth_selected)));
        mBeautyBaseItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.basic_smooth2), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_smooth_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_smooth_selected)));
        return mBeautyBaseItem;
    }

    @Override
    public ArrayList<BeautyItem> getProfessionalBeautyList() {
        ArrayList<BeautyItem> mProfessionalBeautyItem = new ArrayList<>();
        mProfessionalBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.reshape_shrink_face), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_shrink_face_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_shrink_face_selected)));
        mProfessionalBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.reshape_enlarge_eye), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_enlargeeye_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_enlargeeye_selected)));
        mProfessionalBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.reshape_shrink_jaw), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_small_face_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_small_face_selected)));
        mProfessionalBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.reshape_narrow_face), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_narrow_face_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_narrow_face_selected)));
        mProfessionalBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.reshape_round_eye), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_round_eye_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_round_eye_selected)));
        return mProfessionalBeautyItem;
    }

    @Override
    public ArrayList<BeautyItem> getAdjustBeautyList() {
        ArrayList<BeautyItem> mAdjustBeautyItem = new ArrayList<>();
        mAdjustBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.adjust_contrast), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_contrast_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_contrast_selected)));
        mAdjustBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.adjust_saturation), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_saturation_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_saturation_selected)));
        mAdjustBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.adjust_sharpen), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_sharp_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_sharp_selected)));
        mAdjustBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.adjust_clear), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_clear_unselected), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_clear_selected)));
        return mAdjustBeautyItem;
    }

    @Override
    public FullBeautyItem fullBeautyItemDefault() {
        FilterInfo filterInfo = new FilterInfo("filter_portrait", "nvshen", 100);
        ArrayList<MakeupInfo> makeupList = new ArrayList<>();
        MakeupInfo defaultMakeupInfo1 = new MakeupInfo("makeup_all", "nvshen", 100);
        makeupList.add(defaultMakeupInfo1);

        float[] mNewBeautifyParamsTypeBase = {0f, 0f, 0f, 0f, 0f, 0.5f};
        float[] mNewBeautifyParamsTypeProfessional = {0.34f, 0.29f, 0.1f, 0.25f, 0.07f};
        float[] mNewBeautifyParamsTypeMicro = {0f, 0.45f, 0.2f, 0f, 0.3f,
                0.21f, 0f, 0.1f, 0.51f, 0f,
                -0.23f, 0f, 0f, 0.25f, 0.69f,
                0.6f, 0.2f, 0.36f, 0f};
        float[] mNewBeautifyParamsTypeAdjust = {0.0f, 0.0f, 0.5f, 0.2f};

        return new FullBeautyItem(MultiLanguageUtils.getStr(R.string.whole_default), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_default_unselected),
                BitmapFactory.decodeResource(getResources(), R.drawable.beauty_default_selected),
                mNewBeautifyParamsTypeBase, mNewBeautifyParamsTypeProfessional, mNewBeautifyParamsTypeMicro, mNewBeautifyParamsTypeAdjust,
                makeupList, filterInfo);
    }

    @Override
    public FullBeautyItem fullBeautyItemTianRan() {
        FilterInfo filterInfo = new FilterInfo("filter_portrait", "tianran", 100);
        ArrayList<MakeupInfo> makeupList = new ArrayList<>();
        MakeupInfo defaultMakeupInfo1 = new MakeupInfo("makeup_all", "tianran", 100);

        makeupList.add(defaultMakeupInfo1);

        float[] mNewBeautifyParamsTypeBase = {0f, 0f, 0f, 0f, 0f, 0.5f};
        float[] mNewBeautifyParamsTypeProfessional = {0.34f, 0.29f, 0.1f, 0.25f, 0.07f};
        float[] mNewBeautifyParamsTypeMicro = {0f, 0.45f, 0.2f, 0f, 0.3f,
                0.21f, 0f, 0.1f, 0.51f, 0f,
                -0.23f, 0f, 0f, 0.25f, 0.69f,
                0.6f, 0.2f, 0.36f, 0f};
        float[] mNewBeautifyParamsTypeAdjust = {0.0f, 0.0f, 0.5f, 0.2f};

        return new FullBeautyItem(MultiLanguageUtils.getStr(R.string.whole_tianran), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_tianran_unselected),
                BitmapFactory.decodeResource(getResources(), R.drawable.beauty_tianran_unselected),
                mNewBeautifyParamsTypeBase, mNewBeautifyParamsTypeProfessional, mNewBeautifyParamsTypeMicro, mNewBeautifyParamsTypeAdjust,
                makeupList, filterInfo);
    }

    @Override
    public FullBeautyItem fullBeautyItemSweetGirl() {
        FilterInfo filterInfo = new FilterInfo("filter_portrait", "sweetgirl", 100);
        ArrayList<MakeupInfo> makeupList = new ArrayList<>();
        MakeupInfo defaultMakeupInfo1 = new MakeupInfo("makeup_all", "sweetgirl", 100);

        makeupList.add(defaultMakeupInfo1);

        float[] mNewBeautifyParamsTypeBase = {0f, 0f, 0f, 0f, 0f, 0.5f};
        float[] mNewBeautifyParamsTypeProfessional = {0.34f, 0.29f, 0.1f, 0.25f, 0.07f};
        float[] mNewBeautifyParamsTypeMicro = {0f, 0.45f, 0.2f, 0f, 0.3f,
                0.21f, 0f, 0.1f, 0.51f, 0f,
                -0.23f, 0f, 0f, 0.25f, 0.69f,
                0.6f, 0.2f, 0.36f, 0f};
        float[] mNewBeautifyParamsTypeAdjust = {0.0f, 0.0f, 0.5f, 0.2f};

        return new FullBeautyItem(MultiLanguageUtils.getStr(R.string.whole_sweetgirl), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_sweetgirl_unselected),
                BitmapFactory.decodeResource(getResources(), R.drawable.beauty_sweetgirl_unselected),
                mNewBeautifyParamsTypeBase, mNewBeautifyParamsTypeProfessional, mNewBeautifyParamsTypeMicro, mNewBeautifyParamsTypeAdjust,
                makeupList, filterInfo);
    }

    @Override
    public FullBeautyItem fullBeautyItemOxyGen() {
        FilterInfo filterInfo = new FilterInfo("filter_portrait", "oxygen", 100);
        ArrayList<MakeupInfo> makeupList = new ArrayList<>();
        MakeupInfo defaultMakeupInfo1 = new MakeupInfo("makeup_all", "oxygen", 100);

        makeupList.add(defaultMakeupInfo1);

        float[] mNewBeautifyParamsTypeBase = {0f, 0f, 0f, 0f, 0f, 0.5f};
        float[] mNewBeautifyParamsTypeProfessional = {0.34f, 0.29f, 0.1f, 0.25f, 0.07f};
        float[] mNewBeautifyParamsTypeMicro = {0f, 0.45f, 0.2f, 0f, 0.3f,
                0.21f, 0f, 0.1f, 0.51f, 0f,
                -0.23f, 0f, 0f, 0.25f, 0.69f,
                0.6f, 0.2f, 0.36f, 0f};
        float[] mNewBeautifyParamsTypeAdjust = {0.0f, 0.0f, 0.5f, 0.2f};

        return new FullBeautyItem(MultiLanguageUtils.getStr(R.string.whole_oxygen), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_oxygen_unselected),
                BitmapFactory.decodeResource(getResources(), R.drawable.beauty_oxygen_unselected),
                mNewBeautifyParamsTypeBase, mNewBeautifyParamsTypeProfessional, mNewBeautifyParamsTypeMicro, mNewBeautifyParamsTypeAdjust,
                makeupList, filterInfo);
    }

    @Override
    public FullBeautyItem fullBeautyItemZiPaiZiRan() {
        FilterInfo filterInfo = new FilterInfo("filter_portrait", "native", 60);
        ArrayList<MakeupInfo> makeupList = new ArrayList<>();
        MakeupInfo defaultMakeupInfo1 = new MakeupInfo("makeup_lip", "6自然", 40);
        MakeupInfo defaultMakeupInfo2 = new MakeupInfo("makeup_highlight", "faceE", 58);

        makeupList.add(defaultMakeupInfo1);
        makeupList.add(defaultMakeupInfo2);

        float[] mNewBeautifyParamsTypeBase = {0f, 0f, 0f, 0f, 0f, 0.5f};
        float[] mNewBeautifyParamsTypeProfessional = {0.34f, 0.29f, 0.1f, 0.25f, 0.07f};
        float[] mNewBeautifyParamsTypeMicro = {0f, 0.45f, 0.2f, 0f, 0.3f,
                0.21f, 0f, 0.1f, 0.51f, 0f,
                -0.23f, 0f, 0f, 0.25f, 0.69f,
                0.6f, 0.2f, 0.36f, 0f};
        float[] mNewBeautifyParamsTypeAdjust = {0.0f, 0.0f, 0.5f, 0.2f};

        return new FullBeautyItem(MultiLanguageUtils.getStr(R.string.whole_zipaiziran), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_zipaiziran_unselected),
                BitmapFactory.decodeResource(getResources(), R.drawable.beauty_zipaiziran_selected),
                mNewBeautifyParamsTypeBase, mNewBeautifyParamsTypeProfessional, mNewBeautifyParamsTypeMicro, mNewBeautifyParamsTypeAdjust,
                makeupList, filterInfo);
    }

    @Override
    public FullBeautyItem fullBeautyItemRedWine() {
        FilterInfo filterInfo = new FilterInfo("filter_portrait", "redwhine", 85);
        ArrayList<MakeupInfo> makeupList = new ArrayList<>();
        MakeupInfo defaultMakeupInfo1 = new MakeupInfo("makeup_all", "redwine", 80);
        makeupList.add(defaultMakeupInfo1);

        float[] mNewBeautifyParamsTypeBase = {0f, 0f, 0f, 0f, 0f, 0.5f};
        float[] mNewBeautifyParamsTypeProfessional = {0.34f, 0.29f, 0.1f, 0.25f, 0.07f};
        float[] mNewBeautifyParamsTypeMicro = {0f, 0.45f, 0.2f, 0f, 0.3f,
                0.21f, 0f, 0.1f, 0.51f, 0f,
                -0.23f, 0f, 0f, 0.25f, 0.69f,
                0.6f, 0.2f, 0.36f, 0f};
        float[] mNewBeautifyParamsTypeAdjust = {0.0f, 0.0f, 0.5f, 0.2f};

        return new FullBeautyItem(MultiLanguageUtils.getStr(R.string.whole_redwine), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_redwine_unselected),
                BitmapFactory.decodeResource(getResources(), R.drawable.beauty_redwine_unselected),
                mNewBeautifyParamsTypeBase, mNewBeautifyParamsTypeProfessional, mNewBeautifyParamsTypeMicro, mNewBeautifyParamsTypeAdjust,
                makeupList, filterInfo);
    }

    @Override
    public FullBeautyItem fullBeautyItemWhiteTea() {
        FilterInfo filterInfo = new FilterInfo("filter_portrait", "whitetea", 85);
        ArrayList<MakeupInfo> makeupList = new ArrayList<>();
        MakeupInfo defaultMakeupInfo1 = new MakeupInfo("makeup_all", "whiteTea", 80);

        makeupList.add(defaultMakeupInfo1);

        float[] mNewBeautifyParamsTypeBase = {0f, 0f, 0f, 0f, 0f, 0.5f};
        float[] mNewBeautifyParamsTypeProfessional = {0.34f, 0.29f, 0.1f, 0.25f, 0.07f};
        float[] mNewBeautifyParamsTypeMicro = {0f, 0.45f, 0.2f, 0f, 0.3f,
                0.21f, 0f, 0.1f, 0.51f, 0f,
                -0.23f, 0f, 0f, 0.25f, 0.69f,
                0.6f, 0.2f, 0.36f, 0f};
        float[] mNewBeautifyParamsTypeAdjust = {0.0f, 0.0f, 0.5f, 0.2f};

        return new FullBeautyItem(MultiLanguageUtils.getStr(R.string.whole_whitetea), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_whitetea_unselected),
                BitmapFactory.decodeResource(getResources(), R.drawable.beauty_whitetea_unselected),
                mNewBeautifyParamsTypeBase, mNewBeautifyParamsTypeProfessional, mNewBeautifyParamsTypeMicro, mNewBeautifyParamsTypeAdjust,
                makeupList, filterInfo);
    }

    @Override
    public FullBeautyItem fullBeautyItemZhigan() {
        FilterInfo filterInfo = new FilterInfo("filter_portrait", "pinzhi", 100);
        ArrayList<MakeupInfo> makeupList = new ArrayList<>();
        MakeupInfo defaultMakeupInfo1 = new MakeupInfo("makeup_all", "zhigan", 80);

        makeupList.add(defaultMakeupInfo1);
        float[] mNewBeautifyParamsTypeBase = {0f, 0f, 0f, 0f, 0f, 0.5f};
        float[] mNewBeautifyParamsTypeProfessional = {0.34f, 0.29f, 0.1f, 0.25f, 0.07f};
        float[] mNewBeautifyParamsTypeMicro = {0f, 0.45f, 0.2f, 0f, 0.3f,
                0.21f, 0f, 0.1f, 0.51f, 0f,
                -0.23f, 0f, 0f, 0.25f, 0.69f,
                0.6f, 0.2f, 0.36f, 0f};
        float[] mNewBeautifyParamsTypeAdjust = {0.0f, 0.0f, 0.5f, 0.2f};

        return new FullBeautyItem(MultiLanguageUtils.getStr(R.string.whole_zhigan), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_zhigan_unselected),
                BitmapFactory.decodeResource(getResources(), R.drawable.beauty_zhigan_unselected),
                mNewBeautifyParamsTypeBase, mNewBeautifyParamsTypeProfessional, mNewBeautifyParamsTypeMicro, mNewBeautifyParamsTypeAdjust,
                makeupList, filterInfo);
    }

    @Override
    public FullBeautyItem fullBeautyItemSweet() {
        FilterInfo filterInfo = new FilterInfo("filter_portrait", "sweet", 85);
        ArrayList<MakeupInfo> makeupList = new ArrayList<>();
        MakeupInfo defaultMakeupInfo1 = new MakeupInfo("makeup_all", "sweet", 80);

        makeupList.add(defaultMakeupInfo1);
        float[] mNewBeautifyParamsTypeBase = {0f, 0f, 0f, 0f, 0f, 0.5f};
        float[] mNewBeautifyParamsTypeProfessional = {0.34f, 0.29f, 0.1f, 0.25f, 0.07f};
        float[] mNewBeautifyParamsTypeMicro = {0f, 0.45f, 0.2f, 0f, 0.3f,
                0.21f, 0f, 0.1f, 0.51f, 0f,
                -0.23f, 0f, 0f, 0.25f, 0.69f,
                0.6f, 0.2f, 0.36f, 0f};
        float[] mNewBeautifyParamsTypeAdjust = {0.0f, 0.0f, 0.5f, 0.2f};

        return new FullBeautyItem(MultiLanguageUtils.getStr(R.string.whole_sweet), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_sweet_unselected),
                BitmapFactory.decodeResource(getResources(), R.drawable.beauty_sweet_unselected),
                mNewBeautifyParamsTypeBase, mNewBeautifyParamsTypeProfessional, mNewBeautifyParamsTypeMicro, mNewBeautifyParamsTypeAdjust,
                makeupList, filterInfo);
    }

    @Override
    public FullBeautyItem fullBeautyItemWestern() {
        FilterInfo filterInfo = new FilterInfo("filter_portrait", "ziranguang", 60);
        ArrayList<MakeupInfo> makeupList = new ArrayList<>();
        MakeupInfo defaultMakeupInfo1 = new MakeupInfo("makeup_all", "Western", 60);
        makeupList.add(defaultMakeupInfo1);

        float[] mNewBeautifyParamsTypeBase = {0f, 0f, 0f, 0f, 0f, 0.5f};
        float[] mNewBeautifyParamsTypeProfessional = {0.34f, 0.29f, 0.1f, 0.25f, 0.07f};
        float[] mNewBeautifyParamsTypeMicro = {0f, 0.45f, 0.2f, 0f, 0.3f,
                0.21f, 0f, 0.1f, 0.51f, 0f,
                -0.23f, 0f, 0f, 0.25f, 0.69f,
                0.6f, 0.2f, 0.36f, 0f};
        float[] mNewBeautifyParamsTypeAdjust = {0.0f, 0.0f, 0.5f, 0.2f};

        return new FullBeautyItem(MultiLanguageUtils.getStr(R.string.whole_western), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_western_unselected),
                BitmapFactory.decodeResource(getResources(), R.drawable.beauty_western_unselected),
                mNewBeautifyParamsTypeBase, mNewBeautifyParamsTypeProfessional, mNewBeautifyParamsTypeMicro, mNewBeautifyParamsTypeAdjust,
                makeupList, filterInfo);
    }

    @Override
    public FullBeautyItem fullBeautyItemDeep() {
        FilterInfo filterInfo = new FilterInfo("filter_portrait", "deep", 100);
        ArrayList<MakeupInfo> makeupList = new ArrayList<>();
        MakeupInfo defaultMakeupInfo1 = new MakeupInfo("makeup_all", "deep", 100);

        makeupList.add(defaultMakeupInfo1);

        float[] mNewBeautifyParamsTypeBase = {0f, 0f, 0f, 0f, 0f, 0.5f};
        float[] mNewBeautifyParamsTypeProfessional = {0.34f, 0.29f, 0.1f, 0.25f, 0.07f};
        float[] mNewBeautifyParamsTypeMicro = {0f, 0.45f, 0.2f, 0f, 0.3f,
                0.21f, 0f, 0.1f, 0.51f, 0f,
                -0.23f, 0f, 0f, 0.25f, 0.69f,
                0.6f, 0.2f, 0.36f, 0f};
        float[] mNewBeautifyParamsTypeAdjust = {0.0f, 0.0f, 0.5f, 0.2f};

        return new FullBeautyItem(MultiLanguageUtils.getStr(R.string.whole_deep), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_deep_unselected),
                BitmapFactory.decodeResource(getResources(), R.drawable.beauty_deep_unselected),
                mNewBeautifyParamsTypeBase, mNewBeautifyParamsTypeProfessional, mNewBeautifyParamsTypeMicro, mNewBeautifyParamsTypeAdjust,
                makeupList, filterInfo);
    }

    @Override
    public FullBeautyItem fullBeautyItemZiPaiYuanQi() {
        FilterInfo filterInfo = new FilterInfo("filter_portrait", "native", 91);
        ArrayList<MakeupInfo> makeupList = new ArrayList<>();
        MakeupInfo defaultMakeupInfo1 = new MakeupInfo("makeup_lip", "11自然", 25);
        MakeupInfo defaultMakeupInfo2 = new MakeupInfo("makeup_brow", "browB", 15);
        MakeupInfo defaultMakeupInfo3 = new MakeupInfo("makeup_blush", "blusha", 40);

        makeupList.add(defaultMakeupInfo1);
        makeupList.add(defaultMakeupInfo2);
        makeupList.add(defaultMakeupInfo3);

        float[] mNewBeautifyParamsTypeBase = {0f, 0f, 0f, 0f, 0f, 0.5f};
        float[] mNewBeautifyParamsTypeProfessional = {0.11f, 0.2f, 0.1f, 0.12f, 0f};
        float[] mNewBeautifyParamsTypeMicro = {0f, 0.15f, 0.2f, 0f, 0.3f,
                0.21f, 0f, 0.1f, 0f, 0f,
                0f, 0f, 0f, 0.43f, 0.29f,
                0.3f, 0.2f, 0f, 0f};
        float[] mNewBeautifyParamsTypeAdjust = {0.0f, 0.0f, 0.5f, 0.2f};

        return new FullBeautyItem(MultiLanguageUtils.getStr(R.string.whole_zipaiyuanqi), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_zipaiyuanqi_unselected),
                BitmapFactory.decodeResource(getResources(), R.drawable.beauty_zipaiyuanqi_selected),
                mNewBeautifyParamsTypeBase, mNewBeautifyParamsTypeProfessional, mNewBeautifyParamsTypeMicro, mNewBeautifyParamsTypeAdjust,
                makeupList, filterInfo);
    }

    @Override
    public FullBeautyItem fullBeautyItemZhiBoZiRan() {
        FilterInfo filterInfo = new FilterInfo("filter_portrait", "snow", 52);
        ArrayList<MakeupInfo> makeupList = new ArrayList<>();
        MakeupInfo defaultMakeupInfo1 = new MakeupInfo("makeup_lip", "11自然", 25);
        MakeupInfo defaultMakeupInfo2 = new MakeupInfo("makeup_brow", "browB", 15);
        MakeupInfo defaultMakeupInfo3 = new MakeupInfo("makeup_blush", "blusha", 23);

        makeupList.add(defaultMakeupInfo1);
        makeupList.add(defaultMakeupInfo2);
        makeupList.add(defaultMakeupInfo3);

        float[] mNewBeautifyParamsTypeBase = {0f, 0f, 0f, 0f, 0f, 0.5f};
        float[] mNewBeautifyParamsTypeProfessional = {0.40f, 0.35f, 0.1f, 0f, 0f};
        float[] mNewBeautifyParamsTypeMicro = {0f, 0.15f, 0.2f, 0f, 0.3f,
                0.21f, 0f, 0.1f, 0f, 0f,
                0f, 0f, 0f, 0f, 0.29f,
                0.3f, 0.2f, 0f, 0f};
        float[] mNewBeautifyParamsTypeAdjust = {0.0f, 0.0f, 0.5f, 0.2f};

        return new FullBeautyItem(MultiLanguageUtils.getStr(R.string.whole_zhiboziran), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_zhiboziran_unselected),
                BitmapFactory.decodeResource(getResources(), R.drawable.beauty_zhiboziran_selected),
                mNewBeautifyParamsTypeBase, mNewBeautifyParamsTypeProfessional, mNewBeautifyParamsTypeMicro, mNewBeautifyParamsTypeAdjust,
                makeupList, filterInfo);
    }

    @Override
    public FullBeautyItem fullBeautyItemZhiBoYuanQi() {
        FilterInfo filterInfo = new FilterInfo("filter_portrait", "native", 80);
        ArrayList<MakeupInfo> makeupList = new ArrayList<>();
        MakeupInfo defaultMakeupInfo1 = new MakeupInfo("makeup_lip", "12自然", 54);
        MakeupInfo defaultMakeupInfo2 = new MakeupInfo("makeup_brow", "browB", 20);
        MakeupInfo defaultMakeupInfo3 = new MakeupInfo("makeup_eye", "eyeshadowa", 80);
        MakeupInfo defaultMakeupInfo4 = new MakeupInfo("makeup_eyelash", "eyelashk", 40);

        makeupList.add(defaultMakeupInfo1);
        makeupList.add(defaultMakeupInfo2);
        makeupList.add(defaultMakeupInfo3);
        makeupList.add(defaultMakeupInfo4);

        float[] mNewBeautifyParamsTypeBase = {0f, 0f, 0f, 0f, 0f, 0.5f};
        float[] mNewBeautifyParamsTypeProfessional = {0.40f, 0.35f, 0.1f, 0.12f, 0f};
        float[] mNewBeautifyParamsTypeMicro = {0f, 0.15f, 0.2f, 0f, 0.3f,
                0.2f, 0f, 0.15f, 0f, 0f,
                0f, 0.1f, 0.15f, 0.33f, 0.50f,
                0.5f, 0f, 0.0f, 0f};
        float[] mNewBeautifyParamsTypeAdjust = {0.0f, 0.0f, 0.5f, 0.2f};

        return new FullBeautyItem(MultiLanguageUtils.getStr(R.string.whole_zhiboyuanqi), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_zhiboyuanqi_unselected),
                BitmapFactory.decodeResource(getResources(), R.drawable.beauty_zhiboyuanqi_selected),
                mNewBeautifyParamsTypeBase, mNewBeautifyParamsTypeProfessional, mNewBeautifyParamsTypeMicro, mNewBeautifyParamsTypeAdjust,
                makeupList, filterInfo);
    }

    @Override
    public FullBeautyItem fullBeautyItemZhiBoYuanHua() {
        FilterInfo filterInfo = new FilterInfo("filter_portrait", "ziranguang", 100);

        float[] mNewBeautifyParamsTypeBase = {0f, 0f, 0f, 0f, 0f, 0.5f};
        float[] mNewBeautifyParamsTypeProfessional = {0.30f, 0.3f, 0.2f, 0f, 0f};
        float[] mNewBeautifyParamsTypeMicro = {0f, 0.16f, 0f, 0f, 0f,
                0f, 0f, 0f, 0f, 0f,
                -0.5f, 0f, 0f, 0f, 0.0f,
                0f, 0f, 0.0f, 0f};
        float[] mNewBeautifyParamsTypeAdjust = {0.0f, 0.0f, 0.5f, 0.2f};

        return new FullBeautyItem(MultiLanguageUtils.getStr(R.string.whole_zhiboyuanhua), BitmapFactory.decodeResource(getResources(), R.drawable.beauty_zhiboyuanhua_unselected),
                BitmapFactory.decodeResource(getResources(), R.drawable.beauty_zhiboyuanhua_selected),
                mNewBeautifyParamsTypeBase, mNewBeautifyParamsTypeProfessional, mNewBeautifyParamsTypeMicro, mNewBeautifyParamsTypeAdjust,
                null, filterInfo);
    }

    private static Resources getResources() {
        return SenseMeApplication.getContext().getResources();
    }
}

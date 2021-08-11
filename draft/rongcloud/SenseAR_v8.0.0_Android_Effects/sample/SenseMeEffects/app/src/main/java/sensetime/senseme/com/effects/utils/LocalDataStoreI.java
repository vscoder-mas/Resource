package sensetime.senseme.com.effects.utils;

import java.util.ArrayList;
import java.util.HashMap;

import sensetime.senseme.com.effects.view.BeautyItem;
import sensetime.senseme.com.effects.view.BeautyOptionsItem;
import sensetime.senseme.com.effects.view.FullBeautyItem;
import sensetime.senseme.com.effects.view.MakeupItem;
import sensetime.senseme.com.effects.view.StickerOptionsItem;

public interface LocalDataStoreI {

    // 整妆所有本地素材
    HashMap<String, ArrayList<MakeupItem>> getMakeupLists();

    // 美颜标题
    ArrayList<BeautyOptionsItem> getBeautyOptionsList();

    // 贴纸标题
    ArrayList<StickerOptionsItem> getStickerOptionsList();

    // 美形
    ArrayList<BeautyItem> getMicroBeautyList();

    // 基础美颜
    ArrayList<BeautyItem> getBeautyBaseList();

    // 微整形
    ArrayList<BeautyItem> getProfessionalBeautyList();

    // 调整
    ArrayList<BeautyItem> getAdjustBeautyList();

    // 整体效果 all list
    ArrayList<FullBeautyItem> getFullBeautyList();

    // 1.default
    FullBeautyItem fullBeautyItemDefault();

    // 2.tianran
    FullBeautyItem fullBeautyItemTianRan();

    // 3.sweetgirl
    FullBeautyItem fullBeautyItemSweetGirl();

    // 4.oxygen
    FullBeautyItem fullBeautyItemOxyGen();

    // 自拍自然
    FullBeautyItem fullBeautyItemZiPaiZiRan();

    FullBeautyItem fullBeautyItemRedWine();

    FullBeautyItem fullBeautyItemWhiteTea();

    FullBeautyItem fullBeautyItemZhigan();

    FullBeautyItem fullBeautyItemSweet();

    FullBeautyItem fullBeautyItemWestern();

    FullBeautyItem fullBeautyItemDeep();

    // 自拍元气
    FullBeautyItem fullBeautyItemZiPaiYuanQi();

    // 直播自然
    FullBeautyItem fullBeautyItemZhiBoZiRan();

    // 直播元气
    FullBeautyItem fullBeautyItemZhiBoYuanQi();

    // 直播元画
    FullBeautyItem fullBeautyItemZhiBoYuanHua();

}

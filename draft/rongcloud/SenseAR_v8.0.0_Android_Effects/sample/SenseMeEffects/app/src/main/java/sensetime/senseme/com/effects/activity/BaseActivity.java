package sensetime.senseme.com.effects.activity;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

//import com.sensetime.sensearsourcemanager.SenseArMaterial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sensetime.senseme.com.effects.R;
import sensetime.senseme.com.effects.adapter.MakeupAdapter;
import sensetime.senseme.com.effects.adapter.StickerAdapter;
import sensetime.senseme.com.effects.utils.Constants;
import sensetime.senseme.com.effects.utils.MultiLanguageUtils;
import sensetime.senseme.com.effects.view.MakeUpOptionsItem;
import sensetime.senseme.com.effects.view.MakeupItem;
import sensetime.senseme.com.effects.view.StickerItem;

public class BaseActivity extends Activity {

    protected HashMap<String, MakeupAdapter> mMakeupAdapters = new HashMap<>();
    protected HashMap<String, StickerAdapter> mStickerAdapters = new HashMap<>();

    protected RecyclerView mMakeupOptionsRecycleView;
    protected RecyclerView mStickersRecycleView;
    protected HashMap<Integer, Integer> mMakeupOptionSelectedIndex = new HashMap<>();
    protected HashMap<String, Integer> mMakeupOptionIndex = new HashMap<>();

    public static final ArrayList<MakeUpOptionsItem> MAKE_UP_OPTIONS_LIST = new ArrayList<MakeUpOptionsItem>() {
        {
            add(new MakeUpOptionsItem("makeup_all", Constants.GROUP_MAKEUP_ALL));// 1整妆
            add(new MakeUpOptionsItem("makeup_lip", Constants.GROUP_MAKEUP_LIP));// 2口红
            add(new MakeUpOptionsItem("makeup_hairdye", Constants.GROUP_MAKEUP_HAIRDYE));// 3头发
            add(new MakeUpOptionsItem("makeup_blush", Constants.GROUP_MAKEUP_BLUSH));// 4腮红
            add(new MakeUpOptionsItem("makeup_highlight", Constants.GROUP_MAKEUP_HIGH_LIGHT));// 5修容
            add(new MakeUpOptionsItem("makeup_brow", Constants.GROUP_MAKEUP_BROW));// 6眉毛
            add(new MakeUpOptionsItem("makeup_eye", Constants.GROUP_MAKEUP_EYE));// 7眼影
            add(new MakeUpOptionsItem("makeup_eyeliner", Constants.GROUP_MAKEUP_EYELINER));// 8眼线
            add(new MakeUpOptionsItem("makeup_eyelash", Constants.GROUP_MAKEUP_EYELASH));// 9眼睫毛
            add(new MakeUpOptionsItem("makeup_eyeball", Constants.GROUP_MAKEUP_EYEBALL));// 10美瞳
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MultiLanguageUtils.appContext);
    }

    /**
     * 直接变更ui ,不通过数据驱动，相比notify data change 反应会快些
     */
    public void notifyStickerViewState(StickerItem stickerItem, int position, String name) {
        RecyclerView.ViewHolder viewHolder = mStickersRecycleView.findViewHolderForAdapterPosition(position);
        //排除不必要变更
        if (viewHolder == null || mStickersRecycleView.getAdapter() != mStickerAdapters.get(name))
            return;
        View itemView = viewHolder.itemView;
        ImageView normalState = (ImageView) itemView.findViewById(R.id.normalState);
        ImageView downloadingState = (ImageView) itemView.findViewById(R.id.downloadingState);
        ViewGroup loadingStateParent = (ViewGroup) itemView.findViewById(R.id.loadingStateParent);
        switch (stickerItem.state) {
            case NORMAL_STATE:
                //设置为等待下载状态
                if (normalState.getVisibility() != View.VISIBLE) {
                    Log.i("StickerAdapter", "NORMAL_STATE");
                    normalState.setVisibility(View.VISIBLE);
                    downloadingState.setVisibility((View.INVISIBLE));
                    downloadingState.setActivated(false);
                    loadingStateParent.setVisibility((View.INVISIBLE));
                }
                break;
            case LOADING_STATE:
                //设置为loading 状态
                if (downloadingState.getVisibility() != View.VISIBLE) {
                    Log.i("StickerAdapter", "LOADING_STATE");
                    normalState.setVisibility(View.INVISIBLE);
                    downloadingState.setActivated(true);
                    downloadingState.setVisibility((View.VISIBLE));
                    loadingStateParent.setVisibility((View.VISIBLE));
                }
                break;
            case DONE_STATE:
                //设置为下载完成状态
                if (normalState.getVisibility() != View.INVISIBLE || downloadingState.getVisibility() != View.INVISIBLE) {
                    Log.i("StickerAdapter", "DONE_STATE");
                    normalState.setVisibility(View.INVISIBLE);
                    downloadingState.setVisibility((View.INVISIBLE));
                    downloadingState.setActivated(false);
                    loadingStateParent.setVisibility((View.INVISIBLE));
                }
                break;
        }
    }

    public void notifyMakeUpViewState(MakeupItem stickerItem, int position, String groupName) {
        RecyclerView.ViewHolder viewHolder = mMakeupOptionsRecycleView.findViewHolderForAdapterPosition(position);
        //排除不必要变更
        if (viewHolder == null || mMakeupOptionsRecycleView.getAdapter() != mMakeupAdapters.get(groupName))
            return;
        View itemView = viewHolder.itemView;
        ImageView normalState = (ImageView) itemView.findViewById(R.id.normalState);
        ImageView downloadingState = (ImageView) itemView.findViewById(R.id.downloadingState);
        ViewGroup loadingStateParent = (ViewGroup) itemView.findViewById(R.id.loadingStateParent);
        switch (stickerItem.state) {
            case NORMAL_STATE:
                //设置为等待下载状态
                if (normalState.getVisibility() != View.VISIBLE) {
                    Log.i("StickerAdapter", "NORMAL_STATE");
                    normalState.setVisibility(View.VISIBLE);
                    downloadingState.setVisibility((View.INVISIBLE));
                    downloadingState.setActivated(false);
                    loadingStateParent.setVisibility((View.INVISIBLE));
                }
                break;
            case LOADING_STATE:
                //设置为loading 状态
                if (downloadingState.getVisibility() != View.VISIBLE) {
                    Log.i("StickerAdapter", "LOADING_STATE");
                    normalState.setVisibility(View.INVISIBLE);
                    downloadingState.setActivated(true);
                    downloadingState.setVisibility((View.VISIBLE));
                    loadingStateParent.setVisibility((View.VISIBLE));
                }
                break;
            case DONE_STATE:
                //设置为下载完成状态
                if (normalState.getVisibility() != View.INVISIBLE || downloadingState.getVisibility() != View.INVISIBLE) {
                    Log.i("StickerAdapter", "DONE_STATE");
                    normalState.setVisibility(View.INVISIBLE);
                    downloadingState.setVisibility((View.INVISIBLE));
                    downloadingState.setActivated(false);
                    loadingStateParent.setVisibility((View.INVISIBLE));
                }
                break;
        }
    }

//    protected SenseArMaterial getSenseArMaterialByName(List<SenseArMaterial> list, String name) {
//        SenseArMaterial sarm = null;
//        for (SenseArMaterial item : list) {
//            if (item.name.equals(name)) {
//                sarm = item;
//            }
//        }
//        return sarm;
//    }
}
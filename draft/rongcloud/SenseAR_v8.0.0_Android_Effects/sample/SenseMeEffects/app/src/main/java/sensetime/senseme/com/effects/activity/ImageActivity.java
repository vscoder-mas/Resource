//package sensetime.senseme.com.effects.activity;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.ActivityInfo;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.graphics.Rect;
//import android.media.MediaActionSound;
//import android.media.MediaScannerConnection;
//import android.net.Uri;
//import android.opengl.GLSurfaceView;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.recyclerview.widget.StaggeredGridLayoutManager;
//
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.SeekBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.sensetime.sensearsourcemanager.SenseArMaterial;
//import com.sensetime.sensearsourcemanager.SenseArMaterialService;
//import com.sensetime.sensearsourcemanager.SenseArMaterialType;
//import com.sensetime.stmobile.STEffectBeautyType;
//import com.sensetime.stmobile.model.STMobileMakeupType;
//
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//
//import sensetime.senseme.com.effects.R;
//import sensetime.senseme.com.effects.adapter.BeautyItemAdapter;
//import sensetime.senseme.com.effects.adapter.BeautyOptionsAdapter;
//import sensetime.senseme.com.effects.adapter.FilterAdapter;
//import sensetime.senseme.com.effects.adapter.FilterInfo;
//import sensetime.senseme.com.effects.adapter.FullBeautyItemAdapter;
//import sensetime.senseme.com.effects.adapter.MakeupAdapter;
//import sensetime.senseme.com.effects.adapter.NativeStickerAdapter;
//import sensetime.senseme.com.effects.adapter.StickerAdapter;
//import sensetime.senseme.com.effects.adapter.StickerOptionsAdapter;
//import sensetime.senseme.com.effects.display.ImageDisplay;
//import sensetime.senseme.com.effects.utils.Accelerometer;
//import sensetime.senseme.com.effects.utils.CollectionSortUtils;
//import sensetime.senseme.com.effects.utils.Constants;
//import sensetime.senseme.com.effects.utils.EffectInfoDataHelper;
//import sensetime.senseme.com.effects.utils.FileUtils;
//import sensetime.senseme.com.effects.utils.ImageUtils;
//import sensetime.senseme.com.effects.utils.LocalDataStore;
//import sensetime.senseme.com.effects.utils.LogUtils;
//import sensetime.senseme.com.effects.utils.MultiLanguageUtils;
//import sensetime.senseme.com.effects.utils.NetworkUtils;
//import sensetime.senseme.com.effects.STLicenseUtils;
//import sensetime.senseme.com.effects.utils.STUtils;
//import sensetime.senseme.com.effects.view.BeautyItem;
//import sensetime.senseme.com.effects.view.BeautyOptionsItem;
//import sensetime.senseme.com.effects.view.FilterItem;
//import sensetime.senseme.com.effects.view.FullBeautyItem;
//import sensetime.senseme.com.effects.view.IndicatorSeekBar;
//import sensetime.senseme.com.effects.view.MakeUpOptionsItem;
//import sensetime.senseme.com.effects.view.MakeupItem;
//import sensetime.senseme.com.effects.view.StickerItem;
//import sensetime.senseme.com.effects.view.StickerOptionsItem;
//import sensetime.senseme.com.effects.view.StickerState;
//
//import static sensetime.senseme.com.effects.utils.Constants.APPID;
//import static sensetime.senseme.com.effects.utils.Constants.APPKEY;
//import static sensetime.senseme.com.effects.utils.Constants.GROUP_NEW;
//import static sensetime.senseme.com.effects.utils.Constants.mNewBeautifyParamsTypeAdjust;
//import static sensetime.senseme.com.effects.utils.Constants.mNewBeautifyParamsTypeBase;
//import static sensetime.senseme.com.effects.utils.Constants.mNewBeautifyParamsTypeMicro;
//import static sensetime.senseme.com.effects.utils.Constants.mNewBeautifyParamsTypeProfessional;
//
//public class ImageActivity extends BaseActivity implements View.OnClickListener {
//    private final static String TAG = "ImageActivity";
//    protected Accelerometer mAccelerometer = null;
//    protected ImageDisplay mImageDisplay;
//    protected FrameLayout mPreviewFrameLayout;
//
//    protected String mImagePath;
//    protected String mSavePath;
//
//    protected RecyclerView mStickersRecycleView;
//    protected RecyclerView mStickerOptionsRecycleView, mFilterOptionsRecycleView, mMakeupOptionsRecycleView;
//    protected RecyclerView mBeautyBaseRecycleView;
//    protected StickerOptionsAdapter mStickerOptionsAdapter;
//    protected BeautyOptionsAdapter mBeautyOptionsAdapter;
//    protected BeautyItemAdapter mBeautyBaseAdapter, mBeautyProfessionalAdapter, mAdjustAdapter, mMicroAdapter;
//    protected ArrayList<StickerOptionsItem> mStickerOptionsList = new ArrayList<>();
//    protected ArrayList<StickerItem> mNewStickers, mAddPackageStickers;
//    ;
//    protected ArrayList<BeautyOptionsItem> mBeautyOptionsList;
//    private View gpFullBeautySeekBar;
//    protected HashMap<String, StickerAdapter> mStickerAdapters = new HashMap<>();
//    //    private HashMap<String, NewStickerAdapter> mNewStickerAdapters = new HashMap<>();
//    protected HashMap<String, NativeStickerAdapter> mNativeStickerAdapters = new HashMap<>();
//    protected HashMap<String, BeautyItemAdapter> mBeautyItemAdapters = new HashMap<>();
//    protected HashMap<String, ArrayList<StickerItem>> mStickerlists = new HashMap<>();
//    protected HashMap<String, ArrayList<BeautyItem>> mBeautylists = new HashMap<>();
//    protected HashMap<Integer, String> mBeautyOption = new HashMap<>();
//    protected HashMap<Integer, Integer> mBeautyOptionSelectedIndex = new HashMap<>();
//    protected Map<Integer, Integer> mStickerPackageMap;
//    protected int mCurrentNewStickerPosition = -1;
//
//    protected HashMap<String, MakeupAdapter> mMakeupAdapters = new HashMap<>();
//    protected HashMap<String, ArrayList<MakeupItem>> mMakeupLists = new HashMap<>();
//
//
//    protected HashMap<Integer, Integer> mMakeupStrength = new HashMap<>();
//    private HashMap<Integer, Integer> mDefaultMakeupStrength = new HashMap<>();
//
//    protected HashMap<String, FilterAdapter> mFilterAdapters = new HashMap<>();
//    protected HashMap<String, ArrayList<FilterItem>> mFilterLists = new HashMap<>();
//
//    protected TextView mSavingTv, mResetTextView, mResetZeroTextView;
//    protected TextView mAttributeText;
//    protected TextView mShowOriginBtn1, mShowOriginBtn2, mShowOriginBtn3;
//    protected IndicatorSeekBar mIndicatorSeekbar, mIndicatorSeekbarNew;
//
//    protected LinearLayout mFilterGroupsLinearLayout, mFilterGroupPortrait, mFilterGroupStillLife, mFilterGroupScenery, mFilterGroupFood;
//    protected LinearLayout mMakeupGroupLip, mMakeupGroupCheeks, mMakeupGroupFace, mMakeupGroupBrow, mMakeupGroupEye, mMakeupGroupEyeLiner, mMakeupGroupEyeLash, mMakeupGroupEyeBall, mMakeupGroupHairDye, mMakeupGroupTypeAll;
//    protected RelativeLayout mFilterIconsRelativeLayout, mFilterStrengthLayout, mMakeupIconsRelativeLayout, mMakeupGroupRelativeLayout;
//    protected ImageView mFilterGroupBack, mMakeupGroupBack;
//    protected TextView mFilterGroupName, mFilterStrengthText;
//    protected TextView mMakeupGroupName;
//    protected SeekBar mFilterStrengthBar;
//    protected int mFilterStrength;
//    private int mDefaultFilterStrength;
//    protected int mCurrentFilterGroupIndex = -1;
//    protected int mCurrentFilterIndex = -1;
//    protected int mCurrentMakeupGroupIndex = -1;
//    protected int mCurrentBeautyIndex;
//
//    protected FullBeautyItemAdapter mBeautyFullAdapter;
//    protected ArrayList<FullBeautyItem> mBeautyFullItem = new ArrayList<FullBeautyItem>();
//    protected int mCurrentFullBeautyIndex = 0;
//
//    protected Context mContext;
//    protected Bitmap mImageBitmap;
//
//    private float fullBeautyFilterScale = 0.85f;
//    private float fullBeautyMakeupScale = 0.85f;
//
//    public static final int MSG_SAVING_IMG = 1;
//    public static final int MSG_SAVED_IMG = 2;
//    //    public final static int MSG_NEED_UPDATE_STICKER_MAP = 105;
//    public final static int MSG_NEED_REPLACE_STICKER_MAP = 106;
//    public final static int MSG_NEED_SHOW_TOO_MUCH_STICKER_TIPS = 107;
//    public final static int MSG_NEED_UPDATE_BEAUTY_PARAMS = 108;
//    public final static int MSG_NEED_RECOVERUI = 110;
//
//    public static final int PERMISSION_REQUEST_WRITE_PERMISSION = 101;
//    protected boolean mPermissionDialogShowing = false;
//    protected final int REQUEST_PICK_IMAGE = 1;
//    private boolean isFromUserSeekBar = true;
//    private int lastIndex = 0;
//
//    protected LinearLayout mStickerOptionsSwitch;
//    protected RelativeLayout mStickerOptions;
//    protected RecyclerView mStickerIcons;
//    protected boolean mIsStickerOptionsOpen = false;
//    protected int mCurrentStickerOptionsIndex = -1;
//    protected int mCurrentStickerPosition = -1;
//
//    protected LinearLayout mBeautyOptionsSwitch, mBaseBeautyOptions;
//    protected RecyclerView mFilterIcons, mBeautyOptionsRecycleView;
//    protected boolean mIsBeautyOptionsOpen = false;
//    protected int mBeautyOptionsPosition = 0;
//    protected ArrayList<SeekBar> mBeautyParamsSeekBarList = new ArrayList<SeekBar>();
//
//    protected ImageView mBeautyOptionsSwitchIcon, mStickerOptionsSwitchIcon;
//    protected TextView mBeautyOptionsSwitchText, mStickerOptionsSwitchText;
//    protected RelativeLayout mFilterAndBeautyOptionView;
//    protected LinearLayout mSelectOptions;
//    //记录用户最后一次点击的素材id ,包括还未下载的，方便下载完成后，直接应用素材
//    protected String preMaterialId = "";
//    private MediaActionSound mMediaActionSound;
//
//    protected Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//
//            switch (msg.what) {
//                case MSG_SAVING_IMG:
//                    if (Constants.ACTIVITY_MODE_FOR_TV) {
//                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date());
//                        File mediaFile = new File(mSavePath + File.separator +
//                                "IMG_" + timeStamp + ".png");
//                        saveToSDCard(mediaFile, mImageDisplay.getBitmap());
//                    } else {
//                        saveToSDCard(FileUtils.getOutputMediaFile(), mImageDisplay.getBitmap());
//                    }
//
//                    break;
//                case MSG_SAVED_IMG:
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            mSavingTv.setVisibility(View.GONE);
//                        }
//                    });
//                    break;
////                case MSG_NEED_UPDATE_STICKER_MAP:
////                    int packageId = msg.arg1;
////                    mStickerPackageMap.put(mCurrentNewStickerPosition, packageId);
////                    break;
//
//                case MSG_NEED_REPLACE_STICKER_MAP:
//                    int oldPackageId = msg.arg1;
//                    int newPackageId = msg.arg2;
//                    if (mStickerPackageMap == null) return;
//                    for (Integer index : mStickerPackageMap.keySet()) {
//                        int stickerId = mStickerPackageMap.get(index);//得到每个key多对用value的值
//
//                        if (stickerId == oldPackageId) {
//                            mStickerPackageMap.put(index, newPackageId);
//                        }
//                    }
//                    break;
//
//                case MSG_NEED_SHOW_TOO_MUCH_STICKER_TIPS:
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(mContext, "添加太多贴纸了", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    break;
//
//                case MSG_NEED_UPDATE_BEAUTY_PARAMS:
//                    updateBeautyParamsFromPackage();
//                    break;
//                case MSG_NEED_RECOVERUI:
//                    recoverUI();
//                    break;
//            }
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EffectInfoDataHelper.setType(EffectInfoDataHelper.Type.IMG);
//        if (Constants.ACTIVITY_MODE_LANDSCAPE) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        } else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
//
//        //进程后台时被系统强制kill，需重新checkLicense
//        if (savedInstanceState != null && savedInstanceState.getBoolean("process_killed")) {
//            if (!STLicenseUtils.checkLicense(this)) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "请检查License授权！", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
////            if (!STLicenseUtils.checkLicense(this, SenseArMaterialService.shareInstance().getLicenseData())) {
////                runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        Toast.makeText(getApplicationContext(), "请检查License授权！", Toast.LENGTH_SHORT).show();
////                    }
////                });
////            }
//        }
//
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.activity_main);
//        MultiLanguageUtils.initLanguageConfig();
//        mContext = this;
//
//        initView();
//        initStickerListFromNet();
//        initEvents();
//
//        resetFilterView(false);
//        mShowOriginBtn1.setVisibility(View.VISIBLE);
//        mShowOriginBtn2.setVisibility(View.INVISIBLE);
//        mShowOriginBtn3.setVisibility(View.INVISIBLE);
//
//        //设置默认美颜/滤镜/美妆效果
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (mBeautyFullItem.size()>0) {
//                setFullBeauty(mBeautyFullItem.get(0));
//                EffectInfoDataHelper.getInstance().fullBeautyName = "Default";
//                }
//            }
//        }, 2000);
//        mMediaActionSound = new MediaActionSound();
//
//        if (Constants.ACTIVITY_MODE_FOR_TV) {
//            findViewById(R.id.tv_capture).setVisibility(View.INVISIBLE);
//        }
//
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                initDefaultFullBeautySeekBar();
//            }
//        }, 200);
//        mDefaultMakeupStrength.clear();
//        mDefaultMakeupStrength.putAll(mMakeupStrength);
//        isFromUserSeekBar = false;
//    }
//
//    private void initDefaultFullBeautySeekBar() {
//        mBeautyFullItem.get(0).setFilterProgress(85);
//        mBeautyFullItem.get(0).setMakeupAllProgress(85);
//        mIndicatorSeekbar.setProgress(85);
//        mIndicatorSeekbarNew.setProgress(85);
//    }
//
//    private View[][] makeupViews, filterViews;
//
//    public void setMakeUpViewSelected(boolean selected, int... selectedIndexes) {
//        if (selectedIndexes.length == 1 && selectedIndexes[0] == -1) {
//            for (View[] views : makeupViews)
//                for (View v : views) v.setSelected(selected);
//        } else {
//            for (int index : selectedIndexes) {
//                View[] view = makeupViews[index];
//                for (View v : view) {
//                    v.setSelected(selected);
//                }
//            }
//        }
//    }
//
//    protected void initDisplay() {
//        GLSurfaceView glSurfaceView = (GLSurfaceView) findViewById(R.id.id_gl_sv);
//        mImageDisplay = new ImageDisplay(getApplicationContext(), glSurfaceView, mHandler);
//
//        if (Constants.ACTIVITY_MODE_FOR_TV) {
//            mImagePath = this.getIntent().getStringExtra("ImagePath");
//            if (mImagePath != null) {
//                File file = new File(mImagePath);
//                mSavePath = file.getParent();
//            }
//
//            try {
//                FileInputStream fis = new FileInputStream(mImagePath);
//                mImageBitmap = BitmapFactory.decodeStream(fis);
//                mImageDisplay.setImageBitmap(mImageBitmap);
//            } catch (Exception e) {
//                mImageBitmap = null;
//            }
//        } else {
//            int mode = 0;
//            mode = this.getIntent().getBundleExtra("bundle").getInt("mode");
//
//            switch (mode) {
//                case LoadImageActivity.MODE_GALLERY_IMAGE:
//
//                    Uri imageUri = this.getIntent().getParcelableExtra("imageUri");
//                    if ("file".equals(imageUri.getScheme())) {
//                        mImageBitmap = STUtils.getBitmapFromFile(imageUri);
//                    } else {
//                        mImageBitmap = STUtils.getBitmapAfterRotate(imageUri, mContext);
//                    }
//                    break;
//
//                case LoadImageActivity.MODE_TAKE_PHOTO:
//                    Uri photoUri = this.getIntent().getParcelableExtra("imageUri");
//                    mImageBitmap = STUtils.getBitmapFromFileAfterRotate(photoUri);
//                    break;
//            }
//        }
//
//        mImageDisplay.setImageBitmap(mImageBitmap);
//    }
//
//    private void setFilterViewSelected(int index) {
//        if (index == -1) {
//            for (View[] view : filterViews)
//                for (View v : view) v.setSelected(false);
//        } else {
//            for (View view : filterViews[index]) view.setSelected(true);
//        }
//    }
//
//    protected void initView() {
//        ImageView iv_makeup_group_all = (ImageView) findViewById(R.id.iv_makeup_group_all);//1整妆
//        TextView tv_makeup_group_all = (TextView) findViewById(R.id.tv_makeup_group_all);
//
//        ImageView iv_makeup_group_hairdye = (ImageView) findViewById(R.id.iv_makeup_group_hairdye);//染发
//        TextView tv_makeup_group_hairdye = (TextView) findViewById(R.id.tv_makeup_group_hairdye);
//
//        ImageView iv_makeup_group_lip = (ImageView) findViewById(R.id.iv_makeup_group_lip);// 3口红
//        TextView tv_makeup_group_lip = (TextView) findViewById(R.id.tv_makeup_group_lip);
//
//        ImageView iv_makeup_group_cheeks = (ImageView) findViewById(R.id.iv_makeup_group_cheeks);//4腮红
//        TextView tv_makeup_group_cheeks = (TextView) findViewById(R.id.tv_makeup_group_cheeks);
//
//        ImageView iv_makeup_group_face = (ImageView) findViewById(R.id.iv_makeup_group_face);//5.修容
//        TextView tv_makeup_group_face = (TextView) findViewById(R.id.tv_makeup_group_face);
//
//        ImageView iv_makeup_group_brow = (ImageView) findViewById(R.id.iv_makeup_group_brow);// 6.眉毛
//        TextView tv_makeup_group_brow = (TextView) findViewById(R.id.tv_makeup_group_brow);
//
//        ImageView iv_makeup_group_eye = (ImageView) findViewById(R.id.iv_makeup_group_eye);//7.眼影
//        TextView tv_makeup_group_eye = (TextView) findViewById(R.id.tv_makeup_group_eye);
//
//        ImageView iv_makeup_group_eyeliner = (ImageView) findViewById(R.id.iv_makeup_group_eyeliner);//8.眼线
//        TextView tv_makeup_group_eyeliner = (TextView) findViewById(R.id.tv_makeup_group_eyeliner);
//
//        ImageView iv_makeup_group_eyelash = (ImageView) findViewById(R.id.iv_makeup_group_eyelash);//9.眼睫毛
//        TextView tv_makeup_group_eyelash = (TextView) findViewById(R.id.tv_makeup_group_eyelash);
//        mResetTextView = (TextView) findViewById(R.id.reset);
//        mResetZeroTextView = (TextView) findViewById(R.id.reset_zero);
//
//        mResetTextView.setOnClickListener(this);
//        mResetZeroTextView.setOnClickListener(this);
//
//        ImageView iv_makeup_group_eyeball = (ImageView) findViewById(R.id.iv_makeup_group_eyeball);
//        TextView tv_makeup_group_eyeball = (TextView) findViewById(R.id.tv_makeup_group_eyeball);
//
//        ImageView iv_filter_group_portrait = (ImageView) findViewById(R.id.iv_filter_group_portrait);
//        // 滤镜
//        TextView tv_filter_group_portrait = (TextView) findViewById(R.id.tv_filter_group_portrait);
//        ImageView iv_filter_group_scenery = (ImageView) findViewById(R.id.iv_filter_group_scenery);
//        TextView tv_filter_group_scenery = (TextView) findViewById(R.id.tv_filter_group_scenery);
//        ImageView iv_filter_group_still_life = (ImageView) findViewById(R.id.iv_filter_group_still_life);
//        TextView tv_filter_group_still_life = (TextView) findViewById(R.id.tv_filter_group_still_life);
//        ImageView iv_filter_group_food = (ImageView) findViewById(R.id.iv_filter_group_food);
//        TextView tv_filter_group_food = (TextView) findViewById(R.id.tv_filter_group_food);
//
//        makeupViews = new View[][]{// 和Constants STMobileMakeupType位置对应
//                {iv_makeup_group_hairdye, tv_makeup_group_hairdye},// 0.default
//                {iv_makeup_group_eye, tv_makeup_group_eye},//1.眼部美妆
//                {iv_makeup_group_cheeks, tv_makeup_group_cheeks},// //2.腮部
//                {iv_makeup_group_lip, tv_makeup_group_lip},//3.口红
//                {iv_makeup_group_face, tv_makeup_group_face},// 4.修容
//                {iv_makeup_group_brow, tv_makeup_group_brow},// 5.眉毛
//                {iv_makeup_group_eyeliner, tv_makeup_group_eyeliner},// 6.EYELINER
//                {iv_makeup_group_eyelash, tv_makeup_group_eyelash},// 7.eye_lash
//                {iv_makeup_group_eyeball, tv_makeup_group_eyeball},// 8.eyeball
//                {iv_makeup_group_hairdye, tv_makeup_group_hairdye}, // 9.染发
//                {iv_makeup_group_all, tv_makeup_group_all},// 10.整妆
//                {iv_makeup_group_all, tv_makeup_group_all}
//        };
//
//        filterViews = new View[][]{
//                {iv_filter_group_portrait, tv_filter_group_portrait},
//                {iv_filter_group_scenery, tv_filter_group_scenery},
//                {iv_filter_group_still_life, tv_filter_group_still_life},
//                {iv_filter_group_food, tv_filter_group_food},
//        };
//
//        gpFullBeautySeekBar = findViewById(R.id.gp_full_beauty_seek_bar);
//        mBeautyOption.put(0, "fullBeauty");
//        //copy model file to sdcard
//        FileUtils.copyModelsFiles(this, "models");
//
//        mAccelerometer = new Accelerometer(getApplicationContext());
//        mPreviewFrameLayout = (FrameLayout) findViewById(R.id.id_preview_layout);
//        initDisplay();
//
//        mIndicatorSeekbarNew = (IndicatorSeekBar) findViewById(R.id.beauty_item_seekbar_new);
//        mIndicatorSeekbarNew.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (mBeautyOptionsPosition == 0) {
//                    mIndicatorSeekbarNew.updateTextView(progress);
//                    if (mCurrentFullBeautyIndex != -1)
//                        mBeautyFullItem.get(mCurrentFullBeautyIndex).setMakeupAllProgress(progress);
//                    if (fromUser) {
//                        for (Map.Entry<Integer, Integer> entry : mMakeupStrength.entrySet()) {
//                            Integer itValue = entry.getValue();
//                            Integer itKey = entry.getKey();
//                            float scale = progress / 100f;
//                            mImageDisplay.setStrengthForType(itKey, ((int) (itValue * scale)) / 100f);
//                            mImageDisplay.setBeautyParam(410, ((int) (itValue * scale)) / 100f);
//
//                            isFromUserSeekBar = false;
//                            fullBeautyMakeupScale = scale;
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//
//        mIndicatorSeekbar = (IndicatorSeekBar) findViewById(R.id.beauty_item_seekbar);
//        mIndicatorSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser) {
//                    if (checkMicroType(mBeautyOptionsPosition)) {
//                        mIndicatorSeekbar.updateTextView(STUtils.convertToDisplay(progress));
//                        mImageDisplay.setBeautyParam(mCurrentBeautyIndex, (float) STUtils.convertToDisplay(progress) / 100f);
//                        mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(mBeautyOptionSelectedIndex.get(mBeautyOptionsPosition)).setProgress(STUtils.convertToDisplay(progress));
//                    } else {
//                        if (mBeautyOptionsPosition == 0) {
//                            setFullBeautyFilter(progress);
//                        } else {
//                            mIndicatorSeekbar.updateTextView(progress);
//                            mImageDisplay.setBeautyParam(mCurrentBeautyIndex, (float) progress / 100f);
//                            mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(mBeautyOptionSelectedIndex.get(mBeautyOptionsPosition)).setProgress(progress);
//
//                            if (mBeautyOptionsPosition == 1) {
//                                int[][] mutexArray = {{0, 1, 2}, {4, 5}};
//                                int subItemSelectedIndex = mBeautyOptionSelectedIndex.get(mBeautyOptionsPosition);
//                                for (int[] mutex : mutexArray) {
//                                    setMutex(subItemSelectedIndex, mutex);
//                                }
//                            }
//                        }
//                    }
//                    if (mBeautyOptionsPosition != 0) {
//                        mBeautyItemAdapters.get(mBeautyOption.get(mBeautyOptionsPosition)).notifyItemChanged(mBeautyOptionSelectedIndex.get(mBeautyOptionsPosition));
////                        if (!checkNeedBeauty()) {
////                            mCameraDisplay.enableBeautify(false);
////                        } else {
////                            mCameraDisplay.enableBeautify(true);
////                        }
//                    }
////                    if (mBeautyOptionsPosition!=0) {
////                        mBeautyItemAdapters.get(mBeautyOption.get(mBeautyOptionsPosition)).notifyItemChanged(mBeautyOptionSelectedIndex.get(mBeautyOptionsPosition));
////                        clearFullBeautyView();
////                    }
//
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//                EffectInfoDataHelper.getInstance().save();
//            }
//        });
//
////        Switch makeupSw = (Switch)findViewById(R.id.makeup_sw);
////        makeupSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
////            @Override
////            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
////                if(isChecked){
////                    mImageDisplay.setNeed240(true);
////                }else{
////                    mImageDisplay.setNeed240(false);
////                }
////            }
////        });
//
//        mBeautyBaseRecycleView = (RecyclerView) findViewById(R.id.rv_beauty_base);
//        LinearLayoutManager ms = new LinearLayoutManager(this);
//        ms.setOrientation(LinearLayoutManager.HORIZONTAL);
//        mBeautyBaseRecycleView.setLayoutManager(ms);
//        mBeautyBaseRecycleView.addItemDecoration(new BeautyItemDecoration(STUtils.dip2px(this, 15)));
//
//        ArrayList mBeautyBaseItem = LocalDataStore.getInstance().getBeautyBaseList();
//        for (int i = 0; i < mBeautyBaseItem.size(); i++) {
//            ((BeautyItem) mBeautyBaseItem.get(i)).setProgress((int) (mNewBeautifyParamsTypeBase[i] * 100));
//        }
//        mIndicatorSeekbar.getSeekBar().setProgress((int) (mNewBeautifyParamsTypeBase[0] * 100));
//        mIndicatorSeekbar.updateTextView((int) (mNewBeautifyParamsTypeBase[0] * 100));
//
//        mBeautylists.put("baseBeauty", mBeautyBaseItem);
//        mBeautyBaseAdapter = new BeautyItemAdapter(this, mBeautyBaseItem);
//        mBeautyItemAdapters.put("baseBeauty", mBeautyBaseAdapter);
//        mBeautyOption.put(1, "baseBeauty");
//        mBeautyBaseRecycleView.setAdapter(mBeautyBaseAdapter);
//
//        ArrayList mProfessionalBeautyItem = new ArrayList<>();
//        mProfessionalBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.reshape_shrink_face), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.beauty_shrink_face_unselected), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.beauty_shrink_face_selected)));
//        mProfessionalBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.reshape_enlarge_eye), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.beauty_enlargeeye_unselected), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.beauty_enlargeeye_selected)));
//        mProfessionalBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.reshape_shrink_jaw), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.beauty_thin_face_unselected), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.beauty_thin_face_selected)));
//        mProfessionalBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.reshape_narrow_face), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.beauty_narrow_face_unselected), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.beauty_narrow_face_selected)));
//        mProfessionalBeautyItem.add(new BeautyItem(MultiLanguageUtils.getStr(R.string.reshape_round_eye), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.beauty_round_eye_unselected), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.beauty_round_eye_selected)));
//
//        for (int i = 0; i < mProfessionalBeautyItem.size(); i++) {
//            ((BeautyItem) mProfessionalBeautyItem.get(i)).setProgress((int) (mNewBeautifyParamsTypeProfessional[i] * 100));
//        }
//
//        mBeautylists.put("professionalBeauty", mProfessionalBeautyItem);
//        mBeautyProfessionalAdapter = new BeautyItemAdapter(this, mProfessionalBeautyItem);
//        mBeautyItemAdapters.put("professionalBeauty", mBeautyProfessionalAdapter);
//        mBeautyOption.put(2, "professionalBeauty");
//
//        ArrayList mMicroBeautyItem = LocalDataStore.getInstance().getMicroBeautyList();
//
//        for (int i = 0; i < mMicroBeautyItem.size(); i++) {
//            ((BeautyItem) mMicroBeautyItem.get(i)).setProgress((int) (mNewBeautifyParamsTypeMicro[i] * 100));
//        }
//        mBeautylists.put("microBeauty", mMicroBeautyItem);
//        mMicroAdapter = new BeautyItemAdapter(this, mMicroBeautyItem);
//        mBeautyItemAdapters.put("microBeauty", mMicroAdapter);
//        mBeautyOption.put(3, "microBeauty");
//
//        mMakeupOptionsRecycleView = (RecyclerView) findViewById(R.id.rv_makeup_icons);
//        mMakeupOptionsRecycleView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
//        mMakeupOptionsRecycleView.addItemDecoration(new SpaceItemDecoration(0));
//
//        mMakeupLists.put("makeup_lip", FileUtils.getMakeupFiles(this, "makeup_lip"));
//        mMakeupLists.put("makeup_highlight", FileUtils.getMakeupFiles(this, "makeup_highlight"));
//        mMakeupLists.put("makeup_blush", FileUtils.getMakeupFiles(this, "makeup_blush"));
//        mMakeupLists.put("makeup_brow", FileUtils.getMakeupFiles(this, "makeup_brow"));
//        mMakeupLists.put("makeup_eye", FileUtils.getMakeupFiles(this, "makeup_eye"));
//        mMakeupLists.put("makeup_eyeliner", FileUtils.getMakeupFiles(this, "makeup_eyeliner"));
//        mMakeupLists.put("makeup_eyelash", FileUtils.getMakeupFiles(this, "makeup_eyelash"));
//        mMakeupLists.put("makeup_eyeball", FileUtils.getMakeupFiles(this, "makeup_eyeball"));
//        mMakeupLists.put("makeup_hairdye", FileUtils.getMakeupFiles(this, "makeup_hairdye"));
//        mMakeupLists.put("makeup_all", FileUtils.getMakeupFiles(this, "makeup_all"));
//
//        mMakeupAdapters.put("makeup_lip", new MakeupAdapter(mMakeupLists.get("makeup_lip"), this));
//        mMakeupAdapters.put("makeup_highlight", new MakeupAdapter(mMakeupLists.get("makeup_highlight"), this));
//        mMakeupAdapters.put("makeup_blush", new MakeupAdapter(mMakeupLists.get("makeup_blush"), this));
//        mMakeupAdapters.put("makeup_brow", new MakeupAdapter(mMakeupLists.get("makeup_brow"), this));
//        mMakeupAdapters.put("makeup_eye", new MakeupAdapter(mMakeupLists.get("makeup_eye"), this));
//        mMakeupAdapters.put("makeup_eyeliner", new MakeupAdapter(mMakeupLists.get("makeup_eyeliner"), this));
//        mMakeupAdapters.put("makeup_eyelash", new MakeupAdapter(mMakeupLists.get("makeup_eyelash"), this));
//        mMakeupAdapters.put("makeup_eyeball", new MakeupAdapter(mMakeupLists.get("makeup_eyeball"), this));
//        mMakeupAdapters.put("makeup_hairdye", new MakeupAdapter(mMakeupLists.get("makeup_hairdye"), this));
//        mMakeupAdapters.put("makeup_all", new MakeupAdapter(mMakeupLists.get("makeup_all"), this));
//        for (MakeUpOptionsItem item : MAKE_UP_OPTIONS_LIST) {
//            initMakeUpListener(item.groupName, null);
//        }
//
//        mMakeupOptionIndex.put("makeup_lip", 3);
//        mMakeupOptionIndex.put("makeup_highlight", 4);
//        mMakeupOptionIndex.put("makeup_blush", 2);
//        mMakeupOptionIndex.put("makeup_brow", 5);
//        mMakeupOptionIndex.put("makeup_eye", 1);
//        mMakeupOptionIndex.put("makeup_eyeliner", 6);
//        mMakeupOptionIndex.put("makeup_eyelash", 7);
//        mMakeupOptionIndex.put("makeup_eyeball", 8);
//        mMakeupOptionIndex.put("makeup_hairdye", 9);
//        mMakeupOptionIndex.put("makeup_all", 10);
//
//        for (int i = 0; i < Constants.MAKEUP_TYPE_COUNT; i++) {
//            mMakeupOptionSelectedIndex.put(i, 0);
//            mMakeupStrength.put(i, 80);
//        }
//
//        mMakeupIconsRelativeLayout = (RelativeLayout) findViewById(R.id.rl_makeup_icons);
//        mMakeupGroupRelativeLayout = ((RelativeLayout) findViewById(R.id.rl_makeup_groups));
//        mMakeupGroupLip = (LinearLayout) findViewById(R.id.ll_makeup_group_lip);
//        mMakeupGroupLip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickMakeupGroup("makeup_lip", Constants.ST_MAKEUP_LIP);
//            }
//        });
//
//        mMakeupGroupCheeks = (LinearLayout) findViewById(R.id.ll_makeup_group_cheeks);
//        mMakeupGroupCheeks.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickMakeupGroup("makeup_blush", Constants.ST_MAKEUP_BLUSH);
//            }
//        });
//
//        mMakeupGroupFace = (LinearLayout) findViewById(R.id.ll_makeup_group_face);
//        mMakeupGroupFace.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickMakeupGroup("makeup_highlight", Constants.ST_MAKEUP_HIGHLIGHT);
//            }
//        });
//
//        mMakeupGroupBrow = (LinearLayout) findViewById(R.id.ll_makeup_group_brow);
//        mMakeupGroupBrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickMakeupGroup("makeup_brow", Constants.ST_MAKEUP_BROW);
//            }
//        });
//
//        mMakeupGroupEye = (LinearLayout) findViewById(R.id.ll_makeup_group_eye);
//        mMakeupGroupEye.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickMakeupGroup("makeup_eye", Constants.ST_MAKEUP_EYE);
//            }
//        });
//
//        mMakeupGroupEyeLiner = (LinearLayout) findViewById(R.id.ll_makeup_group_eyeliner);
//        mMakeupGroupEyeLiner.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickMakeupGroup("makeup_eyeliner", Constants.ST_MAKEUP_EYELINER);
//            }
//        });
//
//        mMakeupGroupEyeLash = (LinearLayout) findViewById(R.id.ll_makeup_group_eyelash);
//        mMakeupGroupEyeLash.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickMakeupGroup("makeup_eyelash", Constants.ST_MAKEUP_EYELASH);
//            }
//        });
//
//        mMakeupGroupEyeBall = (LinearLayout) findViewById(R.id.ll_makeup_group_eyeball);
//        mMakeupGroupEyeBall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickMakeupGroup("makeup_eyeball", Constants.ST_MAKEUP_EYEBALL);
//            }
//        });
//
//        mMakeupGroupHairDye = (LinearLayout) findViewById(R.id.ll_makeup_group_hairdye);
//        mMakeupGroupHairDye.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickMakeupGroup("makeup_hairdye", Constants.ST_MAKEUP_HAIR_DYE);
//                mMakeupAdapters.get("makeup_hairdye").setSelectedPosition(mMakeupOptionSelectedIndex.get(Constants.ST_MAKEUP_HAIR_DYE));
//            }
//        });
//
//        mMakeupGroupTypeAll = (LinearLayout) findViewById(R.id.ll_makeup_group_all);
//        mMakeupGroupTypeAll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickMakeupGroup("makeup_all", Constants.ST_MAKEUP_ALL);
//            }
//        });
//
//        mMakeupGroupBack = (ImageView) findViewById(R.id.iv_makeup_group);
//        mMakeupGroupBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mMakeupGroupRelativeLayout.setVisibility(View.VISIBLE);
//                mMakeupIconsRelativeLayout.setVisibility(View.INVISIBLE);
//                mFilterStrengthLayout.setVisibility(View.INVISIBLE);
//
//                setMakeUpViewSelected(mMakeupOptionSelectedIndex.get(Constants.ST_MAKEUP_LIP) != 0, Constants.ST_MAKEUP_LIP);
//                setMakeUpViewSelected(mMakeupOptionSelectedIndex.get(Constants.ST_MAKEUP_BLUSH) != 0, Constants.ST_MAKEUP_BLUSH);
//                setMakeUpViewSelected(mMakeupOptionSelectedIndex.get(Constants.ST_MAKEUP_HIGHLIGHT) != 0, Constants.ST_MAKEUP_HIGHLIGHT);
//                setMakeUpViewSelected(mMakeupOptionSelectedIndex.get(Constants.ST_MAKEUP_BROW) != 0, Constants.ST_MAKEUP_BROW);
//                setMakeUpViewSelected(mMakeupOptionSelectedIndex.get(Constants.ST_MAKEUP_EYE) != 0, Constants.ST_MAKEUP_EYE);
//                setMakeUpViewSelected(mMakeupOptionSelectedIndex.get(Constants.ST_MAKEUP_EYELINER) != 0, Constants.ST_MAKEUP_EYELINER);
//                setMakeUpViewSelected(mMakeupOptionSelectedIndex.get(Constants.ST_MAKEUP_EYELASH) != 0, Constants.ST_MAKEUP_EYELASH);
//                setMakeUpViewSelected(mMakeupOptionSelectedIndex.get(Constants.ST_MAKEUP_EYEBALL) != 0, Constants.ST_MAKEUP_EYEBALL);
//                setMakeUpViewSelected(mMakeupOptionSelectedIndex.get(Constants.ST_MAKEUP_HAIR_DYE) != 0, Constants.ST_MAKEUP_HAIR_DYE);
//                setMakeUpViewSelected(mMakeupOptionSelectedIndex.get(Constants.ST_MAKEUP_ALL) != 0, Constants.ST_MAKEUP_ALL);
//            }
//        });
//        mMakeupGroupName = (TextView) findViewById(R.id.tv_makeup_group);
//
//        ArrayList mAdjustBeautyItem = LocalDataStore.getInstance().getAdjustBeautyList();
//        for (int i = 0; i < mAdjustBeautyItem.size(); i++) {
//            ((BeautyItem) mAdjustBeautyItem.get(i)).setProgress((int) (mNewBeautifyParamsTypeAdjust[i] * 100));
//        }
//        mBeautylists.put("adjustBeauty", mAdjustBeautyItem);
//        mAdjustAdapter = new BeautyItemAdapter(this, mAdjustBeautyItem);
//        mBeautyItemAdapters.put("adjustBeauty", mAdjustAdapter);
//        mBeautyOption.put(6, "adjustBeauty");
//
//        //整体效果
//        initFullBeauty();
//
//        mBeautyOptionSelectedIndex.put(0, 0);
//        mBeautyOptionSelectedIndex.put(1, 0);
//        mBeautyOptionSelectedIndex.put(2, 0);
//        mBeautyOptionSelectedIndex.put(3, 0);
//        mBeautyOptionSelectedIndex.put(6, 0);
//
//        mStickerOptionsRecycleView = (RecyclerView) findViewById(R.id.rv_sticker_options);
//        mStickerOptionsRecycleView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
//        mStickerOptionsRecycleView.addItemDecoration(new SpaceItemDecoration(0));
//
//        //layout elements
//        mStickersRecycleView = (RecyclerView) findViewById(R.id.rv_sticker_icons);
//
//        mStickersRecycleView.setLayoutManager(new GridLayoutManager(this, 6));
//        mStickersRecycleView.addItemDecoration(new SpaceItemDecoration(0));
//
//        mNewStickers = FileUtils.getStickerFiles(this, "newEngine");
//        mAddPackageStickers = FileUtils.getStickerFiles(this, "newEngine");
//        mStickerOptionsList = new ArrayList<>();
//        mStickerOptionsList = LocalDataStore.getInstance().getStickerOptionsList();
//
//        mNativeStickerAdapters.put("sticker_new_engine", new NativeStickerAdapter(mNewStickers, this));
//        mNativeStickerAdapters.put("sticker_add_package", new NativeStickerAdapter(mAddPackageStickers, this));
//
//        mNativeStickerAdapters.get("sticker_new_engine").notifyDataSetChanged();
//        mNativeStickerAdapters.get("sticker_add_package").notifyDataSetChanged();
//        initNativeStickerAdapter("sticker_new_engine", 0);
//        initAddPackageStickerAdapter("sticker_add_package", 0);
//        mStickerOptionsAdapter = new StickerOptionsAdapter(mStickerOptionsList, this);
//        mStickerOptionsAdapter.setSelectedPosition(0);
//        mStickerOptionsAdapter.notifyDataSetChanged();
//
//        findViewById(R.id.iv_close_sticker).setBackground(getResources().getDrawable(R.drawable.close_sticker_selected));
//
//        mStickerOptionsSwitch = (LinearLayout) findViewById(R.id.ll_sticker_options_switch);
//        mStickerOptionsSwitch.setOnClickListener(this);
//        mStickerOptions = (RelativeLayout) findViewById(R.id.rl_sticker_options);
//        mStickerIcons = (RecyclerView) findViewById(R.id.rv_sticker_icons);
//        mIsStickerOptionsOpen = false;
//
//        mFilterAndBeautyOptionView = (RelativeLayout) findViewById(R.id.rv_beauty_and_filter_options);
//        mBeautyOptionsRecycleView = (RecyclerView) findViewById(R.id.rv_beauty_options);
//        mBeautyOptionsRecycleView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
//        mBeautyOptionsRecycleView.addItemDecoration(new SpaceItemDecoration(0));
//
//        mBeautyOptionsList = new ArrayList<>();
//        mBeautyOptionsList.add(0, new BeautyOptionsItem(MultiLanguageUtils.getStr(R.string.menu_title_full_beauty)));
//        mBeautyOptionsList.add(1, new BeautyOptionsItem(MultiLanguageUtils.getStr(R.string.menu_title_base)));
//        mBeautyOptionsList.add(2, new BeautyOptionsItem(MultiLanguageUtils.getStr(R.string.menu_title_reshape)));
//        mBeautyOptionsList.add(3, new BeautyOptionsItem(MultiLanguageUtils.getStr(R.string.menu_title_plastic)));
//        mBeautyOptionsList.add(4, new BeautyOptionsItem(MultiLanguageUtils.getStr(R.string.menu_title_makeup)));
//        mBeautyOptionsList.add(5, new BeautyOptionsItem(MultiLanguageUtils.getStr(R.string.menu_title_filter)));
//        mBeautyOptionsList.add(6, new BeautyOptionsItem(MultiLanguageUtils.getStr(R.string.menu_title_tone)));
//
//        mBeautyOptionsAdapter = new BeautyOptionsAdapter(mBeautyOptionsList, this);
//        mBeautyOptionsRecycleView.setAdapter(mBeautyOptionsAdapter);
//
//        mBeautyOptionsSwitch = (LinearLayout) findViewById(R.id.ll_beauty_options_switch);
//        mBeautyOptionsSwitch.setOnClickListener(this);
//
//        mBaseBeautyOptions = (LinearLayout) findViewById(R.id.ll_base_beauty_options);
//        mBaseBeautyOptions.setOnClickListener(null);
//        mIsBeautyOptionsOpen = false;
//
//        mFilterOptionsRecycleView = (RecyclerView) findViewById(R.id.rv_filter_icons);
//        mFilterOptionsRecycleView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
//        mFilterOptionsRecycleView.addItemDecoration(new SpaceItemDecoration(0));
//
//        mFilterLists.put("filter_portrait", FileUtils.getFilterFiles(this, "filter_portrait"));
//        mFilterLists.put("filter_scenery", FileUtils.getFilterFiles(this, "filter_scenery"));
//        mFilterLists.put("filter_still_life", FileUtils.getFilterFiles(this, "filter_still_life"));
//        mFilterLists.put("filter_food", FileUtils.getFilterFiles(this, "filter_food"));
//
//        mFilterAdapters.put("filter_portrait", new FilterAdapter(mFilterLists.get("filter_portrait"), this));
//        mFilterAdapters.put("filter_scenery", new FilterAdapter(mFilterLists.get("filter_scenery"), this));
//        mFilterAdapters.put("filter_still_life", new FilterAdapter(mFilterLists.get("filter_still_life"), this));
//        mFilterAdapters.put("filter_food", new FilterAdapter(mFilterLists.get("filter_food"), this));
//
//        mFilterIconsRelativeLayout = (RelativeLayout) findViewById(R.id.rl_filter_icons);
//        mFilterGroupsLinearLayout = (LinearLayout) findViewById(R.id.ll_filter_groups);
//        mFilterGroupPortrait = (LinearLayout) findViewById(R.id.ll_filter_group_portrait);
//        mFilterGroupPortrait.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickFilterGroup("filter_portrait");
//            }
//        });
//        mFilterGroupScenery = (LinearLayout) findViewById(R.id.ll_filter_group_scenery);
//        mFilterGroupScenery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickFilterGroup("filter_scenery");
//            }
//        });
//        mFilterGroupStillLife = (LinearLayout) findViewById(R.id.ll_filter_group_still_life);
//        mFilterGroupStillLife.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickFilterGroup("filter_still_life");
//            }
//        });
//        mFilterGroupFood = (LinearLayout) findViewById(R.id.ll_filter_group_food);
//        mFilterGroupFood.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickFilterGroup("filter_food");
//            }
//        });
//
//        mFilterGroupBack = (ImageView) findViewById(R.id.iv_filter_group);
//        mFilterGroupBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mFilterGroupsLinearLayout.setVisibility(View.VISIBLE);
//                mFilterIconsRelativeLayout.setVisibility(View.INVISIBLE);
//                mFilterStrengthLayout.setVisibility(View.INVISIBLE);
//
//                mShowOriginBtn3.setVisibility(View.VISIBLE);
//            }
//        });
//        mFilterGroupName = (TextView) findViewById(R.id.tv_filter_group);
//        mFilterStrengthText = (TextView) findViewById(R.id.tv_filter_strength);
//
//        mFilterStrengthLayout = (RelativeLayout) findViewById(R.id.rv_filter_strength);
//        mFilterStrengthBar = (SeekBar) findViewById(R.id.sb_filter_strength);
//        mFilterStrengthBar.setProgress(91);
//        mFilterStrengthText.setText("91");
//        mFilterStrength = 91;
//        mFilterStrengthBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser) {
//                    if (mBeautyOptionsPosition == 5) {
//                        mFilterStrength = progress;
//                        mImageDisplay.setFilterStrength((float) progress / 100);
//                        mFilterStrengthText.setText(progress + "");
//                        if (fromUser) isFromUserSeekBar = true;
//                    } else if (mBeautyOptionsPosition == 4) {
//                        mImageDisplay.setStrengthForType(mCurrentMakeupGroupIndex, (float) progress / 100);
//                        mMakeupStrength.put(mCurrentMakeupGroupIndex, progress);
//                        mFilterStrengthText.setText(progress + "");
//                        if (fromUser) isFromUserSeekBar = true;
//                    }
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//        });
//
//        mStickerOptionsRecycleView.setAdapter(mStickerOptionsAdapter);
//        mFilterOptionsRecycleView.setAdapter(mFilterAdapters.get("filter_portrait"));
//        mFilterIcons = (RecyclerView) findViewById(R.id.rv_filter_icons);
//
//        mAttributeText = (TextView) findViewById(R.id.tv_face_attribute);
//        mAttributeText.setVisibility(View.VISIBLE);
//        mSavingTv = (TextView) findViewById(R.id.tv_saving_image);
//
//        findViewById(R.id.iv_setting_options_switch).setVisibility(View.INVISIBLE);
//        findViewById(R.id.iv_mode_picture).setVisibility(View.INVISIBLE);
//        findViewById(R.id.btn_capture_picture).setVisibility(View.INVISIBLE);
//        findViewById(R.id.ll_blank_view).setVisibility(View.GONE);
//
//        mSelectOptions = (LinearLayout) findViewById(R.id.ll_select_options);
//        mSelectOptions.setBackgroundColor(Color.parseColor("#00000000"));
//    }
//
//    private void clickMakeupGroup(String type, int groupIndex) {
//        if (mMakeupOptionSelectedIndex.get(10) != 0 && groupIndex != Constants.ST_MAKEUP_ALL) {
//            showMakeupTips();
//            return;
//        }
//
//        mCurrentMakeupGroupIndex = groupIndex;
//        if (type.equals("makeup_lip")) {
//            mMakeupGroupName.setText(MultiLanguageUtils.getStr(R.string.make_up_lipstick));
//            if (mMakeupOptionSelectedIndex.get(3) != 0) {
//                mFilterStrengthLayout.setVisibility(View.VISIBLE);
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_lip_selected));
//            } else {
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_lip_unselected));
//            }
//        } else if (type.equals("makeup_blush")) {
//            mMakeupGroupName.setText(MultiLanguageUtils.getStr(R.string.make_up_blusher));
//            if (mMakeupOptionSelectedIndex.get(2) != 0) {
//                mFilterStrengthLayout.setVisibility(View.VISIBLE);
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_cheeks_selected));
//            } else {
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_cheeks_unselected));
//            }
//        } else if (type.equals("makeup_highlight")) {
//            mMakeupGroupName.setText(MultiLanguageUtils.getStr(R.string.make_up_highlight));
//            if (mMakeupOptionSelectedIndex.get(4) != 0) {
//                mFilterStrengthLayout.setVisibility(View.VISIBLE);
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_face_selected));
//            } else {
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_face_unselected));
//            }
//        } else if (type.equals("makeup_brow")) {
//            mMakeupGroupName.setText(MultiLanguageUtils.getStr(R.string.make_up_brow));
//            if (mMakeupOptionSelectedIndex.get(5) != 0) {
//                mFilterStrengthLayout.setVisibility(View.VISIBLE);
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_brow_selected));
//            } else {
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_brow_unselected));
//            }
//        } else if (type.equals("makeup_eye")) {
//            mMakeupGroupName.setText(MultiLanguageUtils.getStr(R.string.make_up_eye_shadow));
//            if (mMakeupOptionSelectedIndex.get(1) != 0) {
//                mFilterStrengthLayout.setVisibility(View.VISIBLE);
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_eye_selected));
//            } else {
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_eye_unselected));
//            }
//        } else if (type.equals("makeup_eyeliner")) {
//            mMakeupGroupName.setText(MultiLanguageUtils.getStr(R.string.make_up_eye_liner));
//            if (mMakeupOptionSelectedIndex.get(6) != 0) {
//                mFilterStrengthLayout.setVisibility(View.VISIBLE);
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_eyeliner_selected));
//            } else {
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_eyeline_unselected));
//            }
//        } else if (type.equals("makeup_eyelash")) {
//            mMakeupGroupName.setText(MultiLanguageUtils.getStr(R.string.make_up_eyelash));
//            if (mMakeupOptionSelectedIndex.get(7) != 0) {
//                mFilterStrengthLayout.setVisibility(View.VISIBLE);
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_eyelash_selected));
//            } else {
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_eyelash_unselected));
//            }
//        } else if (type.equals("makeup_eyeball")) {
//            mMakeupGroupName.setText(MultiLanguageUtils.getStr(R.string.make_up_eyeball));
//            if (mMakeupOptionSelectedIndex.get(7) != 0) {
//                mFilterStrengthLayout.setVisibility(View.VISIBLE);
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_eyeball_selected));
//            } else {
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_eyeball_unselected));
//            }
//        } else if (type.equals("makeup_hairdye")) {
//            mMakeupGroupName.setText(MultiLanguageUtils.getStr(R.string.make_up_hair));
//            if (mMakeupOptionSelectedIndex.get(9) != 0) {
//                mFilterStrengthLayout.setVisibility(View.VISIBLE);
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_hairdye_selected));
//            } else {
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_hairdye_unselected));
//            }
//        } else if (type.equals("makeup_all")) {
//            mMakeupGroupName.setText(MultiLanguageUtils.getStr(R.string.make_up_whole));
//            if (mMakeupOptionSelectedIndex.get(10) != 0) {
//                mFilterStrengthLayout.setVisibility(View.VISIBLE);
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_all_selected));
//            } else {
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_all_unselected));
//            }
//        }
//
//        mMakeupGroupRelativeLayout.setVisibility(View.INVISIBLE);
//        mMakeupIconsRelativeLayout.setVisibility(View.VISIBLE);
//        int progress = mMakeupStrength.get(mCurrentMakeupGroupIndex);
//        if (!isFromUserSeekBar && mCurrentFullBeautyIndex != -1) {
//            progress = (Math.round(mDefaultMakeupStrength.get(mCurrentMakeupGroupIndex) * fullBeautyMakeupScale));
//        }
//        mFilterStrengthBar.setProgress(progress);
//        mFilterStrengthText.setText(String.valueOf(progress));
//        mMakeupStrength.put(mCurrentMakeupGroupIndex, progress);
//
//        mMakeupOptionsRecycleView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
//        mMakeupOptionsRecycleView.setAdapter(mMakeupAdapters.get(type));
//        mMakeupAdapters.get(type).setSelectedPosition(mMakeupOptionSelectedIndex.get(groupIndex));
//    }
//
//    private void clickFilterGroup(String type) {
//        mFilterGroupsLinearLayout.setVisibility(View.INVISIBLE);
//        mFilterIconsRelativeLayout.setVisibility(View.VISIBLE);
//
//        Log.d(TAG, "clickFilterGroup: mCurrentFilterIndex:" + mCurrentFilterIndex);
//        if (mCurrentFilterIndex != -1) {
//            mFilterStrengthLayout.setVisibility(View.VISIBLE);
//        } else {
//            mFilterStrengthLayout.setVisibility(View.INVISIBLE);
//        }
//
//        if (!isFromUserSeekBar && mCurrentFullBeautyIndex != -1) {
//            mFilterStrength = (Math.round(mDefaultFilterStrength * fullBeautyFilterScale));
//        }
//
//        mFilterStrengthBar.setProgress(mFilterStrength);
//        mFilterStrengthText.setText(String.valueOf(mFilterStrength));
//
//        mFilterOptionsRecycleView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
//        mFilterOptionsRecycleView.setAdapter(mFilterAdapters.get(type));
//        if (type.equals("filter_food")) {
//            mFilterGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.icon_food_selected));
//            mFilterGroupName.setText(MultiLanguageUtils.getStr(R.string.filter_food));
//        } else if (type.equals("filter_still_life")) {
//            mFilterGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.icon_still_life_selected));
//            mFilterGroupName.setText(MultiLanguageUtils.getStr(R.string.filter_still_life));
//        } else if (type.equals("filter_scenery")) {
//            mFilterGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.icon_scenery_selected));
//            mFilterGroupName.setText(MultiLanguageUtils.getStr(R.string.filter_scenery));
//        } else if (type.equals("filter_portrait")) {
//            mFilterGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.icon_portrait_selected));
//            mFilterGroupName.setText(MultiLanguageUtils.getStr(R.string.filter_portrait));
//        }
//    }
//
//    protected void initEvents() {
//
//        initStickerTabListener();
//        mFilterAdapters.get("filter_portrait").setClickFilterListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                resetFilterView(false);
//                int position = Integer.parseInt(v.getTag().toString());
//                mFilterAdapters.get("filter_portrait").setSelectedPosition(position);
//                mCurrentFilterGroupIndex = 0;
//                mCurrentFilterIndex = -1;
//
//                if (position == 0) {
//                    mImageDisplay.enableFilter(false);
//                } else {
//                    mImageDisplay.setFilterStyle("filter_portrait", mFilterLists.get("filter_portrait").get(position).model, mFilterLists.get("filter_portrait").get(position).name);
//                    mImageDisplay.enableFilter(true);
//                    mCurrentFilterIndex = position;
//
//                    mFilterStrengthLayout.setVisibility(View.VISIBLE);
//                    mFilterStrengthBar.setProgress(mFilterStrength);
//                    mShowOriginBtn1.setVisibility(View.INVISIBLE);
//                    mShowOriginBtn2.setVisibility(View.INVISIBLE);
//                    mShowOriginBtn3.setVisibility(View.VISIBLE);
//                    setFilterViewSelected(mCurrentFilterGroupIndex);
//                }
//
//                mFilterAdapters.get("filter_portrait").notifyDataSetChanged();
//                clearFullBeautyView();
//            }
//        });
//
//        mFilterAdapters.get("filter_scenery").setClickFilterListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                resetFilterView(false);
//                int position = Integer.parseInt(v.getTag().toString());
//                mFilterAdapters.get("filter_scenery").setSelectedPosition(position);
//                mCurrentFilterGroupIndex = 1;
//                mCurrentFilterIndex = -1;
//
//                if (position == 0) {
//                    mImageDisplay.enableFilter(false);
//                } else {
//                    mImageDisplay.setFilterStyle("filter_scenery", mFilterLists.get("filter_scenery").get(position).model, mFilterLists.get("filter_scenery").get(position).name);
//                    mImageDisplay.enableFilter(true);
//                    mCurrentFilterIndex = position;
//
//                    mFilterStrengthLayout.setVisibility(View.VISIBLE);
//                    mFilterStrengthBar.setProgress(mFilterStrength);
//                    mShowOriginBtn1.setVisibility(View.INVISIBLE);
//                    mShowOriginBtn2.setVisibility(View.INVISIBLE);
//                    mShowOriginBtn3.setVisibility(View.VISIBLE);
//                    setFilterViewSelected(mCurrentFilterGroupIndex);
//                }
//
//                mFilterAdapters.get("filter_scenery").notifyDataSetChanged();
//                clearFullBeautyView();
//            }
//        });
//
//        mFilterAdapters.get("filter_still_life").setClickFilterListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                resetFilterView(false);
//                int position = Integer.parseInt(v.getTag().toString());
//                mFilterAdapters.get("filter_still_life").setSelectedPosition(position);
//                mCurrentFilterGroupIndex = 2;
//                mCurrentFilterIndex = -1;
//
//                if (position == 0) {
//                    mImageDisplay.enableFilter(false);
//                } else {
//                    mImageDisplay.setFilterStyle("filter_still_life", mFilterLists.get("filter_still_life").get(position).model, mFilterLists.get("filter_scenery").get(position).name);
//                    mImageDisplay.enableFilter(true);
//                    mCurrentFilterIndex = position;
//
//                    mFilterStrengthLayout.setVisibility(View.VISIBLE);
//                    mFilterStrengthBar.setProgress(mFilterStrength);
//                    mShowOriginBtn1.setVisibility(View.INVISIBLE);
//                    mShowOriginBtn2.setVisibility(View.INVISIBLE);
//                    mShowOriginBtn3.setVisibility(View.VISIBLE);
//                    setFilterViewSelected(mCurrentFilterGroupIndex);
//                }
//
//                mFilterAdapters.get("filter_still_life").notifyDataSetChanged();
//                clearFullBeautyView();
//            }
//        });
//
//        mFilterAdapters.get("filter_food").setClickFilterListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                resetFilterView(false);
//                int position = Integer.parseInt(v.getTag().toString());
//                mFilterAdapters.get("filter_food").setSelectedPosition(position);
//                mCurrentFilterGroupIndex = 3;
//                mCurrentFilterIndex = -1;
//
//                if (position == 0) {
//                    mImageDisplay.enableFilter(false);
//                } else {
//                    mImageDisplay.setFilterStyle("filter_food", mFilterLists.get("filter_food").get(position).model, mFilterLists.get("filter_scenery").get(position).name);
//                    mImageDisplay.enableFilter(true);
//                    mCurrentFilterIndex = position;
//
//                    mFilterStrengthLayout.setVisibility(View.VISIBLE);
//                    mFilterStrengthBar.setProgress(mFilterStrength);
//                    mShowOriginBtn1.setVisibility(View.INVISIBLE);
//                    mShowOriginBtn2.setVisibility(View.INVISIBLE);
//                    mShowOriginBtn3.setVisibility(View.VISIBLE);
//                    setFilterViewSelected(mCurrentFilterGroupIndex);
//                }
//
//                mFilterAdapters.get("filter_food").notifyDataSetChanged();
//                clearFullBeautyView();
//            }
//        });
//        /*
//        for(final Map.Entry<String, MakeupAdapter> entry: mMakeupAdapters.entrySet()){
//            entry.getValue().setClickMakeupListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = Integer.parseInt(v.getTag().toString());
//
//                    if(position == 0){
//                        entry.getValue().setSelectedPosition(position);
//                        mMakeupOptionSelectedIndex.put(mMakeupOptionIndex.get(entry.getKey()), position);
//
//                        mFilterStrengthLayout.setVisibility(View.INVISIBLE);
//                        mImageDisplay.removeMakeupByType(mCurrentMakeupGroupIndex);
//                        updateMakeupOptions(mCurrentMakeupGroupIndex, false);
//                    }else if(position == mMakeupOptionSelectedIndex.get(mMakeupOptionIndex.get(entry.getKey()))){
//                        entry.getValue().setSelectedPosition(0);
//                        mMakeupOptionSelectedIndex.put(mMakeupOptionIndex.get(entry.getKey()), 0);
//
//                        mFilterStrengthLayout.setVisibility(View.INVISIBLE);
//                        mImageDisplay.removeMakeupByType(mCurrentMakeupGroupIndex);
//                        updateMakeupOptions(mCurrentMakeupGroupIndex, false);
//                    }else{
//                        if(mCurrentMakeupGroupIndex == Constants.ST_MAKEUP_ALL){
//                            resetMakeup(false, false);
//                        }
//
//                        entry.getValue().setSelectedPosition(position);
//                        mMakeupOptionSelectedIndex.put(mMakeupOptionIndex.get(entry.getKey()), position);
//
//                        mImageDisplay.setMakeupForType(mCurrentMakeupGroupIndex, mMakeupLists.get(getMakeupNameOfType(mCurrentMakeupGroupIndex)).get(position).path);
//                        mImageDisplay.setStrengthForType(mCurrentMakeupGroupIndex, (float)mMakeupStrength.get(mCurrentMakeupGroupIndex)/100.f);
//                        mFilterStrengthLayout.setVisibility(View.VISIBLE);
//                        mFilterStrengthBar.setProgress(mMakeupStrength.get(mCurrentMakeupGroupIndex));
//                        updateMakeupOptions(mCurrentMakeupGroupIndex, true);
//                    }
//
//                    if(checkMakeUpSelect()){
//                        mImageDisplay.enableMakeUp(true);
//                    }else{
//                        mImageDisplay.enableMakeUp(false);
//                    }
//                    entry.getValue().notifyDataSetChanged();
//
//                    clearFullBeautyView();
//                }
//            });
//        }*/
//
//        mBeautyOptionsAdapter.setClickBeautyListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position = Integer.parseInt(v.getTag().toString());
//                mBeautyOptionsAdapter.setSelectedPosition(position);
//                mBeautyOptionsPosition = position;
//                mFilterGroupsLinearLayout.setVisibility(View.INVISIBLE);
//                mBaseBeautyOptions.setVisibility(View.VISIBLE);
//                mMakeupGroupRelativeLayout.setVisibility(View.INVISIBLE);
//                //整体效果
//                if (mBeautyOptionsPosition == 0) {
//                    if (mCurrentFullBeautyIndex == -1) {
//                        gpFullBeautySeekBar.setVisibility(View.GONE);
//                        mIndicatorSeekbar.setVisibility(View.INVISIBLE);
//                    } else {
//                        gpFullBeautySeekBar.setVisibility(View.VISIBLE);
//                        mIndicatorSeekbar.setVisibility(View.VISIBLE);
//
//                        mIndicatorSeekbar.getSeekBar().setProgress(mBeautyFullItem.get(mCurrentFullBeautyIndex).getFilterProgress());
//                        mIndicatorSeekbar.updateTextView(mBeautyFullItem.get(mCurrentFullBeautyIndex).getFilterProgress());
//                        mIndicatorSeekbarNew.getSeekBar().setProgress(mBeautyFullItem.get(mCurrentFullBeautyIndex).getMakeupAllProgress());
//                        mIndicatorSeekbarNew.updateTextView(mBeautyFullItem.get(mCurrentFullBeautyIndex).getMakeupAllProgress());
//                    }
//                    //mIndicatorSeekbar.getSeekBar().setProgress(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(mBeautyOptionSelectedIndex.get(position)).getProgress());
//                    mBeautyFullAdapter.setSelectedPosition(mCurrentFullBeautyIndex);
//                    mBeautyFullAdapter.notifyDataSetChanged();
//                } else if (mBeautyOptionsPosition != 4 && mBeautyOptionsPosition != 5) {
//                    gpFullBeautySeekBar.setVisibility(View.GONE);
//                    calculateBeautyIndex(mBeautyOptionsPosition, mBeautyOptionSelectedIndex.get(mBeautyOptionsPosition));
//                    mIndicatorSeekbar.setVisibility(View.VISIBLE);
//                    if (checkMicroType(mBeautyOptionsPosition)) {
//                        mIndicatorSeekbar.getSeekBar().setProgress(STUtils.convertToData(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(mBeautyOptionSelectedIndex.get(position)).getProgress()));
//                    } else {
//                        mIndicatorSeekbar.getSeekBar().setProgress(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(mBeautyOptionSelectedIndex.get(position)).getProgress());
//                    }
//                    mIndicatorSeekbar.updateTextView(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(mBeautyOptionSelectedIndex.get(position)).getProgress());
//                } else {
//                    gpFullBeautySeekBar.setVisibility(View.GONE);
//                    mIndicatorSeekbar.setVisibility(View.INVISIBLE);
//                }
//                mFilterIconsRelativeLayout.setVisibility(View.INVISIBLE);
//                mMakeupIconsRelativeLayout.setVisibility(View.INVISIBLE);
//                mShowOriginBtn3.setVisibility(View.VISIBLE);
//                mFilterStrengthLayout.setVisibility(View.INVISIBLE);
//                if (position == 0) {
//                    mBeautyBaseRecycleView.setAdapter(mBeautyFullAdapter);
//                } else if (position == 1) {
//                    mBeautyBaseRecycleView.setAdapter(mBeautyItemAdapters.get("baseBeauty"));
//                } else if (position == 2) {
//                    mBeautyBaseRecycleView.setAdapter(mBeautyItemAdapters.get("professionalBeauty"));
//                } else if (position == 3) {
//                    mBeautyBaseRecycleView.setAdapter(mBeautyItemAdapters.get("microBeauty"));
//                } else if (position == 4) {
//                    mMakeupGroupRelativeLayout.setVisibility(View.VISIBLE);
//                    mBaseBeautyOptions.setVisibility(View.INVISIBLE);
//                } else if (position == 5) {
//                    mFilterGroupsLinearLayout.setVisibility(View.VISIBLE);
//                    mBaseBeautyOptions.setVisibility(View.INVISIBLE);
//                } else if (position == 6) {
//                    mBeautyBaseRecycleView.setAdapter(mBeautyItemAdapters.get("adjustBeauty"));
//                }
//                mBeautyOptionsAdapter.notifyDataSetChanged();
//            }
//        });
//
//        //整体效果初始状态
//        mBeautyFullAdapter.setSelectedPosition(mCurrentFullBeautyIndex);
//        mBeautyFullAdapter.notifyDataSetChanged();
//        mBeautyFullAdapter.setClickBeautyListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position = Integer.parseInt(v.getTag().toString());
//                mIndicatorSeekbar.setVisibility(View.VISIBLE);
//                mBeautyOptionSelectedIndex.put(mBeautyOptionsPosition, position);
//                if (mCurrentFullBeautyIndex == position) {
//
//                } else {
//                    isFromUserSeekBar = false;
//                    fullBeautyMakeupScale = 0.85f;
//                    fullBeautyFilterScale = 0.85f;
//                    setFullBeauty(mBeautyFullItem.get(position));
//                    mBeautyFullAdapter.setSelectedPosition(position);
//                    mCurrentFullBeautyIndex = position;
//                    mBeautyFullAdapter.notifyDataSetChanged();
//                }
//
//                gpFullBeautySeekBar.setVisibility(View.VISIBLE);
//                mIndicatorSeekbar.getSeekBar().setProgress(mBeautyFullItem.get(position).getFilterProgress());
//                mIndicatorSeekbar.updateTextView(mBeautyFullItem.get(position).getFilterProgress());
//                mIndicatorSeekbarNew.getSeekBar().setProgress(mBeautyFullItem.get(position).getMakeupAllProgress());
//                mIndicatorSeekbarNew.updateTextView(mBeautyFullItem.get(position).getMakeupAllProgress());
//
//                if (lastIndex != position) {
//                    EffectInfoDataHelper.getInstance().fullBeautyMakeupProgress = 85;
//                    EffectInfoDataHelper.getInstance().fullBeautyFilterProgress = 85;
//                    mBeautyFullItem.get(position).setFilterProgress(85);
//                    mBeautyFullItem.get(position).setMakeupAllProgress(85);
//                    mIndicatorSeekbar.getSeekBar().setProgress(85);
//                    mIndicatorSeekbar.updateTextView(85);
//                    mIndicatorSeekbarNew.getSeekBar().setProgress(85);
//                    mIndicatorSeekbarNew.updateTextView(85);
//                }
//                lastIndex = position;
//                EffectInfoDataHelper.getInstance().fullBeautyName = mBeautyFullItem.get(position).getText();
//            }
//        });
//
//        for (Map.Entry<String, BeautyItemAdapter> entry : mBeautyItemAdapters.entrySet()) {
//            final BeautyItemAdapter adapter = entry.getValue();
//            adapter.setClickBeautyListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = Integer.parseInt(v.getTag().toString());
//                    if (position < 2) {
//                        mImageDisplay.setWhitenFromAssetsFile(null);
//                    }
//                    if (position == 2) {
//                        mImageDisplay.setWhitenFromAssetsFile("whiten_gif.zip");
//                    }
//                    adapter.setSelectedPosition(position);
//                    mBeautyOptionSelectedIndex.put(mBeautyOptionsPosition, position);
//                    if (checkMicroType(mBeautyOptionsPosition)) {
//                        mIndicatorSeekbar.getSeekBar().setProgress(STUtils.convertToData(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(position).getProgress()));
//                    } else {
//                        mIndicatorSeekbar.getSeekBar().setProgress(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(position).getProgress());
//                    }
//                    mIndicatorSeekbar.updateTextView(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(position).getProgress());
//                    calculateBeautyIndex(mBeautyOptionsPosition, position);
//                    adapter.notifyDataSetChanged();
//                }
//            });
//        }
//
//        mShowOriginBtn1 = (TextView) findViewById(R.id.tv_show_origin1);
//        mShowOriginBtn1.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // TODO Auto-generated method stub
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    mImageDisplay.setShowOriginal(true);
//                } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                    mImageDisplay.setShowOriginal(false);
//                }
//                return true;
//            }
//        });
//        mShowOriginBtn1.setVisibility(View.VISIBLE);
//
//        mShowOriginBtn2 = (TextView) findViewById(R.id.tv_show_origin2);
//        mShowOriginBtn2.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // TODO Auto-generated method stub
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    mImageDisplay.setShowOriginal(true);
//                } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                    mImageDisplay.setShowOriginal(false);
//                }
//                return true;
//            }
//        });
//        mShowOriginBtn2.setVisibility(View.INVISIBLE);
//
//        mShowOriginBtn3 = (TextView) findViewById(R.id.tv_show_origin3);
//        mShowOriginBtn3.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // TODO Auto-generated method stub
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    mImageDisplay.setShowOriginal(true);
//                } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                    mImageDisplay.setShowOriginal(false);
//                }
//                return true;
//            }
//        });
//        mShowOriginBtn3.setVisibility(View.INVISIBLE);
//
//        mImageDisplay.setCostChangeListener(new ImageDisplay.CostChangeListener() {
//            @Override
//            public void onCostChanged(final int value) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ((TextView) findViewById(R.id.tv_frame_radio)).setText(String.valueOf(value));
//                    }
//                });
//            }
//        });
//
//        findViewById(R.id.ll_cpu_radio).setVisibility(View.GONE);
//        findViewById(R.id.tv_layout_tips).setVisibility(View.GONE);
//        findViewById(R.id.tv_capture).setOnClickListener(this);
//        findViewById(R.id.tv_cancel).setOnClickListener(this);
//        findViewById(R.id.id_gl_sv).setOnClickListener(this);
//
//        findViewById(R.id.rv_close_sticker).setOnClickListener(this);
//
//        mImageDisplay.enableBeautify(true);
//        mResetTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onClickReset();
//            }
//        });
//
//        mResetZeroTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onClickZero();
//            }
//        });
//    }
//
//    private void onClickZero() {
//        if (mBeautyOptionsPosition == 0) {
//            EffectInfoDataHelper.getInstance().fullBeautyName = "";
//            resetZeroFullBeautyView();
//            clearFullBeautyView();
//        } else if (mBeautyOptionsPosition == 4) {
//            resetMakeup(true, false);
//            clearFullBeautyView();
//        } else if (mBeautyOptionsPosition == 5) {
//            resetFilterView(false);
//            mImageDisplay.setFilterStrength(mFilterStrength);
//            clearFullBeautyView();
//        } else {
//            setBeautyZero(mBeautyOptionsPosition);
//            setBeautyListsZero(mBeautyOptionsPosition);
//            mBeautyItemAdapters.get(mBeautyOption.get(mBeautyOptionsPosition)).notifyDataSetChanged();
//            if (checkMicroType(mBeautyOptionsPosition)) {
//                mIndicatorSeekbar.getSeekBar().setProgress(STUtils.convertToData(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(mBeautyOptionSelectedIndex.get(mBeautyOptionsPosition)).getProgress()));
//            } else {
//                mIndicatorSeekbar.getSeekBar().setProgress(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(mBeautyOptionSelectedIndex.get(mBeautyOptionsPosition)).getProgress());
//            }
//            mIndicatorSeekbar.updateTextView(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(mBeautyOptionSelectedIndex.get(mBeautyOptionsPosition)).getProgress());
//        }
//    }
//
//    private void onClickReset() {
//        if (mBeautyOptionsPosition == 0) {
//            resetFullBeautyView();
//            EffectInfoDataHelper.getInstance().fullBeautyName = "Default";
//            mIndicatorSeekbar.setVisibility(View.VISIBLE);
//            gpFullBeautySeekBar.setVisibility(View.VISIBLE);
//        } else if (mBeautyOptionsPosition == 4) {
//            mCurrentFullBeautyIndex = -1;
//            lastIndex = -1;
//            resetMakeup(true, false);
//            setDefaultMakeup();
//            mBeautyFullAdapter.setSelectedPosition(mCurrentFullBeautyIndex);
//            mBeautyFullAdapter.notifyDataSetChanged();
//
//            mMakeupGroupRelativeLayout.setVisibility(View.VISIBLE);
//            mMakeupIconsRelativeLayout.setVisibility(View.INVISIBLE);
//            mFilterStrengthLayout.setVisibility(View.INVISIBLE);
//        } else if (mBeautyOptionsPosition == 5) {
//            lastIndex = -1;
//            setDefaultFilter();
//            mFilterStrengthBar.setProgress(100);
//            mFilterStrength = 100;
//            mBeautyFullAdapter.setSelectedPosition(mCurrentFullBeautyIndex);
//            mBeautyFullAdapter.notifyDataSetChanged();
//            mCurrentFullBeautyIndex = -1;
//            isFromUserSeekBar = false;
//        } else {
//            resetSetBeautyParam(mBeautyOptionsPosition);
//            resetBeautyLists(mBeautyOptionsPosition);
//            mBeautyItemAdapters.get(mBeautyOption.get(mBeautyOptionsPosition)).notifyDataSetChanged();
//            if (checkMicroType(mBeautyOptionsPosition)) {
//                mIndicatorSeekbar.getSeekBar().setProgress(STUtils.convertToData(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(mBeautyOptionSelectedIndex.get(mBeautyOptionsPosition)).getProgress()));
//            } else {
//                mIndicatorSeekbar.getSeekBar().setProgress(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(mBeautyOptionSelectedIndex.get(mBeautyOptionsPosition)).getProgress());
//            }
//            mIndicatorSeekbar.updateTextView(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(mBeautyOptionSelectedIndex.get(mBeautyOptionsPosition)).getProgress());
//
//            //clearFullBeautyView();
//        }
//        if (mBeautyOptionsPosition == 0) {
//            mBeautyFullItem.get(0).setFilterProgress(85);
//            mBeautyFullItem.get(0).setMakeupAllProgress(85);
//            mIndicatorSeekbar.getSeekBar().setProgress(85);
//            mIndicatorSeekbar.updateTextView(85);
//            mIndicatorSeekbarNew.getSeekBar().setProgress(85);
//            mIndicatorSeekbarNew.updateTextView(85);
//        }
//    }
//
//    private void setFullBeautyFilter(int progress) {
//        mIndicatorSeekbar.setProgress(progress);
//        mImageDisplay.setFilterStrength((float) progress / 100f);
//        EffectInfoDataHelper.getInstance().fullBeautyFilterProgress = progress;
//        mBeautyFullItem.get(mCurrentFullBeautyIndex).setFilterProgress(progress);
//        float scale = progress / 100f;
//        isFromUserSeekBar = false;
//        fullBeautyFilterScale = scale;
//    }
//
//    private void setFullBeautyMakeup(int progress) {
//        EffectInfoDataHelper.getInstance().fullBeautyMakeupProgress = progress;
//        for (Map.Entry<Integer, Integer> entry : mMakeupStrength.entrySet()) {
//            Integer itValue = entry.getValue();
//            Integer itKey = entry.getKey();
//            float scale = progress / 100f;
//            mImageDisplay.setStrengthForType(itKey, ((int) (itValue * scale)) / 100f);
//            isFromUserSeekBar = false;
//            fullBeautyMakeupScale = scale;
//        }
//    }
//
//    private void setMakeupOptionSelectedIndex(String[] arrays) {
//        for (int i = 0; i < arrays.length; i++) {
//            String arr = arrays[i];
//            if (arr != null && !TextUtils.isEmpty(arr) && !arr.equals("null")) {
//                for (Map.Entry<String, MakeupAdapter> entry : mMakeupAdapters.entrySet()) {
//                    String key = entry.getKey();
//                    MakeupAdapter adapter = mMakeupAdapters.get(key);
//                    List<MakeupItem> lists = adapter.getData();
//                    for (int j = 0; j < lists.size(); j++) {
//                        MakeupItem item = lists.get(j);
//                        if (item.path != null && item.path.equals(arr)) {
//                            mMakeupOptionSelectedIndex.put(i, j);
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private int getFullBeautyIndex(String name) {
//        ArrayList<FullBeautyItem> lists = mBeautyFullAdapter.getData();
//        for (int i = 0; i < lists.size(); i++) {
//            FullBeautyItem item = lists.get(i);
//            if (item.getText().equals(name)) return i;
//        }
//        return -1;
//    }
//
//    private void recoverUI() {
//        mCurrentFullBeautyIndex = getFullBeautyIndex(EffectInfoDataHelper.getInstance().getFullBeautyName());
//        if (mCurrentFullBeautyIndex != -1) {
//            isFromUserSeekBar = false;
//            String tempFullBeautyName = String.copyValueOf(EffectInfoDataHelper.getInstance().fullBeautyName.toCharArray());
//            setFullBeauty(mBeautyFullItem.get(mCurrentFullBeautyIndex));
//            fullBeautyFilterScale = EffectInfoDataHelper.getInstance().getFullBeautyFilterProgress() / 100f;
//            fullBeautyMakeupScale = EffectInfoDataHelper.getInstance().getFullBeautyMakeupProgress() / 100f;
//            setFullBeautyMakeup(EffectInfoDataHelper.getInstance().getFullBeautyMakeupProgress());
//            setFullBeautyFilter(EffectInfoDataHelper.getInstance().getFullBeautyFilterProgress());
//            EffectInfoDataHelper.getInstance().fullBeautyName = tempFullBeautyName;
//        } else {
//            gpFullBeautySeekBar.setVisibility(View.GONE);
//            mIndicatorSeekbar.setVisibility(View.INVISIBLE);
//            // 恢复滤镜
//            String filterType = EffectInfoDataHelper.getInstance().getFilterType();
//            String filterName = EffectInfoDataHelper.getInstance().getFilterName();
//            int filterStr = (int) (EffectInfoDataHelper.getInstance().getFilterStrength() * 100f);
//            if (!TextUtils.isEmpty(filterType)) {
//                isFromUserSeekBar = true;
//                if (filterType.equals("filter_portrait")) {
//                    mCurrentFilterGroupIndex = 0;
//                } else if (filterType.equals("filter_scenery")) {
//                    mCurrentFilterGroupIndex = 1;
//                } else if (filterType.equals("filter_still_life")) {
//                    mCurrentFilterGroupIndex = 2;
//                } else if (filterType.equals("filter_food")) {
//                    mCurrentFilterGroupIndex = 3;
//                }
//
//                if (mFilterLists.get(filterType).size() > 0) {
//                    for (int i = 0; i < mFilterLists.get(filterType).size(); i++) {
//                        if (mFilterLists.get(filterType).get(i).name.equals(filterName)) {
//                            mCurrentFilterIndex = i;
//                        }
//                    }
//                }
//                if (mCurrentFilterIndex > 0) {
//                    mFilterAdapters.get(filterType).setSelectedPosition(mCurrentFilterIndex);
//                    mImageDisplay.enableFilter(true);
//                    mFilterStrength = filterStr;
//                    mFilterStrengthBar.setProgress(mFilterStrength);
//                    mFilterStrengthText.setText(String.valueOf(mFilterStrength));
//                    setFilterViewSelected(mCurrentFilterGroupIndex);
//                }
//            }
//
//            // 恢复美妆UI
//            float[] strength = EffectInfoDataHelper.getInstance().getCurrentMakeUpStrength(null);
//            String[] arrays = EffectInfoDataHelper.getInstance().getCurrentMakeUp();
//            if (isEmpty(arrays[Constants.ST_MAKEUP_ALL])) {
//                mImageDisplay.removeMakeupByType(Constants.ST_MAKEUP_ALL);
//            }
//            setMakeupOptionSelectedIndex(arrays);
//            for (int i = 0; i < arrays.length; i++) {
//                String arr = arrays[i];
//                if (arr != null&&!TextUtils.isEmpty(arr) && !arr.equals("null")) {
//                    if (strength[i] > 0 && mMakeupStrength != null)
//                        mMakeupStrength.put(i, (int) (strength[i] * 100f));
//                    View[] view = makeupViews[i];
//                    for (View v : view) v.setSelected(true);
//                }
//            }
//        }
//        // 恢复基础美颜参数、美形、微整形
//        float[] beautifyParamsTypeBase = EffectInfoDataHelper.getInstance().getBaseParams();
//        mImageDisplay.mBeautifyParamsTypeBase= beautifyParamsTypeBase;
//        for (int i = 0; i < mBeautylists.get("baseBeauty").size(); i++) {
//            ((BeautyItem) mBeautylists.get("baseBeauty").get(i)).setProgress((int) (beautifyParamsTypeBase[i] * 100));
//        }
//        mBeautyItemAdapters.get("baseBeauty").notifyDataSetChanged();
//        // 美形
//        float[] beautifyParamsTypeProfessional = EffectInfoDataHelper.getInstance().getProfessionalParams();
//        mImageDisplay.mBeautifyParamsTypeProfessional= beautifyParamsTypeProfessional;
//        for (int i = 0; i < mBeautylists.get("professionalBeauty").size(); i++) {
//            ((BeautyItem) mBeautylists.get("professionalBeauty").get(i)).setProgress((int) (beautifyParamsTypeProfessional[i] * 100));
//        }
//        mBeautyItemAdapters.get("professionalBeauty").notifyDataSetChanged();
//        // 微整形
//        float[] beautifyParamsTypeMicro = EffectInfoDataHelper.getInstance().getMicroParams();
//        mImageDisplay.mBeautifyParamsTypeMicro= beautifyParamsTypeMicro;
//        for (int i = 0; i < mBeautylists.get("microBeauty").size(); i++) {
//            ((BeautyItem) mBeautylists.get("microBeauty").get(i)).setProgress((int) (beautifyParamsTypeMicro[i] * 100));
//        }
//        mBeautyItemAdapters.get("microBeauty").notifyDataSetChanged();
//        // 调整
//        float[] beautifyParamsTypeAdjust = EffectInfoDataHelper.getInstance().getAdjustParams();
//        mImageDisplay.mBeautifyParamsTypeAdjust= beautifyParamsTypeAdjust;
//        for (int i = 0; i < mBeautylists.get("adjustBeauty").size(); i++) {
//            ((BeautyItem) mBeautylists.get("adjustBeauty").get(i)).setProgress((int) (beautifyParamsTypeAdjust[i] * 100));
//        }
//        mBeautyItemAdapters.get("adjustBeauty").notifyDataSetChanged();
//
//        // 刷新seekBar
//        refreshSeekBar();
//    }
//
//    private void refreshSeekBar() {
//        ArrayList<BeautyItem> beautyItems = mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition));
//        if (null == beautyItems) return;
//        Integer integer = mBeautyOptionSelectedIndex.get(mBeautyOptionsPosition);
//        int progress = beautyItems.get(integer).getProgress();
//        if (checkMicroType(mBeautyOptionsPosition))
//            progress = STUtils.convertToData(progress);
//        mIndicatorSeekbar.setProgress(progress);
//        String str = mBeautyOption.get(mBeautyOptionsPosition);
//        mBeautyItemAdapters.get(str).notifyDataSetChanged();
//    }
//
//
//    private void clickMakeUpItem(int position, MakeupItem item, SenseArMaterial material, MakeupAdapter mMakeupAdapter, String groupName) {
//
//
//        if (position == 0) {
//            lastIndex = -1;
//            mMakeupAdapter.setSelectedPosition(position);
//            mMakeupOptionSelectedIndex.put(mMakeupOptionIndex.get(groupName), position);
//
//            mFilterStrengthLayout.setVisibility(View.INVISIBLE);
//            mImageDisplay.removeMakeupByType(mCurrentMakeupGroupIndex);
//            updateMakeupOptions(mCurrentMakeupGroupIndex, false);
//        } else if (position == mMakeupOptionSelectedIndex.get(mMakeupOptionIndex.get(groupName))) {
//            mMakeupAdapter.setSelectedPosition(0);
//            mMakeupOptionSelectedIndex.put(mMakeupOptionIndex.get(groupName), 0);
//
//            mFilterStrengthLayout.setVisibility(View.INVISIBLE);
//            mFilterStrengthBar.setProgress(mMakeupStrength.get(mCurrentMakeupGroupIndex));
//            mImageDisplay.removeMakeupByType(mCurrentMakeupGroupIndex);
//            updateMakeupOptions(mCurrentMakeupGroupIndex, false);
//        } else {
//            if (mCurrentMakeupGroupIndex == Constants.ST_MAKEUP_ALL) {
//                resetMakeup(false, false);
//            }
//
//            mMakeupAdapter.setSelectedPosition(position);
//            mMakeupOptionSelectedIndex.put(mMakeupOptionIndex.get(groupName), position);
//
//            mImageDisplay.setMakeupForType(mCurrentMakeupGroupIndex, mMakeupLists.get(getMakeupNameOfType(mCurrentMakeupGroupIndex)).get(position).path);
//            mImageDisplay.setStrengthForType(mCurrentMakeupGroupIndex, (float) mMakeupStrength.get(mCurrentMakeupGroupIndex) / 100.f);
//            mFilterStrengthLayout.setVisibility(View.VISIBLE);
//            mFilterStrengthBar.setProgress(mMakeupStrength.get(mCurrentMakeupGroupIndex));
//            mFilterStrengthText.setText(mMakeupStrength.get(mCurrentMakeupGroupIndex) + "");
//            updateMakeupOptions(mCurrentMakeupGroupIndex, true);
//        }
//
//        if (checkMakeUpSelect()) {
//            mImageDisplay.enableMakeUp(true);
//        } else {
//            mImageDisplay.enableMakeUp(false);
//        }
//        mMakeupAdapter.notifyDataSetChanged();
//
//        clearFullBeautyView();
//    }
//
//    public void setDefaultFilter() {
//        resetFilterView(false);
//        if (mFilterLists.get("filter_portrait").size() > 0) {
//            for (int i = 0; i < mFilterLists.get("filter_portrait").size(); i++) {
//                if (mFilterLists.get("filter_portrait").get(i).name.equals("nvshen")) {
//                    mCurrentFilterIndex = i;
//                }
//            }
//
//            if (mCurrentFilterIndex > 0) {
//                mCurrentFilterGroupIndex = 0;
//                mFilterAdapters.get("filter_portrait").setSelectedPosition(mCurrentFilterIndex);
//                mImageDisplay.setFilterStyle("filter_portrait", mFilterLists.get("filter_portrait").get(mCurrentFilterIndex).model, mFilterLists.get("filter_portrait").get(mCurrentFilterIndex).name);
//                mImageDisplay.enableFilter(true);
//
//                setFilterViewSelected(mCurrentFilterGroupIndex);
//                mFilterAdapters.get("filter_portrait").notifyDataSetChanged();
//            }
//        }
//    }
//
//    public void setDefaultMakeup() {
//        if (mBeautyFullItem.get(0).getMakeupList() != null) {
//            for (int i = 0; i < mBeautyFullItem.get(0).getMakeupList().size(); i++) {
//                String type = mBeautyFullItem.get(0).getMakeupList().get(i).getType();
//                String name = mBeautyFullItem.get(0).getMakeupList().get(i).getMakeupName();
//                int strength = mBeautyFullItem.get(0).getMakeupList().get(i).getStrength();
//                int position = getMakeupIndexByName(type, name);
//
//                setMakeupWithType(type, strength, position);
//            }
//        }
//        mDefaultMakeupStrength.clear();
//        mDefaultMakeupStrength.putAll(mMakeupStrength);
//    }
//
//    protected void resetSetBeautyParam(int beautyOptionsPosition) {
//        switch (beautyOptionsPosition) {
//            case 1:
//                for (int i = 0; i < mNewBeautifyParamsTypeBase.length; i++) {
//                    mImageDisplay.setBeautyParam(STEffectBeautyType.EFFECT_BEAUTY_BASE_WHITTEN + i, (mNewBeautifyParamsTypeBase[i]));
//                }
//                break;
//            case 2:
//                for (int i = 0; i < mNewBeautifyParamsTypeProfessional.length; i++) {
//                    mImageDisplay.setBeautyParam(STEffectBeautyType.EFFECT_BEAUTY_RESHAPE_SHRINK_FACE + i, (mNewBeautifyParamsTypeProfessional[i]));
//                }
//                break;
//            case 3:
//                for (int i = 0; i < mNewBeautifyParamsTypeMicro.length; i++) {
//                    mImageDisplay.setBeautyParam(STEffectBeautyType.EFFECT_BEAUTY_PLASTIC_THINNER_HEAD + i, (mNewBeautifyParamsTypeMicro[i]));
//                }
//                break;
//            case 6:
//                for (int i = 0; i < mNewBeautifyParamsTypeAdjust.length; i++) {
//                    mImageDisplay.setBeautyParam(STEffectBeautyType.EFFECT_BEAUTY_TONE_CONTRAST + i, (mNewBeautifyParamsTypeAdjust[i]));
//                }
//                break;
//        }
//    }
//
//    protected void setBeautyZero(int beautyOptionsPosition) {
//        switch (beautyOptionsPosition) {
//            case 1:
//                for (int i = 0; i < mNewBeautifyParamsTypeBase.length; i++) {
//                    mImageDisplay.setBeautyParam(STEffectBeautyType.EFFECT_BEAUTY_BASE_WHITTEN + i, 0f);
//                }
//                EffectInfoDataHelper.getInstance().setZeroBaseParams();
//                break;
//            case 2:
//                for (int i = 0; i < mNewBeautifyParamsTypeProfessional.length; i++) {
//                    mImageDisplay.setBeautyParam(STEffectBeautyType.EFFECT_BEAUTY_RESHAPE_SHRINK_FACE + i, 0f);
//                }
//                EffectInfoDataHelper.getInstance().setZeroProfessionalParams();
//                break;
//            case 3:
//                for (int i = 0; i < mNewBeautifyParamsTypeMicro.length; i++) {
//                    mImageDisplay.setBeautyParam(STEffectBeautyType.EFFECT_BEAUTY_PLASTIC_THINNER_HEAD + i, 0f);
//                }
//                EffectInfoDataHelper.getInstance().setZeroMicroParams();
//                break;
//            case 6:
//                for (int i = 0; i < mNewBeautifyParamsTypeAdjust.length; i++) {
//                    mImageDisplay.setBeautyParam(STEffectBeautyType.EFFECT_BEAUTY_TONE_CONTRAST + i, 0f);
//                }
//                EffectInfoDataHelper.getInstance().setZeroAdjustParams();
//                break;
//        }
//    }
//
//    private void setMutex(int subItemSelectedIndex, int[] mutex) {
//        if (Arrays.binarySearch(mutex, subItemSelectedIndex) >= 0) {
//            for (int i : mutex) {
//                if (i != subItemSelectedIndex) {
//                    mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(i).setProgress(0);
//                    mBeautyItemAdapters.get(mBeautyOption.get(mBeautyOptionsPosition)).notifyItemChanged(i);
//                }
//            }
//        }
//    }
//
//    protected void resetBeautyLists(int beautyOptionsPosition) {
//        switch (beautyOptionsPosition) {
//            case 1:
//                for (int i = 0; i < mNewBeautifyParamsTypeBase.length; i++) {
//                    mBeautylists.get(mBeautyOption.get(beautyOptionsPosition)).get(i).setProgress((int) (mNewBeautifyParamsTypeBase[i] * 100));
//                }
//                break;
//            case 2:
//                for (int i = 0; i < mNewBeautifyParamsTypeProfessional.length; i++) {
//                    mBeautylists.get(mBeautyOption.get(beautyOptionsPosition)).get(i).setProgress((int) (mNewBeautifyParamsTypeProfessional[i] * 100));
//                }
//                break;
//            case 3:
//                for (int i = 0; i < mNewBeautifyParamsTypeMicro.length; i++) {
//                    mBeautylists.get(mBeautyOption.get(beautyOptionsPosition)).get(i).setProgress((int) (mNewBeautifyParamsTypeMicro[i] * 100));
//                }
//                break;
//            case 6:
//                for (int i = 0; i < mNewBeautifyParamsTypeAdjust.length; i++) {
//                    mBeautylists.get(mBeautyOption.get(beautyOptionsPosition)).get(i).setProgress((int) (mNewBeautifyParamsTypeAdjust[i] * 100));
//                }
//                break;
//        }
//    }
//
//    protected void setBeautyListsZero(int beautyOptionsPosition) {
//        switch (beautyOptionsPosition) {
//            case 1:
//                for (int i = 0; i < mNewBeautifyParamsTypeBase.length; i++) {
//                    mBeautylists.get(mBeautyOption.get(beautyOptionsPosition)).get(i).setProgress(0);
//                }
//                break;
//            case 2:
//                for (int i = 0; i < mNewBeautifyParamsTypeProfessional.length; i++) {
//                    mBeautylists.get(mBeautyOption.get(beautyOptionsPosition)).get(i).setProgress(0);
//                }
//                break;
//            case 3:
//                for (int i = 0; i < mNewBeautifyParamsTypeMicro.length; i++) {
//                    mBeautylists.get(mBeautyOption.get(beautyOptionsPosition)).get(i).setProgress(0);
//                }
//                break;
//            case 6:
//                for (int i = 0; i < mNewBeautifyParamsTypeAdjust.length; i++) {
//                    mBeautylists.get(mBeautyOption.get(beautyOptionsPosition)).get(i).setProgress(0);
//                }
//                break;
//        }
//    }
//
//    protected void calculateBeautyIndex(int beautyOptionPosition, int selectPosition) {
//        switch (beautyOptionPosition) {
//            case 1:
//                mCurrentBeautyIndex = selectPosition + STEffectBeautyType.EFFECT_BEAUTY_BASE_WHITTEN;
//                break;
//            case 2:
//                mCurrentBeautyIndex = selectPosition + STEffectBeautyType.EFFECT_BEAUTY_RESHAPE_SHRINK_FACE;
//                break;
//            case 3:
//                mCurrentBeautyIndex = selectPosition + STEffectBeautyType.EFFECT_BEAUTY_PLASTIC_THINNER_HEAD;
//                break;
//            case 6:
//                mCurrentBeautyIndex = selectPosition + STEffectBeautyType.EFFECT_BEAUTY_TONE_CONTRAST;
//                break;
//        }
//    }
//
////    protected void initStickerAdapter(final String stickerClassName, final int index) {
////
////        mStickerAdapters.get(stickerClassName).setClickStickerListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                int position = Integer.parseInt(v.getTag().toString());
////
////                resetNewStickerAdapter();
////
////                if (mCurrentStickerOptionsIndex == index && mCurrentStickerPosition == position) {
////                    mStickerAdapters.get(stickerClassName).setSelectedPosition(-1);
////                    mCurrentStickerOptionsIndex = -1;
////                    mCurrentStickerPosition = -1;
////
////                    findViewById(R.id.iv_close_sticker).setBackground(getResources().getDrawable(R.drawable.close_sticker_selected));
////                    mImageDisplay.enableSticker(false);
////                    mImageDisplay.removeAllStickers();
////
////                } else {
////                    mCurrentStickerOptionsIndex = index;
////                    mCurrentStickerPosition = position;
////
////                    findViewById(R.id.iv_close_sticker).setBackground(getResources().getDrawable(R.drawable.close_sticker));
////
////                    mStickerAdapters.get(stickerClassName).setSelectedPosition(position);
////                    mImageDisplay.enableSticker(true);
////                    recoverUI();
////                    mImageDisplay.changeSticker(mStickerlists.get(stickerClassName).get(position).path);
////                }
////
////                mStickerAdapters.get(stickerClassName).notifyDataSetChanged();
////            }
////        });
////    }
//
//    protected void resetStickerAdapter() {
//
//        if (mCurrentStickerPosition != -1) {
//            mImageDisplay.removeAllStickers();
//            mCurrentStickerPosition = -1;
//        }
//
//        //重置所有状态为为选中状态
//        for (StickerOptionsItem optionsItem : mStickerOptionsList) {
//            if (optionsItem.name.equals("sticker_new_engine")) {
//                continue;
//            } else {
//                if (mStickerAdapters.get(optionsItem.name) != null) {
//                    mStickerAdapters.get(optionsItem.name).setSelectedPosition(-1);
//                    mStickerAdapters.get(optionsItem.name).notifyDataSetChanged();
//                }
//            }
//        }
//    }
//
//    protected void resetNewStickerAdapter() {
//        if (mCurrentNewStickerPosition != -1) {
//            mImageDisplay.removeAllStickers();
//            mCurrentNewStickerPosition = -1;
//        }
//
//        if (mStickerPackageMap != null) {
//            mStickerPackageMap.clear();
//        }
//
//        if (mNativeStickerAdapters.get("sticker_new_engine") != null) {
//            mNativeStickerAdapters.get("sticker_new_engine").setSelectedPosition(-1);
//            mNativeStickerAdapters.get("sticker_new_engine").notifyDataSetChanged();
//        }
//        if (mNativeStickerAdapters.get("sticker_add_package") != null) {
//            mNativeStickerAdapters.get("sticker_add_package").setSelectedPosition(-1);
//            mNativeStickerAdapters.get("sticker_add_package").notifyDataSetChanged();
//        }
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//        if (id == R.id.rv_close_sticker) { // 取消贴纸
//            if (!mImageDisplay.stickerMapIsEmpty()) {
//                recoverUI();
//            }
//            //重置所有状态为为选中状态
//            resetStickerAdapter();
//            resetNewStickerAdapter();
//            mCurrentStickerPosition = -1;
//            mCurrentNewStickerPosition = -1;
//            mImageDisplay.enableSticker(false);
//
//            mImageDisplay.removeAllStickers();
//
//            findViewById(R.id.iv_close_sticker).setBackground(getResources().getDrawable(R.drawable.close_sticker_selected));
//        } else if (id == R.id.ll_sticker_options_switch) {
//            saveEffectSettings();
//            mStickerOptionsSwitch.setVisibility(View.INVISIBLE);
//            mBeautyOptionsSwitch.setVisibility(View.INVISIBLE);
//            mSelectOptions.setBackgroundColor(Color.parseColor("#80000000"));
//            mIndicatorSeekbar.setVisibility(View.INVISIBLE);
//            mStickerOptions.setVisibility(View.VISIBLE);
//            mStickerIcons.setVisibility(View.VISIBLE);
//            mIsStickerOptionsOpen = true;
//            mShowOriginBtn1.setVisibility(View.INVISIBLE);
//            mShowOriginBtn2.setVisibility(View.VISIBLE);
//            mShowOriginBtn3.setVisibility(View.INVISIBLE);
//            mResetTextView.setVisibility(View.INVISIBLE);
//            mResetZeroTextView.setVisibility(View.INVISIBLE);
//            mFilterGroupsLinearLayout.setVisibility(View.INVISIBLE);
//            mFilterIconsRelativeLayout.setVisibility(View.INVISIBLE);
//            mFilterStrengthLayout.setVisibility(View.INVISIBLE);
//            mFilterAndBeautyOptionView.setVisibility(View.INVISIBLE);
//            mBaseBeautyOptions.setVisibility(View.INVISIBLE);
//            mIsBeautyOptionsOpen = false;
//        } else if (id == R.id.ll_beauty_options_switch) {
//            if (/*mCurrentStickerPosition!=-1 || */!mImageDisplay.stickerMapIsEmpty() && mImageDisplay.overlappedBeautyCountChanged()) {
//                mImageDisplay.updateBeautyParamsUI();
//            }
//            mStickerOptionsSwitch.setVisibility(View.INVISIBLE);
//            mBeautyOptionsSwitch.setVisibility(View.INVISIBLE);
//            mSelectOptions.setBackgroundColor(Color.parseColor("#80000000"));
//            mBaseBeautyOptions.setVisibility(View.VISIBLE);
//            mIndicatorSeekbar.setVisibility(View.VISIBLE);
//            if (mBeautyOptionsPosition == 0) {
//                if (mCurrentFullBeautyIndex > -1) {
//                    mIndicatorSeekbar.setVisibility(View.VISIBLE);
//                    gpFullBeautySeekBar.setVisibility(View.GONE);
//                    gpFullBeautySeekBar.setVisibility(View.VISIBLE);
//                }
//                if (mCurrentFullBeautyIndex == -1) {
//                    gpFullBeautySeekBar.setVisibility(View.INVISIBLE);
//                    mIndicatorSeekbar.setVisibility(View.INVISIBLE);
//                }
//            } else if (mBeautyOptionsPosition == 4) {
//                mBaseBeautyOptions.setVisibility(View.INVISIBLE);
//                mMakeupGroupRelativeLayout.setVisibility(View.VISIBLE);
//                mMakeupIconsRelativeLayout.setVisibility(View.INVISIBLE);
//                mFilterStrengthLayout.setVisibility(View.INVISIBLE);
//                mIndicatorSeekbar.setVisibility(View.INVISIBLE);
//            } else if (mBeautyOptionsPosition == 5) {
//                mBaseBeautyOptions.setVisibility(View.INVISIBLE);
//                mFilterGroupsLinearLayout.setVisibility(View.VISIBLE);
//                mFilterIconsRelativeLayout.setVisibility(View.INVISIBLE);
//                mFilterStrengthLayout.setVisibility(View.INVISIBLE);
//                mIndicatorSeekbar.setVisibility(View.INVISIBLE);
//            }
//            mFilterAndBeautyOptionView.setVisibility(View.VISIBLE);
//            mIsBeautyOptionsOpen = true;
//            mShowOriginBtn1.setVisibility(View.INVISIBLE);
//            mShowOriginBtn2.setVisibility(View.INVISIBLE);
//            mShowOriginBtn3.setVisibility(View.VISIBLE);
//            mResetTextView.setVisibility(View.VISIBLE);
//            mResetZeroTextView.setVisibility(View.VISIBLE);
//            mIsStickerOptionsOpen = false;
//        } else if (id == R.id.id_gl_sv) {
//            gpFullBeautySeekBar.setVisibility(View.GONE);
//            mStickerOptionsSwitch.setVisibility(View.VISIBLE);
//            mBeautyOptionsSwitch.setVisibility(View.VISIBLE);
//            mSelectOptions.setBackgroundColor(Color.parseColor("#00000000"));
//            mStickerOptionsSwitchIcon = (ImageView) findViewById(R.id.iv_sticker_options_switch);
//            mBeautyOptionsSwitchIcon = (ImageView) findViewById(R.id.iv_beauty_options_switch);
//            mStickerOptionsSwitchText = (TextView) findViewById(R.id.tv_sticker_options_switch);
//            mBeautyOptionsSwitchText = (TextView) findViewById(R.id.tv_beauty_options_switch);
//
//            mStickerOptions.setVisibility(View.INVISIBLE);
//            mStickerIcons.setVisibility(View.INVISIBLE);
//            mStickerOptionsSwitchIcon.setImageDrawable(getResources().getDrawable(R.drawable.sticker));
//            mStickerOptionsSwitchText.setTextColor(Color.parseColor("#ffffff"));
//            mIsStickerOptionsOpen = false;
//
//            mFilterGroupsLinearLayout.setVisibility(View.INVISIBLE);
//            mFilterIconsRelativeLayout.setVisibility(View.INVISIBLE);
//            mFilterStrengthLayout.setVisibility(View.INVISIBLE);
//            mFilterAndBeautyOptionView.setVisibility(View.INVISIBLE);
//            mBaseBeautyOptions.setVisibility(View.INVISIBLE);
//            mBeautyOptionsSwitchIcon.setImageDrawable(getResources().getDrawable(R.drawable.beauty));
//            mBeautyOptionsSwitchText.setTextColor(Color.parseColor("#ffffff"));
//            mIsBeautyOptionsOpen = false;
//
//            mShowOriginBtn1.setVisibility(View.VISIBLE);
//            mShowOriginBtn2.setVisibility(View.INVISIBLE);
//            mShowOriginBtn3.setVisibility(View.INVISIBLE);
//            mIndicatorSeekbar.setVisibility(View.INVISIBLE);
//            mResetTextView.setVisibility(View.INVISIBLE);
//            mResetZeroTextView.setVisibility(View.INVISIBLE);
//            mMakeupGroupRelativeLayout.setVisibility(View.INVISIBLE);
//            mMakeupIconsRelativeLayout.setVisibility(View.INVISIBLE);
//        } else if (id == R.id.tv_capture) {
//            if (this.isWritePermissionAllowed()) {
//                mSavingTv.setVisibility(View.VISIBLE);
//                mImageDisplay.setHandler(mHandler);
//                mImageDisplay.enableSave(true);
//                if (mMediaActionSound != null) {
//                    mMediaActionSound.play(MediaActionSound.SHUTTER_CLICK);
//                }
//            } else {
//                requestWritePermission();
//            }
//        } else if (id == R.id.tv_cancel) {// back to welcome page
//            finish();
//        }
//    }
//
//    private void saveEffectSettings() {
//        EffectInfoDataHelper.getInstance().fullBeautyMakeupProgress = mIndicatorSeekbarNew.getSeekBar().getProgress();
//        EffectInfoDataHelper.getInstance().save();
//    }
//
//    // 分隔间距 继承RecyclerView.ItemDecoration
//    protected class SpaceItemDecoration extends RecyclerView.ItemDecoration {
//        private int space;
//
//        public SpaceItemDecoration(int space) {
//            this.space = space;
//        }
//
//        @Override
//        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//            super.getItemOffsets(outRect, view, parent, state);
//            if (parent.getChildAdapterPosition(view) != 0) {
//                outRect.top = space;
//            }
//        }
//    }
//
//    protected class BeautyItemDecoration extends RecyclerView.ItemDecoration {
//        private int space;
//
//        public BeautyItemDecoration(int space) {
//            this.space = space;
//        }
//
//        @Override
//        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//            super.getItemOffsets(outRect, view, parent, state);
//            outRect.left = space;
//            outRect.right = space;
//        }
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle savedInstanceState) {
//        savedInstanceState.putBoolean("process_killed", true);
//        super.onSaveInstanceState(savedInstanceState);
//    }
//
//    @Override
//    protected void onResume() {
//        LogUtils.i(TAG, "onResume");
//        super.onResume();
//        mAccelerometer.start();
//        mImageDisplay.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        LogUtils.i(TAG, "onPause");
//        super.onPause();
//        if (!mPermissionDialogShowing) {
//            mAccelerometer.stop();
//            mImageDisplay.onPause();
//            //finish();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        EffectInfoDataHelper.getInstance().clear();
//        mImageDisplay.onDestroy();
//
//        mStickerAdapters.clear();
//        mNativeStickerAdapters.clear();
//        mStickerlists.clear();
//        mBeautyParamsSeekBarList.clear();
//        mFilterAdapters.clear();
//        mFilterLists.clear();
//        mStickerOptionsList.clear();
//        mBeautyOptionsList.clear();
//        mBeautyFullItem.clear();
//
//        if (mMediaActionSound != null) {
//            mMediaActionSound.release();
//            mMediaActionSound = null;
//        }
//    }
//
//    protected void onPictureTaken(ByteBuffer data, File file, int mImageWidth, int mImageHeight) {
//        if (mImageWidth <= 0 || mImageHeight <= 0)
//            return;
//        Bitmap srcBitmap = Bitmap.createBitmap(mImageWidth, mImageHeight, Bitmap.Config.ARGB_8888);
//        data.position(0);
//        srcBitmap.copyPixelsFromBuffer(data);
//        saveToSDCard(file, srcBitmap);
//        srcBitmap.recycle();
//    }
//
//
//    protected void saveToSDCard(File file, Bitmap bmp) {
//
//        BufferedOutputStream bos = null;
//        try {
//            bos = new BufferedOutputStream(new FileOutputStream(file));
//            bmp.compress(Bitmap.CompressFormat.JPEG, 90, bos);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } finally {
//            if (bos != null)
//                try {
//                    bos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//        }
//
//        if (mHandler != null) {
//            String path = file.getAbsolutePath();
//            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//            Uri contentUri = Uri.fromFile(file);
//            mediaScanIntent.setData(contentUri);
//            this.sendBroadcast(mediaScanIntent);
//
//            if (Build.VERSION.SDK_INT >= 19) {
//
//                MediaScannerConnection.scanFile(this, new String[]{path}, null, null);
//            }
//
//            mHandler.sendEmptyMessage(this.MSG_SAVED_IMG);
//        }
//    }
//
//    protected boolean isWritePermissionAllowed() {
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    != PackageManager.PERMISSION_GRANTED) {
//                return false;
//            }
//        }
//
//        return true;
//    }
//
//    protected void requestWritePermission() {
//        if (Build.VERSION.SDK_INT >= 23) {
//            mPermissionDialogShowing = true;
//            this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    PERMISSION_REQUEST_WRITE_PERMISSION);
//        }
//    }
//
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == PERMISSION_REQUEST_WRITE_PERMISSION) {
//            mPermissionDialogShowing = false;
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                this.onClick(findViewById(R.id.tv_capture));
//            }
//        }
//    }
//
//
//    protected void resetFilterView(boolean onlyResetUI) {
//        mCurrentFilterIndex = -1;
//        setFilterViewSelected(mCurrentFilterIndex);
//
//        mFilterAdapters.get("filter_portrait").setSelectedPosition(0);
//        mFilterAdapters.get("filter_scenery").setSelectedPosition(0);
//        mFilterAdapters.get("filter_still_life").setSelectedPosition(0);
//        mFilterAdapters.get("filter_food").setSelectedPosition(0);
//
//        if (!onlyResetUI) {
//            mImageDisplay.setFilterStyle("", null, "");
//            mImageDisplay.enableFilter(false);
//        }
//
//        mFilterStrengthLayout.setVisibility(View.INVISIBLE);
//    }
//
//    /**
//     * 直接变更ui ,不通过数据驱动，相比notify data change 反应会快些
//     *
//     * @param stickerItem
//     * @param position
//     * @param name
//     */
//    public void notifyStickerViewState(StickerItem stickerItem, int position, String name) {
//        RecyclerView.ViewHolder viewHolder = mStickersRecycleView.findViewHolderForAdapterPosition(position);
//        //排除不必要变更
//        if (viewHolder == null || mStickersRecycleView.getAdapter() != mStickerAdapters.get(name))
//            return;
//        View itemView = viewHolder.itemView;
//        ImageView normalState = (ImageView) itemView.findViewById(R.id.normalState);
//        ImageView downloadingState = (ImageView) itemView.findViewById(R.id.downloadingState);
//        ViewGroup loadingStateParent = (ViewGroup) itemView.findViewById(R.id.loadingStateParent);
//        switch (stickerItem.state) {
//            case NORMAL_STATE:
//                //设置为等待下载状态
//                if (normalState.getVisibility() != View.VISIBLE) {
//                    Log.i("StickerAdapter", "NORMAL_STATE");
//                    normalState.setVisibility(View.VISIBLE);
//                    downloadingState.setVisibility((View.INVISIBLE));
//                    downloadingState.setActivated(false);
//                    loadingStateParent.setVisibility((View.INVISIBLE));
//                }
//                break;
//            case LOADING_STATE:
//                //设置为loading 状态
//                if (downloadingState.getVisibility() != View.VISIBLE) {
//                    Log.i("StickerAdapter", "LOADING_STATE");
//                    normalState.setVisibility(View.INVISIBLE);
//                    downloadingState.setActivated(true);
//                    downloadingState.setVisibility((View.VISIBLE));
//                    loadingStateParent.setVisibility((View.VISIBLE));
//                }
//                break;
//            case DONE_STATE:
//                //设置为下载完成状态
//                if (normalState.getVisibility() != View.INVISIBLE || downloadingState.getVisibility() != View.INVISIBLE) {
//                    Log.i("StickerAdapter", "DONE_STATE");
//                    normalState.setVisibility(View.INVISIBLE);
//                    downloadingState.setVisibility((View.INVISIBLE));
//                    downloadingState.setActivated(false);
//                    loadingStateParent.setVisibility((View.INVISIBLE));
//                }
//                break;
//        }
//    }
//
//    /**
//     * 首先鉴权，鉴权成功后，根据group id 获取相应的group 下的素材列表
//     */
//    protected void initStickerListFromNet() {
//        SenseArMaterialService.shareInstance().authorizeWithAppId(this, APPID, APPKEY, new SenseArMaterialService.OnAuthorizedListener() {
//            @Override
//            public void onSuccess() {
//                LogUtils.d(TAG, "鉴权成功！");
//                fetchGroupMaterialList(mStickerOptionsList);
//                fetchMakeUpGroupMaterialList(MAKE_UP_OPTIONS_LIST);
//            }
//
//            @Override
//            public void onFailure(SenseArMaterialService.AuthorizeErrorCode errorCode, String errorMsg) {
//                LogUtils.d(TAG, String.format(Locale.getDefault(), "鉴权失败！%d, %s", errorCode, errorMsg));
//            }
//        });
//    }
//
//    private void fetchMakeUpGroupMaterialList(final List<MakeUpOptionsItem> groups) {
//        for (int i = 0; i < groups.size(); i++) {
//            final MakeUpOptionsItem item = groups.get(i);
//            //使用网络下载
//            final int j = i;
//            SenseArMaterialService.shareInstance().fetchMaterialsFromGroupId("", item.groupId, SenseArMaterialType.Effect, new SenseArMaterialService.FetchMaterialListener() {
//                @Override
//                public void onSuccess(final List<SenseArMaterial> materials) {
//                    fetchMakeUpGroupMaterialInfo(item.groupName, materials, j);
//                }
//
//                @Override
//                public void onFailure(int code, String message) {
//                    Log.e(TAG, String.format(Locale.getDefault(), "下载素材信息失败！%d, %s", code, TextUtils.isEmpty(message) ? "" : message));
//                }
//            });
//        }
//
//    }
//
//    private void fetchMakeUpGroupMaterialInfo(final String groupName, final List<SenseArMaterial> materials, final int index) {
//        if (materials == null || materials.size() <= 0) {
//            return;
//        }
//        final ArrayList<MakeupItem> stickerList = mMakeupLists.get(groupName);
//        mMakeupLists.put(groupName, stickerList);
//        int localMakeUpLength = stickerList.size();
//
//        mMakeupAdapters.put(groupName, new MakeupAdapter(mMakeupLists.get(groupName), getApplicationContext()));
//        mMakeupAdapters.get(groupName).setSelectedPosition(-1);
//        Log.d(TAG, "group id is " + groupName + " materials size is " + materials.size());
//        //initStickerListener(groupId, index, materials);
//        for (int i = 0; i < materials.size(); i++) {
//            SenseArMaterial sarm = materials.get(i);
//            Bitmap bitmap = null;
//            try {
//                bitmap = ImageUtils.getImageSync(sarm.thumbnail, this);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (bitmap == null) {
//                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.none);
//            }
//            String path = "";
//            //如果已经下载则传入路径地址
//            if (SenseArMaterialService.shareInstance().isMaterialDownloaded(this, sarm)) {
//                path = SenseArMaterialService.shareInstance().getMaterialCachedPath(this, sarm);
//            }
//            sarm.cachedPath = path;
//            stickerList.add(new MakeupItem(sarm.name, bitmap, path));
//        }
//        CollectionSortUtils.sort(stickerList);
//        initMakeUpListener(groupName, materials);// 点击事件转移到这里
//    }
//
//    private void initMakeUpListener(final String groupName, final List<SenseArMaterial> materials) {
//        mMakeupAdapters.get(groupName).setClickMakeupListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                {
//                    final MakeupAdapter mMakeupAdapter = mMakeupAdapters.get(groupName);
//                    final int position = Integer.parseInt(v.getTag().toString());
//                    final MakeupItem stickerItem = mMakeupAdapters.get(groupName).getItem(position);
//                    if (stickerItem.state == StickerState.NORMAL_STATE && materials != null) {
//                        if (!NetworkUtils.isNetworkAvailable(getApplicationContext())) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(getApplicationContext(), "Network unavailable.", Toast.LENGTH_LONG).show();
//                                }
//                            });
//                            return;
//                        }
//                        stickerItem.state = StickerState.LOADING_STATE;
//                        notifyMakeUpViewState(stickerItem, position, groupName);
//                        SenseArMaterial sarm = getSenseArMaterialByName(materials, stickerItem.name);
//                        SenseArMaterialService.shareInstance().downloadMaterial(ImageActivity.this, sarm, new SenseArMaterialService.DownloadMaterialListener() {
//                            @Override
//                            public void onSuccess(final SenseArMaterial material) {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        stickerItem.path = material.cachedPath;
//                                        stickerItem.state = StickerState.DONE_STATE;
//                                        clickMakeUpItem(position, null, null, mMakeupAdapter, groupName);
//                                        notifyMakeUpViewState(stickerItem, position, groupName);
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onFailure(SenseArMaterial senseArMaterial, int i, String s) {
//                                Log.i(TAG, "下载失败");
//                            }
//
//                            @Override
//                            public void onProgress(SenseArMaterial senseArMaterial, float v, int i) {
//
//                            }
//                        });
//
//
//                    } else if (stickerItem.state == StickerState.DONE_STATE) {
//                        clickMakeUpItem(position, null, null, mMakeupAdapter, groupName);
//                    }
//                }
//            }
//        });
//    }
//
//    //初始化tab 点击
//    protected void initStickerTabListener() {
//        //tab 切换事件订阅
//        mStickerOptionsAdapter.setClickStickerListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mStickerOptionsList == null || mStickerOptionsList.size() <= 0) {
//                    LogUtils.e(TAG, "group 列表不能为空");
//                    return;
//                }
//                int position = Integer.parseInt(v.getTag().toString());
//                mStickerOptionsAdapter.setSelectedPosition(position);
//                mStickersRecycleView.setLayoutManager(new GridLayoutManager(mContext, 6));
//
//                //更新这一次的选择
//                StickerOptionsItem selectedItem = mStickerOptionsAdapter.getPositionItem(position);
//                if (selectedItem == null) {
//                    LogUtils.e(TAG, "选择项目不能为空!");
//                    return;
//                }
//                RecyclerView.Adapter selectedAdapter;
//                if (selectedItem.name.equals("sticker_new_engine")) {
//                    selectedAdapter = mNativeStickerAdapters.get(selectedItem.name);
//                } else if (selectedItem.name.equals("sticker_add_package")) {
//                    selectedAdapter = mNativeStickerAdapters.get(selectedItem.name);
//                } else {
//                    selectedAdapter = mStickerAdapters.get(selectedItem.name);
//                }
//
//                if (selectedAdapter == null) {
//                    LogUtils.e(TAG, "贴纸adapter 不能为空");
//                    Toast.makeText(getApplicationContext(), "列表正在拉取，或拉取出错!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                mStickersRecycleView.setAdapter(selectedAdapter);
//                mStickerOptionsAdapter.notifyDataSetChanged();
//                selectedAdapter.notifyDataSetChanged();
//            }
//        });
//    }
//
//    private void initAddPackageStickerAdapter(final String stickerClassName, final int index) {
//        mNativeStickerAdapters.get(stickerClassName).setSelectedPosition(-1);
//        mNativeStickerAdapters.get(stickerClassName).setClickStickerListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick() called with: v = [" + v + "]");
//                if (mStickerPackageMap != null) {
//                    mStickerPackageMap.clear();
//                }
//
//                if (mNativeStickerAdapters.get("sticker_new_engine") != null) {
//                    mNativeStickerAdapters.get("sticker_new_engine").setSelectedPosition(-1);
//                    mNativeStickerAdapters.get("sticker_new_engine").notifyDataSetChanged();
//                }
//
//                //重置所有状态为为选中状态
//                for (StickerOptionsItem optionsItem : mStickerOptionsList) {
//                    if (optionsItem.name.equals("sticker_new_engine")) {
//                        continue;
//                    } else if (optionsItem.name.equals("object_track")) {
//                        continue;
//                    } else {
//                        if (mStickerAdapters.get(optionsItem.name) != null) {
//                            mStickerAdapters.get(optionsItem.name).setSelectedPosition(-1);
//                            mStickerAdapters.get(optionsItem.name).notifyDataSetChanged();
//                        }
//                    }
//                }
//                int position = Integer.parseInt(v.getTag().toString());
//
//                if (mCurrentStickerOptionsIndex == index && mCurrentStickerPosition == position) {
//                    mNativeStickerAdapters.get(stickerClassName).setSelectedPosition(-1);
//                    mCurrentStickerOptionsIndex = -1;
//                    mCurrentStickerPosition = -1;
//
//                    findViewById(R.id.iv_close_sticker).setBackground(getResources().getDrawable(R.drawable.close_sticker_selected));
//                    mImageDisplay.enableSticker(false);
//                    int size = mImageDisplay.mCurrentStickerMaps.size();
//                    mImageDisplay.removeSticker(mNewStickers.get(position).path);
//                    if (size == 1) recoverUI();
//                } else {
//                    mCurrentStickerOptionsIndex = index;
//                    mCurrentStickerPosition = position;
//
//                    findViewById(R.id.iv_close_sticker).setBackground(getResources().getDrawable(R.drawable.close_sticker));
//
//                    mNativeStickerAdapters.get(stickerClassName).setSelectedPosition(position);
//                    mImageDisplay.enableSticker(true);
//                    mImageDisplay.addSticker(mNewStickers.get(position).path);
//                }
//
//                mNativeStickerAdapters.get(stickerClassName).notifyDataSetChanged();
//            }
//        });
//    }
//
//    /**
//     * 根据group id 对应素材列表
//     *
//     * @param groups group id 列表
//     */
//    protected void fetchGroupMaterialList(final List<StickerOptionsItem> groups) {
//        String[] array = {"sticker_new_engine", "object_track", "sticker_add_package"};
//        for (int i = 0; i < groups.size(); i++) {
//            final StickerOptionsItem groupId = groups.get(i);
//            if (STUtils.contains(array, groupId.name)) continue;
//            //使用网络下载
//            final int j = i;
//            SenseArMaterialService.shareInstance().fetchMaterialsFromGroupId("", groupId.name, SenseArMaterialType.Effect, new SenseArMaterialService.FetchMaterialListener() {
//                @Override
//                public void onSuccess(final List<SenseArMaterial> materials) {
//                    fetchGroupMaterialInfo(groupId.name, materials, j);
//                    if (groupId.name == GROUP_NEW) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                mStickersRecycleView.setAdapter(mStickerAdapters.get(GROUP_NEW));
//                            }
//                        });
//                    }
//                }
//
//                @Override
//                public void onFailure(int code, String message) {
//                    LogUtils.e(TAG, String.format(Locale.getDefault(), "下载素材信息失败！%d, %s", code, message));
//                }
//            });
//        }
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (mStickerOptionsRecycleView.getAdapter() == null) {
//                    mStickerOptionsRecycleView.setAdapter(mStickerOptionsAdapter);
//                }
//                mStickerOptionsAdapter.setSelectedPosition(0);
//                mStickerOptionsAdapter.notifyDataSetChanged();
//            }
//        });
//
//    }
//
//    /**
//     * 初始化素材列表中的点击事件回调
//     *
//     * @param groupId
//     * @param index
//     * @param materials
//     */
//    protected void initStickerListener(final String groupId, final int index, final List<SenseArMaterial> materials) {
//        mStickerAdapters.get(groupId).setClickStickerListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                recoverUI();
//                if (!NetworkUtils.isNetworkAvailable(getApplicationContext())) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getApplicationContext(), "Network unavailable.", Toast.LENGTH_LONG).show();
//
//                        }
//                    });
//                }
//                final int position = Integer.parseInt(v.getTag().toString());
//                final StickerItem stickerItem = mStickerAdapters.get(groupId).getItem(position);
//                if (stickerItem != null && stickerItem.state == StickerState.LOADING_STATE) {
//                    LogUtils.d(TAG, String.format(Locale.getDefault(), "正在下载，请稍后点击!"));
//                    return;
//                }
//
//                if (mCurrentStickerOptionsIndex == index && mCurrentStickerPosition == position) {
//                    preMaterialId = "";
//                    mStickerAdapters.get(groupId).setSelectedPosition(-1);
//                    mCurrentStickerOptionsIndex = -1;
//                    mCurrentStickerPosition = -1;
//
//                    findViewById(R.id.iv_close_sticker).setBackground(getResources().getDrawable(R.drawable.close_sticker_selected));
//                    mImageDisplay.enableSticker(false);
//                    mImageDisplay.removeAllStickers();
//                    mStickerAdapters.get(groupId).notifyDataSetChanged();
//                    recoverUI();
//                    return;
//                }
//                final SenseArMaterial sarm = materials.get(position);
//                preMaterialId = sarm.id;
//                preMaterialId = sarm.id;
//                //如果素材还未下载，点击时需要下载
//                if (stickerItem.state == StickerState.NORMAL_STATE) {
//                    stickerItem.state = StickerState.LOADING_STATE;
//                    notifyStickerViewState(stickerItem, position, groupId);
////                    mStickerAdapters.get(groupId).notifyDataSetChanged();
//                    SenseArMaterialService.shareInstance().downloadMaterial(ImageActivity.this, sarm, new SenseArMaterialService.DownloadMaterialListener() {
//                        @Override
//                        public void onSuccess(final SenseArMaterial material) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    stickerItem.path = material.cachedPath;
//                                    stickerItem.state = StickerState.DONE_STATE;
//                                    //如果本次下载是用户用户最后一次选中项，则直接应用
//                                    if (preMaterialId.equals(sarm.id)) {
//                                        resetNewStickerAdapter();
//                                        resetStickerAdapter();
//                                        mCurrentStickerOptionsIndex = index;
//                                        mCurrentStickerPosition = position;
//                                        findViewById(R.id.iv_close_sticker).setBackground(getResources().getDrawable(R.drawable.close_sticker));
//
//                                        mStickerAdapters.get(groupId).setSelectedPosition(position);
//                                        mImageDisplay.enableSticker(true);
//                                        recoverUI();
//                                        mImageDisplay.changeSticker(stickerItem.path);
//                                    }
//                                    notifyStickerViewState(stickerItem, position, groupId);
////                                    mStickerAdapters.get(groupId).notifyDataSetChanged();
//                                }
//                            });
//                            LogUtils.d(TAG, String.format(Locale.getDefault(), "素材下载成功:%s,cached path is %s", material.materials, material.cachedPath));
//                        }
//
//                        @Override
//                        public void onFailure(SenseArMaterial material, int code, String message) {
//                            LogUtils.d(TAG, String.format(Locale.getDefault(), "素材下载失败:%s", material.materials));
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    stickerItem.state = StickerState.NORMAL_STATE;
//                                    notifyStickerViewState(stickerItem, position, groupId);
////                                    mStickerAdapters.get(groupId).notifyDataSetChanged();
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onProgress(SenseArMaterial material, float progress, int size) {
//
//                        }
//                    });
//                } else if (stickerItem.state == StickerState.DONE_STATE) {
//                    resetNewStickerAdapter();
//                    resetStickerAdapter();
//                    mCurrentStickerOptionsIndex = index;
//                    mCurrentStickerPosition = position;
//
//                    findViewById(R.id.iv_close_sticker).setBackground(getResources().getDrawable(R.drawable.close_sticker));
//
//                    mStickerAdapters.get(groupId).setSelectedPosition(position);
//                    mImageDisplay.enableSticker(true);
//                    mImageDisplay.changeSticker(mStickerlists.get(groupId).get(position).path);
//                }
//            }
//        });
//    }
//
//    protected void initNativeStickerAdapter(final String stickerClassName, final int index) {
//        mNativeStickerAdapters.get(stickerClassName).setSelectedPosition(-1);
//        mNativeStickerAdapters.get(stickerClassName).setClickStickerListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                resetNewStickerAdapter();
//                resetStickerAdapter();
//                int position = Integer.parseInt(v.getTag().toString());
//
//                if (mCurrentStickerOptionsIndex == index && mCurrentStickerPosition == position) {
//                    mNativeStickerAdapters.get(stickerClassName).setSelectedPosition(-1);
//                    mCurrentStickerOptionsIndex = -1;
//                    mCurrentStickerPosition = -1;
//
//                    findViewById(R.id.iv_close_sticker).setBackground(getResources().getDrawable(R.drawable.close_sticker_selected));
//                    mImageDisplay.enableSticker(false);
//                    mImageDisplay.changeSticker(null);
//                    recoverUI();
//                } else {
//                    mCurrentStickerOptionsIndex = index;
//                    mCurrentStickerPosition = position;
//
//                    findViewById(R.id.iv_close_sticker).setBackground(getResources().getDrawable(R.drawable.close_sticker));
//
//                    mNativeStickerAdapters.get(stickerClassName).setSelectedPosition(position);
//                    mImageDisplay.enableSticker(true);
//                    recoverUI();
//                    mImageDisplay.changeSticker(mNewStickers.get(position).path);
//                }
//
//                mNativeStickerAdapters.get(stickerClassName).notifyDataSetChanged();
//            }
//        });
//    }
//
//    /**
//     * 初始化素材的基本信息，如缩略图，是否已经缓存
//     *
//     * @param groupId   组id
//     * @param materials 服务器返回的素材list
//     */
//    protected void fetchGroupMaterialInfo(final String groupId, final List<SenseArMaterial> materials, final int index) {
//        final ArrayList<StickerItem> stickerList = new ArrayList<>();
//        LogUtils.e(TAG, "group id is " + groupId + " materials size is " + materials.size());
//        mStickerlists.put(groupId, stickerList);
//        mStickerAdapters.put(groupId, new StickerAdapter(mStickerlists.get(groupId), getApplicationContext()));
//        mStickerAdapters.get(groupId).setSelectedPosition(-1);
//        initStickerListener(groupId, index, materials);
//        for (int i = 0; i < materials.size(); i++) {
//            SenseArMaterial sarm = materials.get(i);
//            Bitmap bitmap = null;
//            try {
//                bitmap = ImageUtils.getImageSync(sarm.thumbnail, ImageActivity.this);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (bitmap == null) {
//                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.none);
//            }
//            String path = "";
//            //如果已经下载则传入路径地址
//            if (SenseArMaterialService.shareInstance().isMaterialDownloaded(ImageActivity.this, sarm)) {
//                path = SenseArMaterialService.shareInstance().getMaterialCachedPath(ImageActivity.this, sarm);
//            }
//            sarm.cachedPath = path;
//            stickerList.add(new StickerItem(sarm.name, bitmap, path));
//        }
//    }
//
//    private boolean checkMicroType(int beautyOptionsPosition) {
//        int type = mBeautyOptionSelectedIndex.get(beautyOptionsPosition);
//        boolean ans = ((type != 0) &&
//                (type != 1) &&
//                (type != 4) &&
//                (type != 5) &&
//                (type != 7) &&
//                (type != 12) &&
//                (type != 13) &&
//                (type != 14) &&
//                (type != 17) &&
//                (type != 15) && (type != 16) && (type != 18));
//        return ans && (3 == beautyOptionsPosition);
//    }
//
//    protected void updateMakeupOptions(int type, boolean value) {
//        setMakeUpViewSelected(value, type);
//        switch (type) {
//            case Constants.ST_MAKEUP_ALL:
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.selector_makeup_all_img));
//                break;
//            case Constants.ST_MAKEUP_HAIR_DYE:
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.selector_makeup_hairdye_img));
//                break;
//            case Constants.ST_MAKEUP_LIP:
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.selector_makeup_lip_img));
//                break;
//            case Constants.ST_MAKEUP_BLUSH://腮红
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.selector_makeup_cheeks_img));
//                break;
//            case Constants.ST_MAKEUP_HIGHLIGHT://修容
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.selector_makeup_face_img));
//                break;
//            case Constants.ST_MAKEUP_BROW://眉毛
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.selector_makeup_brow_img));
//                break;
//            case Constants.ST_MAKEUP_EYE://眼影
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.selector_makeup_eye_img));
//                break;
//            case Constants.ST_MAKEUP_EYELINER://眼线
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.selector_makeup_eyeline_img));
//                break;
//            case Constants.ST_MAKEUP_EYELASH://眼睫毛
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.selector_makeup_eyelash_img));
//                break;
//            case Constants.ST_MAKEUP_EYEBALL://美瞳
//                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.selector_makeup_eyeball_img));
//                break;
//        }
//        mMakeupGroupBack.setSelected(value);
//    }
//
//    protected void resetMakeup(boolean needResetAll, boolean onlyResetUI) {
//        //美妆互斥逻辑
//        int count = Constants.MAKEUP_TYPE_COUNT;
//        if (!needResetAll) {
//            count = Constants.MAKEUP_TYPE_COUNT - 1;
//        }
//
//        if (!onlyResetUI) {
//            for (int i = 0; i < count; i++) {
//                mImageDisplay.removeMakeupByType(i);
//                mMakeupOptionSelectedIndex.put(i, 0);
//                mMakeupStrength.put(i, 80);
//            }
//        }
//
//        mFilterStrengthLayout.setVisibility(View.INVISIBLE);
//
//        setMakeUpViewSelected(false, -1);
//
//        mMakeupAdapters.get("makeup_lip").setSelectedPosition(0);
//        mMakeupAdapters.get("makeup_lip").notifyDataSetChanged();
//        mMakeupAdapters.get("makeup_highlight").setSelectedPosition(0);
//        mMakeupAdapters.get("makeup_highlight").notifyDataSetChanged();
//        mMakeupAdapters.get("makeup_blush").setSelectedPosition(0);
//        mMakeupAdapters.get("makeup_blush").notifyDataSetChanged();
//        mMakeupAdapters.get("makeup_brow").setSelectedPosition(0);
//        mMakeupAdapters.get("makeup_brow").notifyDataSetChanged();
//        mMakeupAdapters.get("makeup_eye").setSelectedPosition(0);
//        mMakeupAdapters.get("makeup_eye").notifyDataSetChanged();
//        mMakeupAdapters.get("makeup_eyeliner").setSelectedPosition(0);
//        mMakeupAdapters.get("makeup_eyeliner").notifyDataSetChanged();
//        mMakeupAdapters.get("makeup_eyelash").setSelectedPosition(0);
//        mMakeupAdapters.get("makeup_eyelash").notifyDataSetChanged();
//
//        mMakeupAdapters.get("makeup_eyeball").setSelectedPosition(0);
//        mMakeupAdapters.get("makeup_eyeball").notifyDataSetChanged();
//        mMakeupAdapters.get("makeup_hairdye").setSelectedPosition(0);
//        mMakeupAdapters.get("makeup_hairdye").notifyDataSetChanged();
//
//        if (needResetAll) {
//            setMakeUpViewSelected(false, Constants.ST_MAKEUP_ALL);
//            mMakeupAdapters.get("makeup_all").setSelectedPosition(0);
//            mMakeupAdapters.get("makeup_all").notifyDataSetChanged();
//        }
//    }
//
//    protected String getMakeupNameOfType(int type) {
//        String name = "makeup_blush";
//        if (type == STMobileMakeupType.ST_MAKEUP_TYPE_BROW) {
//            name = "makeup_brow";
//        } else if (type == STMobileMakeupType.ST_MAKEUP_TYPE_EYE) {
//            name = "makeup_eye";
//        } else if (type == STMobileMakeupType.ST_MAKEUP_TYPE_BLUSH) {
//            name = "makeup_blush";
//        } else if (type == STMobileMakeupType.ST_MAKEUP_TYPE_LIP) {
//            name = "makeup_lip";
//        } else if (type == STMobileMakeupType.ST_MAKEUP_TYPE_HIGHLIGHT) {
//            name = "makeup_highlight";
//        } else if (type == STMobileMakeupType.ST_MAKEUP_TYPE_EYELINER) {
//            name = "makeup_eyeliner";
//        } else if (type == STMobileMakeupType.ST_MAKEUP_TYPE_EYELASH) {
//            name = "makeup_eyelash";
//        } else if (type == STMobileMakeupType.ST_MAKEUP_TYPE_EYEBALL) {
//            name = "makeup_eyeball";
//        } else if (type == STMobileMakeupType.ST_MAKEUP_TYPE_HAIR_DYE) {
//            name = "makeup_hairdye";
//        } else if (type == STMobileMakeupType.ST_MAKEUP_TYPE_ALL) {
//            name = "makeup_all";
//        }
//
//        return name;
//    }
//
//    protected boolean checkMakeUpSelect() {
//        for (Map.Entry<Integer, Integer> entry : mMakeupOptionSelectedIndex.entrySet()) {
//            if (entry.getValue() != 0) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    //整体效果
//    protected void initFullBeauty() {
//        mBeautyFullItem = LocalDataStore.getInstance().getFullBeautyList();
//        mBeautyFullAdapter = new FullBeautyItemAdapter(this, mBeautyFullItem);
//        mBeautyOption.put(0, "fullBeauty");
//        mBeautyBaseRecycleView.setAdapter(mBeautyFullAdapter);
//        lastIndex = -1;
//    }
//
//    public boolean isEmpty(String str) {
//        if (str != null) str = str.trim();
//        return TextUtils.isEmpty(str) || "null".equalsIgnoreCase(str);
//    }
//
//    private void setFullBeauty(FullBeautyItem fullBeautyItem) {
//        mBeautyFullAdapter.setSelectedPosition(mCurrentFullBeautyIndex);
//        mBeautyFullAdapter.notifyDataSetChanged();
//        //处理美妆
//        resetMakeup(true, false);
//        if (fullBeautyItem.getMakeupList() != null) {
//            for (int i = 0; i < fullBeautyItem.getMakeupList().size(); i++) {
//                String type = fullBeautyItem.getMakeupList().get(i).getType();
//                String name = fullBeautyItem.getMakeupList().get(i).getMakeupName();
//                int strength = fullBeautyItem.getMakeupList().get(i).getStrength();
//                int position = getMakeupIndexByName(type, name);
//
//                setMakeupWithType(type, strength, position);
//            }
//        }
//
//        //设置滤镜
//        if (fullBeautyItem.getFilter() == null) {
//            resetFilterView(false);
//
//        } else {
//            setFilterWithType(fullBeautyItem.getFilter());
//        }
//        mDefaultMakeupStrength.clear();
//        mDefaultMakeupStrength.putAll(mMakeupStrength);
//    }
//
//    protected void setBeautyParamForFullBeauty(float[] beautifyParamsBase, float[] beautifyParamsProfessional, float[] beautifyParamsMicro, float[] beautifyParamsAdjust) {
//        for (int i = 0; i < beautifyParamsBase.length; i++) {
//            mImageDisplay.setBeautyParam(STEffectBeautyType.EFFECT_BEAUTY_BASE_WHITTEN + i, (beautifyParamsBase[i]));
//        }
//
//        for (int i = 0; i < beautifyParamsProfessional.length; i++) {
//            mImageDisplay.setBeautyParam(STEffectBeautyType.EFFECT_BEAUTY_RESHAPE_SHRINK_FACE + i, (beautifyParamsProfessional[i]));
//        }
//
//        for (int i = 0; i < beautifyParamsMicro.length; i++) {
//            mImageDisplay.setBeautyParam(STEffectBeautyType.EFFECT_BEAUTY_PLASTIC_THINNER_HEAD + i, (beautifyParamsMicro[i]));
//        }
//
//        for (int i = 0; i < beautifyParamsAdjust.length; i++) {
//            mImageDisplay.setBeautyParam(STEffectBeautyType.EFFECT_BEAUTY_TONE_CONTRAST + i, (beautifyParamsAdjust[i]));
//        }
//    }
//
//    protected void setBeautyListsForFullBeauty(int beautyOptionsPosition, float[] beautifyParams) {
//        for (int i = 0; i < beautifyParams.length; i++) {
//            mBeautylists.get(mBeautyOption.get(beautyOptionsPosition)).get(i).setProgress((int) (beautifyParams[i] * 100));
//        }
//    }
//
//    protected void setMakeupWithType(String type, int strength, int position) {
//        if (type.equals("makeup_lip")) {
//            mImageDisplay.removeMakeupByType(Constants.ST_MAKEUP_LIP);
//            mMakeupOptionSelectedIndex.put(Constants.ST_MAKEUP_LIP, position);
//            mMakeupStrength.put(Constants.ST_MAKEUP_LIP, strength);
//            updateMakeupOptions(Constants.ST_MAKEUP_LIP, true);
//            mMakeupAdapters.get("makeup_lip").setSelectedPosition(position);
//            mMakeupAdapters.get("makeup_lip").notifyDataSetChanged();
//            mImageDisplay.setMakeupForType(Constants.ST_MAKEUP_LIP, mMakeupLists.get("makeup_lip").get(position).path);
//            mImageDisplay.setStrengthForType(Constants.ST_MAKEUP_LIP, (float) strength / 100.f);
//            mImageDisplay.enableMakeUp(true);
//        } else if (type.equals("makeup_highlight")) {
//            mImageDisplay.removeMakeupByType(Constants.ST_MAKEUP_HIGHLIGHT);
//            mMakeupOptionSelectedIndex.put(Constants.ST_MAKEUP_HIGHLIGHT, position);
//            mMakeupStrength.put(Constants.ST_MAKEUP_HIGHLIGHT, strength);
//            updateMakeupOptions(Constants.ST_MAKEUP_HIGHLIGHT, true);
//            mMakeupAdapters.get("makeup_highlight").setSelectedPosition(position);
//            mMakeupAdapters.get("makeup_highlight").notifyDataSetChanged();
//            mImageDisplay.setMakeupForType(Constants.ST_MAKEUP_HIGHLIGHT, mMakeupLists.get("makeup_highlight").get(position).path);
//            mImageDisplay.setStrengthForType(Constants.ST_MAKEUP_HIGHLIGHT, (float) strength / 100.f);
//            mImageDisplay.enableMakeUp(true);
//        } else if (type.equals("makeup_blush")) {
//            mImageDisplay.removeMakeupByType(Constants.ST_MAKEUP_BLUSH);
//            mMakeupOptionSelectedIndex.put(Constants.ST_MAKEUP_BLUSH, position);
//            mMakeupStrength.put(Constants.ST_MAKEUP_BLUSH, strength);
//            updateMakeupOptions(Constants.ST_MAKEUP_BLUSH, true);
//            mMakeupAdapters.get("makeup_blush").setSelectedPosition(position);
//            mMakeupAdapters.get("makeup_blush").notifyDataSetChanged();
//            mImageDisplay.setMakeupForType(Constants.ST_MAKEUP_BLUSH, mMakeupLists.get("makeup_blush").get(position).path);
//            mImageDisplay.setStrengthForType(Constants.ST_MAKEUP_BLUSH, (float) strength / 100.f);
//            mImageDisplay.enableMakeUp(true);
//        } else if (type.equals("makeup_brow")) {
//            mImageDisplay.removeMakeupByType(Constants.ST_MAKEUP_BROW);
//            mMakeupOptionSelectedIndex.put(Constants.ST_MAKEUP_BROW, position);
//            mMakeupStrength.put(Constants.ST_MAKEUP_BROW, strength);
//            updateMakeupOptions(Constants.ST_MAKEUP_BROW, true);
//            mMakeupAdapters.get("makeup_brow").setSelectedPosition(position);
//            mMakeupAdapters.get("makeup_brow").notifyDataSetChanged();
//            mImageDisplay.setMakeupForType(Constants.ST_MAKEUP_BROW, mMakeupLists.get("makeup_brow").get(position).path);
//            mImageDisplay.setStrengthForType(Constants.ST_MAKEUP_BROW, (float) strength / 100.f);
//            mImageDisplay.enableMakeUp(true);
//        } else if (type.equals("makeup_eye")) {
//            mImageDisplay.removeMakeupByType(Constants.ST_MAKEUP_EYE);
//            mMakeupOptionSelectedIndex.put(Constants.ST_MAKEUP_EYE, position);
//            mMakeupStrength.put(Constants.ST_MAKEUP_EYE, strength);
//            updateMakeupOptions(Constants.ST_MAKEUP_EYE, true);
//            mMakeupAdapters.get("makeup_eye").setSelectedPosition(position);
//            mMakeupAdapters.get("makeup_eye").notifyDataSetChanged();
//            mImageDisplay.setMakeupForType(Constants.ST_MAKEUP_EYE, mMakeupLists.get("makeup_eye").get(position).path);
//            mImageDisplay.setStrengthForType(Constants.ST_MAKEUP_EYE, (float) strength / 100.f);
//            mImageDisplay.enableMakeUp(true);
//        } else if (type.equals("makeup_eyeliner")) {
//            mImageDisplay.removeMakeupByType(Constants.ST_MAKEUP_EYELINER);
//            mMakeupOptionSelectedIndex.put(Constants.ST_MAKEUP_EYELINER, position);
//            mMakeupStrength.put(Constants.ST_MAKEUP_EYELINER, strength);
//            updateMakeupOptions(Constants.ST_MAKEUP_EYELINER, true);
//            mMakeupAdapters.get("makeup_eyeliner").setSelectedPosition(position);
//            mMakeupAdapters.get("makeup_eyeliner").notifyDataSetChanged();
//            mImageDisplay.setMakeupForType(Constants.ST_MAKEUP_EYELINER, mMakeupLists.get("makeup_eyeliner").get(position).path);
//            mImageDisplay.setStrengthForType(Constants.ST_MAKEUP_EYELINER, (float) strength / 100.f);
//            mImageDisplay.enableMakeUp(true);
//        } else if (type.equals("makeup_eyelash")) {
//            mImageDisplay.removeMakeupByType(Constants.ST_MAKEUP_EYELASH);
//            mMakeupOptionSelectedIndex.put(Constants.ST_MAKEUP_EYELASH, position);
//            mMakeupStrength.put(Constants.ST_MAKEUP_EYELASH, strength);
//            updateMakeupOptions(Constants.ST_MAKEUP_EYELASH, true);
//            mMakeupAdapters.get("makeup_eyelash").setSelectedPosition(position);
//            mMakeupAdapters.get("makeup_eyelash").notifyDataSetChanged();
//            mImageDisplay.setMakeupForType(Constants.ST_MAKEUP_EYELASH, mMakeupLists.get("makeup_eyelash").get(position).path);
//            mImageDisplay.setStrengthForType(Constants.ST_MAKEUP_EYELASH, (float) strength / 100.f);
//            mImageDisplay.enableMakeUp(true);
//        } else if (type.equals("makeup_eyeball")) {
//            mImageDisplay.removeMakeupByType(Constants.ST_MAKEUP_EYEBALL);
//            mMakeupOptionSelectedIndex.put(Constants.ST_MAKEUP_EYEBALL, position);
//            mMakeupStrength.put(Constants.ST_MAKEUP_EYEBALL, strength);
//            updateMakeupOptions(Constants.ST_MAKEUP_EYEBALL, true);
//            mMakeupAdapters.get("makeup_eyeball").setSelectedPosition(position);
//            mMakeupAdapters.get("makeup_eyeball").notifyDataSetChanged();
//            mImageDisplay.setMakeupForType(Constants.ST_MAKEUP_EYEBALL, mMakeupLists.get("makeup_eyeball").get(position).path);
//            mImageDisplay.setStrengthForType(Constants.ST_MAKEUP_EYEBALL, (float) strength / 100.f);
//            mImageDisplay.enableMakeUp(true);
//        } else if (type.equals("makeup_hairdye")) {
//            mImageDisplay.removeMakeupByType(Constants.ST_MAKEUP_HAIR_DYE);
//            mMakeupOptionSelectedIndex.put(Constants.ST_MAKEUP_HAIR_DYE, position);
//            mMakeupStrength.put(Constants.ST_MAKEUP_HAIR_DYE, strength);
//            updateMakeupOptions(Constants.ST_MAKEUP_HAIR_DYE, true);
//            mMakeupAdapters.get("makeup_hairdye").setSelectedPosition(position);
//            mMakeupAdapters.get("makeup_hairdye").notifyDataSetChanged();
//            mImageDisplay.setMakeupForType(Constants.ST_MAKEUP_HAIR_DYE, mMakeupLists.get("makeup_hairdye").get(position).path);
//            mImageDisplay.setStrengthForType(Constants.ST_MAKEUP_HAIR_DYE, (float) strength / 100.f);
//            mImageDisplay.enableMakeUp(true);
//        } else if (type.equals("makeup_all")) {
//            mImageDisplay.removeMakeupByType(Constants.ST_MAKEUP_ALL);
//            mMakeupOptionSelectedIndex.put(Constants.ST_MAKEUP_ALL, position);
//            mMakeupStrength.put(Constants.ST_MAKEUP_ALL, strength);
//            updateMakeupOptions(Constants.ST_MAKEUP_ALL, true);
//            mMakeupAdapters.get("makeup_all").setSelectedPosition(position);
//            mMakeupAdapters.get("makeup_all").notifyDataSetChanged();
//            mImageDisplay.setMakeupForType(Constants.ST_MAKEUP_ALL, mMakeupLists.get("makeup_all").get(position).path);
//            mImageDisplay.setStrengthForType(Constants.ST_MAKEUP_ALL, (float) strength / 100.f);
//            mImageDisplay.enableMakeUp(true);
//        }
//    }
//
//    public int getMakeupIndexByName(String type, String name) {
//        int position = 0;
//        if (mMakeupLists.get(type).size() > 0) {
//            for (int i = 0; i < mMakeupLists.get(type).size(); i++) {
//                if (mMakeupLists.get(type).get(i).name.equals(name)) {
//                    position = i;
//                }
//            }
//        }
//
//        return position;
//    }
//
//    public void setFilterWithType(FilterInfo filterInfo) {
//        resetFilterView(false);
//
//        if (filterInfo == null) {
//            mFilterAdapters.get("filter_portrait").setSelectedPosition(0);
//            mFilterAdapters.get("filter_scenery").setSelectedPosition(0);
//            mFilterAdapters.get("filter_still_life").setSelectedPosition(0);
//            mFilterAdapters.get("filter_food").setSelectedPosition(0);
//            mImageDisplay.setFilterStyle("", null, "");
//            mImageDisplay.enableFilter(false);
//        }
//
//        String type = filterInfo.getType();
//        String filterName = filterInfo.getFilterName();
//        int strength = filterInfo.getStrength();
//        if (mFilterLists.get(type).size() > 0) {
//            for (int i = 0; i < mFilterLists.get(type).size(); i++) {
//                if (mFilterLists.get(type).get(i).name.equals(filterName)) {
//                    mCurrentFilterIndex = i;
//                }
//            }
//
//            if (mCurrentFilterIndex > 0) {
//
//                mFilterAdapters.get(type).setSelectedPosition(mCurrentFilterIndex);
//                mImageDisplay.setFilterStyle(type, mFilterLists.get(type).get(mCurrentFilterIndex).model, mFilterLists.get(type).get(mCurrentFilterIndex).name);
//                mImageDisplay.setFilterStrength((float) strength / 100f);
//                mImageDisplay.enableFilter(true);
//                mFilterStrength = strength;
//                mDefaultFilterStrength = strength;
//
//                if (type.equals("filter_portrait")) {
//                    mCurrentFilterGroupIndex = 0;
//                } else if (type.equals("filter_scenery")) {
//                    mCurrentFilterGroupIndex = 1;
//                } else if (type.equals("filter_still_life")) {
//                    mCurrentFilterGroupIndex = 2;
//                } else if (type.equals("filter_food")) {
//                    mCurrentFilterGroupIndex = 3;
//                }
//                setFilterViewSelected(mCurrentFilterGroupIndex);
//
//                mFilterAdapters.get(type).notifyDataSetChanged();
//            }
//        }
//    }
//
//    protected void clearFullBeautyView() {
//        gpFullBeautySeekBar.setVisibility(View.GONE);
//        if (mCurrentBeautyIndex == 0) {
//            mIndicatorSeekbar.setVisibility(View.INVISIBLE);
//        }
//        mCurrentFullBeautyIndex = -1;
//        lastIndex = -1;
//        mBeautyFullAdapter.setSelectedPosition(mCurrentFullBeautyIndex);
//        mBeautyFullAdapter.notifyDataSetChanged();
//    }
//
//    protected void resetFullBeautyView() {
//        setFullBeauty(mBeautyFullItem.get(0));
//        mCurrentFullBeautyIndex = 0;
//        mBeautyFullAdapter.setSelectedPosition(mCurrentFullBeautyIndex);
//        mBeautyFullAdapter.notifyDataSetChanged();
//    }
//
//    protected void resetZeroFullBeautyView() {
//        resetMakeup(true, false);
//        resetFilterView(false);
//        lastIndex = -1;
//        mCurrentFullBeautyIndex = -1;
//        mBeautyFullAdapter.setSelectedPosition(mCurrentFullBeautyIndex);
//        mBeautyFullAdapter.notifyDataSetChanged();
//        gpFullBeautySeekBar.setVisibility(View.GONE);
//        mIndicatorSeekbar.setVisibility(View.INVISIBLE);
//    }
//
//    private void showMakeupTips() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(getApplicationContext(), MultiLanguageUtils.getStr(R.string.toast_cancel_makeup), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    protected void updateBeautyParamsFromPackage() {
//        //处理美颜
//        int[] indexOfBeauty = {1, 2, 3, 6};
//        for (int i = 0; i < indexOfBeauty.length; i++) {
//            if (i == 0) {
//                setBeautyListsForFullBeauty(indexOfBeauty[i], mImageDisplay.getBeautifyParamsTypeBase());
//            } else if (i == 1) {
//                setBeautyListsForFullBeauty(indexOfBeauty[i], mImageDisplay.getBeautifyParamsTypeProfessional());
//            } else if (i == 2) {
//                setBeautyListsForFullBeauty(indexOfBeauty[i], mImageDisplay.getBeautifyParamsTypeMicro());
//            } else if (i == 3) {
//                setBeautyListsForFullBeauty(indexOfBeauty[i], mImageDisplay.getBeautifyParamsTypeAdjust());
//            }
//            String str = mBeautyOption.get(mBeautyOptionsPosition);
//            if (mBeautyItemAdapters.get(str) != null)
//                mBeautyItemAdapters.get(str).notifyDataSetChanged();
////            mBeautyItemAdapters.get(mBeautyOption.get(indexOfBeauty[i])).notifyDataSetChanged();
////            if (checkMicroType(indexOfBeauty[i])) {
////                mIndicatorSeekbar.getSeekBar().setProgress(STUtils.convertToData(mBeautylists.get(mBeautyOption.get(indexOfBeauty[i])).get(mBeautyOptionSelectedIndex.get(indexOfBeauty[i])).getProgress()));
////            } else {
////                mIndicatorSeekbar.getSeekBar().setProgress(mBeautylists.get(mBeautyOption.get(indexOfBeauty[i])).get(mBeautyOptionSelectedIndex.get(indexOfBeauty[i])).getProgress());
////            }
////            mIndicatorSeekbar.updateTextView(mBeautylists.get(mBeautyOption.get(indexOfBeauty[i])).get(mBeautyOptionSelectedIndex.get(indexOfBeauty[i])).getProgress());
//        }
//
//        if (mImageDisplay.getNeedResetMakeupView() /*&& mLastStickerPosition==-1*/) {
//            clearFullBeautyView();
//            resetMakeup(true, true);
//            setMakeUpViewSelected(false, -1);
//
//            for (int i = 0; i < Constants.MAKEUP_TYPE_COUNT; i++) {
//                mMakeupOptionSelectedIndex.put(i, 0);
//            }
//        }
//        if (mImageDisplay.getNeedResetFilterView()) {
//            clearFullBeautyView();
//            resetFilterView(true);
//        }
//
//        refreshSeekBar();
//
//        mImageDisplay.mBeautifyParamsTypeBase= EffectInfoDataHelper.getInstance().getBaseParams();
//
//        mImageDisplay.mBeautifyParamsTypeBase= EffectInfoDataHelper.getInstance().getBaseParams();
//
//        mImageDisplay.mBeautifyParamsTypeProfessional= EffectInfoDataHelper.getInstance().getProfessionalParams();
//
//        // 微整形
//        mImageDisplay.mBeautifyParamsTypeMicro= EffectInfoDataHelper.getInstance().getMicroParams();
//
//        // 调整
//        mImageDisplay.mBeautifyParamsTypeAdjust= EffectInfoDataHelper.getInstance().getAdjustParams();
//
//        // 美妆
//        String[] arrays = EffectInfoDataHelper.getInstance().getCurrentMakeUp();
//        setMakeupOptionSelectedIndex(arrays);
//    }
//
//    public void notifyMakeUpViewState(MakeupItem stickerItem, int position, String groupName) {
//        RecyclerView.ViewHolder viewHolder = mMakeupOptionsRecycleView.findViewHolderForAdapterPosition(position);
//        //排除不必要变更
//        if (viewHolder == null || mMakeupOptionsRecycleView.getAdapter() != mMakeupAdapters.get(groupName))
//            return;
//        View itemView = viewHolder.itemView;
//        ImageView normalState = (ImageView) itemView.findViewById(R.id.normalState);
//        ImageView downloadingState = (ImageView) itemView.findViewById(R.id.downloadingState);
//        ViewGroup loadingStateParent = (ViewGroup) itemView.findViewById(R.id.loadingStateParent);
//        switch (stickerItem.state) {
//            case NORMAL_STATE:
//                //设置为等待下载状态
//                if (normalState.getVisibility() != View.VISIBLE) {
//                    Log.i("StickerAdapter", "NORMAL_STATE");
//                    normalState.setVisibility(View.VISIBLE);
//                    downloadingState.setVisibility((View.INVISIBLE));
//                    downloadingState.setActivated(false);
//                    loadingStateParent.setVisibility((View.INVISIBLE));
//                }
//                break;
//            case LOADING_STATE:
//                //设置为loading 状态
//                if (downloadingState.getVisibility() != View.VISIBLE) {
//                    Log.i("StickerAdapter", "LOADING_STATE");
//                    normalState.setVisibility(View.INVISIBLE);
//                    downloadingState.setActivated(true);
//                    downloadingState.setVisibility((View.VISIBLE));
//                    loadingStateParent.setVisibility((View.VISIBLE));
//                }
//                break;
//            case DONE_STATE:
//                //设置为下载完成状态
//                if (normalState.getVisibility() != View.INVISIBLE || downloadingState.getVisibility() != View.INVISIBLE) {
//                    Log.i("StickerAdapter", "DONE_STATE");
//                    normalState.setVisibility(View.INVISIBLE);
//                    downloadingState.setVisibility((View.INVISIBLE));
//                    downloadingState.setActivated(false);
//                    loadingStateParent.setVisibility((View.INVISIBLE));
//                }
//                break;
//        }
//    }
//}

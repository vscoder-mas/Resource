package sensetime.senseme.com.effects.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaActionSound;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

//import com.sensetime.sensearsourcemanager.SenseArMaterial;
//import com.sensetime.sensearsourcemanager.SenseArMaterialService;
//import com.sensetime.sensearsourcemanager.SenseArMaterialType;
import com.rongcloud.st.beauty.RCRTCBeautyEngineImpl;
import com.rongcloud.st.beauty.RCRTCBeautyOption;
import com.sensetime.stmobile.STEffectBeautyType;
import com.sensetime.stmobile.STMobileHumanActionNative;
import com.sensetime.stmobile.STSoundPlay;
import com.sensetime.stmobile.model.STMobileMakeupType;
import com.sensetime.stmobile.model.STPerformanceHintType;
import com.sensetime.stmobile.model.STPoint;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import sensetime.senseme.com.effects.R;
import sensetime.senseme.com.effects.adapter.BeautyItemAdapter;
import sensetime.senseme.com.effects.adapter.BeautyOptionsAdapter;
import sensetime.senseme.com.effects.adapter.FilterAdapter;
import sensetime.senseme.com.effects.adapter.FilterInfo;
import sensetime.senseme.com.effects.adapter.FullBeautyItemAdapter;
import sensetime.senseme.com.effects.adapter.MakeupAdapter;
import sensetime.senseme.com.effects.adapter.NativeStickerAdapter;
import sensetime.senseme.com.effects.adapter.ObjectAdapter;
import sensetime.senseme.com.effects.adapter.StickerAdapter;
import sensetime.senseme.com.effects.adapter.StickerOptionsAdapter;
import sensetime.senseme.com.effects.display.BaseCameraDisplay;
import sensetime.senseme.com.effects.display.CameraDisplaySingleBuffer;
import sensetime.senseme.com.effects.display.ChangePreviewSizeListener;
import sensetime.senseme.com.effects.encoder.MediaAudioEncoder;
import sensetime.senseme.com.effects.encoder.MediaEncoder;
import sensetime.senseme.com.effects.encoder.MediaMuxerWrapper;
import sensetime.senseme.com.effects.encoder.MediaVideoEncoder;
import sensetime.senseme.com.effects.utils.Accelerometer;
import sensetime.senseme.com.effects.utils.BaseHandler;
import sensetime.senseme.com.effects.utils.CheckAudioPermission;
import sensetime.senseme.com.effects.utils.CollectionSortUtils;
import sensetime.senseme.com.effects.utils.CommomDialog;
import sensetime.senseme.com.effects.utils.Constants;
import sensetime.senseme.com.effects.utils.EffectInfoDataHelper;
import sensetime.senseme.com.effects.utils.FileUtils;
import sensetime.senseme.com.effects.utils.ImageUtils;
import sensetime.senseme.com.effects.utils.LocalDataStore;
import sensetime.senseme.com.effects.utils.LogUtils;
import sensetime.senseme.com.effects.utils.MultiLanguageUtils;
import sensetime.senseme.com.effects.utils.NetworkUtils;
import sensetime.senseme.com.effects.STLicenseUtils;
import sensetime.senseme.com.effects.utils.STUtils;
import sensetime.senseme.com.effects.view.BeautyItem;
import sensetime.senseme.com.effects.view.BeautyOptionsItem;
import sensetime.senseme.com.effects.view.FilterItem;
import sensetime.senseme.com.effects.view.FullBeautyItem;
import sensetime.senseme.com.effects.view.IndicatorSeekBar;
import sensetime.senseme.com.effects.view.MakeUpOptionsItem;
import sensetime.senseme.com.effects.view.MakeupItem;
import sensetime.senseme.com.effects.view.ObjectItem;
import sensetime.senseme.com.effects.view.StickerItem;
import sensetime.senseme.com.effects.view.StickerOptionsItem;
import sensetime.senseme.com.effects.view.StickerState;
import sensetime.senseme.com.effects.view.VerticalSeekBar;

import static sensetime.senseme.com.effects.utils.Constants.APPID;
import static sensetime.senseme.com.effects.utils.Constants.APPKEY;
import static sensetime.senseme.com.effects.utils.Constants.GROUP_NEW;
import static sensetime.senseme.com.effects.utils.Constants.mNewBeautifyParamsTypeAdjust;
import static sensetime.senseme.com.effects.utils.Constants.mNewBeautifyParamsTypeBase;
import static sensetime.senseme.com.effects.utils.Constants.mNewBeautifyParamsTypeMicro;
import static sensetime.senseme.com.effects.utils.Constants.mNewBeautifyParamsTypeProfessional;

public class CameraActivity extends BaseActivity implements View.OnClickListener, SensorEventListener {
    private final static String TAG = "CameraActivity";

    //debug for test
    public static final boolean DEBUG = false;
    private Accelerometer mAccelerometer = null;

    //双输入优化
    private BaseCameraDisplay mCameraDisplay;

    private FrameLayout mPreviewFrameLayout;
    private ImageView mMeteringArea;
    private ImageView iv_close_sticker;

    private RecyclerView mStickerOptionsRecycleView, mFilterOptionsRecycleView;
    private RecyclerView mBeautyBaseRecycleView;
    private StickerOptionsAdapter mStickerOptionsAdapter;
    private BeautyOptionsAdapter mBeautyOptionsAdapter;
    private FullBeautyItemAdapter mBeautyFullAdapter;
    private ArrayList<StickerOptionsItem> mStickerOptionsList = new ArrayList<>();
    private ArrayList<BeautyOptionsItem> mBeautyOptionsList;

    private final HashMap<String, NativeStickerAdapter> mNativeStickerAdapters = new HashMap<>();
    private final HashMap<String, BeautyItemAdapter> mBeautyItemAdapters = new HashMap<>();
    private final HashMap<String, ArrayList<StickerItem>> mStickerlists = new HashMap<>();
    private ArrayList<StickerItem> mNewStickers;
    private final HashMap<String, ArrayList<BeautyItem>> mBeautylists = new HashMap<>();
    private ArrayList<FullBeautyItem> mBeautyFullItem = new ArrayList<>();
    private final HashMap<Integer, String> mBeautyOption = new HashMap<>();
    private final HashMap<Integer, Integer> mBeautyOptionSelectedIndex = new HashMap<>();

    private final HashMap<String, ArrayList<MakeupItem>> mMakeupLists = new HashMap<>();
    private final HashMap<String, Integer> mMakeupOptionIndex = new HashMap<>();
    private final HashMap<Integer, Integer> mMakeupOptionSelectedIndex = new HashMap<>();
    private final HashMap<Integer, Integer> mMakeupStrength = new HashMap<>();
    private final HashMap<Integer, Integer> mDefaultMakeupStrength = new HashMap<>();

    private final HashMap<String, FilterAdapter> mFilterAdapters = new HashMap<>();
    private final HashMap<String, ArrayList<FilterItem>> mFilterLists = new HashMap<>();

    private ObjectAdapter mObjectAdapter;
    private List<ObjectItem> mObjectList;

    private TextView mSavingTv;
    private TextView mAttributeText;

    private TextView mShowOriginBtn1, mShowOriginBtn2, mShowOriginBtn3;
    private TextView mShowShortVideoTime;
    private TextView mSmallPreviewSize, mLargePreviewSize, mLargestPreviewSize;

    private View gpFullBeautySeekBar;
    private LinearLayout mFilterGroupsLinearLayout;
    private RelativeLayout mFilterIconsRelativeLayout, mFilterStrengthLayout, mMakeupIconsRelativeLayout, mMakeupGroupRelativeLayout;
    private ImageView mFilterGroupBack, mMakeupGroupBack;
    private TextView mFilterGroupName, mFilterStrengthText;
    private TextView mMakeupGroupName;
    private SeekBar mFilterStrengthBar;
    private int mFilterStrength;
    private int mDefaultFilterStrength = 85;
    private VerticalSeekBar mVerticalSeekBar;
    private int mCurrentFilterGroupIndex = -1;
    private int mCurrentFilterIndex = -1;
    private int mCurrentMakeupGroupIndex = -1;
    private int mCurrentObjectIndex = -1;

    private float fullBeautyFilterScale = 0.85f;
    private float fullBeautyMakeupScale = 0.85f;

    private RelativeLayout mTipsLayout;
    private TextView mTipsTextView, mResetTextView, mResetZeroTextView;
    private ImageView mTipsImageView;
    private IndicatorSeekBar mIndicatorSeekbar, mIndicatorSeekbarNew;
    private Context mContext;
    private Handler mTipsHandler = new Handler();
    private Runnable mTipsRunnable;

    public static final int MSG_SAVING_IMG = 1;
    public static final int MSG_SAVED_IMG = 2;
    public static final int MSG_DRAW_OBJECT_IMAGE_AND_RECT = 3;
    public static final int MSG_DRAW_OBJECT_IMAGE = 4;
    public static final int MSG_CLEAR_OBJECT = 5;
    public static final int MSG_MISSED_OBJECT_TRACK = 6;
    public static final int MSG_DRAW_FACE_EXTRA_POINTS = 7;
    private static final int MSG_NEED_UPDATE_TIMER = 8;
    private static final int MSG_NEED_START_CAPTURE = 9;
    private static final int MSG_NEED_START_RECORDING = 10;
    private static final int MSG_STOP_RECORDING = 11;
    public static final int MSG_HIDE_VERTICALSEEKBAR = 12;

    public final static int MSG_UPDATE_HAND_ACTION_INFO = 100;
    public final static int MSG_RESET_HAND_ACTION_INFO = 101;
    public final static int MSG_UPDATE_BODY_ACTION_INFO = 102;
    public final static int MSG_UPDATE_FACE_EXPRESSION_INFO = 103;
    public final static int MSG_NEED_UPDATE_STICKER_TIPS = 104;
    //    public final static int MSG_NEED_UPDATE_STICKER_MAP = 105;
    public final static int MSG_NEED_REPLACE_STICKER_MAP = 106;
    public final static int MSG_NEED_UPDATE_BEAUTY_PARAMS = 108;
    public final static int MSG_STICKER_HAS_MAKEUP = 109;
    public final static int MSG_NEED_RECOVERUI = 110;

    private static final int PERMISSION_REQUEST_WRITE_PERMISSION = 1001;
    private boolean mPermissionDialogShowing = false;
    private Thread mCpuInofThread;
    private float mCurrentCpuRate = 0.0f;
    private boolean isFromUserSeekBar = true;

    private SurfaceView mSurfaceViewOverlap;
    private Bitmap mGuideBitmap;
    private Paint mPaint = new Paint();

    private int mIndexX = 0, mIndexY = 0;
    private boolean mCanMove = false;

    private LinearLayout mStickerOptionsSwitch;
    private RelativeLayout mStickerOptions;
    private RecyclerView mStickerIcons;
    private boolean mIsStickerOptionsOpen = false;

    private int mCurrentStickerOptionsIndex = -1;
    private int mCurrentStickerPosition = -1;
    private int mLastStickerPosition = -1;
    private int mCurrentBeautyIndex;

    private LinearLayout mBeautyOptionsSwitch, mBaseBeautyOptions;
    private boolean mIsBeautyOptionsOpen = false;
    private int mBeautyOptionsPosition = 0;
    private int mCurrentFullBeautyIndex = -1;
    private final ArrayList<SeekBar> mBeautyParamsSeekBarList = new ArrayList<>();

    private int lastIndex = 0;
    private ImageView mSettingOptionsSwitch;
    private ConstraintLayout mSettingOptions;
    private boolean mIsSettingOptionsOpen = false;
    private LinearLayout mFpsInfo;

    private ImageView mSelectionPicture;
    private Button mCaptureButton;

    private ImageView mBeautyOptionsSwitchIcon, mStickerOptionsSwitchIcon;
    private TextView mBeautyOptionsSwitchText, mStickerOptionsSwitchText;
    private RelativeLayout mFilterAndBeautyOptionView;
    private LinearLayout mSelectOptions;

    private int mTimeSeconds = 0;
    private int mTimeMinutes = 0;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private boolean mIsRecording = false;
    private String mVideoFilePath = null;
    private long mTouchDownTime = 0;
    private long mTouchCurrentTime = 0;
    private boolean mOnBtnTouch = false;
    private boolean mIsHasAudioPermission = false;

    private Map<Integer, Integer> mStickerPackageMap;

    private boolean testboolean = false;
    private boolean mNeedStopCpuRate = false;

    private SensorManager mSensorManager;
    private Sensor mRotation;
    long timeDown = 0;
    int downX, downY;
    private float oldDist = 1f;
    //记录用户最后一次点击的素材id ,包括还未下载的，方便下载完成后，直接应用素材
    private String preMaterialId = "";
//    private Bitmap mMeteringAreaBitmap;
//    private Matrix matrix = new Matrix();

    private MediaActionSound mMediaActionSound;

    private final Handler mHandler = new MyHandler(this);

    private static class MyHandler extends BaseHandler<CameraActivity> {

        MyHandler(CameraActivity cameraActivity) {
            super(cameraActivity);
        }

        @Override
        protected void handleMessage(final CameraActivity mActivity, Message msg) {
            switch (msg.what) {
                case MSG_SAVING_IMG:
                    ByteBuffer data = (ByteBuffer) msg.obj;
                    Bundle bundle = msg.getData();
                    int imageWidth = bundle.getInt("imageWidth");
                    int imageHeight = bundle.getInt("imageHeight");
                    mActivity.onPictureTaken(data, FileUtils.getOutputMediaFile(), imageWidth, imageHeight);

                    break;
                case MSG_SAVED_IMG:
                    mActivity.mSavingTv.setVisibility(View.VISIBLE);
                    mActivity.mSavingTv.setText(MultiLanguageUtils.getStr(R.string.toast_save_img));
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            mActivity.mSavingTv.setVisibility(View.GONE);
                        }
                    }, 1000);

                    break;
                case MSG_DRAW_OBJECT_IMAGE_AND_RECT:
                    Rect indexRect = (Rect) msg.obj;
                    mActivity.drawObjectImage(indexRect, true);
                    break;
                case MSG_DRAW_OBJECT_IMAGE:
                    Rect rect = (Rect) msg.obj;
                    mActivity.drawObjectImage(rect, false);
                    break;
                case MSG_CLEAR_OBJECT:
                    mActivity.clearObjectImage();
                    break;
                case MSG_MISSED_OBJECT_TRACK:
                    mActivity.mObjectAdapter.setSelectedPosition(1);
                    mActivity.mObjectAdapter.notifyDataSetChanged();
                    break;
                case MSG_DRAW_FACE_EXTRA_POINTS:
                    STPoint[] points = (STPoint[]) msg.obj;
                    mActivity.drawFaceExtraPoints(points);
                    break;
                case MSG_NEED_UPDATE_TIMER:
                    mActivity.updateTimer();
                    break;
                case MSG_NEED_START_RECORDING:
                    //开始录制
                    mActivity.startRecording();
                    mActivity.closeTableView();
                    mActivity.disableShowLayouts();
                    mActivity.mShowShortVideoTime.setVisibility(View.VISIBLE);

                    mActivity.mTimer = new Timer();
                    mActivity.mTimerTask = new TimerTask() {
                        @Override
                        public void run() {
                            Message msg = mActivity.mHandler.obtainMessage(MSG_NEED_UPDATE_TIMER);
                            mActivity.mHandler.sendMessage(msg);
                        }
                    };

                    mActivity.mTimer.schedule(mActivity.mTimerTask, 1000, 1000);
                    break;

                case MSG_STOP_RECORDING:
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            //结束录制
                            if (mActivity.mIsRecording) {
                                return;
                            }
                            mActivity.stopRecording();
                            mActivity.enableShowLayouts();
                            mActivity.mShowShortVideoTime.setVisibility(View.INVISIBLE);

                            if (mActivity.mTimeMinutes == 0 && mActivity.mTimeSeconds < 2) {
                                if (mActivity.mVideoFilePath != null) {
                                    File file = new File(mActivity.mVideoFilePath);
                                    if (file != null) {
                                        file.delete();
                                    }
                                    mActivity.mVideoFilePath = null;
                                }
                                mActivity.mSavingTv.setText(MultiLanguageUtils.getStr(R.string.toast_short_video));
                            } else {
                                if (mActivity.mVideoFilePath != null) {
                                    File file = new File(mActivity.mVideoFilePath);
                                    if (file != null) {
                                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                        Uri contentUri = Uri.fromFile(file);
                                        mediaScanIntent.setData(contentUri);
                                        mActivity.mContext.sendBroadcast(mediaScanIntent);

                                        //通知相册更新数据
                                        if (Build.VERSION.SDK_INT >= 19) {
                                            MediaScannerConnection.scanFile(mActivity.mContext, new String[]{mActivity.mVideoFilePath}, null, null);
                                        }
                                    }
                                }

                                mActivity.mSavingTv.setText(MultiLanguageUtils.getStr(R.string.toast_save_video));
                            }
                            mActivity.notifyVideoUpdate(mActivity.mVideoFilePath);
                            mActivity.resetTimer();
                        }
                    }, 100);

                    mActivity.mSavingTv.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            mActivity.mSavingTv.setVisibility(View.GONE);
                        }
                    }, 1000);

                    break;

                case MSG_UPDATE_HAND_ACTION_INFO:
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mActivity.mCameraDisplay != null) {
                                mActivity.showHandActionInfo(mActivity.mCameraDisplay.getHandActionInfo());
                            }
                        }
                    });
                    break;
                case MSG_RESET_HAND_ACTION_INFO:
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mActivity.mCameraDisplay != null) {
                                mActivity.resetHandActionInfo();
                            }
                        }
                    });
                    break;

                case MSG_UPDATE_BODY_ACTION_INFO:
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mActivity.mCameraDisplay != null) {
                                mActivity.showBodyActionInfo(mActivity.mCameraDisplay.getBodyActionInfo());
                            }
                        }
                    });
                    break;

                case MSG_UPDATE_FACE_EXPRESSION_INFO:
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mActivity.mCameraDisplay != null) {
                                mActivity.showFaceExpressionInfo(mActivity.mCameraDisplay.getFaceExpressionInfo());
                            }
                        }
                    });
                    break;
                case MSG_NEED_UPDATE_STICKER_TIPS:
                    long action = mActivity.mCameraDisplay.getStickerTriggerAction();
                    mActivity.showActiveTips(action);
                    break;
                case MSG_NEED_REPLACE_STICKER_MAP:
                    int oldPackageId = msg.arg1;
                    int newPackageId = msg.arg2;
                    for (Integer index : mActivity.mStickerPackageMap.keySet()) {
                        int stickerId = mActivity.mStickerPackageMap.get(index);//得到每个key多对用value的值

                        if (stickerId == oldPackageId) {
                            mActivity.mStickerPackageMap.put(index, newPackageId);
                        }
                    }
                    break;
                case MSG_HIDE_VERTICALSEEKBAR:
                    mActivity.performVerticalSeekBarVisiable(false);
                    break;

                case MSG_NEED_UPDATE_BEAUTY_PARAMS:
                    mActivity.updateBeautyParamsFromPackage();
                    break;
                case MSG_STICKER_HAS_MAKEUP:
                    Bundle makeupBundle = msg.getData();
                    int makeupType = makeupBundle.getInt("makeupType", -1);
                    if (makeupType!=-1) {
                        mActivity.mMakeupOptionSelectedIndex.put(makeupType, 0);
                        mActivity.setMakeUpViewSelected(false, makeupType);
                    }
                    break;
                case MSG_NEED_RECOVERUI:
                    mActivity.recoverUI();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EffectInfoDataHelper.setType(EffectInfoDataHelper.Type.CAMERA);

        //进程后台时被系统强制kill，需重新checkLicense
        if (savedInstanceState != null && savedInstanceState.getBoolean("process_killed")) {
            if (!STLicenseUtils.checkLicense(this)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "请检查License授权！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Constants.ACTIVITY_MODE_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(R.layout.activity_main);
        mContext = this;
        initView();
//        initStickerListFromNet();
        initEvents();

        if (DEBUG) {
            findViewById(R.id.rl_test_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_face_expression).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_hand_action_info).setVisibility(View.VISIBLE);

            (findViewById(R.id.testSwitch0)).setVisibility(View.VISIBLE);
            (findViewById(R.id.testSwitch1)).setVisibility(View.VISIBLE);
            (findViewById(R.id.testSwitch2)).setVisibility(View.VISIBLE);

            LogUtils.setIsLoggable(true);
        }

        resetFilterView(true);
        mShowOriginBtn1.setVisibility(View.VISIBLE);
        mShowOriginBtn2.setVisibility(View.INVISIBLE);
        mShowOriginBtn3.setVisibility(View.INVISIBLE);

        SharedPreferences sharedPreferences = getSharedPreferences("senseme", Activity.MODE_PRIVATE);
        boolean isFirstLoad = sharedPreferences.getBoolean("isFirstLoad", true);

        if (isFirstLoad && !Constants.ACTIVITY_MODE_LANDSCAPE) {
            SharedPreferences mySharedPreferences = getSharedPreferences("senseme", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            editor.putBoolean("isFirstLoad", false);
            editor.commit();

            new CommomDialog(this, R.style.dialog, MultiLanguageUtils.getStr(R.string.dlg_des), new CommomDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean confirm) {
                    if (confirm) {
                        dialog.dismiss();
                    }
                }
            }).show();
        }

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        //todo 判断是否存在rotation vector sensor
        mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        mMediaActionSound = new MediaActionSound();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initDefaultFullBeautySeekBar();
            }
        }, 300);
        mDefaultMakeupStrength.putAll(mMakeupStrength);
        isFromUserSeekBar = false;
        recoverUI();
    }

    private void setMakeupOptionSelectedIndex(String[] arrays) {
        for (int i = 0; i < arrays.length; i++) {
            String arr = arrays[i];
            if (!isEmpty(arr)) {
                for (Map.Entry<String, MakeupAdapter> entry : mMakeupAdapters.entrySet()) {
                    String key = entry.getKey();
                    MakeupAdapter adapter = mMakeupAdapters.get(key);
                    List<MakeupItem> lists = adapter.getData();
                    for (int j = 0; j < lists.size(); j++) {
                        MakeupItem item = lists.get(j);
                        if (item.path != null && item.path.equals(arr)) {
                            mMakeupOptionSelectedIndex.put(i, j);
                            break;
                        }
                    }
                }
            }
        }
    }

    public boolean isEmpty(String str) {
        if (str != null) str = str.trim();
        return TextUtils.isEmpty(str) || "null".equalsIgnoreCase(str);
    }

    private int getFullBeautyIndex(String name) {
        ArrayList<FullBeautyItem> lists = mBeautyFullAdapter.getData();
        for (int i = 0; i < lists.size(); i++) {
            FullBeautyItem item = lists.get(i);
            if (item.getText().equals(name)) return i;
        }
        return -1;
    }

    private void recoverUI() {
        mCurrentFullBeautyIndex = getFullBeautyIndex(EffectInfoDataHelper.getInstance().getFullBeautyName());
        if (mCurrentFullBeautyIndex != -1) {
            isFromUserSeekBar = false;
            String tempFullBeautyName = String.copyValueOf(EffectInfoDataHelper.getInstance().fullBeautyName.toCharArray());
            setFullBeauty(mBeautyFullItem.get(mCurrentFullBeautyIndex));
            fullBeautyFilterScale = EffectInfoDataHelper.getInstance().getFullBeautyFilterProgress() / 100f;
            fullBeautyMakeupScale = EffectInfoDataHelper.getInstance().getFullBeautyMakeupProgress() / 100f;
            setFullBeautyMakeup(EffectInfoDataHelper.getInstance().getFullBeautyMakeupProgress());
            setFullBeautyFilter(EffectInfoDataHelper.getInstance().getFullBeautyFilterProgress());
            EffectInfoDataHelper.getInstance().fullBeautyName = tempFullBeautyName;
        } else {
            isFromUserSeekBar = true;
            gpFullBeautySeekBar.setVisibility(View.GONE);
            mIndicatorSeekbar.setVisibility(View.INVISIBLE);
            // 恢复滤镜
            String filterType = EffectInfoDataHelper.getInstance().getFilterType();
            String filterName = EffectInfoDataHelper.getInstance().getFilterName();
            int filterStr = (int) (EffectInfoDataHelper.getInstance().getFilterStrength() * 100f);
            if (!TextUtils.isEmpty(filterType)) {
                isFromUserSeekBar = true;
                if (filterType.equals("filter_portrait")) {
                    mCurrentFilterGroupIndex = 0;
                } else if (filterType.equals("filter_scenery")) {
                    mCurrentFilterGroupIndex = 1;
                } else if (filterType.equals("filter_still_life")) {
                    mCurrentFilterGroupIndex = 2;
                } else if (filterType.equals("filter_food")) {
                    mCurrentFilterGroupIndex = 3;
                }

                if (mFilterLists.get(filterType).size() > 0) {
                    for (int i = 0; i < mFilterLists.get(filterType).size(); i++) {
                        if (mFilterLists.get(filterType).get(i).name.equals(filterName)) {
                            mCurrentFilterIndex = i;
                        }
                    }
                }
                if (mCurrentFilterIndex > 0) {
                    mFilterAdapters.get(filterType).setSelectedPosition(mCurrentFilterIndex);
                    mCameraDisplay.enableFilter(true);
                    mFilterStrength = filterStr;
                    mFilterStrengthBar.setProgress(mFilterStrength);
                    mFilterStrengthText.setText(String.valueOf(mFilterStrength));
                    setFilterViewSelected(mCurrentFilterGroupIndex);
                }
            }

            // 恢复美妆UI
            int delayMillins = 1500;
            if (mCurrentStickerPosition!=-1) {
                delayMillins = 0;
            }
            String[] arrays = EffectInfoDataHelper.getInstance().getCurrentMakeUp();
            if (isEmpty(arrays[Constants.ST_MAKEUP_ALL])) {
                mCameraDisplay.removeMakeupByType(Constants.ST_MAKEUP_ALL);
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    float[] strength = EffectInfoDataHelper.getInstance().getCurrentMakeUpStrength(null);
                    String[] arrays = EffectInfoDataHelper.getInstance().getCurrentMakeUp();
                    setMakeupOptionSelectedIndex(arrays);
                    for (int i = 0; i < arrays.length; i++) {
                        String arr = arrays[i];
                        if (arr != null&&!TextUtils.isEmpty(arr) && !arr.equals("null")) {
                            if (strength[i] > 0) mMakeupStrength.put(i, (int) (strength[i] * 100f));
                            View[] view = makeupViews[i];
                            for (View v : view) v.setSelected(true);
                        }
                    }
                }
            }, delayMillins);
        }
        // 恢复基础美颜参数、美形、微整形
        float[] beautifyParamsTypeBase = EffectInfoDataHelper.getInstance().getBaseParams();
        mCameraDisplay.mBeautifyParamsTypeBase= beautifyParamsTypeBase;
        for (int i = 0; i < mBeautylists.get("baseBeauty").size(); i++) {
            ((BeautyItem) mBeautylists.get("baseBeauty").get(i)).setProgress((int) (Math.round(beautifyParamsTypeBase[i] * 100)));
        }
        mBeautyItemAdapters.get("baseBeauty").notifyDataSetChanged();
        // 美形
        float[] beautifyParamsTypeProfessional = EffectInfoDataHelper.getInstance().getProfessionalParams();
        mCameraDisplay.mBeautifyParamsTypeProfessional= beautifyParamsTypeProfessional;
        for (int i = 0; i < mBeautylists.get("professionalBeauty").size(); i++) {
            ((BeautyItem) mBeautylists.get("professionalBeauty").get(i)).setProgress((int) (Math.round(beautifyParamsTypeProfessional[i] * 100)));
        }
        mBeautyItemAdapters.get("professionalBeauty").notifyDataSetChanged();
        // 微整形
        float[] beautifyParamsTypeMicro = EffectInfoDataHelper.getInstance().getMicroParams();
        mCameraDisplay.mBeautifyParamsTypeMicro= beautifyParamsTypeMicro;
        for (int i = 0; i < mBeautylists.get("microBeauty").size(); i++) {
            ((BeautyItem) mBeautylists.get("microBeauty").get(i)).setProgress((int) (Math.round(beautifyParamsTypeMicro[i] * 100)));
        }
        mBeautyItemAdapters.get("microBeauty").notifyDataSetChanged();
        // 调整
        float[] beautifyParamsTypeAdjust = EffectInfoDataHelper.getInstance().getAdjustParams();
        mCameraDisplay.mBeautifyParamsTypeAdjust= beautifyParamsTypeAdjust;
        for (int i = 0; i < mBeautylists.get("adjustBeauty").size(); i++) {
            ((BeautyItem) mBeautylists.get("adjustBeauty").get(i)).setProgress((int) (Math.round(beautifyParamsTypeAdjust[i] * 100)));
        }
        mBeautyItemAdapters.get("adjustBeauty").notifyDataSetChanged();

        // 刷新seekBar
        refreshSeekBar();
    }

    private void refreshSeekBar() {
        ArrayList<BeautyItem> beautyItems = mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition));
        if (null == beautyItems) return;
        Integer integer = mBeautyOptionSelectedIndex.get(mBeautyOptionsPosition);
        int progress = beautyItems.get(integer).getProgress();
        if (checkMicroType(mBeautyOptionsPosition))
            progress = STUtils.convertToData(progress);
        mIndicatorSeekbar.setProgress(progress);
        String str = mBeautyOption.get(mBeautyOptionsPosition);
        mBeautyItemAdapters.get(str).notifyDataSetChanged();
    }

    private void initDefaultFullBeautySeekBar() {
        mBeautyFullItem.get(0).setFilterProgress(EffectInfoDataHelper.getInstance().getFullBeautyFilterProgress());
        mBeautyFullItem.get(0).setMakeupAllProgress(EffectInfoDataHelper.getInstance().getFullBeautyMakeupProgress());
        mIndicatorSeekbar.setProgress(EffectInfoDataHelper.getInstance().getFullBeautyFilterProgress());
        mIndicatorSeekbarNew.setProgress(EffectInfoDataHelper.getInstance().getFullBeautyMakeupProgress());
        mCameraDisplay.setFilterStrength(EffectInfoDataHelper.getInstance().getFullBeautyFilterProgress() / 100f);
    }

    private View[][] makeupViews, filterViews;

    public void setMakeUpViewSelected(boolean selected, int... selectedIndexes) {
        if (selectedIndexes.length == 1 && selectedIndexes[0] == -1) {
            for (View[] views : makeupViews)
                for (View v : views) v.setSelected(selected);
        } else {
            for (int index : selectedIndexes) {
                View[] view = makeupViews[index];
                for (View v : view) {
                    v.setSelected(selected);
                }
            }
        }
    }

    private void setFilterViewSelected(int index) {
        if (index == -1) {
            for (View[] view : filterViews)
                for (View v : view) v.setSelected(false);
        } else {
            for (View view : filterViews[index]) view.setSelected(true);
        }
    }

    private int[] makeupBackIds = {
            R.drawable.makeup_lip_selected,// 0.default
            R.drawable.makeup_eye_selected,// 1.眼部美妆
            R.drawable.makeup_cheeks_selected,// 2.腮部
            R.drawable.makeup_lip_selected,// 3.口红
            R.drawable.makeup_face_selected,// 4.修容
            R.drawable.makeup_brow_selected,// 5.眉毛
            R.drawable.makeup_eyeliner_selected,// 6.
            R.drawable.makeup_eyelash_selected,// 7.eye_lash
            R.drawable.makeup_eyeball_selected,// 8.eyeball
            R.drawable.makeup_hairdye_selected,// 9.染发
            R.drawable.makeup_all_selected,// 10.整妆
            R.drawable.makeup_all_selected//
    };

    private void initView() {
        FileUtils.copyModelsFiles(this, "models");
        mMakeupOptionsRecycleView = (RecyclerView) findViewById(R.id.rv_makeup_icons);
        iv_close_sticker = (ImageView) findViewById(R.id.iv_close_sticker);
        ImageView iv_makeup_group_all = (ImageView) findViewById(R.id.iv_makeup_group_all);//1整妆
        TextView tv_makeup_group_all = (TextView) findViewById(R.id.tv_makeup_group_all);
        ImageView iv_makeup_group_hairdye = (ImageView) findViewById(R.id.iv_makeup_group_hairdye);//染发
        TextView tv_makeup_group_hairdye = (TextView) findViewById(R.id.tv_makeup_group_hairdye);
        ImageView iv_makeup_group_lip = (ImageView) findViewById(R.id.iv_makeup_group_lip);// 3口红
        TextView tv_makeup_group_lip = (TextView) findViewById(R.id.tv_makeup_group_lip);
        ImageView iv_makeup_group_cheeks = (ImageView) findViewById(R.id.iv_makeup_group_cheeks);//4腮红
        TextView tv_makeup_group_cheeks = (TextView) findViewById(R.id.tv_makeup_group_cheeks);
        ImageView iv_makeup_group_face = (ImageView) findViewById(R.id.iv_makeup_group_face);//5.修容
        TextView tv_makeup_group_face = (TextView) findViewById(R.id.tv_makeup_group_face);
        ImageView iv_makeup_group_brow = (ImageView) findViewById(R.id.iv_makeup_group_brow);// 6.眉毛
        TextView tv_makeup_group_brow = (TextView) findViewById(R.id.tv_makeup_group_brow);
        ImageView iv_makeup_group_eye = (ImageView) findViewById(R.id.iv_makeup_group_eye);//7.眼影
        TextView tv_makeup_group_eye = (TextView) findViewById(R.id.tv_makeup_group_eye);
        ImageView iv_makeup_group_eyeliner = (ImageView) findViewById(R.id.iv_makeup_group_eyeliner);//8.眼线
        TextView tv_makeup_group_eyeliner = (TextView) findViewById(R.id.tv_makeup_group_eyeliner);
        ImageView iv_makeup_group_eyelash = (ImageView) findViewById(R.id.iv_makeup_group_eyelash);//9.眼睫毛
        TextView tv_makeup_group_eyelash = (TextView) findViewById(R.id.tv_makeup_group_eyelash);
        ImageView iv_makeup_group_eyeball = (ImageView) findViewById(R.id.iv_makeup_group_eyeball);
        TextView tv_makeup_group_eyeball = (TextView) findViewById(R.id.tv_makeup_group_eyeball);
        ImageView iv_filter_group_portrait = (ImageView) findViewById(R.id.iv_filter_group_portrait);
        // 滤镜
        TextView tv_filter_group_portrait = (TextView) findViewById(R.id.tv_filter_group_portrait);
        ImageView iv_filter_group_scenery = (ImageView) findViewById(R.id.iv_filter_group_scenery);
        TextView tv_filter_group_scenery = (TextView) findViewById(R.id.tv_filter_group_scenery);
        ImageView iv_filter_group_still_life = (ImageView) findViewById(R.id.iv_filter_group_still_life);
        TextView tv_filter_group_still_life = (TextView) findViewById(R.id.tv_filter_group_still_life);
        ImageView iv_filter_group_food = (ImageView) findViewById(R.id.iv_filter_group_food);
        TextView tv_filter_group_food = (TextView) findViewById(R.id.tv_filter_group_food);
        Switch sw_language = (Switch) findViewById(R.id.sw_language);
        Switch mPerformanceSwitch = (Switch) findViewById(R.id.perfomance_switch);
        gpFullBeautySeekBar = findViewById(R.id.gp_full_beauty_seek_bar);

        mResetTextView = (TextView) findViewById(R.id.reset);
        mResetZeroTextView = (TextView) findViewById(R.id.reset_zero);

        LinearLayout mMakeupGroupTypeAll = (LinearLayout) findViewById(R.id.ll_makeup_group_all);

        mResetTextView.setOnClickListener(this);
        (findViewById(R.id.testSwitch0)).setOnClickListener(this);
        (findViewById(R.id.testSwitch1)).setOnClickListener(this);
        (findViewById(R.id.testSwitch2)).setOnClickListener(this);
        mMakeupGroupTypeAll.setOnClickListener(this);
        mResetZeroTextView.setOnClickListener(this);

        makeupViews = new View[][]{// 和Constants STMobileMakeupType位置对应
                {iv_makeup_group_hairdye, tv_makeup_group_hairdye},// 0.default
                {iv_makeup_group_eye, tv_makeup_group_eye},//1.眼部美妆
                {iv_makeup_group_cheeks, tv_makeup_group_cheeks},// //2.腮部
                {iv_makeup_group_lip, tv_makeup_group_lip},//3.口红
                {iv_makeup_group_face, tv_makeup_group_face},// 4.修容
                {iv_makeup_group_brow, tv_makeup_group_brow},// 5.眉毛
                {iv_makeup_group_eyeliner, tv_makeup_group_eyeliner},// 6.EYELINER
                {iv_makeup_group_eyelash, tv_makeup_group_eyelash},// 7.eye_lash
                {iv_makeup_group_eyeball, tv_makeup_group_eyeball},// 8.eyeball
                {iv_makeup_group_hairdye, tv_makeup_group_hairdye}, // 9.染发
                {iv_makeup_group_all, tv_makeup_group_all},// 10.整妆
                {iv_makeup_group_all, tv_makeup_group_all}
        };

        filterViews = new View[][]{
                {iv_filter_group_portrait, tv_filter_group_portrait},
                {iv_filter_group_scenery, tv_filter_group_scenery},
                {iv_filter_group_still_life, tv_filter_group_still_life},
                {iv_filter_group_food, tv_filter_group_food},
        };

        String language = MultiLanguageUtils.getSPSelectedLocale().getLanguage();
        sw_language.setChecked(language.contains("en"));
        sw_language.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    MultiLanguageUtils.setEnglish();
                else
                    MultiLanguageUtils.setChinese();
                MultiLanguageUtils.restart(CameraActivity.this);
            }
        });
        mBeautyOption.put(0, "fullBeauty");
        //copy model file to sdcard
        FileUtils.copyModelFiles(this);

        mPerformanceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCameraDisplay.setPerformanceHint(STPerformanceHintType.ST_PREFER_PERFORMANCE);
                } else {
                    mCameraDisplay.setPerformanceHint(STPerformanceHintType.ST_PREFER_EFFECT);
                }
            }
        });

        mAccelerometer = new Accelerometer(getApplicationContext());
        GLSurfaceView glSurfaceView = (GLSurfaceView) findViewById(R.id.id_gl_sv);
        mSurfaceViewOverlap = (SurfaceView) findViewById(R.id.surfaceViewOverlap);
        mPreviewFrameLayout = (FrameLayout) findViewById(R.id.id_preview_layout);
        mMeteringArea = new ImageView(this);
        mMeteringArea.setImageResource(R.drawable.choose);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(80, 80);
        mMeteringArea.setLayoutParams(layoutParams);
        mPreviewFrameLayout.addView(mMeteringArea);
        mMeteringArea.setVisibility(View.INVISIBLE);

        //默认使用单输入buffer多线程
        mCameraDisplay = new CameraDisplaySingleBuffer(getApplicationContext(), mChangePreviewSizeListener, glSurfaceView);

        mCameraDisplay.setHandler(mHandler);

        mIndicatorSeekbar = (IndicatorSeekBar) findViewById(R.id.beauty_item_seekbar);
        mIndicatorSeekbarNew = (IndicatorSeekBar) findViewById(R.id.beauty_item_seekbar_new);
        mIndicatorSeekbarNew.setOnSeekBarChangeListener(new IndicatorSeekBar.Listener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                super.onProgressChanged(seekBar, progress, fromUser);
                if (fromUser) {
                    if (mBeautyOptionsPosition == 0) {
                        mIndicatorSeekbarNew.updateTextView(progress);
                        if (mCurrentFullBeautyIndex != -1)
                            mBeautyFullItem.get(mCurrentFullBeautyIndex).setMakeupAllProgress(progress);
                        if (fromUser) {
                            setFullBeautyMakeup(progress);
                        }
                    }
                }
            }
        });
        mIndicatorSeekbar.setOnSeekBarChangeListener(new IndicatorSeekBar.Listener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                super.onStopTrackingTouch(seekBar);
                EffectInfoDataHelper.getInstance().save();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (checkMicroType(mBeautyOptionsPosition)) {
                        mIndicatorSeekbar.updateTextView(STUtils.convertToDisplay(progress));
                        mCameraDisplay.setBeautyParam(mCurrentBeautyIndex, (float) STUtils.convertToDisplay(progress) / 100f);
                        mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(mBeautyOptionSelectedIndex.get(mBeautyOptionsPosition)).setProgress(STUtils.convertToDisplay(progress));
                    } else {
                        if (mBeautyOptionsPosition == 0) {
                            setFullBeautyFilter(progress);
                        } else {
                            mIndicatorSeekbar.updateTextView(progress);
                            mCameraDisplay.setBeautyParam(mCurrentBeautyIndex, (float) progress / 100f);
                            mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(mBeautyOptionSelectedIndex.get(mBeautyOptionsPosition)).setProgress(progress);

                            //美白1和2，磨皮1和2互斥逻辑
                            if (mBeautyOptionsPosition == 1) {
                                int[][] mutexArray = {{0, 1, 2}, {4, 5}};
                                int subItemSelectedIndex = mBeautyOptionSelectedIndex.get(mBeautyOptionsPosition);
                                for (int[] mutex : mutexArray) {
                                    setMutex(subItemSelectedIndex, mutex);
                                }
                            }

                        }
                    }
                    if (mBeautyOptionsPosition != 0) {
                        mBeautyItemAdapters.get(mBeautyOption.get(mBeautyOptionsPosition)).notifyItemChanged(mBeautyOptionSelectedIndex.get(mBeautyOptionsPosition));
                        if (!checkNeedBeauty()) {
                            mCameraDisplay.enableBeautify(false);
                        } else {
                            mCameraDisplay.enableBeautify(true);
                        }
                    }
                }
            }
        });

        mBeautyBaseRecycleView = (RecyclerView) findViewById(R.id.rv_beauty_base);
        LinearLayoutManager ms = new LinearLayoutManager(this);
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);
        mBeautyBaseRecycleView.setLayoutManager(ms);
        mBeautyBaseRecycleView.addItemDecoration(new BeautyItemDecoration(STUtils.dip2px(this, 15)));

        ArrayList mBeautyBaseItem = LocalDataStore.getInstance().getBeautyBaseList();
        float[] beautifyParamsTypeBase = EffectInfoDataHelper.getInstance().getBaseParams();
        for (int i = 0; i < mBeautyBaseItem.size(); i++) {
            ((BeautyItem) mBeautyBaseItem.get(i)).setProgress((int) (beautifyParamsTypeBase[i] * 100));
        }

        mIndicatorSeekbar.getSeekBar().setProgress((int) (beautifyParamsTypeBase[0] * 100));
        mIndicatorSeekbar.updateTextView((int) (beautifyParamsTypeBase[0] * 100));

        mBeautylists.put("baseBeauty", mBeautyBaseItem);
        BeautyItemAdapter mBeautyBaseAdapter = new BeautyItemAdapter(this, mBeautyBaseItem);
        mBeautyItemAdapters.put("baseBeauty", mBeautyBaseAdapter);
        mBeautyOption.put(1, "baseBeauty");
        mBeautyBaseRecycleView.setAdapter(mBeautyBaseAdapter);

        ArrayList mProfessionalBeautyItem = LocalDataStore.getInstance().getProfessionalBeautyList();
        //float[] beautifyParamsTypeProfessional = SpUtils.getFloatArray(SP_PROFESSIONAL_PARAMS, mNewBeautifyParamsTypeProfessional);
        float[] beautifyParamsTypeProfessional = EffectInfoDataHelper.getInstance().getProfessionalParams();

        for (int i = 0; i < mProfessionalBeautyItem.size(); i++) {
            ((BeautyItem) mProfessionalBeautyItem.get(i)).setProgress((int) (beautifyParamsTypeProfessional[i] * 100));
        }

        mBeautylists.put("professionalBeauty", mProfessionalBeautyItem);
        BeautyItemAdapter mBeautyProfessionalAdapter = new BeautyItemAdapter(this, mProfessionalBeautyItem);
        mBeautyItemAdapters.put("professionalBeauty", mBeautyProfessionalAdapter);
        mBeautyOption.put(2, "professionalBeauty");

        ArrayList mMicroBeautyItem = LocalDataStore.getInstance().getMicroBeautyList();
        float[] beautifyParamsTypeMicro = EffectInfoDataHelper.getInstance().getMicroParams();

        for (int i = 0; i < mMicroBeautyItem.size(); i++) {
            ((BeautyItem) mMicroBeautyItem.get(i)).setProgress((int) (beautifyParamsTypeMicro[i] * 100));
        }

        mBeautylists.put("microBeauty", mMicroBeautyItem);
        BeautyItemAdapter mMicroAdapter = new BeautyItemAdapter(this, mMicroBeautyItem);
        mBeautyItemAdapters.put("microBeauty", mMicroAdapter);
        mBeautyOption.put(3, "microBeauty");

        mMakeupOptionsRecycleView = (RecyclerView) findViewById(R.id.rv_makeup_icons);
        mMakeupOptionsRecycleView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        mMakeupOptionsRecycleView.addItemDecoration(new SpaceItemDecoration(0));

        mMakeupLists.putAll(LocalDataStore.getInstance().getMakeupLists());

        mMakeupAdapters.put("makeup_lip", new MakeupAdapter(mMakeupLists.get("makeup_lip"), this));
        mMakeupAdapters.put("makeup_highlight", new MakeupAdapter(mMakeupLists.get("makeup_highlight"), this));
        mMakeupAdapters.put("makeup_blush", new MakeupAdapter(mMakeupLists.get("makeup_blush"), this));
        mMakeupAdapters.put("makeup_brow", new MakeupAdapter(mMakeupLists.get("makeup_brow"), this));
        mMakeupAdapters.put("makeup_eye", new MakeupAdapter(mMakeupLists.get("makeup_eye"), this));
        mMakeupAdapters.put("makeup_eyeliner", new MakeupAdapter(mMakeupLists.get("makeup_eyeliner"), this));
        mMakeupAdapters.put("makeup_eyelash", new MakeupAdapter(mMakeupLists.get("makeup_eyelash"), this));
        mMakeupAdapters.put("makeup_eyeball", new MakeupAdapter(mMakeupLists.get("makeup_eyeball"), this));
        mMakeupAdapters.put("makeup_hairdye", new MakeupAdapter(mMakeupLists.get("makeup_hairdye"), this));
        mMakeupAdapters.put("makeup_all", new MakeupAdapter(mMakeupLists.get("makeup_all"), this));
//        for (MakeUpOptionsItem item : MAKE_UP_OPTIONS_LIST) {
//            initMakeUpListener(item.groupName, null);
//        }
        mMakeupOptionIndex.put("makeup_lip", 3);
        mMakeupOptionIndex.put("makeup_highlight", 4);
        mMakeupOptionIndex.put("makeup_blush", 2);
        mMakeupOptionIndex.put("makeup_brow", 5);
        mMakeupOptionIndex.put("makeup_eye", 1);
        mMakeupOptionIndex.put("makeup_eyeliner", 6);
        mMakeupOptionIndex.put("makeup_eyelash", 7);
        mMakeupOptionIndex.put("makeup_eyeball", 8);
        mMakeupOptionIndex.put("makeup_hairdye", 9);
        mMakeupOptionIndex.put("makeup_all", 10);


        for (int i = 0; i < Constants.MAKEUP_TYPE_COUNT; i++) {
            mMakeupOptionSelectedIndex.put(i, 0);
            mMakeupStrength.put(i, 80);
        }

        mMakeupIconsRelativeLayout = (RelativeLayout) findViewById(R.id.rl_makeup_icons);
        mMakeupGroupRelativeLayout = ((RelativeLayout) findViewById(R.id.rl_makeup_groups));
        LinearLayout mMakeupGroupLip = (LinearLayout) findViewById(R.id.ll_makeup_group_lip);
        mMakeupGroupLip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickMakeupGroup("makeup_lip", Constants.ST_MAKEUP_LIP);
            }
        });

        LinearLayout mMakeupGroupCheeks = (LinearLayout) findViewById(R.id.ll_makeup_group_cheeks);
        mMakeupGroupCheeks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickMakeupGroup("makeup_blush", Constants.ST_MAKEUP_BLUSH);
            }
        });

        LinearLayout mMakeupGroupFace = (LinearLayout) findViewById(R.id.ll_makeup_group_face);
        mMakeupGroupFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickMakeupGroup("makeup_highlight", Constants.ST_MAKEUP_HIGHLIGHT);
            }
        });

        LinearLayout mMakeupGroupBrow = (LinearLayout) findViewById(R.id.ll_makeup_group_brow);
        mMakeupGroupBrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickMakeupGroup("makeup_brow", Constants.ST_MAKEUP_BROW);
            }
        });

        LinearLayout mMakeupGroupEye = (LinearLayout) findViewById(R.id.ll_makeup_group_eye);
        mMakeupGroupEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickMakeupGroup("makeup_eye", Constants.ST_MAKEUP_EYE);
            }
        });

        LinearLayout mMakeupGroupEyeLiner = (LinearLayout) findViewById(R.id.ll_makeup_group_eyeliner);
        mMakeupGroupEyeLiner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickMakeupGroup("makeup_eyeliner", Constants.ST_MAKEUP_EYELINER);
            }
        });

        LinearLayout mMakeupGroupEyeLash = (LinearLayout) findViewById(R.id.ll_makeup_group_eyelash);
        mMakeupGroupEyeLash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickMakeupGroup("makeup_eyelash", Constants.ST_MAKEUP_EYELASH);
            }
        });

        LinearLayout mMakeupGroupEyeBall = (LinearLayout) findViewById(R.id.ll_makeup_group_eyeball);
        mMakeupGroupEyeBall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickMakeupGroup("makeup_eyeball", Constants.ST_MAKEUP_EYEBALL);

            }
        });

        LinearLayout mMakeupGroupHairDye = (LinearLayout) findViewById(R.id.ll_makeup_group_hairdye);
        mMakeupGroupHairDye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickMakeupGroup("makeup_hairdye", Constants.ST_MAKEUP_HAIR_DYE);
                mMakeupAdapters.get("makeup_hairdye").setSelectedPosition(mMakeupOptionSelectedIndex.get(Constants.ST_MAKEUP_HAIR_DYE));
            }
        });

        mMakeupGroupBack = (ImageView) findViewById(R.id.iv_makeup_group);
        mMakeupGroupBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMakeupGroupRelativeLayout.setVisibility(View.VISIBLE);
                mMakeupIconsRelativeLayout.setVisibility(View.INVISIBLE);
                mFilterStrengthLayout.setVisibility(View.INVISIBLE);

                setMakeUpViewSelected(mMakeupOptionSelectedIndex.get(Constants.ST_MAKEUP_LIP) != 0, Constants.ST_MAKEUP_LIP);
                setMakeUpViewSelected(mMakeupOptionSelectedIndex.get(Constants.ST_MAKEUP_BLUSH) != 0, Constants.ST_MAKEUP_BLUSH);
                setMakeUpViewSelected(mMakeupOptionSelectedIndex.get(Constants.ST_MAKEUP_HIGHLIGHT) != 0, Constants.ST_MAKEUP_HIGHLIGHT);
                setMakeUpViewSelected(mMakeupOptionSelectedIndex.get(Constants.ST_MAKEUP_BROW) != 0, Constants.ST_MAKEUP_BROW);
                setMakeUpViewSelected(mMakeupOptionSelectedIndex.get(Constants.ST_MAKEUP_EYE) != 0, Constants.ST_MAKEUP_EYE);
                setMakeUpViewSelected(mMakeupOptionSelectedIndex.get(Constants.ST_MAKEUP_EYELINER) != 0, Constants.ST_MAKEUP_EYELINER);
                setMakeUpViewSelected(mMakeupOptionSelectedIndex.get(Constants.ST_MAKEUP_EYELASH) != 0, Constants.ST_MAKEUP_EYELASH);
                setMakeUpViewSelected(mMakeupOptionSelectedIndex.get(Constants.ST_MAKEUP_EYEBALL) != 0, Constants.ST_MAKEUP_EYEBALL);
                setMakeUpViewSelected(mMakeupOptionSelectedIndex.get(Constants.ST_MAKEUP_HAIR_DYE) != 0, Constants.ST_MAKEUP_HAIR_DYE);
                setMakeUpViewSelected(mMakeupOptionSelectedIndex.get(Constants.ST_MAKEUP_ALL) != 0, Constants.ST_MAKEUP_ALL);
            }
        });
        mMakeupGroupName = (TextView) findViewById(R.id.tv_makeup_group);

        ArrayList mAdjustBeautyItem = LocalDataStore.getInstance().getAdjustBeautyList();
        float[] beautifyParamsTypeAdjust = EffectInfoDataHelper.getInstance().getAdjustParams();
        for (int i = 0; i < mAdjustBeautyItem.size(); i++) {
            ((BeautyItem) mAdjustBeautyItem.get(i)).setProgress((int) (beautifyParamsTypeAdjust[i] * 100));
        }

        mBeautylists.put("adjustBeauty", mAdjustBeautyItem);
        BeautyItemAdapter mAdjustAdapter = new BeautyItemAdapter(this, mAdjustBeautyItem);
        mBeautyItemAdapters.put("adjustBeauty", mAdjustAdapter);
        mBeautyOption.put(6, "adjustBeauty");

        //整体效果
        initFullBeauty();

        mBeautyOptionSelectedIndex.put(0, 0);
        mBeautyOptionSelectedIndex.put(1, 0);
        mBeautyOptionSelectedIndex.put(2, 0);
        mBeautyOptionSelectedIndex.put(3, 0);
        mBeautyOptionSelectedIndex.put(6, 0);

        mStickerOptionsRecycleView = (RecyclerView) findViewById(R.id.rv_sticker_options);
        mStickerOptionsRecycleView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        mStickerOptionsRecycleView.addItemDecoration(new SpaceItemDecoration(0));

        mStickersRecycleView = (RecyclerView) findViewById(R.id.rv_sticker_icons);
        mStickersRecycleView.setLayoutManager(new GridLayoutManager(this, 6));
        mStickersRecycleView.addItemDecoration(new SpaceItemDecoration(0));

        mFilterOptionsRecycleView = (RecyclerView) findViewById(R.id.rv_filter_icons);
        mFilterOptionsRecycleView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        mFilterOptionsRecycleView.addItemDecoration(new SpaceItemDecoration(0));
        mNewStickers = FileUtils.getStickerFiles(this, "newEngine");
        ArrayList<StickerItem> mAddPackageStickers = FileUtils.getStickerFiles(this, "newEngine");

        mStickerOptionsList = LocalDataStore.getInstance().getStickerOptionsList();

        mNativeStickerAdapters.put("sticker_new_engine", new NativeStickerAdapter(mNewStickers, this));
        mNativeStickerAdapters.put("sticker_add_package", new NativeStickerAdapter(mAddPackageStickers, this));

        mNativeStickerAdapters.get("sticker_new_engine").notifyDataSetChanged();
        mNativeStickerAdapters.get("sticker_add_package").notifyDataSetChanged();
        initNativeStickerAdapter("sticker_new_engine", 0);
        initAddPackageStickerAdapter("sticker_add_package", 0);
        mStickerOptionsAdapter = new StickerOptionsAdapter(mStickerOptionsList, this);
        mStickerOptionsAdapter.notifyDataSetChanged();

        findViewById(R.id.iv_close_sticker).setBackground(getResources().getDrawable(R.drawable.close_sticker_selected));

        mFilterAndBeautyOptionView = (RelativeLayout) findViewById(R.id.rv_beauty_and_filter_options);

        RecyclerView mBeautyOptionsRecycleView = (RecyclerView) findViewById(R.id.rv_beauty_options);
        mBeautyOptionsRecycleView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        mBeautyOptionsRecycleView.addItemDecoration(new SpaceItemDecoration(0));

        mBeautyOptionsList = LocalDataStore.getInstance().getBeautyOptionsList();

        mBeautyOptionsAdapter = new BeautyOptionsAdapter(mBeautyOptionsList, this);
        mBeautyOptionsRecycleView.setAdapter(mBeautyOptionsAdapter);

        mFilterLists.put("filter_portrait", FileUtils.getFilterFiles(this, "filter_portrait"));
        mFilterLists.put("filter_scenery", FileUtils.getFilterFiles(this, "filter_scenery"));
        mFilterLists.put("filter_still_life", FileUtils.getFilterFiles(this, "filter_still_life"));
        mFilterLists.put("filter_food", FileUtils.getFilterFiles(this, "filter_food"));

        mFilterAdapters.put("filter_portrait", new FilterAdapter(mFilterLists.get("filter_portrait"), this));
        mFilterAdapters.put("filter_scenery", new FilterAdapter(mFilterLists.get("filter_scenery"), this));
        mFilterAdapters.put("filter_still_life", new FilterAdapter(mFilterLists.get("filter_still_life"), this));
        mFilterAdapters.put("filter_food", new FilterAdapter(mFilterLists.get("filter_food"), this));

        mFilterIconsRelativeLayout = (RelativeLayout) findViewById(R.id.rl_filter_icons);
        mFilterGroupsLinearLayout = (LinearLayout) findViewById(R.id.ll_filter_groups);
        LinearLayout mFilterGroupPortrait = (LinearLayout) findViewById(R.id.ll_filter_group_portrait);
        mFilterGroupPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFilterGroup("filter_portrait");
            }
        });
        LinearLayout mFilterGroupScenery = (LinearLayout) findViewById(R.id.ll_filter_group_scenery);
        mFilterGroupScenery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFilterGroup("filter_scenery");
            }
        });
        LinearLayout mFilterGroupStillLife = (LinearLayout) findViewById(R.id.ll_filter_group_still_life);
        mFilterGroupStillLife.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFilterGroup("filter_still_life");
            }
        });
        LinearLayout mFilterGroupFood = (LinearLayout) findViewById(R.id.ll_filter_group_food);
        mFilterGroupFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFilterGroup("filter_food");
            }
        });

        mFilterGroupBack = (ImageView) findViewById(R.id.iv_filter_group);
        mFilterGroupBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFilterGroupsLinearLayout.setVisibility(View.VISIBLE);
                mFilterIconsRelativeLayout.setVisibility(View.INVISIBLE);
                mFilterStrengthLayout.setVisibility(View.INVISIBLE);

                mShowOriginBtn3.setVisibility(View.VISIBLE);
            }
        });
        mFilterGroupName = (TextView) findViewById(R.id.tv_filter_group);
        mFilterStrengthText = (TextView) findViewById(R.id.tv_filter_strength);

        mFilterStrengthLayout = (RelativeLayout) findViewById(R.id.rv_filter_strength);
        mFilterStrengthBar = (SeekBar) findViewById(R.id.sb_filter_strength);
        mFilterStrengthBar.setProgress(100);
        mFilterStrength = 100;
        mFilterStrengthText.setText("100");
        mFilterStrengthBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (mBeautyOptionsPosition == 5) {
                        mFilterStrength = progress;
                        mCameraDisplay.setFilterStrength((float) progress / 100);
                        mFilterStrengthText.setText(progress + "");
                        if (fromUser) isFromUserSeekBar = true;
                    } else if (mBeautyOptionsPosition == 4) {
                        mCameraDisplay.setStrengthForType(mCurrentMakeupGroupIndex, (float) progress / 100);
                        mMakeupStrength.put(mCurrentMakeupGroupIndex, progress);
                        mFilterStrengthText.setText(progress + "");
                        if (fromUser) isFromUserSeekBar = true;
                    }
                    if (fromUser) {
                        int[] makeupFilterIndices = {4, 5};
                        if (Arrays.binarySearch(makeupFilterIndices, mBeautyOptionsPosition) >= 0) {
                            clearFullBeautyView();
                        }
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mVerticalSeekBar = (VerticalSeekBar) findViewById(R.id.vertical_seekbar);
        mVerticalSeekBar.setProgress(50);
        mVerticalSeekBar.setHandler(mHandler);
        mVerticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mCameraDisplay.setExposureCompensation(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mStickerOptionsRecycleView.setAdapter(mStickerOptionsAdapter);

        mObjectList = FileUtils.getObjectList();
        mObjectAdapter = new ObjectAdapter(mObjectList, this);
        mObjectAdapter.setSelectedPosition(-1);

        mFilterOptionsRecycleView.setAdapter(mFilterAdapters.get("filter_portrait"));

        mSavingTv = (TextView) findViewById(R.id.tv_saving_image);
        mTipsLayout = (RelativeLayout) findViewById(R.id.tv_layout_tips);
        mAttributeText = (TextView) findViewById(R.id.tv_face_attribute);
        mAttributeText.setVisibility(View.VISIBLE);
        mTipsTextView = (TextView) findViewById(R.id.tv_text_tips);
        mTipsImageView = (ImageView) findViewById(R.id.iv_image_tips);
        mTipsLayout.setVisibility(View.GONE);

        mBeautyOptionsSwitch = (LinearLayout) findViewById(R.id.ll_beauty_options_switch);
        mBeautyOptionsSwitch.setOnClickListener(this);
        RecyclerView mFilterIcons = (RecyclerView) findViewById(R.id.rv_filter_icons);

        mBaseBeautyOptions = (LinearLayout) findViewById(R.id.ll_base_beauty_options);
        mBaseBeautyOptions.setOnClickListener(null);
        mIsBeautyOptionsOpen = false;

        mStickerOptionsSwitch = (LinearLayout) findViewById(R.id.ll_sticker_options_switch);
        mStickerOptionsSwitch.setOnClickListener(this);
        mStickerOptions = (RelativeLayout) findViewById(R.id.rl_sticker_options);
        mStickerIcons = (RecyclerView) findViewById(R.id.rv_sticker_icons);
        mIsStickerOptionsOpen = false;

        mSettingOptionsSwitch = (ImageView) findViewById(R.id.iv_setting_options_switch);
        mSettingOptionsSwitch.setOnClickListener(this);
        mSettingOptions = (ConstraintLayout) findViewById(R.id.rl_setting_options);
        mIsSettingOptionsOpen = false;

        mSelectionPicture = (ImageView) findViewById(R.id.iv_mode_picture);
//        mSelectionPicture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), LoadImageActivity.class));
//                finish();
//            }
//        });

        findViewById(R.id.tv_cancel).setVisibility(View.INVISIBLE);
        findViewById(R.id.tv_capture).setVisibility(View.INVISIBLE);

        mSmallPreviewSize = (TextView) findViewById(R.id.tv_small_size_unselected);
        mSmallPreviewSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mCameraDisplay != null && !mCameraDisplay.isChangingPreviewSize()) {
                    mCameraDisplay.changePreviewSize(1);
                    findViewById(R.id.tv_small_size_selected).setVisibility(View.VISIBLE);
                    findViewById(R.id.tv_small_size_unselected).setVisibility(View.INVISIBLE);
                    findViewById(R.id.tv_large_size_selected).setVisibility(View.INVISIBLE);
                    findViewById(R.id.tv_large_size_unselected).setVisibility(View.VISIBLE);
                    findViewById(R.id.tv_largest_size_selected).setVisibility(View.INVISIBLE);
                    findViewById(R.id.tv_largest_size_unselected).setVisibility(View.VISIBLE);

                    mSmallPreviewSize.setClickable(false);
                    mLargePreviewSize.setClickable(true);
                    mLargestPreviewSize.setClickable(true);
                }
            }
        });
        mLargePreviewSize = (TextView) findViewById(R.id.tv_large_size_unselected);
        mLargePreviewSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mCameraDisplay != null && !mCameraDisplay.isChangingPreviewSize()) {
                    mCameraDisplay.changePreviewSize(0);
                    findViewById(R.id.tv_small_size_selected).setVisibility(View.INVISIBLE);
                    findViewById(R.id.tv_small_size_unselected).setVisibility(View.VISIBLE);
                    findViewById(R.id.tv_large_size_selected).setVisibility(View.VISIBLE);
                    findViewById(R.id.tv_large_size_unselected).setVisibility(View.INVISIBLE);
                    findViewById(R.id.tv_largest_size_selected).setVisibility(View.INVISIBLE);
                    findViewById(R.id.tv_largest_size_unselected).setVisibility(View.VISIBLE);

                    mSmallPreviewSize.setClickable(true);
                    mLargePreviewSize.setClickable(false);
                    mLargestPreviewSize.setClickable(true);
                }
            }
        });

        mLargestPreviewSize = (TextView) findViewById(R.id.tv_largest_size_unselected);
        mLargestPreviewSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mCameraDisplay != null && !mCameraDisplay.isChangingPreviewSize()) {
                    mCameraDisplay.changePreviewSize(2);
                    findViewById(R.id.tv_small_size_selected).setVisibility(View.INVISIBLE);
                    findViewById(R.id.tv_small_size_unselected).setVisibility(View.VISIBLE);
                    findViewById(R.id.tv_large_size_selected).setVisibility(View.INVISIBLE);
                    findViewById(R.id.tv_large_size_unselected).setVisibility(View.VISIBLE);
                    findViewById(R.id.tv_largest_size_selected).setVisibility(View.VISIBLE);
                    findViewById(R.id.tv_largest_size_unselected).setVisibility(View.INVISIBLE);

                    mSmallPreviewSize.setClickable(true);
                    mLargePreviewSize.setClickable(true);
                    mLargestPreviewSize.setClickable(false);
                }
            }
        });

        mFpsInfo = (LinearLayout) findViewById(R.id.ll_fps_info);
        mFpsInfo.setVisibility(View.INVISIBLE);
        Switch mPerformanceInfoSwitch = (Switch) findViewById(R.id.sw_performance_switch);
        mPerformanceInfoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    mFpsInfo.setVisibility(View.VISIBLE);
                } else {
                    mFpsInfo.setVisibility(View.INVISIBLE);
                }
            }
        });

        mSelectOptions = (LinearLayout) findViewById(R.id.ll_select_options);
        mSelectOptions.setBackgroundColor(Color.parseColor("#00000000"));
        mCaptureButton = (Button) findViewById(R.id.btn_capture_picture);

        findViewById(R.id.tv_provisions_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.rl_provisions).setVisibility(View.VISIBLE);
                ((WebView) findViewById(R.id.wv_docs)).loadUrl("file:///android_asset/SenseME_Provisions_v1.0.html");
                ((WebView) findViewById(R.id.wv_docs)).getSettings().setTextSize(WebSettings.TextSize.SMALLER);
            }
        });

        findViewById(R.id.tv_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.rl_provisions).setVisibility(View.GONE);
            }
        });

        if (Constants.ACTIVITY_MODE_FOR_TV) {
            mCaptureButton.setVisibility(View.INVISIBLE);
        }
    }

    private void setFullBeautyFilter(int progress) {
        if (mCurrentFullBeautyIndex == -1) return;
        mIndicatorSeekbar.setProgress(progress);
        mCameraDisplay.setFilterStrength((float) progress / 100f);
        EffectInfoDataHelper.getInstance().fullBeautyFilterProgress = progress;
        mBeautyFullItem.get(mCurrentFullBeautyIndex).setFilterProgress(progress);
        float scale = progress / 100f;
        isFromUserSeekBar = false;
        fullBeautyFilterScale = scale;
    }

    private void setFullBeautyMakeup(int progress) {
        EffectInfoDataHelper.getInstance().fullBeautyMakeupProgress = progress;
        mBeautyFullItem.get(mCurrentFullBeautyIndex).setMakeupAllProgress(progress);
        for (Map.Entry<Integer, Integer> entry : mMakeupStrength.entrySet()) {
            Integer itValue = entry.getValue();
            Integer itKey = entry.getKey();
            float scale = progress / 100f;
            mCameraDisplay.setStrengthForType(itKey, ((int) (itValue * scale)) / 100f);
            //mCameraDisplay.setBeautyParam(410, ((int) (itValue * scale)) / 100f);
            isFromUserSeekBar = false;
            fullBeautyMakeupScale = scale;
        }
    }

    private void clickMakeupGroup(String type, int groupIndex) {
        if (mMakeupOptionSelectedIndex.get(10) != 0 && groupIndex != Constants.ST_MAKEUP_ALL) {
            showMakeupTips();
            return;
        }

        mCurrentMakeupGroupIndex = groupIndex;
        if (type.equals("makeup_lip")) {
            mMakeupGroupName.setText(MultiLanguageUtils.getStr(R.string.make_up_lipstick));
            if (mMakeupOptionSelectedIndex.get(3) != 0) {
                mFilterStrengthLayout.setVisibility(View.VISIBLE);
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_lip_selected));
            } else {
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_lip_unselected));
            }
        } else if (type.equals("makeup_blush")) {
            mMakeupGroupName.setText(MultiLanguageUtils.getStr(R.string.make_up_blusher));
            if (mMakeupOptionSelectedIndex.get(2) != 0) {
                mFilterStrengthLayout.setVisibility(View.VISIBLE);
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_cheeks_selected));
            } else {
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_cheeks_unselected));
            }
        } else if (type.equals("makeup_highlight")) {
            mMakeupGroupName.setText(MultiLanguageUtils.getStr(R.string.make_up_highlight));
            if (mMakeupOptionSelectedIndex.get(4) != 0) {
                mFilterStrengthLayout.setVisibility(View.VISIBLE);
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_face_selected));
            } else {
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_face_unselected));
            }
        } else if (type.equals("makeup_brow")) {
            mMakeupGroupName.setText(MultiLanguageUtils.getStr(R.string.make_up_brow));
            if (mMakeupOptionSelectedIndex.get(5) != 0) {
                mFilterStrengthLayout.setVisibility(View.VISIBLE);
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_brow_selected));
            } else {
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_brow_unselected));
            }
        } else if (type.equals("makeup_eye")) {
            mMakeupGroupName.setText(MultiLanguageUtils.getStr(R.string.make_up_eye_shadow));
            if (mMakeupOptionSelectedIndex.get(1) != 0) {
                mFilterStrengthLayout.setVisibility(View.VISIBLE);
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_eye_selected));
            } else {
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_eye_unselected));
            }
        } else if (type.equals("makeup_eyeliner")) {
            mMakeupGroupName.setText(MultiLanguageUtils.getStr(R.string.make_up_eye_liner));
            if (mMakeupOptionSelectedIndex.get(6) != 0) {
                mFilterStrengthLayout.setVisibility(View.VISIBLE);
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_eyeliner_selected));
            } else {
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_eyeline_unselected));
            }
        } else if (type.equals("makeup_eyelash")) {
            mMakeupGroupName.setText(MultiLanguageUtils.getStr(R.string.make_up_eyelash));
            if (mMakeupOptionSelectedIndex.get(7) != 0) {
                mFilterStrengthLayout.setVisibility(View.VISIBLE);
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_eyelash_selected));
            } else {
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_eyelash_unselected));
            }
        } else if (type.equals("makeup_eyeball")) {
            mMakeupGroupName.setText(MultiLanguageUtils.getStr(R.string.make_up_eyeball));
            if (mMakeupOptionSelectedIndex.get(7) != 0) {
                mFilterStrengthLayout.setVisibility(View.VISIBLE);
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_eyeball_selected));
            } else {
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_eyeball_unselected));
            }
        } else if (type.equals("makeup_hairdye")) {
            mMakeupGroupName.setText(MultiLanguageUtils.getStr(R.string.make_up_hair));
            if (mMakeupOptionSelectedIndex.get(9) != 0) {
                mFilterStrengthLayout.setVisibility(View.VISIBLE);
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_hairdye_selected));
            } else {
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_hairdye_unselected));
            }
        } else if (type.equals("makeup_all")) {
            mMakeupGroupName.setText(MultiLanguageUtils.getStr(R.string.make_up_whole));
            if (mMakeupOptionSelectedIndex.get(10) != 0) {
                mFilterStrengthLayout.setVisibility(View.VISIBLE);
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_all_selected));
            } else {
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.makeup_all_unselected));
            }
        }

        mMakeupGroupRelativeLayout.setVisibility(View.INVISIBLE);
        mMakeupIconsRelativeLayout.setVisibility(View.VISIBLE);
        int progress = mMakeupStrength.get(mCurrentMakeupGroupIndex);
        if (!isFromUserSeekBar && mCurrentFullBeautyIndex != -1) {
            progress = (Math.round(mDefaultMakeupStrength.get(mCurrentMakeupGroupIndex) * fullBeautyMakeupScale));
        }
        mFilterStrengthBar.setProgress(progress);
        mFilterStrengthText.setText(String.valueOf(progress));
        mMakeupStrength.put(mCurrentMakeupGroupIndex, progress);

        mMakeupOptionsRecycleView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        mMakeupOptionsRecycleView.setAdapter(mMakeupAdapters.get(type));
        mMakeupAdapters.get(type).setSelectedPosition(mMakeupOptionSelectedIndex.get(groupIndex));
    }

    private void clickFilterGroup(String type) {
        mFilterGroupsLinearLayout.setVisibility(View.INVISIBLE);
        mFilterIconsRelativeLayout.setVisibility(View.VISIBLE);

        Log.d(TAG, "clickFilterGroup: mCurrentFilterIndex:" + mCurrentFilterIndex);
        if (mCurrentFilterIndex != -1) {
            mFilterStrengthLayout.setVisibility(View.VISIBLE);
        } else {
            mFilterStrengthLayout.setVisibility(View.INVISIBLE);
        }

        if (!isFromUserSeekBar && mCurrentFullBeautyIndex != -1) {
            mFilterStrength = (Math.round(mDefaultFilterStrength * fullBeautyFilterScale));
        }

        mFilterStrengthBar.setProgress(mFilterStrength);
        mFilterStrengthText.setText(String.valueOf(mFilterStrength));

        mFilterOptionsRecycleView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        mFilterOptionsRecycleView.setAdapter(mFilterAdapters.get(type));
        if (type.equals("filter_food")) {
            mFilterGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.icon_food_selected));
            mFilterGroupName.setText(MultiLanguageUtils.getStr(R.string.filter_food));
        } else if (type.equals("filter_still_life")) {
            mFilterGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.icon_still_life_selected));
            mFilterGroupName.setText(MultiLanguageUtils.getStr(R.string.filter_still_life));
        } else if (type.equals("filter_scenery")) {
            mFilterGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.icon_scenery_selected));
            mFilterGroupName.setText(MultiLanguageUtils.getStr(R.string.filter_scenery));
        } else if (type.equals("filter_portrait")) {
            mFilterGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.icon_portrait_selected));
            mFilterGroupName.setText(MultiLanguageUtils.getStr(R.string.filter_portrait));
        }
    }

    private void initEvents() {
        mSurfaceViewOverlap.setZOrderOnTop(true);
        mSurfaceViewOverlap.setZOrderMediaOverlay(true);
        mSurfaceViewOverlap.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        mPaint = new Paint();
        mPaint.setColor(Color.rgb(240, 100, 100));
        int strokeWidth = 10;
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        initStickerTabListener();

        mFilterAdapters.get("filter_portrait").setClickFilterListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EffectInfoDataHelper.getInstance().filterStyle = "filter_portrait";
                resetFilterView(false);
                int position = Integer.parseInt(v.getTag().toString());
                mFilterAdapters.get("filter_portrait").setSelectedPosition(position);
                mCurrentFilterGroupIndex = 0;
                mCurrentFilterIndex = -1;

                if (position == 0) {
                    mCameraDisplay.enableFilter(false);
                } else {
                    mCameraDisplay.setFilterStyle("filter_portrait", mFilterLists.get("filter_portrait").get(position).name, mFilterLists.get("filter_portrait").get(position).model);
                    mCameraDisplay.enableFilter(true);
                    mCurrentFilterIndex = position;

                    mFilterStrengthLayout.setVisibility(View.VISIBLE);
                    mFilterStrengthBar.setProgress(mFilterStrength);
                    mFilterStrengthText.setText(String.valueOf(mFilterStrength));
                    mShowOriginBtn1.setVisibility(View.INVISIBLE);
                    mShowOriginBtn2.setVisibility(View.INVISIBLE);
                    mShowOriginBtn3.setVisibility(View.VISIBLE);
                    setFilterViewSelected(mCurrentFilterGroupIndex);
                }

                mFilterAdapters.get("filter_portrait").notifyDataSetChanged();
                clearFullBeautyView();
            }
        });

        mFilterAdapters.get("filter_scenery").setClickFilterListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFilterView(false);
                int position = Integer.parseInt(v.getTag().toString());
                mFilterAdapters.get("filter_scenery").setSelectedPosition(position);
                mCurrentFilterGroupIndex = 1;
                mCurrentFilterIndex = -1;

                if (position == 0) {
                    mCameraDisplay.enableFilter(false);
                } else {
                    mCameraDisplay.setFilterStyle("filter_scenery", mFilterLists.get("filter_scenery").get(position).name, mFilterLists.get("filter_scenery").get(position).model);
                    mCameraDisplay.enableFilter(true);
                    mCurrentFilterIndex = position;

                    mFilterStrengthLayout.setVisibility(View.VISIBLE);
                    mFilterStrengthBar.setProgress(mFilterStrength);
                    mFilterStrengthText.setText(String.valueOf(mFilterStrength));
                    mShowOriginBtn1.setVisibility(View.INVISIBLE);
                    mShowOriginBtn2.setVisibility(View.INVISIBLE);
                    mShowOriginBtn3.setVisibility(View.VISIBLE);
                    setFilterViewSelected(mCurrentFilterGroupIndex);
//                    ((ImageView) findViewById(R.id.iv_filter_group_scenery)).setImageDrawable(getResources().getDrawable(R.drawable.icon_scenery_selected));
//                    ((TextView) findViewById(R.id.tv_filter_group_scenery)).setTextColor(Color.parseColor("#c460e1"));
                }

                mFilterAdapters.get("filter_scenery").notifyDataSetChanged();
                clearFullBeautyView();
            }
        });

        mFilterAdapters.get("filter_still_life").setClickFilterListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFilterView(false);
                int position = Integer.parseInt(v.getTag().toString());
                mFilterAdapters.get("filter_still_life").setSelectedPosition(position);
                mCurrentFilterGroupIndex = 2;
                mCurrentFilterIndex = -1;

                if (position == 0) {
                    mCameraDisplay.enableFilter(false);
                } else {
                    mCameraDisplay.setFilterStyle("filter_still_life", mFilterLists.get("filter_still_life").get(position).name, mFilterLists.get("filter_still_life").get(position).model);
                    mCameraDisplay.enableFilter(true);
                    mCurrentFilterIndex = position;

                    mFilterStrengthLayout.setVisibility(View.VISIBLE);
                    mFilterStrengthBar.setProgress(mFilterStrength);
                    mFilterStrengthText.setText(String.valueOf(mFilterStrength));
                    mShowOriginBtn1.setVisibility(View.INVISIBLE);
                    mShowOriginBtn2.setVisibility(View.INVISIBLE);
                    mShowOriginBtn3.setVisibility(View.VISIBLE);
                    setFilterViewSelected(mCurrentFilterGroupIndex);
                }

                mFilterAdapters.get("filter_still_life").notifyDataSetChanged();
                clearFullBeautyView();
            }
        });

        mFilterAdapters.get("filter_food").setClickFilterListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFilterView(false);
                int position = Integer.parseInt(v.getTag().toString());
                mFilterAdapters.get("filter_food").setSelectedPosition(position);
                mCurrentFilterGroupIndex = 3;
                mCurrentFilterIndex = -1;

                if (position == 0) {
                    mCameraDisplay.enableFilter(false);
                } else {
                    mCameraDisplay.setFilterStyle("filter_food", mFilterLists.get("filter_food").get(position).name, mFilterLists.get("filter_food").get(position).model);
                    mCameraDisplay.enableFilter(true);
                    mCurrentFilterIndex = position;

                    mFilterStrengthLayout.setVisibility(View.VISIBLE);
                    mFilterStrengthBar.setProgress(mFilterStrength);
                    mFilterStrengthText.setText(String.valueOf(mFilterStrength));
                    mShowOriginBtn1.setVisibility(View.INVISIBLE);
                    mShowOriginBtn2.setVisibility(View.INVISIBLE);
                    mShowOriginBtn3.setVisibility(View.VISIBLE);
                    setFilterViewSelected(mCurrentFilterGroupIndex);
                }

                mFilterAdapters.get("filter_food").notifyDataSetChanged();

                clearFullBeautyView();
            }
        });

        mBeautyOptionsAdapter.setClickBeautyListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(v.getTag().toString());
                mBeautyOptionsAdapter.setSelectedPosition(position);
                mBeautyOptionsPosition = position;
                mFilterGroupsLinearLayout.setVisibility(View.INVISIBLE);
                mBaseBeautyOptions.setVisibility(View.VISIBLE);
                mMakeupGroupRelativeLayout.setVisibility(View.INVISIBLE);

                //整体效果
                if (mBeautyOptionsPosition == 0) {
                    if (mCurrentFullBeautyIndex == -1) {
                        gpFullBeautySeekBar.setVisibility(View.GONE);
                        mIndicatorSeekbar.setVisibility(View.INVISIBLE);
                    } else {
                        gpFullBeautySeekBar.setVisibility(View.VISIBLE);
                        mIndicatorSeekbar.setVisibility(View.VISIBLE);

                        mIndicatorSeekbar.getSeekBar().setProgress(mBeautyFullItem.get(mCurrentFullBeautyIndex).getFilterProgress());
                        mIndicatorSeekbar.updateTextView(mBeautyFullItem.get(mCurrentFullBeautyIndex).getFilterProgress());
                        mIndicatorSeekbarNew.getSeekBar().setProgress(mBeautyFullItem.get(mCurrentFullBeautyIndex).getMakeupAllProgress());
                        mIndicatorSeekbarNew.updateTextView(mBeautyFullItem.get(mCurrentFullBeautyIndex).getMakeupAllProgress());
                    }
                    //mIndicatorSeekbar.getSeekBar().setProgress(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(mBeautyOptionSelectedIndex.get(position)).getProgress());
                    mBeautyFullAdapter.setSelectedPosition(mCurrentFullBeautyIndex);
                    mBeautyFullAdapter.notifyDataSetChanged();
                } else if (mBeautyOptionsPosition != 4 && mBeautyOptionsPosition != 5) {
                    gpFullBeautySeekBar.setVisibility(View.GONE);
                    calculateBeautyIndex(mBeautyOptionsPosition, mBeautyOptionSelectedIndex.get(mBeautyOptionsPosition));
                    mIndicatorSeekbar.setVisibility(View.VISIBLE);
                    if (checkMicroType(mBeautyOptionsPosition)) {
                        mIndicatorSeekbar.getSeekBar().setProgress(STUtils.convertToData(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(mBeautyOptionSelectedIndex.get(position)).getProgress()));
                    } else {
                        mIndicatorSeekbar.getSeekBar().setProgress(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(mBeautyOptionSelectedIndex.get(position)).getProgress());
                    }
                    mIndicatorSeekbar.updateTextView(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(mBeautyOptionSelectedIndex.get(position)).getProgress());
                } else {
                    gpFullBeautySeekBar.setVisibility(View.GONE);
                    mIndicatorSeekbar.setVisibility(View.INVISIBLE);
                }
                mFilterIconsRelativeLayout.setVisibility(View.INVISIBLE);
                mMakeupIconsRelativeLayout.setVisibility(View.INVISIBLE);
                mShowOriginBtn3.setVisibility(View.VISIBLE);
                mFilterStrengthLayout.setVisibility(View.INVISIBLE);
                if (position == 0) {
                    mBeautyBaseRecycleView.setAdapter(mBeautyFullAdapter);
                } else if (position == 1) {
                    mBeautyBaseRecycleView.setAdapter(mBeautyItemAdapters.get("baseBeauty"));
                } else if (position == 2) {
                    mBeautyBaseRecycleView.setAdapter(mBeautyItemAdapters.get("professionalBeauty"));
                } else if (position == 3) {
                    mBeautyBaseRecycleView.setAdapter(mBeautyItemAdapters.get("microBeauty"));
                } else if (position == 4) {
                    mMakeupGroupRelativeLayout.setVisibility(View.VISIBLE);
                    mBaseBeautyOptions.setVisibility(View.INVISIBLE);
                } else if (position == 5) {
                    mFilterGroupsLinearLayout.setVisibility(View.VISIBLE);
                    mBaseBeautyOptions.setVisibility(View.INVISIBLE);
                } else if (position == 6) {
                    mBeautyBaseRecycleView.setAdapter(mBeautyItemAdapters.get("adjustBeauty"));
                }
                mBeautyOptionsAdapter.notifyDataSetChanged();
            }
        });

        //整体效果初始状态
        mBeautyFullAdapter.setSelectedPosition(mCurrentFullBeautyIndex);
        mBeautyFullAdapter.notifyDataSetChanged();
        mBeautyFullAdapter.setClickBeautyListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(v.getTag().toString());
//                EffectInfoDataHelper.fullBeautyStatus = true;
                mIndicatorSeekbar.setVisibility(View.VISIBLE);

                if (mCurrentFullBeautyIndex == position) {

                } else {
                    isFromUserSeekBar = false;
                    fullBeautyMakeupScale = 0.85f;
                    fullBeautyFilterScale = 0.85f;
                    setFullBeauty(mBeautyFullItem.get(position));
                    mBeautyFullAdapter.setSelectedPosition(position);
                    mCurrentFullBeautyIndex = position;
                    mBeautyFullAdapter.notifyDataSetChanged();
                }

                gpFullBeautySeekBar.setVisibility(View.VISIBLE);
                mIndicatorSeekbar.getSeekBar().setProgress(mBeautyFullItem.get(position).getFilterProgress());
                mIndicatorSeekbar.updateTextView(mBeautyFullItem.get(position).getFilterProgress());
                mIndicatorSeekbarNew.getSeekBar().setProgress(mBeautyFullItem.get(position).getMakeupAllProgress());
                mIndicatorSeekbarNew.updateTextView(mBeautyFullItem.get(position).getMakeupAllProgress());

                if (lastIndex != position) {
                    EffectInfoDataHelper.getInstance().fullBeautyMakeupProgress = 85;
                    EffectInfoDataHelper.getInstance().fullBeautyFilterProgress = 85;
                    mBeautyFullItem.get(position).setFilterProgress(85);
                    mBeautyFullItem.get(position).setMakeupAllProgress(85);
                    mIndicatorSeekbar.getSeekBar().setProgress(85);
                    mIndicatorSeekbar.updateTextView(85);
                    mIndicatorSeekbarNew.getSeekBar().setProgress(85);
                    mIndicatorSeekbarNew.updateTextView(85);
                }
                lastIndex = position;
                EffectInfoDataHelper.getInstance().fullBeautyName = mBeautyFullItem.get(position).getText();
            }
        });

        mObjectAdapter.setClickObjectListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(v.getTag().toString());

                if (mCurrentObjectIndex == position) {
                    mCurrentObjectIndex = -1;
                    mObjectAdapter.setSelectedPosition(-1);
                    mObjectAdapter.notifyDataSetChanged();
                    mCameraDisplay.enableObject(false);
                } else {
                    mObjectAdapter.setSelectedPosition(position);
                    mCameraDisplay.enableObject(true);
                    mGuideBitmap = BitmapFactory.decodeResource(mContext.getResources(), mObjectList.get(position).drawableID);
                    mCameraDisplay.resetIndexRect();

                    mObjectAdapter.notifyDataSetChanged();

                    mCurrentObjectIndex = position;
                }
            }
        });

        for (Map.Entry<String, BeautyItemAdapter> entry : mBeautyItemAdapters.entrySet()) {
            final BeautyItemAdapter adapter = entry.getValue();
            adapter.setClickBeautyListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = Integer.parseInt(v.getTag().toString());
                    if (position < 2) {
                        mCameraDisplay.setWhitenFromAssetsFile(null);
                    }
                    if (position == 2) {
                        mCameraDisplay.setWhitenFromAssetsFile("whiten_gif.zip");
                    }
                    adapter.setSelectedPosition(position);
                    mBeautyOptionSelectedIndex.put(mBeautyOptionsPosition, position);
                    if (checkMicroType(mBeautyOptionsPosition)) {
                        mIndicatorSeekbar.getSeekBar().setProgress(STUtils.convertToData(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(position).getProgress()));
                    } else {
                        mIndicatorSeekbar.getSeekBar().setProgress(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(position).getProgress());
                    }
                    mIndicatorSeekbar.updateTextView(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(position).getProgress());
                    calculateBeautyIndex(mBeautyOptionsPosition, position);
                    adapter.notifyDataSetChanged();
                }
            });
        }

        mShowOriginBtn1 = (TextView) findViewById(R.id.tv_show_origin1);
        mShowOriginBtn1.setOnClickListener(this);
//        mShowOriginBtn1.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // TODO Auto-generated method stub
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    mCameraDisplay.setShowOriginal(true);
//                    mCaptureButton.setEnabled(false);
//                    findViewById(R.id.tv_change_camera).setEnabled(false);
//                } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                    mCameraDisplay.setShowOriginal(false);
//                    mCaptureButton.setEnabled(true);
//                    findViewById(R.id.tv_change_camera).setEnabled(true);
//                }
//                return true;
//            }
//        });
        mShowOriginBtn1.setVisibility(View.VISIBLE);

        mShowOriginBtn2 = (TextView) findViewById(R.id.tv_show_origin2);
        mShowOriginBtn2.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mCameraDisplay.setShowOriginal(true);
                    mCaptureButton.setEnabled(false);
                    findViewById(R.id.tv_change_camera).setEnabled(false);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mCameraDisplay.setShowOriginal(false);
                    mCaptureButton.setEnabled(true);
                    findViewById(R.id.tv_change_camera).setEnabled(true);
                }
                return true;
            }
        });
        mShowOriginBtn2.setVisibility(View.INVISIBLE);

        mShowOriginBtn3 = (TextView) findViewById(R.id.tv_show_origin3);
        mShowOriginBtn3.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mCameraDisplay.setShowOriginal(true);
                    mCaptureButton.setEnabled(false);
                    findViewById(R.id.tv_change_camera).setEnabled(false);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mCameraDisplay.setShowOriginal(false);
                    mCaptureButton.setEnabled(true);
                    findViewById(R.id.tv_change_camera).setEnabled(true);
                }
                return true;
            }
        });
        mShowOriginBtn3.setVisibility(View.INVISIBLE);

        mCaptureButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mTouchDownTime = System.currentTimeMillis();
                    mOnBtnTouch = true;
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            while (mOnBtnTouch && Build.VERSION.SDK_INT >= 17) {
                                mTouchCurrentTime = System.currentTimeMillis();
                                if (mTouchCurrentTime - mTouchDownTime >= 500 && !mIsRecording && !mIsPaused) {
                                    //开始录制
                                    Message msg = mHandler.obtainMessage(MSG_NEED_START_RECORDING);
                                    mHandler.sendMessage(msg);
                                    mIsRecording = true;
                                } else if (mTouchCurrentTime - mTouchDownTime >= 10500 && mIsRecording && !mIsPaused) {
                                    //超时结束录制
                                    Message msg = mHandler.obtainMessage(MSG_STOP_RECORDING);
                                    mHandler.sendMessage(msg);
                                    mIsRecording = false;

                                    break;
                                }
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                    thread.start();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mOnBtnTouch = false;
                    if (mTouchCurrentTime - mTouchDownTime > 500 && mIsRecording && !mIsPaused && Build.VERSION.SDK_INT >= 17) {
                        //结束录制
                        Message msg = mHandler.obtainMessage(MSG_STOP_RECORDING);
                        mHandler.sendMessage(msg);
                        mIsRecording = false;
                    } else if (mTouchCurrentTime - mTouchDownTime <= 500) {
                        //保存图片
                        if (isWritePermissionAllowed()) {
                            mCameraDisplay.setHandler(mHandler);
                            mCameraDisplay.setSaveImage();
                            if (mMediaActionSound != null) {
                                mMediaActionSound.play(MediaActionSound.SHUTTER_CLICK);
                            }
                        } else {
                            requestWritePermission();
                        }
                    }
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    mOnBtnTouch = false;
                }
                return true;
            }
        });

        mShowShortVideoTime = (TextView) findViewById(R.id.tv_short_video_time);
        findViewById(R.id.rv_close_sticker).setOnClickListener(this);
        findViewById(R.id.tv_change_camera).setOnClickListener(this);
        findViewById(R.id.tv_change_camera).setVisibility(View.VISIBLE);

        // switch camera
        findViewById(R.id.id_tv_changecamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraDisplay.switchCamera();
            }
        });

        mCameraDisplay.enableBeautify(true);
        mIsHasAudioPermission = CheckAudioPermission.isHasPermission(mContext);

        if (DEBUG) {
            //for test add sub model
            Switch mBodySwitch = (Switch) findViewById(R.id.sw_add_body_model_switch);
            mBodySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    // TODO Auto-generated method stub
                    if (isChecked) {
                        mCameraDisplay.addSubModelByName(FileUtils.MODEL_NAME_BODY_FOURTEEN);
                    } else {
                        mCameraDisplay.removeSubModelByConfig(STMobileHumanActionNative.ST_MOBILE_ENABLE_BODY_KEYPOINTS);
                    }
                }
            });

            Switch mFaceExtraInfoSwitch = (Switch) findViewById(R.id.sw_add_face_extra_model);
            mFaceExtraInfoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    // TODO Auto-generated method stub
                    if (isChecked) {
                        mCameraDisplay.addSubModelByName(FileUtils.MODEL_NAME_FACE_EXTRA);
                    } else {
                        mCameraDisplay.removeSubModelByConfig(STMobileHumanActionNative.ST_MOBILE_ENABLE_FACE_EXTRA_DETECT);
                    }
                }
            });

            Switch mEyeBallContourSwitch = (Switch) findViewById(R.id.sw_add_iris_model);
            mEyeBallContourSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    // TODO Auto-generated method stub
                    if (isChecked) {
                        mCameraDisplay.addSubModelByName(FileUtils.MODEL_NAME_EYEBALL_CONTOUR);
                    } else {
                        mCameraDisplay.removeSubModelByConfig(STMobileHumanActionNative.ST_MOBILE_ENABLE_EYEBALL_CONTOUR_DETECT);
                    }
                }
            });

            Switch mHandActionSwitch = (Switch) findViewById(R.id.sw_add_hand_action_model);
            mHandActionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    // TODO Auto-generated method stub
                    if (isChecked) {
                        mCameraDisplay.addSubModelByName(FileUtils.MODEL_NAME_HAND);
                    } else {
                        mCameraDisplay.removeSubModelByConfig(STMobileHumanActionNative.ST_MOBILE_ENABLE_HAND_DETECT);
                    }
                }
            });
        }
    }

    private void onClickZero() {
        if (mBeautyOptionsPosition == 0) {
            resetZeroFullBeautyView();
            EffectInfoDataHelper.getInstance().fullBeautyName = "";
            clearFullBeautyView();
        } else if (mBeautyOptionsPosition == 4) {
            resetMakeup(true, false);
            clearFullBeautyView();
            EffectInfoDataHelper.getInstance().setZeroMakeup();
        } else if (mBeautyOptionsPosition == 5) {
            resetFilterView(false);
            //mFilterStrength = 0;
            mCameraDisplay.setFilterStrength(mFilterStrength);
            clearFullBeautyView();
        } else {
            setBeautyZero(mBeautyOptionsPosition);
            setBeautyListsZero(mBeautyOptionsPosition);
            mBeautyItemAdapters.get(mBeautyOption.get(mBeautyOptionsPosition)).notifyDataSetChanged();
            if (checkMicroType(mBeautyOptionsPosition)) {
                mIndicatorSeekbar.getSeekBar().setProgress(STUtils.convertToData(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(mBeautyOptionSelectedIndex.get(mBeautyOptionsPosition)).getProgress()));
            } else {
                mIndicatorSeekbar.getSeekBar().setProgress(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(mBeautyOptionSelectedIndex.get(mBeautyOptionsPosition)).getProgress());
            }
            mIndicatorSeekbar.updateTextView(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(mBeautyOptionSelectedIndex.get(mBeautyOptionsPosition)).getProgress());

            if (!checkNeedBeauty()) {
                mCameraDisplay.enableBeautify(false);
            }
        }
    }

    private void onClickMakeupAll() {
        clickMakeupGroup("makeup_all", Constants.ST_MAKEUP_ALL);
    }

    private void onClickReset() {
        if (mBeautyOptionsPosition == 0) {
            resetFullBeautyView();
            mIndicatorSeekbar.setVisibility(View.VISIBLE);
            gpFullBeautySeekBar.setVisibility(View.VISIBLE);
        } else if (mBeautyOptionsPosition == 4) {
            mCurrentFullBeautyIndex = -1;
            lastIndex = -1;
            resetMakeup(true, false);
            setDefaultMakeup();
            mBeautyFullAdapter.setSelectedPosition(mCurrentFullBeautyIndex);
            mBeautyFullAdapter.notifyDataSetChanged();
            isFromUserSeekBar = true;

            mMakeupGroupRelativeLayout.setVisibility(View.VISIBLE);
            mMakeupIconsRelativeLayout.setVisibility(View.INVISIBLE);
            mFilterStrengthLayout.setVisibility(View.INVISIBLE);
        } else if (mBeautyOptionsPosition == 5) {
            setDefaultFilter();
            lastIndex = -1;
            mFilterStrengthBar.setProgress(100);
            mFilterStrength = 100;
            mBeautyFullAdapter.setSelectedPosition(mCurrentFullBeautyIndex);
            mBeautyFullAdapter.notifyDataSetChanged();
            mCurrentFullBeautyIndex = -1;
            isFromUserSeekBar = false;
        } else {
            resetSetBeautyParam(mBeautyOptionsPosition);
            resetBeautyLists(mBeautyOptionsPosition);
            mBeautyItemAdapters.get(mBeautyOption.get(mBeautyOptionsPosition)).notifyDataSetChanged();
            if (checkMicroType(mBeautyOptionsPosition)) {
                mIndicatorSeekbar.getSeekBar().setProgress(STUtils.convertToData(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(mBeautyOptionSelectedIndex.get(mBeautyOptionsPosition)).getProgress()));
            } else {
                mIndicatorSeekbar.getSeekBar().setProgress(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(mBeautyOptionSelectedIndex.get(mBeautyOptionsPosition)).getProgress());
            }
            mIndicatorSeekbar.updateTextView(mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(mBeautyOptionSelectedIndex.get(mBeautyOptionsPosition)).getProgress());

            //clearFullBeautyView();
        }
        if (mBeautyOptionsPosition == 0) {
            mBeautyFullItem.get(0).setFilterProgress(85);
            mBeautyFullItem.get(0).setMakeupAllProgress(85);
            mIndicatorSeekbar.getSeekBar().setProgress(85);
            mIndicatorSeekbar.updateTextView(85);
            mIndicatorSeekbarNew.getSeekBar().setProgress(85);
            mIndicatorSeekbarNew.updateTextView(85);
        }
        isFromUserSeekBar = false;
    }

    public void setDefaultFilter() {
        resetFilterView(false);
        if (mFilterLists.get("filter_portrait").size() > 0) {
            for (int i = 0; i < mFilterLists.get("filter_portrait").size(); i++) {
                if (mFilterLists.get("filter_portrait").get(i).name.equals("nvshen")) {
                    mCurrentFilterIndex = i;
                }
            }

            if (mCurrentFilterIndex > 0) {
                mCurrentFilterGroupIndex = 0;
                mFilterAdapters.get("filter_portrait").setSelectedPosition(mCurrentFilterIndex);
                mCameraDisplay.setFilterStyle("filter_portrait", mFilterLists.get("filter_portrait").get(mCurrentFilterIndex).name, mFilterLists.get("filter_portrait").get(mCurrentFilterIndex).model);
                mCameraDisplay.enableFilter(true);

                setFilterViewSelected(mCurrentFilterGroupIndex);
                mFilterAdapters.get("filter_portrait").notifyDataSetChanged();
            }
        }
    }

    public void setDefaultMakeup() {
        if (mBeautyFullItem.get(0).getMakeupList() != null) {
            for (int i = 0; i < mBeautyFullItem.get(0).getMakeupList().size(); i++) {
                String type = mBeautyFullItem.get(0).getMakeupList().get(i).getType();
                String name = mBeautyFullItem.get(0).getMakeupList().get(i).getMakeupName();
                int strength = mBeautyFullItem.get(0).getMakeupList().get(i).getStrength();
                int position = getMakeupIndexByName(type, name);

                setMakeupWithType(type, strength, position);
            }
            mDefaultMakeupStrength.clear();
            mDefaultMakeupStrength.putAll(mMakeupStrength);
        }
    }

//    private void clickMakeUpItem(int position, MakeupItem item, SenseArMaterial material, MakeupAdapter mMakeupAdapter, String groupName) {
//
//        EffectInfoDataHelper.getInstance().filterStrength = mDefaultFilterStrength / 100f;
//        if (position == 0) {
//            lastIndex = -1;
//            EffectInfoDataHelper.getInstance().fullBeautyName = "";
//            mMakeupAdapter.setSelectedPosition(position);
//            mMakeupOptionSelectedIndex.put(mMakeupOptionIndex.get(groupName), position);
//
//            mFilterStrengthLayout.setVisibility(View.INVISIBLE);
//            mCameraDisplay.removeMakeupByType(mCurrentMakeupGroupIndex);
//            updateMakeupOptions(mCurrentMakeupGroupIndex, false);
//        } else if (position == mMakeupOptionSelectedIndex.get(mMakeupOptionIndex.get(groupName))) {
//            mMakeupAdapter.setSelectedPosition(0);
//            mMakeupOptionSelectedIndex.put(mMakeupOptionIndex.get(groupName), 0);
//
//            mFilterStrengthLayout.setVisibility(View.INVISIBLE);
//            mCameraDisplay.removeMakeupByType(mCurrentMakeupGroupIndex);
//            updateMakeupOptions(mCurrentMakeupGroupIndex, false);
//        } else {
//            if (mCurrentMakeupGroupIndex == Constants.ST_MAKEUP_ALL) {
//                resetMakeup(false, false);
//            }
//            mMakeupAdapter.setSelectedPosition(position);
//            mMakeupOptionSelectedIndex.put(mMakeupOptionIndex.get(groupName), position);
//
//            mCameraDisplay.setMakeupForType(mCurrentMakeupGroupIndex, mMakeupLists.get(getMakeupNameOfType(mCurrentMakeupGroupIndex)).get(position).path);
//            mCameraDisplay.setStrengthForType(mCurrentMakeupGroupIndex, (float) mMakeupStrength.get(mCurrentMakeupGroupIndex) / 100.f);
//            mFilterStrengthLayout.setVisibility(View.VISIBLE);
//            mFilterStrengthBar.setProgress(mMakeupStrength.get(mCurrentMakeupGroupIndex));
//            mFilterStrengthText.setText(mMakeupStrength.get(mCurrentMakeupGroupIndex) + "");
//            updateMakeupOptions(mCurrentMakeupGroupIndex, true);
//        }
//
//        if (checkMakeUpSelect()) {
//            mCameraDisplay.enableMakeUp(true);
//        } else {
//            mCameraDisplay.enableMakeUp(false);
//        }
//        mMakeupAdapter.notifyDataSetChanged();
//
//        clearFullBeautyView();
//    }

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
//                        SenseArMaterialService.shareInstance().downloadMaterial(CameraActivity.this, sarm, new SenseArMaterialService.DownloadMaterialListener() {
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

    private void resetSetBeautyParam(int beautyOptionsPosition) {
        switch (beautyOptionsPosition) {
            case 1:
                for (int i = 0; i < mNewBeautifyParamsTypeBase.length; i++) {
                    mCameraDisplay.setBeautyParam(STEffectBeautyType.EFFECT_BEAUTY_BASE_WHITTEN + i, (mNewBeautifyParamsTypeBase[i]));
                }
                break;
            case 2:
                for (int i = 0; i < mNewBeautifyParamsTypeProfessional.length; i++) {
                    mCameraDisplay.setBeautyParam(STEffectBeautyType.EFFECT_BEAUTY_RESHAPE_SHRINK_FACE + i, (mNewBeautifyParamsTypeProfessional[i]));
                }
                break;
            case 3:
                for (int i = 0; i < mNewBeautifyParamsTypeMicro.length; i++) {
                    mCameraDisplay.setBeautyParam(STEffectBeautyType.EFFECT_BEAUTY_PLASTIC_THINNER_HEAD + i, (mNewBeautifyParamsTypeMicro[i]));
                }
                break;
            case 6:
                for (int i = 0; i < mNewBeautifyParamsTypeAdjust.length; i++) {
                    mCameraDisplay.setBeautyParam(STEffectBeautyType.EFFECT_BEAUTY_TONE_CONTRAST + i, (mNewBeautifyParamsTypeAdjust[i]));
                }
                break;
        }
    }

    private void setBeautyZero(int beautyOptionsPosition) {
        switch (beautyOptionsPosition) {
            case 1:
                for (int i = 0; i < mNewBeautifyParamsTypeBase.length; i++) {
                    mCameraDisplay.setBeautyParam(STEffectBeautyType.EFFECT_BEAUTY_BASE_WHITTEN + i, 0f);
                }
                EffectInfoDataHelper.getInstance().setZeroBaseParams();
                break;
            case 2:
                for (int i = 0; i < mNewBeautifyParamsTypeProfessional.length; i++) {
                    mCameraDisplay.setBeautyParam(STEffectBeautyType.EFFECT_BEAUTY_RESHAPE_SHRINK_FACE + i, 0f);
                }
                EffectInfoDataHelper.getInstance().setZeroProfessionalParams();
                break;
            case 3:
                for (int i = 0; i < mNewBeautifyParamsTypeMicro.length; i++) {
                    mCameraDisplay.setBeautyParam(STEffectBeautyType.EFFECT_BEAUTY_PLASTIC_THINNER_HEAD + i, 0f);
                }
                EffectInfoDataHelper.getInstance().setZeroMicroParams();
                break;
            case 6:
                for (int i = 0; i < mNewBeautifyParamsTypeAdjust.length; i++) {
                    mCameraDisplay.setBeautyParam(STEffectBeautyType.EFFECT_BEAUTY_TONE_CONTRAST + i, 0f);
                }
                EffectInfoDataHelper.getInstance().setZeroAdjustParams();
                break;
        }
    }

    private void setMutex(int subItemSelectedIndex, int[] mutex) {
        if (Arrays.binarySearch(mutex, subItemSelectedIndex) >= 0) {
            for (int i : mutex) {
                if (i != subItemSelectedIndex) {
                    mBeautylists.get(mBeautyOption.get(mBeautyOptionsPosition)).get(i).setProgress(0);
                    mBeautyItemAdapters.get(mBeautyOption.get(mBeautyOptionsPosition)).notifyItemChanged(i);
                    mCameraDisplay.setMutex(i);
                }
            }
        }
    }

    private void resetBeautyLists(int beautyOptionsPosition) {
        switch (beautyOptionsPosition) {
            case 1:
                for (int i = 0; i < mNewBeautifyParamsTypeBase.length; i++) {
                    mBeautylists.get(mBeautyOption.get(beautyOptionsPosition)).get(i).setProgress((int) (mNewBeautifyParamsTypeBase[i] * 100));
                }
                break;
            case 2:
                for (int i = 0; i < mNewBeautifyParamsTypeProfessional.length; i++) {
                    mBeautylists.get(mBeautyOption.get(beautyOptionsPosition)).get(i).setProgress((int) (mNewBeautifyParamsTypeProfessional[i] * 100));
                }
                break;
            case 3:
                for (int i = 0; i < mNewBeautifyParamsTypeMicro.length; i++) {
                    mBeautylists.get(mBeautyOption.get(beautyOptionsPosition)).get(i).setProgress((int) (mNewBeautifyParamsTypeMicro[i] * 100));
                }
                break;
            case 6:
                for (int i = 0; i < mNewBeautifyParamsTypeAdjust.length; i++) {
                    mBeautylists.get(mBeautyOption.get(beautyOptionsPosition)).get(i).setProgress((int) (mNewBeautifyParamsTypeAdjust[i] * 100));
                }
                break;
        }
    }

    private void setBeautyListsZero(int beautyOptionsPosition) {
        switch (beautyOptionsPosition) {
            case 1:
                for (int i = 0; i < mNewBeautifyParamsTypeBase.length; i++) {
                    mBeautylists.get(mBeautyOption.get(beautyOptionsPosition)).get(i).setProgress(0);
                }
                break;
            case 2:
                for (int i = 0; i < mNewBeautifyParamsTypeProfessional.length; i++) {
                    mBeautylists.get(mBeautyOption.get(beautyOptionsPosition)).get(i).setProgress(0);
                }
                break;
            case 3:
                for (int i = 0; i < mNewBeautifyParamsTypeMicro.length; i++) {
                    mBeautylists.get(mBeautyOption.get(beautyOptionsPosition)).get(i).setProgress(0);
                }
                break;
            case 6:
                for (int i = 0; i < mNewBeautifyParamsTypeAdjust.length; i++) {
                    mBeautylists.get(mBeautyOption.get(beautyOptionsPosition)).get(i).setProgress(0);
                }
                break;
        }
    }

    private void calculateBeautyIndex(int beautyOptionPosition, int selectPosition) {
        switch (beautyOptionPosition) {
            case 1:
                mCurrentBeautyIndex = selectPosition + STEffectBeautyType.EFFECT_BEAUTY_BASE_WHITTEN;
                break;
            case 2:
                mCurrentBeautyIndex = selectPosition + STEffectBeautyType.EFFECT_BEAUTY_RESHAPE_SHRINK_FACE;
                break;
            case 3:
                mCurrentBeautyIndex = selectPosition + STEffectBeautyType.EFFECT_BEAUTY_PLASTIC_THINNER_HEAD;
                break;
            case 6:
                mCurrentBeautyIndex = selectPosition + STEffectBeautyType.EFFECT_BEAUTY_TONE_CONTRAST;
                break;
        }
    }

    private void perFormSetMeteringArea(float touchX, float touchY) {
        performVerticalSeekBarVisiable(true);
        mCameraDisplay.setMeteringArea(touchX, touchY);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mMeteringArea.getLayoutParams();
        params.setMargins((int) touchX - 50, (int) touchY - 50, 0, 0);
        mMeteringArea.setLayoutParams(params);
        mMeteringArea.setVisibility(View.VISIBLE);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animX = ObjectAnimator.ofFloat(mMeteringArea, "scaleX", 1.5f, 1.2f);
        ObjectAnimator animY = ObjectAnimator.ofFloat(mMeteringArea, "scaleY", 1.5f, 1.2f);
        animatorSet.setDuration(500);
        animatorSet.play(animX).with(animY);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mMeteringArea.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void performVerticalSeekBarVisiable(boolean isVisiable) {
        if (isVisiable) {
            mHandler.removeMessages(MSG_HIDE_VERTICALSEEKBAR);
            mHandler.sendEmptyMessageDelayed(MSG_HIDE_VERTICALSEEKBAR, 2000);
            mVerticalSeekBar.setVisibility(View.VISIBLE);
        } else {
            mVerticalSeekBar.setVisibility(View.GONE);
        }
    }

    private boolean isSlide(int downX, int downY, int upX, int upY) {
        if (Math.abs(upX - downX) > 25 || Math.abs(upY - downY) > 25) {
            return true;
        }
        return false;
    }

    private static float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void resetStickerAdapter() {

        if (mCurrentStickerPosition != -1) {
            mCameraDisplay.removeAllStickers();
            mCurrentStickerPosition = -1;
            mLastStickerPosition = -1;
        }

        //重置所有状态为为选中状态
        for (StickerOptionsItem optionsItem : mStickerOptionsList) {
            if (optionsItem.name.equals("sticker_new_engine")) {
                continue;
            } else if (optionsItem.name.equals("sticker_add_package")) {
                continue;
            } else if (optionsItem.name.equals("object_track")) {
                continue;
            } else {
                if (mStickerAdapters.get(optionsItem.name) != null) {
                    mStickerAdapters.get(optionsItem.name).setSelectedPosition(-1);
                    mStickerAdapters.get(optionsItem.name).notifyDataSetChanged();
                }
            }
        }
    }

    private void resetNewStickerAdapter() {
        mCameraDisplay.removeAllStickers();
        if (mStickerPackageMap != null) {
            mStickerPackageMap.clear();
        }

        if (mNativeStickerAdapters.get("sticker_new_engine") != null) {
            mNativeStickerAdapters.get("sticker_new_engine").setSelectedPosition(-1);
            mNativeStickerAdapters.get("sticker_new_engine").notifyDataSetChanged();
        }
        if (mNativeStickerAdapters.get("sticker_add_package") != null) {
            mNativeStickerAdapters.get("sticker_add_package").setSelectedPosition(-1);
            mNativeStickerAdapters.get("sticker_add_package").notifyDataSetChanged();
        }

    }

    private void startShowCpuInfo() {
        mNeedStopCpuRate = false;
        mCpuInofThread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (!mNeedStopCpuRate) {
                    final String cpuRate;
                    if (Build.VERSION.SDK_INT <= 25) {
                        cpuRate = String.valueOf(getProcessCpuRate());
                    } else {
                        cpuRate = "null";
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView) findViewById(R.id.tv_cpu_radio)).setText(String.valueOf(cpuRate));
                            if (mCameraDisplay != null) {
                                ((TextView) findViewById(R.id.tv_frame_radio)).setText(String.valueOf(mCameraDisplay.getFrameCost()));
                                ((TextView) findViewById(R.id.tv_fps_info)).setText(mCameraDisplay.getFpsInfo() + "");

                                showFaceAttributeInfo();
                            }
                        }
                    });

                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        mCpuInofThread.start();
    }

    private void stopShowCpuInfo() {
        if (mCpuInofThread != null) {
            mNeedStopCpuRate = true;
            mCpuInofThread.interrupt();
            //mCpuInofThread.stop();
            mCpuInofThread = null;
        }
    }

    private void showActiveTips(long actionNum) {
        if (actionNum != -1 && actionNum != 0) {
            mTipsLayout.setVisibility(View.VISIBLE);
        }

        String triggerTips = "";
        mTipsImageView.setImageDrawable(null);

        if ((actionNum & STMobileHumanActionNative.ST_MOBILE_EYE_BLINK) > 0) {
            mTipsImageView.setImageResource(R.drawable.ic_trigger_blink);
            triggerTips = triggerTips + MultiLanguageUtils.getStr(R.string.action_tip_blink);
        }
        if ((actionNum & STMobileHumanActionNative.ST_MOBILE_MOUTH_AH) > 0) {
            mTipsImageView.setImageResource(R.drawable.ic_trigger_mouth);
            triggerTips = triggerTips + MultiLanguageUtils.getStr(R.string.action_tip_mouth);
        }
        if ((actionNum & STMobileHumanActionNative.ST_MOBILE_HEAD_YAW) > 0) {
            mTipsImageView.setImageResource(R.drawable.ic_trigger_shake);
            triggerTips = triggerTips + MultiLanguageUtils.getStr(R.string.action_tip_head);
        }
        if ((actionNum & STMobileHumanActionNative.ST_MOBILE_HEAD_PITCH) > 0) {
            mTipsImageView.setImageResource(R.drawable.ic_trigger_nod);
            triggerTips = triggerTips + MultiLanguageUtils.getStr(R.string.action_tip_put_nod);
        }
        if ((actionNum & STMobileHumanActionNative.ST_MOBILE_BROW_JUMP) > 0) {
            mTipsImageView.setImageResource(R.drawable.ic_trigger_frown);
            triggerTips = triggerTips + MultiLanguageUtils.getStr(R.string.action_tip_jump);
        }

        if ((actionNum & STMobileHumanActionNative.ST_MOBILE_FACE_LIPS_UPWARD) > 0) {
            mTipsImageView.setImageResource(R.drawable.ic_trigger_lips_upward);
            triggerTips = triggerTips + MultiLanguageUtils.getStr(R.string.action_tip_lips_upward);
        }
        if ((actionNum & STMobileHumanActionNative.ST_MOBILE_FACE_LIPS_POUTED) > 0) {
            mTipsImageView.setImageResource(R.drawable.ic_trigger_lips_pouted);
            triggerTips = triggerTips + MultiLanguageUtils.getStr(R.string.action_tip_put_mouth);
        }

        if ((actionNum & STMobileHumanActionNative.ST_MOBILE_HAND_PALM) > 0) {
            mTipsImageView.setImageResource(R.drawable.ic_trigger_palm_selected);
            triggerTips = triggerTips + MultiLanguageUtils.getStr(R.string.action_tip_palm);
        }
        if ((actionNum & STMobileHumanActionNative.ST_MOBILE_HAND_LOVE) > 0) {
            mTipsImageView.setImageResource(R.drawable.ic_trigger_heart_hand_selected);
            triggerTips = triggerTips + MultiLanguageUtils.getStr(R.string.action_tip_heart);
        }
        if ((actionNum & STMobileHumanActionNative.ST_MOBILE_HAND_HOLDUP) > 0) {
            mTipsImageView.setImageResource(R.drawable.ic_trigger_palm_up_selected);
            triggerTips = triggerTips + MultiLanguageUtils.getStr(R.string.action_tip_hands);
        }
        if ((actionNum & STMobileHumanActionNative.ST_MOBILE_HAND_CONGRATULATE) > 0) {
            mTipsImageView.setImageResource(R.drawable.ic_trigger_congratulate_selected);
            triggerTips = triggerTips + MultiLanguageUtils.getStr(R.string.action_tip_hold_a_fist);
        }
        if ((actionNum & STMobileHumanActionNative.ST_MOBILE_HAND_FINGER_HEART) > 0) {
            mTipsImageView.setImageResource(R.drawable.ic_trigger_finger_heart_selected);
            triggerTips = triggerTips + MultiLanguageUtils.getStr(R.string.action_tip_put_one_hand);
        }
        if ((actionNum & STMobileHumanActionNative.ST_MOBILE_HAND_GOOD) > 0) {
            mTipsImageView.setImageResource(R.drawable.ic_trigger_thumb_selected);
            triggerTips = triggerTips + MultiLanguageUtils.getStr(R.string.action_tip_thumb);
        }
        if ((actionNum & STMobileHumanActionNative.ST_MOBILE_HAND_OK) > 0) {
            mTipsImageView.setImageResource(R.drawable.ic_trigger_ok_selected);
            triggerTips = triggerTips + MultiLanguageUtils.getStr(R.string.action_tip_ok);
        }
        if ((actionNum & STMobileHumanActionNative.ST_MOBILE_HAND_SCISSOR) > 0) {
            mTipsImageView.setImageResource(R.drawable.ic_trigger_scissor_selected);
            triggerTips = triggerTips + MultiLanguageUtils.getStr(R.string.action_tip_scissor);
        }
        if ((actionNum & STMobileHumanActionNative.ST_MOBILE_HAND_PISTOL) > 0) {
            mTipsImageView.setImageResource(R.drawable.ic_trigger_pistol_selected);
            triggerTips = triggerTips + MultiLanguageUtils.getStr(R.string.action_tip_pistol);
        }
        if ((actionNum & STMobileHumanActionNative.ST_MOBILE_HAND_FINGER_INDEX) > 0) {
            mTipsImageView.setImageResource(R.drawable.ic_trigger_one_finger_selected);
            triggerTips = triggerTips + MultiLanguageUtils.getStr(R.string.action_tip_finger);
        }
        if ((actionNum & STMobileHumanActionNative.ST_MOBILE_HAND_FIST) > 0) {
            mTipsImageView.setImageResource(R.drawable.ic_trigger_first_selected);
            triggerTips = triggerTips + MultiLanguageUtils.getStr(R.string.action_tip_raise);
        }
        if ((actionNum & STMobileHumanActionNative.ST_MOBILE_HAND_666) > 0) {
            mTipsImageView.setImageResource(R.drawable.ic_trigger_sixsixsix_selected);
            triggerTips = triggerTips + MultiLanguageUtils.getStr(R.string.action_tip_666);
        }
        if ((actionNum & STMobileHumanActionNative.ST_MOBILE_HAND_BLESS) > 0) {
            mTipsImageView.setImageResource(R.drawable.ic_trigger_handbless_selected);
            triggerTips = triggerTips + MultiLanguageUtils.getStr(R.string.action_tip_bless);
        }
        if ((actionNum & STMobileHumanActionNative.ST_MOBILE_HAND_ILOVEYOU) > 0) {
            mTipsImageView.setImageResource(R.drawable.ic_trigger_love_selected);
            triggerTips = triggerTips + MultiLanguageUtils.getStr(R.string.action_tip_love);
        }
        mTipsTextView.setText(triggerTips);

        mTipsLayout.setVisibility(View.VISIBLE);
        if (mTipsRunnable != null) {
            mTipsHandler.removeCallbacks(mTipsRunnable);
        }

        mTipsRunnable = new Runnable() {
            @Override
            public void run() {
                mTipsLayout.setVisibility(View.GONE);
            }
        };

        mTipsHandler.postDelayed(mTipsRunnable, 2000);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.reset_zero) {
            onClickZero();
        } else if (id == R.id.ll_makeup_group_all) {
            onClickMakeupAll();
        } else if (id == R.id.reset) {// 重置
            onClickReset();
        } else if (id == R.id.rv_close_sticker) {// 取消贴纸
            if (!mCameraDisplay.stickerMapIsEmpty()) {
                recoverUI();
            }
            //重置所有状态为未选中状态
            resetStickerAdapter();
            resetNewStickerAdapter();
            mCurrentStickerPosition = -1;
            mLastStickerPosition = -1;
            mCameraDisplay.removeAllStickers();
            mCameraDisplay.enableSticker(false);

            mCurrentObjectIndex = -1;
            mObjectAdapter.setSelectedPosition(-1);
            mObjectAdapter.notifyDataSetChanged();
            mCameraDisplay.enableObject(false);

            iv_close_sticker.setBackground(getResources().getDrawable(R.drawable.close_sticker_selected));
        } else if (id == R.id.ll_sticker_options_switch) {
            saveEffectSettings();
            mStickerOptionsSwitch.setVisibility(View.INVISIBLE);
            mBeautyOptionsSwitch.setVisibility(View.INVISIBLE);
            mSelectOptions.setBackgroundColor(Color.parseColor("#80000000"));
            mIndicatorSeekbar.setVisibility(View.INVISIBLE);
            mStickerOptions.setVisibility(View.VISIBLE);
            mStickerIcons.setVisibility(View.VISIBLE);
            mIsStickerOptionsOpen = true;
            mShowOriginBtn1.setVisibility(View.INVISIBLE);
            mShowOriginBtn2.setVisibility(View.VISIBLE);
            mShowOriginBtn3.setVisibility(View.INVISIBLE);
            mFilterGroupsLinearLayout.setVisibility(View.INVISIBLE);
            mFilterIconsRelativeLayout.setVisibility(View.INVISIBLE);
            mFilterStrengthLayout.setVisibility(View.INVISIBLE);
            mFilterAndBeautyOptionView.setVisibility(View.INVISIBLE);
            mBaseBeautyOptions.setVisibility(View.INVISIBLE);
            mResetTextView.setVisibility(View.INVISIBLE);
            mResetZeroTextView.setVisibility(View.INVISIBLE);
            mIsBeautyOptionsOpen = false;
            mSettingOptions.setVisibility(View.INVISIBLE);
            mIsSettingOptionsOpen = false;
        } else if (id == R.id.ll_beauty_options_switch) {
            if (/*mCurrentStickerPosition!=-1 || */!mCameraDisplay.stickerMapIsEmpty() && mCameraDisplay.overlappedBeautyCountChanged()) {
                mCameraDisplay.updateBeautyParamsUI();
            }
            mStickerOptionsSwitch.setVisibility(View.INVISIBLE);
            mBeautyOptionsSwitch.setVisibility(View.INVISIBLE);
            mSelectOptions.setBackgroundColor(Color.parseColor("#80000000"));
            mBaseBeautyOptions.setVisibility(View.VISIBLE);
            mIndicatorSeekbar.setVisibility(View.VISIBLE);
            if (mBeautyOptionsPosition == 0) {
                if (mCurrentFullBeautyIndex > -1) {
                    mIndicatorSeekbar.setVisibility(View.VISIBLE);
                    gpFullBeautySeekBar.setVisibility(View.GONE);
                    gpFullBeautySeekBar.setVisibility(View.VISIBLE);
                }
                if (mCurrentFullBeautyIndex == -1) {
                    gpFullBeautySeekBar.setVisibility(View.INVISIBLE);
                    mIndicatorSeekbar.setVisibility(View.INVISIBLE);
                }
            } else if (mBeautyOptionsPosition == 4) {
                mBaseBeautyOptions.setVisibility(View.INVISIBLE);
                mMakeupGroupRelativeLayout.setVisibility(View.VISIBLE);
                mMakeupIconsRelativeLayout.setVisibility(View.INVISIBLE);
                mFilterStrengthLayout.setVisibility(View.INVISIBLE);
                mIndicatorSeekbar.setVisibility(View.INVISIBLE);
                gpFullBeautySeekBar.setVisibility(View.INVISIBLE);
            } else if (mBeautyOptionsPosition == 5) {
                mBaseBeautyOptions.setVisibility(View.INVISIBLE);
                mFilterGroupsLinearLayout.setVisibility(View.VISIBLE);
                mFilterIconsRelativeLayout.setVisibility(View.INVISIBLE);
                mFilterStrengthLayout.setVisibility(View.INVISIBLE);
                mIndicatorSeekbar.setVisibility(View.INVISIBLE);
                gpFullBeautySeekBar.setVisibility(View.INVISIBLE);
            } else {
                gpFullBeautySeekBar.setVisibility(View.INVISIBLE);
            }
            mFilterAndBeautyOptionView.setVisibility(View.VISIBLE);
            mIsBeautyOptionsOpen = true;
            mShowOriginBtn1.setVisibility(View.INVISIBLE);
            mShowOriginBtn2.setVisibility(View.INVISIBLE);
            mShowOriginBtn3.setVisibility(View.VISIBLE);
            mResetTextView.setVisibility(View.VISIBLE);
            mResetZeroTextView.setVisibility(View.VISIBLE);
            mIsStickerOptionsOpen = false;
            mSettingOptions.setVisibility(View.INVISIBLE);
            mIsSettingOptionsOpen = false;
        } else if (id == R.id.iv_setting_options_switch) {
            gpFullBeautySeekBar.setVisibility(View.GONE);
            mSelectOptions.setBackgroundColor(Color.parseColor("#80000000"));
            mStickerOptionsSwitch.setVisibility(View.VISIBLE);
            mBeautyOptionsSwitch.setVisibility(View.VISIBLE);
            if (mIsSettingOptionsOpen) {
                mSelectOptions.setBackgroundColor(Color.parseColor("#00000000"));
                mSettingOptions.setVisibility(View.INVISIBLE);
                mIsSettingOptionsOpen = false;
                mIndicatorSeekbar.setVisibility(View.INVISIBLE);
                mShowOriginBtn1.setVisibility(View.VISIBLE);
                mShowOriginBtn2.setVisibility(View.INVISIBLE);
                mShowOriginBtn3.setVisibility(View.INVISIBLE);
            } else {
                mSettingOptions.setVisibility(View.VISIBLE);
                mIsSettingOptionsOpen = true;

                mShowOriginBtn1.setVisibility(View.INVISIBLE);
                mShowOriginBtn2.setVisibility(View.VISIBLE);
                mShowOriginBtn3.setVisibility(View.INVISIBLE);

                mStickerOptions.setVisibility(View.INVISIBLE);
                mStickerIcons.setVisibility(View.INVISIBLE);
            }

            mStickerOptionsSwitchIcon = (ImageView) findViewById(R.id.iv_sticker_options_switch);
            mBeautyOptionsSwitchIcon = (ImageView) findViewById(R.id.iv_beauty_options_switch);
            mStickerOptionsSwitchText = (TextView) findViewById(R.id.tv_sticker_options_switch);
            mBeautyOptionsSwitchText = (TextView) findViewById(R.id.tv_beauty_options_switch);

            mStickerOptionsSwitchIcon.setImageDrawable(getResources().getDrawable(R.drawable.sticker));
            mStickerOptionsSwitchText.setTextColor(Color.parseColor("#ffffff"));
            mIsStickerOptionsOpen = false;

            mFilterGroupsLinearLayout.setVisibility(View.INVISIBLE);
            mFilterIconsRelativeLayout.setVisibility(View.INVISIBLE);
            mFilterStrengthLayout.setVisibility(View.INVISIBLE);
            mFilterAndBeautyOptionView.setVisibility(View.INVISIBLE);
            mMakeupGroupRelativeLayout.setVisibility(View.INVISIBLE);
            mMakeupIconsRelativeLayout.setVisibility(View.INVISIBLE);
            mBaseBeautyOptions.setVisibility(View.INVISIBLE);
            mIndicatorSeekbar.setVisibility(View.INVISIBLE);
            mResetTextView.setVisibility(View.INVISIBLE);
            mResetZeroTextView.setVisibility(View.INVISIBLE);
            mBeautyOptionsSwitchIcon.setImageDrawable(getResources().getDrawable(R.drawable.beauty));
            mBeautyOptionsSwitchText.setTextColor(Color.parseColor("#ffffff"));
            mIsBeautyOptionsOpen = false;
        } else if (id == R.id.id_gl_sv) {
            mSelectOptions.setBackgroundColor(Color.parseColor("#00000000"));
            mStickerOptions.setVisibility(View.INVISIBLE);
            mStickerIcons.setVisibility(View.INVISIBLE);

            mStickerOptionsSwitchIcon = (ImageView) findViewById(R.id.iv_sticker_options_switch);
            mBeautyOptionsSwitchIcon = (ImageView) findViewById(R.id.iv_beauty_options_switch);
            mStickerOptionsSwitchText = (TextView) findViewById(R.id.tv_sticker_options_switch);
            mBeautyOptionsSwitchText = (TextView) findViewById(R.id.tv_beauty_options_switch);

            mStickerOptionsSwitchIcon.setImageDrawable(getResources().getDrawable(R.drawable.sticker));
            mStickerOptionsSwitchText.setTextColor(Color.parseColor("#ffffff"));
            mIsStickerOptionsOpen = false;

            mFilterGroupsLinearLayout.setVisibility(View.INVISIBLE);
            mFilterIconsRelativeLayout.setVisibility(View.INVISIBLE);
            mFilterStrengthLayout.setVisibility(View.INVISIBLE);
            mFilterAndBeautyOptionView.setVisibility(View.INVISIBLE);
            mBaseBeautyOptions.setVisibility(View.INVISIBLE);
            mBeautyOptionsSwitchIcon.setImageDrawable(getResources().getDrawable(R.drawable.beauty));
            mBeautyOptionsSwitchText.setTextColor(Color.parseColor("#ffffff"));
            mIsBeautyOptionsOpen = false;

            mSettingOptions.setVisibility(View.INVISIBLE);
            mIsSettingOptionsOpen = false;

            mShowOriginBtn1.setVisibility(View.VISIBLE);
            mShowOriginBtn2.setVisibility(View.INVISIBLE);
            mShowOriginBtn3.setVisibility(View.INVISIBLE);
        } else if (id == R.id.tv_change_camera) {
            if (mCameraDisplay != null) {
                mCameraDisplay.switchCamera();
            }
        } else if (id == R.id.tv_cancel) {// back to welcome page
        } else if (id == R.id.tv_show_origin1) {
            if (origin) {
                RCRTCBeautyOption option = RCRTCBeautyEngineImpl.getInstance().getCurrentBeautyOption();
                option.setWhitenessLevel(0);
                option.setRuddyLevel(0);
                option.setSmoothLevel(8);
                RCRTCBeautyEngineImpl.getInstance().setBeautyOption(option);
                RCRTCBeautyEngineImpl.getInstance().setBeautyEnable(true);
            } else {
                RCRTCBeautyEngineImpl.getInstance().setBeautyEnable(false);
            }

            origin = !origin;
        }
    }
    private boolean origin = true;


    // 分隔间距 继承RecyclerView.ItemDecoration
    class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (parent.getChildAdapterPosition(view) != 0) {
                outRect.top = space;
            }
        }
    }

    class BeautyItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public BeautyItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.left = space;
            outRect.right = space;
        }
    }

    private ChangePreviewSizeListener mChangePreviewSizeListener = new ChangePreviewSizeListener() {
        public void onChangePreviewSize(final int previewW, final int previewH) {
            CameraActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPreviewFrameLayout.requestLayout();
                }
            });
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("process_killed", true);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        LogUtils.i(TAG, "onResume");
        STSoundPlay.getInstance(this).resumeSound();
        super.onResume();
        EffectInfoDataHelper.setType(EffectInfoDataHelper.Type.CAMERA);
        mAccelerometer.start();
        mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_GAME);

        mCameraDisplay.onResume();
        mCameraDisplay.setShowOriginal(false);

        resetTimer();
        mIsRecording = false;
        startShowCpuInfo();
        mIsPaused = false;
    }

    private boolean mIsPaused = false;

    private void saveEffectSettings() {
        EffectInfoDataHelper.getInstance().fullBeautyMakeupProgress = mIndicatorSeekbarNew.getSeekBar().getProgress();
        EffectInfoDataHelper.getInstance().save();
    }

    @Override
    protected void onPause() {
        super.onPause();
        STSoundPlay.getInstance(this).pauseSound();
        saveEffectSettings();
        LogUtils.i(TAG, "onPause");

        mSensorManager.unregisterListener(this);

        //if is recording, stop recording
        mIsPaused = true;
        if (mIsRecording) {
            mHandler.removeMessages(MSG_STOP_RECORDING);
            stopRecording();
            enableShowLayouts();

            if (mVideoFilePath != null) {
                File file = new File(mVideoFilePath);
                if (file != null) {
                    file.delete();
                }
            }

            resetTimer();
            mIsRecording = false;
        }

        if (!mPermissionDialogShowing) {
            mAccelerometer.stop();
            mCameraDisplay.onPause();
        }
        stopShowCpuInfo();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        mCameraDisplay.onDestroy();
        mStickerAdapters.clear();
        mNativeStickerAdapters.clear();
        mStickerlists.clear();
        mBeautyParamsSeekBarList.clear();
        mFilterAdapters.clear();
        mFilterLists.clear();
        mObjectList.clear();
        mStickerOptionsList.clear();
        mBeautyOptionsList.clear();
        mBeautyFullItem.clear();
        if (mStickerPackageMap != null) {
            mStickerPackageMap.clear();
            mStickerPackageMap = null;
        }

        if (mMediaActionSound != null) {
            mMediaActionSound.release();
            mMediaActionSound = null;
        }
    }

    private float getProcessCpuRate() {
        long totalCpuTime1 = getTotalCpuTime();
        long processCpuTime1 = getAppCpuTime();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long totalCupTime2 = getTotalCpuTime();
        long processCpuTime2 = getAppCpuTime();

        if (totalCpuTime1 != totalCupTime2) {
            float rate = (float) (100 * (processCpuTime2 - processCpuTime1) / (totalCupTime2 - totalCpuTime1));
            if (rate >= 0.0f || rate <= 100.0f) {
                mCurrentCpuRate = rate;
            }
        }

        return mCurrentCpuRate;
    }

    private long getTotalCpuTime() {
        // 获取系统总CPU使用时间
        String[] cpuInfos = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Long.parseLong(cpuInfos[2])
                + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
                + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
                + Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
    }

    private long getAppCpuTime() {
        //获取应用占用的CPU时间
        String[] cpuInfos = null;
        int pid = android.os.Process.myPid();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/" + pid + "/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Long.parseLong(cpuInfos[13])
                + Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
                + Long.parseLong(cpuInfos[16]);

    }

    private void onPictureTaken(ByteBuffer data, File file, int mImageWidth, int mImageHeight) {
        if (mImageWidth <= 0 || mImageHeight <= 0)
            return;
        Bitmap srcBitmap = Bitmap.createBitmap(mImageWidth, mImageHeight, Bitmap.Config.ARGB_8888);
        data.position(0);
        srcBitmap.copyPixelsFromBuffer(data);
        saveToSDCard(file, srcBitmap);
        srcBitmap.recycle();
    }


    private void saveToSDCard(File file, Bitmap bmp) {

        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, bos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (bos != null)
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        if (mHandler != null) {
            String path = file.getAbsolutePath();
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);

            if (Build.VERSION.SDK_INT >= 19) {

                MediaScannerConnection.scanFile(this, new String[]{path}, null, null);
            }

            mHandler.sendEmptyMessage(CameraActivity.MSG_SAVED_IMG);
        }
    }

    private boolean isWritePermissionAllowed() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    private void requestWritePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            mPermissionDialogShowing = true;
            this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_WRITE_PERMISSION);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_WRITE_PERMISSION) {
            mPermissionDialogShowing = false;
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.onClick(findViewById(R.id.tv_capture));
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventAction = event.getAction();
        Rect indexRect = mCameraDisplay.getIndexRect();

        if (mIsStickerOptionsOpen || mIsBeautyOptionsOpen || mIsSettingOptionsOpen) {
            closeTableView();
        }

        if (event.getPointerCount() == 1) {
            switch (eventAction) {
                case MotionEvent.ACTION_DOWN:
                    if ((int) event.getX() >= indexRect.left && (int) event.getX() <= indexRect.right &&
                            (int) event.getY() >= indexRect.top && (int) event.getY() <= indexRect.bottom) {
                        mIndexX = (int) event.getX();
                        mIndexY = (int) event.getY();
                        mCameraDisplay.setIndexRect(mIndexX - indexRect.width() / 2, mIndexY - indexRect.width() / 2, true);
                        mCanMove = true;
                        mCameraDisplay.disableObjectTracking();
                    } else {
                        timeDown = System.currentTimeMillis();
                        downX = (int) event.getX();
                        downY = (int) event.getY();
                    }

                    if (!testboolean) {
                        mCameraDisplay.changeCustomEvent();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mCanMove) {
                        mIndexX = (int) event.getX();
                        mIndexY = (int) event.getY();
                        mCameraDisplay.setIndexRect(mIndexX - indexRect.width() / 2, mIndexY - indexRect.width() / 2, true);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (mCanMove) {
                        mIndexX = (int) event.getX();
                        mIndexY = (int) event.getY();
                        mCameraDisplay.setIndexRect(mIndexX - indexRect.width() / 2, mIndexY - indexRect.width() / 2, false);
                        mCameraDisplay.setObjectTrackRect();

                        mCanMove = false;
                    } else {
                        int upX = (int) event.getX();
                        int upY = (int) event.getY();
                        if (System.currentTimeMillis() - timeDown < 300 && !isSlide(downX, downY, upX, upY)) {
                            perFormSetMeteringArea(upX, upY);
                        }
                    }
            }
        } else {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = getFingerSpacing(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float newDist = getFingerSpacing(event);
                    if (newDist > oldDist) {
                        mCameraDisplay.handleZoom(true);
                    } else if (newDist < oldDist) {
                        mCameraDisplay.handleZoom(false);
                    }
                    oldDist = newDist;
                    break;
            }
        }
        return true;
    }


    private void drawObjectImage(final Rect rect, final boolean needDrawRect) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!mSurfaceViewOverlap.getHolder().getSurface().isValid()) {
                    return;
                }
                Canvas canvas = mSurfaceViewOverlap.getHolder().lockCanvas();
                if (canvas == null)
                    return;

                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                if (needDrawRect) {
                    canvas.drawRect(rect, mPaint);
                }
                canvas.drawBitmap(mGuideBitmap, new Rect(0, 0, mGuideBitmap.getWidth(), mGuideBitmap.getHeight()), rect, mPaint);

                mSurfaceViewOverlap.getHolder().unlockCanvasAndPost(canvas);
            }
        });
    }

    private void clearObjectImage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!mSurfaceViewOverlap.getHolder().getSurface().isValid()) {
                    return;
                }
                Canvas canvas = mSurfaceViewOverlap.getHolder().lockCanvas();
                if (canvas == null)
                    return;

                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                mSurfaceViewOverlap.getHolder().unlockCanvasAndPost(canvas);
            }
        });
    }

    private void drawFaceExtraPoints(final STPoint[] points) {
        if (points == null || points.length == 0) {
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!mSurfaceViewOverlap.getHolder().getSurface().isValid()) {
                    return;
                }
                Canvas canvas = mSurfaceViewOverlap.getHolder().lockCanvas();
                if (canvas == null)
                    return;

                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                STUtils.drawPoints(canvas, mPaint, points);

                mSurfaceViewOverlap.getHolder().unlockCanvasAndPost(canvas);
            }
        });
    }

    private void notifyVideoUpdate(String videoFilePath) {
        if (videoFilePath == null || videoFilePath.length() == 0) {
            return;
        }
        File file = new File(videoFilePath);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        if (Build.VERSION.SDK_INT >= 19) {
            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{videoFilePath}, null, null);
        }
    }

    /**
     * callback methods from encoder
     */
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (encoder instanceof MediaVideoEncoder && mCameraDisplay != null)
                mCameraDisplay.setVideoEncoder((MediaVideoEncoder) encoder);
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            if (encoder instanceof MediaVideoEncoder && mCameraDisplay != null)
                mCameraDisplay.setVideoEncoder(null);
        }
    };

    private MediaMuxerWrapper mMuxer;

    private void startRecording() {
        try {
            mMuxer = new MediaMuxerWrapper(".mp4");    // if you record audio only, ".m4a" is also OK.

            // for video capturing
            new MediaVideoEncoder(mMuxer, mMediaEncoderListener, mCameraDisplay.getPreviewWidth(), mCameraDisplay.getPreviewHeight());

            if (mIsHasAudioPermission) {
                // for audio capturing
                new MediaAudioEncoder(mMuxer, mMediaEncoderListener);
            }

            mMuxer.prepare();
            mMuxer.startRecording();
        } catch (final IOException e) {
            Log.e(TAG, "startCapture:", e);
        }
    }

    private void stopRecording() {
        if (mMuxer != null) {
            mVideoFilePath = mMuxer.getFilePath();
            mMuxer.stopRecording();
            //mMuxer = null;
        }
        System.gc();
    }

    private void updateTimer() {
        String timeInfo;
        mTimeSeconds++;

        if (mTimeSeconds >= 60) {
            mTimeMinutes++;
            mTimeSeconds = 0;
        }

        if (mTimeSeconds < 10 && mTimeMinutes < 10) {
            timeInfo = "00:0" + mTimeMinutes + ":" + "0" + mTimeSeconds;
        } else if (mTimeSeconds < 10 && mTimeMinutes >= 10) {
            timeInfo = "00:" + mTimeMinutes + ":" + "0" + mTimeSeconds;
        } else if (mTimeSeconds >= 10 && mTimeMinutes < 10) {
            timeInfo = "00:0" + mTimeMinutes + ":" + mTimeSeconds;
        } else {
            timeInfo = "00:" + mTimeMinutes + ":" + mTimeSeconds;
        }

        mShowShortVideoTime.setText(timeInfo);
    }

    private void resetTimer() {
        mTimeMinutes = 0;
        mTimeSeconds = 0;
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
        }

        mShowShortVideoTime.setText("00:00:00");
        mShowShortVideoTime.setVisibility(View.INVISIBLE);
    }

    private void closeTableView() {
        gpFullBeautySeekBar.setVisibility(View.GONE);
        mStickerOptionsSwitch.setVisibility(View.VISIBLE);
        mBeautyOptionsSwitch.setVisibility(View.VISIBLE);
        mSelectOptions.setBackgroundColor(Color.parseColor("#00000000"));

        mStickerOptions.setVisibility(View.INVISIBLE);
        mStickerIcons.setVisibility(View.INVISIBLE);

        mStickerOptionsSwitchIcon = (ImageView) findViewById(R.id.iv_sticker_options_switch);
        mBeautyOptionsSwitchIcon = (ImageView) findViewById(R.id.iv_beauty_options_switch);
        mStickerOptionsSwitchText = (TextView) findViewById(R.id.tv_sticker_options_switch);
        mBeautyOptionsSwitchText = (TextView) findViewById(R.id.tv_beauty_options_switch);

        mStickerOptionsSwitchIcon.setImageDrawable(getResources().getDrawable(R.drawable.sticker));
        mStickerOptionsSwitchText.setTextColor(Color.parseColor("#ffffff"));
        mIsStickerOptionsOpen = false;

        mFilterGroupsLinearLayout.setVisibility(View.INVISIBLE);
        mFilterIconsRelativeLayout.setVisibility(View.INVISIBLE);
        mFilterStrengthLayout.setVisibility(View.INVISIBLE);
        mFilterAndBeautyOptionView.setVisibility(View.INVISIBLE);
        mBaseBeautyOptions.setVisibility(View.INVISIBLE);
        mBeautyOptionsSwitchIcon.setImageDrawable(getResources().getDrawable(R.drawable.beauty));
        mBeautyOptionsSwitchText.setTextColor(Color.parseColor("#ffffff"));
        mIsBeautyOptionsOpen = false;
        mIndicatorSeekbar.setVisibility(View.INVISIBLE);
        mSettingOptions.setVisibility(View.INVISIBLE);
        mIsSettingOptionsOpen = false;

        mShowOriginBtn1.setVisibility(View.VISIBLE);
        mShowOriginBtn2.setVisibility(View.INVISIBLE);
        mShowOriginBtn3.setVisibility(View.INVISIBLE);
        mResetTextView.setVisibility(View.INVISIBLE);
        mResetZeroTextView.setVisibility(View.INVISIBLE);

        mMakeupGroupRelativeLayout.setVisibility(View.INVISIBLE);
        mMakeupIconsRelativeLayout.setVisibility(View.INVISIBLE);
    }

    private void disableShowLayouts() {
        mShowOriginBtn1.setVisibility(View.INVISIBLE);

        findViewById(R.id.tv_change_camera).setVisibility(View.INVISIBLE);
        mSettingOptionsSwitch.setVisibility(View.INVISIBLE);

        mBeautyOptionsSwitch.setVisibility(View.INVISIBLE);
        mStickerOptionsSwitch.setVisibility(View.INVISIBLE);
        mSelectionPicture.setVisibility(View.INVISIBLE);
    }

    private void enableShowLayouts() {
        mShowOriginBtn1.setVisibility(View.VISIBLE);

        findViewById(R.id.tv_change_camera).setVisibility(View.VISIBLE);
        mSettingOptionsSwitch.setVisibility(View.VISIBLE);

        mBeautyOptionsSwitch.setVisibility(View.VISIBLE);
        mStickerOptionsSwitch.setVisibility(View.VISIBLE);
        mSelectionPicture.setVisibility(View.VISIBLE);
    }

    private void resetFilterView(boolean onlyResetUI) {
        mCurrentFilterIndex = -1;
        setFilterViewSelected(mCurrentFilterIndex);

        mFilterAdapters.get("filter_portrait").setSelectedPosition(0);
        mFilterAdapters.get("filter_scenery").setSelectedPosition(0);
        mFilterAdapters.get("filter_still_life").setSelectedPosition(0);
        mFilterAdapters.get("filter_food").setSelectedPosition(0);

        if (!onlyResetUI) {
            Log.d(TAG, "resetFilterView: ");
            mCameraDisplay.setFilterStyle(null, null, null);
            mCameraDisplay.enableFilter(false);
        }

        mFilterStrengthLayout.setVisibility(View.INVISIBLE);
    }

    private int mColorBlue = Color.parseColor("#0a8dff");

    private void showHandActionInfo(long action) {

        resetHandActionInfo();

        if ((action & STMobileHumanActionNative.ST_MOBILE_HAND_PALM) > 0) {
            findViewById(R.id.iv_palm).setBackgroundColor(mColorBlue);
            ((ImageView) findViewById(R.id.iv_palm)).setImageDrawable(getResources().getDrawable(R.drawable.ic_trigger_palm_selected));
        } else if ((action & STMobileHumanActionNative.ST_MOBILE_HAND_GOOD) > 0) {
            findViewById(R.id.iv_thumb).setBackgroundColor(mColorBlue);
            ((ImageView) findViewById(R.id.iv_thumb)).setImageDrawable(getResources().getDrawable(R.drawable.ic_trigger_thumb_selected));
        } else if ((action & STMobileHumanActionNative.ST_MOBILE_HAND_OK) > 0) {
            findViewById(R.id.iv_ok).setBackgroundColor(mColorBlue);
            ((ImageView) findViewById(R.id.iv_ok)).setImageDrawable(getResources().getDrawable(R.drawable.ic_trigger_ok_selected));
        } else if ((action & STMobileHumanActionNative.ST_MOBILE_HAND_PISTOL) > 0) {
            findViewById(R.id.iv_pistol).setBackgroundColor(mColorBlue);
            ((ImageView) findViewById(R.id.iv_pistol)).setImageDrawable(getResources().getDrawable(R.drawable.ic_trigger_pistol_selected));
        } else if ((action & STMobileHumanActionNative.ST_MOBILE_HAND_FINGER_INDEX) > 0) {
            findViewById(R.id.iv_one_finger).setBackgroundColor(mColorBlue);
            ((ImageView) findViewById(R.id.iv_one_finger)).setImageDrawable(getResources().getDrawable(R.drawable.ic_trigger_one_finger_selected));
        } else if ((action & STMobileHumanActionNative.ST_MOBILE_HAND_FINGER_HEART) > 0) {
            findViewById(R.id.iv_finger_heart).setBackgroundColor(mColorBlue);
            ((ImageView) findViewById(R.id.iv_finger_heart)).setImageDrawable(getResources().getDrawable(R.drawable.ic_trigger_finger_heart_selected));
        } else if ((action & STMobileHumanActionNative.ST_MOBILE_HAND_LOVE) > 0) {
            findViewById(R.id.iv_heart_hand).setBackgroundColor(mColorBlue);
            ((ImageView) findViewById(R.id.iv_heart_hand)).setImageDrawable(getResources().getDrawable(R.drawable.ic_trigger_heart_hand_selected));
        } else if ((action & STMobileHumanActionNative.ST_MOBILE_HAND_SCISSOR) > 0) {
            findViewById(R.id.iv_scissor).setBackgroundColor(mColorBlue);
            ((ImageView) findViewById(R.id.iv_scissor)).setImageDrawable(getResources().getDrawable(R.drawable.ic_trigger_scissor_selected));
        } else if ((action & STMobileHumanActionNative.ST_MOBILE_HAND_CONGRATULATE) > 0) {
            findViewById(R.id.iv_congratulate).setBackgroundColor(mColorBlue);
            ((ImageView) findViewById(R.id.iv_congratulate)).setImageDrawable(getResources().getDrawable(R.drawable.ic_trigger_congratulate_selected));
        } else if ((action & STMobileHumanActionNative.ST_MOBILE_HAND_HOLDUP) > 0) {
            findViewById(R.id.iv_palm_up).setBackgroundColor(mColorBlue);
            ((ImageView) findViewById(R.id.iv_palm_up)).setImageDrawable(getResources().getDrawable(R.drawable.ic_trigger_palm_up_selected));
        }
    }

    private void resetHandActionInfo() {
        findViewById(R.id.iv_palm).setBackgroundColor(Color.parseColor("#00000000"));
        ((ImageView) findViewById(R.id.iv_palm)).setImageDrawable(getResources().getDrawable(R.drawable.ic_trigger_palm));

        findViewById(R.id.iv_thumb).setBackgroundColor(Color.parseColor("#00000000"));
        ((ImageView) findViewById(R.id.iv_thumb)).setImageDrawable(getResources().getDrawable(R.drawable.ic_trigger_thumb));

        findViewById(R.id.iv_ok).setBackgroundColor(Color.parseColor("#00000000"));
        ((ImageView) findViewById(R.id.iv_ok)).setImageDrawable(getResources().getDrawable(R.drawable.ic_trigger_ok));

        findViewById(R.id.iv_pistol).setBackgroundColor(Color.parseColor("#00000000"));
        ((ImageView) findViewById(R.id.iv_pistol)).setImageDrawable(getResources().getDrawable(R.drawable.ic_trigger_pistol));

        findViewById(R.id.iv_one_finger).setBackgroundColor(Color.parseColor("#00000000"));
        ((ImageView) findViewById(R.id.iv_one_finger)).setImageDrawable(getResources().getDrawable(R.drawable.ic_trigger_one_finger));

        findViewById(R.id.iv_finger_heart).setBackgroundColor(Color.parseColor("#00000000"));
        ((ImageView) findViewById(R.id.iv_finger_heart)).setImageDrawable(getResources().getDrawable(R.drawable.ic_trigger_finger_heart));

        findViewById(R.id.iv_heart_hand).setBackgroundColor(Color.parseColor("#00000000"));
        ((ImageView) findViewById(R.id.iv_heart_hand)).setImageDrawable(getResources().getDrawable(R.drawable.ic_trigger_heart_hand));

        findViewById(R.id.iv_scissor).setBackgroundColor(Color.parseColor("#00000000"));
        ((ImageView) findViewById(R.id.iv_scissor)).setImageDrawable(getResources().getDrawable(R.drawable.ic_trigger_scissor));

        findViewById(R.id.iv_congratulate).setBackgroundColor(Color.parseColor("#00000000"));
        ((ImageView) findViewById(R.id.iv_congratulate)).setImageDrawable(getResources().getDrawable(R.drawable.ic_trigger_congratulate));

        findViewById(R.id.iv_palm_up).setBackgroundColor(Color.parseColor("#00000000"));
        ((ImageView) findViewById(R.id.iv_palm_up)).setImageDrawable(getResources().getDrawable(R.drawable.ic_trigger_palm_up));
    }

    private void showBodyActionInfo(long action) {
        TextView bodyActionView = (TextView) findViewById(R.id.tv_show_body_action);
        bodyActionView.setVisibility(View.VISIBLE);
        //for test body action
        if ((action & STMobileHumanActionNative.ST_MOBILE_BODY_ACTION3) > 0) {
            bodyActionView.setText("肢体动作：摊手");
        } else if ((action & STMobileHumanActionNative.ST_MOBILE_BODY_ACTION2) > 0) {
            bodyActionView.setText("肢体动作：一休");
        } else if ((action & STMobileHumanActionNative.ST_MOBILE_BODY_ACTION1) > 0) {
            bodyActionView.setText("肢体动作：龙拳");
        } else {
            bodyActionView.setVisibility(View.INVISIBLE);
        }
    }

    private void showFaceAttributeInfo() {
        if (mCameraDisplay.getFaceAttributeString() != null) {
            mAttributeText.setVisibility(View.VISIBLE);
            if (mCameraDisplay.getFaceAttributeString().equals("noFace")) {
                mAttributeText.setText("");
            } else {
                mAttributeText.setText("人脸属性: " + mCameraDisplay.getFaceAttributeString());
            }
        } else {
            mAttributeText.setVisibility(View.INVISIBLE);
        }
    }

    private void showFaceExpressionInfo(boolean[] faceExpressionInfo) {

        resetFaceExpression();

        if (faceExpressionInfo != null) {
            if (faceExpressionInfo[STMobileHumanActionNative.STMobileExpression.ST_MOBILE_EXPRESSION_HEAD_NORMAL.getExpressionCode()]) {
                ((ImageView) findViewById(R.id.iv_face_expression_head_normal)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_head_normal_selected));
            }
            if (faceExpressionInfo[STMobileHumanActionNative.STMobileExpression.ST_MOBILE_EXPRESSION_SIDE_FACE_LEFT.getExpressionCode()]) {
                ((ImageView) findViewById(R.id.iv_face_expression_side_face_left)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_side_face_left_selected));
            }
            if (faceExpressionInfo[STMobileHumanActionNative.STMobileExpression.ST_MOBILE_EXPRESSION_SIDE_FACE_RIGHT.getExpressionCode()]) {
                ((ImageView) findViewById(R.id.iv_face_expression_side_face_right)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_side_face_right_selected));
            }
            if (faceExpressionInfo[STMobileHumanActionNative.STMobileExpression.ST_MOBILE_EXPRESSION_TILTED_FACE_LEFT.getExpressionCode()]) {
                ((ImageView) findViewById(R.id.iv_face_expression_tilted_face_left)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_tilted_face_left_selected));
            }
            if (faceExpressionInfo[STMobileHumanActionNative.STMobileExpression.ST_MOBILE_EXPRESSION_TILTED_FACE_RIGHT.getExpressionCode()]) {
                ((ImageView) findViewById(R.id.iv_face_expression_tilted_face_right)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_tilted_face_right_selected));
            }

            if (faceExpressionInfo[STMobileHumanActionNative.STMobileExpression.ST_MOBILE_EXPRESSION_HEAD_RISE.getExpressionCode()]) {
                ((ImageView) findViewById(R.id.iv_face_expression_head_rise)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_head_rise_selected));
            }
            if (faceExpressionInfo[STMobileHumanActionNative.STMobileExpression.ST_MOBILE_EXPRESSION_HEAD_LOWER.getExpressionCode()]) {
                ((ImageView) findViewById(R.id.iv_face_expression_head_lower)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_head_lower_selected));
            }
            if (faceExpressionInfo[STMobileHumanActionNative.STMobileExpression.ST_MOBILE_EXPRESSION_TWO_EYE_OPEN.getExpressionCode()]) {
                ((ImageView) findViewById(R.id.iv_face_expression_two_eye_open)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_two_eye_open_selected));
            }
            if (faceExpressionInfo[STMobileHumanActionNative.STMobileExpression.ST_MOBILE_EXPRESSION_TWO_EYE_CLOSE.getExpressionCode()]) {
                ((ImageView) findViewById(R.id.iv_face_expression_two_eye_close)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_two_eye_close_selected));
            }
            if (faceExpressionInfo[STMobileHumanActionNative.STMobileExpression.ST_MOBILE_EXPRESSION_LEFTEYE_CLOSE_RIGHTEYE_OPEN.getExpressionCode()]) {
                ((ImageView) findViewById(R.id.iv_face_expression_lefteye_close_righteye_open)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_lefteye_close_righteye_open_selected));
            }

            if (faceExpressionInfo[STMobileHumanActionNative.STMobileExpression.ST_MOBILE_EXPRESSION_LEFTEYE_OPEN_RIGHTEYE_CLOSE.getExpressionCode()]) {
                ((ImageView) findViewById(R.id.iv_face_expression_lefteye_open_righteye_close)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_lefteye_open_righteye_close_selected));
            }
            if (faceExpressionInfo[STMobileHumanActionNative.STMobileExpression.ST_MOBILE_EXPRESSION_MOUTH_OPEN.getExpressionCode()]) {
                ((ImageView) findViewById(R.id.iv_face_expression_mouth_open)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_mouth_open_selected));
            }
            if (faceExpressionInfo[STMobileHumanActionNative.STMobileExpression.ST_MOBILE_EXPRESSION_MOUTH_CLOSE.getExpressionCode()]) {
                ((ImageView) findViewById(R.id.iv_face_expression_mouth_close)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_mouth_close_selected));
            }
            if (faceExpressionInfo[STMobileHumanActionNative.STMobileExpression.ST_MOBILE_EXPRESSION_FACE_LIPS_POUTED.getExpressionCode()]) {
                ((ImageView) findViewById(R.id.iv_face_expression_face_lips_pouted)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_face_lips_pouted_selected));
            }
            if (faceExpressionInfo[STMobileHumanActionNative.STMobileExpression.ST_MOBILE_EXPRESSION_FACE_LIPS_UPWARD.getExpressionCode()]) {
                ((ImageView) findViewById(R.id.iv_face_expression_face_lips_upward)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_face_lips_upward_selected));
            }

            if (faceExpressionInfo[STMobileHumanActionNative.STMobileExpression.ST_MOBILE_EXPRESSION_FACE_LIPS_CURL_LEFT.getExpressionCode()]) {
                ((ImageView) findViewById(R.id.iv_face_expression_lips_curl_left)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_lips_curl_left_selected));
            }
            if (faceExpressionInfo[STMobileHumanActionNative.STMobileExpression.ST_MOBILE_EXPRESSION_FACE_LIPS_CURL_RIGHT.getExpressionCode()]) {
                ((ImageView) findViewById(R.id.iv_face_expression_lips_curl_right)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_lips_curl_right_selected));
            }
        }
    }

    private void resetFaceExpression() {
        ((ImageView) findViewById(R.id.iv_face_expression_head_normal)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_head_normal));
        ((ImageView) findViewById(R.id.iv_face_expression_side_face_left)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_side_face_left));
        ((ImageView) findViewById(R.id.iv_face_expression_side_face_right)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_side_face_right));
        ((ImageView) findViewById(R.id.iv_face_expression_tilted_face_left)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_tilted_face_left));
        ((ImageView) findViewById(R.id.iv_face_expression_tilted_face_right)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_tilted_face_right));

        ((ImageView) findViewById(R.id.iv_face_expression_head_rise)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_head_rise));
        ((ImageView) findViewById(R.id.iv_face_expression_head_lower)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_head_lower));
        ((ImageView) findViewById(R.id.iv_face_expression_two_eye_open)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_two_eye_open));
        ((ImageView) findViewById(R.id.iv_face_expression_two_eye_close)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_two_eye_close));
        ((ImageView) findViewById(R.id.iv_face_expression_lefteye_close_righteye_open)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_lefteye_close_righteye_open));

        ((ImageView) findViewById(R.id.iv_face_expression_lefteye_open_righteye_close)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_lefteye_open_righteye_close));
        ((ImageView) findViewById(R.id.iv_face_expression_mouth_open)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_mouth_open));
        ((ImageView) findViewById(R.id.iv_face_expression_mouth_close)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_mouth_close));
        ((ImageView) findViewById(R.id.iv_face_expression_face_lips_pouted)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_face_lips_pouted));
        ((ImageView) findViewById(R.id.iv_face_expression_face_lips_upward)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_face_lips_upward));

        ((ImageView) findViewById(R.id.iv_face_expression_lips_curl_left)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_lips_curl_left));
        ((ImageView) findViewById(R.id.iv_face_expression_lips_curl_right)).setImageDrawable(getResources().getDrawable(R.drawable.face_expression_lips_curl_right));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mCameraDisplay.setSensorEvent(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 首先鉴权，鉴权成功后，根据group id 获取相应的group 下的素材列表
     */
//    private void initStickerListFromNet() {
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

    /**
     * 初始化tab 点击事件
     */
    private void initStickerTabListener() {
        //tab 切换事件订阅
        mStickerOptionsAdapter.setClickStickerListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStickerOptionsList == null || mStickerOptionsList.size() <= 0) {
                    LogUtils.e(TAG, "group 列表不能为空");
                    return;
                }
                int position = Integer.parseInt(v.getTag().toString());
                mStickerOptionsAdapter.setSelectedPosition(position);
                mStickersRecycleView.setLayoutManager(new GridLayoutManager(mContext, 6));

                //更新这一次的选择
                StickerOptionsItem selectedItem = mStickerOptionsAdapter.getPositionItem(position);
                if (selectedItem == null) {
                    LogUtils.e(TAG, "选择项目不能为空!");
                    return;
                }
                RecyclerView.Adapter selectedAdapter;
                if (selectedItem.name.equals("sticker_new_engine")) {
                    selectedAdapter = mNativeStickerAdapters.get(selectedItem.name);
                } else if (selectedItem.name.equals("sticker_add_package")) {
                    selectedAdapter = mNativeStickerAdapters.get(selectedItem.name);
                } else if (selectedItem.name.equals("object_track")) {
                    if (mCameraDisplay.getCameraID() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        mCameraDisplay.switchCamera();
                    }
                    selectedAdapter = mObjectAdapter;
                } else {
                    selectedAdapter = mStickerAdapters.get(selectedItem.name);
                }

                if (selectedAdapter == null) {
                    LogUtils.e(TAG, "贴纸adapter 不能为空");
                    Toast.makeText(getApplicationContext(), "列表正在拉取，或拉取出错!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mStickersRecycleView.setAdapter(selectedAdapter);
                mStickerOptionsAdapter.notifyDataSetChanged();
                selectedAdapter.notifyDataSetChanged();
            }
        });
    }

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
//
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    mMakeupOptionsRecycleView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
//                    mMakeupOptionsRecycleView.addItemDecoration(new SpaceItemDecoration(0));
//                    switch (mCurrentMakeupGroupIndex) {
//                        case Constants.ST_MAKEUP_ALL://整妆
//                            mMakeupOptionsRecycleView.setAdapter(mMakeupAdapters.get("makeup_all"));
//                            break;
//                        case Constants.ST_MAKEUP_HAIR_DYE://染发
//                            mMakeupOptionsRecycleView.setAdapter(mMakeupAdapters.get("makeup_hairdye"));
//                            break;
//                        case Constants.ST_MAKEUP_LIP:
//                            mMakeupOptionsRecycleView.setAdapter(mMakeupAdapters.get("makeup_lip"));
//                            break;
//                        case Constants.ST_MAKEUP_BLUSH:
//                            mMakeupOptionsRecycleView.setAdapter(mMakeupAdapters.get("makeup_blush"));
//                            break;
//                        case Constants.ST_MAKEUP_HIGHLIGHT://修容
//                            mMakeupOptionsRecycleView.setAdapter(mMakeupAdapters.get("makeup_highlight"));
//                            break;
//                        case Constants.ST_MAKEUP_BROW://眉毛
//                            mMakeupOptionsRecycleView.setAdapter(mMakeupAdapters.get("makeup_brow"));
//                            break;
//                        case Constants.ST_MAKEUP_EYE://眼影
//                            mMakeupOptionsRecycleView.setAdapter(mMakeupAdapters.get("makeup_eye"));
//                            break;
//                        case Constants.ST_MAKEUP_EYELINER://眼线
//                            mMakeupOptionsRecycleView.setAdapter(mMakeupAdapters.get("makeup_eyeliner"));
//                            break;
//                        case Constants.ST_MAKEUP_EYELASH://眼睫毛
//                            mMakeupOptionsRecycleView.setAdapter(mMakeupAdapters.get("makeup_eyelash"));
//                            break;
//                        case Constants.ST_MAKEUP_EYEBALL://美瞳
//                            mMakeupOptionsRecycleView.setAdapter(mMakeupAdapters.get("makeup_eyeball"));
//                            break;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
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

    /**
     * 根据group id 对应素材列表
     *
     */
//    private void fetchGroupMaterialList(final List<StickerOptionsItem> groups) {
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
//                    LogUtils.e(TAG, String.format(Locale.getDefault(), "下载素材信息失败！%d, %s", code, TextUtils.isEmpty(message) ? "" : message));
//                }
//            });
//        }
//
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
//    }
//
//    // 初始化素材列表中的点击事件回调
//    private void initStickerListener(final String groupId, final int index, final List<SenseArMaterial> materials) {
//        mStickerAdapters.get(groupId).setClickStickerListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!NetworkUtils.isNetworkAvailable(getApplicationContext())) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getApplicationContext(), "Network unavailable.", Toast.LENGTH_LONG).show();
//
//                        }
//                    });
//                }
//                mTipsLayout.setVisibility(View.GONE);
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
//                    mLastStickerPosition = -1;
//
//                    findViewById(R.id.iv_close_sticker).setBackground(getResources().getDrawable(R.drawable.close_sticker_selected));
//                    mCameraDisplay.enableSticker(false);
//                    mCameraDisplay.removeAllStickers();
//                    mStickerAdapters.get(groupId).notifyDataSetChanged();
//                    recoverUI();
//                    return;
//                }
//                SenseArMaterial sarm = materials.get(position);
//                preMaterialId = sarm.id;
//                //如果素材还未下载，点击时需要下载
//                if (stickerItem.state == StickerState.NORMAL_STATE) {
//                    stickerItem.state = StickerState.LOADING_STATE;
//                    notifyStickerViewState(stickerItem, position, groupId);
////                    mStickerAdapters.get(groupId).notifyDataSetChanged();
//                    SenseArMaterialService.shareInstance().downloadMaterial(CameraActivity.this, sarm, new SenseArMaterialService.DownloadMaterialListener() {
//                        @Override
//                        public void onSuccess(final SenseArMaterial material) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    stickerItem.path = material.cachedPath;
//                                    stickerItem.state = StickerState.DONE_STATE;
//                                    //如果本次下载是用户用户最后一次选中项，则直接应用
//                                    if (preMaterialId.equals(material.id)) {
//                                        clickStickerItem(position, index, groupId);
//                                    }
//                                    notifyStickerViewState(stickerItem, position, groupId);
////                                    mStickerAdapters.get(groupId).notifyDataSetChanged();
//                                }
//                            });
//                            LogUtils.d(TAG, String.format(Locale.getDefault(), "素材下载成功:%s,cached path is %s", material.materials, material.cachedPath));
//                        }
//
//                        @Override
//                        public void onFailure(SenseArMaterial material, final int code, String message) {
//                            LogUtils.d(TAG, String.format(Locale.getDefault(), "素材下载失败:%s", material.materials));
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    stickerItem.state = StickerState.NORMAL_STATE;
//                                    notifyStickerViewState(stickerItem, position, groupId);
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
//                    clickStickerItem(position, index, groupId);
//                }
//            }
//        });
//    }

    private void clickStickerItem(int position, int index, String groupId) {
        resetNewStickerAdapter();
        resetStickerAdapter();
        mCurrentStickerOptionsIndex = index;
        mLastStickerPosition = mCurrentStickerPosition;
        mCurrentStickerPosition = position;

        findViewById(R.id.iv_close_sticker).setBackground(getResources().getDrawable(R.drawable.close_sticker));

        if (mStickerAdapters.get(groupId)!=null) {
        mStickerAdapters.get(groupId).setSelectedPosition(position);
        mCameraDisplay.enableSticker(true);
        recoverUI();
        mCameraDisplay.changeSticker(mStickerlists.get(groupId).get(position).path);
        }
    }

    private void initNativeStickerAdapter(final String stickerClassName, final int index) {
        mNativeStickerAdapters.get(stickerClassName).setSelectedPosition(-1);
        mNativeStickerAdapters.get(stickerClassName).setClickStickerListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTipsLayout.setVisibility(View.GONE);
                resetNewStickerAdapter();
                resetStickerAdapter();
                int position = Integer.parseInt(v.getTag().toString());

                if (mCurrentStickerOptionsIndex == index && mCurrentStickerPosition == position) {
                    mNativeStickerAdapters.get(stickerClassName).setSelectedPosition(-1);
                    mCurrentStickerOptionsIndex = -1;
                    mCurrentStickerPosition = -1;
                    mLastStickerPosition = -1;

                    findViewById(R.id.iv_close_sticker).setBackground(getResources().getDrawable(R.drawable.close_sticker_selected));
                    mCameraDisplay.enableSticker(false);
                    mCameraDisplay.changeSticker(null);
                    recoverUI();
                } else {
                    mCurrentStickerOptionsIndex = index;
                    mLastStickerPosition = mCurrentStickerPosition;
                    mCurrentStickerPosition = position;

                    findViewById(R.id.iv_close_sticker).setBackground(getResources().getDrawable(R.drawable.close_sticker));

                    mNativeStickerAdapters.get(stickerClassName).setSelectedPosition(position);
                    mCameraDisplay.enableSticker(true);
                    recoverUI();
                    mCameraDisplay.changeSticker(mNewStickers.get(position).path);
                }

                mNativeStickerAdapters.get(stickerClassName).notifyDataSetChanged();
            }
        });
    }

    private void initAddPackageStickerAdapter(final String stickerClassName, final int index) {
        mNativeStickerAdapters.get(stickerClassName).setSelectedPosition(-1);
        mNativeStickerAdapters.get(stickerClassName).setClickStickerListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTipsLayout.setVisibility(View.GONE);
                if (mStickerPackageMap != null) {
                    mStickerPackageMap.clear();
                }

                if (mNativeStickerAdapters.get("sticker_new_engine") != null) {
                    mNativeStickerAdapters.get("sticker_new_engine").setSelectedPosition(-1);
                    mNativeStickerAdapters.get("sticker_new_engine").notifyDataSetChanged();
                }

                //重置所有状态为为选中状态
                for (StickerOptionsItem optionsItem : mStickerOptionsList) {
                    if (optionsItem.name.equals("sticker_new_engine")) {
                        continue;
                    } else if (optionsItem.name.equals("object_track")) {
                        continue;
                    } else {
                        if (mStickerAdapters.get(optionsItem.name) != null) {
                            mStickerAdapters.get(optionsItem.name).setSelectedPosition(-1);
                            mStickerAdapters.get(optionsItem.name).notifyDataSetChanged();
                        }
                    }
                }
                int position = Integer.parseInt(v.getTag().toString());

                if (mCurrentStickerOptionsIndex == index && mCurrentStickerPosition == position) {
                    mNativeStickerAdapters.get(stickerClassName).setSelectedPosition(-1);
                    mCurrentStickerOptionsIndex = -1;
                    mCurrentStickerPosition = -1;
                    mLastStickerPosition = -1;

                    iv_close_sticker.setBackground(getResources().getDrawable(R.drawable.close_sticker_selected));
                    mCameraDisplay.enableSticker(false);
                    int size = mCameraDisplay.mCurrentStickerMaps.size();
                    mCameraDisplay.removeSticker(mNewStickers.get(position).path);
                    if(size == 1)
                        recoverUI();
                } else {
                    mCurrentStickerOptionsIndex = index;
                    mLastStickerPosition = mCurrentStickerPosition;
                    mCurrentStickerPosition = position;

                    iv_close_sticker.setBackground(getResources().getDrawable(R.drawable.close_sticker));

                    mNativeStickerAdapters.get(stickerClassName).setSelectedPosition(position);
                    mCameraDisplay.enableSticker(true);
                    mCameraDisplay.addSticker(mNewStickers.get(position).path);
                }

                mNativeStickerAdapters.get(stickerClassName).notifyDataSetChanged();
            }
        });
    }

    /**
     * 初始化素材的基本信息，如缩略图，是否已经缓存
     *
     * @param groupId   组id
     * @param materials 服务器返回的素材list
     */
//    private void fetchGroupMaterialInfo(final String groupId, final List<SenseArMaterial> materials, final int index) {
//        if (materials == null || materials.size() <= 0) {
//            return;
//        }
//        final ArrayList<StickerItem> stickerList = new ArrayList<>();
//        mStickerlists.put(groupId, stickerList);
//        mStickerAdapters.put(groupId, new StickerAdapter(mStickerlists.get(groupId), getApplicationContext()));
//        mStickerAdapters.get(groupId).setSelectedPosition(-1);
//        LogUtils.d(TAG, "group id is " + groupId + " materials size is " + materials.size());
//        initStickerListener(groupId, index, materials);
//        for (int i = 0; i < materials.size(); i++) {
//            SenseArMaterial sarm = materials.get(i);
//            Bitmap bitmap = null;
//            try {
//                bitmap = ImageUtils.getImageSync(sarm.thumbnail, CameraActivity.this);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (bitmap == null) {
//                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.none);
//            }
//            String path = "";
//            //如果已经下载则传入路径地址
//            if (SenseArMaterialService.shareInstance().isMaterialDownloaded(CameraActivity.this, sarm)) {
//                path = SenseArMaterialService.shareInstance().getMaterialCachedPath(CameraActivity.this, sarm);
//            }
//            sarm.cachedPath = path;
//            stickerList.add(new StickerItem(sarm.name, bitmap, path));
//        }
//    }

    //
    private boolean checkMicroType(int beautyOptionsPosition) {
        int type = mBeautyOptionSelectedIndex.get(beautyOptionsPosition);
        boolean ans = ((type != 0) &&
                (type != 1) &&
                (type != 4) &&
                (type != 5) &&
                (type != 7) &&
                (type != 12) &&
                (type != 13) &&
                (type != 14) &&
                (type != 17) &&
                (type != 15) && (type != 16) && (type != 18));
        return ans && (3 == beautyOptionsPosition);
    }

    private void updateMakeupOptions(int type, boolean value) {
        setMakeUpViewSelected(value, type);
        switch (type) {
            case Constants.ST_MAKEUP_ALL:
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.selector_makeup_all_img));
                break;
            case Constants.ST_MAKEUP_HAIR_DYE:
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.selector_makeup_hairdye_img));
                break;
            case Constants.ST_MAKEUP_LIP://口红
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.selector_makeup_lip_img));
                break;
            case Constants.ST_MAKEUP_BLUSH://腮红
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.selector_makeup_cheeks_img));
                break;
            case Constants.ST_MAKEUP_HIGHLIGHT://修容
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.selector_makeup_face_img));
                break;
            case Constants.ST_MAKEUP_BROW://眉毛
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.selector_makeup_brow_img));
                break;
            case Constants.ST_MAKEUP_EYE://眼影
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.selector_makeup_eye_img));
                break;
            case Constants.ST_MAKEUP_EYELINER://眼线
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.selector_makeup_eyeline_img));
                break;
            case Constants.ST_MAKEUP_EYELASH://眼睫毛
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.selector_makeup_eyelash_img));
                break;
            case Constants.ST_MAKEUP_EYEBALL://美瞳
                mMakeupGroupBack.setImageDrawable(getResources().getDrawable(R.drawable.selector_makeup_eyeball_img));
                break;
        }
        mMakeupGroupBack.setSelected(value);
    }

    private void resetMakeup(boolean needResetAll, boolean onlyResetUI) {
        //美妆互斥逻辑
        int count = Constants.MAKEUP_TYPE_COUNT;
        if (!needResetAll) {
            count = Constants.MAKEUP_TYPE_COUNT - 1;
        }

        if (!onlyResetUI) {
            for (int i = 0; i < count; i++) {
                mCameraDisplay.removeMakeupByType(i);
                mMakeupOptionSelectedIndex.put(i, 0);
                mMakeupStrength.put(i, 80);
            }
        }

        mFilterStrengthLayout.setVisibility(View.INVISIBLE);

        setMakeUpViewSelected(false, -1);

        mMakeupAdapters.get("makeup_lip").setSelectedPosition(0);
        mMakeupAdapters.get("makeup_lip").notifyDataSetChanged();
        mMakeupAdapters.get("makeup_highlight").setSelectedPosition(0);
        mMakeupAdapters.get("makeup_highlight").notifyDataSetChanged();
        mMakeupAdapters.get("makeup_blush").setSelectedPosition(0);
        mMakeupAdapters.get("makeup_blush").notifyDataSetChanged();
        mMakeupAdapters.get("makeup_brow").setSelectedPosition(0);
        mMakeupAdapters.get("makeup_brow").notifyDataSetChanged();
        mMakeupAdapters.get("makeup_eye").setSelectedPosition(0);
        mMakeupAdapters.get("makeup_eye").notifyDataSetChanged();
        mMakeupAdapters.get("makeup_eyeliner").setSelectedPosition(0);
        mMakeupAdapters.get("makeup_eyeliner").notifyDataSetChanged();
        mMakeupAdapters.get("makeup_eyelash").setSelectedPosition(0);
        mMakeupAdapters.get("makeup_eyelash").notifyDataSetChanged();

        mMakeupAdapters.get("makeup_eyeball").setSelectedPosition(0);
        mMakeupAdapters.get("makeup_eyeball").notifyDataSetChanged();
        mMakeupAdapters.get("makeup_hairdye").setSelectedPosition(0);
        mMakeupAdapters.get("makeup_hairdye").notifyDataSetChanged();

        if (needResetAll) {
            setMakeUpViewSelected(false, Constants.ST_MAKEUP_ALL);
            mMakeupAdapters.get("makeup_all").setSelectedPosition(0);
            mMakeupAdapters.get("makeup_all").notifyDataSetChanged();
        }
    }

    private String getMakeupNameOfType(int type) {
        String name = "makeup_blush";
        if (type == STMobileMakeupType.ST_MAKEUP_TYPE_BROW) {
            name = "makeup_brow";
        } else if (type == STMobileMakeupType.ST_MAKEUP_TYPE_EYE) {
            name = "makeup_eye";
        } else if (type == STMobileMakeupType.ST_MAKEUP_TYPE_BLUSH) {
            name = "makeup_blush";
        } else if (type == STMobileMakeupType.ST_MAKEUP_TYPE_LIP) {
            name = "makeup_lip";
        } else if (type == STMobileMakeupType.ST_MAKEUP_TYPE_HIGHLIGHT) {
            name = "makeup_highlight";
        } else if (type == STMobileMakeupType.ST_MAKEUP_TYPE_EYELINER) {
            name = "makeup_eyeliner";
        } else if (type == STMobileMakeupType.ST_MAKEUP_TYPE_EYELASH) {
            name = "makeup_eyelash";
        } else if (type == STMobileMakeupType.ST_MAKEUP_TYPE_EYEBALL) {
            name = "makeup_eyeball";
        } else if (type == STMobileMakeupType.ST_MAKEUP_TYPE_HAIR_DYE) {
            name = "makeup_hairdye";
        } else if (type == STMobileMakeupType.ST_MAKEUP_TYPE_ALL) {
            name = "makeup_all";
        }
        return name;
    }

    private boolean checkMakeUpSelect() {
        for (Map.Entry<Integer, Integer> entry : mMakeupOptionSelectedIndex.entrySet()) {
            if (entry.getValue() != 0) {
                return true;
            }
        }
        return false;
    }

    private boolean checkNeedBeauty() {
        for (Map.Entry<String, ArrayList<BeautyItem>> entry : mBeautylists.entrySet()) {
            ArrayList<BeautyItem> list = entry.getValue();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getProgress() != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    //整体效果
    private void initFullBeauty() {
        mBeautyFullItem = LocalDataStore.getInstance().getFullBeautyList();

        mBeautyFullAdapter = new FullBeautyItemAdapter(this, mBeautyFullItem);
        mBeautyOption.put(0, "fullBeauty");
        mBeautyBaseRecycleView.setAdapter(mBeautyFullAdapter);
        lastIndex = -1;
    }

    private void setFullBeauty(FullBeautyItem fullBeautyItem) {
        mBeautyFullAdapter.setSelectedPosition(mCurrentFullBeautyIndex);
        mBeautyFullAdapter.notifyDataSetChanged();
        //处理美妆
        resetMakeup(true, false);
        if (fullBeautyItem.getMakeupList() != null) {
            for (int i = 0; i < fullBeautyItem.getMakeupList().size(); i++) {
                String type = fullBeautyItem.getMakeupList().get(i).getType();
                String name = fullBeautyItem.getMakeupList().get(i).getMakeupName();
                int strength = fullBeautyItem.getMakeupList().get(i).getStrength();
                int position = getMakeupIndexByName(type, name);

                setMakeupWithType(type, strength, position);
            }
        }

        //设置滤镜
        if (fullBeautyItem.getFilter() == null) {
            resetFilterView(false);
        } else {
            setFilterWithType(fullBeautyItem.getFilter());
        }
        mDefaultMakeupStrength.clear();
        mDefaultMakeupStrength.putAll(mMakeupStrength);
    }

    private void setBeautyListsForFullBeauty(int beautyOptionsPosition, float[] beautifyParams) {
        for (int i = 0; i < beautifyParams.length; i++) {
            mBeautylists.get(mBeautyOption.get(beautyOptionsPosition)).get(i).setProgress((int) (beautifyParams[i] * 100));
        }
    }

    private void setMakeupWithType(String type, int strength, int position) {
        if (type.equals("makeup_lip")) {
            mCameraDisplay.removeMakeupByType(Constants.ST_MAKEUP_LIP);
            mMakeupOptionSelectedIndex.put(Constants.ST_MAKEUP_LIP, position);
            mMakeupStrength.put(Constants.ST_MAKEUP_LIP, strength);
            updateMakeupOptions(Constants.ST_MAKEUP_LIP, true);
            mMakeupAdapters.get("makeup_lip").setSelectedPosition(position);
            mMakeupAdapters.get("makeup_lip").notifyDataSetChanged();
            mCameraDisplay.setMakeupForType(Constants.ST_MAKEUP_LIP, mMakeupLists.get("makeup_lip").get(position).path);
            mCameraDisplay.setStrengthForType(Constants.ST_MAKEUP_LIP, (float) strength / 100.f);
            mCameraDisplay.enableMakeUp(true);
        } else if (type.equals("makeup_highlight")) {
            mCameraDisplay.removeMakeupByType(Constants.ST_MAKEUP_HIGHLIGHT);
            mMakeupOptionSelectedIndex.put(Constants.ST_MAKEUP_HIGHLIGHT, position);
            mMakeupStrength.put(Constants.ST_MAKEUP_HIGHLIGHT, strength);
            updateMakeupOptions(Constants.ST_MAKEUP_HIGHLIGHT, true);
            mMakeupAdapters.get("makeup_highlight").setSelectedPosition(position);
            mMakeupAdapters.get("makeup_highlight").notifyDataSetChanged();
            mCameraDisplay.setMakeupForType(Constants.ST_MAKEUP_HIGHLIGHT, mMakeupLists.get("makeup_highlight").get(position).path);
            mCameraDisplay.setStrengthForType(Constants.ST_MAKEUP_HIGHLIGHT, (float) strength / 100.f);
            mCameraDisplay.enableMakeUp(true);
        } else if (type.equals("makeup_blush")) {
            mCameraDisplay.removeMakeupByType(Constants.ST_MAKEUP_BLUSH);
            mMakeupOptionSelectedIndex.put(Constants.ST_MAKEUP_BLUSH, position);
            mMakeupStrength.put(Constants.ST_MAKEUP_BLUSH, strength);
            updateMakeupOptions(Constants.ST_MAKEUP_BLUSH, true);
            mMakeupAdapters.get("makeup_blush").setSelectedPosition(position);
            mMakeupAdapters.get("makeup_blush").notifyDataSetChanged();
            mCameraDisplay.setMakeupForType(Constants.ST_MAKEUP_BLUSH, mMakeupLists.get("makeup_blush").get(position).path);
            mCameraDisplay.setStrengthForType(Constants.ST_MAKEUP_BLUSH, (float) strength / 100.f);
            mCameraDisplay.enableMakeUp(true);
        } else if (type.equals("makeup_brow")) {
            mCameraDisplay.removeMakeupByType(Constants.ST_MAKEUP_BROW);
            mMakeupOptionSelectedIndex.put(Constants.ST_MAKEUP_BROW, position);
            mMakeupStrength.put(Constants.ST_MAKEUP_BROW, strength);
            updateMakeupOptions(Constants.ST_MAKEUP_BROW, true);
            mMakeupAdapters.get("makeup_brow").setSelectedPosition(position);
            mMakeupAdapters.get("makeup_brow").notifyDataSetChanged();
            mCameraDisplay.setMakeupForType(Constants.ST_MAKEUP_BROW, mMakeupLists.get("makeup_brow").get(position).path);
            mCameraDisplay.setStrengthForType(Constants.ST_MAKEUP_BROW, (float) strength / 100.f);
            mCameraDisplay.enableMakeUp(true);
        } else if (type.equals("makeup_eye")) {
            mCameraDisplay.removeMakeupByType(Constants.ST_MAKEUP_EYE);
            mMakeupOptionSelectedIndex.put(Constants.ST_MAKEUP_EYE, position);
            mMakeupStrength.put(Constants.ST_MAKEUP_EYE, strength);
            updateMakeupOptions(Constants.ST_MAKEUP_EYE, true);
            mMakeupAdapters.get("makeup_eye").setSelectedPosition(position);
            mMakeupAdapters.get("makeup_eye").notifyDataSetChanged();
            mCameraDisplay.setMakeupForType(Constants.ST_MAKEUP_EYE, mMakeupLists.get("makeup_eye").get(position).path);
            mCameraDisplay.setStrengthForType(Constants.ST_MAKEUP_EYE, (float) strength / 100.f);
            mCameraDisplay.enableMakeUp(true);
        } else if (type.equals("makeup_eyeliner")) {
            mCameraDisplay.removeMakeupByType(Constants.ST_MAKEUP_EYELINER);
            mMakeupOptionSelectedIndex.put(Constants.ST_MAKEUP_EYELINER, position);
            mMakeupStrength.put(Constants.ST_MAKEUP_EYELINER, strength);
            updateMakeupOptions(Constants.ST_MAKEUP_EYELINER, true);
            mMakeupAdapters.get("makeup_eyeliner").setSelectedPosition(position);
            mMakeupAdapters.get("makeup_eyeliner").notifyDataSetChanged();
            mCameraDisplay.setMakeupForType(Constants.ST_MAKEUP_EYELINER, mMakeupLists.get("makeup_eyeliner").get(position).path);
            mCameraDisplay.setStrengthForType(Constants.ST_MAKEUP_EYELINER, (float) strength / 100.f);
            mCameraDisplay.enableMakeUp(true);
        } else if (type.equals("makeup_eyelash")) {
            mCameraDisplay.removeMakeupByType(Constants.ST_MAKEUP_EYELASH);
            mMakeupOptionSelectedIndex.put(Constants.ST_MAKEUP_EYELASH, position);
            mMakeupStrength.put(Constants.ST_MAKEUP_EYELASH, strength);
            updateMakeupOptions(Constants.ST_MAKEUP_EYELASH, true);
            mMakeupAdapters.get("makeup_eyelash").setSelectedPosition(position);
            mMakeupAdapters.get("makeup_eyelash").notifyDataSetChanged();
            mCameraDisplay.setMakeupForType(Constants.ST_MAKEUP_EYELASH, mMakeupLists.get("makeup_eyelash").get(position).path);
            mCameraDisplay.setStrengthForType(Constants.ST_MAKEUP_EYELASH, (float) strength / 100.f);
            mCameraDisplay.enableMakeUp(true);
        } else if (type.equals("makeup_eyeball")) {
            mCameraDisplay.removeMakeupByType(Constants.ST_MAKEUP_EYEBALL);
            mMakeupOptionSelectedIndex.put(Constants.ST_MAKEUP_EYEBALL, position);
            mMakeupStrength.put(Constants.ST_MAKEUP_EYEBALL, strength);
            updateMakeupOptions(Constants.ST_MAKEUP_EYEBALL, true);
            mMakeupAdapters.get("makeup_eyeball").setSelectedPosition(position);
            mMakeupAdapters.get("makeup_eyeball").notifyDataSetChanged();
            mCameraDisplay.setMakeupForType(Constants.ST_MAKEUP_EYEBALL, mMakeupLists.get("makeup_eyeball").get(position).path);
            mCameraDisplay.setStrengthForType(Constants.ST_MAKEUP_EYEBALL, (float) strength / 100.f);
            mCameraDisplay.enableMakeUp(true);
        } else if (type.equals("makeup_hairdye")) {
            mCameraDisplay.removeMakeupByType(Constants.ST_MAKEUP_HAIR_DYE);
            mMakeupOptionSelectedIndex.put(Constants.ST_MAKEUP_HAIR_DYE, position);
            mMakeupStrength.put(Constants.ST_MAKEUP_HAIR_DYE, strength);
            updateMakeupOptions(Constants.ST_MAKEUP_HAIR_DYE, true);
            mMakeupAdapters.get("makeup_hairdye").setSelectedPosition(position);
            mMakeupAdapters.get("makeup_hairdye").notifyDataSetChanged();
            mCameraDisplay.setMakeupForType(Constants.ST_MAKEUP_HAIR_DYE, mMakeupLists.get("makeup_hairdye").get(position).path);
            mCameraDisplay.setStrengthForType(Constants.ST_MAKEUP_HAIR_DYE, (float) strength / 100.f);
            mCameraDisplay.enableMakeUp(true);
        } else if (type.equals("makeup_all")) {
            mCameraDisplay.removeMakeupByType(Constants.ST_MAKEUP_ALL);
            mMakeupOptionSelectedIndex.put(Constants.ST_MAKEUP_ALL, position);
            mMakeupStrength.put(Constants.ST_MAKEUP_ALL, strength);
            updateMakeupOptions(Constants.ST_MAKEUP_ALL, true);
            mMakeupAdapters.get("makeup_all").setSelectedPosition(position);
            mMakeupAdapters.get("makeup_all").notifyDataSetChanged();
            mCameraDisplay.setMakeupForType(Constants.ST_MAKEUP_ALL, mMakeupLists.get("makeup_all").get(position).path);
            mCameraDisplay.setStrengthForType(Constants.ST_MAKEUP_ALL, (float) strength / 100.f);
            mCameraDisplay.enableMakeUp(true);
        }
    }

    public int getMakeupIndexByName(String type, String name) {
        int position = 0;
        if (mMakeupLists.get(type).size() > 0) {
            for (int i = 0; i < mMakeupLists.get(type).size(); i++) {
                if (mMakeupLists.get(type).get(i).name.equals(name)) {
                    position = i;
                }
            }
        }

        return position;
    }

    public void setFilterWithType(FilterInfo filterInfo) {
        resetFilterView(false);

        if (filterInfo == null) {
            mFilterAdapters.get("filter_portrait").setSelectedPosition(0);
            mFilterAdapters.get("filter_scenery").setSelectedPosition(0);
            mFilterAdapters.get("filter_still_life").setSelectedPosition(0);
            mFilterAdapters.get("filter_food").setSelectedPosition(0);
            mCameraDisplay.setFilterStyle(null, null, null);
            mCameraDisplay.enableFilter(false);
        }

        String type = filterInfo.getType();
        String filterName = filterInfo.getFilterName();
        int strength = filterInfo.getStrength();
        if (mFilterLists.get(type).size() > 0) {
            for (int i = 0; i < mFilterLists.get(type).size(); i++) {
                if (mFilterLists.get(type).get(i).name.equals(filterName)) {
                    mCurrentFilterIndex = i;
                }
            }

            if (mCurrentFilterIndex > 0) {

                mFilterAdapters.get(type).setSelectedPosition(mCurrentFilterIndex);
                mCameraDisplay.setFilterStyle(type, mFilterLists.get(type).get(mCurrentFilterIndex).name, mFilterLists.get(type).get(mCurrentFilterIndex).model);
                mCameraDisplay.setFilterStrength((float) strength / 100f);
                mCameraDisplay.enableFilter(true);
                mFilterStrength = strength;
                mDefaultFilterStrength = strength;
                mFilterStrengthBar.setProgress(mFilterStrength);
                mFilterStrengthText.setText(mFilterStrength + "");

                if (type.equals("filter_portrait")) {
                    mCurrentFilterGroupIndex = 0;
                } else if (type.equals("filter_scenery")) {
                    mCurrentFilterGroupIndex = 1;
                } else if (type.equals("filter_still_life")) {
                    mCurrentFilterGroupIndex = 2;
                } else if (type.equals("filter_food")) {
                    mCurrentFilterGroupIndex = 3;
                }
                setFilterViewSelected(mCurrentFilterGroupIndex);
                mFilterAdapters.get(type).notifyDataSetChanged();
            }
        }
    }

    private void clearFullBeautyView() {
        gpFullBeautySeekBar.setVisibility(View.GONE);
        if (mCurrentBeautyIndex == 0) {
            mIndicatorSeekbar.setVisibility(View.INVISIBLE);
        }
        mCurrentFullBeautyIndex = -1;
        lastIndex = -1;
        mBeautyFullAdapter.setSelectedPosition(mCurrentFullBeautyIndex);
        mBeautyFullAdapter.notifyDataSetChanged();
    }

    private void resetFullBeautyView() {
        setFullBeauty(mBeautyFullItem.get(0));
        mCurrentFullBeautyIndex = 0;
        mBeautyFullAdapter.setSelectedPosition(mCurrentFullBeautyIndex);
        mBeautyFullAdapter.notifyDataSetChanged();
    }

    private void resetZeroFullBeautyView() {
        resetMakeup(true, false);
        resetFilterView(false);
        lastIndex = -1;

        mCurrentFullBeautyIndex = -1;
        mBeautyFullAdapter.setSelectedPosition(mCurrentFullBeautyIndex);
        mBeautyFullAdapter.notifyDataSetChanged();
        gpFullBeautySeekBar.setVisibility(View.GONE);
        mIndicatorSeekbar.setVisibility(View.INVISIBLE);

    }

    private void showMakeupTips() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), MultiLanguageUtils.getStr(R.string.toast_cancel_makeup), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBeautyParamsFromPackage() {
        //处理美颜
        int[] indexOfBeauty = {1, 2, 3, 6};
        for (int i = 0; i < indexOfBeauty.length; i++) {
            if (i == 0) {
                setBeautyListsForFullBeauty(indexOfBeauty[i], mCameraDisplay.getBeautifyParamsTypeBase());
            } else if (i == 1) {
                setBeautyListsForFullBeauty(indexOfBeauty[i], mCameraDisplay.getBeautifyParamsTypeProfessional());
            } else if (i == 2) {
                setBeautyListsForFullBeauty(indexOfBeauty[i], mCameraDisplay.getBeautifyParamsTypeMicro());
            } else if (i == 3) {
                setBeautyListsForFullBeauty(indexOfBeauty[i], mCameraDisplay.getBeautifyParamsTypeAdjust());
            }
        }

        if (mCameraDisplay.getNeedResetMakeupView() && mCurrentFullBeautyIndex!=-1) {
            clearFullBeautyView();
            resetMakeup(true, true);
            setMakeUpViewSelected(false, -1);

            for (int i = 0; i < Constants.MAKEUP_TYPE_COUNT; i++) {
                mMakeupOptionSelectedIndex.put(i, 0);
            }
            mLastStickerPosition = 0;
        }
        if (mCameraDisplay.getNeedResetFilterView()) {
            clearFullBeautyView();
            resetFilterView(true);
        }

        refreshSeekBar();
        mCameraDisplay.mBeautifyParamsTypeBase= EffectInfoDataHelper.getInstance().getBaseParams();

        mCameraDisplay.mBeautifyParamsTypeBase= EffectInfoDataHelper.getInstance().getBaseParams();

        mCameraDisplay.mBeautifyParamsTypeProfessional= EffectInfoDataHelper.getInstance().getProfessionalParams();

        // 微整形
        mCameraDisplay.mBeautifyParamsTypeMicro= EffectInfoDataHelper.getInstance().getMicroParams();

        // 调整
        mCameraDisplay.mBeautifyParamsTypeAdjust= EffectInfoDataHelper.getInstance().getAdjustParams();

        // 美妆
        String[] arrays = EffectInfoDataHelper.getInstance().getCurrentMakeUp();
        //setMakeupOptionSelectedIndex(arrays);
    }

}

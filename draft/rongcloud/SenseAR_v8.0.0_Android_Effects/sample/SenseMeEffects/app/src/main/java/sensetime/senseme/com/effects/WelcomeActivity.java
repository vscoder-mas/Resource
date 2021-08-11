package sensetime.senseme.com.effects;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sensetime.sensearsourcemanager.SenseArMaterialService;
import com.sensetime.sensearsourcemanager.SenseArServerType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import sensetime.senseme.com.effects.activity.CameraActivity;
import sensetime.senseme.com.effects.utils.Constants;
import sensetime.senseme.com.effects.utils.FileUtils;
import sensetime.senseme.com.effects.utils.MultiLanguageUtils;


public class WelcomeActivity extends Activity {
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 2;
    private static final int PERMISSION_REQUEST_INTERNET = 3;
    private static final String TAG = "WelcomeActivity";

    private static final int MSG_LOADING_SHOW = 100;
    private static final int MSG_LOADING_GONE = 101;

    private Context mContext;
    private ProgressBar mProgressBar;
    private TextView mLoading;
    private boolean mIsPaused = false;

    private Handler mHandleMessage = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_LOADING_GONE:
                    mLoading.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);

                    break;

                case MSG_LOADING_SHOW:
                    mLoading.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);

                    break;

                default:

                    break;
            }
        }
    };

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(mIsPaused)return;

            startActivity(new Intent(getApplicationContext(), CameraActivity.class));
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Constants.ACTIVITY_MODE_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        //正式
        SenseArMaterialService.setServerType(SenseArServerType.DomesticServer);
        //需要初始化一次
        SenseArMaterialService.shareInstance().initialize(getApplicationContext());
//        //测试
//        SenseArMaterialService.setServerType(SenseArServerType.DomesticTestServer);
//        //国际
//        SenseArMaterialService.setServerType(SenseArServerType.InternationalServer);
        setContentView(R.layout.activity_welcome);
        MultiLanguageUtils.initLanguageConfig();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mContext = this;
        mProgressBar = (ProgressBar) findViewById(R.id.process_loading);
        mProgressBar.setVisibility(View.VISIBLE);
        mLoading = (TextView)findViewById(R.id.tv_loading);
        mLoading.setVisibility(View.INVISIBLE);

        //关闭android p提示
        closeAndroidPDialog();

        //检查app权限
        checkCameraPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsPaused = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsPaused = true;
        finish();
    }

    @Override
    public void onBackPressed() {
        mHandler.removeCallbacks(mRunnable);
        mHandleMessage.removeMessages(MSG_LOADING_GONE);
        mHandleMessage.removeMessages(MSG_LOADING_SHOW);
        finish();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkWritePermission();
            } else {
                Toast.makeText(this, "Camera权限被拒绝", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkAudioPermission();
            } else {
                Toast.makeText(this, "存储卡读写权限被拒绝", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == PERMISSION_REQUEST_RECORD_AUDIO){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraActivity();
            } else {
                Toast.makeText(this, "麦克风权限被拒绝", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkCameraPermission(){

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.INTERNET)) {
                    requestPermissions(new String[]{Manifest.permission.INTERNET},
                            PERMISSION_REQUEST_INTERNET);
                }
            }
        }

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {}
                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            }else {
                checkWritePermission();
            }
        }else{
            startCameraActivity();
        }
    }

    private void checkWritePermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            }else {
                startCameraActivity();
            }
        }
    }

    private void checkAudioPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                        PERMISSION_REQUEST_RECORD_AUDIO);
            }else {
                startCameraActivity();
            }
        }
    }

    //打包使用
    private void startCameraActivity(){
        if (!STLicenseUtils.checkLicense(mContext)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "请检查License授权！", Toast.LENGTH_SHORT).show();
                }
            });
        }
        new Thread(){
            public void run() {
                Message msg = mHandleMessage.obtainMessage(MSG_LOADING_SHOW);
                mHandleMessage.sendMessage(msg);

                FileUtils.copyStickerFiles(mContext, "newEngine");
                FileUtils.copyStickerFiles(mContext, "makeup_eye");
                FileUtils.copyStickerFiles(mContext, "makeup_brow");
                FileUtils.copyStickerFiles(mContext, "makeup_blush");
                FileUtils.copyStickerFiles(mContext, "makeup_highlight");
                FileUtils.copyStickerFiles(mContext, "makeup_lip");
                FileUtils.copyStickerFiles(mContext, "makeup_eyeliner");
                FileUtils.copyStickerFiles(mContext, "makeup_eyelash");
                FileUtils.copyStickerFiles(mContext, "makeup_eyeball");
                FileUtils.copyStickerFiles(mContext, "makeup_hairdye");
                FileUtils.copyStickerFiles(mContext, "makeup_all");

                if(mIsPaused)return;

                Message msg1 = mHandleMessage.obtainMessage(MSG_LOADING_GONE);
                mHandleMessage.sendMessage(msg1);

                mHandler.postDelayed(mRunnable, 1000);
            }
        }.start();
    }

    private void closeAndroidPDialog(){
        try {
            @SuppressLint("PrivateApi") Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            @SuppressLint("PrivateApi") Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MultiLanguageUtils.appContext);
    }
}

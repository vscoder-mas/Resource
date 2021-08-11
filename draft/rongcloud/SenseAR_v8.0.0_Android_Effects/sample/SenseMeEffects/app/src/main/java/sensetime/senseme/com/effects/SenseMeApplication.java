package sensetime.senseme.com.effects;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;

import com.tencent.bugly.crashreport.CrashReport;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import sensetime.senseme.com.effects.utils.Constants;
import sensetime.senseme.com.effects.utils.MultiLanguageUtils;
import sensetime.senseme.com.effects.utils.SpUtils;

/**
 * Created by sensetime on 18-1-26.
 */

public class SenseMeApplication extends Application {

    public static final boolean BUG_COLLECTION = true;
    public static final String TAG = "SenseMeApplication";
    private static Context mContext;
    private final String PROCESS = "sensetime.senseme.com.effects";

    @Override
    public void onCreate() {
        super.onCreate();
        if (BUG_COLLECTION) {
            CrashReport.initCrashReport(getApplicationContext(), "414601badf", false);
        }

        closeAndroidPDialog();

        mContext = this;
        MultiLanguageUtils.initLanguageConfig();
        new WebView(this).destroy();
        Log.e("thread", "  线程值  " + Thread.currentThread());
        Constants.init(this);
        SpUtils.init(this);
    }

    private void initWebView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName();
            if (!PROCESS.equals(processName)) {
                WebView.setDataDirectorySuffix("senseTime");
            }
        }
    }

    private void closeAndroidPDialog() {
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
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
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        initWebView();
    }

    public static Context getContext() {
        return mContext;
    }
}

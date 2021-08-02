package com.tinysoft.rongcloud.test.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tinysoft.rongcloud.test.R;
import com.tinysoft.rongcloud.test.util.PermissionsUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final int REQUEST_CODE_PERMISSIONS = 10;
    private Button btnMeeting;
    private Button btnLive;
    private PowerManager.WakeLock wakeLock;
    private String[] PERMISSIONS = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnMeeting = (Button) findViewById(R.id.button_meeting_id);
        btnMeeting.setOnClickListener(this);
        btnLive = (Button) findViewById(R.id.button_live_id);
        btnLive.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            PermissionsUtils.checkAndRequestMorePermissions(this, PERMISSIONS, REQUEST_CODE_PERMISSIONS,
                    new PermissionsUtils.PermissionRequestSuccessCallBack() {
                        @Override
                        public void onHasPermission() {
                            StringBuilder sb = new StringBuilder();
                            for (String str : PERMISSIONS) {
                                sb.append(str + ",");
                            }
                            String msg = String.format("%s has allowed !", sb.substring(0, sb.length() - 1));
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onResume() {
        super.onResume();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "liveTAG");
        wakeLock.acquire();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionsUtils.isPermissionRequestSuccess(grantResults)) {
            StringBuilder sb = new StringBuilder();
            for (String str : PERMISSIONS) {
                sb.append(str + ",");
            }
            String msg = String.format("%s has allowed !", sb.substring(0, sb.length() - 1));
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.button_meeting_id) {
            Intent intent = new Intent(this, MeetingActivity.class);
            startActivity(intent);
        } else if (viewId == R.id.button_live_id) {
            Intent intent = new Intent(this, LiveActivity.class);
            startActivity(intent);
        }
    }
}


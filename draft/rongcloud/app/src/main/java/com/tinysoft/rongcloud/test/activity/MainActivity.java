package com.tinysoft.rongcloud.test.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tinysoft.rongcloud.test.R;
import com.tinysoft.rongcloud.test.util.PermissionsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.rongcloud.rtc.api.RCRTCConfig;
import cn.rongcloud.rtc.api.RCRTCEngine;
import cn.rongcloud.rtc.api.RCRTCRemoteUser;
import cn.rongcloud.rtc.api.RCRTCRoom;
import cn.rongcloud.rtc.api.callback.IRCRTCResultCallback;
import cn.rongcloud.rtc.api.callback.IRCRTCResultDataCallback;
import cn.rongcloud.rtc.api.callback.IRCRTCRoomEventsListener;
import cn.rongcloud.rtc.api.stream.RCRTCInputStream;
import cn.rongcloud.rtc.api.stream.RCRTCVideoInputStream;
import cn.rongcloud.rtc.api.stream.RCRTCVideoStreamConfig;
import cn.rongcloud.rtc.api.stream.RCRTCVideoView;
import cn.rongcloud.rtc.base.RCRTCMediaType;
import cn.rongcloud.rtc.base.RCRTCParamsType;
import cn.rongcloud.rtc.base.RCRTCStreamType;
import cn.rongcloud.rtc.base.RTCErrorCode;
import io.rong.imlib.RongIMClient;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = MainActivity.class.getSimpleName();
    private String mToken = "Mv1XFZDLe6YJWyih+tpsnr7m8k7VOYicft9MoV63wY0=@27wv.cn.rongnav.com;27wv.cn.rongcfg.com";
    //    private String mToken = "W5cY7PHJkPTzpK7RLFSXVtTOVQD1DHxCRV4SGtT5ORQ=@27wv.cn.rongnav.com;27wv.cn.rongcfg.com";
    private String mRoomId = "6104708055591928163403682037563228668304497123698583708082708364";
    private Button btnConn;
    private FrameLayout layoutMaster;
    private FrameLayout layoutRemote;
    private RCRTCRoom rcrtcRoom = null;
    private Button btnMaster;
    private Button btnSlave;
    private TextView tvRole;
    private PowerManager.WakeLock wakeLock;
    private final int REQUEST_CODE_PERMISSIONS = 10;

    private String[] PERMISSIONS = new String[]{Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnConn = (Button) findViewById(R.id.button_conn_id);
        btnConn.setOnClickListener(this);
        layoutMaster = (FrameLayout) findViewById(R.id.layout_master_id);
        layoutRemote = (FrameLayout) findViewById(R.id.layout_remote_id);
        btnMaster = (Button) findViewById(R.id.button_master_id);
        btnMaster.setOnClickListener(this);
        btnSlave = (Button) findViewById(R.id.button_slave_id);
        btnSlave.setOnClickListener(this);
        tvRole = (TextView) findViewById(R.id.textview_role_id);

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionsUtils.isPermissionRequestSuccess(grantResults)) {
            StringBuilder sb = new StringBuilder();
            for (String str : PERMISSIONS) {
                sb.append(str + ",");
            }
            String msg = String.format("%s has allowed !", sb.substring(0, sb.length() - 1));
            Log.d(TAG, "onRequestPermissionsResult: " + msg);
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    private String genRoomId() {
        Random random = new Random();
        String result = "";
        for (int i = 0; i < 64; i++) {
            result += random.nextInt(10);
        }

        return result;
    }

    /**
     * 房间事件监听文档：https://www.rongcloud.cn/docs/api/android/rtclib_v4/cn/rongcloud/rtc/api/callback/IRCRTCRoomEventsListener.html
     */
    private IRCRTCRoomEventsListener roomEventsListener = new IRCRTCRoomEventsListener() {
        /**
         * 房间内用户发布资源
         *
         * @param rcrtcRemoteUser 远端用户
         * @param list    发布的资源
         */
        @Override
        public void onRemoteUserPublishResource(RCRTCRemoteUser rcrtcRemoteUser, final List<RCRTCInputStream> list) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (RCRTCInputStream inputStream : list) {
                        if (inputStream.getMediaType() == RCRTCMediaType.VIDEO) {
                            RCRTCVideoView remoteVideoView = new RCRTCVideoView(getApplicationContext());
                            layoutRemote.removeAllViews();
                            //将远端视图添加至布局
                            layoutRemote.addView(remoteVideoView);
                            ((RCRTCVideoInputStream) inputStream).setVideoView(remoteVideoView);
                            //选择订阅大流或是小流。默认小流
                            ((RCRTCVideoInputStream) inputStream).setStreamType(RCRTCStreamType.NORMAL);
                        }
                    }

                    //TODO 按需在此订阅远端用户发布的资源
                    rcrtcRoom.getLocalUser().subscribeStreams(list, new IRCRTCResultCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(MainActivity.this, "订阅成功", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailed(RTCErrorCode rtcErrorCode) {
                            Toast.makeText(MainActivity.this, "订阅失败：" + rtcErrorCode, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        @Override
        public void onRemoteUserMuteAudio(RCRTCRemoteUser rcrtcRemoteUser, RCRTCInputStream rcrtcInputStream, boolean b) {

        }

        @Override
        public void onRemoteUserMuteVideo(RCRTCRemoteUser rcrtcRemoteUser, RCRTCInputStream rcrtcInputStream, boolean b) {
        }


        @Override
        public void onRemoteUserUnpublishResource(RCRTCRemoteUser rcrtcRemoteUser, List<RCRTCInputStream> list) {
            layoutRemote.removeAllViews();
        }

        /**
         * 用户加入房间
         *
         * @param rcrtcRemoteUser 远端用户
         */
        @Override
        public void onUserJoined(final RCRTCRemoteUser rcrtcRemoteUser) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "用户：" + rcrtcRemoteUser.getUserId() + " 加入房间", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "- IRCRTCRoomEventsListener onUserJoined: " + "用户：" + rcrtcRemoteUser.getUserId() + " 加入房间");
                }
            });
        }

        /**
         * 用户离开房间
         *
         * @param rcrtcRemoteUser 远端用户
         */
        @Override
        public void onUserLeft(RCRTCRemoteUser rcrtcRemoteUser) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    layoutRemote.removeAllViews();
                    Log.d(TAG, "- IRCRTCRoomEventsListener onUserLeft: " + "用户：" + rcrtcRemoteUser.getUserId() + " left room !");
                }
            });
        }

        @Override
        public void onUserOffline(RCRTCRemoteUser rcrtcRemoteUser) {
            Log.d(TAG, "- IRCRTCRoomEventsListener onUserOffline: " + "用户：" + rcrtcRemoteUser.getUserId() + " offline !");
        }

        /**
         * 自己退出房间。 例如断网退出等
         * @param i 状态码
         */
        @Override
        public void onLeaveRoom(int i) {

        }
    };

    private void joinRoom() {
        RCRTCConfig config = RCRTCConfig.Builder.create()
                //是否硬解码
                .enableHardwareDecoder(true)
                //是否硬编码
                .enableHardwareEncoder(true)
                .build();
        RCRTCEngine.getInstance().init(getApplicationContext(), config);

        RCRTCVideoStreamConfig videoConfigBuilder = RCRTCVideoStreamConfig.Builder.create()
                //设置分辨率
                .setVideoResolution(RCRTCParamsType.RCRTCVideoResolution.RESOLUTION_480_640)
                //设置帧率
                .setVideoFps(RCRTCParamsType.RCRTCVideoFps.Fps_15)
                //设置最小码率，480P下推荐200
                .setMinRate(200)
                //设置最大码率，480P下推荐900
                .setMaxRate(900)
                .build();
        RCRTCEngine.getInstance().getDefaultVideoStream().setVideoConfig(videoConfigBuilder);

        // 创建本地视频显示视图
        RCRTCVideoView rongRTCVideoView = new RCRTCVideoView(getApplicationContext());
        RCRTCEngine.getInstance().getDefaultVideoStream().setVideoView(rongRTCVideoView);
        //TODO 将本地视图添加至FrameLayout布局，需要开发者自行创建布局
        layoutMaster.addView(rongRTCVideoView);
        //开启摄像头采集视频数据
        RCRTCEngine.getInstance().getDefaultVideoStream().startCamera(null);

        //mRoomId：长度 64 个字符，可包含：`A-Z`、`a-z`、`0-9`、`+`、`=`、`-`、`_`
        RCRTCEngine.getInstance().joinRoom(mRoomId, new IRCRTCResultDataCallback<RCRTCRoom>() {
            @Override
            public void onSuccess(RCRTCRoom rcrtcRoom) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        //TODO 加入房间成功后，发布资源前需要开启摄像头采集视频数据，否则对端无法观看
//                        RCRTCEngine.getInstance().getDefaultVideoStream().startCamera(callback);
                        Toast.makeText(MainActivity.this, "加入房间成功 !", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "加入房间成功 !");

                        //注册房间事件回调
                        MainActivity.this.rcrtcRoom = rcrtcRoom;
                        rcrtcRoom.registerRoomListener(roomEventsListener);
                        //加入房间成功后，发布默认音视频流
                        publishDefaultAVStream(rcrtcRoom);
                        //加入房间成功后，如果房间中已存在用户且发布了音、视频流，就订阅远端用户发布的音视频流.
                        subscribeAVStream(rcrtcRoom);
                    }
                });
            }

            @Override
            public void onFailed(RTCErrorCode rtcErrorCode) {
                Toast.makeText(MainActivity.this, "加入房间失败：" + rtcErrorCode.getReason(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "- onFailed: joinRoom errCode:" + rtcErrorCode.getReason());
            }
        });
    }

    private void subscribeAVStream(RCRTCRoom rtcRoom) {
        if (rtcRoom == null || rtcRoom.getRemoteUsers() == null) {
            return;
        }

        List<RCRTCInputStream> inputStreams = new ArrayList<>();
        for (final RCRTCRemoteUser remoteUser : rcrtcRoom.getRemoteUsers()) {
            if (remoteUser.getStreams().size() == 0) {
                continue;
            }

            List<RCRTCInputStream> userStreams = remoteUser.getStreams();
            for (RCRTCInputStream inputStream : userStreams) {
                if (inputStream.getMediaType() == RCRTCMediaType.VIDEO) {
                    //选择订阅大流或是小流。默认小流
                    ((RCRTCVideoInputStream) inputStream).setStreamType(RCRTCStreamType.NORMAL);
                    //创建VideoView并设置到stream
                    RCRTCVideoView videoView = new RCRTCVideoView(getApplicationContext());
                    ((RCRTCVideoInputStream) inputStream).setVideoView(videoView);
                    //将远端视图添加至布局
                    layoutRemote.addView(videoView);
                }
            }
            inputStreams.addAll(remoteUser.getStreams());
        }

        if (inputStreams.size() == 0) {
            return;
        }

        rcrtcRoom.getLocalUser().subscribeStreams(inputStreams, new IRCRTCResultCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "订阅成功", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "- onSuccess: subscribeStreams .");
            }

            @Override
            public void onFailed(RTCErrorCode errorCode) {
                Toast.makeText(MainActivity.this, "订阅失败：" + errorCode.getReason(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "- onFailed: subscribeStreams .");
            }
        });
    }

    private void publishDefaultAVStream(RCRTCRoom rcrtcRoom) {
        rcrtcRoom.getLocalUser().publishDefaultStreams(new IRCRTCResultCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "发布资源成功", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "- onSuccess: publishDefaultStreams ");
            }

            @Override
            public void onFailed(RTCErrorCode rtcErrorCode) {
                Toast.makeText(MainActivity.this, "发布失败：" + rtcErrorCode.getReason(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "- onFailed: publishDefaultStreams errCode:" + rtcErrorCode.getReason());
            }
        });
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.button_conn_id) {
            RongIMClient.connect(mToken, new RongIMClient.ConnectCallback() {
                @Override
                public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus code) {

                }

                @Override
                public void onSuccess(String s) {
                    //TODO 服务连接成功后，可以进行音视频加入房间操作 RCRTCEngine.getInstance().joinRoom(...)
                    Log.d(TAG, "- onSuccess s:" + s);
                    joinRoom();
                }

                @Override
                public void onError(RongIMClient.ConnectionErrorCode errorCode) {

                }
            });
        } else if (viewId == R.id.button_master_id) {
            //master: userId:mas
            mToken = "Mv1XFZDLe6YJWyih+tpsnr7m8k7VOYicft9MoV63wY0=@27wv.cn.rongnav.com;27wv.cn.rongcfg.com";
            tvRole.setText("master");
        } else if (viewId == R.id.button_slave_id) {
            //slave: userId:shuai
            mToken = "W5cY7PHJkPTzpK7RLFSXVtTOVQD1DHxCRV4SGtT5ORQ=@27wv.cn.rongnav.com;27wv.cn.rongcfg.com";
            tvRole.setText("slave");
        }
    }
}


package com.tiysoft.rongrtc.v5.activity;

import static cn.rongcloud.rtc.base.RCRTCLiveRole.BROADCASTER;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tinysoft.rongrtc.v5.R;
import com.tinysoft.rongrtc.v5.manager.VideoViewManager;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.rtc.api.RCRTCEngine;
import cn.rongcloud.rtc.api.RCRTCRemoteUser;
import cn.rongcloud.rtc.api.RCRTCRoom;
import cn.rongcloud.rtc.api.RCRTCRoomConfig;
import cn.rongcloud.rtc.api.callback.IRCRTCResultCallback;
import cn.rongcloud.rtc.api.callback.IRCRTCResultDataCallback;
import cn.rongcloud.rtc.api.stream.RCRTCInputStream;
import cn.rongcloud.rtc.api.stream.RCRTCLiveInfo;
import cn.rongcloud.rtc.api.stream.RCRTCOutputStream;
import cn.rongcloud.rtc.api.stream.RCRTCVideoInputStream;
import cn.rongcloud.rtc.api.stream.RCRTCVideoOutputStream;
import cn.rongcloud.rtc.api.stream.RCRTCVideoView;
import cn.rongcloud.rtc.base.RCRTCMediaType;
import cn.rongcloud.rtc.base.RCRTCRoomType;
import cn.rongcloud.rtc.base.RCRTCStreamType;
import cn.rongcloud.rtc.base.RTCErrorCode;
import io.rong.imlib.RongIMClient;

public class MasterActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = MasterActivity.class.getSimpleName();
    private String mToken = "Mv1XFZDLe6YJWyih+tpsnr7m8k7VOYicft9MoV63wY0=@27wv.cn.rongnav.com;27wv.cn.rongcfg.com";
    private String mRoomId = "6104708055591928163403682037563228668304497123698583708082708364";
    private Button btnConn;
    private FrameLayout layoutMaster;
    private RCRTCRoom rcrtcRoom = null;
    private VideoViewManager videoViewManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_layout);
        btnConn = (Button) findViewById(R.id.button_conn_id);
        btnConn.setOnClickListener(this);
        layoutMaster = (FrameLayout) findViewById(R.id.layout_master_id);
        layoutMaster.post(() -> videoViewManager = new VideoViewManager(layoutMaster, layoutMaster.getWidth(),
                layoutMaster.getHeight()));
    }

    private void getVideoStream(List<RCRTCVideoOutputStream> outputStreams, List<RCRTCVideoInputStream> inputStreams) {
        for (final RCRTCRemoteUser remoteUser : rcrtcRoom.getRemoteUsers()) {
            if (remoteUser.getStreams().size() == 0) {
                continue;
            }

            List<RCRTCInputStream> userStreams = remoteUser.getStreams();
            for (RCRTCInputStream i : userStreams) {
                if (i.getMediaType() == RCRTCMediaType.VIDEO) {
                    inputStreams.add((RCRTCVideoInputStream) i);
                }
            }
        }

        for (RCRTCOutputStream o : rcrtcRoom.getLocalUser().getStreams()) {
            if (o.getMediaType() == RCRTCMediaType.VIDEO) {
                outputStreams.add((RCRTCVideoOutputStream) o);
            }
        }
    }

    /**
     * 当远端或本端视频流发生变化时全量更新ui
     */
    void updateVideoView(List<RCRTCVideoOutputStream> outputStreams, List<RCRTCVideoInputStream> inputStreams) {
        ArrayList<RCRTCVideoView> list = new ArrayList<>();
        if (null != outputStreams) {
            for (RCRTCVideoOutputStream o : outputStreams) {
                RCRTCVideoView rongRTCVideoView = new RCRTCVideoView(MasterActivity.this);
                o.setVideoView(rongRTCVideoView);
                list.add(rongRTCVideoView);
            }
        }

        if (null != inputStreams) {
            for (RCRTCVideoInputStream i : inputStreams) {
                RCRTCVideoView rongRTCVideoView = new RCRTCVideoView(MasterActivity.this);
                i.setVideoView(rongRTCVideoView);
                list.add(rongRTCVideoView);
            }
        }

        videoViewManager.update(list);
    }

    /**
     * 订阅远端用户资源
     */
    public void subscribeAVStream() {
        if (rcrtcRoom == null || rcrtcRoom.getRemoteUsers() == null) {
            Log.d(TAG, "- subscribeAVStream: if (rcrtcRoom == null || rcrtcRoom.getRemoteUsers() == null) {");
            return;
        }

        List<RCRTCInputStream> subscribeInputStreams = new ArrayList<>();
        for (final RCRTCRemoteUser remoteUser : rcrtcRoom.getRemoteUsers()) {
            if (remoteUser.getStreams().size() == 0) {
                continue;
            }

            List<RCRTCInputStream> userStreams = remoteUser.getStreams();
            for (RCRTCInputStream inputStream : userStreams) {
                if (inputStream.getMediaType() == RCRTCMediaType.VIDEO) {
                    //选择订阅大流或是小流。默认小流
                    ((RCRTCVideoInputStream) inputStream).setStreamType(RCRTCStreamType.NORMAL);
                }
            }

            subscribeInputStreams.addAll(userStreams);
        }

        if (subscribeInputStreams.size() == 0) {
            Log.d(TAG, "- subscribeAVStream: subscribeInputStreams.size() == 0");
            return;
        }

        rcrtcRoom.getLocalUser().subscribeStreams(subscribeInputStreams, new IRCRTCResultCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(MasterActivity.this, "订阅成功", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "- subscribeStreams onSuccess");
            }

            @Override
            public void onFailed(RTCErrorCode errorCode) {
                Toast.makeText(MasterActivity.this, "订阅成功", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "- subscribeStreams onFailed: errorCode:" + errorCode);
            }
        });
    }

    private void joinRoom() {
        RCRTCRoomConfig roomConfig = RCRTCRoomConfig.Builder.create()
                // 根据实际场景，选择音视频直播：LIVE_AUDIO_VIDEO 或音频直播：LIVE_AUDIO
                .setRoomType(RCRTCRoomType.LIVE_AUDIO_VIDEO)
                .setLiveRole(BROADCASTER)
                .build();

        RCRTCEngine.getInstance().joinRoom(mRoomId, roomConfig, new IRCRTCResultDataCallback<RCRTCRoom>() {
            @Override
            public void onSuccess(final RCRTCRoom room) {
                runOnUiThread(() -> {
                    Log.d(TAG, "- joinRoom onSuccess room");
                    List<RCRTCVideoOutputStream> outputStreams = new ArrayList<>();
                    List<RCRTCVideoInputStream> inputStreams = new ArrayList<>();
                    getVideoStream(outputStreams, inputStreams);
                    updateVideoView(outputStreams, inputStreams);

                    // 保存房间对象
                    MasterActivity.this.rcrtcRoom = room;
                    RCRTCEngine.getInstance().getDefaultVideoStream().startCamera(null);
                    room.getLocalUser().publishDefaultLiveStreams(new IRCRTCResultDataCallback<RCRTCLiveInfo>() {
                        @Override
                        public void onSuccess(RCRTCLiveInfo liveInfo) {
                            Log.d(TAG, "- publishDefaultLiveStreams onSuccess");
                        }

                        @Override
                        public void onFailed(RTCErrorCode code) {
                            Log.d(TAG, "- publishDefaultLiveStreams onFailed errorCode:" + code);
                        }
                    });

                    subscribeAVStream();
                });
            }

            @Override
            public void onFailed(RTCErrorCode code) {
                Log.d(TAG, "- joinRoom onFailed: errorCode:" + code);
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
                    Log.d(TAG, "- connect onSuccess userId:" + s);
                    joinRoom();
                }

                @Override
                public void onError(RongIMClient.ConnectionErrorCode errorCode) {

                }
            });
        }
    }
}

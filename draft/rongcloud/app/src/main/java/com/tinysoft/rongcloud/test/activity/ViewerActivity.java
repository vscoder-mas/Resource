package com.tinysoft.rongcloud.test.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.tinysoft.rongcloud.test.R;

import cn.rongcloud.rtc.api.RCRTCConfig;
import cn.rongcloud.rtc.api.RCRTCEngine;
import cn.rongcloud.rtc.api.callback.IRCRTCResultCallback;
import cn.rongcloud.rtc.api.callback.RCRTCLiveCallback;
import cn.rongcloud.rtc.api.stream.RCRTCAudioInputStream;
import cn.rongcloud.rtc.api.stream.RCRTCVideoInputStream;
import cn.rongcloud.rtc.api.stream.RCRTCVideoView;
import cn.rongcloud.rtc.base.RCRTCAVStreamType;
import cn.rongcloud.rtc.base.RTCErrorCode;
import io.rong.imlib.RongIMClient;

public class ViewerActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = ViewerActivity.class.getSimpleName();
    private Button btnSubscribe;
    private Button btnUnsubscribe;
    private FrameLayout layoutViewer;
    private String mLiveUrl = "fcONuH3d/8we7uDdGarslQig79JP7f/XE6TlzR/t7tcQ+bWIfdi8iFPyvYhT8r2IU/G5ikf3vYhF883NHq/izRnCjSQ8gu7AMJfIizCX6sA8gNfBJPK0zCTytNUz8d/UJ5fMizCH29Uyh9/VMofAiTC5y9Aw8dyNM6nL0jOUxIwnhMPVM4LP+TOpyM8zh+7PMofMiTOX2I0wl+bBMofIijC53M8wudSMMKnMwjO52IowucTBMofUijKHwM8zh9yNM7nIwTC51I0yh9iMMLnuzzKHzIwwqeqMMofqjDKCzIV9w4zDduBFiX32/9siru7nG/Tp3RjzuohIpbWMG/u+jU7y7IsZ+ruJHvbvgBmg64wikeLWGoDh1win3+w+nLy4fcONjzGhsrhIse7nEKDS3kqn6N1N9L2NG/u53kXwuItMor7cRPW820ihtdwepbnnL6zj3z6v4s0Zkdn7IvONuH3D3k7zRQ==";
    private String mToken = "W5cY7PHJkPTzpK7RLFSXVtTOVQD1DHxCRV4SGtT5ORQ=@27wv.cn.rongnav.com;27wv.cn.rongcfg.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_layout);
        btnSubscribe = (Button) findViewById(R.id.button_subscribe_id);
        btnSubscribe.setOnClickListener(this);
        btnUnsubscribe = (Button) findViewById(R.id.button_unsubscribe_id);
        btnUnsubscribe.setOnClickListener(this);
        layoutViewer = (FrameLayout) findViewById(R.id.layout_local_id);
    }

    private void subscribe() {
        //TODO RCRTCConfig.Builder 请参考API文档：https://www.rongcloud.cn/docs/api/android/rtclib_v4/cn/rongcloud/rtc/api/RCRTCConfig.Builder.html
        RCRTCConfig config = cn.rongcloud.rtc.api.RCRTCConfig.Builder.create()
                //是否硬解码
                .enableHardwareDecoder(true)
                //是否硬编码
                .enableHardwareEncoder(true)
                .build();
        RCRTCEngine.getInstance().init(getApplicationContext(), config);
        // 仅直播模式下观众可用。 作为观众，直接观看主播的直播，无需加入房间，通过传入主播的 url 进行订阅
        RCRTCEngine.getInstance().subscribeLiveStream(mLiveUrl, RCRTCAVStreamType.AUDIO_VIDEO, new RCRTCLiveCallback() {
            @Override
            public void onSuccess() {
                //订阅成功，后续会根据订阅的类型，触发 onAudioStreamReceived 和 onVideoStreamReceived方法
                Log.d(TAG, "- subscribeLiveStream onSuccess");
            }

            @Override
            public void onVideoStreamReceived(final RCRTCVideoInputStream stream) {
                //收到视频流。操作UI需要转到 UI 线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //创建 RCRTCVideoView
                        RCRTCVideoView videoView = new RCRTCVideoView(getApplicationContext());
                        //给input stream设置用于显示视频的视图
                        stream.setVideoView(videoView);
                        //将 RCRTCVideoView 添加到自己的Layout容器中
                        layoutViewer.addView(videoView);
                    }
                });
            }

            @Override
            public void onAudioStreamReceived(RCRTCAudioInputStream stream) {
                //收到音频流
                Log.d(TAG, "- subscribeLiveStream onAudioStreamReceived audio:" + stream);
            }

            @Override
            public void onFailed(RTCErrorCode errorCode) {
                //订阅失败, 详情查看 errorCode
                Log.e(TAG, "subscribeLiveStream onFailed: errorCode:" + errorCode);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.button_subscribe_id) {
            RongIMClient.connect(mToken, new RongIMClient.ConnectCallback() {
                @Override
                public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus code) {
                }

                @Override
                public void onSuccess(String s) {
                    //TODO 服务连接成功后，可以进行音视频加入房间操作 RCRTCEngine.getInstance().joinRoom(...)
                    Log.d(TAG, "- onSuccess userId:" + s);
                    subscribe();
                }

                @Override
                public void onError(RongIMClient.ConnectionErrorCode errorCode) {

                }
            });
        } else if (viewId == R.id.button_unsubscribe_id) {
            RCRTCEngine.getInstance().unsubscribeLiveStream(mLiveUrl, new IRCRTCResultCallback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "- unsubscribeLiveStream onSuccess");
                }

                @Override
                public void onFailed(final RTCErrorCode errorCode) {
                    Log.e(TAG, "- unsubscribeLiveStream onFailed: errorCode:" + errorCode);
                }
            });
        }
    }
}

package com.tinysoft.rongcloud.test.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tinysoft.rongcloud.test.R;

import io.rong.imlib.RongIMClient;

public class LiveActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = LiveActivity.class.getSimpleName();
    private TextView tvTitle;
    private Button btnConn;
    private Button btnMaster;
    private Button btnSlave;
    private FrameLayout layoutMaster;
    private FrameLayout layoutRemote;
    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_layout);
        tvTitle = (TextView) findViewById(R.id.textview_role_id);
        btnConn = (Button) findViewById(R.id.button_conn_id);
        btnConn.setOnClickListener(this);
        btnMaster = (Button) findViewById(R.id.button_master_id);
        btnMaster.setOnClickListener(this);
        btnSlave = (Button) findViewById(R.id.button_slave_id);
        btnSlave.setOnClickListener(this);
        layoutMaster = (FrameLayout) findViewById(R.id.layout_master_id);
        layoutRemote = (FrameLayout) findViewById(R.id.layout_remote_id);
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
                    Log.d(TAG, "- onSuccess userId:" + s);
//                    joinRoom();
                }

                @Override
                public void onError(RongIMClient.ConnectionErrorCode errorCode) {

                }
            });
        } else if (viewId == R.id.button_master_id) {
            //master: userId:mas
            mToken = "Mv1XFZDLe6YJWyih+tpsnr7m8k7VOYicft9MoV63wY0=@27wv.cn.rongnav.com;27wv.cn.rongcfg.com";
            tvTitle.setText("master");
        } else if (viewId == R.id.button_slave_id) {
            //slave: userId:shuai
            mToken = "W5cY7PHJkPTzpK7RLFSXVtTOVQD1DHxCRV4SGtT5ORQ=@27wv.cn.rongnav.com;27wv.cn.rongcfg.com";
            tvTitle.setText("slave");
        }
    }
}

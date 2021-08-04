package com.qihoo360.test;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MainActivity extends Activity {
    private String TAG = MainActivity.class.getSimpleName();
    private TextView tvHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvHello = (TextView) findViewById(R.id.textview_hello_id);
        tvHello.setText(JniSupport.getCrashId() + "");


//        tvHello.setText(JniSupport.getBase64());
//        Log.e("MainActivity", "-> onCreate() base64=" + JniSupport.getBase64());

        String md5 = getSignatureMD5(this, "com.qihoo360.test");
        Log.e("MainActivity", "-> onCreate() md5=" + md5);

        findViewById(R.id.button_goto_scan_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = JniSupport.encrypt(MainActivity.this, "123");
                Log.e("MainActivity", "-> onClick() encrypt=" + result);
                result = JniSupport.decrypt(MainActivity.this, "gCTJw5JI4+b8uBRpN+tedg==");
                Log.e("MainActivity", "-> onClick() decrypt=" + result);
            }
        });
    }

    private String getSignatureMD5(Context context, String packageName) {
        //获取包管理器
        PackageManager pm = context.getPackageManager();

        //返回包括在包中的签名信息
        int flags = PackageManager.GET_SIGNATURES;
        PackageInfo packageInfo = null;
        try {
            //获得包的所有内容信息类
            packageInfo = pm.getPackageInfo(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return "";
        }
        //签名信息
        Signature[] signatures = packageInfo.signatures;
        byte[] cert = signatures[0].toByteArray();
//        for(int i=0;i<cert.length;i++){
//            Log.i("ly-s",cert[i]+"");
//        }

        String hexString = null;
        try {
            //加密算法的类，这里的参数可以使MD4,MD5等加密算法
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(cert);
            //获得公钥
            byte[] publicKey = md.digest();
            //字节到十六进制的格式转换
            hexString = byte2HexFormatted(publicKey);
            hexString = hexString.replaceAll(":", "");

        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return hexString;
    }

    //这里是将获取到得编码进行16进制转换
    private static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1)
                h = "0" + h;
            if (l > 2)
                h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1))
                str.append(':');
        }
        return str.toString();
    }
}
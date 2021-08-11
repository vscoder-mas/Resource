package sensetime.senseme.com.effects.encoder.mediacodec.utils;

import android.content.res.Resources;

import java.io.InputStream;

import sensetime.senseme.com.effects.SenseMeApplication;

/**
 * Created by qqche_000 on 2018/6/3.
 * 用于OpenGl的工具类
 */

public class ShaderUtils {
    //通过资源路径加载shader脚本文件
    public static String uRes(String path) {
        Resources resources = SenseMeApplication.getContext().getResources();
        StringBuilder result = new StringBuilder();
        try {
            InputStream is = resources.getAssets().open(path);
            int ch;
            byte[] buffer = new byte[1024];
            while (-1 != (ch = is.read(buffer))) {
                result.append(new String(buffer, 0, ch));
            }
        } catch (Exception e) {
            return null;
        }
        return result.toString().replaceAll("\\r\\n", "\n");
    }
}

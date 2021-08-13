package com.rongcloud.st.beauty.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sensetime on 16-11-16.
 */

public class STFileUtils {
    public static final String MODEL_NAME_ACTION = "models/M_SenseME_Face_Video_7.0.0.model";
    public static final String MODEL_NAME_FACE_ATTRIBUTE = "models/M_SenseME_Attribute_1.0.1.model";
    public static final String MODEL_NAME_EYEBALL_CENTER = "models/M_Eyeball_Center.model";
    public static final String MODEL_NAME_EYEBALL_CONTOUR = "models/M_SenseME_Iris_2.0.0.model";
    public static final String MODEL_NAME_FACE_EXTRA = "models/M_SenseME_Face_Extra_Advanced_6.0.11.model";
    public static final String MODEL_NAME_HAND = "models/M_SenseME_Hand_5.4.0.model";
    public static final String MODEL_NAME_SEGMENT = "models/M_SenseME_Segment_4.10.8.model";
    public static final String MODEL_NAME_BODY_FOURTEEN = "models/M_SenseME_Body_Fourteen_1.2.0.model";
    public static final String MODEL_NAME_AVATAR_CORE = "models/M_SenseME_Avatar_Core_2.0.0.model";
    public static final String MODEL_NAME_CATFACE_CORE = "models/M_SenseME_CatFace_3.0.0.model";
    public static final String MODEL_NAME_AVATAR_HELP = "models/M_SenseME_Avatar_Help_2.2.0.model";
    public static final String MODEL_NAME_TONGUE = "models/M_Align_DeepFace_Tongue_1.0.0.model";
    public static final String MODEL_NAME_HAIR = "models/M_SenseME_Segment_Hair_1.3.4.model";
    public static final String MODEL_NAME_LIPS_PARSING = "models/M_SenseAR_Segment_MouthOcclusion_FastV1_1.1.2.model";
    public static final String HEAD_SEGMENT_MODEL_NAME = "models/M_SenseME_Segment_Head_1.0.3.model";

    public static String getActionModelName() {
        return MODEL_NAME_ACTION;
    }

    public static List<String> copyFilterModelFiles(Context context, String index) {
        String files[] = null;
        ArrayList<String> modelFiles = new ArrayList<String>();

        try {
            files = context.getAssets().list(index);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String folderpath = null;
        File dataDir = context.getExternalFilesDir(null);
        if (dataDir != null) {
            folderpath = dataDir.getAbsolutePath() + File.separator + index;

            File folder = new File(folderpath);

            if (!folder.exists()) {
                folder.mkdir();
            }
        }
        for (int i = 0; i < files.length; i++) {
            String str = files[i];
            if (str.indexOf(".model") != -1) {
                copyFileIfNeed(context, str, index);
            }
        }

        File file = new File(folderpath);
        File[] subFile = file.listFiles();

        if (subFile == null || subFile.length == 0) {
            return modelFiles;
        }

        for (int i = 0; i < subFile.length; i++) {
            // 判断是否为文件夹
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getAbsolutePath();
                String path = subFile[i].getPath();
                // 判断是否为model结尾
                if (filename.trim().toLowerCase().endsWith(".model") && filename.indexOf("filter") != -1) {
                    modelFiles.add(filename);
                }
            }
        }

        return modelFiles;
    }

    private static String getFilePath(Context context, String fileName) {
        String path = null;
        File dataDir = context.getApplicationContext().getExternalFilesDir(null);
        if (dataDir != null) {
            path = dataDir.getAbsolutePath() + File.separator + fileName;
        }

        return path;
    }

    private static boolean copyFileIfNeed(Context context, String fileName, String className) {
        String path = getFilePath(context, className + File.separator + fileName);
        if (path != null) {
            File file = new File(path);
            if (!file.exists()) {
                //如果模型文件不存在
                try {
                    if (file.exists()) {
                        file.delete();
                    }

                    file.createNewFile();
                    InputStream in = context.getAssets().open(className + File.separator + fileName);
                    if (in == null) {
                        Log.e("copyMode", "the src is not existed");
                        return false;
                    }

                    OutputStream out = new FileOutputStream(file);
                    byte[] buffer = new byte[4096];
                    int n;
                    while ((n = in.read(buffer)) > 0) {
                        out.write(buffer, 0, n);
                    }
                    in.close();
                    out.close();
                } catch (IOException e) {
                    file.delete();
                    return false;
                }
            }
        }

        return true;
    }
}


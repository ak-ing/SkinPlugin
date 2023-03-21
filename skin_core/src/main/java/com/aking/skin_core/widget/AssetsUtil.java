package com.aking.skin_core.widget;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Rick on 2023-03-21  17:11.<p>
 * <p>
 * Description: 加载Assets目录下的文件
 */
public class AssetsUtil {

    public static String copySkinFromAssets(Context context, String name) {
        File skinDir = new File(getCacheDir(context), Constants.SKIN_PATH_CACHE);
        if (!skinDir.exists()) {
            skinDir.mkdirs();
        }

        String skinPath = new File(skinDir.getAbsolutePath(), name).getAbsolutePath();
        try {
            InputStream is = context.getAssets().open(name);
            OutputStream os = new FileOutputStream(skinPath);
            int byteCount;
            byte[] bytes = new byte[1024];
            while ((byteCount = is.read(bytes)) != -1) {
                os.write(bytes, 0, byteCount);
            }
            os.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return skinPath;
    }

    private static String getCacheDir(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File cacheDir = context.getExternalCacheDir();
            if (cacheDir != null && (cacheDir.exists() || cacheDir.mkdirs())) {
                return cacheDir.getAbsolutePath();
            }
        }
        return context.getCacheDir().getAbsolutePath();
    }
}

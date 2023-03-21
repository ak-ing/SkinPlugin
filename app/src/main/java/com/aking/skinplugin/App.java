package com.aking.skinplugin;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.aking.skin_core.i.ISkinMethodHolder;
import com.aking.skin_core.manager.SkinManager;

/**
 * Created by Rick at 2023/3/18 23:08.
 *
 * @Description:
 */
public class App extends Application {

    public static boolean checkPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                return true;
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
                return false;
            }
        } else {
            return context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onCreate() {
        super.onCreate();

        ISkinMethodHolder<TextView, Float> setTextSize = TextView::setTextSize;
        SkinManager.INSTANCE.addSkinAttrHolder("textSize", setTextSize);
    }
}

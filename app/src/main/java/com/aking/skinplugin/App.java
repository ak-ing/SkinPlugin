package com.aking.skinplugin;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import com.aking.skin_core.domain.MethodAcceptAndThen;
import com.aking.skin_core.i.ISkinMethodHolder;
import com.aking.skin_core.manager.SkinManager;

/**
 * Created by Rick at 2023/3/18 23:08.
 *
 * @Description:
 */
public class App extends Application {

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onCreate() {
        super.onCreate();
        ISkinMethodHolder<EditText, Drawable> setTextCursorDrawable = EditText::setTextCursorDrawable;

        MethodAcceptAndThen<EditText, Drawable> methodHolder = setTextCursorDrawable.andThen(new ISkinMethodHolder<EditText, Drawable>() {
            @Override
            public void accept(EditText editText, Drawable drawable) {

            }
        });
        SkinManager.INSTANCE.addSkinAttrHolder("textCursorDrawable", methodHolder);
    }

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
            if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
    }
}

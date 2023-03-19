package com.aking.skinplugin;

import android.app.Application;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import com.aking.skin_core.domain.SkinMethodHolder;
import com.aking.skin_core.i.ISkinMethodHolder;
import com.aking.skin_core.manager.SkinManager;

/**
 * Created by Rick at 2023/3/18 23:08.
 * @Description:
 */
public class App extends Application {

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onCreate() {
        super.onCreate();
        ISkinMethodHolder<EditText, Drawable> setTextCursorDrawable = EditText::setTextCursorDrawable;
        SkinMethodHolder<EditText, Drawable> methodHolder = setTextCursorDrawable.andThen(new ISkinMethodHolder<EditText, Drawable>() {
            @Override
            public void accept(EditText editText, Drawable drawable) {

            }
        });
        SkinManager.INSTANCE.addSkinAttrHolder("textCursorDrawable", methodHolder);
    }
}

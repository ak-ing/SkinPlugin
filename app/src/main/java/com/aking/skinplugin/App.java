package com.aking.skinplugin;

import android.app.Application;
import android.os.Build;
import android.util.TypedValue;
import android.widget.EditText;
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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onCreate() {
        super.onCreate();

        //Hin文字t颜色
        ISkinMethodHolder<EditText, Integer> setHintTextColor = EditText::setHintTextColor;
        SkinManager.INSTANCE.addSkinAttrHolder("textColorHint", setHintTextColor);

        //文字大小
        ISkinMethodHolder<TextView, Float> setTextSize = this::setTextSize;
        SkinManager.INSTANCE.addSkinAttrHolder("textSize", setTextSize);
    }

    public void setTextSize(TextView view, float px) {
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, px);
    }
}

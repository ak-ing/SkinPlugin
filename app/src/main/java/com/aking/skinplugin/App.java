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

//        //文字颜色
//        ISkinMethodHolder<TextView, Integer> setTextColor = TextView::setTextColor;
//        MethodAcceptAndThen<TextView, Integer> setTextColorAndThen = setTextColor.andThen((textView, integer) -> {
//            if ("title".equals(textView.getTag(R.id.skinMethodTagID))) {
//                textView.setText("春节主题皮肤");
//            }
//        });
//        SkinManager.INSTANCE.addSkinAttrHolder("setTextColor", setTextColorAndThen);
//
//        ISkinMethodHolder<View, Drawable> setBackground = View::setBackground;
//        MethodAcceptProxy<View, Drawable> setBackgroundProxy = setBackground.proxy((view, value, methodHolder) -> {
//
//        });
//        SkinManager.INSTANCE.addSkinAttrHolder("setBackground", setBackgroundProxy);
    }

    public void setTextSize(TextView view, float px) {
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, px);
    }
}

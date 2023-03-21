package com.aking.skin_core.widget;

import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aking.skin_core.i.ISkinMethodHolder;

/**
 * Created by Rick at 2023/3/21 21:53.
 * Description: 便捷
 */
public class SkinMethod {

    public static final ISkinMethodHolder<TextView, Integer> setTextColor = TextView::setTextColor;
    public static final ISkinMethodHolder<View, Drawable> setBackground = View::setBackground;
    public static final ISkinMethodHolder<ImageView, Drawable> setImageDrawable = ImageView::setImageDrawable;
    public static final ISkinMethodHolder<TextView, CharSequence> setText = TextView::setText;
    public static final ISkinMethodHolder<EditText, Integer> setHintTextColor = EditText::setHintTextColor;
    public static final ISkinMethodHolder<TextView, Float> setTextSize = SkinMethod::setTextSize;


    static void setTextSize(TextView view, float px) {
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, px);
    }

}

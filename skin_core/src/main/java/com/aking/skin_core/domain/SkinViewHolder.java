package com.aking.skin_core.domain;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.arch.core.util.Function;

import com.aking.skin_core.i.IExceptFunction;
import com.aking.skin_core.i.ISkinMethodHolder;
import com.aking.skin_core.manager.SkinManager;

import java.util.List;

/**
 * Created by Rick at 2023/3/18 1:37.
 * <p>
 * Description: 需要换肤的View的缓存对象
 */
public class SkinViewHolder {

    private final boolean methodTag;
    //mView需要换肤的属性对象集合
    private final List<SkinAttributeItem> mSkinAttributeItems;
    private View mView;

    public SkinViewHolder(View view, boolean methodTag, List<SkinAttributeItem> skinAttributeItems) {
        mView = view;
        this.methodTag = methodTag;
        mSkinAttributeItems = skinAttributeItems;
    }

    /**
     * 换肤
     */
    public void apply() {
        for (SkinAttributeItem attributeItem : mSkinAttributeItems) {
            if (mView == null) return;
            String typeName = attributeItem.getTypeName();
            ISkinMethodHolder<? extends View, ?> methodHolder = attributeItem.getAttrMethodHolder();
            if (mView instanceof TextView) {
                Log.d("TAG", "apply: ");
            }
            if ("color".equals(typeName)) {
                //如果设置的是Color类型的Drawable.
                if (tryColorDrawable(attributeItem, methodHolder)) continue;
                tryExec(() -> applySkinColor(attributeItem, methodHolder));
            } else if ("drawable".equals(typeName) || "mipmap".equals(typeName)) {
                tryExec(() -> applySkinBackground(attributeItem, methodHolder));
            } else if ("dimen".equals(typeName)) {
                tryExec(() -> applySkinDimen(attributeItem, methodHolder));
            } else if ("string".equals(typeName)) {
                tryExec(() -> applySkinString(attributeItem, methodHolder));
            }
        }
    }

    private void applySkinBackground(SkinAttributeItem attributeItem, ISkinMethodHolder methodHolder) {
        Drawable drawable = SkinManager.INSTANCE.getDrawable(attributeItem.getResId());
        methodHolder.accept(mView, drawable);
        handlerAfter(methodHolder, drawable);
    }

    private void applySkinColor(SkinAttributeItem attributeItem, ISkinMethodHolder methodHolder) {
        int color = SkinManager.INSTANCE.getColor(attributeItem.getResId());
        methodHolder.accept(mView, color);
        handlerAfter(methodHolder, color);
    }

    private void applySkinString(SkinAttributeItem attributeItem, ISkinMethodHolder methodHolder) {
        String string = SkinManager.INSTANCE.getString(attributeItem.getResId());
        methodHolder.accept(mView, string);
        handlerAfter(methodHolder, string);
    }

    private void applySkinDimen(SkinAttributeItem attributeItem, ISkinMethodHolder methodHolder) {
        float dimension = SkinManager.INSTANCE.getDimension(attributeItem.getResId());
        Function<View, ViewGroup.LayoutParams> getLayoutParams = View::getLayoutParams;
        ViewGroup.LayoutParams layoutParams = getLayoutParams.apply(mView);
        if ("layout_width".equals(attributeItem.getAttrName())) {
            layoutParams.width = (int) dimension;
        } else if ("layout_height".equals(attributeItem.getAttrName())) {
            layoutParams.height = (int) dimension;
        }
        methodHolder.accept(mView, layoutParams);
        handlerAfter(methodHolder, layoutParams);
    }


    /**
     * 尝试设置Color类型的Drawable.
     * 如：background或src设置Color资源
     */
    private boolean tryColorDrawable(SkinAttributeItem attributeItem, ISkinMethodHolder methodHolder) {
        try {
            Drawable drawable = SkinManager.INSTANCE.getDrawable(attributeItem.getResId());
            methodHolder.accept(mView, drawable);
            handlerAfter(methodHolder, drawable);
            return true;
        } catch (ClassCastException e) {
            //忽略
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 方法异常包装
     */
    private void tryExec(IExceptFunction block) {
        try {
            block.call();
        } catch (Exception e) {
            Log.e("SkinViewHolder", "applySkin: " + mView.getClass().getSimpleName() + "：" + e.getMessage());
        }
    }

    private void handlerAfter(ISkinMethodHolder methodHolder, Object o) {
        if (methodTag && methodHolder instanceof MethodAcceptAndThen) {
            ((MethodAcceptAndThen) methodHolder).acceptThenAfter(mView, o);
        }
    }

    public View getView() {
        return mView;
    }

    public void setView(View view) {
        mView = view;
    }
}

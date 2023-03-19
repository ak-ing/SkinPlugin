package com.aking.skin_core.domain;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.aking.skin_core.i.IExceptFunction;
import com.aking.skin_core.i.ISkinMethodHolder;
import com.aking.skin_core.manager.SkinManager;

import java.util.List;

/**
 * Created by Rick at 2023/3/18 1:37.
 * @Description: 需要换肤的View的缓存对象
 */
public class SkinViewHolder {

    private View mView;
    private final boolean methodTag;
    //mView需要换肤的属性对象集合
    private final List<SkinAttributeItem> mSkinAttributeItems;

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
            if ("color".equals(typeName)) {
                //如果设置的是Color类型的Drawable.
                if (tryColorDrawable(attributeItem, methodHolder)) continue;
                tryExec(() -> applySkinColor(attributeItem, methodHolder));
            } else if ("drawable".equals(typeName) || "mipmap".equals(typeName)) {
                tryExec(() -> applySkinBackground(attributeItem, methodHolder));
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
        if (methodTag && methodHolder instanceof SkinMethodHolder) {
            ((SkinMethodHolder) methodHolder).acceptThenAfter(mView, o);
        }
    }

    public View getView() {
        return mView;
    }

    public void setView(View view) {
        mView = view;
    }
}

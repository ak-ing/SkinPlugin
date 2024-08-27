package com.aking.skin_core.domain;

import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.aking.skin_core.i.IExceptFunction;
import com.aking.skin_core.i.ISkinMethodHolder;
import com.aking.skin_core.manager.SkinManager;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Rick at 2023/3/18 1:37.
 * <p>
 * Description: 需要换肤的View的缓存对象
 */
public class SkinViewHolder {

    //mView需要换肤的属性对象集合
    private final List<SkinAttributeItem> mSkinAttributeItems;
    private final WeakReference<View> mView;

    public SkinViewHolder(View view, List<SkinAttributeItem> skinAttributeItems) {
        mView = new WeakReference<>(view);
        mSkinAttributeItems = skinAttributeItems;
    }

    /**
     * 换肤
     */
    public void apply() {
        Runnable runnable = () -> {
            for (SkinAttributeItem attributeItem : mSkinAttributeItems) {
                String typeName = attributeItem.getTypeName();
                ISkinMethodHolder<? extends View, ?> methodHolder = attributeItem.getAttrMethodHolder();
                if ("color".equals(typeName)) {
                    //如果设置的是Color类型的Drawable.
                    if (tryColorDrawable(attributeItem, methodHolder)) continue;
                    tryExec(() -> applySkinColor(attributeItem, methodHolder));
                } else if ("drawable".equals(typeName) || "mipmap".equals(typeName)) {
                    tryExec(() -> applySkinBackground(attributeItem, methodHolder));
                } else if ("string".equals(typeName)) {
                    tryExec(() -> applySkinString(attributeItem, methodHolder));
                } else if ("dimen".equals(typeName)) {
                    tryExec(() -> applyDimension(attributeItem, methodHolder));
                }
            }
        };

        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            runnable.run();
        } else {
            mView.get().post(runnable);
        }
    }

    private void applySkinBackground(SkinAttributeItem attributeItem, ISkinMethodHolder methodHolder) {
        Drawable drawable = SkinManager.INSTANCE.getDrawable(attributeItem.getResId());
        methodHolder.accept(mView.get(), drawable);
    }

    private void applySkinColor(SkinAttributeItem attributeItem, ISkinMethodHolder methodHolder) {
        int color = SkinManager.INSTANCE.getColor(attributeItem.getResId());
        methodHolder.accept(mView.get(), color);
    }

    private void applySkinString(SkinAttributeItem attributeItem, ISkinMethodHolder methodHolder) {
        String string = SkinManager.INSTANCE.getString(attributeItem.getResId());
        methodHolder.accept(mView.get(), string);
    }

    private void applyDimension(SkinAttributeItem attributeItem, ISkinMethodHolder methodHolder) {
        float dimension = SkinManager.INSTANCE.getDimension(attributeItem.getResId());
        methodHolder.accept(mView.get(), dimension);
    }

    /**
     * 尝试设置Color类型的Drawable.
     * 如：background或src设置Color资源
     */
    private boolean tryColorDrawable(SkinAttributeItem attributeItem, ISkinMethodHolder methodHolder) {
        try {
            Drawable drawable = SkinManager.INSTANCE.getDrawable(attributeItem.getResId());
            methodHolder.accept(mView.get(), drawable);
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

    public View getView() {
        return mView.get();
    }

}

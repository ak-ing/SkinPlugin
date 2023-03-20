package com.aking.skin_core.domain;

import android.view.View;

import androidx.annotation.IdRes;

import com.aking.skin_core.i.ISkinMethodHolder;

/**
 * Created by Rick at 2023/3/18 1:31.
 * <p>
 * Description: 控件某条属性的封装
 */
public class SkinAttributeItem {
    private final String attrName;
    private final String typeName;
    private final String entryName;
    @IdRes
    private final int resId;
    private final ISkinMethodHolder<? extends View, ?> attrMethodHolder;

    public SkinAttributeItem(String attrName, String typeName, String entryName, int resId, ISkinMethodHolder<? extends View, ?> attrMethodHolder) {
        this.attrName = attrName;
        this.typeName = typeName;
        this.entryName = entryName;
        this.resId = resId;
        this.attrMethodHolder = attrMethodHolder;
    }

    public String getAttrName() {
        return attrName == null ? "" : attrName;
    }

    public String getTypeName() {
        return typeName == null ? "" : typeName;
    }

    public String getEntryName() {
        return entryName == null ? "" : entryName;
    }

    public int getResId() {
        return resId;
    }

    public ISkinMethodHolder<? extends View, ?> getAttrMethodHolder() {
        return attrMethodHolder;
    }
}

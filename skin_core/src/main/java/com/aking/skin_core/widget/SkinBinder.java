package com.aking.skin_core.widget;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.aking.skin_core.domain.SkinAttributeItem;
import com.aking.skin_core.domain.SkinViewHolder;
import com.aking.skin_core.i.ISkinMethodHolder;
import com.aking.skin_core.manager.SkinManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态 View 换肤绑定器
 * <p>
 * 使用示例:
 * <pre>
 * // 方式1：绑定到 View 生命周期（推荐）
 * SkinBinder.bind(textView)
 *     .bindColor(R.color.text_color, TextView::setTextColor)
 *     .bindDrawable(R.drawable.bg, View::setBackground)
 *     .bind();
 *
 * // 方式2：绑定到 LifecycleOwner（Activity/Fragment）
 * SkinBinder.bind(textView)
 *     .bindColor(R.color.text_color, TextView::setTextColor)
 *     .bindTo(lifecycleOwner);
 * </pre>
 */
public class SkinBinder<V extends View> {

    private final V view;
    private final List<SkinAttributeItem> items = new ArrayList<>();
    private final Resources resources;

    private SkinBinder(V view) {
        this.view = view;
        this.resources = view.getContext().getResources();
    }

    /**
     * 创建 SkinBinder 实例
     *
     * @param view 需要换肤的 View
     * @return SkinBinder 实例
     */
    public static <V extends View> SkinBinder<V> bind(V view) {
        if (view == null) {
            throw new IllegalArgumentException("View cannot be null");
        }
        return new SkinBinder<>(view);
    }

    /**
     * 绑定颜色资源
     *
     * @param resId  颜色资源 ID
     * @param setter 设置方法，如 TextView::setTextColor
     * @return SkinBinder 实例
     */
    public SkinBinder<V> bindColor(@ColorRes int resId, ISkinMethodHolder<? super V, Integer> setter) {
        addItem(resId, "color", setter);
        return this;
    }

    /**
     * 绑定 Drawable 资源
     *
     * @param resId  Drawable 资源 ID
     * @param setter 设置方法，如 View::setBackground
     * @return SkinBinder 实例
     */
    public SkinBinder<V> bindDrawable(@DrawableRes int resId, ISkinMethodHolder<? super V, Drawable> setter) {
        addItem(resId, "drawable", setter);
        return this;
    }

    /**
     * 绑定 Mipmap 资源
     *
     * @param resId  Mipmap 资源 ID
     * @param setter 设置方法
     * @return SkinBinder 实例
     */
    public SkinBinder<V> bindMipmap(@DrawableRes int resId, ISkinMethodHolder<? super V, Drawable> setter) {
        addItem(resId, "mipmap", setter);
        return this;
    }

    /**
     * 绑定字符串资源
     *
     * @param resId  字符串资源 ID
     * @param setter 设置方法，如 TextView::setText
     * @return SkinBinder 实例
     */
    public SkinBinder<V> bindString(@StringRes int resId, ISkinMethodHolder<? super V, String> setter) {
        addItem(resId, "string", setter);
        return this;
    }

    /**
     * 绑定尺寸资源
     *
     * @param resId  尺寸资源 ID
     * @param setter 设置方法
     * @return SkinBinder 实例
     */
    public SkinBinder<V> bindDimen(@DimenRes int resId, ISkinMethodHolder<? super V, Float> setter) {
        addItem(resId, "dimen", setter);
        return this;
    }

    /**
     * 完成绑定，使用 View 自身生命周期管理（推荐）
     * <p>
     * View 从窗口 detach 时自动解绑
     */
    public void bind() {
        SkinViewHolder holder = new SkinViewHolder(view, items);
        SkinManager.INSTANCE.addDynamicSkinViewHolder(holder);

        // 监听 View 的 detach 事件，自动解绑
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(@NonNull View v) {
                // 无需处理
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull View v) {
                SkinManager.INSTANCE.removeDynamicSkinViewHolder(holder);
                v.removeOnAttachStateChangeListener(this);
            }
        });
    }

    /**
     * 完成绑定，绑定到 LifecycleOwner 生命周期
     * <p>
     * LifecycleOwner（如 Activity/Fragment）销毁时自动解绑
     *
     * @param owner LifecycleOwner 实例
     */
    public void bindTo(LifecycleOwner owner) {
        SkinViewHolder holder = new SkinViewHolder(view, items);
        SkinManager.INSTANCE.addDynamicSkinViewHolder(holder);

        // 监听 Lifecycle 的 ON_DESTROY 事件，自动解绑
        owner.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (event == Lifecycle.Event.ON_DESTROY) {
                SkinManager.INSTANCE.removeDynamicSkinViewHolder(holder);
            }
        });
    }

    private void addItem(int resId, String typeName, ISkinMethodHolder<?, ?> setter) {
        try {
            String entryName = resources.getResourceEntryName(resId);
            items.add(new SkinAttributeItem(null, typeName, entryName, resId, setter));
        } catch (Resources.NotFoundException e) {
            // 资源未找到，忽略
        }
    }
}

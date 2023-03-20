package com.aking.skin_core.manager;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.aking.skin_core.R;
import com.aking.skin_core.domain.SkinAttributeItem;
import com.aking.skin_core.domain.SkinViewHolder;
import com.aking.skin_core.i.ISkinMethodHolder;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Rick at 2023/3/17 23:28.
 * Description: 构造View的工厂，
 * 代理创建view的过程，实现换肤能力
 */
public class SkinFactory implements LayoutInflater.Factory2 {
    private static final Class<?>[] mConstructorSignature = new Class[]{
            Context.class, AttributeSet.class};
    //缓存构造方法
    private static final HashMap<String, Constructor<? extends View>> sConstructorMap =
            new HashMap<String, Constructor<? extends View>>();
    private static final String[] prxFixList = {
            "android.view.",
            "android.widget.",
            "android.webkit."
    };
    private static final ClassLoader BOOT_CLASS_LOADER = LayoutInflater.class.getClassLoader();
    /**
     * 用到AppCompatDelegate的onCreateView，通过mAppCompatViewInflater对象
     * 的createView方法构造AppCompat(兼容View) [AppCompatViewInflater 119行]
     * {@link androidx.appcompat.app.AppCompatViewInflater}
     */
    private final AppCompatDelegate mDelegate;
    private final List<SkinViewHolder> mSkinViewHolders = new ArrayList<>();

    public SkinFactory(AppCompatDelegate delegate) {
        mDelegate = delegate;
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        //实例化每个控件
        View view = null;
        if (mDelegate != null) {    //是AppCompatActivity
            view = mDelegate.createView(parent, name, context, attrs);
        }

        if (view == null) {
            //通过反射去创建View(源码实现)
            if (-1 == name.indexOf('.')) {
                for (String s : prxFixList) {
                    view = onCreateView(s + name, context, attrs);
                    if (view != null) break;
                }
            } else {
                view = onCreateView(name, context, attrs);
            }
        }

        //缓存需要换肤的控件
        if (view != null) {
            holderView(context, view, attrs);
        }
        return view;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        View view = null;
        Constructor<? extends View> constructor = sConstructorMap.get(name);
        if (constructor != null && !verifyClassLoader(context, constructor)) {
            constructor = null;
            sConstructorMap.remove(name);
        }
        Class<? extends View> clazz = null;

        try {
            if (constructor == null) {
                //获取控件的Class对象
                clazz = Class.forName(name, false, context.getClassLoader()).asSubclass(View.class);
                constructor = clazz.getConstructor(mConstructorSignature);
                constructor.setAccessible(true);
                sConstructorMap.put(name, constructor);
            }

            view = constructor.newInstance(context, attrs);
            if (view instanceof ViewStub) {
                final ViewStub viewStub = (ViewStub) view;
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                layoutInflater.setFactory2(this);
                viewStub.setLayoutInflater(layoutInflater);
            }
        } catch (ClassNotFoundException e) {
            // 尝试创建View失败，我们不想捕获这些异常
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    /**
     * 缓存真正需要换肤的View，skinEnable==true && getNeedSkinAttribute！=null
     */
    private void holderView(Context context, View view, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.skinAble);
        Resources resources = context.getResources();
        if (a.getBoolean(R.styleable.skinAble_skinEnable, SkinManager.INSTANCE.isSkinGlobalEnable())) {
            List<SkinAttributeItem> skinItems = new ArrayList<>();
            for (int i = 0; i < attrs.getAttributeCount(); i++) {
                String attrName = getNeedSkinAttribute(attrs.getAttributeName(i));
                if (attrName != null) {
                    //拿到Id值 @11111111
                    String attributeValue = attrs.getAttributeValue(i);
                    if (!attributeValue.startsWith("@")) continue;
                    int resId = Integer.parseInt(attributeValue.substring(1));
                    try {
                        String typeName = resources.getResourceTypeName(resId);
                        String entryName = resources.getResourceEntryName(resId);
                        ISkinMethodHolder<? extends View, ?> methodHolder = SkinManager.INSTANCE.mSkinAttrHolder.get(attrName);
                        skinItems.add(new SkinAttributeItem(attrName, typeName, entryName, resId, methodHolder));
                    } catch (Resources.NotFoundException e) {
                        //我们不想捕获这些异常
                    }
                }
            }
            //当存在需要换肤的属性时
            if (skinItems.size() > 0) {
                boolean methodTag = a.getBoolean(R.styleable.skinAble_skinMethodTag, false);
                SkinViewHolder skinViewHolder = new SkinViewHolder(view, methodTag, skinItems);
                mSkinViewHolders.add(skinViewHolder);
                skinViewHolder.apply();
            }
        }
        a.recycle();
    }

    private String getNeedSkinAttribute(String attrName) {
        for (String s : SkinManager.INSTANCE.mSkinAttrHolder.keySet()) {
            if (attrName.equals(s)) return s;
        }
        return null;
    }

    public void apply() {
        for (SkinViewHolder skinViewHolder : mSkinViewHolders) {
            skinViewHolder.apply();
        }
    }

    private boolean verifyClassLoader(Context context, Constructor<? extends View> constructor) {
        final ClassLoader constructorLoader = constructor.getDeclaringClass().getClassLoader();
        if (constructorLoader == BOOT_CLASS_LOADER) {
            // fast path for boot class loader (most common case?) - always ok
            return true;
        }
        // in all normal cases (no dynamic code loading), we will exit the following loop on the
        // first iteration (i.e. when the declaring classloader is the contexts class loader).
        ClassLoader cl = context.getClassLoader();
        do {
            if (constructorLoader == cl) {
                return true;
            }
            cl = cl.getParent();
        } while (cl != null);
        return false;
    }

}

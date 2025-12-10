package com.aking.skin_core.manager;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.aking.skin_core.i.ISkinMethodHolder;
import com.aking.skin_core.widget.AssetsUtil;
import com.aking.skin_core.widget.Constants;
import com.aking.skin_core.widget.SkinActivityLifecycleCallback;
import com.aking.skin_core.widget.SkinMethod;
import com.aking.skin_core.widget.SpUtil;

import com.aking.skin_core.domain.SkinViewHolder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.util.TypedValue.TYPE_DIMENSION;
import static com.aking.skin_core.widget.Constants.HOOK_ASSET_NAME;

/**
 * Created by Rick at 2023/3/15 21:15.
 * <p>
 * Description: 换肤 {@link #INSTANCE}
 */
public enum SkinManager {

    INSTANCE;

    public static final int INVALID_ID = 0;
    final Map<String, ISkinMethodHolder<? extends View, ?>> mSkinAttrHolder;
    // 动态添加的 View 的 SkinViewHolder 列表
    private final List<SkinViewHolder> mDynamicSkinViewHolders = new ArrayList<>();
    private Application mContext;
    private Resources mResources;
    private Resources appResources;
    private String mSkinPackageName;    //资源包中的包名
    private boolean mSkinGlobalEnable = true;
    private SkinActivityLifecycleCallback skinActivityLifecycleCallback;

    SkinManager() {
        mSkinAttrHolder = new HashMap<>();
        mSkinAttrHolder.put(Constants.SKIN_ATTR_BACKGROUND, SkinMethod.setBackground);
        mSkinAttrHolder.put(Constants.SKIN_ATTR_SRC, SkinMethod.setImageDrawable);
        mSkinAttrHolder.put(Constants.SKIN_ATTR_TEXT_COLOR, SkinMethod.setTextColor);
        mSkinAttrHolder.put(Constants.SKIN_ATTR_TEXT, SkinMethod.setText);
    }

    void init(Application context) {
        mContext = context;
        skinActivityLifecycleCallback = new SkinActivityLifecycleCallback();
        mContext.registerActivityLifecycleCallbacks(skinActivityLifecycleCallback);
        appResources = context.getResources();

        //应用启动时加载皮肤包
        String path = SpUtil.INSTANCE.getPath();
        if (!path.isEmpty()) {
            loadSkinFile(path);
        }
    }

    /**
     * 根据路径加载资源包，来得到资源包的资源对象
     *
     * @param path 资源包文件路径
     */
    public void loadSkinFile(String path) {
        String oldPath = SpUtil.INSTANCE.getPath();
        // 判断皮肤路径相等,并且当前已经加载过皮肤包，return
        if (oldPath.equals(path) && !resourceIsNull()) return;

        try {
            //拿到资源包的包名
            PackageManager packageManager = mContext.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
            mSkinPackageName = packageInfo.packageName;
            //拿到AssetManager的setApkAssets方法来设置资源路径
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getDeclaredMethod(HOOK_ASSET_NAME, String.class);
            addAssetPath.setAccessible(true);
            addAssetPath.invoke(assetManager, path);
            //创建资源对象，管理资源包里面的资源
            mResources = new Resources(assetManager, appResources.getDisplayMetrics(), appResources.getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (resourceIsNull()) return;
        //通知所有已缓存页面刷新
        skinActivityLifecycleCallback.notifySkinHolder();

        // 存储皮肤状态
        SpUtil.INSTANCE.putPath(path);
    }


    /**
     * 加载Assets目录下的资源包，优先使用{@link #loadSkinFile(String)}来加载.
     * <p>
     * 此操作会将Assets目录下的文件复制一份到缓存文件夹
     *
     * @param name 资源包文件名
     */
    public void loadSkinAssets(String name) {
        String cachePath = AssetsUtil.copySkinFromAssets(mContext, name);
        loadSkinFile(cachePath);
    }


    /**
     * 重置换肤，切换为默认
     */
    public void loadDefault() {
        if (mResources != null) {
            SpUtil.INSTANCE.putPath("");
            mResources = null;
            mSkinPackageName = null;
            skinActivityLifecycleCallback.notifySkinHolder();
        }
    }

    /**
     * 添加需要换肤的属性
     *
     * @param attrName     属性名
     * @param methodHolder 换肤[方法]缓存对象
     *                     比如：
     *                     ISkinMethodHolder<TextView, Integer> setTextColor = TextView::setTextColor;
     *                     addSkinAttrHolder("textColor", setTextColor);
     */
    public <T> void addSkinAttrHolder(String attrName, ISkinMethodHolder<? extends View, ?> methodHolder) {
        mSkinAttrHolder.put(attrName, methodHolder);
    }

    /**
     * 获取资源包的资源对象，如果为空，返回APP的资源对象
     *
     * @return 资源对象
     */
    public Resources getSkinResource() {
        if (resourceIsNull()) {
            return appResources;
        } else {
            return mResources;
        }
    }


    /**
     * 是否为换肤状态
     *
     * @return 已换肤：true
     */
    public boolean isSkinState() {
        return !resourceIsNull();
    }

    /**
     * 是否已启用全局换肤
     *
     * @return 全局换肤：true
     */
    boolean isSkinGlobalEnable() {
        return mSkinGlobalEnable;
    }

    /**
     * 设置是否启用全局换肤开关，默认为true，
     * 可通过{app:skinEnable="boolean"}属性单独给View设置
     */
    public void setSkinGlobalEnable(boolean enable) {
        mSkinGlobalEnable = enable;
    }

    /**
     * 添加动态 View 的 SkinViewHolder
     *
     * @param holder SkinViewHolder 对象
     */
    public void addDynamicSkinViewHolder(SkinViewHolder holder) {
        if (holder != null && !mDynamicSkinViewHolders.contains(holder)) {
            mDynamicSkinViewHolders.add(holder);
            // 如果当前已处于换肤状态，立即应用
            if (isSkinState()) {
                holder.apply();
            }
        }
    }

    /**
     * 移除动态 View 的 SkinViewHolder
     *
     * @param holder SkinViewHolder 对象
     */
    public void removeDynamicSkinViewHolder(SkinViewHolder holder) {
        mDynamicSkinViewHolders.remove(holder);
    }

    /**
     * 应用所有动态 ViewHolder 的换肤
     */
    public void applyDynamicSkinViewHolders() {
        for (SkinViewHolder holder : mDynamicSkinViewHolders) {
            holder.apply();
        }
    }

    private boolean resourceIsNull() {
        return mResources == null;
    }

    void onConfigChanged(Configuration newConfig) {
        if (resourceIsNull()) return;
        mResources.updateConfiguration(newConfig, appResources.getDisplayMetrics());
    }


    /* -------------------------------匹配资源的方法------------------------------ */


    /**
     * 根据资源ID获取资源包中的Color颜色值
     *
     * @param colorRes color资源引用
     * @return 资源包中的颜色值
     */
    public int getColor(@ColorRes int colorRes) {
        if (resourceIsNull()) return ContextCompat.getColor(mContext, colorRes);
        //从应用内Resources获取资源name和资源type
        String typeName = appResources.getResourceTypeName(colorRes);
        String entryName = appResources.getResourceEntryName(colorRes);
        int identifier = mResources.getIdentifier(entryName, typeName, mSkinPackageName);
        if (identifier == INVALID_ID) return ContextCompat.getColor(mContext, colorRes);
        return ResourcesCompat.getColor(mResources, identifier, null);
    }

    /**
     * 根据资源ID获取资源包中的drawable
     *
     * @param drawableId drawable资源引用
     * @return 资源包中的drawable
     */
    public Drawable getDrawable(@DrawableRes int drawableId) {
        if (resourceIsNull()) return ContextCompat.getDrawable(mContext, drawableId);
        //从应用内Resources获取资源name和资源type
        String typeName = appResources.getResourceTypeName(drawableId);
        String entryName = appResources.getResourceEntryName(drawableId);
        int identifier = mResources.getIdentifier(entryName, typeName, mSkinPackageName);
        if (identifier == INVALID_ID) return ContextCompat.getDrawable(mContext, drawableId);
        return ResourcesCompat.getDrawable(mResources, identifier, null);
    }


    public String getString(@StringRes int stringId) {
        if (resourceIsNull()) return appResources.getString(stringId);
        //从应用内Resources获取资源name和资源type
        String typeName = appResources.getResourceTypeName(stringId);
        String entryName = appResources.getResourceEntryName(stringId);
        int identifier = mResources.getIdentifier(entryName, typeName, mSkinPackageName);
        if (identifier == INVALID_ID) return appResources.getString(stringId);
        return mResources.getString(identifier);
    }

    public float getDimension(@DimenRes int dimensionId) {
        if (resourceIsNull()) return appResources.getDimension(dimensionId);
        //从应用内Resources获取资源name和资源type
        String typeName = appResources.getResourceTypeName(dimensionId);
        String entryName = appResources.getResourceEntryName(dimensionId);
        int identifier = mResources.getIdentifier(entryName, typeName, mSkinPackageName);
        TypedValue typedValue = new TypedValue();
        mResources.getValue(identifier, typedValue, true);
        if (typedValue.type != TYPE_DIMENSION) {
            return typedValue.getFloat();
        }
        if (identifier == INVALID_ID) return appResources.getDimension(dimensionId);
        return mResources.getDimension(identifier);
    }

    /**
     * 根据资源Id获取到皮肤包中的Id.
     * <br/>
     * 注意：必现使用{@link #getSkinResource()}对象，才能获取到皮肤包中的资源内容
     *
     * @param resId 应用内资源ID
     * @return 皮肤包中对应的ID
     */
    @IdRes
    public int getSkinResId(@IdRes int resId) {
        if (resourceIsNull()) return resId;
        //从应用内Resources获取资源name和资源type
        String typeName = appResources.getResourceTypeName(resId);
        String entryName = appResources.getResourceEntryName(resId);
        int identifier = mResources.getIdentifier(entryName, typeName, mSkinPackageName);
        if (identifier == INVALID_ID) return resId;
        return identifier;
    }

}

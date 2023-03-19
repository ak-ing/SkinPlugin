package com.aking.skin_core.manager;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.aking.skin_core.i.ISkinMethodHolder;
import com.aking.skin_core.widget.SkinActivityLifecycleCallback;
import com.aking.skin_core.widget.SpUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rick at 2023/3/15 21:15.
 *
 * @Description: 去加载资源包文件，能够从中获取想要的资源
 */
public enum SkinManager {

    INSTANCE;

    private static final int INVALID_ID = 0;
    private static final String HOOK_ASSET_NAME = "addAssetPath";
    private Application mContext;
    private Resources mResources;
    private Resources appResources;
    private String mSkinPackageName;    //资源包中的包名
    private boolean mSkinGlobalEnable = true;
    private SkinActivityLifecycleCallback skinActivityLifecycleCallback;
    final Map<String, ISkinMethodHolder<? extends View, ?>> mSkinAttrHolder;

    SkinManager() {
        mSkinAttrHolder = new HashMap<>();
        ISkinMethodHolder<TextView, Integer> setTextColor = TextView::setTextColor;
        ISkinMethodHolder<View, Drawable> setBackground = View::setBackground;
        ISkinMethodHolder<ImageView, Drawable> setImageDrawable = ImageView::setImageDrawable;
        mSkinAttrHolder.put("textColor", setTextColor);
        mSkinAttrHolder.put("background", setBackground);
        mSkinAttrHolder.put("src", setImageDrawable);
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
            mResources = new Resources(assetManager, mContext.getResources().getDisplayMetrics(),
                    mContext.getResources().getConfiguration());
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
     * 重置换肤，切换为默认
     */
    public void reset() {
        if (mResources != null) {
            SpUtil.INSTANCE.putPath("");
            mResources = null;
            mSkinPackageName = null;
            skinActivityLifecycleCallback.notifySkinHolder();
        }
    }


    /**
     * 设置是否启用全局换肤开关，默认为true，
     * 可通过{app:skinEnable="boolean"}属性单独给View设置
     */
    public void setSkinGlobalEnable(boolean enable) {
        mSkinGlobalEnable = enable;
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


    public boolean isSkinState() {
        return !resourceIsNull();
    }


    boolean isSkinGlobalEnable() {
        return mSkinGlobalEnable;
    }

    private boolean resourceIsNull() {
        return mResources == null;
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
        return ResourcesCompat.getColor(mResources, colorRes, null);
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
        return ResourcesCompat.getDrawable(mResources, drawableId, null);
    }


    public String getString(@StringRes int stringId) {
        if (resourceIsNull()) return appResources.getString(stringId);
        //从应用内Resources获取资源name和资源type
        String typeName = appResources.getResourceTypeName(stringId);
        String entryName = appResources.getResourceEntryName(stringId);
        int identifier = mResources.getIdentifier(entryName, typeName, mSkinPackageName);
        if (identifier == INVALID_ID) return appResources.getString(stringId);
        return mResources.getString(stringId);
    }

    public float getDimension(@DimenRes int dimensionId) {
        if (resourceIsNull()) return appResources.getDimension(dimensionId);
        //从应用内Resources获取资源name和资源type
        String typeName = appResources.getResourceTypeName(dimensionId);
        String entryName = appResources.getResourceEntryName(dimensionId);
        int identifier = mResources.getIdentifier(entryName, typeName, mSkinPackageName);
        if (identifier == INVALID_ID) return appResources.getDimension(dimensionId);
        return mResources.getDimension(dimensionId);
    }

}

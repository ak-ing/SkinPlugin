package com.aking.skin_core.widget;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Rick at 2023/3/19 23:48.
 *
 * @Description: 储存皮肤包路径
 */
public enum SpUtil {
    INSTANCE;

    public static final String SKIN_PREFERENCE_NAME = "SkinPlugin_db";
    public static final String SKIN_PREFERENCE_PATH = "key_skin_path";
    private SharedPreferences mSp;

    public void init(Context context) {
        mSp = context.getSharedPreferences(SKIN_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void putPath(String path) {
        mSp.edit().putString(SKIN_PREFERENCE_PATH, path).apply();
    }

    public String getPath() {
        return mSp.getString(SKIN_PREFERENCE_PATH, "");
    }
}

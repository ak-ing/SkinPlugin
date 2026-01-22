package com.aking.skin_core.i;

/**
 * 换肤改变监听器
 */
public interface OnSkinChangeListener {
    /**
     * 换肤成功后的回调
     *
     * @param isDefault 是否是默认皮肤
     */
    void onSkinChanged(boolean isDefault);
}

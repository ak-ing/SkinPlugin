package com.aking.skin_core.domain;

import android.view.View;

import com.aking.skin_core.R;
import com.aking.skin_core.i.IMethodProxyCallback;
import com.aking.skin_core.i.ISkinMethodHolder;

/**
 * Created by Rick at 2023/3/20 21:35.
 * <p>
 * Description: {@link ISkinMethodHolder}的功能拓展，代理当前对象，在回调中自己实现逻辑
 */
public class MethodAcceptProxy<V extends View, U> implements ISkinMethodHolder<V, U> {

    private final ISkinMethodHolder<V, U> innerMethodHolder;
    private final IMethodProxyCallback<V, U> mProxyCallback;

    public MethodAcceptProxy(ISkinMethodHolder<V, U> innerMethodHolder, IMethodProxyCallback<V, U> proxyCallback) {
        this.innerMethodHolder = innerMethodHolder;
        mProxyCallback = proxyCallback;
    }

    @Override
    public void accept(V v, U u) {
        if (v.getTag(R.id.skinMethodTagID) != null) {
            mProxyCallback.callBack(v, u, innerMethodHolder);
        }
    }

}

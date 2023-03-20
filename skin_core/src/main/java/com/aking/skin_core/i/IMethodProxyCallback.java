package com.aking.skin_core.i;

import android.view.View;

/**
 * Created by Rick at 2023/3/20 21:47.
 * <p>
 * Description: {@link com.aking.skin_core.domain.MethodProxy}的回调
 */
public interface IMethodProxyCallback<V extends View, U> {
    void callBack(V view, U value, ISkinMethodHolder<V, U> methodHolder);
}

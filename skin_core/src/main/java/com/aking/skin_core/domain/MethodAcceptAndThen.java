package com.aking.skin_core.domain;

import android.view.View;

import com.aking.skin_core.i.ISkinMethodHolder;

import java.util.Objects;

/**
 * Created by Rick at 2023/3/19 17:12.
 * <p>
 * Description: {@link ISkinMethodHolder}的功能拓展，增加一个after操作
 */
public class MethodAcceptAndThen<V extends View, U> implements ISkinMethodHolder<V, U> {
    private final ISkinMethodHolder<V, U> mAccept;
    private final ISkinMethodHolder<V, U> mAfter;

    public MethodAcceptAndThen(ISkinMethodHolder<V, U> accept, ISkinMethodHolder<V, U> after) {
        mAccept = accept;
        mAfter = after;
    }

    @Override
    public void accept(V v, U u) {
        mAccept.accept(v, u);
    }

    public void acceptThenAfter(V v, U u) {
        mAccept.accept(v, u);
        Objects.requireNonNull(mAfter);
        mAfter.accept(v, u);

    }

}

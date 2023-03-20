package com.aking.skin_core.i;

import android.view.View;

import com.aking.skin_core.domain.MethodAcceptAndThen;

import java.util.Objects;

/**
 * Created by Rick at 2023/3/18 2:47.
 * <p>
 * Description: [方法引用]缓存对象, 属性的set方法, 如：TextView::setTextColor
 */
@FunctionalInterface
public interface ISkinMethodHolder<V extends View, U> {

    void accept(V v, U u);

    /**
     * 返回SkinMethodHolder对象,按顺序执行此操作，后跟一个 after 操作。
     * <br/>
     * 请注意：必须要给目标View设置[app:skinMethodTag="true"]属性
     *
     * @param after 此操作后要执行的操作
     * @return {@link MethodAcceptAndThen}
     */
    default MethodAcceptAndThen<V, U> andThen(ISkinMethodHolder<V, U> after) {
        Objects.requireNonNull(after);
        return new MethodAcceptAndThen<V, U>(this, after);
    }
}

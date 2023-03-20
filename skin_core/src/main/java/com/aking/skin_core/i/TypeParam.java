package com.aking.skin_core.i;

import androidx.annotation.IntDef;

import com.aking.skin_core.Constants;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Rick on 2023-03-20  17:56.
 * Description: {@link ISkinMethodHolder}缓存对象的set类型
 */
@IntDef({Constants.TYPE_VALUE, Constants.TYPE_RES_ID})
@Retention(RetentionPolicy.SOURCE)
public @interface TypeParam {
}

package com.aking.skin_core.widget;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aking.skin_core.manager.SkinFactory;

/**
 * Created by Rick at 2023/3/17 23:26.
 * Description: 换肤能力Activity基类
 */
public class SkinBaseActivity extends AppCompatActivity {

    protected SkinFactory mFactory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mFactory = new SkinFactory(getDelegate());
        getLayoutInflater().setFactory2(mFactory);
        super.onCreate(savedInstanceState);
    }
}

package com.aking.skinplugin;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;

import com.aking.skin_core.widget.SkinBaseActivity;
import com.aking.skinplugin.databinding.ActivityMainBinding;

public class MainActivity extends SkinBaseActivity {

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initView();
    }

    private void initView() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, new MFragment());
        fragmentTransaction.commitNow();

        mBinding.btnStep.setOnClickListener((v) -> {
            startActivity(new Intent(this, MainActivity2.class));
        });

    }

}
package com.aking.skinplugin;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.aking.skin_core.manager.SkinManager;
import com.aking.skin_core.widget.SkinBaseActivity;
import com.aking.skinplugin.databinding.ActivityMain2Binding;

import java.util.ArrayList;

public class MainActivity2 extends SkinBaseActivity {

    private ActivityMain2Binding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main2);
        initView();
    }


    private void initView() {
        mBinding.btnChange.setOnClickListener(v -> {
            if (!Permission.checkPermission(this)) return;
            if (SkinManager.INSTANCE.isSkinState()) {
                SkinManager.INSTANCE.loadDefault();
            } else {
                SkinManager.INSTANCE.loadSkinAssets("skin.apk");
            }
        });
        MAdapter mAdapter = new MAdapter();
        mBinding.recyclerView.setAdapter(mAdapter);
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("");
        }
        mAdapter.submitList(list);

        MDialog mDialog = new MDialog(this);

        mBinding.btnDialog.setOnClickListener(v -> {
            mDialog.show();
        });
    }
}
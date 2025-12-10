package com.aking.skinplugin;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.aking.skin_core.manager.SkinManager;
import com.aking.skin_core.widget.SkinBaseActivity;
import com.aking.skin_core.widget.SkinBinder;
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
            //if (!Permission.checkPermission(this)) return;
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

        // 动态添加 View 换肤示例
        addDynamicViewDemo();
    }

    /**
     * 动态添加 View 换肤示例
     */
    private void addDynamicViewDemo() {
        // 创建容器
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        containerParams.topMargin = 32;
        container.setLayoutParams(containerParams);

        // 创建标题
        TextView title = new TextView(this);
        title.setText("动态添加的 View（SkinBinder）");
        title.setTextSize(20);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleParams.bottomMargin = 16;
        title.setLayoutParams(titleParams);
        container.addView(title);

        // 使用 SkinBinder 绑定标题换肤 (绑定到 View 生命周期)
        SkinBinder.bind(title)
                .bindColor(R.color.color_blue_50, TextView::setTextColor)
                .bind();

        // 创建动态 TextView
        TextView dynamicTextView = new TextView(this);
        dynamicTextView.setText("动态 TextView - 文字颜色换肤");
        dynamicTextView.setTextSize(18);
        dynamicTextView.setGravity(Gravity.CENTER);
        dynamicTextView.setPadding(16, 32, 16, 32);
        container.addView(dynamicTextView);

        // 使用 SkinBinder 绑定换肤 (绑定到 LifecycleOwner)
        SkinBinder.bind(dynamicTextView)
                .bindColor(R.color.color_blue_50, TextView::setTextColor)
                .bindColor(R.color.color_blue, View::setBackgroundColor)
                .bindTo(this);

        // 添加到布局
        mBinding.root.addView(container, 2);
    }
}
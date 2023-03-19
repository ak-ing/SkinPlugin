package com.aking.skinplugin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.aking.skin_core.manager.SkinManager;
import com.aking.skin_core.widget.SkinBaseActivity;

import java.util.ArrayList;

public class MainActivity2 extends SkinBaseActivity {

    private Button btnChange;
    private EditText editText;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initView();
        btnChange.setOnClickListener(v -> {
            if (!checkPermission()) return;
            if (SkinManager.INSTANCE.isSkinState()) {
                SkinManager.INSTANCE.reset();
            } else {
                SkinManager.INSTANCE.loadSkinFile(Environment.getExternalStorageDirectory() + "/skin.apk");
            }
        });
        MAdapter mAdapter = new MAdapter();
        recyclerView.setAdapter(mAdapter);
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("");
        }
        mAdapter.submitList(list);

        MDialog mDialog = new MDialog(this);

        findViewById(R.id.btnDialog).setOnClickListener(v -> {
            mDialog.show();
        });
    }


    boolean checkPermission() {
        if (Environment.isExternalStorageManager()) {
            return true;
        } else {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return false;
        }
    }

    private void initView() {
        btnChange = (Button) findViewById(R.id.btnChange);
        editText = (EditText) findViewById(R.id.editText);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }
}
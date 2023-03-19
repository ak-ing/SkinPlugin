package com.aking.skinplugin;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

/**
 * Created by Rick at 2023/3/19 23:29.
 *
 * @Description:
 */
class MDialog extends Dialog {
    public MDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_me);
        TextView textView = findViewById(R.id.tvContent);
        textView.setText("Dialog");
    }
}

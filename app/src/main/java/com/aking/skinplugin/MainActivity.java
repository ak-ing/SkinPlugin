package com.aking.skinplugin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.FragmentTransaction;

import com.aking.skin_core.manager.SkinManager;
import com.aking.skin_core.widget.SkinBaseActivity;

public class MainActivity extends SkinBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.tvHello);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, new MFragment());
        fragmentTransaction.commitNow();
        button.setOnClickListener((v) -> {
            startActivity(new Intent(this, MainActivity2.class));
        });


//        BiConsumer<ImageView, Integer> biConsumer = ImageView::setBackgroundResource;
//        biConsumer.accept(imageView, R.mipmap.ic_launcher_round);

    }

}
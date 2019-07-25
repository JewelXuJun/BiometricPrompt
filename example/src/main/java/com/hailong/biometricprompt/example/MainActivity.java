package com.hailong.biometricprompt.example;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

/**
 * Created by ZuoHailong on 2019/7/9.
 */
public class MainActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnFingerprint).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, FingerDemoActivity.class)));

        findViewById(R.id.btnFace).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, FaceDemoActivity.class)));
    }
}

package com.hailong.biometrics.fingerprint.example;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.hailong.biometrics.fingerprint.FingerprintCallback;
import com.hailong.biometrics.fingerprint.FingerprintVerifyManager;

/**
 * 指纹识别Demo
 * Created by ZuoHailong on 2019/7/9.
 */
public class FingerDemoActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_finger);
        findViewById(R.id.tvFingerprint).setOnClickListener(v -> {
            fingerprint();
        });
    }

    private void fingerprint() {
        FingerprintVerifyManager.Builder builder = new FingerprintVerifyManager.Builder(FingerDemoActivity.this);
        builder.callback(fingerprintCallback)
                .fingerprintColor(ContextCompat.getColor(FingerDemoActivity.this, R.color.colorPrimary))
                .build();
    }

    private FingerprintCallback fingerprintCallback = new FingerprintCallback() {
        @Override
        public void onSucceeded() {
            Toast.makeText(FingerDemoActivity.this, getString(R.string.biometricprompt_verify_success), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed() {
            Toast.makeText(FingerDemoActivity.this, getString(R.string.biometricprompt_verify_failed), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onUsepwd() {
            Toast.makeText(FingerDemoActivity.this, getString(R.string.fingerprint_usepwd), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(FingerDemoActivity.this, getString(R.string.fingerprint_cancel), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onHwUnavailable() {
            Toast.makeText(FingerDemoActivity.this, getString(R.string.biometricprompt_finger_hw_unavailable), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNoneEnrolled() {
            //弹出提示框，跳转指纹添加页面
            AlertDialog.Builder lertDialogBuilder = new AlertDialog.Builder(FingerDemoActivity.this);
            lertDialogBuilder.setTitle(getString(R.string.biometricprompt_tip))
                    .setMessage(getString(R.string.biometricprompt_finger_add))
                    .setCancelable(false)
                    .setNegativeButton(getString(R.string.biometricprompt_finger_add_confirm), ((DialogInterface dialog, int which) -> {
                        Intent intent = new Intent(Settings.ACTION_FINGERPRINT_ENROLL);
                        startActivity(intent);
                    }
                    ))
                    .setPositiveButton(getString(R.string.biometricprompt_cancel), ((DialogInterface dialog, int which) -> {
                        dialog.dismiss();
                    }
                    ))
                    .create().show();
        }

    };
}

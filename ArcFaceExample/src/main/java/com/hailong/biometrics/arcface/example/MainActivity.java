package com.hailong.biometrics.arcface.example;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.arcsoft.face.ErrorInfo;
import com.blankj.utilcode.util.ActivityUtils;
import com.drumbeat.baselib.base.activity.BaseActivity;
import com.hailong.biometrics.arcface.callback.FaceCallback;
import com.hailong.biometrics.arcface.FaceHelper;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.READ_PHONE_STATE;
import static com.hailong.biometrics.arcface.example.constant.Constant.APP_ID;
import static com.hailong.biometrics.arcface.example.constant.Constant.FACE_SDK_KEY;

public class MainActivity extends BaseActivity {

    @BindView(R.id.btnActiveOnline)
    Button btnActiveOnline;
    @BindView(R.id.btnImage)
    Button btnImage;
    @BindView(R.id.btnVideo)
    Button btnVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        activeOnline();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FaceHelper.newInstance().uninitFaceEngine();
    }

    @Override
    public void onEmptyPageClick() {

    }

    @Override
    public void initView() {
        customActionBar.setCenterTitleText(getString(R.string.app_name))
                .setLeftVisiable(false)
                .setRightVisiable(false);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.btnActiveOnline, R.id.btnImage, R.id.btnVideo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnActiveOnline:
                activeOnline();
                break;
            case R.id.btnImage:
                startActivity(ImageModeActivity.class);
                break;
            case R.id.btnVideo:
                startActivity(VideoModeActivity.class);
                break;
        }
    }

    /**
     * 查询手机状态权限，并授权激活当前设备
     */
    private void activeOnline() {
        AndPermission.with(getContext()).runtime().permission(READ_PHONE_STATE).onGranted(new Action<List<String>>() {
            // manifest 必须添加权限，并代码请求 READ_PHONE_STATE 权限，此注释是为了拒绝使用系统方法判断权限是否获取
            @SuppressLint("MissingPermission")
            @Override
            public void onAction(List<String> data) {
                //在线授权激活
                FaceHelper.newInstance().activeOnlie(getContext(), APP_ID, FACE_SDK_KEY, new FaceCallback() {
                    @Override
                    public void onSuccess() {
                        btnActiveOnline.setVisibility(View.GONE);
                        showToastShort("此设备授权激活成功");
                    }

                    @Override
                    public void onFail(int errorCode) {
                        // MERR_ASF_ALREADY_ACTIVATED：此设备已激活过
                        if (errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                            showToastShort("此设备授权激活失败，错误码：" + errorCode);
                            btnActiveOnline.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }).onDenied(new Action<List<String>>() {
            @Override
            public void onAction(List<String> data) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("提示")
                        .setMessage("请求获取手机状态权限，否则面部识别功能不可用")
                        .setPositiveButton("好的", (dialog, which) -> activeOnline()).setNegativeButton("退出", (dialog, which) -> ActivityUtils.finishAllActivities()).create().show();
            }
        }).start();
    }
}

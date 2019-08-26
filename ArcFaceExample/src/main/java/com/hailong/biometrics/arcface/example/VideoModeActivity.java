package com.hailong.biometrics.arcface.example;

import android.content.DialogInterface;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.TextureView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.arcsoft.face.FaceEngine;
import com.drumbeat.baselib.base.activity.BaseActivity;
import com.hailong.biometrics.arcface.FaceHelper;
import com.hailong.biometrics.arcface.bean.FaceDetectResultBean;
import com.hailong.biometrics.arcface.callback.FaceCallback;
import com.hailong.biometrics.arcface.callback.FaceDetectCallback;
import com.hailong.biometrics.arcface.example.utils.camera.CameraHelper;
import com.hailong.biometrics.arcface.example.utils.camera.CameraListener;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_PHONE_STATE;

/**
 * Created by ZuoHailong on 2019-08-20
 */
public class VideoModeActivity extends BaseActivity implements ViewTreeObserver.OnGlobalLayoutListener {

    @BindView(R.id.textureView)
    TextureView previewView;
    @BindView(R.id.tvFace)
    TextView tvFace;

    private Camera.Size previewSize;

    private FaceEngine faceEngine = new FaceEngine();
    private CameraHelper cameraHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_mode);
        ButterKnife.bind(this);
        // 保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 监听视图的可见性变化，当视图可见后，初始化 FaceEngine 和 Camera，并移除此监听，回调函数onGlobalLayout()
        previewView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void initView() {
        customActionBar.setCenterTitleText("相机预览（视频模式）")
                .setLeftTextVisiable(false)
                .setRightVisiable(false);
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FaceHelper.newInstance().uninitFaceEngine();
        if (cameraHelper != null) {
            cameraHelper.release();
            cameraHelper = null;
        }
    }

    // 相机预览视图可见
    @Override
    public void onGlobalLayout() {
        // 移除视图的可见性变化监听
        previewView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        requestPermissions();
    }

    /**
     * 请求相机权限
     */
    private void requestPermissions() {
        AndPermission.with(getContext())
                .runtime()
                .permission(READ_PHONE_STATE, CAMERA)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        initFaceEngine();
                        initCamera();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("提示")
                                .setMessage("请求获取相机权限，否则视频模式的面试识别功能不可用")
                                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermissions();
                                    }
                                }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).create().show();
                    }
                })
                .start();
    }

    // 初始化虹软人脸引擎
    private void initFaceEngine() {
        FaceHelper.newInstance().initFaceEngineVideo(getContext(), new FaceCallback() {
            @Override
            public void onSuccess() {
                showToastShort("人脸引擎初始化成功");
            }

            @Override
            public void onFail(int errorCode) {
                showToastLong("人脸引擎初始化失败，错误码：" + errorCode);
            }
        });
    }

    //  初始化相机，设置监听，进行人脸检测
    private void initCamera() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        CameraListener cameraListener = new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                previewSize = camera.getParameters().getPreviewSize();
            }

            @Override
            public void onPreview(byte[] data, Camera camera) {
                showLoading();
                FaceHelper.newInstance().detectFace(data, previewSize.width, previewSize.height, new FaceDetectCallback() {
                    @Override
                    public void onSuccess(List<FaceDetectResultBean> faceDetectResultBeans) {
                        hideLoading();
                        if (faceDetectResultBeans == null || faceDetectResultBeans.size() == 0) {
                            showToastLong("未检测到人脸信息");
                            return;
                        }
                        tvFace.setVisibility(View.VISIBLE);
                        tvFace.setText(faceDetectResultBeans.get(0).formatData());
                    }

                    @Override
                    public void onFail(int errorCode) {
                        hideLoading();
                        showToastLong("人脸检测失败，错误码：" + errorCode);
                    }
                });
            }

            @Override
            public void onCameraClosed() {

            }

            @Override
            public void onCameraError(Exception e) {

            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {

            }
        };

        cameraHelper = new CameraHelper.Builder()
                .previewViewSize(new Point(previewView.getMeasuredWidth(), previewView.getMeasuredHeight()))
                .rotation(getWindowManager().getDefaultDisplay().getRotation())
                .specificCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT)
                .isMirror(false)
                .previewOn(previewView)
                .cameraListener(cameraListener)
                .build();
        cameraHelper.init();
        cameraHelper.start();
    }
}

package com.hailong.biometrics.arcface.example;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arcsoft.face.FaceFeature;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.PathUtils;
import com.bumptech.glide.Glide;
import com.drumbeat.baselib.base.BaseActivity;
import com.hailong.biometrics.arcface.FaceHelper;
import com.hailong.biometrics.arcface.bean.FaceDetectResultBean;
import com.hailong.biometrics.arcface.callback.FaceCallback;
import com.hailong.biometrics.arcface.callback.FaceCompareCallback;
import com.hailong.biometrics.arcface.callback.FaceDetectCallback;
import com.hailong.biometrics.arcface.example.db.entity.TbUserFace;
import com.hailong.biometrics.arcface.example.utils.GlideEngine;
import com.hailong.biometrics.arcface.example.view.CommonRecycleViewAdapter;
import com.hailong.biometrics.arcface.example.view.ViewHolder;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.models.album.entity.Photo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 图片模式示例
 * Created by ZuoHailong on 2019/8/7.
 */
public class ImageModeActivity extends BaseActivity {

    @BindView(R.id.btnInitFaceEngine)
    Button btnInitFaceEngine;
    @BindView(R.id.ivPhoto)
    ImageView ivPhoto;
    @BindView(R.id.btnDetect)
    Button btnDetect;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.layoutRegister)
    LinearLayout layoutRegister;

    private Bitmap bitmapSelected;
    private int positionRegistering = -1;

    private List<FaceDetectResultBean> rowsBeanList = new ArrayList<>();
    private CommonRecycleViewAdapter<FaceDetectResultBean> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_mode);
        ButterKnife.bind(this);
        initFaceEngine();
    }

    @Override
    public void onEmptyPageClick() {

    }

    @Override
    public void initView() {
        customActionBar.setCenterTitleText("图片模式")
                .setLeftTextVisiable(false)
                .setRightVisiable(false);
        adapter = new CommonRecycleViewAdapter<FaceDetectResultBean>(getContext(), R.layout.listitem_faceinfo, rowsBeanList) {
            @Override
            public void convert(ViewHolder holder, FaceDetectResultBean faceInfoOutputBean, int position) {
                ((TextView) holder.getView(R.id.textView)).setText("人脸" + (position + 1) + "\n\n" + faceInfoOutputBean.formatData());
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((view, position) -> {
            FaceDetectResultBean detectResultBean = rowsBeanList.get(position);
            FaceFeature faceFeature = detectResultBean.getFaceFeature();
            if (faceFeature == null) {
                showToastShort("未获取到其人脸特征数据，无法注册，请重新检测");
                return;
            }
            // 从数据库查询所有已注册的用户人脸数据
            List<TbUserFace> userFaceList = CustomApplication.getApplication().getDaoSession().loadAll(TbUserFace.class);
            if (userFaceList == null || userFaceList.size() == 0) {
                // 注册新人脸
                layoutRegister.setVisibility(View.VISIBLE);
                positionRegistering = position;
                return;
            }
            // 与数据库中已注册人脸比对，当有相似度 >= 0.9 的，则认为是已注册人脸
            for (int i = 0; i < userFaceList.size(); i++) {
                TbUserFace userFace = userFaceList.get(i);
                int finalI = i;
                FaceHelper.newInstance().compareFace(new FaceFeature(userFace.getFaceFeature()), faceFeature, new FaceCompareCallback() {
                    @Override
                    public void onSuccess(float similarScore) {
                        /*if (finalI == userFaceList.size() - 1) {
                            if (similarScoreMax >= 0.9) {
                                showToastLong("你好啊，老朋友 " + userFace.getName());
                                return;
                            }
                        }*/
                    }

                    @Override
                    public void onFail(int errorCode) {

                    }
                });
            }
            // 注册新人脸
            layoutRegister.setVisibility(View.VISIBLE);
            positionRegistering = position;
        });
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.btnInitFaceEngine, R.id.btnSelectAlbum, R.id.btnDetect, R.id.btnRegister, R.id.layoutRegister})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnInitFaceEngine:
                initFaceEngine();
                break;
            case R.id.btnSelectAlbum:
                selectAlbum();
                break;
            case R.id.btnDetect:
                showLoading();
                FaceHelper.newInstance().detectFace(bitmapSelected, new FaceDetectCallback() {

                    @Override
                    public void onSuccess(List<FaceDetectResultBean> faceDetectResultBeans) {
                        hideLoading();
                        if (faceDetectResultBeans == null || faceDetectResultBeans.size() == 0) {
                            showToastLong("未检测到人脸信息");
                            return;
                        }
                        rowsBeanList.clear();
                        rowsBeanList.addAll(faceDetectResultBeans);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFail(int errorCode) {
                        hideLoading();
                        showToastLong("人脸检测失败，错误码：" + errorCode);
                    }
                });
                break;
            case R.id.btnRegister:
                String name = etName.getEditableText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    showToastShort("请输入姓名");
                    return;
                }
                insertToDB(name);
                break;
            case R.id.layoutRegister:
                layoutRegister.setVisibility(View.GONE);
                break;
        }
    }

    private void initFaceEngine() {
        FaceHelper.newInstance().initFaceEngineImage(getContext(), new FaceCallback() {
            @Override
            public void onSuccess() {
                btnInitFaceEngine.setVisibility(View.GONE);
                showToastShort("人脸引擎初始化成功");
            }

            @Override
            public void onFail(int errorCode) {
                showToastLong("人脸引擎初始化失败，错误码：" + errorCode);
                btnInitFaceEngine.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 选择图片
     */
    private void selectAlbum() {
        btnDetect.setVisibility(View.INVISIBLE);
        EasyPhotos.createAlbum(this, true, GlideEngine.getInstance())
                .setFileProviderAuthority(AppUtils.getAppPackageName() + ".fileProvider")
                .start(new SelectCallback() {
                    @Override
                    public void onResult(ArrayList<Photo> photos, ArrayList<String> paths, boolean isOriginal) {
                        if (paths == null || paths.size() == 0) {
                            showToastLong("请重新选择照片");
                            return;
                        }
                        //压缩图片
                        showLoading();
                        Luban.with(getContext())
                                .load(paths.get(0))
                                .setTargetDir(PathUtils.getExternalAppPicturesPath())
                                .setCompressListener(new OnCompressListener() {
                                    @Override
                                    public void onStart() {

                                    }

                                    @Override
                                    public void onSuccess(File file) {
                                        hideLoading();
                                        if (file == null) {
                                            showToastLong("图片压缩异常，请重新选择");
                                            return;
                                        }
                                        bitmapSelected = BitmapFactory.decodeFile(file.getAbsolutePath());
                                        Glide.with(getContext()).load(file).into(ivPhoto);
                                        btnDetect.setVisibility(View.VISIBLE);
                                        hideLoading();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        hideLoading();
                                        showToastLong("图片压缩异常，请重新选择");
                                    }
                                }).launch();
                    }
                });
    }

    /**
     * 向数据库中写入人脸信息
     *
     * @param name 人脸所属人姓名
     */
    private void insertToDB(String name) {
        FaceFeature faceFeature = rowsBeanList.get(positionRegistering).getFaceFeature();
        TbUserFace userFaceEntity = new TbUserFace();
        userFaceEntity.setFaceId(System.currentTimeMillis() + "");
        userFaceEntity.setName(name);
        userFaceEntity.setFaceFeature(faceFeature.getFeatureData());
        CustomApplication.getApplication().getDaoSession().insert(userFaceEntity);
        showToastShort("注册成功");
        etName.setText("");
        positionRegistering = -1;
        layoutRegister.setVisibility(View.GONE);
    }

}

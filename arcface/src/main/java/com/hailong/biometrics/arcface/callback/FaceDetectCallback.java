package com.hailong.biometrics.arcface.callback;

import com.hailong.biometrics.arcface.bean.FaceDetectResultBean;

import java.util.List;

/**
 * 面部检测的结果回调
 * Created by ZuoHailong on 2019/8/7.
 */
public interface FaceDetectCallback {
    void onSuccess(List<FaceDetectResultBean> faceDetectResultBeans);

    void onFail(int errorCode);
}

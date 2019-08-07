package com.hailong.biometrics.arcface.callback;

/**
 * 面部各种处理行为的结果回调
 * Created by ZuoHailong on 2019/8/7.
 */
public interface FaceCallback {
    void onSuccess();

    void onFail(int errorCode);
}

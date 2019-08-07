package com.hailong.biometrics.arcface.callback;

/**
 * 面部比对的结果回调
 * Created by ZuoHailong on 2019/8/7.
 */
public interface FaceCompareCallback {
    /**
     * @param similarScore 相似度，0 ~ 1.0
     */
    void onSuccess(float similarScore);

    void onFail(int errorCode);
}

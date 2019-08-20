package com.hailong.biometrics.arcface;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.RequiresPermission;

import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.Face3DAngle;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;
import com.hailong.biometrics.arcface.bean.FaceDetectResultBean;
import com.hailong.biometrics.arcface.callback.FaceCallback;
import com.hailong.biometrics.arcface.callback.FaceCompareCallback;
import com.hailong.biometrics.arcface.callback.FaceDetectCallback;
import com.hailong.biometrics.arcface.utils.ArcImageUtils;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_PHONE_STATE;
import static com.arcsoft.face.FaceEngine.ASF_AGE;
import static com.arcsoft.face.FaceEngine.ASF_FACE3DANGLE;
import static com.arcsoft.face.FaceEngine.ASF_GENDER;
import static com.arcsoft.face.FaceEngine.ASF_LIVENESS;

/**
 * Created by ZuoHailong on 2019/8/2.
 */
public class FaceHelper {

    private static FaceHelper faceHelper;

    private FaceEngine faceEngine;

    public static FaceHelper newInstance() {
        if (faceHelper == null) {
            synchronized (FaceHelper.class) {
                if (faceHelper == null) {
                    faceHelper = new FaceHelper();
                }
            }
        }
        return faceHelper;
    }

    /**
     * 在当前设备上，在线授权激活 FACE SDK
     *
     * @param context 上下文
     * @param appId   虹软 APP 应用的 APP ID
     * @param sdkKey  此应用的 Face SDK 的 SDK KEY
     * @return ErrorInfo.code，激活结果码，要与 ErrorInfo 中 常量比对判断异常原因
     */
    @RequiresPermission(allOf = {READ_PHONE_STATE, INTERNET})
    public void activeOnlie(Context context, String appId, String sdkKey, FaceCallback callback) {
        int resultCode = new FaceEngine().activeOnline(context, appId, sdkKey);
        if (resultCode == ErrorInfo.MOK) {
            callback.onSuccess();
        } else {
            callback.onFail(resultCode);
        }
    }

    /**
     * 初始化人脸引擎
     *
     * @param context
     * @param callback
     */
    public void initFaceEngineImage(Context context, FaceCallback callback) {
        initFaceEngineImage(context, FaceEngine.ASF_OP_0_ONLY, 30, 10, callback);
    }

    /**
     * 初始化人脸引擎
     *
     * @param context
     * @param detectFaceOrientPriority 人脸检测角度
     * @param detectFaceScaleVal       识别的最小人脸比例（图片长边与人脸框长边的比值）
     * @param detectFaceMaxNum         最多检测出人脸的数量 1~50
     * @param callback
     */
    public void initFaceEngineImage(Context context, int detectFaceOrientPriority, int detectFaceScaleVal, int detectFaceMaxNum, FaceCallback callback) {
        if (faceEngine == null) {
            faceEngine = new FaceEngine();
        }
        /*
         * 初始化人脸引擎
         * 参数2：检测模式，这里指定为图片模式
         * 参数3：人脸检测角度
         * 参数4：识别的最小人脸比例（图片长边与人脸框长边的比值）
         * 参数5：最多检测出人脸的数量 1~50
         * 参数6：启动的检测组合，如检测、识别、年龄、三维信息等
         *
         * */
        int initResult = faceEngine.init(context, FaceEngine.ASF_DETECT_MODE_IMAGE, detectFaceOrientPriority,
                detectFaceScaleVal, detectFaceMaxNum, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT | ASF_AGE | ASF_GENDER | ASF_FACE3DANGLE | ASF_LIVENESS);
        if (initResult == ErrorInfo.MOK) {
            callback.onSuccess();
        } else {
            callback.onFail(initResult);
        }
    }

    /**
     * 对人脸图片进行检测
     *
     * @param mBitmap  传入的人脸图片
     * @param callback 人脸检测回调
     */
    public void detectFace(Bitmap mBitmap, FaceDetectCallback callback) {

        // 没有init或者init失败
        if (faceEngine == null) {
            callback.onFail(ErrorInfo.MERR_BAD_STATE);
            return;
        }

        Bitmap bitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
        if (bitmap == null) {
            callback.onFail(ErrorInfo.MERR_UNKNOWN);
            return;
        }

        // 确保传给引擎的BGR24数据宽度为4的倍数
        bitmap = ArcImageUtils.alignBitmapForBgr24(bitmap);
        if (bitmap == null) {
            callback.onFail(ErrorInfo.MERR_UNKNOWN);
            return;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // bitmap转bgr24
        byte[] bgr24 = ArcImageUtils.bitmapToBgr24(bitmap);
        if (bgr24 == null || bgr24.length == 0) {
            callback.onFail(ErrorInfo.MERR_UNKNOWN);
            return;
        }

        // 开始检测
        detectFace(bgr24, width, height, FaceEngine.CP_PAF_BGR24, callback);

    }

    /**
     * 对人脸的视频流进行检测（相机预览等）
     *
     * @param data     人脸的视频流
     * @param width    预览视图宽
     * @param height   预览视图高
     * @param callback 检测回调
     */
    public void detectFace(byte[] data, int width, int height, FaceDetectCallback callback) {

        // 没有init或者init失败
        if (faceEngine == null) {
            callback.onFail(ErrorInfo.MERR_BAD_STATE);
            return;
        }

        // 开始检测
        detectFace(data, width, height, FaceEngine.CP_PAF_NV21, callback);

    }

    /**
     * 比对人脸相似度
     *
     * @param first    first FaceFeature
     * @param second   second FaceFeature
     * @param callback
     */
    public void compareFace(FaceFeature first, FaceFeature second, FaceCompareCallback callback) {
        // 没有init或者init失败
        if (faceEngine == null) {
            callback.onFail(ErrorInfo.MERR_BAD_STATE);
            return;
        }
        FaceSimilar faceSimilar = new FaceSimilar();
        int compareResult = faceEngine.compareFaceFeature(first, second, faceSimilar);
        if (compareResult == ErrorInfo.MOK) {
            callback.onSuccess(faceSimilar.getScore());
        } else {
            callback.onFail(compareResult);
        }
    }

    /**
     * 销毁人脸引擎
     */
    public void uninitFaceEngine() {
        if (faceEngine != null) {
            faceEngine.unInit();
            faceEngine = null;
        }
    }

    private void detectFace(byte[] data, int width, int height, int format, FaceDetectCallback callback) {
        /*
         * 1、检测是否存在人脸
         * */
        // 检测到的人脸列表
        List<FaceInfo> faceInfoList = new ArrayList<>();
        // 开始检测
        int detectFacesResult = faceEngine.detectFaces(data, width, height, format, faceInfoList);
        // 检测异常
        if (detectFacesResult != ErrorInfo.MOK) {
            callback.onFail(detectFacesResult);
            return;
        }
        // 未检测到脸部信息
        if (faceInfoList.size() == 0) {
            callback.onFail(ErrorInfo.MERR_ASF_EX_INVALID_FACE_INFO);
            return;
        }

        /*
         * 2、进行活体、年龄、性别、三维角度检测
         * process()执行后，可调用后续方法获取活体、年龄、性别、三维角度的信息
         * */
        List<LivenessInfo> livenessInfoList = new ArrayList<>();
        List<GenderInfo> genderInfoList = new ArrayList<>();
        List<AgeInfo> ageInfoList = new ArrayList<>();
        List<Face3DAngle> face3DAngleList = new ArrayList<>();

        // 2.1 process
        int processResult = faceEngine.process(data, width, height, format, faceInfoList, ASF_AGE | ASF_GENDER | ASF_FACE3DANGLE | ASF_LIVENESS);
        if (processResult != ErrorInfo.MOK) {
            callback.onFail(detectFacesResult);
            return;
        }
        // 2.2 活体
        int livenessResult = faceEngine.getLiveness(livenessInfoList);
        if (livenessResult != ErrorInfo.MOK) {
            callback.onFail(detectFacesResult);
            return;
        }
        // 2.3 性别
        int genderResult = faceEngine.getGender(genderInfoList);
        if (genderResult != ErrorInfo.MOK) {
            callback.onFail(detectFacesResult);
            return;
        }
        // 2.4 年龄
        int ageResult = faceEngine.getAge(ageInfoList);
        if (ageResult != ErrorInfo.MOK) {
            callback.onFail(detectFacesResult);
            return;
        }
        // 2.5 三维
        int face3DAngleResult = faceEngine.getFace3DAngle(face3DAngleList);
        if (face3DAngleResult != ErrorInfo.MOK) {
            callback.onFail(detectFacesResult);
            return;
        }


        List<FaceDetectResultBean> faceDetectResultBeans = new ArrayList<>();
        /*
         * 3、提取人脸特征数据
         * */
        for (int i = 0; i < faceInfoList.size(); i++) {
            FaceInfo faceInfo = faceInfoList.get(i);
            FaceFeature faceFeature = new FaceFeature();
            faceEngine.extractFaceFeature(data, width, height, format, faceInfo, faceFeature);
            // 将人脸特征数据和年龄、性别等信息储存起来
            FaceDetectResultBean detectResultBean = new FaceDetectResultBean();
            detectResultBean.setFaceFeature(faceFeature);

            detectResultBean.setFaceInfo(faceInfo);
            detectResultBean.setLivenessInfo(livenessInfoList.size() > i ? livenessInfoList.get(i) : null);
            detectResultBean.setGenderInfo(genderInfoList.size() > i ? genderInfoList.get(i) : null);
            detectResultBean.setAgeInfo(ageInfoList.size() > i ? ageInfoList.get(i) : null);
            detectResultBean.setFace3DAngle(face3DAngleList.size() > i ? face3DAngleList.get(i) : null);

            faceDetectResultBeans.add(detectResultBean);
        }

        callback.onSuccess(faceDetectResultBeans);
    }

}

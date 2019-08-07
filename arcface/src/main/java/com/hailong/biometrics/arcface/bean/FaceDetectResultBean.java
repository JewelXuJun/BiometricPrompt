package com.hailong.biometrics.arcface.bean;

import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.Face3DAngle;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;

/**
 * 人脸信息输出展示的bean
 * Created by ZuoHailong on 2019-08-04
 */
public class FaceDetectResultBean {
    private FaceInfo faceInfo;
    private LivenessInfo livenessInfo;
    private GenderInfo genderInfo;
    private AgeInfo ageInfo;
    private Face3DAngle face3DAngle;
    private FaceFeature faceFeature;

    public FaceFeature getFaceFeature() {
        return faceFeature;
    }

    public void setFaceFeature(FaceFeature faceFeature) {
        this.faceFeature = faceFeature;
    }

    public FaceInfo getFaceInfo() {
        return faceInfo;
    }

    public void setFaceInfo(FaceInfo faceInfo) {
        this.faceInfo = faceInfo;
    }

    public LivenessInfo getLivenessInfo() {
        return livenessInfo;
    }

    public void setLivenessInfo(LivenessInfo livenessInfo) {
        this.livenessInfo = livenessInfo;
    }

    public GenderInfo getGenderInfo() {
        return genderInfo;
    }

    public void setGenderInfo(GenderInfo genderInfo) {
        this.genderInfo = genderInfo;
    }

    public AgeInfo getAgeInfo() {
        return ageInfo;
    }

    public void setAgeInfo(AgeInfo ageInfo) {
        this.ageInfo = ageInfo;
    }

    public Face3DAngle getFace3DAngle() {
        return face3DAngle;
    }

    public void setFace3DAngle(Face3DAngle face3DAngle) {
        this.face3DAngle = face3DAngle;
    }

    /**
     * 对检测的结果信息格式化
     *
     * @return
     */
    public String formatData() {
        StringBuilder stringBuilder = new StringBuilder();

        // FaceInfo
        if (faceInfo != null) {
            stringBuilder.append("1、人脸信息\nFaceID：" + faceInfo.getFaceId() + "\n摄像头：");
            switch (faceInfo.getOrient()) {
                case 1:
                    stringBuilder.append("静态图片\n");
                    break;
                case 2:
                    stringBuilder.append("后置\n");
                    break;
                case 3:
                    stringBuilder.append("前置\n");
                    break;
            }
        }
        // 活体信息
        if (livenessInfo != null) {
            switch (livenessInfo.getLiveness()) {
                case LivenessInfo.ALIVE:
                    stringBuilder.append("2、活体信息：活体\n");
                    break;
                case LivenessInfo.NOT_ALIVE:
                    stringBuilder.append("2、活体信息：非活体\n");
                    break;
                default:
                    stringBuilder.append("2、活体信息：未知\n");
                    break;
            }
        }
        // 性别
        if (genderInfo != null) {
            switch (genderInfo.getGender()) {
                case GenderInfo.MALE:
                    stringBuilder.append("3、性别：男\n");
                    break;
                case GenderInfo.FEMALE:
                    stringBuilder.append("3、性别：女\n");
                    break;
                default:
                    stringBuilder.append("3、性别：未知\n");
                    break;
            }
        }
        // 年龄
        if (ageInfo != null) {
            stringBuilder.append("4、年龄：" + (ageInfo.getAge() == 0 ? "未知\n" : ageInfo.getAge() + "\n"));
        }
        /*
         * 三维
         * */
        if (face3DAngle != null) {
            stringBuilder.append("5、三维\n" + (face3DAngle.getStatus() == 0 ? "检测正常，下述三维信息可信\n" : "检测异常，下述三维信息不可信\n"));
            //偏航角，机头左右摆动
            stringBuilder.append("偏航角（机头左右摆动）：");
            if (face3DAngle.getYaw() == 0) {
                stringBuilder.append("正脸 ");
            } else if (face3DAngle.getYaw() > 0) {
                stringBuilder.append("向右侧脸 ");
            } else if (face3DAngle.getYaw() < 0) {
                stringBuilder.append("向左侧脸 ");
            }
            stringBuilder.append(face3DAngle.getYaw() + "\n");
            //俯仰角，机头上下摆动
            stringBuilder.append("俯仰角（机头上下摆动）：");
            if (face3DAngle.getPitch() == 0) {
                stringBuilder.append("正脸 ");
            } else if (face3DAngle.getPitch() > 0) {
                stringBuilder.append("抬头 ");
            } else if (face3DAngle.getPitch() < 0) {
                stringBuilder.append("低头 ");
            }
            stringBuilder.append(face3DAngle.getPitch() + "\n");
            //翻滚角，机身左下压/右下压
            stringBuilder.append("翻滚角（机身左下压/右下压）：");
            if (face3DAngle.getRoll() == 0) {
                stringBuilder.append("正脸 ");
            } else if (face3DAngle.getRoll() > 0) {
                stringBuilder.append("向右侧低头 ");
            } else if (face3DAngle.getRoll() < 0) {
                stringBuilder.append("向左侧低头 ");
            }
            stringBuilder.append(face3DAngle.getRoll() + "\n");
        }
        stringBuilder.append("6、FaceFeature：" + faceFeature + "\n");
        return stringBuilder.toString();
    }

}

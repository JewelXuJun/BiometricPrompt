package com.hailong.biometrics.arcface.example.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by ZuoHailong on 2019/8/5.
 */
@Entity
public class TbUserFace {
    @Id(autoincrement = true)
    private Long id;
    // 用户姓名
    private String name;
    // 注册人脸图片的路径
    private String imgPath;
    @NotNull
    @Unique
    private String faceId;
    private byte[] faceFeature;
    @Generated(hash = 1653648514)
    public TbUserFace(Long id, String name, String imgPath, @NotNull String faceId,
            byte[] faceFeature) {
        this.id = id;
        this.name = name;
        this.imgPath = imgPath;
        this.faceId = faceId;
        this.faceFeature = faceFeature;
    }
    @Generated(hash = 1761903628)
    public TbUserFace() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getImgPath() {
        return this.imgPath;
    }
    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
    public String getFaceId() {
        return this.faceId;
    }
    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }
    public byte[] getFaceFeature() {
        return this.faceFeature;
    }
    public void setFaceFeature(byte[] faceFeature) {
        this.faceFeature = faceFeature;
    }

}

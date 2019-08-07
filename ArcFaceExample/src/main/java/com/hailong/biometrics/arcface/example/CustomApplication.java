package com.hailong.biometrics.arcface.example;

import android.database.sqlite.SQLiteDatabase;

import com.drumbeat.baselib.base.BaseApplication;
import com.hailong.biometrics.arcface.example.db.greenDao.db.DaoMaster;
import com.hailong.biometrics.arcface.example.db.greenDao.db.DaoSession;

/**
 * Created by ZuoHailong on 2019/8/5.
 */
public class CustomApplication extends BaseApplication {

    private static CustomApplication application;
    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        initGreenDao();
    }

    public static CustomApplication getApplication() {
        return application;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    /**
     * 初始化GreenDao,直接在Application中进行初始化操作
     */
    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "face.db");
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }
}

package com.zh.physiology.assist;

import android.app.Application;

import org.xutils.DbManager;
import org.xutils.x;

/**
 * author：heng.zhang
 * date：2017/2/5
 * description：
 */
public class MyApp extends Application {
    private DbManager.DaoConfig daoConfig;
    public DbManager.DaoConfig getDaoConfig() {
        return daoConfig;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);//Xutils初始化
        daoConfig = new DbManager.DaoConfig()
                .setDbName("physiology")//创建数据库的名称
                .setDbVersion(1)//数据库版本号
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                    }
                });//数据库更新操作
    }
}

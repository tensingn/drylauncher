package com.ntensing.launcher.database;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

public class AppRepository {
    private AppDao appDao;
    private DatabaseConnection db;

    public AppRepository(Context context) {
        db = DatabaseConnectionSingleton.getInstance(context);
        appDao = db.appDao();
    }

    public LiveData<List<AppEntity>> getAllApps() {
        return appDao.getAllApps();
    }

    public LiveData<List<AppEntity>> getAppByAppId(String appId) {
        return appDao.getAppByAppId(appId);
    }

    public void insertAll(List<AppEntity> apps) {
        DatabaseConnection.executor.execute(() ->
                appDao.insertAll(apps.toArray(new AppEntity[0]))
        );
    }

    public void insert(AppEntity app) {
        appDao.insertAll(app);
    }

    public void delete(AppEntity app) {
        appDao.delete(app);
    }
}

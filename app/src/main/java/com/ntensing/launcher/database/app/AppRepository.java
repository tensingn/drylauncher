package com.ntensing.launcher.database.app;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.ntensing.launcher.database.DatabaseConnection;
import com.ntensing.launcher.database.DatabaseConnectionSingleton;
import com.ntensing.launcher.database.app.AppDao;
import com.ntensing.launcher.database.app.AppEntity;

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

    public LiveData<AppEntity> getAppByAppId(String appId) {
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

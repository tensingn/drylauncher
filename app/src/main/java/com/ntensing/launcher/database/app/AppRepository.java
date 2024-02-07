package com.ntensing.launcher.database.app;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.ntensing.launcher.database.DatabaseConnection;
import com.ntensing.launcher.database.DatabaseConnectionSingleton;

import java.util.List;

public class AppRepository {
    private static AppRepository instance;
    private AppDao appDao;
    private DatabaseConnection db;

    private AppRepository(Context context) {
        db = DatabaseConnectionSingleton.getInstance(context);
        appDao = db.appDao();
    }

    public static synchronized AppRepository getInstance(Context context) {
        if (instance == null) {
            instance = new AppRepository(context);
        }

        return instance;
    }

    public LiveData<List<AppEntity>> getAllApps() {
        return appDao.getAllApps();
    }

    public LiveData<AppEntity> getAppByAppId(String appId) {
        return appDao.getAppByAppId(appId);
    }

    public LiveData<List<AppWithGeofencesEntity>> getAppsWithGeofences() { return appDao.getAppsWithGeofences(); }


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

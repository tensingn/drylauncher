package com.ntensing.launcher.database.geofence;


import android.content.Context;

import androidx.lifecycle.LiveData;

import com.ntensing.launcher.database.DatabaseConnection;
import com.ntensing.launcher.database.DatabaseConnectionSingleton;

import java.util.List;

public class GeofenceRepository {
    private GeofenceDao geofenceDao;
    private DatabaseConnection db;

    public GeofenceRepository(Context context) {
        db = DatabaseConnectionSingleton.getInstance(context);
        geofenceDao = db.geofenceDao();
    }

    public LiveData<List<GeofenceEntity>> getAllGeofences() {
        return geofenceDao.getAllGeofences();
    }

    public LiveData<List<GeofenceEntity>> getGeofencesByAppId(String appId) {
        return geofenceDao.getGeofencesByAppId(appId);
    }

    public LiveData<GeofenceEntity> getGeofenceByGeofenceId(String geofenceId) {
        return geofenceDao.getGeofenceByGeofenceId(geofenceId);
    }

    public void insertAll(List<GeofenceEntity> geofences) {
        DatabaseConnection.executor.execute(() ->
                geofenceDao.insertAll(geofences.toArray(new GeofenceEntity[0]))
        );
    }

    public void insert(GeofenceEntity geofence) {
        DatabaseConnection.executor.execute(() ->
                geofenceDao.insertAll(geofence)
        );
    }

    public void delete(GeofenceEntity geofence) {
        geofenceDao.delete(geofence);
    }
}


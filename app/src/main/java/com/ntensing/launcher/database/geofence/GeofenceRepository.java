package com.ntensing.launcher.database.geofence;


import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.ntensing.launcher.database.DatabaseConnection;
import com.ntensing.launcher.database.DatabaseConnectionSingleton;
import com.ntensing.launcher.database.migrations.Migration1to2;
import com.ntensing.launcher.database.migrations.Migration2to3;

import java.util.List;

public class GeofenceRepository {
    private static GeofenceRepository instance;
    private GeofenceDao geofenceDao;
    private DatabaseConnection db;

    private GeofenceRepository(Context context) {
        db = DatabaseConnectionSingleton.getInstance(context);
        geofenceDao = db.geofenceDao();
    }

    public static synchronized GeofenceRepository getInstance(Context context) {
        if (instance == null) {
            instance = new GeofenceRepository(context);
        }

        return instance;
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

    public void updateCurrentlyIn(String geofenceID, boolean currentlyIn) {
        DatabaseConnection.executor.execute(() ->
                geofenceDao.updateCurrentlyIn(geofenceID, currentlyIn));
    }

    public void deleteById(String geofenceId) {
        DatabaseConnection.executor.execute(() ->
                geofenceDao.deleteById(geofenceId));
    }
}


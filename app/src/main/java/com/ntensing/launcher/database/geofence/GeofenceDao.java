package com.ntensing.launcher.database.geofence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.ntensing.launcher.database.app.AppEntity;

import java.util.List;

@Dao
public interface GeofenceDao {
    @Query("SELECT * FROM geofence")
    LiveData<List<GeofenceEntity>> getAllGeofences();

    @Query("SELECT * FROM geofence WHERE geofenceId = (:geofenceId) LIMIT 1")
    LiveData<GeofenceEntity> getGeofenceByGeofenceId(String geofenceId);

    @Query("SELECT * FROM geofence WHERE appId = (:appId)")
    LiveData<List<GeofenceEntity>> getGeofencesByAppId(String appId);

    @Insert
    void insertAll(GeofenceEntity... geofences);

    @Delete
    void delete(GeofenceEntity geofence);
}

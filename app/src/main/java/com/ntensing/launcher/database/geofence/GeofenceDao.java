package com.ntensing.launcher.database.geofence;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.google.android.gms.maps.model.LatLng;
import com.ntensing.launcher.database.app.AppEntity;

import java.util.List;

@Dao
public interface GeofenceDao {

    // GETS
    @Query("SELECT * FROM geofence;")
    LiveData<List<GeofenceEntity>> getAllGeofences();

    @Query("SELECT * FROM geofence WHERE geofenceId = :geofenceId LIMIT 1;")
    LiveData<GeofenceEntity> getGeofenceByGeofenceId(String geofenceId);

    @Query("SELECT * FROM geofence WHERE appId = :appId;")
    LiveData<List<GeofenceEntity>> getGeofencesByAppId(String appId);

    // WRITES
    @Insert
    void insertAll(GeofenceEntity... geofences);

    @Query("UPDATE geofence SET currentlyIn = :currentlyIn WHERE geofenceId = :geofenceID;")
    void updateCurrentlyIn(String geofenceID, boolean currentlyIn);

    @Query("DELETE FROM geofence WHERE geofenceId = :geofenceId;")
    void deleteById(@NonNull String geofenceId);
}

package com.ntensing.launcher.database.app;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.ntensing.launcher.database.geofence.GeofenceEntity;

import java.util.List;
import java.util.Map;

@Dao
public interface AppDao {
    // READS
    @Query("SELECT * FROM app;")
    LiveData<List<AppEntity>> getAllApps();

    @Query("SELECT * FROM app WHERE appId = (:appId) LIMIT 1;")
    LiveData<AppEntity> getAppByAppId(String appId);

    @Query("SELECT * FROM app a ")
    LiveData<List<AppWithGeofencesEntity>> getAppsWithGeofences();

    // WRITES
    @Insert
    void insertAll(AppEntity... apps);

    @Delete
    void delete(AppEntity app);
}

package com.ntensing.launcher.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AppDao {
    @Query("SELECT * FROM app")
    LiveData<List<AppEntity>> getAllApps();

    @Query("SELECT * FROM app WHERE appId = (:appId)")
    LiveData<List<AppEntity>> getAppByAppId(String appId);

    @Insert
    void insertAll(AppEntity... apps);

    @Delete
    void delete(AppEntity app);
}

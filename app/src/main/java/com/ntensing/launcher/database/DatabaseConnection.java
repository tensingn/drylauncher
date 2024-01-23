package com.ntensing.launcher.database;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.ntensing.launcher.database.app.AppDao;
import com.ntensing.launcher.database.app.AppEntity;
import com.ntensing.launcher.database.geofence.GeofenceDao;
import com.ntensing.launcher.database.geofence.GeofenceEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {AppEntity.class, GeofenceEntity.class}, version = 3, exportSchema = false)
public abstract class DatabaseConnection extends RoomDatabase {
    public abstract AppDao appDao();
    public abstract GeofenceDao geofenceDao();
    private static final int THREAD_COUNT = 4;
    public static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
}

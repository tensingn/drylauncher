package com.ntensing.launcher.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {AppEntity.class}, version = 1, exportSchema = false)
public abstract class DatabaseConnection extends RoomDatabase {
    public abstract AppDao appDao();
    private static final int THREAD_COUNT = 4;
    static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
}

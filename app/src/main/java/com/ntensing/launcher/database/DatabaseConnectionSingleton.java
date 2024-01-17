package com.ntensing.launcher.database;

import android.content.Context;

import androidx.room.Room;

public class DatabaseConnectionSingleton {
    private static DatabaseConnection instance;

    private DatabaseConnectionSingleton() {}

    public static synchronized DatabaseConnection getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, DatabaseConnection.class,"launcher").build();
        }

        return instance;
    }
}

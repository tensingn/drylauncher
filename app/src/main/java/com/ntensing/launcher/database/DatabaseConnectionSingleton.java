package com.ntensing.launcher.database;

import android.content.Context;

import androidx.room.Room;

import com.ntensing.launcher.database.migrations.Migration1to2;
import com.ntensing.launcher.database.migrations.Migration2to3;
import com.ntensing.launcher.database.migrations.Migration3to4;

public class DatabaseConnectionSingleton {
    private static DatabaseConnection instance;

    private DatabaseConnectionSingleton() {}

    public static synchronized DatabaseConnection getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, DatabaseConnection.class,"launcher")
                    .addMigrations(new Migration1to2(), new Migration2to3(), new Migration3to4())
                        .build();
        }

        return instance;
    }
}

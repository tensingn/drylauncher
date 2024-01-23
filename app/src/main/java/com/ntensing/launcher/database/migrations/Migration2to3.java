package com.ntensing.launcher.database.migrations;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migration2to3 extends Migration {
    public Migration2to3() {
        super(2, 3);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
        supportSQLiteDatabase.execSQL("ALTER TABLE `geofence` ADD COLUMN latitude REAL NOT NULL;");
        supportSQLiteDatabase.execSQL("ALTER TABLE `geofence` ADD COLUMN longitude REAL NOT NULL;");
        supportSQLiteDatabase.execSQL("ALTER TABLE `geofence` ADD COLUMN radius REAL NOT NULL;");
    }
}

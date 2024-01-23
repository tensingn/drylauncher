package com.ntensing.launcher.database.migrations;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migration1to2 extends Migration {
    public Migration1to2() {
        super(1, 2);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
        supportSQLiteDatabase.execSQL("CREATE TABLE `geofence`("
            + "`geofenceId` TEXT PRIMARY KEY NOT NULL,"
            + "`appId` TEXT NOT NULL, "
            + "FOREIGN KEY (`appId`)"
            + " REFERENCES `app` (`appId`)"
            + "     ON DELETE CASCADE"
            + "     ON UPDATE NO ACTION"
            + ");");

        supportSQLiteDatabase.execSQL("CREATE INDEX `index_geofence_appId` "
            + "ON `geofence` (`appId`);");
    }
}

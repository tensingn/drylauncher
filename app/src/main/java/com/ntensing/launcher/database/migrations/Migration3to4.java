package com.ntensing.launcher.database.migrations;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migration3to4 extends Migration {
    public Migration3to4() {
        super(3, 4);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
        supportSQLiteDatabase.execSQL("ALTER TABLE `geofence` ADD COLUMN currentlyIn INTEGER DEFAULT 0 NOT NULL;");
    }
}

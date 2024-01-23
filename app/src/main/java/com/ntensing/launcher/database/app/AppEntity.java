package com.ntensing.launcher.database.app;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "app")
public class AppEntity {
    @PrimaryKey
    @NonNull
    private String appId;

    @ColumnInfo(name = "appName")
    private String appName;

    @ColumnInfo(name = "activityName")
    private String activityName;

    public AppEntity(@NonNull String appId, @NonNull String appName, @NonNull String activityName) {
        this.appId = appId;
        this.appName = appName;
        this.activityName = activityName;
    }

    @Override
    public String toString() { return appName; }

    public String getAppId() { return appId; }

    public String getAppName() { return appName; }

    public String getActivityName() { return activityName; }
}

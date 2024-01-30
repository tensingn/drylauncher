package com.ntensing.launcher.database.app;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.ntensing.launcher.database.geofence.GeofenceEntity;

import java.util.List;

public class AppWithGeofencesEntity {
    @Embedded
    public AppEntity app;

    @Relation(
            parentColumn = "appId",
            entityColumn = "appId"
    )
    public List<GeofenceEntity> geofences;
}

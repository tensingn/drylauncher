package com.ntensing.launcher.database.geofence;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;
import com.ntensing.launcher.database.app.AppEntity;

@Entity(tableName = "geofence", foreignKeys = {
        @ForeignKey(entity = AppEntity.class, parentColumns = "appId", childColumns = "appId", onDelete = ForeignKey.CASCADE)
})
public class GeofenceEntity {
    @PrimaryKey
    @NonNull
    private String geofenceId;

    @ColumnInfo(name = "appId", index = true)
    @NonNull
    private String appId;

    @ColumnInfo(name = "latitude")
    @NonNull
    private double latitude;

    @ColumnInfo(name = "longitude")
    @NonNull
    private double longitude;

    @ColumnInfo(name = "radius")
    @NonNull
    private double radius;

    public GeofenceEntity(@NonNull String geofenceId,
                          @NonNull String appId,
                          @NonNull double latitude,
                          @NonNull double longitude,
                          @NonNull double radius) {
        this.geofenceId = geofenceId;
        this.appId = appId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    public String getGeofenceId() {
        return geofenceId;
    }

    public String getAppId() {
        return appId;
    }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }

    public double getRadius() { return radius; }

    public LatLng getLatLng() { return new LatLng(latitude, longitude); }
}

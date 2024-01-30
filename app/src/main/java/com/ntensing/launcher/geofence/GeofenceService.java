package com.ntensing.launcher.geofence;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;
import com.ntensing.launcher.LauncherViewModel;
import com.ntensing.launcher.database.geofence.GeofenceRepository;

public class GeofenceService extends ContextWrapper {
    private PendingIntent pendingIntent;

    public GeofenceService(Context context) {
        super(context);
    }

    public GeofencingRequest createGeofencingRequest(Geofence geofence) {
        return new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .build();
    }

    public Geofence createGeofence(String id, LatLng latLng, float radius, int transitionTypes) {
        return new Geofence.Builder()
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setRequestId(id)
                .setTransitionTypes(transitionTypes)
                .setLoiteringDelay(5000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
    }

    public PendingIntent getPendingIntent() {
        if (pendingIntent != null) {
            return pendingIntent;
        }

        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 2607, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        return pendingIntent;
    }

    public String getErrorString(Exception ex) {
        if (ex instanceof ApiException) {
            ApiException aex = (ApiException) ex;

            switch (aex.getStatusCode()) {
                case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                    return "NOT AVAILABLE";
                case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    return "TOO MANY PENDING INTENTS";
                case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                    return "TOO MANY PENDING INTENTS";
            }
        }

        return ex.getLocalizedMessage();
    }
}

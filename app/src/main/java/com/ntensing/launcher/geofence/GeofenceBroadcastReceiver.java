package com.ntensing.launcher.geofence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.ntensing.launcher.LauncherViewModel;
import com.ntensing.launcher.R;
import com.ntensing.launcher.database.geofence.GeofenceRepository;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private GeofenceRepository geofenceRepository;
    private static final String TAG = "GeofenceBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        geofenceRepository = GeofenceRepository.getInstance(context);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        boolean currentlyIn = geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL;

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    triggeringGeofences,
                    currentlyIn
            );

            updateCurrentlyIns(triggeringGeofences, currentlyIn);

            Log.i(TAG, geofenceTransitionDetails);
        } else {
            Log.e(TAG, "unknown transition");
        }
    }

    private String getGeofenceTransitionDetails(List<Geofence> triggeringGeofences, boolean currentlyIn) {
        String details = currentlyIn ? "entered geofence with id " : "exited geofence with id ";

        for (Geofence geofence : triggeringGeofences) {
            details += geofence.getRequestId() + ", ";
        }

        return details;
    }

    private void updateCurrentlyIns(List<Geofence> geofences, boolean currentlyIn) {
        for (Geofence geofence : geofences) {
            geofenceRepository.updateCurrentlyIn(geofence.getRequestId(), currentlyIn);
        }
    }
}
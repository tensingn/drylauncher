package com.ntensing.launcher.rules;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.ntensing.launcher.R;
import com.ntensing.launcher.database.app.AppEntity;
import com.ntensing.launcher.database.app.AppWithGeofencesEntity;
import com.ntensing.launcher.database.geofence.GeofenceEntity;

import java.util.List;

public class RulesService {
    private static RulesService instance;
    private SharedPreferences prefs;
    private Context context;

    private RulesService(Context context) {
        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static RulesService getInstance(Context context) {
        if (instance == null) {
            instance = new RulesService(context);
        }

        return instance;
    }

    public boolean shouldAllowApp(AppWithGeofencesEntity awg) {
        AppEntity app = awg.app;
        List<GeofenceEntity> geofences = awg.geofences;

        // if not top-level enabled, don't show
        String enabledPrefKey = app.getAppId() + context.getString(R.string.enabledPrefSuffix);
        if (!prefs.getBoolean(enabledPrefKey, false)) return false;

        // if location rules not enabled, show
        String locationRuleEnabledPrefKey = app.getAppId() + context.getString(R.string.locationRuleEnabledPrefSuffix);
        if (!prefs.getBoolean(locationRuleEnabledPrefKey, false)) return true;

        // check each geofence. if currently in a geofence, show app
        for (GeofenceEntity geofence : geofences) {
            if (geofence.getCurrentlyIn()) {
                return true;
            }
        }

        return false;
    }
}

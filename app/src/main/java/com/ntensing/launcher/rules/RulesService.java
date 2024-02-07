package com.ntensing.launcher.rules;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.ntensing.launcher.R;
import com.ntensing.launcher.database.app.AppEntity;
import com.ntensing.launcher.database.app.AppWithGeofencesEntity;
import com.ntensing.launcher.database.geofence.GeofenceEntity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RulesService {
    private static final String TAG = "RulesService";
    private static RulesService instance;
    private SharedPreferences prefs;
    private Context context;
    public static List<String> alwaysAllow = SetupAlwaysAllow();

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

    public boolean shouldEnableAppPref(String appId) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String startTimeString = prefs.getString(context.getString(R.string.appSettingsEditTimesStartTimePref), null);
        String endTimeString = prefs.getString(context.getString(R.string.appSettingsEditTimesEndTimePref), null);

        LocalTime startTime;
        LocalTime endTime;
        try {
            startTime = LocalTime.parse(startTimeString);
            endTime = LocalTime.parse(endTimeString);
        } catch(Exception e) {
            return true;
        }

        boolean appSettingsEditTimesPrefIsOn = prefs.getBoolean(context.getString(R.string.appSettingsEditTimesPref) + context.getString(R.string.enabledPrefSuffix), false);
        boolean appSettingsEditTimesPrefIsOnForApp = prefs.getBoolean(context.getString(R.string.appSettingsEditTimesPref) + appId + context.getString(R.string.enabledPrefSuffix), false);
        boolean inTimeSlot = LocalTime.now().isAfter(startTime) && LocalTime.now().isBefore(endTime);

        return !(appSettingsEditTimesPrefIsOn && appSettingsEditTimesPrefIsOnForApp && !inTimeSlot);
    }

    private static List<String> SetupAlwaysAllow() {
        List<String> allowed = new ArrayList<>();
        allowed.add("com.ntensing.launcher");
        return allowed;
    }
}

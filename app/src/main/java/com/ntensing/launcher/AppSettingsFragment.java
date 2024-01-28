package com.ntensing.launcher;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

public class AppSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.root_preferences);

        PreferenceScreen screen = this.getPreferenceScreen();

        SwitchPreference enabledPreference = new SwitchPreference(screen.getContext());
        enabledPreference.setTitle("Enabled");
        enabledPreference.setSummary("If ON, app only can be opened if rules are met.");
        enabledPreference.setKey(getAppId() + "_enabled");
        screen.addPreference(enabledPreference);

        SwitchPreference locationRuleEnabledPreference = new SwitchPreference(screen.getContext());
        locationRuleEnabledPreference.setTitle("Enable Location Rules");
        locationRuleEnabledPreference.setSummary("If ON, app only can be opened if location rules are met.");
        locationRuleEnabledPreference.setKey(getAppId() + "_locationRule_enabled");
        screen.addPreference(locationRuleEnabledPreference);
        locationRuleEnabledPreference.setDependency(getAppId() + "_enabled");

        enabledPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            if (!(Boolean) newValue) {
                locationRuleEnabledPreference.setChecked(false);
            }
            return true;
        });

        Preference geofencePreference = new Preference(screen.getContext());
        geofencePreference.setTitle("Edit Locations");
        geofencePreference.setKey(getAppId() + "_geofence");
        geofencePreference.setOnPreferenceClickListener((preference) -> {
            Bundle bundle = new Bundle();
            bundle.putString("appId", getAppId());
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_appSettingsFragment_to_locationRuleFragment, bundle);
            return true;
        });
        screen.addPreference(geofencePreference);
        geofencePreference.setDependency(getAppId() + "_locationRule_enabled");
    }

    private String getAppId() {
        String appId = "";

        Bundle receivedBundle = getArguments();
        if (receivedBundle != null) {
            appId = receivedBundle.getString("appId");
        }

        return appId;
    }
}
package com.ntensing.launcher;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

public class AppSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.root_preferences);

        PreferenceScreen screen = this.getPreferenceScreen();

        SwitchPreference switchPreference = new SwitchPreference(screen.getContext());
        switchPreference.setTitle("Enabled");
        switchPreference.setSummary("If ON, app can be opened if rules are met.");
        switchPreference.setKey(getAppId() + "_enabled");

        screen.addPreference(switchPreference);
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
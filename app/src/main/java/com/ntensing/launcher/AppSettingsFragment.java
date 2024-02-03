package com.ntensing.launcher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

public class AppSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        MainActivity activity = ((MainActivity)getActivity());
        activity.getSupportActionBar().setTitle(getAppName());

        createMenu();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.root_preferences);

        PreferenceScreen screen = this.getPreferenceScreen();

        SwitchPreference enabledPreference = new SwitchPreference(screen.getContext());
        enabledPreference.setTitle("Enabled");
        enabledPreference.setSummary("If ON, app only can be opened if rules are met.");
        enabledPreference.setKey(getAppId() + getString(R.string.enabledPrefSuffix));
        screen.addPreference(enabledPreference);

        SwitchPreference locationRuleEnabledPreference = new SwitchPreference(screen.getContext());
        locationRuleEnabledPreference.setTitle("Enable Location Rules");
        locationRuleEnabledPreference.setSummary("If ON, app only can be opened if location rules are met.");
        locationRuleEnabledPreference.setKey(getAppId() + getString(R.string.locationRuleEnabledPrefSuffix));
        screen.addPreference(locationRuleEnabledPreference);
        locationRuleEnabledPreference.setDependency(getAppId() + getString(R.string.enabledPrefSuffix));

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

    private String getAppName() {
        String appName = "";

        Bundle receivedBundle = getArguments();
        if (receivedBundle != null) {
            appName = receivedBundle.getString("appName");
        }

        return appName;
    }

    private void createMenu() {
        MainActivity activity = (MainActivity)getActivity();
        activity.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.action_settings) {
                    Navigation.findNavController(activity, R.id.nav_host_fragment_content_main)
                            .navigate(R.id.action_AppsFragment_to_settingsFragment);
                    return true;
                }

                return false;
            }
        });
    }
}
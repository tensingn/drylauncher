package com.ntensing.launcher;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.ntensing.launcher.database.app.AppEntity;

import java.util.List;

public class SettingsFragment extends PreferenceFragmentCompat {
    List<AppEntity> apps;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.root_preferences);

        PreferenceScreen screen = this.getPreferenceScreen();

        LauncherViewModel model = new ViewModelProvider(requireActivity()).get(LauncherViewModel.class);
        model.getLauncherApps().observe(getActivity(), savedApps -> {
            apps = savedApps;
            for (AppEntity app : apps) {
                Preference pref = new Preference(screen.getContext());
                pref.setTitle(app.toString());
                pref.setKey(app.getAppId());
                pref.setOnPreferenceClickListener(preference -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("appId", app.getAppId());
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
                            .navigate(R.id.action_settingsFragment_to_appSettingsFragment, bundle);
                    return true;
                });
                screen.addPreference(pref);
            }
        });
    }
}
package com.ntensing.launcher;

import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import java.util.List;

public class SettingsFragment extends PreferenceFragmentCompat {
    List<LauncherApp> apps;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getApps();

        //setPreferencesFromResource(R.xml.root_preferences, rootKey);
        addPreferencesFromResource(R.xml.root_preferences);

        PreferenceScreen screen = this.getPreferenceScreen();

        for (LauncherApp app : apps) {
            Preference pref = new Preference(screen.getContext());
            pref.setTitle(app.toString());
            pref.setKey(app.toString());
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Bundle bundle = new Bundle();
                    bundle.putString("appName", app.toString());
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
                            .navigate(R.id.action_settingsFragment_to_appSettingsFragment, bundle);
                    return true;
                }
            });
            screen.addPreference(pref);
        }
    }


    private void getApps() {
        LauncherViewModel model = new ViewModelProvider(requireActivity()).get(LauncherViewModel.class);
        apps = model.getLauncherApps();
    }
}
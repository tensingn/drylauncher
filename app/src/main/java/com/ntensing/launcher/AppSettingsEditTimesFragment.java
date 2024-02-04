package com.ntensing.launcher;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.preference.CheckBoxPreference;
import androidx.preference.DialogPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.ntensing.launcher.database.app.AppEntity;
import com.ntensing.launcher.rules.RulesService;

import java.util.HashMap;

public class AppSettingsEditTimesFragment extends PreferenceFragmentCompat {
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        createMenu();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        addPreferencesFromResource(R.xml.root_preferences);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        PreferenceScreen screen = this.getPreferenceScreen();

        SwitchPreference timeEditsAllowedPrefEnabled = new SwitchPreference(screen.getContext());
        timeEditsAllowedPrefEnabled.setTitle("Enabled");
        timeEditsAllowedPrefEnabled.setSummary("If ON, app settings only can be edited during the below time.");
        timeEditsAllowedPrefEnabled.setKey(getString(R.string.appSettingsEditTimesPref) + getString(R.string.enabledPrefSuffix));
        screen.addPreference(timeEditsAllowedPrefEnabled);

        ValidatedTimePreference startTimePref = new ValidatedTimePreference(screen.getContext(),
                getChildFragmentManager(),
                ValidatedTimePreference.ValidatedTimePreferenceComparator.BEFORE,
                getString(R.string.appSettingsEditTimesEndTimePref));
        startTimePref.setKey(getString(R.string.appSettingsEditTimesStartTimePref));
        startTimePref.setTitle("Start Time");
        startTimePref.setSummary(prefs.getString(getString(R.string.appSettingsEditTimesStartTimePref), null));
        screen.addPreference(startTimePref);
        startTimePref.setDependency(getString(R.string.appSettingsEditTimesPref) + getString(R.string.enabledPrefSuffix));

        ValidatedTimePreference endTimePref = new ValidatedTimePreference(screen.getContext(),
                getChildFragmentManager(),
                ValidatedTimePreference.ValidatedTimePreferenceComparator.AFTER,
                getString(R.string.appSettingsEditTimesStartTimePref));
        endTimePref.setKey(getString(R.string.appSettingsEditTimesEndTimePref));
        endTimePref.setTitle("End Time");
        endTimePref.setSummary(prefs.getString(getString(R.string.appSettingsEditTimesEndTimePref), null));
        screen.addPreference(endTimePref);
        endTimePref.setDependency(getString(R.string.appSettingsEditTimesPref) + getString(R.string.enabledPrefSuffix));

        LauncherViewModel model = new ViewModelProvider(requireActivity()).get(LauncherViewModel.class);
        model.getLauncherApps().observe(getActivity(), savedApps -> {
            for (AppEntity app : savedApps) {
                CheckBoxPreference pref = new CheckBoxPreference(screen.getContext());
                pref.setTitle(app.toString());
                pref.setKey(getString(R.string.appSettingsEditTimesPref) + app.getAppId() + getString(R.string.enabledPrefSuffix));
                screen.addPreference(pref);
                pref.setDependency(getString(R.string.appSettingsEditTimesPref) + getString(R.string.enabledPrefSuffix));
            }
        });
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

                if (id == R.id.action_advanced_settings) {
                    Navigation.findNavController(activity, R.id.nav_host_fragment_content_main)
                            .navigate(R.id.action_AppsFragment_to_settingsFragment);
                    return true;
                }

                return false;
            }
        });
    }
}

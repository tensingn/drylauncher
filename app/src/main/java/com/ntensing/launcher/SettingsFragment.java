package com.ntensing.launcher;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.ntensing.launcher.database.app.AppEntity;
import com.ntensing.launcher.rules.RulesService;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsFragment extends PreferenceFragmentCompat {
    HashMap<String, Preference> preferences;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        createMenu();

        if (preferences != null) {
            for (Map.Entry<String, Preference> entry : preferences.entrySet()) {
                entry.getValue().setEnabled(RulesService.getInstance(getContext()).shouldEnableAppPref(entry.getKey()));
            }
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity activity = (MainActivity)getActivity();
        if (activity.findViewById(R.id.fab) != null) {
            ((MainActivity)getActivity()).displayPhoneFab(false);
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.root_preferences);

        PreferenceScreen screen = this.getPreferenceScreen();

        LauncherViewModel model = new ViewModelProvider(requireActivity()).get(LauncherViewModel.class);
        model.getLauncherApps().observe(getActivity(), savedApps -> {
            preferences = new HashMap<>();
            for (AppEntity app : savedApps) {
                Preference pref = new Preference(screen.getContext());
                pref.setTitle(app.toString());
                pref.setKey(app.getAppId());
                pref.setOnPreferenceClickListener(preference -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("appId", app.getAppId());
                    bundle.putString("appName", app.getAppName());
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
                            .navigate(R.id.action_settingsFragment_to_appSettingsFragment, bundle);
                    return true;
                });
                pref.setEnabled(RulesService.getInstance(getContext()).shouldEnableAppPref(app.getAppId()));
                screen.addPreference(pref);
                preferences.put(app.getAppId(), pref);
            }
        });
    }

    private void createMenu() {
        MainActivity activity = (MainActivity)getActivity();
        activity.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                menuInflater.inflate(R.menu.menu_advanced_settings, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.action_advanced_settings) {
                    Navigation.findNavController(activity, R.id.nav_host_fragment_content_main)
                            .navigate(R.id.action_settingsFragment_to_advancedSettingsFragment);
                    return true;
                }

                return false;
            }
        });
    }
}
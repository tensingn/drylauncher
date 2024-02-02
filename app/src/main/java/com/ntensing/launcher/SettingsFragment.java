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
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        createMenu();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

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
                    bundle.putString("appName", app.getAppName());
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
                            .navigate(R.id.action_settingsFragment_to_appSettingsFragment, bundle);
                    return true;
                });
                screen.addPreference(pref);
            }
        });
    }

    private void createMenu() {
        MainActivity activity = (MainActivity)getActivity();
        activity.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                //menuInflater.inflate(R.menu.menu_main, menu);
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
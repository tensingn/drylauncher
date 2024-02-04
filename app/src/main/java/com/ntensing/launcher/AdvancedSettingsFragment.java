package com.ntensing.launcher;

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
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

public class AdvancedSettingsFragment extends PreferenceFragmentCompat {
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

        PreferenceScreen screen = this.getPreferenceScreen();

        Preference timeEditsAllowedPref = new Preference(screen.getContext());
        timeEditsAllowedPref.setTitle("App Settings Edit Times");
        timeEditsAllowedPref.setKey(getString(R.string.appSettingsEditTimesPref));
        timeEditsAllowedPref.setSummary("Restrict app settings edits to only be allowed during a certain time of day.");
        screen.addPreference(timeEditsAllowedPref);
        timeEditsAllowedPref.setOnPreferenceClickListener(preference -> {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_advancedSettingsFragment_to_appSettingsEditTimesFragment);
            return true;
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

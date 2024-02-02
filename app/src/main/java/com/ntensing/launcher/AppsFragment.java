package com.ntensing.launcher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.ntensing.launcher.database.app.AppEntity;
import com.ntensing.launcher.database.app.AppWithGeofencesEntity;
import com.ntensing.launcher.databinding.FragmentAppsBinding;
import com.ntensing.launcher.rules.RulesService;

import java.util.ArrayList;
import java.util.List;

public class AppsFragment extends Fragment {
    private static final String TAG = "AppsFragment";

    private FragmentAppsBinding binding;

    private List<AppEntity> shownApps;

    private RulesService rulesService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        createMenu();

        binding = FragmentAppsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeDisplayContent();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initializeDisplayContent() {
        rulesService = RulesService.getInstance(this.getContext());
        final ListView listViewApps = getView().findViewById(R.id.listview_apps);

        final ArrayAdapter<AppEntity> launcherAppArrayAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1);
        listViewApps.setAdapter(launcherAppArrayAdapter);

        listViewApps.setOnItemClickListener((adapterView, view, position, l) -> {
            AppEntity app = shownApps.get(position);
            Intent intent = getContext().getPackageManager().getLaunchIntentForPackage(app.getActivityName());
            startActivity(intent);
        });

        LauncherViewModel model = new ViewModelProvider(requireActivity()).get(LauncherViewModel.class);

        model.getAppsWithGeofences().observe(getActivity(), appsWithGeofences -> {
            launcherAppArrayAdapter.clear();

            shownApps = new ArrayList<>();
            for (AppWithGeofencesEntity awg : appsWithGeofences) {
                if (rulesService.shouldAllowApp(awg)) {
                    shownApps.add(awg.app);
                }
            }

            launcherAppArrayAdapter.addAll(shownApps);
        });
    }

    private void createMenu() {
        MainActivity activity = (MainActivity)getActivity();
        activity.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu);
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
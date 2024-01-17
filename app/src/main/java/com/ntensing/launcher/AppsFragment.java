package com.ntensing.launcher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.ntensing.launcher.database.AppEntity;
import com.ntensing.launcher.databinding.FragmentAppsBinding;

import java.util.ArrayList;
import java.util.List;

public class AppsFragment extends Fragment {

    private FragmentAppsBinding binding;

    private List<AppEntity> launcherApps;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
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
        final ListView listViewApps = getView().findViewById(R.id.listview_apps);

        final ArrayAdapter<AppEntity> launcherAppArrayAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1);
        listViewApps.setAdapter(launcherAppArrayAdapter);

        listViewApps.setOnItemClickListener((adapterView, view, position, l) -> {
            AppEntity app = launcherApps.get(position);
            Intent intent = getContext().getPackageManager().getLaunchIntentForPackage(app.getActivityName());
            startActivity(intent);
        });

        LauncherViewModel model = new ViewModelProvider(requireActivity()).get(LauncherViewModel.class);

        model.getLauncherApps().observe(getActivity(), savedApps -> {
            launcherApps = savedApps;

            List<AppEntity> shownApps = new ArrayList<>();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            for (AppEntity app : launcherApps) {
                String prefKey = app.getAppId() + "_enabled";
                if (prefs.getBoolean(prefKey, false)) {
                    shownApps.add(app);
                }
            }

            launcherAppArrayAdapter.addAll(shownApps);
        });
    }
}
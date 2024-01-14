package com.ntensing.launcher;

import android.content.Intent;
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

import com.ntensing.launcher.databinding.FragmentAppsBinding;

import java.util.List;

public class AppsFragment extends Fragment {

    private FragmentAppsBinding binding;

    private List<LauncherApp> launcherApps;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        LauncherViewModel model = new ViewModelProvider(requireActivity()).get(LauncherViewModel.class);

        launcherApps = model.getLauncherApps();

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

        ArrayAdapter<LauncherApp> launcherAppArrayAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1, launcherApps);
        listViewApps.setAdapter(launcherAppArrayAdapter);

        listViewApps.setOnItemClickListener((adapterView, view, position, l) -> {
            LauncherApp app = launcherApps.get(position);
            Intent intent = getContext().getPackageManager().getLaunchIntentForPackage(app.getActivityName());
            startActivity(intent);
        });
    }
}
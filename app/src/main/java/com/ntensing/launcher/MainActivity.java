package com.ntensing.launcher;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ntensing.launcher.database.AppEntity;
import com.ntensing.launcher.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static String PHONE = "Phone";
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private AppEntity phoneApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        final PackageManager pm = getApplication().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);

        LauncherViewModel model = new ViewModelProvider(MainActivity.this).get(LauncherViewModel.class);
        model.getLauncherApps().observe(this, savedApps -> {
                List<AppEntity> appsToSave = new ArrayList<>();

                for (int i = 0; i < apps.size(); i++) {
                    ResolveInfo app = apps.get(i);
                    String name = app.loadLabel(pm).toString();
                    String id = app.activityInfo.packageName + "/" + name;

                    if (savedApps.stream().noneMatch(ae -> ae.getAppId().equals(id))) {
                        appsToSave.add(new AppEntity(id, name, app.activityInfo.packageName));
                    }
                }

                for (AppEntity app : appsToSave) {
                    if (app.toString().equalsIgnoreCase(PHONE)) {
                        phoneApp = app;
                    }
                }

                model.insertAll(appsToSave);
            });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getPackageManager().getLaunchIntentForPackage(phoneApp.getActivityName());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_AppsFragment_to_settingsFragment);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
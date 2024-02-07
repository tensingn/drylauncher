package com.ntensing.launcher;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ntensing.launcher.database.app.AppEntity;
import com.ntensing.launcher.databinding.ActivityMainBinding;
import com.ntensing.launcher.services.AppMonitoringService;

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

        NotificationChannel channel = new NotificationChannel("Launcher_Channel", "Launcher Notifications", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);

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

                for (AppEntity app : savedApps) {
                    if (app.toString().equalsIgnoreCase(PHONE)) {
                        phoneApp = app;
                    }
                }

                model.insertApps(appsToSave);
            });

        binding.fab.setOnClickListener(view -> {
            Intent phoneIntent = getPackageManager().getLaunchIntentForPackage(phoneApp.getActivityName());
            startActivity(phoneIntent);
        });

        if (!AppMonitoringService.running) {
            startService(new Intent(this, AppMonitoringService.class));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
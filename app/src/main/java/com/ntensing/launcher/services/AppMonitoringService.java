package com.ntensing.launcher.services;

import android.app.Notification;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;

import com.ntensing.launcher.MainActivity;
import com.ntensing.launcher.R;
import com.ntensing.launcher.database.app.AppRepository;
import com.ntensing.launcher.database.app.AppWithGeofencesEntity;
import com.ntensing.launcher.rules.RulesService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

public class AppMonitoringService extends LifecycleService {
    private static final String TAG = "AppMonitoringService";
    private Handler handler;
    private Runnable runnable;
    private List<AppWithGeofencesEntity> appsOnDevice;
    private UsageStatsManager usageStatsManager;
    private final int DELAY = 10;
    public static boolean running = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        start();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listenForUsageEvents(false);
        running = false;
    }

    private void start() {
        running = true;
        Notification n = new NotificationCompat.Builder(this, "Launcher_Channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Launcher")
                .setContentText("Monitoring Apps...")
                .build();
        startForeground(1, n);

        usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

        appsOnDevice = new ArrayList<>();
        listenForAppRuleChanges();

        listenForUsageEvents(true);
    }

    private void listenForUsageEvents(boolean start) {
        if (start) {
            handler = new Handler();
            runnable = () -> {
                removeDisallowedAppFromForeground();
                handler.postDelayed(runnable, DELAY * 1000);
            };

            handler.post(runnable);
        } else {
            if (handler != null) {
                handler.removeCallbacks(runnable);
                handler = null;
            }

            runnable = null;
        }
    }

    private void listenForAppRuleChanges() {
        AppRepository repository = AppRepository.getInstance(this);

        repository.getAppsWithGeofences().observe(this, appsWithGeofences -> appsOnDevice = appsWithGeofences);
    }

    private void removeDisallowedAppFromForeground() {
        AppWithGeofencesEntity openApp = getOpenAppFromUsageStats();

        if (openApp != null
                && !RulesService.alwaysAllow.contains(openApp.app.getActivityName())
                && !RulesService.getInstance(this).shouldAllowApp(openApp)) {
            Log.d(TAG, "shouldn't show app " + openApp.app.getAppName() + "! opening launcher...");

            // this shouldn't work for a normal app but I think works for me because I am
            // installing this app on my own device
            Intent launcherIntent = new Intent(this, MainActivity.class);
            launcherIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(launcherIntent);
        }
    }


    private AppWithGeofencesEntity getOpenAppFromUsageStats() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -5);

        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST, calendar.getTimeInMillis(), System.currentTimeMillis() + 1000);

        AppWithGeofencesEntity openApp = null;
        if (usageStatsList != null && usageStatsList.size() > 0) {
            SortedMap<Long, AppWithGeofencesEntity> recentlyOpenApps = new TreeMap<>();

            for (UsageStats usageStats : usageStatsList) {
                String packageName = usageStats.getPackageName();

                Optional<AppWithGeofencesEntity> app = appsOnDevice.stream().filter(awg -> awg.app.getActivityName().equals(packageName)).findFirst();

                if (app.isPresent()) {
                    recentlyOpenApps.put(usageStats.getLastTimeUsed(), app.get());
                }
            }

            if (recentlyOpenApps != null && !recentlyOpenApps.isEmpty()) {
                openApp = recentlyOpenApps.get(recentlyOpenApps.lastKey());
            }
        }

        return openApp;
    }
}

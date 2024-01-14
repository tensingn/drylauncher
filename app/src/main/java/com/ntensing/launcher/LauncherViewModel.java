package com.ntensing.launcher;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.ArrayList;
import java.util.List;

public class LauncherViewModel extends AndroidViewModel {
    private final List<LauncherApp> launcherApps;

    public LauncherViewModel(@NonNull Application application) {
        super(application);
        launcherApps = initializeApps();
    }

    public List<LauncherApp> getLauncherApps() {
        return launcherApps;
    }

    private List<LauncherApp> initializeApps() {
        List<LauncherApp> launcherApps = new ArrayList<>();

        final PackageManager pm = getApplication().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps1 = pm.queryIntentActivities(intent, 0);

        for (int i = 0; i < apps1.size(); i++) {
            ResolveInfo app = apps1.get(i);
            launcherApps.add(new LauncherApp(app, app.loadLabel(pm).toString(), true));
        }

        return  launcherApps;
    }


}


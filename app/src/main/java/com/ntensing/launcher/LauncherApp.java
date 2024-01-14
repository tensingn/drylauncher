package com.ntensing.launcher;

import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;

public class LauncherApp {
    private ResolveInfo applicationInfo;
    private String name;
    private boolean enabled;

    public LauncherApp(ResolveInfo applicationInfo, String name, boolean enabled) {
        this.applicationInfo = applicationInfo;
        this.name = name;
        this.enabled = enabled;
    }

    @Override
    public String toString() { return name; }

    public String getActivityName() {
        return applicationInfo.activityInfo.packageName;
    }
}

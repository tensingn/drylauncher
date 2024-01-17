package com.ntensing.launcher;

import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;

public class LauncherApp1 {
    private String appId;
    private ResolveInfo applicationInfo;
    private String appName;

    public LauncherApp1(String appID, ResolveInfo applicationInfo, String appName) {
        this.appId = appID;
        this.applicationInfo = applicationInfo;
        this.appName = appName;
    }

    public String getAppId() { return appId; }

    @Override
    public String toString() { return appName; }

    public String getActivityName() {
        return applicationInfo.activityInfo.packageName;
    }
}

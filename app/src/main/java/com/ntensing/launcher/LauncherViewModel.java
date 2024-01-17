package com.ntensing.launcher;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ntensing.launcher.database.AppEntity;
import com.ntensing.launcher.database.AppRepository;

import java.util.ArrayList;
import java.util.List;

public class LauncherViewModel extends AndroidViewModel {
    private AppRepository appRepository;

    public LauncherViewModel(@NonNull Application application) {
        super(application);
        appRepository = new AppRepository(application);
    }

    public LiveData<List<AppEntity>> getLauncherApps() {
        return appRepository.getAllApps();
    }

    public void insertAll(List<AppEntity> apps) { appRepository.insertAll(apps); }

    public void insert(AppEntity app) { appRepository.insert(app); }
}


package com.ntensing.launcher;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ntensing.launcher.database.app.AppEntity;
import com.ntensing.launcher.database.app.AppRepository;
import com.ntensing.launcher.database.app.AppWithGeofencesEntity;
import com.ntensing.launcher.database.geofence.GeofenceEntity;
import com.ntensing.launcher.database.geofence.GeofenceRepository;

import java.util.List;
import java.util.Map;

public class LauncherViewModel extends AndroidViewModel {
    private AppRepository appRepository;

    private GeofenceRepository geofenceRepository;

    public LauncherViewModel(@NonNull Application application) {
        super(application);
        appRepository = AppRepository.getInstance(application);
        geofenceRepository = GeofenceRepository.getInstance(application);
    }

    // APPS
    public LiveData<List<AppEntity>> getLauncherApps() {
        return appRepository.getAllApps();
    }

    public void insertApps(List<AppEntity> apps) { appRepository.insertAll(apps); }

    public void insertApp(AppEntity app) { appRepository.insert(app); }


    // GEOFENCES
    public LiveData<List<GeofenceEntity>> getGeofencesByAppId(String appId) { return geofenceRepository.getGeofencesByAppId(appId); }

    public void insertGeofences(List<GeofenceEntity> geofences) { geofenceRepository.insertAll(geofences); }

    public void insertGeofence(GeofenceEntity geofence) { geofenceRepository.insert(geofence); }

    public void deleteGeofenceById(String geofenceId) { geofenceRepository.deleteById(geofenceId); }

    // APP/GEOFENCE
    public LiveData<List<AppWithGeofencesEntity>> getAppsWithGeofences() {
        return appRepository.getAppsWithGeofences();
    }
}

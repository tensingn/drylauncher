package com.ntensing.launcher;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.ntensing.launcher.database.geofence.GeofenceEntity;
import com.ntensing.launcher.database.geofence.GeofenceRepository;
import com.ntensing.launcher.geofence.GeofenceService;

import java.util.List;
import java.util.UUID;

public class LocationRuleFragment
        extends Fragment
        implements GoogleMap.OnMapLongClickListener,
            RemoveGeofenceDialogFragment.RemoveGeofenceDialogListener,
            AddGeofenceDialogFragment.AddGeofenceDialogListener {
    private static final String TAG = "LocationRuleFragment";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastLocation;
    private GeofencingClient geofencingClient;
    private GoogleMap map;
    private GeofenceService geofenceService;
    private LauncherViewModel model;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;

            if (lastLocation != null) {
                LatLng lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(lastLatLng).title("Current Location"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(lastLatLng));
                googleMap.setMaxZoomPreference(20.0f);
                googleMap.setMinZoomPreference(10.0f);
            }

            displaySavedGeofences();

            googleMap.setOnMapLongClickListener(LocationRuleFragment.this);
            googleMap.setOnMarkerClickListener(marker -> {
                GeofenceEntity geofenceEntity = (GeofenceEntity) marker.getTag();
                new RemoveGeofenceDialogFragment(geofenceEntity.getGeofenceId())
                        .show(getChildFragmentManager(), "REMOVE_GEOFENCE");
                return false;
            });
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        createMenu();

        return inflater.inflate(R.layout.fragment_location_rule, container, false);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(LauncherViewModel.class);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    android.Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            Boolean backgroundLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,false);

                            if (!fineLocationGranted || !coarseLocationGranted || !backgroundLocationGranted) {
                                // don't have enough location permissions
                                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
                                        .popBackStack(R.id.appSettingsFragment, false);
                                Toast.makeText(requireActivity(), "Not enough location permissions", Toast.LENGTH_SHORT).show();
                            }
                        }
                );

        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        });

        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, new CancellationTokenSource().getToken())
                .addOnSuccessListener(getActivity(), location -> {
                    if (location != null) {
                        lastLocation = location;
                        SupportMapFragment mapFragment = isAdded() ?
                                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map) : null;
                        if (mapFragment != null) {
                            mapFragment.getMapAsync(callback);
                        }
                    }
                }).addOnFailureListener(getActivity(), ex -> {
                    Log.e(TAG, ex.getMessage());
                });

        geofencingClient = LocationServices.getGeofencingClient(getContext());
        geofenceService = new GeofenceService(getContext());
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        new AddGeofenceDialogFragment(latLng)
                .show(getChildFragmentManager(), "ADD_GEOFENCE");
    }

    @SuppressLint("MissingPermission")
    private void addGeofence(LatLng latLng, float radius) {
        String geofenceId = UUID.randomUUID().toString();
        Geofence geofence = geofenceService.createGeofence(geofenceId, latLng, radius,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL);
        GeofencingRequest geofencingRequest = geofenceService.createGeofencingRequest(geofence);

        geofencingClient.addGeofences(geofencingRequest, geofenceService.getPendingIntent())
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "onSuccess: geofence added with id " + geofenceId);

                    // persist geofence
                    model.insertGeofence(new GeofenceEntity(geofenceId, getAppId(),
                            geofence.getLatitude(), geofence.getLongitude(), geofence.getRadius(), false));
                })
                .addOnFailureListener(e -> {
                    String msg = geofenceService.getErrorString(e);
                    Log.d(TAG, "onFailure: " + msg);
                });
    }

    private String getAppId() {
        String appId = "";

        Bundle receivedBundle = getArguments();
        if (receivedBundle != null) {
            appId = receivedBundle.getString("appId");
        }

        return appId;
    }

    private void displaySavedGeofences() {
        model.getGeofencesByAppId(getAppId()).observe(getActivity(), savedGeofences -> {
            // need to clear map so we can get rid of deleted geofences
            map.clear();
            LatLng lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            map.addMarker(new MarkerOptions().position(lastLatLng).title("Current Location"));

            for (GeofenceEntity geofenceEntity : savedGeofences) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(geofenceEntity.getLatLng());
                Marker marker = map.addMarker(markerOptions);
                marker.setTag(geofenceEntity);

                CircleOptions circleOptions = new CircleOptions()
                        .center(geofenceEntity.getLatLng())
                        .radius(geofenceEntity.getRadius())
                        .strokeColor(Color.argb(255, 0, 0, 255))
                        .strokeWidth(4)
                        .fillColor(Color.argb(50, 0, 0, 255));
                map.addCircle(circleOptions);
            }
        });
    }

    @Override
    public void onRemoveGeofenceDialogYesClick(String geofenceId) {
        Log.d(TAG, "On RemoveGeofenceDialogFragment YES click: Deleting geofence " + geofenceId + "...");
        model.deleteGeofenceById(geofenceId);
    }

    @Override
    public void onRemoveGeofenceDialogNoClick(String geofenceId) {
        Log.d(TAG, "On RemoveGeofenceDialogFragment NO click: Not deleting geofence " + geofenceId + "...");
    }


    @Override
    public void onAddGeofenceDialogYesClick(LatLng latLng, float radius) {
        Log.d(TAG, "On AddGeofenceDialogFragment YES click: Adding geofence...");
        addGeofence(latLng, radius);
    }

    @Override
    public void onAddGeofenceDialogNoClick() {
        Log.d(TAG, "On AddGeofenceDialogFragment NO click: Not adding geofence...");
    }

    private void createMenu() {
        MainActivity activity = (MainActivity)getActivity();
        activity.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                //menuInflater.inflate(R.menu.menu_main, menu);
                menu.clear();
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.action_settings) {
                    Navigation.findNavController(activity, R.id.nav_host_fragment_content_main)
                            .navigate(R.id.action_AppsFragment_to_settingsFragment);
                    return true;
                }

                return false;
            }
        });
    }
}
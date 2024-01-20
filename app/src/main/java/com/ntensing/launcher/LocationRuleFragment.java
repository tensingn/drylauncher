package com.ntensing.launcher;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ntensing.launcher.geofence.GeofenceBroadcastReceiver;
import com.ntensing.launcher.geofence.GeofenceService;

import java.util.UUID;

public class LocationRuleFragment extends Fragment implements GoogleMap.OnMapLongClickListener {
    private static final String TAG = "LocationRuleFragment";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastLocation;
    private GeofencingClient geofencingClient;
    private GoogleMap map;
    private GeofenceService geofenceService;

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

            googleMap.setOnMapLongClickListener(LocationRuleFragment.this);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location_rule, container, false);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
                    if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                            } else {
                                // No location access granted.
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
                        SupportMapFragment mapFragment =
                                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
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
        map.clear();

        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        map.addMarker(markerOptions);

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(250);
        circleOptions.strokeColor(Color.argb(255, 0, 0, 255));
        circleOptions.strokeWidth(4);
        circleOptions.fillColor(Color.argb(50, 0, 0, 255));
        map.addCircle(circleOptions);

        addGeofence(latLng, 250);
    }

    @SuppressLint("MissingPermission")
    private void addGeofence(LatLng latLng, float radius) {
        Geofence geofence = geofenceService.getGeofence(UUID.randomUUID().toString(), latLng, radius,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceService.getGeofencingRequest(geofence);
        geofencingClient.addGeofences(geofencingRequest, geofenceService.getPendingIntent())
                .addOnSuccessListener(unused -> Log.d(TAG, "onSuccess: geofence added"))
                .addOnFailureListener(e -> {
                    String msg = geofenceService.getErrorString(e);
                    Log.d(TAG, "onFailure: " + msg);
                });
    }
}
package com.ntensing.launcher;

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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.common.api.Status;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.ntensing.launcher.database.geofence.GeofenceEntity;
import com.ntensing.launcher.geofence.GeofenceService;

import java.util.Arrays;
import java.util.HashMap;
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
    private Marker lastMarker;
    private HashMap<String, MarkerCircle> geofenceMarkerCircles = new HashMap<>();

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;

            if (lastLocation != null) {
                LatLng lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                moveMap(lastLatLng);
            }

            displaySavedGeofences();

            googleMap.setOnMapLongClickListener(LocationRuleFragment.this);
            googleMap.setOnMarkerClickListener(marker -> {
                String geofenceId = (String) marker.getTag();
                if (geofenceId != null) {
                    new RemoveGeofenceDialogFragment(geofenceId)
                            .show(getChildFragmentManager(), "REMOVE_GEOFENCE");
                }

                return false;
            });

            createAutocomplete();
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

    private void removeGeofence(String geofenceId) {
        model.deleteGeofenceById(geofenceId);
        if (geofenceMarkerCircles.containsKey(geofenceId)) {
            MarkerCircle mc = geofenceMarkerCircles.get(geofenceId);
            mc.marker.remove();
            mc.circle.remove();
        }
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
            for (GeofenceEntity geofenceEntity : savedGeofences) {
                if (!geofenceMarkerCircles.containsKey(geofenceEntity.getGeofenceId())) {
                    MarkerCircle mc = createMarkerCircle(geofenceEntity.getLatLng(), geofenceEntity.getRadius(), geofenceEntity.getGeofenceId());
                    geofenceMarkerCircles.put(geofenceEntity.getGeofenceId(), mc);
                }
            }
        });
    }

    @Override
    public void onRemoveGeofenceDialogYesClick(String geofenceId) {
        Log.d(TAG, "On RemoveGeofenceDialogFragment YES click: Deleting geofence " + geofenceId + "...");
        removeGeofence(geofenceId);
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

    private void createAutocomplete() {
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        Fragment f = getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), BuildConfig.MAPS_API_KEY);
        }

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        if (lastLocation != null) {
            autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                    new LatLng(lastLocation.getLatitude() - 1, lastLocation.getLongitude() - 1),
                    new LatLng(lastLocation.getLatitude() + 1, lastLocation.getLongitude() + 1)
            ));
        }

        autocompleteFragment.setCountries("US");

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + "selected...");
                moveMap(place.getLatLng());
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: show toast or popup that error occurred
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    private void moveMap(LatLng latLng) {
        if (map != null && latLng != null) {
            if (lastMarker != null) {
                lastMarker.remove();
            }
            lastMarker = map.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            map.setMaxZoomPreference(20.0f);
            map.setMinZoomPreference(10.0f);
        }
    }

    private class MarkerCircle {
        Marker marker;
        Circle circle;
    }

    private MarkerCircle createMarkerCircle(LatLng latLng, double radius, Object markerTag) {
        MarkerCircle mc = new MarkerCircle();

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng);
        mc.marker = map.addMarker(markerOptions);
        mc.marker.setTag(markerTag);

        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(radius)
                .strokeColor(Color.argb(255, 0, 0, 255))
                .strokeWidth(4)
                .fillColor(Color.argb(50, 0, 0, 255));
        mc.circle = map.addCircle(circleOptions);

        return mc;
    }
}
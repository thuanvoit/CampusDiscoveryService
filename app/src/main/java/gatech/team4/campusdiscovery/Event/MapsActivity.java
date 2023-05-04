package gatech.team4.campusdiscovery.Event;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import gatech.team4.campusdiscovery.Database.DBHelper;
import gatech.team4.campusdiscovery.Map.MapInfoWindowAdapter;
import gatech.team4.campusdiscovery.Models.CampusActivity;
import gatech.team4.campusdiscovery.R;
import gatech.team4.campusdiscovery.Utils.PermissionUtils;
import gatech.team4.campusdiscovery.databinding.ActivityMapsBinding;

public class MapsActivity extends AppCompatActivity
        implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap map;
    private ActivityMapsBinding binding;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;

    private String building;
    private String room;
    private double lat;
    private double lon;

    private DBHelper db;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor prefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("CampusDiscoveryPrefs",
                MODE_PRIVATE);
        prefsEditor = sharedPreferences.edit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Event Map");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = new DBHelper(this);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private ArrayList<gatech.team4.campusdiscovery.Models.Location> getBuildings() {
        ArrayList<gatech.team4.campusdiscovery.Models.Location> buildings = new ArrayList<>();
        Resources res = getResources();
        String[] arr = res.getStringArray(R.array.building_list);
        for (int i = 0; i < arr.length; i++) {
            try {
                JSONObject jsonObject = new JSONObject(arr[i]);
                String buildingName = jsonObject.getString("name");
                double buildingLat = jsonObject.getDouble("lat");
                double buildingLon = jsonObject.getDouble("lon");
                gatech.team4.campusdiscovery.Models.Location thisBuilding =
                        new gatech.team4.campusdiscovery.Models.Location(
                                buildingName, buildingLat, buildingLon);
                buildings.add(thisBuilding);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return buildings;
    }

    private gatech.team4.campusdiscovery.Models.Location getBuildingFromBuildingName(
            String buildingName) {
        for (gatech.team4.campusdiscovery.Models.Location loc : getBuildings()) {
            if (loc.getBuilding().toLowerCase().equalsIgnoreCase(buildingName.toLowerCase())) {
                return loc;
            }
        }
        return null;
    }

    private String[] getBuildingsArray() {
        Resources res = getResources();
        String[] arr = res.getStringArray(R.array.building_list);
        for (int i = 0; i < arr.length; i++) {
            try {
                JSONObject jsonObject = new JSONObject(arr[i]);
                String buildingName = jsonObject.getString("name");
                arr[i] = buildingName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return arr;
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        this.map = gMap;

        MapInfoWindowAdapter infoAdapter = new MapInfoWindowAdapter(MapsActivity.this);
        map.setInfoWindowAdapter(infoAdapter);

        map.clear();
        //Log.i("MAPDEBUG", "" +lat + " " + lon);
        //addMarker(building, room, lat, lon);
        addAllEventMarker();

        // end zoom control
        map.getUiSettings().setZoomControlsEnabled(true);
        // end my location
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);

        enableMyLocation();

        map.setOnInfoWindowClickListener(marker -> {
            getEventFromLocation(marker.getTitle());
        });

    }


    private void getEventFromLocation(String location) {
        prefsEditor.putString("filterOrganizer", "");
        prefsEditor.putString("filterLocation", location);
        prefsEditor.putString("filterDate", "");
        prefsEditor.commit();
        finish();
    }

    public void addAllEventMarker() {
        Intent intent = getIntent();
        ArrayList<CampusActivity> activities =
                (ArrayList<CampusActivity>) intent.getSerializableExtra("activities");
        LinkedHashSet<String> availableBuilding = new LinkedHashSet<>();
        for (CampusActivity activity : activities) {
            if (availableBuilding.add(activity.getLocation().split("__,__")[0])) {
                gatech.team4.campusdiscovery.Models.Location loc = getBuildingFromBuildingName(
                        activity.getLocation().split("__,__")[0]
                );
                addMarker(loc);
            }
        }

    }

    public void addMarker(gatech.team4.campusdiscovery.Models.Location loc) {
        LatLng latlon = new LatLng(loc.getLat(), loc.getLon());
        map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                .position(latlon)
                .title(loc.getBuilding())
                //.snippet("Events: " + 5)
                .draggable(false)).showInfoWindow();
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlon, 16));
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Looking for your location...", Toast.LENGTH_SHORT)
                .show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            return;
        }

        // 2. Otherwise, request location permissions from the user.

        PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION, true);
        PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_COARSE_LOCATION, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION) || PermissionUtils
                .isPermissionGranted(permissions, grantResults,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    // back button on actionbar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
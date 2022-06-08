package com.example.taskmaster.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.taskmaster.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;

@RequiresApi(api = Build.VERSION_CODES.N)
@SuppressLint({"SetTextI18n", "MissingPermission", "WrongThread"})
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();

    private double latitude, longitude;

    private boolean isMapReady;

    private final int PERMISSION_ID = 44;

    private LoadingDialog loadingDialog;

    private GoogleMap googleMap;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.i(TAG, "onCreate: Called");
        loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();
        if (isMapReady) prepareGoogleMaps();

    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume: Called");
        super.onResume();
        if (checkPermissions()) {
            prepareGoogleMaps();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        isMapReady = true;
        this.googleMap = googleMap;
        loadingDialog.dismissLoadingDialog();
    }


    private void prepareGoogleMaps() {

        //method to get the location
        getLastLocation();

        //get a handle to the fragment and register the callback
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().
                findFragmentById(R.id.location_fragment);
        mapFragment.getMapAsync(this);
    }

    private void getLastLocation() {
        //check if the permission is given
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                //get last location from FusedLocationClient object
                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            String intent = getIntent().getStringExtra("Is from AddTaskActivity");

                            try {
                                if (intent.equals(TaskDetailsActivity.class.getSimpleName()) || intent != null) {

                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }catch (NullPointerException nullPointerException){
                                // the index 0 represent the latitude and the index 1 represent the longitude in coordinate list
                                latitude = Double.parseDouble(getIntent().getStringExtra(TaskDetailsActivity.LATITUDE));
                                longitude = Double.parseDouble(getIntent().getStringExtra(TaskDetailsActivity.LONGITUDE));
                            }



                            Log.i(TAG, "onComplete: The latitude and longitude is -> " + location.getLatitude() + "-> " + location.getLongitude());
                            Log.i(TAG, "Google map is -> " + googleMap);
                            googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(latitude, longitude))
                                    .title("Marker"));

                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));

                        }
                    }
                });

            } else {
                Toast.makeText(this, "Please turn on your location....", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permission arent available request for permission
            requestPermission();
        }
    }

    private boolean checkPermissions() {

        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        /*
         * If you want a background location in android 10 and higher use
         * Manifest.permission.ACCESS_BACKGROUND_LOCATION
         */
    }

    private boolean isLocationEnabled() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            LatLng coordinate = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            googleMap.addMarker(new MarkerOptions()
                    .position(coordinate)
                    .title("Marker"));

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 12.0f));
        }
    };
}
package com.volive.whitecab.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.volive.whitecab.R;
import com.volive.whitecab.util.GPSTracker;
import com.volive.whitecab.util.SessionManager;

import org.json.JSONObject;

import java.util.HashMap;

public class SplashActivity extends AppCompatActivity  implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int MY_PERMISSIONS_REQUEST = 1;
    private static final String TAG = SplashActivity.class.getSimpleName();
    GPSTracker gpsTracker;
    private int SPLASH_TIME_OUT = 3000;
    private Location mCurrentLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationCallback mLocationCallback;
    private Boolean mRequestingLocationUpdates;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationRequest mLocationRequest;
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    String strUserId="",strMobile="";
    SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sm=new SessionManager(SplashActivity.this);
        HashMap<String, String> userDetail = sm.getUserDetails();

        if(userDetail.get(SessionManager.KEY_ID) != null){
            strUserId = userDetail.get(SessionManager.KEY_ID);
            Log.e("key_id", strUserId);
            strMobile= userDetail.get(SessionManager.KEY_NUMBER);
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        gpsTracker = new GPSTracker(SplashActivity.this);
        checkPermission();

        createLocationCallback();
        createLocationRequest1();
        setUpLocation();
        buildLocationSettingsRequest();
        mRequestingLocationUpdates = false;
        if (mLocationRequest == null) {
            startLocationUopdate();
        }

        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        }

    }


    private void startLocationUopdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i("SplashActivity", "All location settings are satisfied.");

                        //noinspection MissingPermission
                        if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            // ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            // public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            // int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                // startLocationUpdates();
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(SplashActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                //  startLocationUpdates();
                                Log.e(TAG, errorMessage);
                                Toast.makeText(SplashActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }

                    }
                });
    }
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }
    @SuppressLint("RestrictedApi")
    private void createLocationRequest1() {

        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //request runtime permission
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);

        } else {

            if (checkPlayServices()) {

                builsGoogleApiClient();
                createLocationRequest();
                /*if (location_switch.isChecked()) {*/
                //displayLocation();
                //  }

            }
        }
    }

    @SuppressLint("RestrictedApi")
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        /*mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);*/
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICE_RES_REQUEST).show();
            else {
                Toast.makeText(this, "not support", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void builsGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mLocationRequest == null) {
            mGoogleApiClient.connect();
            startLocationUopdate();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();

            }
        };
    }

    private void checkPermission() {

        int permissionLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionLocation2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int readPermission=ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission=ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int callPermission=ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);


        if (permissionLocation != PackageManager.PERMISSION_GRANTED && permissionLocation2 != PackageManager.PERMISSION_GRANTED&& readPermission != PackageManager.PERMISSION_GRANTED&& writePermission != PackageManager.PERMISSION_GRANTED&& callPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE}, 1);
        } else {
            callPermission();
        }

    }

    private void callPermission() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(strUserId.isEmpty()){
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }else {
                    /*startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    finish();*/
                    Bundle bundle = getIntent().getExtras();
                    if (bundle != null && sm.isLoggedIn()) {


                        String bundledata = bundle.getString("body");
                        String type = bundle.getString("type");

                        if (bundledata != null && type.equalsIgnoreCase("AR")) {

                            System.out.println("bundle data" + bundledata);
                            JSONObject jsonObject = null;


                            Intent intent = new Intent(SplashActivity.this, TrackingActivity.class);
                            intent.putExtra("fromscreen", "1");
                            intent.putExtra("vehicle_name", bundle.getString("vehicle_name"));
                            System.out.println("vehicle_name" + bundle.getString("vehicle_name"));
                            intent.putExtra("vehicle_number", bundle.getString("vehicle_number"));
                            intent.putExtra("driver_name", bundle.getString("driver_name"));
                            intent.putExtra("driver_mobile", bundle.getString("driver_mobile"));
                            intent.putExtra("trip_id", bundle.getString("trip_id"));
                            intent.putExtra("time", "");
                            intent.putExtra("distance", bundle.getString("distance"));
                            intent.putExtra("driver_profile", bundle.getString("driver_profile"));
                            intent.putExtra("driver_lat", bundle.getString("driver_lat"));
                            intent.putExtra("driver_long", bundle.getString("driver_long"));
                            intent.putExtra("driver_id", bundle.getString("driver_id"));
                            intent.putExtra("type","");
                            startActivity(intent);
                            finish();
                        } else if (bundledata != null && type.equalsIgnoreCase("DA") || bundledata != null && type.equalsIgnoreCase("RS")) {
                            String type123="";
                            System.out.println("bundle data" + bundledata);
                            JSONObject jsonObject = null;
                            if(type.equalsIgnoreCase("RS")){
                                type123="4";
                            }


                            Intent intent = new Intent(SplashActivity.this, TrackingActivity.class);
                            intent.putExtra("fromscreen", "1");
                            intent.putExtra("vehicle_name", bundle.getString("vehicle_name"));
                            System.out.println("vehicle_name" + bundle.getString("vehicle_name"));
                            intent.putExtra("vehicle_number", bundle.getString("vehicle_number"));
                            intent.putExtra("driver_name", bundle.getString("driver_name"));
                            intent.putExtra("driver_mobile", bundle.getString("driver_mobile"));
                            intent.putExtra("trip_id", bundle.getString("trip_id"));
                            intent.putExtra("time", "");
                            intent.putExtra("distance", bundle.getString("distance"));
                            intent.putExtra("driver_profile", bundle.getString("driver_profile"));
                            intent.putExtra("driver_lat", bundle.getString("driver_lat"));
                            intent.putExtra("driver_long", bundle.getString("driver_long"));
                            intent.putExtra("driver_id", bundle.getString("driver_id"));
                            intent.putExtra("type",type123);

                            intent.putExtra("color",bundle.getString("vehicle_color"));
                            intent.putExtra("avg_rating",bundle.getString("avg_rating"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else if (bundledata != null && type.equalsIgnoreCase("RC")) {
                            Intent intent = new Intent(SplashActivity.this, RatingActivity.class);
                            intent.putExtra("fromscreen", "1");
                            intent.putExtra("driver_name", bundle.getString("username"));
                            intent.putExtra("trip_id", bundle.getString("trip_id"));
                            intent.putExtra("driver_id", bundle.getString("driver_id"));
                            intent.putExtra("driver_profile", bundle.getString("user_profile"));
                            intent.putExtra("money", bundle.getString("final_fare"));
                            intent.putExtra("ride_fare", bundle.getString("ride_fare"));
                            intent.putExtra("rider_disc", bundle.getString("rider_disc"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }else if(bundledata != null && type.equalsIgnoreCase("DC")){
                            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else if (sm.isLoggedIn()) {
                            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } /*else {
                            Intent i = new Intent(SplashScreenActivity.this, SelectLanguage.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        }*/


                    } else {
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }/* else {
                        Intent i = new Intent(SplashActivity.this, SelectLanguage.class);
                        startActivity(i);
                        finish();
                    }*/
                }

            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST:
                int j = 0;
                if (grantResults != null && grantResults.length > 0) {
                    for (j = 0; j < grantResults.length; j++) {
                        if (grantResults[j] != PackageManager.PERMISSION_GRANTED) {

                            break;
                        }
                    }
                    if (j == grantResults.length) {

                        Intent mainIntent;

                        if(strUserId.isEmpty()){
                            mainIntent = new Intent(SplashActivity.this, HomeActivity.class);
                            startActivity(mainIntent);
                            finish();
                        }else {
                            mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(mainIntent);
                            finish();
                        }

                    }

                }
                break;
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
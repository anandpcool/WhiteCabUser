package com.volive.whitecab.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.volive.whitecab.R;
import com.volive.whitecab.util.GPSTracker;
import com.volive.whitecab.util.MapUtil;
import com.volive.whitecab.util.PreferenceUtils;

public class DropOffActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    Button btn_confirm;
    ImageView back_drop_off;

    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    GPSTracker gpsTracker;
    String strLat = "", strLong = "", strAddress = "";
    EditText et_to_address;
    PreferenceUtils preferenceUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drop_off);

        initUI();
        initViews();

    }

    private void initUI() {
        preferenceUtils=new PreferenceUtils(DropOffActivity.this);
        gpsTracker=new GPSTracker(DropOffActivity.this);
        btn_confirm=findViewById(R.id.btn_confirm);
        back_drop_off=findViewById(R.id.back_drop_off);
        et_to_address=findViewById(R.id.et_to_address);
    }

    private void initViews() {
        btn_confirm.setOnClickListener(this);
        back_drop_off.setOnClickListener(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.drop_off_map);
        mapFragment.getMapAsync(DropOffActivity.this);

        try {
            strLat = String.valueOf(gpsTracker.getLatitude());
            strLong = String.valueOf(gpsTracker.getLongitude());
            strAddress = MapUtil.getLatLongToAddress(gpsTracker.getLatitude(), gpsTracker.getLongitude(), this);
            et_to_address.setText(strAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_drop_off:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

            case R.id.btn_confirm:

                if(preferenceUtils.getRideType().equalsIgnoreCase("")){
                    Intent intent=new Intent(DropOffActivity.this, BookingActivity.class);
                    intent.putExtra("key","dropoff");
                    intent.putExtra("to_address",et_to_address.getText().toString());
                    intent.putExtra("DropAddress", strAddress);
                    intent.putExtra("Droplatitude", strLat);
                    intent.putExtra("Droplongitute", strLong);
                    //startActivity(intent);
                    setResult(RESULT_OK, intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }else {
                    Intent intent=new Intent(DropOffActivity.this, LaterBookingActivity.class);
                    intent.putExtra("key","dropoff");
                    intent.putExtra("to_address",et_to_address.getText().toString());
                    intent.putExtra("DropAddress", strAddress);
                    intent.putExtra("Droplatitude", strLat);
                    intent.putExtra("Droplongitute", strLong);
                    //startActivity(intent);
                    setResult(RESULT_OK, intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }


                break;

        }

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(false);

        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()), 16));



        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                String lat = String.valueOf(cameraPosition.target.latitude);
                String log = String.valueOf(cameraPosition.target.longitude);

                if (!lat.equalsIgnoreCase(strLat) && !log.equalsIgnoreCase(strLong)) {
                    strLat = String.valueOf(cameraPosition.target.latitude);
                    strLong = String.valueOf(cameraPosition.target.longitude);
                    strAddress = MapUtil.getLatLongToAddress(cameraPosition.target.latitude, cameraPosition.target.longitude, DropOffActivity.this);
                    et_to_address.setText(strAddress);
                }

            }
        });

    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}

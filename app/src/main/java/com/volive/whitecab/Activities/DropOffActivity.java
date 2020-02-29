package com.volive.whitecab.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    ImageView back_drop_off,img_myLoc;
    TextView tv_dest_address;
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    GPSTracker gpsTracker;
    String dropLat = "", dropLong = "", dropAddress = "";
    PreferenceUtils preferenceUtils;
    LinearLayout ll_drop_off;
    String strFromAddress="", strTime="",strDate="",strVehicleType="", strFromLat="",strFromLong="",strDriverId="",strDistance="",strVehicleNumber="";
    int dropLocationResultCode = 222;
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
        tv_dest_address=findViewById(R.id.tv_dest_address);
        ll_drop_off=findViewById(R.id.ll_drop_off);
        img_myLoc=findViewById(R.id.img_myLoc);
    }

    private void initViews() {
        btn_confirm.setOnClickListener(this);
        back_drop_off.setOnClickListener(this);
        ll_drop_off.setOnClickListener(this);
        img_myLoc.setOnClickListener(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.drop_off_map);
        mapFragment.getMapAsync(DropOffActivity.this);

        try {
            dropLat = String.valueOf(gpsTracker.getLatitude());
            dropLong = String.valueOf(gpsTracker.getLongitude());
            dropAddress = MapUtil.getLatLongToAddress(gpsTracker.getLatitude(), gpsTracker.getLongitude(), this);
            tv_dest_address.setText(dropAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(getIntent().getExtras() != null){
            Intent intent = getIntent();
            if(preferenceUtils.getRideType().equalsIgnoreCase("")){
                strFromAddress = intent.getStringExtra("from_address");
                strFromLat= intent.getStringExtra("from_lat");
                strFromLong= intent.getStringExtra("from_long");
                strVehicleType = intent.getStringExtra("vehicleType");
                strDriverId= intent.getStringExtra("driverId");
                strDistance=intent.getStringExtra("distance");
                strVehicleNumber=intent.getStringExtra("vehicleNumber");
            }else {
                strFromAddress = intent.getStringExtra("from_address");
                strVehicleType = intent.getStringExtra("vehicleType");
                strTime=intent.getStringExtra("Date");
                strDate=intent.getStringExtra("Time");
                strFromLat= intent.getStringExtra("from_lat");
                strFromLong= intent.getStringExtra("from_long");
            }

        }


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_drop_off:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

            case R.id.img_myLoc:

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()), 16));

                dropAddress = MapUtil.getLatLongToAddress(gpsTracker.getLatitude(), gpsTracker.getLongitude(), DropOffActivity.this);

                tv_dest_address.setText(dropAddress);
                dropLat= String.valueOf(gpsTracker.getLatitude());
                dropLong = String.valueOf(gpsTracker.getLongitude());

                break;

            case R.id.btn_confirm:

                if(preferenceUtils.getRideType().equalsIgnoreCase("")){
                    Intent intent=new Intent(DropOffActivity.this, BookingActivity.class);
                    intent.putExtra("key","dropoff");
                    intent.putExtra("drop_address",tv_dest_address.getText().toString());
                    intent.putExtra("drop_latitude", dropLat);
                    intent.putExtra("drop_longitude", dropLong);
                    intent.putExtra("from_address", strFromAddress);
                    intent.putExtra("from_lat", strFromLat);
                    intent.putExtra("from_long", strFromLong);
                    intent.putExtra("vehicleType", strVehicleType);
                    intent.putExtra("driverId", strDriverId);
                    System.out.println("driverId" + strDriverId);
                    intent.putExtra("distance", strDistance);
                    intent.putExtra("vehicleNumber", strVehicleNumber);
                    startActivity(intent);
//                    setResult(RESULT_OK, intent);
//                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }else {
                    Intent intent=new Intent(DropOffActivity.this, LaterBookingActivity.class);
                    intent.putExtra("key","dropoff");
                    intent.putExtra("drop_address",tv_dest_address.getText().toString());
                    intent.putExtra("drop_latitude", dropLat);
                    intent.putExtra("drop_longitude", dropLong);
                    intent.putExtra("from_address", strFromAddress);
                    intent.putExtra("vehicleType", strVehicleType);
                    intent.putExtra("Date", strDate);
                    intent.putExtra("Time", strTime);
                    intent.putExtra("from_lat", strFromLat);
                    intent.putExtra("from_long", strFromLong);
                    startActivity(intent);
                   // setResult(RESULT_OK, intent);
                  //  finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }


                break;

            case R.id.ll_drop_off:

                Intent intent=new Intent(DropOffActivity.this, DropLocationActivity.class);
                intent.putExtra("from_address",strFromAddress);
                intent.putExtra("to_address",tv_dest_address.getText().toString());
                intent.putExtra("from_lat", strFromLat);
                intent.putExtra("from_long", strFromLong);
                startActivityForResult(intent, dropLocationResultCode);

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

                if (!lat.equalsIgnoreCase(dropLat) && !log.equalsIgnoreCase(dropLong)) {
                    dropLat = String.valueOf(cameraPosition.target.latitude);
                    dropLong = String.valueOf(cameraPosition.target.longitude);
                    dropAddress = MapUtil.getLatLongToAddress(cameraPosition.target.latitude, cameraPosition.target.longitude, DropOffActivity.this);
                    tv_dest_address.setText(dropAddress);
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == dropLocationResultCode) {

                dropAddress= data.getStringExtra("dest_address");
                dropLat= data.getStringExtra("dest_lat");
                dropLong= data.getStringExtra("dest_long");

                strFromAddress = data.getStringExtra("from_address");
                strFromLat = data.getStringExtra("from_lat");
                strFromLong = data.getStringExtra("from_long");

                tv_dest_address.setText(dropAddress);

                Log.e("jakshfdas",strFromLat+" "+strFromLong);

                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {

                        mMap.clear();
                        mMap = googleMap;
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(dropLat), Double.parseDouble(dropLong)), 16));

                    }
                });

            }
        }
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}

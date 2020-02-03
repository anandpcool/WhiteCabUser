package com.volive.whitecab.Activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.volive.whitecab.R;
import com.volive.whitecab.util.ApiUrl;
import com.volive.whitecab.util.DialogsUtils;
import com.volive.whitecab.util.GPSTracker;
import com.volive.whitecab.util.MapUtil;
import com.volive.whitecab.util.MessageToast;
import com.volive.whitecab.util.NetworkConnection;
import com.volive.whitecab.util.ServiceHandler;
import com.volive.whitecab.util.SessionManager;

import org.json.JSONObject;

public class LaterBookingActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    ImageView back_later_booking,current_loc;
    TextView tv_dropoff,tv_booking_date,tv_fare;
    TextInputLayout input_dropOff;
    EditText et_drop_loc,et_from_address;
    Button btn_conform_booking;
    LinearLayout ll_fare;

    GoogleMap mMap;
    GPSTracker gpsTracker;
    SupportMapFragment mapFragment;
    String strLat = "", strLong = "", strAddress = "", Address="",strvehicleType="",strDate="",strTime="",strDropAddress = "", strDropLat = "", strDropLong = "";
    int nSearchResultCode = 000;
    int nDropLocationResultCode = 00111;
    TextInputLayout input_toAddress;
    ProgressDialog myDialog;
    NetworkConnection nw;
    String strLatitude, strLongitute,strLanguage,strFare,strUserId,strParamCode,strVehicleType;
    Boolean netConnection = false;
    Boolean nodata = false;
    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_later_booking);

        initUI();
        initViews();

    }

    private void initUI() {
        back_later_booking=findViewById(R.id.back_later_booking);
        tv_dropoff=findViewById(R.id.tv_dropoff);
        input_dropOff=findViewById(R.id.input_dropOff);
        et_drop_loc=findViewById(R.id.et_drop_loc);
        btn_conform_booking=findViewById(R.id.btn_conform_booking);
        et_from_address=findViewById(R.id.et_from_address);
        tv_booking_date=findViewById(R.id.tv_booking_date);
        current_loc=findViewById(R.id.current_loc);
        input_toAddress=findViewById(R.id.input_toAddress);
        tv_fare=findViewById(R.id.tv_fare);
        ll_fare=findViewById(R.id.ll_fare);

        gpsTracker=new GPSTracker(LaterBookingActivity.this);
        nw=new NetworkConnection(LaterBookingActivity.this);

    }

    private void initViews() {

        back_later_booking.setOnClickListener(this);
        tv_dropoff.setOnClickListener(this);
        btn_conform_booking.setOnClickListener(this);
        current_loc.setOnClickListener(this);

        if(getIntent().getExtras() != null){

             Address=  getIntent().getStringExtra("Address");
             strvehicleType=  getIntent().getStringExtra("vehicleType");
             strDate= getIntent().getStringExtra("Date");
             strTime= getIntent().getStringExtra("Time");
             tv_booking_date.setText("Ride on "+strDate+" at "+strTime);

        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.booking_map);
        mapFragment.getMapAsync(LaterBookingActivity.this);

        try {
            strLat = String.valueOf(gpsTracker.getLatitude());
            strLong = String.valueOf(gpsTracker.getLongitude());
            strAddress = MapUtil.getLatLongToAddress(gpsTracker.getLatitude(), gpsTracker.getLongitude(), this);
            et_from_address.setText(strAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_later_booking:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;


            case R.id.tv_dropoff:

                Intent intent=new Intent(LaterBookingActivity.this, DropOffActivity.class);
              //  startActivity(intent);
                startActivityForResult(intent, nDropLocationResultCode);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                break;

            case R.id.btn_conform_booking:

                if(et_drop_loc.getText().toString().isEmpty()){

                    Toast.makeText(this, "Please Enter Your Destination", Toast.LENGTH_SHORT).show();

                }else {
                    new requestRideLater().execute();
                }

                break;

            case R.id.current_loc:

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()), 16));

                strAddress = MapUtil.getLatLongToAddress(gpsTracker.getLatitude(), gpsTracker.getLongitude(), LaterBookingActivity.this);

                et_from_address.setText(strAddress);
                strLat = String.valueOf(gpsTracker.getLatitude());
                strLong = String.valueOf(gpsTracker.getLongitude());

                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK){

            if (requestCode == nSearchResultCode){

            }else if (requestCode == nDropLocationResultCode) {

                input_toAddress.setVisibility(View.VISIBLE);
                tv_dropoff.setVisibility(View.GONE);

                strDropAddress = data.getStringExtra("DropAddress");
                strDropLat = data.getStringExtra("Droplatitude");
                strDropLong = data.getStringExtra("Droplongitute");
                et_drop_loc.setText(strDropAddress);

                new getFare().execute();
            }


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
                    strAddress = MapUtil.getLatLongToAddress(cameraPosition.target.latitude, cameraPosition.target.longitude, LaterBookingActivity.this);
                    et_from_address.setText(strAddress);
                }

            }
        });
    }

    private class requestRideLater extends AsyncTask<Void, Void, Void> {

        String response = null;
        boolean status;
        String message, message_ar;
        JSONObject jsonObject;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(LaterBookingActivity.this, getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();

                try {

                    json.put("API-KEY", "1514209135");
                    json.put("user_id", strUserId);
                    json.put("from_address", strAddress);
                    json.put("from_latitude", strLatitude);
                    json.put("from_longitude", strLongitute);
                    json.put("to_address", strDropAddress);
                    json.put("to_latitude", strDropLat);
                    json.put("to_longitude", strDropLong);
                    json.put("trip_date", strDate);
                    json.put("requested_time", strTime);
                    json.put("promo_code", strParamCode);
                    json.put("vehicle_type", strVehicleType);

                    Log.e("SendDriverParam", json.toString());
                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(ApiUrl.strBaseUrl + ApiUrl.strRideLater, ServiceHandler.POST, json);


                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");

                    Log.e("strRideLater", response.toString());
//                    message_ar = js.getString("message_ar");


                    if (status) {
                        if (strLanguage.equalsIgnoreCase("1")) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }

//                        jsonObject = js.getJSONObject("Driver_details");
                    } else {

                        if (strLanguage.equalsIgnoreCase("1")) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }
//                        message_ar = js.getString("message_ar");

                    }

                    nodata = false;

                } catch (Exception ex) {
                    ex.printStackTrace();
                    nodata = true;
                }

                netConnection = true;

            } else {

                netConnection = false;

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (myDialog.isShowing())
                myDialog.dismiss();

            if (netConnection) {

                if (nodata) {

                    MessageToast.showToastMethod(LaterBookingActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {

                        showRideConfirmDialog(message);

                    } else {
                        MessageToast.showToastMethod(LaterBookingActivity.this, message);

                    }

                }
            } else {

                MessageToast.showToastMethod(LaterBookingActivity.this, getString(R.string.check_net_connection));

            }

        }

    }

    private void showRideConfirmDialog(String message) {

        dialog = new Dialog(LaterBookingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        dialog.setContentView(R.layout.ride_confirm_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        ImageView img_ride_confirm,img_cancel;

        img_cancel=dialog.findViewById(R.id.img_cancel);
        TextView tv_confirm_msg=dialog.findViewById(R.id.tv_confirm_msg);
        img_ride_confirm=dialog.findViewById(R.id.img_ride_confirm);

        //tv_confirm_msg.setText()

        img_ride_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();

            }
        });

        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

            }
        });

        dialog.show();

    }

    private class getFare extends AsyncTask<Void, Void, Void> {

        String response = null;
        boolean status;
        String message, message_ar;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(LaterBookingActivity.this, getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();
                try {

                   /* json.put("API-KEY", "1514209135");
                    json.put("user_id", strUserId);
                    json.put("lat", gpsTracker.getLatitude());
                    json.put("long", gpsTracker.getLongitude());
                    json.put("address", strAddress);*/

                    String finalUrl = ApiUrl.strBaseUrl+"calc_fare?from_lat=" + strLatitude + "&from_long=" + strLongitute + "&to_lat=" + strDropLat + "&to_long=" + strDropLong + "&API-KEY=1514209135";
                    Log.e("FinalUrl", finalUrl);
                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(finalUrl, ServiceHandler.GET, json);


                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");

                    Log.e("Fare", response.toString());
//                    message_ar = js.getString("message_ar");


                    if (status) {

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }

                        JSONObject jsonObject = js.getJSONObject("details");
                        strFare = jsonObject.getString("fare");

                    } else {

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }
//                        message_ar = js.getString("message_ar");

                    }

                    nodata = false;

                } catch (Exception ex) {
                    ex.printStackTrace();
                    nodata = true;
                }

                netConnection = true;

            } else {

                netConnection = false;

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (myDialog.isShowing())
                myDialog.dismiss();

            if (netConnection) {

                if (nodata) {

                    MessageToast.showToastMethod(LaterBookingActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {
                        ll_fare.setVisibility(View.VISIBLE);
                        tv_fare.setText(strFare);
                    } else {
                        MessageToast.showToastMethod(LaterBookingActivity.this, message);
                    }

                }
            } else {

                MessageToast.showToastMethod(LaterBookingActivity.this, getString(R.string.check_net_connection));

            }

        }

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
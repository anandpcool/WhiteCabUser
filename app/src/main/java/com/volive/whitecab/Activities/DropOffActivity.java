package com.volive.whitecab.Activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
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
import com.volive.whitecab.util.Constants;
import com.volive.whitecab.util.DialogsUtils;
import com.volive.whitecab.util.GPSTracker;
import com.volive.whitecab.util.MapUtil;
import com.volive.whitecab.util.MessageToast;
import com.volive.whitecab.util.NetworkConnection;
import com.volive.whitecab.util.PreferenceUtils;
import com.volive.whitecab.util.ServiceHandler;
import com.volive.whitecab.util.SessionManager;

import org.json.JSONObject;

import java.util.HashMap;

public class DropOffActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    Button btn_confirm;
    ImageView back_drop_off,img_myLoc,img_saveAddress;
    TextView tv_dest_address;
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    GPSTracker gpsTracker;
    String dropLat = "", dropLong = "", dropAddress = "",strUserId="",strLanguage="";
    PreferenceUtils preferenceUtils;
    LinearLayout ll_drop_off;
    String strFromAddress="", strTime="",strDate="",strVehicleType="", strFromLat="",strFromLong="",strDriverId="",strDistance="",strVehicleNumber="";
    int dropLocationResultCode = 222;
    NetworkConnection nw;
    SessionManager sm;
    Boolean netConnection = false;
    Boolean nodata = false;
    private ProgressDialog myDialog;
    private Dialog addressDialog;
    boolean boolean_save_address=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drop_off);

        initUI();
        initViews();

    }

    private void initUI() {
        nw=new NetworkConnection(DropOffActivity.this);
        sm=new SessionManager(DropOffActivity.this);
        HashMap<String, String> userDetail = sm.getUserDetails();
        strUserId = userDetail.get(SessionManager.KEY_ID);

        preferenceUtils=new PreferenceUtils(DropOffActivity.this);
        gpsTracker=new GPSTracker(DropOffActivity.this);
        btn_confirm=findViewById(R.id.btn_confirm);
        back_drop_off=findViewById(R.id.back_drop_off);
        tv_dest_address=findViewById(R.id.tv_dest_address);
        ll_drop_off=findViewById(R.id.ll_drop_off);
        img_myLoc=findViewById(R.id.img_myLoc);
        img_saveAddress=findViewById(R.id.img_saveAddress);
    }

    private void initViews() {
        btn_confirm.setOnClickListener(this);
        back_drop_off.setOnClickListener(this);
        ll_drop_off.setOnClickListener(this);
        img_myLoc.setOnClickListener(this);
        img_saveAddress.setOnClickListener(this);

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

    /*@Override
    protected void onResume() {
        super.onResume();
        new checkFavorite().execute();
    }
*/
    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_drop_off:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

            case R.id.img_saveAddress:


                if(boolean_save_address){
                    Toast.makeText(DropOffActivity.this, "This Address is already saved", Toast.LENGTH_SHORT).show();
                }else {
                    addAddressTitleDialog();
                }


                break;

            case R.id.img_myLoc:

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()), 16));

                dropAddress = MapUtil.getLatLongToAddress(gpsTracker.getLatitude(), gpsTracker.getLongitude(), DropOffActivity.this);

                tv_dest_address.setText(dropAddress);
                dropLat= String.valueOf(gpsTracker.getLatitude());
                dropLong = String.valueOf(gpsTracker.getLongitude());
                new checkFavorite().execute();
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

    private void addAddressTitleDialog() {
        addressDialog=new Dialog(DropOffActivity.this);
        addressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = addressDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        addressDialog.setContentView(R.layout.address_title_dialog);
        addressDialog.setCanceledOnTouchOutside(true);
        addressDialog.setCancelable(true);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        addressDialog.show();

        final EditText edt_title=(EditText)addressDialog.findViewById(R.id.edt_title);
        Button btn_save=(Button)addressDialog.findViewById(R.id.btn_save);
        ImageView img_close=(ImageView)addressDialog.findViewById(R.id.img_close);


        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addressDialog.dismiss();

            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edt_title.getText().toString().isEmpty()){
                    MessageToast.showToastMethod(DropOffActivity.this, ""+getResources().getString(R.string.add_title));
                }else {
                    new saveAddress(edt_title.getText().toString()).execute();
                }

            }
        });

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
                    boolean_save_address=false;
                    new checkFavorite().execute();
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
                new checkFavorite().execute();
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


    private class saveAddress extends AsyncTask<Void, Void, Void> {

        String response = null,title;
        boolean status;
        String message, message_ar;

        public saveAddress(String title) {
            this.title=title;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(DropOffActivity.this, getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();
                try {

                    json.put("API-KEY", Constants.API_KEY);
                    json.put("user_id", strUserId);
                    json.put("lat", dropLat);
                    json.put("long", dropLong);
                    json.put("address", dropAddress);
                    json.put("type",title);
                    json.put("request_type","save");

                    Log.e("Param", json.toString());
                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(ApiUrl.strBaseUrl + ApiUrl.strSaveAddress, ServiceHandler.POST, json);

                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");

                    Log.e("strSaveAddress", response.toString());
//                    message_ar = js.getString("message_ar");

                    if (status) {

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }

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
            if (myDialog.isShowing()){
                myDialog.dismiss();
            }

            if (netConnection) {

                if (nodata) {

                    MessageToast.showToastMethod(DropOffActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {
                        addressDialog.dismiss();
                        img_saveAddress.setImageDrawable(getResources().getDrawable(R.drawable.ic_love_yellow));
                        MessageToast.showToastMethod(DropOffActivity.this, message);
                        boolean_save_address=true;
                    } else {
                        MessageToast.showToastMethod(DropOffActivity.this, message);
                    }

                }

            } else {

                MessageToast.showToastMethod(DropOffActivity.this, getString(R.string.check_net_connection));

            }

        }

    }

    private class checkFavorite extends AsyncTask<Void,Void,Void>{

        String response = null;
        boolean status;
        String message, message_ar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoader();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();
                try {

                    json.put("API-KEY", Constants.API_KEY);
                    json.put("user_id", strUserId);
                    json.put("lat", dropLat);
                    json.put("long", dropLong);


                    Log.e("Param", json.toString());
                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(ApiUrl.strBaseUrl + ApiUrl.checkFavorite, ServiceHandler.POST, json);

                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");

                    Log.e("strFavouriteAddress", response.toString());

                    if (status) {

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }

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
            hideLoader();

            if (netConnection) {

                if (nodata) {

                    MessageToast.showToastMethod(DropOffActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {
                        img_saveAddress.setImageDrawable(getResources().getDrawable(R.drawable.ic_love_yellow));
                        boolean_save_address=true;
                    } else {
                        img_saveAddress.setImageDrawable(getResources().getDrawable(R.drawable.love_gray));
                        boolean_save_address=false;
                    }

                }

            } else {

                MessageToast.showToastMethod(DropOffActivity.this, getString(R.string.check_net_connection));

            }

        }


    }

    // Show progress bar
    public void showLoader() {
        try {
            if (myDialog != null)
                myDialog.dismiss();
            myDialog = null;
            myDialog = new ProgressDialog(DropOffActivity.this);

            myDialog.setTitle("");
            myDialog.setMessage(getString(R.string.please_wait));
            myDialog.setCancelable(false);
            myDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Hide progress Bar
    public void hideLoader() {
        try {
            if (myDialog != null && myDialog.isShowing())
                myDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}

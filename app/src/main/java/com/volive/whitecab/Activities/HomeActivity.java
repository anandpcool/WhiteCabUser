package com.volive.whitecab.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.volive.whitecab.Adapters.RecyclerAdapters.VechicleAdapter;
import com.volive.whitecab.DataModels.VechileType;
import com.volive.whitecab.DataModels.Vehicle;
import com.volive.whitecab.R;
import com.volive.whitecab.util.ApiUrl;
import com.volive.whitecab.util.Constants;
import com.volive.whitecab.util.GPSTracker;
import com.volive.whitecab.util.GalleryUriToPath;
import com.volive.whitecab.util.MapUtil;
import com.volive.whitecab.util.MessageToast;
import com.volive.whitecab.util.NetworkConnection;
import com.volive.whitecab.util.PreferenceUtils;
import com.volive.whitecab.util.ServiceHandler;
import com.volive.whitecab.util.SessionManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    public static DrawerLayout drawerLayout;
    NavigationView navigationView;
    RecyclerView drawerRecyclerView,vehicle_recycler;
    NavigationItemAdapter itemAdapter;
    String[] nav_texts=new String[]{"Home","My Rides","Wallet","Offers","Settings","About","Contact Us","Help"};
    int[] nav_icons=new int[]{R.drawable.ic_home_nav,R.drawable.ic_car_nav,R.drawable.ic_wallet_nav,R.drawable.ic_offer_nav,R.drawable.ic_setting_nav,R.drawable.ic_about_nav,R.drawable.ic_contact_nav,R.drawable.ic_help_nav};
    ImageView img_home_menu,imgSaveAddress;
    VechicleAdapter vechicleAdapter;
    Button btn_ride_now,btn_ride_later;

    int[] vehicle_images=new int[]{R.drawable.car_mini,R.drawable.car_sedan,R.drawable.car_suv,R.drawable.car_luxury,R.drawable.car_redo};
    String[] vehicle_texts=new String[]{"Mini","Sedan","SUV","Luxury","Redo"};
    PreferenceUtils preferenceUtils;

    LinearLayout ll_pick,ll_fromAddress;
    TextView txt_pickTime,tv_from_address,tv_user_name,tv_user_email;
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    ImageView img_currentLocation,img_user_profile;
    GPSTracker gpsTracker;
    String strAddress,strLat, strLong, strDate,strUserId, strEmail, strName,strLanguage="",strVehicleBaseOnType,strDriverId, strVehicleNumber, strDistance;
    NetworkConnection nw;
    SessionManager sm;
    Boolean netConnection = false;
    Boolean nodata = false;
    Boolean netConnection1 = false;
    Boolean nodata1 = false;
    ArrayList<Vehicle> arrVehicle;
    ArrayList<VechileType> arrVehicleType = new ArrayList<>();
    ProgressDialog myDialog;
    boolean isSelectVehicle = false;
    HorizontalAdapter horizontalAdapter;
    int pickLocationResultCode = 111;
    public static int REQUEST_CAMERA = 5;
    public static int SELECT_FILE = 4;
    private static final int GRANT_LOC_ACCESS = 800;
    String picturePath = "empty";
    String PickedImgPath = "empty",strImagePath, strImageUrl;
    private Dialog addressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initUI();
        initViews();

    }

    private void initUI() {
        preferenceUtils=new PreferenceUtils(HomeActivity.this);
        nw=new NetworkConnection(HomeActivity.this);
        sm = new SessionManager(HomeActivity.this);
        gpsTracker = new GPSTracker(HomeActivity.this);

        img_home_menu=findViewById(R.id.img_home_menu);
        vehicle_recycler=findViewById(R.id.vehicle_recycler);
        vehicle_recycler.setItemAnimator(new DefaultItemAnimator());
        btn_ride_now=findViewById(R.id.btn_ride_now);
        btn_ride_later=findViewById(R.id.btn_ride_later);
        img_currentLocation=findViewById(R.id.img_currentLocation);
//        et_from_address=findViewById(R.id.et_from_address);
//        et_from_address.setEnabled(false);
        tv_from_address=findViewById(R.id.tv_from_address);
        ll_pick=findViewById(R.id.ll_pick);
        txt_pickTime=findViewById(R.id.txt_pickTime);
        imgSaveAddress=findViewById(R.id.imgSaveAddress);
        ll_fromAddress=findViewById(R.id.ll_fromAddress);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(HomeActivity.this);

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date dateNew = new Date();
        strDate = format1.format(dateNew);

        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) navigationView.getLayoutParams();
        navigationView.removeHeaderView(navigationView.getHeaderView(0));
        View headerView = navigationView.inflateHeaderView(R.layout.left_layout);
        tv_user_name=(TextView) headerView.findViewById(R.id.tv_user_name);
        tv_user_email=(TextView) headerView.findViewById(R.id.tv_user_email);
        img_user_profile=(ImageView) headerView.findViewById(R.id.img_user_profile);
        drawerRecyclerView = (RecyclerView) headerView.findViewById(R.id.nav_drawer_recycler_view);
        itemAdapter=new NavigationItemAdapter(HomeActivity.this,nav_texts,nav_icons);
        drawerRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        drawerRecyclerView.setHasFixedSize(true);
        drawerRecyclerView.setAdapter(itemAdapter);

        navigationView.setVerticalScrollBarEnabled(false);


        HashMap<String, String> userDetail = sm.getUserDetails();
        HashMap<String, String> userProfile = sm.returnProfile_url();
        strImagePath = userProfile.get(SessionManager.PROFILE_IMG_PATH);
        strImageUrl = userProfile.get(SessionManager.PROFILE_IMG_URL);

        if(userDetail.get(SessionManager.KEY_ID) != null){
            strUserId = userDetail.get(SessionManager.KEY_ID);
            strName = userDetail.get(SessionManager.KEY_NAME);
            strEmail = userDetail.get(SessionManager.KEY_EMAIL);
            strLanguage = userDetail.get(SessionManager.KEY_LANGUAGE);

            tv_user_name.setText(strName);
            tv_user_email.setText(strEmail);
            if(strImageUrl.isEmpty()){
                img_user_profile.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile_empty));
            }else {
                Glide.with(HomeActivity.this).load(strImagePath + strImageUrl).into(img_user_profile);
            }

        }
    }

    private void initViews() {
        img_home_menu.setOnClickListener(this);
        btn_ride_now.setOnClickListener(this);
        btn_ride_later.setOnClickListener(this);
        img_currentLocation.setOnClickListener(this);
        imgSaveAddress.setOnClickListener(this);
        ll_fromAddress.setOnClickListener(this);
        img_user_profile.setOnClickListener(this);

        try {
            strLat = String.valueOf(gpsTracker.getLatitude());
            strLong = String.valueOf(gpsTracker.getLongitude());
            System.out.println("latlong" + strLat + strLong);
            strAddress = MapUtil.getLatLongToAddress(gpsTracker.getLatitude(), gpsTracker.getLongitude(), this);

            tv_from_address.setText(strAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*vechicleAdapter=new VechicleAdapter(HomeActivity.this,vehicle_images,vehicle_texts);
        vehicle_recycler.setHasFixedSize(true);
        vehicle_recycler.setAdapter(vechicleAdapter);*/

    }

    @Override
    protected void onResume() {
        super.onResume();
      //  new currentRide().execute();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.img_user_profile:

                if (ActivityCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(HomeActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, GRANT_LOC_ACCESS);
                } else {
                    selectImage();
                }

                break;

            case R.id.ll_fromAddress:

                Intent intent=new Intent(HomeActivity.this,PickLocationActivity.class);
                intent.putExtra("strAddress",strAddress);
                startActivityForResult(intent,pickLocationResultCode);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                break;


            case R.id.imgSaveAddress:

              /*  if(boolean_save_address){
                    Toast.makeText(HomeActivity.this, "This Address is already saved", Toast.LENGTH_SHORT).show();
                }else {

                }*/

                addAddressTitleDialog();

                break;

            case R.id.img_currentLocation:

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()), 16));

                strAddress = MapUtil.getLatLongToAddress(gpsTracker.getLatitude(), gpsTracker.getLongitude(), HomeActivity.this);

                tv_from_address.setText(strAddress);
                strLat = String.valueOf(gpsTracker.getLatitude());
                strLong = String.valueOf(gpsTracker.getLongitude());
                new getVehicle().execute();
                new vehicleType().execute();
                new checkFavorite().execute();

                break;

            case R.id.img_home_menu:

                drawerLayout.openDrawer(GravityCompat.START);

                break;

            case R.id.btn_ride_now:
                preferenceUtils.setRideType("");

                    Intent intent1 = new Intent(HomeActivity.this, DropOffActivity.class);
                    intent1.putExtra("from_lat", strLat);
                    intent1.putExtra("from_long", strLong);
                    intent1.putExtra("from_address", strAddress);
                    intent1.putExtra("vehicleType", strVehicleBaseOnType);
                    intent1.putExtra("driverId", strDriverId);
                    System.out.println("driverId" + strDriverId);
                    intent1.putExtra("distance", strDistance);
                    intent1.putExtra("vehicleNumber", strVehicleNumber);
                    startActivity(intent1);
                    // startActivity(new Intent(HomeActivity.this, BookingActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                break;

            case R.id.btn_ride_later:

               /* if (!isSelectVehicle) {
                    Toast.makeText(HomeActivity.this, R.string.empty_select_car, Toast.LENGTH_SHORT).show();
                } else {*/

                    Intent my_intent = new Intent(HomeActivity.this, RideLaterActivity.class);
                    my_intent.putExtra("address", strAddress);
                    my_intent.putExtra("from_lat", strLat);
                    my_intent.putExtra("from_long", strLong);
                    my_intent.putExtra("vehicleType", strVehicleBaseOnType);
                    startActivity(my_intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

               // }

                break;

        }
    }

    private void addAddressTitleDialog() {
        addressDialog=new Dialog(HomeActivity.this);
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
                    MessageToast.showToastMethod(HomeActivity.this, ""+getResources().getString(R.string.add_title));
                }else {
                    new saveAddress(edt_title.getText().toString()).execute();
                }

            }
        });

    }

    public void onItemSelect(int position) {

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
                    json.put("lat", strLat);
                    json.put("long", strLong);


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

                    MessageToast.showToastMethod(HomeActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {
                        imgSaveAddress.setImageDrawable(getResources().getDrawable(R.drawable.ic_love_yellow));
                       // boolean_save_address=true;
                    } else {
                        imgSaveAddress.setImageDrawable(getResources().getDrawable(R.drawable.love_gray));
                        //boolean_save_address=false;
                    }

                }

            } else {

                MessageToast.showToastMethod(HomeActivity.this, getString(R.string.check_net_connection));

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
                    strAddress = MapUtil.getLatLongToAddress(cameraPosition.target.latitude, cameraPosition.target.longitude, HomeActivity.this);
                    tv_from_address.setText(strAddress);
                    imgSaveAddress.setImageDrawable(getResources().getDrawable(R.drawable.love_gray));
                  //  boolean_save_address=false;
                    new getVehicle().execute();
                    new vehicleType().execute();
                    new checkFavorite().execute();
                }
            }
        });

    }



    private class getVehicle extends AsyncTask<Void, Void, Void> {

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
                    json.put("lat", strLat);
                    json.put("long", strLong);
                    json.put("datetime", strDate);
                    json.put("user_id", sm.getSingleField(SessionManager.KEY_ID));
                    // Log.e("user_id",sm.getSingleField(SessionManager.KEY_ID));

                    Log.e("Param", json.toString());
                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(ApiUrl.strBaseUrl + ApiUrl.strVehicle, ServiceHandler.POST, json);


                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");

                    Log.e("getVehicleResponse", response.toString());

                    arrVehicle = new ArrayList<>();

                    if (status) {
                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }

                        JSONArray jsonArray = js.getJSONArray("Vehicles");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            String vehicle_id = jsonArray.getJSONObject(i).getString("vehicle_id");
                            String vehicle_type = jsonArray.getJSONObject(i).getString("vehicle_type");
                            String vehicle_name = jsonArray.getJSONObject(i).getString("vehicle_name");
                            String driver_id = jsonArray.getJSONObject(i).getString("driver_id");
                            String lattitude = jsonArray.getJSONObject(i).getString("lattitude");
                            String longitude = jsonArray.getJSONObject(i).getString("longitude");
                            String distance = jsonArray.getJSONObject(i).getString("distance");

                            Vehicle vehicle = new Vehicle(vehicle_id, vehicle_type, vehicle_name, driver_id, lattitude, longitude, distance, "");
                            arrVehicle.add(vehicle);
                        }
                    } else {

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }

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
                    MessageToast.showToastMethod(HomeActivity.this, getString(R.string.no_data));
                } else {
                    if (status) {

                        for (int i = 0; i < arrVehicle.size(); i++) {
                            mMap.clear();
                            Log.e("caricon", arrVehicle.get(i).getLattitude() + arrVehicle.get(i).getLongitude());
                            mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(arrVehicle.get(i).getLattitude()), Double.parseDouble(arrVehicle.get(i).getLongitude()))).title(arrVehicle.get(i).getVehicle_type()).icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
                        }
                    } else {
                        mMap.clear();
                        MessageToast.showToastMethod(HomeActivity.this, message);
                    }
                }
            } else {
                MessageToast.showToastMethod(HomeActivity.this, getString(R.string.check_net_connection));
            }

        }

    }

    // Show progress bar
    public void showLoader() {
        try {
            if (myDialog != null)
                myDialog.dismiss();
            myDialog = null;
            myDialog = new ProgressDialog(HomeActivity.this);

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


    private class vehicleType extends AsyncTask<Void, Void, Void> {

        String response = null;
        boolean status;
        String message, message_ar;
        String peaktime_status, peak_factor;


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
                    json.put("lat", strLat);
                    json.put("long", strLong);
                    json.put("datetime", strDate);

                    Log.e("Param", json.toString());
                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(ApiUrl.strBaseUrl + ApiUrl.strVehicleType, ServiceHandler.POST, json);


                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");

                    Log.e("vehicleTypeResponse", response.toString());

                    arrVehicleType = new ArrayList<>();
                    if (status) {

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }

                        peaktime_status = js.getString("peaktime_status");
                        peak_factor = js.getString("peak_factor");
                        JSONArray jsonArray = js.getJSONArray("cab_types");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            String driver_id = jsonArray.getJSONObject(i).getString("driver_id");
                            String lattitude = jsonArray.getJSONObject(i).getString("lattitude");
                            String longitude = jsonArray.getJSONObject(i).getString("longitude");
                            String distance = jsonArray.getJSONObject(i).getString("distance");
                            String vehicle_type = jsonArray.getJSONObject(i).getString("vehicle_type");
                            String time = jsonArray.getJSONObject(i).getString("time");
                            String cab_type_image = jsonArray.getJSONObject(i).getString("cab_type_image");
                            String vehicle_type_ar = jsonArray.getJSONObject(i).getString("vehicle_type_ar");

                            VechileType vechileType = new VechileType(driver_id, lattitude, longitude, distance, vehicle_type, time, cab_type_image, vehicle_type_ar);
                            arrVehicleType.add(vechileType);
                        }
                        //arrVehicleType.get(0).setIseTextVisible(true);

                    } else {

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }

                    }
                    nodata1 = false;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    nodata1 = true;
                }

                netConnection1 = true;
            } else {
                netConnection1 = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            hideLoader();

            if (netConnection1) {

                if (nodata1) {
                    MessageToast.showToastMethod(HomeActivity.this, getString(R.string.no_data));
                } else {
                    if (status) {

                        if (peaktime_status.equalsIgnoreCase("1")) {
                            ll_pick.setVisibility(View.VISIBLE);
                            txt_pickTime.setText(peak_factor + "x | Peak factor");
                        }
                        Log.e("vechiles",arrVehicleType.size()+"");
                        horizontalAdapter = new HorizontalAdapter(HomeActivity.this, arrVehicleType);
                        vehicle_recycler.setAdapter(horizontalAdapter);
                    } else {
                        Log.e("vechiles123",arrVehicleType.size()+"");
                        horizontalAdapter = new HorizontalAdapter(HomeActivity.this, arrVehicleType);
                        vehicle_recycler.setAdapter(horizontalAdapter);
                        // MessageToast.showToastMethod(HomeScreenActivity.this, message);
                    }

                }
            } else {
                MessageToast.showToastMethod(HomeActivity.this, getString(R.string.check_net_connection));

            }

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK){

            if (requestCode == pickLocationResultCode){

                strAddress = data.getStringExtra("from_address");
                strLat=data.getStringExtra("from_lat");
                strLong=data.getStringExtra("from_long");
                Log.e("data123", strAddress+" "+strLat+","+strLong);
                tv_from_address.setText(strAddress);

                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {

                        mMap.clear();
                        mMap = googleMap;
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(strLat), Double.parseDouble(strLong)), 16));

                    }
                });

            }else  if (requestCode == SELECT_FILE){
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA){
                onCaptureImageResult(data);
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
            showLoader();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();
                try {

                    json.put("API-KEY", Constants.API_KEY);
                    json.put("user_id", strUserId);
                    json.put("lat", strLat);
                    json.put("long", strLong);
                    json.put("address", strAddress);
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
            hideLoader();

            if (netConnection) {

                if (nodata) {

                    MessageToast.showToastMethod(HomeActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {
                        addressDialog.dismiss();
                        imgSaveAddress.setImageDrawable(getResources().getDrawable(R.drawable.ic_love_yellow));
                        //boolean_save_address=true;
                        MessageToast.showToastMethod(HomeActivity.this, message);
                    } else {
                        MessageToast.showToastMethod(HomeActivity.this, message);
                    }

                }

            } else {

                MessageToast.showToastMethod(HomeActivity.this, getString(R.string.check_net_connection));

            }

        }

    }

    public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

        ArrayList<VechileType> arrList;
        Context context;
        int selectedPosition = 0;

        public HorizontalAdapter(Context context, ArrayList<VechileType> arrList) {
            this.arrList = arrList;
            this.context = context;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_row, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final int row_index = 0;
            Boolean isselected = true;
            final VechileType vechileType = arrList.get(position);

            if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                holder.txtview.setText(vechileType.getVehicle_type());
            } else if (strLanguage.equalsIgnoreCase("2")) {
                holder.txtview.setText(vechileType.getVehicle_type_ar());
            }

            holder.textViewTime.setText(vechileType.getTime());
            Glide.with(context).load(vechileType.getCab_type_image()).into(holder.imageView);

            if (vechileType.iseTextVisible()) {
                isSelectVehicle = true;
                    holder.txtview.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.txtview.setTypeface(holder.txtview.getTypeface(), Typeface.BOLD);
            } else {
                holder.txtview.setTextColor(context.getResources().getColor(R.color.black));
            }

            if (position == selectedPosition) {

                strVehicleBaseOnType = vechileType.getVehicle_type();
                holder.txtview.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.txtview.setTypeface(holder.txtview.getTypeface(), Typeface.BOLD);
                new getVehicleBaseOntype().execute();

            } else {
                holder.txtview.setTextColor(context.getResources().getColor(R.color.black));
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    for (VechileType item1 : arrList) {
                        item1.setIseTextVisible(false);
                    }

                    vechileType.setIseTextVisible(true);

                    // strVehicleBaseOnType = vechileType.getVehicle_type();
                    // new getVehicleBaseOntype().execute();
                    selectedPosition = position;
                    notifyDataSetChanged();
                }
            });


        }

        @Override
        public int getItemCount() {
            return arrList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            CircleImageView imageView;
            TextView txtview, textViewTime;

            public MyViewHolder(View view) {
                super(view);
                imageView = (CircleImageView) view.findViewById(R.id.imageview);
                txtview = (TextView) view.findViewById(R.id.txtname);
                textViewTime = (TextView) view.findViewById(R.id.txttime);
              //  txtview.setTypeface(typefaceLight);
            }
        }
    }


    private class NavigationItemAdapter extends RecyclerView.Adapter<NavigationItemAdapter.MyHolder> {
        Context context;
        String[] nav_texts;
        private int selected= -1;
        int[] nav_icon;

        public NavigationItemAdapter(Context context, String[] nav_texts, int[] nav_icons) {
            this.context=context;
            this.nav_texts=nav_texts;
            this.nav_icon=nav_icons;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_adapter_layout,parent,false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, final int position) {

            holder.tv_nav_text.setText(nav_texts[position]);
            holder.nav_icon.setImageResource(nav_icon[position]);

            if(position == 2){
                holder.tv_walletAmount.setVisibility(View.VISIBLE);
            }

            holder.ll_nav_adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    selected=position;
                    notifyDataSetChanged();

                    final String element = nav_texts[position];
                    ((HomeActivity)context).onItemSelected(element);

                }
            });

            if(selected == position){
                holder.tv_nav_text.setTextColor(context.getResources().getColor(R.color.black));
            } else {
                holder.tv_nav_text.setTextColor(context.getResources().getColor(R.color.black));
            }

        }


        @Override
        public int getItemCount() {
            return nav_texts.length;
        }


        public class MyHolder extends RecyclerView.ViewHolder {
            TextView tv_nav_text,tv_walletAmount;
            ImageView nav_icon;
            LinearLayout ll_nav_adapter;
            public MyHolder(View itemView) {
                super(itemView);

                ll_nav_adapter=itemView.findViewById(R.id.ll_nav_adapter);
                nav_icon=itemView.findViewById(R.id.nav_icon);
                tv_nav_text=itemView.findViewById(R.id.tv_nav_text);
                tv_walletAmount=itemView.findViewById(R.id.tv_walletAmount);

            }
        }

    }

    private class getVehicleBaseOntype extends AsyncTask<Void, Void, Void> {

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

                    json.put("API-KEY", "1514209135");
                    json.put("lat", strLat);
                    json.put("long", strLong);
                    json.put("datetime", strDate);
                    json.put("vehicle_type", strVehicleBaseOnType);

                    Log.e("Param", json.toString());
                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(ApiUrl.strBaseUrl + ApiUrl.strVehicleBaseOntype, ServiceHandler.POST, json);

                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");

                    //  Log.e("strVehicleBaseOntype", response.toString());
                    System.out.println("strVehicleBaseOntype1234" + response.toString());
//                    message_ar = js.getString("message_ar");


                    if (status) {

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }

                        arrVehicle.clear();
                        JSONArray jsonArray = js.getJSONArray("Vehicles");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            String vehicle_id = jsonArray.getJSONObject(i).getString("vehicle_id");
                            String vehicle_type = jsonArray.getJSONObject(i).getString("vehicle_type");
                            String vehicle_name = jsonArray.getJSONObject(i).getString("vehicle_name");
                            String driver_id = jsonArray.getJSONObject(i).getString("driver_id");
                            String lattitude = jsonArray.getJSONObject(i).getString("lattitude");
                            String longitude = jsonArray.getJSONObject(i).getString("longitude");
                            String distance = jsonArray.getJSONObject(i).getString("distance");
                            String vehicle_number = jsonArray.getJSONObject(i).getString("vehicle_number");

                            Vehicle vehicle = new Vehicle(vehicle_id, vehicle_type, vehicle_name, driver_id, lattitude, longitude, distance, vehicle_number);
                            arrVehicle.add(vehicle);
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

                    MessageToast.showToastMethod(HomeActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {

                        strDriverId = arrVehicle.get(0).getDriver_id();
                        System.out.println("change driverid" + strDriverId);
                        strDistance = arrVehicle.get(0).getDistance();
                        strVehicleNumber = arrVehicle.get(0).getVehicle_number();

                        mMap.clear();
                        for (int i = 0; i < arrVehicle.size(); i++) {
                            mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(arrVehicle.get(i).getLattitude()), Double.parseDouble(arrVehicle.get(i).getLongitude()))).title(arrVehicle.get(i).getVehicle_type()).icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
                        }

                    } else {
                        MessageToast.showToastMethod(HomeActivity.this, message);
                    }

                }
            } else {
                MessageToast.showToastMethod(HomeActivity.this, getString(R.string.check_net_connection));
            }

        }

    }

    private void onItemSelected(String element) {

        if(element.equalsIgnoreCase(getString(R.string.settings)))
        {
            drawerLayout.closeDrawers();
            Intent intent=new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        }else if(element.equalsIgnoreCase(getString(R.string.my_rides))){
            drawerLayout.closeDrawers();
            Intent intent=new Intent(HomeActivity.this, MyRidesActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        }else if(element.equalsIgnoreCase(getString(R.string.home))){
            drawerLayout.closeDrawers();
        }else if(element.equalsIgnoreCase(getString(R.string.contact_us))){
            drawerLayout.closeDrawers();

            Intent intent=new Intent(HomeActivity.this, ContactUsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        }else if(element.equalsIgnoreCase(getString(R.string.help))){

            drawerLayout.closeDrawers();

            Intent intent=new Intent(HomeActivity.this, HelpActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        }else if(element.equalsIgnoreCase(getString(R.string.about))){

            drawerLayout.closeDrawers();

            Intent intent=new Intent(HomeActivity.this, AboutActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


        }else if(element.equalsIgnoreCase(getString(R.string.offers))){

            drawerLayout.closeDrawers();

            Intent intent=new Intent(HomeActivity.this, OffersActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


        }else if(element.equalsIgnoreCase(getString(R.string.wallet))){

            drawerLayout.closeDrawers();

            Intent intent=new Intent(HomeActivity.this, WalletActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        }

    }


    public class currentRide extends AsyncTask<Void, Void, Void> {

        String response = null;
        boolean status;
        String type;
        String message, message_ar, driver_id, vehicle_name, vehicle_number, driver_name, driver_mobile, trip_id, time, distance, driver_profile, driver_lat, driver_long;

        String color, avg_rating="",from_address,dest_address,from_latitude,from_longitude,to_latitude,to_longitude;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showLoader();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();
                try {
                    json.put("API-KEY", Constants.API_KEY);
                    json.put("user_id", strUserId);

                    Log.e("Param", json.toString());
                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(ApiUrl.strBaseUrl + "waiting_requests", ServiceHandler.POST, json);


                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");

                    Log.e("currentrides", response.toString());
                    System.out.println("current rides" + response.toString());
//                    message_ar = js.getString("message_ar");


                    if (status) {
                        JSONObject aps = js.getJSONObject("aps");
                        JSONObject alert = aps.getJSONObject("alert");
                        JSONObject data = alert.getJSONObject("data");
                        vehicle_name = data.getString("driver_name");
                        vehicle_number = data.getString("vehicle_number");
                        driver_name = data.getString("vehicle_name");
                        driver_mobile = data.getString("driver_mobile");
                        trip_id = data.getString("trip_id");
                        time = data.getString("req_time");
                        distance = data.getString("distance");
                        driver_profile = data.getString("driver_profile");
                        driver_lat = data.getString("driver_lat");
                        driver_long = data.getString("driver_long");
                        driver_id = data.getString("driver_id");
                        color = data.getString("vehicle_color");
                        avg_rating = data.getString("avg_rating");

                        from_address=data.getString("from_address");
                        dest_address=data.getString("to_address");
                        from_latitude=data.getString("from_latitude");
                        from_longitude=data.getString("from_longitude");
                        to_latitude=data.getString("to_latitude");
                        to_longitude=data.getString("to_longitude");


                        if (strLanguage.equalsIgnoreCase("1")) {
                            // message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            // message = js.getString("message_ar");
                        }

                        JSONObject start_ride = js.getJSONObject("start_ride");
                        JSONObject alerts = start_ride.getJSONObject("alerts");
                        String ride_status = alerts.getString("type");
                        if (ride_status.equalsIgnoreCase("4")) {
                            type = ride_status;

                        } else {
                            type = "";
                        }

                    } else {

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            // message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            //message = js.getString("message_ar");
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
            // hideLoader();

            if (netConnection) {

                if (nodata) {

                    MessageToast.showToastMethod(HomeActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {

                        Intent intent = new Intent(HomeActivity.this, TrackingActivity.class);
                        intent.putExtra("fromscreen", "1");
                        intent.putExtra("vehicle_name", vehicle_name);
                        intent.putExtra("vehicle_number", vehicle_number);
                        intent.putExtra("driver_name", driver_name);
                        intent.putExtra("driver_mobile", driver_mobile);
                        intent.putExtra("trip_id", trip_id);
                        intent.putExtra("time", time);
                        intent.putExtra("distance", distance);
                        intent.putExtra("driver_profile", driver_profile);
                        intent.putExtra("driver_lat", driver_lat);
                        intent.putExtra("driver_long", driver_long);
                        intent.putExtra("driver_id", driver_id);

                        intent.putExtra("type", type);
                        intent.putExtra("avg_rating ", avg_rating);
                        intent.putExtra("color", color);

                        intent.putExtra("from_address",from_address);
                        intent.putExtra("dest_address",dest_address);
                        intent.putExtra("from_latitude",from_latitude);
                        intent.putExtra("from_longitude",from_longitude);
                        intent.putExtra("to_latitude",to_latitude);
                        intent.putExtra("to_longitude",to_longitude);
                        startActivity(intent);

                    } /*else {
                         MessageToast.showToastMethod(HomeScreenActivity.this, "There is no current ride now");
                    }*/


                }
            } else {

                MessageToast.showToastMethod(HomeActivity.this, getString(R.string.check_net_connection));

            }

        }

    }

    private void selectImage() {
        final CharSequence[] items = {getString(R.string.takeaphoto), getString(R.string.choosefrmgallery),
                getString(R.string.cancel)};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HomeActivity.this);
        builder.setTitle(R.string.add_photo);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.takeaphoto))) {
                    cameraIntent();
                } else if (items[item].equals(getString(R.string.choosefrmgallery))) {
                    galleryIntent();
                } else if (items[item].equals(getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(HomeActivity.this.getContentResolver(), data.getData());

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = GalleryUriToPath.getPath(HomeActivity.this, selectedImage);
                PickedImgPath = GalleryUriToPath.getPath(HomeActivity.this, selectedImage);
                Log.e("Gallery Path", picturePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
       // img_user_profile.setImageBitmap(bm);
        new uploadProfilePic().execute();
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
            picturePath = destination.getAbsolutePath();
            Log.e("Camera Path", destination.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
     //  img_user_profile.setImageBitmap(thumbnail);
        new uploadProfilePic().execute();
    }

    private class uploadProfilePic extends AsyncTask<Void, Void, Void> {


        String res;
        String message;
        boolean status;

        String image, path;

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


                    String res = postFile();
                    System.out.println("register activity response res: " + res.toString());

                    JSONObject js = new JSONObject(res);
                    status = js.getBoolean("status");

                    if (status) {

                        if (strLanguage.equalsIgnoreCase("1")) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }
                        JSONObject userobj = js.getJSONObject("user_details");
                        image = userobj.getString("profile_pic");
                        path = js.getString("base_url");
                        sm.profileImageUrl(image, path);
                    } else {
                        if (strLanguage.equalsIgnoreCase("1")) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }
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

                    MessageToast.showToastMethod(HomeActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {

                        Glide.with(HomeActivity.this).load(path + image).into(img_user_profile);
                        MessageToast.showToastMethod(HomeActivity.this, message);
                    } else {

                        MessageToast.showToastMethod(HomeActivity.this, message);
                    }

                }
            } else {

                MessageToast.showToastMethod(HomeActivity.this, getString(R.string.check_net_connection));

            }

        }

    }

    private String postFile() {

        String responseStr = "Empty";

        try {

            Log.v("textFile: ", picturePath);

            // new HttpClient
            HttpClient httpClient = new DefaultHttpClient();

            // post header
            HttpPost httpPost = new HttpPost(ApiUrl.strBaseUrl + ApiUrl.strEditProfileImage);

            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            reqEntity.addPart("user_id", new StringBody(strUserId));
            reqEntity.addPart("API-KEY", new StringBody("1514209135"));


            if (!picturePath.equals("empty")) {

                File pickedfile = new File(picturePath);

                long fileSizeInBytes = pickedfile.length();
                float fileSizeInKB = fileSizeInBytes / 1024;
                System.out.println("/-/-/-/-/-/-  file size is " + fileSizeInKB);
                FileBody fBody = new FileBody(pickedfile);

                reqEntity.addPart("profile_picture", fBody);

                System.out.println("bvksbvkjgmjlsf" + fBody);

            }
            //  reqEntity.addPart("attached_file", fileBody);  //file
            httpPost.setEntity(reqEntity);

            // execute HTTP post request
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();

            if (resEntity != null) {

                responseStr = EntityUtils.toString(resEntity).trim();
                Log.e("Response in change: ", responseStr);
                // you can add an if statement here and do other actions based on the response
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseStr;
    }


    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers();
        }else {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

    }

}
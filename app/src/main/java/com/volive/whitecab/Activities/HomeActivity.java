package com.volive.whitecab.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.volive.whitecab.util.GPSTracker;
import com.volive.whitecab.util.MapUtil;
import com.volive.whitecab.util.MessageToast;
import com.volive.whitecab.util.NetworkConnection;
import com.volive.whitecab.util.PreferenceUtils;
import com.volive.whitecab.util.ServiceHandler;
import com.volive.whitecab.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

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

    EditText et_from_address;
    LinearLayout ll_pick,ll_fromAddress;
    TextView txt_pickTime;

    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    ImageView img_currentLocation;
    GPSTracker gpsTracker;
    String strAddress,strLat, strLong, strDate,strUserId, strEmail, strName,strLanguage,strVehicleBaseOnType,strDriverId, strVehicleNumber, strDistance;
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
        et_from_address=findViewById(R.id.et_from_address);
        et_from_address.setEnabled(false);
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
        drawerRecyclerView = (RecyclerView) headerView.findViewById(R.id.nav_drawer_recycler_view);
        itemAdapter=new NavigationItemAdapter(HomeActivity.this,nav_texts,nav_icons);
        drawerRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        drawerRecyclerView.setHasFixedSize(true);
        drawerRecyclerView.setAdapter(itemAdapter);

        navigationView.setVerticalScrollBarEnabled(false);
    }

    private void initViews() {
        img_home_menu.setOnClickListener(this);
        btn_ride_now.setOnClickListener(this);
        btn_ride_later.setOnClickListener(this);
        img_currentLocation.setOnClickListener(this);
        imgSaveAddress.setOnClickListener(this);
        ll_fromAddress.setOnClickListener(this);

        HashMap<String, String> userDetail = sm.getUserDetails();
        strUserId = userDetail.get(SessionManager.KEY_ID);
        strName = userDetail.get(SessionManager.KEY_NAME);
        strEmail = userDetail.get(SessionManager.KEY_EMAIL);
        strLanguage = userDetail.get(SessionManager.KEY_LANGUAGE);

        try {
            strLat = String.valueOf(gpsTracker.getLatitude());
            strLong = String.valueOf(gpsTracker.getLongitude());
            System.out.println("latlong" + strLat + strLong);
            strAddress = MapUtil.getLatLongToAddress(gpsTracker.getLatitude(), gpsTracker.getLongitude(), this);

            et_from_address.setText(strAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*vechicleAdapter=new VechicleAdapter(HomeActivity.this,vehicle_images,vehicle_texts);
        vehicle_recycler.setHasFixedSize(true);
        vehicle_recycler.setAdapter(vechicleAdapter);*/

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.ll_fromAddress:

                Intent intent=new Intent(HomeActivity.this,PickLocationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                break;


            case R.id.imgSaveAddress:

                new saveAddress().execute();

                break;

            case R.id.img_currentLocation:

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()), 16));

                strAddress = MapUtil.getLatLongToAddress(gpsTracker.getLatitude(), gpsTracker.getLongitude(), HomeActivity.this);

                et_from_address.setText(strAddress);
                strLat = String.valueOf(gpsTracker.getLatitude());
                strLong = String.valueOf(gpsTracker.getLongitude());
                new getVehicle().execute();
                new vehicleType().execute();

                break;

            case R.id.img_home_menu:

                drawerLayout.openDrawer(GravityCompat.START);

                break;

            case R.id.btn_ride_now:
                preferenceUtils.setRideType("");
                //startActivity(new Intent(HomeActivity.this, PickLocationActivity.class));
                startActivity(new Intent(HomeActivity.this, BookingActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.btn_ride_later:

                if (!isSelectVehicle) {
                    Toast.makeText(HomeActivity.this, R.string.empty_select_car, Toast.LENGTH_SHORT).show();
                } else {

                    Intent my_intent = new Intent(HomeActivity.this, RideLaterActivity.class);
                    my_intent.putExtra("address", strAddress);
                    my_intent.putExtra("vehicleType", strVehicleBaseOnType);
                    startActivity(my_intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }

                break;

        }
    }

    public void onItemSelect(int position) {

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
                    et_from_address.setText(strAddress);
                    new getVehicle().execute();
                    new vehicleType().execute();
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
                    json.put("API-KEY", "1514209135");
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

                    Log.e("strVehicle", response.toString());

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
                    json.put("API-KEY", "1514209135");
                    json.put("lat", strLat);
                    json.put("long", strLong);
                    json.put("datetime", strDate);

                    Log.e("Param", json.toString());
                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(ApiUrl.strBaseUrl + ApiUrl.strVehicleType, ServiceHandler.POST, json);


                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");

                    Log.e("strVehicleType", response.toString());

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

    private class saveAddress extends AsyncTask<Void, Void, Void> {

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
                    json.put("user_id", strUserId);
                    json.put("lat", strLat);
                    json.put("long", strLong);
                    json.put("address", strAddress);

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
                        imgSaveAddress.setImageDrawable(getResources().getDrawable(R.drawable.star_fill));
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
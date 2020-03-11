package com.volive.whitecab.Activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.volive.whitecab.Adapters.RecyclerAdapters.ComplaintAdapter;
import com.volive.whitecab.Adapters.RecyclerAdapters.RideCancelAdapter;
import com.volive.whitecab.DataModels.ComplaintModel;
import com.volive.whitecab.R;
import com.volive.whitecab.util.ApiUrl;
import com.volive.whitecab.util.Constants;
import com.volive.whitecab.util.DialogsUtils;
import com.volive.whitecab.util.DirectionsJSONParser;
import com.volive.whitecab.util.MessageToast;
import com.volive.whitecab.util.MyApplication;
import com.volive.whitecab.util.NetworkConnection;
import com.volive.whitecab.util.PreferenceUtils;
import com.volive.whitecab.util.ServiceHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import de.hdodenhof.circleimageview.CircleImageView;


public class TrackingActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback,LocationListener, Observer {

    ImageView back_tracking;
   public static  Button btn_cancel_ride,btn_call_captain;
    RideCancelAdapter cancelAdapter;
    String[] texts=new String[]{"Too many riders","Too much luggage","Rider requested cancel","Rider didn't answer","Wrong address shown","Other"};
    String vehicle_name="", vehicle_number="", driver_name="", driver_mobile, trip_id="", time, distance="", driver_profile="", driver_lat, driver_long;
    String color, avg_rating="",driver_id = "",type,from_address,dest_address,from_latitude,from_longitude,to_latitude,to_longitude;
    TextView tracking_from_address,tracking_dest_address,tv_captainName,tv_rideDistance,tv_vehicleName,tv_vehicleNumber,tv_rating;
    CircleImageView captainProfile;
    SupportMapFragment mapFragment;
    GoogleMap mMap;
    private Marker fromMarker,now;
    private Marker destinationMarker;
    private static final float ANCHOR_VALUE = 0.5f;
    private Polyline polyline1,polyline2;
    private static final int POLYLINE_WIDTH = 10;
    private ProgressDialog myDialog123;
    NetworkConnection nw;
    Boolean netConnection = false;
    Boolean nodata = false;
    private String cancel_reason="",strLanguage="";
    ArrayList<ComplaintModel> cancelArrayList;
    MyApplication myApplication;
    PreferenceUtils preferenceUtils;

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            btn_call_captain.setVisibility(View.GONE);
            btn_cancel_ride.setVisibility(View.GONE);
            myApplication.getObserver().setValue("1");

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("custom-message"));
        initUI();
        initViews();

    }

    private void initViews() {
        back_tracking.setOnClickListener(this);
        btn_call_captain.setOnClickListener(this);
        btn_cancel_ride.setOnClickListener(this);

        Intent intent=getIntent();
        if(intent.getExtras() != null){

            String fromscreen = intent.getStringExtra("fromscreen");

            if(fromscreen.equalsIgnoreCase("1")) {
                driver_lat = intent.getStringExtra("driver_lat");
                driver_long = intent.getStringExtra("driver_long");
                from_address=intent.getStringExtra("from_address");
                dest_address=intent.getStringExtra("dest_address");
                from_latitude=intent.getStringExtra("from_latitude");
                from_longitude=intent.getStringExtra("from_longitude");
                to_latitude=intent.getStringExtra("to_latitude");
                to_longitude=intent.getStringExtra("to_longitude");
                vehicle_name = intent.getStringExtra("vehicle_name");
                vehicle_number = intent.getStringExtra("vehicle_number");
                driver_name = intent.getStringExtra("driver_name");
                driver_mobile = intent.getStringExtra("driver_mobile");
                trip_id = intent.getStringExtra("trip_id");
                time = intent.getStringExtra("time");
                distance = intent.getStringExtra("distance");
                driver_profile = intent.getStringExtra("driver_profile");

                driver_id = intent.getStringExtra("driver_id");
                type = intent.getStringExtra("type");
                color = intent.getStringExtra("color");
                avg_rating = intent.getStringExtra("avg_rating");

            }

            tracking_from_address.setText(from_address);
            tracking_dest_address.setText(dest_address);
            tv_captainName.setText(driver_name);
            tv_vehicleName.setText(vehicle_name);
            tv_vehicleNumber.setText(vehicle_number);

            Glide.with(TrackingActivity.this).load(driver_profile).into(captainProfile);


            tv_rating.setText(avg_rating);

            if(distance.isEmpty()){
                tv_rideDistance.setText("0 KM");
            }else {
                tv_rideDistance.setText(distance + " KM");
            }

        }

        if (myApplication.getObserver().getValue().equalsIgnoreCase("1")) {
            btn_call_captain.setVisibility(View.GONE);
            btn_cancel_ride.setVisibility(View.GONE);
        }

    }

    private void initUI() {
        nw=new NetworkConnection(TrackingActivity.this);
        preferenceUtils=new PreferenceUtils(TrackingActivity.this);
        cancelArrayList=new ArrayList<>();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.tracking_map);
        mapFragment.getMapAsync(TrackingActivity.this);
        myApplication = (MyApplication) getApplication();
        myApplication.getObserver().addObserver(this);
        back_tracking=findViewById(R.id.back_tracking);
        btn_call_captain=findViewById(R.id.btn_call_captain);
        btn_cancel_ride=findViewById(R.id.btn_cancel_ride);
        tv_captainName=findViewById(R.id.tv_captainName);
        tv_rideDistance=findViewById(R.id.tv_rideDistance);
        tv_vehicleName=findViewById(R.id.tv_vehicleName);
        tv_vehicleNumber=findViewById(R.id.tv_vehicleNumber);
        tv_rating=findViewById(R.id.tv_rating);
        captainProfile=findViewById(R.id.captainProfile);

        tracking_dest_address=findViewById(R.id.tracking_dest_address);
        tracking_from_address=findViewById(R.id.tracking_from_address);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_tracking:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

            case R.id.btn_cancel_ride:

                new cancelRideList().execute();

                break;

            case R.id.btn_call_captain:

                driver_mobile = "0" + driver_mobile;
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + driver_mobile));
                if (ActivityCompat.checkSelfPermission(TrackingActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(callIntent);

                break;

        }
    }

    @Override
    public void update(Observable observable, Object o) {
        if (myApplication.getObserver().getValue().equalsIgnoreCase("1")) {
            mMap.clear();

            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            btn_call_captain.setVisibility(View.GONE);
            btn_cancel_ride.setVisibility(View.GONE);

            LatLng drivrLatlong = new LatLng(Double.parseDouble(driver_lat), Double.parseDouble(driver_long));

            now = mMap.addMarker(new MarkerOptions()
                    .position(drivrLatlong)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_new)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(drivrLatlong, 13));

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (now != null) {
            now.remove();
        }
        LatLng drivrLatlong = new LatLng(location.getLatitude(), location.getLongitude());

        now = mMap.addMarker(new MarkerOptions()
                .position(drivrLatlong)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.current_loc_icon)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(drivrLatlong, 13));
    }


    private class cancelRideList extends AsyncTask<Void,Void,Void>{

        String response = null;
        boolean status;
        String message, message_ar;
        private ProgressDialog myDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(TrackingActivity.this, getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();
                try {

                    String finalUrl = ApiUrl.strBaseUrl+ApiUrl.cancelRideList + "&API-KEY=1514209135";
                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(finalUrl, ServiceHandler.GET, json);


                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");
                    Log.e("response", response.toString());
//                    message_ar = js.getString("message_ar");

                    if (status) {

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }

                        JSONArray jsonArray=js.getJSONArray("CancelRideList");
                        cancelArrayList.clear();
                        if(jsonArray != null && jsonArray.length() > 0){
                            cancelArrayList=new ArrayList<>();
                            for(int i=0; i<jsonArray.length(); i++){

                                JSONObject object=jsonArray.getJSONObject(i);

                                String id= object.getString("id");
                                String name_en=object.getString("name_en");
                                ComplaintModel complaintPojo= new ComplaintModel(id, name_en);
                                cancelArrayList.add(complaintPojo);
                            }

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
            if (myDialog.isShowing())
                myDialog.dismiss();

            if (netConnection) {

                if (nodata) {

                    MessageToast.showToastMethod(TrackingActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {

                        openCancelAlert();

                    } else {
                        MessageToast.showToastMethod(TrackingActivity.this, message);
                    }

                }
            } else {

                MessageToast.showToastMethod(TrackingActivity.this, getString(R.string.check_net_connection));

            }

        }



    }

    private void openCancelAlert() {

        final Dialog dialog = new Dialog(TrackingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        dialog.setContentView(R.layout.cancel_ride_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        RecyclerView recyclerView=dialog.findViewById(R.id.cancel_recycler);
        cancelAdapter=new RideCancelAdapter(TrackingActivity.this,cancelArrayList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(cancelAdapter);

        ImageView img_cancel=dialog.findViewById(R.id.img_cancel);
        RelativeLayout rl_submit=dialog.findViewById(R.id.rl_submit);


        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

            }
        });

        rl_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(cancel_reason.isEmpty()){
                 MessageToast.showToastMethod(TrackingActivity.this,getString(R.string.please_select_reason));
                }else {
                    new cancelRequest().execute();
                }

            }
        });

        dialog.show();

    }

    public void onItemSelect(String cancel_reason) {
        this.cancel_reason=cancel_reason;
    }

    private class cancelRequest extends AsyncTask<Void, Void, Void> {

        String response = null;
        boolean status;
        String message, message_ar;
        JSONObject jsonObject;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog123 = DialogsUtils.showProgressDialog(TrackingActivity.this, getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if (nw.isConnectingToInternet()) {

                if(trip_id.isEmpty()){
                    trip_id=preferenceUtils.getTripId();
                }

                JSONObject json = new JSONObject();
                try {
                    json.put("API-KEY", Constants.API_KEY);
                    json.put("trip_id", trip_id);
                    json.put("cancel_reason",cancel_reason);

                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(ApiUrl.strBaseUrl + ApiUrl.strCanceltrip, ServiceHandler.POST, json);


                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");

                    Log.e("strCanceltrip", response.toString());
//                    message_ar = js.getString("message_ar");


                    if (status) {
                        message = js.getString("message");
//                        jsonObject = js.getJSONObject("Driver_details");
                    } else {

                        message = js.getString("message");
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
            if (myDialog123.isShowing())
                myDialog123.dismiss();

            if (netConnection) {

                if (nodata) {

                    MessageToast.showToastMethod(TrackingActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {

                        MessageToast.showToastMethod(TrackingActivity.this, message);
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        finish();

                    } else {
                        MessageToast.showToastMethod(TrackingActivity.this, message);
                       // RideCompletedDialog();
                    }

                }
            } else {

                MessageToast.showToastMethod(TrackingActivity.this, getString(R.string.check_net_connection));

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

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(from_latitude), Double.valueOf(from_longitude)), 12));

        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(from_latitude), Double.valueOf(from_longitude)), 16));

        LatLng drivrLatlong = new LatLng(Double.parseDouble(driver_lat), Double.parseDouble(driver_long));

        now = mMap.addMarker(new MarkerOptions()
                .position(drivrLatlong)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.current_loc_icon)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(drivrLatlong, 13));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                setPolyline();

            }
        },1000);

    }

    private void setPolyline() {
        final LatLng fromLatLng = new LatLng(Double.valueOf(from_latitude), Double.valueOf(from_longitude));
        final LatLng destLatLng = new LatLng(Double.valueOf(to_latitude), Double.valueOf(to_longitude));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Your code to run in GUI thread here
                if (fromMarker == null) {
                    setStartPoint(fromLatLng);
                    setDestination(destLatLng);
                } else {
                    animateMarker(destinationMarker, destLatLng, false);
                }
                setPath(fromMarker, 1);
            }
        });

    }


    private void setStartPoint(final LatLng mLatLng) {
        fromMarker = mMap.addMarker(new MarkerOptions()
                .position(mLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.current_loc_icon)));
    }


    private void setDestination(final LatLng mLatLng) {
        Location startLocation = new Location("Start Location");
        startLocation.setLatitude(mLatLng.latitude);
        startLocation.setLongitude(mLatLng.longitude);

        destinationMarker = mMap.addMarker(new MarkerOptions()
                .position(mLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_icon))
                .rotation(startLocation.getBearing())
                .flat(true)
                .anchor(ANCHOR_VALUE, ANCHOR_VALUE));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(destinationMarker.getPosition());
        builder.include(fromMarker.getPosition());
        LatLngBounds bounds = builder.build();
        int padding = Math.round(30);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }

    public void animateMarker(final Marker marker, final LatLng toPosition, final boolean hideMarker) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Projection proj = mMap.getProjection();
        final Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 3000;
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                final long elapsed = SystemClock.uptimeMillis() - start;
                final float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                final double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                final double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;

                final LatLng driverPoint = new LatLng(lat, lng);

                Location startLocation = new Location("Start Location");
                startLocation.setLatitude(startLatLng.latitude);
                startLocation.setLongitude(startLatLng.longitude);

                Location endLocation = new Location("End Location");
                endLocation.setLatitude(toPosition.latitude);
                endLocation.setLongitude(toPosition.longitude);
                /* rotate the marker to the new latlng position */
                float bearing = startLocation.bearingTo(endLocation);
                marker.setRotation(bearing);
                marker.setAnchor(ANCHOR_VALUE, ANCHOR_VALUE);
                /* Change the position of the marker instead of adding new marker
                 * in order to make a smooth marker movement */
                marker.setPosition(driverPoint);

               /* final int delayDuration = 16;
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, delayDuration);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }*/
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 6);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    /**
     * set polyline path
     */
    private void setPath(final Marker marker, final int type) {
        LatLng origin = new LatLng(destinationMarker.getPosition().latitude,
                destinationMarker.getPosition().longitude);
        // map.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 19));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 12f));
        LatLng dest = new LatLng(marker.getPosition().latitude,
                marker.getPosition().longitude);
        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin, dest);
        DownloadTask downloadTask = new DownloadTask(type);
        downloadTask.execute(url);
    }

    private String getDirectionsUrl(final LatLng origin, final LatLng dest) {
        // Origin of route
        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String strDest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key=" + getString(R.string.google_maps_key);
        // Building the parameters to the web service
        String parameters = strOrigin + "&" + strDest + "&" + sensor + "&" + mode + "&" + key;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }



    /**
     * asynctask class to fetch the points between 2 positions
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        private int mType;

        DownloadTask(final int type) {
            mType = type;
        }

        @Override
        protected String doInBackground(final String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask(mType);
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        private int mType;

        ParserTask(final int type) {
            mType = type;
        }

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(final String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(final List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            if (result == null) {
                return;
            }
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);

                }
                List<PatternItem> pattern = Arrays.<PatternItem>asList(
                        new Gap(10), new Dash(20), new Gap(10));
                lineOptions.addAll(points);
                lineOptions.width(POLYLINE_WIDTH);
                lineOptions.color(Color.BLACK);
                // lineOptions.pattern(pattern);
                lineOptions.geodesic(true);
            }
            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                switch (mType) {
                    case 1:
                        if (polyline1 != null) {
                            polyline1.remove();
                        }
                        polyline1 = mMap.addPolyline(lineOptions);
                        break;

                    case 2:
                        if (polyline2 != null) {
                            polyline2.remove();
                        }
                        polyline2 = mMap.addPolyline(lineOptions);
                        break;

                    default:
                }
            }
        }
    }

    private String downloadUrl(final String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private void RideCompletedDialog() {
        final Dialog dialog = new Dialog(TrackingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        dialog.setContentView(R.layout.ride_completed_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        Button btn_complaint,btn_rating;
        TextView tv_trip_id,tv_rating,tv_driver_name,txt_ride_distance,txt_vehicle_number,
                txt_vehicle_name,txtFromAddress,txtStartTime,txtEndTime,txtDestAddress,txt_total_amount,txt_discount,txt_final_amount;
        CircleImageView img_captainProfile;
        tv_trip_id=dialog.findViewById(R.id.tv_trip_id);

        btn_complaint=dialog.findViewById(R.id.btn_complaint);
        btn_rating=dialog.findViewById(R.id.btn_rating);

        tv_rating=dialog.findViewById(R.id.tv_rating);
        img_captainProfile=dialog.findViewById(R.id.img_captainProfile);
        tv_driver_name=dialog.findViewById(R.id.tv_driver_name);
        txt_ride_distance=dialog.findViewById(R.id.txt_ride_distance);
        txt_vehicle_number=dialog.findViewById(R.id.txt_vehicle_number);
        txt_vehicle_name=dialog.findViewById(R.id.txt_vehicle_name);
        txtFromAddress=dialog.findViewById(R.id.txtFromAddress);
        txtDestAddress=dialog.findViewById(R.id.txtDestAddress);
        txtStartTime=dialog.findViewById(R.id.txtStartTime);
        txtEndTime=dialog.findViewById(R.id.txtEndTime);
        txt_total_amount=dialog.findViewById(R.id.txt_total_amount);
        txt_discount=dialog.findViewById(R.id.txt_discount);
        txt_final_amount=dialog.findViewById(R.id.txt_final_amount);

        String text="<b> <font color=#000>"+getResources().getString(R.string.trip_id)+"</font> </b>"+" "+trip_id;
        tv_trip_id.setText(Html.fromHtml(text));
        if(avg_rating.isEmpty()){
            tv_rating.setText("0");
        }else {
            tv_rating.setText(avg_rating);
        }

        tv_driver_name.setText(driver_name);
        txt_vehicle_number.setText(vehicle_number);
        txt_vehicle_name.setText(vehicle_name);
        txtFromAddress.setText(from_address);
        txtDestAddress.setText(dest_address);
        if(distance.isEmpty()){
            txt_ride_distance.setText("0 KM");
        }else {
            txt_ride_distance.setText(distance+" KM");
        }

        Glide.with(TrackingActivity.this).load(Constants.IMAGE_BASE_URL+driver_profile).into(img_captainProfile);

        btn_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(TrackingActivity.this, RatingActivity.class);
                intent.putExtra("driver_id",driver_id);
                intent.putExtra("driver_name",driver_name);
                intent.putExtra("driver_profile",driver_profile);
                intent.putExtra("driver_rating",avg_rating);
                intent.putExtra("trip_id",trip_id);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btn_complaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TrackingActivity.this, ComplaintActivity.class);
                intent.putExtra("trip_id",trip_id);
                intent.putExtra("driver_id",driver_id);
                intent.putExtra("driver_name",driver_name);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        dialog.show();
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

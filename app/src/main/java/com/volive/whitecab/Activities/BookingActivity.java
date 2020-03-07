package com.volive.whitecab.Activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.volive.whitecab.R;
import com.volive.whitecab.util.ApiUrl;
import com.volive.whitecab.util.Constants;
import com.volive.whitecab.util.DialogsUtils;
import com.volive.whitecab.util.DirectionsJSONParser;
import com.volive.whitecab.util.GPSTracker;
import com.volive.whitecab.util.MessageToast;
import com.volive.whitecab.util.NetworkConnection;
import com.volive.whitecab.util.PreferenceUtils;
import com.volive.whitecab.util.ServiceHandler;
import com.volive.whitecab.util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.view.View.VISIBLE;

public class BookingActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    ImageView back_booking;
    Button btn_book_now;
    ImageView img_booking_map,img_exclamation,img_current_loc;
    TextView tv_estimate,tv_fare;
    LinearLayout ll_fare,linear_cash,linear_promo;
    PreferenceUtils preferenceUtils;
    //EditText et_drop_location,et_from_address;
    TextView tv_from_address, tv_dest_address;
    TextInputLayout input_destination;
    private static final int POLYLINE_WIDTH = 10;
    int nSearchResultCode = 000;
    int nDropLocationResultCode = 00111;
    private GoogleMap mMap;
    GPSTracker gpsTracker;
    SupportMapFragment mapFragment;
    String strParamCode="",strUserId="",strPromoCode = "",strFromLat = "", strVehicleType="",strFromLong = "", strFromAddress = "",strDropAddress = "", strDropLat = "", strDropLong = "",strLanguage="",strFare;
    ProgressDialog myDialog;
    NetworkConnection nw;
    Boolean netConnection = false;
    Boolean nodata = false;
    Handler handler = new Handler();
    private long delay=3000;
    private Marker fromMarker;
    private Marker destinationMarker;
    private static final float ANCHOR_VALUE = 0.5f;
    private Polyline polyline1,polyline2;
    Dialog prmo_dialog;
    EditText edtPromoCode;
    SessionManager sm;
    String strDate="", strTime="",trip_id="",strDriverId="", strVehicleNumber="", strDistance="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        initUI();
        initViews();

    }

    private void initUI() {
        back_booking=findViewById(R.id.back_booking);
        btn_book_now=findViewById(R.id.btn_book_now);
        img_booking_map=findViewById(R.id.img_booking_map);
        tv_estimate=findViewById(R.id.tv_estimate);
        img_exclamation=findViewById(R.id.img_exclamation);
        ll_fare=findViewById(R.id.ll_fare);
        linear_cash=findViewById(R.id.linear_cash);
        linear_promo=findViewById(R.id.linear_promo);
       // et_drop_location=findViewById(R.id.et_drop_location);
        input_destination=findViewById(R.id.input_destination);
      //  et_from_address=findViewById(R.id.et_from_address);
     //   img_current_loc=findViewById(R.id.img_current_loc);
        tv_from_address=findViewById(R.id.tv_from_address);
        tv_dest_address=findViewById(R.id.tv_dest_address);
        tv_fare=findViewById(R.id.tv_fare);
        preferenceUtils=new PreferenceUtils(BookingActivity.this);
        gpsTracker=new GPSTracker(BookingActivity.this);
        nw=new NetworkConnection(BookingActivity.this);
        sm=new SessionManager(BookingActivity.this);
        HashMap<String, String> userDetail = sm.getUserDetails();
        if(userDetail.get(SessionManager.KEY_ID) != null){
            Log.e("key_id", userDetail.get(SessionManager.KEY_ID));
            strUserId = userDetail.get(SessionManager.KEY_ID);
        }


    }

    private void initViews() {
        back_booking.setOnClickListener(this);
        btn_book_now.setOnClickListener(this);
        linear_promo.setOnClickListener(this);
        linear_cash.setOnClickListener(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.booking_map);
        mapFragment.getMapAsync(BookingActivity.this);

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date dateNew = new Date();
        strDate = format1.format(dateNew);

        SimpleDateFormat format2 = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        Date dateNewTime = new Date();
        strTime = format2.format(dateNewTime);

        if(getIntent().getExtras() !=null){

            /*if(preferenceUtils.getRideType().equalsIgnoreCase("Ride Later")){

                img_exclamation.setVisibility(View.GONE);
                img_booking_map.setImageDrawable(getResources().getDrawable(R.drawable.booking_map2));
                tv_estimate.setText("Note: This is an approximate estimate, Actual cost may be different due to traffic and waiting time");
                btn_book_now.setText(getString(R.string.confirm_booking));

            }else */if(getIntent().getStringExtra("key").equalsIgnoreCase("dropoff")){

                strFromAddress=  getIntent().getStringExtra("from_address");
                strDropAddress= getIntent().getStringExtra("drop_address");
                strDropLat= getIntent().getStringExtra("drop_latitude");
                strDropLong= getIntent().getStringExtra("drop_longitude");
                strFromLat= getIntent().getStringExtra("from_lat");
                strFromLong= getIntent().getStringExtra("from_long");
                strVehicleType = getIntent().getStringExtra("vehicleType");
                strDriverId= getIntent().getStringExtra("driverId");
                strDistance=getIntent().getStringExtra("distance");
                strVehicleNumber=getIntent().getStringExtra("vehicleNumber");

                tv_from_address.setText(strFromAddress);
                tv_dest_address.setText(strDropAddress);

                btn_book_now.setText(getString(R.string.book_now));
                img_exclamation.setVisibility(View.GONE);
                img_booking_map.setImageDrawable(getResources().getDrawable(R.drawable.booking_map2));

                new getFare().execute();

            }

        }
    }

    private void setPolyline() {
        final LatLng fromLatLng = new LatLng(Double.parseDouble(strFromLat), Double.parseDouble(strFromLong));
        final LatLng destLatLng = new LatLng(Double.parseDouble(strDropLat), Double.parseDouble(strDropLong));


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
             //   lineOptions.pattern(pattern);
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


    @Override
    public void onClick(View view) {

        switch (view.getId()){

           /* case R.id.img_current_loc:

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()), 16));

                strFromAddress = MapUtil.getLatLongToAddress(gpsTracker.getLatitude(), gpsTracker.getLongitude(), BookingActivity.this);

                et_from_address.setText(strFromAddress);
                strFromLat = String.valueOf(gpsTracker.getLatitude());
                strFromLong = String.valueOf(gpsTracker.getLongitude());

                break;*/

            case R.id.back_booking:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                break;


            case R.id.btn_book_now:

                if(btn_book_now.getText().toString().equalsIgnoreCase(getString(R.string.book_now))){

                    new SendRequestToDriver().execute();

//                    startActivity(new Intent(BookingActivity.this, TrackingActivity.class));
//                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }

                break;

            case R.id.linear_cash:

                openPaymentDialog();

                break;

            case R.id.linear_promo:

                openPromoDialog();

                break;

        }

    }

    private class SendRequestToDriver extends AsyncTask<Void, Void, Void> {

        String response = null;
        boolean status;
        String message, message_ar, value;
        JSONObject jsonObject;
        JSONObject js;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(BookingActivity.this, getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();
                try {
                    json.put("API-KEY", Constants.API_KEY);
                    json.put("user_id", strUserId);
                    json.put("type", strVehicleType);
//                    json.put("driver_id", strDriverId);
                    json.put("from_address", strFromAddress);
                    json.put("from_latitude", strFromLat);
                    json.put("from_longitude", strFromLong);
                    json.put("to_address", strDropAddress);
                    json.put("to_latitude", strDropLat);
                    json.put("to_longitude", strDropLong);
                    json.put("trip_date", strDate);
                    json.put("requested_time", strTime);
                    json.put("vehicle_number", strVehicleNumber);
                    json.put("promo_code", strParamCode);
                    json.put("distance", strDistance);
                    System.out.println(strUserId + ".." + strFromAddress + strFromLat + strFromLong + strDropAddress + strDropLat);
                    System.out.println(strDropLong + strDate + strTime + ".." + strParamCode + "..");

                    Log.e("SendDriverParam", json.toString());
                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(ApiUrl.strBaseUrl + ApiUrl.strSendRequestToDriver, ServiceHandler.POST, json);


                    js  = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");
                    Log.e("SendRequestToDriver", response.toString());



//                    message_ar = js.getString("message_ar");


                    if (status) {
                        value = js.getString("value");
                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }

                    } else {

                        if (strLanguage.equalsIgnoreCase("1")  || strLanguage.isEmpty()) {
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

                    // MessageToast.showToastMethod(BookingActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {
                        if (value.equalsIgnoreCase("1")) {
                            try {
                                trip_id = js.getString("Trip_id");
                                preferenceUtils.setTripId(trip_id);
//                                Intent intent = new Intent(BookingActivity.this, TrackingActivity.class);
//                                startActivity(intent);
                                MessageToast.showToastMethod(BookingActivity.this,message);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else if (value.equalsIgnoreCase("2")) {
                            try {
                                JSONObject driver_details = new JSONObject(response);
                                jsonObject = driver_details.getJSONObject("Driver_details");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }


                    } else {
                        MessageToast.showToastMethod(BookingActivity.this, message);
                        Intent intent = new Intent(BookingActivity.this, HomeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }

                }
            } else {

                MessageToast.showToastMethod(BookingActivity.this, getString(R.string.check_net_connection));

            }

        }

    }

    private class getFare extends AsyncTask<Void, Void, Void> {

        String response = null;
        boolean status;
        String message, message_ar;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(BookingActivity.this, getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();
                try {

                    String finalUrl = ApiUrl.strBaseUrl+"calc_fare?from_lat=" + strFromLat + "&from_long=" + strFromLong + "&to_lat=" + strDropLat + "&to_long=" + strDropLong + "&API-KEY=1514209135"+"&driver_id="+strDriverId;
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

            setPolyline();

            if (netConnection) {

                if (nodata) {

                    MessageToast.showToastMethod(BookingActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {
                        img_exclamation.setVisibility(View.GONE);
                        tv_estimate.setText("Note: This is an approximate estimate, Actual cost may be different due to traffic and waiting time");
                        ll_fare.setVisibility(View.VISIBLE);
                        tv_fare.setText(strFare);
                    } else {
                        MessageToast.showToastMethod(BookingActivity.this, message);
                    }

                }
            } else {

                MessageToast.showToastMethod(BookingActivity.this, getString(R.string.check_net_connection));

            }

        }

    }


    private void showRideConfirmDialog() {

        final Dialog dialog = new Dialog(BookingActivity.this);
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
        img_ride_confirm=dialog.findViewById(R.id.img_ride_confirm);

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

    private void openPaymentDialog() {
        final Dialog dialog = new Dialog(BookingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        dialog.setContentView(R.layout.open_payment_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        final LinearLayout ll_payment,ll_wallet,wallet_layout,ll_card,ll_cash;
        ImageView wallet_button,img_cancel;

        ll_card=dialog.findViewById(R.id.ll_card);
        img_cancel=dialog.findViewById(R.id.img_cancel);
        ll_payment=dialog.findViewById(R.id.ll_payment);
        ll_wallet=dialog.findViewById(R.id.ll_wallet);
        wallet_layout=dialog.findViewById(R.id.wallet_layout);
        wallet_button=dialog.findViewById(R.id.wallet_button);
        ll_cash=dialog.findViewById(R.id.ll_cash);


        ll_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BookingActivity.this, RideCompletedActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        ll_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(BookingActivity.this, PaymentActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });

        wallet_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_payment.setVisibility(View.GONE);
                ll_wallet.setVisibility(View.VISIBLE);
            }
        });

        wallet_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(BookingActivity.this, PaymentActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });

        dialog.show();
    }

    private void openPromoDialog() {
        prmo_dialog = new Dialog(BookingActivity.this);
        prmo_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        prmo_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = prmo_dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        prmo_dialog.setContentView(R.layout.open_promo_dialog);
        prmo_dialog.setCanceledOnTouchOutside(true);
        prmo_dialog.setCancelable(true);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        ImageView promo_button,img_cancel,img_clear_code;

        img_clear_code=prmo_dialog.findViewById(R.id.img_clear_code);
        edtPromoCode=prmo_dialog.findViewById(R.id.edtPromoCode);
        img_cancel=prmo_dialog.findViewById(R.id.img_cancel);
        promo_button=prmo_dialog.findViewById(R.id.promo_button);

        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prmo_dialog.dismiss();
            }
        });

        img_clear_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edtPromoCode.setText("");

            }
        });

        promo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                strPromoCode = edtPromoCode.getText().toString();

                if (strPromoCode.isEmpty()) {
                    Toast.makeText(BookingActivity.this, R.string.empty_promocode, Toast.LENGTH_SHORT).show();
                } else {
                    new applyPromocode().execute();
                }

            }
        });

        prmo_dialog.show();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(strFromLat), Double.valueOf(strFromLong)), 16));
      //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(strDropLat), Double.valueOf(strDropLong)), 16));


       /* mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                String lat = String.valueOf(cameraPosition.target.latitude);
                String log = String.valueOf(cameraPosition.target.longitude);

                if (!lat.equalsIgnoreCase(strFromLat) && !log.equalsIgnoreCase(strFromLong)) {
                    strFromLat = String.valueOf(cameraPosition.target.latitude);
                    strFromLong = String.valueOf(cameraPosition.target.longitude);
                    strFromAddress = MapUtil.getLatLongToAddress(cameraPosition.target.latitude, cameraPosition.target.longitude, BookingActivity.this);
                    tv_from_address.setText(strFromAddress);
                }

            }
        });*/

    }

    private class applyPromocode extends AsyncTask<Void, Void, Void> {

        String response = null;
        boolean status;
        String message, message_ar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(BookingActivity.this, getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();
                try {

                    String finalUrl = ApiUrl.strBaseUrl+"check_promocode?user_id=" + strUserId + "&coupon_code=" + strPromoCode + "&API-KEY=1514209135";
                    Log.e("FinalUrl", finalUrl);
                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(finalUrl, ServiceHandler.GET, json);


                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");

                    Log.e("PromoCode", response.toString());
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

                    MessageToast.showToastMethod(BookingActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {

                        strParamCode = edtPromoCode.getText().toString();
                        MessageToast.showToastMethod(BookingActivity.this, message);

                        prmo_dialog.dismiss();
                    } else {
                        MessageToast.showToastMethod(BookingActivity.this, message);
                    }

                }
            } else {

                MessageToast.showToastMethod(BookingActivity.this, getString(R.string.check_net_connection));

            }

        }

    }

}
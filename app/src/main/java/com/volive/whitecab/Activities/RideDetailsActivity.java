package com.volive.whitecab.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.volive.whitecab.util.DirectionsJSONParser;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class RideDetailsActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    ImageView back_ride_details;
    TextView tv_trip_id,tv_trip_date,tvFromAddress,tvDestAddress,tv_trip_startTime,tv_trip_endTime,
            tv_totalAmount,tv_finalAmount,tv_discountAmount,tv_captain_name,tv_ride_distance,tv_vehicle_number,tv_vehicle_name, tv_captainRating;
    Double fromLat,fromLong,destLat,destLong;
    RatingBar customerRating;
    CircleImageView captain_profile;
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private Marker fromMarker;
    private Marker destinationMarker;
    private static final float ANCHOR_VALUE = 0.5f;
    private Polyline polyline1,polyline2;
    private static final int POLYLINE_WIDTH = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_details);

        initUI();
        initViews();

    }

    private void initUI() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.ride_details_map);
        mapFragment.getMapAsync(RideDetailsActivity.this);


        back_ride_details=findViewById(R.id.back_ride_details);
        tv_trip_id=findViewById(R.id.tv_trip_id);
        tv_trip_date=findViewById(R.id.tv_trip_date);
        tvFromAddress=findViewById(R.id.tvFromAddress);
        tv_trip_startTime=findViewById(R.id.tv_trip_startTime);
        tvDestAddress=findViewById(R.id.tvDestAddress);
        tv_trip_endTime=findViewById(R.id.tv_trip_endTime);
        tv_totalAmount=findViewById(R.id.tv_totalAmount);
        tv_discountAmount=findViewById(R.id.tv_discountAmount);
        tv_finalAmount=findViewById(R.id.tv_finalAmount);
        customerRating =findViewById(R.id.captain_rating);
        tv_captain_name=findViewById(R.id.tv_captain_name);
        tv_ride_distance=findViewById(R.id.tv_ride_distance);
        tv_vehicle_number=findViewById(R.id.tv_vehicle_number);
        tv_vehicle_name=findViewById(R.id.tv_vehicle_name);
        tv_captainRating =findViewById(R.id.tv_rating);
        captain_profile=findViewById(R.id.captain_profile);

    }

    private void initViews() {
        back_ride_details.setOnClickListener(this);

        if(getIntent().getExtras() != null){

          String trip_id=  getIntent().getStringExtra("trip_id");
          String trip_date= getIntent().getStringExtra("trip_date");
          String from_address= getIntent().getStringExtra("from_address");
          String dest_address= getIntent().getStringExtra("dest_address");
          String from_lat= getIntent().getStringExtra("from_lat");
          String from_long= getIntent().getStringExtra("from_long");
          String dest_lat= getIntent().getStringExtra("dest_lat");
          String dest_long= getIntent().getStringExtra("dest_long");

          fromLat=Double.valueOf(from_lat);
          fromLong=Double.valueOf(from_long);
          destLat=Double.valueOf(dest_lat);
          destLong=Double.valueOf(dest_long);

          String fare= getIntent().getStringExtra("fare");
          String payment_type= getIntent().getStringExtra("payment_type");
          String captain_rating= getIntent().getStringExtra("captain_rating");
          String user_rating=getIntent().getStringExtra("user_rating");
          String trip_distance= getIntent().getStringExtra("trip_distance");
          String vehicle_number= getIntent().getStringExtra("vehicle_number");
          String vehicle_type= getIntent().getStringExtra("vehicle_type");
          String trip_start_time= getIntent().getStringExtra("trip_start_time");
          String trip_end_time= getIntent().getStringExtra("trip_end_time");
          String driver_name=getIntent().getStringExtra("driver_name");
          String driver_email=getIntent().getStringExtra("driver_email");
          String driver_profile_pic=getIntent().getStringExtra("driver_profile_pic");
          String discount_price=getIntent().getStringExtra("discount_price");
          String final_amount=getIntent().getStringExtra("final_amount");


          if(driver_profile_pic.isEmpty()){

          }else {
              Glide.with(RideDetailsActivity.this).load(driver_profile_pic).into(captain_profile);
          }
         String text="<b> <font color=#000>"+getResources().getString(R.string.trip_id)+"</font> </b>"+" "+trip_id;
         tv_trip_id.setText(Html.fromHtml(text));
         tv_trip_date.setText(trip_date);
         tvFromAddress.setText(from_address);
         tvDestAddress.setText(dest_address);
         tv_trip_startTime.setText(trip_start_time);
         tv_trip_endTime.setText(trip_end_time);
         tv_totalAmount.setText(fare+" CAD");
         tv_discountAmount.setText(discount_price+" CAD");
         tv_finalAmount.setText(final_amount+" CAD");
         customerRating.setRating(Float.valueOf(user_rating));
         tv_captain_name.setText(driver_name);
         tv_vehicle_number.setText(vehicle_number);
         tv_vehicle_name.setText(vehicle_type);

         if(trip_distance.isEmpty()){
             tv_ride_distance.setText("0 KM");
         }else {
             tv_ride_distance.setText(trip_distance+" KM");
         }

         if(captain_rating.isEmpty()){
             tv_captainRating.setText("0");
         }else {
             tv_captainRating.setText(captain_rating);
         }


        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_ride_details:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(fromLat, fromLong), 8));

        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(fromLat, fromLong), 10));


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                setPolyline();

            }
        },1000);

    }

    private void setPolyline() {
        final LatLng fromLatLng = new LatLng(fromLat, fromLong);
        final LatLng destLatLng = new LatLng(destLat, destLong);

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


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

package com.volive.whitecab.util;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import com.volive.whitecab.Activities.TrackingActivity;
import com.volive.whitecab.R;
import org.json.JSONObject;
import java.util.HashMap;

/**
 * Created by volive on 9/7/2018.
 */

public class ApplicationLifecycleHandler implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {

    private static final String TAG = ApplicationLifecycleHandler.class.getSimpleName();
    private static boolean isInBackground = false;
    Boolean netConnection = false;
    Boolean nodata = false;
    NetworkConnection nw;
    SessionManager sm;
    String strUserId, strLanguage,from_address,dest_address,from_latitude,from_longitude,to_latitude,to_longitude;
    Activity act;

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {

        if (isInBackground) {
            Log.e(TAG, "app went to foreground");
            isInBackground = false;
            nw = new NetworkConnection(activity);
            sm = new SessionManager(activity);
            HashMap<String, String> userDetail = sm.getUserDetails();
            strUserId = userDetail.get(SessionManager.KEY_ID);
            strLanguage = userDetail.get(SessionManager.KEY_LANGUAGE);
            act = activity;

            if(!(activity.getClass().equals(TrackingActivity.class))){
                new currentRide().execute();
            }

        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
    }

    @Override
    public void onLowMemory() {
    }

    @Override
    public void onTrimMemory(int i) {
        if (i == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            Log.e(TAG, "app went to background");
            isInBackground = true;
        }
    }

    public class currentRide extends AsyncTask<Void, Void, Void> {

        String response = null;
        boolean status;
        String type;
        String message, message_ar, driver_id, vehicle_name, vehicle_number, driver_name, driver_mobile, trip_id, time, distance, driver_profile, driver_lat, driver_long;

        String color,avg_rating;

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
                    json.put("API-KEY", "1514209135");
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

                        if (strLanguage.equalsIgnoreCase("1")) {
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

                    MessageToast.showToastMethod(act, act.getString(R.string.no_data));

                } else {

                    if (status) {
                        Intent intent = new Intent(act, TrackingActivity.class);
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
                        act.startActivity(intent);


                    } else {

                        // MessageToast.showToastMethod(HomeScreenActivity.this, "There is no current ride now");
                    }


                }
            } else {

                MessageToast.showToastMethod(act, act.getString(R.string.check_net_connection));

            }

        }

    }
}
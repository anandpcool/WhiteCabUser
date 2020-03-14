package com.volive.whitecab.Notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.volive.whitecab.Activities.HomeActivity;
import com.volive.whitecab.Activities.RatingActivity;
import com.volive.whitecab.Activities.TrackingActivity;
import com.volive.whitecab.R;
import com.volive.whitecab.util.ApiUrl;
import com.volive.whitecab.util.Constants;
import com.volive.whitecab.util.MessageToast;
import com.volive.whitecab.util.MyApplication;
import com.volive.whitecab.util.MyObservab;
import com.volive.whitecab.util.NetworkConnection;
import com.volive.whitecab.util.ServiceHandler;
import com.volive.whitecab.util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import static com.volive.whitecab.Activities.TrackingActivity.btn_call_captain;
import static com.volive.whitecab.Activities.TrackingActivity.btn_cancel_ride;

/**
 * Created by VOLIVE on 1/16/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService implements Observer {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    SessionManager sessionManager;
    String type = "", strLanguage = "1";
    NetworkConnection nw;
    SessionManager sm;
    String strUserId;
    boolean nodata, netConnection;
    MyApplication myBase;
    MyObservab myObservab;
    Activity act;
    private NotificationUtils notificationUtils;
    int m = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e(TAG, "remoteMessage: " + remoteMessage.getFrom());
        sessionManager = new SessionManager(getApplicationContext());
        // strLanguage = sessionManager.getSingleField(SessionManager.KEY_LANGUAGE);
        myBase = (MyApplication) getApplication();
        myBase.getObserver().addObserver(this);

        if (remoteMessage == null)
            return;

        Random random = new Random();
        m = random.nextInt(9999 - 1000) + 1000;
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification());
            System.out.println("notification boby" + remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Log.e("drivcer details", remoteMessage.getData().toString());
            System.out.println("driver details" + remoteMessage.getData().toString());


            try {
                JSONObject json = new JSONObject(remoteMessage.getData());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

    }

    private void handleDataMessage(JSONObject json) {
        String message = null, title = null;
        Log.e(TAG, "push json: " + json.toString());
        System.out.println("rideontheway" + json.toString());


        try {
            type = json.getString("type");
            if (type.equalsIgnoreCase("6")) {
                message = json.getString("message");
                //notification(message);
                loadnotificationfromadmin(message);
            } else {
                if (strLanguage.equalsIgnoreCase("1")) {
                    message = json.getString("body");
                } else {
                    message = json.getString("message_ar");
                }


                if (!NotificationUtils.isAppIsInBackground(getApplicationContext()))
                {

                    System.out.println("app backfroud");

                    final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                            getApplicationContext());

                    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

                    inboxStyle.addLine(message);

                    Notification notification;
                    String CHANNEL_ID = "my_channel_01";
                    CharSequence name = getApplicationContext().getString(R.string.app_name);
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationManager notificationManager =
                            (NotificationManager)
                                    getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                        notificationManager.createNotificationChannel(mChannel);

                    }

                    notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker(message).setWhen(0)
                            .setAutoCancel(true)
                            .setChannelId(CHANNEL_ID)
                            .setContentTitle(message)
                            .setStyle(inboxStyle)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                            .setContentText(message)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .build();

                    notificationManager.notify(0, notification);
                    if (type.equalsIgnoreCase("AR")) {

                        System.out.println("json response" + json);
                        redirectingtorideontheway(json, type);
                    } else if (type.equalsIgnoreCase("RC")) {

                        redirectingtorideontheway(json, type);
                    } else if (type.equalsIgnoreCase("RS")) {
                        Log.e("jdsafsdfds","Ride Started");

                        Intent my_intent = new Intent("custom-message");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(my_intent);

                    } else if (type.equalsIgnoreCase("DC")) {
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }


    private void loadnotificationfromadmin(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());

            int m = 200;
            PendingIntent fullScreenIntent;
            // app is in foreground, broadcast the push message
            System.out.println("app backfroud");

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

            inboxStyle.addLine(message);

            Notification notification;
            String CHANNEL_ID = "my_channel_01";
            CharSequence name = getApplicationContext().getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                notificationManager.createNotificationChannel(mChannel);
            }

            notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker(message).setWhen(0)
                    .setAutoCancel(true)
                    .setChannelId(CHANNEL_ID)
                    .setContentTitle(message)
                    .setStyle(inboxStyle)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                    .setContentText(message)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();

            System.out.println("showing on top");

            notificationManager.notify(0, notification);
        }
    }

    public void redirectingtorideontheway(JSONObject bundle, String type) {
        Log.e(TAG, "redirectingtorideontheway: " + type);
        if (type.equalsIgnoreCase("AR")) {

            nw = new NetworkConnection(getApplicationContext());
            sm = new SessionManager(getApplicationContext());
            HashMap<String, String> userDetail = sm.getUserDetails();
            strUserId = userDetail.get(SessionManager.KEY_ID);
            strLanguage = userDetail.get(SessionManager.KEY_LANGUAGE);

            try {
                new currentRide().execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (type.equalsIgnoreCase("RC")) {
            Log.e(TAG, "redirectingtorideontheway: " + type);
            Intent intent = new Intent(getApplicationContext(), RatingActivity.class);
            intent.putExtra("fromscreen", "1");
            try {
                System.out.println("jsonfmkdgk" + bundle.toString());
                intent.putExtra("driver_name", bundle.getString("username"));
                intent.putExtra("trip_id", bundle.getString("trip_id"));
                intent.putExtra("driver_id", bundle.getString("driver_id"));
                intent.putExtra("driver_profile", bundle.getString("user_profile"));
                intent.putExtra("money", bundle.getString("final_fare"));
                intent.putExtra("ride_fare", bundle.getString("ride_fare"));
                intent.putExtra("rider_disc", bundle.getString("rider_disc"));
                intent.putExtra("driver_rating", bundle.getString("driver_rating"));


                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void update(Observable observable, Object o) {

    }

    public class currentRide extends AsyncTask<Void, Void, Void> {

        String response = null;
        boolean status;
        String type;
        String message, message_ar, driver_id, vehicle_name, vehicle_number, driver_name, driver_mobile, trip_id, time, distance, driver_profile, driver_lat, driver_long;

        String color, avg_rating, from_address, dest_address, from_latitude, from_longitude, to_latitude, to_longitude;

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

                        from_address = data.getString("from_address");
                        dest_address = data.getString("to_address");
                        from_latitude = data.getString("from_latitude");
                        from_longitude = data.getString("from_longitude");
                        to_latitude = data.getString("to_latitude");
                        to_longitude = data.getString("to_longitude");


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

                    MessageToast.showToastMethod(getApplicationContext(), getApplication().getString(R.string.no_data));

                } else {

                    if (status) {
                        Intent intent = new Intent(getApplicationContext(), TrackingActivity.class);
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
                        intent.putExtra("avg_rating", avg_rating);
                        intent.putExtra("color", color);

                        intent.putExtra("from_address", from_address);
                        intent.putExtra("dest_address", dest_address);
                        intent.putExtra("from_latitude", from_latitude);
                        intent.putExtra("from_longitude", from_longitude);
                        intent.putExtra("to_latitude", to_latitude);
                        intent.putExtra("to_longitude", to_longitude);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }


                }
            } else {

                MessageToast.showToastMethod(getApplicationContext(), getApplicationContext().getString(R.string.check_net_connection));

            }

        }

    }
}
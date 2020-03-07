package com.volive.whitecab.Activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.volive.whitecab.Adapters.RecyclerAdapters.MyRidesAdapter;
import com.volive.whitecab.DataModels.HistoryModel;
import com.volive.whitecab.R;
import com.volive.whitecab.util.ApiUrl;
import com.volive.whitecab.util.DialogsUtils;
import com.volive.whitecab.util.MessageToast;
import com.volive.whitecab.util.NetworkConnection;
import com.volive.whitecab.util.ServiceHandler;
import com.volive.whitecab.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MyRidesActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView back_my_rides;
    RecyclerView my_ride_recycler;
    MyRidesAdapter ridesAdapter;
    TextView tv_completed,tv_schedule,tv_cancelled;
    private ProgressDialog myDialog;
    NetworkConnection nw;
    String strLanguage="",strUserId="";
    Boolean netConnection = false;
    Boolean nodata = false;
    SessionManager sm;
    ArrayList<HistoryModel> completedArrayList,sheduledArrayList,cancelledArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rides);

        initUI();
        initViews();
    }

    private void initUI() {
        back_my_rides=findViewById(R.id.back_my_rides);
        my_ride_recycler=findViewById(R.id.my_ride_recycler);
        tv_completed=findViewById(R.id.tv_completed);
        tv_schedule=findViewById(R.id.tv_schedule);
        tv_cancelled=findViewById(R.id.tv_cancelled);

        tv_completed.setTextColor(getResources().getColor(R.color.black));
        tv_schedule.setTextColor(getResources().getColor(R.color.gray));
        tv_cancelled.setTextColor(getResources().getColor(R.color.gray));

        nw=new NetworkConnection(MyRidesActivity.this);
        sm=new SessionManager(MyRidesActivity.this);
        HashMap<String, String> userDetail = sm.getUserDetails();
        strUserId = userDetail.get(SessionManager.KEY_ID);
    }

    private void initViews() {
        back_my_rides.setOnClickListener(this);
        tv_schedule.setOnClickListener(this);
        tv_completed.setOnClickListener(this);
        tv_cancelled.setOnClickListener(this);
      /*  ridesAdapter=new MyRidesAdapter(MyRidesActivity.this);
        my_ride_recycler.setHasFixedSize(true);
        my_ride_recycler.setAdapter(ridesAdapter);*/

        completedArrayList=new ArrayList<>();
        sheduledArrayList=new ArrayList<>();
        cancelledArrayList=new ArrayList<>();
        new myRides().execute();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_my_rides:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

            case R.id.tv_completed:

                tv_completed.setTextColor(getResources().getColor(R.color.black));
                tv_schedule.setTextColor(getResources().getColor(R.color.gray));
                tv_cancelled.setTextColor(getResources().getColor(R.color.gray));

                if(completedArrayList.isEmpty()){
                    MessageToast.showToastMethod(MyRidesActivity.this, getString(R.string.no_completed_rides));
                    my_ride_recycler.setVisibility(View.GONE);
                }else {
                    my_ride_recycler.setVisibility(View.VISIBLE);
                    ridesAdapter = new MyRidesAdapter(MyRidesActivity.this, completedArrayList);
                    my_ride_recycler.setHasFixedSize(true);
                    my_ride_recycler.setAdapter(ridesAdapter);
                }

                break;


            case R.id.tv_schedule:

                tv_completed.setTextColor(getResources().getColor(R.color.gray));
                tv_schedule.setTextColor(getResources().getColor(R.color.black));
                tv_cancelled.setTextColor(getResources().getColor(R.color.gray));

                if(sheduledArrayList.isEmpty()){
                    MessageToast.showToastMethod(MyRidesActivity.this, getString(R.string.no_scheduled_rides));
                    my_ride_recycler.setVisibility(View.GONE);
                }else {
                    my_ride_recycler.setVisibility(View.VISIBLE);
                    ridesAdapter = new MyRidesAdapter(MyRidesActivity.this, sheduledArrayList);
                    my_ride_recycler.setHasFixedSize(true);
                    my_ride_recycler.setAdapter(ridesAdapter);
                }

                break;

            case R.id.tv_cancelled:

                tv_completed.setTextColor(getResources().getColor(R.color.gray));
                tv_schedule.setTextColor(getResources().getColor(R.color.gray));
                tv_cancelled.setTextColor(getResources().getColor(R.color.black));

                if(cancelledArrayList.isEmpty()){
                    MessageToast.showToastMethod(MyRidesActivity.this, getString(R.string.no_cancelled_rides));
                    my_ride_recycler.setVisibility(View.GONE);
                }else {
                    my_ride_recycler.setVisibility(View.VISIBLE);
                    ridesAdapter = new MyRidesAdapter(MyRidesActivity.this, cancelledArrayList);
                    my_ride_recycler.setHasFixedSize(true);
                    my_ride_recycler.setAdapter(ridesAdapter);
                }

                break;

        }
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private class myRides extends AsyncTask<Void,Void,Void>{

        String response = null;
        boolean status;
        String message, message_ar, base_url;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(MyRidesActivity.this, getString(R.string.please_wait));
        }


        @Override
        protected Void doInBackground(Void... voids) {
            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();
                try {

                    String finalUrl = ApiUrl.strBaseUrl+"my_rides?" + "API-KEY=1514209135" + "&customer_id=" + strUserId;
                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(finalUrl, ServiceHandler.GET, json);


                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");
                    base_url= js.getString("base_url");

                    Log.e("history", response.toString());
//                    message_ar = js.getString("message_ar");


                    if (status) {

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }

                        completedArrayList.clear();
                        cancelledArrayList.clear();
                        sheduledArrayList.clear();

                        JSONArray jsonArray = js.getJSONArray("cancelled");
                        JSONArray completedJsonArray = js.getJSONArray("completed");
                        JSONArray sheduledJsonArray = js.getJSONArray("sheduled");

                        if(jsonArray != null && jsonArray.length() > 0){
                            for (int i = 0; i < jsonArray.length(); i++) {

                                String id = jsonArray.getJSONObject(i).getString("id");
                                String trip_id = jsonArray.getJSONObject(i).getString("trip_id");
                                String trip_date=jsonArray.getJSONObject(i).getString("trip_date");
                                String from_address = jsonArray.getJSONObject(i).getString("from_address");
                                String from_latitude = jsonArray.getJSONObject(i).getString("from_latitude");
                                String from_longitude = jsonArray.getJSONObject(i).getString("from_longitude");
                                String to_address = jsonArray.getJSONObject(i).getString("to_address");
                                String to_latitude = jsonArray.getJSONObject(i).getString("to_latitude");
                                String to_longitude = jsonArray.getJSONObject(i).getString("to_longitude");
                                String fare = jsonArray.getJSONObject(i).getString("fare");
                             //   String trip_status = jsonArray.getJSONObject(i).getString("trip_status");
                             //   String trip_status_ar = jsonArray.getJSONObject(i).getString("trip_status_ar");
                                String captain_rating=jsonArray.getJSONObject(i).getString("captain_rating");
                                String user_rating=jsonArray.getJSONObject(i).getString("user_rating");
                                String promo_code=jsonArray.getJSONObject(i).getString("promo_code");
                                String payment_type=jsonArray.getJSONObject(i).getString("payment_type");
                                String trip_distance=jsonArray.getJSONObject(i).getString("trip_distance");
                                String trip_start_time=jsonArray.getJSONObject(i).getString("start_time");
                                String trip_end_time=jsonArray.getJSONObject(i).getString("end_time");
                                String vehicle_number=jsonArray.getJSONObject(i).getString("vehicle_number");
                                String vehicle_type=jsonArray.getJSONObject(i).getString("vehicle_type");
                                String driver_name= jsonArray.getJSONObject(i).getString("driver_name");
                                String driver_email= jsonArray.getJSONObject(i).getString("driver_email");
                                String driver_profile_pic= base_url+jsonArray.getJSONObject(i).getString("driver_profile_pic");
                                String discount_price=jsonArray.getJSONObject(i).getString("discount_price");
                                String final_amount= jsonArray.getJSONObject(i).getString("final_amount");

                                HistoryModel model = new HistoryModel(id, trip_id, from_address, from_latitude,
                                        from_longitude, to_address, to_latitude, to_longitude, fare,
                                        captain_rating,promo_code,payment_type,trip_distance,vehicle_number,vehicle_type,
                                        trip_start_time,trip_end_time,driver_name,driver_email,driver_profile_pic,discount_price,final_amount,user_rating,trip_date);


                                cancelledArrayList.add(model);
                            }
                        }

                        if(sheduledJsonArray != null && sheduledJsonArray.length() > 0){
                            for (int i = 0; i < sheduledJsonArray.length(); i++) {

                                String id = sheduledJsonArray.getJSONObject(i).getString("id");
                                String trip_id = sheduledJsonArray.getJSONObject(i).getString("trip_id");
                                String trip_date=sheduledJsonArray.getJSONObject(i).getString("trip_date");
                                String from_address = sheduledJsonArray.getJSONObject(i).getString("from_address");
                                String from_latitude = sheduledJsonArray.getJSONObject(i).getString("from_latitude");
                                String from_longitude = sheduledJsonArray.getJSONObject(i).getString("from_longitude");
                                String to_address = sheduledJsonArray.getJSONObject(i).getString("to_address");
                                String to_latitude = sheduledJsonArray.getJSONObject(i).getString("to_latitude");
                                String to_longitude = sheduledJsonArray.getJSONObject(i).getString("to_longitude");
                                String fare = sheduledJsonArray.getJSONObject(i).getString("fare");
                             //   String trip_status = sheduledJsonArray.getJSONObject(i).getString("trip_status");
                               // String trip_status_ar = sheduledJsonArray.getJSONObject(i).getString("trip_status_ar");
                                String captain_rating=sheduledJsonArray.getJSONObject(i).getString("captain_rating");
                                String user_rating=sheduledJsonArray.getJSONObject(i).getString("user_rating");
                                String promo_code=sheduledJsonArray.getJSONObject(i).getString("promo_code");
                                String payment_type=sheduledJsonArray.getJSONObject(i).getString("payment_type");
                                String trip_distance=sheduledJsonArray.getJSONObject(i).getString("trip_distance");
                                String trip_start_time=sheduledJsonArray.getJSONObject(i).getString("start_time");
                                String trip_end_time=sheduledJsonArray.getJSONObject(i).getString("end_time");
                                String vehicle_number=sheduledJsonArray.getJSONObject(i).getString("vehicle_number");
                                String vehicle_type=sheduledJsonArray.getJSONObject(i).getString("vehicle_type");
                                String driver_name= sheduledJsonArray.getJSONObject(i).getString("driver_name");
                                String driver_email= sheduledJsonArray.getJSONObject(i).getString("driver_email");
                                String driver_profile_pic= base_url+sheduledJsonArray.getJSONObject(i).getString("driver_profile_pic");
                                String discount_price=sheduledJsonArray.getJSONObject(i).getString("discount_price");
                                String final_amount= sheduledJsonArray.getJSONObject(i).getString("final_amount");

                                HistoryModel model = new HistoryModel(id, trip_id, from_address, from_latitude,
                                        from_longitude, to_address, to_latitude, to_longitude, fare,
                                        captain_rating,promo_code,payment_type,trip_distance,vehicle_number,vehicle_type,
                                        trip_start_time,trip_end_time,driver_name,driver_email,driver_profile_pic,discount_price,final_amount,user_rating,trip_date);

                                sheduledArrayList.add(model);
                            }
                        }


                        if(completedJsonArray != null && completedJsonArray.length() > 0){
                            for (int i = 0; i < completedJsonArray.length(); i++) {

                                String id = completedJsonArray.getJSONObject(i).getString("id");
                                String trip_id = completedJsonArray.getJSONObject(i).getString("trip_id");
                                String trip_date=completedJsonArray.getJSONObject(i).getString("trip_date");
                                String from_address = completedJsonArray.getJSONObject(i).getString("from_address");
                                String from_latitude = completedJsonArray.getJSONObject(i).getString("from_latitude");
                                String from_longitude = completedJsonArray.getJSONObject(i).getString("from_longitude");
                                String to_address = completedJsonArray.getJSONObject(i).getString("to_address");
                                String to_latitude = completedJsonArray.getJSONObject(i).getString("to_latitude");
                                String to_longitude = completedJsonArray.getJSONObject(i).getString("to_longitude");
                               // String req_time = completedJsonArray.getJSONObject(i).getString("req_time");
                                String fare = completedJsonArray.getJSONObject(i).getString("fare");
                               // String trip_status = completedJsonArray.getJSONObject(i).getString("trip_status");
                               // String trip_status_ar = completedJsonArray.getJSONObject(i).getString("trip_status_ar");
                                String captain_rating=completedJsonArray.getJSONObject(i).getString("captain_rating");
                                String user_rating=completedJsonArray.getJSONObject(i).getString("user_rating");
                                String promo_code=completedJsonArray.getJSONObject(i).getString("promo_code");
                                String payment_type=completedJsonArray.getJSONObject(i).getString("payment_type");
                                String trip_distance=completedJsonArray.getJSONObject(i).getString("trip_distance");
                                String trip_start_time=completedJsonArray.getJSONObject(i).getString("start_time");
                                String trip_end_time=completedJsonArray.getJSONObject(i).getString("end_time");
                                String vehicle_number=completedJsonArray.getJSONObject(i).getString("vehicle_number");
                                String vehicle_type=completedJsonArray.getJSONObject(i).getString("vehicle_type");
                                String driver_name= completedJsonArray.getJSONObject(i).getString("driver_name");
                                String driver_email= completedJsonArray.getJSONObject(i).getString("driver_email");
                                String driver_profile_pic= base_url+completedJsonArray.getJSONObject(i).getString("driver_profile_pic");
                                String discount_price=completedJsonArray.getJSONObject(i).getString("discount_price");
                                String final_amount= completedJsonArray.getJSONObject(i).getString("final_amount");

                                HistoryModel model = new HistoryModel(id, trip_id, from_address, from_latitude,
                                        from_longitude, to_address, to_latitude, to_longitude, fare,
                                        captain_rating,promo_code,payment_type,trip_distance,vehicle_number,vehicle_type,
                                        trip_start_time,trip_end_time,driver_name,driver_email,driver_profile_pic,discount_price,final_amount,user_rating,trip_date);

                                completedArrayList.add(model);
                            }
                        }

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

                    MessageToast.showToastMethod(MyRidesActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {

                        if(completedArrayList.isEmpty()){
                            MessageToast.showToastMethod(MyRidesActivity.this, getString(R.string.no_completed_rides));
                            my_ride_recycler.setVisibility(View.GONE);
                        }else {
                            my_ride_recycler.setVisibility(View.VISIBLE);
                            ridesAdapter = new MyRidesAdapter(MyRidesActivity.this, completedArrayList);
                            my_ride_recycler.setHasFixedSize(true);
                            my_ride_recycler.setAdapter(ridesAdapter);
                        }

                    } else {
                        MessageToast.showToastMethod(MyRidesActivity.this, message);
                    }

                }
            } else {

                MessageToast.showToastMethod(MyRidesActivity.this, getString(R.string.check_net_connection));

            }

        }


    }
}

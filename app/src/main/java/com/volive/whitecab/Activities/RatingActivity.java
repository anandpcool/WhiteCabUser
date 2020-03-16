package com.volive.whitecab.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Rating;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.volive.whitecab.R;
import com.volive.whitecab.util.ApiUrl;
import com.volive.whitecab.util.Constants;
import com.volive.whitecab.util.DialogsUtils;
import com.volive.whitecab.util.MessageToast;
import com.volive.whitecab.util.NetworkConnection;
import com.volive.whitecab.util.ServiceHandler;
import com.volive.whitecab.util.SessionManager;

import org.json.JSONObject;

import java.util.HashMap;

public class RatingActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView back_rating,img_captain_profile;
    Button btn_submit;
    TextView txt_captain_name,txt_captain_rating,txt_rate_value;
    RatingBar customer_rating;
    EditText et_rideComment;
    String driver_id,driver_name,driver_profile,driver_rating,trip_id,review,strLanguage="",strUserId;
    private float rating;
    NetworkConnection nw;
    private boolean netConnection1 = false;
    private boolean nodata1 = false;
    SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        initUI();
        initViews();

    }

    private void initUI() {
        sm = new SessionManager(RatingActivity.this);
        nw = new NetworkConnection(RatingActivity.this);
        HashMap<String, String> userDetail = sm.getUserDetails();
        strUserId = userDetail.get(SessionManager.KEY_ID);
        back_rating=findViewById(R.id.back_rating);
        btn_submit=findViewById(R.id.btn_submit);
        img_captain_profile=findViewById(R.id.img_captain_profile);
        txt_captain_name=findViewById(R.id.txt_captain_name);
        txt_captain_rating=findViewById(R.id.txt_captain_rating);
        customer_rating=findViewById(R.id.customer_rating);
        txt_rate_value=findViewById(R.id.txt_rate_value);
        et_rideComment=findViewById(R.id.et_rideComment);
    }

    private void initViews() {
        back_rating.setOnClickListener(this);
        btn_submit.setOnClickListener(this);

        if(getIntent().getExtras()!=null){
            driver_id= getIntent().getStringExtra("driver_id");
            driver_name= getIntent().getStringExtra("username");
            driver_profile= getIntent().getStringExtra("user_profile");
            driver_rating= getIntent().getStringExtra("driver_rating");
            trip_id= getIntent().getStringExtra("trip_id");

            txt_captain_name.setText(driver_name);
            if(driver_rating.isEmpty()){
                txt_captain_rating.setText("0");
            }else {
                txt_captain_rating.setText(driver_rating);
            }

            Glide.with(RatingActivity.this).load(driver_profile).into(img_captain_profile);
        }

        customer_rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                if(ratingBar.getRating() == 0){
                    txt_rate_value.setText("Bad");
                } else if(ratingBar.getRating() == 1){
                    txt_rate_value.setText("Okay!");
                }else if(ratingBar.getRating() == 2){
                    txt_rate_value.setText("Good");
                }else if(ratingBar.getRating() == 3){
                    txt_rate_value.setText("Nice");
                }else if(ratingBar.getRating() == 4){
                    txt_rate_value.setText("Excellent");
                }else if(ratingBar.getRating() == 5){
                    txt_rate_value.setText("Awesome");
                }

            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_rating:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

            case R.id.btn_submit:

                rating = customer_rating.getRating();
                review = et_rideComment.getText().toString();

                new reviewRating().execute();

                break;
        }

    }

    private class reviewRating extends AsyncTask<Void, Void, Void> {
        String response = null;
        boolean status;
        String message, message_ar;
        private ProgressDialog myDialog1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog1 = DialogsUtils.showProgressDialog(RatingActivity.this, getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();
                try {
                    json.put("API-KEY", Constants.API_KEY);
                    json.put("driver_id", driver_id);
                    json.put("user_id", strUserId);
                    json.put("trip_id", trip_id);
                    json.put("review", review);
                    json.put("rating", String.valueOf(rating));

                    ServiceHandler sh = new ServiceHandler();
                    System.out.println("rating to driver" + json.toString());
                    response = sh.callToServer(ApiUrl.strBaseUrl + ApiUrl.strReviewRating, ServiceHandler.POST, json);

                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    Log.e("reviewrating", response.toString());

                    if (status) {
                        //write
//                        message = js.getString("message");
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
                        //message_ar=js.getString("message_ar");
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
            if (myDialog1.isShowing())
                myDialog1.dismiss();

            if (netConnection1) {

                if (nodata1) {
                    MessageToast.showToastMethod(RatingActivity.this, getString(R.string.no_data));
                } else {
                    if (status) {
                        MessageToast.showToastMethod(RatingActivity.this, message);

                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        finish();

                    } else {

                        MessageToast.showToastMethod(RatingActivity.this, message);
                    }

                }
            } else {
                MessageToast.showToastMethod(RatingActivity.this, getString(R.string.check_net_connection));
            }

        }

    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

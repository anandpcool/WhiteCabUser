package com.volive.whitecab.Activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.volive.whitecab.R;
import com.volive.whitecab.util.ApiUrl;
import com.volive.whitecab.util.DialogsUtils;
import com.volive.whitecab.util.GPSTracker;
import com.volive.whitecab.util.MapUtil;
import com.volive.whitecab.util.MessageToast;
import com.volive.whitecab.util.NetworkConnection;
import com.volive.whitecab.util.PreferenceUtils;
import com.volive.whitecab.util.ServiceHandler;

import org.json.JSONObject;

public class BookingActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    ImageView back_booking;
    TextView tv_dropoff;
    Button btn_select_drop_off;
    ImageView img_booking_map,img_exclamation,img_current_loc;
    TextView tv_estimate,tv_fare;
    LinearLayout ll_fare,linear_cash,linear_promo,ll_ride_time;
    PreferenceUtils preferenceUtils;
    EditText et_drop_location,et_from_address;
    TextInputLayout input_destination;

    int nSearchResultCode = 000;
    int nDropLocationResultCode = 00111;
    private GoogleMap mMap;
    GPSTracker gpsTracker;
    SupportMapFragment mapFragment;
    String strLat = "", strLong = "", strAddress = "",strDropAddress = "", strDropLat = "", strDropLong = "",strLatitude, strLongitute,strLanguage,strFare;
    ProgressDialog myDialog;
    NetworkConnection nw;
    Boolean netConnection = false;
    Boolean nodata = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        initUI();
        initViews();

    }

    private void initUI() {
        back_booking=findViewById(R.id.back_booking);
        tv_dropoff=findViewById(R.id.tv_dropoff);
        btn_select_drop_off=findViewById(R.id.btn_select_drop_off);
        img_booking_map=findViewById(R.id.img_booking_map);
        tv_estimate=findViewById(R.id.tv_estimate);
        img_exclamation=findViewById(R.id.img_exclamation);
        ll_fare=findViewById(R.id.ll_fare);
        linear_cash=findViewById(R.id.linear_cash);
        linear_promo=findViewById(R.id.linear_promo);
        ll_ride_time=findViewById(R.id.ll_ride_time);
        et_drop_location=findViewById(R.id.et_drop_location);
        input_destination=findViewById(R.id.input_destination);
        et_from_address=findViewById(R.id.et_from_address);
        img_current_loc=findViewById(R.id.img_current_loc);
        tv_fare=findViewById(R.id.tv_fare);
        preferenceUtils=new PreferenceUtils(BookingActivity.this);
        gpsTracker=new GPSTracker(BookingActivity.this);
        nw=new NetworkConnection(BookingActivity.this);
    }

    private void initViews() {
        back_booking.setOnClickListener(this);
        tv_dropoff.setOnClickListener(this);
        btn_select_drop_off.setOnClickListener(this);
        linear_promo.setOnClickListener(this);
        linear_cash.setOnClickListener(this);
        img_current_loc.setOnClickListener(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.booking_map);
        mapFragment.getMapAsync(BookingActivity.this);

        try {
            strLat = String.valueOf(gpsTracker.getLatitude());
            strLong = String.valueOf(gpsTracker.getLongitude());
            strAddress = MapUtil.getLatLongToAddress(gpsTracker.getLatitude(), gpsTracker.getLongitude(), this);
            et_from_address.setText(strAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(getIntent().getExtras() !=null){

            if(preferenceUtils.getRideType().equalsIgnoreCase("Ride Later")){

                img_exclamation.setVisibility(View.GONE);
                tv_dropoff.setVisibility(View.GONE);
                ll_fare.setVisibility(View.VISIBLE);
                input_destination.setVisibility(View.VISIBLE);
                img_booking_map.setImageDrawable(getResources().getDrawable(R.drawable.booking_map2));
                tv_estimate.setText("Note: This is an approximate estimate, Actual cost may be different due to traffic and waiting time");
                ll_ride_time.setVisibility(View.VISIBLE);
                btn_select_drop_off.setText(getString(R.string.confirm_booking));

            }else if(getIntent().getStringExtra("key").equalsIgnoreCase("dropoff")){

                String to_address=getIntent().getStringExtra("to_address");
                et_drop_location.setText(to_address);

                btn_select_drop_off.setText(getString(R.string.book_now));
                img_exclamation.setVisibility(View.GONE);
                tv_dropoff.setVisibility(View.GONE);
                ll_fare.setVisibility(View.VISIBLE);
                input_destination.setVisibility(View.VISIBLE);
                img_booking_map.setImageDrawable(getResources().getDrawable(R.drawable.booking_map2));
                tv_estimate.setText("Note: This is an approximate estimate, Actual cost may be different due to traffic and waiting time");

            }

        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.img_current_loc:

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()), 16));

                strAddress = MapUtil.getLatLongToAddress(gpsTracker.getLatitude(), gpsTracker.getLongitude(), BookingActivity.this);

                et_from_address.setText(strAddress);
                strLat = String.valueOf(gpsTracker.getLatitude());
                strLong = String.valueOf(gpsTracker.getLongitude());

                break;

            case R.id.back_booking:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                break;

            case R.id.tv_dropoff:

                Intent intent=new Intent(BookingActivity.this, DropOffActivity.class);
                intent.putExtra("from_address", et_from_address.getText().toString());
                //startActivity(intent);
                startActivityForResult(intent, nDropLocationResultCode);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                break;

            case R.id.btn_select_drop_off:

                if(btn_select_drop_off.getText().toString().equalsIgnoreCase(getString(R.string.book_now))){
                    startActivity(new Intent(BookingActivity.this, TrackingActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }else if(btn_select_drop_off.getText().toString().equalsIgnoreCase(getString(R.string.confirm_booking))){
                      showRideConfirmDialog();
                } else  {
                    Intent intent1=new Intent(BookingActivity.this, DropOffActivity.class);
                    intent1.putExtra("from_address", et_from_address.getText().toString());
                    //startActivity(intent1);
                    startActivityForResult(intent1, nDropLocationResultCode);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK){

            if (requestCode == nSearchResultCode){

            }else if (requestCode == nDropLocationResultCode) {

                btn_select_drop_off.setText(getString(R.string.book_now));

                input_destination.setVisibility(View.VISIBLE);
                tv_dropoff.setVisibility(View.GONE);

                strDropAddress = data.getStringExtra("DropAddress");
                strDropLat = data.getStringExtra("Droplatitude");
                strDropLong = data.getStringExtra("Droplongitute");
                et_drop_location.setText(strDropAddress);

                new getFare().execute();
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

                    String finalUrl = ApiUrl.strBaseUrl+"calc_fare?from_lat=" + strLatitude + "&from_long=" + strLongitute + "&to_lat=" + strDropLat + "&to_long=" + strDropLong + "&API-KEY=1514209135";
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

            if (netConnection) {

                if (nodata) {

                    MessageToast.showToastMethod(BookingActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {
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
        final Dialog dialog = new Dialog(BookingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        dialog.setContentView(R.layout.open_promo_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        ImageView promo_button,img_cancel;

        img_cancel=dialog.findViewById(R.id.img_cancel);
        promo_button=dialog.findViewById(R.id.promo_button);

        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        promo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               dialog.dismiss();
            }
        });

        dialog.show();
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()), 16));



        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                String lat = String.valueOf(cameraPosition.target.latitude);
                String log = String.valueOf(cameraPosition.target.longitude);

                if (!lat.equalsIgnoreCase(strLat) && !log.equalsIgnoreCase(strLong)) {
                    strLat = String.valueOf(cameraPosition.target.latitude);
                    strLong = String.valueOf(cameraPosition.target.longitude);
                    strAddress = MapUtil.getLatLongToAddress(cameraPosition.target.latitude, cameraPosition.target.longitude, BookingActivity.this);
                    et_from_address.setText(strAddress);
                }

            }
        });

    }
}
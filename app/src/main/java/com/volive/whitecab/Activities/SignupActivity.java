package com.volive.whitecab.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.iid.FirebaseInstanceId;
import com.volive.whitecab.Adapters.RecyclerAdapters.RegionAdapter;
import com.volive.whitecab.DataModels.RegionModel;
import com.volive.whitecab.R;
import com.volive.whitecab.util.AdapterCallBack;
import com.volive.whitecab.util.ApiUrl;
import com.volive.whitecab.util.Constants;
import com.volive.whitecab.util.DialogsUtils;
import com.volive.whitecab.util.GPSTracker;
import com.volive.whitecab.util.MessageToast;
import com.volive.whitecab.util.NetworkConnection;
import com.volive.whitecab.util.ServiceHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener, AdapterCallBack {

    EditText et_your_name,et_email,et_phone_number,etPassword,et_con_password;
    TextView txt_show,txt_show_con,tv_login,tv_terms_conditions;
    public static TextView txt_countryCode;
    Button btn_sign_up;
    ImageView back_sign_up,selectdrop_down,img_country;
    private ProgressDialog myDialog;
    NetworkConnection nw;
    Boolean netConnection = false;
    Boolean nodata = false;
    CheckBox checkbox;
    String strLanguage="1",user_id,otp,phone,firebase_token="";
    Dialog dialog;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ArrayList<RegionModel> regionModels = new ArrayList<>();
    ArrayList<String> array_regionid = new ArrayList<>();
    ArrayList<String> array_country_code = new ArrayList<>();
    String selectlang="",strPhoneCode = "";
    AdapterCallBack adapterCallBack;
    GPSTracker gpsTracker;
    String country_ids="",code = "",countryid = "";
    private String dialing_code="",country_imagge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

//        firebase_token = FirebaseInstanceId.getInstance().getToken();
//        Log.e("firebase_token", firebase_token);

        initUI();
        initViews();

    }

    private void initUI() {
        et_your_name=findViewById(R.id.et_your_name);
        et_email=findViewById(R.id.et_email);
        et_phone_number=findViewById(R.id.et_phone_number);
        etPassword = findViewById(R.id.etPassword);
        et_con_password=findViewById(R.id.et_con_password);
        txt_show = findViewById(R.id.txt_show);
        txt_show_con=findViewById(R.id.txt_show_con);
        back_sign_up=findViewById(R.id.back_sign_up);
        btn_sign_up=findViewById(R.id.btn_sign_up);
        tv_login=findViewById(R.id.tv_login);
        checkbox=findViewById(R.id.checkbox);
        tv_terms_conditions=findViewById(R.id.tv_terms_conditions);
        selectdrop_down=findViewById(R.id.selectdrop_down);
        img_country=findViewById(R.id.img_country);
        txt_countryCode=findViewById(R.id.txt_countryCode);
        nw=new NetworkConnection(SignupActivity.this);
        adapterCallBack = SignupActivity.this;
        gpsTracker=new GPSTracker(SignupActivity.this);
    }

    private void initViews() {
        String text=getResources().getString(R.string.have_account)+"<u> <font color=#000>"+getResources().getString(R.string.login)+"</font></u>";
        tv_login.setText(Html.fromHtml(text));
        String terms_text=getResources().getString(R.string.signing_term)+" "+"<u> <font color=#000>"+getResources().getString(R.string.terms_service)+"</font></u>"+" "+getResources().getString(R.string.privacy_policy);
        tv_terms_conditions.setText(Html.fromHtml(terms_text));
        txt_show.setOnClickListener(this);
        txt_show_con.setOnClickListener(this);
        btn_sign_up.setOnClickListener(this);
        back_sign_up.setOnClickListener(this);
        tv_login.setOnClickListener(this);
        tv_terms_conditions.setOnClickListener(this);
        selectdrop_down.setOnClickListener(this);

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getSimCountryIso();

        Log.e("init: ", GetCountryZipCode());

        if (!GetCountryZipCode().isEmpty()) {
            strPhoneCode = GetCountryZipCode();
            String simcode = GetCountryZipCode();
            if(strPhoneCode.equalsIgnoreCase("91")){
                country_ids="IN";
            }else if(strPhoneCode.equalsIgnoreCase("966")){
                country_ids="SA";
            }else {
                country_ids="IN";
            }

            txt_countryCode.setText(strPhoneCode);

        }
        code = getCountryName(SignupActivity.this, gpsTracker.getLatitude(), gpsTracker.getLongitude());

        loadSpinner(false);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_sign_up:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

            case R.id.selectdrop_down:

                loadSpinner(true);

                break;

            case R.id.btn_sign_up:

                 String pattern = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

                if(et_your_name.getText().toString().equalsIgnoreCase("")){
                    MessageToast.showToastMethod(SignupActivity.this,getString(R.string.enter_your_name));
                }else if(et_email.getText().toString().equalsIgnoreCase("")){
                    MessageToast.showToastMethod(SignupActivity.this,getString(R.string.enter_your_mail));
                }else if(!(et_email.getText().toString().matches(pattern))){
                    MessageToast.showToastMethod(SignupActivity.this,getString(R.string.enter_valid_mail));
                }else if(txt_countryCode.getText().toString().isEmpty() || txt_countryCode.getText().toString().equals(null)){
                    MessageToast.showToastMethod(SignupActivity.this, getString(R.string.choose_country_code));
                }else if(et_phone_number.getText().toString().equalsIgnoreCase("")){
                    MessageToast.showToastMethod(SignupActivity.this,getString(R.string.enter_phone_number));
                }else if(etPassword.getText().toString().equalsIgnoreCase("")){
                    MessageToast.showToastMethod(SignupActivity.this,getString(R.string.create_your_password));
                }else if(etPassword.getText().toString().length() < 6){
                    MessageToast.showToastMethod(SignupActivity.this,getString(R.string.min_six_characters));
                }else if(et_con_password.getText().toString().equalsIgnoreCase("")){
                    MessageToast.showToastMethod(SignupActivity.this,getString(R.string.confirm_your_password));
                }else if(!(etPassword.getText().toString().equals(et_con_password.getText().toString()))){
                    MessageToast.showToastMethod(SignupActivity.this,getString(R.string.password_mismatch));
                }else if(!(checkbox.isChecked())){
                    MessageToast.showToastMethod(SignupActivity.this, getString(R.string.please_accept_terms));
                } else{
                    new Register().execute();
                }


                break;

            case R.id.txt_show:

                if(txt_show.getText().toString().equalsIgnoreCase(getString(R.string.show))){
                    txt_show.setText(getResources().getString(R.string.hide));
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    txt_show.setText(getResources().getString(R.string.show));
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

                if(etPassword.length()>0){
                    etPassword.setSelection(etPassword.length());
                }

                break;

            case R.id.txt_show_con:

                if(txt_show_con.getText().toString().equalsIgnoreCase(getString(R.string.show))){
                    txt_show_con.setText(getResources().getString(R.string.hide));
                    et_con_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    txt_show_con.setText(getResources().getString(R.string.show));
                    et_con_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

                if(et_con_password.length()>0){
                    et_con_password.setSelection(et_con_password.length());
                }

                break;

            case R.id.tv_login:

                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                break;

            case R.id.tv_terms_conditions:

                startActivity(new Intent(SignupActivity.this, TermsActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                break;

        }

    }

    @Override
    public void onClickCallback(int position) {
        dialog.dismiss();
        RegionModel regionModel = regionModels.get(position);
        txt_countryCode.setText(regionModel.getName());
        strPhoneCode = regionModel.getName();
        countryid = regionModel.getId();
        country_ids=regionModel.getId();
     //   img_country.setVisibility(View.VISIBLE);
        Glide.with(SignupActivity.this).load(regionModel.getIs_unep()).into(img_country);

    }


    private class Register extends AsyncTask<Void,Void,Void> {

        String response = null;
        int status;
        String message, message_ar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(SignupActivity.this, getString(R.string.please_wait));
          //  myDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (nw.isConnectingToInternet()){
                firebase_token = FirebaseInstanceId.getInstance().getToken();
                JSONObject json = new JSONObject();

                try{

                    json.put("API-KEY", Constants.API_KEY);
                    json.put("username", et_your_name.getText().toString());
                    json.put("email", et_email.getText().toString());
                    json.put("phone", et_phone_number.getText().toString());
                    json.put("password", etPassword.getText().toString());
                    json.put("phone_code",txt_countryCode.getText().toString());
                    json.put("country_code",countryid);
//                    json.put("device_token",firebase_token);
//                    json.put("device_name", Constants.DIVICE_TYPE);


                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(ApiUrl.strBaseUrl + ApiUrl.strUserRegister, ServiceHandler.POST, json);

                    JSONObject js = new JSONObject(response);
                    status = js.getInt("status");
                    message = js.getString("message");
                    Log.e("strVerifiyOtp", response.toString() + " : " + status);

                    if(status == 1){

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.equalsIgnoreCase("")) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }

                        JSONObject userobj = js.getJSONObject("user_info");

                        user_id = userobj.getString("user_id");
                        String username = userobj.getString("username");
                        String email = userobj.getString("email");
                        phone = userobj.getString("phone");
                        String password = userobj.getString("passwd");
                        otp=userobj.getString("otp");
                        System.out.println("mobile user_id"+user_id);

                    }else {

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.equalsIgnoreCase("")) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }

                    }

                    nodata = false;

                }catch (Exception ex) {
                    ex.printStackTrace();
                    nodata = true;
                }
                netConnection = true;
            }else {

                netConnection = false;

            }

                return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (myDialog.isShowing()){
                myDialog.dismiss();
            }

            if(netConnection){

                if(nodata){
                    MessageToast.showToastMethod(SignupActivity.this, getString(R.string.no_data));
                }else {

                    if (status == 1) {

                        Intent intent = new Intent(SignupActivity.this, VerificaionActivity.class);
                        intent.putExtra("user_id", user_id);
                        intent.putExtra("phone",phone);
                        intent.putExtra("otp", otp);
                        intent.putExtra("forgot",false);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                       /* if (isFromForgot) {

                            Intent intent = new Intent(SignupActivity.this, PasswordActivity.class);
                            intent.putExtra("isComeFromForgot", isFromForgot);
                            startActivity(intent);

                        } else {
                            Intent intent = new Intent(SignupActivity.this, HomeScreenActivity.class);
                            // intent.putExtra("strPhoneCode",strPhoneCode);
                            startActivity(intent);
                        }*/

                    } else {
                        MessageToast.showToastMethod(SignupActivity.this, message);
                    }


                }

            }

        }

    }

    private void loadSpinner(boolean b) {
        dialog = new Dialog(SignupActivity.this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.recyculer_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        recyclerView = (RecyclerView) dialog.findViewById(R.id.recycleview);
        layoutManager = new LinearLayoutManager(SignupActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        new getCountryList().execute();

        slideUp(recyclerView, true);
        if(b){
            dialog.show();
        }

    }

    public static void slideUp(View view, boolean isFill) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                view.getHeight());                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(isFill);
        view.startAnimation(animate);
    }


    private class getCountryList extends AsyncTask<Void, Void, Void> {
        String response = null;
        String imgbase_path;
        boolean status;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoader();
           // myDialog = DialogsUtils.showProgressDialog(SignupActivity.this, getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();
                try {
                    ServiceHandler sh = new ServiceHandler();

                    regionModels.clear();
                    response = sh.callToServer(ApiUrl.strBaseUrl + "country_codes?API-KEY=1514209135&lang="+selectlang, ServiceHandler.GET, json);
                    System.out.println("city : " + response);
                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    JSONObject data = js.getJSONObject("data");
                    String baseurl = data.getString("image_path");

                    JSONArray jsonArray = data.getJSONArray("countries");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject js1 = jsonArray.getJSONObject(i);
                        String id = js1.getString("country_code");
                        String name = js1.getString("dialing_code");
                        String status = js1.getString("image");
                        String imageurl = baseurl + status;

                        country_imagge=baseurl+jsonArray.getJSONObject(1).getString("image");
                        dialing_code=jsonArray.getJSONObject(1).getString("dialing_code");

                        RegionModel regionModel = new RegionModel(id, name, imageurl);

                        array_regionid.add(id);
                        array_country_code.add(name);
                        regionModels.add(regionModel);


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

                    MessageToast.showToastMethod(SignupActivity.this, getString(R.string.no_data));

                } else {
                    if (status) {
                        String image = "yes";
                        txt_countryCode.setText(dialing_code);
                        Glide.with(SignupActivity.this).load(country_imagge).into(img_country);
                        RegionAdapter regionAdapter = new RegionAdapter(SignupActivity.this, adapterCallBack, regionModels, image);
                        recyclerView.setAdapter(regionAdapter);
                    }

                }
            } else {

                MessageToast.showToastMethod(SignupActivity.this, getString(R.string.check_net_connection));

            }

        }

    }

    public String GetCountryZipCode() {
        String CountryID = "";
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                Log.e("GetCountryZipCode: ", CountryZipCode);
                break;
            }
        }
        return CountryZipCode;
    }

    public static String getCountryName(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                //  Log.e("countryname",addresses.get(0).getCountryName());
                // code=addresses.get(0).getCountryCode();
                // Log.e("code",code);
                return addresses.get(0).getCountryCode();
            }
        } catch (IOException ioe) {
        }
        return null;
    }

    // Show progress bar
    public void showLoader() {
        try {
            if (myDialog != null)
                myDialog.dismiss();
            myDialog = null;
            myDialog = new ProgressDialog(SignupActivity.this);

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


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}

package com.volive.whitecab.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.volive.whitecab.R;
import com.volive.whitecab.util.ApiUrl;
import com.volive.whitecab.util.Constants;
import com.volive.whitecab.util.DialogsUtils;
import com.volive.whitecab.util.MessageToast;
import com.volive.whitecab.util.NetworkConnection;
import com.volive.whitecab.util.ServiceHandler;
import com.volive.whitecab.util.SessionManager;

import org.json.JSONObject;

public class VerificaionActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_verify;
    ImageView back_verify;
    String strLanguage="1",user_id,otp,strOtp="",phone;
    private ProgressDialog myDialog;
    NetworkConnection nw;
    Boolean netConnection = false;
    Boolean nodata = false,forgot;
    SessionManager sm;
    EditText edt_one,edt_two,edt_three,edt_four;
    TextView tv_mobile_num,tv_resendCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificaion);

        initUI();
        initViews();

    }

    private void initUI() {
        btn_verify=findViewById(R.id.btn_verify);
        back_verify=findViewById(R.id.back_verify);
        tv_mobile_num=findViewById(R.id.tv_mobile_num);
        tv_resendCode=findViewById(R.id.tv_resendCode);

        edt_one=findViewById(R.id.edt_one);
        edt_two=findViewById(R.id.edt_two);
        edt_three=findViewById(R.id.edt_three);
        edt_four=findViewById(R.id.edt_four);

        nw=new NetworkConnection(VerificaionActivity.this);
        sm=new SessionManager(VerificaionActivity.this);

    }

    private void initViews() {
        btn_verify.setOnClickListener(this);
        back_verify.setOnClickListener(this);
        tv_resendCode.setOnClickListener(this);

        if(getIntent().getExtras() != null){
            user_id= getIntent().getStringExtra("user_id");
            otp= getIntent().getStringExtra("otp");
            phone=getIntent().getStringExtra("phone");
            forgot=getIntent().getBooleanExtra("forgot",false);
            String text=getString(R.string.on)+" "+"<b> <font color=#000>"+phone+"</font></b>";
            tv_mobile_num.setText(Html.fromHtml(text));
        }


        edt_one.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println("///count///" + s.length());
                if (s.length() > 0){
                    edt_two.requestFocus();
                }

            }
        });

        edt_two.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println("///count///" + s.length());
                if (s.length() > 0){
                    edt_three.requestFocus();
                }

            }
        });

        edt_three.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println("///count///" + s.length());
                if (s.length() > 0){
                    edt_four.requestFocus();
                }

            }
        });


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.tv_resendCode:

                new resendCode().execute();

                break;

            case R.id.btn_verify:

                String one = edt_one.getText().toString();
                String two = edt_two.getText().toString();
                String three = edt_three.getText().toString();
                String four = edt_four.getText().toString();

                if (one.isEmpty()) {
                    Toast.makeText(VerificaionActivity.this, R.string.empty_otp, Toast.LENGTH_SHORT).show();
                } else if (two.isEmpty()) {
                    Toast.makeText(VerificaionActivity.this, R.string.empty_otp, Toast.LENGTH_SHORT).show();
                } else if (three.isEmpty()) {
                    Toast.makeText(VerificaionActivity.this, R.string.empty_otp, Toast.LENGTH_SHORT).show();
                } else if (four.isEmpty()) {
                    Toast.makeText(VerificaionActivity.this, R.string.empty_otp, Toast.LENGTH_SHORT).show();
                } else {
                    strOtp = one + two + three + four;
                    new VerifyOTP().execute();
                }

//                startActivity(new Intent(VerificaionActivity.this, HomeActivity.class));
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                break;

            case R.id.back_verify:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

        }

    }

    private class VerifyOTP extends AsyncTask<Void,Void,Void>{
        String response = null;
        int status;
        String message, message_ar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(VerificaionActivity.this, getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (nw.isConnectingToInternet()){

                JSONObject json = new JSONObject();

                try{
                    json.put("API-KEY", Constants.API_KEY);
                    json.put("user_id", user_id);
                    json.put("otp", strOtp);

                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(ApiUrl.strBaseUrl + ApiUrl.strVerifiyOtp, ServiceHandler.POST, json);

                    JSONObject js = new JSONObject(response);
                    status = js.getInt("status");
                    message = js.getString("message");
                    Log.e("strVerifiyOtp", response.toString() + " : " + status);

                    if(status == 1){

                        if (strLanguage.equalsIgnoreCase("1")) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }

                        JSONObject userobj = js.getJSONObject("user_info");

                        String user_id = userobj.getString("user_id");
                        String username = userobj.getString("username");
                        String email = userobj.getString("email");
                        String password = userobj.getString("passwd");
                        String mobile = userobj.getString("phone");
                        System.out.println("mobile user_id"+user_id);

                        if(forgot){
                            finish();
                        }else {
                            sm.createLoginSession(user_id, email, username, password, mobile, true);
                        }

                    }else {

                        if (strLanguage.equalsIgnoreCase("1")) {
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
            super.onPostExecute(aVoid);
            // Dismiss the progress dialog
            if (myDialog.isShowing()){
                myDialog.dismiss();
            }

            if (netConnection) {

                if (nodata) {

                    MessageToast.showToastMethod(VerificaionActivity.this, getString(R.string.no_data));

                } else {

                    if (status == 1) {

                        Intent intent = new Intent(VerificaionActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        MessageToast.showToastMethod(VerificaionActivity.this, message);
                    }

                }
            } else {

                MessageToast.showToastMethod(VerificaionActivity.this, getString(R.string.check_net_connection));

            }

        }
    }

    private class resendCode extends AsyncTask<Void, Void, Void>{
        String response = null;
        boolean status;
        String message, message_ar,help;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoader();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();
                try {

                    String finalUrl = ApiUrl.strBaseUrl+ApiUrl.resendCode + "&API-KEY=1514209135"+"&user_id="+user_id;

                    Log.e("resendFinalUrl", finalUrl);
                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(finalUrl, ServiceHandler.GET, json);

                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");
                    Log.e("Fare", response.toString());

                    if (status) {

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                            help=js.getString("help_ar");
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

                    MessageToast.showToastMethod(VerificaionActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {
                        MessageToast.showToastMethod(VerificaionActivity.this, message);
                    } else {
                        MessageToast.showToastMethod(VerificaionActivity.this, message);
                    }

                }
            } else {

                MessageToast.showToastMethod(VerificaionActivity.this, getString(R.string.check_net_connection));

            }

        }


    }

    // Show progress bar
    public void showLoader() {
        try {
            if (myDialog != null)
                myDialog.dismiss();
            myDialog = null;
            myDialog = new ProgressDialog(VerificaionActivity.this);

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
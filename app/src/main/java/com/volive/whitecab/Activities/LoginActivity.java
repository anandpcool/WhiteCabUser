package com.volive.whitecab.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    TextView txt_forgot,tv_sign_in;
    Button btn_log_in;
    EditText et_phone_login,etPassword_login;
    private ProgressDialog myDialog;
    NetworkConnection nw;
    String strLanguage="1",phone,password;
    Boolean netConnection = false;
    Boolean nodata = false;
    SessionManager sm;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUI();
        initViews();

    }

    private void initUI() {
        txt_forgot=findViewById(R.id.txt_forgot);
        tv_sign_in=findViewById(R.id.tv_sign_in);
        btn_log_in=findViewById(R.id.btn_log_in);
        et_phone_login=findViewById(R.id.et_phone_login);
        etPassword_login=findViewById(R.id.etPassword_login);
        nw=new NetworkConnection(LoginActivity.this);
        sm=new SessionManager(LoginActivity.this);
    }

    private void initViews() {
        String text=getResources().getString(R.string.dont_have_account)+"<u> <font color=#000>"+getResources().getString(R.string.sign_up)+"</font></u>";
        tv_sign_in.setText(Html.fromHtml(text));
        txt_forgot.setOnClickListener(this);
        tv_sign_in.setOnClickListener(this);
        btn_log_in.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.txt_forgot:

                ShowEmailDialog();

                break;

            case R.id.tv_sign_in:

                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.btn_log_in:

                 phone=et_phone_login.getText().toString();
                 password=etPassword_login.getText().toString();

                if(phone.isEmpty()){
                    MessageToast.showToastMethod(LoginActivity.this, getString(R.string.enter_phone_number));
                }else if(password.isEmpty()){
                    MessageToast.showToastMethod(LoginActivity.this, getString(R.string.enter_your_pass));
                }else {
                    new Login().execute();
                }

//                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                break;

        }

    }

    private void ShowEmailDialog() {

        dialog=new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        dialog.setContentView(R.layout.email_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

        final EditText edt_mail=(EditText)dialog.findViewById(R.id.edt_mail);
        Button btn_send=(Button)dialog.findViewById(R.id.btn_send);
        ImageView cancel=(ImageView)dialog.findViewById(R.id.cancel);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edt_mail.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, ""+getResources().getString(R.string.enter_your_mail), Toast.LENGTH_SHORT).show();
                }else {
                    new Forgot(edt_mail.getText().toString()).execute();
                }

            }
        });


    }

    private class Forgot extends AsyncTask<Void,Void,Void>{
        String response = null;
        int status;
        String message, message_ar,email;

        public Forgot(String email) {
            this.email=email;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(LoginActivity.this, getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (nw.isConnectingToInternet()){

                JSONObject json = new JSONObject();

                try{
                    json.put("API-KEY", Constants.API_KEY);
                    json.put("email",email);

                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(ApiUrl.strBaseUrl + ApiUrl.strForgotPassword, ServiceHandler.POST, json);

                    JSONObject js = new JSONObject(response);
                    status = js.getInt("status");
                    message = js.getString("message");

                    if(status == 1){

                        if (strLanguage.equalsIgnoreCase("1")) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
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

            if(netConnection){

                if (nodata){
                    MessageToast.showToastMethod(LoginActivity.this, getString(R.string.no_data));
                }else {

                    if(status == 1){
                        MessageToast.showToastMethod(LoginActivity.this, message);
                    }else {
                        MessageToast.showToastMethod(LoginActivity.this, message);
                    }

                    dialog.dismiss();

                }

            }else {
                MessageToast.showToastMethod(LoginActivity.this, getString(R.string.check_net_connection));
            }



        }
    }


    private class Login extends AsyncTask<Void,Void,Void>{

        String response = null;
        boolean status;
        String message, message_ar;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(LoginActivity.this, getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (nw.isConnectingToInternet()){

                JSONObject json = new JSONObject();
                try{

                    json.put("API-KEY", Constants.API_KEY);
                    json.put("phone",phone);
                    json.put("password",password);
                    json.put("device_token","abc");
                    json.put("device_name", Constants.DIVICE_TYPE);

                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(ApiUrl.strBaseUrl + ApiUrl.strUserLogin, ServiceHandler.POST, json);

                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");

                    if(status){

                        String base_url=js.getString("base_url");

                        if (strLanguage.equalsIgnoreCase("1")) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }

                        JSONObject userobj = js.getJSONObject("user_details");

                        String user_id = userobj.getString("user_id");
                        String username = userobj.getString("username");
                        String email = userobj.getString("email");
                        String password = userobj.getString("password");
                        String mobile = userobj.getString("phone");
                        String profile_pic = userobj.getString("profile_pic");
                        System.out.println("mobile user_id"+user_id);

                        sm.createLoginSession(user_id, email, username, password, mobile, false);

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

            if(netConnection){

                if (nodata){
                    MessageToast.showToastMethod(LoginActivity.this, getString(R.string.no_data));
                }else {

                    if(status){
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        MessageToast.showToastMethod(LoginActivity.this, message);
                    }else {
                        MessageToast.showToastMethod(LoginActivity.this, message);
                    }

                }

            }else {
                MessageToast.showToastMethod(LoginActivity.this, getString(R.string.check_net_connection));
            }
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
package com.volive.whitecab.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.volive.whitecab.R;
import com.volive.whitecab.util.ApiUrl;
import com.volive.whitecab.util.DialogsUtils;
import com.volive.whitecab.util.MessageToast;
import com.volive.whitecab.util.NetworkConnection;
import com.volive.whitecab.util.ServiceHandler;

import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_your_name,et_email,et_phone_number,etPassword,et_con_password;
    TextView txt_show,txt_show_con,tv_login;
    Button btn_sign_up;
    ImageView back_sign_up;
    private ProgressDialog myDialog;
    NetworkConnection nw;
    Boolean netConnection = false;
    Boolean nodata = false;
    CheckBox checkbox;
    String strLanguage="1",user_id,otp,phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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
        nw=new NetworkConnection(SignupActivity.this);
    }

    private void initViews() {
        String text=getResources().getString(R.string.have_account)+"<u> <font color=#000>"+getResources().getString(R.string.login)+"</font></u>";
        tv_login.setText(Html.fromHtml(text));
        txt_show.setOnClickListener(this);
        txt_show_con.setOnClickListener(this);
        btn_sign_up.setOnClickListener(this);
        back_sign_up.setOnClickListener(this);
        tv_login.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_sign_up:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
                }else if(et_phone_number.getText().toString().equalsIgnoreCase("")){
                    MessageToast.showToastMethod(SignupActivity.this,getString(R.string.enter_phone_number));
                }else if(etPassword.getText().toString().equalsIgnoreCase("")){
                    MessageToast.showToastMethod(SignupActivity.this,getString(R.string.create_your_password));
                }else if(et_con_password.getText().toString().equalsIgnoreCase("")){
                    MessageToast.showToastMethod(SignupActivity.this,getString(R.string.confirm_your_password));
                }else if(!(etPassword.getText().toString().equals(et_con_password.getText().toString()))){
                    MessageToast.showToastMethod(SignupActivity.this,getString(R.string.password_mismatch));
                }else if(!(checkbox.isChecked())){
                    MessageToast.showToastMethod(SignupActivity.this, getString(R.string.please_accept_terms));
                } else{
                    new Register().execute();
                }

                /*Intent intent=new Intent(SignupActivity.this, VerificaionActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
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

        }

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
                JSONObject json = new JSONObject();

                try{

                    json.put("API-KEY", "1514209135");
                    json.put("username", et_your_name.getText().toString());
                    json.put("email", et_email.getText().toString());
                    json.put("phone", et_phone_number.getText().toString());
                    json.put("password", etPassword.getText().toString());

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


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}

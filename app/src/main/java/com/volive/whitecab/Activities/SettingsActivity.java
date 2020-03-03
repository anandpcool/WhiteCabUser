package com.volive.whitecab.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.volive.whitecab.BuildConfig;
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

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView back_settings;
    TextView tv_sign_out,tv_edit,tv_change_pass,tv_terms_condition,tv_app_version;
    EditText et_user_name,et_user_mobile,et_user_mail;
    String strUserId="", strEmail="", strName="", strPassword="", strNumber="", strSelectLanguage="";
    ProgressDialog myDialog;
    NetworkConnection nw;
    SessionManager sm;
    String strParamphone;
    Boolean netConnection = false;
    Boolean nodata = false;
    RelativeLayout rl_change_pass;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initUI();
        initViews();

    }

    private void initUI() {
        back_settings=findViewById(R.id.back_settings);
        tv_sign_out=findViewById(R.id.tv_sign_out);
        tv_edit=findViewById(R.id.tv_edit);
        et_user_name=findViewById(R.id.et_user_name);
        et_user_mobile=findViewById(R.id.et_user_mobile);
        et_user_mail=findViewById(R.id.et_user_mail);
        tv_change_pass=findViewById(R.id.tv_change_pass);
        tv_terms_condition=findViewById(R.id.tv_terms_condition);
        rl_change_pass=findViewById(R.id.rl_change_pass);
        tv_app_version=findViewById(R.id.tv_app_version);

        Log.e("askljhdfds", BuildConfig.VERSION_CODE+"");
        tv_app_version.setText(String.valueOf(BuildConfig.VERSION_CODE));

        et_user_name.setEnabled(false);
        et_user_mobile.setEnabled(false);
        et_user_mail.setEnabled(false);

        nw=new NetworkConnection(SettingsActivity.this);
        sm=new SessionManager(SettingsActivity.this);

        HashMap<String, String> userDetail = sm.getUserDetails();
        if(userDetail.get(SessionManager.KEY_ID) != null ){
            strUserId = userDetail.get(SessionManager.KEY_ID);
            strName = userDetail.get(SessionManager.KEY_NAME);
            strEmail = userDetail.get(SessionManager.KEY_EMAIL);
            strNumber = userDetail.get(SessionManager.KEY_NUMBER);
            strPassword = userDetail.get(SessionManager.KEY_PASSWORD);
            strSelectLanguage = userDetail.get(SessionManager.KEY_LANGUAGE);

            Log.e("strPassword", strPassword);

            et_user_name.setText(strName);
            et_user_mobile.setText(strNumber);
            et_user_mail.setText(strEmail);
            tv_change_pass.setText(strPassword);
        }

    }

    private void initViews() {
        back_settings.setOnClickListener(this);
        tv_sign_out.setOnClickListener(this);
        tv_edit.setOnClickListener(this);
        tv_terms_condition.setOnClickListener(this);
        rl_change_pass.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_settings:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

            case R.id.tv_sign_out:
                SignOutDialog();
                break;

            case R.id.rl_change_pass:

                PasswordDialog();

                break;

            case R.id.tv_terms_condition:

                Intent intent = new Intent(SettingsActivity.this, TermsActivity.class);
                startActivity(intent);

                break;

            case R.id.tv_edit:

                if(tv_edit.getText().toString().equalsIgnoreCase(getString(R.string.edit))){
                    tv_edit.setText(getString(R.string.save));
                    et_user_name.setEnabled(true);
                    et_user_mobile.setEnabled(true);
                    et_user_mail.setEnabled(true);
                    et_user_name.setCursorVisible(true);
                    et_user_name.setSelection(et_user_name.length());
                }else {
                    strName = et_user_name.getText().toString().trim();
                    strEmail = et_user_mail.getText().toString().trim();
                    strNumber = et_user_mobile.getText().toString().trim();
                    strPassword = tv_change_pass.getText().toString().trim();

                    if (strName.isEmpty()) {
                        Toast.makeText(SettingsActivity.this, getString(R.string.empty_name), Toast.LENGTH_SHORT).show();
                    } else if (strEmail.isEmpty()||!android.util.Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
                        Toast.makeText(SettingsActivity.this, getString(R.string.enter_your_mail), Toast.LENGTH_SHORT).show();
                    } else if (strNumber.isEmpty()) {
                        Toast.makeText(SettingsActivity.this, getString(R.string.enter_phone_number), Toast.LENGTH_SHORT).show();
                    } else if (strPassword.isEmpty()) {
                        Toast.makeText(SettingsActivity.this, getString(R.string.enter_your_pass), Toast.LENGTH_SHORT).show();
                    } else {
                        new editProfile().execute();
                    }
                }

                break;

        }

    }

    private void PasswordDialog() {
        dialog=new Dialog(SettingsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        dialog.setContentView(R.layout.password_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

        final EditText edt_old_pass=(EditText)dialog.findViewById(R.id.edt_old_pass);
        final EditText edt_new_pass=(EditText)dialog.findViewById(R.id.edt_new_pass);
        Button btn_update=(Button)dialog.findViewById(R.id.btn_update);
        ImageView pass_cancel=(ImageView)dialog.findViewById(R.id.pass_cancel);


        pass_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edt_old_pass.getText().toString().isEmpty() || edt_new_pass.getText().toString().isEmpty()){
                    Toast.makeText(SettingsActivity.this, ""+getResources().getString(R.string.enter_your_current_pass), Toast.LENGTH_SHORT).show();
                }else if(edt_new_pass.getText().toString().isEmpty()) {
                    Toast.makeText(SettingsActivity.this, ""+getResources().getString(R.string.create_your_password), Toast.LENGTH_SHORT).show();
                }else {
                    new changedPassword(edt_old_pass.getText().toString(),edt_new_pass.getText().toString()).execute();
                }

            }
        });

    }

    private void SignOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.sure_signout));
        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                new signout().execute();

            }
        });

        builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                dialog.cancel();

            }
        });

        AlertDialog alert = builder.create();
        alert.setTitle(getResources().getString(R.string.confirm));
        alert.show();
    }

    private class editProfile extends AsyncTask<Void, Void, Void> {

        String response = null;
        boolean status, otp_status;
        String message, message_ar;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(SettingsActivity.this, getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();
                try {
                    json.put("API-KEY", Constants.API_KEY);
                    json.put("user_id", strUserId);
                    json.put("username", strName);
                    json.put("mobile", strNumber);
                    json.put("email", strEmail);

                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(ApiUrl.strBaseUrl + ApiUrl.strEditProfile, ServiceHandler.POST, json);


                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");
                    Log.e("strEditProfile", response);
//                    message_ar = js.getString("message_ar");


                    if (status) {

                        if (strSelectLanguage.equalsIgnoreCase("1") || strSelectLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strSelectLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }

                        JSONObject userobj = js.getJSONObject("user_details");
                        String user_id = userobj.getString("user_id");
                        //  otp_status = js.getBoolean("otp_status");
                        String username = userobj.getString("username");
                        String email = userobj.getString("email");
                        String password = userobj.getString("password");
                        strParamphone = userobj.getString("phone");
//                        String profile_pic = userobj.getString("profile_pic");
//                        String base_url = js.getString("base_url");

                        sm.createLoginSession(user_id, email, username, password, strParamphone, true);
//                        sm.profileImageUrl(profile_pic, base_url);

                    } else {

                        if (strSelectLanguage.equalsIgnoreCase("1") || strSelectLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strSelectLanguage.equalsIgnoreCase("2")) {
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

                    MessageToast.showToastMethod(SettingsActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {
                        MessageToast.showToastMethod(SettingsActivity.this, message);
                        et_user_name.setEnabled(false);
                        et_user_mobile.setEnabled(false);
                        et_user_mail.setEnabled(false);
                        tv_edit.setText(getString(R.string.edit));
//                        Intent i = new Intent(SettingsActivity.this, HomeScreenActivity.class);
//                        startActivity(i);
//                        finish();
//                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                    } else {

                        MessageToast.showToastMethod(SettingsActivity.this, message);
                    }

                }
            } else {

                MessageToast.showToastMethod(SettingsActivity.this, getString(R.string.check_net_connection));

            }

        }

    }

    private class changedPassword extends AsyncTask<Void, Void, Void> {

        String response = null;
        boolean status;
        String message, message_ar, old_password,new_password;

        public changedPassword(String old_password, String new_password) {
            this.old_password=old_password;
            this.new_password=new_password;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(SettingsActivity.this, getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();
                try {
                    json.put("API-KEY", Constants.API_KEY);
                    json.put("user_id", strUserId);
                    json.put("password", new_password);
                    json.put("old_password", old_password);

                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(ApiUrl.strBaseUrl + ApiUrl.strChangedPasswordNew, ServiceHandler.POST, json);


                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");
                    Log.e("strChangedPassword", response.toString() + " : " + status);
//                    message_ar = js.getString("message_ar");


                    if (status) {

                        if (strSelectLanguage.equalsIgnoreCase("1") || strSelectLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strSelectLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }

                        JSONObject userobj = js.getJSONObject("user_details");

                        String user_id = userobj.getString("user_id");
                        String username = userobj.getString("username");
                        String email = userobj.getString("email");
                        String password = userobj.getString("password");
                        String phone = userobj.getString("mobile");
                        String profile_pic = userobj.getString("profile_pic");
                        String base_url = js.getString("base_url");

                        tv_change_pass.setText(password);
                        sm.createLoginSession(user_id, email, username, password, phone, true);
                        sm.profileImageUrl(profile_pic, base_url);

                    } else {

                        if (strSelectLanguage.equalsIgnoreCase("1") || strSelectLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strSelectLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }

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

                    MessageToast.showToastMethod(SettingsActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {
                        dialog.dismiss();
                        MessageToast.showToastMethod(SettingsActivity.this, message);
                      /*  Intent i = new Intent(SettingActivity.this, HomeScreenActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();*/

                    } else {

                        MessageToast.showToastMethod(SettingsActivity.this, message);
                    }

                }
            } else {

                MessageToast.showToastMethod(SettingsActivity.this, getString(R.string.check_net_connection));

            }

        }

    }


    private class signout extends AsyncTask<Void, Void, Void> {

        String response = null;
        boolean status, otp_status;
        String message, message_ar;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(SettingsActivity.this, getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();
                try {
                    json.put("API-KEY", Constants.API_KEY);
                    json.put("user_id", strUserId);
                    Log.e("userid",strUserId);

                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(ApiUrl.strBaseUrl + ApiUrl.logout, ServiceHandler.POST, json);


                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");

//                    message_ar = js.getString("message_ar");


                    if (status) {

                        if (strSelectLanguage.equalsIgnoreCase("1")) {
                            message = js.getString("message");
                        } else if (strSelectLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }


                    } else {

                        if (strSelectLanguage.equalsIgnoreCase("1")) {
                            message = js.getString("message");
                        } else if (strSelectLanguage.equalsIgnoreCase("2")) {
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

                    MessageToast.showToastMethod(SettingsActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {
                        sm.logoutUser();

                    } else {

                        MessageToast.showToastMethod(SettingsActivity.this, message);
                    }

                }
            } else {

                MessageToast.showToastMethod(SettingsActivity.this, getString(R.string.check_net_connection));

            }

        }

    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

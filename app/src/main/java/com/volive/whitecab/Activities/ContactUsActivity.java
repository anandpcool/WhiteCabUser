package com.volive.whitecab.Activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.volive.whitecab.Adapters.ViewPagerAdapters.AboutPagerAdapter;
import com.volive.whitecab.DataModels.AboutPojo;
import com.volive.whitecab.R;
import com.volive.whitecab.util.ApiUrl;
import com.volive.whitecab.util.Constants;
import com.volive.whitecab.util.DialogsUtils;
import com.volive.whitecab.util.MessageToast;
import com.volive.whitecab.util.NetworkConnection;
import com.volive.whitecab.util.ServiceHandler;
import com.volive.whitecab.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactUsActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView back_contact;
    NetworkConnection nw;
    SessionManager sm;
    String strUserId="", strEmail="", strName="", strPassword="", strNumber="", strLanguage="";
    EditText txt_edt_name,txt_edt_email,txt_edt_phone,txt_edt_message;
    Button btn_contact_submit;
    private ProgressDialog myDialog;
    Boolean netConnection = false;
    Boolean nodata = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        initUI();
        initViews();
    }

    private void initUI() {
        back_contact=findViewById(R.id.back_contact);
        txt_edt_name=findViewById(R.id.txt_edt_name);
        txt_edt_email=findViewById(R.id.txt_edt_email);
        txt_edt_phone=findViewById(R.id.txt_edt_phone);
        txt_edt_message=findViewById(R.id.txt_edt_message);
        btn_contact_submit=findViewById(R.id.btn_contact_submit);
    }

    private void initViews() {
        back_contact.setOnClickListener(this);
        btn_contact_submit.setOnClickListener(this);

        nw=new NetworkConnection(ContactUsActivity.this);
        sm=new SessionManager(ContactUsActivity.this);

        HashMap<String, String> userDetail = sm.getUserDetails();
        if(userDetail.get(SessionManager.KEY_ID) != null ){
            strUserId = userDetail.get(SessionManager.KEY_ID);
            strName = userDetail.get(SessionManager.KEY_NAME);
            strEmail = userDetail.get(SessionManager.KEY_EMAIL);
            strNumber = userDetail.get(SessionManager.KEY_NUMBER);
            strPassword = userDetail.get(SessionManager.KEY_PASSWORD);
         //   strLanguage = userDetail.get(SessionManager.KEY_LANGUAGE);

            Log.e("strPassword", strPassword);

//            txt_edt_name.setText(strName);
//            txt_edt_phone.setText(strNumber);
//            txt_edt_email.setText(strEmail);

        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){


            case R.id.back_contact:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

            case R.id.btn_contact_submit:

                String pattern = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

                if(txt_edt_name.getText().toString().isEmpty()){
                    MessageToast.showToastMethod(ContactUsActivity.this, getString(R.string.enter_your_name));
                }else if(txt_edt_email.getText().toString().isEmpty()){
                    MessageToast.showToastMethod(ContactUsActivity.this, getString(R.string.enter_your_mail));
                }else if(!(txt_edt_email.getText().toString().matches(pattern))){
                    MessageToast.showToastMethod(ContactUsActivity.this, getString(R.string.enter_valid_mail));
                }else if(txt_edt_phone.getText().toString().isEmpty()){
                    MessageToast.showToastMethod(ContactUsActivity.this, getString(R.string.enter_phone_number));
                }else if(txt_edt_message.getText().toString().isEmpty()){
                    MessageToast.showToastMethod(ContactUsActivity.this, getString(R.string.add_your_message));
                }else {
                    new contactUs().execute();
                }

                break;
        }

    }

    private class contactUs extends AsyncTask<Void, Void, Void>{

        String response = null;
        int status;
        String base_url,message, message_ar;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(ContactUsActivity.this, getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();
                try {
                    json.put("API-KEY", Constants.API_KEY);
                    json.put("user_id", strUserId);
                    json.put("name", strName);
                    json.put("phone", strNumber);
                    json.put("email", strEmail);
                    json.put("message", txt_edt_message.getText().toString());

                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(ApiUrl.strBaseUrl + ApiUrl.contact_us, ServiceHandler.POST, json);

                    JSONObject js = new JSONObject(response);
                    status = js.getInt("status");
                    message = js.getString("message");
                    Log.e("strContactUs", response);

                    if (status == 1){

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }

                    }else {

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
            }else {

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

                    MessageToast.showToastMethod(ContactUsActivity.this, getString(R.string.no_data));

                } else {

                    if (status == 1) {

                        MessageToast.showToastMethod(ContactUsActivity.this, message);
//                        finish();
//                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    } else {

                        MessageToast.showToastMethod(ContactUsActivity.this, message);
                    }

                }
            } else {

                MessageToast.showToastMethod(ContactUsActivity.this, getString(R.string.check_net_connection));

            }

        }


    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

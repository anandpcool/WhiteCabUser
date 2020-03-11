package com.volive.whitecab.Activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.volive.whitecab.Adapters.ViewPagerAdapters.AboutPagerAdapter;
import com.volive.whitecab.DataModels.AboutPojo;
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

public class HelpActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView back_help;
    private ProgressDialog myDialog;
    NetworkConnection nw;
    SessionManager sm;
    Boolean netConnection = false;
    Boolean nodata = false;
    String strLanguage="";
    WebView help_webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        initUI();
        initViews();

    }

    private void initUI() {
        back_help=findViewById(R.id.back_help);
        help_webView=findViewById(R.id.help_webView);
        nw=new NetworkConnection(HelpActivity.this);
        sm=new SessionManager(HelpActivity.this);
    }

    private void initViews() {
        back_help.setOnClickListener(this);
        new help().execute();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_help:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

        }

    }

    private class help extends AsyncTask<Void, Void, Void>{
        String response = null;
        boolean status;
        String base_url,message, message_ar,help;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(HelpActivity.this, getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();
                try {

                    String finalUrl = ApiUrl.strBaseUrl+"help?" + "&API-KEY=1514209135";
                    Log.e("HelpFinalUrl", finalUrl);
                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(finalUrl, ServiceHandler.GET, json);


                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");
                    Log.e("Fare", response.toString());

                    if (status) {

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            message = js.getString("message");
                            help=js.getString("help");
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
            if (myDialog.isShowing())
                myDialog.dismiss();

            if (netConnection) {

                if (nodata) {

                    MessageToast.showToastMethod(HelpActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            help_webView.loadDataWithBaseURL(null, help, "text/html", "UTF-8", null);
                        }

                    } else {
                        MessageToast.showToastMethod(HelpActivity.this, message);
                    }

                }
            } else {

                MessageToast.showToastMethod(HelpActivity.this, getString(R.string.check_net_connection));

            }

        }


    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
package com.volive.whitecab.Activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.volive.whitecab.R;
import com.volive.whitecab.util.ApiUrl;
import com.volive.whitecab.util.DialogsUtils;
import com.volive.whitecab.util.MessageToast;
import com.volive.whitecab.util.NetworkConnection;
import com.volive.whitecab.util.ServiceHandler;
import com.volive.whitecab.util.SessionManager;

import org.json.JSONObject;

public class TermsActivity extends AppCompatActivity {
    ProgressDialog myDialog;
    Boolean netConnection = false;
    Boolean nodata = false;
    ImageView back_terms;
    NetworkConnection nw;
    SessionManager sm;
    String strLanguage="";
    WebView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        initUI();
        initViews();
    }

    private void initUI() {
        nw = new NetworkConnection(TermsActivity.this);
        sm = new SessionManager(TermsActivity.this);
        back_terms= findViewById(R.id.back_terms);
        webview = (WebView) findViewById(R.id.webview);
    }

    private void initViews() {
        back_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        new getTerms().execute();
    }

    private class getTerms extends AsyncTask<Void, Void, Void> {

        String response = null;
        boolean status;
        String message, message_ar, terms, terms_ar;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(TermsActivity.this, getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();
                try {

                    String finalUrl = ApiUrl.strBaseUrl+"terms?API-KEY=1514209135";
                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(finalUrl, ServiceHandler.GET, json);


                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");
                    Log.e("strEditProfile", response);
//                    message_ar = js.getString("message_ar");


                    if (status) {

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }
                        terms = js.getString("terms");
                        terms_ar = js.getString("terms_ar");
                    } else {

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
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

                    MessageToast.showToastMethod(TermsActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            webview.loadDataWithBaseURL(null, terms, "text/html", "UTF-8", null);
                        } else if (strLanguage.equalsIgnoreCase("2")) {

                            webview.loadDataWithBaseURL(null, terms_ar, "text/html", "UTF-8", null);

                        }


                    } else {

                        MessageToast.showToastMethod(TermsActivity.this, message);
                    }

                }
            } else {

                MessageToast.showToastMethod(TermsActivity.this, getString(R.string.check_net_connection));

            }

        }

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

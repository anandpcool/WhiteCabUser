package com.volive.whitecab.Activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.volive.whitecab.Adapters.RecyclerAdapters.OfferAdapter;
import com.volive.whitecab.Adapters.ViewPagerAdapters.AboutPagerAdapter;
import com.volive.whitecab.DataModels.AboutPojo;
import com.volive.whitecab.DataModels.OfferPojo;
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

public class OffersActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView offer_recycler;
    OfferAdapter adapter;
    ImageView back_offer;
    NetworkConnection nw;
    SessionManager sm;
    String strLanguage="";
    private ProgressDialog myDialog;
    Boolean netConnection = false;
    Boolean nodata = false;
    OfferPojo offerPojo;
    ArrayList<OfferPojo> offerPojoArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        initUI();
        initViews();

    }

    private void initUI() {
        offer_recycler=findViewById(R.id.offer_recycler);
        back_offer=findViewById(R.id.back_offer);
        nw=new NetworkConnection(OffersActivity.this);
        sm=new SessionManager(OffersActivity.this);
    }

    private void initViews() {
        back_offer.setOnClickListener(this);

        new offer().execute();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_offer:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

        }

    }

    private class offer extends AsyncTask<Void, Void, Void> {

        String response = null;
        boolean status;
        String base_url,message, message_ar;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(OffersActivity.this, getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();
                try {

                    String finalUrl = ApiUrl.strBaseUrl+"offers?" + "&API-KEY=1514209135";
                    Log.e("OfferFinalUrl", finalUrl);
                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(finalUrl, ServiceHandler.GET, json);


                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");
                    base_url=js.getString("base_url");
                    Log.e("offer_response", response.toString());
//                    message_ar = js.getString("message_ar");

                    if (status) {

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }

                        JSONArray jsonArray=js.getJSONArray("offers");

                        if(jsonArray != null && jsonArray.length() > 0){
                            offerPojoArrayList=new ArrayList<>();
                            for(int i=0; i<jsonArray.length(); i++){

                                JSONObject object=jsonArray.getJSONObject(i);

                                String date= object.getString("date");
                                String image=object.getString("image");
                                OfferPojo offerPojo= new OfferPojo(date, image);
                                offerPojoArrayList.add(offerPojo);
                            }

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

                    MessageToast.showToastMethod(OffersActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {

                        adapter=new OfferAdapter(OffersActivity.this,offerPojoArrayList,base_url);
                        offer_recycler.setAdapter(adapter);

                    } else {
                        MessageToast.showToastMethod(OffersActivity.this, message);
                    }

                }
            } else {

                MessageToast.showToastMethod(OffersActivity.this, getString(R.string.check_net_connection));

            }

        }



    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

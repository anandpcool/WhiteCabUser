package com.volive.whitecab.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.volive.whitecab.Adapters.RecyclerAdapters.ComplaintAdapter;
import com.volive.whitecab.DataModels.ComplaintModel;
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

public class ComplaintActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView back_complaint;
    Button btn_complaint;
    RecyclerView complaint_recycler;
    ComplaintAdapter adapter;
    String trip_id,driver_id,driver_name,strLanguage="";
    String[] complaint_texts=new String[]{"I lost an item","Bad driver behaviour","I would like a refund","Different driver / vehicle","Other"};
    private ProgressDialog myDialog;
    NetworkConnection nw;
    SessionManager sm;
    Boolean netConnection = false;
    Boolean nodata = false;
    ComplaintModel complaintModel;
    ArrayList<ComplaintModel> complaintArrayList;
    private String complaint_id="",strUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        initUI();
        initViews();

    }

    private void initUI() {
        back_complaint=findViewById(R.id.back_complaint);
        btn_complaint=findViewById(R.id.btn_complaint);
        complaint_recycler=findViewById(R.id.complaint_recycler);
        complaintArrayList=new ArrayList<>();
        nw=new NetworkConnection(ComplaintActivity.this);
        sm=new SessionManager(ComplaintActivity.this);
        HashMap<String, String> userDetail = sm.getUserDetails();
        strUserId = userDetail.get(SessionManager.KEY_ID);
    }

    private void initViews() {
        back_complaint.setOnClickListener(this);
        btn_complaint.setOnClickListener(this);
        if(getIntent().getExtras()!=null){
            trip_id= getIntent().getStringExtra("trip_id");
            driver_id= getIntent().getStringExtra("driver_id");
            driver_name= getIntent().getStringExtra("driver_name");
        }

        new complaintList().execute();
        /*adapter=new ComplaintAdapter(ComplaintActivity.this,complaint_texts);
        complaint_recycler.setHasFixedSize(true);
        complaint_recycler.setAdapter(adapter);*/
    }

    public void onItemSelect(String complaint_id) {
        this.complaint_id=complaint_id;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_complaint:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;


            case R.id.btn_complaint:

                new submitComplaint().execute();

                break;

        }

    }

    private class submitComplaint extends AsyncTask<Void,Void,Void>{

        String response = null;
        boolean status;
        String message, message_ar;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(ComplaintActivity.this, getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();
                try {
                    json.put("API-KEY", Constants.API_KEY);
                    json.put("user_id", strUserId);
                    json.put("complaint_id", complaint_id);


                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(ApiUrl.strBaseUrl + ApiUrl.userComplaint, ServiceHandler.POST, json);

                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");
                    Log.e("strContactUs", response);

                    if (status){

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

                    MessageToast.showToastMethod(ComplaintActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {
                        MessageToast.showToastMethod(ComplaintActivity.this, message);
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        finish();
                    } else {

                        MessageToast.showToastMethod(ComplaintActivity.this, message);
                    }

                }
            } else {

                MessageToast.showToastMethod(ComplaintActivity.this, getString(R.string.check_net_connection));

            }

        }


    }

    private class complaintList extends AsyncTask<Void, Void, Void> {

        String response = null;
        boolean status;
        String message, message_ar;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(ComplaintActivity.this, getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();
                try {

                    String finalUrl = ApiUrl.strBaseUrl+ApiUrl.complaintList + "&API-KEY=1514209135";
                    Log.e("OfferFinalUrl", finalUrl);
                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(finalUrl, ServiceHandler.GET, json);


                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");
                    Log.e("offer_response", response.toString());
//                    message_ar = js.getString("message_ar");

                    if (status) {

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }

                        JSONArray jsonArray=js.getJSONArray("complaints");
                        complaintArrayList.clear();
                        if(jsonArray != null && jsonArray.length() > 0){
                            complaintArrayList=new ArrayList<>();
                            for(int i=0; i<jsonArray.length(); i++){

                                JSONObject object=jsonArray.getJSONObject(i);

                                String id= object.getString("id");
                                String complaint_title=object.getString("complaint_title");
                                ComplaintModel complaintPojo= new ComplaintModel(id, complaint_title);
                                complaintArrayList.add(complaintPojo);
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

                    MessageToast.showToastMethod(ComplaintActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {
                        adapter=new ComplaintAdapter(ComplaintActivity.this,complaintArrayList);
                        complaint_recycler.setHasFixedSize(true);
                        complaint_recycler.setAdapter(adapter);
                    } else {
                        MessageToast.showToastMethod(ComplaintActivity.this, message);
                    }

                }
            } else {

                MessageToast.showToastMethod(ComplaintActivity.this, getString(R.string.check_net_connection));

            }

        }



    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}

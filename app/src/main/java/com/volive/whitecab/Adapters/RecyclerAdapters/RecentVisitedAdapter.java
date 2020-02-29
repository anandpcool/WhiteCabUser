package com.volive.whitecab.Adapters.RecyclerAdapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.volive.whitecab.Activities.BookingActivity;
import com.volive.whitecab.Activities.DropLocationActivity;
import com.volive.whitecab.Activities.DropOffActivity;
import com.volive.whitecab.Activities.HomeActivity;
import com.volive.whitecab.Activities.PickLocationActivity;
import com.volive.whitecab.DataModels.RecentRideModel;
import com.volive.whitecab.R;
import com.volive.whitecab.util.ApiUrl;
import com.volive.whitecab.util.Constants;
import com.volive.whitecab.util.DialogsUtils;
import com.volive.whitecab.util.MessageToast;
import com.volive.whitecab.util.NetworkConnection;
import com.volive.whitecab.util.ServiceHandler;
import com.volive.whitecab.util.SessionManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RecentVisitedAdapter extends RecyclerView.Adapter<RecentVisitedAdapter.MyHolder> {

    Context context;
    boolean check;
    ArrayList<RecentRideModel> arrayList;
    Dialog addressDialog;
    ProgressDialog myDialog;
    NetworkConnection nw;
    SessionManager sm;
    String strUserId="",strLanguage="",strLat="",strLong="",strAddress="";
    Boolean netConnection = false;
    Boolean nodata = false;

    public RecentVisitedAdapter(Context context, boolean check, ArrayList<RecentRideModel> arrayList) {
        this.context=context;
        this.check=check;
        this.arrayList=arrayList;
        nw=new NetworkConnection(context);
        sm=new SessionManager(context);
        HashMap<String, String> userDetail = sm.getUserDetails();
        strUserId = userDetail.get(SessionManager.KEY_ID);

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.visited_adapter_layout,viewGroup,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {

        holder.tv_address_title.setText(arrayList.get(position).getTo_address());

        holder.ll_recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(check){
                    ((PickLocationActivity)context).send_fav(arrayList.get(position).getTo_latitude(),arrayList.get(position).getTo_longitude(),arrayList.get(position).getTo_address());
                }else {
                    ((DropLocationActivity)context).send_fav(arrayList.get(position).getTo_latitude(),arrayList.get(position).getTo_longitude(),arrayList.get(position).getTo_address());
                }

            }
        });

        holder.img_love_gray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addAddressTitleDialog();
                strLat= arrayList.get(position).getTo_latitude();
                strLong= arrayList.get(position).getTo_longitude();
                strAddress=arrayList.get(position).getTo_address();
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        TextView tv_address_title,tv_address_desc;
        RelativeLayout ll_recent;
        ImageView img_love_gray;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tv_address_title=itemView.findViewById(R.id.tv_address_title);
            tv_address_desc=itemView.findViewById(R.id.tv_address_desc);
            ll_recent=itemView.findViewById(R.id.ll_recent);
            img_love_gray=itemView.findViewById(R.id.img_love_gray);
        }
    }

    private void addAddressTitleDialog() {
        addressDialog=new Dialog(context);
        addressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = addressDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        addressDialog.setContentView(R.layout.address_title_dialog);
        addressDialog.setCanceledOnTouchOutside(true);
        addressDialog.setCancelable(true);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        addressDialog.show();

        final EditText edt_title=(EditText)addressDialog.findViewById(R.id.edt_title);
        Button btn_save=(Button)addressDialog.findViewById(R.id.btn_save);
        ImageView img_close=(ImageView)addressDialog.findViewById(R.id.img_close);


        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addressDialog.dismiss();

            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edt_title.getText().toString().isEmpty()){
                    MessageToast.showToastMethod(context, ""+context.getResources().getString(R.string.add_title));
                }else {
                    new saveAddress(edt_title.getText().toString()).execute();
                }

            }
        });

    }

    private class saveAddress extends AsyncTask<Void, Void, Void> {

        String response = null,title;
        boolean status;
        String message, message_ar;

        public saveAddress(String title) {
            this.title=title;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(context, context.getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();
                try {

                    json.put("API-KEY", Constants.API_KEY);
                    json.put("user_id", strUserId);
                    json.put("lat", strLat);
                    json.put("long", strLong);
                    json.put("address", strAddress);
                    json.put("type",title);
                    json.put("request_type","save");

                    Log.e("Param", json.toString());
                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(ApiUrl.strBaseUrl + ApiUrl.strSaveAddress, ServiceHandler.POST, json);

                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");

                    Log.e("strSaveAddress", response.toString());
//                    message_ar = js.getString("message_ar");

                    if (status) {

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
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
            if (myDialog.isShowing()){
                myDialog.dismiss();
            }

            if (netConnection) {

                if (nodata) {

                    MessageToast.showToastMethod(context, context.getString(R.string.no_data));

                } else {

                    if (status) {
                        addressDialog.dismiss();
                        MessageToast.showToastMethod(context, message);
                        if(check){
                            ((PickLocationActivity)context).updateFavLocations();
                        }else {
                            ((DropLocationActivity)context).updateFavLocations();
                        }
                    } else {
                        MessageToast.showToastMethod(context, message);
                    }

                }

            } else {

                MessageToast.showToastMethod(context, context.getString(R.string.check_net_connection));

            }

        }

    }


}

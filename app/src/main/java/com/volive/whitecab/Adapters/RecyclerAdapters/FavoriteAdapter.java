package com.volive.whitecab.Adapters.RecyclerAdapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.volive.whitecab.Activities.DropLocationActivity;
import com.volive.whitecab.Activities.PickLocationActivity;
import com.volive.whitecab.DataModels.FavouriteModel;
import com.volive.whitecab.R;
import com.volive.whitecab.util.ApiUrl;
import com.volive.whitecab.util.Constants;
import com.volive.whitecab.util.DialogsUtils;
import com.volive.whitecab.util.MessageToast;
import com.volive.whitecab.util.NetworkConnection;
import com.volive.whitecab.util.PreferenceUtils;
import com.volive.whitecab.util.ServiceHandler;
import com.volive.whitecab.util.SessionManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.MyHolder> {

    Context context;
    boolean check;
    ArrayList<FavouriteModel> favouriteArrayList;
    PreferenceUtils preferenceUtils;
    Dialog addressDialog;
    ProgressDialog myDialog;
    NetworkConnection nw;
    SessionManager sm;
    String strUserId="",strLanguage="", strLat ="", strLong ="", strAddress ="";
    Boolean netConnection = false;
    Boolean nodata = false;

    public FavoriteAdapter(Context context, boolean check, ArrayList<FavouriteModel> favouriteArrayList) {
        this.context=context;
        this.check=check;
        preferenceUtils=new PreferenceUtils(context);
        this.favouriteArrayList=favouriteArrayList;
        nw=new NetworkConnection(context);
        sm=new SessionManager(context);
        HashMap<String, String> userDetail = sm.getUserDetails();
        strUserId = userDetail.get(SessionManager.KEY_ID);
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fav_adapter_layout,viewGroup,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {

        holder.tv_address_title.setText(favouriteArrayList.get(position).getType());
        holder.tv_address_desc.setText(favouriteArrayList.get(position).getAddress());

        holder.ll_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(check){
                    ((PickLocationActivity)context).send_fav(favouriteArrayList.get(position).getLattitude(),favouriteArrayList.get(position).getLongitude(),favouriteArrayList.get(position).getAddress());
                }else {
                    ((DropLocationActivity)context).send_fav(favouriteArrayList.get(position).getLattitude(),favouriteArrayList.get(position).getLongitude(),favouriteArrayList.get(position).getAddress());
                }

            }
        });

        holder.cancel_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new deleteAddress(favouriteArrayList.get(position).getType(),favouriteArrayList.get(position).getLattitude(),favouriteArrayList.get(position).getLongitude(),favouriteArrayList.get(position).getAddress()).execute();

            }
        });

    }

    @Override
    public int getItemCount() {
        return favouriteArrayList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        TextView tv_address_title,tv_address_desc;
        RelativeLayout ll_fav;
        ImageView cancel_image;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            ll_fav=itemView.findViewById(R.id.ll_fav);
            tv_address_title=itemView.findViewById(R.id.tv_address_title);
            tv_address_desc=itemView.findViewById(R.id.tv_address_desc);
            cancel_image=itemView.findViewById(R.id.cancel_image);
        }
    }

    private class deleteAddress extends AsyncTask<Void, Void, Void> {

        String response = null,title;
        boolean status;
        String message, message_ar;

        public deleteAddress(String title, String lattitude, String longitude, String address) {
            strLat =lattitude;
            strLong =longitude;
            strAddress =address;
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
                    json.put("request_type","cancel");

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

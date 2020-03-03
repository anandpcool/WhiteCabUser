package com.volive.whitecab.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.volive.whitecab.Adapters.RecyclerAdapters.FavoriteAdapter;
import com.volive.whitecab.Adapters.RecyclerAdapters.RecentVisitedAdapter;
import com.volive.whitecab.DataModels.FavouriteModel;
import com.volive.whitecab.DataModels.RecentRideModel;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DropLocationActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView fav_recycler,visited_recycler;
    FavoriteAdapter fav_adapter;
    RecentVisitedAdapter visitedAdapter;
    ImageView back_drop_off,img_cancel;
    CardView cardView_fromTo;
    TextView tv_from_add,tv_destination_Address;
    String from_address="",to_address="";
    AutocompleteSupportFragment from_autocomplete,dest_autocomplete;
    LinearLayout ll_from_autoComeplete,layout_toAddress,ll_dest_autoComeplete;
    double from_lat,from_lng, dest_lat, dest_lng;
    NetworkConnection nw;
    SessionManager sm;
    String strLanguage="",strUserId="";
    Boolean netConnection = false;
    Boolean nodata = false;
    ArrayList<FavouriteModel> favouriteArrayList;
    ArrayList<RecentRideModel> recentArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drop_location);

        initUI();
        initViews();

    }

    private void initUI() {
        nw=new NetworkConnection(DropLocationActivity.this);
        sm=new SessionManager(DropLocationActivity.this);
        favouriteArrayList=new ArrayList<>();
        recentArrayList=new ArrayList<>();
        HashMap<String, String> userDetail = sm.getUserDetails();
        strUserId = userDetail.get(SessionManager.KEY_ID);
        back_drop_off=findViewById(R.id.back_drop_off);
        fav_recycler=findViewById(R.id.fav_recycler);
        visited_recycler=findViewById(R.id.visited_recycler);
        cardView_fromTo=findViewById(R.id.cardView_fromTo);
        tv_from_add=findViewById(R.id.tv_from_add);
        tv_destination_Address=findViewById(R.id.tv_destination_Address);
        img_cancel=findViewById(R.id.img_cancel);
        from_autocomplete=(AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.from_autocomplete);
        dest_autocomplete=(AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.dest_autocomplete);
        ll_from_autoComeplete=findViewById(R.id.ll_from_autoComeplete);
        layout_toAddress=findViewById(R.id.layout_toAddress);
        ll_dest_autoComeplete=findViewById(R.id.ll_dest_autoComeplete);
    }

    private void initViews() {

        if(getIntent().getExtras() !=null){
           from_address =  getIntent().getStringExtra("from_address");
           to_address= getIntent().getStringExtra("to_address");
          String latitude= getIntent().getStringExtra("from_lat");
          String longitude= getIntent().getStringExtra("from_long");
          from_lat=Double.valueOf(latitude);
          from_lng=Double.valueOf(longitude);
           tv_from_add.setText(from_address);
           tv_destination_Address.setText(to_address);
        }

        img_cancel.setOnClickListener(this);
        tv_destination_Address.setOnClickListener(this);
        tv_from_add.setOnClickListener(this);
        back_drop_off.setOnClickListener(this);

        new favouritePlaces().execute();


    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_drop_off:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

            case R.id.cardView_fromTo:

                break;

            case R.id.img_cancel:

                showFromGoogleAddress();

                break;

            case R.id.tv_from_add:

                showFromGoogleAddress();

                break;

            case R.id.tv_destination_Address:

                showDestGoogleAddress();

                break;


            case R.id.layout_toAddress:

                showDestGoogleAddress();

                break;

        }

    }

    public void send_fav(String lattitude, String longitude, String address) {
        dest_lat=Double.parseDouble(lattitude);
        dest_lng=Double.parseDouble(longitude);
        to_address=address;

        Intent intent=new Intent(DropLocationActivity.this, DropOffActivity.class);
        intent.putExtra("dest_address", to_address);
        intent.putExtra("dest_lat", Double.toString(dest_lat));
        intent.putExtra("dest_long", Double.toString(dest_lng));
        intent.putExtra("from_address", from_address);
        intent.putExtra("from_lat", Double.toString(from_lat));
        intent.putExtra("from_long", Double.toString(from_lng));

        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }

    public void updateFavLocations() {
        new favouritePlaces().execute();
    }


    private class favouritePlaces extends AsyncTask<Void,Void,Void> {

        String response = null;
        boolean status;
        String message, message_ar;
        private ProgressDialog myDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(DropLocationActivity.this, getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (nw.isConnectingToInternet()) {

                JSONObject json = new JSONObject();
                try {

                    String finalUrl = ApiUrl.strBaseUrl+ApiUrl.favouriteLocations + "&API-KEY=1514209135"+"&customer_id="+strUserId;
                    Log.e("OfferFinalUrl", finalUrl);
                    ServiceHandler sh = new ServiceHandler();
                    response = sh.callToServer(finalUrl, ServiceHandler.GET, json);


                    JSONObject js = new JSONObject(response);
                    status = js.getBoolean("status");
                    message = js.getString("message");
                    Log.e("favouriteResponse", response.toString());
//                    message_ar = js.getString("message_ar");

                    if (status) {

                        if (strLanguage.equalsIgnoreCase("1") || strLanguage.isEmpty()) {
                            message = js.getString("message");
                        } else if (strLanguage.equalsIgnoreCase("2")) {
                            message = js.getString("message_ar");
                        }

                        JSONObject object=js.getJSONObject("rides");


                        JSONArray favoriteArray=object.getJSONArray("favorite");
                        JSONArray recentArray=object.getJSONArray("recent");
                        favouriteArrayList.clear();
                        recentArrayList.clear();
                        if(favoriteArray != null && favoriteArray.length() > 0){
                            for(int i=0; i<favoriteArray.length(); i++){

                                JSONObject fav_object=favoriteArray.getJSONObject(i);

                                String id= fav_object.getString("aid");
                                String user_id=fav_object.getString("user_id");
                                String lattitude=fav_object.getString("lattitude");
                                String longitude=fav_object.getString("longitude");
                                String address=fav_object.getString("address");
                                String type=fav_object.getString("type");

                                FavouriteModel favouritePojo= new FavouriteModel(id,user_id,lattitude,longitude,address,type);
                                favouriteArrayList.add(favouritePojo);
                            }

                        }


                        if(recentArray != null && recentArray.length() > 0){
                            for(int i=0; i<recentArray.length(); i++){

                                JSONObject recent_object=recentArray.getJSONObject(i);

                                String id= recent_object.getString("id");
                                String to_latitude= recent_object.getString("to_latitude");
                                String to_longitude=recent_object.getString("to_longitude");
                                String to_address= recent_object.getString("to_address");

                                RecentRideModel recentRidePojo= new RecentRideModel(id,to_latitude,to_longitude,to_address);
                                recentArrayList.add(recentRidePojo);
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

                    MessageToast.showToastMethod(DropLocationActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {
                        if(!favouriteArrayList.isEmpty()){
                            fav_adapter=new FavoriteAdapter(DropLocationActivity.this,false,favouriteArrayList);
                            fav_recycler.setHasFixedSize(true);
                            fav_recycler.setNestedScrollingEnabled(false);
                            fav_recycler.setAdapter(fav_adapter);
                        }

                        if(!recentArrayList.isEmpty()){
                            visitedAdapter=new RecentVisitedAdapter(DropLocationActivity.this,false,recentArrayList);
                            visited_recycler.setHasFixedSize(true);
                            visited_recycler.setNestedScrollingEnabled(false);
                            visited_recycler.setAdapter(visitedAdapter);
                        }

                    } else {
                        MessageToast.showToastMethod(DropLocationActivity.this, message);
                    }

                }
            } else {

                MessageToast.showToastMethod(DropLocationActivity.this, getString(R.string.check_net_connection));

            }

        }
    }

    private void showDestGoogleAddress() {
        layout_toAddress.setVisibility(View.GONE);
        ll_dest_autoComeplete.setVisibility(View.VISIBLE);

        // Initialize Places.
        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(DropLocationActivity.this);

        // Specify the types of place data to return.
        dest_autocomplete.setHint(getResources().getString(R.string.search_address_here));
        dest_autocomplete.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        dest_autocomplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                // TODO: Get info about the selected place.
                Log.i("sdfg", "Place: " + place.getName() + ", " +  place.getLatLng().latitude);

                dest_lat =place.getLatLng().latitude;
                dest_lng =place.getLatLng().longitude;

                //googlemap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 16));
                 to_address= getCompleteAddressString(place.getLatLng().latitude,place.getLatLng().longitude);

                if(from_address.length()>0 && to_address.length()>0){
                    Intent intent=new Intent(DropLocationActivity.this, DropOffActivity.class);
                    intent.putExtra("dest_address", to_address);
                    intent.putExtra("dest_lat", Double.toString(place.getLatLng().latitude));
                    intent.putExtra("dest_long", Double.toString(place.getLatLng().longitude));
                    intent.putExtra("from_address", from_address);
                    intent.putExtra("from_lat", Double.toString(from_lat));
                    intent.putExtra("from_long", Double.toString(from_lng));

                    setResult(RESULT_OK, intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                }else {
                    MessageToast.showToastMethod(DropLocationActivity.this,getString(R.string.please_add_your_address));
                }


                tv_destination_Address.setText(to_address);
                ll_dest_autoComeplete.setVisibility(View.GONE);
                layout_toAddress.setVisibility(View.VISIBLE);

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("agfd", "An error occurred: " + status);
            }
        });
    }

    private void showFromGoogleAddress() {
        img_cancel.setVisibility(View.GONE);
        tv_from_add.setVisibility(View.GONE);
        ll_from_autoComeplete.setVisibility(View.VISIBLE);

        // Initialize Places.
        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(DropLocationActivity.this);

        // Specify the types of place data to return.
        from_autocomplete.setHint(getResources().getString(R.string.search_address_here));
        from_autocomplete.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        from_autocomplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                // TODO: Get info about the selected place.
                Log.i("sdfg", "Place: " + place.getName() + ", " +  place.getLatLng().latitude);

                from_lat=place.getLatLng().latitude;
                from_lng=place.getLatLng().longitude;

                //googlemap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 16));
                 from_address= getCompleteAddressString(place.getLatLng().latitude,place.getLatLng().longitude);

                        /*Intent intent=new Intent(DropLocationActivity.this, HomeActivity.class);
                        intent.putExtra("from_address", address);
                        intent.putExtra("from_lat", Double.toString(lat));
                        intent.putExtra("from_long", Double.toString(lng));

                        setResult(RESULT_OK, intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
*/
                        tv_from_add.setText(from_address);
                ll_from_autoComeplete.setVisibility(View.GONE);
                img_cancel.setVisibility(View.VISIBLE);
                tv_from_add.setVisibility(View.VISIBLE);

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("agfd", "An error occurred: " + status);
            }
        });

    }

    public String getCompleteAddressString(Double LATITUDE, Double LONGITUDE) {
        String strAdd = "";
        // tv_from.setText("");
        Geocoder geocoder = new Geocoder(DropLocationActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
               // tv_from_add.setText(strAdd);
                if (strAdd.trim().isEmpty()) {
                    String address = addresses.get(0).getAddressLine(0);
                    strAdd = address;
                   // tv_from_add.setText(strAdd);
                }
                Log.d("Current location add", "" + strReturnedAddress.toString());
            } else {
                Log.d("My Current location add", "No Address returned!");
            }
        } catch (Exception e) {
            Log.e("getCompleteAddressSt: ", e.getMessage());
        }
        return strAdd;

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}

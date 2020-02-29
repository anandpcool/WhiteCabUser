package com.volive.whitecab.Activities;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.volive.whitecab.Adapters.RecyclerAdapters.ComplaintAdapter;
import com.volive.whitecab.Adapters.RecyclerAdapters.FavoriteAdapter;
import com.volive.whitecab.Adapters.RecyclerAdapters.RecentVisitedAdapter;
import com.volive.whitecab.DataModels.ComplaintModel;
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

public class PickLocationActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView fav_recycler,visited_recycler;
    FavoriteAdapter fav_adapter;
    RecentVisitedAdapter visitedAdapter;
    ImageView back_pickup;
    CardView cardView_pickup;
    TextView tv_from;
    String strAddress;
    AutocompleteSupportFragment autocompleteFragment;
    LinearLayout ll_auto_fragment,ll_from;
    double lat,lng;
    NetworkConnection nw;
    SessionManager sm;
    String strLanguage="",strUserId="",address;
    Boolean netConnection = false;
    Boolean nodata = false;
    ArrayList<FavouriteModel> favouriteArrayList;
    ArrayList<RecentRideModel> recentArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_location);

        initUI();
        initViews();

    }

    private void initUI() {
        nw=new NetworkConnection(PickLocationActivity.this);
        sm=new SessionManager(PickLocationActivity.this);
        favouriteArrayList=new ArrayList<>();
        recentArrayList=new ArrayList<>();
        HashMap<String, String> userDetail = sm.getUserDetails();
        strUserId = userDetail.get(SessionManager.KEY_ID);
        fav_recycler=findViewById(R.id.fav_recycler);
        visited_recycler=findViewById(R.id.visited_recycler);
        back_pickup=findViewById(R.id.back_pickup);
        cardView_pickup=findViewById(R.id.cardView_pickup);
        tv_from=findViewById(R.id.tv_from);
        ll_auto_fragment=findViewById(R.id.ll_auto_fragment);
        ll_from=findViewById(R.id.ll_from);

        if(getIntent().getExtras() != null){
            strAddress= getIntent().getStringExtra("strAddress");
            tv_from.setText(strAddress);
        }
    }

    private void initViews() {
        cardView_pickup.setOnClickListener(this);
        back_pickup.setOnClickListener(this);

        new favouritePlaces().execute();

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_pickup:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

            case R.id.cardView_pickup:

                ll_auto_fragment.setVisibility(View.VISIBLE);
                ll_from.setVisibility(View.GONE);

                // Initialize Places.
                Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
                // Create a new Places client instance.
                PlacesClient placesClient = Places.createClient(PickLocationActivity.this);

                // Initialize the AutocompleteSupportFragment.
                autocompleteFragment = (AutocompleteSupportFragment)
                        getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

                // Specify the types of place data to return.
                autocompleteFragment.setHint(getResources().getString(R.string.search_address_here));
                autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

                // Set up a PlaceSelectionListener to handle the response.
                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {

                        // TODO: Get info about the selected place.
                        Log.i("sdfg", "Place: " + place.getName() + ", " +  place.getLatLng().latitude);

                        lat=place.getLatLng().latitude;
                        lng=place.getLatLng().longitude;

                        //googlemap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 16));
                        address= getCompleteAddressString(place.getLatLng().latitude,place.getLatLng().longitude);

                        Intent intent=new Intent(PickLocationActivity.this, HomeActivity.class);
                        intent.putExtra("from_address", address);
                        intent.putExtra("from_lat", Double.toString(lat));
                        intent.putExtra("from_long", Double.toString(lng));

                        setResult(RESULT_OK, intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                        ll_auto_fragment.setVisibility(View.GONE);
                        ll_from.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onError(Status status) {
                        // TODO: Handle the error.
                        Log.i("agfd", "An error occurred: " + status);
                    }
                });


                /*startActivity(new Intent(PickLocationActivity.this, BookingActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
                break;

        }
    }

    public String getCompleteAddressString(Double LATITUDE, Double LONGITUDE) {
        String strAdd = "";
       // tv_from.setText("");
        Geocoder geocoder = new Geocoder(PickLocationActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                tv_from.setText(strAdd);
                if (strAdd.trim().isEmpty()) {
                    String address = addresses.get(0).getAddressLine(0);
                    strAdd = address;
                    tv_from.setText(strAdd);
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

    public void send_fav(String lattitude, String longitude, String address) {
        lat=Double.parseDouble(lattitude);
        lng=Double.parseDouble(longitude);
        this.address=address;

        Intent intent=new Intent(PickLocationActivity.this, HomeActivity.class);
        intent.putExtra("from_address", address);
        intent.putExtra("from_lat", Double.toString(lat));
        intent.putExtra("from_long", Double.toString(lng));

        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }

    public void updateFavLocations() {
        new favouritePlaces().execute();
    }


    private class favouritePlaces extends AsyncTask<Void,Void,Void>{

        String response = null;
        boolean status;
        String message, message_ar;
        private ProgressDialog myDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myDialog = DialogsUtils.showProgressDialog(PickLocationActivity.this, getString(R.string.please_wait));
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

                    MessageToast.showToastMethod(PickLocationActivity.this, getString(R.string.no_data));

                } else {

                    if (status) {
                        fav_adapter=new FavoriteAdapter(PickLocationActivity.this,true,favouriteArrayList);
                        fav_recycler.setHasFixedSize(true);
                        fav_recycler.setNestedScrollingEnabled(false);
                        fav_recycler.setAdapter(fav_adapter);

                        visitedAdapter=new RecentVisitedAdapter(PickLocationActivity.this,true,recentArrayList);
                        visited_recycler.setHasFixedSize(true);
                        visited_recycler.setNestedScrollingEnabled(false);
                        visited_recycler.setAdapter(visitedAdapter);
                    } else {
                        MessageToast.showToastMethod(PickLocationActivity.this, message);
                    }

                }
            } else {

                MessageToast.showToastMethod(PickLocationActivity.this, getString(R.string.check_net_connection));

            }

        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

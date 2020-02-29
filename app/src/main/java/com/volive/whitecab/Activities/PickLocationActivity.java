package com.volive.whitecab.Activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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
import com.volive.whitecab.Adapters.RecyclerAdapters.FavoriteAdapter;
import com.volive.whitecab.Adapters.RecyclerAdapters.RecentVisitedAdapter;
import com.volive.whitecab.R;

import java.util.Arrays;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_location);

        initUI();
        initViews();

    }

    private void initUI() {
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
        fav_adapter=new FavoriteAdapter(PickLocationActivity.this,true);
        fav_recycler.setHasFixedSize(true);
        fav_recycler.setNestedScrollingEnabled(false);
        fav_recycler.setAdapter(fav_adapter);
        visitedAdapter=new RecentVisitedAdapter(PickLocationActivity.this,true);
        visited_recycler.setHasFixedSize(true);
        visited_recycler.setNestedScrollingEnabled(false);
        visited_recycler.setAdapter(visitedAdapter);
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
                        String address= getCompleteAddressString(place.getLatLng().latitude,place.getLatLng().longitude);

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


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

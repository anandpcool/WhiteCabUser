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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.volive.whitecab.Adapters.RecyclerAdapters.FavoriteAdapter;
import com.volive.whitecab.Adapters.RecyclerAdapters.RecentVisitedAdapter;
import com.volive.whitecab.R;
import com.volive.whitecab.util.MessageToast;

import java.util.Arrays;
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
    double from_lat,from_lng, dest_lan, dest_lng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drop_location);

        initUI();
        initViews();

    }

    private void initUI() {
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
        fav_adapter=new FavoriteAdapter(DropLocationActivity.this, false);
        fav_recycler.setHasFixedSize(true);
        fav_recycler.setNestedScrollingEnabled(false);
        fav_recycler.setAdapter(fav_adapter);
        visitedAdapter=new RecentVisitedAdapter(DropLocationActivity.this, false);
        visited_recycler.setHasFixedSize(true);
        visited_recycler.setNestedScrollingEnabled(false);
        visited_recycler.setAdapter(visitedAdapter);
        cardView_fromTo.setOnClickListener(this);
        layout_toAddress.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_drop_off:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

            case R.id.cardView_fromTo:

                startActivity(new Intent(DropLocationActivity.this, DropOffActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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

                dest_lan=place.getLatLng().latitude;
                dest_lng =place.getLatLng().longitude;

                //googlemap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 16));
                String dest_address= getCompleteAddressString(place.getLatLng().latitude,place.getLatLng().longitude);

                if(from_address.length()>0 && dest_address.length()>0){
                    Intent intent=new Intent(DropLocationActivity.this, DropOffActivity.class);
                    intent.putExtra("dest_address", dest_address);
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


                tv_destination_Address.setText(dest_address);
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

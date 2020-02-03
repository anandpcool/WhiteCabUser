package com.volive.whitecab.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.volive.whitecab.Adapters.RecyclerAdapters.OfferAdapter;
import com.volive.whitecab.R;

public class OffersActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView offer_recycler;
    OfferAdapter adapter;
    ImageView back_offer;

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
    }

    private void initViews() {
        back_offer.setOnClickListener(this);
        adapter=new OfferAdapter(OffersActivity.this);
        offer_recycler.setHasFixedSize(true);
        offer_recycler.setAdapter(adapter);

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

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

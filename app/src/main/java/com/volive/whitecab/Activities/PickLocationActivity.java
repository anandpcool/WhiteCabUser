package com.volive.whitecab.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.volive.whitecab.Adapters.RecyclerAdapters.FavoriteAdapter;
import com.volive.whitecab.Adapters.RecyclerAdapters.RecentVisitedAdapter;
import com.volive.whitecab.R;

public class PickLocationActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView fav_recycler,visited_recycler;
    FavoriteAdapter fav_adapter;
    RecentVisitedAdapter visitedAdapter;
    ImageView back_pickup;
    CardView cardView_pickup;

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

                startActivity(new Intent(PickLocationActivity.this, BookingActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

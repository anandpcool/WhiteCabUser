package com.volive.whitecab.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.volive.whitecab.Adapters.RecyclerAdapters.FavoriteAdapter;
import com.volive.whitecab.Adapters.RecyclerAdapters.RecentVisitedAdapter;
import com.volive.whitecab.R;

public class DropLocationActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView fav_recycler,visited_recycler;
    FavoriteAdapter fav_adapter;
    RecentVisitedAdapter visitedAdapter;
    ImageView back_drop_off;
    CardView cardView_fromTo;
    EditText et_from_add;

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
        et_from_add=findViewById(R.id.et_from_add);
    }

    private void initViews() {

        if(getIntent().getExtras() !=null){

          String from_add =  getIntent().getStringExtra("from_address");
            et_from_add.setText(from_add);
        }


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

        }

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}

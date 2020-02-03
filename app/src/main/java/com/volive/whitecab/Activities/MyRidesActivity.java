package com.volive.whitecab.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.volive.whitecab.Adapters.RecyclerAdapters.MyRidesAdapter;
import com.volive.whitecab.R;

public class MyRidesActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView back_my_rides;
    RecyclerView my_ride_recycler;
    MyRidesAdapter ridesAdapter;
    TextView tv_completed,tv_schedule,tv_cancelled;
    int tv_value=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rides);

        initUI();
        initViews();
    }

    private void initUI() {
        back_my_rides=findViewById(R.id.back_my_rides);
        my_ride_recycler=findViewById(R.id.my_ride_recycler);
        tv_completed=findViewById(R.id.tv_completed);
        tv_schedule=findViewById(R.id.tv_schedule);
        tv_cancelled=findViewById(R.id.tv_cancelled);
    }

    private void initViews() {
        back_my_rides.setOnClickListener(this);
        tv_schedule.setOnClickListener(this);
        tv_completed.setOnClickListener(this);
        tv_cancelled.setOnClickListener(this);
        ridesAdapter=new MyRidesAdapter(MyRidesActivity.this);
        my_ride_recycler.setHasFixedSize(true);
        my_ride_recycler.setAdapter(ridesAdapter);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_my_rides:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

            case R.id.tv_completed:

                if(tv_value == 1){
                    tv_completed.setTextColor(getResources().getColor(R.color.black));
                    tv_schedule.setTextColor(getResources().getColor(R.color.lightGrey));
                    tv_cancelled.setTextColor(getResources().getColor(R.color.lightGrey));
                }else {
                    tv_value=1;
                }

                break;


            case R.id.tv_schedule:


                if(tv_value == 2){
                    tv_completed.setTextColor(getResources().getColor(R.color.lightGrey));
                    tv_schedule.setTextColor(getResources().getColor(R.color.black));
                    tv_cancelled.setTextColor(getResources().getColor(R.color.lightGrey));
                }else {
                    tv_value=2;
                }


                break;

            case R.id.tv_cancelled:
                if(tv_value == 3){
                    tv_completed.setTextColor(getResources().getColor(R.color.lightGrey));
                    tv_schedule.setTextColor(getResources().getColor(R.color.lightGrey));
                    tv_cancelled.setTextColor(getResources().getColor(R.color.black));
                }else {
                    tv_value=3;
                }

                break;

        }
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}

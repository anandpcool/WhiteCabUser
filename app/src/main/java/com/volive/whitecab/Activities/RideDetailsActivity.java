package com.volive.whitecab.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.volive.whitecab.R;

public class RideDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView back_ride_details;
    TextView tv_trip_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_details);

        initUI();
        initViews();

    }

    private void initUI() {
        back_ride_details=findViewById(R.id.back_ride_details);
        tv_trip_id=findViewById(R.id.tv_trip_id);
    }

    private void initViews() {
        back_ride_details.setOnClickListener(this);
        String text="<b> <font color=#000>"+getResources().getString(R.string.trip_id)+"</font> </b>"+" WC456723";
        tv_trip_id.setText(Html.fromHtml(text));
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_ride_details:

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

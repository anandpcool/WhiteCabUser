package com.volive.whitecab.Activities;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;

import com.volive.whitecab.Adapters.RecyclerAdapters.RideCancelAdapter;
import com.volive.whitecab.R;

public class TrackingActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView back_tracking;
    Button btn_cancel_ride;
    RideCancelAdapter cancelAdapter;
    String[] texts=new String[]{"Too many riders","Too much luggage","Rider requested cancel","Rider didn't answer","Wrong address shown","Other"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        initUI();
        initViews();

    }

    private void initViews() {
        back_tracking.setOnClickListener(this);
        btn_cancel_ride.setOnClickListener(this);
    }

    private void initUI() {
        back_tracking=findViewById(R.id.back_tracking);
        btn_cancel_ride=findViewById(R.id.btn_cancel_ride);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_tracking:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

            case R.id.btn_cancel_ride:

                openCancelAlert();

                break;

        }
    }

    private void openCancelAlert() {

        final Dialog dialog = new Dialog(TrackingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        dialog.setContentView(R.layout.cancel_ride_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        RecyclerView recyclerView=dialog.findViewById(R.id.cancel_recycler);
        ImageView img_cancel=dialog.findViewById(R.id.img_cancel);
        cancelAdapter=new RideCancelAdapter(TrackingActivity.this,texts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(cancelAdapter);

        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

            }
        });

        dialog.show();

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

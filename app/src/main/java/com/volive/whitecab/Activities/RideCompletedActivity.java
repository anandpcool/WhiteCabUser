package com.volive.whitecab.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.volive.whitecab.R;

public class RideCompletedActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView back_ride_completed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_completed);

        initUI();
        initViews();

    }

    private void initUI() {
        back_ride_completed=findViewById(R.id.back_ride_completed);
    }

    private void initViews() {
        back_ride_completed.setOnClickListener(this);

        openDialog();
    }

    private void openDialog() {
        final Dialog dialog = new Dialog(RideCompletedActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        dialog.setContentView(R.layout.ride_completed_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        Button btn_complaint,btn_rating;
        TextView tv_trip_id;
        tv_trip_id=dialog.findViewById(R.id.tv_trip_id);
        btn_complaint=dialog.findViewById(R.id.btn_complaint);
        btn_rating=dialog.findViewById(R.id.btn_rating);

        String text="<b> <font color=#000>"+getResources().getString(R.string.trip_id)+"</font> </b>"+" WC456723";
        tv_trip_id.setText(Html.fromHtml(text));

        btn_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(RideCompletedActivity.this, RatingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btn_complaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(RideCompletedActivity.this, ComplaintActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        dialog.show();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_ride_completed:

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

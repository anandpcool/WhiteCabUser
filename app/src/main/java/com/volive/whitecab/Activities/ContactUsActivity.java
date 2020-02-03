package com.volive.whitecab.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.volive.whitecab.R;

public class ContactUsActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView back_contact;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        initUI();
        initViews();
    }

    private void initUI() {
        back_contact=findViewById(R.id.back_contact);
    }

    private void initViews() {
        back_contact.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){


            case R.id.back_contact:

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

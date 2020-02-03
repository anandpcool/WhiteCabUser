package com.volive.whitecab.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.volive.whitecab.R;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView back_settings;
    TextView tv_sign_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initUI();
        initViews();

    }

    private void initUI() {
        back_settings=findViewById(R.id.back_settings);
        tv_sign_out=findViewById(R.id.tv_sign_out);
    }

    private void initViews() {
        back_settings.setOnClickListener(this);
        tv_sign_out.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_settings:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

            case R.id.tv_sign_out:

                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
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

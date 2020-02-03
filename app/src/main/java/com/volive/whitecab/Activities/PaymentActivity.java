package com.volive.whitecab.Activities;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.volive.whitecab.Adapters.ViewPagerAdapters.CardsViewPagerAdapter;
import com.volive.whitecab.R;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener {

    ViewPager cards_viewPager;
    CardsViewPagerAdapter cardsPager;
    int[] cards_images=new int[]{R.drawable.payment_card1,R.drawable.payment_card2,R.drawable.payment_card3};
    TextView tv_activity_name,tv_card_type,tv_card_details;
    ImageView back_payment,img_add;
    Button btn_pay_now;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initUI();
        initViews();
    }

    private void initUI() {
        cards_viewPager=findViewById(R.id.cards_viewPager);
        tv_activity_name=findViewById(R.id.tv_activity_name);
        back_payment=findViewById(R.id.back_payment);
        tv_card_type=findViewById(R.id.tv_card_type);
        tv_card_details=findViewById(R.id.tv_card_details);
        img_add=findViewById(R.id.img_add);
        btn_pay_now=findViewById(R.id.btn_pay_now);
    }

    private void initViews() {

        back_payment.setOnClickListener(this);
        btn_pay_now.setOnClickListener(this);
        img_add.setOnClickListener(this);


        /*if(getIntent().getStringExtra("type").equalsIgnoreCase(getString(R.string.payment))){

            tv_activity_name.setText(getString(R.string.payment));
            tv_card_type.setText(getString(R.string.select_your_card));
            btn_pay_now.setText(getString(R.string.pay_now));

        }else {
            tv_activity_name.setText(getString(R.string.add_card));
            tv_card_type.setText(getString(R.string.saved_card));
            img_add.setVisibility(View.GONE);
            tv_card_details.setVisibility(View.GONE);
            btn_pay_now.setText(getString(R.string.save));
        }*/

        cards_viewPager.setPageMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
        cardsPager=new CardsViewPagerAdapter(PaymentActivity.this,cards_images);
        cards_viewPager.setCurrentItem(1);
        cards_viewPager.setAdapter(cardsPager);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.back_payment:

                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

            case R.id.btn_pay_now:

                    if(btn_pay_now.getText().toString().equalsIgnoreCase(getString(R.string.pay_now))){

                        Intent intent=new Intent(PaymentActivity.this, TrackingActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    }else {
                        tv_activity_name.setText(getString(R.string.payment));
                        tv_card_type.setText(getString(R.string.select_your_card));
                        btn_pay_now.setText(getString(R.string.pay_now));
                    }

                break;

            case R.id.img_add:

                tv_activity_name.setText(getString(R.string.add_card));
                tv_card_type.setText(getString(R.string.saved_card));
                img_add.setVisibility(View.GONE);
                tv_card_details.setVisibility(View.GONE);
                btn_pay_now.setText(getString(R.string.save));

                break;

        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

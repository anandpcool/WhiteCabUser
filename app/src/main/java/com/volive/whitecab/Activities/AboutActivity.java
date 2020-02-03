package com.volive.whitecab.Activities;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.rd.PageIndicatorView;
import com.volive.whitecab.Adapters.ViewPagerAdapters.AboutPagerAdapter;
import com.volive.whitecab.R;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView back_about;
    ViewPager about_viewPager;
    AboutPagerAdapter adapter;
    PageIndicatorView circlePageIndicator;
    int[] about_images=new int[]{R.drawable.view_pager_image,R.drawable.view_pager_image,R.drawable.view_pager_image,R.drawable.view_pager_image,R.drawable.view_pager_image};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initUI();
        initViews();

    }

    private void initUI() {
        back_about=findViewById(R.id.back_about);
        about_viewPager=findViewById(R.id.about_viewPager);
        circlePageIndicator = findViewById(R.id.pager_indicator);
        circlePageIndicator.setViewPager(about_viewPager);
    }

    private void initViews() {
        back_about.setOnClickListener(this);

        //View Pager
        final float density = getResources().getDisplayMetrics().density;
        circlePageIndicator.setRadius(5 * density);
        about_viewPager.setPageMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
        adapter=new AboutPagerAdapter(AboutActivity.this, about_images);
        about_viewPager.setAdapter(adapter);
        circlePageIndicator.setCount(about_images.length);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){


            case R.id.back_about:

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

package com.volive.whitecab.Activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.volive.whitecab.Fragments.AddedFragment;
import com.volive.whitecab.Fragments.AllFragment;
import com.volive.whitecab.Fragments.PaidFragment;
import com.volive.whitecab.R;

import java.util.ArrayList;
import java.util.List;

public class WalletActivity extends AppCompatActivity {

    ImageView imageView;
    ViewPager wallet_viewpager;
    TabLayout tabLayout;
    private LinearLayout lLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        initUI();
        initViews();

    }

    private void initUI() {
        lLayout = (LinearLayout) findViewById(R.id.linearlayout2);
        imageView = (ImageView) findViewById(R.id.back_wallet);
        wallet_viewpager=findViewById(R.id.wallet_viewpager);
        tabLayout=findViewById(R.id.tabLayout);
    }

    private void initViews() {
        setupViewPager(wallet_viewpager);

        tabLayout.setupWithViewPager(wallet_viewpager);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AllFragment(), getString(R.string.all));
        adapter.addFragment(new PaidFragment(), getString(R.string.paid));
        adapter.addFragment(new AddedFragment(), getString(R.string.added));

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            if(mFragmentTitleList.size()!=0){
                return mFragmentTitleList.size();
            }else
                return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

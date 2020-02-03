package com.volive.whitecab.Adapters.ViewPagerAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.volive.whitecab.R;


public class AboutPagerAdapter extends PagerAdapter {

    Context context;
    int[] about_images;

    public AboutPagerAdapter(Context context, int[] about_images) {
        this.context=context;
        this.about_images=about_images;
    }

    @Override
    public int getCount() {
        return about_images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view==o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull View container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.offers_page_layout, null);

        ImageView img_offer = view.findViewById(R.id.img_offer);

        img_offer.setImageResource(about_images[position]);

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}

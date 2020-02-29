package com.volive.whitecab.Adapters.ViewPagerAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.volive.whitecab.DataModels.AboutPojo;
import com.volive.whitecab.R;

import java.util.ArrayList;


public class AboutPagerAdapter extends PagerAdapter {

    Context context;
    ArrayList<AboutPojo> about_images;
    String base_url;

    public AboutPagerAdapter(Context context, ArrayList<AboutPojo> about_images, String base_url) {
        this.context=context;
        this.about_images=about_images;
        this.base_url=base_url;
    }

    @Override
    public int getCount() {
        return about_images.size();
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
        Log.e("imagelink",base_url+about_images.get(position).getImage());
        Glide.with(context).load(base_url+about_images.get(position).getImage()).into(img_offer);

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

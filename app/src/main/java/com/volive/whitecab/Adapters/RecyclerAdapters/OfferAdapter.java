package com.volive.whitecab.Adapters.RecyclerAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.volive.whitecab.Activities.OffersActivity;
import com.volive.whitecab.DataModels.OfferPojo;
import com.volive.whitecab.R;

import java.util.ArrayList;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.MyHolder> {
    Context context;
    ArrayList<OfferPojo> offerPojoArrayList;
    String base_url;
    public OfferAdapter(Context context, ArrayList<OfferPojo> offerPojoArrayList, String base_url) {
        this.context=context;
        this.offerPojoArrayList=offerPojoArrayList;
        this.base_url=base_url;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.offer_adapter_layout,viewGroup,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        holder.tv_date.setText(offerPojoArrayList.get(position).getDate());
        Glide.with(context).load(base_url+offerPojoArrayList.get(position).getImage()).into(holder.offer_image);

    }

    @Override
    public int getItemCount() {
        return offerPojoArrayList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        ImageView offer_image;
        TextView tv_date;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            offer_image=itemView.findViewById(R.id.offer_image);
            tv_date=itemView.findViewById(R.id.tv_date);
        }
    }
}

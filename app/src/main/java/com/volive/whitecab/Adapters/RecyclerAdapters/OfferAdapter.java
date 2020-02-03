package com.volive.whitecab.Adapters.RecyclerAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.volive.whitecab.Activities.OffersActivity;
import com.volive.whitecab.R;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.MyHolder> {
    Context context;
    public OfferAdapter(Context context) {
        this.context=context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.offer_adapter_layout,viewGroup,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        if(position == 1){
            holder.tv_date.setText("December 01,2019");
            holder.offer_image.setImageDrawable(context.getResources().getDrawable(R.drawable.offer_image2));
        }


    }

    @Override
    public int getItemCount() {
        return 2;
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

package com.volive.whitecab.Adapters.RecyclerAdapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.volive.whitecab.Activities.RideDetailsActivity;
import com.volive.whitecab.R;

public class MyRidesAdapter extends RecyclerView.Adapter<MyRidesAdapter.MyHolder> {

    Context context;

    public MyRidesAdapter(Context context) {
        this.context=context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_rides_adapter_layout,viewGroup,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int i) {

        holder.cardView_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(context, RideDetailsActivity.class);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        CardView cardView_ride;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            cardView_ride=itemView.findViewById(R.id.cardView_ride);
        }
    }
}

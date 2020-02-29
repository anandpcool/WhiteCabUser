package com.volive.whitecab.Adapters.RecyclerAdapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.volive.whitecab.Activities.RideDetailsActivity;
import com.volive.whitecab.DataModels.HistoryModel;
import com.volive.whitecab.R;

import java.util.ArrayList;

public class MyRidesAdapter extends RecyclerView.Adapter<MyRidesAdapter.MyHolder> {

    Context context;
    ArrayList<HistoryModel> arrayList;

    public MyRidesAdapter(Context context, ArrayList<HistoryModel> arrayList) {
        this.context=context;
        this.arrayList=arrayList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_rides_adapter_layout,viewGroup,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int i) {

        holder.tv_destAddress.setText(arrayList.get(i).getTo_address());
        holder.tv_fromAddress.setText(arrayList.get(i).getFrom_address());
        holder.tv_ride_date.setText(arrayList.get(i).getTrip_date());

        if(arrayList.get(i).getFare().isEmpty()){
            holder.tv_ride_cost.setText("0 CAD");
        }else {
            holder.tv_ride_cost.setText(arrayList.get(i).getFare()+ " CAD");
        }


        holder.ride_rating.setRating(Float.valueOf(arrayList.get(i).getUser_rating()));

        holder.cardView_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(context, RideDetailsActivity.class);

                intent.putExtra("trip_id", arrayList.get(i).getTrip_id());
                intent.putExtra("trip_date", arrayList.get(i).getTrip_date());
                intent.putExtra("from_address", arrayList.get(i).getFrom_address());
                intent.putExtra("dest_address", arrayList.get(i).getTo_address());
                intent.putExtra("from_lat", arrayList.get(i).getFrom_latitude());
                intent.putExtra("from_long", arrayList.get(i).getFrom_longitude());
                intent.putExtra("dest_lat", arrayList.get(i).getTo_latitude());
                intent.putExtra("dest_long", arrayList.get(i).getTo_longitude());
                intent.putExtra("fare", arrayList.get(i).getFare());
                intent.putExtra("payment_type", arrayList.get(i).getPayment_type());
                intent.putExtra("captain_rating", arrayList.get(i).getCaptain_rating());
                intent.putExtra("user_rating", arrayList.get(i).getUser_rating());
                intent.putExtra("trip_distance", arrayList.get(i).getTrip_distance());
                intent.putExtra("vehicle_number", arrayList.get(i).getVehicle_number());
                intent.putExtra("vehicle_type", arrayList.get(i).getVehicle_type());
                intent.putExtra("trip_start_time", arrayList.get(i).getTrip_start_time());
                intent.putExtra("trip_end_time", arrayList.get(i).getTrip_end_time());
                intent.putExtra("driver_name", arrayList.get(i).getDriver_name());
                intent.putExtra("driver_email", arrayList.get(i).getDriver_email());
                intent.putExtra("driver_profile_pic", arrayList.get(i).getDriver_profile_pic());
                intent.putExtra("discount_price", arrayList.get(i).getDiscount_price());
                intent.putExtra("final_amount", arrayList.get(i).getFinal_amount());

                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        CardView cardView_ride;
        TextView tv_fromAddress,tv_destAddress,tv_ride_date,tv_ride_cost;
        RatingBar ride_rating;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tv_destAddress=itemView.findViewById(R.id.tv_destAddress);
            tv_fromAddress=itemView.findViewById(R.id.tv_fromAddress);
            cardView_ride=itemView.findViewById(R.id.cardView_ride);
            tv_ride_date=itemView.findViewById(R.id.tv_ride_date);
            tv_ride_cost=itemView.findViewById(R.id.tv_ride_cost);
            ride_rating=itemView.findViewById(R.id.ride_rating);
        }
    }
}

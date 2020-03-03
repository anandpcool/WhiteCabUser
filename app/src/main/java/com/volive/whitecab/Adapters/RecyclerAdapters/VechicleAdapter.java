package com.volive.whitecab.Adapters.RecyclerAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.volive.whitecab.Activities.ComplaintActivity;
import com.volive.whitecab.Activities.HomeActivity;
import com.volive.whitecab.R;

public class VechicleAdapter extends RecyclerView.Adapter<VechicleAdapter.MyHolder> {

    Context context;
    int[] vehicle_images;
    int selected=-1;
    String[] vehicle_texts;

    public VechicleAdapter(Context context, int[] vehicle_images, String[] vehicle_texts) {
        this.context=context;
        this.vehicle_images=vehicle_images;
        this.vehicle_texts=vehicle_texts;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vehicle_adapter_layout,viewGroup,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {

        holder.vehicle_text.setText(vehicle_texts[position]);
        holder.vehicle_image.setImageResource(vehicle_images[position]);

        if(position == 0){
            holder.vehicle_arrival_time.setText("15 Min");
        }else if(position == 3) {
            holder.vehicle_arrival_time.setText("20 Min");
        }

        holder.car_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selected=position;

//                ((HomeActivity)context).onItemSelect(position);
//
//                notifyDataSetChanged();

            }
        });

        if(selected == position){
            holder.car_cardView.setBackground(context.getResources().getDrawable(R.drawable.yellow_background));
        } else {
            holder.car_cardView.setBackground(context.getResources().getDrawable(R.drawable.white_background));
        }

    }

    @Override
    public int getItemCount() {
        return vehicle_images.length;
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        CardView car_cardView;
        TextView vehicle_text,vehicle_arrival_time;
        ImageView vehicle_image;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            vehicle_arrival_time=itemView.findViewById(R.id.txttime);
            vehicle_image=itemView.findViewById(R.id.vehicle_image);
            vehicle_text=itemView.findViewById(R.id.txtname);
            car_cardView=itemView.findViewById(R.id.car_cardView);
        }
    }
}

package com.volive.whitecab.Adapters.RecyclerAdapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.volive.whitecab.Activities.BookingActivity;
import com.volive.whitecab.Activities.DropOffActivity;
import com.volive.whitecab.R;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.MyHolder> {

    Context context;
    boolean check;

    public FavoriteAdapter(Context context, boolean check) {
        this.context=context;
        this.check=check;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fav_adapter_layout,viewGroup,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        if(position == 1){
            holder.tv_address_title.setText("Work");
            holder.tv_address_desc.setText("1933 Mountain Rd, Moncton");
        }else if(position == 2){
            holder.tv_address_title.setText("Church");
            holder.tv_address_desc.setText("The Church of Jesus Christ of Latter-day Saints");
        }

        holder.ll_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(check){
                    context.startActivity(new Intent(context, BookingActivity.class));
                }else {
                    context.startActivity(new Intent(context, DropOffActivity.class));
                }



            }
        });

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        TextView tv_address_title,tv_address_desc;
        RelativeLayout ll_fav;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            ll_fav=itemView.findViewById(R.id.ll_fav);
            tv_address_title=itemView.findViewById(R.id.tv_address_title);
            tv_address_desc=itemView.findViewById(R.id.tv_address_desc);
        }
    }
}

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

public class RecentVisitedAdapter extends RecyclerView.Adapter<RecentVisitedAdapter.MyHolder> {

    Context context;
    boolean check;

    public RecentVisitedAdapter(Context context, boolean check) {
        this.context=context;
        this.check=check;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.visited_adapter_layout,viewGroup,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        if(position == 1){
            holder.tv_address_title.setText("IKEA Moncton Collection Point");
            holder.tv_address_desc.setText("623 Mapleton Rd, Moncton, NB E1G 2k5");
        }else if(position == 2){
            holder.tv_address_title.setText("Holland Park");
            holder.tv_address_desc.setText("100 Holland Moncton, NB E1G 0x1, Canada");
        }


        holder.ll_recent.setOnClickListener(new View.OnClickListener() {
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
        RelativeLayout ll_recent;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tv_address_title=itemView.findViewById(R.id.tv_address_title);
            tv_address_desc=itemView.findViewById(R.id.tv_address_desc);
            ll_recent=itemView.findViewById(R.id.ll_recent);
        }
    }
}

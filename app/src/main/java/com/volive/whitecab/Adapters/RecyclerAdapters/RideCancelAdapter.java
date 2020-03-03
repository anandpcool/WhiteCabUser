package com.volive.whitecab.Adapters.RecyclerAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.volive.whitecab.Activities.TrackingActivity;
import com.volive.whitecab.DataModels.ComplaintModel;
import com.volive.whitecab.R;

import java.util.ArrayList;

public class RideCancelAdapter extends RecyclerView.Adapter<RideCancelAdapter.MyHolder> {

    Context context;
    ArrayList<ComplaintModel> arrayList;
    private int selected= -1;

    public RideCancelAdapter(Context context, ArrayList<ComplaintModel> arrayList) {
        this.context=context;
        this.arrayList=arrayList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cancel_adapter_layout,viewGroup,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {

        holder.tv_reason.setText(arrayList.get(position).getComplaint_title());


        holder.rl_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selected=position;

                ((TrackingActivity)context).onItemSelect(arrayList.get(position).getComplaint_title());

                notifyDataSetChanged();

            }
        });

        if(selected == position){
            holder.img_check.setVisibility(View.VISIBLE);
        } else {
            holder.img_check.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        TextView tv_reason;
        RelativeLayout rl_cancel;
        ImageView img_check;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            img_check=itemView.findViewById(R.id.img_check);
            rl_cancel=itemView.findViewById(R.id.rl_cancel);
            tv_reason=itemView.findViewById(R.id.tv_reason);
        }
    }
}

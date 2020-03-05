package com.volive.whitecab.Adapters.RecyclerAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.volive.whitecab.DataModels.RegionModel;
import com.volive.whitecab.R;
import com.volive.whitecab.util.AdapterCallBack;
import com.volive.whitecab.util.SessionManager;
import java.util.ArrayList;

import static com.volive.whitecab.Activities.SignupActivity.txt_countryCode;

/**
 * Created by volive on 7/20/2018.
 */

public class RegionAdapter extends RecyclerView.Adapter<RegionAdapter.ViewHolder>{
    Context context;
    AdapterCallBack adapterCallBack;
    ArrayList<RegionModel> regionModels;
    SessionManager sessionManager;
    String image;

    public RegionAdapter(Context context, AdapterCallBack adapterCallBack, ArrayList<RegionModel> regionModels, String yes) {
        this.context=context;
        this.adapterCallBack=adapterCallBack;
        this.regionModels=regionModels;
        sessionManager=new SessionManager(context);
        this.image=yes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.region_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
       // String selectlang=sessionManager.getSingleField(SessionManager.KEY_LANGUAGE);
        String selectlang="1";
        RegionModel regionModel=regionModels.get(position);
        if(image.equalsIgnoreCase("yes")){
            Glide.with(context).load(regionModel.getIs_unep()).into(holder.flag);
        }else {
               holder.flag.setVisibility(View.GONE);
        }
        if(selectlang.equalsIgnoreCase("1")){
            holder.textView.setText(regionModel.getName());
        }else {
            holder.textView.setText(regionModel.getName());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(adapterCallBack!=null){
                    adapterCallBack.onClickCallback(position);

                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return regionModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        LinearLayout linear;
        ImageView flag;


        public ViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.religion);
            flag = (ImageView) itemView.findViewById(R.id.flag);


        }
    }
}

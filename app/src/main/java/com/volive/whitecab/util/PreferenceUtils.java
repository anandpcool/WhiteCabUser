package com.volive.whitecab.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtils  {

    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public static String RIDE_TYPE="ride_type";
    public static String TRIP_ID="trip_id";

    public PreferenceUtils(Context context) {
        this.context=context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor=preferences.edit();
    }

    public void setRideType(String type) {
        editor.putString(RIDE_TYPE,type);
        editor.apply();
        editor.commit();
    }

    public String getRideType(){
        return preferences.getString(RIDE_TYPE,"");
    }


    public void setTripId(String type) {
        editor.putString(TRIP_ID,type);
        editor.apply();
        editor.commit();
    }

    public String getTripId(){
        return preferences.getString(TRIP_ID,"");
    }


}

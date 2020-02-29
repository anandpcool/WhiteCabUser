package com.volive.whitecab.DataModels;

public class RecentRideModel {
    String id,to_latitude,to_longitude,to_address;
    public RecentRideModel(String id, String to_latitude, String to_longitude, String to_address) {
        this.id=id;
        this.to_latitude=to_latitude;
        this.to_longitude=to_longitude;
        this.to_address=to_address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTo_latitude() {
        return to_latitude;
    }

    public void setTo_latitude(String to_latitude) {
        this.to_latitude = to_latitude;
    }

    public String getTo_longitude() {
        return to_longitude;
    }

    public void setTo_longitude(String to_longitude) {
        this.to_longitude = to_longitude;
    }

    public String getTo_address() {
        return to_address;
    }

    public void setTo_address(String to_address) {
        this.to_address = to_address;
    }
}

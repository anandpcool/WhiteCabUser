package com.volive.whitecab.DataModels;

public class FavouriteModel {
    String id,user_id,lattitude,longitude,address,type;
    public FavouriteModel(String id, String user_id, String lattitude, String longitude, String address, String type) {
        this.id=id;
        this.user_id=user_id;
        this.lattitude=lattitude;
        this.longitude=longitude;
        this.address=address;
        this.type=type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

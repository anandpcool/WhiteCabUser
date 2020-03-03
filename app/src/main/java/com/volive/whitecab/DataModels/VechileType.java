package com.volive.whitecab.DataModels;

import java.io.Serializable;

/**
 * Created by VOLIVE on 1/11/2018.
 */

public class VechileType implements Serializable {

    String driver_id, lattitude, longitude, distance, vehicle_type, time, cab_type_image,vehicle_type_ar,capacity;

    private boolean iseTextVisible;

    public VechileType(String driver_id, String lattitude, String longitude, String distance, String vehicle_type, String time, String cab_type_image, String vehicle_type_ar, String capacity) {
        this.driver_id = driver_id;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.distance = distance;
        this.vehicle_type = vehicle_type;
        this.time = time;
        this.cab_type_image = cab_type_image;
        this.vehicle_type_ar = vehicle_type_ar;
        this.capacity=capacity;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getVehicle_type_ar() {
        return vehicle_type_ar;
    }

    public void setVehicle_type_ar(String vehicle_type_ar) {
        this.vehicle_type_ar = vehicle_type_ar;
    }

    public void setIseTextVisible(boolean iseTextVisible) {
        this.iseTextVisible = iseTextVisible;
    }

    public boolean iseTextVisible() {
        return iseTextVisible;
    }

    public String getCab_type_image() {
        return cab_type_image;
    }

    public void setCab_type_image(String cab_type_image) {
        this.cab_type_image = cab_type_image;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }
}

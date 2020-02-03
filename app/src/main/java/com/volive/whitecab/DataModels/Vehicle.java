package com.volive.whitecab.DataModels;

import java.io.Serializable;

/**
 * Created by VOLIVE on 1/10/2018.
 */

public class Vehicle implements Serializable {

    String vehicle_id,vehicle_type,vehicle_name,driver_id,lattitude,longitude,distance,vehicle_number;

    public Vehicle(String vehicle_id, String vehicle_type, String vehicle_name, String driver_id, String lattitude, String longitude, String distance, String vehicle_number) {
        this.vehicle_id = vehicle_id;
        this.vehicle_type = vehicle_type;
        this.vehicle_name = vehicle_name;
        this.driver_id = driver_id;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.distance = distance;
        this.vehicle_number = vehicle_number;
    }


    public String getVehicle_number() {
        return vehicle_number;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
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

    public String getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public String getVehicle_name() {
        return vehicle_name;
    }

    public void setVehicle_name(String vehicle_name) {
        this.vehicle_name = vehicle_name;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }
}

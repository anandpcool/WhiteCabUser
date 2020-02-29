package com.volive.whitecab.DataModels;

import java.io.Serializable;

/**
 * Created by VOLIVE on 2/2/2018.
 */

public class HistoryModel implements Serializable {

    String captain_rating,promo_code,payment_type,trip_distance,vehicle_number,vehicle_type,trip_start_time,trip_end_time,driver_name,driver_email,driver_profile_pic,discount_price,final_amount;

    String id,trip_id,from_address,from_latitude,from_longitude,to_address,to_latitude,to_longitude,trip_date,fare,user_rating;

    public HistoryModel(String id, String trip_id, String from_address, String from_latitude, String from_longitude, String to_address, String to_latitude, String to_longitude,String fare, String captain_rating, String promo_code, String payment_type, String trip_distance, String vehicle_number, String vehicle_type, String trip_start_time, String trip_end_time,String driver_name,String driver_email,String driver_profile_pic,String discount_price, String final_amount,String user_rating,String trip_date) {
        this.id = id;
        this.trip_id = trip_id;
        this.from_address = from_address;
        this.from_latitude = from_latitude;
        this.from_longitude = from_longitude;
        this.to_address = to_address;
        this.to_latitude = to_latitude;
        this.to_longitude = to_longitude;
        this.trip_date = trip_date;
        this.fare = fare;

        this.captain_rating=captain_rating;
        this.user_rating=user_rating;
        this.promo_code=promo_code;
        this.payment_type=payment_type;
        this.trip_distance=trip_distance;
        this.vehicle_number=vehicle_number;
        this.vehicle_type=vehicle_type;
        this.trip_start_time=trip_start_time;
        this.trip_end_time=trip_end_time;

        this.driver_name=driver_name;
        this.driver_email=driver_email;
        this.driver_profile_pic=driver_profile_pic;
        this.discount_price=discount_price;
        this.final_amount=final_amount;
    }


    public String getUser_rating() {
        return user_rating;
    }

    public void setUser_rating(String user_rating) {
        this.user_rating = user_rating;
    }

    public String getDiscount_price() {
        return discount_price;
    }

    public void setDiscount_price(String discount_price) {
        this.discount_price = discount_price;
    }

    public String getFinal_amount() {
        return final_amount;
    }

    public void setFinal_amount(String final_amount) {
        this.final_amount = final_amount;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getDriver_email() {
        return driver_email;
    }

    public void setDriver_email(String driver_email) {
        this.driver_email = driver_email;
    }

    public String getDriver_profile_pic() {
        return driver_profile_pic;
    }

    public void setDriver_profile_pic(String driver_profile_pic) {
        this.driver_profile_pic = driver_profile_pic;
    }

    public String getTrip_start_time() {
        return trip_start_time;
    }

    public void setTrip_start_time(String trip_start_time) {
        this.trip_start_time = trip_start_time;
    }

    public String getTrip_end_time() {
        return trip_end_time;
    }

    public void setTrip_end_time(String trip_end_time) {
        this.trip_end_time = trip_end_time;
    }

    public String getCaptain_rating() {
        return captain_rating;
    }

    public void setCaptain_rating(String captain_rating) {
        this.captain_rating = captain_rating;
    }

    public String getPromo_code() {
        return promo_code;
    }

    public void setPromo_code(String promo_code) {
        this.promo_code = promo_code;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getTrip_distance() {
        return trip_distance;
    }

    public void setTrip_distance(String trip_distance) {
        this.trip_distance = trip_distance;
    }

    public String getVehicle_number() {
        return vehicle_number;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }


    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    public String getFrom_address() {
        return from_address;
    }

    public void setFrom_address(String from_address) {
        this.from_address = from_address;
    }

    public String getFrom_latitude() {
        return from_latitude;
    }

    public void setFrom_latitude(String from_latitude) {
        this.from_latitude = from_latitude;
    }

    public String getFrom_longitude() {
        return from_longitude;
    }

    public void setFrom_longitude(String from_longitude) {
        this.from_longitude = from_longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTo_address() {
        return to_address;
    }

    public void setTo_address(String to_address) {
        this.to_address = to_address;
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

    public String getTrip_date() {
        return trip_date;
    }

    public void setTrip_date(String trip_date) {
        this.trip_date = trip_date;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }


}

package com.volive.whitecab.DataModels;

public class OfferPojo {

    String date, image;

    public OfferPojo(String date, String image) {
        this.date=date;
        this.image=image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

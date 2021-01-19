package com.agnet.lete_drop.models;

public class Partner {
    int id;
    String name, business_name, phone;
    double lng, lat;
    public Partner(int id,double lng, double lat, String phone){
        this.id = id;
        this.lng =  lng;
        this.lat = lat;
        this.phone = phone;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    public String getPhone() {
        return phone;
    }

    public int getId() {
        return id;
    }
}

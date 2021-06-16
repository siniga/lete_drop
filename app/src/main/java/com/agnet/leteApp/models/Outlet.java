package com.agnet.leteApp.models;

import com.google.gson.annotations.SerializedName;

public class Outlet {

    private int id;
    private String name;
    private  Double lat, lng;

    @SerializedName("qr_code")
    private  String qrCode;

    public  Outlet(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }
}

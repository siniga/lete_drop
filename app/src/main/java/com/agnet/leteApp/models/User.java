package com.agnet.leteApp.models;

import com.google.gson.annotations.SerializedName;

public class User {

    private int id;
    private String phone, name;


    @SerializedName("agency_id")
    int agencyId;


    public User(int id, String phone, String name,int agencyId) {

        this.phone = phone;
        this.name =  name;
        this.agencyId = agencyId;

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

}


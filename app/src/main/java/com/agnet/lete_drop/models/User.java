package com.agnet.lete_drop.models;

import com.google.gson.annotations.SerializedName;

public class User {

    private int localId, numPlate;
    private String phone, name;


    @SerializedName("avatar")
    private String img;

    @SerializedName("id")
    private int serverId;

    @SerializedName("saler_id")
    private int salerId;



    public User(int localId, String phone, String name,String img, int numPlate, int serverId, int salerId) {
        this.localId = localId;
        this.phone = phone;
        this.name =  name;
        this.numPlate = numPlate;
        this.serverId = serverId;
        this.salerId = salerId;
        this.img = img;
    }

    public int getId() {
        return localId;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public int getNumPlate() {
        return numPlate;
    }

    public int getServerId() {
        return serverId;
    }

    public int getSalerId() {
        return salerId;
    }

    public String getImg() {
        return img;
    }
}


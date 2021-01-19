package com.agnet.lete_drop.models;

import com.google.gson.annotations.SerializedName;

public class User {

    int localId, numPlate;
    String phone, name;

    @SerializedName("id")
    int serverId;



    public User(int localId, String phone, String name, int numPlate, int serverId) {
        this.localId = localId;
        this.phone = phone;
        this.name =  name;
        this.numPlate = numPlate;
        this.serverId = serverId;
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
}


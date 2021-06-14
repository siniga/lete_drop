package com.agnet.leteApp.models;

import com.google.gson.annotations.SerializedName;

public class Project {

    int id;
    String name, description, img, date, type;

    @SerializedName("client_id")
    int clientId;

    String client;


    public Project(int id,  String name, String description, String img, int clientId, String date, String type, String client){
       this.id = id;
       this.name =name;
       this.description = description;
       this.img = img;
       this.clientId = clientId;
       this.date = date;
       this.type = type;
       this.client = client;

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public int getClientId() {
        return clientId;
    }

    public String getDescription() {
        return description;
    }

    public String getClient() {
        return client;
    }

}

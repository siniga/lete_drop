package com.agnet.leteApp.models;

public class Category {

    int id, serverId;
    String  name,photo ;

    public Category(int id, String name, String photo, int serverId){
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.serverId = serverId;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return photo;
    }

    public int getServerId() {
        return serverId;
    }
}

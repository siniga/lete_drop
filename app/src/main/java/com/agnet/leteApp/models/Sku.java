package com.agnet.leteApp.models;

public class Sku {

    int id, serverId;
    String  name;



    public Sku(int id, String name, String imgUrl, int serverId){
        this.id = id;
        this.name = name;
        this.serverId = serverId;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getServerId() {
        return serverId;
    }
}

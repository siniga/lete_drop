package com.agnet.leteApp.models;

public class Client {

    int id;
    String  name,photo ;

    public Client(int id, String name, String photo){
        this.id = id;
        this.name = name;
        this.photo = photo;
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

}

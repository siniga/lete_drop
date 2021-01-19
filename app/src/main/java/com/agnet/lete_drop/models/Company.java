package com.agnet.lete_drop.models;

public class Company {

    int id;
    String  name,photo ;

    public Company(int id, String name, String photo){
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

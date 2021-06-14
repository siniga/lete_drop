package com.agnet.leteApp.models;

public class Street {

    int id;
    String  name;


    public Street(int id, String name){
        this.id = id;
        this.name = name;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

package com.agnet.lete_drop.models;

public class CustomerType {

    private int id;
    private String name;

    public  CustomerType(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}



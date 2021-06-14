package com.agnet.leteApp.models;

public class Option {

    private int id;
    private String option;

    public Option(String option){
        this.option = option;
    }

    public int getId() {
        return id;
    }

    public String getOption() {
        return option;
    }

    @Override
    public String toString() {
        return this.option;
    }
}

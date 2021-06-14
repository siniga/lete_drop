package com.agnet.leteApp.models;

public class OutletImage {

    private String name, img;

    public OutletImage(String img, String name) {
        this.name = name;
        this.img = img;

    }

    public String getImg() {
        return img;
    }

    public String getName() {
        return name;
    }

}

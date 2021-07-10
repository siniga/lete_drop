package com.agnet.leteApp.models;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class MerchandiseImg {
    private int id;
    private Bitmap imageView;

    public MerchandiseImg(int id,Bitmap img){
        this.id = id;
        this.imageView = img;
    }

    public Bitmap getImageView() {
        return imageView;
    }

    public int getId() {
        return id;
    }
}

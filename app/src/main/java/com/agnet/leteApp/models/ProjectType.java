package com.agnet.leteApp.models;

import com.google.gson.annotations.SerializedName;

public class ProjectType {

    int id;
    String name, icon;
    int stats;


    public ProjectType(int id, String name, String icon, int stats){
       this.id = id;
       this.name =name;
       this.icon = icon;
       this.stats = stats;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }


    @Override
    public String toString() {
        return name;
    }

    public int getStats() {
        return stats;
    }
}

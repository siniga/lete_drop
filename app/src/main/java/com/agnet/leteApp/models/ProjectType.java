package com.agnet.leteApp.models;

import com.google.gson.annotations.SerializedName;

public class ProjectType {

    int id;
    String name, icon, selectedIcon;


    public ProjectType(int id, String name, String icon, String selectedIcon){
       this.id = id;
       this.name =name;
       this.icon = icon;
       this.selectedIcon = selectedIcon;
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

    public String getSelectedIcon() {
        return selectedIcon;
    }
}

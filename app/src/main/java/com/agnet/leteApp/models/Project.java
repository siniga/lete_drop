package com.agnet.leteApp.models;

import com.google.gson.annotations.SerializedName;

public class Project {

    int id;
    String name, description, img, date, type;

    @SerializedName("client_id")
    int clientId;

    int target;
    float revenue;

    @SerializedName("revenue_formated")
    String formattedRevenue;

    @SerializedName("revenue_target")
    float revenueTarget;

    int mapping;

    int merchandise;


    String client;


    public Project(int id, String name, String description, String img, int clientId, String date,
                   String type, String client, int target, int mapping, float revenue,
                   float revenueTarget, String formattedRevenue, int merchandise) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.img = img;
        this.clientId = clientId;
        this.date = date;
        this.type = type;
        this.client = client;
        this.target = target;
        this.mapping = mapping;
        this.revenue = revenue;
        this.revenueTarget = revenueTarget;
        this.formattedRevenue = formattedRevenue;
        this.merchandise = merchandise;

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public int getClientId() {
        return clientId;
    }

    public String getDescription() {
        return description;
    }

    public String getClient() {
        return client;
    }

    public float getRevenue() {
        return revenue;
    }

    public String getFormattedRevenue() {
        return formattedRevenue;
    }

    public float getRevenueTarget() {
        return revenueTarget;
    }

    public int getMapping() {
        return mapping;
    }

    public int getMerchandise() {
        return merchandise;
    }

    public int getTarget() {
        return target;
    }
}

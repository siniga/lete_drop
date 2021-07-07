package com.agnet.leteApp.models;

import com.google.gson.annotations.SerializedName;

public class Stat {

    float revenue;

    @SerializedName("revenue_target")
    float revenueTarget;

    @SerializedName("revenue_formatted")
    String revenueFormatted;

    @SerializedName("revenue_target_formatted")
    String revenueTargetFormatted;


    @SerializedName("mapping_count")
    int mappingCount;

    @SerializedName("mapping_target")
    int mappingTarget;

    @SerializedName("merchandise_count")
    int merchandiseCount;

    @SerializedName("merchandise_target")
    int merchandiseTarget;

    @SerializedName("outlet_count")
    int outletCount;

    @SerializedName("outlet_target")
    int outletTarget;


    public Stat(float revenue, float revenueTarget, String revenueFormatted, String revenueTargetFormatted,int mappingCount, int mappingTarget, int merchandiseCount, int merchandiseTarget, int outletCount, int outletTarget){
        this.revenue = revenue;
        this.revenueTarget = revenueTarget;
        this.mappingCount = mappingCount;
        this.mappingTarget = mappingTarget;
        this.merchandiseCount = merchandiseCount;
        this.merchandiseTarget = merchandiseTarget;
        this.outletCount = outletCount;
        this.outletTarget = outletTarget;
        this.revenueFormatted = revenueFormatted;
        this.revenueTargetFormatted = revenueTargetFormatted;
    }

    public float getRevenue() {
        return revenue;
    }

    public float getRevenueTarget() {
        return revenueTarget;
    }

    public String getRevenueFormatted() {
        return revenueFormatted;
    }

    public String getRevenueTargetFormatted() {
        return revenueTargetFormatted;
    }

    public int getMappingCount() {
        return mappingCount;
    }

    public int getMappingTarget() {
        return mappingTarget;
    }

    public int getMerchandiseCount() {
        return merchandiseCount;
    }

    public int getMerchandiseTarget() {
        return merchandiseTarget;
    }

    public int getOutletCount() {
        return outletCount;
    }

    public int getOutletTarget() {
        return outletTarget;
    }




}

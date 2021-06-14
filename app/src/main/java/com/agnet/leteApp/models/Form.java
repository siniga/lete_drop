package com.agnet.leteApp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Form {

    private int id;
    private String  name,created_at;

    @SerializedName("user")
    private String created_by;

    private int status;

    @SerializedName("questions")
    List<Quesionnaire> questions;

    public Form(int id, String name, String created_at, String created_by, int status){
        this.id = id;
        this.name = name;
        this.created_at = created_at;
        this.created_by = created_by;
        this.status = status;
    }

    public void setQuestions(List<Quesionnaire> questions) {
        this.questions = questions;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getCreated_by() {
        return created_by;
    }

    public int getStatus() {
        return status;
    }

    public List<Quesionnaire> getQuestions() {
        return questions;
    }
}

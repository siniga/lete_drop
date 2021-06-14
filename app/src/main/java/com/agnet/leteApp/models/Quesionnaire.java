package com.agnet.leteApp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Quesionnaire {

    private int id;

    @SerializedName("control_type_id")
    private int typeId;
    private String question;


    @SerializedName("question_options")
    private List<Option> options;


    public Quesionnaire(int id, int typeId, String question,List<Option> options){
        this.typeId = typeId;
        this.question = question;
        this.options = options;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getTypeId() {
        return typeId;
    }

    public String getQuestion() {
        return question;
    }

    public List<Option> getOptions() {
        return options;
    }
}

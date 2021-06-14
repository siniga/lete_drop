package com.agnet.leteApp.models;

public class Answer {
    String answer;
    int questionId;

    public Answer(String answer, int questionId) {
        this.answer = answer;
        this.questionId = questionId;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public String getAnswer() {
        return answer;
    }
}

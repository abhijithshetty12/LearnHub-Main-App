package com.example.learnhub.model;

public class QuizResultModel {
    private String username , score;

    public QuizResultModel(String username, String score) {
        this.username = username;
        this.score = score;
    }

    public QuizResultModel() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}

package com.example.learnhub.model;

import java.util.List;

public class QuizModel {
    private String quizQuestion,correctAnswer,userAnswer;
    List<String> options ;

    public QuizModel() {
    }

    public QuizModel( String quizQuestion, List<String> options,String correctAnswer) {
        this.quizQuestion = quizQuestion;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.userAnswer = "";
    }



    public String getQuizQuestion() {
        return quizQuestion;
    }

    public void setQuizQuestion(String quizQuestion) {
        this.quizQuestion = quizQuestion;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }
}

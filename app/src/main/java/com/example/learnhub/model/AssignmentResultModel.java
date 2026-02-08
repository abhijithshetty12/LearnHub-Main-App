package com.example.learnhub.model;

public class AssignmentResultModel {
    private String username , submissionStatus,uid;

    public AssignmentResultModel() {
    }

    public AssignmentResultModel(String username, String submissionStatus) {
        this.username = username;
        this.submissionStatus = submissionStatus;
    }

    public AssignmentResultModel(String username, String submissionStatus, String uid) {
        this.username = username;
        this.submissionStatus = submissionStatus;
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSubmissionStatus() {
        return submissionStatus;
    }

    public void setSubmissionStatus(String submissionStatus) {
        this.submissionStatus = submissionStatus;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

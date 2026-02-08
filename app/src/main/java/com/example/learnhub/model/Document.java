package com.example.learnhub.model;

import java.util.List;
import java.util.Map;

public class Document {
    private String topic , description;
    private List<String> documentList;


    public Document(String topic, String description, List< String> documentList) {
        this.topic = topic;
        this.description = description;
        this.documentList = documentList;
    }

    public Document(String topic, List< String> documentList) {
        this.topic = topic;
        this.documentList = documentList;
    }

    public Document() {
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getDocumentList() {
        return documentList;
    }

    public void setDocumentList(List<String> documentList) {
        this.documentList = documentList;
    }
}

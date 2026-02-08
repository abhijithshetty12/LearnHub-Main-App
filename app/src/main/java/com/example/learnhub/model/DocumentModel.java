package com.example.learnhub.model;

import android.net.Uri;

public class DocumentModel {
    private String fileName;
    private Uri fileURI;

    public DocumentModel(String fileName, Uri fileURI) {
        this.fileName = fileName;
        this.fileURI = fileURI;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Uri getFileURI() {
        return fileURI;
    }

    public void setFileURI(Uri fileURI) {
        this.fileURI = fileURI;
    }
}


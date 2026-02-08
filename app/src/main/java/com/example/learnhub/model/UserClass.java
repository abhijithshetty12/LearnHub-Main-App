package com.example.learnhub.model;

public class UserClass {
    String className , classSubject,userName,userEmail,userType,classcode;

    public UserClass(String className, String classSubject, String userName, String userEmail, String userType,String classcode) {
        this.className = className;
        this.classSubject = classSubject;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userType = userType;
        this.classcode = classcode;
    }

    public UserClass() {
    }

    public UserClass(String studentname,String userEmail,String usertype) {
        this.userEmail =userEmail;
        this.userName =studentname;
        this.userType =usertype;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassSubject() {
        return classSubject;
    }

    public void setClassSubject(String classSubject) {
        this.classSubject = classSubject;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getClasscode() {
        return classcode;
    }

    public void setClasscode(String classcode) {
        this.classcode = classcode;
    }
}

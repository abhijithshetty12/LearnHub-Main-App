package com.example.learnhub;

public class Join {
    private String username ,email,Classname ,ClassCode,Section,Subject;


    public Join() {
    }

    public Join(String username, String email, String classname, String classCode) {
        this.username = username;
        this.email = email;
        Classname = classname;
        ClassCode = classCode;
    }

    public Join(String username, String email, String classname, String classCode, String section) {
        this.username = username;
        this.email = email;
        Classname = classname;
        ClassCode = classCode;
        Section = section;
    }

    public Join(String username, String email, String classname, String classCode, String section, String subject) {
        this.username = username;
        this.email = email;
        Classname = classname;
        ClassCode = classCode;
        Section = section;
        Subject = subject;
    }

    public String getSection() {
        return Section;
    }

    public void setSection(String section) {
        Section = section;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClassname() {
        return Classname;
    }

    public void setClassname(String classname) {
        Classname = classname;
    }

    public String getClassCode() {
        return ClassCode;
    }

    public void setClassCode(String classCode) {
        ClassCode = classCode;
    }
}

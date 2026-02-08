package com.example.learnhub;

public class Class {
    private String Classname,ClassDescription,Section,Room,Subject,ClassCode,Username,Email;

    public Class(String classname, String classDescription, String section, String room, String subject, String classCode, String username, String email) {
        Classname = classname;
        ClassDescription = classDescription;
        Section = section;
        Room = room;
        Subject = subject;
        ClassCode = classCode;
        Username=username;
        Email=email;
    }

    public Class(String classname, String classCode, String username, String email) {
        Classname = classname;
        ClassCode = classCode;
        Username = username;
        Email = email;
    }

    public Class() {

    }

    public String getClassname() {
        return Classname;
    }

    public void setClassname(String classname) {
        Classname = classname;
    }

    public String getClassDescription() {
        return ClassDescription;
    }

    public void setClassDescription(String classDescription) {
        ClassDescription = classDescription;
    }

    public String getSection() {
        return Section;
    }

    public void setSection(String section) {
        Section = section;
    }

    public String getRoom() {
        return Room;
    }

    public void setRoom(String room) {
        Room = room;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getClassCode() {
        return ClassCode;
    }

    public void setClassCode(String classCode) {
        ClassCode = classCode;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}

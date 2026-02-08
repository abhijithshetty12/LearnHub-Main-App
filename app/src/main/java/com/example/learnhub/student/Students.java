package com.example.learnhub.student;

public class Students {
    private String name;
    private String password;
    private String email;

    public Students( ) {

    }

    public Students(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Students(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

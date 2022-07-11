package com.example.pixcy.models;

public class User {
    protected String username = "";
    public String gender = "";
    public String email = "";
    public String dob = "";

    public User() {

    }

    protected User(String dob, String email, String gender, String username) {
        this.dob = dob;
        this.email = email;
        this.gender = gender;
        this.username = username;
    }

    public String getDob() {
        return dob;
    }
    public void setDob(String dob) {
        this.dob = dob;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    protected String getUsername() {
        return username;
    }
    protected void setUsername(String username) {
        this.username = username;
    }

}

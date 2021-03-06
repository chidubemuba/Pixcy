package com.example.pixcy.models;

import org.parceler.Parcel;

@Parcel
public class User {
    private String username;
    private String bio;
    private String gender;
    private String email;
    private String dob;

    // empty constructor needed by the Parceler library
    public User() {

    }

    public User(String bio, String dob, String email, String gender, String username) {
        this.bio = bio;
        this.dob = dob;
        this.email = email;
        this.gender = gender;
        this.username = username;
    }

    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
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
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User {" + "username: " + this.username + '\'' + "bio: " + this.bio + "gender: "
                + this.gender + '\'' + "email: " + this.email + '\'' + "dob :" + this.dob + '}';
    }

}

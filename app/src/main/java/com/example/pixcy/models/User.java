package com.example.pixcy.models;

import org.parceler.Parcel;

@Parcel
public class User {
    public String username;
    public String bio;
    public String gender;
    public String email;
    public String dob;
    public String profile_pic;

    // empty constructor needed by the Parceler library
    public User() {

    }

    public User(String bio, String dob, String email, String gender, String profile_pic, String username) {
        this.bio = bio;
        this.dob = dob;
        this.email = email;
        this.gender = gender;
        this.username = username;
        this.profile_pic = profile_pic;
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
    public String getProfile_pic() {
        return profile_pic;
    }
    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
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
                + this.gender + '\'' + "email: " + this.email + '\'' + "dob :" + this.dob +
                '\'' + "profile pic url: " + this.profile_pic + '}';
    }
}

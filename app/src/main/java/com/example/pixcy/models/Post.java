package com.example.pixcy.models;

import android.util.Log;

import com.google.firebase.firestore.ServerTimestamp;

import org.parceler.Parcel;

import java.util.Date;

@Parcel
public class Post {
    private String description;
    private String image_url;
    private @ServerTimestamp Date timestamp;
    private String user_id;
    public double longitude;
    public double latitude;
    public String address;
    public String city;
    public String state;
    public String postal_code;
    public String country;

    public Post() {
    }

    public Post(String address, String city, String country, String description, String image_url,
                double latitude, double longitude, String postal_code, String state, Date timestamp, String user_id) {
        this.address = address;
        this.city = city;
        this.country = country;
        this.description = description;
        this.image_url = image_url;
        this.latitude = latitude;
        this.longitude = longitude;
        this.postal_code = postal_code;
        this.state = state;
        this.timestamp = timestamp;
        this.user_id = user_id;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getImage_url() {
        return image_url;
    }
    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public String getPostal_code() {
        return postal_code;
    }
    public void setPostal_code(String postalCode) {
        this.postal_code = postalCode;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public Date getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    public static String calculateTimeAgo(Date createdAt) {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (Exception e) {
            Log.i("Error:", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
    }

    @Override
    public String toString() {
        return "Post {" + "address: " + this.address + '\'' + "city: " + this.city  + '\'' + "country: "
                + this.country + '\'' + "description :" + this.description + '\'' + "image_url :"
                + this.image_url + '\'' + "latitude :" + this.latitude + '\'' + "longitude :"
                + this.longitude + '\'' + "postal_code :" + this.postal_code + '\'' + "state :"
                + this.state + '\'' + "timestamp :" + this.timestamp + '\'' + "user_id :" + this.user_id + '}';
    }

}

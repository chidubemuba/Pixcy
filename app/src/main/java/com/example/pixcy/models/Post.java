package com.example.pixcy.models;

import android.util.Log;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Post {
    private String description = "";
    private String image_url = "";
    private @ServerTimestamp Date timestamp;
    private String user_id = "";
    private String user_name = "";

    public Post() {
    }

    public Post(String description, String image_url, Date timestamp, String user_id, String user_name) {
        this.description = description;
        this.image_url = image_url;
        this.timestamp = timestamp;
        this.user_id = user_id;
        this.user_name = user_name;
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
    public Date getTimestamp() {
        return timestamp;
    }
    public void  {
        this.creation_time_ms = creation_time_ms;
    }
    public void setImage_url(String image_url) {
        this.image_url = image_url;
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

}

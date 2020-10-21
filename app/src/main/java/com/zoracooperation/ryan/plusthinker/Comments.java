package com.zoracooperation.ryan.plusthinker;

public class Comments {
    private String uid, comment, profileImage, username, date, time;

    public Comments() {
    }

    public Comments(String uid, String comments, String profileImage, String fullName, String date, String time) {
        this.uid = uid;
        this.comment = comments;
        this.profileImage = profileImage;
        this.username = fullName;
        this.date = date;
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public String getComment() {
        return comment;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getUsername() {
        return username;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}


package com.zoracooperation.ryan.plusthinker;

public class Post {
    public String uid, time, date, postImage, description, profileImage, fullName, postKey, totalPost;

    public Post() {
    }

    public Post(String uid, String time, String date, String postImage, String description, String profileImage, String fullName) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.postImage = postImage;
        this.description = description;
        this.profileImage = profileImage;
        this.fullName = fullName;
    }

    public Post(String uid, String time, String date, String postImage, String description, String profileImage, String fullName, String postKey, String totalPost) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.postImage = postImage;
        this.description = description;
        this.profileImage = profileImage;
        this.fullName = fullName;
        this.postKey = postKey;
        this.totalPost = totalPost;
    }

    public String getUid() {
        return uid;
    }

    public String getTotalPost() {
        return totalPost;
    }

    public String getPostKey() {
        return postKey;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getPostImage() {
        return postImage;
    }

    public String getDescription() {
        return description;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getFullName() {
        return fullName;
    }
}

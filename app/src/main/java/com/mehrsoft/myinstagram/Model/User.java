package com.mehrsoft.myinstagram.Model;

public class User {


    private String id;
    private String username;
    private String fullname;
    private String bio;
    private String imageurl;


    public User() {
    }

    public User(String id, String username, String fullname, String bio, String imageurl) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.bio = bio;
        this.imageurl = imageurl;
    }


    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullname() {
        return fullname;
    }

    public String getBio() {
        return bio;
    }

    public String getImageurl() {
        return imageurl;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", fullname='" + fullname + '\'' +
                ", bio='" + bio + '\'' +
                ", imageurl='" + imageurl + '\'' +
                '}';
    }
}

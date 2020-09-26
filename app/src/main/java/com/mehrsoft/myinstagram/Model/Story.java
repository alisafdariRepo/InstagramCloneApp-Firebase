package com.mehrsoft.myinstagram.Model;

public class Story {

    private String userName;

    private String imageUrl;

    public Story(String userName, String imageUrl) {
        this.userName = userName;
        this.imageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

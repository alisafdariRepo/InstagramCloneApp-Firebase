package com.mehrsoft.myinstagram.Model;

public class Post {

    private String postid;
    private String postimage;
    private String publisher;
    private String description;

    public String getPostid() {
        return postid;
    }

    public String getPostimage() {
        return postimage;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getDescription() {
        return description;
    }


    @Override
    public String toString() {
        return "Post{" +
                "postid='" + postid + '\'' +
                ", postimage='" + postimage + '\'' +
                ", publisher='" + publisher + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

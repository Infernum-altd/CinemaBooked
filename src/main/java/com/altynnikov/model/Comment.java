package com.altynnikov.model;

public class Comment {
    private String userName;
    private String userComment;
    private int userId;

    public Comment(String userName, String userComment, int userId) {
        this.userName = userName;
        this.userComment = userComment;
        this.userId = userId;
    }

    public Comment(String userComment, int userId){
        this.userComment = userComment;
        this.userId = userId;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}

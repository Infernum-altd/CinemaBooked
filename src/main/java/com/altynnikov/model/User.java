package com.altynnikov.model;

public class User {
    private int id;
    private String login;
    private String password;

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public int getId() {
        return id;
    }

    public User(String login, String password){
        this.login = login;
        this.password = password;
    }

    public User(int id, String login, String password){
        this.id = id;
        this.login = login;
        this.password = password;
    }



}

package com.altynnikov.service;

import com.altynnikov.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Authorization {
    private User currentUser;
    public boolean isAuthorized(String login, String password){
        ResultSet users = DbInteraction.getUsersFromDB();

        try {
            while (users.next()){
                int id = users.getInt("id");
                String loginFromDB = users.getString("login");
                String passwordFromDB = users.getString("password");
                if (loginFromDB.equals(login) && passwordFromDB.equals(Hash.generateHash(password))){
                    currentUser = new User(id, login, password);
                    return true;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}

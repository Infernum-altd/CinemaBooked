package com.altynnikov.service;

import com.altynnikov.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Registration {

    private static boolean isRegistrate(User user){
        ResultSet users = DbInteraction.getUsersFromDB();

        try {
            while (users.next()){
                String login = users.getString("login");
                if (user.getLogin().equals(login)){
                    return false;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return true;
    }

    public static boolean register(User user){
        if (isRegistrate(user)){
            DbInteraction.addUserToDB(user);
            return true;
        }
        else {
            return false;
        }
    }

}

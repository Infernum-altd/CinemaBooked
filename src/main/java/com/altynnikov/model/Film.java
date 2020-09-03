package com.altynnikov.model;

import com.altynnikov.service.DbInteraction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class Film {
    private int id;
    private String name;
    private String description;
    private Time duration;
    private int ageLimit;

    public Film(){}

    public Film(String name, String description, Time duration) {
        this.name = name;
        this.description = description;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    public Time getDuration() {
        return duration;
    }

    private void setDuration(Time duration) {
        this.duration = duration;
    }

    public int getAgeLimit() {
        return ageLimit;
    }

    private void setAgeLimit(int ageLimit) {
        this.ageLimit = ageLimit;
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public static List<Film> getFilmsFromDB(){
        ArrayList<Film> films = new ArrayList<>();
        ResultSet filmsFromBD = DbInteraction.getFilmFromDB();
        try{
            while (filmsFromBD.next()){
                Film film = new Film();
                film.setId(filmsFromBD.getInt("id"));
                film.setName(filmsFromBD.getString("name"));
                film.setDescription(filmsFromBD.getString("description"));
                film.setDuration(filmsFromBD.getTime("duration"));
                //film.setAgeLimit(filmsFromBD.getInt("age_limit"));
                films.add(film);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return films;
    }

    public static List<String> getFilmsNameFromDB(){
        ArrayList<String> filmsName = new ArrayList<>();
        ResultSet filmsFromBD = DbInteraction.getFilmFromDB();
        try{
            while (filmsFromBD.next()){
                filmsName.add(filmsFromBD.getString("name"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return filmsName;
    }
 }

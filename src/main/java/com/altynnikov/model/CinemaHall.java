package com.altynnikov.model;

import com.altynnikov.service.DbInteraction;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CinemaHall {
    private int id;
    private String name;
    private int CinemaId;
    private Place[][] places;

    public int getCinemaId() {
        return CinemaId;
    }

    public void setCinemaId(int cinemaId) {
        CinemaId = cinemaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPlaces(Place[][] places) {
        this.places = places;
    }

/*    public int getCinemaHallId() {
        return cinemaHallId;
    }

    public void setCinemaHallId(int cinemaHallId) {
        this.cinemaHallId = cinemaHallId;
    }*/

    public CinemaHall(){}

    public CinemaHall(String name, int cinemaId) {
        this.name = name;
        CinemaId = cinemaId;
        this.id = DbInteraction.getCinemaHallIdByName(name);
    }

    public CinemaHall(String name, int cinemaId, int id) {
        this.name = name;
        CinemaId = cinemaId;
        this.id = id;
    }

/*    public CinemaHall(int id, int rows, int maxPlaces*//*, int cinemaHallId*//*){
        this.id = id;
        //this.cinemaHallId = cinemaHallId;
        places = createPlaces(rows,maxPlaces);
    }*/

    public CinemaHall(int id, int rows, int maxPlaces){
        this.id = id;
        places = createPlaces(rows,maxPlaces);
    }


    private Place[][] createPlaces(int rows, int maxPlaces){
        Place[][] createdPlaces = new Place[rows][maxPlaces];
        //ResultSet placesFormDB = DbInteraction.getPlacesFromDB(cinemaHallId);
        int numOfSpace = 0;

        for (int i = 0; i < createdPlaces.length; i++){
            int j = 0;

            if (i != 0 && i % 2 == 0){
                numOfSpace += 2;
            }

            for (; j < numOfSpace/2; ++j){
                createdPlaces[i][j] = null;
            }

            for (int placeNumber = 1; j < createdPlaces[1].length-(numOfSpace/2); j++){

                    createdPlaces[i][j] = DbInteraction.getPlace(id,i+1,placeNumber++);
            }

            for (; j < createdPlaces[1].length; j++){
                createdPlaces[i][j] = null;
            }

        }
        return createdPlaces;
    }


    public void showCinemaHall(){
        for (int i = 0; i < places.length; i++){

            if (i < 9){
                System.out.print(i+1 + ".   ");
            }
            else {
                System.out.print(i+1 + ".  ");
            }

            for (int j = 0; j < this.places[i].length; j++){

                if (this.places[i][j] == null || false/*!placeIsBooked(this.places[i][j])*/){
                    System.out.print("  ");
                }
                else {
                    System.out.print(this.places[i][j].getPlaceNumber() + " ");
                }
            }
            System.out.println();
        }
    }

    public String getSeanceDisplay(int seanceId){
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < places.length; i++){

            if (i < 9){
                result.append(i+1 + ".   ");
            }
            else {
                result.append(i+1 + ".  ");
            }

            for (int j = 0; j < this.places[i].length; j++){

                if (this.places[i][j] == null || placeIsBooked(this.places[i][j], seanceId)){
                    result.append("  ");
                }
                else {
                    result.append(this.places[i][j].getPlaceNumber() + " ");
                }
            }
            result.append("\n");
        }
        return result.toString();
    }

    public Place[][] getPlaces() {
        return places;
    }

    private boolean placeIsBooked(Place place, int seanceId){
        boolean isBooked = false;
        ResultSet ticketFromDB = DbInteraction.getTicketFromDB(place.getId(), seanceId);
        try {
            ticketFromDB.next();
            isBooked = ticketFromDB.getBoolean("is_booked");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return isBooked;
    }
}

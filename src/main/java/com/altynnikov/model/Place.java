package com.altynnikov.model;

public class Place {
    private int id;
    private int rowNumber;
    private int placeNumber;
    private int cinemaHallId;

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getPlaceNumber() {
        return placeNumber;
    }

    public void setPlace_Number(int place_Number) {
        this.placeNumber = place_Number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCinemaHallId() {
        return cinemaHallId;
    }

    public void setCinemaHallId(int cinemaHallId) {
        this.cinemaHallId = cinemaHallId;
    }

    public Place(int id, int rowNumber, int placeNumber, int cinemaHallId){
        this.id = id;
        this.rowNumber = rowNumber;
        this.placeNumber = placeNumber;
        this.cinemaHallId = cinemaHallId;
    }

    public Place(int rowNumber, int placeNumber){
        this.rowNumber = rowNumber;
        this.placeNumber = placeNumber;
    }

    public Place() {
    }
}

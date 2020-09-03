package com.altynnikov.model;

import com.altynnikov.service.DbInteraction;

import java.sql.*;

public class Ticket {
    private int id;
    private int cost;
    private boolean isBooked;
    private int placeId;
    private int seanceId;
    private String cinemaName;
    private String address;
    private Time timeStart;
    private Date date;
    private Place place;

    public int getSeanceId() {
        return seanceId;
    }

    public void setSeanceId(int seanceId) {
        this.seanceId = seanceId;
    }

    public int getId() {
        return id;
    }

    public int getCost() {
        return cost;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public int getPlaceId() {
        return placeId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Time getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Time timeStart) {
        this.timeStart = timeStart;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    private Ticket(){}

    public Ticket(int id, int cost, boolean isBooked, int placeId){
        this.id = id;
        this.cost = cost;
        this.isBooked = isBooked;
        this.placeId = placeId;
    }

    public static Ticket createTicketForPDF(int id, int cost, boolean isBooked, int placeId, String cinemaName, String address, Time timeStart, Date date, Place place, int seanceId){
        Ticket result = new Ticket();
        result.setId(id);
        result.setCost(cost);
        result.setBooked(isBooked);
        result.setPlaceId(placeId);
        result.setCinemaName(cinemaName);
        result.setAddress(address);
        result.setTimeStart(timeStart);
        result.setDate(date);
        result.setPlace(place);
        result.setSeanceId(seanceId);

        return result;
    }

    public void booked(int userId){
        Connection connection = DbInteraction.getDBConnection();

        try {
            Statement statement = connection.createStatement();
            String uptadeTableSQL = "update sinemabooked.ticket set is_booked = true, user_id = " + userId + "  where id = " + this.id +"";
            statement.execute(uptadeTableSQL);
        }catch (SQLException e){
            e.printStackTrace();
        }

        this.isBooked = true;
    }
}

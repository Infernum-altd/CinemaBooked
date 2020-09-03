package com.altynnikov.model;

import com.altynnikov.service.DbInteraction;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class Seance {
    private CinemaHall cinemaHall;
    private int id;
    private Time startTime;
    private Date data;
    private int filmId;
    private List<Ticket> tickets = new ArrayList<>();

    public int getFilmId() {
        return filmId;
    }

    public void setFilmId(int filmId) {
        this.filmId = filmId;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public CinemaHall getCinemaHall() {
        return cinemaHall;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setCinemaHall(CinemaHall cinemaHall) {
        this.cinemaHall = cinemaHall;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public Seance(){}

    public Seance(int id, Time startTime, CinemaHall cinemaHall){
        this.cinemaHall = cinemaHall;
        this.id = id;
        this.startTime = startTime;
        //seances.add(this);
        setTickets();
    }



    private void setTickets(){
        ResultSet ticketsFromDB = DbInteraction.getTicketsFromDB(id);

        try {
            while (ticketsFromDB.next()){
                int id = ticketsFromDB.getInt("id");
                int cost = ticketsFromDB.getInt("cost");
                boolean isBooked = ticketsFromDB.getBoolean("is_booked");
                int placeId = ticketsFromDB.getInt("place_id");
                tickets.add(new Ticket(id, cost, isBooked, placeId));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        return "Seance{" +
                "cinemaHall=" + cinemaHall +
                ", id=" + id +
                ", startTime=" + startTime +
                ", data=" + data +
                ", filmId=" + filmId +
                ", tickets=" + tickets +
                '}';
    }
}

package com.altynnikov.windowState;

import com.altynnikov.model.CinemaHall;
import com.altynnikov.model.Seance;
import com.altynnikov.service.DateController;
import com.altynnikov.service.DbInteraction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class SeanceByDateWindow extends Window {
    int filmId;

    SeanceByDateWindow(Socket client, int userId){
        setClient(client);
        setUserId(userId);
    }

    SeanceByDateWindow(Socket client, int userId, int filmId){
        setClient(client);
        setUserId(userId);
        this.filmId = filmId;
    }

    @Override
    Window nextWindow() {
        return this;
    }

    @Override
    Window previousMenu() {
        return new UserWindow(getClient(), getUserId());
    }

    private LocalDate requestChoseData() {
        DateController dateController = new DateController();
        LocalDate seanceData = null;
        try {
            DataOutputStream out = new DataOutputStream(getClient().getOutputStream());
            out.writeUTF(dateController.getAvailableDates() +
                    "Введите дату сеанса");

            String answer = new DataInputStream(getClient().getInputStream()).readUTF();
            int seanceDate = 0;

            if (answer.matches("\\d{1,2}"))
                seanceDate = Integer.parseInt(answer);

            if (dateController.containsDate(seanceDate)){
                if (LocalDate.now().getDayOfMonth() > seanceDate){
                    seanceData = LocalDate.now().plusMonths(1).withDayOfMonth(seanceDate);
                }
                seanceData = LocalDate.now().withDayOfMonth(seanceDate);
            }else {
                out.writeUTF("illegalInput");
                out.writeUTF("Недоступная дата");
                seanceData = requestChoseData();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return seanceData;
    }


    @Override
    public Window interactWithWindow() throws IOException {
        Seance seance = requestChoseSeance(requestChoseData());
        if (seance != null)
            return new SeanceWindow(seance, getClient(), getUserId());
        else
            return new FilmWindow(getClient(), getUserId());
    }

    private Seance requestChoseSeance(LocalDate seanceDate){
        ResultSet seances = null;
        if (filmId!=0)
            seances = DbInteraction.getSeancesByDate(seanceDate.minusDays(1), filmId);
        else
            seances = DbInteraction.getSeancesByDate(seanceDate.minusDays(1));
        int seanceCounter = 1;
        try {
            StringBuffer request = new StringBuffer();
            DataOutputStream out = new DataOutputStream(getClient().getOutputStream());
            while (seances.next()){
                request.append(seanceCounter++ + ". " + seances.getString("name") + " " +
                        seances.getTime("time") + " " +
                        DateController.convertToLocalDate(seances.getDate("date")).plusDays(1) + "\n");
            }
            request.append("Введите номер сеанса");
            out.writeUTF(request.toString());
        }catch (SQLException | IOException e){
            e.printStackTrace();
        }
        seanceCounter = 1;
        Seance seance = null;
        try {
            String answer = new DataInputStream(getClient().getInputStream()).readUTF();
            int seanceNumber = 0;
            if (answer.matches("\\d{1,2}"))
                seanceNumber = Integer.parseInt(answer);

            seances.first();
            seances.previous();
            while (seances.next()){
                if (seanceNumber == seanceCounter++){
                    seance = new Seance(
                            seances.getInt("id"),
                            seances.getTime("time"),
                            new CinemaHall(seances.getInt("cinema_hall_id") ,
                                    seances.getInt("number_of_rows"),
                                    seances.getInt("max_number_of_place_in_row")));
                    break;
                }
            }
        }catch (SQLException | IOException e){
            e.printStackTrace();
        }

        return seance;
    }
}

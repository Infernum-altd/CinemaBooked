package com.altynnikov.windowState;

import com.altynnikov.model.Cinema;
import com.altynnikov.model.CinemaHall;
import com.altynnikov.model.Film;
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
import java.util.List;

public class FilmWindow extends Window {
    private List<Film> filmsList;

    FilmWindow(Socket client, int userId){
        this.filmsList = uploadFilmsFromDB();
        setClient(client);
        setUserId(userId);
    }

    @Override
    Window nextWindow() {
        return null;
    }

    @Override
    Window previousMenu() {
        return null;
    }

    private String parseDescription(String description){
        String[] words = description.split(" ");
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0 ; i < words.length; i++){
            if (i%6==0)
                stringBuilder.append("\n    ");
            stringBuilder.append(words[i] + " ");
        }
        return stringBuilder.toString();
    }

    private String requestToClient(int filmNumber) throws IOException{
        StringBuilder request = new StringBuilder();
        request.append("Film info:\n").
                append("    " + filmsList.get(filmNumber-1).getName() + ":\n").
                append("    Описание: " + parseDescription(filmsList.get(filmNumber-1).getDescription()) + "\n").
                append("    Длительность: " + filmsList.get(filmNumber-1).getDuration() + "\n");

        request.append("1. Выбрать сеанс\n").
                append("2. Коментарии\n").
                append("3. Выбрать дату сеансов\n").
                append("Введите число для перехода");

        DataOutputStream out = new DataOutputStream(getClient().getOutputStream());
        out.writeUTF(request.toString());
        out.flush();
        DataInputStream in = new DataInputStream(getClient().getInputStream());
        return in.readUTF();
    }

    private Integer choseSeanceRequest(int filmNumber){
        ResultSet seances = DbInteraction.getSeansesFromDB(filmsList.get(filmNumber-1).getId(), LocalDate.now());
        StringBuilder request = new StringBuilder();
        String answer = null;
        int seanceNumber = 0;

        try {
            DataOutputStream out = new DataOutputStream(getClient().getOutputStream());
            while (seances.next()){
                request.append(++seanceNumber + ". ").
                        append(seances.getTime("time").toString() + " ").
                        append(DateController.convertToLocalDate(seances.getDate("date")).plusDays(1)  + " ").
                        append(seances.getString("name") + " ").
                        append(seances.getString("address") + " \n");
            }
            request.append("Введите номер сеанса");
            out.writeUTF("SeanceInfo " + String.valueOf(request).getBytes().length);
            out.write(String.valueOf(request).getBytes());
            answer = new DataInputStream(getClient().getInputStream()).readUTF();
        }catch (SQLException | IOException e){
            e.printStackTrace();
        }

        Integer inputAction = null;
        if (answer.matches("\\d{1,2}")){
            inputAction = Integer.parseInt(answer);
        }
        if (inputAction!= null && seanceNumber < inputAction)
            inputAction = null;

        return inputAction;
    }


    @Override
    public Window interactWithWindow() throws IOException{
        Window nextWindow = null;
        Integer filmNumber = choseFilm();
        int inputAction = 0;
        String answer = "";

        if (filmNumber != null)
            answer = requestToClient(filmNumber);

        if (filmNumber != null && answer.matches("\\d"))
            inputAction = Integer.parseInt(answer);

        switch (inputAction){
            case 1:
                Integer seanceNumber = choseSeanceRequest(filmNumber);
                if (seanceNumber != null){
                    Seance seance = choseSeance(filmNumber, seanceNumber);
                    nextWindow = new SeanceWindow(seance, getClient(), getUserId());
                }else {
                    nextWindow = this;
                    new DataOutputStream(getClient().getOutputStream()).writeUTF("illegalInput");
                    new DataOutputStream(getClient().getOutputStream()).writeUTF("Несуществующие действие");
                }
                break;
            case 2:
                nextWindow = new CommentWindow(filmsList.get(filmNumber-1).getId(), getClient(), getUserId());
                break;
            case 3:
                nextWindow = new SeanceByDateWindow(getClient(), getUserId(), filmsList.get(filmNumber-1).getId());
                break;
            default:
                nextWindow = this;
                new DataOutputStream(getClient().getOutputStream()).writeUTF("illegalInput");
                new DataOutputStream(getClient().getOutputStream()).writeUTF("Несуществующие действие");
                break;
        }
        return nextWindow;
    }

    private Integer choseFilm() throws IOException{
        StringBuilder request = new StringBuilder();
        for (int i = 0; i < filmsList.size(); i++){
            request.append(i+1 + " " + filmsList.get(i).getName() + "\n");
        }

        request.append("Введите номер фильма");
        DataOutputStream out = new DataOutputStream(getClient().getOutputStream());
        out.writeUTF(request.toString());
        out.flush();
        DataInputStream in = new DataInputStream(getClient().getInputStream());
        Integer inputAction = null;

        String answer = in.readUTF();
        if (answer.matches("\\d{1,2}") && filmsList.size() < (inputAction = Integer.parseInt(answer))){
            out.writeUTF("illegalInput");
            out.writeUTF("Фильма с таким номером нет");
            inputAction = null;
        }

        return inputAction;
    }

    private Seance choseSeance(int filmNumber, int seanceNumber) {
        ResultSet seances = DbInteraction.getSeansesFromDB(filmsList.get(filmNumber-1).getId(), LocalDate.now());

        Seance seance = null;
        try {
            int seanceCounter = 1;
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
        }catch (SQLException e){
            e.printStackTrace();
        }

        return seance;
    }

    private List<Film> uploadFilmsFromDB(){
        return Film.getFilmsFromDB();
    }
}

package com.altynnikov.windowState;

import com.altynnikov.model.Seance;
import com.altynnikov.service.DbInteraction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SeanceWindow extends Window {
    private Seance seance;
    //private int userId;

    public SeanceWindow(Seance seance, Socket client, int userId){
        this.seance = seance;
        setClient(client);
        setUserId(userId);
    }

    @Override
    Window nextWindow() {
        return this;
    }

    @Override
    Window previousMenu() {
        return new FilmWindow(getClient(), getUserId());
    }

    String requestToClient() {
        String answer = null;
        String requset = seance.getCinemaHall().getSeanceDisplay(seance.getId()) + "\n" +
                "1. Забронировать место\n" +
                "2. Посмотреть сеансы\n" +
                "3. Выйти в главное меню\n" +
                "Введите число для перехода";
        try {
            new DataOutputStream(getClient().getOutputStream()).writeUTF(requset);
            answer = new DataInputStream(getClient().getInputStream()).readUTF();
        }catch (IOException e){
            e.printStackTrace();
        }
        return answer;
    }

    String requestRowNumber(){
        String answer = null;
        try {
            new DataOutputStream(getClient().getOutputStream()).writeUTF("Введите номер ряда: ");
            answer = new DataInputStream(getClient().getInputStream()).readUTF();
        }catch (IOException e){
            e.printStackTrace();
        }
        return answer;
    }

    String requestPlaceNumber(){
        String answer = null;
        try {
            new DataOutputStream(getClient().getOutputStream()).writeUTF("Введите номер места: ");
            answer = new DataInputStream(getClient().getInputStream()).readUTF();
        }catch (IOException e){
            e.printStackTrace();
        }
        return answer;
    }

    @Override
    public Window interactWithWindow() throws IOException {
        Window nextWindow = null;
        String answer = requestToClient();
        int inputAction = 0;
        if (answer.matches("\\d"))
            inputAction = Integer.parseInt(answer);

        switch (inputAction){
            case 1:
                String answerRowNumber = requestRowNumber();
                String answerPlaceNumber = requestPlaceNumber();
                int rowNumber = 0;
                int placeNumber = 0;
                if (answerRowNumber.matches("\\d{1,2}") && answerPlaceNumber.matches("\\d{1,2}")){
                    rowNumber = Integer.parseInt(answerRowNumber);
                    placeNumber = Integer.parseInt(answerPlaceNumber);
                } else {
                    new DataOutputStream(getClient().getOutputStream()).writeUTF("illegalInput");
                    new DataOutputStream(getClient().getOutputStream()).writeUTF("Несуществующие действие");
                    return this;
                }


                for (int placeIndex = 0; placeIndex < seance.getCinemaHall().getPlaces()[rowNumber-1].length; placeIndex++){
                    if (seance.getCinemaHall().getPlaces()[rowNumber-1][placeIndex] !=null && seance.getCinemaHall().getPlaces()[rowNumber-1][placeIndex].getPlaceNumber() == placeNumber ){
                        for (int listIndex = 0; listIndex < seance.getTickets().size(); listIndex++){
                            if (seance.getCinemaHall().getPlaces()[rowNumber-1][placeIndex].getId() == seance.getTickets().get(listIndex).getPlaceId()){
                                if (DbInteraction.getIsTickedBooked(seance.getTickets().get(listIndex).getPlaceId(), seance.getId())){
                                    new DataOutputStream(getClient().getOutputStream()).writeUTF("Этот билет уже забронирован");
                                } else {
                                    seance.getTickets().get(listIndex).booked(getUserId());
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
                nextWindow = new SeanceWindow(seance, getClient(), getUserId());
                break;
            case 2:
                nextWindow = previousMenu();
                break;
            case 3:
                nextWindow = new UserWindow(getClient(), getUserId());
                break;
            default:
                nextWindow = this;
                new DataOutputStream(getClient().getOutputStream()).writeUTF("illegalInput");
                new DataOutputStream(getClient().getOutputStream()).writeUTF("Несуществующие действие");
                break;
        }
        return nextWindow;
    }
}

package com.altynnikov.windowState;

import com.altynnikov.model.Ticket;
import com.altynnikov.model.TicketFile;
import com.altynnikov.service.DbInteraction;
import com.itextpdf.text.DocumentException;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TicketsWindow extends Window {
    TicketsWindow(Socket client, int userId){
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

    private String requestToClient() {
        String answer = null;
        List<Ticket> tickets = DbInteraction.getTicketsInfoByUserId(getUserId());
        StringBuilder request = new StringBuilder();
        try  {
            DataOutputStream out = new DataOutputStream(getClient().getOutputStream());

            for (Ticket ticket : tickets) {
                request.append(tickets.indexOf(ticket)+1 + " " +  ticket.getCinemaName() + " " + ticket.getAddress() + " " + ticket.getDate()
                        + " " + ticket.getTimeStart() + " " + ticket.getCost() + " " + ticket.getPlace().getRowNumber() + " " + ticket.getPlace().getPlaceNumber() + "\n");
            }

            request.append("1. Скачать билет\n" +
                    "2. Скачать все билеты\n" +
                    "3. Назад\n" +
                    "Введите число для перехода");
            out.writeUTF(request.toString());
            out.flush();

            DataInputStream in = new DataInputStream(getClient().getInputStream());
            answer = in.readUTF();
        } catch (IOException e){
            e.printStackTrace();
        }
        return answer;
    }

    private Ticket requestChoseTicket() throws IOException{
        Ticket result = null;
        String answer = null;
        List<Ticket> tickets = DbInteraction.getTicketsInfoByUserId(getUserId());

        try{
            DataOutputStream out = new DataOutputStream(getClient().getOutputStream());
            out.writeUTF("Введите номер билета");
            answer = new DataInputStream(getClient().getInputStream()).readUTF();
        }catch (IOException e){
            e.printStackTrace();
        }
        if (!answer.matches("\\d") || tickets.size() < Integer.parseInt(answer)){
            new DataOutputStream(getClient().getOutputStream()).writeUTF("illegalInput");
            new DataOutputStream(getClient().getOutputStream()).writeUTF("Такого билета нет");
        }
        else
            result = tickets.get(Integer.parseInt(answer)-1);
        return result;
    }

    private void sendTicket(Ticket ticket){
        try {
            String fileName = (TicketFile.createFile(ticket).getName());
            byte[] fileInByte = Files.readAllBytes(Paths.get(fileName));
            new DataOutputStream(getClient().getOutputStream()).writeUTF("file " + fileInByte.length);
            new DataOutputStream(getClient().getOutputStream()).write(fileInByte);
        }catch (IOException | DocumentException e){
            e.printStackTrace();
        }
    }

    @Override
    public Window interactWithWindow() throws IOException, NumberFormatException {
        Window nextWindow = null;
        String answer = requestToClient();
        int inputAction = 0;
        if (answer.matches("\\d"))
            inputAction = Integer.parseInt(answer);

        switch (inputAction){
            case 1:
                Ticket ticketToSend = requestChoseTicket();

                if (ticketToSend != null)
                    sendTicket(ticketToSend);

                nextWindow = new TicketsWindow(getClient(), getUserId());
                break;
            case 2:
                List<Ticket> tickets = DbInteraction.getTicketsInfoByUserId(getUserId());
                for (Ticket ticket : tickets){
                    sendTicket(ticket);
                }
                nextWindow = new TicketsWindow(getClient(), getUserId());
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

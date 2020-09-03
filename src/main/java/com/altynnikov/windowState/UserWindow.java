package com.altynnikov.windowState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class UserWindow extends Window {
    @Override
    Window nextWindow() {
        return null;
    }

    @Override
    Window previousMenu() {
        return null;
    }

    UserWindow(Socket client, int userId){
        setUserId(userId);
        setClient(client);
    }

    String requestToClient() {
        String result = null;
        try  {
            DataOutputStream out = new DataOutputStream(getClient().getOutputStream());
            out.writeUTF("1. Выбрать фильм\n" +
                    "2. Выбрать дату сеанса\n" +
                    "3. Посмотреть мои билеты\n" +
                    "4. LogOut\n" +
                    "5. Выйти из приложения\n" +
                    "Введите число для перехода");
            out.flush();
            DataInputStream in = new DataInputStream(getClient().getInputStream());
            result = in.readUTF();
        } catch (IOException e){
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Window interactWithWindow() throws IOException, NumberFormatException {
        Window nextWindow = null;
        int inputAction = 0;
        String answer = requestToClient();
        if (answer.matches("\\d"))
            inputAction = Integer.parseInt(answer);

        switch (inputAction){
            case 1:
                nextWindow = new FilmWindow(getClient(), getUserId());
                break;
            case 2:
                nextWindow = new SeanceByDateWindow(getClient(), getUserId());
                break;
            case 3:
                nextWindow = new TicketsWindow(getClient(), getUserId());
                break;
            case 4:
                nextWindow = new MainWindow(getClient());
                break;
            case 5:
                new DataOutputStream(getClient().getOutputStream()).writeUTF("exit");
                getClient().close();
                nextWindow = this;
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

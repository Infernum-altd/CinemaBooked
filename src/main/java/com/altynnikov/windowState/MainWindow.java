package com.altynnikov.windowState;

import java.io.*;
import java.net.Socket;

public class MainWindow extends Window {
    MainWindow(Socket client){
        setClient(client);
    }

    @Override
    Window nextWindow() {
        return new RegistrationWindow(getClient());
    }

    @Override
    Window previousMenu() {
        System.exit(0);
        return null;
    }

    String requestToClient() {
        String result = null;
        try  {
            DataOutputStream out = new DataOutputStream(getClient().getOutputStream());
            out.writeUTF("1. Зарегистрироваться\n" +
                    "2. Авторизоваться\n" +
                    "3. Выйти из приложения\n" +
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
    public Window interactWithWindow() throws IOException {
        Window nextWindow = null;
        String answer = requestToClient();
        int inputAction = 0;

        if (answer.matches("\\d"))
         inputAction = Integer.parseInt(answer);

        switch (inputAction){
            case 1:
                nextWindow = nextWindow();
                break;
            case 2:
                nextWindow = new AuthorizationWindow(getClient());
                break;
            case 3:
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

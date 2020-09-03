package com.altynnikov.windowState;

import com.altynnikov.service.Authorization;

import java.io.*;
import java.net.Socket;

public class AuthorizationWindow extends Window {

    @Override
    Window nextWindow() {
        return new UserWindow(getClient(),getUserId());
    }


    @Override
    Window previousMenu() {
        return new MainWindow(getClient());
    }

    @Override
    public Window interactWithWindow() throws IOException{
        String login;
        String password;
        Authorization authorization = new Authorization();

        login = loginRequest();
        password = passwordRequest();

        while (!authorization.isAuthorized(login,password)){
            String answer = mistakeRequest();
            int inputAction = 0;
            if (answer.matches("\\d")){
                inputAction = Integer.parseInt(answer);
            }

            switch (inputAction){
                case 1:
                    return this;
                case 2:
                    return new MainWindow(getClient());
                case 0:
                    new DataOutputStream(getClient().getOutputStream()).writeUTF("illegalInput");
                    new DataOutputStream(getClient().getOutputStream()).writeUTF("Несуществующие действие");
                    return this;
            }
            login = loginRequest();
            password = passwordRequest();
        }

        setUserId(authorization.getCurrentUser().getId());
        return new UserWindow(getClient(), getUserId());
    }

    private String loginRequest(){
        String answer = null;
        try {
            DataOutputStream out = new DataOutputStream(getClient().getOutputStream());
            out.writeUTF("Введите логин");
            out.flush();
            DataInputStream in = new DataInputStream(getClient().getInputStream());
            answer = in.readUTF();
        } catch (IOException e){
            e.printStackTrace();
        }
        return answer;
    }

    private String passwordRequest(){
        String answer = null;
        try {
            DataOutputStream out = new DataOutputStream(getClient().getOutputStream());
            out.writeUTF("Введите пароль");
            out.flush();
            DataInputStream in = new DataInputStream(getClient().getInputStream());
            answer = in.readUTF();
        } catch (IOException e){
            e.printStackTrace();
        }
        return answer;
    }

    private String mistakeRequest(){
        String answer = null;
        try  {
            new DataOutputStream(getClient().getOutputStream()).writeUTF("Неверный пароль или логин\n" +
                    "1. Повторить попытку\n" +
                    "2. Вернуться в главное меню\n" +
                    "Введите число для перехода");
            answer = new DataInputStream(getClient().getInputStream()).readUTF();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        return answer;
    }

    AuthorizationWindow(Socket client){
        setClient(client);
    }
}

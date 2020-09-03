package com.altynnikov.windowState;

import com.altynnikov.model.User;
import com.altynnikov.service.Registration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class RegistrationWindow extends Window {
    RegistrationWindow(Socket client){
        setClient(client);
    }

    @Override
    Window nextWindow() {
        return new MainWindow(getClient());
    }

    @Override
    Window previousMenu() {
        return new MainWindow(getClient());
    }

    private String requestToClient() {
        String answer = null;
        try {
            DataOutputStream out = new DataOutputStream(getClient().getOutputStream());
            out.writeUTF("1. Продолжить регистрацию\n" +
                             "2. Вернуться назад");
            answer = new DataInputStream(getClient().getInputStream()).readUTF();
        }catch (IOException e){
            e.printStackTrace();
        }
        return answer;
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
            new DataOutputStream(getClient().getOutputStream()).writeUTF("Даный логин занят\n" +
                    "1. Повторить попытку\n" +
                    "2. Вернуться в главное меню\n" +
                    "Введите число для перехода");
            answer = new DataInputStream(getClient().getInputStream()).readUTF();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        return answer;
    }

    @Override
    public Window interactWithWindow() {
        String login;
        String password;
        boolean flag = true;
        while (flag){
            String answer = requestToClient();
            int inputAction = 0;
            if (answer.matches("\\d")){
                inputAction = Integer.parseInt(answer);
            }

            switch (inputAction){
                case 1:
                    flag = false;
                    break;
                case 2:
                    return new MainWindow(getClient());
            }
        }

        login = loginRequest();
        password = passwordRequest();
        User user = new User(login,password);
        while (!Registration.register(user)){
            String answer = mistakeRequest();
            int inputAction = 0;
            if (answer.matches("\\d"))
                inputAction = Integer.parseInt(answer);

            switch (inputAction){
                case 1:
                    return this;
                case 2:
                    return new MainWindow(getClient());
            }
            login = loginRequest();
            password = passwordRequest();
            user = new User(login,password);
        }

        return nextWindow();
    }

}

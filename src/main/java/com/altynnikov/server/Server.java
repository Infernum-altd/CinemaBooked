package com.altynnikov.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public void waitForClient(){
        try(ServerSocket listener = new ServerSocket(4444)) {
            while (!listener.isClosed()){
                Socket client = listener.accept();
                System.out.println("Client connected");
                new Thread(new ServerThread(client)).start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

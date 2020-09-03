package com.altynnikov.server;

import com.altynnikov.model.User;
import com.altynnikov.windowState.WindowController;

import java.net.Socket;

public class ServerThread implements Runnable {
    private Socket client;
    ServerThread(Socket client){
        this.client = client;
    }

    @Override
    public void run() {
        new WindowController(client).runningWindow();
    }
}

package com.altynnikov.windowState;

import java.io.IOException;
import java.net.Socket;

abstract public class Window {
    private Socket client;
    private int userId;
    abstract Window nextWindow();
    abstract Window previousMenu();
    abstract public Window interactWithWindow() throws IOException, NumberFormatException;

    public Socket getClient() {
        return client;
    }

    public void setClient(Socket client) {
        this.client = client;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}

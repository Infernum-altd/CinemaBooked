package com.altynnikov.windowState;

import java.io.IOException;
import java.net.Socket;

public class WindowController {
    private Window currentWindow;

    public void runningWindow(){
        while (!currentWindow.getClient().isClosed() /*|| currentWindow.getClient().isConnected()*/){
            try {
                currentWindow = currentWindow.interactWithWindow();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public WindowController(Socket client){
        this.currentWindow = new MainWindow(client);
    }
}

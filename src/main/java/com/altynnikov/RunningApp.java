package com.altynnikov;

import com.altynnikov.server.Server;
import com.altynnikov.service.DeleteOutputDataThread;
import com.altynnikov.service.ThreadDbUpdate;

public class RunningApp {
    public static void main(String[] args) {
        Thread deleterThread = new Thread(new DeleteOutputDataThread());
        deleterThread.setDaemon(true);
        deleterThread.start();

        new Thread(new ThreadDbUpdate()).start();
        new Server().waitForClient();
    }
}

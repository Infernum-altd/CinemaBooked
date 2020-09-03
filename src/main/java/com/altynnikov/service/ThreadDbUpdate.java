package com.altynnikov.service;


public class ThreadDbUpdate implements Runnable {

    @Override
    public void run() {
        System.out.println("Updating data");
        DbUpdate.updateAllSeancesInDB();
        DbUpdate.deleteOutputFilm();
    }
}

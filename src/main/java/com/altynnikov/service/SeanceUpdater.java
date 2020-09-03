package com.altynnikov.service;

public class SeanceUpdater implements Runnable {
    String urlToSeancePage;

    SeanceUpdater(String urlToSeancePage){
        this.urlToSeancePage = urlToSeancePage;
    }

    @Override
    public void run() {
        try {
            DbUpdate.updateSeanceInfoPerOneDay(urlToSeancePage);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

}

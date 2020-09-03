package com.altynnikov.service;

public class DeleteOutputDataThread implements Runnable {
    @Override
    public void run() {
        while (true){
            System.out.println("cleaning");
            DbUpdate.deleteOutputSeances();
            try {
                Thread.sleep(150000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}

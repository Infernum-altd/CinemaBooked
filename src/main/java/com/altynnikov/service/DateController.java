package com.altynnikov.service;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class DateController {
    private int[] dates = new int[3];

    public void showDate(){
        LocalDate localDate = LocalDate.now();
        for (int i = 0; i < 3; i++){
            dates[i] = localDate.plusDays(i).getDayOfMonth();
            System.out.print(dates[i] + " ");
        }
        System.out.println();
    }


    public String getAvailableDates(){
        StringBuilder result = new StringBuilder();
        LocalDate localDate = LocalDate.now();
        for (int i = 0; i < 3; i++){
            dates[i] = localDate.plusDays(i).getDayOfMonth();
            result.append(dates[i] + " ");
        }
        result.append("\n");
        return result.toString();
    }

    private int[] getDates() {
        return dates;
    }

    public boolean containsDate(int date){
        for (int i = 0; i < dates.length; i++){
            if (dates[i] == date){
                return true;
            }
        }
        return false;
    }

    public static LocalDate convertToLocalDate(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}

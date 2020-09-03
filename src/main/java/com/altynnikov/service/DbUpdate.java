package com.altynnikov.service;

import com.altynnikov.model.Cinema;
import com.altynnikov.model.CinemaHall;
import com.altynnikov.model.Film;
import com.altynnikov.model.Seance;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DbUpdate {

    public static Document getPage(String url){
        //String url = "https://kinoafisha.ua/kinoafisha/";
        Document page = null;
        try {
            page = Jsoup.connect(url).get();//Jsoup.parse(new URL(url),3000);
        }catch (IOException | IllegalArgumentException e){
            System.out.println(url);
            e.printStackTrace();
        }
        return page;
    }

    private static List<String> getFilmsName() {
        List<String> films = new ArrayList<>();
        Document page = getPage("https://m.vkino.ua/ru/afisha/kiev");
        Elements filmsCase = page.select("div[class=sub-info]");
        for (Element filmC:filmsCase) {
            films.add(filmC.select("span").text());//.first
        }

        return films;
    }

    private static List<String> getLinksOnDescriptionPage(){
        List<String> links = new ArrayList<>();
        Document page = getPage("https://m.vkino.ua/ru/afisha/kiev");
        Elements filmsCase = page.select("div[class=sub-info]");
        for (Element filmC:filmsCase) {
            links.add("https://m.vkino.ua/ru" + filmC.select("a").attr("href"));
        }
        return links;
    }

    private static List<String> getLinksOnDescriptionPage(String startUrl){
        List<String> links = new ArrayList<>();
        Document page = getPage(startUrl);
        Elements filmsCase = page.select("div[class=sub-info]");
        for (Element filmC:filmsCase) {
            links.add("https://m.vkino.ua/ru" + filmC.select("a").attr("href"));
        }
        return links;
    }

    private static String getDescription(String linkOnDescriptionPage){
        Document page = getPage(linkOnDescriptionPage);

        Elements descriptions = page.select("div[class=description-content]");

        return descriptions.select("p").first().text();
    }

    private static LocalTime getDuration(String linkOnDescriptionPage){
        Document page = getPage(linkOnDescriptionPage);

        Elements duration = page.select("ul[class=film-data-list]");

        String result = "";
        for (Element li : duration.select("li")){
            if (li.select("span").text().contains("Длительность:")){
                result = li.text();
                break;
            }
        }

        String[] time = result.split(" ");
        int[] over = new int[2];
        int j = 0;
        for (int i = 0; i < time.length; i++){
            if (time[i].matches("\\d{1,2}")){
                if (j==0 && Integer.parseInt(time[i])<24)
                    over[j] = Integer.parseInt(time[i]);
                j++;
            }
        }

        return LocalTime.of(over[0],over[1]);
    }

    private static List<String> getLinksOnSeancesPages(String starterPage){
        List<String> links = new ArrayList<>();
        Document page = getPage(starterPage);

        Elements days = page.select("select[id=date-url]");

        Elements elements = days.select("option");

        elements.forEach(x -> links.add(starterPage + "?date=" + x.attr("value")));

        return links;
    }

    private static Date getDateOfCurrentPage(String urlToSeancePage) throws IllegalArgumentException{
        Document page = getPage(urlToSeancePage);
        String date = page.select("div[class=data-list ]").attr("data-date");;
        return Date.valueOf(date);
    }

     static void updateSeanceInfoPerOneDay(String urlToSeancePage) throws InterruptedException {
        Document page = getPage(urlToSeancePage);
        String filmName = page.select("h3[class=title]").text();
        int seanceLimit = 0;
        Elements seancesBlock = page.select("div[class=schedule-row ]");

        for (Element filmSession : seancesBlock) {
            if (seanceLimit==10)
                break;

            List<String> urlToCinemaRooms = new ArrayList<>();
            String cinemaName = filmSession.select("span[class=theater-name]").text();
            filmSession.select("li").forEach(x -> urlToCinemaRooms.add(x.select("a").attr("href")));

            if (DbInteraction.getCinemaIdByName(cinemaName) == -1)
                DbInteraction.addCinemaToDB(new Cinema(cinemaName, filmSession.select("address").text()));


            for (int i = 1; i < urlToCinemaRooms.size(); i++) {
                if (seanceLimit==10)
                    break;

                if (urlToCinemaRooms.get(i).isEmpty())
                    break;

                Document cinemaHall = getPage(urlToCinemaRooms.get(i));
                String cinemaHallName = cinemaHall.select("div[class=showtime-hall]").text();

                CinemaHall cinemaRoom = new CinemaHall(cinemaHallName, DbInteraction.getCinemaIdByName(cinemaName));

                if (DbInteraction.getCinemaHallIdByName(cinemaRoom.getName()) == -1) {
                    DbInteraction.addCinemaHall(cinemaRoom);
                    Thread.sleep(500);
                    cinemaRoom = new CinemaHall(cinemaHallName, DbInteraction.getCinemaIdByName(cinemaName));
                    DbInteraction.addPlacesToDB(DbInteraction.getCinemaHallIdByName(cinemaRoom.getName()));
                }

                Seance seance = new Seance();
                String date = cinemaHall.select("div[class=show-time]").text() + ":00";

                if (!date.equals(":00"))
                    seance.setStartTime(Time.valueOf(date));
                else
                    seance.setStartTime(Time.valueOf(LocalTime.now()));

                seance.setFilmId(DbInteraction.getFilmIdByName(filmName));
                seance.setData(getDateOfCurrentPage(urlToSeancePage));
                seance.setCinemaHall(cinemaRoom);
                if (!DbInteraction.isSeanceExist(seance)){
                    DbInteraction.addSeanceToDB(seance);
                    DbInteraction.addTicketsToDB(seance);
                }
                seanceLimit++;
            }

        }
    }

    static void updateAllSeancesInDB() {
        List<String> starterLinks = getLinksOnDescriptionPage();
        updateFilmInDB(starterLinks);

        for (int i = 0; i < starterLinks.size()/4; i++){
            List<String> linksOnSeancePage = getLinksOnSeancesPages(starterLinks.get(i));
            for (int j = 1; j < linksOnSeancePage.size(); j++){
                if (j == 4)
                    break;

                new Thread(new SeanceUpdater(linksOnSeancePage.get(j))).start();
            }
        }
    }

    static void deleteOutputFilm(){
        List<Integer> filmsId = DbInteraction.getFilmsId();

        for (int filmId : filmsId){
            if (!DbInteraction.isExicstSeanceByFilmId(filmId))
                DbInteraction.deleteFilmById(filmId);
        }
    }

    static void deleteOutputSeances(){
        List<Seance> seances = DbInteraction.getSeances(LocalDate.now());
        for (Seance seance : seances){
            if (seance.getStartTime().toLocalTime().isBefore(LocalTime.now())){
                DbInteraction.deleteTicketBySeanceId(seance.getId());
                DbInteraction.deleteSeanceById(seance.getId());
            }

        }
    }

    public static boolean updateFilmInDB(List<String> linksOnDescriptionPage) {
        List<String> currentFilmsInDB = Film.getFilmsNameFromDB();
        List<Film> films = new ArrayList<>();
        List<String> filmsName = getFilmsName();
        //List<String> linksOnDescriptionPage = linksOnDescriptionPages;//getLinksOnDescriptionPage();

        for (int i = 0; i < filmsName.size(); i++){
            if (currentFilmsInDB.size() == 0){
                films.add(new Film(filmsName.get(i),
                        getDescription(linksOnDescriptionPage.get(i)),
                        Time.valueOf(getDuration(linksOnDescriptionPage.get(i)))));
            } else if (!currentFilmsInDB.contains(filmsName.get(i))){
                films.add(new Film(filmsName.get(i),
                        getDescription(linksOnDescriptionPage.get(i)),
                        Time.valueOf(getDuration(linksOnDescriptionPage.get(i)))));
            }
        }
        //currentFilmsInDB.addAll(films);
        films.forEach(DbInteraction::addFilmToDB);
        return true;
    }
}

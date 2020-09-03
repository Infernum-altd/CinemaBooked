package com.altynnikov.service;

import com.altynnikov.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DbInteraction {
    private final static String db_url="jdbc:mysql://localhost/sinemabooked?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private final static String db_user="test";
    private final static String db_password="test";
    private final static String db_driver="com.mysql.cj.jdbc.Driver";

    public static Connection getDBConnection() {
        Connection dbConnection = null;
        try {
            Class.forName(db_driver);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            dbConnection = DriverManager.getConnection(db_url, db_user,db_password);
            return dbConnection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return dbConnection;
    }

    static void addUserToDB(User user){
        try (Connection connection = getDBConnection()) {
            Statement statement = connection.createStatement();
            String insertTableSQL = "INSERT INTO sinemabooked.user"
                    + "(login, password) " + "VALUES"  + "("+ "\'" + user.getLogin() + "\'" + "," + "\'" + Hash.generateHash(user.getPassword()) + "\'" + ")";
            statement.executeUpdate(insertTableSQL);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    static void addPlacesToDB(int cinemaHallId){
        int[][] createdPlaces = new int[10][20];
        int numOfSpace = 0;

        for (int i = 1; i <= createdPlaces.length; i++){
            int j = 1;

            try (Connection connection = DbInteraction.getDBConnection()){
                for (; j <= createdPlaces[1].length-(numOfSpace); j++){
                    String insertSQL = "insert into sinemabooked.place (`row_number`, place_number, cinema_hall_id) values (?,?,?)";
                    PreparedStatement statement = connection.prepareStatement(insertSQL);
                    statement.setInt(1, i);
                    statement.setInt(2, j);
                    statement.setInt(3, cinemaHallId);

                    statement.executeUpdate();
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
            if (i != 0 && i % 2 == 0){
                numOfSpace += 2;
            }
        }
    }

    static void addFilmToDB(Film film){
        try (Connection connection = getDBConnection()) {
            String insertTableSQL = "INSERT INTO sinemabooked.film (name, description, duration) VALUES (?,?,?)";
            PreparedStatement statement = connection.prepareStatement(insertTableSQL);
            statement.setString(1,film.getName());
            statement.setString(2, film.getDescription());
            statement.setTime(3, film.getDuration());
            statement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    static void addSeanceToDB(Seance seance){
        try (Connection connection = getDBConnection()) {
            String insertTableSQL = "INSERT INTO sinemabooked.seance (time, film_id, cinema_hall_id, date) VALUES (?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(insertTableSQL);
            statement.setTime(1,seance.getStartTime());
            statement.setInt(2, seance.getFilmId());
            statement.setInt(3, seance.getCinemaHall().getId());
            statement.setDate(4, seance.getData());
            //System.out.println(seance.toString() + " " + seance.getCinemaHall().getId());
            statement.executeUpdate();
        }catch (SQLException e){
            System.out.println("nonexistent film in DB");
            //e.printStackTrace();
        }
    }

    static void addTicketsToDB(Seance seance){
        try (Connection connection = getDBConnection()) {
            String selectTableSQL = "select * from sinemabooked.seance where time=? && film_id=? && cinema_hall_id=? && date=? ";
            PreparedStatement statement = connection.prepareStatement(selectTableSQL);
            statement.setTime(1,seance.getStartTime());
            statement.setInt(2, seance.getFilmId());
            statement.setInt(3, seance.getCinemaHall().getId());
            statement.setDate(4, seance.getData());
            ResultSet resultSetIDs = statement.executeQuery();

            int seanceId=0;
            int cinemaHallId=0;

            while (resultSetIDs.next()){
                seanceId = resultSetIDs.getInt("id");
                cinemaHallId = resultSetIDs.getInt("cinema_hall_id");
            }

            List<Place> places = new ArrayList<>();
            if (cinemaHallId!=0){
                String selectPlaces = "select * from sinemabooked.place where cinema_hall_id=?";
                statement = connection.prepareStatement(selectPlaces);
                statement.setInt(1,cinemaHallId);
                ResultSet resultSetPlaces = statement.executeQuery();

                while (resultSetPlaces.next()){
                    Place place = new Place();
                    place.setId(resultSetPlaces.getInt("id"));
                    places.add(place);
                }
            }


            for (Place place : places){
                String insertTicket = "insert into sinemabooked.ticket (cost, is_booked, seance_id, place_id) values (?, ?, ?, ?)";
                statement = connection.prepareStatement(insertTicket);
                statement.setInt(1,100);
                statement.setBoolean(2,false);
                statement.setInt(3, seanceId);
                statement.setInt(4, place.getId());
                statement.executeUpdate();
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    static void addCinemaToDB(Cinema cinema){
        try (Connection connection = DbInteraction.getDBConnection()){
            String insertTableSQL = "INSERT INTO sinemabooked.cinema (name, address) VALUES (?,?)";
            PreparedStatement statement = connection.prepareStatement(insertTableSQL);
            statement.setString(1, cinema.getName());
            statement.setString(2, cinema.getAddress());
            statement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    static void addCinemaHall(CinemaHall cinemaHall){
        try (Connection connection = DbInteraction.getDBConnection()){
            String insertTableSQL = "INSERT INTO sinemabooked.cinema_hall (name, cinema_id, number_of_rows, max_number_of_place_in_row) VALUES (?,?,10,20)";
            PreparedStatement statement = connection.prepareStatement(insertTableSQL);
            statement.setString(1, cinemaHall.getName());
            statement.setInt(2, cinemaHall.getCinemaId());
            statement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    static ResultSet getUsersFromDB(){
        ResultSet resultSet = null;
        Connection connection = getDBConnection();
        try {
            Statement statement = connection.createStatement();
            String selectTableSQL = "SELECT id, login, password from sinemabooked.user";
            resultSet = statement.executeQuery(selectTableSQL);
            return resultSet;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return resultSet;
    }

    public static ResultSet getFilmFromDB(){
        ResultSet resultSet = null;
        Connection connection = getDBConnection();
        try {
            Statement statement = connection.createStatement();
            String selectTableSQL = "select * from sinemabooked.film";
            resultSet = statement.executeQuery(selectTableSQL);
            return resultSet;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return resultSet;
    }

    public static ResultSet getSeansesFromDB(int film_id, LocalDate date) {
        ResultSet resultSet = null;
        Connection connection = getDBConnection();
        try {
            String selectTableSQL = "select * from sinemabooked.seance inner join sinemabooked.cinema inner join sinemabooked.cinema_hall where film_id=? && `date`=? && cinema_hall_id = sinemabooked.cinema_hall.id && sinemabooked.cinema_hall.cinema_id = sinemabooked.cinema.id";
            PreparedStatement statement = connection.prepareStatement(selectTableSQL);
            statement.setInt(1,film_id);
            statement.setDate(2, Date.valueOf(date));
            resultSet = statement.executeQuery();
            return resultSet;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return resultSet;
    }

    public static ResultSet getPlacesFromDB(int cinemaHallId){
        ResultSet resultSet = null;
        Connection connection = getDBConnection();
        try {
            String selectTableSQL = "select * from sinemabooked.place where cinema_hall_id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectTableSQL);
            preparedStatement.setInt(1,cinemaHallId);
            resultSet = preparedStatement.executeQuery();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return resultSet;
    }

    public static Place getPlace(int cinemaHallId, int rowNumber, int placeNumber){
        Place place = null;
        try (Connection connection = getDBConnection()){
            String selectSQL = "select * from sinemabooked.place where cinema_hall_id = ? && place_number = ? && sinemabooked.place.`row_number`= ?";
            PreparedStatement statement = connection.prepareStatement(selectSQL);
            statement.setInt(1, cinemaHallId);
            statement.setInt(2, placeNumber);
            statement.setInt(3, rowNumber);
            ResultSet resultSet = statement.executeQuery();


            while (resultSet.next()){
                place = new Place();
                place.setId(resultSet.getInt("id"));
                place.setPlace_Number(resultSet.getInt("place_number"));
                place.setRowNumber(resultSet.getInt("row_number"));
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return place;
    }

    public static ResultSet getTicketsFromDB(int seanseId){
        ResultSet resultSet = null;
        Connection connection = getDBConnection();
        try {
            String selectTableSQL = "select * from sinemabooked.ticket where seance_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectTableSQL);
            preparedStatement.setInt(1, seanseId);
            resultSet = preparedStatement.executeQuery();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return resultSet;
    }

    public static ResultSet getTicketFromDB(int placeId, int seanceId){
        ResultSet resultSet = null;
        Connection connection = getDBConnection();
        try {
            String selectTableSQL = "select * from sinemabooked.ticket where place_id = ? && seance_id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectTableSQL);
            preparedStatement.setInt(1, placeId);
            preparedStatement.setInt(2,seanceId);
            resultSet = preparedStatement.executeQuery();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return resultSet;
    }

    public static boolean getIsTickedBooked(int placeId, int seanceId){
        boolean isBooked = false;
        Connection connection = getDBConnection();
        try {
            String selectTableSQL = "select * from sinemabooked.ticket where place_id = ? && seance_id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectTableSQL);
            preparedStatement.setInt(1, placeId);
            preparedStatement.setInt(2, seanceId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            isBooked = resultSet.getBoolean("is_booked");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return isBooked;
    }

    public static ResultSet getSeancesByDate(LocalDate date){
        ResultSet resultSet = null;
        Connection connection = getDBConnection();
        try {
            String selectTableSQL = "select * from sinemabooked.seance, sinemabooked.film, sinemabooked.cinema_hall where date = ? && film_id = film.id && cinema_hall_id = cinema_hall.id";
            PreparedStatement statement = connection.prepareStatement(selectTableSQL, ResultSet.TYPE_SCROLL_INSENSITIVE);
            statement.setDate(1, Date.valueOf(date));
            resultSet = statement.executeQuery();
            return resultSet;
        }catch (SQLException e){
            e.printStackTrace();
        }

        return resultSet;
    }

    public static List<Seance> getSeances(LocalDate date){
        List<Seance> seances = new ArrayList<>();

        try (Connection connection = getDBConnection();){
            String selectTableSQL = "select * from sinemabooked.seance where date = ?";
            PreparedStatement statement = connection.prepareStatement(selectTableSQL);
            statement.setDate(1, Date.valueOf(date));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                Seance seance = new Seance();
                seance.setId(resultSet.getInt("id"));
                seance.setStartTime(resultSet.getTime("time"));
                seance.setData(resultSet.getDate("date"));
                seance.setFilmId(resultSet.getInt("film_id"));
                seances.add(seance);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return seances;
    }

    public static ResultSet getSeancesByDate(LocalDate date, int filmId){
        ResultSet resultSet = null;
        Connection connection = getDBConnection();
        try {
            String selectTableSQL = "select * from sinemabooked.seance, sinemabooked.film, sinemabooked.cinema_hall where date = ? && film_id = ? && film_id = film.id && cinema_hall_id = cinema_hall.id";
            PreparedStatement statement = connection.prepareStatement(selectTableSQL, ResultSet.TYPE_SCROLL_INSENSITIVE);
            statement.setDate(1, Date.valueOf(date.plusDays(1)));
            statement.setInt(2, filmId);
            resultSet = statement.executeQuery();
            return resultSet;
        }catch (SQLException e){
            e.printStackTrace();
        }

        return resultSet;
    }

    public static void addComment(String comment, int userId, int filmId){
        try (Connection connection = getDBConnection()) {
            String insertTableSQL = "INSERT INTO sinemabooked.comments(comment, film_id, user_id) VALUES (?,?,?)";
            PreparedStatement statement = connection.prepareStatement(insertTableSQL);
            statement.setString(1,comment);
            statement.setInt(2,filmId);
            statement.setInt(3,userId);
            statement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static List<Comment> getComments(int filmId){
        List<Comment> commentsFromDB = new ArrayList<>();
        try (Connection connection = getDBConnection()) {
            String selectTableSQL = "select * from sinemabooked.comments, sinemabooked.user where film_id = ? && user_id = user.id";
            PreparedStatement statement = connection.prepareStatement(selectTableSQL);
            statement.setInt(1,filmId);
            ResultSet dataFromDb = statement.executeQuery();

            while (dataFromDb.next()){
                commentsFromDB.add(new Comment(dataFromDb.getString("login"),dataFromDb.getString("comment"), dataFromDb.getInt("user_id")));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return commentsFromDB;
    }

    public static List<Ticket> getTicketsInfoByUserId(int userId){
        ResultSet dataFromDb = null;
        List<Ticket> ticketsFromDB = new ArrayList<>();
        Connection connection = getDBConnection();
        try {
            String selectTableSQL = "select * from sinemabooked.ticket inner join sinemabooked.place inner join sinemabooked.seance inner join sinemabooked.cinema inner join sinemabooked.cinema_hall  on place_id = sinemabooked.place.id && user_id = ? && seance_id = sinemabooked.seance.id && sinemabooked.seance.cinema_hall_id = sinemabooked.cinema_hall.id && sinemabooked.cinema_hall.cinema_id = sinemabooked.cinema.id";

            PreparedStatement preparedStatement = connection.prepareStatement(selectTableSQL);
            preparedStatement.setInt(1, userId);
            dataFromDb = preparedStatement.executeQuery();

            while (dataFromDb.next()){
                dataFromDb.getString("name");
                ticketsFromDB.add(Ticket.createTicketForPDF(dataFromDb.getInt("id"),
                        dataFromDb.getInt("cost"),
                        dataFromDb.getBoolean("is_booked"),
                        dataFromDb.getInt("place_id"),
                        dataFromDb.getString("name"),
                        dataFromDb.getString("address"),
                        dataFromDb.getTime("time"),
                        dataFromDb.getDate("date"),
                        new Place(dataFromDb.getInt("row_number"), dataFromDb.getInt("place_number")),
                        dataFromDb.getInt("seance_id")));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return ticketsFromDB;
    }

    public static Film getFilmById(int id){
        Film filmFromDb = null;
        try (Connection connection = getDBConnection()) {
            String selectTableSQL = "select * from sinemabooked.film where  id=?";
            PreparedStatement statement = connection.prepareStatement(selectTableSQL);
            statement.setInt(1,id);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            filmFromDb = new Film(resultSet.getString("name"), resultSet.getString("description"), resultSet.getTime("duration"));
        }catch (SQLException e){
            e.printStackTrace();
        }
        return filmFromDb;
    }

    public static List<Seance> getSeancesDate(){
        List<Seance> seances = new ArrayList<>();

        try (Connection connection = getDBConnection()){
            String selectTableSQL = "select * from sinemabooked.seance";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(selectTableSQL);

            while (resultSet.next()){
                Seance seance = new Seance();
                seance.setId(resultSet.getInt("id"));
                seance.setData(resultSet.getDate("date"));

                seances.add(seance);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return seances;
    }

    static void deleteSeanceById(int seanceId){
        try (Connection connection = getDBConnection()) {

            deleteTicketsBySeanceId(seanceId);

            String deleteTableSQL = "delete from sinemabooked.seance where id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteTableSQL);
            preparedStatement.setInt(1, seanceId);
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void deleteTicketsBySeanceId(int seanceId){
        try (Connection connection = getDBConnection()) {
            String deleteTableSQL = "delete from sinemabooked.ticket where seance_id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteTableSQL);
            preparedStatement.setInt(1, seanceId);
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    static void deleteFilmById(int filmId){

        deleteCommentsByFilmId(filmId);

        try (Connection connection = getDBConnection()) {
            String deleteTableSQL = "delete from sinemabooked.film where id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteTableSQL);
            preparedStatement.setInt(1, filmId);
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void deleteCommentsByFilmId(int filmId){
        try (Connection connection = getDBConnection()) {
            String deleteTableSQL = "delete from sinemabooked.comments where film_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteTableSQL);
            preparedStatement.setInt(1, filmId);
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    static Seance getSeanceByFilmId(int filmId){
        Seance seance = null;
        try (Connection connection = getDBConnection()) {
            String deleteTableSQL = "select * from sinemabooked.seance where film_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteTableSQL);
            preparedStatement.setInt(1, filmId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                seance = new Seance();
                seance.setId(resultSet.getInt("id"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return seance;
    }

    static int getCinemaIdByName(String cinemaName){
        int cinemaId = -1;
        try (Connection connection = DbInteraction.getDBConnection()){
            String selectSQL = "select * from sinemabooked.cinema where name=?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
            preparedStatement.setString(1,cinemaName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next())
                cinemaId = resultSet.getInt("id");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return cinemaId;
    }

    static int getFilmIdByName(String filmName){
        int cinemaId = -1;
        try (Connection connection = DbInteraction.getDBConnection()){
            String selectSQL = "select * from sinemabooked.film where name=?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
            preparedStatement.setString(1,filmName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next())
                cinemaId = resultSet.getInt("id");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return cinemaId;
    }

    public static int getCinemaHallIdByName(String cinemaHallName){
        int cinemaId = -1;
        try (Connection connection = DbInteraction.getDBConnection()){
            String selectSQL = "select * from sinemabooked.cinema_hall where name=?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
            preparedStatement.setString(1,cinemaHallName);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next())
                cinemaId = resultSet.getInt("id");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return cinemaId;
    }

    public static String getFilmNameBySeanceId(int seanceId){
        String filmName = "";
        try (Connection connection = getDBConnection()){
            String selectSQL = "select name from sinemabooked.film inner join sinemabooked.seance where sinemabooked.seance.id = ? && film_id = sinemabooked.film.id";
            PreparedStatement statement = connection.prepareStatement(selectSQL);
            statement.setInt(1, seanceId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                filmName = resultSet.getString("name");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return filmName;
    }

    static List<Integer> getFilmsId(){
        List<Integer> filmsId = new ArrayList<>();
        try (Connection connection = DbInteraction.getDBConnection()){
            String selectSQL = "select id from sinemabooked.film";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(selectSQL);
            while (resultSet.next()){
                filmsId.add(resultSet.getInt("id"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return filmsId;
    }

    static boolean isExicstSeanceByFilmId(int filmId){
        boolean isExist = false;
        try (Connection connection = DbInteraction.getDBConnection()){
            String selectSQL = "select * from sinemabooked.seance where film_id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
            preparedStatement.setInt(1, filmId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                isExist = true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return isExist;
    }

    static void deleteTicketBySeanceId(int seanceId){
        try (Connection connection = DbInteraction.getDBConnection()){
            String deleteSQL = "delete from sinemabooked.ticket where seance_id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL);
            preparedStatement.setInt(1,seanceId);
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    static boolean isSeanceExist(Seance seance){
        boolean result = false;
        try (Connection connection = DbInteraction.getDBConnection()){
            String selectSQL = "select * from sinemabooked.seance where film_id=? && time=? && date=? && cinema_hall_id=?";
            PreparedStatement statement = connection.prepareStatement(selectSQL);
            statement.setInt(1, seance.getFilmId());
            statement.setTime(2, seance.getStartTime());
            statement.setDate(3, seance.getData());
            statement.setInt(4, seance.getCinemaHall().getId());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                result = true;
                break;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return result;
    }

}

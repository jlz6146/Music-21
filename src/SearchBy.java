import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.*;
import java.util.Scanner;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

public class SearchBy {

    public enum OrderType { title, artist, album, genre, year }
    public static Scanner search = new Scanner(System.in);

    public static void Search(Connection conn) throws SQLException {


        Statement stmt = conn.createStatement();
        System.out.println("Input search keyword (title, artist, album, genre, release year):");
        String input = search.nextLine();

        String searchQuery = "Select song_id, title, artist_name, album_name, length From song Where ";
        String whereStmt = "title like '%" + input + "%' Or artist_name like '%" + input + "%' Or album_name like '%" + input + "%' Or genre_name like '%" + input +"%'";

        System.out.println(searchQuery + whereStmt);
        ResultSet rs = stmt.executeQuery(searchQuery + whereStmt);
        while (rs.next()){
            int listenCount = totalListen(stmt, rs.getString("song_id"));
            System.out.println(rs.getString("title") + " " + rs.getString("artist_name") + " " + rs.getString("album_name") + " " + rs.getTime("length") + " " + listenCount);
        }

        /**
        System.out.println("Order songs by: title | artist | album | genre | year");
        String orderIn = search.nextLine().replaceAll("[^A-Za-z]+", "").toLowerCase();
        System.out.println("asc | desc | null");
        String ordering = search.nextLine().replaceAll("[^A-Za-z]+", "").toLowerCase();

        rs = stmt.executeQuery(searchQuery + whereStmt + orderBy(orderIn, ordering));
        if (rs.next()){
            System.out.println(rs.getString("title") + " " + rs.getString("artist_name") + " " + rs.getString("album_name") + " " + rs.getTime("length"));
        }
         */

    }

    public static int totalListen(Statement stmt, String songID) throws SQLException {
        String songQuery = "Select sum(listen_count) as listenCount from user_play Where song_id = '" + songID + "'";
        ResultSet rs = stmt.executeQuery(songQuery);
        return rs.getInt("listenCount");
    }

    public static String orderBy(String orderInput, String ordering){
        //OrderType type = OrderType.valueOf(orderInput);
        String TypeString = "Order By '" + orderInput + "'";
        if (!ordering.equals("null")) {
            TypeString += " " + ordering;
        }
        return TypeString;
    }

}

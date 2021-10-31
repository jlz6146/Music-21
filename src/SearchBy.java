import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.*;
import java.util.Locale;
import java.util.Scanner;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

public class SearchBy {

    public enum OrderType {
        title ("S.title"),
        artist ("S.artist_name"),
        album ("A.album_name"),
        genre ("S.genre_name"),
        year ("DATEPART('year', S.release_date)");

        public final String label;

        private OrderType(String label){
            this.label = label;
        }
    }

    public enum OrderDirection { asc, desc }
    public static Scanner search = new Scanner(System.in);

    public static void Search(Connection conn) throws SQLException {


        Statement stmt = conn.createStatement();

            System.out.println("Input search keyword (title, artist, album, genre):");
            String input = search.nextLine();

            /*String sQuery =
                    "Select S.song_id, S.title, S.artist_name, A.album_name, S.length, S.release_date, U_P.plays" +
                            " From song as S, album_songs as A_S, album as A," +
                            " (Select song_id, sum(user_play_count) as plays From user_play Group By song_id) as U_P" +
                            " Where S.song_id = A_S.song_id AND A_S.album_id = A.album_id AND" +
                            " (S.title like '%" + input.toLowerCase() + "%' OR S.artist_name like '%" + input.toUpperCase() + "%' OR S.genre_name like '%" + input.toLowerCase() + "%'OR A.album_name LIKE '%" + input.toUpperCase() + "')";
*/
            PreparedStatement sQuery = conn.prepareStatement("select song.song_id, song.title, song.artist_name, album.album_name, song.length, song.release_date, coalesce(u_p.plays, 0) as plays " +
                        "from song " +
                        "inner join album_songs on album_songs.song_id = song.song_id " +
                        "inner join album on album_songs.album_id = album.album_id " +
                        "left join (select song_id, sum(user_play_count) as plays from user_play group by song_id) as u_p on u_p.song_id = song.song_id " +
                        "Where (song.title like ? or song.artist_name like ? or song.genre_name like ? or album.album_name like ?) ";

            sQuery.setString(1, "%" + input.toLowerCase() + "%");
            sQuery.setString(2, "%" + input.toUpperCase() + "%");
            sQuery.setString(3, "%" + input.toLowerCase() + "%");
            sQuery.setString(4, "%" + input.toUpperCase() + "%");

            ResultSet rs = sQuery.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("title") + " " + rs.getString("artist_name") + " " + rs.getString("album_name") + " " + rs.getTime("length") + " " + rs.getDate("release_date") + " " + rs.getInt("plays"));
            }

            while(true) {
                System.out.println("reorder (reorder)     ||      search again (search)    ||      exit search (exit)");
                String inNext = search.nextLine().replaceAll("[^A-Za-z]+ ", "").toLowerCase();
                if (inNext.equals("search")) {
                    Search(conn);
                }
                else if (inNext.equals("exit")) {
                    break;
                }
                else if (!inNext.equals("reorder") && !inNext.equals("search") && !inNext.equals("exit")) {
                    System.out.println("Sorry, please input a valid argument.");
                }
                else if (inNext.equals("reorder")) {
                    OrderType type = null;
                    OrderDirection dir = null;

                    while (true) {
                        System.out.println("reorder by: title | artist | album | genre | year");
                        String orderBy = search.nextLine();
                        try {
                            type = OrderType.valueOf(orderBy);
                            break;
                        } catch (IllegalArgumentException E) {
                            System.out.println("Invalid order type, please try again");
                            continue;
                        }
                    }

                    while (true) {
                        System.out.println("ascending/descending? (asc/desc)");
                        String ascDesc = search.nextLine();
                        try {
                            dir = OrderDirection.valueOf(ascDesc);
                            break;
                        } catch (IllegalArgumentException F) {
                            System.out.println("Invalid order direction, please try again");
                            continue;
                        }
                    }

                    //PreparedStatement orderQuery = conn.createStatement(sQuery + " Order By " + type + " " + dir);
                    //rs = orderQuery.executeQuery();
                    while (rs.next()) {
                        System.out.println(rs.getString("title") + " | " + rs.getString("artist_name") + " | " + rs.getString("album_name") + " | " + rs.getTime("length") + " | " + rs.getDate("release_date") + " | " + rs.getInt("plays"));
                    }
                }
            }
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



   /* public static int totalListen(Statement stmt, int songID) throws SQLException {
        String songQuery = "Select sum(user_play_count) as ListenCount from user_play Where song_id = '" + songID + "'";
        ResultSet rs = stmt.executeQuery(songQuery);
        if (rs.next()) {
            return rs.getInt("ListenCount");
        }
        return -1;
    }*/

    public static String orderBy(String orderInput){
        OrderType type = null;
        OrderDirection dir = null;
        String TypeString = "";
        String[] input = orderInput.split(" ");
        System.out.println(input[0]);
        try { type = OrderType.valueOf(input[0]); }
        catch(IllegalArgumentException E){
        }

        if ((type != null) || (input[1] != null)) {
            TypeString = "Order By '" + type + "'";
            try { dir = OrderDirection.valueOf(input[1]); }
            catch (IllegalArgumentException F) {
            }
            catch (ArrayIndexOutOfBoundsException G){

            }
            if (dir != null) {
                TypeString += " " + dir;
            }
        }
        System.out.println(TypeString);
        return TypeString;
    }

}

import java.sql.*;
import java.util.Scanner;

public class SearchBy {

    public enum OrderType { title, artist, album, genre, year }
    public enum OrderDirection { asc, desc }
    public static Scanner search = new Scanner(System.in);

    public static void Search(Connection conn) throws SQLException {


//        Statement stmt = conn.createStatement();


        System.out.println("Input search keyword (title, artist, album, genre, release year):");
        String input = search.nextLine();
        System.out.println("What would you like to order by? (title, artist, album, genre)");
        String orderTerm = search.nextLine();
        if (orderTerm.equals("album")) {
            orderTerm = "album." + orderTerm + "_name";
        } else if (orderTerm.equals("title")) {
            orderTerm = "song." + orderTerm;
        }  else {
            orderTerm = "song." + orderTerm + "_name";
        }
        System.out.println("How would you like it ordered? (asc, desc)");
        String orderDirection = search.nextLine();

//        PreparedStatement pStmt = conn.prepareStatement("Select S.song_id, S.title, S.artist_name, A.album_name, S.length, S.release_date, U_P.plays" +
//                " From song as S, album_songs as A_S, album as A," +
//                " (Select song_id, sum(user_play_count) as plays From user_play Group By song_id) as U_P" +
//                " Where S.song_id = A_S.song_id AND A_S.album_id = A.album_id AND" +
//                " (S.title like ? or S.artist_name like ? or s.genre_name like ? or a.album_name like ?) " +
//                "order by " + orderTerm + " " + orderDirection);

        PreparedStatement pStmt = conn.prepareStatement("select song.song_id, song.title, song.artist_name, album.album_name, song.length, song.release_date, coalesce(u_p.plays, 0) as plays " +
                "from song " +
                "inner join album_songs on album_songs.song_id = song.song_id " +
                "inner join album on album_songs.album_id = album.album_id " +
                "left join (select song_id, sum(user_play_count) as plays from user_play group by song_id) as u_p on u_p.song_id = song.song_id " +
                "Where (song.title like ? or song.artist_name like ? or song.genre_name like ? or album.album_name like ?) " +
                "order by " + orderTerm + " " + orderDirection);

        pStmt.setString(1, "%" + input.toLowerCase() + "%");
        pStmt.setString(2, "%" + input.toUpperCase() + "%");
        pStmt.setString(3, "%" + input.toLowerCase() + "%");
        pStmt.setString(4, "%" + input.toUpperCase() + "%");

//        String sQuery =
//                "Select S.song_id, S.title, S.artist_name, A.album_name, S.length, S.release_date, U_P.plays" +
//                        " From song as S, album_songs as A_S, album as A," +
//                        " (Select song_id, sum(user_play_count) as plays From user_play Group By song_id) as U_P" +
//                        " Where S.song_id = A_S.song_id AND A_S.album_id = A.album_id AND" +
//                        " (S.title like '%" + input + "%' OR S.artist_name like '%" + input.toUpperCase() + "%' OR S.genre_name like '%" + input + "%'OR A.album_name LIKE '%" + input + "')";

//        System.out.println(sQuery);
//        ResultSet rs = stmt.executeQuery(sQuery);
        ResultSet rs = pStmt.executeQuery();
        while (rs.next()) {
            System.out.println("ID: " + rs.getString("song_id") +
                    " | Title: \"" + rs.getString("title") + "\" | Artist: \""
                    + rs.getString("artist_name") + "\" | Album: \"" + rs.getString("album_name") +
                    "\" | Length: " + rs.getTime("length") + " | Release Date: " +
                    rs.getDate("release_date") + " | Plays: " + rs.getInt("plays"));
        }

        /*System.out.println("reorder: title | artist | album | genre | year         exit search? (y/n)");
        String inNext = search.nextLine().replaceAll("[^A-Za-z]+", "").toLowerCase();
        if (inNext.equals("y")){
            break;
        }
        if (!inNext.equals("n") && !inNext.equals("y")){
            orderBy(inNext);
        }*/

        /*
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


   /* public static int totalListen(Statement stmt, int songID) throws SQLException {
        String songQuery = "Select sum(user_play_count) as ListenCount from user_play Where song_id = '" + songID + "'";
        ResultSet rs = stmt.executeQuery(songQuery);
        if (rs.next()) {
            return rs.getInt("ListenCount");
        }
        return -1;
    }*/

    /*public static String orderBy(String orderInput){
        OrderType type = null;
        OrderDirection dir = null;
        String TypeString = "";
        String input[] = orderInput.split(" ", 2)
                ;        try { type = OrderType.valueOf(input[0]); }
        catch(IllegalArgumentException E){
        }

        if (type != null) {
            TypeString = "Order By '" + type + "'";
            try { dir = OrderDirection.valueOf(input[1]); }
            catch (IllegalArgumentException F) {
            }
            if (dir != null) {
                TypeString += " " + dir;
            }
        }
        return TypeString;
    }*/
}

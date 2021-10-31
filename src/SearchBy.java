import java.sql.*;
import java.util.Scanner;

public class SearchBy {

    public enum OrderType { title, artist, album, genre, year }
    public enum OrderDirection { asc, desc }
    public static Scanner search = new Scanner(System.in);

    public static void Search(Connection conn) throws SQLException {
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

        ResultSet rs = pStmt.executeQuery();
        while (rs.next()) {
            System.out.println("ID: " + rs.getString("song_id") +
                    " | Title: \"" + rs.getString("title") + "\" | Artist: \""
                    + rs.getString("artist_name") + "\" | Album: \"" + rs.getString("album_name") +
                    "\" | Length: " + rs.getTime("length") + " | Release Date: " +
                    rs.getDate("release_date") + " | Plays: " + rs.getInt("plays"));
        }
    }
}

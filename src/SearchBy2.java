import java.sql.*;
import java.util.Scanner;

public class SearchBy2 {

    /**
     * The different parameters a user can order their search by
     */
    public enum OrderType {
        title ("song.title"),
        artist ("song.artist_name"),
        album ("album.album_name"),
        genre ("song.genre_name"),
        year ("song.release_date");

        public final String label;

        OrderType(String label){
            this.label = label;
        }
    }

    /**
     * Allows the user to choose how their search results are ordered
     */
    public enum OrderDirection { asc, desc }
    public static Scanner search = new Scanner(System.in);

    /**
     * Allows a user to easily search through songs in the database
     * @param conn a connection to the database storing all necessary data
     * @throws SQLException included in the off case there is a database access error
     */
    public static void Search(Connection conn) throws SQLException {
        boolean searchRunning = true;

        System.out.println("Input search keyword (title, artist, album, genre):");
        String input = search.nextLine();

        String searchBy = "select song.song_id, song.title, song.artist_name, album.album_name, song.length, song.release_date, coalesce(u_p.plays, 0) as plays " +
                "from song " +
                "inner join album_songs on album_songs.song_id = song.song_id " +
                "inner join album on album_songs.album_id = album.album_id " +
                "left join (select song_id, sum(user_play_count) as plays from user_play group by song_id) as u_p on u_p.song_id = song.song_id " +
                "Where (song.title like ? or song.artist_name like ? or song.genre_name like ? or album.album_name like ?) ";
        String orderBy = "Order by song.title, song.artist_name asc";

        PreparedStatement sQuery = conn.prepareStatement(searchBy + orderBy);

        sQuery.setString(1, "%" + input.toLowerCase() + "%");
        sQuery.setString(2, "%" + input.toUpperCase() + "%");
        sQuery.setString(3, "%" + input.toLowerCase() + "%");
        sQuery.setString(4, "%" + input.toUpperCase() + "%");

        ResultSet rs = sQuery.executeQuery();
        while (rs.next()) {
            System.out.println("ID: " + rs.getString("song_id") +
                    " | Title: \"" + rs.getString("title") + "\" | Artist: \""
                    + rs.getString("artist_name") + "\" | Album: \"" + rs.getString("album_name") +
                    "\" | Length: " + rs.getTime("length") + " | Release Date: " +
                    rs.getDate("release_date") + " | Plays: " + rs.getInt("plays"));
        }

        while(searchRunning) {
            System.out.println("reorder (reorder)     ||      search again (search)    ||      exit search (exit)");
            String inNext = search.nextLine().replaceAll("[^A-Za-z]+ ", "").toLowerCase();
            if (inNext.equals("search")) {
                searchRunning = false;
                Search(conn);
            }
            else if (inNext.equals("exit")) {
                searchRunning = false;
            }
            else if (!inNext.equals("reorder") && !inNext.equals("search") && !inNext.equals("exit")) {
                System.out.println("Sorry, please input a valid argument.");
            }
            else if (inNext.equals("reorder")) {
                OrderType type = null;
                OrderDirection dir = null;

                while (true) {
                    System.out.println("reorder by: title | artist | album | genre | year");
                    String orderByTerm = search.nextLine().replaceAll("[^A-Za-z]+ ", "").toLowerCase();
                    try {
                        type = OrderType.valueOf(orderByTerm);
                        break;
                    } catch (IllegalArgumentException E) {
                        System.out.println("Invalid order type, please try again");
                    }
                }

                while (true) {
                    System.out.println("ascending/descending? (asc/desc)");
                    String ascDesc = search.nextLine().replaceAll("[^A-Za-z]+ ", "").toLowerCase();
                    try {
                        dir = OrderDirection.valueOf(ascDesc);
                        break;
                    } catch (IllegalArgumentException F) {
                        System.out.println("Invalid order direction, please try again");
                    }
                }

                PreparedStatement orderQuery = conn.prepareStatement(searchBy + " Order By " + type.label + " " + dir);
                rs = orderQuery.executeQuery();
                while (rs.next()) {
                    System.out.println("ID: " + rs.getString("song_id") +
                            " | Title: \"" + rs.getString("title") + "\" | Artist: \""
                            + rs.getString("artist_name") + "\" | Album: \"" + rs.getString("album_name") +
                            "\" | Length: " + rs.getTime("length") + " | Release Date: " +
                            rs.getDate("release_date") + " | Plays: " + rs.getInt("plays"));
                }
            }
        }
    }
}

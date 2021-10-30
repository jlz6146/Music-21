import java.sql.*;

public class AccountCommands {

    /**
     * Prints out all collections from a given user
     * @param conn a connection to the database storing all necessary data
     * @param username the username of the current user
     */
    public static void show_collections(Connection conn, String username){
        PreparedStatement pStmt; ResultSet rSet;
        try{
            pStmt = conn.prepareStatement("Select collection_name, collection_id from collection " +
                    "where username = ?" +
                    "order by collection_name");
            pStmt.setString(1, username);
            rSet = pStmt.executeQuery();
            while(rSet.next()){
                String collection_name = rSet.getString("collection_name");
                int songs;
                String dur;
                songs = getSongInfo(conn, rSet.getInt("collection_id"));
                dur = getCount(conn, rSet.getInt("collection_id"), songs);

                System.out.println("Name: " + collection_name + " | No.Songs: " + songs + " | Duration: " + dur);
            }

        } catch (SQLException sqle) {
            System.out.println("SQLExepction: " + sqle);
        }

    }

    /**
     * helper function for show_collections. Gathers number of songs in a collection given a collection id.
     * @param conn a connection to the database storing all necessary data
     * @param collection_id a collection the user wants to get song information from
     * @return an integer showing the number of songs in the collection
     */
    public static int getSongInfo(Connection conn, int collection_id){
        PreparedStatement pStmt; ResultSet rSet;
        int songs = 0;
        try{
            pStmt = conn.prepareStatement("Select length from song inner join collection_songs " +
                    "on song.song_id = collection_songs.song_id " +
                    "where collection_songs.collection_id = ?");
            pStmt.setInt(1,collection_id);
            rSet = pStmt.executeQuery();
            while(rSet.next()){
                songs++;
            }

        } catch (SQLException sqle) {
            System.out.println("SQLException: " + sqle);
        }
        return songs;
    }

    /**
     * helper function for show_collections. Counts up the duration of all songs in a collection,
     * given a collection id.
     * @param conn a connection to the database storing all necessary data
     * @param collection_id a collection the user owns
     * @param songs an integer with the number of songs in the collection
     * @return a string showing the total runtime of the collection
     */
    public static String getCount(Connection conn, int collection_id, int songs){
        PreparedStatement pStmt; ResultSet rSet;
        if(songs==0){
            return "00:00:00";
        }
        try{
            pStmt = conn.prepareStatement("select sum(song.length) as runtime from collection_songs " +
                    "inner join song on song.song_id = collection_songs.song_id " +
                    "where collection_id = ?");
            pStmt.setInt(1,collection_id);
            rSet = pStmt.executeQuery();
            rSet.next();
            return rSet.getString("runtime");
        } catch (SQLException sqle) {
            System.out.println("SQLException: " + sqle);
        }
        return "error getting count";
    }

    /**
     * adds a song or an album to a collection
     * @param conn a connection to the database storing all necessary data
     * @param username the username of the current user
     * @param collection_id a collection the user owns
     * @param ID either a song_id or an album_id to be added to the given collection
     * @param isAlbum a boolean saying if the given ID is an album (true) or a single song_id (false)
     */
    public static void add_songs(Connection conn, String username, int collection_id, int ID, boolean isAlbum){
        PreparedStatement pStmt; ResultSet rSet;
        if(isAlbum){    // need to add entire album to the collection (ID is album_id)
            try{
                pStmt = conn.prepareStatement("select album_id, song_id from album_songs" +
                        "w here album_id = ?");
                pStmt.setInt(1,ID);
                rSet = pStmt.executeQuery();
                while(rSet.next()){
                    pStmt = conn.prepareStatement("insert into collection_songs (username, collection_id, " +
                            "song_id) values(?, ?, ?");
                    pStmt.setString(1,username);
                    pStmt.setInt(2,collection_id);
                    pStmt.setInt(3,ID);
                    pStmt.executeUpdate();
                }
            } catch (SQLException sqle) {
                System.out.println("SQLException: " + sqle);
            }
        }
        else{    // only need to add one song to the collection (ID is song_id)
            try{
                pStmt = conn.prepareStatement("insert into collection_songs (username, collection_id, song_id)" +
                        " values(?, ?, ?");
                pStmt.setString(1,username);
                pStmt.setInt(2,collection_id);
                pStmt.setInt(3,ID);
                pStmt.executeUpdate();

            } catch (SQLException sqle) {
                System.out.println("SQLException: " + sqle);
            }
        }
    }

    /**
     * deletes a song or an album from a collection
     * @param conn a connection to the database storing all necessary data
     * @param username the username of the current user
     * @param collection_id a collection the user owns
     * @param ID either a song_id or an album_id to be removed to the given collection
     * @param isAlbum a boolean saying if the given ID is an album (true) or a single song_id (false)
     */
    public static void delete_songs(Connection conn, String username, int collection_id, int ID, boolean isAlbum){
        PreparedStatement pStmt; ResultSet rSet;
        if(isAlbum){    // need to delete entire album from the collection (ID is album_id)
            try{
                pStmt = conn.prepareStatement("select album_id, song_id from album_songs" +
                        "w here album_id = ?");
                pStmt.setInt(1,ID);
                rSet = pStmt.executeQuery();
                while(rSet.next()){
                    pStmt = conn.prepareStatement("delete from collection_songs " +
                            "where username = ? and collection_id = ? and song_id = ?");
                    pStmt.setString(1,username);
                    pStmt.setInt(2,collection_id);
                    pStmt.setInt(3,ID);
                    pStmt.executeUpdate();
                }
            } catch (SQLException sqle) {
                System.out.println("SQLException: " + sqle);
            }
        }
        else{    // only need to delete one song from the collection (ID is song_id)
            try{
                pStmt = conn.prepareStatement("delete from collection_songs " +
                            "where username = ? and collection_id = ? and song_id = ?");
                pStmt.setString(1,username);
                pStmt.setInt(2,collection_id);
                pStmt.setInt(3,ID);
                pStmt.executeUpdate();

            } catch (SQLException sqle) {
                System.out.println("SQLException: " + sqle);
            }
        }
    }
}

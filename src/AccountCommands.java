import java.sql.*;

public class AccountCommands {

    /**
     * prints out all collections from a given user
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
    public static void add_songs(Connection conn, String collection_name, int songID, int albumID){

    }


    public static void delete_songs(Connection conn, String collection_name, int songID, int albumID){

    }
     */
}

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
                int duration, songs;
                int[] arr = getSongInfo(conn, rSet.getInt("collection_id"));
                duration = arr[0];
                songs = arr[1];

                System.out.println("Name: " + collection_name + "No. Songs: " + songs + "Duration: " + duration);
            }

        } catch (SQLException sqle) {
            System.out.println("SQLExepction: " + sqle);
        }

    }

    public static int[] getSongInfo(Connection conn, int collection_id){
        PreparedStatement pStmt; ResultSet rSet;
        int dur = 0, songs = 0;
        int[] arr = new int[2];
        try{
            pStmt = conn.prepareStatement("Select length from song inner join collection_songs " +
                    "on song.song_id = collection_songs.song_id " +
                    "where collection_songs.collection_id = ?");
            pStmt.setInt(1,collection_id);
            rSet = pStmt.executeQuery();
            while(rSet.next()){
                dur += rSet.getInt("length");
                songs++;
            }

        } catch (SQLException sqle) {
            System.out.println("SQLExepction: " + sqle);
        }
        arr[0] = dur;
        arr[1] = songs;

        return arr;
    }


    /**
    public static void add_songs(Connection conn, String collection_name, int songID, int albumID){

    }


    public static void delete_songs(Connection conn, String collection_name, int songID, int albumID){

    }
     */
}

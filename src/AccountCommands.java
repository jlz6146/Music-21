import java.sql.*;

public class AccountCommands {

    /**
     * Displays to the user what commands do what
     */
    public static void help(){
        System.out.println("""
                !help
                !search
                !follow [other email]
                !unfollow [other email]
                !create_collection [name]
                !change_collection_name [id] [new_name]
                !show_collections
                !add_songs [collection ID] [song/album ID] ["true" if album; "false" otherwise]
                !delete_collection [collection ID]
                !delete_songs [collection ID] [song/album ID] ["true" if album; "false" otherwise]
                !play_collection [collection ID]
                !play_song [song ID]
                !logout
                """);

    }


    /**
     * Handles the follow command on the database
     * @param conn - the database connection
     * @param username - the username of the follower; person inputting command
     * @param other_email - the email of the person to be followed
     */
    public static void follow(Connection conn, String username, String other_email){
        //TODO: implement the call in PostgresSSHTest
        ResultSet rset; PreparedStatement stment; boolean exists;
        String local_email;

        try {
            stment = conn.prepareStatement("select email from users where username = ?");
            stment.setString(1, username);

            //Query if a user holds the given email
            rset = stment.executeQuery();
            rset.next();
            local_email = rset.getString(1);
        }
        catch(SQLException sqle){
            System.out.println("SQLException: " +sqle);
            return;
        }

        try {
            stment = conn.prepareStatement("select email from users where email = ?");
            stment.setString(1, other_email);

            //Query if a user holds the given email
            rset = stment.executeQuery();

            exists = rset.next();
        }
        catch(SQLException sqle){
            System.out.println("Enter a valid email!");
            return;
        }

        //Check if user exists under given email
        if(exists){
            try {
                stment = conn.prepareStatement("insert into follows (follower, following) values(?, ?)");
                stment.setString(1, local_email);
                stment.setString(2, other_email);

                stment.executeUpdate();
                System.out.println("You are now following: " + other_email + ".");
            }
            catch(SQLException sqle){
                System.out.println("Could not insert tuple. " +sqle);
            }
        }
        else{
            System.out.println(other_email + " is not connected to a user.");
        }


        try{ stment.close(); }
        catch(SQLException sqle){ System.out.println("SQLException: " +sqle); }
    }

    /**
     * Handles the unfollow command on the database
     * @param conn - the database connection
     * @param username - the username of the follower
     * @param other_email - the email of the person to be unfollowed
     */
    public static void unfollow(Connection conn, String username, String other_email){
        //TODO: implement the call in PostgresSSHTest
        PreparedStatement stment; ResultSet rset; boolean exists;
        String local_email;

        try {
            stment = conn.prepareStatement("select email from users where username = ?");
            stment.setString(1, username);

            //Query if a user holds the given email
            rset = stment.executeQuery();
            rset.next();
            local_email = rset.getString(1);
        }
        catch(SQLException sqle){
            System.out.println("SQLException: " +sqle);
            return;
        }

        try {
            stment = conn.prepareStatement("select following from follows where follower = ? and following = ?");
            stment.setString(1, local_email);
            stment.setString(2, other_email);

            //Query the given follows tuple
            rset = stment.executeQuery();

            exists = rset.next();
        }
        catch(SQLException sqle){
            System.out.println("SQLException: " +sqle);
            return;
        }

        //Check if the follows tuple exists
        if(exists){
            try {
                stment = conn.prepareStatement("delete from follows where follower = ? and following = ?");
                stment.setString(1, local_email);
                stment.setString(2, other_email);

                stment.executeUpdate();
                System.out.println("You unfollowed: " + other_email + ".");
            }
            catch(SQLException sqle){
                System.out.println("Could not delete tuple: " +sqle);
            }
        }
        else{
            System.out.println("You are currently not following " + other_email + ".");
        }

        try{ stment.close(); }
        catch(SQLException sqle){ System.out.println("SQLException: " +sqle); }
    }

    public static void create_collection(Connection conn, String username, String coll_name){
        //TODO: implement the call in PostgresSSHTest
        PreparedStatement stment; ResultSet rset; int coll_id; int max_id;

        try {
            stment = conn.prepareStatement("select MAX(collection_id) from collection where username = ?");
            stment.setString(1, username);
            rset = stment.executeQuery();

            rset.next();
            max_id = rset.getInt("max");
        }
        catch(SQLException sqle){
            System.out.println("SQLException: " +sqle);
            return;
        }

        coll_id = max_id + 1;

        try{
            stment = conn.prepareStatement("insert into collection (username, collection_id, collection_name) values(?, ?, ?)");
            stment.setString(1, username);
            stment.setInt(2, coll_id);
            stment.setString(3, coll_name);

            stment.executeUpdate();
            System.out.println("Collection: " + coll_name + " has been created with id: " + coll_id + ".");
        }
        catch(SQLException sqle){
            System.out.println("Could not create collection: " +sqle);
        }

        try{ stment.close(); }
        catch(SQLException sqle){ System.out.println("SQLException: " +sqle); }
    }

    public static void change_collection_name(Connection conn, String username, int coll_id, String new_name){
        //TODO: implement the call in PostgresSSHTest
        PreparedStatement stment; ResultSet rset; boolean exists;

        try{
            stment = conn.prepareStatement("select collection_id from collection where username = ? and collection_id = ?");
            stment.setString(1, username);
            stment.setInt(2, coll_id);

            rset = stment.executeQuery();

            exists = rset.next();
        }
        catch(SQLException sqle){
            System.out.println("SQLException: " +sqle);
            return;
        }

        if(exists){
            try{
                stment = conn.prepareStatement("update collection set collection_name = ? where username = ? and collection_id = ?");
                stment.setString(1, new_name);
                stment.setString(2, username);
                stment.setInt(3, coll_id);

                stment.executeUpdate();
                System.out.println("The given collection has been updated with the name: " + new_name + ".");
            }
            catch (SQLException sqle){
                System.out.println("Could not modify the name of this collection: " +sqle);
            }
        }
        else{
            System.out.println("You have not created a collection with id " + coll_id + ".");
        }

        try{ stment.close(); }
        catch(SQLException sqle){ System.out.println("SQLException: " +sqle); }
    }

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
    private static int getSongInfo(Connection conn, int collection_id){
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
    private static String getCount(Connection conn, int collection_id, int songs){
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
                pStmt = conn.prepareStatement("select album_id, song_id from album_songs " +
                        "where album_id = ?");
                pStmt.setInt(1,ID);
                rSet = pStmt.executeQuery();
                while(rSet.next()){
                    pStmt = conn.prepareStatement("insert into collection_songs (username, collection_id, " +
                            "song_id) values(?, ?, ?)");
                    pStmt.setString(1,username);
                    pStmt.setInt(2,collection_id);
                    pStmt.setInt(3,rSet.getInt("song_id"));
                    pStmt.executeUpdate();
                }
            } catch (SQLException sqle) {
                System.out.println("SQLException: " + sqle);
            }
        }
        else{    // only need to add one song to the collection (ID is song_id)
            try{
                pStmt = conn.prepareStatement("insert into collection_songs (username, collection_id, song_id) " +
                        "values(?, ?, ?)");
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
                pStmt = conn.prepareStatement("select album_id, song_id from album_songs " +
                        "where album_id = ?");
                pStmt.setInt(1,ID);
                rSet = pStmt.executeQuery();
                while(rSet.next()){
                    pStmt = conn.prepareStatement("delete from collection_songs " +
                            "where username = ? and collection_id = ? and song_id = ?");
                    pStmt.setString(1,username);
                    pStmt.setInt(2,collection_id);
                    pStmt.setInt(3,rSet.getInt("song_id"));
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

    public static void delete_collection(Connection conn, String username, int collectionID) {
        PreparedStatement pStmt;
        try{
            pStmt = conn.prepareStatement("delete from collection " +
                    "where username = ? and collection_id = ?");
            pStmt.setString(1, username);
            pStmt.setInt(2, collectionID);
            pStmt.executeUpdate();

        } catch (SQLException sqle) {
            System.out.println("SQLException: " + sqle);
        }
    }


    /**
     * Plays an entire collection of songs
     * @param conn a connection to the database storing all necessary data
     * @param username the username of the current user
     * @param collectionID the collection that the user wants to play
     */
    public static void play_collection(Connection conn, String username, int collectionID) {
        PreparedStatement pStmt; ResultSet rSet;
        try {
            // First get all the songs that are in the collection specified
            pStmt = conn.prepareStatement(
                    "select c_s.song_id from collection as c, collection_songs as c_s where c.username = c_s.username and c.collection_id = c_s.collection_id and c.username = ? and c.collection_id = ?");
            pStmt.setString(1, username);
            pStmt.setInt(2, collectionID);

            rSet = pStmt.executeQuery();

            // If there are no rows in the set, do not do anything and display an error message
            if (!rSet.next()) {
                System.out.println("The Collection provided either has no songs or is invalid.");
                System.out.println("Please retry with a valid Collection ID.");
                // Otherwise, get each songID in the collection and play it
            } else {
                int songID;
                do {
                    songID = rSet.getInt(1);
                    play_song(conn, username, songID);
                } while (rSet.next());
            }

        } catch (SQLException sqle) {
            System.out.println("SQLException: " + sqle);
        }
    }

    /**
     * Plays any specific song
     * @param conn a connection to the database storing all necessary data
     * @param username the username of the current user
     * @param songID the song that the user wants to play
     */
    public static void play_song(Connection conn, String username, int songID) {
        PreparedStatement pStmt; ResultSet rSet;
        try {
            // See what the current songCount is
            pStmt = conn.prepareStatement("select user_play_count from user_play where username = ? and song_id = ?");
            pStmt.setString(1, username);
            pStmt.setInt(2, songID);

            rSet = pStmt.executeQuery();
            // If there is no songCount the user has never listened to the song
            if (!rSet.next()) {
                // In this case, add need to add the new relation to the user_play table
                add_row(conn, username, songID);
                // Otherwise, just increment the playCount of the song for that user
            } else {
                update_play_count(conn, username, songID);
            }

        } catch (SQLException sqle) {
            System.out.println("SQLException: " + sqle);
        }
    }

    /**
     * Add a new relation between a user and a song if it is the first time they have played it
     * @param conn a connection to the database storing all necessary data
     * @param username the username of the current user
     * @param songID the song that the user wants to play
     */
    private static void add_row(Connection conn, String username, int songID) {
        PreparedStatement pStmt;
        try {
            pStmt = conn.prepareStatement("insert into user_play values(?, ?, ?)");
            pStmt.setString(1, username);
            pStmt.setInt(2, songID);
            // The user would have played the song to get here, so the initial play_count would be 1
            pStmt.setInt(3, 1);

            pStmt.executeUpdate();
            // If a user is logged in, the only possible error at this point is that the song does not exist
            //    as the username should be validated upon logging in
        } catch (SQLException sqle) {
            // TODO: remove first print statement when finished (just for testing)
            System.out.println("SQLException: " + sqle);
            System.out.println("Please enter a valid songID.");
        }
    }

    /**
     * Add a single play to the song played by the user
     * @param conn a connection to the database storing all necessary data
     * @param username the username of the current user
     * @param songID the song that the user wants to play
     */
    private static void update_play_count(Connection conn, String username, int songID) {
        PreparedStatement pStmt;
        try {
            pStmt = conn.prepareStatement("update user_play set user_play_count = user_play_count + 1 where username = ? and song_id = ?");
            pStmt.setString(1, username);
            pStmt.setInt(2, songID);

            pStmt.executeUpdate();
            // Should not be any errors at this step
        } catch (SQLException sqle) {
            System.out.println("SQLException: " + sqle);
        }
    }
}

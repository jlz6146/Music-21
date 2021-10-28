import java.sql.*;

public class AccountCommands {
    public static void play_collection(Connection conn, String username, int collectionID) {

    }

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

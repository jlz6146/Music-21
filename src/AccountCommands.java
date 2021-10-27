import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AccountCommands {
    public static void play_song(Connection conn, String title) throws SQLException {
        try {
            PreparedStatement pStmt = conn.prepareStatement("select album_id from album where album_name = ?");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Closing Database Connection");
                conn.close();
            }
        }
    }
}

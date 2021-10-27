import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AccountCommands {
    public static void play_song(Connection conn, String title) throws SQLException {
        PreparedStatement pStmt = conn.prepareStatement("select album_id from album where album_name = ?");
    }
}

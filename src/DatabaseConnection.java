import com.jcraft.jsch.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    public static String username;

    public static void runCommand(String user, String password, String command, String[] args) throws SQLException {
        int lport = 5432;
        String rhost = "starbug.cs.rit.edu";
        int rport = 5432;
        String databaseName = "p320_01"; //change to your database name

        String driverName = "org.postgresql.Driver";
        Connection conn = null;
        Session session = null;
        try {
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            session = jsch.getSession(user, rhost, 22);
            session.setPassword(password);
            session.setConfig(config);
            session.setConfig("PreferredAuthentications","publickey,keyboard-interactive,password");
            session.connect();
            int assigned_port = session.setPortForwardingL(lport, "localhost", rport);

            // Assigned port could be different from 5432 but rarely happens
            String url = "jdbc:postgresql://localhost:"+ assigned_port + "/" + databaseName;

            Properties props = new Properties();
            props.put("user", user);
            props.put("password", password);

            Class.forName(driverName);
            conn = DriverManager.getConnection(url, props);

            // Do something with the database....
            switch (command) {
                case "!login" -> username = Login.logMeIn(conn);
                case "!signup" -> username = Login.signMeUp(conn);
                case "!help" -> AccountCommands.help();
                case "!search" -> SearchBy2.Search(conn);
                case "!follow" -> AccountCommands.follow(conn, username, args[1]);
                case "!unfollow" -> AccountCommands.unfollow(conn, username, args[1]);
                case "!create_collection" -> AccountCommands.create_collection(conn, username, args[1]);
                case "!change_collection_name" -> AccountCommands.change_collection_name(conn, username, Integer.parseInt(args[1]), args[2]);
                case "!show_collections" -> AccountCommands.show_collections(conn, username);
                case "!add_songs" -> AccountCommands.add_songs(conn, username, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Boolean.parseBoolean(args[3]));
                case "!delete_collection" -> AccountCommands.delete_collection(conn, user, Integer.parseInt(args[1]));
                case "!delete_songs" -> AccountCommands.delete_songs(conn, username, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Boolean.parseBoolean(args[3]));
                case "!play_collection" -> AccountCommands.play_collection(conn, username, Integer.parseInt(args[1]));
                case "!play_song" -> AccountCommands.play_song(conn, username, Integer.parseInt(args[1]));
                case "!logout" -> System.out.println("Goodbye!");
                default -> System.out.println("Please enter a valid command!");
            }

        } catch (Exception e) {
            System.out.println("Please enter valid arguments!");
        } finally {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
}

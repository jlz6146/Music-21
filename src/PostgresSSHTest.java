import com.jcraft.jsch.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

public class PostgresSSHTest {

    public static void main(String[] args) throws SQLException {
        int lport = 5432;
        String rhost = "starbug.cs.rit.edu";
        int rport = 5432;
        String user = args[0]; //change to your username
        String password = args[1]; //change to your password
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
            System.out.println("Connected");
            int assigned_port = session.setPortForwardingL(lport, "localhost", rport);
            System.out.println("Port Forwarded");

            // Assigned port could be different from 5432 but rarely happens
            String url = "jdbc:postgresql://localhost:"+ assigned_port + "/" + databaseName;

            System.out.println("database Url: " + url);
            Properties props = new Properties();
            props.put("user", user);
            props.put("password", password);

            Class.forName(driverName);
            conn = DriverManager.getConnection(url, props);
            System.out.println("Database connection established");

            // Putting in all song info
            /*PreparedStatement pStmt = conn.prepareStatement("insert into song values(?,?,?,?,?,?)");
            Scanner scanner = new Scanner(new File("SampleData/artists-songs-albums-tags.csv"));
            int i = 1;
            while (scanner.hasNextLine()) {
                String[] row = scanner.nextLine().split(",");
                try {
                    pStmt.setInt(1, i);
                    pStmt.setString(2, row[1]);
                    pStmt.setString(3, row[3].substring(0, row[3].length() - 1));
                    pStmt.setString(4, row[0].toLowerCase());
                    pStmt.setTime(5, songLength());
                    pStmt.setDate(6, releaseDate());

                    pStmt.executeUpdate();
                    i++;
                } catch (SQLException sqle) {
                    System.out.println("Couldn't add tuple: " + sqle);
                }
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Closing Database Connection");
                conn.close();
            }
            if (session != null && session.isConnected()) {
                System.out.println("Closing SSH Connection");
                session.disconnect();
            }
        }
    }

    private static int randomInRange(int min, int max) {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }

    private static Time songLength() {
        int mins = randomInRange(1, 4);
        int secs = randomInRange(0, 59);
        String timeString = String.format("00:0%d:%02d", mins, secs);
        return Time.valueOf(timeString);
    }

    private static Date releaseDate() {
        int month = randomInRange(1, 12);
        int day = 0;
        switch (month) {
            case 1, 3, 5, 8, 10, 12 -> day = randomInRange(1, 31);
            case 2 -> day = randomInRange(1, 28);
            case 4, 6, 7, 9, 11 -> day = randomInRange(1, 30);
        }
        int year = randomInRange(1980, 2020);
        String dateString = String.format("%d-%02d-%02d", year, month, day);
        return Date.valueOf(dateString);
    }
}

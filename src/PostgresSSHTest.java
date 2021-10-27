import com.jcraft.jsch.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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



            // Filling in album genre table
            /*Scanner scanner = new Scanner(new File("SampleData/artists-songs-albums-tags.csv"));
            PreparedStatement pStmt = conn.prepareStatement("select album_id from album where album_name = ?");
            PreparedStatement pStmt2 = conn.prepareStatement("insert into album_genre values(?, ?)");
            while (scanner.hasNextLine()) {
                String[] row = scanner.nextLine().split(",");
                pStmt.setString(1, row[2]);
                ResultSet rSet = pStmt.executeQuery();
                rSet.next();
                int albumID = rSet.getInt(1);
                try {
                    pStmt2.setInt(1, albumID);
                    pStmt2.setString(2, row[3].substring(0, row[3].length() - 1));
                    pStmt2.executeUpdate();
                } catch (SQLException sqle) {
                    System.out.println("Couldn't add tuple: " + sqle);
                }
            }*/

            // Putting in album info
            /*Scanner scanner = new Scanner(new File("SampleData/artists-songs-albums-tags.csv"));
            HashMap<String, Date> albums = new HashMap<>();
            PreparedStatement pStmt = conn.prepareStatement("select release_date from song where title = ?");

            while (scanner.hasNextLine()) {
                String[] row = scanner.nextLine().split(",");
                pStmt.setString(1, row[0].toLowerCase());
                ResultSet rSet = pStmt.executeQuery();
                rSet.next();
                Date newDate = rSet.getDate(1);
                if (albums.containsKey(row[2])) {
                    if (albums.get(row[2]).compareTo(newDate) < 0) {
                        albums.put(row[2], newDate);
                    }
                } else {
                    albums.put(row[2], newDate);
                }
            }

            Object[][] albumData = new Object[albums.size()][3];

            int i = 0;
            for (String s : albums.keySet()) {
                albumData[i][1] = s;
                albumData[i][2] = randomDateBetween(albums.get(s));
                i++;
            }

            HashMap<String, HashSet<String>> artists = new HashMap<>();

            Scanner scan2 = new Scanner(new File("SampleData/artists-songs-albums-tags.csv"));
            while (scan2.hasNextLine()) {
                String[] row = scan2.nextLine().split(",");
                if (!artists.containsKey(row[2])) {
                    artists.put(row[2], new HashSet<>());
                }
                artists.get(row[2]).add(row[1]);
            }

            i = 0;
            for (String s : artists.keySet()) {
                String[] albumArtists = artists.get(s).toArray(new String[0]);
                String artist = "";
                for (int j = 0; j < albumArtists.length; j++) {
                    if (j != albumArtists.length - 1) {
                        artist += albumArtists[j] + ", ";
                    } else {
                        artist += albumArtists[j];
                    }
                }
                albumData[i][0] = artist;
                i++;
            }

            PreparedStatement pStmt2 = conn.prepareStatement("insert into album values(?, ?, ?, ?)");
            for (int j = 0; j < albumData.length; j++) {
                try {
                    pStmt2.setInt(1, j + 1);
                    pStmt2.setString(2, (String) albumData[j][0]);
                    pStmt2.setString(3, (String) albumData[j][1]);
                    pStmt2.setDate(4, (Date) albumData[j][2]);
                    pStmt2.executeUpdate();
                } catch (SQLException sqle) {
                    System.out.println("Couldn't add tuple: " + sqle);
                }
            }*/

            // Putting in all song info
            /* PreparedStatement pStmt = conn.prepareStatement("insert into song values(?,?,?,?,?,?)");
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

    private static Date randomDateBetween(Date startDate) {
        long startMillis = startDate.getTime();
        Date endDate = Date.valueOf("2021-01-01");
        long endMillis = endDate.getTime();
        long randomMillis = ThreadLocalRandom.current().nextLong(startMillis, endMillis);

        return new Date(randomMillis);
    }
}

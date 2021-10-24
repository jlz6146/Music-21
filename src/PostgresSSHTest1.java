import com.jcraft.jsch.*;

import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class PostgresSSHTest {

    public static void main(String[] args) throws SQLException {

        int lport = 5432;
        String rhost = "starbug.cs.rit.edu";
        int rport = 5432;

        //Query checking for user in database from user info
        Scanner Scan = new Scanner(System.in);

        String databaseName = "p320_01"; //change to your database name

        String driverName = "org.postgresql.Driver";
        Connection conn = null;
        Session session = null;

        while (true) {
            System.out.println("Enter Username:");
            String user = Scan.nextLine();
            System.out.println("Enter Password:");
            String password = Scan.nextLine();
            //String logInQuery = "select userName from User" + " where userName == " + userName;


            //String user = "user"; //change to your username
            //String password = "pass"; //change to your password

            try {
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                JSch jsch = new JSch();
                session = jsch.getSession(user, rhost, 22);
                session.setPassword(password);
                session.setConfig(config);
                session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
                session.connect();
                System.out.println("Connected");
                int assigned_port = session.setPortForwardingL(lport, "localhost", rport);
                System.out.println("Port Forwarded");

                // Assigned port could be different from 5432 but rarely happens
                String url = "jdbc:postgresql://localhost:" + assigned_port + "/" + databaseName;

                System.out.println("database Url: " + url);
                Properties props = new Properties();
                props.put("user", user);
                props.put("password", password);

                Class.forName(driverName);
                conn = DriverManager.getConnection(url, props);
                System.out.println("Database connection established");
                break;

                /**String query = "select \"ArtistName\" from \"Songs\"";
                 try (Statement stmt = conn.createStatement()) {
                 ResultSet rs = stmt.executeQuery(query);
                 while (rs.next()) {
                 System.out.println(rs.getString("ArtistName"));
                 }
                 }*/

            } catch (JSchException whoops) {
                System.out.println(whoops.getMessage());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

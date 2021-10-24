import com.jcraft.jsch.*;

import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class PostgresSSHTest1 {

    public static void main(String[] args) throws SQLException {

        int lport = 5432;
        String rhost = "starbug.cs.rit.edu";
        int rport = 5432;

        //Query checking for user in database from user info

        String databaseName = "p320_01"; //change to your database name

        String driverName = "org.postgresql.Driver";
        Connection conn = null;
        Session session = null;

        String adminUser = "jlz6146";
        String adminPass = "Rochester404@";

        //String logInQuery = "select userName from User" + " where userName == " + userName;
        //String user = "user"; //change to your username
        //String password = "pass"; //change to your password

        try {
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            session = jsch.getSession(adminUser, rhost, 22);
            session.setPassword(adminPass);
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
            props.put("user", adminUser);
            props.put("password", adminPass);

            Class.forName(driverName);
            conn = DriverManager.getConnection(url, props);
            System.out.println("Database connection established");


        } catch (JSchException whoops) {
            System.out.println(whoops.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
        }

        LogIn.logMeIn(conn);

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

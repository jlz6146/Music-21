import com.jcraft.jsch.*;

import java.io.Console;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

public class PostgresSSHTest {
    private static boolean valid = false;

    public static void main(String[] args) throws SQLException {
        //Console console = System.console();
        Scanner input = new Scanner(System.in);

        int lport = 5432;
        String rhost = "starbug.cs.rit.edu";
        int rport = 5432;

        String user; //change to your username
        String password; //change to your password
        String databaseName = "p320_01"; //change to your database name

        //If database input is incorrect, ask again
        while(!valid) {
            System.out.println("Enter your database username: ");
            user = input.nextLine();

            //Console.readPassword could replace this, must be run via command line
            System.out.println("Enter your database password: ");
            password = input.nextLine();

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

                // Database account valid
                valid = true;

                // Do something with the database....
                Main.main(args);

            } catch (Exception e) {
                System.out.println("The user or password input is incorrect.");
                //e.printStackTrace();
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
    }
}

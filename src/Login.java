import java.sql.*;
import java.util.Scanner;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

public class Login {

    public static Scanner Scan = new Scanner(System.in);

    /**
     * Allows a new user to create an account
     * @param conn a connection to the database storing all necessary data
     * @return the newly created username of the user accessing the database
     * @throws SQLException included in the off case there is a database access error
     */
    public static String signMeUp(Connection conn) throws SQLException {
        String userName = null, password = null, email = null, firstName = null, lastName = null;
        PreparedStatement pStmt = conn.prepareStatement("Select count(1) As number From users Where username = ?");

        while (true) {
            while (userName == null || userName.trim().equals("")) {
                System.out.println("Enter Username:");
                userName = Scan.nextLine();
            }
            pStmt.setString(1, userName);
            ResultSet rs = pStmt.executeQuery();
            if (rs.next()) {
                if (rs.getInt("number") != 0) {
                    System.out.println("Username already exists! Please try something else.");
                    userName = null;
                }
                else {
                    break;
                }
            }
        }

        while (password == null || password.trim().equals("")) {
            System.out.println("Enter Password:");
            password = Scan.nextLine();
        }
        while (email == null || email.trim().equals("")) {
            System.out.println("Enter Email");
            email = Scan.nextLine();
        }
        while (firstName == null || firstName.trim().equals("")) {
            System.out.println("First name");
            firstName = Scan.nextLine();
        }
        while (lastName == null || lastName.trim().equals("")) {
            System.out.println("Last name");
            lastName = Scan.nextLine();
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate now = LocalDate.now();

        pStmt = conn.prepareStatement("insert into users (username, email, password, first_name, last_name, create_date, last_access_date) values(?, ?, ?, ?, ?, ?, ?)");
        pStmt.setString(1, userName);
        pStmt.setString(2, email);
        pStmt.setString(3, password);
        pStmt.setString(4, firstName);
        pStmt.setString(5, lastName);
        pStmt.setDate(6, Date.valueOf(dtf.format(now)));
        pStmt.setDate(7, Date.valueOf(dtf.format(now)));

        pStmt.executeUpdate();

        System.out.println("Successfully Signed Up!");
        return userName;
    }

    /**
     * Allows an existing user to login, maintaining any account info previously saved in the database
     * @param conn a connection to the database storing all necessary data
     * @return the existing username of the user accessing the database
     * @throws SQLException included in the off case there is a database access error
     */
    public static String logMeIn(Connection conn) throws SQLException{
        String user;
        while (true) {
            System.out.println("Enter Username:");
            user = Scan.nextLine();
            System.out.println("Enter Password:");
            String password = Scan.nextLine();

            PreparedStatement pStmt = conn.prepareStatement("Select count(1) As number From users Where username = ? and password = ?");
            pStmt.setString(1, user);
            pStmt.setString(2, password);
            ResultSet rs = pStmt.executeQuery();
            if (rs.next()) {
                if (rs.getInt("number") != 0) {
                    System.out.println("Welcome " + user + "!");

                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate now = LocalDate.now();
                    pStmt = conn.prepareStatement("update users set last_access_date = ? where username = ?");
                    pStmt.setDate(1, Date.valueOf(dtf.format(now)));
                    pStmt.setString(2, user);
                    pStmt.executeUpdate();
                    break;
                } else {
                    System.out.println("Sorry, username or password was incorrect. Please try again.");
                }
            }
        }
        return user;
    }

}

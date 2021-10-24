import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.*;
import java.util.Scanner;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

public class LogIn {

    public static Scanner Scan = new Scanner(System.in);

    public static void signMeUp(Connection conn) throws SQLException {

        String userName = null;
        Statement stmt = conn.createStatement();

        while (true) {
            System.out.println("Enter Username:");
            userName = Scan.nextLine();
            String checkUserQuery = "Select count(1) As number From users Where username = '" + userName + "'";
            ResultSet rs = stmt.executeQuery(checkUserQuery);
            if (rs.next()) {
                if (rs.getInt("number") != 0) {
                    System.out.println("Username already exists! Please try something else.");
                }
                else{
                    break;
                }
            }
        }

        System.out.println("Enter Password:");
        String password = Scan.nextLine();
        System.out.println("Enter Email");
        String email = Scan.nextLine();
        System.out.println("First name");
        String firstName = Scan.nextLine();
        System.out.println("Last name");
        String lastName = Scan.nextLine();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate now = LocalDate.now();

        String insertUserTemplate = "Insert into users (username, email, password, first_name, last_name, create_date, last_access_date) values ('";
        String userInput = userName + "', '" + email + "', '" + password + "', '" + firstName + "', '" + lastName + "', '" + dtf.format(now) + "', '" + dtf.format(now) + "')";

        stmt.executeQuery(insertUserTemplate + userInput);

    }

    public static void logMeIn(Connection conn) throws SQLException{


        while (true) {
            System.out.println("Enter Username:");
            String user = Scan.nextLine();
            System.out.println("Enter Password:");
            String password = Scan.nextLine();

            String logInQuery = "Select count(1) As number From users Where username = '" + user + "'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(logInQuery);
            if (rs.next()) {
                if (rs.getInt("number") != 0) {
                    System.out.println("Welcome " + user + "!");
                    break;
                } else {
                    System.out.println("Sorry, username or password was incorrect. Please try again.");
                }
            }
        }
    }

}

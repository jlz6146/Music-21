import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.*;
import java.util.Scanner;

public class LogIn {

    public static void signMeUp(){

    }

    public static void logMeIn(Connection conn) throws SQLException{
        Scanner Scan = new Scanner(System.in);

        while (true) {
            System.out.println("Enter Username:");
            String user = Scan.nextLine();
            System.out.println("Enter Password:");
            String password = Scan.nextLine();

            String logInQuery = "select username, password from users where username == " + user + " where password == " + password;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(logInQuery);
            if (rs.next() != false){
                System.out.println("Welcome " + rs.getString("username") + "!");
                break;
            }
            else {
                System.out.println("Sorry, username or password was incorrect. Please try again.");
            }
        }
    }

}

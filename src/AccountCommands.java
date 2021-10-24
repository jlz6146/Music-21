import java.sql.*;

public class AccountCommands {


    public static void follow(Connection conn, String local_name, String other_email) throws SQLException {
        Statement stment = conn.createStatement();
        ResultSet rset = stment.executeQuery("" +
                "select email from User where email = 'other_email'");

        String check = rset.getString("email");
        if(check.equals(other_email)){
            String other_name = rset.getString("username");
            stment.executeUpdate("" +
                    "insert into follows values('" + local_name + "', '" + other_name + "')");
        }
        else{
            System.out.println("The given email is not connected to a user.");
        }


        stment.close();
    }
}

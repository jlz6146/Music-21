import java.sql.*;

public class AccountCommands {

    /**
     * Displays to the user what commands do what
     */
    public static void help(){
        System.out.println("" +
                "!help ..." +
                "!follow [email] ..." +
                "!unfollow [email] ... " +
                "!create_collection [name] ..." +
                "!add_to_collection [name] [song/album] ..." +
                "!delete_collection [name] ..." +
                "!change_collection_name [old_name] [new_name] ..." +
                "" +
                "" +
                "" +
                "" );

    }


    /**
     * Handles the follow command
     * @param conn - the database connection
     * @param local_name - the name of the follower; person inputting command
     * @param other_email - the email of the person to be followed
     * @throws SQLException
     */
    public static void follow(Connection conn, String local_name, String other_email) throws SQLException {
        //TODO: implement the call in PostgresSSHTest
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

    public static void unfollow(Connection conn, String local_name, String other_email){

    }
}

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
     * Handles the follow command on the database
     * @param conn - the database connection
     * @param local_email - the email of the follower; person inputting command
     * @param other_email - the email of the person to be followed
     */
    public static void follow(Connection conn, String local_email, String other_email){
        //TODO: implement the call in PostgresSSHTest & test if works
        ResultSet rset; Statement stment; String check;

        try {
            stment = conn.createStatement();

            //Query if a user holds the given email
            rset = stment.executeQuery("" +
                    "select email from users where email = '" + other_email + "'");

            check = rset.getString("email");
        }
        catch(SQLException sqle){
            System.out.println("SQLException: " +sqle);
            return;
        }

        //Check if user exists under given email
        if(check != null){
            try {
                stment.executeUpdate("" +
                        "insert into follows (follower, following) " +
                        "values('" + local_email + "', '" + other_email + "')");
            }
            catch(SQLException sqle){
                System.out.println("Could not insert tuple. " +sqle);
            }
        }
        else{
            System.out.println("The given email is not connected to a user.");
        }


        try{ stment.close(); }
        catch(SQLException sqle){ System.out.println("SQLException: " +sqle); }
    }

    /**
     * Handles the unfollow command on the database
     * @param conn - the database connection
     * @param local_email - the email of the follower
     * @param other_email - the email of the person to be unfollowed
     */
    public static void unfollow(Connection conn, String local_email, String other_email){
        //TODO: implement the call in PostgresSSHTest & test if works
        Statement stment; ResultSet rset; String check;

        try {
            stment = conn.createStatement();

            //Query the given follows tuple
            rset = stment.executeQuery("" +
                    "select following from follows where follower = '" + local_email + "' and " +
                    "following = '" + other_email + "'");

            check = rset.getString("following");
        }
        catch(SQLException sqle){
            System.out.println("SQLException: " +sqle);
            return;
        }

        //Check if the follows tuple exists
        if(check.equals(other_email)){
            try {
                stment.executeUpdate("" +
                        "delete from follows where follower = '" + local_email + "' and " +
                        "following = '" + other_email + "'");
            }
            catch(SQLException sqle){
                System.out.println("Could not delete tuple: " +sqle);
            }
        }
        else{
            System.out.println("You are currently not following " + check + ".");
        }

        try{ stment.close(); }
        catch(SQLException sqle){ System.out.println("SQLException: " +sqle); }
    }

    public static void create_collection(Connection conn, String username, String coll_name){
        //TODO: implement the call in PostgresSSHTest & test if works
        Statement stment; ResultSet rset; int col_id;

        try{
            stment = conn.createStatement();

            rset = stment.executeQuery("" +
                    "select MAX(collection_id) from collection");
            if(rset == null){ col_id = 1; }
            else{ col_id = Integer.parseInt(rset.getString("collection_id")) + 1; }
            stment.executeUpdate("" +
                    "insert into collection (username, collection_id, collection_name) values('" + username + "', '" + col_id + "'. '" + coll_name + "')");

        }
        catch(SQLException sqle){
            System.out.println("Could not create collection: " +sqle);
        }
    }
}

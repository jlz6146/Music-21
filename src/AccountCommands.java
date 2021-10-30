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
                "!change_collection_name [id] [old_name] [new_name] ..." +
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
        //TODO: implement the call in PostgresSSHTest
        ResultSet rset; PreparedStatement stment; boolean exists;

        try {
            stment = conn.prepareStatement("select email from users where email = ?");
            stment.setString(1, other_email);

            //Query if a user holds the given email
            rset = stment.executeQuery();

            exists = rset.next();
        }
        catch(SQLException sqle){
            System.out.println("SQLException: " +sqle);
            return;
        }

        //Check if user exists under given email
        if(exists){
            try {
                stment = conn.prepareStatement("insert into follows (follower, following) values(?, ?)");
                stment.setString(1, local_email);
                stment.setString(2, other_email);

                stment.executeUpdate();
                System.out.println("You are now following: " + other_email + ".");
            }
            catch(SQLException sqle){
                System.out.println("Could not insert tuple. " +sqle);
            }
        }
        else{
            System.out.println(other_email + " is not connected to a user.");
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
        //TODO: implement the call in PostgresSSHTest
        PreparedStatement stment; ResultSet rset; boolean exists;

        try {
            stment = conn.prepareStatement("select following from follows where follower = ? and following = ?");
            stment.setString(1, local_email);
            stment.setString(2, other_email);

            //Query the given follows tuple
            rset = stment.executeQuery();

            exists = rset.next();
        }
        catch(SQLException sqle){
            System.out.println("SQLException: " +sqle);
            return;
        }

        //Check if the follows tuple exists
        if(exists){
            try {
                stment = conn.prepareStatement("delete from follows where follower = ? and following = ?");
                stment.setString(1, local_email);
                stment.setString(2, other_email);

                stment.executeUpdate();
                System.out.println("You unfollowed: " + other_email + ".");
            }
            catch(SQLException sqle){
                System.out.println("Could not delete tuple: " +sqle);
            }
        }
        else{
            System.out.println("You are currently not following " + other_email + ".");
        }

        try{ stment.close(); }
        catch(SQLException sqle){ System.out.println("SQLException: " +sqle); }
    }

    public static void create_collection(Connection conn, String username, String coll_name){
        //TODO: implement the call in PostgresSSHTest
        PreparedStatement stment; ResultSet rset; int coll_id; int max_id;

        try {
            stment = conn.prepareStatement("select MAX(collection_id) from collection where username = ?");
            stment.setString(1, username);
            rset = stment.executeQuery();

            rset.next();
            max_id = rset.getInt("max");
        }
        catch(SQLException sqle){
            System.out.println("SQLException: " +sqle);
            return;
        }

        coll_id = max_id + 1;

        try{
            stment = conn.prepareStatement("insert into collection (username, collection_id, collection_name) values(?, ?, ?)");
            stment.setString(1, username);
            stment.setInt(2, coll_id);
            stment.setString(3, coll_name);

            stment.executeUpdate();
            System.out.println("Collection: " + coll_name + " has been created with id: " + coll_id + ".");
        }
        catch(SQLException sqle){
            System.out.println("Could not create collection: " +sqle);
        }

        try{ stment.close(); }
        catch(SQLException sqle){ System.out.println("SQLException: " +sqle); }
    }

    public static void change_collection_name(Connection conn, String username, int coll_id, String new_name){
        //TODO: implement the call in PostgresSSHTest
        PreparedStatement stment; ResultSet rset; boolean exists;

        try{
            stment = conn.prepareStatement("select collection_id from collection where username = ? and collection_id = ?");
            stment.setString(1, username);
            stment.setInt(2, coll_id);

            rset = stment.executeQuery();

            exists = rset.next();
        }
        catch(SQLException sqle){
            System.out.println("SQLException: " +sqle);
            return;
        }

        if(exists){
            try{
                stment = conn.prepareStatement("update collection set collection_name = ? where username = ? and collection_id = ?");
                stment.setString(1, new_name);
                stment.setString(2, username);
                stment.setInt(3, coll_id);

                stment.executeUpdate();
                System.out.println("The given collection has been updated with the name: " + new_name + ".");
            }
            catch (SQLException sqle){
                System.out.println("Could not modify the name of this collection: " +sqle);
            }
        }
        else{
            System.out.println("You have not created a collection with id " + coll_id + ".");
        }

        try{ stment.close(); }
        catch(SQLException sqle){ System.out.println("SQLException: " +sqle); }
    }
}

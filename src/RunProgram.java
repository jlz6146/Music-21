import java.sql.SQLException;
import java.util.Scanner;

public class RunProgram {

    public static void main(String[] args) throws SQLException {
        // When running input your RIT Username and Password in as arguments
        String user = args[0];
        String pass = args[1];

        Scanner scanner = new Scanner(System.in);
        String command = "";
        String[] arguments;

        while (!(command.equals("!signup") || command.equals("!login"))) {
            System.out.println("Would you like to login or signup? Enter either \"!login\" or \"!signup\"");
            command = scanner.nextLine();
        }
        arguments = command.split(" ");
        DatabaseConnection.runCommand(user, pass, arguments[0], arguments);

        while (!command.trim().equals("!logout")) {
            System.out.println("What would you like to do? (Enter \"!help\" for list of commands)");
            command = scanner.nextLine();
            arguments = command.split(" ");
            DatabaseConnection.runCommand(user, pass, arguments[0], arguments);
        }
    }
}

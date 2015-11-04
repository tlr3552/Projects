package main;

import java.io.File;
import javafx.application.Application;
import persistence.PortfolioWriter;
import persistence.UserWriter;
import views.*;

/**
 * The Main Driver for the Porfolio System
 * 
 * @author Bill Dybas
 * @author Drew Heintz
 *
 */
public class Main {

    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("-delete")) {
                if (args.length < 2) {
                    System.err.println(
                            "When specifying '-delete' you need to provide a user-id to delete");
                } else {
                    String username = args[1];
                    File userFile = UserWriter.getUserFile(username);
                    if (userFile.exists()) {
                        UserWriter.getUserFile(username).delete();
                        PortfolioWriter.getPortfolioFile(username).delete();
                        System.out.println("User '" + username + "' deleted");
                    } else {
                        System.err
                                .println("User '" + username + "' does not exist");
                    }
                }
            }
        }
        Application.launch(MainApp.class, args);
    }
}

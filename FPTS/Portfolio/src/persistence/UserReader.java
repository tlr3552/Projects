package persistence;

import models.Portfolio;
import models.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.jasypt.util.password.BasicPasswordEncryptor;

/**
 * Imports a User from a properly formatted JSON text file.
 * 
 * @author Bill Dybas
 * @author Drew Heintz
 *
 */
public class UserReader {

    public UserReader() {}

    public boolean check(String username, String password)
            throws FileNotFoundException {
    	
        boolean result;
        JsonReader jr = null;

        File userFile = UserWriter.getUserFile(username);
        jr = Json.createReader(new BufferedReader(new FileReader(userFile)));

        JsonObject jo = jr.readObject();
        String u = jo.getString("username");
        String p = jo.getString("password");

        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        if (username.equals(u)
                && passwordEncryptor.checkPassword(password, p)) {
            result = true;
        } else {
            result = false;
        }

        if (jr != null) {
            jr.close();
        }

        return result;
    }

    /**
     * Read in a User's information given their username
     * 
     * @param username
     *            User
     * 
     * @return a fully-formed User
     * 
     * @throws FileNotFoundException
     */
    public User read(String username) throws FileNotFoundException {
        JsonReader jr = null;

        File userFile = UserWriter.getUserFile(username);
        jr = Json.createReader(new BufferedReader(new FileReader(userFile)));

        JsonObject jo = jr.readObject();
        String id = jo.getString("username");
        String password = jo.getString("password");

        PortfolioReader pr = new PortfolioReader();
        Portfolio portfolio;
        
        // if they don't have a portfolio then don't sweat it, just make an
        // empty one
        try {
            portfolio = pr.read(username);
        } catch (FileNotFoundException e) {
            System.err.println(e.toString());
            portfolio = new Portfolio();
        }

        User u = new User(id, password, portfolio);

        if (jr != null) {
            jr.close();
        }

        return u;
    }

    /**
     * Checks whether a file for the given username exists
     * 
     * @param username
     *            the username to check
     * 
     * @return True if the file exists
     */
    public boolean checkDuplicate(String username) {
        File f = UserWriter.getUserFile(username);
        if (f.exists() && !f.isDirectory()) {
            return true;
        } else {
            return false;
        }
    }
}

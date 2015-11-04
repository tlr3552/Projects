package persistence;

import models.User;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;

/**
 * 
 * Exports a User to a JSON text file.
 * 
 * @author Bill Dybas
 * @author Drew Heintz
 *
 */
public class UserWriter {
    
    public static final File USER_DIRECTORY = new File("User");
	
	public UserWriter(){}
	
	/**
	 * Writes out a User
	 * 
	 * @param u		User
	 */
	public void write(User u){
		JsonWriter jw = null;
		
		try {
		    jw = openUserFileWrite(u.getUserName());
			JsonObject job = buildUser(u);
			jw.writeObject(job);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(jw != null){
				jw.close();
			}
		}
	}
	
	/**
	 * Builds a JsonObject representing a User
	 * 
	 * @param u		User
	 * 
	 * @return		JsonObject representing a User
	 */
	public JsonObject buildUser(User u){		
		JsonObjectBuilder user = Json.createObjectBuilder();
		user.add("username", u.getUserName());
		user.add("password", u.getPassword());
		
		return user.build();
	}
	
	/**
	 * Creates a new User file based on the credentials supplied.
	 * 
	 * @param username		the User's username
	 * @param password		the User's password
	 */
	public User createNewUser(String username, String password) throws IOException {
		JsonWriter jw = null;
		
		try {
			jw = openUserFileWrite(username);
			// create a new user with an invalid password
			User user = new User(username, "!");
			// now change their password to the desired password
			user.changePassword(password);
			// build the basic user
			JsonObject job = buildUser(user);
			// and save it
			jw.writeObject(job);
			
			return user;
		} finally {
			if(jw != null){
				jw.close();
			}
		}
	}
	
	/**
	 * Open the user's file for writing. Ensures the directory exists before
	 * trying to open the file.
	 * 
	 * @param username - the username for the user to open
	 * @return a JsonWriter suitable for writing the user's data
	 * @throws IOException
	 */
	private JsonWriter openUserFileWrite(String username) throws IOException {
	    File userFile = getUserFile(username);
        return Json.createWriter(
                new BufferedWriter(new FileWriter(userFile)));
	}
	
	
	public static File getUserFile(String username) {
	    // Create the directory if it does not exist
        if(!USER_DIRECTORY.isDirectory()) {
            USER_DIRECTORY.mkdirs();
        }
        
	    return new File(USER_DIRECTORY, username + ".json");
	}
}

package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import models.User;
import persistence.UserReader;
import persistence.UserWriter;

/*
 * Authenticator checks a username and password
 * to what has been stored by the system and 
 * logs a User in if the credentials are correct.
 * 
 * @author Bill Dybas
 * @author Tyler Russell
 */
public class Authenticator {
	private User user;
	
	public Authenticator(){
		this.user = null;
	}
	
	/**
	 * Login a User
	 * 
	 * @param username		their username
	 * @param password		their password
	 * 
	 * @return				True if successfully logged in
	 */
	public boolean login(String username, String password){
		UserReader ur = new UserReader();
		
		try {
			if(ur.check(username, password)){
				user = ur.read(username);
				return true;
			}
			else{
				return false;
			}
		} catch (FileNotFoundException e) {
		    System.err.println(e.toString());
			user = null;
			return false;
		}
	}
	
	public boolean register(String username, String password) {
		UserReader ur = new UserReader();
		
		if(!(ur.checkDuplicate(username))){
			UserWriter uw = new UserWriter();
			try {
			    this.user = uw.createNewUser(username, password);
			} catch (IOException e) {
			    e.printStackTrace();
			    return false;
			}
			
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * Returns a User if they have successfully been logged in
	 * 
	 * @return		User (if logged in) or null
	 */
	public User getUser(){
		return this.user;
	}
	

	/**
	 * Log out the currently logged in user. Does nothing if nobody is logged
	 * in.
	 */
	public void logout() {
	    user = null;
	}
}
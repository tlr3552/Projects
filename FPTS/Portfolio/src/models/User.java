package models;

import org.jasypt.util.password.BasicPasswordEncryptor;

/**
 * The Representation of a User of the FPTS
 * 
 * @author Bill Dybas
 * @author Drew Heintz
 *
 */
public class User {
	private String username;
	private String password; // The password in encrypted form
	private Portfolio userPortfolio;
	
	/**
	 * Create a User with a default Portfolio.
	 * 
	 * @param username - the user's name
	 * @param encryptedPassword - the encrypted password
	 */
	public User(String username, String encryptedPassword){
		this(username, encryptedPassword, new Portfolio());
	}
	
	/**
	 * Create a User.
	 * 
	 * @param username - the user name
	 * @param encryptedPassword - the encrypted password
	 * @param userPortfolio - the portfolio
	 */
	public User(String username, String encryptedPassword, Portfolio userPortfolio){
		this.username = username;
		this.password = encryptedPassword;
		this.userPortfolio = userPortfolio;
	}
	
	public Portfolio getPortfolio(){
		return this.userPortfolio;
	}
	
	public String getUserName(){
		return this.username;
	}
	
	public String getPassword(){
		return this.password;
	}
	
	/**
	 * Change the user's password.
	 *  
	 * @param plainPassword - the password in plain text
	 */
	public void changePassword(String plainPassword) {
	    BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        this.password = passwordEncryptor.encryptPassword(plainPassword);
	}
}

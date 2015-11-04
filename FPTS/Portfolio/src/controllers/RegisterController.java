package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import views.AppComponent;
import views.MainApp;
/**
 * Controller for Register GUI
 * 
 * @author Fawaz Alhenaki
 * @author Bill Dybas
 *
 */
public class RegisterController implements AppComponent {
    private MainApp application;
    
    @FXML
    TextField txtUsername;
    @FXML
    PasswordField passPassword;
    @FXML
    PasswordField confirmPassword;
    @FXML
    Button Register;
    @FXML
    Label lblMessage;
	
    @Override
    public void setup() {
        txtUsername.setPromptText("Username");
        passPassword.setPromptText("Password");
    }

    @Override
    public void setApp(MainApp application) {
        this.application = application;
    }
    
    public void processRegister(ActionEvent event) throws Exception {
    	lblMessage.setTextFill(Color.RED);
    	
    	// Usernames may only contain alphabet, dashes, underscores, also must have between 1 to 25 chars.
    	String username = getTxtUsername();
    	username = username.toLowerCase();
    	if(username.matches("^[a-z0-9_-]{1,25}$")){
        	//If user tries registering with empty credentials, nothing happens.
	    	if(passPassword.getText().equals("") || username.equals("")){
	    		return;
	    	}
	    	
	    	if (passPassword.getText().length() <= 5){
	            lblMessage.setText("Password must have six or more charcters");
	            return;
	    	}
	    	
	    	if(passPassword.getText().equals(confirmPassword.getText())){
	    		if(application.attemptRegister(username, passPassword.getText())) {
	    			application.replaceSceneContent("CreateOrImport.fxml");
	            } else {
	                lblMessage.setText("Username '" + username + "' already exists");
	            }
	    	}
	        else {
	            lblMessage.setText("Passwords don't match.");
	        }
	    }
    	else {
	    	lblMessage.setText("Usernames may only contain alphabet, dashes, underscores");
	    	return;
    	}
    }
    
    public void processBack(ActionEvent event) throws Exception {
        application.replaceSceneContent("Login.fxml");
    }
    
	private String getTxtUsername() {
		return txtUsername.getText();
	}
}
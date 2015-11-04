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
 * Controller for login GUI
 *
 * @author Fawaz Alhenaki
 *
 */
public class LoginController implements AppComponent {

    private MainApp application;

    @FXML
    TextField txtUsername;
    @FXML
    PasswordField passPassword;
    @FXML
    Button login;
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

    public void processLogin(ActionEvent event) throws Exception {
    	//If user tries login with empty credentials, nothing happens.
    	if(txtUsername.getText().equals("") || passPassword.getText().equals("")){
    		return;
    	}
        if(application.attemptLogin(txtUsername.getText().toLowerCase().replace(" ", ""), passPassword.getText())) {
            application.replaceSceneContent("Main.fxml");
        } else {
            lblMessage.setText("Invalid Credentials");
            lblMessage.setTextFill(Color.RED);
        }
    }

    public void processRegisterWindow(ActionEvent event) throws Exception {
        application.replaceSceneContent("Register.fxml");
    }
}

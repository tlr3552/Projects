package views;

import java.io.InputStream;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import main.Authenticator;
import models.User;
import persistence.EquitiesImporter;
import persistence.EquityUpdater;
import persistence.EquityUpdaterProxy;

/**
 * MainApp
 *
 * The Main JavaFX Driver for the GUI
 *
 * @author Drew Heintz
 * @author Fawaz Alhenaki
 * @author Bill Dybas
 *
 */
public class MainApp extends Application {

    private static final String TITLE = "Financial Portfolio Tracking System";

    private Stage stage;
	private Authenticator auth;
	private EquityUpdater updater;
	private Timer timer;
	private static final int timerInterval = 30000; // 30 seconds

    @Override
    public void init() throws Exception {
    	new EquitiesImporter();
    	auth = new Authenticator();
    	updater = new EquityUpdaterProxy();

    	
    	// Create a new Timer that tries to update
    	// every 30 seconds
    	timer = new Timer();
    	timer.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run() {
				updater.update();
			}
    	}, 0, timerInterval);
    	
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setTitle(TITLE);
        replaceSceneContent("Login.fxml");
        stage.show();

        // Adds a listener to the exit button
        // to act as a logout button before exiting
        // Also shuts down the timer before exiting
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                if(getUser() != null){
                	if(!logout()){
                		we.consume();
                	}
                	else{
                    	timer.cancel();
                        timer.purge();
                	}
                }
                else{
                	timer.cancel();
                    timer.purge();
                }
            }
        });
    }

    public AppComponent replaceSceneContent(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        InputStream in = MainApp.class.getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(MainApp.class.getResource(fxml));
        Pane page;
        try {
            page = (Pane) loader.load(in);
        } finally {
            in.close();
        }
        page.autosize();
        Scene scene = new Scene(page, page.getWidth(), page.getHeight());
        stage.setMinWidth(page.getMinWidth() >= 0 ? page.getMinWidth() : 0);
        stage.setMinHeight(page.getMinHeight() >= 0 ? page.getMinHeight() : 0);
        stage.setScene(scene);
        stage.sizeToScene();

        AppComponent component = (AppComponent) loader.getController();
        component.setApp(this);
        component.setup();
        return component;
    }

    public Stage getStage() {
        return stage;
    }


    /*
     * Portfolio Tracking System
     * Related Controller Methods
     *
     */

    public User getUser() {
        return auth.getUser();
    }

    public void updateEquities(){
    	this.updater.update();
    }

    public void setUpdateInterval(int seconds){
    	((EquityUpdaterProxy) this.updater).setUpdateInterval(seconds);
    }

    public boolean attemptLogin(String username, String password){
    	return auth.login(username, password);
    }

    public boolean attemptRegister(String username, String password){
    	return auth.register(username, password);
    }

    public boolean logout() {
    	//do you confirm?
    	//if yes = >> call quit without saving
    	//if no , stop
    	if(getUser().getPortfolio().hasPendingTransactions()){
	    	Alert alert = new Alert(AlertType.CONFIRMATION);
	    	alert.setTitle("Confirmation Dialog");
	    	alert.setHeaderText("Logout");
	    	alert.setContentText("Are you sure you want to logout without saving?");
	    	Optional<ButtonType> result = alert.showAndWait();

	    	// The User didn't click the OK Button
	    	if (!(result.isPresent() && result.get() == ButtonType.OK)){
	    		return false;
	    	}
	    	// They want to logout without saving
	    	else{
	    		getUser().getPortfolio().exitWithoutSaving();
	    	}
    	}

        try {
            auth.logout();
            replaceSceneContent("Login.fxml");
        } catch (Exception e) {
            // handle it the bad way!
            e.printStackTrace();
        }
        return true;
	}
}

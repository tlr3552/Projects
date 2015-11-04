package controllers;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import persistence.PortfolioWriter;
import views.AppComponent;
import views.MainApp;
/**
 * Controller for CreateOrImport GUI
 *
 * @author Fawaz Alhenaki
 *
 */
public class CreateOrImportController implements AppComponent {

    private MainApp application;

    @FXML
    Label lblMessage;

    @Override
    public void setup() {

    }

    @Override
    public void setApp(MainApp application) {
        this.application = application;
    }

    public void createPortfolio(ActionEvent event) throws Exception {
    	PortfolioWriter pw = new PortfolioWriter();
    	pw.write(application.getUser());
    	
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("Information Dialog");
    	alert.setHeaderText(null);
    	alert.setContentText("Registration Complete!");
    	alert.showAndWait();

    	application.replaceSceneContent("Login.fxml");	
    }

    /**
     *
     * @param event
     * @return The selected Portfolio file.
     * @throws Exception
     */
    public File importPortfolio(ActionEvent event) throws Exception {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter =  new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
		File selectedFile = fileChooser.showOpenDialog(null);

		if (selectedFile != null) {
            lblMessage.setText("File selected: " + selectedFile.getName());
            lblMessage.setTextFill(Color.RED);
            return selectedFile;
		}
		else {
            lblMessage.setText("File selection cancelled.");
            lblMessage.setTextFill(Color.RED);
            return null;
		}

    }
}
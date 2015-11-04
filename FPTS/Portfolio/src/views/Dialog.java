package views;

import java.io.IOException;
import java.io.InputStream;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * A JavaFX Dialog which automatically loads an FXML file.
 * 
 * @author Drew Heintz
 *
 */
public class Dialog extends Stage {
    
    public Dialog(Window owner, String fxml) throws IOException {
        // Initialize style
        initStyle(StageStyle.UTILITY);
        // When a window is modal you cannot use it's parent window. That's what we want.
        initModality(Modality.WINDOW_MODAL);
        initOwner(owner);
        
        // Load the requested FXML file
        loadScene(fxml);
    }
    
    private void loadScene(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        // We need the resource so we can load it
        InputStream in = MainApp.class.getResourceAsStream(fxml);
        // It's required
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        // No idea what this is for
        loader.setLocation(MainApp.class.getResource(fxml));
        // The Dialog subclass will be the controller.
        loader.setController(this);
        // Actually load the page. Region is a high-level parent class of most
        // of the JavaFX controls and layouts so we can cast almost anything to it.
        Region page;
        try {
            page = (Region) loader.load(in);
        } finally {
            in.close();
        }
        Scene scene = new Scene(page, page.getWidth(), page.getHeight());
        setScene(scene);
    }
}

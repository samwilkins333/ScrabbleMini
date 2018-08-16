package Scrabble;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;


/**
 * 
 * @author Samuel Wilkins, samwilkins333@gmail.com
 *
 */
public class App extends Application {

	/**
	 * Initializes the sole, non-resizable main screen of the app's interface
	 */
    @Override
    public void start(Stage stage) {
	    PaneOrganizer organizer = new PaneOrganizer(this, stage);
	    Scene scene = new Scene(organizer.getRoot(), Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT);
	    stage.setScene(scene);
        stage.setWidth(Constants.SCENE_WIDTH);
        stage.setResizable(false);
	    stage.setTitle("Scrabble Mini");
	    stage.show();
    }

    /**
     * Main line to initialize application
     * @param args initialization arguments
     */
    public static void main(String[] args) {
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Scrabble Mini");
        launch(args);
    }
    
}

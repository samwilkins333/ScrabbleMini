package Scrabble;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class App extends Application {

    @Override
    public void start(Stage stage) {
	    	PaneOrganizer organizer = new PaneOrganizer(this, stage);
	    	Scene scene = new Scene(organizer.getRoot(), Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT);
	    	stage.setScene(scene);
            stage.setWidth(Constants.SCENE_WIDTH);
            stage.setResizable(false);
	    	stage.setTitle("Mini Scrabble");
	    	stage.show();
    }

    public static void main(String[] argv) {
        launch(argv);
    }
    
}

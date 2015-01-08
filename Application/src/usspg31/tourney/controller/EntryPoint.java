package usspg31.tourney.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class EntryPoint extends Application {
	private static final Logger log = Logger.getLogger(EntryPoint.class.getName());

	public static void main(String[] args) throws ClassNotFoundException {
		log.info("Starting Application");
		log.info("Running JavaFX Version " + System.getProperty("javafx.runtime.version"));

		try {
			launch(args);
		} catch (Throwable t) {
			log.log(Level.SEVERE, t.getMessage(), t);
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			Pane root = MainWindow.getInstance();
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Tourney");

			primaryStage.minWidthProperty().bind(root.minWidthProperty());
			primaryStage.minHeightProperty().bind(root.minHeightProperty());

			primaryStage.show();
		} catch(Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			System.exit(1);
		}
	}
}

package usspg31.tourney.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class EntryPoint extends Application {
	private static final Logger log = Logger.getLogger(EntryPoint.class.getName());

	private static Stage primaryStage;

	public static void main(String[] args) {
		log.info("Starting Application");
		log.info("Running JavaFX Version " + System.getProperty("javafx.runtime.version"));

		try {
			launch(args);
		} catch (Throwable t) {
			log.log(Level.SEVERE, t.getMessage(), t);
		}
	}

	public static Stage getPrimaryStage() {
		return primaryStage;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		EntryPoint.primaryStage = primaryStage;
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

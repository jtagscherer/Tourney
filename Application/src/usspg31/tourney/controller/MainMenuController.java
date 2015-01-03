package usspg31.tourney.controller;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainMenuController {

	private static final Logger log = Logger.getLogger(MainMenuController.class.getName());

	private @FXML Button buttonNewEvent;
	private @FXML Button buttonOpenEvent;
	private @FXML Button buttonEditTournamentModules;
	private @FXML Button buttonOpenOptions;

	@FXML private void initialize() {
		this.buttonOpenOptions.setOnAction(event -> {
			log.finer("Options Button was clicked");
			MainWindow.getInstance().displayOptionsView();
		});
	}

}

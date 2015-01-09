package usspg31.tourney.controller;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import usspg31.tourney.model.Event;

public class MainMenuController {

	private static final Logger log = Logger.getLogger(MainMenuController.class.getName());

	@FXML private Button buttonNewEvent;
	@FXML private Button buttonOpenEvent;
	@FXML private Button buttonEditTournamentModules;
	@FXML private Button buttonOpenOptions;

	@FXML private VBox eventButtonsLeft;
	@FXML private VBox eventButtonsRight;
	@FXML private HBox eventButtonsContainer;

	@FXML private void initialize() {
		this.buttonNewEvent.setOnAction(event -> {
			log.finer("New Event Button was clicked");
			MainWindow.getInstance().getEventPhaseViewController().loadEvent(new Event());
			MainWindow.getInstance().slideUp(
					MainWindow.getInstance().getEventPhaseView());
		});
		this.buttonOpenEvent.setOnAction(event -> {
			log.finer("Open Event Button was clicked");
			MainWindow.getInstance().slideUp(
					MainWindow.getInstance().getEventPhaseView());
		});

		this.buttonOpenOptions.setOnAction(event -> {
			log.finer("Options Button was clicked");

			MainWindow.getInstance().getOptionsViewController()
			.setExitProperties("Hauptmenü", "Zurückkehren",
					"Kehren Sie zum Hauptmenü zurück", () -> {
						MainWindow.getInstance().slideDown(
								MainWindow.getInstance().getMainMenu());
					});

			MainWindow.getInstance().slideUp(
					MainWindow.getInstance().getOptionsView());
		});
	}

}

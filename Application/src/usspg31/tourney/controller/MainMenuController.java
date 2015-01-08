package usspg31.tourney.controller;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
		this.buttonOpenOptions.setOnAction(event -> {
			log.finer("Options Button was clicked");
			MainWindow.getInstance().displayOptionsView();
		});

		// make the two event-related buttons share the width of their parent
		this.eventButtonsLeft.prefWidthProperty().bind(
				this.eventButtonsContainer.widthProperty().divide(2));
		this.eventButtonsRight.prefWidthProperty().bind(
				this.eventButtonsContainer.widthProperty()
				.subtract(this.eventButtonsLeft.prefWidthProperty()));
	}

}

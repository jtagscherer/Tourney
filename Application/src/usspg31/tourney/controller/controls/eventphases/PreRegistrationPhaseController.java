package usspg31.tourney.controller.controls.eventphases;

import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import usspg31.tourney.controller.controls.EventUser;
import usspg31.tourney.model.Event;

public class PreRegistrationPhaseController implements EventUser {

	private static final Logger log = Logger.getLogger(PreRegistrationPhaseController.class.getName());

	@FXML private TextField textFieldPlayerSearch;
	@FXML private TableView<String> tablePreRegisteredPlayers;
	@FXML private Button buttonAddPlayer;
	@FXML private Button buttonRemovePlayer;
	@FXML private Button buttonEditPlayer;

	@Override
	public void loadEvent(Event event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unloadEvent() {
		// TODO Auto-generated method stub

	}

	@FXML private void onButtonAddPlayerClicked(ActionEvent event) {
		log.fine("Add Player Button clicked");
	}

	@FXML private void onButtonRemovePlayerClicked(ActionEvent event) {
		log.fine("Remove Player Button clicked");
	}

	@FXML private void onButtonEditPlayerClicked(ActionEvent event) {
		log.fine("Edit Player Button clicked");
	}
}

package usspg31.tourney.controller.controls.eventphases;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import usspg31.tourney.controller.controls.EventUser;
import usspg31.tourney.model.Event;

public class RegistrationPhaseController implements EventUser {

	@FXML private TextField textFieldPlayerSearch;
	@FXML private TableView<String> tableRegisteredPlayers;
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

	}

	@FXML private void onButtonRemovePlayerClicked(ActionEvent event) {

	}

	@FXML private void onButtonEditPlayerClicked(ActionEvent event) {

	}
}

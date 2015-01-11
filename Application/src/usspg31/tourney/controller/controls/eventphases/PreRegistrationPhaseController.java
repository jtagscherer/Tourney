package usspg31.tourney.controller.controls.eventphases;

import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import usspg31.tourney.controller.controls.EventUser;
import usspg31.tourney.controller.dialogs.DialogResult;
import usspg31.tourney.controller.dialogs.PlayerPreRegistrationDialogController;
import usspg31.tourney.model.Event;
import usspg31.tourney.model.Player;

public class PreRegistrationPhaseController implements EventUser {

	private static final Logger log = Logger.getLogger(PreRegistrationPhaseController.class.getName());

	@FXML private TextField textFieldPlayerSearch;
	@FXML private TableView<Player> tablePreRegisteredPlayers;
	@FXML private Button buttonAddPlayer;
	@FXML private Button buttonRemovePlayer;
	@FXML private Button buttonEditPlayer;

	private Event loadedEvent;

	@Override
	public void loadEvent(Event event) {
		log.info("Loading Event");
		if (this.loadedEvent != null) {
			this.unloadEvent();
		}

		this.loadedEvent = event;

		// TODO: add bindings to the model
	}

	@Override
	public void unloadEvent() {
		log.info("Unloading Event");
		if (this.loadedEvent == null) {
			log.warning("Trying to unload an event, even though no event was loaded");
			return;
		}

		// TODO: unregister all listeners we registered to anything in the event

		this.loadedEvent = null;
	}

	@FXML private void onButtonAddPlayerClicked(ActionEvent event) {
		log.fine("Add Player Button clicked");
		this.checkEventLoaded();
		new PlayerPreRegistrationDialogController().modalDialog()
		.properties(new Player())
		.properties(this.loadedEvent)
		.onResult((result, returnValue) -> {
			if (result == DialogResult.OK && returnValue != null) {
				this.loadedEvent.getRegisteredPlayers().add(returnValue);
			}
		}).show();
	}

	@FXML private void onButtonRemovePlayerClicked(ActionEvent event) {
		log.fine("Remove Player Button clicked");
		this.checkEventLoaded();
		// TODO: get selected player in the table and remove him
	}

	@FXML private void onButtonEditPlayerClicked(ActionEvent event) {
		log.fine("Edit Player Button clicked");
		this.checkEventLoaded();
		// TODO: get selected player in the table
		final Player selectedPlayer = null;
		new PlayerPreRegistrationDialogController().modalDialog()
		.properties(selectedPlayer)
		.properties(this.loadedEvent)
		.onResult((result, returnValue) -> {
			if (result == DialogResult.OK && returnValue != null) {
				this.loadedEvent.getRegisteredPlayers().remove(selectedPlayer);
				this.loadedEvent.getRegisteredPlayers().add(returnValue);
			}
		}).show();
	}

	private void checkEventLoaded() {
		if (this.loadedEvent == null) {
			throw new IllegalStateException("An Event must be loaded in order "
					+ "to perform actions on this controller");
		}
	}
}

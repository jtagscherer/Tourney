package usspg31.tourney.controller.controls.eventphases;

import java.util.logging.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import usspg31.tourney.controller.EntryPoint;
import usspg31.tourney.controller.controls.EventUser;
import usspg31.tourney.controller.dialogs.DialogResult;
import usspg31.tourney.controller.dialogs.PlayerPreRegistrationDialogController;
import usspg31.tourney.controller.util.SearchUtilities;
import usspg31.tourney.model.Event;
import usspg31.tourney.model.Player;

@SuppressWarnings("deprecation")
public class RegistrationPhaseController implements EventUser {

	private static final Logger log = Logger
			.getLogger(RegistrationPhaseController.class.getName());

	@FXML
	private TextField textFieldPlayerSearch;
	@FXML
	private TableView<Player> tableRegisteredPlayers;
	@FXML
	private Button buttonAddPlayer;
	@FXML
	private Button buttonRemovePlayer;
	@FXML
	private Button buttonEditPlayer;

	private TableColumn<Player, String> tableColumnPlayerFirstName;
	private TableColumn<Player, String> tableColumnPlayerLastName;
	private TableColumn<Player, String> tableColumnPlayerNickName;
	private TableColumn<Player, String> tableColumnPlayerMailAddress;
	private TableColumn<Player, Boolean> tableColumnPlayerPayed;
	private TableColumn<Player, String> tableColumnPlayerStartNumber;

	private Event loadedEvent;

	@Override
	public void loadEvent(Event event) {
		log.info("Loading Event");
		if (this.loadedEvent != null) {
			this.unloadEvent();
		}

		this.loadedEvent = event;

		this.initPlayerTable();

		// Add all registered players to the table view and enable searching
		FilteredList<Player> filteredPlayerList = new FilteredList<>(
				this.loadedEvent.getRegisteredPlayers(), p -> true);

		this.textFieldPlayerSearch.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					filteredPlayerList.setPredicate(player -> {
						if (newValue == null || newValue.isEmpty()) {
							return true;
						}

						return SearchUtilities.fuzzyMatches(
								player.getFirstName(), newValue)
								|| SearchUtilities.fuzzyMatches(
										player.getLastName(), newValue)
										|| SearchUtilities.fuzzyMatches(
												player.getNickName(), newValue)
												|| SearchUtilities.fuzzyMatches(
														player.getMailAddress(), newValue);
					});
				});

		SortedList<Player> sortedPlayerList = new SortedList<>(
				filteredPlayerList);
		sortedPlayerList.comparatorProperty().bind(
				this.tableRegisteredPlayers.comparatorProperty());

		this.tableRegisteredPlayers.setItems(sortedPlayerList);

		// Bind the button's availability to the list selection
		this.buttonRemovePlayer.disableProperty().bind(
				this.tableRegisteredPlayers.getSelectionModel()
				.selectedItemProperty().isNull());
		this.buttonEditPlayer.disableProperty().bind(
				this.tableRegisteredPlayers.getSelectionModel()
				.selectedItemProperty().isNull());
	}

	@Override
	public void unloadEvent() {
		log.info("Unloading Event");
		if (this.loadedEvent == null) {
			log.warning("Trying to unload an event, even though no event was loaded");
			return;
		}

		// TODO: unregister all listeners we registered to anything in the event
		this.tableRegisteredPlayers.getColumns().clear();

		this.loadedEvent = null;
	}

	private void initPlayerTable() {
		this.tableColumnPlayerFirstName = new TableColumn<>("Vorname");
		this.tableColumnPlayerFirstName
		.setCellValueFactory(cellData -> cellData.getValue()
				.firstNameProperty());
		this.tableColumnPlayerFirstName.setEditable(false);
		this.tableRegisteredPlayers.getColumns().add(
				this.tableColumnPlayerFirstName);

		this.tableColumnPlayerLastName = new TableColumn<>("Nachname");
		this.tableColumnPlayerLastName.setCellValueFactory(cellData -> cellData
				.getValue().lastNameProperty());
		this.tableColumnPlayerLastName.setEditable(false);
		this.tableRegisteredPlayers.getColumns().add(
				this.tableColumnPlayerLastName);

		this.tableColumnPlayerNickName = new TableColumn<>("Nickname");
		this.tableColumnPlayerNickName.setCellValueFactory(cellData -> cellData
				.getValue().nickNameProperty());
		this.tableColumnPlayerNickName.setEditable(false);
		this.tableRegisteredPlayers.getColumns().add(
				this.tableColumnPlayerNickName);

		this.tableColumnPlayerMailAddress = new TableColumn<>("E-Mail");
		this.tableColumnPlayerMailAddress
		.setCellValueFactory(cellData -> cellData.getValue()
				.mailAdressProperty());
		this.tableColumnPlayerMailAddress.setEditable(false);
		this.tableRegisteredPlayers.getColumns().add(
				this.tableColumnPlayerMailAddress);

		this.tableColumnPlayerPayed = new TableColumn<>("Bezahlt");
		this.tableColumnPlayerPayed
		.setCellValueFactory(new PropertyValueFactory<Player, Boolean>(
				"payed"));
		this.tableColumnPlayerPayed.setCellFactory(CheckBoxTableCell
				.forTableColumn(this.tableColumnPlayerPayed));
		this.tableColumnPlayerPayed.setEditable(true);
		this.tableRegisteredPlayers.getColumns().add(
				this.tableColumnPlayerPayed);

		this.tableColumnPlayerStartNumber = new TableColumn<Player, String>(
				"Startnummer");
		this.tableColumnPlayerStartNumber
		.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Player, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(
					TableColumn.CellDataFeatures<Player, String> cellData) {
				if (cellData.getValue() != null) {
					if (cellData.getValue().getStartingNumber()
							.equals("")) {
						return new SimpleStringProperty(
								"Nicht anwesend");
					} else {
						return new SimpleStringProperty(cellData
								.getValue().getStartingNumber());
					}
				} else {
					return new SimpleStringProperty("");
				}
			}
		});
		this.tableColumnPlayerStartNumber.setEditable(false);
		this.tableRegisteredPlayers.getColumns().add(
				this.tableColumnPlayerStartNumber);

		this.tableRegisteredPlayers.setEditable(true);
	}

	@FXML
	private void onButtonAddPlayerClicked(ActionEvent event) {
		log.fine("Add Player Button clicked");
		this.checkEventLoaded();
		new PlayerPreRegistrationDialogController()
		.modalDialog()
		.properties(new Player())
		.properties(this.loadedEvent)
		.onResult(
				(result, returnValue) -> {
					if (result == DialogResult.OK
							&& returnValue != null) {
						this.loadedEvent.getRegisteredPlayers().add(
								returnValue);
					}
				}).show();
	}

	@FXML
	private void onButtonRemovePlayerClicked(ActionEvent event) {
		log.fine("Remove Player Button clicked");
		this.checkEventLoaded();

		Player selectedPlayer = this.tableRegisteredPlayers.getSelectionModel()
				.getSelectedItem();
		if (selectedPlayer == null) {
			Dialogs.create()
			.owner(EntryPoint.getPrimaryStage())
			.title("Fehler")
			.message(
					"Bitte wählen Sie einen Spieler aus der Liste aus.")
					.showError();
		} else {
			Action response = Dialogs
					.create()
					.owner(EntryPoint.getPrimaryStage())
					.title("Spieler löschen")
					.message(
							"Wollen Sie den Spieler \""
									+ selectedPlayer.getFirstName() + " "
									+ selectedPlayer.getLastName()
									+ "\" wirklich löschen?").showConfirm();

			if (response == Dialog.ACTION_YES) {
				this.loadedEvent.getRegisteredPlayers().remove(selectedPlayer);
			}
		}
	}

	@FXML
	private void onButtonEditPlayerClicked(ActionEvent event) {
		log.fine("Edit Player Button clicked");
		this.checkEventLoaded();

		final Player selectedPlayer = this.tableRegisteredPlayers
				.getSelectionModel().getSelectedItem();
		if (selectedPlayer == null) {
			Dialogs.create()
			.owner(EntryPoint.getPrimaryStage())
			.title("Fehler")
			.message(
					"Bitte wählen Sie einen Spieler aus der Liste aus.")
					.showError();
		} else {
			new PlayerPreRegistrationDialogController()
			.modalDialog()
			.properties(selectedPlayer)
			.properties(this.loadedEvent)
			.onResult(
					(result, returnValue) -> {
						if (result == DialogResult.OK
								&& returnValue != null) {
							this.loadedEvent.getRegisteredPlayers()
							.remove(selectedPlayer);
							this.loadedEvent.getRegisteredPlayers()
							.add(returnValue);
						}
					}).show();
		}
	}

	private void checkEventLoaded() {
		if (this.loadedEvent == null) {
			throw new IllegalStateException("An Event must be loaded in order "
					+ "to perform actions on this controller");
		}
	}
}

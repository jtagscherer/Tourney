package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import usspg31.tourney.controller.EntryPoint;
import usspg31.tourney.model.Event;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.Tournament;

@SuppressWarnings("deprecation")
public class PlayerPreRegistrationDialogController extends VBox implements
		IModalDialogProvider<Object, Player> {

	private static final Logger log = Logger
			.getLogger(PlayerPreRegistrationDialogController.class.getName());

	@FXML private TextField textFieldFirstName;
	@FXML private TextField textFieldLastName;
	@FXML private TextField textFieldEmail;
	@FXML private TextField textFieldNickname;
	@FXML private TableView<Tournament> tableTournaments;
	@FXML private Button buttonAddTournament;
	@FXML private Button buttonRemoveTournament;
	@FXML private CheckBox checkBoxPayed;

	private TableColumn<Tournament, String> tableColumnTournamentName;
	private ObservableList<Tournament> registeredTournaments;

	private Player loadedPlayer;
	private Player editedPlayer;
	private Event loadedEvent;

	public PlayerPreRegistrationDialogController() {
		try {
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
					"/ui/fxml/dialogs/player-pre-registration-dialog.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@FXML
	private void initialize() {
		this.registeredTournaments = FXCollections.observableArrayList();
		this.initTournamentTable();

		// Bind the remove button's availability to the selected item
		this.buttonRemoveTournament.disableProperty().bind(
				this.tableTournaments.getSelectionModel()
						.selectedItemProperty().isNull());
	}

	private void initTournamentTable() {
		this.tableColumnTournamentName = new TableColumn<>("Turniername");
		this.tableColumnTournamentName.setCellValueFactory(cellData -> cellData
				.getValue().nameProperty());
		this.tableTournaments.getColumns().add(this.tableColumnTournamentName);
	}

	private void updateTournamentList() {
		this.registeredTournaments.clear();
		for (Tournament tournament : this.loadedEvent.getTournaments()) {
			for (Player player : tournament.getRegisteredPlayers()) {
				if (player.getId().equals(this.loadedPlayer.getId())) {
					this.registeredTournaments.add(tournament);
				}
			}
		}
	}

	@Override
	public void setProperties(Object properties) {
		if (properties instanceof Player) {
			this.loadedPlayer = (Player) properties;
			// Clone the loaded player
			this.editedPlayer = new Player();
			this.editedPlayer.setFirstName(this.loadedPlayer.getFirstName());
			this.editedPlayer.setLastName(this.loadedPlayer.getId());
			this.editedPlayer.setId(this.loadedPlayer.getId());
			this.editedPlayer.setMailAdress(this.loadedPlayer.getMailAddress());
			this.editedPlayer.setNickName(this.loadedPlayer.getNickName());
			this.editedPlayer.setPayed(this.loadedPlayer.getPayed());
			this.editedPlayer.setDisqualified(this.loadedPlayer
					.getDisqualified());
			this.editedPlayer.setStartingNumber(this.loadedPlayer
					.getStartingNumber());

			// Bind dialog controls to the copied player
			this.textFieldFirstName.textProperty().bindBidirectional(
					this.editedPlayer.firstNameProperty());
			this.textFieldLastName.textProperty().bindBidirectional(
					this.editedPlayer.lastNameProperty());
			this.textFieldEmail.textProperty().bindBidirectional(
					this.editedPlayer.mailAdressProperty());
			this.textFieldNickname.textProperty().bindBidirectional(
					this.editedPlayer.nickNameProperty());
			this.checkBoxPayed.selectedProperty().bindBidirectional(
					this.editedPlayer.payedProperty());
		} else if (properties instanceof Event) {
			this.loadedEvent = (Event) properties;
		}

		if (this.loadedEvent != null && this.editedPlayer != null) {
			this.updateTournamentList();

			// create table bindings
			this.tableTournaments.setItems(this.registeredTournaments);
		}
	}

	@Override
	public Player getReturnValue() {
		return this.editedPlayer;
	}

	@Override
	public void initModalDialog(ModalDialog<Object, Player> modalDialog) {
		modalDialog.title("Spieler voranmelden").dialogButtons(
				DialogButtons.OK_CANCEL);
	}

	@FXML
	private void onButtonAddTournamentClicked(ActionEvent event) {
		ObservableList<Tournament> unregisteredTournaments = FXCollections
				.observableArrayList();
		for (Tournament tournament : this.loadedEvent.getTournaments()) {
			boolean playerRegistered = false;
			for (Player player : tournament.getRegisteredPlayers()) {
				if (player.getId().equals(this.loadedPlayer.getId())) {
					playerRegistered = true;
				}
			}
			if (!playerRegistered) {
				unregisteredTournaments.add(tournament);
			}
		}

		new TournamentSelectionDialog()
				.modalDialog()
				.properties(unregisteredTournaments)
				.onResult(
						(result, returnValue) -> {
							if (result == DialogResult.OK
									&& returnValue != null) {
								returnValue.getRegisteredPlayers().add(
										this.editedPlayer);
								this.updateTournamentList();
							}
						}).show();
	}

	@FXML
	private void onButtonRemoveTournamentClicked(ActionEvent event) {
		Tournament selectedTournament = this.tableTournaments
				.getSelectionModel().getSelectedItem();
		if (selectedTournament == null) {
			Dialogs.create()
					.owner(EntryPoint.getPrimaryStage())
					.title("Fehler")
					.message("Bitte wählen Sie eine Turnier aus der Liste aus.")
					.showError();
		} else {
			Action response = Dialogs
					.create()
					.owner(EntryPoint.getPrimaryStage())
					.title("Turnier löschen")
					.message(
							"Wollen Sie den Spieler \""
									+ this.loadedPlayer.getFirstName() + " "
									+ this.loadedPlayer.getLastName()
									+ "\" wirklich vom Turnier \""
									+ selectedTournament.getName()
									+ "\" abmelden?").showConfirm();

			if (response == Dialog.ACTION_YES) {
				for (Player player : selectedTournament.getRegisteredPlayers()) {
					if (player.getId().equals(this.loadedPlayer.getId())) {
						selectedTournament.getRegisteredPlayers()
								.remove(player);
					}
				}
			}
		}

		this.updateTournamentList();
	}

}

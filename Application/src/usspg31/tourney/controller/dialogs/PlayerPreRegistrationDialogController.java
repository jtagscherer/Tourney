package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import usspg31.tourney.model.Event;
import usspg31.tourney.model.Player;

public class PlayerPreRegistrationDialogController extends VBox implements
		IModalDialogProvider<Object, Player> {

	private static final Logger log = Logger
			.getLogger(PlayerPreRegistrationDialogController.class.getName());

	@FXML private TextField textFieldFirstName;
	@FXML private TextField textFieldLastName;
	@FXML private TextField textFieldEmail;
	@FXML private TextField textFieldNickname;
	@FXML private TableView<String> tableTournaments;
	@FXML private Button buttonAddTournament;
	@FXML private Button buttonRemoveTournament;
	@FXML private CheckBox checkBoxPayed;

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
			// TODO: bind (not bidirectional) the tournaments contained in the
			// event to table contents
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
		// TODO: show dialog to choose from existing tournaments the player
		// doesn't already attend
	}

	@FXML
	private void onButtonRemoveTournamentClicked(ActionEvent event) {
		// TODO: get selected tournament from the table and remove it
	}

}

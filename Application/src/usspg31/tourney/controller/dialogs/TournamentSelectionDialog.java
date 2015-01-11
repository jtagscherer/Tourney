package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import usspg31.tourney.model.Tournament;

public class TournamentSelectionDialog extends VBox implements IModalDialogProvider<ObservableList<Tournament>, Tournament> {

	private static final Logger log = Logger.getLogger(TournamentSelectionDialog.class.getName());

	@FXML private ComboBox<Tournament> comboBoxAvailableTournaments;

	public TournamentSelectionDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/fxml/dialogs/tournament-selection-dialog.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@FXML private void initialize() {
		this.comboBoxAvailableTournaments.setCellFactory(listView -> {
			return new ListCell<Tournament>() {
				@Override
				protected void updateItem(Tournament item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null || empty) {
						this.setGraphic(null);
					} else {
						this.setText(item.getName());
					}
				}
			};
		});
	}

	@Override
	public void setProperties(ObservableList<Tournament> properties) {
		this.comboBoxAvailableTournaments.setItems(properties);
	}

	@Override
	public Tournament getReturnValue() {
		return this.comboBoxAvailableTournaments.getValue();
	}

	@Override
	public void initModalDialog(
			ModalDialog<ObservableList<Tournament>, Tournament> modalDialog) {
		modalDialog.title("Turnier auswählen").dialogButtons(
				DialogButtons.OK);
	}
}
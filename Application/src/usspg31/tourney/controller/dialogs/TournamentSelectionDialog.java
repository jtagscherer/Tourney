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
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.model.Tournament;

public class TournamentSelectionDialog extends VBox implements
        IModalDialogProvider<ObservableList<Tournament>, Tournament> {

    private static final Logger log = Logger
            .getLogger(TournamentSelectionDialog.class.getName());

    @FXML private ComboBox<Tournament> comboBoxSelectedTournament;

    public TournamentSelectionDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
                    "/ui/fxml/dialogs/tournament-selection-dialog.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @FXML
    private void initialize() {
        this.comboBoxSelectedTournament.setCellFactory(listView -> {
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

        this.comboBoxSelectedTournament
                .setButtonCell(new ListCell<Tournament>() {
                    @Override
                    protected void updateItem(Tournament item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            this.setGraphic(null);
                        } else {
                            this.setText(item.getName());
                        }
                    }
                });
    }

    @Override
    public void setProperties(ObservableList<Tournament> properties) {
        this.comboBoxSelectedTournament.setItems(properties);
    }

    @Override
    public Tournament getReturnValue() {
        return this.comboBoxSelectedTournament.getValue();
    }

    @Override
    public void initModalDialog(
            ModalDialog<ObservableList<Tournament>, Tournament> modalDialog) {
        modalDialog.title("dialogs.tournamentselection").dialogButtons(
                DialogButtons.OK);
    }
}

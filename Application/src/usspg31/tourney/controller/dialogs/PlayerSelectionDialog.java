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
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.model.Player;

public class PlayerSelectionDialog extends VBox implements
        IModalDialogProvider<ObservableList<Player>, Player> {

    private static final Logger log = Logger
            .getLogger(PlayerSelectionDialog.class.getName());

    @FXML private ComboBox<Player> comboBoxSelectedPlayer;

    public PlayerSelectionDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
                    "/ui/fxml/dialogs/player-selection-dialog.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @FXML
    private void initialize() {
        this.comboBoxSelectedPlayer.setCellFactory(listView -> {
            return new ListCell<Player>() {
                @Override
                protected void updateItem(Player item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        this.setGraphic(null);
                        this.setText("");
                    } else {
                        this.setText(item.getFirstName() + " "
                                + item.getLastName() + " ("
                                + item.getStartingNumber() + ")");
                    }
                }
            };
        });

        this.comboBoxSelectedPlayer.setButtonCell(new ListCell<Player>() {
            @Override
            protected void updateItem(Player item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    this.setGraphic(null);
                    this.setText("");
                } else {
                    this.setText(item.getFirstName() + " " + item.getLastName()
                            + " (" + item.getStartingNumber() + ")");
                }
            }
        });
    }

    public String getInputErrorString() {
        if (this.comboBoxSelectedPlayer.getValue() == null) {
            return PreferencesManager.getInstance().localizeString(
                    "playerselectiondialog.error");
        } else {
            return null;
        }
    }

    @Override
    public void setProperties(ObservableList<Player> properties) {
        if (this.comboBoxSelectedPlayer.getItems().size() > 0) {
            this.comboBoxSelectedPlayer.getSelectionModel().clearSelection();
        }

        this.comboBoxSelectedPlayer.setItems(properties);
    }

    @Override
    public Player getReturnValue() {
        return this.comboBoxSelectedPlayer.getValue();
    }

    @Override
    public void initModalDialog(
            ModalDialog<ObservableList<Player>, Player> modalDialog) {
        modalDialog.title("playerselectiondialog.title").dialogButtons(
                DialogButtons.OK_CANCEL);
    }
}

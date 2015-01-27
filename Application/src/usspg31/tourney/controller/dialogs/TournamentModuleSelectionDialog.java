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
import usspg31.tourney.model.TournamentModule;

public class TournamentModuleSelectionDialog extends VBox
        implements
        IModalDialogProvider<ObservableList<TournamentModule>, TournamentModule> {

    private static final Logger log = Logger
            .getLogger(TournamentModuleSelectionDialog.class.getName());

    @FXML private ComboBox<TournamentModule> comboBoxSelectedTournamentModule;

    public TournamentModuleSelectionDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    this.getClass()
                            .getResource(
                                    "/ui/fxml/dialogs/tournament-module-selection-dialog.fxml"),
                    PreferencesManager.getInstance().getSelectedLanguage()
                            .getLanguageBundle());
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @FXML
    private void initialize() {
        this.comboBoxSelectedTournamentModule.setCellFactory(listView -> {
            return new ListCell<TournamentModule>() {
                @Override
                protected void updateItem(TournamentModule item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        this.setGraphic(null);
                    } else {
                        this.setText(item.getName());
                    }
                }
            };
        });

        this.comboBoxSelectedTournamentModule
                .setButtonCell(new ListCell<TournamentModule>() {
                    @Override
                    protected void updateItem(TournamentModule item,
                            boolean empty) {
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
    public void setProperties(ObservableList<TournamentModule> properties) {
        this.comboBoxSelectedTournamentModule.setItems(properties);
    }

    @Override
    public TournamentModule getReturnValue() {
        return this.comboBoxSelectedTournamentModule.getValue();
    }

    @Override
    public void initModalDialog(
            ModalDialog<ObservableList<TournamentModule>, TournamentModule> modalDialog) {
        modalDialog.title("dialogs.tournamentmoduleselection").dialogButtons(
                DialogButtons.OK_CANCEL);
    }
}

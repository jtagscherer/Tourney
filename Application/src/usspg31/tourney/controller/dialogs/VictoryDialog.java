package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;

public class VictoryDialog extends VBox implements
        IModalDialogProvider<VictoryConfiguration, Object> {

    private static final Logger log = Logger.getLogger(VictoryDialog.class
            .getName());

    @FXML private Label labelTournamentName;
    @FXML private Label labelWinner;

    private ModalDialog<VictoryConfiguration, Object> modalDialog;

    public VictoryDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
                    "/ui/fxml/dialogs/victory-dialog.fxml"), PreferencesManager
                    .getInstance().getSelectedLanguage().getLanguageBundle());

            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void setProperties(VictoryConfiguration configuration) {
        this.labelTournamentName.setText(configuration.getTournamentName());
        this.labelWinner.setText(configuration.getWinningPlayer()
                .getFirstName()
                + " "
                + configuration.getWinningPlayer().getLastName());
    }

    @Override
    public void initModalDialog(
            ModalDialog<VictoryConfiguration, Object> modalDialog) {
        this.modalDialog = modalDialog;
        this.modalDialog.dialogButtons(DialogButtons.OK);
        this.modalDialog.title("", false);
    }
}

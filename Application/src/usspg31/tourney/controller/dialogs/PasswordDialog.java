package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;

public class PasswordDialog extends VBox implements IModalDialogProvider<Integer, Integer> {

    private static final Logger log = Logger.getLogger(PasswordDialog.class.getName());

    @FXML private PasswordField passwordField;
    @FXML private Button buttonUnlock;

    private int triesFailed;

    public PasswordDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    this.getClass().getResource("/ui/fxml/dialogs/attendance-dialog.fxml"),
                    PreferencesManager.getInstance().getSelectedLanguage().getLanguageBundle());

            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void initModalDialog(ModalDialog<Integer, Integer> modalDialog) {
        modalDialog.title("dialogs.password");
    }

}

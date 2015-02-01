package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.controls.MaterialPasswordField;
import usspg31.tourney.controller.dialogs.modal.DialogResult;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;

public class PasswordDialog extends VBox implements
        IModalDialogProvider<Object, Object> {

    private static final Logger log = Logger.getLogger(PasswordDialog.class
            .getName());

    private static final int retrySeconds = 5;

    private final IntegerProperty retryIn;
    private final Timeline retryTimer;

    @FXML private MaterialPasswordField passwordField;
    @FXML private Button buttonUnlock;

    private int retryMultiplicator;

    private ModalDialog<Object, Object> modalDialog;

    public PasswordDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
                    "/ui/fxml/dialogs/password-dialog.fxml"),
                    PreferencesManager.getInstance().getSelectedLanguage()
                            .getLanguageBundle());

            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        this.retryIn = new SimpleIntegerProperty();
        this.retryIn
                .addListener((ov, o, n) -> {
                    if (n.intValue() == 0) {
                        this.buttonUnlock.setDisable(false);
                        this.buttonUnlock.textProperty().unbind();
                        this.buttonUnlock.setText(PreferencesManager
                                .getInstance().localizeString(
                                        "dialogs.password.unlock"));
                    } else {
                        if (!this.buttonUnlock.textProperty().isBound()) {
                            this.buttonUnlock.setDisable(true);
                            this.buttonUnlock
                                    .textProperty()
                                    .bind(new SimpleStringProperty(
                                            PreferencesManager
                                                    .getInstance()
                                                    .localizeString(
                                                            "dialogs.password.lockedtext.before"))
                                            .concat(this.retryIn)
                                            .concat(PreferencesManager
                                                    .getInstance()
                                                    .localizeString(
                                                            "dialogs.password.lockedtext.after")));
                            this.retryTimer.play();
                        }
                    }
                });

        this.retryTimer = new Timeline(new KeyFrame(Duration.seconds(1),
                event -> {
                    if (this.retryIn.get() > 1) {
                        this.retryIn.set(this.retryIn.get() - 1);
                    } else {
                        this.retryIn.set(0);
                        this.retryTimer.stop();
                    }
                }));
        this.retryTimer.setCycleCount(Timeline.INDEFINITE);

        // pressing enter in the password field has the same effect as clicking
        // the unlock button
        this.passwordField.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                this.onButtonUnlockClicked(null);
            }
        });

        this.retryMultiplicator = 1;
    }

    @Override
    public void initModalDialog(ModalDialog<Object, Object> modalDialog) {
        this.modalDialog = modalDialog;

        modalDialog.title("dialogs.password");
    }

    @FXML
    private void onButtonUnlockClicked(ActionEvent event) {
        boolean passwordCorrect = PreferencesManager.getInstance()
                .isPasswordCorrect(this.passwordField.getText());

        if (passwordCorrect) {
            // clear the password field or else the password will be already
            // filled in when we open the dialog again
            this.passwordField.setText("");
            this.modalDialog.exitWith(DialogResult.OK);
        } else {
            this.retryIn.set(retrySeconds * this.retryMultiplicator++);
        }
    }

}

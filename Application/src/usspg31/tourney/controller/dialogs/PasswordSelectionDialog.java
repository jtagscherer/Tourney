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

public class PasswordSelectionDialog extends VBox implements
        IModalDialogProvider<Object, Object> {

    private static final Logger log = Logger
            .getLogger(PasswordSelectionDialog.class.getName());

    private static final int retrySeconds = 5;

    private final IntegerProperty retryIn;
    private final Timeline retryTimer;

    @FXML private MaterialPasswordField passwordFieldCurrent;
    @FXML private MaterialPasswordField passwordFieldNew;
    @FXML private MaterialPasswordField passwordFieldNewRepeat;

    @FXML private Button buttonConfirm;
    @FXML private Button buttonCancel;

    private int retryMultiplicator;

    private ModalDialog<Object, Object> modalDialog;

    public PasswordSelectionDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
                    "/ui/fxml/dialogs/password-selection-dialog.fxml"),
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
                        this.buttonConfirm.setDisable(false);
                        this.buttonConfirm.disableProperty().bind(
                                this.passwordFieldNew.textProperty()
                                        .isNotEqualTo(
                                                this.passwordFieldNewRepeat
                                                        .textProperty()));
                        this.buttonConfirm.textProperty().unbind();
                        this.buttonConfirm.setText(PreferencesManager
                                .getInstance().localizeString(
                                        "dialogs.passwordselection.confirm"));
                    } else {
                        if (!this.buttonConfirm.textProperty().isBound()) {
                            this.buttonConfirm.disableProperty().unbind();
                            this.buttonConfirm.setDisable(true);
                            this.buttonConfirm
                                    .textProperty()
                                    .bind(new SimpleStringProperty(
                                            PreferencesManager
                                                    .getInstance()
                                                    .localizeString(
                                                            "dialogs.passwordselection.lockedtext.before"))
                                            .concat(this.retryIn)
                                            .concat(PreferencesManager
                                                    .getInstance()
                                                    .localizeString(
                                                            "dialogs.passwordselection.lockedtext.after")));
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

        // pressing enter in the repeat password field has the same effect as
        // clicking the confirm button
        this.passwordFieldNewRepeat.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                this.onButtonConfirmClicked(null);
            }
        });

        // only activate the confirm button if the repeated password equals the
        // new password
        this.buttonConfirm.disableProperty().bind(
                this.passwordFieldNew.textProperty().isNotEqualTo(
                        this.passwordFieldNewRepeat.textProperty()));

        // disable the current password field if none is set
        this.passwordFieldCurrent.disableProperty().bind(
                PreferencesManager.getInstance().passwordSetProperty().not());

        this.retryMultiplicator = 1;
    }

    @Override
    public void initModalDialog(ModalDialog<Object, Object> modalDialog) {
        this.modalDialog = modalDialog;

        modalDialog.title("dialogs.passwordselection");
    }

    @FXML
    private void onButtonConfirmClicked(ActionEvent event) {
        boolean passwordCorrect = PreferencesManager.getInstance()
                .isPasswordCorrect(this.passwordFieldCurrent.getText());

        if (passwordCorrect) {
            // set the new password
            if (PreferencesManager.getInstance().setPassword(
                    this.passwordFieldCurrent.getText(),
                    this.passwordFieldNew.getText())) {
                this.modalDialog.exitWith(DialogResult.OK);
            } else {
                this.modalDialog.exitWith(DialogResult.CANCEL);
            }

            // clear the password fields or else they will be already filled in
            // when we open the dialog again
            this.passwordFieldCurrent.setText("");
            this.passwordFieldNew.setText("");
            this.passwordFieldNewRepeat.setText("");
        } else {
            this.retryIn.set(retrySeconds * this.retryMultiplicator++);
        }
    }

    @FXML
    private void onButtonCancelClicked(ActionEvent event) {
        // clear the password fields or else they will be already filled in
        // when we open the dialog again
        this.passwordFieldCurrent.setText("");
        this.passwordFieldNew.setText("");
        this.passwordFieldNewRepeat.setText("");
        this.modalDialog.exitWith(DialogResult.CANCEL);
    }

}

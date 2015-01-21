package usspg31.tourney.controller;

import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import usspg31.tourney.controller.dialogs.PasswordSelectionDialog;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;

public class OptionsViewController {

    private static final Logger log = Logger
            .getLogger(OptionsViewController.class.getName());

    @FXML private Button buttonChangeLanguage;
    @FXML private Button buttonChangePassword;
    @FXML private Button buttonExit;

    @FXML private Label labelExitHeading;
    @FXML private Label labelExitDescription;
    @FXML private Label labelExitExplanation;

    private Runnable exitButtonCallback;

    private ModalDialog<Object, Object> passwordSelectionDialog;

    @FXML
    private void initialize() {
        this.exitButtonCallback = null;
    }

    public void setExitProperties(String titleKey, String descriptionKey,
            String explanationKey, Runnable exitCallback) {
        PreferencesManager preferences = PreferencesManager.getInstance();
        this.labelExitHeading.setText(preferences.localizeString(titleKey));
        this.labelExitDescription.setText(preferences
                .localizeString(descriptionKey));
        this.labelExitExplanation.setText(preferences
                .localizeString(explanationKey));
        this.exitButtonCallback = exitCallback;

        this.passwordSelectionDialog = new PasswordSelectionDialog()
                .modalDialog();
    }

    @FXML
    private void onButtonChangeLanguageClicked(ActionEvent event) {
        log.fine("Change Language Button was clicked");
    }

    @FXML
    private void onButtonChangePasswordClicked(ActionEvent event) {
        log.fine("Change Password Button was clicked");
        this.passwordSelectionDialog.show();
    }

    @FXML
    private void onButtonExitClicked(ActionEvent event) {
        log.fine("Exit Button was clicked");
        if (this.exitButtonCallback != null) {
            this.exitButtonCallback.run();
        }
    }

}

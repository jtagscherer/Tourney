package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.controls.NumberTextField;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.controller.dialogs.modal.SimpleDialog;

public class RegistrationDistributionDialog extends VBox implements
        IModalDialogProvider<String, Integer> {

    private static final Logger log = Logger
            .getLogger(RegistrationDistributionDialog.class.getName());

    @FXML private NumberTextField textFieldNumberOfRegistrators;

    public RegistrationDistributionDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
                    "/ui/fxml/dialogs/registration-distribution-dialog.fxml"),
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

    }

    @Override
    public void setProperties(String properties) {

    }

    @Override
    public Integer getReturnValue() {
        if (this.textFieldNumberOfRegistrators.getText().length() == 0) {
            return null;
        } else {
            return Integer
                    .valueOf(this.textFieldNumberOfRegistrators.getText());
        }
    }

    @Override
    public String getInputErrorString() {
        if (this.textFieldNumberOfRegistrators.getText().length() == 0) {
            return PreferencesManager.getInstance().localizeString(
                    "dialogs.registrationdistribution.errors.emptydata");
        } else if (this.getReturnValue() <= 1) {
            return PreferencesManager.getInstance().localizeString(
                    "dialogs.registrationdistribution.errors.numbertoolow");
        } else {
            return null;
        }
    }

    @FXML
    private void onButtonDistributionHelpClicked(ActionEvent event) {
        new SimpleDialog<>(PreferencesManager.getInstance().localizeString(
                "dialogs.registrationdistribution.info.message")).modalDialog()
                .dialogButtons(DialogButtons.OK)
                .title("dialogs.registrationdistribution.info.title").show();
    }

    @Override
    public void initModalDialog(ModalDialog<String, Integer> modalDialog) {
        modalDialog.title("dialogs.registrationdistribution").dialogButtons(
                DialogButtons.OK_CANCEL);
    }
}

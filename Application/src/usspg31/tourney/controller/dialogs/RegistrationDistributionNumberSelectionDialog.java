package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.controls.NumberTextField;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;

public class RegistrationDistributionNumberSelectionDialog extends VBox
	implements IModalDialogProvider<Integer, Integer> {

    private static final Logger log = Logger
	    .getLogger(RegistrationDistributionNumberSelectionDialog.class
		    .getName());

    private int maximumRegistratorNumber;

    @FXML
    private NumberTextField textFieldNumberOfRegistrator;

    public RegistrationDistributionNumberSelectionDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
                    "/ui/fxml/dialogs/registration-distribution-number-selection-dialog.fxml"),
                    PreferencesManager.getInstance().getSelectedLanguage().getLanguageBundle());
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @FXML
    private void initialize() {
	this.maximumRegistratorNumber = Integer.MAX_VALUE;
    }

    @Override
    public void setProperties(Integer properties) {
	this.maximumRegistratorNumber = properties;
    }

    @Override
    public Integer getReturnValue() {
	if (this.textFieldNumberOfRegistrator.getText().length() == 0) {
	    return null;
	} else {
	    return Integer.valueOf(this.textFieldNumberOfRegistrator.getText());
	}
    }

    @Override
    public boolean hasNoInput() {
	return this.textFieldNumberOfRegistrator.getText().length() == 0
		|| this.getReturnValue() <= 1
		|| this.getReturnValue() > this.maximumRegistratorNumber;
    }

    @Override
    public void initModalDialog(ModalDialog<Integer, Integer> modalDialog) {
        modalDialog.title("dialogs.registrationdistributionnumberselection")
        .dialogButtons(
                DialogButtons.OK);
    }
}

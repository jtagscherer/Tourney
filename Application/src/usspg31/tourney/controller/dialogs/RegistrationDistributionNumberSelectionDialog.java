package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import usspg31.tourney.controller.controls.NumberTextField;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;

public class RegistrationDistributionNumberSelectionDialog extends VBox
	implements IModalDialogProvider<String, Integer> {

    private static final Logger log = Logger
	    .getLogger(RegistrationDistributionNumberSelectionDialog.class
		    .getName());

    @FXML
    private NumberTextField textFieldNumberOfRegistrator;

    public RegistrationDistributionNumberSelectionDialog() {
	try {
	    FXMLLoader loader = new FXMLLoader(
		    this.getClass()
			    .getResource(
				    "/ui/fxml/dialogs/registration-distribution-number-selection-dialog.fxml"));
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
	if (this.textFieldNumberOfRegistrator.getText().length() == 0) {
	    return null;
	} else {
	    return Integer.valueOf(this.textFieldNumberOfRegistrator.getText());
	}
    }

    @Override
    public boolean hasNoInput() {
	return this.textFieldNumberOfRegistrator.getText().length() == 0
		|| this.getReturnValue() <= 1;
    }

    @Override
    public void initModalDialog(ModalDialog<String, Integer> modalDialog) {
	modalDialog.title("Arbeitsplatznummer wählen").dialogButtons(
		DialogButtons.OK);
    }
}

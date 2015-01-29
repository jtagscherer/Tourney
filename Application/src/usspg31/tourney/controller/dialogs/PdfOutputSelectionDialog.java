package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;

public class PdfOutputSelectionDialog extends VBox implements
	IModalDialogProvider<PdfOutputConfiguration, PdfOutputConfiguration> {

    private static final Logger log = Logger
            .getLogger(PdfOutputSelectionDialog.class.getName());

    @FXML
    private CheckBox checkBoxExportPlayers;
    @FXML
    private CheckBox checkBoxExportTournaments;

    private PdfOutputConfiguration loadedConfiguration;

    public PdfOutputSelectionDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
		    "/ui/fxml/dialogs/pdf-output-selection-dialog.fxml"),
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
    public void setProperties(PdfOutputConfiguration properties) {
	this.loadedConfiguration = properties;
    }

    @Override
    public PdfOutputConfiguration getReturnValue() {
	return null;
    }

    @Override
    public void initModalDialog(
	    ModalDialog<PdfOutputConfiguration, PdfOutputConfiguration> modalDialog) {
	modalDialog.title("dialogs.pdfoutputselection.title").dialogButtons(
                DialogButtons.OK_CANCEL);
    }
}

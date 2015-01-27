package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.model.Administrator;

public class AdministratorEditorDialog extends VBox implements
        IModalDialogProvider<Administrator, Administrator> {

    private static final Logger log = Logger
            .getLogger(AdministratorEditorDialog.class.getName());

    @FXML private TextField textFieldFirstName;
    @FXML private TextField textFieldLastName;
    @FXML private TextField textFieldEmail;
    @FXML private TextField textFieldPhoneNumber;

    private Administrator loadedAdministrator;

    public AdministratorEditorDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
                    "/ui/fxml/dialogs/administrator-editor-dialog.fxml"),
                    PreferencesManager.getInstance().getSelectedLanguage()
                            .getLanguageBundle());
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void setProperties(Administrator property) {
        this.loadAdministrator(property);
    }

    private void loadAdministrator(Administrator administrator) {
        if (this.loadedAdministrator != null) {
            this.unloadAdministrator();
        }

        this.loadedAdministrator = (Administrator) administrator.clone();

        this.textFieldFirstName.textProperty().bindBidirectional(
                this.loadedAdministrator.firstNameProperty());
        this.textFieldLastName.textProperty().bindBidirectional(
                this.loadedAdministrator.lastNameProperty());
        this.textFieldEmail.textProperty().bindBidirectional(
                this.loadedAdministrator.mailAdressProperty());
        this.textFieldPhoneNumber.textProperty().bindBidirectional(
                this.loadedAdministrator.phoneNumberProperty());
    }

    public void unloadAdministrator() {
        this.textFieldFirstName.textProperty().unbindBidirectional(
                this.loadedAdministrator.firstNameProperty());
        this.textFieldLastName.textProperty().unbindBidirectional(
                this.loadedAdministrator.lastNameProperty());
        this.textFieldEmail.textProperty().unbindBidirectional(
                this.loadedAdministrator.mailAdressProperty());
        this.textFieldPhoneNumber.textProperty().unbindBidirectional(
                this.loadedAdministrator.phoneNumberProperty());

        this.loadedAdministrator = null;
    }

    @Override
    public Administrator getReturnValue() {
        return this.loadedAdministrator;
    }

    @Override
    public void initModalDialog(
            ModalDialog<Administrator, Administrator> modalDialog) {
        modalDialog.title("dialogs.administratoreditor.title").dialogButtons(
                DialogButtons.OK_CANCEL);
    }

    @Override
    public String getInputErrorString() {
        if (this.loadedAdministrator.getFirstName().equals("")
                && this.loadedAdministrator.getLastName().equals("")
                && this.loadedAdministrator.getMailAddress().equals("")
                && this.loadedAdministrator.getPhoneNumber().equals("")) {
            return PreferencesManager.getInstance().localizeString(
                    "dialogs.administratoreditor.errors.emptydata");
        }
        if (!this.loadedAdministrator.hasValidMailAddress()
                && this.textFieldEmail.getText().length() > 0) {
            /* The entered mail address does not withstand simple regex matching */
            return PreferencesManager.getInstance().localizeString(
                    "dialogs.personediting.errors.invalidmail");
        }
        return null;
    }
}

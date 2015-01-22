package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.DialogResult;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.controller.dialogs.modal.SimpleDialog;
import usspg31.tourney.model.Administrator;
import usspg31.tourney.model.EventAdministrator;
import usspg31.tourney.model.TournamentAdministrator;

public class TournamentAdministratorListDialog extends HBox implements
        IModalDialogProvider<ObservableList<TournamentAdministrator>, Object> {

    private static final Logger log = Logger
            .getLogger(TournamentAdministratorListDialog.class.getName());

    @FXML private TableView<TournamentAdministrator> tableAdministrators;
    private TableColumn<TournamentAdministrator, String> tableColumnFirstName;
    private TableColumn<TournamentAdministrator, String> tableColumnLastName;
    private TableColumn<TournamentAdministrator, String> tableColumnMailAddress;
    private TableColumn<TournamentAdministrator, String> tableColumnPhoneNumber;

    @FXML private Button buttonAddAdministrator;
    @FXML private Button buttonRemoveAdministrator;
    @FXML private Button buttonEditAdministrator;

    private ModalDialog<Administrator, Administrator> tournamentAdministratorEditorDialog;

    private ObservableList<TournamentAdministrator> tournamentAdministratorList;

    public TournamentAdministratorListDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
                    "/ui/fxml/dialogs/administrator-list-dialog.fxml"),
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
        this.tournamentAdministratorEditorDialog = new AdministratorEditorDialog()
                .modalDialog();

        this.tableColumnFirstName = new TableColumn<TournamentAdministrator, String>(
                PreferencesManager.getInstance().localizeString(
                        "dialogs.administratorlistdialog.columns.firstname"));
        this.tableColumnFirstName.setCellValueFactory(cellData -> cellData
                .getValue().firstNameProperty());
        this.tableAdministrators.getColumns().add(this.tableColumnFirstName);

        this.tableColumnLastName = new TableColumn<TournamentAdministrator, String>(
                PreferencesManager.getInstance().localizeString(
                        "dialogs.administratorlistdialog.columns.lastname"));
        this.tableColumnLastName.setCellValueFactory(cellData -> cellData
                .getValue().lastNameProperty());
        this.tableAdministrators.getColumns().add(this.tableColumnLastName);

        this.tableColumnMailAddress = new TableColumn<TournamentAdministrator, String>(
                PreferencesManager.getInstance().localizeString(
                        "dialogs.administratorlistdialog.columns.mailaddress"));
        this.tableColumnMailAddress.setCellValueFactory(cellData -> cellData
                .getValue().mailAdressProperty());
        this.tableAdministrators.getColumns().add(this.tableColumnMailAddress);

        this.tableColumnPhoneNumber = new TableColumn<TournamentAdministrator, String>(
                PreferencesManager.getInstance().localizeString(
                        "dialogs.administratorlistdialog.columns.phonenumber"));
        this.tableColumnPhoneNumber.setCellValueFactory(cellData -> cellData
                .getValue().phoneNumberProperty());
        this.tableAdministrators.getColumns().add(this.tableColumnPhoneNumber);

        this.buttonEditAdministrator.disableProperty().bind(
                this.tableAdministrators.getSelectionModel()
                        .selectedItemProperty().isNull());

        this.buttonRemoveAdministrator.disableProperty().bind(
                this.tableAdministrators.getSelectionModel()
                        .selectedItemProperty().isNull());
    }

    @Override
    public void setProperties(ObservableList<TournamentAdministrator> properties) {
        this.tournamentAdministratorList = properties;
        this.tableAdministrators.setItems(this.tournamentAdministratorList);
    }

    @Override
    public void initModalDialog(
            ModalDialog<ObservableList<TournamentAdministrator>, Object> modalDialog) {
        modalDialog.title("dialogs.administratorlistdialog.tournament")
                .dialogButtons(DialogButtons.OK);
    }

    @FXML
    private void onButtonAddAdministratorClicked(ActionEvent event) {
        log.fine("Add Administrator Button clicked");

        this.tournamentAdministratorEditorDialog
                .properties(new EventAdministrator())
                .onResult(
                        (result, returnValue) -> {
                            if (result == DialogResult.OK
                                    && returnValue != null) {
                                TournamentAdministrator administrator = new TournamentAdministrator();
                                administrator.setFirstName(returnValue
                                        .getFirstName());
                                administrator.setLastName(returnValue
                                        .getLastName());
                                administrator.setMailAdress(returnValue
                                        .getMailAddress());
                                administrator.setPhoneNumber(returnValue
                                        .getPhoneNumber());
                                this.tournamentAdministratorList
                                        .add(administrator);
                            }
                        }).show();
    }

    @FXML
    private void onButtonRemoveAdministratorClicked(ActionEvent event) {
        log.fine("Remove Administrator Button clicked");

        TournamentAdministrator selectedAdministrator = this.tableAdministrators
                .getSelectionModel().getSelectedItem();

        if (selectedAdministrator == null) {
            return;
        }

        new SimpleDialog<>("Wollen Sie den Organisator \""
                + selectedAdministrator.getFirstName() + " "
                + selectedAdministrator.getLastName() + "\" wirklich löschen?")
                .modalDialog()
                .title("dialogs.administratorlistdialog.titles.confirmdelete")
                .dialogButtons(DialogButtons.YES_NO)
                .onResult(
                        (result, returnValue) -> {
                            if (result == DialogResult.YES) {
                                this.tableAdministrators.getItems().remove(
                                        selectedAdministrator);
                            }
                        }).show();
    }

    @FXML
    private void onButtonEditAdministratorClicked(ActionEvent event) {
        log.fine("Edit Administrator Button clicked");

        final Administrator selectedAdministrator = this.tableAdministrators
                .getSelectionModel().getSelectedItem();

        this.tournamentAdministratorEditorDialog
                .properties(selectedAdministrator)
                .onResult(
                        (result, returnValue) -> {
                            if (result == DialogResult.OK
                                    && returnValue != null) {
                                TournamentAdministrator administrator = new TournamentAdministrator();
                                administrator.setFirstName(returnValue
                                        .getFirstName());
                                administrator.setLastName(returnValue
                                        .getLastName());
                                administrator.setMailAdress(returnValue
                                        .getMailAddress());
                                administrator.setPhoneNumber(returnValue
                                        .getPhoneNumber());

                                this.tournamentAdministratorList
                                        .remove(selectedAdministrator);
                                this.tournamentAdministratorList
                                        .add(administrator);
                            }
                        }).show();
    }
}

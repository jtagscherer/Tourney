package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.DialogResult;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.controller.dialogs.modal.SimpleDialog;
import usspg31.tourney.model.TournamentModule;

public class TournamentModuleListDialog extends HBox implements
        IModalDialogProvider<ObservableList<TournamentModule>, Object> {

    private static final Logger log = Logger
            .getLogger(TournamentModuleListDialog.class.getName());

    @FXML private TableView<TournamentModule> tableTournamentModules;
    private TableColumn<TournamentModule, String> tableColumnModuleName;
    private TableColumn<TournamentModule, String> tableColumnModuleDescription;

    @FXML private Button buttonAddTournamentModule;
    @FXML private Button buttonRemoveTournamentModule;
    @FXML private Button buttonEditTournamentModule;
    @FXML private Button buttonDuplicateTournamentModule;

    private final ModalDialog<Object, TournamentModule> tournamentmoduleEditorDialog;
    private final TournamentModuleEditorDialog tournamentmoduleEditorDialogController;

    public TournamentModuleListDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
                    "/ui/fxml/dialogs/tournament-module-list-dialog.fxml"),
                    PreferencesManager.getInstance().getSelectedLanguage()
                            .getLanguageBundle());
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        this.tournamentmoduleEditorDialogController = new TournamentModuleEditorDialog();
        this.tournamentmoduleEditorDialog = this.tournamentmoduleEditorDialogController
                .modalDialog();
    }

    @FXML
    private void initialize() {
        PreferencesManager preferences = PreferencesManager.getInstance();

        this.tableColumnModuleName = new TableColumn<TournamentModule, String>(
                preferences.localizeString("dialogs.tournamentmodulelist.name"));
        this.tableColumnModuleName.setCellValueFactory(cellData -> cellData
                .getValue().nameProperty());
        this.tableTournamentModules.getColumns()
                .add(this.tableColumnModuleName);

        this.tableColumnModuleDescription = new TableColumn<TournamentModule, String>(
                preferences
                        .localizeString("dialogs.tournamentmodulelist.description"));
        this.tableColumnModuleDescription
                .setCellValueFactory(cellData -> cellData.getValue()
                        .descriptionProperty());
        this.tableTournamentModules.getColumns().add(
                this.tableColumnModuleDescription);

        /* Edit the tournament module on double click */
        this.tableTournamentModules.setRowFactory(tableView -> {
            TableRow<TournamentModule> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    this.editTournamentModule(row.getItem());
                }
            });
            return row;
        });

        this.tableTournamentModules.setPlaceholder(new Text(PreferencesManager
                .getInstance().localizeString("tableplaceholder.nomodules")));
    }

    @Override
    public void setProperties(ObservableList<TournamentModule> properties) {
        this.loadTournamentModuleList(properties);
    }

    public void loadTournamentModuleList(
            ObservableList<TournamentModule> tournamentModules) {
        this.unloadTournamentModuleList();

        /* Set the table contents */
        this.tableTournamentModules.setItems(tournamentModules);

        /* Bind the edit button's availablility */
        this.buttonEditTournamentModule.disableProperty().bind(
                this.tableTournamentModules.getSelectionModel()
                        .selectedItemProperty().isNull());
        this.buttonRemoveTournamentModule.disableProperty().bind(
                this.tableTournamentModules.getSelectionModel()
                        .selectedItemProperty().isNull());
        this.buttonDuplicateTournamentModule.disableProperty().bind(
                this.tableTournamentModules.getSelectionModel()
                        .selectedItemProperty().isNull());
    }

    public void unloadTournamentModuleList() {
        /* Unbind the tournament table */
        this.tableTournamentModules.getSelectionModel().clearSelection();

        /* Unbind the button's availability */
        this.buttonEditTournamentModule.disableProperty().unbind();
        this.buttonRemoveTournamentModule.disableProperty().unbind();
        this.buttonDuplicateTournamentModule.disableProperty().unbind();

        /* Unbind the dialogs */
        this.tournamentmoduleEditorDialogController.unloadModule();
    }

    @Override
    public void initModalDialog(
            ModalDialog<ObservableList<TournamentModule>, Object> modalDialog) {
        modalDialog.title("dialogs.tournamentmodulelist").dialogButtons(
                DialogButtons.OK);
    }

    @FXML
    private void onButtonAddTournamentModuleClicked(ActionEvent event) {
        log.fine("Add Tournament Module Button clicked");
        this.tournamentmoduleEditorDialog
                .properties(new TournamentModule())
                .properties(this.tableTournamentModules.getItems())
                .onResult(
                        (result, returnValue) -> {
                            if (result == DialogResult.OK
                                    && returnValue != null) {
                                if (this.tableTournamentModules.getItems() == null) {
                                    this.tableTournamentModules
                                            .setItems(FXCollections
                                                    .observableArrayList());
                                }
                                this.tableTournamentModules.getItems().add(
                                        returnValue);
                                PreferencesManager.getInstance()
                                        .saveTournamentModules(
                                                this.tableTournamentModules
                                                        .getItems());
                            }
                        }).show();
    }

    @FXML
    private void onButtonRemoveTournamentModuleClicked(ActionEvent event) {
        log.fine("Remove Tournament Module Button clicked");

        new SimpleDialog<>(PreferencesManager.getInstance().localizeString(
                "dialogs.tournamentmodulelist.dialogs.removemodule.message"))
                .modalDialog()
                .dialogButtons(DialogButtons.YES_NO)
                .title("dialogs.tournamentmodulelist.dialogs.removemodule.title")
                .onResult(
                        (result, returnValue) -> {
                            if (result == DialogResult.YES) {
                                PreferencesManager.getInstance()
                                        .removeTournamentFile(
                                                this.tableTournamentModules
                                                        .getSelectionModel()
                                                        .getSelectedItem()
                                                        .getName());
                                this.tableTournamentModules.getItems().remove(
                                        this.tableTournamentModules
                                                .getSelectionModel()
                                                .getSelectedIndex());
                            }
                        }).show();
    }

    @FXML
    private void onButtonEditTournamentModuleClicked(ActionEvent event) {
        log.fine("Edit Tournament Module Button clicked");
        this.editTournamentModule(this.tableTournamentModules
                .getSelectionModel().getSelectedItem());
    }

    @FXML
    private void onButtonDuplicateTournamentModuleClicked(ActionEvent event) {
        log.fine("Duplicate Tournament Module Button clicked");

        TournamentModule copiedTournamentModule = (TournamentModule) this.tableTournamentModules
                .getSelectionModel().getSelectedItem().clone();
        String copiedModuleName = copiedTournamentModule.getName();

        /* Get the next possible tournament module name */
        ArrayList<String> tournamentModuleNames = new ArrayList<String>();
        for (TournamentModule module : this.tableTournamentModules.getItems()) {
            tournamentModuleNames.add(module.getName());
        }

        while (tournamentModuleNames.contains(copiedModuleName)) {
            copiedModuleName = PreferencesManager.getInstance().localizeString(
                    "dialogs.tournamentmodulelist.duplicatemodule.prefix")
                    + " " + copiedModuleName;

            tournamentModuleNames.clear();
            for (TournamentModule module : this.tableTournamentModules
                    .getItems()) {
                tournamentModuleNames.add(module.getName());
            }
        }

        /* Add and save the tournament module */
        copiedTournamentModule.setName(copiedModuleName);
        this.tableTournamentModules.getItems().add(copiedTournamentModule);
        PreferencesManager.getInstance().saveTournamentModules(
                this.tableTournamentModules.getItems());
    }

    /**
     * Open a dialog to edit the given tournament module
     * 
     * @param selectedModule
     *            Tournament module to be edited
     */
    private void editTournamentModule(TournamentModule selectedModule) {
        this.tournamentmoduleEditorDialog
                .properties(selectedModule)
                .properties(this.tableTournamentModules.getItems())
                .onResult(
                        (result, returnValue) -> {
                            if (result == DialogResult.OK
                                    && returnValue != null) {
                                PreferencesManager.getInstance()
                                        .removeTournamentFile(
                                                selectedModule.getName());
                                this.tableTournamentModules.getItems().remove(
                                        selectedModule);
                                this.tableTournamentModules.getItems().add(
                                        returnValue);
                                PreferencesManager.getInstance()
                                        .saveTournamentModules(
                                                this.tableTournamentModules
                                                        .getItems());
                            }
                        }).show();
    }
}

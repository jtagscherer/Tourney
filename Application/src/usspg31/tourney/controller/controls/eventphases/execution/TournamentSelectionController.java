package usspg31.tourney.controller.controls.eventphases.execution;

import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.controls.EventUser;
import usspg31.tourney.controller.controls.eventphases.TournamentExecutionPhaseController;
import usspg31.tourney.controller.dialogs.PlayerPreRegistrationDialog;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.DialogResult;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.controller.dialogs.modal.SimpleDialog;
import usspg31.tourney.controller.util.SearchUtilities;
import usspg31.tourney.model.Event;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.Tournament;
import usspg31.tourney.model.TournamentRound;

public class TournamentSelectionController implements EventUser {

    private static final Logger log = Logger
            .getLogger(TournamentSelectionController.class.getName());

    @FXML private TextField textFieldTournamentSearch;
    @FXML private TableView<Tournament> tableTournaments;

    @FXML private Button buttonExecuteTournament;
    @FXML private Button buttonExportTournament;
    @FXML private Button buttonImportTournament;

    private TableColumn<Tournament, String> tableColumnTournamentName;
    private TableColumn<Tournament, ObservableList<TournamentRound>> tableColumnTournamentStatus;

    private Event loadedEvent;

    private TournamentExecutionPhaseController phaseController;

    private ModalDialog<Object, Player> preRegistrationDialog;

    @FXML
    private void initialize() {
        this.preRegistrationDialog = new PlayerPreRegistrationDialog()
                .modalDialog();

        this.initPlayerTable();

        // Bind the button's availability to the list selection
        this.buttonExecuteTournament.disableProperty().bind(
                this.tableTournaments.getSelectionModel()
                        .selectedItemProperty().isNull());
        this.buttonExportTournament.disableProperty().bind(
                this.tableTournaments.getSelectionModel()
                        .selectedItemProperty().isNull());
    }

    private void initPlayerTable() {
        this.tableColumnTournamentName = new TableColumn<>(PreferencesManager
                .getInstance().localizeString(
                        "tournamentselection.tournamentname"));
        this.tableColumnTournamentName.setCellValueFactory(cellData -> cellData
                .getValue().nameProperty());
        this.tableTournaments.getColumns().add(this.tableColumnTournamentName);

        this.tableColumnTournamentStatus = new TableColumn<>(PreferencesManager
                .getInstance().localizeString(
                        "tournamentselection.tournamentstatus"));
        this.tableColumnTournamentStatus
                .setCellValueFactory(new PropertyValueFactory<Tournament, ObservableList<TournamentRound>>(
                        "rounds"));
        this.tableColumnTournamentStatus
                .setCellFactory(column -> {
                    return new TableCell<Tournament, ObservableList<TournamentRound>>() {
                        @Override
                        protected void updateItem(
                                ObservableList<TournamentRound> item,
                                boolean empty) {
                            super.updateItem(item, empty);
                            if (item != null) {
                                if (item.size() == 0) {
                                    setText(PreferencesManager
                                            .getInstance()
                                            .localizeString(
                                                    "tournamentselection.tournamentstatus.notexecuted"));
                                } else {
                                    setText(PreferencesManager
                                            .getInstance()
                                            .localizeString(
                                                    "tournamentselection.tournamentstatus.executed"));
                                }
                            }
                        }
                    };
                });
        this.tableTournaments.getColumns()
                .add(this.tableColumnTournamentStatus);
    }

    @Override
    public void loadEvent(Event event) {
        log.info("Loading Event");
        if (this.loadedEvent != null) {
            this.unloadEvent();
        }

        this.tableTournaments.getSelectionModel().clearSelection();

        this.loadedEvent = event;

        // Add all tournaments to the table view and enable searching
        FilteredList<Tournament> filteredTournamentList = new FilteredList<>(
                this.loadedEvent.getTournaments(), p -> true);

        this.textFieldTournamentSearch.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    filteredTournamentList.setPredicate(tournament -> {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }

                        return SearchUtilities.fuzzyMatches(
                                tournament.getName(), newValue);
                    });
                });

        SortedList<Tournament> sortedTournamentList = new SortedList<>(
                filteredTournamentList);
        sortedTournamentList.comparatorProperty().bind(
                this.tableTournaments.comparatorProperty());

        this.tableTournaments.setItems(sortedTournamentList);
    }

    @Override
    public void unloadEvent() {
        log.info("Unloading Event");
        if (this.loadedEvent == null) {
            log.warning("Trying to unload an event, even though no event was loaded");
            return;
        }

        // TODO: unregister all listeners we registered to anything in the event

        this.loadedEvent = null;
    }

    public void setExecutionSuperController(
            TournamentExecutionPhaseController controller) {
        this.phaseController = controller;
    }

    @FXML
    private void onButtonExecuteTournamentClicked(ActionEvent event) {
        log.fine("Execute Tournament Button clicked");
        this.checkEventLoaded();

        final Tournament selectedTournament = this.tableTournaments
                .getSelectionModel().getSelectedItem();
        if (selectedTournament == null) {
            new SimpleDialog<>(PreferencesManager.getInstance().localizeString(
                    "tournamentselection.dialogs.noselection")).modalDialog()
                    .dialogButtons(DialogButtons.OK)
                    .title("dialogs.titles.error").show();
        } else {
            new SimpleDialog<>(PreferencesManager.getInstance().localizeString(
                    "tournamentselection.dialogs.execute.before")
                    + " \""
                    + selectedTournament.getName()
                    + "\" "
                    + PreferencesManager.getInstance().localizeString(
                            "tournamentselection.dialogs.execute.after"))
                    .modalDialog()
                    .dialogButtons(DialogButtons.YES_NO)
                    .title("tournamentselection.dialogs.execute.title")
                    .onResult(
                            (result, returnValue) -> {
                                if (result == DialogResult.YES) {
                                    this.phaseController
                                            .showTournamentExecutionView(selectedTournament);
                                }
                            }).show();
        }
    }

    @FXML
    private void onButtonExportTournamentClicked(ActionEvent event) {
        log.fine("Export Tournament Button clicked");

        // TODO: Implement export functionality
    }

    @FXML
    private void onButtonImportTournamentClicked(ActionEvent event) {
        log.fine("Import Tournament Button clicked");
        this.checkEventLoaded();

        // TODO: Implement import functionality
    }

    private void checkEventLoaded() {
        if (this.loadedEvent == null) {
            throw new IllegalStateException("An Event must be loaded in order "
                    + "to perform actions on this controller");
        }

        if (this.phaseController == null) {
            throw new IllegalStateException(
                    "A super controller must be attached in order "
                            + "to perform actions on this controller");
        }
    }
}

package usspg31.tourney.controller.controls.eventphases;

import java.time.LocalDate;
import java.util.logging.Logger;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import usspg31.tourney.controller.EntryPoint;
import usspg31.tourney.controller.MainWindow;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.controls.EventUser;
import usspg31.tourney.controller.controls.UndoTextArea;
import usspg31.tourney.controller.controls.UndoTextField;
import usspg31.tourney.controller.dialogs.EventAdministratorListDialog;
import usspg31.tourney.controller.dialogs.TournamentDialog;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.DialogResult;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.controller.dialogs.modal.SimpleDialog;
import usspg31.tourney.model.Event;
import usspg31.tourney.model.EventAdministrator;
import usspg31.tourney.model.Tournament;
import usspg31.tourney.model.undo.UndoManager;

public class EventSetupPhaseController implements EventUser {

    private final static Logger log = Logger
            .getLogger(EventSetupPhaseController.class.getName());

    @FXML private UndoTextField textFieldEventTitle;
    @FXML private DatePicker datePickerStartDate;
    @FXML private DatePicker datePickerEndDate;
    @FXML private UndoTextArea textAreaEventLocation;

    @FXML private TableView<Tournament> tableTournaments;
    @FXML private Button buttonAddTournament;
    @FXML private Button buttonRemoveTournament;
    @FXML private Button buttonEditTournament;

    private TableColumn<Tournament, String> tableColumnTournamentTitle;

    private Event loadedEvent;

    private ModalDialog<Tournament, Tournament> tournamentDialog;
    private ModalDialog<ObservableList<EventAdministrator>, Object> eventAdministratorListDialog;

    @FXML
    private void initialize() {
        this.tournamentDialog = new TournamentDialog().modalDialog();
        this.eventAdministratorListDialog = new EventAdministratorListDialog()
                .modalDialog();

        this.initTournamentTable();

        EntryPoint
                .getPrimaryStage()
                .titleProperty()
                .bind(Bindings
                        .when(this.textFieldEventTitle.textProperty().isEmpty())
                        .then(Bindings.concat("Tourney"))
                        .otherwise(
                                Bindings.concat("Tourney \u2014 ").concat(
                                        this.textFieldEventTitle.textProperty())));

        // restrict the maximum start date to be at most the end date
        this.datePickerStartDate.setDayCellFactory(value -> {
            return new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);

                    LocalDate maxDate = EventSetupPhaseController.this.datePickerEndDate
                            .getValue();

                    if (maxDate != null && item.isAfter(maxDate)) {
                        this.setDisable(true);
                    }
                }
            };
        });

        // restrict the minimum end date to be at least the start date
        this.datePickerEndDate.setDayCellFactory(value -> {
            return new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);

                    LocalDate minDate = EventSetupPhaseController.this.datePickerStartDate
                            .getValue();

                    if (minDate == null || item.isBefore(minDate)) {
                        this.setDisable(true);
                    }
                }
            };
        });

        // if the start date is set for the first time, set the same date in
        // the end DatePicker
        this.datePickerStartDate.valueProperty().addListener((ov, o, n) -> {
            if (n != null) {
                this.datePickerEndDate.setValue(n);
            }
        });
    }

    private void initTournamentTable() {
        // create title column
        this.tableColumnTournamentTitle = new TableColumn<>("Titel");
        this.tableColumnTournamentTitle
                .setCellValueFactory(cellData -> cellData.getValue()
                        .nameProperty());

        this.tableColumnTournamentTitle.prefWidthProperty().bind(
                this.tableTournaments.widthProperty());

        this.tableTournaments
                .setPlaceholder(new Text(PreferencesManager.getInstance()
                        .localizeString("tableplaceholder.notournaments")));

        this.tableTournaments.getColumns().add(this.tableColumnTournamentTitle);

        /* Edit the tournament on double click */
        this.tableTournaments.setRowFactory(tableView -> {
            TableRow<Tournament> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    this.editTournament(row.getItem());
                }
            });
            return row;
        });
    }

    @Override
    public void loadEvent(Event event) {
        log.info("Loading Event");
        if (this.loadedEvent != null) {
            this.unloadEvent();
        }

        this.tableTournaments.getSelectionModel().clearSelection();

        this.loadedEvent = event;

        // bind all basic control's values
        this.textFieldEventTitle.undoTextProperty().bindBidirectional(
                event.nameProperty());
        this.datePickerStartDate.valueProperty().bindBidirectional(
                event.startDateProperty());
        this.datePickerEndDate.valueProperty().bindBidirectional(
                event.endDateProperty());
        this.textAreaEventLocation.undoTextProperty().bindBidirectional(
                event.locationProperty());

        // bind the button disable property
        this.buttonEditTournament.disableProperty().bind(
                this.tableTournaments.getSelectionModel()
                        .selectedItemProperty().isNull());
        this.buttonRemoveTournament.disableProperty().bind(
                this.tableTournaments.getSelectionModel()
                        .selectedItemProperty().isNull());

        // create table bindings
        this.tableTournaments.setItems(this.loadedEvent.getTournaments());

        // register undo properties
        UndoManager undo = MainWindow.getInstance()
                .getEventPhaseViewController().getUndoManager();
        undo.registerUndoProperty(this.textFieldEventTitle.textProperty(), true);
        undo.registerUndoProperty(this.textAreaEventLocation.textProperty(), true);
        undo.registerUndoProperty(this.datePickerStartDate.valueProperty());
        undo.registerUndoProperty(this.datePickerEndDate.valueProperty());
        undo.registerUndoProperty(this.tableTournaments.getItems());

        undo.clearHistory();
    }

    @Override
    public void unloadEvent() {
        log.info("Unloading Event");
        if (this.loadedEvent == null) {
            log.warning("Trying to unload an event, even though no event was loaded");
            return;
        }

        Event event = this.loadedEvent;

        // unbind the table
        this.tableTournaments.getSelectionModel().clearSelection();

        // unbind all basic control's values
        event.nameProperty().unbindBidirectional(
                this.textFieldEventTitle.undoTextProperty());
        this.textFieldEventTitle.setText("");
        event.locationProperty().unbindBidirectional(
                this.textAreaEventLocation.textProperty());
        event.startDateProperty().unbindBidirectional(
                this.datePickerStartDate.valueProperty());
        event.endDateProperty().unbindBidirectional(
                this.datePickerEndDate.valueProperty());

        // unbind the buttons
        this.buttonEditTournament.disableProperty().unbind();
        this.buttonRemoveTournament.disableProperty().unbind();

        // unregister undo properties
        UndoManager undo = MainWindow.getInstance()
                .getEventPhaseViewController().getUndoManager();
        undo.unregisterUndoProperty(this.textFieldEventTitle.textProperty());
        undo.unregisterUndoProperty(this.textAreaEventLocation.textProperty());
        undo.unregisterUndoProperty(this.datePickerStartDate.valueProperty());
        undo.unregisterUndoProperty(this.datePickerEndDate.valueProperty());
        undo.unregisterUndoProperty(this.tableTournaments.getItems());

        this.loadedEvent = null;
    }

    @FXML
    private void onButtonEventAdministratorsClicked(ActionEvent event) {
        log.fine("Event Administrator Button clicked");

        this.eventAdministratorListDialog.properties(
                this.loadedEvent.getAdministrators()).show();
    }

    @FXML
    private void onButtonAddTournamentClicked(ActionEvent event) {
        log.fine("Add Tournament Button clicked");
        this.checkEventLoaded();
        this.tournamentDialog
                .properties(new Tournament())
                .onResult(
                        (result, returnValue) -> {
                            if (result == DialogResult.OK
                                    && returnValue != null) {
                                returnValue.setId(String
                                        .valueOf(this.loadedEvent
                                                .getTournaments().size()));
                                this.loadedEvent.getTournaments().add(
                                        returnValue);
                            }
                            returnValue.setId(String.valueOf(this.loadedEvent
                                    .getTournaments().size()));
                        }).show();
    }

    @FXML
    private void onButtonRemoveTournamentClicked(ActionEvent event) {
        log.fine("Remove Tournament Button clicked");
        this.checkEventLoaded();

        final Tournament selectedTournament = this.getSelectedTournament();

        new SimpleDialog<>(
                PreferencesManager
                        .getInstance()
                        .localizeString(
                                "eventsetupphase.dialogs.deletetournament.message.before")
                        + " \""
                        + selectedTournament.getName()
                        + "\" "
                        + PreferencesManager
                                .getInstance()
                                .localizeString(
                                        "eventsetupphase.dialogs.deletetournament.message.after"))
                .modalDialog()
                .dialogButtons(DialogButtons.YES_NO)
                .title("eventsetupphase.dialogs.deletetournament.title")
                .onResult(
                        (result, returnValue) -> {
                            if (result == DialogResult.YES) {
                                this.loadedEvent.getTournaments().remove(
                                        selectedTournament);
                            }
                        }).show();

    }

    @FXML
    private void onButtonEditTournamentClicked(ActionEvent event) {
        log.fine("Edit Tournament Button clicked");
        this.checkEventLoaded();
        final Tournament selectedTournament = this.getSelectedTournament();
        this.editTournament(selectedTournament);
    }

    /**
     * Open a dialog to edit the given tournament
     *
     * @param tournament
     *            Tournament to be edited
     */
    private void editTournament(Tournament tournament) {
        this.tournamentDialog
                .properties(tournament)
                .onResult((result, returnValue) -> {
                    // TODO: well, this obviously won't work like that.
                        if (result == DialogResult.OK && returnValue != null) {
                            UndoManager undo = MainWindow.getInstance()
                                    .getEventPhaseViewController().getUndoManager();
                            undo.beginUndoBatch();
                            this.loadedEvent.getTournaments()
                                    .remove(tournament);
                            this.loadedEvent.getTournaments().add(returnValue);
                            undo.endUndoBatch();
                        }
                    }).show();
    }

    private Tournament getSelectedTournament() {
        return this.tableTournaments.getSelectionModel().getSelectedItem();
    }

    private void checkEventLoaded() {
        if (this.loadedEvent == null) {
            throw new IllegalStateException("An Event must be loaded in order "
                    + "to perform actions on this controller");
        }
    }
}

package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import usspg31.tourney.controller.controls.UndoTextArea;
import usspg31.tourney.controller.controls.UndoTextField;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.DialogResult;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.controller.util.MapToStringBinding;
import usspg31.tourney.model.GamePhase;
import usspg31.tourney.model.PossibleScoring;
import usspg31.tourney.model.TournamentModule;

public class TournamentModuleEditorDialog extends SplitPane implements
	IModalDialogProvider<TournamentModule, TournamentModule> {

    private static final Logger log = Logger
	    .getLogger(TournamentModuleEditorDialog.class.getName());

    @FXML
    private UndoTextField textFieldModuleTitle;
    @FXML
    private UndoTextArea textAreaDescription;

    @FXML
    private TableView<GamePhase> tableTournamentPhases;
    private TableColumn<GamePhase, String> tableColumnPhasesPhaseNumber;
    private TableColumn<GamePhase, String> tableColumnPhasesPairingMethod;
    private TableColumn<GamePhase, String> tableColumnPhasesRoundCount;
    private TableColumn<GamePhase, String> tableColumnPhasesCutoff;
    private TableColumn<GamePhase, String> tableColumnPhasesRoundDuration;
    private TableColumn<GamePhase, String> tableColumnPhasesNumberOfOpponents;

    @FXML
    private Button buttonMovePhaseUp;
    @FXML
    private Button buttonMovePhaseDown;
    @FXML
    private Button buttonAddPhase;
    @FXML
    private Button buttonRemovePhase;
    @FXML
    private Button buttonEditPhase;

    @FXML
    private TableView<PossibleScoring> tablePossibleScores;
    private TableColumn<PossibleScoring, String> tableColumnPossibleScoresPriority;
    private TableColumn<PossibleScoring, String> tableColumnPossibleScoresScores;

    @FXML
    private Button buttonMoveScoreUp;
    @FXML
    private Button buttonMoveScoreDown;
    @FXML
    private Button buttonAddScore;
    @FXML
    private Button buttonRemoveScore;
    @FXML
    private Button buttonEditScore;

    private ModalDialog<GamePhase, GamePhase> tournamentPhaseDialog;

    private TournamentModule loadedModule;

    public TournamentModuleEditorDialog() {
	try {
	    FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
		    "/ui/fxml/dialogs/tournament-module-editor-dialog.fxml"));
	    loader.setController(this);
	    loader.setRoot(this);
	    loader.load();
	} catch (IOException e) {
	    log.log(Level.SEVERE, e.getMessage(), e);
	}
    }

    @FXML
    private void initialize() {
	this.tournamentPhaseDialog = new TournamentPhaseDialog().modalDialog();

	this.initTournamentPhaseTable();
	this.initPossibleScoresTable();
    }

    private void initTournamentPhaseTable() {
	// setup all table columns
	this.tableColumnPhasesPhaseNumber = new TableColumn<>("#");
	this.tableColumnPhasesPhaseNumber.cellValueFactoryProperty().set(
		cellData -> cellData.getValue().phaseNumberProperty()
			.asString());
	this.tableTournamentPhases.getColumns().add(
		this.tableColumnPhasesPhaseNumber);

	this.tableColumnPhasesPairingMethod = new TableColumn<>(
		"Paarungsmethode");
	this.tableColumnPhasesPairingMethod.cellValueFactoryProperty().set(
		cellData -> new SimpleStringProperty(cellData.getValue()
			.getPairingMethod().getName()));
	this.tableTournamentPhases.getColumns().add(
		this.tableColumnPhasesPairingMethod);

	this.tableColumnPhasesRoundCount = new TableColumn<>("Rundendanzahl");
	this.tableColumnPhasesRoundCount.cellValueFactoryProperty()
		.set(cellData -> cellData.getValue().roundCountProperty()
			.asString());
	this.tableTournamentPhases.getColumns().add(
		this.tableColumnPhasesRoundCount);

	this.tableColumnPhasesCutoff = new TableColumn<>("Cutoff");
	this.tableColumnPhasesCutoff.cellValueFactoryProperty().set(
		cellData -> cellData.getValue().cutoffProperty().asString());
	this.tableTournamentPhases.getColumns().add(
		this.tableColumnPhasesCutoff);

	this.tableColumnPhasesRoundDuration = new TableColumn<>("Rundendauer");
	this.tableColumnPhasesRoundDuration.cellValueFactoryProperty().set(
		cellData -> cellData.getValue().roundDurationProperty()
			.asString());
	this.tableTournamentPhases.getColumns().add(
		this.tableColumnPhasesRoundDuration);

	this.tableColumnPhasesNumberOfOpponents = new TableColumn<>(
		"Spieler je Paarung");
	this.tableColumnPhasesNumberOfOpponents.cellValueFactoryProperty().set(
		cellData -> cellData.getValue().numberOfOpponentsProperty()
			.asString());
	this.tableTournamentPhases.getColumns().add(
		this.tableColumnPhasesNumberOfOpponents);
    }

    private void initPossibleScoresTable() {
	this.tableColumnPossibleScoresPriority = new TableColumn<>("Priorität");
	this.tableColumnPossibleScoresPriority.cellValueFactoryProperty().set(
		cellData -> cellData.getValue().priorityProperty().asString());
	this.tablePossibleScores.getColumns().add(
		this.tableColumnPossibleScoresPriority);

	this.tableColumnPossibleScoresScores = new TableColumn<>("Wertungen");
	this.tableColumnPossibleScoresScores.cellValueFactoryProperty().set(
		cellData -> new SimpleStringProperty(""));
	this.tablePossibleScores.getColumns().add(
		this.tableColumnPossibleScoresScores);
    }

    @Override
    public void setProperties(TournamentModule properties) {
	if (this.loadedModule != null) {
	    this.unloadModule();
	}
	this.loadModule(properties);
    }

    @Override
    public TournamentModule getReturnValue() {
	return this.loadedModule;
    }

    @Override
    public void initModalDialog(
	    ModalDialog<TournamentModule, TournamentModule> modalDialog) {
	modalDialog.title("Turniermodul").dialogButtons(DialogButtons.OK);
    }

    private void loadModule(TournamentModule module) {
	log.fine("Loading Tournament Module");
	this.loadedModule = module;

	this.textFieldModuleTitle.textProperty().bindBidirectional(
		module.nameProperty());
	this.textAreaDescription.textProperty().bindBidirectional(
		module.descriptionProperty());

	if (module.getPhaseList().size() == 0) {
	    module.getPhaseList().add(new GamePhase());
	}
	this.tableTournamentPhases.setItems(module.getPhaseList());
	this.tablePossibleScores.setItems(module.getPossibleScores());

	ReadOnlyIntegerProperty selectedPhaseIndex = this.tableTournamentPhases
		.getSelectionModel().selectedIndexProperty();
	ReadOnlyObjectProperty<GamePhase> selectedPhase = this.tableTournamentPhases
		.getSelectionModel().selectedItemProperty();

	// only enable move-up button if an item other than the topmost is
	// selected
	this.buttonMovePhaseUp.disableProperty().bind(
		selectedPhaseIndex.isEqualTo(0).or(selectedPhase.isNull()));

	// only enable move-down button if an item other than the last one is
	// selected
	// index < size - 1 && selected != null
	this.buttonMovePhaseDown.disableProperty().bind(
		selectedPhaseIndex.greaterThanOrEqualTo(
			Bindings.size(this.tableTournamentPhases.getItems())
				.subtract(1)).or(selectedPhase.isNull()));

	// only enable remove button if an item is selected and there is more
	// than one possible score
	this.buttonRemovePhase.disableProperty().bind(
		selectedPhase.isNull().or(
			Bindings.size(this.tableTournamentPhases.getItems())
				.lessThanOrEqualTo(1)));

	// only enable edit button if an item is selected
	this.buttonEditPhase.disableProperty().bind(selectedPhase.isNull());

	// TODO: add bindings for the possible score table's buttons (see
	// TournamentDialog#loadTournament())
	this.tableColumnPossibleScoresScores
		.setCellValueFactory(cellValue -> new MapToStringBinding<>(
			cellValue.getValue().getScores()).getStringProperty());

	log.fine("Tournament Module loaded");
    }

    private void unloadModule() {
	log.fine("Unloading Tournament Module");

	this.textFieldModuleTitle.textProperty().unbindBidirectional(
		this.loadedModule.nameProperty());
	this.textAreaDescription.textProperty().unbindBidirectional(
		this.loadedModule.descriptionProperty());

	log.fine("Tournament Module unloaded");
    }

    @FXML
    private void onButtonMovePhaseUpClicked(ActionEvent event) {
	// TODO: update the phase numbers correctly
	log.fine("Move Tournament Phase Up Button was clicked");
	int selectedIndex = this.tableTournamentPhases.getSelectionModel()
		.getSelectedIndex();
	int indexToSwap = selectedIndex - 1;
	GamePhase tmp = this.getSelectedPhase();
	ObservableList<GamePhase> phases = this.tableTournamentPhases
		.getItems();
	phases.set(selectedIndex, phases.get(indexToSwap));
	phases.set(indexToSwap, tmp);
	this.tableTournamentPhases.getSelectionModel().select(indexToSwap);
    }

    @FXML
    private void onButtonMovePhaseDownClicked(ActionEvent event) {
	// TODO: update the phase numbers correctly
	log.fine("Move Tournament Phase Down Button was clicked");
	int selectedIndex = this.tableTournamentPhases.getSelectionModel()
		.getSelectedIndex();
	int indexToSwap = selectedIndex + 1;
	GamePhase tmp = this.getSelectedPhase();
	ObservableList<GamePhase> phases = this.tableTournamentPhases
		.getItems();
	phases.set(selectedIndex, phases.get(indexToSwap));
	phases.set(indexToSwap, tmp);
	this.tableTournamentPhases.getSelectionModel().select(indexToSwap);
    }

    @FXML
    private void onButtonAddPhaseClicked(ActionEvent event) {
	// TODO: update the phase numbers correctly
	log.fine("Add Tournament Phase Button was clicked");
	this.tournamentPhaseDialog.properties(new GamePhase())
		.onResult((result, returnValue) -> {
		    if (result == DialogResult.OK && returnValue != null) {
			this.loadedModule.getPhaseList().add(returnValue);
		    }
		}).show();
    }

    @FXML
    private void onButtonRemovePhaseClicked(ActionEvent event) {
	// TODO: update the phase numbers correctly
	log.fine("Remove Tournament Phase Button was clicked");
	this.loadedModule.getPhaseList().remove(this.getSelectedPhase());
    }

    @FXML
    private void onButtonEditPhaseClicked(ActionEvent event) {
	log.fine("Edit Tournament Phase Button was clicked");
	this.tournamentPhaseDialog.properties(this.getSelectedPhase())
		.onResult((result, returnValue) -> {
		    if (result == DialogResult.OK && returnValue != null) {
			// this.loadedTournament.getRuleSet().getPhaseList().add(returnValue);
		    }
		}).show();
    }

    private GamePhase getSelectedPhase() {
	return this.tableTournamentPhases.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void onButtonMoveScoreUpClicked(ActionEvent event) {

    }

    @FXML
    private void onButtonMoveScoreDownClicked(ActionEvent event) {

    }

    @FXML
    private void onButtonAddScoreClicked(ActionEvent event) {

    }

    @FXML
    private void onButtonRemoveScoreClicked(ActionEvent event) {

    }

    @FXML
    private void onButtonEditScoreClicked(ActionEvent event) {

    }
}

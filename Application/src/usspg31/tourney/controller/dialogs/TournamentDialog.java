package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import usspg31.tourney.controller.controls.UndoTextField;
import usspg31.tourney.model.GamePhase;
import usspg31.tourney.model.PossibleScoring;
import usspg31.tourney.model.Tournament;

public class TournamentDialog extends VBox implements IModalDialogProvider<Tournament, Tournament> {

	private static final Logger log = Logger.getLogger(TournamentDialog.class.getName());

	@FXML private UndoTextField textFieldTournamentTitle;

	@FXML private Button buttonLoadTournamentModule;
	@FXML private Button buttonEditTournamentModules;

	@FXML private TableView<GamePhase> tableTournamentPhases;
	private TableColumn<GamePhase, String> tableColumnPhasesPhaseNumber;
	private TableColumn<GamePhase, String> tableColumnPhasesPairingMethod;
	private TableColumn<GamePhase, String> tableColumnPhasesRoundCount;
	private TableColumn<GamePhase, String> tableColumnPhasesCutoff;
	private TableColumn<GamePhase, String> tableColumnPhasesRoundDuration;
	private TableColumn<GamePhase, String> tableColumnPhasesNumberOfOpponents;

	@FXML private Button buttonMoveTournamentPhaseUp;
	@FXML private Button buttonMoveTournamentPhaseDown;

	@FXML private Button buttonAddTournamentPhase;
	@FXML private Button buttonRemoveTournamentPhase;
	@FXML private Button buttonEditTournamentPhase;

	@FXML private TableView<PossibleScoring> tablePossibleScores;
	private TableColumn<PossibleScoring, String> tableColumnPossibleScoresPriority;
	private TableColumn<PossibleScoring, String> tableColumnPossibleScoresScores;

	@FXML private Button buttonMovePossibleScoreUp;
	@FXML private Button buttonMovePossibleScoreDown;

	@FXML private Button buttonAddPossibleScore;
	@FXML private Button buttonRemovePossibleScore;
	@FXML private Button buttonEditPossibleScore;

	private Tournament loadedTournament;

	public TournamentDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/fxml/dialogs/tournament-dialog.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@FXML private void initialize() {
		this.initTournamentPhaseTable();
		this.initPossibleScoresTable();
	}

	private void initTournamentPhaseTable() {
		// setup all table columns
		this.tableColumnPhasesPhaseNumber = new TableColumn<>("#");
		this.tableColumnPhasesPhaseNumber.cellValueFactoryProperty().set(
				cellData -> cellData.getValue().phaseNumberProperty().asString());
		this.tableTournamentPhases.getColumns().add(this.tableColumnPhasesPhaseNumber);

		this.tableColumnPhasesPairingMethod = new TableColumn<>("Paarungsmethode");
		// TODO: pairing method doesn't actually has a coded name I could access
		//		this.tableColumnPhasesPairingMethod.cellValueFactoryProperty().set(
		//				cellData -> cellData.getValue().pairingMethodProperty().nameProperty());

		this.tableColumnPhasesRoundCount = new TableColumn<>("Rundendanzahl");
		this.tableColumnPhasesRoundCount.cellValueFactoryProperty().set(
				cellData -> cellData.getValue().roundDurationProperty().asString());
		this.tableTournamentPhases.getColumns().add(this.tableColumnPhasesRoundCount);

		this.tableColumnPhasesCutoff = new TableColumn<>("Cutoff");
		this.tableColumnPhasesCutoff.cellValueFactoryProperty().set(
				cellData -> cellData.getValue().cutoffProperty().asString());
		this.tableTournamentPhases.getColumns().add(this.tableColumnPhasesCutoff);

		this.tableColumnPhasesRoundDuration = new TableColumn<>("Rundendauer");
		this.tableColumnPhasesRoundDuration.cellValueFactoryProperty().set(
				cellData -> cellData.getValue().roundDurationProperty().asString());
		this.tableTournamentPhases.getColumns().add(this.tableColumnPhasesRoundDuration);

		this.tableColumnPhasesNumberOfOpponents = new TableColumn<>("Spieler je Paarung");
		this.tableColumnPhasesNumberOfOpponents.cellValueFactoryProperty().set(
				cellData -> cellData.getValue().numberOfOpponentsProperty().asString());
		this.tableTournamentPhases.getColumns().add(this.tableColumnPhasesNumberOfOpponents);

		// add listeners to the selection property of the table to dis-/enable table buttons accordingly
		ReadOnlyIntegerProperty selectedIndex = this.tableTournamentPhases
				.getSelectionModel().selectedIndexProperty();
		ReadOnlyObjectProperty<GamePhase> selectedItem = this.tableTournamentPhases
				.getSelectionModel().selectedItemProperty();

		// only enable move-up button if an item other than the topmost is selected
		this.buttonMoveTournamentPhaseUp.disableProperty().bind(
				selectedIndex.isEqualTo(0)
				.or(selectedItem.isNull()));

		// only enable move-down button if an item other than the last one is selected
		// index < size - 1 && selected != null
		this.buttonMoveTournamentPhaseDown.disableProperty().bind(
				selectedIndex.greaterThanOrEqualTo(
						Bindings.size(this.tableTournamentPhases.getItems())
						.subtract(1))
						.or(selectedItem.isNull()));

		selectedIndex.greaterThanOrEqualTo(
				Bindings.size(this.tableTournamentPhases.getItems())
				.subtract(1))
				.or(selectedItem.isNull()).addListener((ov, o, n) -> {
					log.info("VALUE IS " + n);
				});

		selectedIndex.addListener((ov, o, n) -> {
			log.info("NEW SELECTED INDEX: " + n +
					" SIZE IS " + Bindings.size(this.tableTournamentPhases.getItems()).subtract(1).get());
		});

		// only enable remove button if an item is selected and there is more than one possible score
		this.buttonRemoveTournamentPhase.disableProperty().bind(
				selectedItem.isNull()
				.or(Bindings.size(this.tableTournamentPhases.getItems())
						.lessThanOrEqualTo(1)));

		// only enable edit button if an item is selected
		this.buttonEditTournamentPhase.disableProperty().bind(
				selectedItem.isNull());
	}

	private void initPossibleScoresTable() {
		this.tableColumnPossibleScoresPriority = new TableColumn<>("Priorität");
		this.tableColumnPossibleScoresPriority.cellValueFactoryProperty().set(
				cellData -> cellData.getValue().getPriority().asString());
		this.tablePossibleScores.getColumns().add(this.tableColumnPossibleScoresPriority);

		// TODO: somehow put the map in a string
		//		this.tableColumnPossibleScoresScores = new TableColumn<>("Wertungen");
		//		this.tableColumnPossibleScoresScores.cellValueFactoryProperty().set(
		//				cellData -> cellData.getValue().getScores().);
		//		this.tablePossibleScores.getColumns().add(this.tableColumnPossibleScoresPriority);


	}

	@Override
	public void setProperties(Tournament properties) {
		if (this.loadedTournament != null) {
			this.unloadTournament();
		}
		this.loadTournament(properties);
	}

	@Override
	public Tournament getReturnValue() {
		return this.loadedTournament;
	}

	@Override
	public void initModalDialog(ModalDialog<Tournament, Tournament> modalDialog) {
		modalDialog.title("Turniere").dialogButtons(DialogButtons.OK_CANCEL);
	}

	private void loadTournament(Tournament tournament) {
		log.fine("Loading Tournament");
		this.loadedTournament = tournament;
		this.textFieldTournamentTitle.textProperty().bindBidirectional(this.loadedTournament.nameProperty());

		if (tournament.getRuleSet().getPhaseList().size() == 0) {
			// at least one phase has to be set
			tournament.getRuleSet().getPhaseList().add(new GamePhase());
		}
		this.tableTournamentPhases.setItems(tournament.getRuleSet().getPhaseList());
		this.tablePossibleScores.setItems(tournament.getRuleSet().getPossibleScores());
		log.fine("Tournament loaded");
	}

	private void unloadTournament() {
		log.fine("Unloading Tournament");
		this.textFieldTournamentTitle.textProperty().unbindBidirectional(this.loadedTournament.nameProperty());
		log.fine("Tournament unloaded");
	}

	@FXML private void onButtonLoadTournamentModuleClicked(ActionEvent event) {

	}

	@FXML private void onButtonEditTournamentModulesClicked(ActionEvent event) {

	}

	@FXML private void onButtonMoveTournamentPhaseUpClicked(ActionEvent event) {
		log.fine("Move Tournament Phase Up Button was clicked");
		int selectedIndex = this.tableTournamentPhases.getSelectionModel().getSelectedIndex();
		int indexToSwap = selectedIndex - 1;
		GamePhase tmp = this.getSelectedTournamentPhase();
		ObservableList<GamePhase> phases = this.tableTournamentPhases.getItems();
		phases.set(selectedIndex, phases.get(indexToSwap));
		phases.set(indexToSwap, tmp);
		this.tableTournamentPhases.getSelectionModel().select(indexToSwap);
	}

	@FXML private void onButtonMoveTournamentPhaseDownClicked(ActionEvent event) {
		log.fine("Move Tournament Phase Down Button was clicked");
		int selectedIndex = this.tableTournamentPhases.getSelectionModel().getSelectedIndex();
		int indexToSwap = selectedIndex + 1;
		GamePhase tmp = this.getSelectedTournamentPhase();
		ObservableList<GamePhase> phases = this.tableTournamentPhases.getItems();
		phases.set(selectedIndex, phases.get(indexToSwap));
		phases.set(indexToSwap, tmp);
		this.tableTournamentPhases.getSelectionModel().select(indexToSwap);
	}

	@FXML private void onButtonAddTournamentPhaseClicked(ActionEvent event) {
		log.fine("Add Tournament Phase Button was clicked");
		new TournamentPhaseDialog().modalDialog()
		.properties(new GamePhase())
		.onResult((result, returnValue) -> {
			if (result == DialogResult.OK && returnValue != null) {
				this.loadedTournament.getRuleSet().getPhaseList().add(returnValue);
			}
		}).show();
	}

	@FXML private void onButtonRemoveTournamentPhaseClicked(ActionEvent event) {
		log.fine("Remove Tournament Phase Button was clicked");
		this.loadedTournament.getRuleSet().getPhaseList().remove(this.getSelectedTournamentPhase());
	}

	private GamePhase getSelectedTournamentPhase() {
		return this.tableTournamentPhases.getSelectionModel().getSelectedItem();
	}

	@FXML private void onButtonEditTournamentPhaseClicked(ActionEvent event) {

	}

	@FXML private void onButtonMovePossibleScoreUpClicked(ActionEvent event) {

	}

	@FXML private void onButtonMovePossibleScoreDownClicked(ActionEvent event) {

	}

	@FXML private void onButtonAddPossibleScoreClicked(ActionEvent event) {

	}

	@FXML private void onButtonRemovePossibleScoreClicked(ActionEvent event) {

	}

	@FXML private void onButtonEditPossibleScoreClicked(ActionEvent event) {

	}
}

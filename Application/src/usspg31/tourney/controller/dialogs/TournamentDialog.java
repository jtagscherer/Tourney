package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.controls.UndoTextField;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.DialogResult;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.controller.util.MapToStringBinding;
import usspg31.tourney.model.GamePhase;
import usspg31.tourney.model.PossibleScoring;
import usspg31.tourney.model.Tournament;
import usspg31.tourney.model.TournamentModule;

public class TournamentDialog extends VBox implements
	IModalDialogProvider<Tournament, Tournament> {

    private static final Logger log = Logger.getLogger(TournamentDialog.class
	    .getName());

    @FXML
    private UndoTextField textFieldTournamentTitle;

    @FXML
    private Button buttonLoadTournamentModule;
    @FXML
    private Button buttonEditTournamentModules;

    @FXML
    private TableView<GamePhase> tableTournamentPhases;
    private TableColumn<GamePhase, String> tableColumnPhasesPhaseNumber;
    private TableColumn<GamePhase, String> tableColumnPhasesPairingMethod;
    private TableColumn<GamePhase, String> tableColumnPhasesRoundCount;
    private TableColumn<GamePhase, String> tableColumnPhasesCutoff;
    private TableColumn<GamePhase, Duration> tableColumnPhasesRoundDuration;
    private TableColumn<GamePhase, String> tableColumnPhasesNumberOfOpponents;

    @FXML
    private Button buttonMoveTournamentPhaseUp;
    @FXML
    private Button buttonMoveTournamentPhaseDown;

    @FXML
    private Button buttonAddTournamentPhase;
    @FXML
    private Button buttonRemoveTournamentPhase;
    @FXML
    private Button buttonEditTournamentPhase;

    @FXML
    private TableView<PossibleScoring> tablePossibleScores;
    private TableColumn<PossibleScoring, String> tableColumnPossibleScoresPriority;
    private TableColumn<PossibleScoring, String> tableColumnPossibleScoresScores;

    @FXML
    private Button buttonMovePossibleScoreUp;
    @FXML
    private Button buttonMovePossibleScoreDown;

    @FXML
    private Button buttonAddPossibleScore;
    @FXML
    private Button buttonRemovePossibleScore;
    @FXML
    private Button buttonEditPossibleScore;

    private ModalDialog<ObservableList<TournamentModule>, Object> tournamentModuleListDialog;
    private ModalDialog<GamePhase, GamePhase> tournamentPhaseDialog;

    private Tournament loadedTournament;

    public TournamentDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
                    "/ui/fxml/dialogs/tournament-dialog.fxml"),
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
	this.tournamentModuleListDialog = new TournamentModuleListDialog()
		.modalDialog();
	this.tournamentPhaseDialog = new TournamentPhaseDialog().modalDialog();

	this.initTournamentPhaseTable();
	this.initPossibleScoresTable();
    }

    private void initTournamentPhaseTable() {
        PreferencesManager preferences = PreferencesManager.getInstance();

        // setup all table columns

        // tournament phase number
        this.tableColumnPhasesPhaseNumber = new TableColumn<>("#");
        this.tableColumnPhasesPhaseNumber.cellValueFactoryProperty().set(
                cellData -> cellData.getValue().phaseNumberProperty()
                .asString());
        this.tableTournamentPhases.getColumns().add(
                this.tableColumnPhasesPhaseNumber);

        // pairing method
        this.tableColumnPhasesPairingMethod = new TableColumn<>(
                preferences.localizeString("dialogs.tournament.pairingmethod"));
        this.tableColumnPhasesPairingMethod.cellValueFactoryProperty().set(
                cellData -> new SimpleStringProperty(cellData.getValue()
                        .getPairingMethod().getName()));
        this.tableTournamentPhases.getColumns().add(
                this.tableColumnPhasesPairingMethod);

        // round count
        this.tableColumnPhasesRoundCount = new TableColumn<>(
                preferences.localizeString("dialogs.tournament.roundcount"));
        this.tableColumnPhasesRoundCount.cellValueFactoryProperty()
        .set(cellData -> cellData.getValue().roundCountProperty()
                .asString());
        this.tableTournamentPhases.getColumns().add(
                this.tableColumnPhasesRoundCount);

        // cutoff
        this.tableColumnPhasesCutoff = new TableColumn<>(
                preferences.localizeString("dialogs.tournament.cutoff"));
        this.tableColumnPhasesCutoff.cellValueFactoryProperty().set(
                cellData -> cellData.getValue().cutoffProperty().asString());
        this.tableTournamentPhases.getColumns().add(
                this.tableColumnPhasesCutoff);

        // round duration
        this.tableColumnPhasesRoundDuration = new TableColumn<>(
                preferences.localizeString("dialogs.tournament.roundduration"));
        this.tableColumnPhasesRoundDuration
        .setCellValueFactory(cellData -> cellData.getValue()
                .roundDurationProperty());
        this.tableColumnPhasesRoundDuration.setCellFactory(column -> {
            return new TableCell<GamePhase, Duration>() {
                @Override
                protected void updateItem(Duration item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        this.setText(String.format("%02d:%02d " +
                                preferences.localizeString("dialogs.tournament.minutes"),
                                item.getSeconds() / 60, item.getSeconds() % 60));
                    }
                }
            };
        });
        this.tableTournamentPhases.getColumns().add(
                this.tableColumnPhasesRoundDuration);

        // number of opponents
        this.tableColumnPhasesNumberOfOpponents = new TableColumn<>(
                preferences.localizeString("dialogs.tournamentmodule.playersperpairing"));
        this.tableColumnPhasesNumberOfOpponents.cellValueFactoryProperty().set(
                cellData -> cellData.getValue().numberOfOpponentsProperty()
                .asString());
        this.tableTournamentPhases.getColumns().add(
                this.tableColumnPhasesNumberOfOpponents);
    }

    /**
     * Adds listeners to the selection property of the tournament phase table to
     * dis-/enable table buttons accordingly
     */
    private void bindTournamentPhaseTableButtons() {
	ReadOnlyIntegerProperty selectedIndex = this.tableTournamentPhases
		.getSelectionModel().selectedIndexProperty();
	ReadOnlyObjectProperty<GamePhase> selectedItem = this.tableTournamentPhases
		.getSelectionModel().selectedItemProperty();

	// only enable move-up button if an item other than the topmost is
	// selected
	this.buttonMoveTournamentPhaseUp.disableProperty().bind(
		selectedIndex.isEqualTo(0).or(selectedItem.isNull()));

	// only enable move-down button if an item other than the last
	// one is selected
	// index < size - 1 && selected != null
	this.buttonMoveTournamentPhaseDown.disableProperty().bind(
		selectedIndex.greaterThanOrEqualTo(
			Bindings.size(this.tableTournamentPhases.getItems())
			.subtract(1))
			.or(selectedItem.isNull()));

	// only enable remove button if an item is selected and there is more
	// than one possible score
	this.buttonRemoveTournamentPhase.disableProperty().bind(
		selectedItem.isNull().or(
			Bindings.size(this.tableTournamentPhases.getItems())
			.lessThanOrEqualTo(1)));

	// only enable edit button if an item is selected
	this.buttonEditTournamentPhase.disableProperty().bind(
		selectedItem.isNull());
    }

    private void unbindTournamentPhaseTableButtons() {
	this.buttonMoveTournamentPhaseUp.disableProperty().unbind();
	this.buttonMoveTournamentPhaseDown.disableProperty().unbind();
	this.buttonRemoveTournamentPhase.disableProperty().unbind();
	this.buttonEditTournamentPhase.disableProperty().unbind();
    }

    private void initPossibleScoresTable() {
        PreferencesManager preferences = PreferencesManager.getInstance();

        this.tableColumnPossibleScoresPriority = new TableColumn<>(
                preferences.localizeString("dialogs.tournament.priority"));
        this.tableColumnPossibleScoresPriority.cellValueFactoryProperty().set(
                cellData -> cellData.getValue().priorityProperty().asString());
        this.tablePossibleScores.getColumns().add(
                this.tableColumnPossibleScoresPriority);

        this.tableColumnPossibleScoresScores = new TableColumn<>(
                preferences.localizeString("dialogs.tournament.scores"));
        this.tableColumnPossibleScoresScores
        .setCellValueFactory(cellData -> new MapToStringBinding<>(
                cellData.getValue().getScores()).getStringProperty());
        this.tablePossibleScores.getColumns().add(
                this.tableColumnPossibleScoresScores);
    }



    /**
     * Adds listeners to the selection property of the possible scores table to
     * dis-/enable table buttons accordingly
     */
    private void bindPossibleScoresTableButtons() {
	ReadOnlyIntegerProperty selectedIndex = this.tablePossibleScores
		.getSelectionModel().selectedIndexProperty();
	ReadOnlyObjectProperty<PossibleScoring> selectedItem = this.tablePossibleScores
		.getSelectionModel().selectedItemProperty();

	// only enable move-up button if an item other than the topmost is
	// selected
	this.buttonMovePossibleScoreUp.disableProperty().bind(
		selectedIndex.isEqualTo(0).or(selectedItem.isNull()));

	// only enable move-down button if an item other than the last one is
	// selected
	// index < size - 1 && selected != null
	this.buttonMovePossibleScoreDown.disableProperty().bind(
		selectedIndex.greaterThanOrEqualTo(
			Bindings.size(this.tableTournamentPhases.getItems())
			.subtract(1)).or(selectedItem.isNull()));

	// only enable remove button if an item is selected and there is more
	// than one possible score
	this.buttonRemovePossibleScore.disableProperty().bind(
		selectedItem.isNull().or(
			Bindings.size(this.tableTournamentPhases.getItems())
			.lessThanOrEqualTo(1)));

	// only enable edit button if an item is selected
	this.buttonEditPossibleScore.disableProperty().bind(
		selectedItem.isNull());
    }

    private void unbindPossibleScoresTableButtons() {
	this.buttonMovePossibleScoreUp.disableProperty().unbind();
	this.buttonMovePossibleScoreDown.disableProperty().unbind();
	this.buttonRemovePossibleScore.disableProperty().unbind();
	this.buttonAddPossibleScore.disableProperty().unbind();
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
    public boolean hasNoInput() {
	return this.loadedTournament.getName().equals("");
    }

    @Override
    public void initModalDialog(ModalDialog<Tournament, Tournament> modalDialog) {
        modalDialog.title("dialogs.tournament").dialogButtons(DialogButtons.OK_CANCEL);
    }



    private void loadTournament(Tournament tournament) {
	log.fine("Loading Tournament");
	this.loadedTournament = tournament;
	this.textFieldTournamentTitle.textProperty().bindBidirectional(
		this.loadedTournament.nameProperty());

	if (tournament.getRuleSet().getPhaseList().size() == 0) {
	    // at least one phase has to be set
	    tournament.getRuleSet().getPhaseList().add(new GamePhase());
	}
	this.tableTournamentPhases.setItems(tournament.getRuleSet()
		.getPhaseList());
	this.tablePossibleScores.setItems(tournament.getRuleSet()
		.getPossibleScores());

	this.bindTournamentPhaseTableButtons();
	this.bindPossibleScoresTableButtons();

	log.fine("Tournament loaded");
    }

    private void unloadTournament() {
	log.fine("Unloading Tournament");
	this.textFieldTournamentTitle.textProperty().unbindBidirectional(
		this.loadedTournament.nameProperty());

	this.unbindTournamentPhaseTableButtons();
	this.unbindPossibleScoresTableButtons();

	log.fine("Tournament unloaded");
    }



    @FXML
    private void onButtonLoadTournamentModuleClicked(ActionEvent event) {
	// TODO: show all available modules and let the use choose one
    }

    @FXML
    private void onButtonEditTournamentModulesClicked(ActionEvent event) {
	log.fine("Edit Tournament Modules Button was clicked");

	// TODO: actually load tournament modules (-> preferencesManager?)
	this.tournamentModuleListDialog.properties(null).show();
    }



    @FXML
    private void onButtonMoveTournamentPhaseUpClicked(ActionEvent event) {
	log.fine("Move Tournament Phase Up Button was clicked");
	int selectedIndex = this.getSelectedTournamentPhaseIndex();
	this.swapGamePhases(selectedIndex, selectedIndex - 1);
    }

    @FXML
    private void onButtonMoveTournamentPhaseDownClicked(ActionEvent event) {
	log.fine("Move Tournament Phase Down Button was clicked");
	int selectedIndex = this.getSelectedTournamentPhaseIndex();
	this.swapGamePhases(selectedIndex, selectedIndex + 1);
    }

    /**
     * Swaps the game phases with the given indices and selects the row with
     * indexB.
     */
    private void swapGamePhases(int indexA, int indexB) {
	log.finer("Swapping gamephases with indices " + indexA + " and " + indexB);
	ObservableList<GamePhase> phases = this.tableTournamentPhases.getItems();

	// swap phase numbers
	int tmpId = phases.get(indexA).getPhaseNumber();
	phases.get(indexA).setPhaseNumber(phases.get(indexB).getPhaseNumber());
	phases.get(indexB).setPhaseNumber(tmpId);

	// swap the actual items in the list
	Collections.swap(phases, indexA, indexB);

	// select the previously selected item which is now at a different index
	this.tableTournamentPhases.getSelectionModel().select(indexB);
    }

    @FXML
    private void onButtonAddTournamentPhaseClicked(ActionEvent event) {
	log.fine("Add Tournament Phase Button was clicked");
	GamePhase newGamePhase = new GamePhase();
	newGamePhase.setPhaseNumber(this.tableTournamentPhases.getItems().size());

	this.tournamentPhaseDialog
		.properties(newGamePhase)
		.onResult((result, returnValue) -> {
		    if (result == DialogResult.OK
			    && returnValue != null) {
			this.loadedTournament.getRuleSet()
			.getPhaseList().add(returnValue);
		    }
		}).show();
    }

    @FXML
    private void onButtonRemoveTournamentPhaseClicked(ActionEvent event) {
	log.fine("Remove Tournament Phase Button was clicked");

	// update indices of all following GamePhases
	int selectedTournamentPhase = this.getSelectedTournamentPhaseIndex();
	int itemCount = this.tableTournamentPhases.getItems().size();
	ObservableList<GamePhase> phases = this.tableTournamentPhases.getItems();
	for (int i = selectedTournamentPhase + 1; i < itemCount; i++) {
	    phases.get(i).setPhaseNumber(i - 1);
	}

	// actually remove the selected GamePhase
	this.loadedTournament.getRuleSet().getPhaseList().remove(selectedTournamentPhase);
    }


    @FXML
    private void onButtonEditTournamentPhaseClicked(ActionEvent event) {
	log.fine("Edit Tournament Phase Button was clicked");
	this.tournamentPhaseDialog
		.properties(this.getSelectedTournamentPhase())
		.onResult((result, returnValue) -> {
		    if (result == DialogResult.OK && returnValue != null) {
			// this.loadedTournament.getRuleSet().getPhaseList().add(returnValue);
		    }
		}).show();
    }

    /**
     * @return the currently selected GamePhase in the tournament phase table
     */
    private GamePhase getSelectedTournamentPhase() {
	return this.tableTournamentPhases.getSelectionModel().getSelectedItem();
    }

    /**
     * @return the index of the currently selected row in the tournament phase table
     */

    private int getSelectedTournamentPhaseIndex() {
	return this.tableTournamentPhases.getSelectionModel().getSelectedIndex();
    }




    @FXML
    private void onButtonMovePossibleScoreUpClicked(ActionEvent event) {
	log.fine("Move Possible Score Up Button was clicked");
	int selectedIndex = this.getSelectedPossibleScoreIndex();
	this.swapPossibleScores(selectedIndex, selectedIndex - 1);
    }

    @FXML
    private void onButtonMovePossibleScoreDownClicked(ActionEvent event) {
	log.fine("Move Possible Score Down Button was clicked");
	int selectedIndex = this.getSelectedPossibleScoreIndex();
	this.swapPossibleScores(selectedIndex, selectedIndex + 1);
    }

    /**
     * Swaps the game phases with the given indices and selects the row with
     * indexB.
     */
    private void swapPossibleScores(int indexA, int indexB) {
	log.finer("Swapping possible scores with indices " + indexA + " and " + indexB);
	ObservableList<PossibleScoring> scores = this.tablePossibleScores.getItems();

	// swap phase numbers
	int tmpId = scores.get(indexA).getPriority();
	scores.get(indexA).setPriority(scores.get(indexB).getPriority());
	scores.get(indexB).setPriority(tmpId);

	// swap the actual items in the list
	Collections.swap(scores, indexA, indexB);

	// select the previously selected item which is now at a different index
	this.tablePossibleScores.getSelectionModel().select(indexB);
    }

    @FXML
    private void onButtonAddPossibleScoreClicked(ActionEvent event) {

    }

    @FXML
    private void onButtonRemovePossibleScoreClicked(ActionEvent event) {
	log.fine("Remove Possible Score Button was clicked");

	// update indices of all following PossibleScores
	int selectedPossibleScore = this.getSelectedPossibleScoreIndex();
	int itemCount = this.tablePossibleScores.getItems().size();
	ObservableList<PossibleScoring> phases = this.tablePossibleScores.getItems();
	for (int i = selectedPossibleScore + 1; i < itemCount; i++) {
	    phases.get(i).setPriority(i - 1);
	}

	// actually remove the selected PossibleScore
	this.loadedTournament.getRuleSet().getPossibleScores().remove(selectedPossibleScore);
    }

    @FXML
    private void onButtonEditPossibleScoreClicked(ActionEvent event) {

    }

    /**
     * @return the currently selected GamePhase in the tournament phase table
     */
    private PossibleScoring getSelectedPossibleScore() {
	return this.tablePossibleScores.getSelectionModel().getSelectedItem();
    }

    /**
     * @return the index of the currently selected row in the tournament phase table
     */
    private int getSelectedPossibleScoreIndex() {
	return this.tablePossibleScores.getSelectionModel().getSelectedIndex();
    }
}

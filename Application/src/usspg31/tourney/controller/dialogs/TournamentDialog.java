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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.controls.UndoTextField;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.DialogResult;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.controller.dialogs.modal.SimpleDialog;
import usspg31.tourney.controller.util.MapToStringBinding;
import usspg31.tourney.model.GamePhase;
import usspg31.tourney.model.PossibleScoring;
import usspg31.tourney.model.Tournament;
import usspg31.tourney.model.TournamentAdministrator;
import usspg31.tourney.model.TournamentModule;

public class TournamentDialog extends VBox implements
        IModalDialogProvider<Tournament, Tournament> {

    private static final Logger log = Logger.getLogger(TournamentDialog.class
            .getName());

    @FXML private UndoTextField textFieldTournamentTitle;

    @FXML private Button buttonEditAdministrators;
    @FXML private Button buttonLoadTournamentModule;
    @FXML private Button buttonEditTournamentModules;

    @FXML private TableView<GamePhase> tableTournamentPhases;
    private TableColumn<GamePhase, String> tableColumnPhasesPhaseNumber;
    private TableColumn<GamePhase, String> tableColumnPhasesPairingMethod;
    private TableColumn<GamePhase, String> tableColumnPhasesRoundCount;
    private TableColumn<GamePhase, String> tableColumnPhasesCutoff;
    private TableColumn<GamePhase, Duration> tableColumnPhasesRoundDuration;
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

    private final ModalDialog<ObservableList<TournamentModule>, Object> tournamentModuleListDialog;
    private final TournamentModuleListDialog tournamentModuleListDialogController;

    private final ModalDialog<GamePhase, GamePhase> tournamentPhaseDialog;
    private final TournamentPhaseDialog tournamentPhaseDialogController;

    private final ModalDialog<PossibleScoring, PossibleScoring> possibleScoringDialog;
    private final TournamentScoringDialog possibleScoringDialogController;

    private final ModalDialog<ObservableList<TournamentAdministrator>, Object> tournamentAdministratorListDialog;
    private final TournamentAdministratorListDialog tournamentAdministratorListDialogController;

    private final ModalDialog<ObservableList<TournamentModule>, TournamentModule> tournamentModuleSelectionDialog;
    private final TournamentModuleSelectionDialog tournamentModuleSelectionDialogController;

    private Tournament loadedTournament;

    public TournamentDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
                    "/ui/fxml/dialogs/tournament-dialog.fxml"),
                    PreferencesManager.getInstance().getSelectedLanguage()
                            .getLanguageBundle());
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        this.tournamentModuleListDialogController = new TournamentModuleListDialog();
        this.tournamentPhaseDialogController = new TournamentPhaseDialog();
        this.possibleScoringDialogController = new TournamentScoringDialog();
        this.tournamentAdministratorListDialogController = new TournamentAdministratorListDialog();
        this.tournamentModuleSelectionDialogController = new TournamentModuleSelectionDialog();

        this.tournamentModuleListDialog = this.tournamentModuleListDialogController
                .modalDialog();
        this.tournamentPhaseDialog = this.tournamentPhaseDialogController
                .modalDialog();
        this.possibleScoringDialog = this.possibleScoringDialogController
                .modalDialog();
        this.tournamentAdministratorListDialog = this.tournamentAdministratorListDialogController
                .modalDialog();
        this.tournamentModuleSelectionDialog = this.tournamentModuleSelectionDialogController
                .modalDialog();
    }

    @FXML
    private void initialize() {
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
                        this.setText(String.format(
                                "%d:%02d "
                                        + preferences
                                                .localizeString("dialogs.tournament.minutes"),
                                item.getSeconds() / 60, item.getSeconds() % 60));
                    } else {
                        this.setText("");
                    }
                }
            };
        });
        this.tableTournamentPhases.getColumns().add(
                this.tableColumnPhasesRoundDuration);

        // number of opponents
        this.tableColumnPhasesNumberOfOpponents = new TableColumn<>(
                preferences
                        .localizeString("dialogs.tournamentmodule.playersperpairing"));
        this.tableColumnPhasesNumberOfOpponents.cellValueFactoryProperty().set(
                cellData -> cellData.getValue().numberOfOpponentsProperty()
                        .asString());
        this.tableTournamentPhases.getColumns().add(
                this.tableColumnPhasesNumberOfOpponents);

        /* Edit the tournament phase on double click */
        this.tableTournamentPhases.setRowFactory(tableView -> {
            TableRow<GamePhase> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    this.editTournamentPhase(row.getItem());
                }
            });
            return row;
        });

        this.tableTournamentPhases.setPlaceholder(new Text(PreferencesManager
                .getInstance().localizeString(
                        "tableplaceholder.notournamentphases")));
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
                                .subtract(1)).or(selectedItem.isNull()));

        // only enable remove button if an item is selected
        this.buttonRemoveTournamentPhase.disableProperty().bind(
                selectedItem.isNull());

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

        /* Edit the possible score on double click */
        this.tablePossibleScores.setRowFactory(tableView -> {
            TableRow<PossibleScoring> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    this.editPossibleScoring(row.getItem());
                }
            });
            return row;
        });

        this.tablePossibleScores.setPlaceholder(new Text(PreferencesManager
                .getInstance().localizeString("tableplaceholder.noscorings")));
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
                        Bindings.size(this.tablePossibleScores.getItems())
                                .subtract(1)).or(selectedItem.isNull()));

        // only enable remove button if an item is selected and there is more
        // than one possible score
        this.buttonRemovePossibleScore.disableProperty().bind(
                selectedItem.isNull());

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
        this.loadTournament((Tournament) properties.clone());
    }

    @Override
    public Tournament getReturnValue() {
        return this.loadedTournament;
    }

    @Override
    public String getInputErrorString() {
        if (this.loadedTournament.getName().equals("")) {
            return PreferencesManager.getInstance().localizeString(
                    "dialogs.tournament.errors.emptydata");
        }
        if (this.loadedTournament.getRuleSet().getPossibleScores().size() == 0) {
            return PreferencesManager.getInstance().localizeString(
                    "dialogs.tournament.errors.noscorings");
        }
        if (this.loadedTournament.getRuleSet().getPhaseList().size() == 0) {
            return PreferencesManager.getInstance().localizeString(
                    "dialogs.tournament.errors.nophases");
        }

        return null;
    }

    @Override
    public void initModalDialog(ModalDialog<Tournament, Tournament> modalDialog) {
        modalDialog.title("dialogs.tournament").dialogButtons(
                DialogButtons.OK_CANCEL);
    }

    private void loadTournament(Tournament tournament) {
        log.fine("Loading Tournament");
        if (this.loadedTournament != null) {
            this.unloadTournament();
        }

        this.loadedTournament = tournament;
        this.textFieldTournamentTitle.textProperty().bindBidirectional(
                this.loadedTournament.nameProperty());

        this.tableTournamentPhases.setItems(this.loadedTournament.getRuleSet()
                .getPhaseList());
        this.tablePossibleScores.setItems(this.loadedTournament.getRuleSet()
                .getPossibleScores());

        this.bindTournamentPhaseTableButtons();
        this.bindPossibleScoresTableButtons();

        log.fine("Tournament loaded");
    }

    private void unloadTournament() {
        log.fine("Unloading Tournament");

        /* Unbind the tournament title text field */
        this.textFieldTournamentTitle.textProperty().unbindBidirectional(
                this.loadedTournament.nameProperty());

        /* Unbind all tables */
        this.tableTournamentPhases.getSelectionModel().clearSelection();
        this.tablePossibleScores.getSelectionModel().clearSelection();

        /* Unbind all buttons */
        this.unbindTournamentPhaseTableButtons();
        this.unbindPossibleScoresTableButtons();

        /* Unbind all properties of dialogs */
        this.tournamentModuleListDialogController.unloadTournamentModuleList();
        this.tournamentPhaseDialogController.unloadGamePhase();
        this.possibleScoringDialogController.unloadPossibleScoring();
        this.tournamentAdministratorListDialogController
                .unloadTournamentAdministratorList();

        log.fine("Tournament unloaded");
    }

    @FXML
    private void onButtonEditAdministratorsClicked(ActionEvent event) {
        this.tournamentAdministratorListDialog.properties(
                this.loadedTournament.getAdministrators()).show();
    }

    @FXML
    private void onButtonLoadTournamentModuleClicked(ActionEvent event) {
        this.tournamentModuleSelectionDialog
                .properties(
                        PreferencesManager.getInstance()
                                .loadTournamentModules())
                .onResult(
                        (result, returnValue) -> {
                            if (result == DialogResult.OK
                                    && returnValue != null) {
                                this.loadedTournament
                                        .setRuleSet((TournamentModule) returnValue
                                                .clone());
                                this.loadTournament(this.loadedTournament);
                            }
                        }).show();
    }

    @FXML
    private void onButtonEditTournamentModulesClicked(ActionEvent event) {
        log.fine("Edit Tournament Modules Button was clicked");

        this.tournamentModuleListDialog.properties(
                PreferencesManager.getInstance().loadTournamentModules())
                .show();
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
        log.finer("Swapping gamephases with indices " + indexA + " and "
                + indexB);
        ObservableList<GamePhase> phases = this.tableTournamentPhases
                .getItems();

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
        newGamePhase.setPhaseNumber(this.tableTournamentPhases.getItems()
                .size());

        this.tournamentPhaseDialog
                .properties(newGamePhase)
                .onResult(
                        (result, returnValue) -> {
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

        new SimpleDialog<>(PreferencesManager.getInstance().localizeString(
                "dialogs.tournament.dialogs.deletephase.message"))
                .modalDialog()
                .dialogButtons(DialogButtons.YES_NO)
                .title("dialogs.tournament.dialogs.deletephase.title")
                .onResult((result, returnValue) -> {
                    if (result == DialogResult.YES) {
                        // update indices of all following GamePhases
                        int selectedTournamentPhase = this
                                .getSelectedTournamentPhaseIndex();
                        int itemCount = this.tableTournamentPhases.getItems()
                                .size();
                        ObservableList<GamePhase> phases = this.tableTournamentPhases
                                .getItems();
                        for (int i = selectedTournamentPhase + 1; i < itemCount; i++) {
                            phases.get(i).setPhaseNumber(i - 1);
                        }

                        // actually remove the selected GamePhase
                        this.loadedTournament.getRuleSet().getPhaseList()
                                .remove(selectedTournamentPhase);
                    }
                }).show();
    }

    @FXML
    private void onButtonEditTournamentPhaseClicked(ActionEvent event) {
        log.fine("Edit Tournament Phase Button was clicked");
        this.editTournamentPhase(this.getSelectedTournamentPhase());
    }

    /**
     * Open a dialog to edit the given tournament phase
     * 
     * @param selectedTournamentPhase
     *            Tournament phase to be edited
     */
    private void editTournamentPhase(GamePhase selectedTournamentPhase) {
        this.tournamentPhaseDialog
                .properties(selectedTournamentPhase)
                .onResult(
                        (result, returnValue) -> {
                            if (result == DialogResult.OK
                                    && returnValue != null) {
                                this.loadedTournament.getRuleSet()
                                        .getPhaseList()
                                        .remove(selectedTournamentPhase);
                                this.loadedTournament.getRuleSet()
                                        .getPhaseList().add(returnValue);
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
     * @return the index of the currently selected row in the tournament phase
     *         table
     */
    private int getSelectedTournamentPhaseIndex() {
        return this.tableTournamentPhases.getSelectionModel()
                .getSelectedIndex();
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
        log.finer("Swapping possible scores with indices " + indexA + " and "
                + indexB);
        ObservableList<PossibleScoring> scores = this.tablePossibleScores
                .getItems();

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
        log.fine("Add Possible Score Button was clicked");

        this.possibleScoringDialog
                .properties(new PossibleScoring())
                .onResult(
                        (result, value) -> {
                            if (result == DialogResult.OK) {
                                value.setPriority(this.tablePossibleScores
                                        .getItems().size());
                                this.tablePossibleScores.getItems().add(value);
                            }
                        }).show();
    }

    @FXML
    private void onButtonRemovePossibleScoreClicked(ActionEvent event) {
        log.fine("Remove Possible Score Button was clicked");

        new SimpleDialog<>(PreferencesManager.getInstance().localizeString(
                "dialogs.tournament.dialogs.deletescoring.message"))
                .modalDialog()
                .dialogButtons(DialogButtons.YES_NO)
                .title("dialogs.tournament.dialogs.deletescoring.title")
                .onResult((result, returnValue) -> {
                    if (result == DialogResult.YES) {
                        // update indices of all following PossibleScores
                        int selectedPossibleScore = this
                                .getSelectedPossibleScoreIndex();
                        int itemCount = this.tablePossibleScores.getItems()
                                .size();
                        ObservableList<PossibleScoring> phases = this.tablePossibleScores
                                .getItems();
                        for (int i = selectedPossibleScore + 1; i < itemCount; i++) {
                            phases.get(i).setPriority(i - 1);
                        }

                        // actually remove the selected PossibleScore
                        this.loadedTournament.getRuleSet().getPossibleScores()
                                .remove(selectedPossibleScore);
                    }
                }).show();
    }

    @FXML
    private void onButtonEditPossibleScoreClicked(ActionEvent event) {
        log.fine("Edit Possible Score Button was clicked");

        PossibleScoring selectedScoring = this.getSelectedPossibleScore();
        this.editPossibleScoring(selectedScoring);
    }

    /**
     * Open a dialog to edit the given possible scoring
     * 
     * @param selectedScoring
     *            Possible scoring to be edited
     */
    private void editPossibleScoring(PossibleScoring selectedScoring) {
        this.possibleScoringDialog
                .properties(selectedScoring)
                .onResult(
                        (result, value) -> {
                            if (result == DialogResult.OK) {
                                this.tablePossibleScores.getItems().remove(
                                        selectedScoring);
                                this.tablePossibleScores.getItems().add(value);
                            }
                        }).show();
    }

    /**
     * @return the currently selected GamePhase in the tournament phase table
     */
    private PossibleScoring getSelectedPossibleScore() {
        return this.tablePossibleScores.getSelectionModel().getSelectedItem();
    }

    /**
     * @return the index of the currently selected row in the tournament phase
     *         table
     */
    private int getSelectedPossibleScoreIndex() {
        return this.tablePossibleScores.getSelectionModel().getSelectedIndex();
    }
}

package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
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
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.controls.UndoTextArea;
import usspg31.tourney.controller.controls.UndoTextField;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.DialogResult;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.controller.dialogs.modal.SimpleDialog;
import usspg31.tourney.controller.util.ScoringToStringBinding;
import usspg31.tourney.model.GamePhase;
import usspg31.tourney.model.PossibleScoring;
import usspg31.tourney.model.TournamentModule;

public class TournamentModuleEditorDialog extends SplitPane implements
        IModalDialogProvider<Object, TournamentModule> {

    private static final Logger log = Logger
            .getLogger(TournamentModuleEditorDialog.class.getName());

    @FXML private UndoTextField textFieldModuleTitle;
    @FXML private UndoTextArea textAreaDescription;

    @FXML private TableView<GamePhase> tableTournamentPhases;
    private TableColumn<GamePhase, String> tableColumnPhasesPhaseNumber;
    private TableColumn<GamePhase, String> tableColumnPhasesPairingMethod;
    private TableColumn<GamePhase, String> tableColumnPhasesRoundCount;
    private TableColumn<GamePhase, String> tableColumnPhasesCutoff;
    private TableColumn<GamePhase, Duration> tableColumnPhasesRoundDuration;
    private TableColumn<GamePhase, String> tableColumnPhasesNumberOfOpponents;

    @FXML private Button buttonMovePhaseUp;
    @FXML private Button buttonMovePhaseDown;
    @FXML private Button buttonAddPhase;
    @FXML private Button buttonRemovePhase;
    @FXML private Button buttonEditPhase;

    @FXML private TableView<PossibleScoring> tablePossibleScores;
    private TableColumn<PossibleScoring, String> tableColumnPossibleScoresPriority;
    private TableColumn<PossibleScoring, String> tableColumnPossibleScoresScores;

    @FXML private Button buttonMoveScoreUp;
    @FXML private Button buttonMoveScoreDown;
    @FXML private Button buttonAddScore;
    @FXML private Button buttonRemoveScore;
    @FXML private Button buttonEditScore;

    private final ModalDialog<GamePhase, GamePhase> tournamentPhaseDialog;
    private final TournamentPhaseDialog tournamentPhaseDialogController;

    private final ModalDialog<PossibleScoring, PossibleScoring> possibleScoringDialog;
    private final TournamentScoringDialog possibleScoringDialogController;

    private TournamentModule loadedModule;
    private ArrayList<String> existingTournamentModuleNames;

    public TournamentModuleEditorDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
                    "/ui/fxml/dialogs/tournament-module-editor-dialog.fxml"),
                    PreferencesManager.getInstance().getSelectedLanguage()
                            .getLanguageBundle());
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        this.tournamentPhaseDialogController = new TournamentPhaseDialog();
        this.tournamentPhaseDialog = this.tournamentPhaseDialogController
                .modalDialog();

        this.possibleScoringDialogController = new TournamentScoringDialog();
        this.possibleScoringDialog = this.possibleScoringDialogController
                .modalDialog();
    }

    @FXML
    private void initialize() {
        this.existingTournamentModuleNames = new ArrayList<String>();

        this.initTournamentPhaseTable();
        this.initPossibleScoresTable();
    }

    private void initTournamentPhaseTable() {
        PreferencesManager preferences = PreferencesManager.getInstance();

        // setup all table columns
        this.tableColumnPhasesPhaseNumber = new TableColumn<>("#");
        this.tableColumnPhasesPhaseNumber.cellValueFactoryProperty().set(
                cellData -> cellData.getValue().phaseNumberProperty()
                        .asString());
        this.tableTournamentPhases.getColumns().add(
                this.tableColumnPhasesPhaseNumber);

        this.tableColumnPhasesPairingMethod = new TableColumn<>(
                PreferencesManager.getInstance().localizeString(
                        "dialogs.tournamentmodule.pairingmethod"));
        this.tableColumnPhasesPairingMethod.cellValueFactoryProperty().set(
                cellData -> new SimpleStringProperty(cellData.getValue()
                        .getPairingMethod().getName()));
        this.tableTournamentPhases.getColumns().add(
                this.tableColumnPhasesPairingMethod);

        this.tableColumnPhasesRoundCount = new TableColumn<>(
                preferences
                        .localizeString("dialogs.tournamentmodule.roundcount"));
        this.tableColumnPhasesRoundCount.cellValueFactoryProperty()
                .set(cellData -> cellData.getValue().roundCountProperty()
                        .asString());
        this.tableTournamentPhases.getColumns().add(
                this.tableColumnPhasesRoundCount);

        this.tableColumnPhasesCutoff = new TableColumn<>(
                preferences.localizeString("dialogs.tournamentmodule.cutoff"));
        this.tableColumnPhasesCutoff.cellValueFactoryProperty().set(
                cellData -> cellData.getValue().cutoffProperty().asString());
        this.tableTournamentPhases.getColumns().add(
                this.tableColumnPhasesCutoff);

        this.tableColumnPhasesRoundDuration = new TableColumn<>(
                preferences
                        .localizeString("dialogs.tournamentmodule.roundduration"));
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
                                                .localizeString("dialogs.tournamentmodule.minutes"),
                                item.getSeconds() / 60, item.getSeconds() % 60));
                    }
                }
            };
        });
        this.tableTournamentPhases.getColumns().add(
                this.tableColumnPhasesRoundDuration);

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

    private void initPossibleScoresTable() {
        PreferencesManager preferences = PreferencesManager.getInstance();

        this.tableColumnPossibleScoresPriority = new TableColumn<>(
                preferences.localizeString("dialogs.tournamentmodule.priority"));
        this.tableColumnPossibleScoresPriority.cellValueFactoryProperty().set(
                cellData -> cellData.getValue().priorityProperty().asString());
        this.tablePossibleScores.getColumns().add(
                this.tableColumnPossibleScoresPriority);

        this.tableColumnPossibleScoresScores = new TableColumn<>(
                preferences.localizeString("dialogs.tournamentmodule.scores"));
        this.tableColumnPossibleScoresScores.cellValueFactoryProperty().set(
                cellData -> new SimpleStringProperty(""));
        this.tablePossibleScores.getColumns().add(
                this.tableColumnPossibleScoresScores);
        // this.tableColumnPossibleScoresScores
        // .setCellValueFactory(cellValue -> new MapToStringBinding<>(
        // cellValue.getValue().getScores()).getStringProperty());
        this.tableColumnPossibleScoresScores
                .setCellValueFactory(cellValue -> new ScoringToStringBinding(
                        cellValue.getValue()));

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

    @SuppressWarnings("unchecked")
    @Override
    public void setProperties(Object properties) {
        if (properties instanceof TournamentModule) {
            this.loadModule((TournamentModule) properties);
        } else if (properties instanceof ObservableList<?>) {
            if (this.loadedModule == null) {
                throw new IllegalStateException(
                        "The tournament module has to be set before the list of existing tournament module names.");
            }

            this.existingTournamentModuleNames.clear();
            for (TournamentModule module : (ObservableList<TournamentModule>) properties) {
                if (!this.loadedModule.getName().equals(module.getName())) {
                    this.existingTournamentModuleNames.add(module.getName());
                }
            }
        }
    }

    @Override
    public TournamentModule getReturnValue() {
        return this.loadedModule;
    }

    @Override
    public String getInputErrorString() {
        if (this.loadedModule.getName().equals("")) {
            return PreferencesManager.getInstance().localizeString(
                    "dialogs.tournamentmodule.errors.emptydata");
        }
        if (this.loadedModule.getPossibleScores().size() == 0) {
            return PreferencesManager.getInstance().localizeString(
                    "dialogs.tournamentmodule.errors.noscorings");
        }
        if (this.loadedModule.getPhaseList().size() == 0) {
            return PreferencesManager.getInstance().localizeString(
                    "dialogs.tournamentmodule.errors.nophases");
        }

        /* Check for another tournament module with the same name */
        boolean duplicateModuleNames = false;
        for (String moduleName : this.existingTournamentModuleNames) {
            if (this.textFieldModuleTitle.getText().equals(moduleName)) {
                duplicateModuleNames = true;
                break;
            }
        }
        if (duplicateModuleNames) {
            return PreferencesManager.getInstance().localizeString(
                    "dialogs.tournamentmodule.errors.duplicatename");
        }

        return null;
    };

    @Override
    public void initModalDialog(
            ModalDialog<Object, TournamentModule> modalDialog) {
        modalDialog.title("dialogs.tournamentmoduleeditor").dialogButtons(
                DialogButtons.OK_CANCEL);
    }

    private void loadModule(TournamentModule module) {
        log.fine("Loading Tournament Module");
        if (this.loadedModule != null) {
            this.unloadModule();
        }

        this.loadedModule = (TournamentModule) module.clone();

        this.textFieldModuleTitle.textProperty().bindBidirectional(
                this.loadedModule.nameProperty());
        this.textAreaDescription.textProperty().bindBidirectional(
                this.loadedModule.descriptionProperty());

        this.tableTournamentPhases.setItems(this.loadedModule.getPhaseList());
        this.tablePossibleScores
                .setItems(this.loadedModule.getPossibleScores());

        this.bindTournamentPhaseButtons();
        this.bindPossibleScoringButtons();

        log.fine("Tournament Module loaded");
    }

    private void bindTournamentPhaseButtons() {
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

        // only enable remove button if an item is selected
        this.buttonRemovePhase.disableProperty().bind(selectedPhase.isNull());

        // only enable edit button if an item is selected
        this.buttonEditPhase.disableProperty().bind(selectedPhase.isNull());

        // TODO: add bindings for the possible score table's buttons (see
        // TournamentDialog#loadTournament())
    }

    private void unbindTournamentPhaseButtons() {
        this.buttonMovePhaseUp.disableProperty().unbind();
        this.buttonMovePhaseDown.disableProperty().unbind();
        this.buttonRemovePhase.disableProperty().unbind();
        this.buttonEditPhase.disableProperty().unbind();
    }

    private void bindPossibleScoringButtons() {
        ReadOnlyIntegerProperty selectedIndex = this.tablePossibleScores
                .getSelectionModel().selectedIndexProperty();
        ReadOnlyObjectProperty<PossibleScoring> selectedItem = this.tablePossibleScores
                .getSelectionModel().selectedItemProperty();

        // only enable move-up button if an item other than the topmost is
        // selected
        this.buttonMoveScoreUp.disableProperty().bind(
                selectedIndex.isEqualTo(0).or(selectedItem.isNull()));

        // only enable move-down button if an item other than the last one is
        // selected
        // index < size - 1 && selected != null
        this.buttonMoveScoreDown.disableProperty().bind(
                selectedIndex.greaterThanOrEqualTo(
                        Bindings.size(this.tablePossibleScores.getItems())
                                .subtract(1)).or(selectedItem.isNull()));

        // only enable remove button if an item is selected
        this.buttonRemoveScore.disableProperty().bind(selectedItem.isNull());

        // only enable edit button if an item is selected
        this.buttonEditScore.disableProperty().bind(selectedItem.isNull());
    }

    private void unbindPossibleScoringButtons() {
        this.buttonMoveScoreUp.disableProperty().unbind();
        this.buttonMoveScoreDown.disableProperty().unbind();
        this.buttonRemoveScore.disableProperty().unbind();
        this.buttonAddScore.disableProperty().unbind();
    }

    public void unloadModule() {
        log.fine("Unloading Tournament Module");

        if (this.loadedModule == null) {
            return;
        }

        /* Unbind the table buttons */
        this.unbindTournamentPhaseButtons();
        this.unbindPossibleScoringButtons();

        /* Unbind the text fields */
        this.textFieldModuleTitle.textProperty().unbindBidirectional(
                this.loadedModule.nameProperty());
        this.textAreaDescription.textProperty().unbindBidirectional(
                this.loadedModule.descriptionProperty());

        /* Unbind and clear the tables */
        this.tablePossibleScores.getSelectionModel().clearSelection();
        this.tableTournamentPhases.getSelectionModel().clearSelection();

        /* Unbind the dialogs */
        this.tournamentPhaseDialogController.unloadGamePhase();
        this.possibleScoringDialogController.unloadPossibleScoring();

        this.loadedModule = null;

        log.fine("Tournament Module unloaded");
    }

    @FXML
    private void onButtonMovePhaseUpClicked(ActionEvent event) {
        log.fine("Move Tournament Phase Up Button was clicked");
        int selectedIndex = this.getSelectedTournamentPhaseIndex();
        this.swapGamePhases(selectedIndex, selectedIndex - 1);
    }

    @FXML
    private void onButtonMovePhaseDownClicked(ActionEvent event) {
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
    private void onButtonAddPhaseClicked(ActionEvent event) {
        log.fine("Add Tournament Phase Button was clicked");
        GamePhase newGamePhase = new GamePhase();
        newGamePhase.setPhaseNumber(this.tableTournamentPhases.getItems()
                .size());

        this.tournamentPhaseDialog.properties(newGamePhase)
                .onResult((result, returnValue) -> {
                    if (result == DialogResult.OK && returnValue != null) {
                        this.loadedModule.getPhaseList().add(returnValue);
                    }
                }).show();
    }

    @FXML
    private void onButtonRemovePhaseClicked(ActionEvent event) {
        log.fine("Remove Tournament Phase Button was clicked");

        new SimpleDialog<>(PreferencesManager.getInstance().localizeString(
                "dialogs.tournamentmoduleeditor.dialogs.removephase.message"))
                .modalDialog()
                .dialogButtons(DialogButtons.YES_NO)
                .title("dialogs.tournamentmoduleeditor.dialogs.removephase.title")
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
                        this.loadedModule.getPhaseList().remove(
                                selectedTournamentPhase);
                    }
                }).show();
    }

    @FXML
    private void onButtonEditPhaseClicked(ActionEvent event) {
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
                                this.loadedModule.getPhaseList().remove(
                                        selectedTournamentPhase);
                                this.loadedModule.getPhaseList().add(
                                        returnValue);
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
    private void onButtonMoveScoreUpClicked(ActionEvent event) {
        log.fine("Move Possible Score Up Button was clicked");
        int selectedIndex = this.getSelectedPossibleScoreIndex();
        this.swapPossibleScores(selectedIndex, selectedIndex - 1);
    }

    @FXML
    private void onButtonMoveScoreDownClicked(ActionEvent event) {
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
    private void onButtonAddScoreClicked(ActionEvent event) {
        log.fine("Add Score Button was clicked");

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
    private void onButtonRemoveScoreClicked(ActionEvent event) {
        log.fine("Remove Possible Score Button was clicked");

        new SimpleDialog<>(PreferencesManager.getInstance().localizeString(
                "dialogs.tournamentmoduleeditor.dialogs.removescoring.message"))
                .modalDialog()
                .dialogButtons(DialogButtons.YES_NO)
                .title("dialogs.tournamentmoduleeditor.dialogs.removescoring.title")
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
                        this.loadedModule.getPossibleScores().remove(
                                selectedPossibleScore);
                    }
                }).show();
    }

    @FXML
    private void onButtonEditScoreClicked(ActionEvent event) {
        log.fine("Edit Score Button was clicked");

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

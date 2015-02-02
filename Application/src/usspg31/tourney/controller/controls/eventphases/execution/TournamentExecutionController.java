package usspg31.tourney.controller.controls.eventphases.execution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import usspg31.tourney.controller.MainWindow;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.RoundTimer;
import usspg31.tourney.controller.controls.PairingView;
import usspg31.tourney.controller.controls.PairingView.OverviewMode;
import usspg31.tourney.controller.controls.TournamentUser;
import usspg31.tourney.controller.controls.eventphases.TournamentExecutionPhaseController;
import usspg31.tourney.controller.dialogs.PairingScoreDialog;
import usspg31.tourney.controller.dialogs.PairingScoreDialog.PairingEntry;
import usspg31.tourney.controller.dialogs.PairingSwapDialog;
import usspg31.tourney.controller.dialogs.PlayerSelectionDialog;
import usspg31.tourney.controller.dialogs.VictoryConfiguration;
import usspg31.tourney.controller.dialogs.VictoryDialog;
import usspg31.tourney.controller.dialogs.modal.DialogResult;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.controller.layout.IconPane;
import usspg31.tourney.model.GamePhase;
import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.PairingHelper;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.PlayerScore;
import usspg31.tourney.model.RoundGeneratorFactory;
import usspg31.tourney.model.Tournament;
import usspg31.tourney.model.Tournament.ExecutionState;
import usspg31.tourney.model.undo.UndoManager;

public class TournamentExecutionController implements TournamentUser {

    private static final Logger log = Logger
            .getLogger(TournamentExecutionController.class.getName());

    @FXML private Button buttonPairingOverview;
    @FXML private Button buttonPhaseOverview;
    @FXML private PairingView pairingView;
    @FXML private Button buttonStartRound;
    @FXML private Button buttonEnterResult;
    @FXML private Button buttonSwapPlayers;
    @FXML private Button buttonDisqualifyPlayer;
    @FXML private Button buttonOpenProjectorWindow;

    @FXML private Label labelTime;
    @FXML private Button buttonAddTime;
    @FXML private Button buttonPauseResumeTime;
    @FXML private Button buttonResetTime;
    @FXML private Button buttonSubtractTime;
    @FXML private IconPane iconPanePauseResume;

    @FXML private Button buttonCancelExecution;

    @FXML private Label labelHeader;

    @FXML private IconPane iconPaneStartRound;

    private RoundTimer roundTimer;

    private Tournament loadedTournament;
    private RoundGeneratorFactory roundGenerator = new RoundGeneratorFactory();

    private final ModalDialog<PairingEntry, Pairing> pairingScoreDialog;
    private final ModalDialog<VictoryConfiguration, Object> victoryDialog;
    private final ModalDialog<ObservableList<Pairing>, ObservableList<Pairing>> swapDialog;
    private final ModalDialog<ObservableList<Player>, Player> disqualificationDialog;

    private TournamentExecutionPhaseController superController;
    private boolean tournamentFinished = false;
    private boolean displayVictoryMessage = false;
    private boolean disqualifiedInRound = false;

    private ArrayList<TournamentExecutionProjectionController> projectorWindowControllers;
    private OverviewMode currentOverviewMode;

    public TournamentExecutionController() {
        this.pairingScoreDialog = new PairingScoreDialog().modalDialog();
        this.victoryDialog = new VictoryDialog().modalDialog();
        this.swapDialog = new PairingSwapDialog().modalDialog();
        this.disqualificationDialog = new PlayerSelectionDialog().modalDialog();
        this.projectorWindowControllers = new ArrayList<TournamentExecutionProjectionController>();
        this.currentOverviewMode = OverviewMode.PAIRING_OVERVIEW;
    }

    @FXML
    public void initialize() {

    }

    public void setSuperController(
            TournamentExecutionPhaseController superController) {
        this.superController = superController;
    }

    @Override
    public void loadTournament(Tournament tournament) {
        log.info("Loading Tournament");
        if (this.loadedTournament != null) {
            this.unloadTournament();
        }
        this.loadedTournament = tournament;
        this.loadedTournament.getRemainingPlayers().addAll(
                this.loadedTournament.getAttendingPlayers());

        this.labelHeader.setText(this.loadedTournament.getName());

        this.buttonPairingOverview.getStyleClass().add("selected-button");

        this.pairingView
                .SelectedRoundProperty()
                .addListener(
                        (ov, o, n) -> {
                            if (n.intValue() > o.intValue()) {
                                if (this.roundTimer != null) {
                                    if (!this.roundTimer.isPaused()) {
                                        this.roundTimer.pause();
                                        this.iconPanePauseResume
                                                .getStyleClass().remove(
                                                        "icon-pause");
                                        this.iconPanePauseResume
                                                .getStyleClass().add(
                                                        "icon-play");
                                    }
                                    int roundDuration = 0;
                                    int round = 0;
                                    for (GamePhase phase : this.loadedTournament
                                            .getRuleSet().getPhaseList()) {
                                        if ((round += phase.getRoundCount()) > this.loadedTournament
                                                .getRounds().size()) {
                                            roundDuration = (int) phase
                                                    .getRoundDuration()
                                                    .getSeconds();
                                        }
                                    }
                                    this.roundTimer.setTime(roundDuration);
                                }
                            }

                            this.buttonSwapPlayers.setDisable(n.intValue() < this.loadedTournament
                                    .getRounds().size() - 1);
                            this.buttonDisqualifyPlayer.setDisable(n.intValue() < this.loadedTournament
                                    .getRounds().size() - 1
                                    || n.intValue() == 0
                                    || this.disqualifiedInRound);
                        });

        this.pairingView.setOnNodeDoubleClicked(() -> {
            this.onButtonEnterResultClicked(null);
        });

        this.pairingView.loadTournament(tournament);

        this.buttonEnterResult.disableProperty().bind(
                this.pairingView.selectedPairingProperty().isNull());

        if (tournament.getRounds().size() == 0) {
            this.generateRound(false);
            if (this.tournamentFinished) {
                this.iconPaneStartRound.getStyleClass().setAll("icon-pane",
                        "icon-finish", "half");
                this.buttonStartRound.setDisable(false);
                this.displayVictoryMessage = true;
                this.roundTimer.reset();
            }
            this.checkForTournamentFinish();
        }

        // register undo properties
        UndoManager undo = MainWindow.getInstance()
                .getEventPhaseViewController().getUndoManager();
        undo.registerUndoProperty(this.loadedTournament.getRounds());

        this.updateRoundTimer();

        this.checkRoundFinished();
    }

    public void disableCancelButton(boolean disable) {
        this.buttonCancelExecution.setDisable(disable);
    }

    private void updateRoundTimer() {
        int roundDuration = -1;
        int round = 0;
        for (GamePhase phase : this.loadedTournament.getRuleSet()
                .getPhaseList()) {
            if ((round += phase.getRoundCount()) > this.loadedTournament
                    .getRounds().size()) {
                roundDuration = (int) phase.getRoundDuration().getSeconds();
            }
        }
        if (roundDuration == -1) {
            roundDuration = 5 * 60; // 5 seconds standard time
        }
        this.roundTimer = new RoundTimer(roundDuration);

        this.labelTime.textProperty().unbind();
        this.labelTime.textProperty().bind(
                this.roundTimer.timerStringProperty());
        this.buttonAddTime.setOnAction(event -> {
            this.roundTimer.addTime();
        });
        this.buttonSubtractTime.setOnAction(event -> {
            this.roundTimer.subtractTime();
        });
        this.buttonPauseResumeTime.setOnAction(event -> {
            if (!this.roundTimer.isPaused()) {
                this.roundTimer.pause();
                this.iconPanePauseResume.getStyleClass().remove("icon-pause");
                this.iconPanePauseResume.getStyleClass().add("icon-play");
            } else {
                this.roundTimer.resume();
                this.iconPanePauseResume.getStyleClass().remove("icon-play");
                this.iconPanePauseResume.getStyleClass().add("icon-pause");
            }
        });
        this.buttonResetTime.setOnAction(event -> {
            this.roundTimer.reset();
        });
    }

    @Override
    public void unloadTournament() {
        log.info("Unloading Tournament");
        this.pairingView.unloadTournament();

        // unregister undo properties
        UndoManager undo = MainWindow.getInstance()
                .getEventPhaseViewController().getUndoManager();
        undo.unregisterUndoProperty(this.loadedTournament.getRounds());
    }

    @FXML
    private void onButtonStartRoundClicked(ActionEvent event) {
        log.finer("Start Round Button was clicked");
        if (!this.displayVictoryMessage) {
            this.generateRound(true);
            this.checkForTournamentFinish();
            this.updateProjectorWindows();
        } else {
            this.buttonStartRound.setDisable(true);
            this.buttonSwapPlayers.setDisable(true);
            this.buttonDisqualifyPlayer.setDisable(true);
            this.buttonAddTime.setDisable(true);
            this.buttonSubtractTime.setDisable(true);
            this.buttonPauseResumeTime.setDisable(true);
            this.buttonResetTime.setDisable(true);
            this.roundTimer.pause();
            this.iconPanePauseResume.getStyleClass().remove("icon-pause");
            this.iconPanePauseResume.getStyleClass().add("icon-play");
            this.loadedTournament.setExecutionState(ExecutionState.FINISHED);
            VictoryConfiguration configuration = new VictoryConfiguration();
            configuration.setWinningPlayer(this.loadedTournament.getRounds()
                    .get(this.loadedTournament.getRounds().size() - 1)
                    .getPairings().get(0).getOpponents().get(0));
            configuration.setTournamentName(this.loadedTournament.getName());
            this.victoryDialog.properties(configuration).show();
            for (TournamentExecutionProjectionController controller : this.projectorWindowControllers) {
                controller.showVictoryDialog(configuration);
            }
        }
    }

    private void generateRound(boolean newRound) {
        log.info("Generating next round");

        this.disqualifiedInRound = false;

        this.loadedTournament.getRounds().add(
                this.roundGenerator.generateRound(this.loadedTournament));
        this.buttonStartRound.setDisable(true);

        if (this.tournamentFinished && newRound) {
            this.iconPaneStartRound.getStyleClass().setAll("icon-pane",
                    "icon-finish", "half");
            this.buttonStartRound.setDisable(false);
            this.displayVictoryMessage = true;
            this.roundTimer.reset();
        }
    }

    private void checkForTournamentFinish() {
        /* Check if this is the last game phase and there are no pairings left */
        if (this.loadedTournament.getRounds()
                .get(this.loadedTournament.getRounds().size() - 1)
                .getPairings().size() == 1
                && this.loadedTournament.getRuleSet().getPhaseList().size() - 1 == PairingHelper
                        .findPhase(this.loadedTournament.getRounds().size(),
                                this.loadedTournament).getPhaseNumber()) {
            this.tournamentFinished = true;
        }
    }

    @FXML
    private void onButtonPairingOverviewClicked(ActionEvent event) {
        log.info("Pairing Overview Button was clicked");
        this.setOverviewMode(OverviewMode.PAIRING_OVERVIEW);
    }

    @FXML
    private void onButtonPhaseOverviewClicked(ActionEvent event) {
        log.info("Phase Overview Button was clicked");
        this.setOverviewMode(OverviewMode.PHASE_OVERVIEW);
    }

    public void setOverviewMode(OverviewMode mode) {
        this.currentOverviewMode = mode;
        this.pairingView.setOverviewMode(mode);

        switch (mode) {
        case PHASE_OVERVIEW:
            this.buttonPairingOverview.getStyleClass()
                    .remove("selected-button");
            if (!this.buttonPhaseOverview.getStyleClass().contains(
                    "selected-button")) {
                this.buttonPhaseOverview.getStyleClass().add("selected-button");
            }
            break;
        case PAIRING_OVERVIEW:
            this.buttonPhaseOverview.getStyleClass().remove("selected-button");
            if (!this.buttonPairingOverview.getStyleClass().contains(
                    "selected-button")) {
                this.buttonPairingOverview.getStyleClass().add(
                        "selected-button");
            }
            break;
        }
    }

    public OverviewMode getOverviewMode() {
        return this.currentOverviewMode;
    }

    @FXML
    private void onButtonEnterResultClicked(ActionEvent event) {
        log.info("Enter Result Button was clicked");
        this.pairingScoreDialog
                .properties(
                        new PairingEntry(this.loadedTournament,
                                this.pairingView.getSelectedPairing()))
                .onResult(
                        (result, value) -> {
                            if (result == DialogResult.OK) {
                                for (int i = 0; i < value.getScoreTable()
                                        .size(); i++) {
                                    PlayerScore score = value.getScoreTable()
                                            .get(i);
                                    PlayerScore selectedScore = this.pairingView
                                            .getSelectedPairing()
                                            .getScoreTable().get(i);
                                    selectedScore.getScore().clear();

                                    for (int j = 0; j < score.getScore().size(); j++) {
                                        Integer newScore = score.getScore()
                                                .get(j);
                                        if (newScore == null) {
                                            newScore = 0;
                                        }
                                        selectedScore.getScore().add(newScore);
                                    }
                                }
                                this.pairingView.updateOverview();
                                this.checkRoundFinished();

                                this.updateProjectorWindows();
                                MainWindow.getInstance()
                                        .getEventPhaseViewController()
                                        .activateSaveButton();
                            }
                        }).show();
    }

    private void checkRoundFinished() {
        // check, if all pairings have a score
        int totalRoundCount = 0;
        for (GamePhase phase : this.loadedTournament.getRuleSet()
                .getPhaseList()) {
            totalRoundCount += phase.getRoundCount();
        }

        // have we reached the last available round?
        if (this.pairingView.getSelectedRound() >= totalRoundCount - 1) {
            this.buttonStartRound.setDisable(true);
            return;
        }

        boolean roundFinished = true;
        roundFinishCheck: for (Pairing pairing : this.loadedTournament
                .getRounds().get(this.pairingView.getSelectedRound())
                .getPairings()) {
            for (PlayerScore playerScore : pairing.getScoreTable()) {
                if (playerScore.getScore().size() < this.loadedTournament
                        .getRuleSet().getPossibleScores().size()) {
                    roundFinished = false;
                    break roundFinishCheck;
                }
                for (Integer score : playerScore.getScore()) {
                    if (score == null) {
                        roundFinished = false;
                        break roundFinishCheck;
                    }
                }
            }
        }
        this.buttonStartRound.setDisable(!roundFinished);
    }

    public void updatePairingView() {
        this.pairingView.updateOverview();
    }

    @FXML
    private void onButtonSwapPlayersClicked(ActionEvent e) {
        log.info("Swap Players Button clicked");
        this.swapDialog
                .properties(
                        this.loadedTournament
                                .getRounds()
                                .get(this.loadedTournament.getRounds().size() - 1)
                                .getPairings())
                .onResult(
                        (result, value) -> {
                            if (result == DialogResult.OK) {
                                this.loadedTournament
                                        .getRounds()
                                        .get(this.loadedTournament.getRounds()
                                                .size() - 1).getPairings()
                                        .setAll(value);
                            }

                            this.pairingView.updateOverview();
                            this.updateProjectorWindows();
                        }).show();
    }

    @FXML
    private void onButtonDisqualifyPlayerClicked(ActionEvent e) {
        log.info("Disqualify Player Button clicked");
        this.disqualificationDialog
                .properties(this.loadedTournament.getRemainingPlayers())
                .onResult(
                        (result, value) -> {
                            if (result == DialogResult.OK && value != null) {
                                value.setDisqualified(true);
                                PlayerScore removedScore = null;
                                Pairing usedPairing = null;
                                for (Pairing pairing : this.loadedTournament
                                        .getRounds()
                                        .get(this.loadedTournament.getRounds()
                                                .size() - 2).getPairings()) {
                                    if (this.collectionContainsPlayer(pairing.getOpponents(), value)) {
                                        for (PlayerScore score : pairing
                                                .getScoreTable()) {
                                            if (score.getPlayer().getId()
                                                    .equals(value.getId())) {
                                                removedScore = score;
                                                usedPairing = pairing;
                                                pairing.getScoreTable().remove(
                                                        score);
                                                break;
                                            }
                                        }
                                        break;
                                    }
                                }
                                this.loadedTournament.getDisqualifiedPlayers()
                                        .add(value);
                                this.loadedTournament.getRemainingPlayers()
                                        .remove(value);

                                this.loadedTournament.getRounds().remove(
                                        this.loadedTournament.getRounds()
                                                .size() - 1);
                                this.generateRound(false);

                                usedPairing.getScoreTable().add(removedScore);
                                this.disqualifiedInRound = true;
                                this.buttonDisqualifyPlayer.setDisable(true);
                            }

                            this.pairingView.updateOverview();
                            this.updateProjectorWindows();
                        }).show();
    }

    private boolean collectionContainsPlayer(Collection<Player> list, Player player) {
        for (Player p : list) {
            if (p.getId().equals(player.getId())) {
                return true;
            }
        }

        return false;
    }

    @FXML
    private void onButtonCancelExecutionClicked(ActionEvent e) {
        log.info("Cancel Execution Button clicked");
        this.superController.cancelExecution();
    }

    private void updateProjectorWindows() {
        for (TournamentExecutionProjectionController controller : this.projectorWindowControllers) {
            controller.loadTournament((Tournament) this.loadedTournament
                    .clone());
            switch (controller.getOverviewMode()) {
            case PHASE_OVERVIEW:
                controller.setOverviewMode(OverviewMode.PAIRING_OVERVIEW);
                controller.setOverviewMode(OverviewMode.PHASE_OVERVIEW);
                break;
            case PAIRING_OVERVIEW:
                controller.setOverviewMode(OverviewMode.PHASE_OVERVIEW);
                controller.setOverviewMode(OverviewMode.PAIRING_OVERVIEW);
                break;
            }
        }
    }

    @FXML
    private void onButtonOpenProjectorWindowClicked(ActionEvent e) {
        log.info("Open Projector Window Button clicked");

        try {
            FXMLLoader executionLoader = new FXMLLoader(
                    this.getClass()
                            .getResource(
                                    "/ui/fxml/controls/eventphases/execution/tournament-execution-projection.fxml"),
                    PreferencesManager.getInstance().getSelectedLanguage()
                            .getLanguageBundle());
            Parent executionWindow = executionLoader.load();

            TournamentExecutionProjectionController controller = executionLoader
                    .getController();
            controller.loadTournament((Tournament) this.loadedTournament
                    .clone());

            Stage stage = new Stage();
            stage.setTitle(this.loadedTournament.getName() + " \u2014 Tourney");
            stage.setScene(new Scene(executionWindow, 700, 450));
            stage.centerOnScreen();
            stage.show();

            controller.setStage(stage);
            this.projectorWindowControllers.add(controller);
            this.roundTimer
                    .setProjectionControllers(this.projectorWindowControllers);
            this.roundTimer.updateTimerString();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}

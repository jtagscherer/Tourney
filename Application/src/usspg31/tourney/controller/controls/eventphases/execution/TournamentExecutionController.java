package usspg31.tourney.controller.controls.eventphases.execution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import usspg31.tourney.controller.MainWindow;
import usspg31.tourney.controller.RoundTimer;
import usspg31.tourney.controller.controls.PairingView;
import usspg31.tourney.controller.controls.PairingView.OverviewMode;
import usspg31.tourney.controller.controls.TournamentUser;
import usspg31.tourney.controller.dialogs.PairingScoreDialog;
import usspg31.tourney.controller.dialogs.PairingScoreDialog.PairingEntry;
import usspg31.tourney.controller.dialogs.modal.DialogResult;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.model.GamePhase;
import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.PairingHelper;
import usspg31.tourney.model.PlayerScore;
import usspg31.tourney.model.RoundGeneratorFactory;
import usspg31.tourney.model.Tournament;
import usspg31.tourney.model.TournamentRound;
import usspg31.tourney.model.undo.UndoManager;

public class TournamentExecutionController implements TournamentUser {

    private static final Logger log = Logger
            .getLogger(TournamentExecutionController.class.getName());

    @FXML private Button buttonPairingOverview;
    @FXML private Button buttonPhaseOverview;
    @FXML private PairingView pairingView;
    @FXML private Button buttonStartRound;
    @FXML private Button buttonEnterResult;

    @FXML private Label labelTime;
    @FXML private Button buttonAddTime;
    @FXML private Button buttonPauseTime;
    @FXML private Button buttonResumeTime;
    @FXML private Button buttonResetTime;
    @FXML private Button buttonSubtractTime;

    private RoundTimer roundTimer;

    private Tournament loadedTournament;
    private RoundGeneratorFactory roundGenerator = new RoundGeneratorFactory();

    private final ModalDialog<PairingEntry, Pairing> pairingScoreDialog;

    public TournamentExecutionController() {
        this.pairingScoreDialog = new PairingScoreDialog().modalDialog();
    }

    @FXML
    public void initialize() {

    }

    @Override
    public void loadTournament(Tournament tournament) {
        log.info("Loading Tournament");
        this.loadedTournament = tournament;
        this.loadedTournament.getRemainingPlayers().addAll(
                this.loadedTournament.getAttendingPlayers());

        this.buttonPairingOverview.getStyleClass().add("selected-button");

        this.pairingView.SelectedRoundProperty().addListener((ov, o, n) -> {
            if (n.intValue() > o.intValue()) {
                // this.updateRoundTimer();
            }
        });

        this.pairingView.setOnNodeDoubleClicked(() -> {
            this.onButtonEnterResultClicked(null);
        });

        this.pairingView.loadTournament(tournament);

        this.buttonEnterResult.disableProperty().bind(
                this.pairingView.selectedPairingProperty().isNull());

        if (tournament.getRounds().size() == 0) {
            this.generateRound();
        }

        // register undo properties
        UndoManager undo = MainWindow.getInstance()
                .getEventPhaseViewController().getUndoManager();
        undo.registerUndoProperty(this.loadedTournament.getRounds());

        // this.updateRoundTimer();
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
                this.roundTimer.getTimerDuration().asString().concat("s"));
        this.buttonAddTime.setOnAction(event -> {
            this.roundTimer.addTime();
        });
        this.buttonSubtractTime.setOnAction(event -> {
            this.roundTimer.subtractTime();
        });
        this.buttonPauseTime.setOnAction(event -> {
            this.roundTimer.pause();
        });
        this.buttonResumeTime.setOnAction(event -> {
            this.roundTimer.resume();
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
        this.generateRound();
    }

    private void generateRound() {
        log.info("Generating next round");
        if (this.loadedTournament.getRounds().size() > 0) {
            TournamentRound currentRound = this.loadedTournament.getRounds()
                    .get(this.loadedTournament.getRounds().size() - 1);
            for (Pairing pairing : currentRound.getPairings()) {
                for (PlayerScore score : pairing.getScoreTable()) {
                    this.loadedTournament.addAScore(score);
                }
            }
        }

        if (PairingHelper.cutOffAfterRound(this.loadedTournament.getRounds()
                .size(), this.loadedTournament)) {
            ArrayList<PlayerScore> cloneScoreTable = new ArrayList<>();
            cloneScoreTable.addAll(this.loadedTournament.getScoreTable());
            Collections.sort(cloneScoreTable);
            this.loadedTournament.getRemainingPlayers().clear();
            for (int i = 0; i < PairingHelper.findPhase(
                    this.loadedTournament.getRounds().size() - 1,
                    this.loadedTournament).getCutoff(); i++) {
                this.loadedTournament.getRemainingPlayers().add(
                        cloneScoreTable.get(cloneScoreTable.size() - 1 - i)
                                .getPlayer());
            }
        }

        this.loadedTournament.getRounds().add(
                this.roundGenerator.generateRound(this.loadedTournament));
        this.buttonStartRound.setDisable(true);
    }

    @FXML
    private void onButtonPairingOverviewClicked(ActionEvent event) {
        log.info("Pairing Overview Button was clicked");
        this.buttonPhaseOverview.getStyleClass().remove("selected-button");
        if (!this.buttonPairingOverview.getStyleClass().contains(
                "selected-button")) {
            this.buttonPairingOverview.getStyleClass().add("selected-button");
        }
        this.pairingView.setOverviewMode(OverviewMode.PAIRING_OVERVIEW);
    }

    @FXML
    private void onButtonPhaseOverviewClicked(ActionEvent event) {
        log.info("Phase Overview Button was clicked");
        this.buttonPairingOverview.getStyleClass().remove("selected-button");
        if (!this.buttonPhaseOverview.getStyleClass().contains(
                "selected-button")) {
            this.buttonPhaseOverview.getStyleClass().add("selected-button");
        }
        this.pairingView.setOverviewMode(OverviewMode.PHASE_OVERVIEW);
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
                                    for (int j = 0; j < score.getScore().size(); j++) {
                                        Integer newScore = score.getScore()
                                                .get(j);
                                        if (newScore == null) {
                                            newScore = 0;
                                        }
                                        selectedScore.getScore().clear();
                                        selectedScore.getScore().add(newScore);
                                    }
                                }
                                this.pairingView.updateOverview();
                                this.checkRoundFinished();

                                MainWindow.getInstance()
                                        .getEventPhaseViewController()
                                        .activateSaveButton();
                            }
                        }).show();
    }

    private void checkRoundFinished() {
        // FIXME: this method does return true even though we didn't fill out
        // all pairings
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

    @FXML
    private void onButtonSwapPlayersClicked(ActionEvent e) {
        log.info("Swap Players Button clicked");

    }
}

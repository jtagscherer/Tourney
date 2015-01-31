package usspg31.tourney.controller.controls.eventphases;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import usspg31.tourney.controller.MainWindow;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.controls.EventUser;
import usspg31.tourney.controller.controls.eventphases.execution.TournamentExecutionController;
import usspg31.tourney.controller.controls.eventphases.execution.TournamentSelectionController;
import usspg31.tourney.controller.dialogs.AttendanceDialog;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.DialogResult;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.controller.dialogs.modal.SimpleDialog;
import usspg31.tourney.model.Event;
import usspg31.tourney.model.Event.UserFlag;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.Tournament;
import usspg31.tourney.model.Tournament.ExecutionState;

public class TournamentExecutionPhaseController implements EventUser {

    private static final Logger log = Logger
            .getLogger(TournamentExecutionPhaseController.class.getName());

    @FXML private StackPane contentBox;

    private Node selectionPhase;
    private TournamentSelectionController selectionController;

    private Node executionPhase;
    private TournamentExecutionController executionController;

    private ModalDialog<ObservableList<Player>, ObservableList<Player>> attendanceDialog;

    private Event loadedEvent;

    // Animations
    private DoubleProperty phasePosition;
    private Timeline currentAnimation;
    private static final Duration transitionDuration = Duration.millis(300);
    private static Interpolator transitionInterpolator = Interpolator.SPLINE(
            .4, 0, 0, 1);

    @FXML
    public void initialize() {
        this.attendanceDialog = new AttendanceDialog().modalDialog();
        this.phasePosition = new SimpleDoubleProperty();
        this.phasePosition.set(0);

        try {
            /* Load the selection view */
            FXMLLoader selectionLoader = new FXMLLoader(
                    this.getClass()
                            .getResource(
                                    "/ui/fxml/controls/eventphases/execution/tournament-selection.fxml"),
                    PreferencesManager.getInstance().getSelectedLanguage()
                            .getLanguageBundle());
            this.selectionPhase = selectionLoader.load();
            this.selectionController = selectionLoader.getController();
            this.selectionController.setExecutionSuperController(this);
            this.selectionPhase.setVisible(true);

            /* Load the execution view */
            FXMLLoader executionLoader = new FXMLLoader(
                    this.getClass()
                            .getResource(
                                    "/ui/fxml/controls/eventphases/execution/tournament-execution.fxml"),
                    PreferencesManager.getInstance().getSelectedLanguage()
                            .getLanguageBundle());
            this.executionPhase = executionLoader.load();
            this.executionController = executionLoader.getController();
            this.executionController.setSuperController(this);
            this.executionPhase.setVisible(true);

            this.contentBox.getChildren().addAll(this.selectionPhase,
                    this.executionPhase);

            /* Bind the phase positions to the animation state */
            this.selectionPhase.translateYProperty().bind(
                    this.contentBox
                            .heightProperty()
                            .multiply(0)
                            .subtract(
                                    this.contentBox.heightProperty().multiply(
                                            this.phasePosition)));
            this.executionPhase.translateYProperty().bind(
                    this.contentBox
                            .heightProperty()
                            .multiply(1)
                            .subtract(
                                    this.contentBox.heightProperty().multiply(
                                            this.phasePosition)));
        } catch (IOException e) {
            log.log(Level.SEVERE,
                    "Could not initialize the tournament execution views.", e);
            e.printStackTrace();
        }
    }

    @Override
    public void loadEvent(Event event) {
        if (this.loadedEvent != null) {
            this.unloadEvent();
        }

        this.loadedEvent = event;
        this.selectionController.loadEvent(this.loadedEvent);
    }

    @Override
    public void unloadEvent() {
        this.loadedEvent = null;
    }

    public void showTournamentExecutionView(Tournament tournament) {
        /*
         * Collect all players that are registered for this tournament and are
         * attending in the event
         */
        ObservableList<Player> attendingPlayers = FXCollections
                .observableArrayList();
        for (Player player : tournament.getRegisteredPlayers()) {
            if (!player.getStartingNumber().equals("")) {
                attendingPlayers.add(player);
            }
        }

        this.executionController.disableCancelButton(this.loadedEvent
                .getUserFlag() == UserFlag.TOURNAMENT_EXECUTION);

        switch (tournament.getExecutionState()) {
        case NOT_EXECUTED:
            /* Open an attendance dialog with the collected players as the input */
            this.attendanceDialog
                    .properties(attendingPlayers)
                    .onResult(
                            (result, returnValue) -> {
                                if (result == DialogResult.OK) {
                                    tournament.getAttendingPlayers().setAll(
                                            returnValue);
                                    tournament
                                            .setExecutionState(ExecutionState.CURRENTLY_EXECUTED);
                                    this.executionController
                                            .loadTournament(tournament);
                                    this.slideToPhase(1);
                                }
                            }).show();
            break;
        case CURRENTLY_EXECUTED:
            this.executionController.loadTournament(tournament);
            this.slideToPhase(1);
            break;
        case FINISHED:
            new SimpleDialog<>(PreferencesManager.getInstance().localizeString(
                    "tournamentselection.dialogs.execute.error"))
                    .modalDialog()
                    .dialogButtons(DialogButtons.YES_NO)
                    .title("dialogs.titles.error")
                    .onResult(
                            (result, returnValue) -> {
                                if (result == DialogResult.YES) {
                                    tournament.getRounds().clear();
                                    tournament.getScoreTable().clear();
                                    tournament.getAttendingPlayers().clear();
                                    tournament.getRemainingPlayers().clear();
                                    tournament.getReceivedByePlayers().clear();
                                    tournament
                                            .setExecutionState(ExecutionState.NOT_EXECUTED);
                                    MainWindow.getInstance()
                                            .getEventPhaseViewController()
                                            .saveEvent();
                                }

                                MainWindow.getInstance()
                                        .getEventPhaseViewController()
                                        .unloadEvent();
                                MainWindow.getInstance().slideDown(
                                        MainWindow.getInstance().getMainMenu());
                            }).show();
            break;
        }
    }

    public void cancelExecution() {
        this.slideToPhase(0);
        this.executionController.unloadTournament();
    }

    private void slideToPhase(int phase) {
        if (this.currentAnimation != null) {
            this.currentAnimation.stop();
        }
        this.currentAnimation = new Timeline(new KeyFrame(Duration.ZERO,
                new KeyValue(this.phasePosition, this.phasePosition.get())),
                new KeyFrame(transitionDuration, new KeyValue(
                        this.phasePosition, phase, transitionInterpolator)));
        this.currentAnimation.play();
    }
}

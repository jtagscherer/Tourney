package usspg31.tourney.controller.controls.eventphases.execution;

import java.util.ArrayList;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import usspg31.tourney.controller.MainWindow;
import usspg31.tourney.controller.RoundTimer;
import usspg31.tourney.controller.controls.PairingView;
import usspg31.tourney.controller.controls.PairingView.OverviewMode;
import usspg31.tourney.controller.controls.TournamentUser;
import usspg31.tourney.controller.dialogs.VictoryConfiguration;
import usspg31.tourney.controller.dialogs.VictoryDialog;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.model.Tournament;
import usspg31.tourney.model.undo.UndoManager;

public class TournamentExecutionProjectionController implements TournamentUser {

    private static final Logger log = Logger
            .getLogger(TournamentExecutionProjectionController.class.getName());

    @FXML private Label labelHeader;
    @FXML private Label labelTime;
    @FXML private PairingView pairingView;

    private RoundTimer roundTimer;

    private Tournament loadedTournament;

    private final ModalDialog<VictoryConfiguration, Object> victoryDialog;

    private boolean tournamentFinished = false;
    private boolean displayVictoryMessage = false;

    private ArrayList<TournamentExecutionProjectionController> projectorWindowControllers;
    private OverviewMode currentOverviewMode;

    public TournamentExecutionProjectionController() {
        this.victoryDialog = new VictoryDialog().modalDialog();
        this.projectorWindowControllers = new ArrayList<TournamentExecutionProjectionController>();
        this.currentOverviewMode = OverviewMode.PAIRING_OVERVIEW;
    }

    @Override
    public void loadTournament(Tournament tournament) {
        log.info("Loading Tournament");
        this.loadedTournament = tournament;
        this.loadedTournament.getRemainingPlayers().addAll(
                this.loadedTournament.getAttendingPlayers());
        this.pairingView.loadTournament(this.loadedTournament);
        this.pairingView.SelectedRoundProperty().set(0);
        this.pairingView.SelectedPhaseProperty().set(0);

        this.labelHeader.setText(this.loadedTournament.getName());
    }

    public void setTimeString(String value) {
        this.labelTime.setText(value);
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
    }

    public OverviewMode getOverviewMode() {
        return this.currentOverviewMode;
    }
}

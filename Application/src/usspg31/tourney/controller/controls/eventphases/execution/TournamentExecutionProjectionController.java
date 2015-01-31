package usspg31.tourney.controller.controls.eventphases.execution;

import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import usspg31.tourney.controller.controls.PairingView;
import usspg31.tourney.controller.controls.PairingView.OverviewMode;
import usspg31.tourney.controller.controls.TournamentUser;
import usspg31.tourney.controller.dialogs.VictoryConfiguration;
import usspg31.tourney.controller.dialogs.VictoryDialog;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.model.Tournament;

public class TournamentExecutionProjectionController implements TournamentUser {

    private static final Logger log = Logger
            .getLogger(TournamentExecutionProjectionController.class.getName());

    @FXML private Label labelHeader;
    @FXML private Label labelTime;
    @FXML private PairingView pairingView;
    @FXML private StackPane contentRoot;

    @FXML private Button buttonPairingOverview;
    @FXML private Button buttonPhaseOverview;

    private Tournament loadedTournament;

    private final ModalDialog<VictoryConfiguration, Object> victoryDialog;

    private OverviewMode currentOverviewMode;
    private Stage stage;

    public TournamentExecutionProjectionController() {
        this.victoryDialog = new VictoryDialog().modalDialog();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        this.setOverviewMode(OverviewMode.PAIRING_OVERVIEW);
    }

    public PairingView getPairingView() {
        return this.pairingView;
    }

    @Override
    public void loadTournament(Tournament tournament) {
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

    public void showVictoryDialog(VictoryConfiguration config) {
        this.victoryDialog.dialogButtons(DialogButtons.NONE);
        this.victoryDialog.properties(config)
                .show(this.stage, this.contentRoot);
    }

    @Override
    public void unloadTournament() {
        log.info("Unloading Tournament");
        this.pairingView.unloadTournament();
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
}

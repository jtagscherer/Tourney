package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.controls.NumberTextField;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.model.GamePhase;
import usspg31.tourney.model.pairingstrategies.FreeForAll;
import usspg31.tourney.model.pairingstrategies.PairingStrategy;
import usspg31.tourney.model.pairingstrategies.SingleElimination;
import usspg31.tourney.model.pairingstrategies.SwissSystem;

public class TournamentPhaseDialog extends VBox implements
        IModalDialogProvider<GamePhase, GamePhase> {

    private static final Logger log = Logger
            .getLogger(TournamentPhaseDialog.class.getName());

    @FXML private NumberTextField textFieldCutoff;
    @FXML private ComboBox<PairingStrategy> comboBoxPairingStrategy;
    @FXML private NumberTextField textFieldRoundCount;
    @FXML private NumberTextField textFieldPlayTimeMinutes;
    @FXML private NumberTextField textFieldPlayTimeSeconds;
    @FXML private NumberTextField textFieldPlayersPerPairing;

    private GamePhase loadedPhase;

    public TournamentPhaseDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
                    "/ui/fxml/dialogs/tournament-phase-dialog.fxml"),
                    PreferencesManager.getInstance().getSelectedLanguage()
                            .getLanguageBundle());
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @FXML
    private void initialize() {
        this.textFieldPlayTimeSeconds.numberValueProperty().addListener(
                (ov, o, n) -> {
                    if (n.intValue() >= 60) {
                        this.textFieldPlayTimeSeconds.setNumberValue(59);
                    }
                });

        // TODO: somehow format the output in the combobox (currently fully
        // qualified class name is shown)
        this.comboBoxPairingStrategy.setItems(this
                .getAvailablePairingStrategies());
    }

    private ObservableList<PairingStrategy> getAvailablePairingStrategies() {
        ObservableList<PairingStrategy> pairingStrategies = FXCollections
                .observableArrayList();

        // TODO: possibly get the available strategies via reflection or
        // something?
        pairingStrategies.addAll(new SingleElimination(), new FreeForAll(),
                new SwissSystem());

        return pairingStrategies;
    }

    @Override
    public void setProperties(GamePhase properties) {
        if (this.loadedPhase != null) {
            this.unloadGamePhase();
        }
        this.loadGamePhase(properties);
    }

    @Override
    public GamePhase getReturnValue() {
        return this.loadedPhase;
    }

    @Override
    public String getInputErrorString() {
        if (this.loadedPhase.getRoundCount() <= 0) {
            return PreferencesManager.getInstance().localizeString(
                    "dialogs.tournamentphase.errors.roundcounttoolow");
        } else if (this.textFieldPlayTimeMinutes.getNumberValue() > 7 * 24 * 60) {
            return PreferencesManager.getInstance().localizeString(
                    "dialogs.tournamentphase.errors.playtimetoobig");
        } else {
            return null;
        }
    };

    @Override
    public void initModalDialog(ModalDialog<GamePhase, GamePhase> modalDialog) {
        modalDialog.title("dialogs.tournamentphase").dialogButtons(
                DialogButtons.OK_CANCEL);
    }

    private void loadGamePhase(GamePhase gamePhase) {
        log.fine("Loading Game Phase");
        this.loadedPhase = gamePhase;

        this.textFieldCutoff.numberValueProperty().bindBidirectional(
                gamePhase.cutoffProperty());
        this.textFieldRoundCount.numberValueProperty().bindBidirectional(
                gamePhase.roundCountProperty());
        this.textFieldPlayTimeMinutes.setNumberValue((int) gamePhase
                .getRoundDuration().getSeconds() / 60);
        this.textFieldPlayTimeSeconds.setNumberValue((int) gamePhase
                .getRoundDuration().getSeconds() % 60);
        this.textFieldPlayersPerPairing.numberValueProperty()
                .bindBidirectional(gamePhase.numberOfOpponentsProperty());

        this.textFieldPlayTimeMinutes.numberValueProperty().addListener(
                this::timeUpdated);
        this.textFieldPlayTimeSeconds.numberValueProperty().addListener(
                this::timeUpdated);

        this.comboBoxPairingStrategy.valueProperty().addListener(
                this::pairingStrategyUpdated);

        log.fine("Game Phase loaded");
    }

    private void unloadGamePhase() {
        log.fine("Unloading Game Phase");

        this.textFieldCutoff.numberValueProperty().unbindBidirectional(
                this.loadedPhase.cutoffProperty());
        this.textFieldRoundCount.numberValueProperty().unbindBidirectional(
                this.loadedPhase.roundCountProperty());
        this.textFieldPlayersPerPairing
                .numberValueProperty()
                .bindBidirectional(this.loadedPhase.numberOfOpponentsProperty());

        this.textFieldPlayTimeMinutes.numberValueProperty().removeListener(
                this::timeUpdated);
        this.textFieldPlayTimeSeconds.numberValueProperty().removeListener(
                this::timeUpdated);

        this.comboBoxPairingStrategy.valueProperty().removeListener(
                this::pairingStrategyUpdated);

        this.loadedPhase = null;
        log.fine("Game Phase unloaded");
    }

    private void timeUpdated(ObservableValue<? extends Number> observable,
            Number o, Number n) {
        if (this.textFieldPlayTimeMinutes.getNumberValue() <= 7 * 24 * 60) {
            this.loadedPhase
                    .setRoundDuration(Duration
                            .ofSeconds(this.textFieldPlayTimeSeconds
                                    .getNumberValue()
                                    + (this.textFieldPlayTimeMinutes
                                            .getNumberValue() * 60)));
        }
    }

    private void pairingStrategyUpdated(
            ObservableValue<? extends PairingStrategy> ov, PairingStrategy o,
            PairingStrategy n) {
        this.loadedPhase.setPairingMethod(n);
    }
}

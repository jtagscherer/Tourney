package usspg31.tourney.controller.dialogs;

import java.io.IOException;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.controller.controls.NumberTextField;
import usspg31.tourney.controller.dialogs.modal.DialogButtons;
import usspg31.tourney.controller.dialogs.modal.IModalDialogProvider;
import usspg31.tourney.controller.dialogs.modal.ModalDialog;
import usspg31.tourney.model.GamePhase;
import usspg31.tourney.model.pairingstrategies.PairingStrategies;
import usspg31.tourney.model.pairingstrategies.PairingStrategy;

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

        this.comboBoxPairingStrategy.setCellFactory(listView -> {
            return new ListCell<PairingStrategy>() {
                @Override
                protected void updateItem(PairingStrategy item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        this.setGraphic(null);
                    } else {
                        this.setText(item.getName());
                    }
                }
            };
        });

        this.comboBoxPairingStrategy
                .setButtonCell(new ListCell<PairingStrategy>() {
                    @Override
                    protected void updateItem(PairingStrategy item,
                            boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            this.setGraphic(null);
                        } else {
                            this.setText(item.getName());
                        }
                    }
                });

        this.comboBoxPairingStrategy.setItems(this
                .getAvailablePairingStrategies());
    }

    private ObservableList<PairingStrategy> getAvailablePairingStrategies() {
        return PairingStrategies.getPairingStrategyInstances();
    }

    @Override
    public void setProperties(GamePhase properties) {
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
        }
        if (this.textFieldPlayTimeMinutes.getNumberValue() > 7 * 24 * 60) {
            return PreferencesManager.getInstance().localizeString(
                    "dialogs.tournamentphase.errors.playtimetoobig");
        }
        if (this.loadedPhase.getRoundDuration().getSeconds() == 0) {
            return PreferencesManager.getInstance().localizeString(
                    "dialogs.tournamentphase.errors.playtimetoolow");
        }
        if (Integer.parseInt(this.textFieldPlayersPerPairing.getText()) < 2) {
            return PreferencesManager.getInstance().localizeString(
                    "dialogs.tournamentphase.errors.playersperpairingtoolow");
        }
        if (this.comboBoxPairingStrategy.getSelectionModel().getSelectedItem() == null) {
            return PreferencesManager.getInstance().localizeString(
                    "dialogs.tournamentphase.errors.nopairingstrategy");
        }

        return null;
    };

    @Override
    public void initModalDialog(ModalDialog<GamePhase, GamePhase> modalDialog) {
        modalDialog.title("dialogs.tournamentphase").dialogButtons(
                DialogButtons.OK_CANCEL);
    }

    private void loadGamePhase(GamePhase gamePhase) {
        log.fine("Loading Game Phase");
        if (this.loadedPhase != null) {
            this.unloadGamePhase();
        }

        this.loadedPhase = (GamePhase) gamePhase.clone();

        this.textFieldCutoff.numberValueProperty().bindBidirectional(
                this.loadedPhase.cutoffProperty());
        this.textFieldRoundCount.numberValueProperty().bindBidirectional(
                this.loadedPhase.roundCountProperty());
        this.textFieldPlayTimeMinutes.setNumberValue((int) this.loadedPhase
                .getRoundDuration().getSeconds() / 60);
        this.textFieldPlayTimeSeconds.setNumberValue((int) this.loadedPhase
                .getRoundDuration().getSeconds() % 60);
        this.textFieldPlayersPerPairing
                .numberValueProperty()
                .bindBidirectional(this.loadedPhase.numberOfOpponentsProperty());

        this.textFieldPlayTimeMinutes.numberValueProperty().addListener(
                this::timeUpdated);
        this.textFieldPlayTimeSeconds.numberValueProperty().addListener(
                this::timeUpdated);

        this.comboBoxPairingStrategy.valueProperty().addListener(
                this::pairingStrategyUpdated);
        int selectionIndex = 0;
        for (PairingStrategy availableStrategy : this.comboBoxPairingStrategy
                .getItems()) {
            if (this.loadedPhase.getPairingMethod().getName()
                    .equals(availableStrategy.getName())) {
                this.comboBoxPairingStrategy.getSelectionModel().select(
                        selectionIndex);
                break;
            }
            selectionIndex++;
        }

        log.fine("Game Phase loaded");
    }

    public void unloadGamePhase() {
        log.fine("Unloading Game Phase");

        if (this.loadedPhase == null) {
            return;
        }

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

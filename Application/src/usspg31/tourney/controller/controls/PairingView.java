package usspg31.tourney.controller.controls;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.Tournament;
import usspg31.tourney.model.TournamentRound;

/**
 * Control used in the EventPhaseView used to display and select pairings.
 */
public class PairingView extends VBox implements TournamentUser {

    private static final Logger log = Logger.getLogger(PairingView.class
            .getName());

    @FXML private Button buttonScrollBreadcrumbsLeft;
    @FXML private Button buttonScrollBreadcrumbsRight;
    @FXML private ScrollPane breadcrumbScrollPane;
    @FXML private HBox breadcrumbContainer;
    @FXML private FlowPane pairingContainer;

    private Tournament loadedTournament;

    private IntegerProperty selectedRound;

    private ObjectProperty<PairingNode> selectedPairingNode;
    private ObjectProperty<Pairing> selectedPairing;

    public PairingView() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
                    "/ui/fxml/controls/pairing-view.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        this.selectedPairingNode = new SimpleObjectProperty<>();
        this.selectedPairingNode.addListener((ov, o, n) -> {
            if (n != null) {
                this.setSelectedPairing(n.getPairing());
            } else {
                this.setSelectedPairing(null);
            }
        });
    }

    @FXML
    private void initialize() {
        this.SelectedRoundProperty().addListener(this::onSelectedRoundChanged);
    }

    private void onSelectedRoundChanged(
            ObservableValue<? extends Number> observableValue, Number oldValue,
            Number newValue) {
        // clear the selected pairing node
        this.selectedPairingNode.set(null);

        // clear the pairing container and add pairing nodes for every pairing
        // there is in the selected round
        TournamentRound round = this.loadedTournament.getRounds().get(
                newValue.intValue());
        this.pairingContainer.getChildren().clear();
        for (int i = 0; i < round.getPairings().size(); i++) {
            PairingNode pairingNode = new PairingNode(this.loadedTournament,
                    round.getPairings().get(i), i);

            pairingNode.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                pairingNode.setSelected(true);
                this.selectedPairingNode.set(pairingNode);
            });

            this.pairingContainer.getChildren().add(pairingNode);
        }
    }

    @Override
    public void loadTournament(Tournament tournament) {
        this.loadedTournament = tournament;

        // automatically show newly added rounds
        tournament.getRounds().addListener(this::onTournamentListChanged);
    }

    @Override
    public void unloadTournament() {
        this.loadedTournament.getRounds().removeListener(
                this::onTournamentListChanged);
        this.loadedTournament = null;
    }

    private void onTournamentListChanged(
            Change<? extends TournamentRound> change) {
        if (change.next()) {
            this.setSelectedRound(this.loadedTournament.getRounds().size() - 1);
            this.refreshBreadcrumbs();
        }
    }

    /**
     * Refreshes the buttons in the breadcrumb bar.
     */
    private void refreshBreadcrumbs() {
        // throw out all old breadcrumb buttons
        this.breadcrumbContainer.getChildren().clear();

        int roundCount = this.loadedTournament.getRounds().size();
        for (int roundNumber = 0; roundNumber < roundCount; roundNumber++) {
            // Users don't like zero-based indices
            Button breadcrumb = new Button("Runde " + (roundNumber + 1));
            final int selectRound = roundNumber;

            // make the button select the correct round
            breadcrumb.setOnAction(event -> {
                this.setSelectedRound(selectRound);
            });

            // assign the correct style classes to our breadcrumb button
            breadcrumb.getStyleClass().add("breadcrumb-button");

            // this is our only button, we don't need additional style classes
            if (roundCount < 1) {
                return;
            }

            if (roundNumber == 0) {
                // this is our left-most breadcrumb
                breadcrumb.getStyleClass().add("left");
            } else if (roundNumber == roundCount - 1) {
                // this is our right-most breadcrumb
                breadcrumb.getStyleClass().add("right");
            } else {
                // seems like we're somewhere in-between left and right
                breadcrumb.getStyleClass().add("middle");
            }

            // add the breadcrumb to the breadcrumb bar
            this.breadcrumbContainer.getChildren().add(breadcrumb);
        }
    }

    /**
     * @return the SelectedRound property
     */
    public IntegerProperty SelectedRoundProperty() {
        if (this.selectedRound == null) {
            this.selectedRound = new SimpleIntegerProperty(-1);
        }
        return this.selectedRound;
    }

    /**
     * @return the value of the SelectedRound property
     */
    public int getSelectedRound() {
        return this.SelectedRoundProperty().get();
    }

    /**
     * @param value
     *            sets the new value for the SelectedRound property
     */
    public void setSelectedRound(int value) {
        this.SelectedRoundProperty().set(value);
    }

    /**
     * @return the selectedPairing property
     */
    public ObjectProperty<Pairing> selectedPairingProperty() {
        if (this.selectedPairing == null) {
            this.selectedPairing = new SimpleObjectProperty<>();
        }
        return this.selectedPairing;
    }

    /**
     * @return the value of the selectedPairing property
     */
    public Pairing getSelectedPairing() {
        return this.selectedPairingProperty().get();
    }

    /**
     * @param value
     *            sets the new value for the selectedPairing property
     */
    private void setSelectedPairing(Pairing value) {
        this.selectedPairingProperty().set(value);
    }
}

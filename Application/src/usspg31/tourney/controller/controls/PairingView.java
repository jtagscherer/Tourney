package usspg31.tourney.controller.controls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberExpression;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import usspg31.tourney.controller.PreferencesManager;
import usspg31.tourney.model.GamePhase;
import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.PairingHelper;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.Tournament;
import usspg31.tourney.model.TournamentRound;
import usspg31.tourney.model.pairingstrategies.DoubleElimination;
import usspg31.tourney.model.pairingstrategies.PairingStrategy;
import usspg31.tourney.model.pairingstrategies.SingleElimination;

/**
 * Control used in the EventPhaseView used to display and select pairings.
 */
public class PairingView extends VBox implements TournamentUser {
    private static final Logger log = Logger.getLogger(PairingView.class
            .getName());

    public static enum OverviewMode {
        PAIRING_OVERVIEW,
        PHASE_OVERVIEW
    }

    @FXML private Button buttonScrollBreadcrumbsLeft;
    @FXML private Button buttonScrollBreadcrumbsRight;
    @FXML private HBox breadcrumbContainer;
    @FXML private ScrollPane pairingScrollPane;
    @FXML private FlowPane pairingContainer;

    private Tournament loadedTournament;

    private IntegerProperty selectedRound;
    private IntegerProperty selectedPhase;

    private ObjectProperty<PairingNode> selectedPairingNode;
    private ObjectProperty<Pairing> selectedPairing;
    private ObjectProperty<OverviewMode> overviewMode;

    private ObjectProperty<Runnable> onNodeDoubleClicked;

    public PairingView() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(
                    "/ui/fxml/controls/pairing-view.fxml"), PreferencesManager
                    .getInstance().getSelectedLanguage().getLanguageBundle());
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        this.selectedPairingNode = new SimpleObjectProperty<>();
        this.selectedPairingNode.addListener((ov, o, n) -> {
            if (o != null) {
                o.setSelected(false);
            }
            if (n != null) {
                if (!n.isDisabled()) {
                    this.setSelectedPairing(n.getPairing());
                    n.setSelected(true);
                }
            } else {
                this.setSelectedPairing(null);
            }
        });
    }

    @FXML
    private void initialize() {
        this.SelectedRoundProperty().addListener(this::onSelectedRoundChanged);
        this.SelectedPhaseProperty().addListener(this::onSelectedPhaseChanged);
        this.overviewModeProperty().addListener(this::onOverviewModeChanged);
    }

    @Override
    public void loadTournament(Tournament tournament) {
        this.loadedTournament = tournament;

        // automatically show newly added rounds
        tournament.getRounds().addListener(this::onTournamentRoundListChanged);

        this.refreshBreadcrumbs();
        // this.setSelectedRound(tournament.getRounds().size());
    }

    @Override
    public void unloadTournament() {
        this.loadedTournament.getRounds().removeListener(
                this::onTournamentRoundListChanged);
        this.loadedTournament = null;
    }

    private void onSelectedRoundChanged(
            ObservableValue<? extends Number> observableValue, Number oldValue,
            Number newValue) {
        // clear the selected pairing node
        this.selectedPairingNode.set(null);
        this.updateOverview();
    }

    private void onSelectedPhaseChanged(
            ObservableValue<? extends Number> observableValue, Number oldValue,
            Number newValue) {
        this.selectedPairingNode.set(null);
        this.updateOverview();
    }

    private void onOverviewModeChanged(
            ObservableValue<? extends OverviewMode> observableValue,
            OverviewMode oldValue, OverviewMode newValue) {
        if (newValue == OverviewMode.PHASE_OVERVIEW) {
            if (this.getSelectedPhase() < 0) {
                this.setSelectedPhase(0);
            }
        }
        this.refreshBreadcrumbs();
        this.updateOverview();
    }

    private void onTournamentRoundListChanged(
            Change<? extends TournamentRound> change) {
        if (change.next()) {
            this.setSelectedRound(this.loadedTournament.getRounds().size() - 1);
            this.refreshBreadcrumbs();
        }
    }

    public void updateOverview() {
        this.pairingContainer.getChildren().clear();

        if (this.getOverviewMode() == OverviewMode.PHASE_OVERVIEW) {
            this.addPhaseOverviewNodes();
        } else {
            this.addPairingOverviewNodes();
        }
    }

    private void addPairingOverviewNodes() {
        // add pairing nodes for every pairing there is in the selected round
        TournamentRound round = this.loadedTournament.getRounds().get(
                this.getSelectedRound());
        boolean isCurrentRound = this.getSelectedRound() == this.loadedTournament
                .getRounds().size() - 1;
        for (int i = 0; i < round.getPairings().size(); i++) {
            PairingNode pairingNode = new PairingNode(this.loadedTournament,
                    round.getPairings().get(i), i + 1);
            if (!isCurrentRound) {
                pairingNode.setDisable(true);
            }
            pairingNode.setFocusTraversable(true);

            pairingNode.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                pairingNode.requestFocus();
                this.selectedPairingNode.set(pairingNode);
                if (event.getClickCount() == 2) {
                    this.getOnNodeDoubleClicked().run();
                }
            });

            this.pairingContainer.getChildren().add(pairingNode);
        }
    }

    private void addPhaseOverviewNodes() {
        PairingStrategy strategy = this.loadedTournament.getRuleSet()
                .getPhaseList().get(this.getSelectedPhase()).getPairingMethod();

        if (strategy instanceof SingleElimination) {
            this.addSingleEliminationNodes();
        } else if (strategy instanceof DoubleElimination) {
            this.addDoubleEliminationNodes();
        } else {
            this.pairingContainer.getChildren().add(
                    new Label(PreferencesManager.getInstance().localizeString(
                            "pairingview.cantdisplayphase")));
        }
    }

    private void addSingleEliminationNodes() {
        // Pane container = new Pane();

        List<List<Pairing>> pairings = new ArrayList<>();

        List<TournamentRound> rounds = this.getAffectedTournamentRounds(
                this.loadedTournament,
                this.getPhaseById(this.getSelectedPhase()));

        List<List<Pairing>> unsortedPairings = new ArrayList<>();
        for (TournamentRound round : rounds) {
            unsortedPairings.add(round.getPairings());
        }

        pairings = this.sortPairings(unsortedPairings);

        Pane container = new Pane();
        container.getChildren().addAll(
                this.generatePairingNodes(pairings,
                        new SimpleDoubleProperty(0),
                        new SimpleDoubleProperty(0)));

        this.pairingContainer.getChildren().add(container);
    }

    private List<Node> generatePairingNodes(List<List<Pairing>> pairings,
            NumberExpression xMin, NumberExpression yMin) {
        List<Node> nodes = new ArrayList<>();

        List<PairingNode> prevNodes = null;
        List<PairingNode> nextNodes = null;

        NumberExpression maxX = xMin;
        NumberExpression maxY = yMin;

        for (List<Pairing> round : pairings) {
            NumberExpression currentMaxX = maxX;

            nextNodes = new ArrayList<>();
            PairingNode prevNode = null;
            int pairingId = 1;
            for (Pairing pairing : round) {
                PairingNode node = new PairingNode(this.loadedTournament,
                        pairing, pairingId++);

                if (prevNode == null) {
                    node.layoutYProperty().bind(yMin);
                } else {
                    node.layoutYProperty().bind(
                            prevNode.layoutYProperty()
                                    .add(prevNode.heightProperty()).add(10));
                }

                node.layoutXProperty().bind(currentMaxX);
                nodes.add(node);
                nextNodes.add(node);

                maxX = Bindings.max(maxX,
                        node.layoutXProperty().add(node.widthProperty())
                                .add(50));
                maxY = Bindings.max(maxY,
                        node.layoutYProperty().add(node.heightProperty()));
                prevNode = node;
            }

            if (prevNodes != null && nextNodes.size() > 0) {
                this.createConnections(prevNodes, nextNodes, nodes);
            }
            prevNodes = nextNodes;

            maxX.add(50);
        }

        return nodes;
    }

    private List<List<Pairing>> sortPairings(
            List<List<Pairing>> unsortedPairings) {
        List<List<Pairing>> sortedPairings = new ArrayList<>();

        for (int i = unsortedPairings.size() - 1; i >= 0; i--) {
            // sort the pairings in the current round according to the pairings
            // in the next round
            if (i < unsortedPairings.size() - 1) {
                List<Pairing> pairingsInPreviousRound = unsortedPairings
                        .get(i + 1);
                List<Pairing> unorderedPairings = new ArrayList<>(
                        unsortedPairings.get(i));
                List<Pairing> orderedPairings = new ArrayList<>();

                for (Pairing previousPairing : pairingsInPreviousRound) {
                    for (Player player : previousPairing.getOpponents()) {
                        Pairing found = null;
                        for (Pairing p : unorderedPairings) {
                            if (p.getOpponents().contains(player)) {
                                found = p;
                                break;
                            }
                        }
                        if (found != null) {
                            orderedPairings.add(found);
                            unorderedPairings.remove(found);
                        }
                    }
                }
                // add pairings that aren't currently in the final list
                orderedPairings.addAll(unorderedPairings);
                sortedPairings.add(0, orderedPairings);
            } else {
                sortedPairings.add(unsortedPairings.get(i));
            }
        }

        return sortedPairings;
    }

    private void createConnections(List<PairingNode> previousPairings,
            List<PairingNode> nextPairings, List<Node> nodes) {
        Map<PairingNode, List<PairingNode>> precedingNodes = new HashMap<>();

        for (PairingNode prev : previousPairings) {
            Pairing prevPairing = prev.getPairing();
            for (Player prevPlayer : prevPairing.getOpponents()) {
                for (PairingNode next : nextPairings) {
                    List<PairingNode> predecessors = precedingNodes.get(next);
                    if (predecessors == null) {
                        predecessors = new ArrayList<>();
                        precedingNodes.put(next, predecessors);
                    }
                    Pairing nextPairing = next.getPairing();
                    if (nextPairing.getOpponents().contains(prevPlayer)) {
                        predecessors.add(prev);
                        this.createConnection(prev, prevPlayer, next, nodes);
                        break;
                    }
                }
            }
        }

        for (Entry<PairingNode, List<PairingNode>> entry : precedingNodes
                .entrySet()) {
            NumberExpression averageY = new SimpleDoubleProperty(0);

            for (PairingNode node : entry.getValue()) {
                averageY = averageY.add(node.layoutYProperty().add(
                        node.heightProperty().divide(2d)));
            }

            averageY = averageY.divide((double) entry.getValue().size());

            entry.getKey().layoutYProperty().unbind();
            entry.getKey()
                    .layoutYProperty()
                    .bind(averageY.subtract(entry.getKey().heightProperty()
                            .divide(2d)));
        }
    }

    private void createConnection(PairingNode previousNode,
            Player previousPlayer, PairingNode nextNode, List<Node> nodes) {
        CubicCurve curve = new CubicCurve();
        curve.setFill(Color.TRANSPARENT);
        curve.setStroke(Color.BLACK);
        curve.setStartX(0);
        curve.setStartY(0);
        curve.controlX1Property().bind(curve.endXProperty());
        curve.setControlY1(0);
        curve.setControlX2(0);
        curve.controlY2Property().bind(curve.endYProperty());
        curve.layoutXProperty().bind(
                previousNode.layoutXProperty()
                        .add(previousNode.widthProperty()));
        curve.layoutYProperty()
                .bind(previousNode.layoutYProperty().add(
                        previousNode
                                .heightProperty()
                                .multiply(
                                        previousNode.getPairing()
                                                .getOpponents()
                                                .indexOf(previousPlayer)
                                                / previousNode.getPairing()
                                                        .getOpponents().size())
                                .divide(2d)
                                .add(previousNode.heightProperty().divide(2d))));
        curve.endXProperty().bind(
                nextNode.layoutXProperty().subtract(curve.layoutXProperty()));
        curve.endYProperty().bind(
                nextNode.layoutYProperty()
                        .add(nextNode.heightProperty().divide(2d))
                        .subtract(curve.layoutYProperty()));

        nodes.add(curve);
    }

    private void addDoubleEliminationNodes() {
        Pane container = new Pane();

        List<List<Pairing>> winnerBracket = new ArrayList<>();
        List<List<Pairing>> loserBracket = new ArrayList<>();

        List<TournamentRound> rounds = this.getAffectedTournamentRounds(
                this.loadedTournament,
                this.getPhaseById(this.getSelectedPhase()));

        Set<Player> previousWinners = new HashSet<>();
        Set<Player> previousLosers = new HashSet<>();

        previousWinners.addAll(this.loadedTournament.getAttendingPlayers());

        // sort all pairings into winner and loser brackets, according to their
        // preceding pairing results
        for (int i = 0; i < rounds.size(); i++) {
            ArrayList<Pairing> winnerPairings = new ArrayList<>();
            ArrayList<Pairing> loserPairings = new ArrayList<>();

            for (Pairing pairing : rounds.get(i).getPairings()) {
                boolean isWinnerBracket = true;
                for (Player player : pairing.getOpponents()) {
                    if (previousLosers.contains(player)) {
                        isWinnerBracket = false;
                    }
                }
                (isWinnerBracket ? winnerPairings : loserPairings).add(pairing);
            }

            // add losers of the current round to the loser set
            for (Pairing pairing : rounds.get(i).getPairings()) {
                previousLosers.addAll(PairingHelper.identifyLoser(pairing));
            }
            // remove all people that previously lost from the winner set
            previousWinners.removeIf(player -> previousLosers.contains(player));

            winnerBracket.add(winnerPairings);
            loserBracket.add(loserPairings);
        }

        List<List<Pairing>> sortedWinnerBracket = this
                .sortPairings(winnerBracket);
        List<List<Pairing>> sortedLoserBracket = this
                .sortPairings(loserBracket);

        container.getChildren().addAll(
                this.generatePairingNodes(sortedWinnerBracket,
                        new SimpleDoubleProperty(0),
                        new SimpleDoubleProperty(0)));

        NumberExpression maxY = null;
        for (Node node : container.getChildren()) {
            if (node instanceof PairingNode) {
                PairingNode pairingNode = (PairingNode) node;
                if (maxY == null) {
                    maxY = pairingNode.layoutYProperty().add(
                            pairingNode.heightProperty());
                } else {
                    maxY = Bindings.max(maxY, pairingNode.layoutYProperty()
                            .add(pairingNode.heightProperty()));
                }
            }
        }

        container.getChildren().addAll(
                this.generatePairingNodes(sortedLoserBracket,
                        new SimpleDoubleProperty(0), maxY.add(50)));

        this.pairingContainer.getChildren().add(container);
    }

    private GamePhase getPhaseById(int phaseNumber) {
        return this.loadedTournament.getRuleSet().getPhaseList()
                .get(phaseNumber);
    }

    private List<TournamentRound> getAffectedTournamentRounds(
            Tournament tournament, GamePhase phase) {
        List<TournamentRound> affectedRounds = new ArrayList<>();

        int skipToRound = 0;
        for (GamePhase gamePhase : tournament.getRuleSet().getPhaseList()) {
            if (gamePhase == phase) {
                break;
            }
            skipToRound += gamePhase.getRoundCount();
        }

        for (int i = skipToRound; i < skipToRound + phase.getRoundCount()
                && i < tournament.getRounds().size(); i++) {
            affectedRounds.add(tournament.getRounds().get(i));
        }

        return affectedRounds;
    }

    /**
     * Refreshes the buttons in the breadcrumb bar.
     */
    private void refreshBreadcrumbs() {
        // throw out all old breadcrumb buttons
        this.breadcrumbContainer.getChildren().clear();

        if (this.getOverviewMode() == OverviewMode.PHASE_OVERVIEW) {
            this.addPhaseBreadcrumbs();
        } else {
            this.addRoundBreadcrumbs();
        }
    }

    private void addPhaseBreadcrumbs() {
        int phaseCount = this.loadedTournament.getRuleSet().getPhaseList()
                .size();

        GamePhase currentPhase = PairingHelper.findPhase(this.loadedTournament
                .getRounds().size() - 1, this.loadedTournament);
        int maxPhaseIndex = currentPhase.getPhaseNumber();

        for (int phaseNumber = 0; phaseNumber < phaseCount; phaseNumber++) {
            // Users don't like zero-based indices
            Button breadcrumb = new Button(PreferencesManager.getInstance()
                    .localizeString("pairingview.phase")
                    + " "
                    + (phaseNumber + 1));
            final int selectedPhase = phaseNumber;

            // make the button select the correct phase
            breadcrumb.setOnAction(event -> {
                this.setSelectedPhase(selectedPhase);
            });

            // assign the correct style classes to our breadcrumb button
            breadcrumb.getStyleClass().add("breadcrumb-button");

            if (phaseNumber == 0) {
                breadcrumb.getStyleClass().add("left");
            } else if (phaseNumber == phaseCount - 1) {
                breadcrumb.getStyleClass().add("right");
            } else {
                breadcrumb.getStyleClass().add("middle");
            }

            // disable breadcrumbs for phases that are yet to be begun
            breadcrumb.setDisable(phaseNumber > maxPhaseIndex);

            this.breadcrumbContainer.getChildren().add(breadcrumb);
        }
    }

    private void addRoundBreadcrumbs() {
        int roundCount = this.loadedTournament.getRounds().size();
        for (int roundNumber = 0; roundNumber < roundCount; roundNumber++) {
            // Users don't like zero-based indices
            Button breadcrumb = new Button(PreferencesManager.getInstance()
                    .localizeString("pairingview.round")
                    + " "
                    + (roundNumber + 1));
            final int selectRound = roundNumber;

            // make the button select the correct round
            breadcrumb.setOnAction(event -> {
                this.setSelectedRound(selectRound);
            });

            // assign the correct style classes to our breadcrumb button
            breadcrumb.getStyleClass().add("breadcrumb-button");

            if (roundNumber == 0) {
                // this is our left-most breadcrumb
                breadcrumb.getStyleClass().add("left");
            } else {
                // this is our right-most breadcrumb
                breadcrumb.getStyleClass().add("middle");
            }

            // add the breadcrumb to the breadcrumb bar
            this.breadcrumbContainer.getChildren().add(breadcrumb);
        }
    }

    /**
     * @return the OverviewMode property
     */
    public ObjectProperty<OverviewMode> overviewModeProperty() {
        if (this.overviewMode == null) {
            this.overviewMode = new SimpleObjectProperty<>(
                    OverviewMode.PAIRING_OVERVIEW);
        }
        return this.overviewMode;
    }

    /**
     * @return the current overview mode
     */
    public OverviewMode getOverviewMode() {
        return this.overviewModeProperty().get();
    }

    /**
     * @param value
     *            the overview mdoe to set
     */
    public void setOverviewMode(OverviewMode value) {
        this.overviewModeProperty().set(value);
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
     * @return the SelectedRound property
     */
    public IntegerProperty SelectedPhaseProperty() {
        if (this.selectedPhase == null) {
            this.selectedPhase = new SimpleIntegerProperty(-1);
        }
        return this.selectedPhase;
    }

    /**
     * @return the value of the SelectedRound property
     */
    public int getSelectedPhase() {
        return this.SelectedPhaseProperty().get();
    }

    /**
     * @param value
     *            sets the new value for the SelectedRound property
     */
    public void setSelectedPhase(int value) {
        this.SelectedPhaseProperty().set(value);
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

    public ObjectProperty<Runnable> onNodeDoubleClickedProperty() {
        if (this.onNodeDoubleClicked == null) {
            this.onNodeDoubleClicked = new SimpleObjectProperty<Runnable>(
                    () -> {});
        }
        return this.onNodeDoubleClicked;
    }

    public Runnable getOnNodeDoubleClicked() {
        return this.onNodeDoubleClickedProperty().get();
    }

    public void setOnNodeDoubleClicked(Runnable value) {
        this.onNodeDoubleClickedProperty().set(value);
    }
}

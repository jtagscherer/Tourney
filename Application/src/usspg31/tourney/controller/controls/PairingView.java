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

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;
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
    @FXML private ScrollPane breadcrumbScrollPane;
    @FXML private HBox breadcrumbContainer;
    @FXML private ScrollPane pairingScrollPane;
    @FXML private FlowPane pairingContainer;

    private Tournament loadedTournament;

    private static final double scaleDelta = 0.1;
    private static final double moveDelta = 1.0;

    private IntegerProperty selectedRound;
    private IntegerProperty selectedPhase;

    private ObjectProperty<PairingNode> selectedPairingNode;
    private ObjectProperty<Pairing> selectedPairing;
    private ObjectProperty<OverviewMode> overviewMode;

    private ObjectProperty<Runnable> onNodeDoubleClicked;

    public double lastMouseX = -1.0;
    public double lastMouseY = -1.0;

    private double pairingScale = 1.0;
    private double pairingX = 0.0;
    private double pairingY = 0.0;

    private double phaseScale = 1.0;
    private double phaseX = 0.0;
    private double phaseY = 0.0;

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

        /* Add a context menu to the pairing view */
        final ContextMenu contextMenu = new ContextMenu();
        MenuItem resetView = new MenuItem(PreferencesManager.getInstance()
                .localizeString("tournamentexecutionphase.resetview"));
        resetView.setOnAction(e -> PairingView.this.resetPairingView());
        contextMenu.getItems().addAll(resetView);

        this.pairingScrollPane.setContextMenu(contextMenu);

        Timeline scrollLeft = new Timeline(
                new KeyFrame(Duration.millis(10),
                        event -> {
                            this.breadcrumbScrollPane
                                    .setHvalue(this.breadcrumbScrollPane
                                            .getHvalue() - 0.01);
                        }));
        scrollLeft.setCycleCount(Timeline.INDEFINITE);
        this.buttonScrollBreadcrumbsLeft.addEventHandler(
                MouseEvent.MOUSE_PRESSED, event -> {
                    scrollLeft.play();
                });
        this.buttonScrollBreadcrumbsLeft.addEventHandler(
                MouseEvent.MOUSE_RELEASED, event -> {
                    scrollLeft.stop();
                });

        Timeline scrollRight = new Timeline(
                new KeyFrame(Duration.millis(10),
                        event -> {
                            this.breadcrumbScrollPane
                                    .setHvalue(this.breadcrumbScrollPane
                                            .getHvalue() + 0.01);
                        }));
        scrollRight.setCycleCount(Timeline.INDEFINITE);
        this.buttonScrollBreadcrumbsRight.addEventHandler(
                MouseEvent.MOUSE_PRESSED, event -> {
                    scrollRight.play();
                });
        this.buttonScrollBreadcrumbsRight.addEventHandler(
                MouseEvent.MOUSE_RELEASED, event -> {
                    scrollRight.stop();
                });
    }

    @FXML
    private void initialize() {
        this.overviewMode = new SimpleObjectProperty<PairingView.OverviewMode>();
        this.SelectedRoundProperty().addListener(this::onSelectedRoundChanged);
        this.SelectedPhaseProperty().addListener(this::onSelectedPhaseChanged);
        this.overviewModeProperty().addListener(this::onOverviewModeChanged);

        /* Enable zooming */
        this.pairingContainer.setOnScroll(event -> {
            event.consume();

            double scaleFactor = 0;
            if (event.getDeltaY() > 0) {
                scaleFactor = 1 + PairingView.scaleDelta;
            } else if (event.getDeltaY() < 0) {
                scaleFactor = 1 - PairingView.scaleDelta;
            }

            if (scaleFactor > 1
                    && PairingView.this.pairingContainer.getScaleX() < 5) {
                PairingView.this.pairingContainer
                        .setScaleX(PairingView.this.pairingContainer
                                .getScaleX() * scaleFactor);
                PairingView.this.pairingContainer
                        .setScaleY(PairingView.this.pairingContainer
                                .getScaleY() * scaleFactor);
            }
            if (scaleFactor < 1
                    && PairingView.this.pairingContainer.getScaleX() > 0.2) {
                PairingView.this.pairingContainer
                        .setScaleX(PairingView.this.pairingContainer
                                .getScaleX() * scaleFactor);
                PairingView.this.pairingContainer
                        .setScaleY(PairingView.this.pairingContainer
                                .getScaleY() * scaleFactor);
            }
        });

        /* Enable moving the view */
        this.pairingContainer.setOnMouseDragged(event -> {
            event.consume();

            if (PairingView.this.lastMouseX == -1.0
                    || PairingView.this.lastMouseY == -1.0) {
                PairingView.this.lastMouseX = event.getX();
                PairingView.this.lastMouseY = event.getY();
            } else {
                for (Node child : PairingView.this.pairingContainer
                        .getChildren()) {
                    child.setTranslateX(child.getTranslateX()
                            + (event.getX() - PairingView.this.lastMouseX)
                            * PairingView.moveDelta);
                    child.setTranslateY(child.getTranslateY()
                            + (event.getY() - PairingView.this.lastMouseY)
                            * PairingView.moveDelta);
                }
                PairingView.this.lastMouseX = event.getX();
                PairingView.this.lastMouseY = event.getY();
            }
        });

        this.pairingContainer.setOnMouseReleased(event -> {
            event.consume();

            PairingView.this.lastMouseX = -1.0;
            PairingView.this.lastMouseY = -1.0;
        });
    }

    @Override
    public void loadTournament(Tournament tournament) {
        this.loadedTournament = tournament;

        // automatically show newly added rounds
        tournament.getRounds().addListener(this::onTournamentRoundListChanged);

        this.refreshBreadcrumbs();
        this.setSelectedRound(tournament.getRounds().size() - 1);
    }

    @Override
    public void unloadTournament() {
        if (this.loadedTournament != null) {
            this.loadedTournament.getRounds().removeListener(
                    this::onTournamentRoundListChanged);
        }
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
        this.refreshBreadcrumbs();
        if (newValue == OverviewMode.PHASE_OVERVIEW) {
            if (this.getSelectedPhase() < 0) {
                this.setSelectedPhase(0);
            }
        }
        this.updateOverview();
    }

    private void onTournamentRoundListChanged(
            Change<? extends TournamentRound> change) {
        if (change.next()) {
            this.refreshBreadcrumbs();
            this.setSelectedRound(this.loadedTournament.getRounds().size() - 1);
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
            if (round.size() == 0) {
                continue;
            }
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

        Line horizontal1 = new Line();
        horizontal1.setFill(Color.TRANSPARENT);
        horizontal1.setStroke(Color.BLACK);
        horizontal1.setStrokeWidth(2);
        Line vertical = new Line();
        vertical.setFill(Color.TRANSPARENT);
        vertical.setStroke(Color.BLACK);
        vertical.setStrokeWidth(2);
        Line horizontal2 = new Line();
        horizontal2.setFill(Color.TRANSPARENT);
        horizontal2.setStroke(Color.BLACK);
        horizontal2.setStrokeWidth(2);

        NumberExpression left = new DoubleBinding() {
            { super.bind(previousNode.layoutXProperty(), previousNode.widthProperty()); }
            @Override
            protected double computeValue() {
                return previousNode.getLayoutX() + previousNode.getWidth();
            }
        };
        NumberExpression top = new DoubleBinding() {
            { super.bind(previousNode.layoutYProperty(), previousNode.heightProperty()); }
            @Override
            protected double computeValue() {
                return Math.floor(previousNode.getLayoutY() + previousNode.getHeight() / 2d);
            }
        };
        NumberExpression bottom = new DoubleBinding() {
            { super.bind(nextNode.layoutYProperty(), nextNode.heightProperty()); }
            @Override
            protected double computeValue() {
                return Math.floor(nextNode.getLayoutY() + nextNode.getHeight() / 2d);
            }
        };
        NumberExpression center = new DoubleBinding() {
            { super.bind(nextNode.layoutXProperty()); }
            @Override
            protected double computeValue() {
                return Math.floor(nextNode.getLayoutX() - 25);
            }
        };

        horizontal1.startXProperty().bind(left);
        horizontal1.endXProperty().bind(center);
        horizontal1.startYProperty().bind(top);
        horizontal1.endYProperty().bind(top);

        vertical.startXProperty().bind(center);
        vertical.endXProperty().bind(center);
        vertical.startYProperty().bind(top);
        vertical.endYProperty().bind(bottom);

        horizontal2.startXProperty().bind(center);
        horizontal2.endXProperty().bind(nextNode.layoutXProperty());
        horizontal2.startYProperty().bind(bottom);
        horizontal2.endYProperty().bind(bottom);

        nodes.add(horizontal1);
        nodes.add(vertical);
        nodes.add(horizontal2);
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

            // disable breadcrumbs for phases that are yet to be begin
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
        switch (value) {
        case PAIRING_OVERVIEW:
            this.phaseScale = this.pairingContainer.getScaleX();
            if (this.pairingContainer.getChildren().size() > 0) {
                this.phaseX = this.pairingContainer.getChildren().get(0)
                        .getTranslateX();
                this.phaseY = this.pairingContainer.getChildren().get(0)
                        .getTranslateY();
            }

            this.overviewModeProperty().set(value);
            this.setSelectedRound(this.getSelectedRound());

            this.pairingContainer.setScaleX(this.pairingScale);
            this.pairingContainer.setScaleY(this.pairingScale);
            for (Node child : this.pairingContainer.getChildren()) {
                child.setTranslateX(child.getTranslateX() + this.pairingX);
                child.setTranslateY(child.getTranslateY() + this.pairingY);
            }
            break;
        case PHASE_OVERVIEW:
            this.pairingScale = this.pairingContainer.getScaleX();
            if (this.pairingContainer.getChildren().size() > 0) {
                this.pairingX = this.pairingContainer.getChildren().get(0)
                        .getTranslateX();
                this.pairingY = this.pairingContainer.getChildren().get(0)
                        .getTranslateY();
            }

            this.overviewModeProperty().set(value);
            this.setSelectedPhase(this.getSelectedPhase());

            this.pairingContainer.setScaleX(this.phaseScale);
            this.pairingContainer.setScaleY(this.phaseScale);
            for (Node child : this.pairingContainer.getChildren()) {
                child.setTranslateX(child.getTranslateX() + this.phaseX);
                child.setTranslateY(child.getTranslateY() + this.phaseY);

            }
            break;
        }
    }

    private void resetPairingView() {
        this.pairingContainer.setScaleX(1.0);
        this.pairingContainer.setScaleY(1.0);
        for (Node child : this.pairingContainer.getChildren()) {
            child.setTranslateX(0.0);
            child.setTranslateY(0.0);

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
        int nodeCount = 0;
        Node selectedBreadcrumb = null;
        for (Node breadcrumbNode : this.breadcrumbContainer.getChildren()) {
            Button breadcrumbButton = (Button) breadcrumbNode;
            if (breadcrumbButton.getText().startsWith(
                    PreferencesManager.getInstance().localizeString(
                            "pairingview.round"))) {
                breadcrumbButton.getStyleClass().remove("selected-button");

                if (nodeCount == value) {
                    selectedBreadcrumb = breadcrumbNode;
                }
            }

            nodeCount++;
        }
        if (selectedBreadcrumb != null) {
            ((Button) selectedBreadcrumb).getStyleClass()
                    .add("selected-button");
        }

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
        int nodeCount = 0;
        Node selectedBreadcrumb = null;
        for (Node breadcrumbNode : this.breadcrumbContainer.getChildren()) {
            Button breadcrumbButton = (Button) breadcrumbNode;
            if (breadcrumbButton.getText().startsWith(
                    PreferencesManager.getInstance().localizeString(
                            "pairingview.phase"))) {
                if (nodeCount == value) {
                    selectedBreadcrumb = breadcrumbNode;
                }

                breadcrumbButton.getStyleClass().remove("selected-button");
            }

            nodeCount++;
        }
        if (selectedBreadcrumb != null) {
            ((Button) selectedBreadcrumb).getStyleClass()
                    .add("selected-button");
        }

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

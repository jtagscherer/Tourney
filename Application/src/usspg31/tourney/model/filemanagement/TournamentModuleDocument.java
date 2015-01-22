package usspg31.tourney.model.filemanagement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.ObservableList;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import usspg31.tourney.model.Bye;
import usspg31.tourney.model.Bye.ByeType;
import usspg31.tourney.model.GamePhase;
import usspg31.tourney.model.PossibleScoring;
import usspg31.tourney.model.TournamentModule;
import usspg31.tourney.model.pairingstrategies.PairingStrategy;

/**
 * A class that wraps an XML document which holds all information that are
 * necessary to describe the rules of a tournament.
 * 
 * @author Jan Tagscherer
 */
public class TournamentModuleDocument {
    private static final Logger log = Logger.getLogger(FileSaver.class
            .getName());

    private Document document;
    private Element rootElement;

    /**
     * Create a new tournament module document
     * 
     * @param document
     *            XML document source to be used
     */
    public TournamentModuleDocument(Document document) {
        this.document = document;

        /* Create the root element of the XML document */
        if (this.document.getFirstChild() == null) {
            this.rootElement = this.document.createElement("rule-template");
            this.document.appendChild(this.rootElement);
        }
    }

    /**
     * Append all meta data to this document
     * 
     * @param tournamentModule
     *            Tournament module to be used for reading its meta data
     */
    public void appendMetaData(TournamentModule tournamentModule) {
        Element meta = this.document.createElement("meta");
        this.rootElement.appendChild(meta);

        /* Add the name of this module template */
        Element name = this.document.createElement("name");
        meta.appendChild(name);
        name.appendChild(this.document.createTextNode(tournamentModule
                .getName()));

        /* Add the description of this module template */
        Element description = this.document.createElement("description");
        meta.appendChild(description);
        description.appendChild(this.document.createTextNode(tournamentModule
                .getDescription()));
    }

    /**
     * Get the name of the tournament module in this document
     * 
     * @return The name of the tournament module
     */
    public String getName() {
        Node meta = this.document.getElementsByTagName("meta").item(0);
        return FileLoader.getFirstChildNodeByTag(meta, "name").getTextContent();
    }

    /**
     * Get the description of the tournament module in this document
     * 
     * @return The description of the tournament module
     */
    public String getDescription() {
        Node meta = this.document.getElementsByTagName("meta").item(0);
        return FileLoader.getFirstChildNodeByTag(meta, "description")
                .getTextContent();
    }

    /**
     * Append all possible scores including all scoring priorities to the
     * document
     * 
     * @param possibleScores
     *            List of map of possible scores
     */
    public void appendPossibleScores(
            ObservableList<PossibleScoring> possibleScores) {
        /* Create a tag that will hold all possible scorings */
        Element possibleScoresElement = this.document
                .createElement("possible-scores");
        this.rootElement.appendChild(possibleScoresElement);

        /* Add all possible scorings */
        for (PossibleScoring scoringPriority : possibleScores) {
            /* Add a new tag for each scoring with its priority as an attribute */
            Element scoringElement = this.document.createElement("scoring");
            Attr scoringPriorityAttribute = this.document
                    .createAttribute("priority");
            scoringPriorityAttribute.setValue(String.valueOf(scoringPriority
                    .getPriority()));
            scoringElement.setAttributeNode(scoringPriorityAttribute);
            possibleScoresElement.appendChild(scoringElement);

            /* Iterate over the hash map of scores */
            Iterator<Entry<String, Integer>> iterator = scoringPriority
                    .getScores().entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, Integer> entry = iterator.next();

                /* Add the current score */
                Element scoreElement = this.document.createElement("score");
                scoringElement.appendChild(scoreElement);

                /* Add the name of the score */
                Element nameElement = this.document.createElement("name");
                scoreElement.appendChild(nameElement);
                nameElement.appendChild(this.document.createTextNode(entry
                        .getKey()));

                /* Add the associated points */
                Element pointsElement = this.document.createElement("points");
                scoreElement.appendChild(pointsElement);
                pointsElement.appendChild(this.document.createTextNode(String
                        .valueOf(entry.getValue())));
            }
        }
    }

    /**
     * Extract a list of possible scores from the module document
     * 
     * @return List of possible scores
     */
    public ArrayList<PossibleScoring> getPossibleScores() {
        ArrayList<PossibleScoring> possibleScores = new ArrayList<PossibleScoring>();

        /* Get the node containing all scorings */
        Node scores = this.document.getElementsByTagName("possible-scores")
                .item(0);

        /* Iterate over all scorings in the node */
        for (Node scoring : FileLoader.getChildNodesByTag(scores, "scoring")) {
            PossibleScoring newScoring = new PossibleScoring();

            /* Extract the priority from the attribute */
            newScoring.setPriority(Integer.valueOf(scoring.getAttributes()
                    .getNamedItem("priority").getTextContent()));

            /* Extract the name and the associated points */
            for (Node score : FileLoader.getChildNodesByTag(scoring, "score")) {
                newScoring.getScores().put(
                        FileLoader.getFirstChildNodeByTag(score, "name")
                                .getTextContent(),
                        Integer.valueOf(FileLoader.getFirstChildNodeByTag(
                                score, "points").getTextContent()));
            }

            possibleScores.add(newScoring);
        }

        return possibleScores;
    }

    /**
     * Append a list of game phases to the document
     * 
     * @param gamePhases
     *            List of game phases to be appended
     */
    public void appendTournamentPhases(ObservableList<GamePhase> gamePhases) {
        /* Create a node that will hold all game phases */
        Element gamePhasesElement = this.document.createElement("game-phases");
        this.rootElement.appendChild(gamePhasesElement);

        /* Add a child node for each game phase */
        for (GamePhase phase : gamePhases) {
            Element phaseElement = this.document.createElement("game-phase");
            gamePhasesElement.appendChild(phaseElement);

            /* Add the phase number */
            Element phaseNumberElement = this.document
                    .createElement("phase-number");
            phaseElement.appendChild(phaseNumberElement);
            phaseNumberElement.appendChild(this.document.createTextNode(String
                    .valueOf(phase.getPhaseNumber())));

            /* Add the number of rounds */
            Element numberOfRoundsElement = this.document
                    .createElement("number-of-rounds");
            phaseElement.appendChild(numberOfRoundsElement);
            numberOfRoundsElement.appendChild(this.document
                    .createTextNode(String.valueOf(phase.getRoundCount())));

            /* Add the used pairing strategy */
            Element pairingStrategyElement = this.document
                    .createElement("pairing-strategy");
            phaseElement.appendChild(pairingStrategyElement);
            pairingStrategyElement.appendChild(this.document
                    .createTextNode(phase.getPairingMethod().getClass()
                            .getName()));

            /* Add the number of players in a pairing */
            Element participantNumberElement = this.document
                    .createElement("opponents-in-pairing");
            phaseElement.appendChild(participantNumberElement);
            participantNumberElement
                    .appendChild(this.document.createTextNode(String
                            .valueOf(phase.getNumberOfOpponents())));

            /*
             * Add the number of players that should be kept after cutting off
             * this round
             */
            Element cutOffElement = this.document
                    .createElement("cutoff-number");
            phaseElement.appendChild(cutOffElement);
            cutOffElement.appendChild(this.document.createTextNode(String
                    .valueOf(phase.getCutoff())));

            /* Add the duration of a round in this game phase */
            Element durationElement = this.document
                    .createElement("round-duration");
            phaseElement.appendChild(durationElement);
            durationElement.appendChild(this.document.createTextNode(phase
                    .getRoundDuration().toString()));
        }
    }

    /**
     * Get a list of all tournament phases present in this document
     * 
     * @return A list of all tournament phases
     */
    public ArrayList<GamePhase> getTournamentPhases() {
        ArrayList<GamePhase> tournamentPhases = new ArrayList<GamePhase>();

        /* Get the node that contains all tournament phases */
        Node gamePhases = this.document.getElementsByTagName("game-phases")
                .item(0);

        /* Extract all individual game phases */
        for (Node phaseNode : FileLoader.getChildNodesByTag(gamePhases,
                "game-phase")) {
            GamePhase phase = new GamePhase();

            /* Extract the phase number and the number of rounds */
            phase.setPhaseNumber(Integer.valueOf(FileLoader
                    .getFirstChildNodeByTag(phaseNode, "phase-number")
                    .getTextContent()));
            phase.setRoundCount(Integer.valueOf(FileLoader
                    .getFirstChildNodeByTag(phaseNode, "number-of-rounds")
                    .getTextContent()));

            /* Extract the pairing strategy using reflection */
            String pairingMethod = FileLoader.getFirstChildNodeByTag(phaseNode,
                    "pairing-strategy").getTextContent();
            Class<?> pairingStrategyClass;
            try {
                pairingStrategyClass = Class.forName(pairingMethod);
                phase.setPairingMethod((PairingStrategy) (pairingStrategyClass
                        .newInstance()));
            } catch (Exception e) {
                log.log(Level.SEVERE,
                        "Could not instantiate the pairing strategy while loading the tournament rules.");
                log.log(Level.SEVERE, e.getMessage(), e);
            }

            /* Extract number of the opponents in one pairing */
            phase.setNumberOfOpponents(Integer.valueOf(FileLoader
                    .getFirstChildNodeByTag(phaseNode, "opponents-in-pairing")
                    .getTextContent()));

            /* Extract the number of players that will be kept after this phase */
            phase.setCutoff(Integer.valueOf(FileLoader.getFirstChildNodeByTag(
                    phaseNode, "cutoff-number").getTextContent()));

            /* Extract the duration of one round in this game phase */
            phase.setRoundDuration(Duration.parse(FileLoader
                    .getFirstChildNodeByTag(phaseNode, "round-duration")
                    .getTextContent()));

            tournamentPhases.add(phase);
        }

        return tournamentPhases;
    }

    /**
     * Append a list of byes that can be used in this tournament rule set
     * 
     * @param byeList
     *            List of byes to be appended
     */
    public void appendByeList(ObservableList<Bye> byeList) {
        /* Create a node that will hold all byes */
        Element byesElement = this.document.createElement("byes");
        this.rootElement.appendChild(byesElement);

        /* Add a child node for each bye */
        for (Bye bye : byeList) {
            Element byeElement = this.document.createElement("bye");
            byesElement.appendChild(byeElement);

            /* Add the bye type */
            Element byeTypeElement = this.document.createElement("type");
            byeElement.appendChild(byeTypeElement);
            byeTypeElement.appendChild(this.document.createTextNode(String
                    .valueOf(bye.getByeType())));

            /* Add the points associated with this bye */
            Element byePointsElements = this.document.createElement("points");
            byeElement.appendChild(byePointsElements);

            for (int score : bye.getByePoints()) {
                Element byeScoreElement = this.document.createElement("score");
                byeScoreElement.appendChild(this.document.createTextNode(String
                        .valueOf(score)));
                byePointsElements.appendChild(byeScoreElement);
            }
        }
    }

    /**
     * Get the list of byes that can be used in this tournament rule set
     * 
     * @return The list of byes that can be used in this tournament rule set
     */
    public ArrayList<Bye> getByeList() {
        ArrayList<Bye> byeList = new ArrayList<Bye>();

        /* Get the node that contains all byes */
        Node byes = this.document.getElementsByTagName("byes").item(0);

        /* Extract all individual byes */
        for (Node byeNode : FileLoader.getChildNodesByTag(byes, "bye")) {
            Bye bye = new Bye();

            /* Extract the bye type and the associated points */
            bye.setByeType(ByeType.valueOf(FileLoader.getFirstChildNodeByTag(
                    byeNode, "type").getTextContent()));

            Node pointsNode = FileLoader.getFirstChildNodeByTag(byeNode,
                    "points");

            for (Node scoreNode : FileLoader.getChildNodesByTag(pointsNode,
                    "score")) {
                bye.getByePoints().add(
                        Integer.parseInt(scoreNode.getTextContent()));
            }

            byeList.add(bye);
        }

        return byeList;
    }

    /**
     * Get the source document of this event document
     * 
     * @return Source document of this event document
     */
    public Document getDocument() {
        return this.document;
    }

    /**
     * Set the source document of this event document
     * 
     * @param document
     *            Source document of this event document
     */
    public void setDocument(Document document) {
        this.document = document;
    }
}

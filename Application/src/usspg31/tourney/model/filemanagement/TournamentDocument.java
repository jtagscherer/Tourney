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
import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.Pairing.PairingFlag;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.PlayerScore;
import usspg31.tourney.model.PossibleScoring;
import usspg31.tourney.model.Tournament;
import usspg31.tourney.model.TournamentAdministrator;
import usspg31.tourney.model.TournamentModule;
import usspg31.tourney.model.TournamentRound;
import usspg31.tourney.model.pairingstrategies.PairingStrategy;

/**
 * An XML document that represents a tournament
 * 
 * @author Jan Tagscherer
 */
public class TournamentDocument {
    private static final Logger log = Logger.getLogger(FileSaver.class
            .getName());

    private Document document;
    private Element rootElement;

    private String id;

    /*
     * public static final int REGISTERED_PLAYERS = 0; public static final int
     * ATTENDANT_PLAYERS = 1; public static final int REMAINING_PLAYERS = 2;
     */

    public enum PlayerListType {
        REGISTERED_PLAYERS,
        ATTENDANT_PLAYERS,
        REMAINING_PLAYERS,
        RECEIVED_BYE_PLAYERS
    }

    /**
     * Create a new event document
     * 
     * @param document
     *            XML document source to be used
     */
    public TournamentDocument(Document document) {
        this.document = document;

        /* Create a new root element in the XML document */
        if (this.document.getFirstChild() == null) {
            this.rootElement = this.document.createElement("tournament");
            this.document.appendChild(this.rootElement);
        }
    }

    /**
     * Append all meta data to this document
     * 
     * @param tournament
     *            Tournament to be used for reading its meta data
     */
    public void appendMetaData(Tournament tournament) {
        Element meta = this.document.createElement("meta");
        this.rootElement.appendChild(meta);

        /* Add the tournament name to the meta data */
        Element name = this.document.createElement("name");
        meta.appendChild(name);
        name.appendChild(this.document.createTextNode(tournament.getName()));
    }

    /**
     * Extract the tournament name out of the attached meta data
     * 
     * @return Name of the tournament
     */
    public String getTournamentName() {
        String name = null;

        /* Extract the tournament from the corresponding XML tag */
        Node metaData = this.document.getElementsByTagName("meta").item(0);
        name = FileLoader.getFirstChildNodeByTag(metaData, "name")
                .getTextContent();

        return name;
    }

    /**
     * Append a list of all tournament administrators to this document
     * 
     * @param administrators
     *            List of tournament administrators to be appended
     */
    public void appendAdministratorList(
            ObservableList<TournamentAdministrator> administrators) {
        /* Add a tag that contains all tournament administrators */
        Element administratorsElement = this.document
                .createElement("tournament-administrators");
        this.rootElement.appendChild(administratorsElement);

        for (TournamentAdministrator administrator : administrators) {
            Element administratorElement = this.document
                    .createElement("administrator");
            administratorsElement.appendChild(administratorElement);

            /* Add the name of the administrator */
            Element administratorName = this.document.createElement("name");
            administratorElement.appendChild(administratorName);

            Element firstName = this.document.createElement("first-name");
            administratorName.appendChild(firstName);
            firstName.appendChild(this.document.createTextNode(administrator
                    .getFirstName()));

            Element lastName = this.document.createElement("last-name");
            administratorName.appendChild(lastName);
            lastName.appendChild(this.document.createTextNode(administrator
                    .getLastName()));

            /* Add the mail address of the administrator */
            Element administratorMail = this.document
                    .createElement("mail-address");
            administratorElement.appendChild(administratorMail);
            administratorMail.appendChild(this.document
                    .createTextNode(administrator.getMailAddress()));

            /* Add the phone number of the administrator */
            Element administratorPhone = this.document
                    .createElement("phone-number");
            administratorElement.appendChild(administratorPhone);
            administratorPhone.appendChild(this.document
                    .createTextNode(administrator.getPhoneNumber()));
        }
    }

    /**
     * Extract a list of tournament administrators from this document
     * 
     * @return List of tournament administrators
     */
    public ArrayList<TournamentAdministrator> getTournamentAdministrators() {
        ArrayList<TournamentAdministrator> administrators = new ArrayList<TournamentAdministrator>();

        /* Get the tag containing all tournament administrators */
        Node tournamentAdministrators = this.document.getElementsByTagName(
                "tournament-administrators").item(0);

        for (Node admin : FileLoader.getChildNodesByTag(
                tournamentAdministrators, "administrator")) {
            TournamentAdministrator administrator = new TournamentAdministrator();

            /* Extract the name of the administrator */
            Node adminName = FileLoader.getFirstChildNodeByTag(admin, "name");
            administrator.setFirstName(FileLoader.getFirstChildNodeByTag(
                    adminName, "first-name").getTextContent());
            administrator.setLastName(FileLoader.getFirstChildNodeByTag(
                    adminName, "last-name").getTextContent());

            /* Extract the mail address of the administrator */
            administrator.setMailAdress(FileLoader.getFirstChildNodeByTag(
                    admin, "mail-address").getTextContent());

            /* Extract the phone number of the administrator */
            administrator.setPhoneNumber(FileLoader.getFirstChildNodeByTag(
                    admin, "phone-number").getTextContent());

            administrators.add(administrator);
        }

        return administrators;
    }

    /**
     * Append a list of references to players to this document
     * 
     * @param players
     *            List of players to be used
     * @param playerType
     *            The type of players that should be referenced, can be
     *            TournamentDocument.REGISTERED_PLAYERS,
     *            TournamentDocument.ATTENDANT_PLAYERS or
     *            TournamentDocument.REMAINING_PLAYERS
     */
    public void appendPlayerList(ObservableList<Player> players,
            PlayerListType playerType) {
        Element playersElement = null;

        /*
         * Create a new element containing all players in the list depending on
         * what kind of player list should be appended
         */
        switch (playerType) {
        case REGISTERED_PLAYERS:
            playersElement = this.document.createElement("registered-players");
            break;
        case ATTENDANT_PLAYERS:
            playersElement = this.document.createElement("attendant-players");
            break;
        case REMAINING_PLAYERS:
            playersElement = this.document.createElement("remaining-players");
            break;
        case RECEIVED_BYE_PLAYERS:
            playersElement = this.document
                    .createElement("received-bye-players");
            break;
        }

        this.rootElement.appendChild(playersElement);

        for (Player player : players) {
            /* Add the current player from the list */
            Element playerElement = this.document.createElement("player");
            playersElement.appendChild(playerElement);

            /* Add the player's unique id */
            Element playerId = this.document.createElement("player-id");
            playerElement.appendChild(playerId);
            playerId.appendChild(this.document.createTextNode(player.getId()));
        }
    }

    /**
     * Extract a list of players from this document
     * 
     * @param playerType
     *            Player type that should be extracted
     * @param playerList
     *            A list of players that may be referenced in this tournament
     * @return The desired list of players
     */
    public ArrayList<Player> getPlayerList(PlayerListType playerType,
            ArrayList<Player> playerList) {
        ArrayList<Player> attachedPlayers = new ArrayList<Player>();

        Node playerNode = null;

        /*
         * Get the tag containing all players depending on the type of player
         * list
         */
        switch (playerType) {
        case ATTENDANT_PLAYERS:
            playerNode = this.document
                    .getElementsByTagName("attendant-players").item(0);
            break;
        case REGISTERED_PLAYERS:
            playerNode = this.document.getElementsByTagName(
                    "registered-players").item(0);
            break;
        case REMAINING_PLAYERS:
            playerNode = this.document
                    .getElementsByTagName("remaining-players").item(0);
            break;
        case RECEIVED_BYE_PLAYERS:
            playerNode = this.document.getElementsByTagName(
                    "received-bye-players").item(0);
            break;
        }

        /* Load all players and their ids from the file */
        for (Node player : FileLoader.getChildNodesByTag(playerNode, "player")) {
            String id = FileLoader.getFirstChildNodeByTag(player, "player-id")
                    .getTextContent();

            for (Player listedPlayer : playerList) {
                if (listedPlayer.getId().equals(id)) {
                    attachedPlayers.add(listedPlayer);
                }
            }
        }

        return attachedPlayers;
    }

    /**
     * Append a list of tournament rounds to this document
     * 
     * @param tournamentRounds
     *            List of tournament rounds to be used
     */
    public void appendTournamentRounds(
            ObservableList<TournamentRound> tournamentRounds) {
        /* Create the tag that will contain all rounds */
        Element tournamentRoundsElement = this.document
                .createElement("tournament-rounds");
        this.rootElement.appendChild(tournamentRoundsElement);

        for (TournamentRound tournamentRound : tournamentRounds) {
            /* Create a new tag for the current round */
            Element roundElement = this.document
                    .createElement("tournament-round");
            tournamentRoundsElement.appendChild(roundElement);

            /* Add the number of the current tournament round */
            Element roundName = this.document.createElement("round-number");
            roundElement.appendChild(roundName);
            roundName.appendChild(this.document.createTextNode(String
                    .valueOf(tournamentRound.getRoundNumber())));

            /* Add all pairings of the current tournament round */
            Element pairings = this.document.createElement("pairings");
            roundElement.appendChild(pairings);

            for (Pairing pairing : tournamentRound.getPairings()) {
                /* Add a new tag for every pairing in this round */
                Element pairingElement = this.document.createElement("pairing");
                pairings.appendChild(pairingElement);

                /* Add the flag of the pairing */
                Element pairingFlag = this.document.createElement("flag");
                pairingElement.appendChild(pairingFlag);
                pairingFlag.appendChild(this.document.createTextNode(pairing
                        .getFlag().toString()));

                /* Add all participants of the pairing */
                Element participants = this.document
                        .createElement("participants");
                pairingElement.appendChild(participants);

                for (Player participant : pairing.getOpponents()) {
                    /* Add a new tag for every participant in this pairing */
                    Element participantElement = this.document
                            .createElement("player");
                    participants.appendChild(participantElement);

                    /* Add the unique identification string of the player */
                    Element playerId = this.document.createElement("player-id");
                    participantElement.appendChild(playerId);
                    playerId.appendChild(this.document
                            .createTextNode(participant.getId()));

                    /* Add the scores of the current participant */
                    Element scoreElement = this.document
                            .createElement("scores");
                    participantElement.appendChild(scoreElement);

                    for (PlayerScore score : pairing.getScoreTable()) {
                        /*
                         * Add the scores of the current participant from the
                         * score table
                         */
                        if (score.getPlayer() == participant) {
                            for (Integer scoreInteger : score.getScore()) {
                                /* Add all scores of this player as integers */
                                Element scoreIntegerElement = this.document
                                        .createElement("score");
                                scoreElement.appendChild(scoreIntegerElement);
                                scoreIntegerElement.appendChild(this.document
                                        .createTextNode(String
                                                .valueOf(scoreInteger)));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Extract a list of tournament rounds from the document
     * 
     * @param playerList
     *            A list of players that may be referenced in these rounds
     * @return List of tournament rounds
     */
    public ArrayList<TournamentRound> getTournamentRounds(
            ArrayList<Player> playerList) {
        ArrayList<TournamentRound> tournamentRounds = new ArrayList<TournamentRound>();

        /* Get the tag containing all rounds */
        Node tournamentRoundsNode = this.document.getElementsByTagName(
                "tournament-rounds").item(0);

        /* Iterate over all rounds */
        for (Node round : FileLoader.getChildNodesByTag(tournamentRoundsNode,
                "tournament-round")) {
            TournamentRound tournamentRound = new TournamentRound(0);

            /* Extract the round number */
            tournamentRound.setRoundNumber(Integer.parseInt(FileLoader
                    .getFirstChildNodeByTag(round, "round-number")
                    .getTextContent()));

            Node pairingNode = FileLoader.getFirstChildNodeByTag(round,
                    "pairings");

            /* Iterate over every pairing in this round */
            for (Node pairing : FileLoader.getChildNodesByTag(pairingNode,
                    "pairing")) {
                Pairing roundPairing = new Pairing();

                /* Extract the flag of the pairing */
                roundPairing.setFlag(PairingFlag.valueOf(FileLoader
                        .getFirstChildNodeByTag(pairing, "flag")
                        .getTextContent()));

                Node participantsNode = FileLoader.getFirstChildNodeByTag(
                        pairing, "participants");

                /* Iterate over every participant in this pairing */
                for (Node participant : FileLoader.getChildNodesByTag(
                        participantsNode, "player")) {
                    Player player = null;

                    /* Extract the player id of the participant */
                    String id = FileLoader.getFirstChildNodeByTag(participant,
                            "player-id").getTextContent();

                    /*
                     * Connect the id that has been read to the player from the
                     * list
                     */
                    boolean referenced = false;
                    for (Player listedPlayer : playerList) {
                        if (listedPlayer.getId().equals(id)) {
                            player = listedPlayer;
                            referenced = true;
                            break;
                        }
                    }

                    if (!referenced) {
                        throw new IllegalArgumentException(
                                "A player that is referenced in a tournament round is not present in the given player list.");
                    }
                    // for (Player listedPlayer : playerList) {
                    // if (listedPlayer.getId().equals(id)) {
                    // player = listedPlayer;
                    // break;
                    // }
                    //
                    // throw new IllegalArgumentException(
                    // "A player that is referenced in a tournament round is not present in the given player list.");
                    // }

                    roundPairing.getOpponents().add(player);

                    /* Extract all scores to the score table */
                    Node scoreNode = FileLoader.getFirstChildNodeByTag(
                            participant, "scores");
                    PlayerScore playerScore = new PlayerScore();
                    playerScore.setPlayer(player);
                    for (Node score : FileLoader.getChildNodesByTag(scoreNode,
                            "score")) {
                        playerScore.getScore().add(
                                Integer.parseInt(score.getTextContent()));
                    }

                    roundPairing.getScoreTable().add(playerScore);
                }

                tournamentRound.getPairings().add(roundPairing);
            }

            tournamentRounds.add(tournamentRound);
        }

        return tournamentRounds;
    }

    /**
     * Append a list of game phases as rule set to this document
     * 
     * @param gamePhases
     *            List of game phases to be appended
     */
    public void appendTournamentRules(TournamentModule ruleSet) {
        /* Create the tag that will hold all tournament rules */
        Element tournamentRules = this.document
                .createElement("tournament-rules");
        this.rootElement.appendChild(tournamentRules);

        /* Append all possible scorings */
        Element possibleScoresElement = this.document
                .createElement("possible-scores");
        tournamentRules.appendChild(possibleScoresElement);

        for (PossibleScoring scoringPriority : ruleSet.getPossibleScores()) {
            Element scoringElement = this.document.createElement("scoring");

            /* Add the scoring priority of the current scoring as an attribute */
            Attr scoringPriorityAttribute = this.document
                    .createAttribute("priority");
            scoringPriorityAttribute.setValue(String.valueOf(scoringPriority
                    .getPriority()));
            scoringElement.setAttributeNode(scoringPriorityAttribute);
            possibleScoresElement.appendChild(scoringElement);

            /*
             * Iterate over the actual scores in this scoring which are a map of
             * the score name and its value
             */
            Iterator<Entry<String, Integer>> iterator = scoringPriority
                    .getScores().entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, Integer> entry = iterator.next();

                /* Add the new score */
                Element scoreElement = this.document.createElement("score");
                scoringElement.appendChild(scoreElement);

                /* Add the name of the score */
                Element nameElement = this.document.createElement("name");
                scoreElement.appendChild(nameElement);
                nameElement.appendChild(this.document.createTextNode(entry
                        .getKey()));

                /* Add the points this score is associated with */
                Element pointsElement = this.document.createElement("points");
                scoreElement.appendChild(pointsElement);
                pointsElement.appendChild(this.document.createTextNode(String
                        .valueOf(entry.getValue())));
            }
        }

        /* Append all tournament phases */
        Element tournamentPhases = this.document
                .createElement("tournament-phases");
        tournamentRules.appendChild(tournamentPhases);

        /* Iterate over every game phase in this tournament */
        for (GamePhase phase : ruleSet.getPhaseList()) {
            Element phaseElement = this.document.createElement("phase");
            tournamentPhases.appendChild(phaseElement);

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
            Element opponentsElement = this.document
                    .createElement("opponents-in-pairing");
            phaseElement.appendChild(opponentsElement);
            opponentsElement.appendChild(this.document.createTextNode(String
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

        /* Append all possible byes in the rule set */

        /* Create a node that will hold all byes */
        Element byesElement = this.document.createElement("byes");
        this.rootElement.appendChild(byesElement);

        /* Add a child node for each bye */
        for (Bye bye : ruleSet.getByeList()) {
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
            byePointsElements.appendChild(this.document.createTextNode(String
                    .valueOf(bye.getByePoints())));
        }
    }

    /**
     * Extract a list of game phases out of this document
     * 
     * @return The list of game phases in this tournament
     */
    public TournamentModule getTournamentRules() {
        TournamentModule tournamentModule = new TournamentModule();

        ArrayList<PossibleScoring> possibleScores = new ArrayList<PossibleScoring>();

        Node scores = this.document.getElementsByTagName("possible-scores")
                .item(0);

        /* Iterate over all possible scorings */
        for (Node scoring : FileLoader.getChildNodesByTag(scores, "scoring")) {
            PossibleScoring newScoring = new PossibleScoring();

            /* Attach the scoring's priority */
            newScoring.setPriority(Integer.valueOf(scoring.getAttributes()
                    .getNamedItem("priority").getTextContent()));

            /* Iterate over all scores in the scoring */
            for (Node score : FileLoader.getChildNodesByTag(scoring, "score")) {
                /* Add the scores name and points */
                newScoring.getScores().put(
                        FileLoader.getFirstChildNodeByTag(score, "name")
                                .getTextContent(),
                        Integer.valueOf(FileLoader.getFirstChildNodeByTag(
                                score, "points").getTextContent()));
            }

            possibleScores.add(newScoring);
        }
        tournamentModule.getPossibleScores().setAll(possibleScores);

        ArrayList<GamePhase> gamePhases = new ArrayList<GamePhase>();

        /* Navigate to the node that holds the tournament phases */
        Node tournamentRules = this.document.getElementsByTagName(
                "tournament-rules").item(0);
        Node tournamentPhases = FileLoader.getFirstChildNodeByTag(
                tournamentRules, "tournament-phases");

        /* Iterate over all tournament phases */
        for (Node phaseNode : FileLoader.getChildNodesByTag(tournamentPhases,
                "phase")) {
            GamePhase phase = new GamePhase();

            /* Extract the phase number and the number of rounds in this phase */
            phase.setPhaseNumber(Integer.valueOf(FileLoader
                    .getFirstChildNodeByTag(phaseNode, "phase-number")
                    .getTextContent()));
            phase.setRoundCount(Integer.valueOf(FileLoader
                    .getFirstChildNodeByTag(phaseNode, "number-of-rounds")
                    .getTextContent()));

            /* Load the pairing strategy using reflection */
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

            /* Extract the number of opponents in a pairing in this phase */
            phase.setNumberOfOpponents(Integer.valueOf(FileLoader
                    .getFirstChildNodeByTag(phaseNode, "opponents-in-pairing")
                    .getTextContent()));

            /*
             * Extract the number of players that should be kept after the end
             * of this phase
             */
            phase.setCutoff(Integer.valueOf(FileLoader.getFirstChildNodeByTag(
                    phaseNode, "cutoff-number").getTextContent()));

            /* Extract the duration of a round in this phase */
            phase.setRoundDuration(Duration.parse(FileLoader
                    .getFirstChildNodeByTag(phaseNode, "round-duration")
                    .getTextContent()));

            gamePhases.add(phase);
        }
        tournamentModule.getPhaseList().setAll(gamePhases);

        /* Load all possible byes in this rule set */

        /* Get the node that contains all byes */
        Node byes = this.document.getElementsByTagName("byes").item(0);

        /* Extract all individual byes */
        for (Node byeNode : FileLoader.getChildNodesByTag(byes, "bye")) {
            Bye bye = new Bye();

            /* Extract the bye type and the associated points */
            bye.setByeType(ByeType.valueOf(FileLoader.getFirstChildNodeByTag(
                    byeNode, "type").getTextContent()));
            bye.setByePoints(Integer.valueOf(FileLoader.getFirstChildNodeByTag(
                    byeNode, "points").getTextContent()));

            tournamentModule.getByeList().add(bye);
        }

        return tournamentModule;
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

    /**
     * Get the id of the represented tournament
     * 
     * @return The id of the tournament
     */
    public String getId() {
        return this.id;
    }

    /**
     * Set the id of the tournament represented in this document
     * 
     * @param id
     *            New id of the tournament
     */
    public void setId(String id) {
        this.id = id;
    }
}

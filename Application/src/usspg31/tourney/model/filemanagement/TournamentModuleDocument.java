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

import usspg31.tourney.model.GamePhase;
import usspg31.tourney.model.PossibleScoring;
import usspg31.tourney.model.TournamentModule;
import usspg31.tourney.model.pairingstrategies.PairingStrategy;

/**
 * An XML document that represents a tournament module
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

		this.rootElement = this.document.createElement("rule-template");
		this.document.appendChild(this.rootElement);
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

		// Add template name
		Element name = this.document.createElement("name");
		meta.appendChild(name);
		name.appendChild(this.document.createTextNode(tournamentModule
				.getName()));

		// Add template description
		Element description = this.document.createElement("description");
		meta.appendChild(description);
		description.appendChild(this.document.createTextNode(tournamentModule
				.getDescription()));
	}

	public String getName() {
		Node meta = this.document.getElementsByTagName("meta").item(0);
		return FileLoader.getFirstChildNodeByTag(meta, "name").getTextContent();
	}

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
		Element possibleScoresElement = this.document
				.createElement("possible-scores");
		this.rootElement.appendChild(possibleScoresElement);

		for (PossibleScoring scoringPriority : possibleScores) {
			Element scoringElement = this.document.createElement("scoring");
			Attr scoringPriorityAttribute = this.document
					.createAttribute("priority");
			scoringPriorityAttribute.setValue(String.valueOf(scoringPriority
					.getPriority().get()));
			scoringElement.setAttributeNode(scoringPriorityAttribute);
			possibleScoresElement.appendChild(scoringElement);

			Iterator<Entry<String, Integer>> iterator = scoringPriority
					.getScores().entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Integer> entry = iterator.next();

				Element scoreElement = this.document.createElement("score");
				scoringElement.appendChild(scoreElement);

				Element nameElement = this.document.createElement("name");
				scoreElement.appendChild(nameElement);
				nameElement.appendChild(this.document.createTextNode(entry
						.getKey()));

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

		Node scores = this.document.getElementsByTagName("possible-scores")
				.item(0);
		for (Node scoring : FileLoader.getChildNodesByTag(scores, "scoring")) {
			PossibleScoring newScoring = new PossibleScoring();
			newScoring.setPriorityValue(Integer.valueOf(scoring.getAttributes()
					.getNamedItem("priority").getTextContent()));
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
		Element gamePhasesElement = this.document.createElement("game-phases");
		this.rootElement.appendChild(gamePhasesElement);

		for (GamePhase phase : gamePhases) {
			Element phaseElement = this.document.createElement("game-phase");
			gamePhasesElement.appendChild(phaseElement);

			// Add the phase number
			Element phaseNumberElement = this.document
					.createElement("phase-number");
			phaseElement.appendChild(phaseNumberElement);
			phaseNumberElement.appendChild(this.document.createTextNode(String
					.valueOf(phase.getPhaseNumber())));

			// Add the number of rounds
			Element numberOfRoundsElement = this.document
					.createElement("number-of-rounds");
			phaseElement.appendChild(numberOfRoundsElement);
			numberOfRoundsElement.appendChild(this.document
					.createTextNode(String.valueOf(phase.getRoundCount())));

			// Add the used pairing strategy
			Element pairingStrategyElement = this.document
					.createElement("pairing-strategy");
			phaseElement.appendChild(pairingStrategyElement);
			pairingStrategyElement.appendChild(this.document
					.createTextNode(phase.getPairingMethod().getClass()
							.getName()));

			// Add the number of players in a pairing
			Element participantNumberElement = this.document
					.createElement("opponents-in-pairing");
			phaseElement.appendChild(participantNumberElement);
			participantNumberElement
					.appendChild(this.document.createTextNode(String
							.valueOf(phase.getNumberOfOpponents())));

			// Add the number of players which should be kept after cutting off
			// this round
			Element cutOffElement = this.document
					.createElement("cutoff-number");
			phaseElement.appendChild(cutOffElement);
			cutOffElement.appendChild(this.document.createTextNode(String
					.valueOf(phase.getCutoff())));

			// Add the duration of a round in this game phase
			Element durationElement = this.document
					.createElement("round-duration");
			phaseElement.appendChild(durationElement);
			durationElement.appendChild(this.document.createTextNode(phase
					.getRoundDuration().toString()));
		}
	}

	public ArrayList<GamePhase> getTournamentPhases() {
		ArrayList<GamePhase> tournamentPhases = new ArrayList<GamePhase>();

		Node gamePhases = this.document.getElementsByTagName("game-phases")
				.item(0);

		for (Node phaseNode : FileLoader.getChildNodesByTag(gamePhases,
				"game-phase")) {
			GamePhase phase = new GamePhase();
			phase.setPhaseNumber(Integer.valueOf(FileLoader
					.getFirstChildNodeByTag(phaseNode, "phase-number")
					.getTextContent()));
			phase.setRoundCount(Integer.valueOf(FileLoader
					.getFirstChildNodeByTag(phaseNode, "number-of-rounds")
					.getTextContent()));

			// Pairing strategy
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

			phase.setNumberOfOpponents(Integer.valueOf(FileLoader
					.getFirstChildNodeByTag(phaseNode, "opponents-in-pairing")
					.getTextContent()));
			phase.setCutoff(Integer.valueOf(FileLoader.getFirstChildNodeByTag(
					phaseNode, "cutoff-number").getTextContent()));
			phase.setRoundDuration(Duration.parse(FileLoader
					.getFirstChildNodeByTag(phaseNode, "round-duration")
					.getTextContent()));

			tournamentPhases.add(phase);
		}

		return tournamentPhases;
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

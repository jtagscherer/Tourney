package usspg31.tourney.model.filemanagement;

import java.util.Iterator;
import java.util.Map.Entry;

import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import usspg31.tourney.model.GamePhase;
import usspg31.tourney.model.TournamentModule;

/**
 * An XML document that represents a tournament module
 * 
 * @author Jan Tagscherer
 */
public class TournamentModuleDocument {
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

	/**
	 * Append all possible scores including all scoring priorities to the
	 * document
	 * 
	 * @param possibleScores
	 *            List of map of possible scores
	 */
	public void appendPossibleScores(
			ObservableList<ObservableMap<String, Integer>> possibleScores) {
		Element possibleScoresElement = this.document
				.createElement("possible-scores");
		this.rootElement.appendChild(possibleScoresElement);

		int priority = 0;
		for (ObservableMap<String, Integer> scoringPriority : possibleScores) {
			Element scoringElement = this.document.createElement("scoring");
			Attr scoringPriorityAttribute = this.document
					.createAttribute("priority");
			scoringPriorityAttribute.setValue(String.valueOf(priority));
			scoringElement.setAttributeNode(scoringPriorityAttribute);
			possibleScoresElement.appendChild(scoringElement);

			Iterator<Entry<String, Integer>> iterator = scoringPriority
					.entrySet().iterator();
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

			priority++;
		}
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

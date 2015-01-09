package usspg31.tourney.model.filemanagement;

import javafx.collections.ObservableList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import usspg31.tourney.model.GamePhase;
import usspg31.tourney.model.Pairing;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.PlayerScore;
import usspg31.tourney.model.Tournament;
import usspg31.tourney.model.TournamentAdministrator;
import usspg31.tourney.model.TournamentRound;

/**
 * An XML document that represents a tournament
 * 
 * @author Jan Tagscherer
 */
public class TournamentDocument {
	private Document document;
	private Element rootElement;

	public static final int REGISTERED_PLAYERS = 0;
	public static final int ATTENDANT_PLAYERS = 1;
	public static final int REMAINING_PLAYERS = 2;

	/**
	 * Create a new event document
	 * 
	 * @param document
	 *            XML document source to be used
	 */
	public TournamentDocument(Document document) {
		this.document = document;

		this.rootElement = this.document.createElement("tournament");
		this.document.appendChild(this.rootElement);
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

		// Add the tournament name to the metadata
		Element name = this.document.createElement("name");
		meta.appendChild(name);
		name.appendChild(this.document.createTextNode(tournament.getName()));
	}

	/**
	 * Append a list of all tournament administrators to this document
	 * 
	 * @param administrators
	 *            List of tournament administrators to be appended
	 */
	public void appendAdministratorList(
			ObservableList<TournamentAdministrator> administrators) {
		Element administratorsElement = this.document
				.createElement("tournament-administrators");
		this.rootElement.appendChild(administratorsElement);

		for (TournamentAdministrator administrator : administrators) {
			Element administratorElement = this.document
					.createElement("administrator");
			administratorsElement.appendChild(administratorElement);

			// Add the name of the administrator
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

			// Add the mail address of the administrator
			Element administratorMail = this.document
					.createElement("mail-address");
			administratorElement.appendChild(administratorMail);
			administratorMail.appendChild(this.document
					.createTextNode(administrator.getMailAddress()));

			// Add the phone number of the administrator
			Element administratorPhone = this.document
					.createElement("phone-number");
			administratorElement.appendChild(administratorPhone);
			administratorPhone.appendChild(this.document
					.createTextNode(administrator.getPhoneNumber()));
		}
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
	public void appendPlayerList(ObservableList<Player> players, int playerType) {
		Element playersElement = null;

		switch (playerType) {
		case TournamentDocument.REGISTERED_PLAYERS:
			playersElement = this.document.createElement("registered-players");
			break;
		case TournamentDocument.ATTENDANT_PLAYERS:
			playersElement = this.document.createElement("attendant-players");
			break;
		case TournamentDocument.REMAINING_PLAYERS:
			playersElement = this.document.createElement("remaining-players");
			break;
		default:
			throw new IllegalArgumentException("The player type " + playerType
					+ " is not valid.");
		}

		this.rootElement.appendChild(playersElement);

		for (Player player : players) {
			Element playerElement = this.document.createElement("player");
			playersElement.appendChild(playerElement);

			Element playerId = this.document.createElement("player-id");
			playerElement.appendChild(playerId);
			playerId.appendChild(this.document.createTextNode(player.getId()));
		}
	}

	/**
	 * Append a list of tournament rounds to this document
	 * 
	 * @param tournamentRounds
	 *            List of tournament rounds to be used
	 */
	public void appendTournamentRounds(
			ObservableList<TournamentRound> tournamentRounds) {
		Element tournamentRoundsElement = this.document
				.createElement("tournament-rounds");
		this.rootElement.appendChild(tournamentRoundsElement);

		for (TournamentRound tournamentRound : tournamentRounds) {
			Element roundElement = this.document
					.createElement("tournament-round");
			tournamentRoundsElement.appendChild(roundElement);

			// Add the number of the current tournament round
			Element roundName = this.document.createElement("round-number");
			roundElement.appendChild(roundName);
			roundName.appendChild(this.document.createTextNode(String
					.valueOf(tournamentRound.getRoundNumber())));

			// Add all pairings of the current tournament round
			Element pairings = this.document.createElement("pairings");
			roundElement.appendChild(pairings);

			for (Pairing pairing : tournamentRound.getPairings()) {
				Element pairingElement = this.document.createElement("pairing");
				pairings.appendChild(pairingElement);

				// Add all participants of the pairing
				Element participants = this.document
						.createElement("participants");
				pairingElement.appendChild(participants);

				for (Player participant : pairing.getOpponents()) {
					Element participantElement = this.document
							.createElement("player");
					participants.appendChild(participantElement);

					// Add the unique identification string of the player
					Element playerId = this.document.createElement("player-id");
					participantElement.appendChild(playerId);
					playerId.appendChild(this.document.createTextNode(String
							.valueOf(participant.getId())));

					// Add the scores of the current participant
					Element scoreElement = this.document
							.createElement("scores");
					participantElement.appendChild(scoreElement);

					for (PlayerScore score : pairing.getScoreTable()) {
						if (score.getPlayer() == participant) {
							for (Integer scoreInteger : score.getScore()) {
								Element scoreIntegerElement = this.document
										.createElement("score");
								scoreElement.appendChild(scoreIntegerElement);
								scoreElement.appendChild(this.document
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
	 * Append a list of game phases as rule set to this document
	 * 
	 * @param gamePhases
	 *            List of game phases to be appended
	 */
	public void appendTournamentPhases(ObservableList<GamePhase> gamePhases) {
		Element tournamentRules = this.document
				.createElement("tournament-rules");
		this.rootElement.appendChild(tournamentRules);

		Element tournamentPhases = this.document
				.createElement("tournament-phases");
		tournamentRules.appendChild(tournamentPhases);

		for (GamePhase phase : gamePhases) {
			Element phaseElement = this.document.createElement("phase");
			tournamentPhases.appendChild(phaseElement);

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
			Element opponentsElement = this.document
					.createElement("opponents-in-pairing");
			phaseElement.appendChild(opponentsElement);
			opponentsElement.appendChild(this.document.createTextNode(String
					.valueOf(phase.getNumberOfOpponents())));

			// Add the number of players which should be kept after cutting
			// off this round
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

package usspg31.tourney.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javafx.collections.ObservableList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Contains static methods that save and load rule modules and events including
 * tournaments and players using XML files
 * 
 * @author Jan Tagscherer
 */
public class FileSaver {
	private static final Logger log = Logger.getLogger(FileSaver.class
			.getName());

	private static DocumentBuilderFactory documentFactory;
	private static DocumentBuilder documentBuilder;
	private static Transformer transformer;
	private static boolean initialized = false;

	/**
	 * Initialize the file manager. Has to be called before all other methods.
	 */
	public static void initialize() {
		FileSaver.documentFactory = DocumentBuilderFactory.newInstance();
		try {
			FileSaver.documentBuilder = FileSaver.documentFactory
					.newDocumentBuilder();

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			FileSaver.transformer = transformerFactory.newTransformer();
			FileSaver.transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			FileSaver.transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "4");

			FileSaver.initialized = true;
		} catch (ParserConfigurationException e) {
			log.log(Level.SEVERE,
					"Error while creating the document builder in file management initialization.");
			log.log(Level.SEVERE, e.getMessage(), e);
		} catch (TransformerConfigurationException e) {
			log.log(Level.SEVERE,
					"Error while creating the transformer in file management initialization.");
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Save an event including tournaments, players and the utilized tournament
	 * modules as a .tef file
	 * 
	 * @param event
	 *            Event that should be saved
	 * @param path
	 *            Path where the event should be saved
	 */
	public static void saveEventToFile(Event event, String path) {
		if (!FileSaver.initialized) {
			FileSaver.initialize();
		}

		String zipFilePath = path + event.getName().replace(" ", "") + ".tef";
		File zipFile = new File(zipFilePath);
		try {
			zipFile.getParentFile().mkdirs();
			zipFile.createNewFile();
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}

		FileOutputStream fileOutputStream;
		ZipOutputStream zipOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(zipFilePath);
			zipOutputStream = new ZipOutputStream(fileOutputStream);
		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}

		FileSaver.saveEvent(event, "Event.xml", zipOutputStream);
		FileSaver.savePlayers(event.getRegisteredPlayers(), "Players.xml",
				zipOutputStream);
		for (Tournament tournament : event.getTournaments()) {
			FileSaver.saveTournament(tournament,
					"Tournament_" + tournament.getId() + ".xml",
					zipOutputStream);
		}

		try {
			zipOutputStream.close();
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Save a tournament rule template to an individual file
	 * 
	 * @param module
	 *            Tournament module to be saved
	 * @param path
	 *            Path where the tournament module should be saved
	 */
	public static void saveTournamentModuleToFile(TournamentModule module,
			String path) {
		if (!FileSaver.initialized) {
			FileSaver.initialize();
		}
		
		if(!path.endsWith(".ttm")) {
			path += ".ttm";
		}
		
		FileSaver.saveTournamentModule(module, path);
	}

	/**
	 * Save an event to a file
	 * 
	 * @param event
	 *            Event to be saved
	 * @param path
	 *            Path were the event should be saved
	 */
	private static void saveEvent(Event event, String fileName,
			ZipOutputStream zipOutputStream) {
		Document document = FileSaver.documentBuilder.newDocument();

		// Add the root element
		Element rootElement = document.createElement("event");
		document.appendChild(rootElement);

		// Add meta data
		Element meta = document.createElement("meta");
		rootElement.appendChild(meta);

		// Add the name
		Element name = document.createElement("name");
		meta.appendChild(name);
		name.appendChild(document.createTextNode(event.getName()));

		// Add the location
		Element location = document.createElement("location");
		meta.appendChild(location);
		location.appendChild(document.createTextNode(event.getLocation()));

		// Add the date
		Element date = document.createElement("date");
		meta.appendChild(date);

		Element startDate = document.createElement("start-date");
		date.appendChild(startDate);
		startDate.appendChild(document.createTextNode(event.getStartDate()
				.toString()));

		Element endDate = document.createElement("end-date");
		date.appendChild(endDate);
		endDate.appendChild(document.createTextNode(event.getEndDate()
				.toString()));

		// Add the event administrators
		Element eventAdministrators = document
				.createElement("event-administrators");
		meta.appendChild(eventAdministrators);

		for (EventAdministrator eventAdministrator : event.getAdministrators()) {
			Element administrator = document.createElement("administrator");
			eventAdministrators.appendChild(administrator);

			// Add the name of the current administrator
			Element administratorName = document.createElement("name");
			administrator.appendChild(administratorName);

			Element firstName = document.createElement("first-name");
			administratorName.appendChild(firstName);
			firstName.appendChild(document.createTextNode(eventAdministrator
					.getFirstName()));

			Element lastName = document.createElement("last-name");
			administratorName.appendChild(lastName);
			lastName.appendChild(document.createTextNode(eventAdministrator
					.getLastName()));

			// Add the mail address of the current administrator
			Element mailAddress = document.createElement("mail-address");
			administrator.appendChild(mailAddress);
			mailAddress.appendChild(document.createTextNode(eventAdministrator
					.getMailAddress()));

			// Add the phone number of the current administrator
			Element phoneNumber = document.createElement("phone-number");
			administrator.appendChild(phoneNumber);
			phoneNumber.appendChild(document.createTextNode(eventAdministrator
					.getPhoneNumber()));
		}

		// Add all tournaments in this event
		Element tournaments = document.createElement("tournaments");
		rootElement.appendChild(tournaments);

		for (Tournament tournament : event.getTournaments()) {
			Element tournamentElement = document.createElement("tournament");
			tournaments.appendChild(tournamentElement);

			// Add a reference to the current tournament using its ID
			Element tournamentId = document.createElement("tournament-id");
			tournamentElement.appendChild(tournamentId);
			tournamentId
					.appendChild(document.createTextNode(tournament.getId()));
		}

		FileSaver.saveDocumentToZip(document, fileName, zipOutputStream);
	}

	/**
	 * Save a tournament to a file
	 * 
	 * @param tournament
	 *            Tournament to be saved
	 * @param path
	 *            Path where the tournament should be saved
	 */
	private static void saveTournament(Tournament tournament, String fileName,
			ZipOutputStream zipOutputStream) {
		Document document = FileSaver.documentBuilder.newDocument();

		// Add the root element
		Element rootElement = document.createElement("tournament");
		document.appendChild(rootElement);

		// Add tournament metadata
		Element meta = document.createElement("meta");
		rootElement.appendChild(meta);

		// Add the tournament name to the metadata
		Element name = document.createElement("name");
		meta.appendChild(name);
		name.appendChild(document.createTextNode(tournament.getName()));

		// Add a node for every tournament administrator
		Element administrators = document
				.createElement("tournament-administrators");
		rootElement.appendChild(administrators);

		for (TournamentAdministrator administrator : tournament
				.getAdministrators()) {
			Element administratorElement = document
					.createElement("administrator");
			administrators.appendChild(administratorElement);

			// Add the name of the administrator
			Element administratorName = document.createElement("name");
			administratorElement.appendChild(administratorName);

			Element firstName = document.createElement("first-name");
			administratorName.appendChild(firstName);
			firstName.appendChild(document.createTextNode(administrator
					.getFirstName()));

			Element lastName = document.createElement("last-name");
			administratorName.appendChild(lastName);
			lastName.appendChild(document.createTextNode(administrator
					.getLastName()));

			// Add the mail address of the administrator
			Element administratorMail = document.createElement("mail-address");
			administratorElement.appendChild(administratorMail);
			administratorMail.appendChild(document.createTextNode(administrator
					.getMailAddress()));

			// Add the phone number of the administrator
			Element administratorPhone = document.createElement("phone-number");
			administratorElement.appendChild(administratorPhone);
			administratorPhone.appendChild(document
					.createTextNode(administrator.getPhoneNumber()));
		}

		// Add a node for every registered player
		Element registeredPlayers = document
				.createElement("registered-players");
		rootElement.appendChild(registeredPlayers);

		for (Player player : tournament.getRegisteredPlayers()) {
			Element playerElement = document.createElement("player");
			registeredPlayers.appendChild(playerElement);

			Element playerId = document.createElement("player-id");
			playerElement.appendChild(playerId);
			playerId.appendChild(document.createTextNode(player.getId()));
		}

		// Add a node for every attendant player
		Element attendantPlayers = document.createElement("attendant-players");
		rootElement.appendChild(attendantPlayers);

		for (Player player : tournament.getAttendingPlayers()) {
			Element playerElement = document.createElement("player");
			attendantPlayers.appendChild(playerElement);

			Element playerId = document.createElement("player-id");
			playerElement.appendChild(playerId);
			playerId.appendChild(document.createTextNode(player.getId()));
		}

		// Add a node for every remaining player in the game
		Element remainingPlayers = document.createElement("remaining-players");
		rootElement.appendChild(remainingPlayers);

		for (Player player : tournament.getRemainingPlayers()) {
			Element playerElement = document.createElement("player");
			remainingPlayers.appendChild(playerElement);

			Element playerId = document.createElement("player-id");
			playerElement.appendChild(playerId);
			playerId.appendChild(document.createTextNode(player.getId()));
		}

		// Add a node for every tournament round
		Element tournamentRounds = document.createElement("tournament-rounds");
		rootElement.appendChild(tournamentRounds);

		for (TournamentRound tournamentRound : tournament.getRounds()) {
			Element roundElement = document.createElement("tournament-round");
			tournamentRounds.appendChild(roundElement);

			// Add the number of the current tournament round
			Element roundName = document.createElement("round-number");
			roundElement.appendChild(roundName);
			roundName.appendChild(document.createTextNode(String
					.valueOf(tournamentRound.getRoundNumber())));

			// Add all pairings of the current tournament round
			Element pairings = document.createElement("pairings");
			roundElement.appendChild(pairings);

			for (Pairing pairing : tournamentRound.getPairings()) {
				Element pairingElement = document.createElement("pairing");
				pairings.appendChild(pairingElement);

				// Add all participants of the pairing
				Element participants = document.createElement("participants");
				pairingElement.appendChild(participants);

				for (Player participant : pairing.getOpponents()) {
					Element participantElement = document
							.createElement("player");
					participants.appendChild(participantElement);

					// Add the unique identification string of the player
					Element playerId = document.createElement("player-id");
					participantElement.appendChild(playerId);
					playerId.appendChild(document.createTextNode(String
							.valueOf(participant.getId())));

					// Add the scores of the current participant
					Element scoreElement = document.createElement("scores");
					participantElement.appendChild(scoreElement);

					for (PlayerScore score : pairing.getScoreTable()) {
						if (score.getPlayer() == participant) {
							for (Integer scoreInteger : score.getScore()) {
								Element scoreIntegerElement = document
										.createElement("score");
								scoreElement.appendChild(scoreIntegerElement);
								scoreElement.appendChild(document
										.createTextNode(String
												.valueOf(scoreInteger)));
							}
						}
					}
				}
			}
		}

		// Add the applied tournament rules for this tournament
		Element tournamentRules = document.createElement("tournament-rules");
		rootElement.appendChild(tournamentRules);

		Element tournamentPhases = document.createElement("tournament-phases");
		tournamentRules.appendChild(tournamentPhases);

		if (tournament.getRuleSet() != null) {
			for (GamePhase phase : tournament.getRuleSet().getPhaseList()) {
				Element phaseElement = document.createElement("phase");
				tournamentPhases.appendChild(phaseElement);

				// Add the phase number
				Element phaseNumberElement = document
						.createElement("phase-number");
				phaseElement.appendChild(phaseNumberElement);
				phaseNumberElement.appendChild(document.createTextNode(String
						.valueOf(phase.getPhaseNumber())));

				// Add the number of rounds
				Element numberOfRoundsElement = document
						.createElement("number-of-rounds");
				phaseElement.appendChild(numberOfRoundsElement);
				numberOfRoundsElement.appendChild(document
						.createTextNode(String.valueOf(phase.getRoundCount())));

				// Add the used pairing strategy
				Element pairingStrategyElement = document
						.createElement("pairing-strategy");
				phaseElement.appendChild(pairingStrategyElement);
				pairingStrategyElement.appendChild(document
						.createTextNode(phase.getPairingMethod().getClass()
								.getName()));

				// Add the number of players in a pairing
				Element opponentsElement = document
						.createElement("opponents-in-pairing");
				phaseElement.appendChild(opponentsElement);
				opponentsElement.appendChild(document.createTextNode(String
						.valueOf(phase.getNumberOfOpponents())));

				// Add the number of players which should be kept after cutting
				// off this round
				Element cutOffElement = document.createElement("cutoff-number");
				phaseElement.appendChild(cutOffElement);
				cutOffElement.appendChild(document.createTextNode(String
						.valueOf(phase.getCutoff())));

				// Add the duration of a round in this game phase
				Element durationElement = document
						.createElement("round-duration");
				phaseElement.appendChild(durationElement);
				durationElement.appendChild(document.createTextNode(phase
						.getRoundDuration().toString()));
			}
		}

		FileSaver.saveDocumentToZip(document, fileName, zipOutputStream);
	}

	/**
	 * Save a list of players to a file
	 * 
	 * @param players
	 *            The list of players to be saved
	 * @param path
	 *            The path where the players should be saved
	 */
	public static void savePlayers(ObservableList<Player> players,
			String fileName, ZipOutputStream zipOutputStream) {
		Document document = FileSaver.documentBuilder.newDocument();

		// Add the root element
		Element rootElement = document.createElement("players");
		document.appendChild(rootElement);

		for (Player player : players) {
			// Add a new player
			Element playerElement = document.createElement("player");
			Attr playerId = document.createAttribute("id");
			playerId.setValue(player.getId());
			playerElement.setAttributeNode(playerId);
			rootElement.appendChild(playerElement);

			// Add the name of the player
			Element name = document.createElement("name");
			playerElement.appendChild(name);

			Element firstName = document.createElement("first-name");
			name.appendChild(firstName);
			firstName
					.appendChild(document.createTextNode(player.getFirstName()));

			Element lastName = document.createElement("last-name");
			name.appendChild(lastName);
			lastName.appendChild(document.createTextNode(player.getLastName()));

			// Add the mail address of the player
			Element mailAddress = document.createElement("mail-address");
			playerElement.appendChild(mailAddress);
			mailAddress.appendChild(document.createTextNode(player
					.getMailAddress()));

			// Add the nick name of the player
			Element nickname = document.createElement("nickname");
			playerElement.appendChild(nickname);
			nickname.appendChild(document.createTextNode(player.getNickName()));

			// Add the starting number of the player
			Element startingNumber = document.createElement("starting-number");
			playerElement.appendChild(startingNumber);
			startingNumber.appendChild(document.createTextNode(player
					.getStartingNumber()));

			// Add the payment status of the player
			Element payed = document.createElement("payed");
			playerElement.appendChild(payed);
			payed.appendChild(document.createTextNode(String.valueOf(player
					.getPayed())));

			// Add the disqualification status of the player
			Element disqualified = document.createElement("disqualified");
			playerElement.appendChild(disqualified);
			disqualified.appendChild(document.createTextNode(String
					.valueOf(player.getDisqualified())));
		}

		FileSaver.saveDocumentToZip(document, fileName, zipOutputStream);
	}

	/**
	 * Save a tournament module to a file
	 * 
	 * @param module
	 *            Tournament module to be saved
	 * @param path
	 *            Path where the tournament module should be saved
	 */
	private static void saveTournamentModule(TournamentModule module,
			String path) {
		Document document = FileSaver.documentBuilder.newDocument();

		// Add the root element
		Element rootElement = document.createElement("rule-template");
		document.appendChild(rootElement);

		// Add template metadata
		Element meta = document.createElement("meta");
		rootElement.appendChild(meta);

		// Add template name
		Element name = document.createElement("name");
		meta.appendChild(name);
		name.appendChild(document.createTextNode(module.getName()));

		// Add template description
		Element description = document.createElement("description");
		meta.appendChild(description);
		description
				.appendChild(document.createTextNode(module.getDescription()));

		// Add all possible scores
		Element possibleScores = document.createElement("possible-scores");
		rootElement.appendChild(possibleScores);

		Iterator<Entry<String, Integer>> iterator = module.getPossibleScores()
				.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Integer> entry = iterator.next();

			Element scoreElement = document.createElement("score");
			possibleScores.appendChild(scoreElement);

			Element nameElement = document.createElement("name");
			scoreElement.appendChild(nameElement);
			nameElement.appendChild(document.createTextNode(entry.getKey()));

			Element pointsElement = document.createElement("points");
			scoreElement.appendChild(pointsElement);
			pointsElement.appendChild(document.createTextNode(String
					.valueOf(entry.getValue())));
		}

		// Add all game phases
		Element gamePhases = document.createElement("game-phases");
		rootElement.appendChild(gamePhases);

		for (GamePhase phase : module.getPhaseList()) {
			Element phaseElement = document.createElement("game-phase");
			gamePhases.appendChild(phaseElement);

			// Add the phase number
			Element phaseNumberElement = document.createElement("phase-number");
			phaseElement.appendChild(phaseNumberElement);
			phaseNumberElement.appendChild(document.createTextNode(String
					.valueOf(phase.getPhaseNumber())));

			// Add the number of rounds
			Element numberOfRoundsElement = document
					.createElement("number-of-rounds");
			phaseElement.appendChild(numberOfRoundsElement);
			numberOfRoundsElement.appendChild(document.createTextNode(String
					.valueOf(phase.getRoundCount())));

			// Add the used pairing strategy
			Element pairingStrategyElement = document
					.createElement("pairing-strategy");
			phaseElement.appendChild(pairingStrategyElement);
			pairingStrategyElement.appendChild(document.createTextNode(phase
					.getPairingMethod().getClass().getName()));

			// Add the number of players which should be kept after cutting off
			// this round
			Element cutOffElement = document.createElement("cutoff-number");
			phaseElement.appendChild(cutOffElement);
			cutOffElement.appendChild(document.createTextNode(String
					.valueOf(phase.getCutoff())));

			// Add the duration of a round in this game phase
			Element durationElement = document.createElement("round-duration");
			phaseElement.appendChild(durationElement);
			durationElement.appendChild(document.createTextNode(phase
					.getRoundDuration().toString()));
		}

		FileSaver.saveDocumentToFile(document, path);
	}

	/**
	 * Save an existing XML document to a file
	 * 
	 * @param document
	 *            The DOM document to be saved
	 * @param path
	 *            The path where the document will be saved
	 */
	private static void saveDocumentToFile(Document document, String path) {
		try {
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(new File(path));

			FileSaver.transformer.transform(source, result);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Could not save the XML file to \"" + path
					+ "\".");
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Save an existing XML document directly to a zip file
	 * 
	 * @param document
	 *            The DOM document to be saved
	 * @param fileName
	 *            The file name of this document in the zip file
	 * @param zipOutputStream
	 *            The zip output stream the document should be saved to
	 */
	private static void saveDocumentToZip(Document document, String fileName,
			ZipOutputStream zipOutputStream) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			DOMSource source = new DOMSource(document);
			StreamResult outputTarget = new StreamResult(outputStream);
			FileSaver.transformer.transform(source, outputTarget);
			InputStream inputStream = new ByteArrayInputStream(
					outputStream.toByteArray());

			ZipEntry zipEntry = new ZipEntry(fileName);
			zipOutputStream.putNextEntry(zipEntry);

			byte[] bytes = new byte[1024];
			int length;
			while ((length = inputStream.read(bytes)) >= 0) {
				zipOutputStream.write(bytes, 0, length);
			}

			zipOutputStream.closeEntry();
			inputStream.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Could not output the XML file.");
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}

package usspg31.tourney.model.filemanagement;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import usspg31.tourney.model.Event;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.Tournament;
import usspg31.tourney.model.TournamentModule;

/**
 * Contains static methods that load rule modules and events including
 * tournaments and players using XML files
 * 
 * @author Jan Tagscherer
 */
public class FileLoader {
	private static final Logger log = Logger.getLogger(FileSaver.class
			.getName());

	private static DocumentBuilder documentBuilder;
	private static boolean initialized = false;

	/**
	 * Initialize the file loader. Has to be called before using any methods
	 */
	public static void initialize() {
		try {
			FileLoader.documentBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			FileLoader.initialized = true;
		} catch (ParserConfigurationException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Load an event from a packaged file
	 * 
	 * @param path
	 *            Path to the file
	 * @return Event including tournaments and players that the file represents
	 * @throws SAXException
	 *             If the files in the zip file can not be parsed
	 * @throws IOException
	 *             If the zip file can not be read
	 */
	public static Event loadEventFromFile(String path) throws IOException,
			SAXException {
		if (!FileLoader.initialized) {
			FileLoader.initialize();
		}

		Event event = new Event();
		String executedTournamentId = null;

		EventDocument eventDocument = null;
		PlayerDocument playerDocument = null;
		ArrayList<TournamentDocument> tournamentDocuments = new ArrayList<TournamentDocument>();

		ZipFile zipFile = new ZipFile(path);

		Enumeration<? extends ZipEntry> entries = zipFile.entries();

		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			InputStream stream = zipFile.getInputStream(entry);

			if (entry.getName().equals("Event.xml")) {
				eventDocument = new EventDocument(
						FileLoader.documentBuilder.parse(stream));
			} else if (entry.getName().equals("Players.xml")) {
				playerDocument = new PlayerDocument(
						FileLoader.documentBuilder.parse(stream));
			} else if (entry.getName().startsWith("Tournament")) {
				TournamentDocument tournamentDocument = new TournamentDocument(
						FileLoader.documentBuilder.parse(stream));
				tournamentDocument.setId(entry.getName().substring(11,
						entry.getName().length() - 4));

				tournamentDocuments.add(tournamentDocument);
			} else if (entry.getName().equals("Meta.xml")) {
				MetaDocument metaDocument = new MetaDocument(
						FileLoader.documentBuilder.parse(stream));
				if (metaDocument.getMetaData().startsWith("Tournament")) {
					executedTournamentId = metaDocument.getMetaData()
							.substring(14);
				} else {
					event.setUserFlag(Event.UserFlag.valueOf(metaDocument
							.getMetaData()));
				}
			}

			stream.close();
		}

		zipFile.close();

		EventMetaData eventMeta = eventDocument.getMetaData();
		event.setName(eventMeta.getName());
		event.setLocation(eventMeta.getLocation());
		event.setStartDate(eventMeta.getStartDate());
		event.setEndDate(eventMeta.getEndDate());
		event.setEventPhase(eventMeta.getEventPhase());
		event.getAdministrators().setAll(eventMeta.getAdministrators());

		ArrayList<Player> playerList = playerDocument.getPlayerList();
		ArrayList<Tournament> tournamentList = new ArrayList<Tournament>();

		for (TournamentDocument tournamentDocument : tournamentDocuments) {
			Tournament newTournament = new Tournament();
			newTournament.setId(tournamentDocument.getId());
			newTournament.setName(tournamentDocument.getTournamentName());
			newTournament.getAdministrators().setAll(
					tournamentDocument.getTournamentAdministrators());

			newTournament.getAttendingPlayers().setAll(
					tournamentDocument.getPlayerList(
							TournamentDocument.ATTENDANT_PLAYERS, playerList));
			newTournament.getRegisteredPlayers().setAll(
					tournamentDocument.getPlayerList(
							TournamentDocument.REGISTERED_PLAYERS, playerList));
			newTournament.getRemainingPlayers().setAll(
					tournamentDocument.getPlayerList(
							TournamentDocument.REMAINING_PLAYERS, playerList));

			newTournament.getRounds().setAll(
					tournamentDocument.getTournamentRounds(playerList));
			newTournament.setRuleSet(tournamentDocument.getTournamentRules());

			tournamentList.add(newTournament);
		}

		event.getTournaments().setAll(tournamentList);
		event.getRegisteredPlayers().setAll(playerList);

		if (executedTournamentId != null) {
			for (Tournament tournament : event.getTournaments()) {
				if (tournament.getId().equals(executedTournamentId)) {
					event.setExecutedTournament(tournament);
					break;
				}
			}
		}

		return event;
	}

	/**
	 * Load a tournament module from a file
	 * 
	 * @param path
	 *            File to be loaded
	 * @return The tournament module that is represented by the file
	 * @throws IOException
	 *             If the file could not be loaded
	 * @throws SAXException
	 *             If the file could not be parsed
	 */
	public static TournamentModule loadTournamentModuleFromFile(String path)
			throws SAXException, IOException {
		if (!FileLoader.initialized) {
			FileLoader.initialize();
		}

		TournamentModule module = new TournamentModule();

		TournamentModuleDocument moduleDocument = new TournamentModuleDocument(
				FileLoader.documentBuilder.parse(new File(path)));

		module.setName(moduleDocument.getName());
		module.setDescription(moduleDocument.getDescription());

		module.getPossibleScores().setAll(moduleDocument.getPossibleScores());
		module.getPhaseList().setAll(moduleDocument.getTournamentPhases());

		return module;
	}

	/**
	 * Get all direct child nodes by tag
	 * 
	 * @param parent
	 *            Parent of the child nodes
	 * @param tag
	 *            Tag to be searched for
	 * @return A list of nodes with the specified tag
	 */
	public static ArrayList<Node> getChildNodesByTag(Node parent, String tag) {
		ArrayList<Node> childNodes = new ArrayList<Node>();

		NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode.getParentNode() == parent
					&& childNode.getNodeName().equals(tag)) {
				childNodes.add(childNode);
			}
		}

		return childNodes;
	}

	/**
	 * Get the first direct child node by tag
	 * 
	 * @param parent
	 *            Parent of the child node
	 * @param tag
	 *            Tag to be searched for
	 * @return A list of nodes with the specified tag
	 */
	public static Node getFirstChildNodeByTag(Node parent, String tag) {
		ArrayList<Node> childNodes = FileLoader.getChildNodesByTag(parent, tag);

		if (childNodes.size() == 0) {
			return null;
		} else {
			return FileLoader.getChildNodesByTag(parent, tag).get(0);
		}
	}
}

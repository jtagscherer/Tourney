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

	/* Initialize all data objects */
	Event event = new Event();
	String executedTournamentId = null;
	Integer numberOfRegistrators = 0;

	EventDocument eventDocument = null;
	PlayerDocument playerDocument = null;
	ArrayList<TournamentDocument> tournamentDocuments = new ArrayList<TournamentDocument>();

	/* Open the given zip file */
	ZipFile zipFile = new ZipFile(path);
	Enumeration<? extends ZipEntry> entries = zipFile.entries();

	/*
	 * Iterate over the files the zip archive contains and extract them as
	 * documents depending on their names
	 */
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
		if (metaDocument.getMetaData().startsWith(
			"TOURNAMENT_EXECUTION")) {
		    executedTournamentId = metaDocument.getMetaData()
			    .substring(24);
		} else if (metaDocument.getMetaData()
			.startsWith("REGISTRATION")) {
		    numberOfRegistrators = Integer.valueOf(metaDocument
			    .getMetaData().substring(20));
		} else {
		    event.setUserFlag(Event.UserFlag.valueOf(metaDocument
			    .getMetaData()));
		}
	    }

	    stream.close();
	}

	zipFile.close();

	/*
	 * Get the event meta data from its document and apply it to the new
	 * event
	 */
	EventMetaData eventMeta = eventDocument.getMetaData();
	event.setName(eventMeta.getName());
	event.setLocation(eventMeta.getLocation());
	event.setStartDate(eventMeta.getStartDate());
	event.setEndDate(eventMeta.getEndDate());
	event.setEventPhase(eventMeta.getEventPhase());
	event.getAdministrators().setAll(eventMeta.getAdministrators());

	/* Load the list of players from its document */
	ArrayList<Player> playerList = playerDocument.getPlayerList();
	ArrayList<Tournament> tournamentList = new ArrayList<Tournament>();

	/*
	 * Add all tournaments from their files to the event while setting all
	 * their data to the fields from the file and piecing together player
	 * ids and the actual players from the list
	 */
	for (TournamentDocument tournamentDocument : tournamentDocuments) {
	    Tournament newTournament = new Tournament();
	    newTournament.setId(tournamentDocument.getId());
	    newTournament.setName(tournamentDocument.getTournamentName());
	    newTournament.getAdministrators().setAll(
		    tournamentDocument.getTournamentAdministrators());

	    newTournament
		    .getAttendingPlayers()
		    .setAll(tournamentDocument
			    .getPlayerList(
				    TournamentDocument.PlayerListType.ATTENDANT_PLAYERS,
				    playerList));
	    newTournament
		    .getRegisteredPlayers()
		    .setAll(tournamentDocument
			    .getPlayerList(
				    TournamentDocument.PlayerListType.REGISTERED_PLAYERS,
				    playerList));
	    newTournament
		    .getRemainingPlayers()
		    .setAll(tournamentDocument
			    .getPlayerList(
				    TournamentDocument.PlayerListType.REMAINING_PLAYERS,
				    playerList));

	    newTournament.getRounds().setAll(
		    tournamentDocument.getTournamentRounds(playerList));
	    newTournament.setRuleSet(tournamentDocument.getTournamentRules());

	    tournamentList.add(newTournament);
	}

	event.getTournaments().setAll(tournamentList);
	event.getRegisteredPlayers().setAll(playerList);

	/*
	 * Set the currently executed tournament if this event has been saved
	 * for a tournament administrator
	 */
	if (executedTournamentId != null) {
	    for (Tournament tournament : event.getTournaments()) {
		if (tournament.getId().equals(executedTournamentId)) {
		    event.setExecutedTournament(tournament);
		    break;
		}
	    }
	}

	/*
	 * Set the number of registrators this event is used by
	 */
	if (numberOfRegistrators > 0) {
	    event.setNumberOfRegistrators(numberOfRegistrators);
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

	/* Initialize a new tournament module to apply all data to */
	TournamentModule module = new TournamentModule();

	/* Load module information from a new file */
	TournamentModuleDocument moduleDocument = new TournamentModuleDocument(
		FileLoader.documentBuilder.parse(new File(path)));

	/* Apply all data from the document to the module */
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

	/*
	 * Iterate over all child nodes of the parent node and add them to the
	 * returned list if they are direct children and have the specified tag
	 */
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

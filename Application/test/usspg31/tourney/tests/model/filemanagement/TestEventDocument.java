package usspg31.tourney.tests.model.filemanagement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;

import usspg31.tourney.model.Event;
import usspg31.tourney.model.Event.EventPhase;
import usspg31.tourney.model.EventAdministrator;
import usspg31.tourney.model.GamePhase;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.PlayerScore;
import usspg31.tourney.model.PossibleScoring;
import usspg31.tourney.model.Tournament;
import usspg31.tourney.model.TournamentModule;
import usspg31.tourney.model.filemanagement.EventDocument;
import usspg31.tourney.model.filemanagement.EventMetaData;
import usspg31.tourney.model.pairingstrategies.SwissSystem;

public class TestEventDocument {
    private EventDocument document;

    @Before
    public void init() {
	DocumentBuilderFactory documentFactory = DocumentBuilderFactory
		.newInstance();
	DocumentBuilder documentBuilder = null;
	try {
	    documentBuilder = documentFactory.newDocumentBuilder();
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	}
	this.document = new EventDocument(documentBuilder.newDocument());
    }

    @Test
    public void testMetaData() {
	Event event = new Event();
	// Set some meta data for the new event
	event.setName("TestEvent");
	event.setLocation("TestLocation");
	event.setStartDate(LocalDate.of(2015, 2, 1));
	event.setEndDate(LocalDate.of(2015, 2, 3));
	event.setEventPhase(Event.EventPhase.PRE_REGISTRATION);

	// Add a list of event administrators
	ArrayList<EventAdministrator> eventAdministrators = new ArrayList<EventAdministrator>();
	EventAdministrator administrator = new EventAdministrator();
	administrator.setFirstName("Aaron");
	administrator.setLastName("Admin");
	administrator.setMailAdress("a.admin@mail.com");
	administrator.setPhoneNumber("123456");
	eventAdministrators.add(administrator);

	event.getAdministrators().setAll(eventAdministrators);

	// Write all data to the document
	this.document.appendMetaData(event);

	// Read the meta data from the document
	EventMetaData metaData = this.document.getMetaData();

	// Test the meta data
	assertEquals("TestEvent", metaData.getName());
	assertEquals("TestLocation", metaData.getLocation());
	assertEquals(LocalDate.of(2015, 2, 1), metaData.getStartDate());
	assertEquals(LocalDate.of(2015, 2, 3), metaData.getEndDate());
	assertEquals(Event.EventPhase.PRE_REGISTRATION,
		metaData.getEventPhase());

	// Test the administrator list
	assertEquals(1, metaData.getAdministrators().size());
	assertEquals("Aaron", metaData.getAdministrators().get(0)
		.getFirstName());
	assertEquals("Admin", metaData.getAdministrators().get(0).getLastName());
	assertEquals("a.admin@mail.com", metaData.getAdministrators().get(0)
		.getMailAddress());
	assertEquals("123456", metaData.getAdministrators().get(0)
		.getPhoneNumber());
    }

    @Test
    public void testEmptyDate() {
	Event event = new Event();
	// Set some meta data for the new event
	event.setName("TestEvent");
	event.setLocation("TestLocation");

	// Add a list of event administrators
	ArrayList<EventAdministrator> eventAdministrators = new ArrayList<EventAdministrator>();
	EventAdministrator administrator = new EventAdministrator();
	administrator.setFirstName("Aaron");
	administrator.setLastName("Admin");
	administrator.setMailAdress("a.admin@mail.com");
	administrator.setPhoneNumber("123456");
	eventAdministrators.add(administrator);

	event.getAdministrators().setAll(eventAdministrators);

	// Write all data to the document
	this.document.appendMetaData(event);

	// Read the meta data from the document
	EventMetaData metaData = this.document.getMetaData();

	// Test the meta data
	assertEquals("TestEvent", metaData.getName());
	assertEquals("TestLocation", metaData.getLocation());
	assertEquals(null, metaData.getStartDate());
	assertEquals(null, metaData.getEndDate());
	assertEquals(EventPhase.EVENT_SETUP, metaData.getEventPhase());

	// Test the administrator list
	assertEquals(1, metaData.getAdministrators().size());
	assertEquals("Aaron", metaData.getAdministrators().get(0)
		.getFirstName());
	assertEquals("Admin", metaData.getAdministrators().get(0).getLastName());
	assertEquals("a.admin@mail.com", metaData.getAdministrators().get(0)
		.getMailAddress());
	assertEquals("123456", metaData.getAdministrators().get(0)
		.getPhoneNumber());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyEventPhase() {
	Event event = new Event();
	// Set some meta data for the new event
	event.setName("TestEvent");
	event.setLocation("TestLocation");
	event.setEventPhase(null);

	// Add a list of event administrators
	ArrayList<EventAdministrator> eventAdministrators = new ArrayList<EventAdministrator>();
	EventAdministrator administrator = new EventAdministrator();
	administrator.setFirstName("Aaron");
	administrator.setLastName("Admin");
	administrator.setMailAdress("a.admin@mail.com");
	administrator.setPhoneNumber("123456");
	eventAdministrators.add(administrator);

	event.getAdministrators().setAll(eventAdministrators);

	// Write all data to the document
	this.document.appendMetaData(event);

	// Read the meta data from the document, should throw an exception due
	// to the empty event phase
	EventMetaData metaData = this.document.getMetaData();
    }

    @Test
    public void testTournamentList() {
	ObservableList<Tournament> tournamentList = FXCollections
		.observableArrayList();

	// Add a new tournament
	Tournament tournament = new Tournament();
	tournament.setId("123");
	tournament.setName("TestTournament");

	Player player = new Player();
	player.setFirstName("Peter");
	player.setLastName("Player");
	player.setId("2");
	player.setMailAdress("p.player@mail.com");
	player.setNickName("pplayer");
	player.setStartingNumber("3");
	player.setPayed(true);
	player.setDisqualified(false);

	tournament.getAttendingPlayers().add(player);
	tournament.getRegisteredPlayers().add(player);
	tournament.getRemainingPlayers().add(player);

	// Add a rule set
	TournamentModule module = new TournamentModule();
	module.setName("TestModule");
	module.setDescription("This is a test rule module");
	PossibleScoring primaryScores = new PossibleScoring();
	primaryScores.setPriority(1);
	primaryScores.getScores().put("Victory", 3);
	primaryScores.getScores().put("Tie", 2);
	primaryScores.getScores().put("Defeat", 1);
	module.getPossibleScores().add(primaryScores);
	GamePhase phase = new GamePhase();
	phase.setPhaseNumber(1);
	phase.setCutoff(16);
	phase.setPairingMethod(new SwissSystem());
	phase.setRoundCount(4);
	phase.setRoundDuration(Duration.ofMinutes(10));
	module.getPhaseList().add(phase);

	tournament.setRuleSet(module);

	// Add a score to the list
	PlayerScore score = new PlayerScore();
	score.setPlayer(player);
	score.getScore().add(42);

	tournament.getScoreTable().add(score);

	tournamentList.add(tournament);

	// Write the data to the document
	this.document.appendTournamentList(tournamentList);

	// Read the data back from the document
	ArrayList<Tournament> tournamentArrayList = new ArrayList<Tournament>(
		tournamentList);
	ArrayList<Tournament> readTournaments = this.document
		.getTournamentList(tournamentArrayList);

	assertEquals(1, readTournaments.size());

	// Test tournament meta data
	Tournament readTournament = readTournaments.get(0);

	assertEquals("TestTournament", readTournament.getName());
	assertEquals("123", readTournament.getId());

	// Test the player lists
	assertEquals(1, readTournament.getAttendingPlayers().size());
	assertEquals(1, readTournament.getRegisteredPlayers().size());
	assertEquals(1, readTournament.getRemainingPlayers().size());

	assertEquals("2", readTournament.getAttendingPlayers().get(0).getId());
	assertEquals("2", readTournament.getRegisteredPlayers().get(0).getId());
	assertEquals("2", readTournament.getRemainingPlayers().get(0).getId());

	Player readPlayer = readTournament.getAttendingPlayers().get(0);
	assertEquals("Peter", readPlayer.getFirstName());
	assertEquals("Player", readPlayer.getLastName());
	assertEquals("p.player@mail.com", readPlayer.getMailAddress());
	assertEquals("pplayer", readPlayer.getNickName());
	assertEquals("3", readPlayer.getStartingNumber());
	assertEquals(true, readPlayer.hasPayed());
	assertEquals(false, readPlayer.isDisqualified());

	// Test the rule set
	TournamentModule readTournamentModule = readTournament.getRuleSet();
	assertEquals("TestModule", readTournamentModule.getName());
	assertEquals("This is a test rule module",
		readTournamentModule.getDescription());

	// Test the possible scorings in the rule set
	assertEquals(1, readTournamentModule.getPossibleScores().size());
	PossibleScoring readScoring = readTournamentModule.getPossibleScores()
		.get(0);
	assertEquals(1, readScoring.getPriority());
	assertEquals(3, readScoring.getScores().get("Victory").intValue());
	assertEquals(2, readScoring.getScores().get("Tie").intValue());
	assertEquals(1, readScoring.getScores().get("Defeat").intValue());

	// Test the game phases in the rule set
	assertEquals(1, readTournamentModule.getPhaseList().size());
	GamePhase readPhase = readTournamentModule.getPhaseList().get(0);
	assertEquals(1, readPhase.getPhaseNumber());
	assertEquals(16, readPhase.getCutoff());
	assertTrue(readPhase.getPairingMethod() instanceof SwissSystem);
	assertEquals(4, readPhase.getRoundCount());
	assertEquals(Duration.ofMinutes(10), readPhase.getRoundDuration());
    }

    @Test
    public void testSetDocument() {
	this.document.setDocument(null);
	assertEquals(null, this.document.getDocument());
    }
}

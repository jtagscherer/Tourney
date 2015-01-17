package usspg31.tourney.tests.model.filemanagement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.Test;
import org.xml.sax.SAXException;

import usspg31.tourney.model.Event;
import usspg31.tourney.model.Event.EventPhase;
import usspg31.tourney.model.EventAdministrator;
import usspg31.tourney.model.GamePhase;
import usspg31.tourney.model.Player;
import usspg31.tourney.model.PossibleScoring;
import usspg31.tourney.model.Tournament;
import usspg31.tourney.model.TournamentModule;
import usspg31.tourney.model.filemanagement.FileLoader;
import usspg31.tourney.model.filemanagement.FileSaver;
import usspg31.tourney.model.pairingstrategies.SwissSystem;

public class TestFileManagement {

    @Test
    public void testEventSaving() {
	Event event = TestFileManagement.getDummyEvent();

	FileSaver.saveEventToFile(event, "test-event.tef");

	Event readEvent = null;
	try {
	    readEvent = FileLoader.loadEventFromFile("test-event.tef");
	} catch (IOException | SAXException e) {
	    fail("Could not load the event.");
	    e.printStackTrace();
	}

	// Test the meta data
	assertEquals("TestEvent", readEvent.getName());
	assertEquals("TestLocation", readEvent.getLocation());
	assertEquals(LocalDate.of(2015, 2, 1), readEvent.getStartDate());
	assertEquals(LocalDate.of(2015, 2, 3), readEvent.getEndDate());
	assertEquals(Event.EventPhase.PRE_REGISTRATION,
		readEvent.getEventPhase());

	// Test the administrator list
	assertEquals(1, readEvent.getAdministrators().size());
	assertEquals("Aaron", readEvent.getAdministrators().get(0)
		.getFirstName());
	assertEquals("Admin", readEvent.getAdministrators().get(0)
		.getLastName());
	assertEquals("a.admin@mail.com", readEvent.getAdministrators().get(0)
		.getMailAddress());
	assertEquals("123456", readEvent.getAdministrators().get(0)
		.getPhoneNumber());

	// Test tournament meta data
	assertEquals(1, readEvent.getTournaments().size());
	Tournament readTournament = readEvent.getTournaments().get(0);

	assertEquals("TestTournament", readTournament.getName());
	assertEquals("123", readTournament.getId());

	// Test the player lists
	assertEquals(7, readTournament.getAttendingPlayers().size());
	assertEquals(10, readTournament.getRegisteredPlayers().size());
	assertEquals(5, readTournament.getRemainingPlayers().size());

	assertEquals("0", readTournament.getAttendingPlayers().get(0).getId());
	assertEquals("0", readTournament.getRegisteredPlayers().get(0).getId());
	assertEquals("0", readTournament.getRemainingPlayers().get(0).getId());

	Player readPlayer = readTournament.getAttendingPlayers().get(0);
	assertEquals("John", readPlayer.getFirstName());
	assertEquals("Doe", readPlayer.getLastName());
	assertEquals("john.doe@mail.com", readPlayer.getMailAddress());
	assertEquals("johnny", readPlayer.getNickName());
	assertEquals("0", readPlayer.getStartingNumber());
	assertEquals(false, readPlayer.hasPayed());
	assertEquals(true, readPlayer.isDisqualified());

	// Delete the generated file again
	new File("test-event.tef").delete();
    }

    @Test
    public void testTournamentModuleFileSaving() {
	TournamentModule module = TestFileManagement.getDummyTournamentModule();

	FileSaver.saveTournamentModuleToFile(module, "test-module.ttm");

	TournamentModule readModule = null;
	try {
	    readModule = FileLoader
		    .loadTournamentModuleFromFile("test-module.ttm");
	} catch (IOException | SAXException e) {
	    fail("Could not load the tournament module.");
	    e.printStackTrace();
	}

	// Test meta data
	assertEquals("TestModule", readModule.getName());
	assertEquals("This is a test module.", readModule.getDescription());

	// Read the list of possible scorings
	ArrayList<PossibleScoring> readScorings = new ArrayList<PossibleScoring>(
		readModule.getPossibleScores());

	// Test the scorings
	assertEquals(1, readScorings.size());
	PossibleScoring readScoring = readScorings.get(0);
	assertEquals(1, readScoring.getPriority());
	assertEquals(3, readScoring.getScores().get("Victory").intValue());
	assertEquals(2, readScoring.getScores().get("Tie").intValue());
	assertEquals(1, readScoring.getScores().get("Defeat").intValue());

	// Read the list of game phases
	ArrayList<GamePhase> readPhases = new ArrayList<GamePhase>(
		readModule.getPhaseList());

	// Test the game phases
	assertEquals(1, readPhases.size());
	GamePhase readPhase = readPhases.get(0);
	assertEquals(1, readPhase.getPhaseNumber());
	assertEquals(16, readPhase.getCutoff());
	assertTrue(readPhase.getPairingMethod() instanceof SwissSystem);
	assertEquals(4, readPhase.getRoundCount());
	assertEquals(Duration.ofMinutes(10), readPhase.getRoundDuration());

	// Delete the generated file again
	new File("test-module.ttm").delete();
    }

    /**
     * Create some dummy event data for testing purposes
     * 
     * @return A new event
     */
    public static Event getDummyEvent() {
	Event event = new Event();

	TournamentModule module = new TournamentModule();
	module.setName("TestTournamentModule");
	module.setDescription("This is a test module.");
	PossibleScoring primaryScores = new PossibleScoring();
	primaryScores.getScores().put("Victory", 3);
	primaryScores.getScores().put("Tie", 2);
	primaryScores.getScores().put("Defeat", 1);
	module.getPossibleScores().add(primaryScores);
	GamePhase phase = new GamePhase();
	phase.setPhaseNumber(1);
	phase.setCutoff(16);
	phase.setPairingMethod(new SwissSystem());
	phase.setRoundCount(4);
	phase.setRoundDuration(Duration.ofMinutes(60));
	module.getPhaseList().add(phase);

	Tournament tournament = new Tournament();
	tournament.setName("TestTournament");
	tournament.setRuleSet(module);
	tournament.setId("123");
	ArrayList<Player> players = new ArrayList<Player>();

	for (int i = 0; i < 20; i++) {
	    Player player = new Player();
	    player.setFirstName("John");
	    player.setLastName("Doe");
	    player.setId(String.valueOf(i));
	    player.setMailAdress("john.doe@mail.com");
	    player.setNickName("johnny");
	    player.setStartingNumber(String.valueOf(i));
	    player.setDisqualified(true);
	    players.add(player);
	    event.getRegisteredPlayers().add(player);

	    if (i % 2 == 0) {
		tournament.getRegisteredPlayers().add(player);
	    }

	    if (i % 3 == 0) {
		tournament.getAttendingPlayers().add(player);
	    }

	    if (i % 4 == 0) {
		tournament.getRemainingPlayers().add(player);
	    }
	}

	event.setEventPhase(EventPhase.PRE_REGISTRATION);
	event.setName("TestEvent");
	event.setLocation("TestLocation");
	LocalDate start = LocalDate.of(2015, 02, 01);
	event.setStartDate(start);
	LocalDate end = LocalDate.of(2015, 02, 03);
	event.setEndDate(end);
	EventAdministrator admin = new EventAdministrator();
	admin.setFirstName("Aaron");
	admin.setLastName("Admin");
	admin.setMailAdress("a.admin@mail.com");
	admin.setPhoneNumber("123456");
	event.getAdministrators().add(admin);
	event.getTournaments().add(tournament);

	return event;
    }

    /**
     * Create a new tournament module with some dummy data for testing purposes
     * 
     * @return New tournament module
     */
    public static TournamentModule getDummyTournamentModule() {
	TournamentModule module = new TournamentModule();
	module.setName("TestModule");
	module.setDescription("This is a test module.");
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

	Tournament tournament = new Tournament();
	tournament.setName("TestTournament");
	tournament.setRuleSet(module);
	tournament.setId("34234");
	ArrayList<Player> players = new ArrayList<Player>();

	for (int i = 0; i < 20; i++) {
	    Player player = new Player();
	    player.setFirstName("John");
	    player.setLastName("Doe");
	    player.setId(String.valueOf(i));
	    player.setMailAdress("john.doe@gmail.com");
	    player.setNickName("johnny");
	    player.setStartingNumber(String.valueOf(i));
	    player.setDisqualified(true);
	    players.add(player);

	    if (i % 2 == 0) {
		tournament.getRegisteredPlayers().add(player);
	    }

	    if (i % 3 == 0) {
		tournament.getAttendingPlayers().add(player);
	    }

	    if (i % 4 == 0) {
		tournament.getRemainingPlayers().add(player);
	    }
	}

	return module;
    }
}

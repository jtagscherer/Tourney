package usspg31.tourney.tests.model.filemanagement;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import javax.xml.parsers.FactoryConfigurationError;

import org.junit.Before;
import org.junit.Test;

import usspg31.tourney.model.Event;
import usspg31.tourney.model.Event.UserFlag;
import usspg31.tourney.model.Tournament;
import usspg31.tourney.model.TournamentModule;
import usspg31.tourney.model.filemanagement.FileLoader;
import usspg31.tourney.model.filemanagement.FileSaver;

public class TestFileLoader {
    @Before
    public void init() {
	System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
		"com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
    }

    @Test
    public void testLoadEventUninitialized() {
	Event event = TestFileManagement.getDummyEvent();
	FileSaver.saveEventToFile(event, "test-event.tef");

	Event loadedEvent = null;
	try {
	    loadedEvent = FileLoader.loadEventFromFile("test-event.tef");
	} catch (Exception e) {
	    fail("Could not load event");
	}

	assertTrue(loadedEvent != null);

	new File("test-event.tef").delete();
    }

    @Test
    public void testLoadTournamentModuleUninitialized() {
	TournamentModule module = TestFileManagement.getDummyTournamentModule();
	FileSaver.saveTournamentModuleToFile(module, "test-module.ttm");

	TournamentModule loadedModule = null;
	try {
	    loadedModule = FileLoader
		    .loadTournamentModuleFromFile("test-module.ttm");
	} catch (Exception e) {
	    fail("Could not load module");
	}

	assertTrue(loadedModule != null);

	new File("test-module.ttm").delete();
    }

    @Test
    public void testEventMetaData() {
	Event event = TestFileManagement.getDummyEvent();
	event.setUserFlag(UserFlag.TOURNAMENT_EXECUTION);
	Tournament secondTournament = (Tournament) event.getTournaments()
		.get(0).clone();
	secondTournament.setId("2");
	event.getTournaments().add(secondTournament);
	event.setExecutedTournament(event.getTournaments().get(1));
	FileSaver.saveEventToFile(event, "test-event.tef");

	Event loadedEvent = null;
	try {
	    loadedEvent = FileLoader.loadEventFromFile("test-event.tef");
	} catch (Exception e) {
	    fail("Could not load event");
	}

	assertTrue(loadedEvent != null);

	new File("test-event.tef").delete();
    }

    @Test(expected = FactoryConfigurationError.class)
    public void testDamagedParserConfiguration() {
	System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
		"invalid");
	FileLoader.initialize();
    }
}
